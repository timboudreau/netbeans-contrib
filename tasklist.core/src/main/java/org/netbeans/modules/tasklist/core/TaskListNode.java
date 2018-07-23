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

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

import javax.swing.*;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Node visualization of TaskList. It creates children
 * by taking default node from contained tasks.
 *
 * @author Petr Kuzel
 */
public class TaskListNode extends AbstractNode {

    /**
     * Creates plain tasklist node. Properties that cannot
     * be derrived from passed tasklist should be provided by
     * client. It covers displayName etc.
     *
     * @param tasklist to be visualized never <code>null</code>
     */
    public TaskListNode(ObservableList tasklist) {
        super(new TaskListChildren(tasklist));
    }

    /**
     * Creates plain tasklist node. Properties that cannot
     * be derrived from passed tasklist should be provided by
     * client. It covers displayName etc.
     *
     * @param tasklist to be visualized never <code>null</code>
     */
    public TaskListNode(ObservableList tasklist, NodeFactory nodeFactory) {
      super(new TaskListChildren(tasklist));
      TaskListChildren list = (TaskListChildren) getChildren();
      list.setNodeFactory(nodeFactory);
    }

    public Action[] getActions(boolean context) {
        return new Action[0];
    }


    /** Creates custom child nodes for TaskListNode */
    public static interface NodeFactory {

        /** Default task.createNode() */
        Node createNode(Object task);
    }

    public void destroy() throws java.io.IOException {
        // explicitly destroy all children, it's not done automatically
        Enumeration en = getChildren().nodes();
        while (en.hasMoreElements()) {
            Node next = (Node) en.nextElement();
            next.destroy();
        }
        super.destroy();
    }    
    
    static class TaskListChildren extends Children.Keys implements TaskListener, Runnable {

        private ObservableList list;
        private NodeFactory nodeFactory;
        private static int BATCH_INTERVAL_MS = 59;
        private volatile RequestProcessor.Task batchSetKeys;
        private volatile boolean active = false;

        TaskListChildren(ObservableList list) {
            assert list != null;
            this.list = list;
        }

        protected void addNotify() {
            super.addNotify();
            setKeys(list.getTasks());
            list.addTaskListener(this);
            active = true;
        }

        protected void removeNotify() {
            active = false;
            list.removeTaskListener(this);
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }

        protected Node[] createNodes(Object key) {
            Task task = (Task) key;
            Node[] nodes;
            if (nodeFactory == null) {
                nodes = task.createNode();
            } else {
                nodes = new Node[] {nodeFactory.createNode(task)};
            }
            return nodes;
        }

        public void setNodeFactory(NodeFactory nodeFactory) {
            this.nodeFactory = nodeFactory;
        }

        // do not update keys too often it's rather heavyweight operation
        // batch all request that come in BATCH_INTERVAL_MS into one real update
        private void batchSetKeys() {
            if (batchSetKeys == null) {
                batchSetKeys = RequestProcessor.getDefault().post(this, BATCH_INTERVAL_MS);
            }
        }

        // TaskListener implementation ~~~~~~~~~~~~~~~

        public void selectedTask(Task t) {
        }

        public void warpedTask(Task t) {
        }

        public void addedTask(Task t) {
            batchSetKeys();
        }

        public void removedTask(Task pt, Task t, int index) {
            batchSetKeys();
        }

        public void structureChanged(Task t) {
            batchSetKeys();
        }

        // called from random request processor thread
        public void run() {
            batchSetKeys = null;
            if (active) {
                setKeys(list.getTasks());
            }
        }


    }
}
