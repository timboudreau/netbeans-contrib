/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.docscan;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.accessibility.AccessibleContext;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.HelpCtx;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.UserCancelException;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.windows.TopComponent;


import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterRepository;
import org.netbeans.modules.tasklist.filter.FiltersPanel;
import org.netbeans.modules.tasklist.suggestions.*;
import org.openide.awt.Mnemonics;
import org.openide.util.Cancellable;



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

    // keep consistent with SourceTasksAction icon
    private final static String ICON_PATH = "org/netbeans/modules/tasklist/docscan/todosAction.gif"; // NOI18N

    private final int MAIN_COLUMN_UID = 2352;
    private final int PRIORITY_COLUMN_UID = 7896;
    private final int FILE_COLUMN_UID = 8902;
    private final int LINE_COLUMN_UID = 6646;
    private final int LOCATION_COLUMN_UID = 6512;

    //XXX keep with sync with SuggestionNode, hidden dependency
//     static final String PROP_SUGG_PRIO = "suggPrio"; // NOI18N
//     static final String PROP_SUGG_FILE = "suggFile"; // NOI18N
//     static final String PROP_SUGG_LINE = "suggLine"; // NOI18N
//     static final String PROP_SUGG_CAT = "suggCat"; // NOI18N
//     static final String PROP_SUGG_LOC = "suggLoc"; // NOI18N

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

    //#45006 save action key that was registered by WS
    private Object windowSystemESCActionKey;

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
                Utilities.loadImage(ICON_PATH), // NOI18N
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
                Utilities.loadImage(ICON_PATH), // NOI18N
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

        KeyStroke refresh = KeyStroke.getKeyStroke(KeyEvent.VK_R, 0);
        inputMap.put(refresh, refresh);
        getActionMap().put(refresh, new DelegateAction(getRefresh()));

        KeyStroke editfilter = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.SHIFT_MASK);
        inputMap.put(editfilter, editfilter);
        getActionMap().put(editfilter, new DelegateAction(getFilterIconButton()));

        KeyStroke filtercombo = KeyStroke.getKeyStroke(KeyEvent.VK_F, 0);
        inputMap.put(filtercombo, "filtercombo");
	AbstractAction a = new AbstractAction("filtercombo") {
	    public void actionPerformed(ActionEvent e) {
	      filterCombo.showPopup();
	      filterCombo.requestFocus();
	    }
	  };
	a.setEnabled(true);
        getActionMap().put("filtercombo", a);

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

    protected Component createCenterComponent() {
      Component ret = super.createCenterComponent();

      // after the center component was created, it is save to getTable
      getTable().getAccessibleContext().setAccessibleName(Util.getString("treetable"));
      getTable().getAccessibleContext().setAccessibleDescription(Util.getString("treetable_hint"));

      return ret;
    }



    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(SourceTasksView.class);
    }
    
    protected Node createRootNode() {
      // we need to provide a specialized node factory because we need
      // SourceTaskNodes for SuggestionImpls and don't want to
      // override SuggestionImpl just for that (it would be correct
      // but to much work)
      return new TaskListNode(getModel(), 
			      new TaskListNode.NodeFactory() {
				public Node createNode(Object task) {
				  if (task instanceof SuggestionImpl) {
				    return new SourceTaskNode((SuggestionImpl)task);//, new SourceTaskChildren((SuggestionImpl)task));
				  }
				  else
				    return ((Task)task).createNode()[0];
				}
			      });
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

    protected ColumnProperty getMainColumn(int width) {
      return createColumns()[0];
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
        return new ColumnProperty(
                MAIN_COLUMN_UID, // UID -- never change (part of serialization
                SourceTaskProperties.PROP_TASK,
		true,
                width);
    }


    private ColumnProperty createPriorityColumn(boolean visible, int width) {
        return new ColumnProperty(
                PRIORITY_COLUMN_UID, // UID -- never change (part of serialization
                SourceTaskProperties.PROP_PRIORITY,
                true,
                visible,
                width
        );
    }

    private ColumnProperty createFileColumn(boolean visible, int width) {
        ColumnProperty file = new ColumnProperty(
                FILE_COLUMN_UID, // UID -- never change (part of serialization
		SourceTaskProperties.PROP_FILENAME,
                true,
                visible,
                width
        );
        return file;
    }

    private ColumnProperty createLineColumn(boolean visible, int width) {
        return new ColumnProperty(
                LINE_COLUMN_UID, // UID -- never change (part of serialization
		SourceTaskProperties.PROP_LINE_NUMBER,
                true,
                visible,
                width
        );
    }

    private ColumnProperty createLocationColumn(boolean visible, int width) {
        ColumnProperty location = new ColumnProperty(
                LOCATION_COLUMN_UID, // UID -- never change (part of serialization
		SourceTaskProperties.PROP_LOCATION,
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
        
        updateButtonsState();
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
        super.setIcon(Utilities.loadImage(ICON_PATH));  // NOI18N

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

    public String getToolTipText() {
        int mode = getMode();
        switch(mode) {
            case CURRENT_FILE_MODE: return Util.getString("win-tt-c");
            case OPENED_FILES_MODE: return Util.getString("win-tt-o");
            case SELECTED_FOLDER_MODE: return Util.getString("win-tt-f");
        }
        return null;
    }

    public String toString() {
        return "SourceTasksView@" + hashCode();
    }

    // North component ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private ProgressHandle progress;
    private JButton stop;
    private AbstractButton refresh;
    private JComponent prev;
    private JComponent next;

    private Cancellable cancellable = new Cancellable(){
        public boolean cancel(){
            handleStop();
            return true;
        }
    };
    
    private ProgressHandle getProgress() {
        if (progress == null) {                          
            progress = ProgressHandleFactory.createHandle(Util.getString("searching"), cancellable);
            progress.start();            
        }
        return progress;
    }

    // Misiatus shows selected folder, limit info
    // and filter status
    private void updateMiniStatus() {
        assert SwingUtilities.isEventDispatchThread();
        String prefix = "";
        getMiniStatus().setHorizontalAlignment(SwingConstants.LEFT);
        StringBuffer msg = new StringBuffer(80);
        if (job == null && allJob== null && selectedFolder != null) {
            if (msg.length() > 0) prefix = ", "; // NOI18N
            msg.append(prefix + Util.getMessage("ctx-flag", createLabel(selectedFolder)));
        }

        if (reasonMsg != null && job == null && allJob== null) {
            if (msg.length() > 0) prefix = ", "; // NOI18N
            msg.append(prefix + Util.getMessage("usa-flag", "" + TLUtils.recursiveCount(getModel().getTasks().iterator())));
            getMiniStatus().setToolTipText(reasonMsg);
        } else {
            getMiniStatus().setToolTipText("");
        }
        getMiniStatus().setText(msg.toString());
    }

    /*package*/ AbstractButton getRefresh() {
        if (refresh == null) {
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/refresh.png");  // NOI18N
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText(Util.getString("rescan_hint") + " (r)");  // NOI18N
            button.setEnabled(job == null);
            button.addActionListener(dispatcher);
            adjustToobarButton(button);

            button.getAccessibleContext().setAccessibleName(Util.getString("rescan"));
            button.getAccessibleContext().setAccessibleDescription(Util.getString("rescan_hint"));


            refresh = button;
        }
        return refresh;
    }

    private JComponent getPrev() {
        if (prev == null) {
            JButton button = new JButton("Prev (Shift+F12)");
            button.addActionListener(dispatcher);
            adjustToobarButton(button);
            prev = button;
        }
        return prev;
    }

    private JComponent getNext() {
        if (next == null) {
            JButton button = new JButton("Next (F12)");
            button.addActionListener(dispatcher);
            adjustToobarButton(button);
            next = button;
        }
        return next;
    }


    private AbstractButton allFilesButton;
    private ButtonGroup group = new ButtonGroup();;

    /*package*/ AbstractButton getAllFiles() {
        if (allFilesButton == null) {
            JToggleButton button = new JToggleButton(Util.getString("see-folder"));
            String tooltiptext ; 
            if (selectedFolder == null) {
                tooltiptext = Util.getString("see-folder_hint1");
            } else {
                // restored from settings
              tooltiptext = Util.getString("see-folder_hint2");
            }

            button.setToolTipText(tooltiptext + " (s)");  // NOI18N

            group.add(button);
            button.setSelected(job == null);
            button.addActionListener(dispatcher);
            adjustToobarButton(button);
//            JButton pop = new JButton("V");
//            adjustHeight(pop);
//            JToggleButton both = new JToggleButton();
//            both.setLayout(new BorderLayout());
//            button.setBorder(null);
//            pop.setBorder(null);
//            both.add(button, BorderLayout.WEST);
//            both.add(pop, BorderLayout.EAST);

            button.getAccessibleContext().setAccessibleName(Util.getString("see-folder"));
            button.getAccessibleContext().setAccessibleDescription(tooltiptext);

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
            adjustToobarButton(button);

            button.getAccessibleContext().setAccessibleName(Util.getString("select-folder"));
            button.getAccessibleContext().setAccessibleDescription(Util.getString("selector_hint"));
            // DBG button.setBorder(BorderFactory.createLineBorder(Color.GREEN));
            folderSelector = button;
        }
        return folderSelector;
    }

    class DropDown extends JButton {

        private static final long serialVersionUID = 1;
        private static final int DROPDOWN_WIDTH = 15;

        DropDown() {
            super(new ImageIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/dropdown.gif")));  // NOI18N
            // setMargin(new Insets(10, 0, 9, 0));
        }

        public Dimension getPreferredSize() {
//            Dimension dim = getAllFiles().getPreferredSize();
//            int HEURITICS_FOR_OCEAN_LF = 1;    // get botton aligned with Selected Folder button
            return new Dimension(DROPDOWN_WIDTH, getToolbarHeight());
        }
    }

    private void showFolderSelectorPopup() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem choose = new JMenuItem();
        Mnemonics.setLocalizedText(choose, Util.getString("Lbl_select-folder"));
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
            JMenuItem item = new JMenuItem();
            Mnemonics.setLocalizedText(item, "&0 " + createLabel(selectedFolder));  // NOI18N
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (getAllFiles().isSelected()) return;
                    handleAllFiles();
                }
            });
            popup.add(item);
        }

        while (it.hasNext()) {
            final FileObject fo = (FileObject) it.next();
            if (fo == null || fo.isValid() == false) continue;
            if (fo.equals(selectedFolder)) continue;
            JMenuItem item = new JMenuItem();
            Mnemonics.setLocalizedText(item, "&" + i + " " + createLabel(fo));    // NOI18N
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
            adjustToobarButton(button);

            button.getAccessibleContext().setAccessibleName(Util.getString("opened"));
            button.getAccessibleContext().setAccessibleDescription(Util.getString("opened_desc"));

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
            adjustToobarButton(button);

            button.getAccessibleContext().setAccessibleName(Util.getString("see-file"));
            button.getAccessibleContext().setAccessibleDescription(Util.getString("see-file_hint"));

            currentFile = button;
        }
        return currentFile;
    }

    private AbstractButton gotoPresenter;

    private AbstractButton getGoto() {
        if (gotoPresenter == null) {
            Image image = Utilities.loadImage("org/netbeans/modules/tasklist/docscan/gotosource.png"); // NOI18N
            JButton button = new JButton(new ImageIcon(image));
            button.setToolTipText(Util.getString("goto_hint") + " (e)");  // NOI18N
            button.addActionListener(dispatcher);
            adjustToobarButton(button);

            button.getAccessibleContext().setAccessibleName(Util.getString("goto"));
            button.getAccessibleContext().setAccessibleDescription(Util.getString("goto_hint"));

            gotoPresenter = button;
        }
        return gotoPresenter;
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
	      if (filterCombo.getSelectedItem() != null) {
		setFilter(((Filter.ListModelElement)(filterCombo.getSelectedItem())).filter);
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
	        FilterSourceTasksAction action = (FilterSourceTasksAction) SystemAction.get(FilterSourceTasksAction.class);
            putClientProperty(FiltersPanel.SELECTED_FILTER, getFilterCombo().getSelectedItem());
            action.actionPerformed(e);
	      // updateMiniStatus();
	    }
            
        }
    }



    private final ActionListener dispatcher = new Dispatcher();

    /** Toolbar controls must be smaller and should be tarnsparent*/
    private void adjustToobarButton(final AbstractButton button) {

        button.setMargin(new Insets(0, 3, 0, 3));

        // workaround for Ocean L&F clutter - toolbars use gradient.
        // To make the gradient visible under buttons the content area must not
        // be filled. To support rollover it must be temporarily filled
        if (button instanceof JToggleButton == false) {
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setContentAreaFilled(true);
                    button.setBorderPainted(true);
                }

                public void mouseExited(MouseEvent e) {
                    button.setContentAreaFilled(false);
                    button.setBorderPainted(false);
                }
            });
        }

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

        adjustToolbarComponentSize(button);
    }
    
    private void adjustToolbarComponentSize(JComponent button) {
        // as we cannot get the button small enough using the margin and border...
        if (button.getBorder() instanceof CompoundBorder) { // from BasicLookAndFeel
            Dimension pref = button.getPreferredSize();
            pref.height = getToolbarHeight();

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

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);  // NOI18N
        Border verysoftbevelborder = BorderFactory.createMatteBorder(0,0,1,0,toolbar.getBackground().darker().darker());
        toolbar.setBorder(verysoftbevelborder);
        toolbar.setLayout(new ToolbarLayout());

        toolbar.add(getCurrentFile());
        toolbar.add(getOpenedFiles());
        toolbar.add(getAllFiles());

        // wrapped in JPanel it looks better on Ocean, GTK+ plaf worse on Metal
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);   // Ocean L&F toolbars use gradients
        wrapper.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        wrapper.add(getFolderSelector());
        toolbar.add(wrapper);

        //JSeparator separator = new JSeparator(JSeparator.VERTICAL);  // Ocean L&F doe snot support vertical separators
        JPanel separator = new JPanel();
        separator.setOpaque(false);  // Ocean L&F toolbars use gradients
        toolbar.add(separator);
        toolbar.add(getGoto());
        toolbar.add(getRefresh());
        toolbar.add(getFilterIconButton());
        toolbar.add(getFilterCombo());

        //JSeparator separator2 = new JSeparator(JSeparator.VERTICAL);  // Ocean L&F doe snot support vertical separators
        JPanel separator2 = new JPanel();
        separator2.setOpaque(false);   // Ocean L&F toolbars use gradients
        toolbar.add(separator2);
        toolbar.add(getMiniStatus());        

        // Eliminates double height toolbar on Metal L&F
        toolbar.setPreferredSize(new Dimension(Integer.MAX_VALUE, getToolbarHeight()));
        return toolbar;

    }

    /**
     * Hardcoded toolbar layout. It eliminates need
     * for nested panels their look is hardly maintanable
     * accross several look and feels
     * (e.g. strange layouting panel borders on GTK+).
     */
    private class ToolbarLayout implements LayoutManager {

        public void removeLayoutComponent(Component comp) {
        }

        public void layoutContainer(Container parent) {
            Dimension max = parent.getSize();
            int label = max.width - preferredLayoutSize(parent).width;

            int components = parent.getComponentCount();
            int horizont = 0;
            for (int i = 0; i<components; i++) {
                JComponent comp = (JComponent) parent.getComponent(i);
                if (comp.isVisible() == false) continue;
                comp.setLocation(horizont, 0);
                Dimension pref = comp.getPreferredSize();
                int width = pref.width;
                if (comp == getMiniStatus()) {
                    width = label;
                }
                comp.setSize(width, getToolbarHeight() - 1);  // 1 verySoftBevel compensation
                horizont += width;
            }
        }

        public void addLayoutComponent(String name, Component comp) {
        }

        public Dimension minimumLayoutSize(Container parent) {
            int components = parent.getComponentCount();
            int horizont = 0;
            for (int i = 0; i<components; i++) {
                Component comp = parent.getComponent(i);
                if (comp.isVisible() == false) continue;
                comp.setLocation(horizont, 0);
                Dimension pref = comp.getPreferredSize();
                horizont += pref.width;
            }

            return new Dimension(horizont, getToolbarHeight());
        }

        public Dimension preferredLayoutSize(Container parent) {
            return getMinimumSize();
        }

    }


    // Monitor impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private int realFolders = 0;
    private int estimatedFolders = -1;

    /** Currently scanned folder or null. */
    private FileObject scannedFolder;

    public void estimate(final int estimate) {
        scannedFolder = null;
        estimatedFolders = estimate;
        
        if (estimate == -1) {            
            getProgress().switchToIndeterminate ();
        } else {
            getProgress().switchToDeterminate(estimatedFolders);                    
        }
                
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {                
                if (estimate == -1) {
                    Cache.load(); // hide this possibly long operation here                    
                }
            }
        });
    }

    public void scanStarted() {
        
        realFolders = 0;
        reasonMsg = null;
                
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {                                
                getRefresh().setEnabled(false);
            }
        });

    }

    public void folderEntered(final FileObject folder) {
        scannedFolder = folder;
               
        if (estimatedFolders > 0) {
            realFolders++;
            if(realFolders > estimatedFolders){
                estimatedFolders = realFolders;
                getProgress().switchToDeterminate(estimatedFolders);
            }
            getProgress().progress(realFolders);
        }
        
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
        
        estimatedFolders = -1;
        progressFinished();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {                
                getRefresh().setEnabled(job == null);
                updateMiniStatus();
            }
        });
    }

    private void progressFinished(){
        getProgress().finish();
        progress = null;                            
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
        // TODO should stop current job and restore it on component showing
        // it requires separate mode field instead of deriving it from job fields
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
        releaseWorkaround();

        // enter new mode

        allJob = SuggestionsBroker.getDefault().startAllOpenedBroker(new SourceTasksProviderAcceptor());

        treeTable.setProperties(createColumns());
        treeTable.setTreePreferredWidth(createColumns()[0].getWidth());
        TaskList list = allJob.getSuggestionList();
        setModel(list);
        loadFilterState(OPENED_FILES_MODE);
        getRefresh().setEnabled(false);
        getTable().requestFocusInWindow();

        updateMiniStatus();
        putClientProperty(JComponent.TOOL_TIP_TEXT_KEY, getToolTipText());
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

        putClientProperty(JComponent.TOOL_TIP_TEXT_KEY, getToolTipText());
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
            return "..." + path.substring(path.length() - 57);
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
            putClientProperty(JComponent.TOOL_TIP_TEXT_KEY, getToolTipText());
        }
    }

    /** Let user choose what folder to scan and set selectedFolder field. */
    private void handleSelectFolder() {

        if (background != null) handleStop();
        background = null;

        // prepare content for selector
        final Node content = Choosers.projectView();
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
            Choosers.icons = null;
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

    private void updateButtonsState() {
        switch (getMode()) {
            case CURRENT_FILE_MODE: 
                currentFile.setSelected(true); 
                openedFiles.setSelected(false); 
                folderSelector.setSelected(false); 
                break;
            case OPENED_FILES_MODE: 
                currentFile.setSelected(false); 
                openedFiles.setSelected(true); 
                folderSelector.setSelected(false); 
                break;
                
            case SELECTED_FOLDER_MODE:
                currentFile.setSelected(false); 
                openedFiles.setSelected(false); 
                folderSelector.setSelected(true); 
                break;
        }
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

    public org.netbeans.modules.tasklist.filter.Filter createFilter() {
      return new SourceTasksFilter(NbBundle.getMessage(SourceTaskNode.class, "new-filter-name"));
    }

    public AccessibleContext getAccessibleContext() {
        AccessibleContext ret = super.getAccessibleContext();
        switch (getMode()) {
            case CURRENT_FILE_MODE:
                ret.setAccessibleDescription(Util.getString("file_desc11"));
                break;
            case OPENED_FILES_MODE:
                ret.setAccessibleDescription(Util.getString("opened_desc11"));
                break;
            case SELECTED_FOLDER_MODE:
                ret.setAccessibleDescription(Util.getMessage("folder_desc11", createLabel(selectedFolder)));
                break;
        }
        return ret;
    }

    private static class TabState {
        boolean filtered;  // filter enabled
        Filter filter; // last filter
    }

    protected void setFiltered() {
      super.setFiltered();
    }

  
    private JButton filterIconButton = null;

    private AbstractButton getFilterIconButton() {
      if (filterIconButton == null) {
            Icon icon = new ImageIcon(Utilities.loadImage("org/netbeans/modules/tasklist/docscan/filter.png")); // NOI18N
	    filterIconButton = new JButton(icon);
	    adjustToobarButton(filterIconButton);
            filterIconButton.setToolTipText(Util.getString("filter_hint") + " (shift+f)");  // NOI18N
	    filterIconButton.addActionListener(dispatcher);

            filterIconButton.getAccessibleContext().setAccessibleName(Util.getString("filter"));
            filterIconButton.getAccessibleContext().setAccessibleDescription(Util.getString("filter_hint"));

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
            List clone = null;
            synchronized(lsnrs) {
                clone = new ArrayList(lsnrs);
            }
            Iterator it = clone.iterator();
            while (it.hasNext()) {
                ((ListDataListener)it.next()).contentsChanged(evt);
            }          
        }
        
        private List lsnrs = Collections.synchronizedList(new LinkedList());
        private Filter.ListModelElement [] elements = null;
        private int activei = -1;
        private FilterRepository rep = null;
    }
    
    private JComboBox getFilterCombo() {
        if (filterCombo == null) {
            filterCombo = new JComboBox(new FiltersComboModel(getFilters()));
            filterCombo.addActionListener(dispatcher);
            adjustToolbarComponentSize(filterCombo);
            Dimension dim = filterCombo.getPreferredSize();
            dim.width = 150;
            dim.height = getToolbarHeight();
            filterCombo.setPreferredSize(dim);

            filterCombo.setToolTipText(Util.getString("choose-filter_hint") + " (f)");  // NOI18N
            filterCombo.getAccessibleContext().setAccessibleName(Util.getString("choose-filter"));
            filterCombo.getAccessibleContext().setAccessibleDescription(Util.getString("choose-filter_hint"));

        }
        
        return filterCombo;
    }


}
