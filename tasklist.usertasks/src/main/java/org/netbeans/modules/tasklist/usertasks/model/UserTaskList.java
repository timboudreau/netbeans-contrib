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

package org.netbeans.modules.tasklist.usertasks.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListEvent;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListListener;

/**
 * This class represents the tasklist itself
 *
 * @author Tor Norbye
 * @author Trond Norbye
 * @author tl
 */
public class UserTaskList {    
    /**
     * Callback for the UserTaskList.process method
     */
    public static interface UserTaskProcessor {
        /**
         * This method will be called for each user task.
         *
         * @param ut reference to the task
         */
        public void process(UserTask ut);
    }
    
    private UserTaskObjectList tasks;

    /** 
     * this value is used by ICalImport/ExportFormat to store additional
     * not editable parameters
     */
    public Object userObject;

    /** <ChangeListener> */
    private EventListenerList listeners = new EventListenerList();
    
    /**
     * Creates a new instance.
     */
    public UserTaskList() {
        tasks = new UserTaskObjectList(this);
        tasks.addListener(new ObjectListListener() {
            public void listChanged(ObjectListEvent ev) {
                UserTaskList.this.fireChange();
            }
        });
    }
    
    /**
     * Searches for owners through all tasks.
     *
     * @return all found categories
     */
    public String[] getOwners() {
        Set<String> cat = new java.util.HashSet<String>();
        
        UserTaskObjectList ol = this.getSubtasks();
        for (int i = 0; i < ol.size(); i++) {
            findOwners(ol.get(i), cat);
        }
        return cat.toArray(new String[cat.size()]);
    }
    
    /**
     * Searches for owners
     *
     * @param task search for owners in this task and all of it's subtasks
     * recursively
     * @param cat container for found owners. <String>
     */
    private static void findOwners(UserTask task, Set<String> cat) {
        if (task.getOwner().length() != 0)
            cat.add(task.getOwner());
        
        UserTaskObjectList ol = task.getSubtasks();
        for(int i = 0; i < ol.size(); i++) {
            findOwners(ol.get(i), cat);
        }
    }
    
    /**
     * Searches for categories through all tasks.
     *
     * @return all found categories
     */
    public String[] getCategories() {
        Iterator it = this.getSubtasks().iterator();
        Set<String> cat = new java.util.HashSet<String>();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
            findCategories(ut, cat);
        }
        return cat.toArray(new String[cat.size()]);
    }
    
    /**
     * Searches for categories
     *
     * @param task search for categories in this task and all of it's subtasks
     * recursively
     * @param cat container for found categories. String[]
     */
    private static void findCategories(UserTask task, Set<String> cat) {
        if (task.getCategory().length() != 0)
            cat.add(task.getCategory());
        
        Iterator it = task.getSubtasks().iterator();
        while (it.hasNext()) {
            findCategories((UserTask) it.next(), cat);
        }
    }
    
    /**
     * Look up a particular item by uid
     */ 
    public UserTask findItem(Iterator tasks, String uid) {
        while (tasks.hasNext()) {
            UserTask task = (UserTask)tasks.next();
            if (task.getUID().equals(uid)) {
                return task;
            }
            if (!task.getSubtasks().isEmpty()) {
                UserTask f = findItem(task.getSubtasks().iterator(), uid);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Process all tasks including subtasks in the depth-first order.
     *
     * @param p a callback that will be called for each task
     * @param list a list of user tasks
     */
    public static void processDepthFirst(UserTaskProcessor p, UserTaskObjectList list) {
        for (int i = 0; i < list.size(); i++) {
            UserTask ut = list.getUserTask(i);
            processDepthFirst(p, ut.getSubtasks());
            p.process(ut);
        }
    }
        
    /** 
     * Returns top-level tasks holded by this list. 
     *
     * @return list of top-level tasks
     */
    public final UserTaskObjectList getSubtasks() {
        return tasks;
    }

    /**
     * Should be called after closing a view. Removes all annotations.
     */
    public void destroy() {
        Iterator it = getSubtasks().iterator();
        while (it.hasNext()) {
            UserTask ut = (UserTask) it.next();
            ut.destroy();
        }
    }
    
    /**
     * Returns all subtasks (searches recursively).
     *
     * @return list of UserTask
     */
    public List getAllSubtasks() {
        List<UserTask> ret = new ArrayList<UserTask>();
        collectAllSubtasks(ret, getSubtasks());
        return ret;
    }
    
    /**
     * Collects all tasks recursively.
     *
     * @param ret output 
     * @param tasks a list of UserTasks
     */
    private void collectAllSubtasks(List<UserTask> ret, UserTaskObjectList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            ret.add(tasks.getUserTask(i));
            collectAllSubtasks(ret, tasks.getUserTask(i).getSubtasks());
        }
    }
    
    /**
     * Adds a change listener. The listener will be notified whenever some
     * change occures to a task at any level in this task list.
     *
     * @param l a listener
     */
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a change listener.
     *
     * @param l a listener.
     */
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
    
    /**
     * Fires a ChangeEvent
     */
    void fireChange() {
        // Guaranteed to return a non-null array
        Object[] list = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        ChangeEvent changeEvent = null;
        for (int i = list.length - 2; i >= 0; i -= 2) {
            if (list[i] == ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener) list[i+1]).stateChanged(changeEvent);
            }
        }
    }    
    
    /**
     * Adds a listener to this list. Adding a listener here has the 
     * same effect as adding the listener to each UserTaskObjectList at
     * every level.
     *
     * @param listener a listener
     */
    public void addObjectListListener(ObjectListListener listener) {
        listeners.add(ObjectListListener.class, listener);
    }
    
    /**
     * Removes a listener.
     *
     * @param listener a listener
     */
    public void removeObjectListListener(ObjectListListener listener) {
        listeners.remove(ObjectListListener.class, listener);
    }

    /**
     * Fires an event
     *
     * @param e an event
     */
    public void fireEvent(ObjectListEvent e) {
        // Guaranteed to return a non-null array
        Object[] l = listeners.getListenerList();
        
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = l.length - 2; i >= 0; i -= 2) {
            if (l[i] == ObjectListListener.class)
                ((ObjectListListener) l[i+1]).listChanged(e);
        }
    }
    
    
    /* For debugging purposes, only. Writes directly to serr. 
    public void print() {
        System.err.println("\nTask List:\n-------------");
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            Task next = (Task) it.next();
            recursivePrint(next, 0);
        }

        System.err.println("\n\n");
    }

    private void recursivePrint(Task node, int depth) {
        if (depth > 20) { // probably invalid list
            Thread.dumpStack();
            return;
        }
        for (int i = 0; i < depth; i++) {
            System.err.print("   ");
        }
        System.err.println(node);
        if (node.getSubtasks() != null) {
            List l = node.getSubtasks();
            ListIterator it = l.listIterator();
            while (it.hasNext()) {
                Task task = (Task) it.next();
                recursivePrint(task, depth + 1);
            }
        }
    }*/

    // TaskListener impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
