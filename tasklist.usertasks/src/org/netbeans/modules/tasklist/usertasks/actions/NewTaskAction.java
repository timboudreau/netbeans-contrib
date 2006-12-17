/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;

import org.netbeans.modules.tasklist.usertasks.EditTaskPanel;
import org.netbeans.modules.tasklist.usertasks.actions.UTViewAction;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.util.AWTThreadAnnotation;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
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
import org.openide.util.Utilities;

/**
 * Action which brings up a dialog where you can create
 * a new subtask.
 *
 * @author Tor Norbye
 * @author Trond Norbye
 * @author tl
 */
public class NewTaskAction extends UTViewAction {
    private static final long serialVersionUID = 2;

    private static EditTaskPanel panel;
    
    /**
     * Constructor.
     *
     * @param utv a user task view.
     */
    public NewTaskAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(NewTaskAction.class, 
                "LBL_NewSubtask")); // NOI18N
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/usertasks/" + // NOI18N
                "actions/newTask.gif"))); // NOI18N
        setEnabled(true);
    }
    
    public void valueChanged(ListSelectionEvent e) {
    }
    
    /**
     * Creates a panel for editing a task
     *
     * @return created panel
     */
    private static EditTaskPanel getEditTaskPanel() {
        if (panel == null) {
            panel = new EditTaskPanel(false);
            panel.setPreferredSize(new Dimension(600,500));
        }
        return panel;
    }
    
    public void actionPerformed(ActionEvent e) {
        TreePath[] tp = utv.getTreeTable().getSelectedPaths();
        final UserTask parent;
        final UserTaskList utl;
        Object last = tp.length > 0 ? tp[0].getLastPathComponent() : null;
        if (last instanceof UserTaskTreeTableNode) {
            parent = ((UserTaskTreeTableNode) last).getUserTask();
            utl = parent.getList();
        } else {
            parent = null;
            utl = utv.getUserTaskList();
        }
        
        // Get the current filename and line number so we can initialize
        // the filename:linenumber columns
        
        // First try to get the editor window itself; if you right click
        // on a node in the User Tasks Window, that node becomes the activated
        // node (which is good - it makes the properties window show the
        // todo item's properties, etc.) but that means that we can't
        // find the editor position via the normal means.
        // So, we go hunting for the topmosteditor tab, and when we find it,
        // ask for its nodes.
        
        // find cursor position
        Line cursor = UTUtils.findCursorPosition(null); // TODO: null
        if (cursor == null) {
            Node[] editorNodes = UTUtils.getEditorNodes();
            if (editorNodes != null)
                cursor = UTUtils.findCursorPosition(editorNodes);
        }
        int lineNumber;
        URL url;
        if (cursor != null) {
            lineNumber = cursor.getLineNumber();
            url = UTUtils.getExternalURLForLine(cursor);
        } else {
            url = null;
            lineNumber = -1;
        }
        
        // After the add - view the todo list as well!
        final UserTaskView utv = UserTaskViewRegistry.getInstance().
                getLastActivated();

        UserTask ut = new UserTask("", utl); // NOI18N

        final EditTaskPanel panel = getEditTaskPanel();
        panel.setTopLevel(parent == null);
        panel.fillPanel(ut);
        panel.setAssociatedFilePos(false);
        panel.setUrl(url);
        panel.setLineNumber(lineNumber);
        panel.focusSummary();
        
        DialogDescriptor dd = new DialogDescriptor(getEditTaskPanel(),
             NbBundle.getMessage(NewTaskAction.class,
             "TITLE_add_todo")); // NOI18N
        dd.setModal(true);
        dd.setHelpCtx(new HelpCtx(
                "org.netbeans.modules.tasklist.usertasks.NewTaskDialog")); // NOI18N
        dd.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);

        // dialog buttons
        final JButton addAnotherButton = new JButton();
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
                    UTUtils.LOGGER.fine(""); // NOI18N
                    Object src = e.getSource();
                    if (src == addAnotherButton) {
                        UserTask ut = new UserTask("", utl); // NOI18N
                        panel.fillObject(ut);

                        // See if the user wants to append or prepend
                        boolean append = panel.getAppend();
                        if (parent != null && !panel.isTopLevel()) {
                            if (append)
                                parent.getSubtasks().add(ut);
                            else
                                parent.getSubtasks().add(0, ut);
                            if (Settings.getDefault().getAutoSwitchToComputed()) {
                                parent.setValuesComputed(true);
                            }
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
                }
            });
            
        Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        d.pack();
        d.setVisible(true);

        if (dd.getValue() == NotifyDescriptor.OK_OPTION) {
            panel.fillObject(ut);

            // See if the user wants to append or prepend
            boolean append = panel.getAppend();
            if (parent != null && !panel.isTopLevel()) {
            	if (append)
                    parent.getSubtasks().add(ut);
            	else
                    parent.getSubtasks().add(0, ut);
                if (Settings.getDefault().getAutoSwitchToComputed()) {
                    parent.setValuesComputed(true);
                }
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

    /**
     * Performs the action
     *
     * @param line the associated line
     */
    @AWTThreadAnnotation
    public static void performAction(Line line) {
        DataObject dob = DataEditorSupport.findDataObject(line);
        if (dob == null)
            return;
        
        FileObject fo = dob.getPrimaryFile();
        URL url = URLMapper.findURL(fo, URLMapper.EXTERNAL);
        if (url == null)
            return;

        // After the add - view the todo list as well!
        UserTaskView utv = UserTaskViewRegistry.getInstance().getLastActivated();
        if (utv == null) {
            utv = UserTaskViewRegistry.getInstance().getDefault();
            utv.showInMode();
        }    
        UserTaskList utl = utv.getUserTaskList();
        
        // find parent task
        UserTask parent = null;
        TreePath tp = utv.getTreeTable().getSelectedPath();
        if (tp != null) {
            parent = ((UserTaskTreeTableNode) tp.getLastPathComponent()).
                    getUserTask();
        }
        
        UserTask ut = new UserTask(line.getText(), utl); // NOI18N

        EditTaskPanel panel = getEditTaskPanel();
        panel.fillPanel(ut);
        panel.setAssociatedFilePos(true);
        panel.setUrl(url);
        panel.setLineNumber(line.getLineNumber());
        panel.focusSummary();
        
        DialogDescriptor dd = new DialogDescriptor(getEditTaskPanel(),
             NbBundle.getMessage(NewTaskAction.class,
             "TITLE_add_todo")); // NOI18N
        dd.setModal(true);
        dd.setHelpCtx(new HelpCtx(
                "org.netbeans.modules.tasklist.usertasks.NewTaskDialog")); // NOI18N
        dd.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);

        // dialog buttons
        dd.setOptions(new Object[] {
            DialogDescriptor.OK_OPTION,
            DialogDescriptor.CANCEL_OPTION,
        });
        dd.setClosingOptions(new Object[] {
            DialogDescriptor.OK_OPTION,
            DialogDescriptor.CANCEL_OPTION
        });
            
        Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        d.pack();
        d.setVisible(true);

        if (dd.getValue() == NotifyDescriptor.OK_OPTION) {
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
}
