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
import java.awt.FlowLayout;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.core.columns.ColumnsConfiguration;
import org.netbeans.modules.tasklist.core.export.ExportImportFormat;
import org.netbeans.modules.tasklist.core.export.ExportImportProvider;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.FilterRepository;
import org.netbeans.modules.tasklist.usertasks.actions.GoToUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.MoveDownAction;
import org.netbeans.modules.tasklist.usertasks.actions.MoveUpAction;
import org.netbeans.modules.tasklist.usertasks.actions.NewTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.PauseAction;
import org.netbeans.modules.tasklist.usertasks.actions.StartTaskAction;
import org.netbeans.modules.tasklist.usertasks.filter.FilterUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.filter.RemoveFilterUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.filter.UserTaskFilter;
import org.netbeans.modules.tasklist.usertasks.translators.HtmlExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalExportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.ICalImportFormat;
import org.netbeans.modules.tasklist.usertasks.translators.XmlExportFormat;
import org.netbeans.modules.tasklist.usertasks.treetable.ChooseColumnsPanel;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTable;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableModel;
import org.openide.ErrorManager;
import org.openide.actions.FindAction;
import org.openide.cookies.InstanceCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
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
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/** 
 * View showing the todo list items
 *
 * @author Tor Norbye
 */
public class UserTaskView extends TopComponent implements
ExplorerManager.Provider, ExportImportProvider {    
    // List category
    private final static String USER_CATEGORY = "usertasks"; // NOI18N    
    
    private static final String DEFAULT_FILTER_NAME = 
        NbBundle.getMessage(UserTaskView.class, 
        "default-filter-name"); // NOI18N

    /** default columns */
    private static ColumnsConfiguration cc;
    
    private static final long serialVersionUID = 1;

    private static final Image ICON = Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/actions/taskView.gif"); // NOI18N
    
    private static int nextViewId = 0;
    
    private static UserTaskView defview = null;
    
    /** 
     * Keeps track of all UserTaskViews. Access should be synchronized on
     * UserTaskView.class
     */
    private transient static List views = new ArrayList();

    static {
        // repaint the view if the number of working hours per day has
        // changed (spent time, rem. effort and effort columns should be
        // repainted
        Settings.getDefault().addPropertyChangeListener(
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName() != Settings.PROP_HOURS_PER_DAY)
                        return;
                    
                    synchronized(UserTaskView.class) {
                        Iterator it = views.iterator();
                        while (it.hasNext()) {
                            WeakReference wr = (WeakReference) it.next();
                            UserTaskView utv = (UserTaskView) wr.get();
                            utv.repaint();
                        }
                    }
                }
            }
        );
    }
    
    /** 
     * Returns the view with the default task list. The view will be opened if
     * it was not.
     *
     * @return the default view
     */
    public static UserTaskView getDefault() {
	if (defview == null) {
	    defview = new UserTaskView();
            defview.setList(UserTaskList.getDefault());
            defview.showInMode();
	}
	return defview;
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

    /** 
     * Locate a particular view showing the given list 
     * @return found view or null
     */
    public static UserTaskView findView(FileObject file) {
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
    
    private int viewId;
    
    private boolean initialized = false;
    
    private UserTaskList tasklist = null;
    
    private FilterRepository filters = null;
    
    private Filter activeFilter = null;
    
    private ExplorerManager manager;
    
    /** 
     * Construct a new UserTaskView.  
     * NOTE: this is used by the window
     * system when deserializing windows. 
     */
    public UserTaskView() {
        this(NbBundle.getMessage(UserTaskView.class, "TaskViewName"), // NOI18N
            new UserTaskList());
    }

    /**
     * Constructor.
     *
     * @param title view title
     * @param category view's category. This value will be used as the name
     * for a subdirectory of "SystemFileSystem/TaskList/" for columns settings
     */
    private UserTaskView(String title, UserTaskList tasklist) {
        setIcon(ICON);
        setList(tasklist);
        
        init_();

        this.viewId = nextViewId++;

        synchronized (UserTaskView.class) {
            views.add(new WeakReference(this));
        }
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
    public UserTaskView(UserTaskList list) {
	this(list.getFile().getNameExt(), list);              
    }

    /**
     * Returns the component representing the tasks.
     *
     * @return TreeTable
     */
    public TreeTable getTreeTable() {
        return tt;
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
            SystemAction.get(PauseAction.class),
            SystemAction.get(MoveUpAction.class),
            SystemAction.get(MoveDownAction.class)
        };
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
        // super.readExternal(objectInput);

        int ver = objectInput.read();

        objectInput.readObject(); // ignore the UID of selected task

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
        readExternalCore(objectInput);
        int ver = objectInput.read();

        if (ver >= 2) {
            // Read tasklist file name
            String urlString = (String)objectInput.readObject();

            UTUtils.LOGGER.fine("reading url " + urlString); // NOI18N

            if (urlString != null) {
                URL url = new URL(urlString);
                FileObject fo = URLMapper.findFileObject(url);
                if (fo != null) {
                    UserTaskList utl = new UserTaskList();
                    utl.readFile(fo);
                    setList(utl);
                } else {
                    Runnable r = new Runnable() {
                        public void run() {
                            close();
                        }
                    };
                    SwingUtilities.invokeLater(r);
                }
            } else {
                setList(UserTaskList.getDefault());
                defview = this;
            }
        }
        if (ver >= 3) {
            tt.readExpandedState(objectInput);
            tt.select(new TreePath(tt.getTreeTableModel().getRoot()));
        }
        if (ver >= 4) {
            Object sel = objectInput.readObject();
            if (sel != null) {
                TreePath tp = tt.readResolveTreePath(sel);
                if (tp != null)
                    tt.select(tp);
            }
        }
        if (ver >= 5) {
            String uid = (String) objectInput.readObject();
            
            // started task
            if (UserTaskList.getStarted() == null && uid != null) {
                UserTask ut = tasklist.findItem(
                    tasklist.getSubtasks().iterator(), uid);
                if (ut != null)
                    ut.start();
            }
        }
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

        // Write out the UID of the currently selected task, or null if none
        objectOutput.write(4); // SERIAL VERSION

        // Write out the UID of the currently selected task, or null if none
        objectOutput.writeObject(null); // Not yet implemented

        UserTaskList tl = (UserTaskList)getList();
        tl.save(); // Only does something if the todolist has changed...        
        
        // Here I should record a few things; in particular, sorting order, view
        // preferences, etc.
        // Since I'm not doing that yet, let's at a minimum put in a version
        // byte so we can do the right thing later without corrupting the userdir
        objectOutput.write(5); // SERIAL VERSION

        FileObject fo = tl.getFile();
        if (fo != null) {
            // Write out the name of the tasklist
            URL url = URLMapper.findURL(fo, URLMapper.EXTERNAL);
            String urlString = url.toExternalForm();
            objectOutput.writeObject(urlString);
        } else {
            objectOutput.writeObject(null);
        }
        
        tt.writeExpandedState(objectOutput);
        
        // selected task
        TreePath tp = tt.getSelectedPath();
        if (tp != null)
            objectOutput.writeObject(tt.writeReplaceTreePath(tp));
        else
            objectOutput.writeObject(null);
        
        // started task
        if (UserTaskList.getStarted() != null && 
            UserTaskList.getStarted().getList() == tasklist) {
            objectOutput.writeObject(UserTaskList.getStarted().getUID());
        } else {
            objectOutput.writeObject(null);
        }
    }

    /**
     * Start showing new tasklist.
     *
     * @param list new tree
     */
    public void setList(UserTaskList list) {
        tasklist = list;
        if (tt != null) {
            tt.setTreeTableModel(
                new UserTasksTreeTableModel(tasklist, tt.getSortingModel(), 
                getFilter()));
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (tasklist.getFile() != null) {
                    setName(tasklist.getFile().getNameExt());
                    setToolTipText(FileUtil.getFileDisplayName(tasklist.getFile()));
                } else {
                    setName(NbBundle.getMessage(UserTaskView.class, "TaskViewName")); // NOI18N
                }
            }
        }); 
    }
    
    public String toString() { 
        return "UserTaskView(" + getName() + ", " + getList() + ")"; // NOI18N
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
        // although TC.preferredId says that the return value of
        // preferredID must not be unique it does not seem to work
        // so viewId is used to identify the views
        return "org.netbeans.modules.tasklist.usertasks.Window" + viewId; // NOI18N
    }    

    protected void setFiltered() {
        if (getFilter() != null) {
            ((RemoveFilterUserTaskAction) SystemAction.get(
                RemoveFilterUserTaskAction.class)).enable();
        }

        TreeTableModel ttm = tt.getTreeTableModel();
        if (ttm instanceof UserTasksTreeTableModel) {
            ((UserTasksTreeTableModel) ttm).destroy();
        }
        tt.setTreeTableModel(new UserTasksTreeTableModel((UserTaskList) getList(), 
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
        
        FindAction find = (FindAction) FindAction.get(FindAction.class);
        FilterUserTaskAction filter = (FilterUserTaskAction) 
            FilterUserTaskAction.get(FilterUserTaskAction.class);
        getActionMap().put(find.getActionMapKey(), filter);

        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        
        tt = new UserTasksTreeTable(
            getExplorerManager(), getList(), getFilter());
        
        JScrollPane sp = new JScrollPane(tt,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        ChooseColumnsPanel.installChooseColumnsButton(sp);
        
        centerPanel.add(sp, BorderLayout.CENTER);
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

        tt.select(new TreePath(tt.getTreeTableModel().getRoot()));
    }

    public ExplorerManager getExplorerManager() {
        return manager;
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
        if (cc != null)
            return cc;

        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject fo = fs.findResource("TaskList/" + USER_CATEGORY + "/columns.settings"); // NOI18N
        assert fo != null : "Missing config TaskList/" + USER_CATEGORY + "/columns.settings";  // NOI18N

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

    /**
     * Loads filters
     */
    protected void loadFilters() {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject fo = fs.findResource("TaskList/" + USER_CATEGORY + "/filters.settings"); // NOI18N
        assert fo != null : "Missing config TaskList/" + USER_CATEGORY + "/filters.settings";  // NOI18N
        
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
        if (initialized) {
            return;
        }
        initialized = true;
        ExplorerUtils.activateActions(manager, false);
        ExplorerUtils.activateActions(manager, true);
    }


    /** Called when the window is closed. Cleans up. */
    protected void componentClosed() {
        getList().destroy();
        
        storeColumnsConfiguration();

 	Iterator it = views.iterator();
        while (it.hasNext()) {
            WeakReference wr = (WeakReference) it.next();
	    UserTaskView tlv = (UserTaskView) wr.get();
            if (tlv == this) {
                it.remove();
                break;
            }
        }
    }

    protected void componentDeactivated() {
        super.componentDeactivated();
        assert initialized : "#37438 dangling componentDeactivated event, no componentOpened() called at " + this;
        ExplorerUtils.activateActions(manager, false);
        storeColumnsConfiguration();
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
    public UserTaskList getList() {
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
        setFiltered();
    }

    public boolean requestFocusInWindow() {
        super.requestFocusInWindow();
        if (tt != null) {
            tt.requestFocusInWindow();
        }
        return true;
    }

    /* check isSliding
    public void showTask(UserTask item, Annotation annotation) {
        // #35917 do not move focus if in sliding mode
        if (view.getClientProperty("isSliding") == Boolean.TRUE) {  // NOi18N
            l.show(Line.SHOW_SHOW);
        } else {
            l.show(Line.SHOW_GOTO);
        }
    }
     **/
    
    /** 
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
}
