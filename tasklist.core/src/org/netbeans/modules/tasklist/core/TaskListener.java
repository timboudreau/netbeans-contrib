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

package org.netbeans.modules.tasklist.core;

import java.util.EventListener;

/** 
 * Listener which when notified off changes in current task
 * updates the editor
 *
 * @author Tor Norbye
 */
public interface TaskListener extends EventListener {

    /** Called to indicate that a particular task is made current.
	 * Do what you can to "select" this task.
     */
    void selectedTask(Task t);

    /** Called to indicate that a particular task has been "warped to".
	Do what you can to "warp to" this task. Typically means show
        associated fileposition in the editor.
     * @deprecated should be removed to split the model from the view
    */
    void warpedTask(Task t);

    /** 
     * A task has been added. If null, a number of tasks have
     * been added. 
     *
     * @param t added task
     */
    void addedTask(Task t);

    /** 
     * A task has been added. If null, a number of tasks have
     * been added. 
     *
     * @param pt parent task
     * @param t removed task
     */
    void removedTask(Task pt, Task t);

    /**
     * A task has been changed (some properties has been changed).
     *
     * @param a task that has changed
     */
    void changedTask(Task t);

    /**
     * Invoked after the tree has drastically changed structure from a 
     * given node down. 
     *
     * @param t parent of the changed subtree.
     */
    void structureChanged(Task t);
}
