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
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectStreamException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.Lookup;
import org.openide.util.UserCancelException;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
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
                "TODOs",
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
                "TODOs",
                Utilities.loadImage("org/netbeans/modules/tasklist/docscan/scanned-task.gif"), // NOI18N
                true,
                job.getSuggestionsList()  // FIXME not filtered
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

        // toggle according to All vs Current file
        if (job != null) {
            putClientProperty("PersistenceType", "OnlyOpened"); // NOI18N
        } else {
            putClientProperty("PersistenceType", "Never"); // NOI18N
        }
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

        objectOutput.writeInt(1);  // version

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
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {

        super.category = CATEGORY;
        super.setName("TODOs");
        super.setIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/scanned-task.gif"));

        int version = objectInput.readInt();
        if (version == 1) {
            // read writeExternal
            //super.readExternal(objectInput);

            boolean snapshot = objectInput.readBoolean();
            if (snapshot) {
                // read cache
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
            } else {
                // XXX defer to isShowing
                job = SuggestionsBroker.getDefault().startBroker();
                ObservableList filtered = new FilteredTasksList(job.getSuggestionsList());
                this.category = CATEGORY;
                registerTaskListView(this);
                setModel(job.getSuggestionsList());
                //setModel(filtered);
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
            progress = new JProgressBar();
            progress.setVisible(job == null);
            progress.setMinimum(0);
            // adjustHeight(progress); it removes bevel effect
        }
        return progress;
    }

    private JButton getStop() {
        if (stop == null) {
            stop = new JButton("Stop");
            stop.setToolTipText("Interrupts background search. (ESC)");
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
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/refresh.gif");
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText("Rescans TODOs for selected folder. (r)");
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
            JButton button = new JButton("Prev [Shift-F12]");
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
            JButton button = new JButton("Next [F12]");
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
            JToggleButton button = new JToggleButton("Selected Folder");
            if (selectedFolder == null) {
                button.setToolTipText("Switches to TODOs for selected folder. (s)");
            } else {
                // restored from settings
                button.setToolTipText("Switches to TODOs for " + selectedFolder.getPath() + " (s)");
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
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/dropdown.gif");
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText("Selects folder to be scanned. (S)");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (recentFolders.size() > 0) {
                        showFolderSelectorPopup();
                    } else {
                        handleSelectFolder();
                    }
                }
            });
            adjustHeight(button);
            folderSelector = button;
        }
        return folderSelector;
    }

    private void showFolderSelectorPopup() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem choose = new JMenuItem("Choose folder...");
        choose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSelectFolder();
            }
        });
        popup.add(choose);

        Iterator it = recentFolders.iterator();
        int i = 1;
        if (it.hasNext()) popup.addSeparator();

        while (it.hasNext()) {
            final FileObject fo = (FileObject) it.next();
            if (fo == null || fo.isValid() == false) continue;
            JMenuItem item = new JMenuItem(i + " " + createLabel(fo));
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
            JToggleButton button = new JToggleButton("Current File");
            button.setToolTipText("Switches to TODOs for edited document. (c)");
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
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/gotosource.gif");
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText("Shows selected TODO in editor. (e)");
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
            Icon icon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/filterOperations.gif"));
            filterButton = new JButton(icon);
            filterButton.setToolTipText("Allows to filter found TODOs. (f)");
            adjustHeight(filterButton);
            filterButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JPopupMenu popup = new JPopupMenu();
                    ButtonGroup group = new ButtonGroup();

                    JRadioButtonMenuItem activate = new JRadioButtonMenuItem("Activate Filter");
                    activate.setSelected(isFiltered());
                    activate.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (filter == null && isFiltered() == false) {
                                SystemAction.get(FilterSourceTasksAction.class).actionPerformed(e);
                            } else if (isFiltered() == false) {
                                setFiltered(true);
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }
                    });
                    group.add(activate);
                    popup.add(activate);

                    JRadioButtonMenuItem deactivate = new JRadioButtonMenuItem("Deactivate Filter");
                    deactivate.setSelected(isFiltered() == false);
                    deactivate.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (isFiltered()) {
                                setFiltered(false);
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }
                    });
                    group.add(deactivate);
                    popup.add(deactivate);

                    popup.add(new JSeparator());

                    JMenuItem editFilter = new JMenuItem("Edit Filter");
                    editFilter.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            SystemAction.get(FilterSourceTasksAction.class).actionPerformed(e);
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
        panel.add(getMiniStatus(), BorderLayout.CENTER);
        panel.add(rightpanel, BorderLayout.EAST);
        return panel;
    }


    // Monitor impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private int realFolders = 0;
    private int estimatedFolders = -1;

    public void estimate(final int estimate) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                estimatedFolders = estimate;
                if (estimate == -1) {
                    getProgress().setVisible(true);
                    getStop().setVisible(true);
                    getProgress().setIndeterminate(true);
                    getMiniStatus().setText("Estimating media search complexity...");
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
            }
        });

    }

    public void folderEntered(final FileObject folder) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (estimatedFolders >0) {
                    realFolders++;
                    getProgress().setValue(realFolders);
                }
                getMiniStatus().setText("Scanning " + folder.getPath());
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (reason == -1) {
                    reasonMsg = "(Low Memory Interrupt)";
                } else if (reason == -2) {
                    reasonMsg = "(Interrupted by User)";
                } else if (reason == -3) {
                    reasonMsg = "(Usability Limit Reached)";
                }
            }
        });
    }

    public void scanFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                estimatedFolders = -1;
                getProgress().setVisible(false);
                getStop().setVisible(false);
                getRefresh().setEnabled(job == null);
            }
        });
    }

    public void statistics(final int todos) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (job == null) {
                    String text = NbBundle.getMessage(SourceTasksView.class,
                                                           "TodoScanDone", new Integer(todos)); // NOI18N
                    getMiniStatus().setText(text + (reasonMsg != null ? reasonMsg : ""));
                }
            }
        });
    }

    // AWT request handlers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void handleStop() {
        background.interrupt();
        getMiniStatus().setText("Stopping...");
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

        allFilesButton.setToolTipText("Switches to TODOs for " + createLabel(selectedFolder) + " (s)");
        ((JToggleButton)allFilesButton).setSelected(true);

        putClientProperty("PersistenceType", "Never"); // NOI18N
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
            getMiniStatus().setText(createLabel(selectedFolder) + " tasks restored from cache.");
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
            getMiniStatus().setText("Error refreshing " + createLabel(selectedFolder));
        }
    }

    private String createLabel(FileObject fo) {
        String path = fo.getPath();
        if ("".equals(path)) { // NOI18N
            try {
                path = fo.getFileSystem().getDisplayName();
            } catch (FileStateInvalidException e) {
                // keep empty path
            }
        }
        return path;
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
            putClientProperty("PersistenceType", "OnlyOpened");  // NOI18N
            treeTable.setProperties(createColumns());
            treeTable.setTreePreferredWidth(createColumns()[0].getWidth());
            ObservableList filtered = new FilteredTasksList(job.getSuggestionsList());
            //setModel(filtered);
            setModel(job.getSuggestionsList());
            setFiltered(false);
        } finally {
            // setModel() above triggers IAE in IconManager after gc()
            getRefresh().setEnabled(false);
            getMiniStatus().setText("");
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
        final Node content = new AbstractNode(kids);
        content.setName("Filesystems");

        try {
            Node[] selected = op.select("Select folder", "Folders:", content, new NodeAcceptor() {
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

    public org.netbeans.modules.tasklist.core.filter.Filter getFilter() {
        if (filter == null) {
            filter = new SuggestionFilter("Simple"); // NOI18N
        }
        return filter;
    }


}
