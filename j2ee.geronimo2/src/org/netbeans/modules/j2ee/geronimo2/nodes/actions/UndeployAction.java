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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.j2ee.geronimo2.nodes.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.j2ee.geronimo2.nodes.GeWebModule;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.actions.NodeAction;

/**
 * Action repsonsible for undeploying of a deployed module
 * @author Max Sauer
 */
public class UndeployAction extends NodeAction {

    @Override
    protected void performAction(Node[] nodes) {
        NodeRefreshTask refresh = new NodeRefreshTask(RequestProcessor.getDefault());
        for (int i=0; i<nodes.length; i++) {
            GeWebModuleCookie cookie = (GeWebModuleCookie) nodes[i].getCookie(GeWebModuleCookie.class);
            if (cookie != null) {
                Task task = cookie.undeploy();

                refresh.addPrerequisity(nodes[i].getParentNode(), task);
            }
        }

        RequestProcessor.getDefault().post(refresh);
    }

    @Override
    protected boolean enable(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            GeWebModule module = (GeWebModule) nodes[i].getLookup().lookup(GeWebModule.class);
            if (module != null) {
                //TODO: Detect non-undeploy modules
                // it should not be allowed to undeploy the /manager application
//                if ("/manager".equals(module.getGeModule().getPath())) { // NOI18N
//                    return false;
//                }
            }
        }
        return true;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected boolean asynchronous() {
        return false;
    }

    
    
    @Override
    public String getName() {
        return NbBundle.getMessage(UndeployAction.class, "LBL_UndeployAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    
    /**
     * Helper class supporting the node(s) refresh after the set of prerequisity
     * tasks is finished.
     * <p>
     * Class itself is <i>thread safe</i> (uses intrinsic lock). Refresh
     * itself is performed from dedicated thread so, the refresh must be
     * implemented in thread safe way.
     *
     * @author Petr Hejl
     */
    private static class NodeRefreshTask implements Runnable {

        private final RequestProcessor requestProcessor;

        private Map<Node, Set<Task>> taskMap = new HashMap<Node, Set<Task>>();

        /**
         * Constructs the NodeRefreshTask using the given RequestProcessor.
         *
         * @param requestProcessor will be used for scheduling the refresh tasks
         */
        public NodeRefreshTask(RequestProcessor requestProcessor) {
            Parameters.notNull("requestProcessor", taskMap);

            this.requestProcessor = requestProcessor;
        }

        /**
         * Adds prerequisity task. Defines that the node should be refreshed
         * after the task (and all already added tasks) is finished.
         *
         * @param node node to refresh when the task is finished
         * @param task task to wait for (multiple task can be assigned) by calling this method
         */
        public synchronized void addPrerequisity(Node node, Task task) {
            Parameters.notNull("node", node);
            Parameters.notNull("task", task);

            Set<Task> tasks = taskMap.get(node);
            if (tasks == null) {
                tasks = new HashSet<Task>();
                taskMap.put(node, tasks);
            }

            tasks.add(task);
        }

        /**
         * Executes this task. For each node added with {@link #addPrerequisity(Node, Task)}
         * it post a new task that waits until all tasks asscociated with the node
         * are finished and after that refreshes the node.
         */
        public synchronized void run() {
            for (Map.Entry<Node, Set<Task>> entry : taskMap.entrySet()) {

                final Node node = entry.getKey();
                final Set<Task> tasks = entry.getValue();

                requestProcessor.post(new Runnable() {
                    public void run() {
                        for (Task task : tasks) {
                            task.waitFinished();
                        }
                        NodeRefreshTask.this.refresh(node);
                    }
                });
            }
        }

        private void refresh(Node node) {
            if (node == null) {
                return;
            }

            RefreshWebModulesCookie cookie = node.getLookup().lookup(RefreshWebModulesCookie.class);
            if (cookie != null) {
                cookie.refresh();
            }
        }

    }
}
