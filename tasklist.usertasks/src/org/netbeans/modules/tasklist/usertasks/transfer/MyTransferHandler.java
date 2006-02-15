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

package org.netbeans.modules.tasklist.usertasks.transfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import org.openide.ErrorManager;

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
                UTUtils.LOGGER.fine("User Tasks Flavor is supported"); // NOI18N
                tasks = (UserTask[]) t.getTransferData(
                        UserTasksTransferable.USER_TASKS_FLAVOR);
            } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String) t.getTransferData(
                        DataFlavor.stringFlavor);
                UTUtils.LOGGER.fine("stringFlavor is supported " + text); // NOI18N
                tasks = UserTask.parse(new StringReader(text));
                UTUtils.LOGGER.fine(tasks.length + " tasks parsed"); // NOI18N
            } else {
                UTUtils.LOGGER.fine("stringFlavor is not supported"); // NOI18N
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } catch (UnsupportedFlavorException e) {
            ErrorManager.getDefault().notify(e);
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
        List tasks = new ArrayList();
        for (int i = 0; i < paths.length; i++) {
            Object last = paths[i].getLastPathComponent();
            if (last instanceof UserTaskTreeTableNode) {
                tasks.add(((UserTaskTreeTableNode) last).getUserTask());
            }
        }
        UserTask[] t = (UserTask[]) tasks.toArray(new UserTask[tasks.size()]);
        t = UserTask.reduce(t);
        if (t.length == 0)
            return null;
        
        transferredTasks = t;
        
        UserTask[] cloned = new UserTask[t.length];
        UTUtils.LOGGER.fine("transferredTasks.length " + transferredTasks.length);
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
            for (int i = 0; i < tasks.length; i++) {
                if (tasks[i].isAncestorOf(target)) {
                    return false;
                }
            }
        } else if (obj instanceof UserTaskListTreeTableNode) {
            UserTaskList utl = ((UserTaskListTreeTableNode) obj).
                    getUserTaskList();
            list = utl.getSubtasks();
        }
        
        UTUtils.LOGGER.fine("found list: " + list); // NOI18N
        List tasks_ = new ArrayList(Arrays.asList(tasks));
        Iterator it = tasks_.iterator();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
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
            UTUtils.LOGGER.fine("transferredTasks.length " + 
                    transferredTasks.length);
            for (int i = 0; i < transferredTasks.length; i++) {
                UserTask ut = transferredTasks[i];
                if (ut.getParent() != null)
                    ut.getParent().getSubtasks().remove(ut);
                else
                    ut.getList().getSubtasks().remove(ut);
                ut.destroy();
            }
        }
        transferredTasks = null;
    }
}
