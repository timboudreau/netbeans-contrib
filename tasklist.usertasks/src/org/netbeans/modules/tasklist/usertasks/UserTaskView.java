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

package org.netbeans.modules.tasklist.usertasks;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.TaskViewListener;
import org.netbeans.modules.tasklist.core.columns.ColumnsConfiguration;
import org.netbeans.modules.tasklist.core.export.ExportImportFormat;
import org.netbeans.modules.tasklist.core.export.ExportImportProvider;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.FilterRepository;
import org.netbeans.modules.tasklist.usertasks.actions.GoToUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.PauseAction;
import org.netbeans.modules.tasklist.usertasks.actions.StartTaskAction;
import org.netbeans.modules.tasklist.usertasks.filter.FilterUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.filter.RemoveFilterUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.filter.UserTaskFilter;
import org.netbeans.modules.tasklist.usertasks.filter.UserTaskProperties;
import org.netbeans.modules.tasklist.usertasks.translators.HtmlExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.XmlExportFormat;
import org.netbeans.modules.tasklist.usertasks.treetable.ChooseColumnsPanel;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableModel;
import org.openide.ErrorManager;
import org.openide.actions.FindAction;
import org.openide.cookies.InstanceCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/** 
 * View showing the todo list items
 *
 * @author Tor Norbye
 */
public class UserTaskView extends TopComponent implements UserTaskListener, 
ExplorerManager.Provider, ExportImportProvider {
    // TODO: replace these constants with the equivalents from UserTaskProperties
    public static final String PROP_TASK_DONE = UserTaskProperties.PROPID_DONE;
    public static final String PROP_TASK_DUE = UserTaskProperties.PROPID_DUE_DATE;
    public static final String PROP_TASK_PRIO = "priority"; // NOI18N
    public static final String PROP_TASK_CAT = "category"; // NOI18N
    public static final String PROP_TASK_FILE = "filename"; // NOI18N
    public static final String PROP_TASK_LINE = "line"; // NOI18N
    public static final String PROP_TASK_DETAILS = "details"; // NOI18N
    public static final String PROP_TASK_CREATED = "created"; // NOI18N
    public static final String PROP_TASK_EDITED = "edited"; // NOI18N
    public static final String PROP_TASK_PERCENT = "progress"; // NOI18N
    public static final String PROP_EFFORT = "effort"; // NOI18N
    public static final String PROP_REMAINING_EFFORT = "remainingEffort"; // NOI18N
    public static final String PROP_SPENT_TIME = "spentTime"; // NOI18N
    
    private static final long serialVersionUID = 1;

    private static UserTaskView defview = null;
    
    /** Keeps track of all UserTaskViews */
    private transient static List views = new ArrayList();

    /** 
     * Returns the view with the default task list. The view will be opened if
     * it was not.
     *
     * @return the default view
     */
    public static UserTaskView getDefault() {
	if (defview == null) {
	    defview = new UserTaskView();
	
	    WindowManager wm = WindowManager.getDefault();
	    Mode mode  = wm.findMode("output"); // NOI18N
	    if (mode != null) {
		mode.dockInto(defview);
	    }

	    defview.open();
	    defview.requestVisible(); // requestFocus???
	}
	return defview;
    }

    /** 
     * Return true iff the default view has been created already 
     */
    static boolean defaultViewCreated() {
        return defview != null;
    }
    
    /** 
     * Return the currently active user task view, or null
     *
     * @return current view
     */
    public static UserTaskView getCurrent() {
        TopComponent activated = WindowManager.getDefault().getRegistry().getActivated();
        if (activated instanceof UserTaskView)
            return (UserTaskView) activated;
        else 
            return null;
    }    

    /** Locate a particular view showing the given list */
    static UserTaskView findListView(FileObject file) {
 	Iterator it = views.iterator();
        while (it.hasNext()) {
            WeakReference wr = (WeakReference) it.next();
	    UserTaskView tlv = (UserTaskView) wr.get();
            if (tlv != null && tlv.getList().getFile() == file) 
                return tlv;
        }
        return null;
    }
    
    private UserTasksTreeTable tt;
    
    /** 
     * Construct a new UserTaskView. Most work is deferred to
     * componentOpened. NOTE: this is only for use by the window
     * system when deserializing windows. Client code should not call
     * it. I can't make it protected because then the window system
     * wouldn't be able to get to this. But the code relies on
     * readExternal getting called after this constructor to finalize
     * construction of the window.
     */
    public UserTaskView() {
        this(UserTaskList.getDefault(), true);
    }

    /**
     * @param category view's category. This value will be used as the name
     * for a subdirectory of "SystemFileSystem/TaskList/" for columns settings
     */
    private UserTaskView(String category, String title, Image icon,
                        boolean persistent, UserTaskList tasklist) {
        init_();

        assert category != null : "category == null";

        this.category = category;
        setName(title);
        this.persistent = persistent;
        setIcon(icon);

        registerTaskListView(this);
        setModel(tasklist);
    }

    /** 
     * Construct a new UserTaskView showing a given list. Most work
     * is deferred to componentOpened. NOTE: this is only for use by
     * the window system when deserializing windows. Client code
     * should not call it. I can't make it protected because then
     * the window system wouldn't be able to get to this. But the
     * code relies on readExternal getting called after this
     * constructor to finalize construction of the window.
     */
    public UserTaskView(UserTaskList list, boolean isDefault) {
	this(UserTaskList.USER_CATEGORY,
              isDefault ?
              NbBundle.getMessage(UserTaskView.class,
                    "TaskViewName") : // NOI18N
              list.getFile().getNameExt(),
              Utilities.loadImage(
                    "org/netbeans/modules/tasklist/usertasks/taskView.gif"), // NOI18N
              true,
              list);              
              
	if (isDefault && (defview == null)) {
	    defview = this;
	}
    }

    /**
     * Returns actions for the toolbar.
     *
     * @return actions for the toolbar or null
     */
    public SystemAction[] getToolBarActions() {
        return new SystemAction[] {
            SystemAction.get(NewTaskAction.class),
            SystemAction.get(GoToUserTaskAction.class),
            SystemAction.get(FilterUserTaskAction.class),
            SystemAction.get(RemoveFilterUserTaskAction.class),
            SystemAction.get(StartTaskAction.class),
            SystemAction.get(PauseAction.class)
        };
    }
    
    protected Component createCenterComponent() {
        tt = new UserTasksTreeTable(
            getExplorerManager(), (UserTaskList) getModel(),
            getFilter());
        
        final JScrollPane sp = new JScrollPane(tt,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        ChooseColumnsPanel.installChooseColumnsButton(sp);
        return sp;
    }
    
    public void componentActivated() {
        super.componentActivated();
        assert initialized : 
            "#37438 dangling componentActivated event, no componentOpened()" + 
            " called at " + this;
        ExplorerUtils.activateActions(manager, true);
        RemoveFilterUserTaskAction removeFilter =
                (RemoveFilterUserTaskAction) SystemAction.get(RemoveFilterUserTaskAction.class);
        removeFilter.enable();

        // it's strange I'd expect live listener based solution
        Iterator it = getModel().getTasks().iterator();
        while (it.hasNext()) {
            UserTask next = (UserTask) it.next();
            next.updateLineNumberRecursively();
        }

        // debug Ctrl+C,V,X
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
    
    /** 
     * Read in a serialized version of the tasklist
     * and reads in sorting preferences etc. such that
     * we use the same preferences now.
     * @param objectInput object stream to read from
     * @todo Use a more robust serialization format (not int uid based)
     * @throws IOException
     * @throws ClassNotFoundException  
     */
    public void readExternalCore(ObjectInput objectInput) throws IOException, java.lang.ClassNotFoundException {
        // Don't call super!
        // See writeExternal for justification
        //super.readExternal(objectInput);

        int ver = objectInput.read();
        //assert ver <= 4 : "serialization version incorrect; should be 1 to 4";

        // Read in the UID of the currently selected task, or null if none
        // TODO: Not yet implemented
        String selUID = (String) objectInput.readObject();

        if (ver == 4)
            return;

        int sortingColumn = objectInput.read();
        int sortAscendingInt = objectInput.read();
        boolean ascending = (sortAscendingInt != 0);
        int numVisible = objectInput.read();

        // Account for conversion to unsigned byte in writeExternal
        if (sortingColumn == 255) {
            sortingColumn = -1;
        }

        /*
        System.out.println("readExternal: " + getClass().getName()); // NOI18N
        System.out.println("numVisible is " + numVisible); // NOI18N
        System.out.println("sortingColumn is " + sortingColumn); // NOI18N
        System.out.println("ascending is " + ascending); // NOI18N
        */

        if (numVisible > 0) {
            String[] columns = new String[0];
            int numColumns = columns.length;
            boolean[] columnVisible = new boolean[numColumns];
            for (int i = 0; i < numColumns; i++) {
                columnVisible[i] = false;
            }
            for (int i = 0; i < numVisible; i++) {
                int uid = objectInput.read();
                /* don't do anything. we just read the bytes
                int index;
                if ((uid < numColumns) && (columns[uid].uid == uid)) {
                    // UID == column index. This is the scenario for now
                    // until we delete columns in the middle etc.
                    index = uid;
                } else {
                    // Have to search for the uid
                    index = -1;
                    for (int j = 0; j < numColumns; j++) {
                        if (columns[j].uid == uid) {
                            index = j;
                            break;
                        }
                    }
                }

    if (index != -1) {
                    columnVisible[index] = true;

                    // Set sorting attribute
                    if (sortingColumn == uid) {
                    columns[index].setValue("SortingColumnTTV", // NOI18N
                                Boolean.TRUE);
                    // Descending sort?
                    if (!ascending) {
                        columns[index].setValue("DescendingOrderTTV", // NOI18N
                                    Boolean.TRUE);
                    }
                    }
                }
    */
            }

            //System.out.print("Column visibility: {");
            /* we don't do anything. we just read the bytes
for (int i = 0; i < columns.length; i++) {
//System.out.print(" " + columnVisible[i]);

            // Is this column visible?
            if (columnVisible[i]) {
                columns[i].setValue("InvisibleInTreeTableView", // NOI18N
                        // NOTE reverse logic: this is INvisible
                        Boolean.FALSE);
            } else {
                // Necessary because by default some columns
                // set invisible by default, so I have to
                // override these
                columns[i].setValue("InvisibleInTreeTableView", // NOI18N
                        // NOTE reverse logic: this is INvisible
                        Boolean.TRUE);
            }
            }
*/
//System.out.println(" }");
        }

        if (ver >= 2) {
            category = (String) objectInput.readObject();
            objectInput.readObject(); // ignoring title
            int persistentInt = objectInput.read();
            persistent = (persistentInt != 0);
        } else {
            category = UserTaskList.USER_CATEGORY; // for compatibility only
        }

        synchronized (UserTaskView.class) {
            views.add(new WeakReference(this));
        }
    }
    
    /** 
     * Read in a serialized version of the tasklist
     * and reads in sorting preferences etc. such that
     * we use the same preferences now.
     * @param objectInput object stream to read from
     * @todo Use a more robust serialization format (not int uid based)
     * @throws IOException
     * @throws ClassNotFoundException  
     */    
    public void readExternal(ObjectInput objectInput) throws IOException, java.lang.ClassNotFoundException {
        try {
        readExternalCore(objectInput);
	int ver = objectInput.read();

        if (ver >= 2) {
            // Read tasklist file name
	    String urlString = (String)objectInput.readObject();
            if (urlString != null) {
                URL url = new URL(urlString);
                final FileObject fo = URLMapper.findFileObject(url);
                if (fo != null) {
                	UserTaskList utl = new UserTaskList();
                	utl.readFile(fo);
                    setModel(utl);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setName(fo.getNameExt());
                        }
                    }); 
                }
                // XXX I do extra work here. I read in the global task
                // list each time (default UserTaskView constructor)
                // and then replace it with my own. If the default is large
                // this is significant. Think of a better way to do it.
                if (defview == this) { // work around one such problem
                    defview = null;
                }
            }
        }
        } catch (Throwable t) {
            if (UTUtils.LOGGER.isLoggable(Level.SEVERE))
                t.printStackTrace();
        }
    }

    /** 
     * Write out relevant settings in the window (visible
     * columns, sorting order, etc.) such that they can
     * be reconstructed the next time the IDE is started.
     * @todo Use a more robust serialization format (not int uid based)
     * @param objectOutput Object stream to write to
     * @throws IOException  
     */
    public void writeExternalCore(ObjectOutput objectOutput) throws IOException {
        if (!persistent) {
            ErrorManager.getDefault().log(
                    ErrorManager.INFORMATIONAL,
                    "Warning: This tasklist window (" + getName() + ") should not have been persisted!");
            return;
        }

        // Don't call super.writeExternal.
        // Our parents are ExplorerPanel and TopComponent.
        // ExplorerPanel writes out the ExplorerManager, which tries
        //  to write out the node selection, explored content, etc.
        //  We don't want that. Specific tasklist children, such as
        //  the usertasklist, may want to persist the selection but
        //  most (such as the suggestions view, source scan etc,
        //  don't want this.)
        // TopComponent persists the name and tooltip text; we
        //  don't care about that either.
        //super.writeExternal(objectOutput);

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

        // TODO Additional:
        // String Object: selected task? (should this be a multi-selection?)

        // Write out the UID of the currently selected task, or null if none
        objectOutput.write(4); // SERIAL VERSION

        // Write out the UID of the currently selected task, or null if none
        objectOutput.writeObject(null); // Not yet implemented

        // Write out the window's properties:
        // TODO: do we really need these properties?
        // objectOutput.writeObject(category);
        // objectOutput.writeObject(title);
        // objectOutput.write(persistent ? 1 : 0);
    }

    /** 
     * Write out relevant settings in the window (visible
     * columns, sorting order, etc.) such that they can
     * be reconstructed the next time the IDE is started.
     * @todo Use a more robust serialization format (not int uid based)
     *
     * @param objectOutput Object stream to write to
     * @throws IOException  
     */    
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
	if (!persistent) {
	    System.out.println(
                "INTERNAL ERROR: THIS WINDOW SHOULD NOT HAVE BEEN PERSISTED!");
	    return;
	}

        writeExternalCore(objectOutput);

        UserTaskList tl = (UserTaskList)getList();
        tl.save(); // Only does something if the todolist has changed...        
        
        // Here I should record a few things; in particular, sorting order, view
        // preferences, etc.
        // Since I'm not doing that yet, let's at a minimum put in a version
        // byte so we can do the right thing later without corrupting the userdir
        objectOutput.write(2); // SERIAL VERSION

        FileObject fo = tl.getFile();
        if (fo != null) {
            // Write out the name of the tasklist
            URL url = URLMapper.findURL(fo, URLMapper.EXTERNAL);
            String urlString = url.toExternalForm();
            objectOutput.writeObject(urlString);
        } else {
            objectOutput.writeObject(null);
        }
    }

    /** 
     * Create the root node to be used in this view 
     *
     * @return created node
     */
    protected Node createRootNode() {
        return new UserTaskListNode((UserTaskList) getModel(), null);
    }

    /** Show the given task. "Showing" means getting the editor to
     * show the associated file position, and open up an area in the
     * tasklist view where the details of the task can be fully read.
     *
     * @param item selected task (or null for hiding last)
     * @param annotation annotation to use or null for
     *        default view provided annotation.
     */
    public void showTaskInEditor(UserTask item, UserTaskAnnotation annotation) {
        UserTask task = item;
        UserTask prevTask = null;
        if ((taskMarker != null) &&
            (taskMarker.getTask() instanceof UserTask)) {
            prevTask = (UserTask)taskMarker.getTask();
        }
        if (task.getAnnotation() != null) {
            task.getAnnotation().detach();
            task.setAnnotation(null);
        }
        if (annotation == null) annotation = getAnnotation(item);
        annotationManager.showTask(item, annotation);
        if (prevTask != null) {
            if ((prevTask.getLine() != null) && (task.getAnnotation() == null)) {
                UserTaskAnnotation anno = new UserTaskAnnotation(prevTask, false);
                anno.attach(prevTask.getLine());
                prevTask.setAnnotation(anno);                
            }
        }
    }

    /**
     * Start showing new tasklist.
     *
     * @param list new tree
     */
    protected void setModel(UserTaskList list) {
        hideList();
        tasklist = list;
        getModel().addTaskListener(this);
        UserTaskList utl = (UserTaskList) this.getList();
        utl.showAnnotations(utl.getTasks().iterator());
    }
    
    protected void hideList() {
        UserTaskList prev = getModel();
        if (prev != null) {
            prev.removeTaskListener(this);
        }
        UserTaskList utl = (UserTaskList) this.getModel();
        if (utl != null)
            utl.hideAnnotations(utl.getTasks().iterator());
    }

    public String toString() { 
        return "UserTaskView(" + getName() + ", " + category + ", " + getModel() + ")"; // NOI18N
    }
    
    /** 
     * Create filter template. 
     *
     * @return created filter
     */
    public org.netbeans.modules.tasklist.core.filter.Filter createFilter() {
        return new UserTaskFilter("Simple"); // NOI18N
    }

    protected java.lang.String preferredID() {
        return "org.netbeans.modules.tasklist.usertasks.Window";
    }    

    protected void setFiltered() {
        if (getFilter() != null) {
            ((RemoveFilterUserTaskAction) SystemAction.get(RemoveFilterUserTaskAction.class)).enable();
        }

        TreeTableModel ttm = tt.getTreeTableModel();
        if (ttm instanceof UserTasksTreeTableModel) {
            ((UserTasksTreeTableModel) ttm).destroy();
        }
        tt.setTreeTableModel(new UserTasksTreeTableModel((UserTaskList) getModel(), 
            tt.getSortingModel(), getFilter()));
    }

    /**
     * Store current column configuration to settings
     */
    protected void storeColumnsConfiguration() {
        if (tt == null)
            return;
        
        ColumnsConfiguration columns = getDefaultColumns();
        tt.storeColumns(columns);
    }

    /**
     * Restore column configuration from settings
     */
    protected void loadColumnsConfiguration() {
        if (UTUtils.LOGGER.isLoggable(Level.FINER))
            Thread.dumpStack();
        if (tt == null)
            return;
        
        ColumnsConfiguration cc = getDefaultColumns();
        tt.loadColumns(cc);
    }
    
    /**
     * Expands all nodes
     */
    public void expandAll() {
        tt.expandAll();
    }
    
    /**
     * Collapses all nodes
     */
    public void collapseAll() {
        tt.collapseAll();
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
        assert tp != null : "tp == null"; // NOI18N
        tt.expandPath(tp.getParentPath());
        tt.select(tp);
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
            new XmlExportFormat(), 
            new HtmlExportFormat(),
            new ICalExportFormat()
        };
    }
    
    public ExportImportFormat[] getImportFormats() {
        return new ExportImportFormat[] {
            new ICalImportFormat()
        };
    }    
    
    // copied from org.netbeans.modules.tasklist.core.TaskListView

    /** Expected border height */
    private static int TOOLBAR_HEIGHT_ADJUSTMENT = 4;

    /** Cached toolbar height */
    private static int toolbarHeight = -1;

    public static final String DEFAULT_FILTER_NAME = 
        NbBundle.getMessage(UserTaskView.class, "default-filter-name");


    /**
     * Registers a view
     *
     * @param view a view to be registered
     */
    protected static void registerTaskListView(UserTaskView view) {
        synchronized (UserTaskView.class) {
            views.add(new WeakReference(view));
        }
    }
    
    /** Property "task summary" */
    public static final String PROP_TASK_SUMMARY = "taskDesc"; // NOI18N

    /** String (category of a view) -> ColumnsConfiguration */
    private static Map defColumns = new HashMap();

    transient protected Node rootNode = null;
    
    transient private boolean initialized = false;

    transient protected String category = null;

    protected transient UserTaskList tasklist = null;

    transient protected FilterRepository filters = null;
    transient protected Filter activeFilter = null;
    
    /** Annotation showing the current position */
    transient protected UserTaskAnnotation taskMarker = null;

    private transient Component centerCmp;

    transient private JPanel centerPanel;
    transient private Component northCmp;
    transient private boolean northCmpCreated;
    transient private JLabel miniStatus;

    private transient ExplorerManager manager;

    protected boolean persistent = false;
    
    /**
     * Construct a new TaskListView. Most work is deferred to
     * componentOpened. NOTE: this is only for use by the window
     * system when deserializing windows. Client code should not call
     * it; use the constructor which takes category, title and icon
     * parameters. But the code relies on
     * readExternal getting called after this constructor to finalize
     * construction of the window.
     *
     * todo make private
     */
    public void createTaskListView() {
        init_();
    }

    /**
     * Common part for all constructors
     */
    private void init_() {
        manager = new ExplorerManager();
        ActionMap map = getActionMap();
        map.put(javax.swing.text.DefaultEditorKit.copyAction, 
            ExplorerUtils.actionCopy(manager));
        map.put(javax.swing.text.DefaultEditorKit.cutAction, 
            ExplorerUtils.actionCut(manager));
        map.put(javax.swing.text.DefaultEditorKit.pasteAction, 
            ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));  // NOI18N

        // following line tells the top component which lookup should be associated with it
        associateLookup(ExplorerUtils.createLookup(manager, map));
    }

    public ExplorerManager getExplorerManager() {
        return manager;
    }

    /**
     * Updates the label showing the number of filtered tasks
     */
    public void updateFilterCount() {
        JLabel filterLabel = (JLabel) getMiniStatus();
        if (getFilter() == null) {
            filterLabel.setText("");
        } else {
            int all = TLUtils.getChildrenCountRecursively(rootNode);
            int shown = TLUtils.getChildrenCountRecursively(
                    getExplorerManager().getExploredContext());
            filterLabel.setText(NbBundle.getMessage(UserTaskView.class,
                    "FilterCount", new Integer(shown), new Integer(all))); // NOI18N
        }
    }

    /**
     * Hides/shows component that will be shown over the TTV
     *
     * @param v true = visible
     */
    protected void setNorthComponentVisible(boolean v) {
        if (v) {
            Component cmp = getNorthComponent();
            if (cmp != null) {
                centerPanel.add(cmp, BorderLayout.NORTH);
                centerPanel.validate();
            }
        } else {
            if (northCmp != null && northCmp.getParent() != null) {
                northCmp.getParent().remove(northCmp);
            }
        }
    }

    /**
     * Is the component above the tree table visible?
     * Visible means it is in the TopComponent
     *
     * @return true = yes
     */
    public boolean isNorthComponentVisible() {
        return northCmp != null && northCmp.getParent() != null;
    }

    /**
     * Returns component that will be shown over the TTV
     *
     * @return component or null
     */
    protected Component getNorthComponent() {
        if (!northCmpCreated) {
            northCmp = createNorthComponent();
            northCmpCreated = true;
        }
        return northCmp;
    }

    /**
     * Creates component that will be shown over the TTV
     *
     * @return created component or null
     */
    protected Component createNorthComponent() {
        return new JLabel(""); // NOI18N
    }

    /**
     * Returns component visualizing view status messages.
     */
    protected JLabel getMiniStatus() {
        if (miniStatus == null) {
            miniStatus = createMiniStatus();
        }
        return miniStatus;
    }

    private JLabel createMiniStatus() {
        JLabel ret =  new JLabel();
        Dimension dim = ret.getPreferredSize();
        dim.height = getToolbarHeight();
        ret.setPreferredSize(dim);
        return ret;
    }

    protected final void setMiniStatus(String text) {
        getMiniStatus().setText(text);
    }

    /**
     * Computes vertical toolbar components height that can used for layout manager hinting.
     * @return size based on font size and expected border.
     */
    public static int getToolbarHeight() {

        if (toolbarHeight == -1) {
            BufferedImage image = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = image.createGraphics();
            UIDefaults def = UIManager.getLookAndFeelDefaults();

            int height = 0;
            String[] fonts = {"Label.font", "Button.font", "ToggleButton.font"};      // NOI18N
            for (int i=0; i<fonts.length; i++) {
                Font f = def.getFont(fonts[i]);
                FontMetrics fm = g.getFontMetrics(f);
                height = Math.max(height, fm.getHeight());
            }
            toolbarHeight = height + TOOLBAR_HEIGHT_ADJUSTMENT;
        }

        return toolbarHeight;
    }


    // XXX probably new instance per view would be better
    // or explicit hideTaskInEditor should not hide foreign annotations
    // but showTaskInEditor's call to hideTaskInEditor should hide them
    private UserTaskEditorListener annotationManager = 
        UserTaskEditorListener.getDefault();

    /**
     * Called to indicate that a particular task should be hidden.
     * This typically means that the task was deleted so it should
     * no longer have any visual cues. The task referred to is the
     * most recent task passed to showTaskInEditor.
     */
    public void hideTaskInEditor() {
        annotationManager.hideTask();
    }

    /**
     * Could be overriden to change actions on second toolbar row.
     * @return
     */
    protected SystemAction[] getGlobalToolBarActions() {
        return null;
    }

    /**
     * Returns default configuration for visible columns
     *
     * @return default columns configuration
     */
    protected ColumnsConfiguration getDefaultColumns() {
        ColumnsConfiguration cc = (ColumnsConfiguration) defColumns.get(category);
        if (cc != null)
            return cc;

        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject fo = fs.findResource("TaskList/" + category + "/columns.settings"); // NOI18N
        assert fo != null : "Missing config TaskList/" + category + "/columns.settings";  // NOI18N

        try {
            DataObject dobj = DataObject.find(fo);
            InstanceCookie ic = (InstanceCookie) dobj.getCookie(InstanceCookie.class);
            cc = (ColumnsConfiguration) ic.instanceCreate();
        } catch (ClassNotFoundException e) {
            ErrorManager.getDefault().notify(e);
        } catch (DataObjectNotFoundException e) {
            ErrorManager.getDefault().notify(e);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        return cc;
    }



    protected void loadFilters() {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject fo = fs.findResource("TaskList/" + category + "/filters.settings"); // NOI18N
        assert fo != null : "Missing config TaskList/" + category + "/filters.settings";  // NOI18N
        
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
            ErrorManager.getDefault().notify(e);
        } catch (DataObjectNotFoundException e) {
            ErrorManager.getDefault().notify(e);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
        
    }
    
    /** 
     * Called when the object is opened. Add the GUI.
     * @todo Trigger source listening on window getting VISIBLE instead
     * of getting opened.
     */
    protected void componentOpened() {
        // Register listeningViews, such as the editor support bridge module
        // TODO: Listeners from Lookup will not be collected
        // registerListeners();

        if (initialized) {
            return;
        }
        initialized = true;

        FindAction find = (FindAction) FindAction.get(FindAction.class);
        FilterUserTaskAction filter = (FilterUserTaskAction) 
            FilterUserTaskAction.get(FilterUserTaskAction.class);
        getActionMap().put(find.getActionMapKey(), filter);

        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerCmp = createCenterComponent();
        
        centerPanel.add(centerCmp, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadColumnsConfiguration();
        
        JPanel toolbars = new JPanel();
        toolbars.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

        SystemAction[] actions = getGlobalToolBarActions();
        if (actions != null) {
            JToolBar toolbar = SystemAction.createToolbarPresenter(actions);
            toolbar.setFloatable(false);
            toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);  // NOI18N
            toolbar.setOrientation(JToolBar.VERTICAL);
            toolbars.add(toolbar);
        }

        actions = getToolBarActions();
        if (actions != null) {
            JToolBar toolbar = SystemAction.createToolbarPresenter(actions);
            toolbar.setFloatable(false);
            toolbar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);  // NOI18N
            toolbar.setOrientation(JToolBar.VERTICAL);
            toolbars.add(toolbar);
        }


        add(toolbars, BorderLayout.WEST);

        // Populate the view
        setModel(tasklist);
    }


    /** Called when the window is closed. Cleans up. */
    protected void componentClosed() {
        hideTaskInEditor();
        hideList();

        // Remove any task markers we've added to the editor
        if (unshowItem != null) {
            removedTask(null, unshowItem, 0); // TODO cannot find the parent of unshowItem
        }

        // Unregister listeningViews
        unregisterListeners();
        
        storeColumnsConfiguration();
    }

    protected void componentDeactivated() {
        super.componentDeactivated();
        assert initialized : "#37438 dangling componentDeactivated event, no componentOpened() called at " + this;
        ExplorerUtils.activateActions(manager, false);
        storeColumnsConfiguration();
    }

    private void setRoot() {
        /* TODO: remove
         rootNode = createRootNode();
	// TODO: usertasks module sets the display name of the root node to
        // "Task List"
        rootNode.setDisplayName(getMainColumn(-1).getDisplayName());

        LOGGER.fine("root created " + rootNode);

        Node prevRoot = getExplorerManager().getRootContext();

        if (isFiltered()) {
            // Create filtered view of the tasklist
            FilteredTaskChildren children =
                new FilteredTaskChildren(this, rootNode, getFilter());
            FilterNode n = new FilterTaskNode(rootNode, children, false);
            getExplorerManager().setRootContext(n);
        } else {
            getExplorerManager().setRootContext(rootNode);
        }

        try {
            if (prevRoot != null) prevRoot.destroy();
        } catch (IOException ex) {
            throw new IllegalStateException("Unexpected IOex in " + prevRoot);  // NOI18N
        }
         **/
    }

    /**
     * Returns the root node. It is never a filtered node.
     *
     * @return root node
     */
    public Node getRootNode() {
        return rootNode;
    }

    // XXX #37543 copied from ExplorerPanel ast was deprecated without replacement
    private static HelpCtx getHelpCtx(Node[] sel, HelpCtx def) {
        HelpCtx result = null;
        for (int i = 0; i < sel.length; i++) {
            HelpCtx attempt = sel[i].getHelpCtx();
            if (attempt != null && !attempt.equals(HelpCtx.DEFAULT_HELP)) {
                if (result == null || result.equals(attempt)) {
                    result = attempt;
                } else {
                    // More than one found, and they conflict. Get general help on the Explorer instead.
                    result = null;
                    break;
                }
            }
        }
        if (result != null)
            return result;
        else
            return def;
    }

    private UserTask unshowItem = null;

    /** Show the given todolist item. "Showing" means getting the
     * editor to show the associated file position, and open up an
     * area in the todolist view where the details of the todolist
     * item can be fully read.
     */
    /*
    public void show(Task item, Annotation anno) {
	if (listeningViews != null) {
	    // Stash item so I can notify of deletion -- see TaskViewListener
	    // doc
	    unshowItem = item;
	    int n = listeningViews.size();
	    for (int i = 0; i < n; i++) {
		TaskViewListener tl = (TaskViewListener)listeningViews.get(i);
		tl.showTaskInEditor(item, anno);
	    }
	}
    }
    */

    /** Unshow the given task, if it's the one currently showing */
    /*
    public void unshow(Task item) {
        if (item != unshowItem) {
            return;
        }
	if (listeningViews != null) {
	    // Stash item so I can notify of deletion -- see TaskViewListener
	    // doc
	    unshowItem = null;
	    int n = listeningViews.size();
	    for (int i = 0; i < n; i++) {
		TaskViewListener tl = (TaskViewListener)listeningViews.get(i);
		tl.hideTaskInEditor();
	    }
	}
    }
    */

    /** List of TaskViewListener object listening on the tasklist view
     for visibility, selection, etc. */
    private List listeningViews = null;

    protected final List getListeningViews() {
        return (listeningViews == null) ? Collections.EMPTY_LIST : listeningViews;
    }

    /** 
     * Locate all tasklist listeningViews and add them to our property
     * change listener setup.
     * @todo Decide whether to unregister and reregister repeatedly;
     * this has the advantage of working with dynamic module
     * installs/uninstalls.
     * @todo Consider using a lookup listener such that I'm notified of
     * later additions/removals
     * @todo Consider doing a factory lookup instead of creating a new
     * instance each time. Would allow better coordination between
     * the listener instance and ScanView in the editor package.
     */
    void registerListeners() {
        // TODO Ensure that this doesn't get called from other windows...
        // find TaskViewListeners
        Lookup l = Lookup.getDefault();
        Lookup.Template template = new Lookup.Template(
            UserTaskViewListener.class);
        Iterator it = l.lookup(template).allInstances().iterator();
        if (it.hasNext()) {
            listeningViews = new ArrayList(4);
        }
        while (it.hasNext()) {
            UserTaskViewListener tl = (UserTaskViewListener) it.next();
            listeningViews.add(tl);
        }
    }

    /** Unregister the listeningViews registered in registerListener such
     that they are no longer notified of changes */
    private void unregisterListeners() {
        listeningViews = null;
    }

    // TODO Pick a better name!
    // TODO make method package private! Can't yet - used in editor/
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
     * Called to indicate that a particular task is made current.
     * Do what you can to "select" this task.
     *
     * <p>
     * Dispatches {@link TaskViewListener#showTask} to all registered views.
     */
    public void selectedTask(UserTask item) {
        if (listeningViews != null) {
            // Stash item so I can notify of deletion -- see TaskViewListener
            // doc
            unshowItem = item;
            int n = listeningViews.size();
            for (int i = 0; i < n; i++) {
                UserTaskViewListener tl = (UserTaskViewListener) listeningViews.get(i);
                tl.showTask(item, getAnnotation(item));
            }
        }
    }

    /** 
     * Called to indicate that a particular task has been "warped to".
     * Do what you can to "warp to" this task. Typically means show
     * associated fileposition in the editor.
     */
    public void warpedTask(UserTask item) {
        // XXX currently identical to selectedTask above!
        if (listeningViews != null) {
            // Stash item so I can notify of deletion -- see TaskViewListener
            // doc
            unshowItem = item;
            int n = listeningViews.size();
            for (int i = 0; i < n; i++) {
                UserTaskViewListener tl = (UserTaskViewListener) listeningViews.get(i);
                tl.showTask(item, null);
            }
        }
    }

    public void addedTask(UserTask t) {
        // Nothing to do?
    }

    public void removedTask(UserTask pt, UserTask task, int index) {
        if ((task == unshowItem) && (listeningViews != null)) {
            unshowItem = null;
            int n = listeningViews.size();
            for (int i = 0; i < n; i++) {
                UserTaskViewListener tl = (UserTaskViewListener) listeningViews.get(i);
                tl.hideTask();
            }
        }
    }

    public void structureChanged(UserTask t) {
    }

    /**  
     * Return the tasklist shown in this view 
     */
    public UserTaskList getList() {
        UserTaskList model = getModel();
        return model;
    }

    public UserTaskList getModel() {
        return tasklist;
    }

    // End of stuff taken from TreeTableView

    /**
     * Get the toggle filter for this view. It's
     * applied if {@link #isFiltered} returns true.
     *
     * @return The toggle filter or <code>null</code> if not defined.
     */
    public final Filter getFilter() {
      //      return getFilters().getActive();
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
        setFiltered();
    }

    private boolean lookupAttempted = false;

    private static void invokeLater(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /** When true, we've already warned about the need to wrap */
    private boolean wrapWarned = false;

    /** Try to select nodes in all known views. */
    private void selectNode(Node node) {
        if (node != null) {
            UserTask nextTask = ((UserTaskNode) node).getTask();
            if (nextTask.getLine() != null) {
                UserTaskAnnotation anno = getAnnotation(nextTask);
                if (anno != null) {
                    showTaskInEditor(nextTask, anno);
                }
            }
            select(nextTask); // XXX call EM directly
        }
    }

    /**
     * @param tail if true take the last one from multiple selection
     *        othervise the first one
     * @return last selected or null
     */
    private Node currentlySelected(boolean tail) {
        Node[] selected = getExplorerManager().getSelectedNodes();
        if (selected != null && selected.length != 0) {
            if (tail) {
                return selected[selected.length -1];
            } else {
                return selected[0];
            }
        } else {
            return null;
        }
    }

    /**
     * Return an editor annotation to use to show the given task
     *
     * @return created annotation or null
     */
    protected UserTaskAnnotation getAnnotation(UserTask task) {
        return new UserTaskAnnotation(task, this);
    }

    protected void componentHidden() {
        hideTaskInEditor();
    }

    public void requestActive() {
        super.requestActive();
        if (tt != null) {
            tt.requestFocus();
        }
    }

    /**
     * Returns visible columns
     *
     * @return visible columns
     */
    /*public final ColumnProperty[] getVisibleColumns() {
        TableColumnModel tcm = getTable().getColumnModel();
        ColumnProperty[] all = getColumns();
        List ret = new ArrayList();
        ret.add(all[0]);
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            String title = tcm.getColumn(i).getHeaderValue().toString();
            for (int j = 1; j < all.length; j++) {
                if (title.equalsIgnoreCase(all[j].getDisplayName())) {
                    ret.add(all[j]);
                    break;
                }
            }
        }
        return (ColumnProperty[]) ret.toArray(new ColumnProperty[ret.size()]);
    } TODO: remove*/
}
