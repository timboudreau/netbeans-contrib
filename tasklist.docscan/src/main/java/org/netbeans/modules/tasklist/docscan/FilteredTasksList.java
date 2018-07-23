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

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.core.ObservableList;
import org.netbeans.modules.tasklist.core.TaskListener;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskList;

import java.util.*;

/**
 * Delegate actions to original task list while
 * narrowing them to source tasks.
 *
 * @author Petr Kuzel
 */
final class FilteredTasksList implements ObservableList {

    /*
     * There is one realy strict implementation assumption
     * that source tasks never nest. It simplifies
     * implementation a lot because it degrades to list.
     *
     * It also assumes that visible view must have attached
     * TaskListener
     */

    private final TaskList peer;
    private List listeners = new ArrayList(1);
    private List tasks = new LinkedList();
    private EventHandler handler;
    private boolean silent = false;

    public FilteredTasksList(TaskList peer) {
        this.peer = peer;
    }

    public List getTasks() {
        return tasks;
    }

    public synchronized void addTaskListener(TaskListener l) {
        // we do not add directly to peer
        // because we filter fired events
        assert l != null;
        assert listeners.contains(l) == false; // missing removeListener ?
        ArrayList clisteners = new ArrayList(listeners);
        clisteners.add(l);
        if (clisteners.size() == 1) {
            handler = new EventHandler();
            peer.addTaskListener(handler);
        }
        listeners = clisteners;
    }

    public synchronized void removeTaskListener(TaskListener l) {
        ArrayList clisteners = new ArrayList(listeners);
        clisteners.remove(l);
        if (clisteners.size() == 0) {
            peer.removeTaskListener(handler);  // nobody is interested in changes
            handler = null;
        }
        listeners = clisteners;
    }

    /**
     * Notify that it's not needed anymore.
     */
    synchronized void byebye() {
        if (listeners.size() > 0) {
//            System.err.println("Leaked listeners: " + listeners);
        }
        listeners.clear();
        peer.removeTaskListener(handler);  // nobody is interested in changes
        handler = null;
    }

    private void fireStructureChanged(Task task) {
//        if (silent) return;  // the event comes from root.updatedStructure

        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            TaskListener listener = (TaskListener) it.next();
            listener.structureChanged(task);
        }
    }

    /** Client must fire structure changed event */
    private void refreshSnapshot() {
        tasks.clear();
        loadSourceTasks(peer.getTasks());
    }

    private void loadSourceTasks(List tasks) {
        if (tasks.size() == 0) return;
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            if (task.getSeed() instanceof SourceTaskProvider) {
                tasks.add(task);
            } else {
                // There are those nesting category tasks
                // if grouping treshold is matched.
                // Eliminate them to sustain list assumption.
                if (task.hasSubtasks()) {
                    loadSourceTasks(task.getSubtasks());  // recursion
                }
            }
        }
    }

    /**
     * Forward filtered events
     */
    private class EventHandler implements TaskListener {
        public void selectedTask(Task t) {
            if (getTasks().contains(t)) {
                Iterator it = listeners.iterator();
                while (it.hasNext()) {
                    TaskListener listener = (TaskListener) it.next();
                    listener.selectedTask(t);
                }
            }
        }

        public void warpedTask(Task t) {
            assert false : "Not implemented";
        }

        public void addedTask(Task t) {
            if (t.getSeed() instanceof SourceTaskProvider) {
                try {
                    silent = true;
                    tasks.add(t);
                } finally {
                    silent = false;
                }

                // fire event

                Iterator it = listeners.iterator();
                while (it.hasNext()) {
                    TaskListener listener = (TaskListener) it.next();
                    listener.addedTask(t);
                }
            } else if (t.hasSubtasks()) {
                // category nodes
                Iterator it = t.subtasksIterator();
                while (it.hasNext()) {
                    Task task = (Task) it.next();
                    addedTask(task);  // recursion
                }
            }
        }

        public void removedTask(Task pt, Task t, int index) {
            if (t.getSeed() instanceof SourceTaskProvider) {
                boolean removed = false;
                try {
                    silent = true;
                    removed = tasks.remove(t);
                } finally {
                    silent = false;
                }

                // fire event

                if (removed) {
                    Iterator it = listeners.iterator();
                    while (it.hasNext()) {
                        TaskListener listener = (TaskListener) it.next();
                        listener.removedTask(null, t, index);
                    }
                }
            } else if (t.hasSubtasks()) {
                // category nodes
                Iterator it = t.subtasksIterator();
                int ind = 0;
                while (it.hasNext()) {
                    Task task = (Task) it.next();
                    // TODO: always use 0 here instead of ind++?
                    removedTask(null, task, ind++);  // recursion
                }
            }
        }

        public void structureChanged(Task t) {
            // need to build it again
            try {
                silent = true;
                refreshSnapshot();
            } finally {
                silent = false;
            }
            fireStructureChanged(t);
        }
    }

}
