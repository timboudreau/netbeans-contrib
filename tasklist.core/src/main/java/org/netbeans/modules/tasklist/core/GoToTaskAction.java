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
