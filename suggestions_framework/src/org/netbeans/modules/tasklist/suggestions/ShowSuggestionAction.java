/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.tasklist.suggestions;

import org.netbeans.modules.tasklist.core.*;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.text.Line;

/** Go to the source code / associated file for a particular
 * suggestion
 * <p>
 * @author Tor Norbye */
public class ShowSuggestionAction extends NodeAction {
    
    /** Do the actual jump to source
     * @param nodes Nodes, where the selected node should be a task
     * node. */    
    protected void performAction(Node[] nodes) {
        Task item =
            (Task)TaskNode.getTask(nodes[0]); // safe - see enable check
        TaskList list = item.getList();
        if (list == null) {
            return; // internal error
        }
        TaskListView v = list.getView();
        if ((v == null) || !(v instanceof SuggestionsView)) {
            return; // internal error
        }
        SuggestionsView tlv = (SuggestionsView)v;
        tlv.showTask(item, new SuggestionAnno(item));
    }

    /** Enable the task iff you've selected exactly one node,
     * and that node is a tasknode. */    
    protected boolean enable(Node[] nodes) {
        if ((nodes == null) || (nodes.length == 0)) {
            return false;
        }
        Task item = TaskNode.getTask(nodes[0]);
        if (item == null) {
            return false;
        }
        return (item.getLine() != null);
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowSuggestionAction.class, "LBL_Goto"); // NOI18N
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
