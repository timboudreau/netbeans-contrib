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
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.FileObject;

import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.core.filter.RemoveFilterAction;
import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.netbeans.modules.tasklist.suggestions.*;
import org.netbeans.api.tasklist.SuggestionPriority;


/**
 * View containing only source tasks (TODOs).
 *
 * @author Petr Kuzel
 */
final class SourceTasksView extends TaskListView implements SourceTasksAction.ScanProgressMonitor, SuggestionView {

    final static String CATEGORY = "sourcetasks"; // NOI18N

    private final int MAIN_COLUMN_UID = 2352;
    private final int PRIORITY_COLUMN_UID = 7896;
    private final int DETAILS_COLUMN_UID = 1098;
    private final int FILE_COLUMN_UID = 8902;
    private final int LINE_COLUMN_UID = 6646;

    //XXX keep with sync with SuggestionNode, hidden dependency
    static final String PROP_SUGG_DETAILS = "suggDetails"; // NOI18N
    static final String PROP_SUGG_PRIO = "suggPrio"; // NOI18N
    static final String PROP_SUGG_FILE = "suggFile"; // NOI18N
    static final String PROP_SUGG_LINE = "suggLine"; // NOI18N
    static final String PROP_SUGG_CAT = "suggCat"; // NOI18N

    // current job or null if snapshot
    private SuggestionsBroker.Job job;

    /**
     * Construct a Scaned tasks view with the given window title, and the given
     * list to show the contents in
     * @param name The name of the window
     * @param list The tasklist to store the scanned tasks in
     */
    public SourceTasksView(String name, SourceTasksList list, String icon) {
        super(
                CATEGORY,
                name,
                Utilities.loadImage(icon),
                false,
                list
        );

        // When the tab is alone in a container, don't show a tab;
        // the category nodes provide enough feedback.
        putClientProperty("TabPolicy", "HideWhenAlone");
    }

    protected TaskNode createRootNode() {
        SuggestionImpl root = (SuggestionImpl) tasklist.getRoot();
        return new SuggestionNode(root, this);
    }


    protected ColumnProperty[] createColumns() {
        // No point allowing other attributes of the task since that's
        // all we support for scan items (they are not created by
        // the user - and they are not persisted.
        return new ColumnProperty[]{
            createMainColumn(800),
            createPriorityColumn(false, 100),
            createDetailsColumn(false, 800),
            createFileColumn(true, 150),
            createLineColumn(true, 50)
        };

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


    private ColumnProperty createDetailsColumn(boolean visible, int width) {
        return new ColumnProperty(
                DETAILS_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_DETAILS,
                String.class,
                NbBundle.getMessage(SourceTaskNode.class, "Details"), // NOI18N
                NbBundle.getMessage(SourceTaskNode.class, "DetailsHint"), // NOI18N
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
        // TODO super.writeExternal(objectOutput);
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        // TODO super.readExternal(objectInput);
    }

    public String toString() {
        return "SourceTasksView@" + hashCode();
    }

    // North component ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private JProgressBar progress;
    private JButton stop;
    private JLabel  status;
    private JComponent refresh;
    private JComponent prev;
    private JComponent next;

    private JProgressBar getProgress() {
        if (progress == null) {
            progress = new JProgressBar();
            progress.setMinimum(0);
        }
        return progress;
    }

    private JButton getStop() {
        if (stop == null) {
            stop = new JButton("stop");
            stop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stop.setText("Stopping...");
                    handleStop();
                }
            });
        }
        return stop;
    }

    private JLabel getStatus() {
        if (status == null) {
            status = new JLabel();
        }
        return status;
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
            button.setSelected(true);
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
                    group.add(activate);
                    popup.add(activate);

                    JRadioButtonMenuItem deactivate = new JRadioButtonMenuItem("Deactivate Filter");
                    deactivate.setSelected(isFiltered() == false);
                    group.add(deactivate);
                    popup.add(deactivate);

                    popup.add(new JSeparator());

                    JMenuItem editFilter = new JMenuItem("Edit Filter");
                    editFilter.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            SystemAction.get(FilterAction.class).actionPerformed(e);
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
//        JPanel leftpanel = new JPanel();
//        leftpanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
//        leftpanel.add(getAllFiles());
//        leftpanel.add(getCurrentFile());
//        leftpanel.add(new JSeparator(JSeparator.VERTICAL));
//        leftpanel.add(getPrev());
//        leftpanel.add(getNext());

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
        toolbar.add(new JSeparator(JSeparator.VERTICAL));

//        leftpanel.add(toolbar);
//        leftpanel.add(new JSeparator(JSeparator.VERTICAL));

        JPanel rightpanel = new JPanel();
        rightpanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightpanel.add(getProgress());
        rightpanel.add(getStop());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
//        panel.add(leftpanel, BorderLayout.WEST);
        panel.add(toolbar, BorderLayout.WEST);
        panel.add(getStatus(), BorderLayout.CENTER);
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
            stop.setText("Stop");
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
        getStatus().setText("Scanning " + folder.getPath());
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
            getStatus().setText(text);
        }
    }

    private void handleStop() {
        interrupt = true;
    }

    private void handleAllFiles() {
        // scan for todos
        if (job != null) {
            job.stopBroker();
        }
        SuggestionList list = new SourceTasksList();
        showList(list);
        SourceTasksAction.scanTasksAsync(this);
        getRefresh().setEnabled(true);
    }

    private void handleCurrentFile() {
        handleStop();
        job = SuggestionsBroker.getDefault().startBroker();
        showList(job.getSuggestionsList());
        getRefresh().setEnabled(false);
        getStatus().setText("");
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
