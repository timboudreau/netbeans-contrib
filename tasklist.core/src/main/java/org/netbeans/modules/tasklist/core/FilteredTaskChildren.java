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

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.tasklist.filter.Filter;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeMemberEvent;

/**
 * Children for a FilteredTaskNode
 *
 * @author Tor Norbye
 */
final class FilteredTaskChildren extends FilterNode.Children {
    private static final Logger LOGGER = TLUtils.getLogger(
        FilteredTaskChildren.class);
    
    static {
        LOGGER.setLevel(Level.OFF);
    }
    
    private Filter filter;
    private TaskListView view;

    public FilteredTaskChildren(TaskListView view, Node n, Filter filter) {
        super(n);
        this.filter = filter;
        this.view = view;
    }

    @Override
    protected Node[] createNodes(Node key) {
        if (!(key instanceof Node))
            return new Node[0];
        
        Node n = (Node) key;
        Task task = TaskNode.getTask(n);
        if (filter.accept(task)) {
            org.openide.nodes.Children children;
            if (n.getChildren() == org.openide.nodes.Children.LEAF) {
                children = org.openide.nodes.Children.LEAF;
            } else {
                children = new FilteredTaskChildren(view, n, filter);
            }
            return new Node[] { new FilterTaskNode(n, children, false) };
        } 
        
        /* TODO if (filter.isFlattened()) {
            // Add all matching subtasks
            ArrayList matches = new ArrayList();
            findMatches(matches, task);

            Node[] nodes = new Node[matches.size()];
            ListIterator it = matches.listIterator();
            int index = 0;
            while (it.hasNext()) {
                Task t = (Task)it.next();
                org.openide.nodes.Children children;
                Node tn = t.createNode()[0];
                if (!t.hasSubtasks()) {
                    children = org.openide.nodes.Children.LEAF;
                } else {
                    children = new FilteredTaskChildren(view, tn, filter);
                }
                nodes[index++] = new FilterTaskNode(n, children,
                    false);
            }
            return nodes;
        } */
        
        // Perhaps the node has subtasks which accept. If so,
        // we check those as well...
        if (hasMatch(task)) {
            // Yes - add task, but mark it with a
            // special nonmatching icon.
            org.openide.nodes.Children children;
            if (n.getChildren() == org.openide.nodes.Children.LEAF) {
                children = org.openide.nodes.Children.LEAF;
            } else {
                children = new FilteredTaskChildren(view, n, filter);
            }
            return new Node[] {
                new FilterTaskNode(n, children, true) };
        }
        
        return new Node[0];
    }

    /** 
     * Return true iff the task n or one of its children matches
     * the filter 
     */
    private boolean hasMatch(Task n) {
        if (filter.accept(n))
            return true;

        Iterator it = n.subtasksIterator();
        while (it.hasNext()) {
            if (hasMatch((Task) it.next()))
                return true;
        }
        return false;
    }

    public void filterChildrenAdded(NodeMemberEvent ev) {
        super.filterChildrenAdded(ev);
        if (view != null) {
            view.updateFilterCount();
        }
    }

    public void filterChildrenRemoved(NodeMemberEvent ev) {
        super.filterChildrenRemoved(ev);
        if (view != null) {
            view.updateFilterCount();
        }
    }
}
