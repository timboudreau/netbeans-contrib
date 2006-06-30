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
package org.netbeans.modules.tasklist.core;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

import javax.swing.*;
import java.awt.*;


/**
 * Go to the source code / associated file for a particular
 * task.
 *
 * @author Tor Norbye
 */
public class GoToTaskAction extends NodeAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }

    /** Do the actual jump to source
     * @param nodes Nodes, where the selected node should be a task
     * node. */    
    protected void performAction(Node[] nodes) {
        final TaskListView tlv = TaskListView.getCurrent();
        if (tlv != null) {
            final Task item = TaskNode.getTask(nodes[0]); // safe - see enable check
            assert item != null;
            SwingUtilities.invokeLater(new Runnable() {  //#39904 eliminate problems with focus
                public void run() {
                    tlv.showTaskInEditor(item, null);
                }
            });
        } else {
            //XXX System.out.println("No current view!");
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    /** Enable the task iff you've selected exactly one node,
     * and that node is a tasknode. */    
    protected boolean enable(Node[] nodes) {
        if ((nodes == null) || (nodes.length != 1)) {
            return false;
        }
        Task item = TaskNode.getTask(nodes[0]);
        if (item == null) {
            return false;
        }
        return (item.getLine() != null);
    }
    
    public String getName() {
        return NbBundle.getMessage(GoToTaskAction.class, "LBL_Goto"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/core/showSource.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (ShowTodoItemAction.class);
    }
}
