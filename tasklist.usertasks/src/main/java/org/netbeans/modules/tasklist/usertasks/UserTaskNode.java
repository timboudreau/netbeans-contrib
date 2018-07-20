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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

import org.netbeans.modules.tasklist.filter.FilterAction;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.export.ExportAction;
import org.netbeans.modules.tasklist.export.ImportAction;
import org.netbeans.modules.tasklist.filter.RemoveFilterAction;

import org.netbeans.modules.tasklist.usertasks.actions.CollapseAllAction;
import org.netbeans.modules.tasklist.usertasks.actions.ExpandAllUserTasksAction;
import org.netbeans.modules.tasklist.usertasks.actions.GoToUserTaskAction;
import org.netbeans.modules.tasklist.usertasks.actions.PauseAction;
import org.netbeans.modules.tasklist.usertasks.actions.ScheduleAction;
import org.netbeans.modules.tasklist.usertasks.actions.StartTaskAction;
import org.netbeans.modules.tasklist.usertasks.editors.DateEditor;
import org.netbeans.modules.tasklist.usertasks.editors.DurationPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PercentsPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.editors.PriorityPropertyEditor;
import org.netbeans.modules.tasklist.usertasks.transfer.UserTasksTransferable;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.actions.CopyAction;
import org.openide.actions.CutAction;
import org.openide.actions.PasteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.MultiTransferObject;
import org.openide.util.datatransfer.PasteType;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskResource;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.util.ObjectList;
import org.openide.actions.DeleteAction;
import org.openide.nodes.Node;

/**
 * Node for a user task
 *
 * @author tl
 */
public final class UserTaskNode extends AbstractNode {
    private UserTask item;
    private UserTaskList utl;
    private UTBasicTreeTableNode node;
    private UserTasksTreeTable tt;
    
    /**
     * Constructor
     *
     * @param node node in the tree associated with this one
     * @param item an user task that will be represented by this node.
     * @param utl user task list that this task belongs to. Should be != null
     * for root tasks
     * @param tt TreeTable
     */
    public UserTaskNode(UTBasicTreeTableNode node, UserTask item, UserTaskList utl,
    UserTasksTreeTable tt) {
        super(Children.LEAF);
        assert item != null;
        
        this.utl = utl;
        this.item = item;
        this.node = node;
        this.tt = tt;
        
        setName(item.getSummary());
        
        item.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String n = e.getPropertyName();
                if (n == UserTask.PROP_PROGRESS) { // NOI18N
                    int old = Math.round(((Float) e.getOldValue()).floatValue());
                    int new_ = Math.round(((Float) e.getNewValue()).floatValue());
                    UserTaskNode.this.firePropertyChange(
                        "percentComplete", // NOI18N
                        new Integer(old), new Integer(new_));
                } else if (n == UserTask.PROP_LINE || 
                        n == "started" || n == "dueAlarmSent") { // NOI18N 
                    // nothing
                    // TODO: strange property "started"??
                } else {
                    UserTaskNode.this.firePropertyChange(e.getPropertyName(),
                        e.getOldValue(), e.getNewValue());
                }
                if (n == "started" || // NOI18N
                        n == UserTask.PROP_VALUES_COMPUTED || 
                        n == UserTask.PROP_LINE) { // NOI18N
                    fireCookieChange();
                }
            }
        });
    } 

    /**
     * Returns the task associated with this node
     * @return 
     */
    public UserTask getTask() {
        return item;
    }
    
    public Action[] getActions(boolean empty) {
        UserTaskView utv = (UserTaskView) 
                SwingUtilities.getAncestorOfClass(UserTaskView.class, tt);
        if (empty) {
            return new Action[] {
                // TODO: SystemAction.get(NewTaskAction.class),
                null,
                PauseAction.getInstance(),
                null,
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(FilterAction.class),
                utv.purgeTasksAction,
                SystemAction.get(ScheduleAction.class),
                null,
                SystemAction.get(ExpandAllUserTasksAction.class),
                SystemAction.get(CollapseAllAction.class),
                null,
                SystemAction.get(ImportAction.class),
                SystemAction.get(ExportAction.class),
            };
        } else {
            return new Action[] {
                utv.newTaskAction,
                //SystemAction.get(ShowScheduleViewAction.class),
                null,
                new StartTaskAction(utv),
                PauseAction.getInstance(),
                null,
                utv.showTaskAction,
                new GoToUserTaskAction(utv),
                null,
                SystemAction.get(CutAction.class),
                SystemAction.get(CopyAction.class),
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(DeleteAction.class),
                null,
                utv.moveUpAction,
                utv.moveDownAction,
                utv.moveLeftAction,
                utv.moveRightAction,
                null,
                SystemAction.get(FilterAction.class),
                SystemAction.get(RemoveFilterAction.class),
                null,
                utv.purgeTasksAction,
                utv.clearCompletedAction,
                SystemAction.get(ScheduleAction.class),
                null,
                SystemAction.get(ExpandAllUserTasksAction.class),
                SystemAction.get(CollapseAllAction.class),
                null,
                SystemAction.get(ImportAction.class),
                SystemAction.get(ExportAction.class),

                // Property: node specific, but by convention last in menu
                null,
                utv.propertiesAction
            };
        }
    }

    protected Sheet createSheet() {
        Sheet s = Sheet.createDefault();
        Set ss = s.get(Sheet.PROPERTIES);

        try {
            PropertySupport.Reflection p;
            p = new PropertySupport.Reflection(item, String.class, "getSummary", "setSummary"); // NOI18N
            p.setName(UserTask.PROP_SUMMARY);
            p.setDisplayName(NbBundle.getMessage(
                    UserTaskNode.class, "Description")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(
                    UserTaskNode.class, "DescriptionHint")); // NOI18N
            ss.put(p);            
            
            p = new PropertySupport.Reflection(item, Integer.TYPE, "getPriority", "setPriority"); // NOI18N
            p.setName(UserTask.PROP_PRIORITY);
            p.setPropertyEditorClass(PriorityPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_priorityProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_priorityProperty")); // NOI18N
            ss.put(p);
            
            
            p = new PropertySupport.Reflection(item, Boolean.TYPE, "isDone", "setDone"); // NOI18N
            p.setName("done"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_doneProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_doneProperty")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, Integer.TYPE, 
                "getPercentComplete", "setPercentComplete"); // NOI18N
            p.setName("percentComplete"); // NOI18N
            p.setPropertyEditorClass(PercentsPropertyEditor.class);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_percentCompleteProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_percentCompleteProperty")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, Boolean.TYPE, 
                "isValuesComputed", "setValuesComputed"); // NOI18N
            p.setName("valuesComputed"); // NOI18N
            p.setDisplayName(org.openide.util.NbBundle.getMessage(UserTaskNode.class, "LBL_valuesComputed")); // NOI18N
            p.setShortDescription(org.openide.util.NbBundle.getMessage(UserTaskNode.class, "HNT_valuesComputed")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, Integer.TYPE, "getEffort", "setEffort"); // NOI18N
            p.setName("effort"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_effortProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_effortProperty")); // NOI18N
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new PropertySupport.Reflection(item, Integer.TYPE, "getRemainingEffort", null); // NOI18N
            p.setName("remainingEffort"); // NOI18N
            p.setValue("canEditAsText", Boolean.FALSE); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_remainingEffortProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_remainingEffortProperty")); // NOI18N
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new PropertySupport.Reflection(item, Integer.TYPE, "getSpentTime", "setSpentTime"); // NOI18N
            p.setName("spentTime"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_spentTimeProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_spentTimeProperty")); // NOI18N
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new PropertySupport.Reflection(item, Integer.TYPE, "getSpentTimeToday", null); // NOI18N
            p.setName("spentTimeToday"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_spentTimeTodayProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_spentTimeTodayProperty")); // NOI18N
            p.setPropertyEditorClass(DurationPropertyEditor.class);
            ss.put(p);

            p = new PropertySupport.Reflection(item, String.class, "getDetails", "setDetails"); // NOI18N
            p.setName(UserTask.PROP_DETAILS);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_detailsProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_detailsProperty")); // NOI18N
            ss.put(p);
            
            p = new PropertySupport.Reflection(item, String.class, "getCategory", "setCategory"); // NOI18N
            p.setName(UserTask.PROP_CATEGORY);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_categoryProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_categoryProperty")); // NOI18N
            p.setValue("canEditAsText", Boolean.TRUE); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getCreatedDate", null) { // NOI18N
                public Object getValue () throws
                    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    return new Date(((UserTask) instance).getCreatedDate());
                }
            };
            p.setPropertyEditorClass(DateEditor.class);
            p.setName("created"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_createdProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_createdProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getLastEditedDate", null) { // NOI18N
                public Object getValue () throws
                    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    return new Date(((UserTask) instance).getLastEditedDate());
                }
            };
            p.setPropertyEditorClass(DateEditor.class);
            p.setName("edited"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_editedProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_editedProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getCompletedDate", null) { // NOI18N
                public Object getValue () throws
                    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    long d = ((UserTask) instance).getCompletedDate();
                    if (d <= 0)
                        return null;
                    else
                        return new Date(d);
                }
            };
            p.setPropertyEditorClass(DateEditor.class);
            p.setName("completedDate"); // NOI18N
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_completedDateProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_completedDateProperty")); // NOI18N
            p.setValue("suppressCustomEditor", Boolean.TRUE); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getDueDate", "setDueDate"); // NOI18N            
            p.setPropertyEditorClass(DateEditor.class);
            p.setName(UserTask.PROP_DUE_DATE);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_dueDateProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_dueDateProperty")); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, String.class, "getOwner", "setOwner"); // NOI18N            
            p.setName(UserTask.PROP_OWNER);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_ownerProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_ownerProperty")); // NOI18N
            ss.put(p);

            p = new PropertySupport.Reflection(item, Date.class, "getStartDate", "setStartDate"); // NOI18N
            p.setPropertyEditorClass(DateEditor.class);
            p.setName(UserTask.PROP_START);
            p.setDisplayName(NbBundle.getMessage(UserTaskNode.class, "LBL_startProperty")); // NOI18N
            p.setShortDescription(NbBundle.getMessage(UserTaskNode.class, "HNT_startProperty")); // NOI18N
            ss.put(p);

            PropertySupport.ReadOnly ro = new PropertySupport.
                    ReadOnly<String>(UserTask.PROP_RESOURCES, String.class, 
                    NbBundle.getMessage(UserTaskNode.class, "LBL_resourcesProperty"), // NOI18N
                    NbBundle.getMessage(UserTaskNode.class, "HNT_resourcesProperty")) { // NOI18N
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    ObjectList<UserTaskResource> rl = item.getResources();
                    StringBuilder sb = new StringBuilder();
                    for (UserTaskResource r: rl) {
                        if (sb.length() != 0)
                            sb.append("\n"); // NOI18N
                        sb.append(r.getDisplayName());
                    }
                    return sb.toString();
                }                
            };
            ss.put(ro);
        } catch (NoSuchMethodException nsme) {
            UTUtils.LOGGER.log(Level.WARNING, "", nsme); // NOI18N
        }
        return s;
    }

    public boolean canDestroy() {
        return true;
    }

    /** Can this node be copied?
    * @return <code>true</code>
    */
    public boolean canCopy () {
        return true;
    }

    /** Can this node be cut?
    * @return <code>false</code>
    */
    public boolean canCut () {
        return true;
    }    
    
    public javax.swing.Action getPreferredAction() {
        return null; // TODO SystemAction.get(ShowTaskAction.class);
    }
    
    public boolean canRename() {
        return true;
    }
    
    @SuppressWarnings("unchecked")
    protected void createPasteTypes(java.awt.datatransfer.Transferable t, 
            List s) {
        super.createPasteTypes(t, s);
        PasteType p = createTodoPasteType(this, t);
        if (p != null) {
            s.add(p); // generates a warning
        }
    }

    /** 
     * Create a paste type from a transferable.
     *
     * @param t the transferable to check
     * @param target parent for the pasted task
     * @return an appropriate paste type, or null if not appropriate
     */
    public static PasteType createTodoPasteType(
    Node target, Transferable t) {
        if (t.isDataFlavorSupported(ExTransferable.multiFlavor)) {
            try {
                // Multiselection
                final MultiTransferObject mto = (MultiTransferObject)
                    t.getTransferData(ExTransferable.multiFlavor);
                if (mto.areDataFlavorsSupported(
                    new DataFlavor[] {UserTasksTransferable.USER_TASKS_FLAVOR})) {
                    return new UserTaskNode.TodoPaste(target, t);
                }
            } catch (UnsupportedFlavorException e) {
                UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
            } catch (IOException e) {
                UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
            }
        } 
        
        if (t.isDataFlavorSupported(UserTasksTransferable.USER_TASKS_FLAVOR)) {
            return new TodoPaste(target, t);
        } 
        return null;
    }

    public org.openide.nodes.Node.Cookie getCookie(Class type) {
        UserTask uitem = (UserTask) item;
        if (type == UserTask.class) {
            return item;
        } else {
            return super.getCookie(type);
        }
    }
    
    public void setName(String nue) {
        super.setName(nue);
        if (!nue.equals(item.getSummary())) {
            item.setSummary(nue);
        }
    }
    
    public Transferable clipboardCopy() throws IOException {
        final UserTask copy = (UserTask) item.clone();
        return new ExTransferable.Single(UserTasksTransferable.USER_TASKS_FLAVOR) {
            protected Object getData() {
                return copy;
            }
        };
    }
    
    public Transferable clipboardCut() throws IOException {
        destroy();
        return clipboardCopy();
    }

    public void destroy() throws IOException {
        AdvancedTreeTableNode n = 
            (AdvancedTreeTableNode) this.node.findNextNodeAfterDelete();
        item.destroy();
        if (item.getParent() != null)
            item.getParent().getSubtasks().remove(item);
        else
            utl.getSubtasks().remove(item);
        super.destroy();
        if (n != null) {
            TreePath tp = new TreePath(n.getPathToRoot());
            tt.select(tp);
            tt.scrollTo(tp);
        }
    }
    
    /**
     * Performs "Paste" for the specified task on this node
     *
     * @param t task to be pasted
     */
    public void pasteTask(UserTask t) {
        t = (UserTask) t.clone();
        item.getSubtasks().add(t);
        int index = this.node.getIndexOfObject(t);
        if (index >= 0) {
            AdvancedTreeTableNode n = 
                (AdvancedTreeTableNode) this.node.getChildAt(index);
            TreePath tp = new TreePath(n.getPathToRoot());
            tt.expandAllPath(tp);
            tt.select(tp);
            tt.scrollTo(tp);
        }
    }
    
    /**
     * Paste type for a pasted task
     */
    private static final class TodoPaste extends PasteType {
        private final Transferable t;
        private final Node target;
        
        /**
         * Creates a paste type for a UserTask
         *
         * @param t a transferable object
         * @param target parent for the pasted task
         */
        public TodoPaste(Node target, Transferable t) {
            this.t = t;
            this.target = target;
        }
        
        public String getName() {
            return NbBundle.getMessage(UserTasksTransferable.class, 
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
                        new DataFlavor[] {UserTasksTransferable.USER_TASKS_FLAVOR})) {
                        for (int i = 0; i < mto.getCount(); i++) {
                            UserTask item = (UserTask)
                                mto.getTransferData(i, UserTasksTransferable.USER_TASKS_FLAVOR);
                            addTask(item);
                        }
                        return null;
                    }
                } 
                
                if (t.isDataFlavorSupported(UserTasksTransferable.USER_TASKS_FLAVOR)) {
                    UserTask item = 
                        (UserTask) t.getTransferData(UserTasksTransferable.USER_TASKS_FLAVOR);
                    addTask(item);
                } 
            } catch (UnsupportedFlavorException ufe) {
                // Should not happen.
                IOException ioe = (IOException) new IOException(
                        ufe.toString()).initCause(ufe);
                throw ioe;
            }
            return null;
        }
        
        /**
         * Adds a task
         *
         * @param item a task
         */
        private void addTask(UserTask item) {
            UserTask ut;
            if (item instanceof UserTask) {
                ut = (UserTask) item;
            } else {
                ut = new UserTask(item.getSummary(), item.getList());
                ut.setDetails(item.getDetails());
                ut.setPriority(item.getPriority());
            }
            ((UserTaskNode) target).pasteTask(ut);
        }
    }
}

