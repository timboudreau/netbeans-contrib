/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks;

import java.beans.PropertyEditorManager;
import java.util.Date;
import java.util.List;
import org.netbeans.api.tasklist.SuggestionPriority;
import org.netbeans.modules.tasklist.core.ExpandAllAction;
import org.netbeans.modules.tasklist.core.ExportAction;
import org.netbeans.modules.tasklist.core.FilterAction;
import org.netbeans.modules.tasklist.core.GoToTaskAction;
import org.netbeans.modules.tasklist.core.ImportAction;
import org.netbeans.modules.tasklist.core.PriorityPropertyEditor;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.openide.ErrorManager;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.Reflection;
import org.openide.nodes.Sheet.Set;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;


class UserTaskNode extends TaskNode {
    /* /// XXX FIXME TODO PENDING @todo !
     *
     * The following is a hack. In order to show Date objects in the
     * table (I don't want to show String representations of these dates,
     * because that means sorting happens based on String comparisons instead
     * of Date comparisons, which is wrong - 6/10 will be considered earlier
     * than 6/3 for example) it turns out the IDE needs to know about a
     * Date property editor (even though my Date fields are read-only so
     * they cannot be edited.)
     * Therefore, I have to register a dummy DateEditor.
     * This will not be necessary when
     *   http://www.netbeans.org/issues/show_bug.cgi?id=19899
     * is fixed.
     */
    static { 
	 PropertyEditorManager.registerEditor(
			 getKlass("java.util.Date"), 
			 getKlass("org.netbeans.modules.tasklist.usertasks.DateEditor"));
    }

    // From NonGui.java - supports the above hack, please read its comment:
    /** Lazily loads classes */ // #9951
    private static final Class getKlass(String cls) {
        try {
            return Class.forName(cls, false, UserTask.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getLocalizedMessage());
        }
    }
        
    // Leaf
    UserTaskNode(UserTask item) {
        super(item);
        init(item);
    } 

    // Non-leaf/parent
    UserTaskNode(UserTask item, List subtasks) {
        super(item, subtasks);
        init(item);
    }

    private void init(UserTask item) {
        this.item = item;
        setDefaultAction(SystemAction.get(ShowTaskAction.class));
    }

    // Handle cloning specially (so as not to invoke the overhead of FilterNode):
    public Node cloneNode () {
	UserTask uitem = (UserTask)item;
        if (uitem.hasSubtasks()) {
            return new UserTaskNode(uitem, uitem.getSubtasks());
        } else {
            return new UserTaskNode(uitem);
        }
    }

    /**
       @todo Should "task has associated filepos" and "task is sourcescan task"
         have separate icons?
    */
    protected void updateIcon() {
	UserTask uitem = (UserTask)item;
	if (uitem.getIcon() != null) {
            return;
        }
        if (uitem.isDone()) {
            setIconBase("org/netbeans/modules/tasklist/core/doneItem"); // NOI18N
        } else if (uitem.hasAssociatedFilePos()) {
            setIconBase("org/netbeans/modules/tasklist/core/editorTask"); // NOI18N
        } else {
            super.updateIcon();
        }
    }
    
    protected SystemAction[] createActions() {
	
	// TODO Perform lookup here to compute an aggregate
	// menu from other modules as well. But how do we determine
	// order? I think NetBeans 4.0's actions re-work will have
	// some better support for integrating context menus so I won't
	// try to be too clever here...

	// XXX look up and locate actions

        if (item.getParent() == null) {
            // Create actions shown on an empty tasklist (e.g. only root
            // is there)
            return new SystemAction[] {};
        } else {
            return new SystemAction[] {
                SystemAction.get(NewTaskAction.class),
                null,
                SystemAction.get(ShowTaskAction.class),
                SystemAction.get(GoToTaskAction.class),
                null,
                SystemAction.get(CutAction.class),
                SystemAction.get(CopyAction.class),
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(DeleteAction.class),
                null,
                SystemAction.get(PropertiesAction.class),
            };
        }
    }

    /** Creates properties.
     */
    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Set ss = s.get(Sheet.PROPERTIES);
        if (item.getParent() == null)
            return s;
        
        Set sse = Sheet.createExpertSet();
        s.put(sse);
        
        try {
            PropertySupport.Reflection p;
            p = new Reflection(item, String.class, "getSummary", "setSummary"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(TaskNode.class, "Description")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(TaskNode.class, "DescriptionHint")); // NOI18N
            ss.put(p);
            
            
            p = new Reflection(item, SuggestionPriority.class, "getPriority", "setPriority"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_PRIO);
            p.setPropertyEditorClass(PriorityPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Priority")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "PriorityHint")); // NOI18N
            ss.put(p);
            
            
            p = new Reflection(item, Boolean.TYPE, "isDone", "setDone"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_DONE);
            p.setPropertyEditorClass(DonePropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Done")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "DoneHint")); // NOI18N
            ss.put(p);
            

            p = new Reflection(item, Integer.TYPE, "getPercentComplete", "setPercentComplete") { // NOI18N
                public Object getValue() {
                    UserTask task = (UserTask) instance;
                    PercentsPropertyEditor.Value v = 
                        new PercentsPropertyEditor.Value();
                    v.progress = task.getPercentComplete();
                    v.computed = task.isProgressComputed();
                    return v;
                }
                public void setValue(Object value) {
                    UserTask task = (UserTask) instance;
                    PercentsPropertyEditor.Value v = 
                        (PercentsPropertyEditor.Value) value;
                    if (v.computed)
                        task.setPercentComplete(-1);
                    else
                        task.setPercentComplete(v.progress);
                }
            };
            p.setName(UserTaskView.PROP_TASK_PERCENT);
            p.setPropertyEditorClass(PercentsPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Percent")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "PercentHint")); // NOI18N
            ss.put(p);
            

            p = new Reflection(item, String.class, "getDetails", "setDetails"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_DETAILS);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Details")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "DetailsHint")); // NOI18N
            ss.put(p);
            
            p = new Reflection(item, String.class, "getFileBaseName", "setFileBaseName"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_FILE);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "File")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "FileHint")); // NOI18N
            ss.put(p);

            p = new Reflection(item, Integer.TYPE, "getLineNumber", "setLineNumber"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_LINE);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Line")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "LineHint")); // NOI18N
            ss.put(p);
            
            p = new Reflection(item, String.class, "getCategory", "setCategory"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_CAT);
            p.setPropertyEditorClass(CategoryPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Category")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "CategoryHint")); // NOI18N
            ss.put(p);

            //p = new PropertySupport.Reflection(item, String.class, "getCreatedDateString", null /* readonly*/); // NOI18N
            p = new Reflection(item, Date.class, "getCreatedDate", null /* readonly*/); // NOI18N
            p.setName(UserTaskView.PROP_TASK_CREATED);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Created")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "CreatedHint")); // NOI18N
            ss.put(p);

            //p = new PropertySupport.Reflection(item, String.class, "getLastEditedDateString", null /* readonly*/); // NOI18N
            p = new Reflection(item, Date.class, "getLastEditedDate", null /* readonly*/); // NOI18N
            p.setName(UserTaskView.PROP_TASK_EDITED);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Edited")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "EditedHint")); // NOI18N
            ss.put(p);


            //p = new Reflection(item, Date.class, "getDueDate", null /* readonly*/); // NOI18N
            p = new Reflection(item, Date.class, "getDueDate", "setDueDate"); // NOI18N

            
            p.setName(UserTaskView.PROP_TASK_DUE);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Due")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "DueHint")); // NOI18N
            ss.put(p);


            
        } catch (NoSuchMethodException nsme) {
            ErrorManager.getDefault().notify(nsme);
        }
        return s;
    }

    public boolean canDestroy() {
        // Can't destroy the root node:
        return (item.getParent() != null);
    }

    /** Can this node be copied?
    * @return <code>true</code>
    */
    public boolean canCopy () {
        // Can't copy the root node:
        return (item.getParent() != null);
    }

    /** Can this node be cut?
    * @return <code>false</code>
    */
    public boolean canCut () {
        // Can't cut the root node:
        return (item.getParent() != null);
    }    
}

