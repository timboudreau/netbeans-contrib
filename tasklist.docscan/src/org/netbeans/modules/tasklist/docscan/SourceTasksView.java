/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.accessibility.AccessibleContext;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.Lookup;
import org.openide.util.UserCancelException;
import org.openide.util.lookup.Lookups;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.windows.TopComponent;


import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.editors.StringPropertyEditor;
import org.netbeans.modules.tasklist.suggestions.*;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.tasklist.core.filter.FilterRepository;
import org.netbeans.spi.project.ui.support.LogicalViews;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ProjectUtils;


/**
 * View containing only source tasks (TODOs) either for
 * all files in project or for current file.
 *
 * @author Petr Kuzel
 */
final class SourceTasksView extends TaskListView implements SourceTasksAction.ScanProgressMonitor, SuggestionView {

    private static final long serialVersionUID = 1;

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
    private final int LOCATION_COLUMN_UID = 6512;

    //XXX keep with sync with SuggestionNode, hidden dependency
    static final String PROP_SUGG_PRIO = "suggPrio"; // NOI18N
    static final String PROP_SUGG_FILE = "suggFile"; // NOI18N
    static final String PROP_SUGG_LINE = "suggLine"; // NOI18N
    static final String PROP_SUGG_CAT = "suggCat"; // NOI18N
    static final String PROP_SUGG_LOC = "suggLoc"; // NOI18N

    private static final int CURRENT_FILE_MODE = 1;
    private static final int OPENED_FILES_MODE = 2;
    private static final int SELECTED_FOLDER_MODE = 3;
    private static final int MODE_COUNT = SELECTED_FOLDER_MODE;

    // current job or null if snapshot
    private SuggestionsBroker.Job job;

    // all files results or null
    private TaskList resultsSnapshot;

    // if terminated then describes why
    private String reasonMsg;

    /** background scanning or null */
    private Background background;

    /** Selcted folder to be scanned or null */
    /*package*/ FileObject selectedFolder;

    private static final int RECENT_ITEMS_COUNT = 4;
    private ArrayList recentFolders = new ArrayList(RECENT_ITEMS_COUNT);

    /** Active opened files job or null */
    private SuggestionsBroker.AllOpenedJob allJob;

    private final TabState[] tabStates = new TabState[MODE_COUNT];



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
        getActionMap().put(filter, new DelegateAction(getFilterButton()));

        KeyStroke editor = KeyStroke.getKeyStroke(KeyEvent.VK_E, 0);
        inputMap.put(editor, editor);
        getActionMap().put(editor, new DelegateAction(getGoto()));

        KeyStroke current = KeyStroke.getKeyStroke(KeyEvent.VK_C, 0);
        inputMap.put(current, current);
        getActionMap().put(current, new DelegateAction(getCurrentFile()));

        KeyStroke opened = KeyStroke.getKeyStroke(KeyEvent.VK_O, 0);
        inputMap.put(opened, opened);
        getActionMap().put(opened, new DelegateAction(getOpenedFiles()));

        KeyStroke folder = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
        inputMap.put(folder, folder);
        getActionMap().put(folder, new DelegateAction(getAllFiles()));

        KeyStroke selectFolder = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_MASK);
        inputMap.put(selectFolder, selectFolder);
        getActionMap().put(selectFolder, new DelegateAction(getFolderSelector()));
    }

    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }


    protected Node createRootNode() {
        return new TaskListNode(getModel());
//        Task root = getModel().getRoot();
//        return new SourceTaskNode(root, this);
    }


    private ColumnProperty[] allFilesColumns;
    private ColumnProperty[] currentFileColumns;

    /** Create columes according to current getMode(). */
    protected ColumnProperty[] createColumns() {
        // No point allowing other attributes of the task since that's
        // all we support for scan items (they are not created by
        // the user - and they are not persisted.

        // See overridden loadColumnConfiguration to supress
        // loading from sourcetasks_columns.xml

        switch (getMode()) {
            case CURRENT_FILE_MODE:
                if (currentFileColumns == null) {
                    currentFileColumns = new ColumnProperty[]{
                        createMainColumn(800),
                        createPriorityColumn(false, 100),
                        createLineColumn(true, 50)
                    };
                }
                return currentFileColumns;

            case OPENED_FILES_MODE:
            case SELECTED_FOLDER_MODE:
                if (allFilesColumns == null) {
                    allFilesColumns = new ColumnProperty[]{
                        createMainColumn(800),
                        createPriorityColumn(false, 100),
                        createLocationColumn(true, 200),
                    };
                }
                return allFilesColumns;

            default:
                throw new IllegalStateException();
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
        return file;
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

    private ColumnProperty createLocationColumn(boolean visible, int width) {
        ColumnProperty location = new ColumnProperty(
                LOCATION_COLUMN_UID, // UID -- never change (part of serialization
                PROP_SUGG_LOC,
                String.class,
                Util.getString("position"),
                Util.getString("position_desc"),
                true,
                visible,
                width
        );
        return location;

    }

    protected void componentOpened() {
        super.componentOpened();
        setNorthComponentVisible(true);
        if (job == null) {  // XXX how relates to deserialization?, is it called at all?
            handleCurrentFile();
        }
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
        if (allJob != null) {
            allJob.stopBroker();
            allJob = null;
        }
        // keep on mind that it's still alive, just invisible
        // Until garbage collected it can be reopen at any time
        // restoring all data from caches (fields).
    }

    /** Returns "todo-window" */
    protected String preferredID() {
        return "todo-window";  // NOI18N
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

    // TODO support de/seriliazation for "all oponed" mode
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
                job = SuggestionsBroker.getDefault().startBroker(new SourceTasksProviderAcceptor());
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

        private static final long serialVersionUID = 1;

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
        if (job == null && selectedFolder != null) {
            if (msg.length() > 0) prefix = ", "; // NOI18N
            msg.append(prefix + Util.getMessage("ctx-flag", createLabel(selectedFolder)));
        }

        if (reasonMsg != null && job == null) {
            if (msg.length() > 0) prefix = ", "; // NOI18N
            msg.append(prefix + Util.getMessage("usa-flag", "" + TLUtils.recursiveCount(getModel().getTasks().iterator())));
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
            stop.addActionListener(dispatcher);
            adjustHeight(stop);
        }
        return stop;
    }

    /*package*/ AbstractButton getRefresh() {
        if (refresh == null) {
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/refresh.gif");  // NOI18N
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText(Util.getString("rescan_hint") + " (r)");  // NOI18N
            button.setEnabled(job == null);
            button.addActionListener(dispatcher);
            adjustHeight(button);
            refresh = button;
        }
        return refresh;
    }

    private JComponent getPrev() {
        if (prev == null) {
            JButton button = new JButton("Prev (Shift+F12)");
            button.addActionListener(dispatcher);
            adjustHeight(button);
            prev = button;
        }
        return prev;
    }

    private JComponent getNext() {
        if (next == null) {
            JButton button = new JButton("Next (F12)");
            button.addActionListener(dispatcher);
            adjustHeight(button);
            next = button;
        }
        return next;
    }


    private AbstractButton allFilesButton;
    private ButtonGroup group = new ButtonGroup();;

    /*package*/ AbstractButton getAllFiles() {
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
            button.addActionListener(dispatcher);
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
            button.addActionListener(dispatcher);
            folderSelector = button;
        }
        return folderSelector;
    }

    class DropDown extends JButton {

        private static final long serialVersionUID = 1;

        DropDown() {
            super(new ImageIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/dropdown.gif")));  // NOI18N
            setMargin(new Insets(10, 0, 9, 0));
        }

        public Dimension getPreferredSize() {
            Dimension dim = getAllFiles().getPreferredSize();
            return new Dimension(11, dim.height);
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
            if (fo.equals(selectedFolder)) continue;
            JMenuItem item = new JMenuItem(i + " " + createLabel(fo));    // NOI18N
            item.setMnemonic(mnemonics[i]);
            item.addActionListener(new RecentActionListener(fo));
            popup.add(item);
            i++;
        }

        popup.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                if (selectedFolder == null) {
                    getCurrentFile().doClick(0);
                }
            }

        });
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

    private AbstractButton openedFiles;

    private AbstractButton getOpenedFiles() {
        if (openedFiles == null) {
            JToggleButton button = new JToggleButton(Util.getString("opened"));
            button.setToolTipText(Util.getString("opened_desc"));
            group.add(button);
            button.setSelected(getMode() == OPENED_FILES_MODE);
            button.addActionListener(dispatcher);
            adjustHeight(button);
            openedFiles = button;
        }
        return openedFiles;
    }

    private AbstractButton currentFile;

    private AbstractButton getCurrentFile() {
        if (currentFile == null) {
            JToggleButton button = new JToggleButton(Util.getString("see-file"));
            button.setToolTipText(Util.getString("see-file_hint") + " (c)");  // NOI18N
            group.add(button);
            button.setSelected(getMode() == CURRENT_FILE_MODE);
            button.addActionListener(dispatcher);
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
            button.addActionListener(dispatcher);
            adjustHeight(button);
            gotoPresenter = button;
        }
        return gotoPresenter;
    }

    private JButton filterButton;

    private AbstractButton getFilterButton() {
        
        if (filterButton == null) {

            filterButton = new JButton((isFiltered())?(getFilter().getName()):"No filter");
            filterButton.setToolTipText(Util.getString("filter_hint") + " (f)");  // NOI18N
            adjustHeight(filterButton);
            filterButton.addActionListener(dispatcher);
	    Dimension dim = filterButton.getPreferredSize();
	    dim.width = 300;
	    filterButton.setPreferredSize(dim);
        }
        return filterButton;
    }

    /** Eliminates action listener inner classes. */
    private class Dispatcher implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            if (obj == getGoto()) {
                GoToTaskAction gotoAction = (GoToTaskAction) SystemAction.get(GoToTaskAction.class);
                if (gotoAction.isEnabled()) {
                    gotoAction.performAction();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            } else if (obj == getCurrentFile()) {
                handleCurrentFile();
            } else if (obj == getOpenedFiles()) {
                handleOpenedFiles();
            } else if (obj == getStop()) {
                handleStop();
            } else if (obj == getFolderSelector()) {
                if (recentFolders.size() > 0 || selectedFolder != null) {
                    showFolderSelectorPopup();
                } else {
                    handleSelectFolder();
                }
            } else if (obj == getAllFiles()) {
                handleAllFiles();
            } else if (obj == getRefresh()) {
                handleRefresh();
            } else if (obj == getPrev()) {
                handlePrev();
            } else if (obj == getNext()) {
                handleNext();
            } else if (obj == getFilterCombo()) {
	      if (((JComboBox)obj).getSelectedItem() != null) {
		setFilter(((Filter.ListModelElement)((JComboBox)obj).getSelectedItem()).filter);
	      }
//                  JPopupMenu popup = new JPopupMenu();


// 		 Iterator it = getFilters().iterator();
// 		 while (it.hasNext()) {
// 		   final Filter f = (Filter)it.next();
// 		   JMenuItem item = new JMenuItem(f.getName());
// 		   item.addActionListener(new ActionListener() {
// 		       public void actionPerformed(ActionEvent e) {
// 			 setFilter(f);
// 		       }
// 		     });
// 		   popup.add(item);
// 		 }

// 		 popup.show(filterButton, 0, filterButton.getHeight() - 2);
		 
	    } else if (obj == getFilterIconButton()) {
	      SystemAction.get(FilterSourceTasksAction.class).actionPerformed(e);
	      // updateMiniStatus();
	    }
            
        }
    }

    private final ActionListener dispatcher = new Dispatcher();

    /** Toolbar controls must be smaller*/
    private static void adjustHeight(AbstractButton button) {

        button.setMargin(new Insets(0, 3, 0, 3));

//        if (button instanceof JToggleButton) {
//            if (buttonBorder == null) { // for some l&f's, core will supply one
//                buttonBorder = UIManager.getBorder("nb.tabbutton.border"); //NOI18N
//            }
//
//            if (buttonBorder == null) {
//                JToolBar toolbar = new JToolBar();
//                toolbar.setRollover(true);
//                toolbar.add(button);
//                buttonBorder = button.getBorder();
//                toolbar.remove(button);
//            }
//
//            button.setBorder(buttonBorder);
//        }

        adjustHeightComponent(button);
    }
    
    private static void adjustHeightComponent(JComponent button) {
        // as we cannot get the button small enough using the margin and border...
        if (button.getBorder() instanceof CompoundBorder) { // from BasicLookAndFeel
            Dimension pref = button.getPreferredSize();
            pref.height += TOOLBAR_HEIGHT_ADJUSTMENT;

            // XXX #41827 workaround w2k, that adds eclipsis (...) insted of actual text
            if ("Windows".equals(UIManager.getLookAndFeel().getID())) {  // NOI18N
                pref.width += 9;
            }
            button.setPreferredSize(pref);
        }

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
        toolbar.add(getOpenedFiles());
        toolbar.add(getAllFiles());
        toolbar.add(getFolderSelector());
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(getGoto());
        toolbar.add(getRefresh());
	toolbar.add(getFilterIconButton());
        toolbar.add(getFilterCombo());
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

        private static final long serialVersionUID = 1;

        public void actionPerformed(ActionEvent e) {
            if (getStop().isVisible()) {
                handleStop();
            }
        }
    }

    /** Programatically invokes action retaining UI effect as it was done by user. */
    private static class DelegateAction extends AbstractAction {

        private static final long serialVersionUID = 1;

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

    // used by unit test
    /*package*/ ObservableList discloseModel() {
        return getModel();
    }

    /*package*/ Object discloseTable() {
        return treeTable;
    }

    /*package*/ Node discloseNode() {
        return rootNode;
    }

    /** User clicked all files mode. Start allJob. */
    private void handleOpenedFiles() {

        // we are still in old mode, stop it
        saveFilterState();

        switch (getMode()) {
            case CURRENT_FILE_MODE:
                job.stopBroker();
                job = null;
                break;
            case OPENED_FILES_MODE:
                return;
            case SELECTED_FOLDER_MODE:
                if (background != null) handleStop();
                background = null;
                break;
        }

        // enter new mode

        allJob = SuggestionsBroker.getDefault().startAllOpenedBroker(new SourceTasksProviderAcceptor());

        treeTable.setProperties(createColumns());
        treeTable.setTreePreferredWidth(createColumns()[0].getWidth());
        TaskList list = allJob.getSuggestionList();
        setModel(list);
        loadFilterState(OPENED_FILES_MODE);
        getRefresh().setEnabled(false);
        getTable().requestFocusInWindow();

    }

    /** User clicked selected folder, restore from cache or ask for context */
    private void handleAllFiles() {

        // prepare (& check) new mode parameters

        if (selectedFolder == null) {
            if (recentFolders.size() > 0) {
                showFolderSelectorPopup();
            } else {
                handleSelectFolder();
            }
            // all popup branches and selectors contain handleAllFiles() call
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

        // terminate old mode

        saveFilterState();

        switch (getMode()) {
            case CURRENT_FILE_MODE:
                job.stopBroker();
                job = null;
                break;
            case OPENED_FILES_MODE:
                allJob.stopBroker();
                allJob = null;
                break;
            case SELECTED_FOLDER_MODE:
                break;
        }

        // enter new mode

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
        loadFilterState(SELECTED_FOLDER_MODE);
        getRefresh().setEnabled(true);
        getTable().requestFocusInWindow();

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
            getTable().requestFocusInWindow();
        } catch (DataObjectNotFoundException e) {
            getMiniStatus().setText(Util.getMessage("refresh-err",createLabel(selectedFolder)));
        }
    }

    private static String createLabel(FileObject fo) {
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

    /** Determine toggle buttons status from internal state. */
    private int getMode() {
        if (job != null) return CURRENT_FILE_MODE;
        if (allJob != null) return OPENED_FILES_MODE;
        return SELECTED_FOLDER_MODE;
    }

    /** Stores filter state for current mode */
    private void saveFilterState() {
        int mode = getMode();
        TabState state = tabStates[mode -1];
        if (state == null) {
            tabStates[mode -1] = new TabState();
            state = tabStates[mode -1];
        }
        state.filtered = isFiltered();
        state.filter = getFilter();
    }

    /** Restore filter state from saved one for given mode. */
    private void loadFilterState(int mode) {  // XXX mode param could be replaced by getMode()
//         TabState state = tabStates[mode -1];
//         if (state != null) {
//             setFilter(state.filter, false);
//             setFiltered(state.filtered);
//         } else {
//             setFiltered(false);
//             setFilter(null, false);  // new filter instance request later on in FilterAction
//         }
    }

    // XXX detects listener leaks
    private void releaseWorkaround() {
        ObservableList filter = getModel();
        if (filter instanceof FilteredTasksList) {
            ((FilteredTasksList)filter).byebye();
        }
    }

    /** Switches to current file mode. */
    private void handleCurrentFile() {

        // terminate previous mode

        saveFilterState();
        switch (getMode()) {
            case CURRENT_FILE_MODE:
                return;
            case OPENED_FILES_MODE:
                allJob.stopBroker();
                allJob = null;
                break;
            case SELECTED_FOLDER_MODE:
                if (background != null) handleStop();
                background = null;
                break;
        }

        // enter new mode

        try {
            job = SuggestionsBroker.getDefault().startBroker(new SourceTasksProviderAcceptor());
            treeTable.setProperties(createColumns());
            treeTable.setTreePreferredWidth(createColumns()[0].getWidth());
            setModel(createFilteredList(job.getSuggestionsList()));
            loadFilterState(CURRENT_FILE_MODE);
        } finally {
            // setModel() above triggers IAE in IconManager after gc()
            getRefresh().setEnabled(false);
            getTable().requestFocusInWindow();
            updateMiniStatus();
        }
    }

    /** Let user choose what folder to scan and set selectedFolder field. */
    private void handleSelectFolder() {

        if (background != null) handleStop();
        background = null;

        // prepare content for selector
        final Node content = projectView();
        NodeOperation op = NodeOperation.getDefault();

        try {
            Node[] selected = op.select(Util.getString("sel_title"), Util.getString("sel-head"), content, new NodeAcceptor() {
                public boolean acceptNodes(Node[] nodes) {
                    return nodes.length == 1 && nodes[0] != content && nodes[0].getLookup().lookup(FileObject.class) != null;
                }
            });

            resultsSnapshot = null;
            updateRecent(selectedFolder);
            selectedFolder = (FileObject) selected[0].getLookup().lookup(FileObject.class);

            handleAllFiles();
        } catch (UserCancelException e) {
            // no folders selected keep previous one
        } finally {
            icons = null;
        }
    }

    // handle select folder life-time
    static Node icons = null;

    /** Logical view over opened projects */
    private Node projectView() {

        Children kids = new Children.Array();
        Set projects = new HashSet();

        // XXX there is planned bettre api to get all opened projects
        GlobalPathRegistry registry = GlobalPathRegistry.getDefault();
        Set sourceRoots = registry.getPaths(ClassPath.SOURCE);
        Iterator it = sourceRoots.iterator();
        while (it.hasNext()) {
            ClassPath next = (ClassPath) it.next();
            FileObject[] roots = next.getRoots();
            if (roots == null || roots.length == 0) continue;
            Project project = FileOwnerQuery.getOwner(roots[0]);
            if (projects.contains(project)) continue;
            projects.add(project);

            Sources sources = ProjectUtils.getSources(project);
                SourceGroup[] group =sources.getSourceGroups(Sources.TYPE_GENERIC);
                for (int i=0; i<group.length; i++) {
                    FileObject folder = group[i].getRootFolder();
                    if (folder.isFolder() == false) continue;
                    kids.add(new Node[] {new FolderNode(folder)});
                    // XXX use SourceGroup getter methods
                    if (icons == null) {
                        try {
                            DataObject dobj = DataObject.find(folder);
                            icons = dobj.getNodeDelegate();
                        } catch (DataObjectNotFoundException e) {
                            // ignore
                        }
                    }
                }
        }
        final Node content = new AbstractNode(kids) {
            public void setName(String name) {
                super.setName(name);
                super.setIconBase("org/netbeans/modules/tasklist/docscan/repository");  // NOI18N
            }
        };

        content.setName(Util.getString("projects"));
        return content;
    }

    /** Visualizes folder structure. */
    private static class FolderNode extends AbstractNode {

        private final FileObject fileObject;

        public FolderNode(FileObject fileObject) {
            super(new FolderContent(fileObject), Lookups.singleton(fileObject));
            this.fileObject = fileObject;
        }

        public String getDisplayName() {
            return fileObject.getName();
        }

        public Image getIcon(int type) {
            // XXX how to dynamically get icon (that is subject to L&F)
            if (icons != null) {
                return icons.getIcon(type);
            } else {
                return super.getIcon(type);
            }
        }

        public Image getOpenedIcon(int type) {
            // XXX how to dynamically get icon (that is subject to L&F)
            if (icons != null) {
                return icons.getOpenedIcon(type);
            } else {
                return super.getOpenedIcon(type);
            }
        }

        private static class FolderContent extends Children.Keys {

            private final FileObject fileObject;

            public FolderContent(FileObject fileObject) {
                this.fileObject = fileObject;
            }

            protected void addNotify() {
                FileObject[] fo = fileObject.getChildren();
                setKeys(Arrays.asList(fo));
            }

            protected void removeNotify() {
                setKeys(Collections.EMPTY_SET);
            }

            protected Node[] createNodes(Object key) {
                FileObject fo = (FileObject) key;
                if (fo.isFolder()) {
                    return new Node[] {new FolderNode(fo)};
                } else {
                    return new Node[0];
                }
            }
        }
    }

    private void updateRecent(FileObject fo) {
        addRecent(recentFolders, fo);
    }

    private static void addRecent(java.util.List recentFolders, FileObject fo) {
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

    public org.netbeans.modules.tasklist.core.filter.Filter createFilter() {
      return new SourceTasksFilter(NbBundle.getMessage(SourceTaskNode.class, "new-filter-name"));
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

    private static class TabState {
        boolean filtered;  // filter enabled
        Filter filter; // last filter
    }

    protected void setFiltered() {
      super.setFiltered();
      filterButton.setText(getFilter() == null ? ("No Filter") : (getFilter().getName()));
    }

  
    private JButton filterIconButton = null;
    private AbstractButton getFilterIconButton() {
      if (filterIconButton == null) {
            Icon icon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/filterOperations.gif")); // NOI18N
	    filterIconButton = new JButton(icon);
	    adjustHeight(filterIconButton);
	    filterIconButton.addActionListener(dispatcher);
	    filterIconButton.setMnemonic(Util.getChar("edit_mne"));
      }

      return filterIconButton;
    }

    private JComboBox filterCombo = null;


    private static class FiltersComboModel implements ComboBoxModel {
        
        public FiltersComboModel(FilterRepository rep) {
            this.rep = rep;
            rep.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {onFiltersChanged(evt);}
            });
        }
        
        public void addListDataListener(javax.swing.event.ListDataListener l) {
            if (lsnrs.indexOf(l) == -1) {
                lsnrs.add(l);
            }
        }        
        
        public Object getElementAt(int index) {
            if (elements == null) prepareElements();
            return elements[index];
        }        

        private void prepareElements() {
          elements = new Filter.ListModelElement[rep.size()+1];
	  elements[0] = new Filter.ListModelElement(null);

          Iterator it = rep.iterator();
          for (int i = 1; i < rep.size()+1; i++) 
            elements[i] = new Filter.ListModelElement((Filter)it.next());

	  if (activei >= rep.size()+1) activei = -1;
          
        }

        public Object getSelectedItem() {
	  if (elements == null) prepareElements();
	  if (activei == -1) {
	    Filter f = rep.getActive();
	    for (int i = 0 ; i < elements.length; i++)
	      if (elements[i].filter == f) { activei  = i; break;}
	  }
	  
	  return (activei >= 0)?elements[activei]:null;
        }
        
        public int getSize() {
            return rep.size()+1;
        }
        
        public void removeListDataListener(javax.swing.event.ListDataListener l) {
            lsnrs.remove(l);
        }
        
        public void setSelectedItem(Object anItem) {
            rep.setActive(((Filter.ListModelElement)anItem).filter);
        }
        
        private void onFiltersChanged(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(FilterRepository.PROP_FILTERS)) {
	      elements = null; 
	      fireContentsChanged();
            } else 
            if (evt.getPropertyName().equals(FilterRepository.PROP_ACTIVE_FILTER)) {
                activei = -1;
                fireContentsChanged();
            }
        }
        
        private void fireContentsChanged() {
            ListDataEvent evt = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, Integer.MAX_VALUE);
            Iterator it = lsnrs.iterator();
            while (it.hasNext()) { ((ListDataListener)it.next()).contentsChanged(evt);}          
        }
        
        private LinkedList lsnrs = new LinkedList();
        private Filter.ListModelElement [] elements = null;
        private int activei = -1;
        private FilterRepository rep = null;
    }
    
    private JComponent getFilterCombo() {
        if (filterCombo == null) {
            filterCombo = new JComboBox(new FiltersComboModel(getFilters()));
            filterCombo.addActionListener(dispatcher);
            adjustHeightComponent(filterCombo);
	    Dimension dim = filterCombo.getPreferredSize();
	    dim.width = 150;
	    filterCombo.setPreferredSize(dim);

            filterCombo.setToolTipText(Util.getString("filter_hint") + " (f)");  // NOI18N
        }
        
        return filterCombo;
    }
    


}
