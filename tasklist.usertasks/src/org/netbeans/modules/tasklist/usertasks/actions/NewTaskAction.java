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

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.JButton;

import org.netbeans.modules.tasklist.usertasks.EditTaskPanel;
import org.netbeans.modules.tasklist.usertasks.UTUtils;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskListNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;
import org.openide.text.Line;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Action which brings up a dialog where you can create
 * a new subtask.
 *
 * @author Tor Norbye
 * @author Trond Norbye
 */
public class NewTaskAction extends NodeAction {

    private static final long serialVersionUID = 1;

    private DialogDescriptor dd;
    private EditTaskPanel panel;
    private Dialog dialog;
    private JButton addAnotherButton;
    
    private UserTaskView utv;
    private UserTask parent;
    private UserTaskList utl;
    private URL url;
    private int lineNumber;
    private boolean associate;
    
    protected boolean enable(Node[] node) {
        return node.length == 1 && 
            (node[0] instanceof UserTaskNode || 
            node[0] instanceof UserTaskListNode);
    }

    /**
     * Creates a panel for editing a task
     *
     * @return created panel
     */
    private EditTaskPanel getEditTaskPanel() {
        if (panel == null) {
            panel = new EditTaskPanel(false);
            panel.setPreferredSize(new Dimension(600,500));
        }
        return panel;
    }
    
    /**
     * Returns the "New Task" dialog
     *
     * @return the created dialog
     */
    private Dialog getDialog() {
        if (dialog == null) {
            dialog = DialogDisplayer.getDefault().createDialog(
                getDialogDescriptor());
            dialog.pack();
        }
        return dialog;
    }
    
    /**
     * Creates a dialog descriptor for the "New Subtask" dialog
     *
     * @return created dialog descriptor
     */
    private DialogDescriptor getDialogDescriptor() {
        if (dd == null) {
            dd = new DialogDescriptor(getEditTaskPanel(),
                 NbBundle.getMessage(NewTaskAction.class,
                                     "TITLE_add_todo")); // NOI18N
            dd.setModal(true);
            dd.setHelpCtx(new HelpCtx("org.netbeans.modules.tasklist.usertasks.NewTaskDialog")); // NOI18N
            dd.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);

            // dialog buttons
            addAnotherButton = new JButton();
            Mnemonics.setLocalizedText(addAnotherButton, NbBundle.getMessage(
                NewTaskAction.class, "BTN_AddAnother")); // NOI18N
            dd.setOptions(new Object[] {
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.CANCEL_OPTION,
                addAnotherButton
            });
            dd.setClosingOptions(new Object[] {
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.CANCEL_OPTION
            });
            dd.setButtonListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object src = e.getSource();
                    if (src == addAnotherButton) {
                        addAnotherTask();
                    }
                }
            });
        }
        return dd;
    }
    
    /**
     * Will be called if the user pressed "Add Another" button
     */
    private void addAnotherTask() {
        UserTask ut = new UserTask("", utl); // NOI18N
        panel.fillObject(ut);

        // See if the user wants to append or prepend
        boolean append = panel.getAppend();
        if (parent != null) {
            if (append)
                parent.getSubtasks().add(ut);
            else
                parent.getSubtasks().add(0, ut);
        } else {
            if (append)
                utl.getSubtasks().add(ut);
            else
                utl.getSubtasks().add(0, ut);
        }

        // After the add - view the todo list as well!
        utv.showInMode();
        utv.select(ut);
        utv.scrollTo(ut);

        ut = new UserTask("", utl); // NOI18N
        panel.fillPanel(ut);
        panel.focusSummary();
    }
    
    protected void performAction(Node[] nodes) {
        // Get the current filename and line number so we can initialize
        // the filename:linenumber columns
        
        // First try to get the editor window itself; if you right click
        // on a node in the Todo Window, that node becomes the activated
        // node (which is good - it makes the properties window show the
        // todo item's properties, etc.) but that means that we can't
        // find the editor position via the normal means.
        // So, we go hunting for the topmosteditor tab, and when we find it,
        // ask for its nodes.
        
        // find cursor position
        Line cursor = UTUtils.findCursorPosition(nodes);
        if (cursor == null) {
            Node[] editorNodes = UTUtils.getEditorNodes();
            if (editorNodes != null)
                cursor = UTUtils.findCursorPosition(editorNodes);
        }
        if (cursor != null) {
            this.lineNumber = cursor.getLineNumber();
            this.url = UTUtils.getExternalURLForLine(cursor);
        } else {
            this.url = null;
            this.lineNumber = -1;
        }
        
        // find parent task
        if (nodes[0] instanceof UserTaskNode) {
            parent = ((UserTaskNode) nodes[0]).getTask();
            utl = parent.getList();
        } else {
            parent = null;
            utl = ((UserTaskListNode) nodes[0]).getUserTaskList();
        }
        
        associate = false;
        
        // After the add - view the todo list as well!
        utv = UserTaskViewRegistry.getInstance().getCurrent();

        performTheAction();
    }

    /**
     * Performs the action
     *
     * @param line the associated line
     */
    public static void performAction(Line line) {
        NewTaskAction nta = (NewTaskAction) NewTaskAction.get(NewTaskAction.class);

        DataObject dob = DataEditorSupport.findDataObject(line);
        if (dob == null)
            return;
        
        FileObject fo = dob.getPrimaryFile();
        URL url = URLMapper.findURL(fo, URLMapper.EXTERNAL);
        if (url == null)
            return;

        nta.associate = true;
        nta.url = url;
        nta.lineNumber = line.getLineNumber();
        
        // After the add - view the todo list as well!
        nta.utv = UserTaskViewRegistry.getInstance().getLastActivated();
        if (nta.utv == null) {
            nta.utv = UserTaskViewRegistry.getInstance().getDefault();
            nta.utv.showInMode();
        }    
        nta.utl = nta.utv.getUserTaskList();
        
        // find parent task
        Node[] nodes = nta.utv.getExplorerManager().getSelectedNodes();
        if (nodes.length > 0 && nodes[0] instanceof UserTaskNode) {
            nta.parent = ((UserTaskNode) nodes[0]).getTask();
        } else {
            nta.parent = null;
        }
        
        nta.performTheAction();
    }
    
    /**
     * Performs the action
     */
    private void performTheAction() {
        UserTask ut = new UserTask("", utl); // NOI18N

        EditTaskPanel panel = getEditTaskPanel();
        panel.fillPanel(ut);
        panel.setAssociatedFilePos(associate);
        panel.setUrl(url);
        panel.setLineNumber(lineNumber);
        panel.focusSummary();
        
        getDialog().show();

        if (getDialogDescriptor().getValue() == NotifyDescriptor.OK_OPTION) {
            panel.fillObject(ut);

            // See if the user wants to append or prepend
            boolean append = panel.getAppend();
            if (parent != null) {
            	if (append)
                    parent.getSubtasks().add(ut);
            	else
                    parent.getSubtasks().add(0, ut);
            } else {
            	if (append)
                    utl.getSubtasks().add(ut);
            	else
                    utl.getSubtasks().add(0, ut);
            }

            assert utv != null;
            
            utv.showInMode();
            utv.select(ut);
            utv.scrollTo(ut);
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(NewTaskAction.class, 
            "LBL_NewSubtask"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/usertasks/actions/newTask.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
    protected boolean asynchronous() {
        return false;
    }    
}
