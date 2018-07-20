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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.tasklist.usertasks.actions;

import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.EditTaskPanel;
import org.netbeans.modules.tasklist.usertasks.actions.UTViewAction;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.util.AWTThread;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
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
        putValue(UTViewAction.ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
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
        if (last instanceof UTTreeTableNode) {
            parent = ((UTTreeTableNode) last).getUserTask();
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
        
        UserTask ut = new UserTask("", utl); // NOI18N

        final EditTaskPanel panel = getEditTaskPanel();
        panel.setTopLevel(parent == null);
        panel.fillPanel(ut);
        if (cursor != null)
            panel.addResource(cursor);
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
    @AWTThread
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
            parent = ((UTTreeTableNode) tp.getLastPathComponent()).
                    getUserTask();
        }
        
        UserTask ut = new UserTask(line.getText(), utl); // NOI18N

        EditTaskPanel panel = getEditTaskPanel();
        panel.fillPanel(ut);
        if (line != null)
            panel.addResource(line);
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
