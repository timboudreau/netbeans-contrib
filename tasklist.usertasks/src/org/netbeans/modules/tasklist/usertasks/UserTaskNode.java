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

package org.netbeans.modules.tasklist.usertasks;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.core.GoToTaskAction;
import org.netbeans.modules.tasklist.core.ExpandAllAction;
import org.netbeans.modules.tasklist.core.ExportAction;
import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.netbeans.modules.tasklist.core.GoToTaskAction;
import org.netbeans.modules.tasklist.core.ImportAction;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.core.TaskTransfer;
import org.netbeans.modules.tasklist.core.editors.LineNumberPropertyEditor;
import org.netbeans.modules.tasklist.core.editors.PriorityPropertyEditor;
import org.netbeans.modules.tasklist.core.editors.StringPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.editors.DurationPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PercentsPropertyEditor;
import org.openide.ErrorManager;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PasteAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.Reflection;
import org.openide.nodes.Sheet.Set;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;
import org.openide.util.datatransfer.PasteType;


class UserTaskNode extends TaskNode {
    private static final Logger LOGGER = TLUtils.getLogger(UserTaskNode.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }
    
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
			 getKlass("org.netbeans.modules.tasklist.usertasks.editors.DateEditor"));
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
    } 

    // Non-leaf/parent
    UserTaskNode(UserTask item, List subtasks) {
        super(item, subtasks);
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

    protected void updateIcon() {
        UserTask uitem = (UserTask)item;
        if (uitem.getIcon() != null) {
            return;
        }
        if (uitem.isDone()) {
            setIconBase("org/netbeans/modules/tasklist/core/doneItem"); // NOI18N
        } else {
            setIconBase("org/netbeans/modules/tasklist/core/task"); // NOI18N
        }
    }
    
    protected SystemAction[] createActions() {
        if (item.getParent() == null) {
            // Create actions shown on an empty tasklist (e.g. only root
            // is there)
            return new SystemAction[] {
                SystemAction.get(NewTaskAction.class),
                SystemAction.get(NewTaskListAction.class),
                null,
                SystemAction.get(PauseAction.class),
                null,
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(FilterAction.class),
                SystemAction.get(PurgeTasksAction.class),
                SystemAction.get(ExpandAllAction.class),
                null,
                SystemAction.get(ImportAction.class),
                SystemAction.get(ExportAction.class),
            };
        } else {
            return new SystemAction[] {
                SystemAction.get(NewTaskAction.class),
                SystemAction.get(NewTaskListAction.class),
                null,
                SystemAction.get(PauseAction.class),
                null,
                SystemAction.get(StartTaskAction.class),
                SystemAction.get(ShowTaskAction.class),
                SystemAction.get(GoToTaskAction.class),
                null,
                SystemAction.get(CutAction.class),
                SystemAction.get(CopyAction.class),
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(DeleteAction.class),

                // "Global" actions (not node specific)
                null,
                SystemAction.get(FilterAction.class),
                SystemAction.get(PurgeTasksAction.class),
                SystemAction.get(ExpandAllAction.class),
                null,
                SystemAction.get(ImportAction.class),
                SystemAction.get(ExportAction.class),

                // Property: node specific, but by convention last in menu
                null,
                SystemAction.get(PropertiesAction.class)
            };
        }
    }

    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Set ss = s.get(Sheet.PROPERTIES);
        if (item.getParent() == null)
            return s;
        
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
                    task.setProgressComputed(v.computed);
                    if (!v.computed)
                        task.setPercentComplete(v.progress);
                }
            };
            p.setName(UserTaskView.PROP_TASK_PERCENT);
            p.setPropertyEditorClass(PercentsPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Percent")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "PercentHint")); // NOI18N
            ss.put(p);
            
            p = new Reflection(item, Integer.TYPE, "getEffort", null);
            p.setName("effort");
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Effort2")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "EffortHint")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE);
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new Reflection(item, Integer.TYPE, "getRemainingEffort", null);
            p.setName("remainingEffort");
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "RemainingEffort")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "RemainingEffortHint")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE);
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new Reflection(item, Integer.TYPE, "getSpentTime", null);
            p.setName("spentTime");
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "SpentTime")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "SpentTimeHint")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE);
            p.setPropertyEditorClass(DurationPropertyEditor.class);
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
            p.setValue("suppressCustomEditor", Boolean.TRUE);
            ss.put(p);

            p = new Reflection(item, Integer.TYPE, "getLineNumber", "setLineNumber"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_LINE);
            p.setPropertyEditorClass(LineNumberPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Line")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "LineHint")); // NOI18N
            ss.put(p);
            
            p = new Reflection(item, String.class, "getCategory", "setCategory"); // NOI18N
            p.setName(UserTaskView.PROP_TASK_CAT);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Category")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "CategoryHint")); // NOI18N
            p.setValue("canEditAsText", Boolean.TRUE); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE);
            ss.put(p);

            p = new Reflection(item, Date.class, "getCreatedDate", null /* readonly*/); // NOI18N
            p.setName(UserTaskView.PROP_TASK_CREATED);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Created")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "CreatedHint")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE);
            ss.put(p);

            p = new Reflection(item, Date.class, "getLastEditedDate", null /* readonly*/); // NOI18N
            p.setName(UserTaskView.PROP_TASK_EDITED);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "Edited")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "EditedHint")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE);
            ss.put(p);


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
    
    public javax.swing.Action getPreferredAction() {
        if (item.getParent() == null)
            return SystemAction.get(NewTaskAction.class);
        else
            return SystemAction.get(ShowTaskAction.class);
    }
    
    public boolean canRename() {
        return (item.getParent() != null);
    }
    
    protected void createPasteTypes(java.awt.datatransfer.Transferable t, List s) {
        super.createPasteTypes(t, s);
        PasteType p = createTodoPasteType(t, (UserTask) item);
        if (p != null) {
            s.add(p);
        }
    }

    /** 
     * Create a paste type from a transferable.
     *
     * @param t the transferable to check
     * @param parent parent for the pasted task
     * @return an appropriate paste type, or null if not appropriate
     */
    public static PasteType createTodoPasteType(
    Transferable t, UserTask parent) {
        if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
            try {
                // Multiselection
                final MultiTransferObject mto = (MultiTransferObject)
                    t.getTransferData(ExTransferable.multiFlavor);
                if (mto.areDataFlavorsSupported(
                    new DataFlavor[] {TaskTransfer.TODO_FLAVOR})) {
                    return new UserTaskNode.TodoPaste(t, parent);
                }
            } catch (UnsupportedFlavorException e) {
                ErrorManager.getDefault().notify(e);
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        } 
        
        if (t.isDataFlavorSupported(TaskTransfer.TODO_FLAVOR)) {
            return new TodoPaste(t, parent);
        } 
        return null;
    }

    /**
     * Paste type for a pasted task
     */
    private static final class TodoPaste extends PasteType {
        private final Transferable t;
        private final UserTask parent;
        
        /**
         * Creates a paste type for a UserTask
         *
         * @param t a transferable object
         * @param parent parent task for the pasted task
         */
        public TodoPaste(Transferable t, UserTask parent) {
            this.t = t;
            this.parent = parent;
        }
        
        public String getName() {
            return NbBundle.getMessage(TaskTransfer.class, 
                "LBL_todo_paste_as_subtask"); // NOI18N
        }
        
        public HelpCtx getHelpCtx() {
            return new HelpCtx("org.netbeans.modules.todo"); // NOI18N
        }
        
        public Transferable paste() throws IOException {
            try {
                if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
                    // Multiselection
                    final MultiTransferObject mto = (MultiTransferObject)
                        t.getTransferData(ExTransferable.multiFlavor);
                    if (mto.areDataFlavorsSupported(
                        new DataFlavor[] {TaskTransfer.TODO_FLAVOR})) {
                        for (int i = 0; i < mto.getCount(); i++) {
                            Task item = (Task)
                            mto.getTransferData(i, TaskTransfer.TODO_FLAVOR);
                            addTask(item);
                        }
                        return null;
                    }
                } 
                
                if (t.isDataFlavorSupported(TaskTransfer.TODO_FLAVOR)) {
                    Task item = (Task)t.getTransferData(TaskTransfer.TODO_FLAVOR);
                    addTask(item);
                } 
            } catch (UnsupportedFlavorException ufe) {
                // Should not happen.
                IOException ioe = new IOException(ufe.toString());
                ErrorManager.getDefault().annotate(ioe, ufe);
                throw ioe;
            }
            return null;
        }
        
        /**
         * Adds a task
         *
         * @param item a task
         */
        private void addTask(Task item) {
            UserTask ut;
            if (item instanceof UserTask) {
                ut = (UserTask) item;
            } else {
                ut = new UserTask(item.getSummary());
                ut.setDetails(item.getDetails());
                ut.setLine(item.getLine());
                ut.setPriority(item.getPriority());
            }
            parent.addSubtask(ut);
        }
    }
}

