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

package org.netbeans.modules.tasklist.usertasks.transfer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
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
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.renderers.UserTaskIconProvider;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

/**
 * TransferHandler for the table.
 *
 * @author tl
 */
public class MyTransferHandler extends TransferHandler {
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
                tasks = (UserTask[]) t.getTransferData(
                        UserTasksTransferable.USER_TASKS_FLAVOR);
                for (int i = 0; i < tasks.length; i++) {
                    tasks[i] = tasks[i].cloneTask();
                }
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
    
    /** 
     * Creates a new instance of MyTransferHandler 
     */
    public MyTransferHandler() {
    }

    public int getSourceActions(JComponent c) {
	return MOVE | COPY;
    }

    protected Transferable createTransferable(JComponent c) {
        UserTasksTreeTable tt = (UserTasksTreeTable) c;
        TreePath[] paths = tt.getSelectedPaths();
        List<UserTask> tasks = new ArrayList<UserTask>();
        for (int i = 0; i < paths.length; i++) {
            Object last = paths[i].getLastPathComponent();
            if (last instanceof UserTaskTreeTableNode) {
                tasks.add(((UserTaskTreeTableNode) last).getUserTask());
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
        return new ImageIcon(UserTaskIconProvider.getUserTaskImage(
                new UserTask("", new UserTaskList()), false));
    }

    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (int i = 0; i < transferFlavors.length; i++) {
            if (transferFlavors[i].equals(
                    UserTasksTransferable.USER_TASKS_FLAVOR) ||
                    transferFlavors[i].equals(DataFlavor.stringFlavor)) {
                return true;
            }
        }
        return false;
    }

    public boolean importData(JComponent comp, Transferable t) {
        // dragged tasks
        UserTask[] tasks = getTasks(t);
        if (tasks == null)
            return false;
        
        // target
        UserTasksTreeTable tt = (UserTasksTreeTable) comp;
        TreePath tp = tt.getSelectedPath();
        if (tp == null)
            return false;
        
        Object obj = tp.getLastPathComponent();
        UserTaskObjectList list = null;
        UserTask target = null;
        if (obj instanceof UserTaskTreeTableNode) {
            target = ((UserTaskTreeTableNode) obj).getUserTask();
            list = target.getSubtasks();
            if (transferredTasks != null) {
                for (int i = 0; i < transferredTasks.length; i++) {
                    if (transferredTasks[i].isAncestorOf(target)) {
                        return false;
                    }
                }
            }
        } else if (obj instanceof UserTaskListTreeTableNode) {
            UserTaskList utl = ((UserTaskListTreeTableNode) obj).
                    getUserTaskList();
            list = utl.getSubtasks();
        }
        
        List<UserTask> tasks_ = new ArrayList<UserTask>(Arrays.asList(tasks));
        Iterator<UserTask> it = tasks_.iterator();
        while (it.hasNext()) {
            UserTask ut = it.next();
            if (list.identityIndexOf(ut) >= 0) {
                it.remove();
            }
        }
        
        if (tasks_.size() == 0)
            return false;

        list.addAll(tasks_);

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
