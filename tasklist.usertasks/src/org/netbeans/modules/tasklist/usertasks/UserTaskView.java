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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.openide.text.Annotation;
import org.netbeans.api.tasklist.SuggestionPriority;
import org.netbeans.modules.tasklist.core.ColumnProperty;
import org.netbeans.modules.tasklist.core.ExpandAllAction;
import org.netbeans.modules.tasklist.core.ExportAction;
import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.netbeans.modules.tasklist.core.ImportAction;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskAnnotation;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskListener;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.core.GoToTaskAction;
import org.netbeans.modules.tasklist.core.filter.RemoveFilterAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;



/** View showing the todo list items
 * @author Tor Norbye
 */
public class UserTaskView extends TaskListView implements TaskListener {
    
    /** Construct a new UserTaskView. Most work is deferred to
	componentOpened. NOTE: this is only for use by the window
	system when deserializing windows. Client code should not call
	it. I can't make it protected because then the window system
	wouldn't be able to get to this. But the code relies on
	readExternal getting called after this constructor to finalize
	construction of the window.*/
    public UserTaskView() {
        this(UserTaskList.getDefault(), true);
    }

    /** Construct a new UserTaskView showing a given list. Most work
	is deferred to componentOpened. NOTE: this is only for use by
	the window system when deserializing windows. Client code
	should not call it.  I can't make it protected because then
	the window system wouldn't be able to get to this. But the
	code relies on readExternal getting called after this
	constructor to finalize construction of the window.*/
    public UserTaskView(UserTaskList list, boolean isDefault) {
	super(UserTaskList.USER_CATEGORY,
              isDefault ?
              NbBundle.getMessage(UserTaskView.class,
                    "TaskViewName") : // NOI18N
              list.getFile().getName(),
              Utilities.loadImage(
                    "org/netbeans/modules/tasklist/usertasks/taskView.gif"), // NOI18N
              true,
              list);
	if (isDefault && (defview == null)) {
	    defview = this;
	}
	synchronized (TaskListView.class) {
	    if (views == null) {
		views = new ArrayList();
	    }
	    views.add(this);
	}
    }

    public SystemAction[] getToolBarActions() {
        return new SystemAction[] {
            SystemAction.get(NewTaskAction.class),
            SystemAction.get(DeleteAction.class),
            SystemAction.get(GoToTaskAction.class),
            SystemAction.get(FilterAction.class),
            new RemoveFilterAction(this)
        };
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
    /*
    public SystemAction[] getSystemActions() {
        SystemAction[] todoActions = new SystemAction[] {
            null,
            SystemAction.get(PasteAction.class),
            null,
            SystemAction.get(FilterAction.class),
            SystemAction.get(PurgeTasksAction.class),
            SystemAction.get(ExpandAllAction.class),
            null,
            SystemAction.get(ImportAction.class),
            SystemAction.get(ExportAction.class)
        };
        SystemAction[] sa = super.getSystemActions ();
        return SystemAction.linkActions (sa, todoActions);
    }
    */
    
    public void componentActivated() {
        super.componentActivated();
        UserTaskList utl = (UserTaskList) this.tasklist;
        ((UserTask) utl.getRoot()).updateLineNumberRecursively();        
    }
    
    /** Read in a serialized version of the tasklist
     * and reads in sorting preferences etc. such that
     * we use the same preferences now.
     * @param objectInput object stream to read from
     * @todo Use a more robust serialization format (not int uid based)
     * @throws IOException
     * @throws ClassNotFoundException  */    
    public void readExternal(ObjectInput objectInput) throws IOException, java.lang.ClassNotFoundException {
        super.readExternal(objectInput);
	int ver = objectInput.read();
        //assert ver == 1 : "serialization version incorrect; should be 1";

        if (ver >= 2) {
            // Read tasklist file name
	    String urlString = (String)objectInput.readObject();
            if (urlString != null) {
                URL url = new URL(urlString);
                FileObject[] fos = URLMapper.findFileObjects(url);
                if ((fos != null) && (fos.length > 0)) {
                    tasklist = new UserTaskList(fos[0]);
                    title = fos[0].getName();
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
        
    }

    /** Write out relevant settings in the window (visible
     * columns, sorting order, etc.) such that they can
     * be reconstructed the next time the IDE is started.
     * @todo Use a more robust serialization format (not int uid based)
     * @param objectOutput Object stream to write to
     * @throws IOException  */    
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
	if (!persistent) {
	    System.out.println("INTERNAL ERROR: THIS WINDOW SHOULD NOT HAVE BEEN PERSISTED!");
	    return;
	}

        super.writeExternal(objectOutput);

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

    static final String PROP_TASK_DONE = "taskDone"; // NOI18N
    static final String PROP_TASK_DUE = "taskDue"; // NOI18N
    static final String PROP_TASK_PRIO = "taskPrio"; // NOI18N
    static final String PROP_TASK_CAT = "taskCat"; // NOI18N
    static final String PROP_TASK_FILE = "taskFile"; // NOI18N
    static final String PROP_TASK_LINE = "taskLine"; // NOI18N
    static final String PROP_TASK_DETAILS = "taskDetails"; // NOI18N
    static final String PROP_TASK_CREATED = "taskCreated"; // NOI18N
    static final String PROP_TASK_EDITED = "taskEdited"; // NOI18N
    static final String PROP_TASK_PERCENT = "taskPercent"; // NOI18N
    
    protected ColumnProperty[] createColumns() {
        return new ColumnProperty[] {
            getMainColumn(800),
            getPriorityColumn(true, 100),
            getCategoryColumn(false, 200),
            getDetailsColumn(false, 800),
            getFileColumn(false, 200),
            getLineColumn(false, 80),
            getCreatedColumn(false, 150),
            getEditedColumn(false, 150),
            getDueColumn(false, 150),
            getDoneColumn(true, 40),
            getPercentColumn(false, 100)
            
            // When adding more columns here, also remember to go to the 
            // constructor and add a column width setting 
            // (setTableColumnPreferredWidth)

            // TODO move to end!
        };
    };

    public ColumnProperty getPriorityColumn(boolean visible, int width) {
        return new ColumnProperty(
	    1, // UID -- never change (part of serialization
            PROP_TASK_PRIO,
            SuggestionPriority.class,
            NbBundle.getMessage(UserTaskView.class, "Priority"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "PriorityHint"), // NOI18N
            true,
            visible,
            width
            );
    }

    public ColumnProperty getCategoryColumn(boolean visible, int width) {
        return new ColumnProperty(
	    2, // UID -- never change (part of serialization
            PROP_TASK_CAT,
            String.class,
            NbBundle.getMessage(UserTaskView.class, "Category"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "CategoryHint"), // NOI18N
            true,
            visible,
            width
            );
    }
   
    public ColumnProperty getDetailsColumn(boolean visible, int width) {
        return new ColumnProperty(
	    3, // UID -- never change (part of serialization
            PROP_TASK_DETAILS,
            String.class,
            NbBundle.getMessage(UserTaskView.class, "Details"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "DetailsHint"), // NOI18N
            true,
            visible,
            width
            );
    }

    public ColumnProperty getFileColumn(boolean visible, int width) {
        return new ColumnProperty(
	    4, // UID -- never change (part of serialization
            PROP_TASK_FILE,
            String.class,
            NbBundle.getMessage(UserTaskView.class, "File"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "FileHint"), // NOI18N
            true,
            visible,
            width
            );
    }

    public ColumnProperty getLineColumn(boolean visible, int width) {
        return new ColumnProperty(
	    5, // UID -- never change (part of serialization
            PROP_TASK_LINE,
            Integer.TYPE,
            NbBundle.getMessage(UserTaskView.class, "Line"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "LineHint"), // NOI18N
            true,
            visible,
            width
            );
    }

    public ColumnProperty getCreatedColumn(boolean visible, int width) {
        return new ColumnProperty(
	    6, // UID -- never change (part of serialization
            PROP_TASK_CREATED,
            //String.class,
            Date.class,
            NbBundle.getMessage(UserTaskView.class, "Created"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "CreatedHint"), // NOI18N
            true,
            visible,
            width
            );
    }

    public ColumnProperty getEditedColumn(boolean visible, int width) {
        return new ColumnProperty(
	    7, // UID -- never change (part of serialization
            PROP_TASK_EDITED,
            //String.class,
            Date.class,
            NbBundle.getMessage(UserTaskView.class, "Edited"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "EditedHint"), // NOI18N
            true,
            visible,
            width
            );
    }

    public ColumnProperty getDoneColumn(boolean visible, int width) {
        return new ColumnProperty(
	    8, // UID -- never change (part of serialization
            PROP_TASK_DONE,
            Boolean.TYPE,
            NbBundle.getMessage(UserTaskView.class, "Done"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "DoneHint"), // NOI18N
            true,
            visible,
            width
            );
    }

    public ColumnProperty getDueColumn(boolean visible, int width) {
        return new ColumnProperty(
	    9, // UID -- never change (part of serialization
            PROP_TASK_DUE,
            //String.class,
            Date.class,
            NbBundle.getMessage(UserTaskView.class, "Due"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "DueHint"), // NOI18N
            true,
            visible,
            width
            );
    }

    public ColumnProperty getPercentColumn(boolean visible, int width) {
        return new ColumnProperty(
    	    10, // UID -- never change (part of serialization
            PROP_TASK_PERCENT,
            Integer.TYPE,
            NbBundle.getMessage(UserTaskView.class, "Percent"), // NOI18N
            NbBundle.getMessage(UserTaskView.class, "PercentHint"), // NOI18N
            true,
            visible,
            width
            );
    }
            
    
    private static UserTaskView defview = null;

    static UserTaskView getDefault() {
	if (defview == null) {
	    defview = new UserTaskView();
	
	    Workspace workspace = WindowManager.getDefault().
		getCurrentWorkspace();
	    Mode mode  = workspace.findMode("output"); // NOI18N
	    if (mode != null) {
		mode.dockInto(defview);
	    }

	    defview.open(workspace);
	    defview.requestVisible(); // requestFocus???
	}
	return defview;
    }

    private static ArrayList views = null; // leak???
    
    /** Return the currently active user task view, or the default
        one if none are active */
    public static TaskListView getCurrent() {
	// Try to figure out which view is current. If none is found to
	// be visible, guess one.
        if (views == null) {
            return defview;
        }
	Iterator it = views.iterator();
        while (it.hasNext()) {
	    UserTaskView tlv = (UserTaskView)it.next();
            if (tlv.isShowing()) {
		return tlv;
	    }
	}
        return defview;
    }    

    /** Locate a particular view showing the given list */
    static UserTaskView findListView(FileObject file) {
        if (views == null) {
            return null;
        }
 	Iterator it = views.iterator();
        while (it.hasNext()) {
	    UserTaskView tlv = (UserTaskView)it.next();
            if (((UserTaskList)tlv.getList()).getFile() == file) {
                return tlv;
            }
        }
        return null;
    }

    protected TaskNode createRootNode() {
        UserTask root = (UserTask)tasklist.getRoot();
        return new UserTaskNode(root, root.getSubtasks());
    }

    /** Show the given task. "Showing" means getting the editor to
     * show the associated file position, and open up an area in the
     * tasklist view where the details of the task can be fully read.
     */
    public void showTask(Task item, TaskAnnotation annotation) {
        UserTask task = (UserTask)item;
        UserTask prevTask = null;
        if ((taskMarker != null) &&
            (taskMarker.getTask() instanceof UserTask)) {
            prevTask = (UserTask)taskMarker.getTask();
        }
        if (task.getAnnotation() != null) {
            task.getAnnotation().detach();
            task.setAnnotation(null);
        }
        super.showTask(item, annotation);
        if (prevTask != null) {
            if ((prevTask.getLine() != null) && (task.getAnnotation() == null)) {
                TaskAnnotation anno = new TaskAnnotation(prevTask, false);
                anno.attach(prevTask.getLine());
                prevTask.setAnnotation(anno);                
            }
        }
    }

    protected void showList() {
        super.showList();
        UserTaskList utl = (UserTaskList) this.tasklist;
        utl.showAnnotations((UserTask)utl.getRoot());
    }
    
    protected void hideList() {
        super.hideList();
        UserTaskList utl = (UserTaskList) this.tasklist;
        utl.hideAnnotations((UserTask)utl.getRoot());
    }

    public String toString() { 
        return "UserTaskView(" + title + ", " + category + ", " + tasklist + ")"; // NOI18N
    }
}
