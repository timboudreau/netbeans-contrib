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


import java.awt.BorderLayout;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;


import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

import org.openide.explorer.view.TreeTableView;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.text.Annotation;
import org.openide.awt.StatusDisplayer;
import org.openide.ErrorManager;
import org.openide.actions.DeleteAction;
import org.openide.actions.FindAction;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.view.Visualizer;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.Workspace;
import org.openide.windows.WindowManager;

import org.netbeans.core.output.NextOutJumpAction;
import org.netbeans.core.output.PreviousOutJumpAction;
import org.openide.util.Utilities;


/** View showing the todo list items
 * @author Tor Norbye, Tim Lebedkov, Trond Norbye
 * @todo Figure out why the window system sometimes creates multiple objects
 *       from this class
 */
public abstract class TaskListView extends ExplorerPanel
    implements TaskListener, ActionListener,
               PropertyChangeListener {
    
    transient protected TaskNode rootNode = null;
    transient protected MyTreeTable treeTable;
    
    transient public ColumnProperty[] columns = null;

    transient private boolean initialized = false;

    transient protected String category = null;

    transient protected TaskList tasklist = null;
    
    transient protected Filter filter = null;

    /** Gotta stash away my own version of the name of the top component,
	since the explorer manager aggressively changes the name I
	set in the constructor with setName to "Explorer[<root name>]"
    */
    transient protected String title = null;
    
    transient private ActionPerformer deletePerformer;
    
    /** Construct a new TaskListView. Most work is deferred to
	componentOpened. NOTE: this is only for use by the window
	system when deserializing windows. Client code should not call
	it; use the constructor which takes category, title and icon
	parameters. I can't make it protected because then the window
	system wouldn't be able to get to this. But the code relies on
	readExternal getting called after this constructor to finalize
	construction of the window.*/
    public TaskListView() {
    }
    
    public TaskListView(String category, String title, Image icon,
			boolean persistent, TaskList tasklist) {
	super();

        this.category = category;
	this.title = title;
	this.persistent = persistent;
	this.tasklist = tasklist;
        if (tasklist != null) {
            tasklist.setView(this);
        }
	
        //deletePerformer = new DeleteActionPerformer(this.getExplorerManager());
        
	setIcon(icon);

	if (persistent) {
	    // Only persist window info if the window is opened on exit.
	    putClientProperty("PersistenceType", "OnlyOpened"); // NOI18N
	} else {
	    putClientProperty("PersistenceType", "Never"); // NOI18N
	}

	synchronized (TaskListView.class) {
	    if (views == null) {
		views = new HashMap();
	    }
	    views.put(category, this);
	}
    }

    /**
     * Could be overridden to change actions for the toolbar.
     * @return actions for the toolbar or null
     */
    public SystemAction[] getToolBarActions() {
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
    public ColumnProperty[] getColumns() {
        return columns;
    }
    
    /** Called when the object is opened. Add the GUI. 
	@todo Trigger source listening on window getting VISIBLE instead
              of getting opened.
     */    
    protected void componentOpened() {
	// Register listeners, such as the editor support bridge module
	registerListeners();
	
	if (initialized) {
	    return;
	}
	initialized = true;
	
        setLayout(new BorderLayout());
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
        
        for (int i = 1; i < columns.length; i++) {
            // This is kind of odd. Column 1 is named column 0 in the
            // treetable.  I'll betcha you could call this on column
            // -1 to set the tree preferred width instead of the above
            // method :-) That's because they +1 to the column number
            // when they set the width on the table model.
            treeTable.setTableColumnPreferredWidth(i-1, 
                                                   columns[i].getWidth());
        }
        
        treeTable.setRootVisible(false);
        treeTable.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        treeTable.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Give 80% to the first column, 10% to the next one, 10% to the third one
        // Grr... find out if this refers to the screen-visible columns, or
        // the potential columns (the setProperties set)
        add(treeTable, BorderLayout.CENTER);  //NOI18N

        SystemAction actions[] = getToolBarActions();
        if (actions != null) {
            JToolBar toolbar = SystemAction.createToolbarPresenter(actions);
            toolbar.setOrientation(JToolBar.VERTICAL);
            add(toolbar, BorderLayout.WEST);
        }
        
	// Populate the view
	showList();

        installJumpActions(true);
    }


    /** Called when the window is closed. Cleans up. */    
    protected void componentClosed() {
	hideList();

        // Remove any task markers we've added to the editor
        if (unshowItem != null) {
            removedTask(unshowItem);
        }
        
	// Unregister listeners
	unregisterListeners();
    }


    /** Create the root node to be used in this view */
    abstract protected TaskNode createRootNode();
    
    protected void showList() {
	tasklist.addListener(TaskListView.this);
        setRoot();
    }

    private void setRoot() {
        Task root = tasklist.getRoot();
        rootNode = createRootNode();

        if (filter != null) {
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
        /*if (!tasklist.getRoot().hasSubtasks()) {
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
	tasklist.removeListener(this);
    }

    public Node getRootNode() {
        // TODO - make sure you use the filternode etc. when appropriate!
        return rootNode;
    }
    
    public void setRootNode(TaskNode r) {
        //Thread.dumpStack();
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

    /** Overrides superclass method. Gets actions for this top component. */
    public SystemAction[] getSystemActions() {
        SystemAction[] todoActions = new SystemAction[] {
            // Doesn't seem like this ever gets used (explorer
            // has its own ideas)
            // SystemAction.get(NewTaskAction.class)
        };
        SystemAction[] sa = super.getSystemActions ();
        return SystemAction.linkActions (sa, todoActions);
    }
    
    // Workaround - is this no longer necessary?
    protected static class MyTreeTable extends TreeTableView {
        MyTreeTable() {
            super();
            JTable table = MyTreeTable.this.treeTable;
            table.setShowHorizontalLines(true);
            table.setShowVerticalLines(false);

            //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	    // No, I can use TreeTableView.setTableAutoResizeMode(int) for this
	    
            // No white clipping lines on selected table rows: reduce separator
            // to 0. That means text may touch but HIE prefers this.
            table.setIntercellSpacing(new Dimension(0, table.getRowMargin()));
            
	    /* Issue 23993 was fixed which probably makes this unnecessary:
            // Grid color: HIE's asked for (230,230,230) but that seems troublesome
            // since we'd have to make a GUI for customizing it. Instead, go
            // with Metal's secondary2, since for alternative UIs this will continue
            // to look good (and it's customizable by the user). And secondary2
            // is close to the request valued - it's (204,204,204).
            table.setGridColor((java.awt.Color)javax.swing.UIManager.getDefaults().get("Label.background")); // NOI18N
	    */
        }
        
        JTree getTree() {
            return tree;
        }
        
        TableModel getModel() {
            // XXX it's private: return tableModel;
            return (TableModel)MyTreeTable.this.treeTable.getModel();
        }

        TableColumnModel getHeaderModel() {
            return MyTreeTable.this.treeTable.getTableHeader().getColumnModel();
        }
    }

    /** Class holding column properties.
     * See debuggercore's TreeTableExplorerViewSupport.java */    
    public static class ColumnProperty extends PropertySupport.ReadOnly {
        /** Id of the column. Used such that with deserialization,
         * we can tell exactly which column you're referring to,
         * even if we've added and removed columns from the system.
         * (Could also store the column property name, but that's
         * more work and more data). */        
	public int uid; // Used to check equivalence in serialized data,
	                // so I don't have to store whole string names
	private int width;

	// Used for non-treetable columns
        /** Construct a new property for a "table column" (e.g. not
         * the leftmost tree column)
         * @param uid UID of this column
         * @param name Property name
         * @param type Type of this property
         * @param displayName Name shown in the display
         * @param hint Tooltip for the property
         * @param sortable Whether or not this column is valid as a sort key
         * @param defaultVisibility Whether or not this column should be shown by
         * @param width Default width for the column
         * default */        
        public ColumnProperty(
            int uid,
            String name,
            Class type,
            String displayName,
            String hint,
            boolean sortable,
            boolean defaultVisibility,
            int width
        ) {
            super(name, type, displayName, hint);
	    this.uid = uid;
            this.width = width;
            setValue ("ColumnDescriptionTTV", hint); // NOI18N
            if (sortable)
                setValue("ComparableColumnTTV", Boolean.TRUE);// NOI18N
            if (!defaultVisibility)
                setValue("InvisibleInTreeTableView", Boolean.TRUE);// NOI18N
        }

	// Used for the Tree column (column 0)
        /** Construct a column object for the treecolumn (leftmost
         * column).
         * @param uid UID of the column
         * @param sortable Whether or not this column is sortable
         * @param width Default width for the column
         */
        public ColumnProperty (
	    int uid,
            String name,
            String displayName,
            boolean sortable,
            int width
        ) {     
            super(name, String.class, displayName, displayName);
	    this.uid = uid;
            this.width = width;
            setValue( "TreeColumnTTV", Boolean.TRUE );// NOI18N
            if (sortable)
                setValue ("ComparableColumnTTV", Boolean.TRUE);// NOI18N
        }       
        
        /**
         * @return  */        
        public Object getValue() {
            return null;
        }

        public int getWidth() {
            return width;
        }
    }

    /** HACK HACK HACK
	This is a temporary hack to help the Node.Handle figure
	out which window it's associated with. Rewriting Node.Handle
	to do something more robust is on my todo list :-)
    */
    public static TaskListView currentDeserializationTarget = null;

    /** Read in a serialized version of the tasklist
     * and reads in sorting preferences etc. such that
     * we use the same preferences now.
     * @param objectInput object stream to read from
     * @todo Use a more robust serialization format (not int uid based)
     * @throws IOException
     * @throws ClassNotFoundException  */    
    public void readExternal(ObjectInput objectInput) throws IOException, java.lang.ClassNotFoundException {
	currentDeserializationTarget = this;
        
        // Don't call super!
        // See writeExternal for justification
        //super.readExternal(objectInput);

	int ver = objectInput.read();
        //assert ver <= 3 : "serialization version incorrect; should be 1, 2 or 3";


	// Read in the UID of the currently selected task, or null if none
	String selUID = (String)objectInput.readObject();
	 // Unused: Not yet implemented
	
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
	    }

	    //System.out.print("Column visibility: {");
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
            //System.out.println(" }");
	}

	if (ver >= 2) {
	    category = (String)objectInput.readObject();
	    title = (String)objectInput.readObject();
	    int persistentInt = objectInput.read();
	    persistent = (persistentInt != 0);
	} else {
	    category = TaskList.USER_CATEGORY; // for compatibility only
	}

	synchronized(TaskListView.class) {
	    if (views == null) {
		views = new HashMap();
	    }
	    views.put(category, this);
	}
        
        //deletePerformer = new DeleteActionPerformer(this.getExplorerManager());
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


        // Here I should record a few things; in particular, sorting order, view
        // preferences, etc.
        // Since I'm not doing that yet, let's at a minimum put in a version
        // byte so we can do the right thing later without corrupting the userdir
        objectOutput.write(3); // SERIAL VERSION

	// Write out the UID of the currently selected task, or null if none
	objectOutput.writeObject(null); // Not yet implemented
	
	// Version 1 format:
	// String: selected uid
	// byte: sortingColumn (255: no sort, otherwise, sorting id)
	// byte: sort ascending (0:false or 1:true)
	// byte: number of visible columns (N)
	// N bytes: visible column uids

	// TODO Additional:
	// N bytes: visible column order id's
	// N bytes: column widths?
	//  (Question: should the above two lines be written for ALL columns
	//   or just visible?)
	// String Object: selected task? (should this be a multi-selection?)
	
	// Look at column properties and figure out which columns are visible,
	// which column is the sorting column and which whether or not the sort
	// is ascending/descending
	int[] visibleColumns = null;
	int numVisible = 0;
	int sortingColumn = -1;
	boolean ascending = false;
	if (columns != null) {
	    int numColumns = columns.length;
	    visibleColumns = new int[numColumns];

	    for (int i = 0; i < columns.length; i++) {
		Boolean invisible =
		    (Boolean)columns[i].getValue("InvisibleInTreeTableView"); // NOI18N

		// Is the column visible?

                // Grrr.... openide must not be using the Boolean enum's;
                // it must be creating new Boolean objects.... so I've
                // gotta use boolean value instead of this nice line:
                //    if (!(invisible == Boolean.TRUE)) {
                if ((invisible == null) || !invisible.booleanValue()) {
		    // yes
		    visibleColumns[numVisible++] = columns[i].uid;
		}

		Boolean sorting =
		    (Boolean)columns[i].getValue( "SortingColumnTTV"); // NOI18N
                if ((sorting != null) && (sorting.booleanValue())) {
		    //assert sortingColumn == -1; // Only one column should be it
		    sortingColumn = columns[i].uid;
		    Boolean desc = (Boolean)columns[i].getValue( "DescendingOrderTTV"); // NOI18N
		    ascending = (desc != Boolean.TRUE);
		}
		
	    }
	}

        /*
        System.out.println("writeExternal: " + getClass().getName()); // NOI18N
	System.out.println("Sorting column: " + sortingColumn); // NOI18N
	System.out.println("Sorting ascending: " + ascending); // NOI18N
	System.out.println("Number of visible columns: " + numVisible); // NOI18N
	for (int i = 0; i < numVisible; i++) {
	    System.out.print("   " + visibleColumns[i]); // NOI18N
	}
	System.out.println("\n"); // NOI18N
        */

	int sortAscendingInt = ascending ? 1 : 0;
	    
	// WARNING! These write functions write BYTES! So don't try to
	// write negative numbers
	if (sortingColumn == -1) {
	    sortingColumn = 255;
	}
	
	//assert ((sortingColumn >= 0) && (sortingColumn <= 255));
	//assert ((sortAscendingInt >= 0) &&
	//	(sortAscendingInt <= 255));
	//assert ((numVisible >= 0) && (numVisible <= 255));
	
	objectOutput.write(sortingColumn);
	objectOutput.write(sortAscendingInt);
	objectOutput.write(numVisible);

	for (int i = 0; i < numVisible; i++) {
	    // Can only write bytes...
	    //assert((visibleColumns[i] >= 0) &&
	    //		    (visibleColumns[i] <= 255));
	    
	    objectOutput.write(visibleColumns[i]);
	}
	    /*
	      column order in ttv ( columns can be moved )
	      (Integer)columns[i].getValue( "OrderNumberTTV" ));
	    */	

	// Write out the UID of the currently selected task, or null if none

	// Write out the window's properties:
	objectOutput.writeObject(category);
	objectOutput.writeObject(title);
	objectOutput.write(persistent ? 1 : 0);
    }

    public static final String PROP_TASK_SUMMARY = "taskDesc"; // NOI18N
    
    /** Return the name of this window ("task list"), as
     * shown in IDE tabs etc.
     * @return Name of window */    
    public String getName() {
	return title;
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

    /** Unshow the given task, if it's the one currently showing */
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
    
    /** Expand nodes and select the particular todo item, IF the todolist
     *  view is showing
     * @param item The item to be shown */
    public void select(final Task item) {
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
                if (n == null){
                    sel = new Node[0];
                } else {
                    sel = new Node[] { n };
                }
                try {
                    getExplorerManager().setSelectedNodes(sel);
                } catch (PropertyVetoException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
        }}); // End hack
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
	    TaskListView tlv = (TaskListView)it.next();
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
	@todo Use the TaskViewListener interface instead of
	           propertychangelistener
	@todo Decide whether to unregister and reregister repeatedly;
              this has the advantage of working with dynamic module
	      installs/uninstalls.
        @todo Use TaskViewListener interface instead of propertychangelisteners
	@todo Consider using a lookup listener such that I'm notified of
              later additions/removals
        @todo Consider doing a factory lookup instead of creating a new
	      instance each time. Would allow better coordination between
	      the listener instance and ScanView in the editor package.
    */
    void registerListeners() {
	// TODO Ensure that this doesn't get called from other windows...
        // find TaskViewListeners
        Lookup l = Lookup.getDefault ();
	Lookup.Template template = new Lookup.Template(TaskViewListener.class);
        Iterator it = l.lookup(template).allInstances().iterator();
	if (it.hasNext()) {
	    listeners = new ArrayList(4);
        }
        while (it.hasNext()) {
	    TaskViewListener tl = (TaskViewListener)it.next();
	    listeners.add(tl);
        }
    }

    /** Unregister the listeners registered in registerListener such
	that they are no longer notified of changes */
    void unregisterListeners() {
	listeners = null;
    }


    public void showInMode() { // TODO Pick a better name!
	// TODO make method package private! Can't yet - used in editor/
        Workspace workspace = WindowManager.getDefault().
           getCurrentWorkspace();
        if (!isOpened(workspace)) {
            Mode mode  = workspace.findMode("output"); // NOI18N
            if (mode != null) {
                mode.dockInto(TaskListView.this);
            }
        }
	open(workspace);
	requestVisible();
        requestFocus();
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
	if (category == null) {
	    ErrorManager.getDefault().log("Internal error: category was null in getTaskListView!");
	}

        if (views == null) {
	    return null;
	}

	TaskListView view = (TaskListView)views.get(category);
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
		TaskViewListener tl = (TaskViewListener)listeners.get(i);
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
		TaskViewListener tl = (TaskViewListener)listeners.get(i);
		tl.showTask(item, null);
	    }
	}
    }


    /** A task has been added. If null, a number of tasks have
	been added. */
    public void addedTask(Task t) {
	// Nothing to do?
    }

    /** A task has been added. If null, a number of tasks have
	been added. */
    public void removedTask(Task task) {
	if ((task == unshowItem) && (listeners != null)) {
	    unshowItem = null;
	    int n = listeners.size();
	    for (int i = 0; i < n; i++) {
		TaskViewListener tl = (TaskViewListener)listeners.get(i);
		tl.hideTask();
	    }
	}
    }

    /**  Return the tasklist shown in this view */
    public TaskList getList() {
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
                        if (o1 == o2)
                            return -1;

                        Node n1 = (Node)o1;
                        Node n2 = (Node)o2;
                        
                        if ( sortedByProperty == null && !sortedByName ) {
                            Node[] nodes = n1.getParentNode().getChildren().getNodes();
                            for ( int i = 0; i < nodes.length; i++ ) {
                                if ( nodes[i].equals( n1 ) )
                                    return -1;
                                else if ( nodes[i].equals( n2 ) )
                                    return 1;
                            }
                            return 0;
                        }
                        
                        int res;
                        if ( sortedByName ) {
                            res = n1.getDisplayName().compareTo(n2.getDisplayName());
                            return sortAscending ? res : -res;
                        }

                        Node.Property p1 = getNodeProperty(n1, sortedByProperty);
                        Node.Property p2 = getNodeProperty(n2, sortedByProperty);
                        
                        if ( p1 == null && p2 == null )
                            return 0;
                        
                        try {
                            if ( p1 == null )
                                res = -1;
                            else if ( p2 == null )
                                res = 1;
                            else {
                                Object v1 = p1.getValue();
                                Object v2 = p2.getValue();
                                if ( v1 == null && v2 == null )
                                    return 0;
                                else if( v1 == null )
                                    res = -1;
                                else if ( v2 == null )
                                    res = 1;
                                else {
                                    if (v1.getClass() != v2.getClass() || !(v1 instanceof Comparable)) {
                                        v1 = v1.toString();
                                        v2 = v2.toString();
                                    }
                                    res = ((Comparable)v1).compareTo(v2);
                                }
                            }
                            return sortAscending ? res : -res;
                        }
                        catch (Exception ex) {
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

    /** Get the filter in effect for this view.
     * @return The filter being used on this view, or null if there is
     * no filter (e.g. all tasks are shown unconditionally).
     */
    public Filter getFilter() {
        return filter;
    }
    
    /** Set the filter to be used in this view.
     * @param filter The filter to be set, or null, to remove filtering.
     * @param showStatusBar When true, show a status bar with a remove button etc.
     */
    public void setFilter(Filter filter, boolean showStatusBar) {
        this.filter = filter;
        try {
            getExplorerManager().setSelectedNodes(new Node[0]);
        } catch (PropertyVetoException e) {
        }
        if (showStatusBar && (filter != null)) {
            addFilterPanel();
            //expandAll(); // [PENDING] Make this optional?
        } else {
            removeFilterPanel();
        }
        setRoot();
    }

    
    public void componentActivated() {
        super.componentActivated();
        FindAction find = (FindAction)FindAction.get(FindAction.class);
        FilterAction filter = (FilterAction)FilterAction.get(FilterAction.class);
        find.setActionPerformer(filter);
        
        /*
        DeleteAction delete = (DeleteAction) DeleteAction.get(DeleteAction.class);
        delete.setActionPerformer(deletePerformer);

        */

        installJumpActions(true);
    } 
    
    public void componentDectivated() {
        super.componentDeactivated();
        
        FindAction find = (FindAction)FindAction.get(FindAction.class);
        find.setActionPerformer(null);

        /*
        DeleteAction delete = (DeleteAction) DeleteAction.get(DeleteAction.class);
        delete.setActionPerformer(null);
        */
    }
    
    private JPanel filterPanel = null;
    private JButton removeFilterButton = null;
    private JLabel filterLabel = null;
    
    private void addFilterPanel() {
        if (filterPanel != null) {
            return;
        }

        filterPanel = new JPanel();
        filterPanel.setLayout(new BorderLayout());
        
        filterLabel = new JLabel();
        updateFilterCount(getExplorerManager().getRootContext().getChildren());
        removeFilterButton = new JButton();
        removeFilterButton.setText(NbBundle.getMessage(TaskListView.class,
                                          "RemoveFilter")); // NOI18N
        removeFilterButton.addActionListener(this);
                       
        // XXX add edit filter button?

        filterPanel.add(filterLabel, BorderLayout.CENTER);
        filterPanel.add(removeFilterButton, BorderLayout.EAST);

        add(filterPanel, BorderLayout.NORTH);
    }

    /** Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == removeFilterButton) {
            setFilter(null, false);
            /*
        } else {
            super.actionPerformed(evt);
            */
        }
    }

    void updateFilterCount(Children children) {
        if (filterLabel == null) {
            return;
        }
        
        // NOTE This doesn't count subtasks correctly... it only counts the
        // first level hierarchy...
        Integer showingCount = new Integer(children.getNodes() == null ?
                                           0 : children.getNodes().length);
        Integer totalCount = new Integer(rootNode.getChildren().
                                         getNodes().length);
        filterLabel.setText(NbBundle.getMessage(TaskListView.class,
                                          "FilterCount", // NOI18N
                                          showingCount,
                                          totalCount));
    }
    
    private void removeFilterPanel() {
        if (filterPanel != null) {
            remove(filterPanel);
            filterPanel = null;
            removeFilterButton = null;
            filterLabel = null;
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
        Task task = TaskNode.getTask(node);

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

    /** Assign the Next/Previous build actions to point to the
     * task window */
    void installJumpActions(boolean install) {

        // TODO - only install if the list is non empty (and call
        // this method from SMI when the list becomes non-empty)

        // Make F12 jump to next task
        NextOutJumpAction nextAction = (NextOutJumpAction)NextOutJumpAction.get(NextOutJumpAction.class);
        PreviousOutJumpAction previousAction = (PreviousOutJumpAction)PreviousOutJumpAction.get(PreviousOutJumpAction.class);
        if (install) {
            nextAction.setActionPerformer(jumpPerformer);
            previousAction.setActionPerformer(jumpPerformer);        
        } else {
            nextAction.setActionPerformer(null);
            previousAction.setActionPerformer(null);        
        }

    }

    private JumpActionPerformer jumpPerformer = new JumpActionPerformer();

    final class JumpActionPerformer implements ActionPerformer {

        /** Performer for actions */
        public void performAction(final SystemAction action) {            
            invokeLater(new Runnable() {
		    public void run() {
			if (action instanceof NextOutJumpAction) {
			    // Traditionally bound to F12
                            nextTask();
			} else if (action instanceof PreviousOutJumpAction) {
                            prevTask();
			}
                        // updateNextPrevActions();
		    }
		});
        }
    }

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
    void nextTask() {
        TaskList list = getList();
        Task curr = getCurrentTask();
        Task next = null;
        if (curr == null) {
            List sgs = list.getTasks();
            if (sgs != null) {
                next = (Task)sgs.get(0);
            } else {
                return;
            }
        } else {
            next = list.findNext(curr, wrapWarned);
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
                Annotation anno = getAnnotation(next);
                if (anno != null) {
                    show(next, anno);
                }
            }
            select(next);
            //StatusDisplayer.getDefault().setStatusText(next.getSummary());
        }

    }
    
    /** Show the previous task in the view */
    void prevTask() {
        TaskList list = getList();
        Task curr = getCurrentTask();
        Task prev = null;
        if (curr == null) {
            List sgs = list.getTasks();
            if (sgs != null) {
                prev = (Task)sgs.get(0);
            } else {
                return;
            }
        } else {
            prev = list.findPrev(curr, wrapWarned);
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
                Annotation anno = getAnnotation(prev);
                if (anno != null) {
                    show(prev, anno);
                }
            }
            select(prev);
            //StatusDisplayer.getDefault().setStatusText(prev.getSummary());
        }
    }

    /** Return an editor annotation to use to show the given task */
    protected Annotation getAnnotation(Task task) {
        // Make sure the editor is here and providing the annotation type
        FileObject f = Repository.getDefault().getDefaultFileSystem().
            findResource("Editors/AnnotationTypes/TaskAnnotation.xml"); // NOI18N
        if (f == null) {
            return null;
        }
        return new TaskAnnotation(task);
    }


    /** @return The currently selected task in the view,
      or if none, the first task (or null, if there are
      no tasks */
    private Task getCurrentTask() {
        Node[] node = getExplorerManager().getSelectedNodes();
        if ((node != null) && (node.length > 0)) {
            Task s = (Task)TaskNode.getTask(node[0]);
            if (s.getParent() != null) { // Make sure it's not the root node
                return s;
            }
        }
        return null;
    }

    protected void componentHidden() {
        // Stop listening for node activation
        getExplorerManager().removePropertyChangeListener(this);	    
        
        // Remove jump actions
        // Cannot do this, because componentHidden can be called
        // after another TaskListView is shown (for example when you
        // switch from one tasklist view to another) so this would
        // cripple the newly showing tasklist view.
        //    installJumpActions(false);
    }

    protected void componentShowning() {
        // Listen for node activation
        getExplorerManager().addPropertyChangeListener(this);	    
        
        installJumpActions(true);
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
                        scheduleNodeExpansion(target, hops+1);
                    }
                }
            });
    }
    
    /** Transfer focus to the dialog */
    public void requestFocus() {
        super.requestFocus();
        if (treeTable != null) {
            treeTable.requestFocus();
        }
    }
}
