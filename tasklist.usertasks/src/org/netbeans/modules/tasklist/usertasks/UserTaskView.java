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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.ColumnProperty;
import org.netbeans.modules.tasklist.core.ExportAction;
import org.netbeans.modules.tasklist.core.GoToTaskAction;
import org.netbeans.modules.tasklist.core.ObservableList;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskAnnotation;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskListener;
import org.netbeans.modules.tasklist.core.columns.ColumnsConfiguration;
import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.netbeans.modules.tasklist.core.filter.RemoveFilterAction;
import org.netbeans.modules.tasklist.usertasks.treetable.ChooseColumnsPanel;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableModel;
import org.openide.actions.DeleteAction;
import org.openide.awt.MouseUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/** 
 * View showing the todo list items
 *
 * @author Tor Norbye
 */
public class UserTaskView extends TaskListView implements TaskListener {
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

    static UserTaskView getDefault() {
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

    private static ArrayList views = null; // leak??? YES! Remove in componentClosed!

    /** Return true iff the default view has been created already */
    static boolean defaultViewCreated() {
        return defview != null;
    }
    
    /** 
     * Return the currently active user task view, or the default
     * one if none are active 
     */
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
     * Construct a new UserTaskView showing a given list. Most work
     * is deferred to componentOpened. NOTE: this is only for use by
     * the window system when deserializing windows. Client code
     * should not call it. I can't make it protected because then
     * the window system wouldn't be able to get to this. But the
     * code relies on readExternal getting called after this
     * constructor to finalize construction of the window.
     */
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
            SystemAction.get(RemoveFilterAction.class),
            SystemAction.get(StartTaskAction.class)
        };
    }
    
    protected Component createCenterComponent() {
        tt = new UserTasksTreeTable(
            getExplorerManager(), (UserTaskList) getModel(),
            getFilter());
        
        final JScrollPane sp = new JScrollPane(tt,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        sp.addMouseListener(new MouseUtils.PopupMouseAdapter() {
            public void showPopup(MouseEvent e) {
                Action[] actions = new Action[] {
                    SystemAction.get(FilterAction.class),
                    SystemAction.get(ExportAction.class)
                };
                JPopupMenu pm = Utilities.actionsToPopup(actions, sp);
                pm.show(sp, e.getX(), e.getY());
            }
        });
        
        ChooseColumnsPanel.installChooseColumnsButton(sp);
        return sp;
    }
    
    public void componentActivated() {
        super.componentActivated();

        // it's strange I'd expect live listener based solution
        Iterator it = getModel().getTasks().iterator();
        while (it.hasNext()) {
            UserTask next = (UserTask) it.next();
            next.updateLineNumberRecursively();
        }

        if (UTUtils.LOGGER.isLoggable(Level.FINE)) {
            InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
        super.readExternal(objectInput);
	int ver = objectInput.read();

        if (ver >= 2) {
            // Read tasklist file name
	    String urlString = (String)objectInput.readObject();
            if (urlString != null) {
                URL url = new URL(urlString);
                final FileObject[] fos = URLMapper.findFileObjects(url);
                if ((fos != null) && (fos.length > 0)) {
                    setModel(new UserTaskList(fos[0]));
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            setName(fos[0].getNameExt());
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

    protected ColumnProperty[] createColumns() {
        return new ColumnProperty[0];
    }
    
    protected Node createRootNode() {
        return new UserTaskListNode((UserTaskList) getModel(), null);
    }

    /** Show the given task. "Showing" means getting the editor to
     * show the associated file position, and open up an area in the
     * tasklist view where the details of the task can be fully read.
     */
    public void showTaskInEditor(Task item, TaskAnnotation annotation) {
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
        super.showTaskInEditor(item, annotation);
        if (prevTask != null) {
            if ((prevTask.getLine() != null) && (task.getAnnotation() == null)) {
                TaskAnnotation anno = new TaskAnnotation(prevTask, false);
                anno.attach(prevTask.getLine());
                prevTask.setAnnotation(anno);                
            }
        }
    }

    protected void setModel(ObservableList list) {
        hideList();
        tasklist = list;
        getModel().addTaskListener(this);
        UserTaskList utl = (UserTaskList) this.getList();
        utl.showAnnotations(utl.getTasks().iterator());
    }
    
    protected void hideList() {
        super.hideList();
        UserTaskList utl = (UserTaskList) this.getModel();
        if (utl != null)
            utl.hideAnnotations(utl.getTasks().iterator());
    }

    public String toString() { 
        return "UserTaskView(" + getName() + ", " + category + ", " + getModel() + ")"; // NOI18N
    }
    
    public org.netbeans.modules.tasklist.core.filter.Filter createFilter() {
        return new UserTaskFilter("Simple"); // NOI18N
    }

    protected java.lang.String preferredID() {
        return "org.netbeans.modules.tasklist.usertasks.Window";
    }    

    protected void setFiltered() {
        if (getFilter() != null) {
            ((RemoveFilterAction) SystemAction.get(RemoveFilterAction.class)).enable();
        }

        TreeTableModel ttm = tt.getTreeTableModel();
        if (ttm instanceof UserTasksTreeTableModel) {
            ((UserTasksTreeTableModel) ttm).destroy();
        }
        tt.setTreeTableModel(new UserTasksTreeTableModel((UserTaskList) getModel(), 
            tt.getSortingModel(), getFilter()));
    }

    protected void storeColumnsConfiguration() {
        if (tt == null)
            return;
        
        ColumnsConfiguration columns = getDefaultColumns();
        tt.storeColumns(columns);
    }

    protected void loadColumnsConfiguration() {
        if (UTUtils.LOGGER.isLoggable(Level.FINER))
            Thread.dumpStack();
        if (tt == null)
            return;
        
        ColumnsConfiguration cc = getDefaultColumns();
        tt.loadColumns(cc);
    }
    
    public void expandAll() {
        tt.expandAll();
    }
    
    public void select(Task task) {
        if (isShowing() == false) return;
        
        tt.select((UserTask) task);
    }
}
