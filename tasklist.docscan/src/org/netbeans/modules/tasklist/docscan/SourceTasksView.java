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

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;


import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.suggestions.*;
import org.netbeans.api.tasklist.SuggestionPriority;


/**
 * View containing only source tasks (TODOs) either for
 * all files in project or for current file.
 *
 * @author Petr Kuzel
 */
final class SourceTasksView extends TaskListView implements SourceTasksAction.ScanProgressMonitor, SuggestionView {

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

        // toggle according to All vs Current file
        if (job != null) {
            putClientProperty("PersistenceType", "OnlyOpened"); // NOI18N
        } else {
            putClientProperty("PersistenceType", "Never"); // NOI18N
        }
    }

    protected TaskNode createRootNode() {
        Task root = getModel().getRoot();
        return new SuggestionNode(root, this);
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
                NbBundle.getMessage(SourceTaskNode.class, "SuggestionsRoot"), // NOI18N
                NbBundle.getMessage(SourceTaskNode.class, "SuggestionsRoot"), // NOI18N
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
        return new ColumnProperty(
                FILE_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_FILE,
                String.class,
                NbBundle.getMessage(SourceTaskNode.class, "File"), // NOI18N
                NbBundle.getMessage(SourceTaskNode.class, "FileHint"), // NOI18N
                true,
                visible,
                width
        );
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
                initList(filtered);
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
    private JComponent refresh;
    private JComponent prev;
    private JComponent next;

    private JProgressBar getProgress() {
        if (progress == null) {
            progress = new JProgressBar();
            progress.setVisible(job == null);
            progress.setMinimum(0);
        }
        return progress;
    }

    private JButton getStop() {
        if (stop == null) {
            stop = new JButton("stop");
            stop.setVisible(job == null);
            stop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleStop();
                }
            });
        }
        return stop;
    }

    private JComponent getRefresh() {
        if (refresh == null) {
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/refresh.gif");
            JButton button = new JButton(new ImageIcon(image));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleRefresh();
                }
            });
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
            next = button;
        }
        return next;
    }

    private void handleRefresh() {
        this.getList().clear();
        SourceTasksAction.scanTasksAsync(this);
    }

    private JToggleButton allFilesButton;
    private ButtonGroup group = new ButtonGroup();;

    private JToggleButton getAllFiles() {
        if (allFilesButton == null) {
            JToggleButton button = new JToggleButton("All Files");
            group.add(button);
            button.setSelected(job == null);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleAllFiles();
                }
            });
            allFilesButton = button;
        }
        return allFilesButton;
    }

    private JComponent currentFile;

    private JComponent getCurrentFile() {
        if (currentFile == null) {
            JToggleButton button = new JToggleButton("Current File");
            group.add(button);
            button.setSelected(job != null);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleCurrentFile();
                }
            });
            currentFile = button;
        }
        return currentFile;
    }

    private Component gotoPresenter;

    private Component getGoto() {
        if (gotoPresenter == null) {
            GoToTaskAction gotoAction = (GoToTaskAction) SystemAction.get(GoToTaskAction.class);
            gotoPresenter = gotoAction.getToolbarPresenter();
        }
        return gotoPresenter;
    }

    private JButton filterButton;

    private JButton getFilterMenu() {
        if (filterButton == null) {
            Icon icon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/tasklist/core/filter.png"));
            filterButton = new JButton(icon);
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

    protected Component createNorthComponent() {

        // toolbars are used to get desired visual rollover effect

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);  // NOI18N
        toolbar.setBorder(null);

        toolbar.add(getAllFiles());
        toolbar.add(getCurrentFile());
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(getGoto());
        toolbar.add(getRefresh());
        toolbar.add(getFilterMenu());

        JPanel rightpanel = new JPanel();
        rightpanel.add(new JSeparator(JSeparator.VERTICAL));
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
        panel.setBorder(new SoftBevelBorder(BevelBorder.RAISED));  // XXX BorderFactory does not support it

        panel.add(toolbar, BorderLayout.WEST);
        panel.add(getMiniStatus(), BorderLayout.CENTER);
        panel.add(rightpanel, BorderLayout.EAST);
        return panel;
    }


    // Monitor impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private boolean interrupt = false;
    private int realFolders = 0;
    private int estimatedFolders = -1;

    public void estimate(int estimate) {
        estimatedFolders = estimate;
        realFolders = 0;
    }

    public void scanStarted() {
        if (estimatedFolders > 0) {
            JProgressBar bar = getProgress();
            bar.setMaximum(estimatedFolders);
            bar.setVisible(true);

            JButton stop = getStop();
            stop.setVisible(true);

            getRefresh().setEnabled(false);
        }
        interrupt = false;
    }

    public void folderEntered(FileObject folder) {
        if (interrupt) {
            Thread.currentThread().interrupt();
            return;
        }

        if (estimatedFolders >0) {
            realFolders++;
            getProgress().setValue(realFolders);
        }
        getMiniStatus().setText("Scanning " + folder.getPath());
        handlePendingAWTEvents();
    }

    public void fileScanned(FileObject fo) {
        if (interrupt) {
            Thread.currentThread().interrupt();
            return;
        }
        handlePendingAWTEvents();
    }

    public void folderScanned(FileObject fo) {
        if (interrupt) {
            Thread.currentThread().interrupt();
            return;
        }
        handlePendingAWTEvents();
    }

    public void scanFinished() {
        estimatedFolders = -1;
        getProgress().setVisible(false);
        getStop().setVisible(false);
        getRefresh().setEnabled(getAllFiles().isSelected());
    }

    public void statistics(int todos) {
        if (getAllFiles().isSelected()) {
            String text = NbBundle.getMessage(SourceTasksView.class,
                                                   "TodoScanDone", new Integer(todos)); // NOI18N
            getMiniStatus().setText(text);
        }
    }

    private void handleStop() {
        getMiniStatus().setText("Stopping...");
        interrupt = true;
    }

    private class StopAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (getStop().isVisible()) {
                handleStop();
            }
        }
    }

    protected void componentHidden() {
        releaseWorkaround();
        super.componentHidden();
    }

    private void handleAllFiles() {
        // scan for todos
        if (job != null) {
            job.stopBroker();
            job = null;
            putClientProperty("PersistenceType", "Never");
        }
        treeTable.setProperties(createColumns());
        treeTable.setTreePreferredWidth(createColumns()[0].getWidth());
        SuggestionList list = new SourceTasksList();
        releaseWorkaround();
        showList(list);
        setFiltered(false);
        interrupt = false;
        SourceTasksAction.scanTasksAsync(this);
        getRefresh().setEnabled(true);
    }

    // XXX detects listener leaks
    private void releaseWorkaround() {
        ObservableList filter = getModel();
        if (filter instanceof FilteredTasksList) {
            ((FilteredTasksList)filter).byebye();
        }
    }

    private void handleCurrentFile() {
        handleStop();
        job = SuggestionsBroker.getDefault().startBroker();
        putClientProperty("PersistenceType", "OnlyOpened");
        treeTable.setProperties(createColumns());
        treeTable.setTreePreferredWidth(createColumns()[0].getWidth());
        ObservableList filtered = new FilteredTasksList(job.getSuggestionsList());
        showList(filtered);
        getRefresh().setEnabled(false);
        setFiltered(false);
        getMiniStatus().setText("");
    }

    private void handlePrev() {
        prevTask();
    }

    private void handleNext() {
        nextTask();
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

}
