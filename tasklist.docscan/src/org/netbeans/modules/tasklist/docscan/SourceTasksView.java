/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.accessibility.AccessibleContext;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.Lookup;
import org.openide.util.UserCancelException;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.*;
import org.openide.loaders.*;


import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.core.editors.StringPropertyEditor;
import org.netbeans.modules.tasklist.suggestions.*;
import org.netbeans.modules.tasklist.client.SuggestionPriority;


/**
 * View containing only source tasks (TODOs) either for
 * all files in project or for current file.
 *
 * @author Petr Kuzel
 */
final class SourceTasksView extends TaskListView implements SourceTasksAction.ScanProgressMonitor, SuggestionView {

    // The category should be DIFFERENT from the category used
    // for the default suggestion view (the active scanning view)
    // such that the "Show Suggestions View" action does not
    // locate and reuse these windows - and so they can have different
    // column configurations (filename is not useful in the active
    // suggestions view window, but is critical in the directory
    // scan for example.)
    final static String CATEGORY = "sourcetasks"; // NOI18N

    private final int MAIN_COLUMN_UID = 2352;
    private final int PRIORITY_COLUMN_UID = 7896;
    private final int FILE_COLUMN_UID = 8902;
    private final int LINE_COLUMN_UID = 6646;

    //XXX keep with sync with SuggestionNode, hidden dependency
    static final String PROP_SUGG_PRIO = "suggPrio"; // NOI18N
    static final String PROP_SUGG_FILE = "suggFile"; // NOI18N
    static final String PROP_SUGG_LINE = "suggLine"; // NOI18N
    static final String PROP_SUGG_CAT = "suggCat"; // NOI18N

    // current job or null if snapshot
    private SuggestionsBroker.Job job;

    // all files results or null
    private TaskList resultsSnapshot;

    // if terminated then describes why
    private String reasonMsg;

    /** background scanning or null */
    private Background background;

    /** Selcted folder to be scanned or null */
    private FileObject selectedFolder;

    private static final int RECENT_ITEMS_COUNT = 4;
    private ArrayList recentFolders = new ArrayList(RECENT_ITEMS_COUNT); // XXX it'd be nice to persist it

    /**
     * Externalization entry point (readExternal).
     */
    public SourceTasksView() {
        super();
        // readExternal, init
    }

    /**
     * Construct TODOs from all files in project.
     * @param list live tasklist driving view
     */
    public SourceTasksView(SourceTasksList list) {
        super(
                CATEGORY,
                Util.getString("win-title"),
                Utilities.loadImage("org/netbeans/modules/tasklist/docscan/scanned-task.gif"), // NOI18N
                true,
                list
        );

        init();
    }

    /**
     * Creates TODOs views for current file.
     * @param job live tasklist monitoring current file
     */
    SourceTasksView(SuggestionsBroker.Job job) {
        super(
                CATEGORY,
                Util.getString("win-title"),
                Utilities.loadImage("org/netbeans/modules/tasklist/docscan/scanned-task.gif"), // NOI18N
                true,
                createFilteredList(job.getSuggestionsList())
        );
        this.job = job;
        init();
    }

    /**
     * Common initialization code shared by constructor and externalization
     */
    private void init() {
        // When the tab is alone in a container, don't show a tab;
        // the category nodes provide enough feedback.
        putClientProperty("TabPolicy", "HideWhenAlone"); // NOI18N

        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        KeyStroke stop = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        inputMap.put(stop, stop);
        getActionMap().put(stop, new StopAction());

        KeyStroke refresh = KeyStroke.getKeyStroke(KeyEvent.VK_R, 0);
        inputMap.put(refresh, refresh);
        getActionMap().put(refresh, new DelegateAction(getRefresh()));

        KeyStroke filter = KeyStroke.getKeyStroke(KeyEvent.VK_F, 0);
        inputMap.put(filter, filter);
        getActionMap().put(filter, new DelegateAction(getFilterMenu()));

        KeyStroke editor = KeyStroke.getKeyStroke(KeyEvent.VK_E, 0);
        inputMap.put(editor, editor);
        getActionMap().put(editor, new DelegateAction(getGoto()));

        KeyStroke current = KeyStroke.getKeyStroke(KeyEvent.VK_C, 0);
        inputMap.put(current, current);
        getActionMap().put(current, new DelegateAction(getCurrentFile()));

        KeyStroke folder = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
        inputMap.put(folder, folder);
        getActionMap().put(folder, new DelegateAction(getAllFiles()));

        KeyStroke selectFolder = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_MASK);
        inputMap.put(selectFolder, selectFolder);
        getActionMap().put(selectFolder, new DelegateAction(getFolderSelector()));

        putClientProperty("PersistenceType", "OnlyOpened"); // NOI18N
    }

    protected TaskNode createRootNode() {
        Task root = getModel().getRoot();
        return new SourceTaskNode(root, this);
    }


    private ColumnProperty[] allFilesColumns;
    private ColumnProperty[] currentFileColumns;

    protected ColumnProperty[] createColumns() {
        // No point allowing other attributes of the task since that's
        // all we support for scan items (they are not created by
        // the user - and they are not persisted.

        // See overridden loadColumnConfiguration to supress
        // loading from sourcetasks_columns.xml
        if (job == null) {
            if (allFilesColumns == null) {
                allFilesColumns = new ColumnProperty[]{
                    createMainColumn(800),
                    createPriorityColumn(false, 100),
                    createFileColumn(true, 150),
                    createLineColumn(true, 50)
                };
            }
            return allFilesColumns;
        } else {
            if (currentFileColumns == null) {
                currentFileColumns = new ColumnProperty[]{
                    createMainColumn(800),
                    createPriorityColumn(false, 100),
                    createLineColumn(true, 50)
                };
            }
            return currentFileColumns;
        }
    }

    protected void loadColumnsConfiguration() {
        // TODO read from proper file
    }

    protected void storeColumnsConfiguration() {
        // XXX write to proper file
        // also note direct call to treeTable.setProperties in subview switch
        // that bypass setting proper client values at columns
    }

    private ColumnProperty createMainColumn(int width) {
        // Tree column
        // NOTE: Task.getDisplayName() must also be kept in sync here
        return new ColumnProperty(
                MAIN_COLUMN_UID, // UID -- never change (part of serialization
                PROP_TASK_SUMMARY,
                NbBundle.getMessage(SourceTaskNode.class, "TODO"), // NOI18N
                NbBundle.getMessage(SourceTaskNode.class, "TODOHint"), // NOI18N
                true,
                width
        );
    }


    private ColumnProperty createPriorityColumn(boolean visible, int width) {
        return new ColumnProperty(
                PRIORITY_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_PRIO,
                SuggestionPriority.class,
                NbBundle.getMessage(SourceTaskNode.class, "Priority"), // NOI18N
                NbBundle.getMessage(SourceTaskNode.class, "PriorityHint"), // NOI18N
                true,
                visible,
                width
        );
    }

    private ColumnProperty createFileColumn(boolean visible, int width) {
        ColumnProperty file = new ColumnProperty(
                FILE_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_FILE,
                String.class,
                NbBundle.getMessage(SourceTaskNode.class, "File"), // NOI18N
                NbBundle.getMessage(SourceTaskNode.class, "FileHint"), // NOI18N
                true,
                visible,
                width
        );
        // #38190 FIXME instead of renderer we do not have access to
        file.setPropertyEditorClass(StringPropertyEditor.class);
        return file;
    }

    protected void componentClosed() {
        super.componentClosed();
        if (background != null) background.interrupt();
        Cache.store();
        releaseWorkaround();
        if (job != null) {
            job.stopBroker();
            job = null;
        }
    }

    private ColumnProperty createLineColumn(boolean visible, int width) {
        return new ColumnProperty(
                LINE_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_LINE,
                Integer.TYPE,
                NbBundle.getMessage(SourceTaskNode.class, "Line"), // NOI18N
                NbBundle.getMessage(SourceTaskNode.class, "LineHint"), // NOI18N
                true,
                visible,
                width
        );
    }



    protected void componentOpened() {
        super.componentOpened();
        setNorthComponentVisible(true);
    }


    public void writeExternal(ObjectOutput objectOutput) throws IOException {

        Cache.store();

        // It's called by window system depending on actual value of:
        // putClientProperty("PersistenceType", "OnlyOpened"|"Never");

        // version 1 format
        // write int 1
        // skip call to super.writeExternal
        // write bool snapshot flag

        // version 2 format appends
        // recentFiles
        objectOutput.writeInt(2);  // version

        // Super method is driven by a private
        // filed passed in contructor and it denies
        // to write anything => fails on read on OptionalDataException
        //super.writeExternal(objectOutput);

        boolean snapshot = job == null;
        objectOutput.writeBoolean(snapshot);
        if (snapshot) {
            // write down tasklist, we know it's not hierachical
//            TaskList list = getList();
//            Task root = list.getRoot();
//            LinkedList tasks = root.getSubtasks();
//            Iterator it = tasks.iterator();
//            objectOutput.writeInt(tasks.size());
//            while (it.hasNext()) {
//                SuggestionImpl task = (SuggestionImpl) it.next();
//                String summary = task.getSummary();
//                String file = task.getFileBaseName();
//                int line = task.getLine().getLineNumber();
//                int prio = task.getPriorityNumber();
//                objectOutput.writeUTF(summary);
//                objectOutput.writeUTF(file);
//                objectOutput.writeInt(line);
//                objectOutput.writeInt(prio);
//            }
        }

        ArrayList recent = recentFolders;
        if (selectedFolder != null) {
            recent = new ArrayList(recentFolders);
            addRecent(recent, selectedFolder);
        }
        objectOutput.writeObject(recent);
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {

        super.category = CATEGORY;
        super.setName(Util.getString("win-title"));
        super.setIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/scanned-task.gif"));  // NOI18N

        int version = objectInput.readInt();
        if (version == 1 || version == 2) {
            // read writeExternal
            //super.readExternal(objectInput);

            boolean snapshot = objectInput.readBoolean();
    //        if (snapshot) {
    //            // read cache
    //            int size = objectInput.readInt();
    //            Children.Array tasks = new Children.Array();
    //            for (int i = 0; i<size; i++) {
    //                String summary = objectInput.readUTF();
    //                String file = objectInput.readUTF();
    //                int line = objectInput.readInt();
    //                int prio = objectInput.readInt();
    //                tasks.add(new Node[] {new SourceTaskNode(summary, file, line, prio)});
    //            }
    //            getExplorerManager().setRootContext(new AbstractNode(tasks));
    //        } else {
                // XXX defer to isShowing
                job = SuggestionsBroker.getDefault().startBroker();
                this.category = CATEGORY;
                registerTaskListView(this);
                setModel(createFilteredList(job.getSuggestionsList()));
     //       }

            if (version == 2) {
                recentFolders = (ArrayList) objectInput.readObject();
            }
        }

        init();

    }

    public String toString() {
        return "SourceTasksView@" + hashCode();
    }

    // North component ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private JProgressBar progress;
    private JButton stop;
    private AbstractButton refresh;
    private JComponent prev;
    private JComponent next;

    private JProgressBar getProgress() {
        if (progress == null) {
            progress = new ScanProgressBar();
            progress.setVisible(job == null);
            progress.setMinimum(0);
            // adjustHeight(progress); it removes bevel effect
        }
        return progress;
    }

    private class ScanProgressBar extends JProgressBar {
        public String getToolTipText() {
            if (scannedFolder != null) {
                return createLabel(scannedFolder);
            } else {
                return super.getToolTipText();
            }
        }
    }

    // Misiatus shows selected folder, limit info
    // and filter status
    private void updateMiniStatus() {
        assert SwingUtilities.isEventDispatchThread();
        String prefix = "";
        getMiniStatus().setHorizontalAlignment(SwingConstants.LEFT);
        StringBuffer msg = new StringBuffer(80);
        if (isFiltered()) {
            msg.append(Util.getString("filter-flag"));
        }
        if (job == null) {
            if (msg.length() > 0) prefix = ", "; // NOI18N
            msg.append(prefix + Util.getMessage("ctx-flag", createLabel(selectedFolder)));
        }

        if (reasonMsg != null && job == null) {
            if (msg.length() > 0) prefix = ", "; // NOI18N
            msg.append(prefix + Util.getMessage("usa-flag", "" + getModel().getRoot().getSubtaskCountRecursively()));
            getMiniStatus().setToolTipText(reasonMsg);
        } else {
            getMiniStatus().setToolTipText("");
        }
        getMiniStatus().setText(msg.toString());
    }

    private JButton getStop() {
        if (stop == null) {
            stop = new JButton(Util.getString("stop"));
            stop.setToolTipText(Util.getString("stop_hint") + " (ESC)");  // NOI18N
            stop.setVisible(job == null);
            stop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleStop();
                }
            });
            adjustHeight(stop);
        }
        return stop;
    }

    private AbstractButton getRefresh() {
        if (refresh == null) {
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/refresh.gif");  // NOI18N
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText(Util.getString("rescan_hint") + " (r)");  // NOI18N
            button.setEnabled(job == null);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleRefresh();
                }
            });
            adjustHeight(button);
            refresh = button;
        }
        return refresh;
    }

    private JComponent getPrev() {
        if (prev == null) {
            JButton button = new JButton("Prev (Shift+F12)");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handlePrev();
                }
            });
            adjustHeight(button);
            prev = button;
        }
        return prev;
    }

    private JComponent getNext() {
        if (next == null) {
            JButton button = new JButton("Next (F12)");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleNext();
                }
            });
            adjustHeight(button);
            next = button;
        }
        return next;
    }


    private AbstractButton allFilesButton;
    private ButtonGroup group = new ButtonGroup();;

    private AbstractButton getAllFiles() {
        if (allFilesButton == null) {
            JToggleButton button = new JToggleButton(Util.getString("see-folder"));
            if (selectedFolder == null) {
                button.setToolTipText(Util.getString("see-folder_hint1") + " (s)");  // NOI18N
            } else {
                // restored from settings
                button.setToolTipText(Util.getMessage("see-folder_hint2", selectedFolder.getPath()) + " (s)");  // NOI18N
            }
            group.add(button);
            button.setSelected(job == null);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleAllFiles();
                }
            });
            adjustHeight(button);
//            JButton pop = new JButton("V");
//            adjustHeight(pop);
//            JToggleButton both = new JToggleButton();
//            both.setLayout(new BorderLayout());
//            button.setBorder(null);
//            pop.setBorder(null);
//            both.add(button, BorderLayout.WEST);
//            both.add(pop, BorderLayout.EAST);
            allFilesButton = button;

        }
        return allFilesButton;
    }

    private AbstractButton folderSelector;
    private AbstractButton getFolderSelector() {
        if (folderSelector == null) {
            JButton button = new DropDown();
            button.setToolTipText(Util.getString("selector_hint") + " (S)"); // NOI18N
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (recentFolders.size() > 0 || selectedFolder != null) {
                        showFolderSelectorPopup();
                    } else {
                        handleSelectFolder();
                    }
                }
            });
            folderSelector = button;
        }
        return folderSelector;
    }

    // XXX it's too hardcoded, but all attempts to compute
    // it dynamically failed
    class DropDown extends JButton {

        DropDown() {
            super(new ImageIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/dropdown.gif")));  // NOI18N
            setMargin(new Insets(10, 0, 9, 0));
        }

        public Dimension getPreferredSize() {
            Dimension dim = getAllFiles().getPreferredSize();
            return new Dimension(11, 28);
        }
    }

    private void showFolderSelectorPopup() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem choose = new JMenuItem(Util.getString("select-folder"));
        choose.setMnemonic(Util.getChar("select-folder_mne"));
        choose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSelectFolder();
            }
        });
        popup.add(choose);

        Iterator it = recentFolders.iterator();
        int i = 1;
        if (it.hasNext() || selectedFolder != null) popup.addSeparator();

        if (selectedFolder != null) {
            JMenuItem item = new JMenuItem("0 " + createLabel(selectedFolder));
            item.setMnemonic(KeyEvent.VK_0);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getAllFiles().isSelected()) return;
                    handleAllFiles();
                }
            });
            popup.add(item);
        }

        int[] mnemonics = new int[] {0, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5};
        while (it.hasNext()) {
            final FileObject fo = (FileObject) it.next();
            if (fo == null || fo.isValid() == false) continue;
            JMenuItem item = new JMenuItem(i + " " + createLabel(fo));    // NOI18N
            item.setMnemonic(mnemonics[i]);
            item.addActionListener(new RecentActionListener(fo));
            popup.add(item);
            i++;
        }
        popup.show(getAllFiles(), 0, getAllFiles().getHeight());
    }

    private class RecentActionListener implements ActionListener {

        private final FileObject fo;

        RecentActionListener(FileObject recent) {
            fo = recent;
        }

        public void actionPerformed(ActionEvent e) {
            updateRecent(selectedFolder);
            selectedFolder = fo;
            resultsSnapshot = null;
            handleAllFiles();
        }
    }

    private AbstractButton currentFile;

    private AbstractButton getCurrentFile() {
        if (currentFile == null) {
            JToggleButton button = new JToggleButton(Util.getString("see-file"));
            button.setToolTipText(Util.getString("see-file_hint") + " (c)");  // NOI18N
            group.add(button);
            button.setSelected(job != null);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleCurrentFile();
                }
            });
            adjustHeight(button);
            currentFile = button;
        }
        return currentFile;
    }

    private AbstractButton gotoPresenter;

    private AbstractButton getGoto() {
        if (gotoPresenter == null) {
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/gotosource.gif"); // NOI18N
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText(Util.getString("goto_hint") + " (e)");  // NOI18N
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    GoToTaskAction gotoAction = (GoToTaskAction) SystemAction.get(GoToTaskAction.class);
                    if (gotoAction.isEnabled()) {
                        gotoAction.performAction();
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            });
            adjustHeight(button);
            gotoPresenter = button;
        }
        return gotoPresenter;
    }

    private JButton filterButton;

    private JButton getFilterMenu() {
        if (filterButton == null) {
            Icon icon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/filterOperations.gif")); // NOI18N
            filterButton = new JButton(icon);
            filterButton.setToolTipText(Util.getString("filter_hint") + " (f)");  // NOI18N
            adjustHeight(filterButton);
            filterButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JPopupMenu popup = new JPopupMenu();
                    ButtonGroup group = new ButtonGroup();

                    JRadioButtonMenuItem activate = new JRadioButtonMenuItem(Util.getString("activate"));
                    activate.setMnemonic(Util.getChar("activate_mne"));
                    activate.setSelected(isFiltered());
                    activate.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (filter == null && isFiltered() == false) {
                                SystemAction.get(FilterSourceTasksAction.class).actionPerformed(e);
                                updateMiniStatus();
                            } else if (isFiltered() == false) {
                                setFiltered(true);
                                updateMiniStatus();
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }
                    });
                    group.add(activate);
                    popup.add(activate);

                    JRadioButtonMenuItem deactivate = new JRadioButtonMenuItem(Util.getString("deactivate"));
                    deactivate.setMnemonic(Util.getChar("deactivate_mne"));
                    deactivate.setSelected(isFiltered() == false);
                    deactivate.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (isFiltered()) {
                                setFiltered(false);
                                updateMiniStatus();
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }
                    });
                    group.add(deactivate);
                    popup.add(deactivate);

                    popup.add(new JSeparator());

                    JMenuItem editFilter = new JMenuItem(Util.getString("edit"));
                    editFilter.setMnemonic(Util.getChar("edit_mne"));
                    editFilter.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            SystemAction.get(FilterSourceTasksAction.class).actionPerformed(e);
                            updateMiniStatus();
                        }
                    });
                    popup.add(editFilter);
                    popup.show(filterButton, 0, filterButton.getHeight() - 2);
                }
            });
        }
        return filterButton;
    }

    /** Toolbar controls must be smaller*/
    private static void adjustHeight(AbstractButton c) {
        Insets in = c.getMargin();
        in.top = 0;
        in.bottom = 0;
        c.setMargin(in);
    }

    public void updateFilterCount() {
        // do not write anything
    }

    protected Component createNorthComponent() {

        // toolbars are used to get desired visual rollover effect

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);  // NOI18N
        toolbar.setBorder(null);

        toolbar.add(getCurrentFile());
        toolbar.add(getAllFiles());
        toolbar.add(getFolderSelector());
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(getGoto());
        toolbar.add(getRefresh());
        toolbar.add(getFilterMenu());
        toolbar.add(new JSeparator(JSeparator.VERTICAL));

        JPanel rightpanel = new JPanel();
        rightpanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightpanel.add(getProgress());
        JToolBar stoptoolbar = new JToolBar();
        stoptoolbar.setFloatable(false);
        stoptoolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);  // NOI18N
        stoptoolbar.setBorder(null);
        stoptoolbar.add(getStop());
        rightpanel.add(stoptoolbar);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        Border verysoftbevelborder = BorderFactory.createMatteBorder(0,0,1,0,panel.getBackground().darker().darker());
        panel.setBorder(verysoftbevelborder);

        panel.add(toolbar, BorderLayout.WEST);
        JLabel ministatus = getMiniStatus();
        ministatus.setBorder(BorderFactory.createEmptyBorder(0,6,0,0));
        panel.add(ministatus, BorderLayout.CENTER);
        panel.add(rightpanel, BorderLayout.EAST);
        return panel;
    }


    // Monitor impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private int realFolders = 0;
    private int estimatedFolders = -1;

    /** Currently scanned folder or null. */
    private FileObject scannedFolder;

    public void estimate(final int estimate) {
        scannedFolder = null;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                estimatedFolders = estimate;
                if (estimate == -1) {
                    getProgress().setVisible(true);
                    getStop().setVisible(true);
                    getProgress().setIndeterminate(true);

                    getMiniStatus().setVisible(false);
                    getMiniStatus().setText(Util.getString("estimating"));
                    getMiniStatus().setHorizontalAlignment(SwingConstants.RIGHT);
                    getMiniStatus().setVisible(true);

                    Cache.load(); // hide this possibly long operation here
                } else {
                    getProgress().setIndeterminate(false);
                    getProgress().setMaximum(estimatedFolders);
                }
            }
        });
    }

    public void scanStarted() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                realFolders = 0;
                reasonMsg = null;
                getProgress().setVisible(true);
                getStop().setVisible(true);
                getRefresh().setEnabled(false);

                getMiniStatus().setVisible(false);
                getMiniStatus().setText(Util.getString("searching"));
                getMiniStatus().setHorizontalAlignment(SwingConstants.RIGHT);
                getMiniStatus().setVisible(true);
            }
        });

    }

    public void folderEntered(final FileObject folder) {
        scannedFolder = folder;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (estimatedFolders >0) {
                    realFolders++;
                    getProgress().setValue(realFolders);
                }
            }
        });

        handlePendingAWTEvents();
    }

    public void fileScanned(FileObject fo) {
        handlePendingAWTEvents();
    }

    public void folderScanned(FileObject fo) {
        handlePendingAWTEvents();
    }

    public void scanTerminated(final int reason) {
        if (reason == -1) {
            reasonMsg = Util.getString("mem_ter");
        } else if (reason == -2) {
            reasonMsg = Util.getString("usr-ter");
        } else if (reason == -3) {
            reasonMsg = Util.getString("usa-ter");
        }
    }

    public void scanFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                estimatedFolders = -1;
                getProgress().setVisible(false);
                getStop().setVisible(false);
                getRefresh().setEnabled(job == null);
                updateMiniStatus();
            }
        });
    }

    public void statistics(final int todos) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateMiniStatus();
            }
        });
    }

    // AWT request handlers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void handleStop() {
        background.interrupt();
        getMiniStatus().setText(Util.getString("stopping"));
    }

    private class StopAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (getStop().isVisible()) {
                handleStop();
            }
        }
    }

    private class DelegateAction extends AbstractAction {
        AbstractButton target;

        DelegateAction(AbstractButton target) {
            this.target = target;
        }

        public void actionPerformed(ActionEvent e) {
            target.doClick();
        }
    }

    protected void componentHidden() {
        releaseWorkaround();
        super.componentHidden();
    }

    /** User clicked selected folder, restore from cache or ask for context */
    private void handleAllFiles() {
        if (job != null) {
            job.stopBroker();
            job = null;
        }

        if (selectedFolder == null) {
            if (recentFolders.size() > 0) {
                showFolderSelectorPopup();
            } else {
                handleSelectFolder();
            }
            return;
        } else {
            // it might be resored from persitent setting and unavailable
            DataObject seletedDataFolder = null;
            try {
                 seletedDataFolder = DataObject.find(selectedFolder);
            } catch (DataObjectNotFoundException e) {
                // let it be null
            }
            if (seletedDataFolder == null) {
                if (recentFolders.size() > 0) {
                    showFolderSelectorPopup();
                } else {
                    handleSelectFolder();
                }
                return;
            }
        }

        allFilesButton.setToolTipText(Util.getMessage("see-folder-hint2", createLabel(selectedFolder)) + " (s)"); // NOI18N
        ((JToggleButton)allFilesButton).setSelected(true);

        treeTable.setProperties(createColumns());
        treeTable.setTreePreferredWidth(createColumns()[0].getWidth());
        TaskList list;
        if (resultsSnapshot == null) {
            list = new SourceTasksList();
        } else {
            list = resultsSnapshot;
        }
        releaseWorkaround();
        setModel(list);
        setFiltered(false);
        getRefresh().setEnabled(true);

        if (list != resultsSnapshot) {
            try {
                DataObject.Container one =
                    (DataObject.Container) DataObject.find(selectedFolder);
                DataObject.Container[] folders = new DataObject.Container[] {one};
                background = SourceTasksScanner.scanTasksAsync(this, folders);
                resultsSnapshot = list;
            } catch (DataObjectNotFoundException e) {
                selectedFolder = null;  // invalid folder
            }
        } else {
            getMiniStatus().setText(Util.getMessage("restored", createLabel(selectedFolder)));
        }
    }

    private void handleRefresh() {
        this.getList().clear();
        DataObject.Container one;
        try {
            one = (DataObject.Container) DataObject.find(selectedFolder);
            DataObject.Container[] folders = new DataObject.Container[] {one};
            background = SourceTasksScanner.scanTasksAsync(this, folders);
        } catch (DataObjectNotFoundException e) {
            getMiniStatus().setText(Util.getMessage("refresh-err",createLabel(selectedFolder)));
        }
    }

    private String createLabel(FileObject fo) {
        String path;
        File file = FileUtil.toFile(fo);
        if (file == null) {
            path = fo.getPath();
            try {
                path = fo.getFileSystem().getDisplayName() + path;
            } catch (FileStateInvalidException e) {
                // keep empty path
            }
        } else {
            path = file.getPath();
        }
        if (path.length() > 60) {
            return "..." + path.substring(path.length() - 60);
        } else {
            return path;
        }
    }

    // XXX detects listener leaks
    private void releaseWorkaround() {
        ObservableList filter = getModel();
        if (filter instanceof FilteredTasksList) {
            ((FilteredTasksList)filter).byebye();
        }
    }

    private void handleCurrentFile() {
        if (job != null) return;
        try {
            if (background != null) handleStop();
            background = null;
            job = SuggestionsBroker.getDefault().startBroker();
            treeTable.setProperties(createColumns());
            treeTable.setTreePreferredWidth(createColumns()[0].getWidth());
            setModel(createFilteredList(job.getSuggestionsList()));
            setFiltered(false);
        } finally {
            // setModel() above triggers IAE in IconManager after gc()
            getRefresh().setEnabled(false);
            updateMiniStatus();
        }
    }

    /** Let user choose what folder to scan and set selectedFolder field. */
    private void handleSelectFolder() {

        if (background != null) handleStop();
        background = null;

        // prepare content for selector

        NodeOperation op = NodeOperation.getDefault();
        Node repo = RepositoryNodeFactory.getDefault().repository(new DataFilter() {
            public boolean acceptDataObject(DataObject obj) {
                return obj instanceof DataObject.Container;
            }
        });
        Children kids = new Children.Array();
        Node[] nodes = repo.getChildren().getNodes(true);
        for (int i = 0; i<nodes.length; i++) {
            DataObject dobj = (DataObject) nodes[i].getCookie(DataObject.class);
            try {
                FileSystem fs = dobj.getPrimaryFile().getFileSystem();
                if (fs.isReadOnly()) continue;
                if (fs.isValid() == false) continue;
                kids.add(new Node[] {new FilterNode(nodes[i])});
            } catch (FileStateInvalidException e) {
                // do not add to chooser
            }
        }
        final Node content = new AbstractNode(kids) {
            public void setName(String name) {
                super.setName(name);
                super.setIconBase("org/netbeans/modules/tasklist/docscan/repository");  // NOI18N
            }
        };

        content.setName(Util.getString("fs"));

        try {
            Node[] selected = op.select(Util.getString("sel_title"), Util.getString("sel-head"), content, new NodeAcceptor() {
                public boolean acceptNodes(Node[] nodes) {
                    return nodes.length == 1 && nodes[0] != content;
                }
            });

            DataObject dobj = (DataObject) selected[0].getCookie(DataObject.class);
            resultsSnapshot = null;
            updateRecent(selectedFolder);
            selectedFolder = dobj.getPrimaryFile();

            handleAllFiles();
        } catch (UserCancelException e) {
            // no folders selected keep previous one
        }
    }

    private void updateRecent(FileObject fo) {
        addRecent(recentFolders, fo);
    }

    private void addRecent(java.util.List recentFolders, FileObject fo) {
        if (fo == null) return;
        if (recentFolders.contains(fo) == false) {
            if (recentFolders.size() == RECENT_ITEMS_COUNT) {
                recentFolders.remove(recentFolders.size() -1);
            }
            recentFolders.add(0, fo);
        } else {
            recentFolders.remove(fo);
            recentFolders.add(0, fo);
        }
    }

    private void handlePrev() {
        prevTask();
    }

    private void handleNext() {
        nextTask();
    }

    /** Set background process that feeds this view */
    final void setBackground(Background background) {
        this.background = background;
    }


    private static long lastUISync = System.currentTimeMillis();

    /**
     * Gives up CPU until all known AWT events get dispatched.
     */
    public static void handlePendingAWTEvents() {
        if (SwingUtilities.isEventDispatchThread()) return;

        long now = System.currentTimeMillis();
        if (now - lastUISync < 103) return;

        lastUISync = now;

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    // nothing no deadlock can occure
                }
            });
        } catch (InterruptedException ignore) {
        } catch (InvocationTargetException ignore) {
        }
    }

    public boolean isObserved(String category) {
        return isShowing() && SourceTaskProvider.TYPE.equals(category);
    }

    public SuggestionList getSuggestionsModel() {
        return null;
    }

    private static ObservableList createFilteredList(TaskList list) {
        return new FilteredTasksList(list);
    }

    public org.netbeans.modules.tasklist.core.filter.Filter getFilter() {
        if (filter == null) {
            filter = new SourceTasksFilter("TODOs"); // NOI18N
        }
        return filter;
    }

    public AccessibleContext getAccessibleContext() {
        AccessibleContext ret = super.getAccessibleContext();
        if (job == null) {
            ret.setAccessibleDescription(Util.getMessage("folder_desc11", createLabel(selectedFolder)));
        } else {
            ret.setAccessibleDescription(Util.getString("file_desc11"));
        }
        return ret;
    }
}
