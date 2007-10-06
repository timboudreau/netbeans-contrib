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

package org.netbeans.modules.tasklist.usertasks;

import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableModel;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.actions.GoToUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.MoveDownAction;
import org.netbeans.modules.tasklist.usertasks.actions.MoveLeftAction;
import org.netbeans.modules.tasklist.usertasks.actions.MoveRightAction;
import org.netbeans.modules.tasklist.usertasks.actions.MoveUpAction;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.PauseAction;
import org.netbeans.modules.tasklist.usertasks.actions.StartTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.UTCopyAction;
import org.netbeans.modules.tasklist.usertasks.actions.UTCutAction;
import org.netbeans.modules.tasklist.usertasks.actions.UTDeleteAction;
import org.netbeans.modules.tasklist.usertasks.actions.UTPasteAction;
import org.netbeans.modules.tasklist.usertasks.actions.UTSaveAction;
import org.netbeans.modules.tasklist.usertasks.filter.UserTaskFilter;
import org.netbeans.modules.tasklist.usertasks.model.StartedUserTask;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.translators.HistoryTextExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.HtmlExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.TextExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.XmlExportFormat;
import org.netbeans.modules.tasklist.core.table.ChooseColumnsPanel;
import org.netbeans.modules.tasklist.export.ExportImportFormat;
import org.netbeans.modules.tasklist.export.ExportImportProvider;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterAction;
import org.netbeans.modules.tasklist.filter.FilterRepository;
import org.netbeans.modules.tasklist.filter.FilteredTopComponent;
import org.netbeans.modules.tasklist.filter.RemoveFilterAction;
import org.netbeans.modules.tasklist.usertasks.actions.AsListAction;
import org.netbeans.modules.tasklist.usertasks.actions.ClearCompletedAction;
import org.netbeans.modules.tasklist.usertasks.actions.PurgeTasksAction;
import org.netbeans.modules.tasklist.usertasks.actions.ShowTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.UTPasteAtTopLevelAction;
import org.netbeans.modules.tasklist.usertasks.actions.UTPropertiesAction;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableModel;
import org.netbeans.modules.tasklist.usertasks.util.AWTThread;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.actions.FindAction;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.table.UTFlatTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.translators.GoogleICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;
import org.netbeans.modules.tasklist.usertasks.util.RightSideBorder;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.SaveCookie;

/** 
 * View showing the user tasks.
 * 
 * @author Tor Norbye
 * @author tl
 */
public class UserTaskView extends TopComponent implements ExportImportProvider, 
        FileChangeListener, FilteredTopComponent {    
    /**
     * Event for this property will be fired when the state of this view
     * has changed.
     */
    public static final String PROP_STATE = "state";
    
    /** State of the view. */
    public enum State {
        /** all tasks with not computed values in a list */
        FLAT, 
        /** tasks with subtasks */
        TREE;
                
        private static final long serialVersionUID = 1;
    }
    
    // List category
    private final static String USER_CATEGORY = "usertasks"; // NOI18N    
    
    private static final String DEFAULT_FILTER_NAME = 
            NbBundle.getMessage(UserTaskView.class, 
            "default-filter-name"); // NOI18N

    private static final long serialVersionUID = 1;

    private static final Image ICON = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/actions/taskView.gif"); // NOI18N
    
    static {
        // DEBUG:
        // UTUtils.dumpClassLoaders(UserTaskView.class.getClassLoader());
        // UTUtils.dumpClassLoaders(Thread.currentThread().getContextClassLoader());
        
        // repaint the view if the number of working hours per day has
        // changed (spent time, rem. effort and effort columns should be
        // repainted)
        Settings.getDefault().addPropertyChangeListener(
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    String n = e.getPropertyName();
                    if (n == Settings.PROP_WORKING_DAY_END ||
                            n == Settings.PROP_WORKING_DAY_END ||
                            n == Settings.PROP_PAUSE_START ||
                            n == Settings.PROP_PAUSE_END ||
                            n == Settings.PROP_WORKING_DAYS) {
                        UserTaskView[] all;
                        synchronized(UserTaskView.class) {
                            all = UserTaskViewRegistry.
                                    getInstance().getAll();
                        }
                        for (int i = 0; i < all.length; i++) {
                            all[i].repaint();
                        }
                    }
                }
            }
        );
    }
    
    /**
     * Returns the default task list file. Copies template file to the
     * default location if the file does not exist.
     *
     * @return default task list file
     */
    public static FileObject getDefaultFile() throws IOException {
        String name = Settings.getDefault().getExpandedFilename();
        File f = FileUtil.normalizeFile(new File(name));
       
        FileObject fo = FileUtil.toFileObject(f);
        if (fo == null) {
            File dir = f.getParentFile();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException(
                    NbBundle.getMessage(UserTaskList.class,
                        "CannotCreateDir", dir.getAbsolutePath())); // NOI18N
            }
            OutputStream os = new FileOutputStream(f);
            try {
                InputStream is = UserTaskList.class.getResourceAsStream(
                        "/org/netbeans/modules/tasklist/usertasks/tasklist.ics"); // NOI18N
                try {
                    UTUtils.copyStream(is, os);
                } finally {
                    is.close();
                }
            } finally {
                os.close();
            }
            fo = FileUtil.toFileObject(f);
        }
        return fo;
    }
    
    private UserTasksTreeTable tt;
    private JScrollPane scrollPane;
    private boolean initialized = false;
    private UserTaskList tasklist = null;
    private FilterRepository filters = null;
    private Filter activeFilter = null;
    private boolean default_;
    
    /** View specific action for moving the selected task up. */
    public MoveUpAction moveUpAction;
    
    /** View specific action for moving the selected task down. */
    public MoveDownAction moveDownAction;
    
    /** View specific action for moving the selected task left. */
    public MoveLeftAction moveLeftAction;
    
    /** View specific action for moving the selected task left. */
    public MoveRightAction moveRightAction;
    
    /** View specific action for purging completed tasks. */
    public PurgeTasksAction purgeTasksAction;
    
    /** View specific action for clearing completion status. */
    public ClearCompletedAction clearCompletedAction;
    
    /** View specific action for creating a sub-task. */
    public NewTaskAction newTaskAction;
    
    /** View specific action for editing a task. */
    public ShowTaskAction showTaskAction;
    
    /** "Show properties" action. */
    public UTPropertiesAction propertiesAction;
    
    /** "Save" */
    public UTSaveAction saveAction;
    
    /** "Paste at the Top Level" */
    public UTPasteAtTopLevelAction pasteAtTopLevelAction;
            
    private AsListAction asListAction;
    private FileObject file;
    private State state = State.TREE;
    
    /** 
     * Construct a new UserTaskView.  
     * NOTE: this is used by the window
     * system when deserializing windows. 
     */
    public UserTaskView() {
    }

    /**
     * Constructor.
     *
     * @param fo .ics file
     * @param default_ true for the global "User Tasks" view
     */
    @AWTThread
    public UserTaskView(FileObject fo, boolean default_) {
        init(fo, default_);
    }

    /**
     * Changes the state of the view.
     * 
     * @param state state of the view 
     */
    public void setState(State state) {
        if (this.state != state) {
            State old = this.state;
            this.state = state;
            fillTreeTable();
            firePropertyChange(PROP_STATE, old, state);
        }
    }
    
    /**
     * Returns current state of the view.
     * 
     * @return state 
     */
    public State getState() {
        return state;
    }    
    
    /**
     * Returns the file shown in this view.
     * 
     * @return file 
     */
    public FileObject getFile() {
        return file;
    }
    
    public boolean canClose() {
        if (!super.canClose())
            return false;
        DataObject do_;
        try {
            do_ = DataObject.find(file);
        } catch (DataObjectNotFoundException e) {
            UTUtils.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        SaveCookie sc = do_.getCookie(SaveCookie.class);
        if (sc == null)
            return true;
        
        String save = NbBundle.getMessage(UserTaskView.class, "Save");
        String discard = NbBundle.getMessage(UserTaskView.class, "Discard");
        String cancel = NbBundle.getMessage(UserTaskView.class, "Cancel");
        
        NotifyDescriptor d = new NotifyDescriptor(
                NbBundle.getMessage(UserTaskView.class, 
                "FileWasModified", // NOI18N
                FileUtil.getFileDisplayName(file)), 
                NbBundle.getMessage(UserTaskView.class, 
                "Question"), // NOI18N
                NotifyDescriptor.YES_NO_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE,
                new Object[] {save, discard, cancel}, save);
        Object ret = DialogDisplayer.getDefault().notify(d);
        if (ret.equals(save)) {
            try {
                sc.save();
                return true;
            } catch (IOException e) {
                UTUtils.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                return false;
            }
        } else if (ret.equals(discard)) {
            do_.setModified(false);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns the component representing the tasks.
     *
     * @return TreeTable
     */
    public UserTasksTreeTable getTreeTable() {
        return tt;
    }
    
    /**
     * Returns actions for the toolbar.
     *
     * @return actions for the toolbar or null
     */
    public Action[] getToolBarActions() {
        return new Action[] {
            saveAction,
            newTaskAction,
            new GoToUserTaskAction(this),
            null,
            SystemAction.get(FilterAction.class),
            SystemAction.get(RemoveFilterAction.class),
            null,
            asListAction,
            null,
            new StartTaskAction(this),
            PauseAction.getInstance(),
            null,
            moveUpAction,
            moveDownAction,
        };
    }
    
    public void componentActivated() {
        super.componentActivated();

        RemoveFilterAction removeFilter =
            (RemoveFilterAction) SystemAction.get(RemoveFilterAction.class);
        removeFilter.enable();
        UserTaskViewRegistry.getInstance().setLastActivated(this);
        
        tt.requestFocus();
    }
    
    /** 
     * Read in a serialized version of the tasklist
     * and reads in sorting preferences etc. such that
     * we use the same preferences now.
     * @param objectInput object stream to read from
     * @throws IOException
     * @throws ClassNotFoundException  
     */
    public void readExternalCore(ObjectInput objectInput) throws IOException, 
            java.lang.ClassNotFoundException {
        // Don't call super!
        // See writeExternal for justification
        // super.readExternal(objectInput);

        int ver = objectInput.read();

        objectInput.readObject(); // ignore the UID of selected task

        if (ver == 4)
            return;

        int sortingColumn = objectInput.read();
        int sortAscendingInt = objectInput.read();
        int numVisible = objectInput.read();

        // Account for conversion to unsigned byte in writeExternal
        if (sortingColumn == 255) {
            sortingColumn = -1;
        }

        if (numVisible > 0) {
            String[] columns = new String[0];
            int numColumns = columns.length;
            boolean[] columnVisible = new boolean[numColumns];
            for (int i = 0; i < numColumns; i++) {
                columnVisible[i] = false;
            }
            for (int i = 0; i < numVisible; i++) {
                int uid = objectInput.read();
            }
        }

        if (ver >= 2) {
            objectInput.readObject(); // ignoring category
            objectInput.readObject(); // ignoring title
            int persistentInt = objectInput.read();
        }
    }
    
    /** 
     * Read in a serialized version of the tasklist
     * and reads in sorting preferences etc. such that
     * we use the same preferences now.
     * @param objectInput object stream to read from
     * @throws IOException
     * @throws ClassNotFoundException  
     */    
    public void readExternal(ObjectInput objectInput) throws IOException, 
            java.lang.ClassNotFoundException {
        readExternalCore(objectInput);
        int ver = objectInput.read();

        if (ver >= 2) {
            // Read tasklist file name
            String urlString = (String)objectInput.readObject();

            if (urlString != null) {
                URL url = new URL(urlString);
                file = URLMapper.findFileObject(url);
                if (file != null && file.isValid()) {
                    init(file, false);
                } else {
                    Runnable r = new Runnable() {
                        public void run() {
                            close();
                        }
                    };
                    SwingUtilities.invokeLater(r);
                }
            } else {
                init(getDefaultFile(), true);
                UserTaskViewRegistry.getInstance().setDefaultView(this);
            }
        }
        if (ver >= 3) {
            // just reading expanded state without using it
            // for compatibility only
            objectInput.readObject();
        }
        if (ver >= 4) {
            // just reading selected node without using it
            // for compatibility only
            objectInput.readObject();
        }
        if (ver >= 5) {
            String uid = (String) objectInput.readObject();
            
            // started task
            if (StartedUserTask.getInstance().getStarted() == null && uid != null) {
                UserTask ut = tasklist.findItem(
                    tasklist.getSubtasks().iterator(), uid);
                if (ut != null && ut.isStartable())
                    ut.start();
            }
        }
        if (ver >= 6) {
            Map m = (Map) objectInput.readObject();

            // state. 9 April 2007
            Integer state = (Integer) m.get("state");
            if (state == null)
                state = State.TREE.ordinal();
            if (state == State.TREE.ordinal())
                setState(State.TREE);
            else
                setState(State.FLAT);

            // scroll bars positions
            Point p = (Point) m.get("scrollPosition"); // NOI18N
            if (p != null) {
                scrollPane.getVerticalScrollBar().setValue(p.y);
                scrollPane.getHorizontalScrollBar().setValue(p.x);
            }
            
            // columns
            TreeTable.ColumnsConfig cc = 
                    (TreeTable.ColumnsConfig) m.get("columns"); // NOI18N
            if (cc != null) {
                tt.setColumnsConfig(cc);
            }
            
            // active filter 25 March 2005
            String filter = (String) m.get("filter"); // NOI18N
            if (filter != null) {
                Filter f = getFilters().getFilterByName(filter);
                setFilter(f);
            }
            
            // expanded state 25 March 2005
            Object expn = m.get("expandedNodes"); // NOI18N
            if (expn != null) {
                tt.setExpandedNodes(tt.readResolveExpandedNodes(expn));
            }
            
            // selected nodes
            Object seln = m.get("selectedNodes"); // NOI18N
            if (seln != null) {
                tt.select(tt.readResolveExpandedNodes(seln));
            }            
        }
    }

    /** 
     * Write out relevant settings in the window (visible
     * columns, sorting order, etc.) such that they can
     * be reconstructed the next time the IDE is started.
     *
     * @param objectOutput Object stream to write to
     * @throws IOException  
     */    
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        // Don't call super.writeExternal.
        // Our parent is TopComponent.
        // TopComponent persists the name and tooltip text; we
        //  don't care about that either.
        // super.writeExternal(objectOutput);

        // Version 1 format:
        // String: selected uid
        // byte: sortingColumn (255: no sort, otherwise, sorting id)
        // byte: sort ascending (0:false or 1:true)
        // byte: number of visible columns (N)
        // N bytes: visible column uids

        // Version 4 format:
        // String: selected UID
        // String: category
        // String: title
        // byte: persistent

        objectOutput.write(4); // SERIAL VERSION

        // Write out the UID of the currently selected task, or null if none
        objectOutput.writeObject(null); // Unused. Was never implemented

        // Here I should record a few things; in particular, sorting order, view
        // preferences, etc.
        // Since I'm not doing that yet, let's at a minimum put in a version
        // byte so we can do the right thing later without corrupting the userdir
        objectOutput.write(6); // SERIAL VERSION

        UserTaskList tl = getUserTaskList();
        if (!default_) {
            // Write out the name of the tasklist
            URL url = URLMapper.findURL(file, URLMapper.EXTERNAL);
            String urlString = url.toExternalForm();
            objectOutput.writeObject(urlString);
        } else {
            objectOutput.writeObject(null);
        }
        
        // writing null instead of expanded nodes
        // just for compatibility
        objectOutput.writeObject(null);
        
        // writing null instead of the selected task 
        // just for compatibility
        objectOutput.writeObject(null);
        
        // started task
        if (StartedUserTask.getInstance().getStarted() != null && 
            StartedUserTask.getInstance().getStarted().getList() == tasklist) {
            objectOutput.writeObject(StartedUserTask.getInstance().
                getStarted().getUID());
            StartedUserTask.getInstance().start(null);
        } else {
            objectOutput.writeObject(null);
        }

        Map<String, Serializable> m = new HashMap<String, Serializable>();
        
        // scroll bars positions
        Point p = new Point(            
            scrollPane.getHorizontalScrollBar().getValue(), 
            scrollPane.getVerticalScrollBar().getValue());
        m.put("scrollPosition", p); // NOI18N

        // columns
        Serializable cc = tt.getColumnsConfig();
        m.put("columns", cc); // NOI18N
        
        // active filter
        if (getFilter() != null)
            m.put("filter", getFilter().getName()); // NOI18N
        
        // expanded nodes
        m.put("expandedNodes", tt.writeReplaceExpandedNodes(tt.getExpandedNodes())); // NOI18N
        
        // selected nodes
        m.put("selectedNodes", tt.writeReplaceExpandedNodes(tt.getSelectedPaths())); // NOI18N
    
        m.put("state", state.ordinal()); // NOI18N
        
        objectOutput.writeObject(m);
    }

    /**
     * Updates the name of this TC and the tooltip 
     * corresponding to the FileObject.
     */
    private void updateNameAndToolTip() {
        setToolTipText(FileUtil.getFileDisplayName(file));
        if (!default_) {
            setName(file.getNameExt());
        } else {
            setName(NbBundle.getMessage(UserTaskView.class, "TaskViewName")); // NOI18N
        }
    }
    
    public String toString() { 
        return "UserTaskView(" + getName() + ", " + getUserTaskList() + ")"; // NOI18N
    }
    
    /** 
     * Create filter template. 
     *
     * @return created filter
     */
    public org.netbeans.modules.tasklist.filter.Filter createFilter() {
        return new UserTaskFilter("Simple"); // NOI18N
    }

    protected java.lang.String preferredID() {
        return "org.netbeans.modules.tasklist.usertasks.Window"; // NOI18N
    }    

    private void fillTreeTable() {
        if (getFilter() != null) {
            ((RemoveFilterAction) SystemAction.get(
                RemoveFilterAction.class)).enable();
        }

        TreeTableModel ttm = tt.getTreeTableModel();
        if (ttm instanceof UTBasicTreeTableModel) {
            ((UTBasicTreeTableModel) ttm).destroy();
        }
        switch (this.state) {
            case FLAT:
                this.tt.setTreeTableModel(new UTFlatTreeTableModel(
                        getUserTaskList(), tt.getSortingModel(), getFilter()));
                break;
            default:
                this.tt.setTreeTableModel(new UTTreeTableModel(
                        getUserTaskList(), tt.getSortingModel(), getFilter()));
                break;
        }
    }

    /** 
     * Expand nodes and select the particular item, IF the list
     * view is showing
     *
     * @param item The item to be shown
     */
    public void select(UserTask task) {
        if (isShowing() == false) return;
        
        assert tt != null : "tt == null"; // NOI18N
        TreePath tp = tt.findPath(task);
        
        // tp could be null if a filter is in use
        if (tp != null) {
            tt.expandPath(tp.getParentPath());
            tt.select(tp);
        }
    }
    
    /**
     * Makes the specified task visible (scrolls to it)
     *
     * @param task a task
     */
    public void scrollTo(UserTask task) {
        if (isShowing() == false)
            return;
        
        TreePath tp = tt.findPath(task);
        tt.scrollTo(tp);
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return new HelpCtx(
            "org.netbeans.modules.tasklist.usertasks.HOMEID"); // NOI18N
    }    
    
    public ExportImportFormat[] getExportFormats() {
        return new ExportImportFormat[] {
            new TextExportFormat(),
            new XmlExportFormat(), 
            new HtmlExportFormat(),
            new ICalExportFormat(),
            new GoogleICalExportFormat(),
            new HistoryTextExportFormat()
        };
    }
    
    public ExportImportFormat[] getImportFormats() {
        return new ExportImportFormat[] {
            new ICalImportFormat()
        };
    }    

    /**
     * Common part for all constructors.
     *
     * file and default_ variable should be initialized before calling this.
     */
    @AWTThread
    private void init(FileObject file, boolean default_) {
        assert SwingUtilities.isEventDispatchThread();
        
        TaskListDataObject do_ = null;
        try {
            do_ = (TaskListDataObject) DataObject.find(file);
        } catch (DataObjectNotFoundException e) {
            UTUtils.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        
        this.file = file;
        this.default_ = default_;
            
        try {
            tasklist = do_.getUserTaskList();
        } catch (IOException e) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    NbBundle.getMessage(TaskListDataObject.class, 
                    "ErrorReadingFile", e.getMessage())); // NOI18N
            DialogDisplayer.getDefault().notify(nd);
        }
        
        updateNameAndToolTip();
        
        file.addFileChangeListener(this);
        
        setIcon(ICON);
        
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        
        tt = new UserTasksTreeTable(this, getUserTaskList(), getFilter());

        associateLookup(Lookups.fixed(do_, do_.getNodeDelegate(),
                getActionMap()));
        
        configureActions();
        
        scrollPane = new JScrollPane(tt,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        ChooseColumnsPanel.installChooseColumnsButton(scrollPane);
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        Action[] actions = getToolBarActions();
        JToolBar toolbar = UTUtils.createToolbarPresenter(actions);
        toolbar.setFloatable(false);
        toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);  // NOI18N
        toolbar.setOrientation(JToolBar.VERTICAL);
        toolbar.setBorder(new RightSideBorder());
        add(toolbar, BorderLayout.WEST);

        if (tt.getRowCount() > 0)
            tt.getSelectionModel().setSelectionInterval(0, 0);        
        
        new DueTasksNotifier(tasklist);
        
        tasklist.addChangeListener(new AutoScheduler(tasklist));
    }

    /**
     * Loads filters
     */
    protected void loadFilters() {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject fo = fs.findResource("TaskList/" + // NOI18N
                USER_CATEGORY + "/filters.settings"); // NOI18N
        assert fo != null : "Missing config TaskList/" + // NOI18N
                USER_CATEGORY + "/filters.settings";  // NOI18N
        
        try {
            DataObject dobj = DataObject.find(fo);
            InstanceCookie ic = (InstanceCookie) dobj.getCookie(InstanceCookie.class);
            filters = (FilterRepository) ic.instanceCreate();
            
            // 	filters.addPropertyChangeListener(new PropertyChangeListener() {
            // 	    public void propertyChange(PropertyChangeEvent evt) {
            // 	      if (evt.getPropertyName().equals(FilterRepository.PROP_ACTIVE_FILTER)) {
            // 		setFilter(filters.getActive());
            // 		//		setFiltered();
            // 	      }
            // 	    }
            // 	  });
            filters.setActive(null);
            
            // create a default filter if there is none
            if (filters.size() == 0) {
                Filter f = createFilter();
                f.setName(DEFAULT_FILTER_NAME);
                filters.add(f);
            }
            
        } catch (ClassNotFoundException e) {
            UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
        } catch (DataObjectNotFoundException e) {
            UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
        } catch (IOException e) {
            UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
        }
    }
    
    /** 
     * Called when the object is opened. Add the GUI.
     */
    protected void componentOpened() {
        UserTaskViewRegistry.getInstance().viewOpened(this);
        if (initialized) {
            return;
        }
        initialized = true;

        UserTaskViewRegistry.getInstance().setLastActivated(this);
    }


    /** 
     * Called when the window is closed. Cleans up. 
     */
    protected void componentClosed() {
        UserTask started = StartedUserTask.getInstance().getStarted();
        if (started != null && started.getList() == getUserTaskList())
            started.stop();
        
        getUserTaskList().destroy();
        
        UserTaskViewRegistry.getInstance().viewClosed(this);
        
        try {
            TaskListDataObject do_ = (TaskListDataObject) DataObject.find(file);
            do_.release();
        } catch (DataObjectNotFoundException e) {
            UTUtils.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Shows the TC in the output mode and activates it.
     */
    public void showInMode() {
        if (!isOpened()) {
            Mode mode = WindowManager.getDefault().findMode("output"); // NOI18N
            if (mode != null) {
                mode.dockInto(UserTaskView.this);
            }
        }
        open();
        requestVisible();
        requestActive();
    }

    /**  
     * Return the tasklist shown in this view 
     *
     * @return task list
     */
    public UserTaskList getUserTaskList() {
        return this.tasklist;
    }

    /**
     * Get the toggle filter for this view. It's
     * applied if {@link #isFiltered} returns true.
     *
     * @return The toggle filter or <code>null</code> if not defined.
     */
    public final Filter getFilter() {
        return activeFilter;
    }

    /** 
     * Returns the collection of filters assiciated with this view.
     * @return FilterRepository, never null
     */
    public FilterRepository getFilters() {
        if (filters == null) loadFilters();
        assert filters != null : "Missing FilterRepository";  // NOI18N

        return filters;
    }
    
    /** Tests if any real filter is applied. */
    public final boolean isFiltered() {
        return getFilter() != null;
    }

    /**
     * Set the filter to be used (determined by isFiltered) in this view.
     * @param filter The filter to be set, or null, to remove filtering.
     */
    public void setFilter(Filter filter) {         
        if (filter == null || getFilters().contains(filter)) {
            getFilters().setActive(filter);  
        } 

        this.activeFilter = filter;
        fillTreeTable();
    }

    public void requestActive() {
        super.requestActive();
        if (tt != null) {
            tt.requestFocusInWindow();
        }
    }

    public void fileRenamed(org.openide.filesystems.FileRenameEvent fe) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UserTaskView.this.updateNameAndToolTip();
            }
        });
    }

    public void fileAttributeChanged(org.openide.filesystems.FileAttributeEvent fe) {
    }

    public void fileFolderCreated(FileEvent fe) {
    }

    public void fileDeleted(FileEvent fe) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UserTaskView.this.close();
            }
        });
    }

    public void fileDataCreated(FileEvent fe) {
    }

    public void fileChanged(FileEvent fe) {
    }
    
    /* check isSliding
        if (view.getClientProperty("isSliding") == Boolean.TRUE)
     **/
    
    /*
     * debug Ctrl+C,V,X
     *
    private void debugCopyPaste() {
         if (UTUtils.LOGGER.isLoggable(Level.FINE)) {
            ActionMap am = this.getActionMap();
            Object[] actionKeys = am.allKeys();
            for (int i = 0; i < actionKeys.length; i++) {
                Action action = am.get(actionKeys[i]);
                UTUtils.LOGGER.fine(actionKeys[i] + " => " + action.getClass());
            }
            
            UTUtils.LOGGER.fine("printing InputMaps:");
            Component cmp = tt;
            while (cmp != null) {
                UTUtils.LOGGER.fine("checking " + cmp.getClass());
                if (cmp instanceof JComponent) {
                    InputMap keys = ((JComponent) cmp).
                        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                    if (keys != null) {
                        UTUtils.LOGGER.fine("InputMap.class: " + keys.getClass());
                        KeyStroke[] ks = keys.keys();
                        if (ks != null) {
                            for (int i = 0; i < ks.length; i++) {
                                UTUtils.LOGGER.fine(ks[i] + " " + keys.get(ks[i]));
                            }
                        } else {
                            UTUtils.LOGGER.fine("InputMap.keys() == null");
                        }
                    } else {
                        UTUtils.LOGGER.fine("InputMap == null");
                    }
                }
                cmp = cmp.getParent();
            }
        }
    }
    */

    /**
     * Configures actions.
     */
    private void configureActions() {
        moveUpAction = new MoveUpAction(this);
        moveDownAction = new MoveDownAction(this);
        moveLeftAction = new MoveLeftAction(this);
        moveRightAction = new MoveRightAction(this);
        purgeTasksAction = new PurgeTasksAction(this);
        clearCompletedAction = new ClearCompletedAction(this);
        newTaskAction = new NewTaskAction(this);
        showTaskAction = new ShowTaskAction(this);
        propertiesAction = new UTPropertiesAction(this);
        pasteAtTopLevelAction = new UTPasteAtTopLevelAction(this);
        asListAction = new AsListAction(this);
        
        try {
            saveAction = new UTSaveAction(DataObject.find(file));
        } catch (DataObjectNotFoundException e) {
            UTUtils.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 
                InputEvent.CTRL_MASK), "moveUp"); // NOI18N
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 
                InputEvent.CTRL_MASK), "moveDown");  // NOI18N
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put((KeyStroke) newTaskAction.getValue(Action.ACCELERATOR_KEY), 
                "newTask");  // NOI18N        
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
                put((KeyStroke) saveAction.getValue(Action.ACCELERATOR_KEY), 
                "save");  // NOI18N
        
        ActionMap map = getActionMap();
        map.put(javax.swing.text.DefaultEditorKit.copyAction, 
            new UTCopyAction(this));
        map.put(javax.swing.text.DefaultEditorKit.cutAction, 
            new UTCutAction(this));
        map.put(javax.swing.text.DefaultEditorKit.pasteAction, 
            new UTPasteAction(this));

        map.put("delete", new UTDeleteAction(tt));  // NOI18N
        map.put("moveUp", moveUpAction); // NOI18N
        map.put("moveDown", moveDownAction); // NOI18N
        map.put("newTask", newTaskAction); // NOI18N
        map.put("save", saveAction); // NOI18N

        FindAction find = (FindAction) FindAction.get(FindAction.class);
        FilterAction filter = (FilterAction) 
            FilterAction.get(FilterAction.class);
        map.put(find.getActionMapKey(), filter);
    }
}
