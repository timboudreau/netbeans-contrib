/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;


import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.netbeans.modules.tasklist.core.columns.ColumnsConfiguration;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.netbeans.modules.tasklist.core.filter.RemoveFilterAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

import org.openide.explorer.view.TreeTableView;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.awt.StatusDisplayer;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.actions.FindAction;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;
import org.openide.windows.TopComponent;
import org.openide.util.actions.CallbackSystemAction;


/**
 * View showing the task list items
 * @author Tor Norbye, Tim Lebedkov, Trond Norbye
 * @todo Figure out why the window system sometimes creates multiple objects
 *       from this class
 */
public abstract class TaskListView extends TopComponent
        implements TaskListener, PropertyChangeListener, ExplorerManager.Provider, Lookup.Provider, TaskSelector {

    /** Property "task summary" */
    public static final String PROP_TASK_SUMMARY = "taskDesc"; // NOI18N

    /** String (category of a view) -> ColumnsConfiguration */
    private static Map defColumns = new HashMap();

    transient protected TaskNode rootNode = null;
    transient protected MyTreeTable treeTable;

    transient public ColumnProperty[] columns = null;

    transient private boolean initialized = false;

    transient protected String category = null;

    private transient ObservableList tasklist = null;

    private transient boolean filterEnabled =  false;
    transient protected Filter filter = null;

    /** Annotation showing the current position */
    transient protected TaskAnnotation taskMarker = null;

    transient protected Component centerCmp;

    transient private JPanel centerPanel;
    transient private Component northCmp;
    transient private boolean northCmpCreated;
    transient private JLabel miniStatus;

    private transient ExplorerManager manager;

    /** 
     * Construct a new TaskListView. Most work is deferred to
     * componentOpened. NOTE: this is only for use by the window
     * system when deserializing windows. Client code should not call
     * it; use the constructor which takes category, title and icon
     * parameters. I can't make it protected because then the window
     * system wouldn't be able to get to this. But the code relies on
     * readExternal getting called after this constructor to finalize
     * construction of the window.
     */
    public TaskListView() {
        initExplorerManager();
    }

    /**
     * @param category view's category. This value will be used as the name
     * for a subdirectory of "SystemFileSystem/TaskList/" for columns settings
     */
    public TaskListView(String category, String title, Image icon,
                        boolean persistent, ObservableList tasklist) {
        initExplorerManager();
        
        assert category != null : "category == null";
        
        this.category = category;
        setName(title);
        this.persistent = persistent;
        setIcon(icon);

        if (persistent) {
            // Only persist window info if the window is opened on exit.
            putClientProperty("PersistenceType", "OnlyOpened"); // NOI18N
        } else {
            putClientProperty("PersistenceType", "Never"); // NOI18N
        }
        initList(tasklist);
    }

    // replacement for subclassing ExplorerPanel
    private void initExplorerManager() {
        manager = new ExplorerManager();
        ActionMap map = getActionMap();
        map.put(javax.swing.text.DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(javax.swing.text.DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(javax.swing.text.DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));  // NOI18N
    }

    protected final void initList(ObservableList list) {
        assert tasklist == null; // call from readExterdnal only
        this.tasklist = list;

        synchronized (TaskListView.class) {
            if (views == null) {
                views = new HashMap();
            }
            views.put(category, this);
        }
    }

    public ExplorerManager getExplorerManager() {
        assert manager != null : 
            "getExplorerManager() called before initExplorerManager()"; // NOI18N
        return manager;
    }

    public Lookup getLookup() {
        return ExplorerUtils.createLookup(getExplorerManager(), getActionMap());
    }

    /**
     * Updates the label showing the number of filtered tasks
     */
    public void updateFilterCount() {
        JLabel filterLabel = (JLabel) getMiniStatus();
        if (filterEnabled == false) {
            filterLabel.setText("");
        } else {
            int all = TLUtils.getChildrenCountRecursively(rootNode);
            int shown = TLUtils.getChildrenCountRecursively(
                    getExplorerManager().getExploredContext());
            filterLabel.setText(NbBundle.getMessage(TaskListView.class,
                    "FilterCount", new Integer(shown), new Integer(all))); // NOI18N
        }
    }

    /**
     * Hides/shows component that will be shown over the TTV
     *
     * @param v true = visible
     */
    public void setNorthComponentVisible(boolean v) {
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
    public Component getNorthComponent() {
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
        return new JLabel();
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
        return new JLabel();
    }

    protected final void setMiniStatus(String text) {
        getMiniStatus().setText(text);
    }

    /**
     * Override default ExplorerPanel behaviour.
     * It was set by explorer manager with setName to "Explorer[<root name>]"
     */
    protected void updateTitle() {
    }

    /** Show the given task. "Showing" means getting the editor to
     * show the associated file position, and open up an area in the
     * tasklist view where the details of the task can be fully read.
     *
     * @param item selected task (or null for hiding last)
     * @param annotation annotation to use or null for
     *        default view provided annotation.
     */
    public void showTask(Task item, TaskAnnotation annotation) {
        hideTask();
        if (item == null) {
            return;
        }

        StatusDisplayer.getDefault().setStatusText(item.getSummary());
        Line l = item.getLine();
        if (l != null) {
            if (taskMarker == null) {
                if (annotation != null) {
                    taskMarker = annotation;
                } else {
                    taskMarker = getAnnotation(item);
                }
            } else {
                taskMarker.detach();
            }
            if (l != null) {
                taskMarker.attach(l);
                // Show the line!
                l.show(Line.SHOW_GOTO);
            } else {
                taskMarker = null;
            }
        }
    }

    /**
     * Called to indicate that a particular task should be hidden.
     * This typically means that the task was deleted so it should
     * no longer have any visual cues. The task referred to is the
     * most recent task passed to showTask.
     */
    public void hideTask() {
        if (taskMarker != null) {
            taskMarker.detach();
            taskMarker = null;
        }
    }

    /**
     * Could be overridden to change actions for the toolbar.
     * @return actions for the toolbar or null
     */
    public SystemAction[] getToolBarActions() {
        return null;
    }

    /**
     * Could be overriden to change actions on second toolbar row.
     * @return
     */
    public SystemAction[] getGlobalToolBarActions() {
        return null;
    }

    public void changedTask(Task task) {
        // Part of fix for #27670
        // It leads to an exception after editing task's description
        // directly in the treetable (see bugzilla)
        //if(filter != null)
        //setRoot();
    }

    /**
     * Returns the <code>TableColumnModel</code> that contains all column information
     * of the table header.
     *
     * @return model for columns
     */
    public TableColumnModel getColumnModel() {
        return treeTable.getHeaderModel();
    }

    /**
     * Returns columns definition for the TreeTable view.
     *
     * @return columns
     */
    public final ColumnProperty[] getColumns() {
        if (columns == null)
            columns = createColumns();
        return columns;
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


    /** Called when the object is opened. Add the GUI.
     @todo Trigger source listening on window getting VISIBLE instead
     of getting opened.
     */
    protected void componentOpened() {
        // Register listeners, such as the editor support bridge module
        // TODO: Listeners from Lookup will not be collected
        // registerListeners();

        if (initialized) {
            return;
        }
        initialized = true;

        loadColumnsConfiguration();

        FindAction find = (FindAction) FindAction.get(FindAction.class);
        FilterAction filter = (FilterAction) FilterAction.get(FilterAction.class);
        getActionMap().put(find.getActionMapKey(), filter);

        setLayout(new BorderLayout());

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerCmp = createCenterComponent();
        centerPanel.add(centerCmp, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

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
        showList();

    }

    /**
     * Creates the component that will be placed in the middle of the TC
     *
     * @return created component != null
     */
    protected Component createCenterComponent() {
        treeTable = new MyTreeTable();
        //treeTable.setProperties(createColumns());
        if (columns == null) {
            columns = createColumns();
        }
        treeTable.setProperties(columns);

        // Column widths
        // How the heck do I set the width of the leftmost column???
        //   treeTable.setTableColumnPreferredWidth(0, 800); // Description
        // AHHHH... there's a separate method for that:
        treeTable.setTreePreferredWidth(columns[0].getWidth());

        treeTable.getTable().getTableHeader().setReorderingAllowed(false);
        treeTable.setRootVisible(false);
        treeTable.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        treeTable.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return treeTable;
    }

    /** Called when the window is closed. Cleans up. */
    protected void componentClosed() {
        hideTask();
        hideList();

        // Remove any task markers we've added to the editor
        if (unshowItem != null) {
            removedTask(null, unshowItem); // TODO cannot find the parent of unshowItem
        }

        // Unregister listeners
        unregisterListeners();
    }

    protected void componentActivated() {
        super.componentActivated();
        assert initialized : "#37438 dangling componentActivated event, no componentOpened() called at " + this;
        installJumpActions(true);
        ExplorerUtils.activateActions(manager, true);
        RemoveFilterAction removeFilter =
                (RemoveFilterAction) SystemAction.get(RemoveFilterAction.class);
        removeFilter.enable();
    }

    protected void componentDeactivated() {
        super.componentDeactivated();
        assert initialized : "#37438 dangling componentDeactivated event, no componentOpened() called at " + this;
        ExplorerUtils.activateActions(manager, true);
        storeColumnsConfiguration();
    }

    /**
     * Store current column configuration to settings
     */
    protected void storeColumnsConfiguration() {
        ColumnsConfiguration columns = getDefaultColumns();
        columns.loadFrom(this);
    }

    /**
     * Restore column configuration from settings
     */
    protected void loadColumnsConfiguration() {
        ColumnsConfiguration cc = getDefaultColumns();
        cc.configure(this);
    }

    /** Create the root node to be used in this view */
    abstract protected TaskNode createRootNode();

    protected void showList() {
        getModel().addListener(this);
        setRoot();
    }

    /**
     * Start showing new tasklist.
     */
    protected final void showList(ObservableList list) {
        if (list == getModel()) return;
        hideList();
        tasklist = list;
        showList();
    }

    private void setRoot() {
        // todo remove
        Thread.dumpStack();
        
        //tasklist.getRoot();
        rootNode = createRootNode();

        if (filterEnabled) {
            // Create filtered view of the tasklist
            TaskNode.FilteredChildren children =
                    new TaskNode.FilteredChildren(this, rootNode, filter);
            FilterNode n = new TaskNode.FilterTaskNode(rootNode, children, false);
            getExplorerManager().setRootContext(n);
        } else {
            getExplorerManager().setRootContext(rootNode);
        }

        // Select the root node, such that the empty tasklist has
        // a context menu - but only if there are no items in the list
        /* TODO not necessary anymore?
         if (!tasklist.getRoot().hasSubtasks()) {
            // See http://www.netbeans.org/issues/show_bug.cgi?id=27696
            Node[] sel = new Node[] { getExplorerManager().getRootContext() };
            try {
                getExplorerManager().setSelectedNodes(sel);
            } catch (PropertyVetoException e) {
                ErrorManager.getDefault().notify(
                                           ErrorManager.INFORMATIONAL, e);
            }
        }*/
    }

    protected void hideList() {
        ObservableList prev = getModel();
        if (prev != null) {
            prev.removeListener(this);
        }
    }

    public Node getRootNode() {
        // TODO - make sure you use the filternode etc. when appropriate!
        return rootNode;
    }

    public void setRootNode(TaskNode r) {
        // todo remove
        Thread.dumpStack();
        
        rootNode = r;
        getExplorerManager().setRootContext(rootNode);

        // TODO - update filter!
    }

    /** Ensures that even if no node is selected in the view,
     * some help specific to this view will still be available.
     */
    public HelpCtx getHelpCtx() {
        return getHelpCtx(
                getExplorerManager().getSelectedNodes(),
                getExplorerManager().getRootContext().getHelpCtx()
        );
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


    /**
     * Returns table with tasks.
     *
     * @return table
     */
    public JTable getTable() {
        return treeTable.getTable();
    }

    // Workaround - is this no longer necessary?
    // No, I can fish the JTable out of the TreeTableView - it's
    // a JScrollPane, so its getViewport().getView() will be the
    // JTable!
    protected static class MyTreeTable extends TreeTableView {
        MyTreeTable() {
            JTable table = treeTable;
            table.setShowHorizontalLines(true);
            table.setShowVerticalLines(false);

            //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            // No, I can use TreeTableView.setTableAutoResizeMode(int) for this

            // No white clipping lines on selected table rows: reduce separator
            // to 0. That means text may touch but HIE prefers this.
            table.setIntercellSpacing(new Dimension(0, table.getRowMargin()));
            double height = new JLabel("Z").getPreferredSize().getHeight();
            int intheight = (int) height;
            table.setRowHeight(intheight);

            /* Issue 23993 was fixed which probably makes this unnecessary:
// Grid color: HIE's asked for (230,230,230) but that seems troublesome
// since we'd have to make a GUI for customizing it. Instead, go
// with Metal's secondary2, since for alternative UIs this will continue
// to look good (and it's customizable by the user). And secondary2
// is close to the request valued - it's (204,204,204).
table.setGridColor((java.awt.Color)javax.swing.UIManager.getDefaults().get("Label.background")); // NOI18N
            */
        }

        public JTree getTree() {
            return tree;
        }

        public JTable getTable() {
            return treeTable;
        }

        public TableModel getModel() {
            // XXX it's private: return tableModel;
            return (TableModel) treeTable.getModel();
        }

        TableColumnModel getHeaderModel() {
            return treeTable.getTableHeader().getColumnModel();
        }
    }


    /** Read in a serialized version of the tasklist
     * and reads in sorting preferences etc. such that
     * we use the same preferences now.
     * @param objectInput object stream to read from
     * @todo Use a more robust serialization format (not int uid based)
     * @throws IOException
     * @throws ClassNotFoundException  */
    public void readExternal(ObjectInput objectInput) throws IOException, java.lang.ClassNotFoundException {
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
            if (columns == null) {
                columns = createColumns();
            }
            int numColumns = columns.length;
            boolean[] columnVisible = new boolean[numColumns];
            for (int i = 0; i < numColumns; i++) {
                columnVisible[i] = false;
            }
            for (int i = 0; i < numVisible; i++) {
                int uid = objectInput.read();
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

                /* don't do anything. we just read the bytes
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
            category = TaskList.USER_CATEGORY; // for compatibility only
        }

        synchronized (TaskListView.class) {
            if (views == null) {
                views = new HashMap();
            }
            views.put(category, this);
        }
    }

    /** Write out relevant settings in the window (visible
     * columns, sorting order, etc.) such that they can
     * be reconstructed the next time the IDE is started.
     * @todo Use a more robust serialization format (not int uid based)
     * @param objectOutput Object stream to write to
     * @throws IOException  */
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
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

    /** Create the list of columns to be used in the view.
     NOTE: The first column SHOULD be a tree column.
     */
    abstract protected ColumnProperty[] createColumns();

    public ColumnProperty getMainColumn(int width) {
        // Tree column
        // NOTE: Task.getDisplayName() must also be kept in sync here
        return new ColumnProperty(
                0, // UID -- never change (part of serialization
                PROP_TASK_SUMMARY,
                NbBundle.getMessage(TaskListView.class, "Description"), // NOI18N
                NbBundle.getMessage(TaskListView.class, "Description"), // NOI18N
                true,
                width
        );
    }

    protected Task unshowItem = null;

    /** Show the given todolist item. "Showing" means getting the
     * editor to show the associated file position, and open up an
     * area in the todolist view where the details of the todolist
     * item can be fully read.
     */
    /*
    public void show(Task item, Annotation anno) {
	if (listeners != null) {
	    // Stash item so I can notify of deletion -- see TaskViewListener
	    // doc
	    unshowItem = item;
	    int n = listeners.size();
	    for (int i = 0; i < n; i++) {
		TaskViewListener tl = (TaskViewListener)listeners.get(i);
		tl.showTask(item, anno);
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
	if (listeners != null) {
	    // Stash item so I can notify of deletion -- see TaskViewListener
	    // doc
	    unshowItem = null;
	    int n = listeners.size();
	    for (int i = 0; i < n; i++) {
		TaskViewListener tl = (TaskViewListener)listeners.get(i);
		tl.hideTask();
	    }
	}
    }
    */

    /** Expand nodes and select the particular todo item, IF the todolist
     *  view is showing
     * @param item The item to be shown
     */
    public void select(final Task item) {

        if (isShowing() == false) return;

        // Expand nodes to show the given item

        // XXX HACK HACK HACK
        // This fails for the case where you add a new subitem to a parent
        // node where it's the first time the parent is getting a subtask
        // (e.g. the node was a leaf).  In that case I'm doing a trick
        // (see TodoItem's addSubtask method) which causes the node to be
        // replaced, and the end result is that when this code runs I'm
        // operating on the old node because the node recreation from keys
        // hasn't happened yet.  So the hack solution: defer expanding the
        // nodes a brief interval. Not sure how long to wait - since I don't
        // think the node refreshing is happening on the AWT thread but on
        // a separate thread. Perhaps there's a method I can call to force
        // a refresh before this happens? Not sure, so for now I just hack
        // it by a delayed call
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Node n = TaskNode.find(getExplorerManager().getRootContext(),
                        item);

                Node[] sel;
                if (n == null) {
                    sel = new Node[0];
                } else {
                    sel = new Node[]{n};
                }
                try {
                    getExplorerManager().setSelectedNodes(sel);
                } catch (PropertyVetoException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
        }); // End hack
    }

    public void expandAll() {
        if (treeTable != null) {
            treeTable.expandAll();
        }
    }

    /**
     Return the "current" component. Ideally, my context menu
     actions can figure out which component they're invoked from.
     But I can't do that (the way I did this in the ifdef module
     was inserting some code in my own popup performer, but in this
     case I don't have access to the popup performer - it's created
     and managed privately by the TreeTableView.
     <p>
     So instead try some likely choices, like the currently visible
     component.
     <p>
     My original todo comment:
     The question is how I associate actions with context menus they're
     brought up from (e.g. the view) - for expand, goto, and edit.
     And what if they're invoked from a toolbar? In that case I'll want
     to use the ACTIVATED component. And what if there is none? Then
     they should be grayed out. Yuckarola! Perhaps I can leave them
     sensitive with a beep. Or perhaps make them act on the default view?
     OR perhaps ALL views (okay for expand, not for show).
     */
    public static TaskListView getCurrent() {
        // Try to figure out which view is current. If none is found to
        // be visible, guess one.
        if (views == null) {
            return null;
        }
        Collection vs = views.values();
        Iterator it = vs.iterator();
        TaskListView first = null;
        while (it.hasNext()) {
            TaskListView tlv = (TaskListView) it.next();
            if (tlv.isShowing()) {
                return tlv;
            }
            if (first == null) {
                first = tlv;
            }
        }
        return first;
    }

    /** List of TaskViewListener object listening on the tasklist view
     for visibility, selection, etc. */
    private ArrayList listeners = null;

    protected ArrayList getListeners() {
        return listeners;
    }

    /** Locate all tasklist listeners and add them to our property
     change listener setup.
     @todo Decide whether to unregister and reregister repeatedly;
     this has the advantage of working with dynamic module
     installs/uninstalls.
     @todo Consider using a lookup listener such that I'm notified of
     later additions/removals
     @todo Consider doing a factory lookup instead of creating a new
     instance each time. Would allow better coordination between
     the listener instance and ScanView in the editor package.
     */
    void registerListeners() {
        // TODO Ensure that this doesn't get called from other windows...
        // find TaskViewListeners
        Lookup l = Lookup.getDefault();
        Lookup.Template template = new Lookup.Template(TaskViewListener.class);
        Iterator it = l.lookup(template).allInstances().iterator();
        if (it.hasNext()) {
            listeners = new ArrayList(4);
        }
        while (it.hasNext()) {
            TaskViewListener tl = (TaskViewListener) it.next();
            listeners.add(tl);
        }
    }

    /** Unregister the listeners registered in registerListener such
     that they are no longer notified of changes */
    void unregisterListeners() {
        listeners = null;
    }

    // TODO Pick a better name!
    // TODO make method package private! Can't yet - used in editor/
    public void showInMode() { 
        if (!isOpened()) {
            Mode mode = WindowManager.getDefault().findMode("output"); // NOI18N
            if (mode != null) {
                mode.dockInto(TaskListView.this);
            }
        }
        open();
        requestVisible();
        requestActive();
    }


    protected boolean persistent = false;

    /** Designate whether or not this window is "persisted", meaning
     whether or not it will show up next time you start the IDE
     if it is showing now.  By default, FALSE. */
    public void setPersistent(boolean persistent) {
        if (this.persistent && !persistent) {
            // Turning OFF persistence on a window, not relying on
            // the default: unusual, but let's handle it correctly
            // anyway
            putClientProperty("PersistenceType", "Never"); // NOI18N
        }

        this.persistent = persistent;

        if (persistent) {
            // Only persist window info if the window is opened on exit.
            putClientProperty("PersistenceType", "OnlyOpened"); // NOI18N
        }
    }


    /** Keeps track of the category tabs we've added */
    private transient static HashMap views = null; // LEAK?

    public static TaskListView getTaskListView(String category) {
        assert category != null;

        if (views == null) {
            return null;
        }

        TaskListView view = (TaskListView) views.get(category);
        return view;
    }

    /** Called to indicate that a particular task is made current.
     Do what you can to "select" this task. */
    public void selectedTask(Task item) {
        if (listeners != null) {
            // Stash item so I can notify of deletion -- see TaskViewListener
            // doc
            unshowItem = item;
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskViewListener tl = (TaskViewListener) listeners.get(i);
                tl.showTask(item, null);
            }
        }
    }


    /** Called to indicate that a particular task has been "warped to".
     Do what you can to "warp to" this task. Typically means show
     associated fileposition in the editor.
     */
    public void warpedTask(Task item) {
        // XXX currently identical to selectedTask above!
        if (listeners != null) {
            // Stash item so I can notify of deletion -- see TaskViewListener
            // doc
            unshowItem = item;
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskViewListener tl = (TaskViewListener) listeners.get(i);
                tl.showTask(item, null);
            }
        }
    }


    /** A task has been added. If null, a number of tasks have
     been added. */
    public void addedTask(Task t) {
        // Nothing to do?
    }

    public void removedTask(Task pt, Task task) {
        if ((task == unshowItem) && (listeners != null)) {
            unshowItem = null;
            int n = listeners.size();
            for (int i = 0; i < n; i++) {
                TaskViewListener tl = (TaskViewListener) listeners.get(i);
                tl.hideTask();
            }
        }
    }

    public void structureChanged(Task t) {
    }

    /**  Return the tasklist shown in this view */
    public TaskList getList() {
        // XXX  FilteredTasksList may appear here for TODOs Current File
        return (TaskList) getModel();
    }

    protected ObservableList getModel() {
        return tasklist;
    }

    /** Pulled straight out of TreeTableView in openide
     Except don't cache rowComparator, and don't case
     to VisualizerNode, cast to Node
     */
    synchronized Comparator getRowComparator(
            final Node.Property sortedByProperty,
            final boolean sortAscending,
            final boolean sortedByName,
            final boolean noSorting) {
        Comparator rowComparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 == o2) {
                    return -1;
                }

                Node n1 = (Node) o1;
                Node n2 = (Node) o2;

                if (sortedByProperty == null && !sortedByName) {
                    Node[] nodes = n1.getParentNode().getChildren().getNodes();
                    for (int i = 0; i < nodes.length; i++) {
                        if (nodes[i].equals(n1))
                            return -1;
                        else if (nodes[i].equals(n2))
                            return 1;
                    }
                    return 0;
                }

                int res;
                if (sortedByName) {
                    res = n1.getDisplayName().compareTo(n2.getDisplayName());
                    return sortAscending ? res : -res;
                }

                Node.Property p1 = getNodeProperty(n1, sortedByProperty);
                Node.Property p2 = getNodeProperty(n2, sortedByProperty);

                if (p1 == null && p2 == null)
                    return 0;

                try {
                    if (p1 == null)
                        res = -1;
                    else if (p2 == null)
                        res = 1;
                    else {
                        Object v1 = p1.getValue();
                        Object v2 = p2.getValue();
                        if (v1 == null && v2 == null)
                            return 0;
                        else if (v1 == null)
                            res = -1;
                        else if (v2 == null)
                            res = 1;
                        else {
                            if (v1.getClass() != v2.getClass() || !(v1 instanceof Comparable)) {
                                v1 = v1.toString();
                                v2 = v2.toString();
                            }
                            res = ((Comparable) v1).compareTo(v2);
                        }
                    }
                    return sortAscending ? res : -res;
                } catch (Exception ex) {
                    // PENDING
                    return 0;
                }
            }
        };
        return rowComparator;
    }

    private Node.Property getNodeProperty(Node node, Node.Property prop) {
        Node.PropertySet[] propsets = node.getPropertySets();
        for (int i = 0, n = propsets.length; i < n; i++) {
            Node.Property[] props = propsets[i].getProperties();

            for (int j = 0, m = props.length; j < m; j++) {
                if (props[j].equals(prop)) {
                    return props[j];
                }
            }
        }
        return null;
    }


    // End of stuff taken from TreeTableView

    /**
     * Get the toggle filter for this view. It's
     * applied if {@link #isFiltered} returns true.
     *
     * @return The toggle filter.
     */
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter(null, true, new ArrayList(), false);
        }
        return filter;
    }

    /** Tests if any real filter is applied. */
    public final boolean isFiltered() {
        return filterEnabled;
    }

    /**
     * Controls filter enableness.
     * @param enableFilter
     */
    public final void setFiltered(boolean enableFilter) {
        if (enableFilter == filterEnabled) return;
        if (enableFilter == true) {
            setFilter(getFilter(), true);
        } else {
            setFilter(null, false);
        }
    }

    /**
     * Set the filter to be used in this view.
     * @param filter The filter to be set, or null, to remove filtering.
     * @param showStatusBar When true, show a status bar with a remove button etc.
     */
    public void setFilter(Filter filter, boolean showStatusBar) {

        // XXX mix of actual filter rule and its enableness
        if (filter == null) {
            filterEnabled = false;
        } else {
            this.filter = filter;
            filterEnabled = true;
        }

        try {
            getExplorerManager().setSelectedNodes(new Node[0]);
        } catch (PropertyVetoException e) {
        }

        if (filterEnabled && showStatusBar && filter.hasConstraints()) {
            setRoot();
            updateFilterCount();
            //expandAll(); // [PENDING] Make this optional?
        } else {
            updateFilterCount();
            setRoot();
        }
    }

    /**
     * @param node it's children will be sorted
     */
    public List getSortedChildren(Node node,
                                  Node.Property sortedByProperty,
                                  boolean sortAscending,
                                  boolean sortedByName,
                                  boolean noSorting) {
        // Only sort by the top/first level nodes
        Node[] firstNodes = node.getChildren().getNodes();
        ArrayList nodes = new ArrayList(firstNodes.length);
        for (int i = 0; i < firstNodes.length; i++) {
            nodes.add(firstNodes[i]);
        }

        Comparator comparator = getRowComparator(sortedByProperty,
                sortAscending,
                sortedByName,
                noSorting);
        Collections.sort(nodes, comparator);
        return nodes;
    }

    private Class nextActionClz = null;
    private Class prevActionClz = null;

    /**
     * Assign the Next/Previous build actions to point to the
     * task window
     *
     * @param install true = install, false = deinstall
     */
    private void installJumpActions(boolean install) {
        // TODO - only install if the list is non empty (and call
        // this method from SMI when the list becomes non-empty)
        // In other words, the next action button shouldn't light
        // up when there are no tasks to move to.

        // Make F12 jump to next task
        if (nextActionClz == null) {
            if (lookupAttempted) {
                return;
            }
            lookupAttempted = true;
            ClassLoader systemClassLoader = (ClassLoader) Lookup.getDefault().
                    lookup(ClassLoader.class);
            try {
                nextActionClz = systemClassLoader.
                        loadClass("org.netbeans.core.output.NextOutJumpAction"); // NOI18N
                prevActionClz = systemClassLoader.
                        loadClass("org.netbeans.core.output.PreviousOutJumpAction"); // NOI18N
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                return;
            }
        }

        CallbackSystemAction nextAction =
                (CallbackSystemAction) SystemAction.get(nextActionClz);
        CallbackSystemAction previousAction =
                (CallbackSystemAction) SystemAction.get(prevActionClz);

        if (install) {
            nextAction.setActionPerformer(new ActionPerformer() {
                public void performAction(SystemAction action) {
                    nextTask();
                }
            });
            previousAction.setActionPerformer(new ActionPerformer() {
                public void performAction(SystemAction action) {
                    prevTask();
                }
            });
        } else {
            nextAction.setActionPerformer(null);
            previousAction.setActionPerformer(null);
        }
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

    /** Show the next task in the view */
    protected final void nextTask() {
        ObservableList list = getModel();
        Task curr = getCurrentTask();
        Task next = null;
        if (curr == null) {
            List sgs = list.getRoot().getSubtasks();
            if (sgs != null) {
                next = (Task) sgs.get(0);
            } else {
                return;
            }
        } else {
            next = TLUtils.findNext(curr, wrapWarned);
        }
        String msg = NbBundle.getBundle(TaskListView.class).
                getString("MSG_AtLastError"); // NOI18N
        if ((next == null) && !wrapWarned) {
            StatusDisplayer.getDefault().setStatusText(msg);
            wrapWarned = true;
        } else {
            wrapWarned = false;
        }
        if (next != null) {
            if (next.getLine() != null) {
                TaskAnnotation anno = getAnnotation(next);
                if (anno != null) {
                    showTask(next, anno);
                }
            }
            select(next);
            //StatusDisplayer.getDefault().setStatusText(next.getSummary());
        }

    }

    /** Show the previous task in the view */
    protected final void prevTask() {
        ObservableList list = getModel();
        Task curr = getCurrentTask();
        Task prev = null;
        if (curr == null) {
            List sgs = list.getRoot().getSubtasks();
            if (sgs != null) {
                prev = (Task) sgs.get(0);
            } else {
                return;
            }
        } else {
            prev = TLUtils.findPrev(curr, wrapWarned);
        }
        String msg = NbBundle.getBundle(TaskListView.class).
                getString("MSG_AtLastError"); // NOI18N
        if ((prev == null) && !wrapWarned) {
            StatusDisplayer.getDefault().setStatusText(msg);
            wrapWarned = true;
        } else {
            wrapWarned = false;
        }
        if (prev != null) {
            if (prev.getLine() != null) {
                TaskAnnotation anno = getAnnotation(prev);
                if (anno != null) {
                    showTask(prev, anno);
                }
            }
            select(prev);
            //StatusDisplayer.getDefault().setStatusText(prev.getSummary());
        }
    }

    /**
     * Return an editor annotation to use to show the given task
     *
     * @return created annotation or null
     */
    protected TaskAnnotation getAnnotation(Task task) {
        return new TaskAnnotation(task, this);
    }


    /**
     * @return The currently selected task in the view,
     * or if none, the first task (or null, if there are
     * no tasks
     */
    private Task getCurrentTask() {
        Node[] node = getExplorerManager().getSelectedNodes();
        if ((node != null) && (node.length > 0)) {
            Task s = (Task) TaskNode.getTask(node[0]);
            if (s.getParent() != null) { // Make sure it's not the root node
                return s;
            }
        }
        return null;
    }

    protected void componentHidden() {
        hideTask();

        // Stop listening for node activation
        getExplorerManager().removePropertyChangeListener(this);

        // Remove jump actions
        // Cannot do this, because componentHidden can be called
        // after another TaskListView is shown (for example when you
        // switch from one tasklist view to another) so this would
        // cripple the newly showing tasklist view.
        //
        // According to issue #37367 hidden and activated events works
        // together smmothly to get desired result
        installJumpActions(false);
    }

    protected void componentShowing() {
        // Listen for node activation
        getExplorerManager().addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent ev) {
        // Display selected node's summary in the status line
        if (ev.getPropertyName() == ExplorerManager.PROP_SELECTED_NODES) {
            Node[] sel = getExplorerManager().getSelectedNodes();
            if ((sel != null) && (sel.length == 1)) {
                Task task = TaskNode.getTask(sel[0]);
                if (task != null) {
                    StatusDisplayer.getDefault().setStatusText(task.getSummary());
                }
            }
        } else if (ExplorerManager.PROP_SELECTED_NODES.equals(
                ev.getPropertyName())) {
            // internal error
            ErrorManager.getDefault().log(
                    "Option property name " + // NOI18N
                    ev.getPropertyName() +
                    " was not interned."); // NOI18N
        }
    }

    /** Return true iff the given node is expanded */
    public boolean isExpanded(Node n) {
        return treeTable.isExpanded(n);
    }

    /** Collapse or expand a given node */
    public void setExpanded(Node n, boolean expanded) {
        // todo remove
        Thread.dumpStack();
        
        if (expanded) {
            treeTable.expandNode(n);
        } else {
            treeTable.collapseNode(n);
        }
    }

    // TODO - get rid of this when you clean up TaskListView.getRootNode() to
    // do the Right Thing(tm) - always return effective explorer context
    /** Return the actual root of the node tree shown in this view.
     * May be a filternode when Filtering is in place.
     */
    public Node getEffectiveRoot() {
        return getExplorerManager().getRootContext();
    }

    /** Schedule a particular target suggestion to be expanded (if it's
     a parent node.  This delay is necessary since the node may not
     yet have been created (and it's being created by a different
     thread, so we're essentially busy waiting, checking each time
     around the event dispatch loop up to a maximum number of checks.
     @param target The task we want expanded
     @param hops Current hop count. Clients should pass in 0 here.
     Used to bounce out of recursion.
     */
    public void scheduleNodeExpansion(final Task target,
                                      final int hops) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // find
                //Node n = TaskNode.find(getRootNode(), target);
                Node n = TaskNode.find(getEffectiveRoot(), target);
                if (n != null) {
                    setExpanded(n, true);
                } else if (hops < 50) {
                    scheduleNodeExpansion(target, hops + 1);
                }
            }
        });
    }

    public void requestActive() {
        if (treeTable != null) {
            treeTable.getTable().requestFocus();
        }
    }

    /**
     * Returns visible columns
     *
     * @return visible columns
     */
    public final ColumnProperty[] getVisibleColumns() {
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
    }

    ///* For debugging purposes:
    public void nodePrint() {
        System.err.println("\nTask List (As Seen via Nodes):\n-------------");
        nodeRecursivePrint(getEffectiveRoot(), 0);
        System.err.println("\n\n");
    }

    private void nodeRecursivePrint(Node node, int depth) {
        if (depth > 20) { // probably invalid list
            Thread.dumpStack();
            return;
        }
        for (int i = 0; i < depth; i++) {
            System.err.print("   ");
        }
        System.err.println(node.getDisplayName());
        if ((node.getChildren() != null) &&
                (node.getChildren().getNodes() != null)) {
            Node[] nodes = node.getChildren().getNodes();
            for (int i = 0; i < nodes.length; i++) {
                nodeRecursivePrint(nodes[i], depth + 1);
            }
        }
    }

    // */

}
