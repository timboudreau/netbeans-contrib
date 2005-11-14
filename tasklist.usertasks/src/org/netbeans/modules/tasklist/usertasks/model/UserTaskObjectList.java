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

package org.netbeans.modules.tasklist.usertasks.model;

import java.util.Iterator;
import org.netbeans.modules.tasklist.core.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.*;

/**
 * List of UserTasks
 */
public class UserTaskObjectList extends ObjectList {
    private ObjectList.Owner parent;
    
    /**
     * Creates a new instance of UserTaskObjectList
     *
     * @param parent parent for all tasks in this collection. UserTask or
     * UserTaskList
     */
    public UserTaskObjectList(ObjectList.Owner parent) {
        assert parent instanceof UserTask || parent instanceof UserTaskList;
        this.parent = parent;
    }
    
    /**
     * Returns the user task with the specified index
     *
     * @param index index of the task
     * @see #get(int)
     */
    public UserTask getUserTask(int index) {
        return (UserTask) get(index);
    }
    
    /**
     * Returns the parent of this tasks or null.
     *
     * @return owner of this list. UserTask or UserTaskList
     */
    public ObjectList.Owner getOwner() {
        return parent;
    }
    
    public void add(int index, Object element) {
        if (parent instanceof UserTask) {
            ((UserTask) element).setParent((UserTask) parent);
            ((UserTask) element).setList(((UserTask) parent).getList());
        } else {
            ((UserTask) element).setParent(null);
            ((UserTask) element).setList((UserTaskList) parent);
        }
        super.add(index, element);
    }
    
    public Object remove(int index) {
        Object element = super.remove(index);
        ((UserTask) element).setParent(null);
        ((UserTask) element).setList(null);
        return element;
    }    
    
    /**
     * Removes completed tasks (recursively).
     */
    public void purgeCompletedItems() {
        if (size() == 0 )
            return;

        Iterator it = iterator();
        while (it.hasNext()) {
            UserTask task = (UserTask) it.next();
            if (task.isDone())
                it.remove();
            else
                task.getSubtasks().purgeCompletedItems();
        }
    }
}
