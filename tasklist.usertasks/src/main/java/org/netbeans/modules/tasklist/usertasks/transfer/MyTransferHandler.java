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

package org.netbeans.modules.tasklist.usertasks.transfer;

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.*;
import org.netbeans.modules.tasklist.usertasks.model.URLResource;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.renderers.UserTaskIconProvider;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableModel;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.TreeTableModel;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

/**
 * TransferHandler for the table.
 *
 * @author tl
 */
public class MyTransferHandler extends TransferHandler {
    private static DataFlavor X_MOZ_URL;
    
    static {
        try {
            X_MOZ_URL = new DataFlavor(
                    "text/x-moz-url; class=\"[B\""); // NOI18N
        } catch (ClassNotFoundException e) {
            UTUtils.LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }
    
    /**
     * Extracts tasks from a Transferable.
     *
     * @return extracted tasks or null
     */
    private UserTask[] getTasks(Transferable t) {
        UserTask[] tasks = null;
        try {
            if (t.isDataFlavorSupported(UserTasksTransferable.
                    USER_TASKS_FLAVOR)) {
                ArrayList list = (ArrayList) t.getTransferData(
                        UserTasksTransferable.USER_TASKS_FLAVOR);
                tasks = (UserTask[]) list.toArray(new UserTask[list.size()]);
                for (int i = 0; i < tasks.length; i++) {
                    tasks[i] = tasks[i].cloneTask();
                }
            } else if (t.isDataFlavorSupported(X_MOZ_URL)) {
                byte[] d = (byte[]) t.getTransferData(X_MOZ_URL);
                String s = new String(d, "UTF-16LE"); // NOI18N
                /* DEBUG
                for (int i = 0; i < s.length(); i++) {
                    UTUtils.LOGGER.fine(Integer.toString(s.charAt(i)));
                }*/
                int index = s.indexOf("\n");
                String url, title;
                if (index < 0)
                    index = s.indexOf(" ");
                if (index < 0)
                    index = s.indexOf("\u0000");
                if (index >= 0) {
                    url = s.substring(0, index);
                    title = s.substring(index + 1).trim();
                } else {
                    url = s;
                    title = null;
                }
                
                // DEBUG UTUtils.LOGGER.fine("'" + url + "'"); // NOI18N
                UserTask ut = new UserTask(title == null ? url : title, null);
                ut.getResources().add(new URLResource(new URL(url)));
                tasks = new UserTask[] {ut};
            } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String) t.getTransferData(
                        DataFlavor.stringFlavor);
                tasks = UserTask.parse(new StringReader(text));
            }
        } catch (IOException e) {
            UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
        } catch (UnsupportedFlavorException e) {
            UTUtils.LOGGER.log(Level.WARNING, "", e); // NOI18N
        }
        return tasks;
    }
    
    private UserTask[] transferredTasks;
    
    public int getSourceActions(JComponent c) {
	return COPY_OR_MOVE;
    }

    protected Transferable createTransferable(JComponent c) {
        UserTasksTreeTable tt = (UserTasksTreeTable) c;
        TreePath[] paths = tt.getSelectedPaths();
        List<UserTask> tasks = new ArrayList<UserTask>();
        for (int i = 0; i < paths.length; i++) {
            Object last = paths[i].getLastPathComponent();
            if (last instanceof UTBasicTreeTableNode) {
                tasks.add(((UTBasicTreeTableNode) last).getUserTask());
            }
        }
        UserTask[] t = tasks.toArray(new UserTask[tasks.size()]);
        t = UserTask.reduce(t);
        if (t.length == 0)
            return null;
        
        transferredTasks = t;
        
        UserTask[] cloned = new UserTask[t.length];
        for (int i = 0; i < t.length; i++) {
            cloned[i] = t[i].cloneTask();
        }
        return new UserTasksTransferable(cloned);
    }

    public Icon getVisualRepresentation(Transferable t) {
        return UserTaskIconProvider.getUserTaskImage(
                new UserTask("", new UserTaskList()), false);
    }

    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (int i = 0; i < transferFlavors.length; i++) {
            if (transferFlavors[i].equals(
                    UserTasksTransferable.USER_TASKS_FLAVOR) ||
                    transferFlavors[i].equals(DataFlavor.stringFlavor) ||
                    transferFlavors[i].equals(X_MOZ_URL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Imports data.
     * 
     * @param comp TreeTable
     * @param t dragged data
     * @param topLevel true = the tasks will be pasted at top level. Otherwise
     * current selected node will be the parent. 
     */
    public boolean importData(JComponent comp, Transferable t, 
            boolean topLevel) {
        if (UTUtils.LOGGER.isLoggable(Level.FINE)) {
            DataFlavor[] dfs = t.getTransferDataFlavors();
            for (DataFlavor df: dfs) {
                UTUtils.LOGGER.fine(df.getMimeType());
            }
        }
        
        // dragged tasks
        UserTask[] tasks = getTasks(t);
        if (tasks == null)
            return false;
        
        // target
        UserTasksTreeTable tt = (UserTasksTreeTable) comp;
        UserTask target = null;
        UserTaskObjectList list = null;

        TreePath tp = tt.getSelectedPath();
        if (!topLevel && tp != null && tp.getLastPathComponent() 
                instanceof UTBasicTreeTableNode) {
            UTBasicTreeTableNode obj = 
                    (UTBasicTreeTableNode) tp.getLastPathComponent();
            target = obj.getUserTask();
            list = target.getSubtasks();
        } else {
            TreeTableModel m = tt.getTreeTableModel();
            list = ((UTBasicTreeTableModel) m).
                    getUserTaskList().getSubtasks();
        }
        
        if (transferredTasks != null && target != null) {
            for (int i = 0; i < transferredTasks.length; i++) {
                if (transferredTasks[i].isAncestorOf(target)) {
                    return false;
                }
            }
        }
        
        List<UserTask> tasks_ = new ArrayList<UserTask>(Arrays.asList(tasks));
        Iterator<UserTask> it = tasks_.iterator();
        while (it.hasNext()) {
            UserTask ut = it.next();
            if (UTUtils.identityIndexOf(list, ut) >= 0) {
                it.remove();
            }
        }
        
        if (tasks_.size() == 0)
            return false;

        if (Settings.getDefault().getAppend())
            list.addAll(tasks_);
        else
            list.addAll(0, tasks_);
        if (target != null && Settings.getDefault().getAutoSwitchToComputed()) {
            target.setValuesComputed(true);
        }

        UserTasksTreeTable uttt = (UserTasksTreeTable) comp;
        if (target != null) {
            uttt.expandPath(uttt.findPath(target));
        }
        TreePath[] paths = new TreePath[tasks_.size()];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = uttt.findPath((UserTask) tasks_.get(i));
        }
        uttt.select(paths);
        uttt.scrollTo(paths[0]);
        
        return true;
    }
    
    public boolean importData(JComponent comp, Transferable t) {
        return importData(comp, t, false);
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE && transferredTasks != null) {
            UserTasksTreeTable tt = (UserTasksTreeTable) source;
            
            // TODO: any delete in the tree leads to
            // clearing the selection in the table. This is a workaround:
            TreePath[] sel = tt.getSelectedPaths();
            
            for (int i = 0; i < transferredTasks.length; i++) {
                UserTask ut = transferredTasks[i];
                if (ut.getParent() != null)
                    ut.getParent().getSubtasks().remove(ut);
                else
                    ut.getList().getSubtasks().remove(ut);
                ut.destroy();
            }
            
            tt.select(sel);
        }
        transferredTasks = null;
    }

    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        boolean exportSuccess = false;
        Transferable t = null;

	int clipboardAction = getSourceActions(comp) & action;
	if (clipboardAction != NONE) {
            t = createTransferable(comp);
            if (t != null) {
                clip.setContents(t, null);
                exportSuccess = true;
            }
        }

        if (exportSuccess && action == MOVE && transferredTasks != null) {
            UserTasksTreeTable tt = (UserTasksTreeTable) comp;
            tt.clearSelection();
            TreePath next = null;
            for (int i = 0; i < transferredTasks.length; i++) {
                UserTask ut = transferredTasks[i];
                if (i == transferredTasks.length - 1) {
                    TreePath sel = tt.findPath(ut);
                    AdvancedTreeTableNode ttn = (AdvancedTreeTableNode)
                            ((AdvancedTreeTableNode) sel.getLastPathComponent()).
                            findNextNodeAfterDelete();
                    next = new TreePath(ttn.getPathToRoot());
                }
                if (ut.getParent() != null)
                    ut.getParent().getSubtasks().remove(ut);
                else
                    ut.getList().getSubtasks().remove(ut);
                ut.destroy();
            }
            tt.select(next);
        }
        
        transferredTasks = null;
    }
}
