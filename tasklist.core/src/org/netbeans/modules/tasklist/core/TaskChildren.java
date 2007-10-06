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

import java.util.*;

import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.WeakListeners;


/**
 * Children object for the Task; used to track
 * TaskList modifications and update nodes appropriately.
 *
 * @author Tor Norbye
 * @author Petr Kuzel
 */
public class TaskChildren extends Children.Keys {

    private final Task parent;
    private Monitor monitor;

    public TaskChildren(Task parent) {
        this.parent = parent;
    }

    private void refreshKeys() {
        Collection keys;
        if (parent.hasSubtasks() == false) {
            keys = Collections.EMPTY_SET;
        } else {

// It does not work and does not save any Node instance creation
//            // threat task clones as equal
//            // XXX we may need to refresh nodes that share key
//            // why do all pay here this extra overhead?
//
//            int size = parent.getSubtasks().size();
//            ArrayList list = new ArrayList(size);
//            keys2tasks = new WeakHashMap(size*2);
//            Iterator it = parent.getSubtasks().iterator();
//            while (it.hasNext()) {
//                Task task = (Task) it.next();
//                Object key = task.getKey();
//                list.add(key);
//                keys2tasks.put(key, task);
//            }
//            setKeys(list);
            keys = parent.getSubtasks();
        }

        Task[] tasks = (Task[])keys.toArray(new Task[keys.size()]);
        setKeys(tasks);
    }

    /**
     * Called when the parent node is expanded; now we need
     * to create nodes for the children.
     */
    protected void addNotify() {
        super.addNotify();
        assert monitor == null : "Dangling addNotify()"; // NOI18N
        monitor = new Monitor();

        // weak listener must be used here because children
        // all listening on tasklist that has different
        // lifetime than parent node nor is driven by it.
        TaskListener l = (TaskListener) WeakListeners.create(TaskListener.class, monitor, parent);
        parent.addTaskListener(l);
        refreshKeys();
    }
    
    /** Called when the parent node is collapsed: cleanup */    
    protected void removeNotify() {
        assert monitor != null : "Dangling removeNotify()"; // NOI18N
        // parent.getList().removeTaskListener(monitor);
        monitor = null;
        setKeys(Collections.EMPTY_SET);
        super.removeNotify();
    }
    
    /**
     * Create nodes for the specified key object (a task)
     * @param key The task used as a parent key
     * @return Node for the key task's children
     */
    protected Node[] createNodes(Object key) {
        // interpret your key here...usually one node generated, but could be zero or more
        //  return new Node[] { new TodoNode((MyParameter) key) };
        //return new Node[] { new TodoNode(key) };

//        Task item = (Task)keys2tasks.get(key);
//        assert item != null : "The key was held by Children.Keys!";
//        return ((Task)key).createNode(); // XXX I do not like this
//        model-view 1:1
      
      Task task = (Task)key;
      return new Node[] { createNode(task) };
    }

    public Object clone() {
      return new TaskChildren(this.parent);
    }
    


    /** 
     * A factory method for creating new task nodes 
     */
    protected TaskNode createNode(Task task) {
      return new TaskNode(task);
    }


    // Monitor tasklist and react to changes ~~~~~~~~~~~~~~~~~~~~~

    private class Monitor implements TaskListener {
        public Monitor () {}
        
        public void selectedTask(Task t) {
            // it's node job
        }

        public void warpedTask(Task t) {
            // it's node job
        }

        public void addedTask(Task t) {
            if (t.getParent() == parent) {
                refreshKeys();
            }
        }

        public void removedTask(Task pt, Task t, int index) {
            if (t.getParent() == parent) {
                refreshKeys();
            }
        }

        public void structureChanged(Task t) {
            if (t == null || t.isParentOf(parent)) {
                refreshKeys();
            }
        }

    }
}
