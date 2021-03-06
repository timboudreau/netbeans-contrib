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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

import java.util.Iterator;
import org.netbeans.modules.tasklist.usertasks.*;
import org.netbeans.modules.tasklist.usertasks.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.util.ObjectListEvent;

/**
 * List of UserTasks
 *
 * @author tl
 */
public class UserTaskObjectList extends ObjectList<UserTask> {
    private Object parent;

    /**
     * Creates a new instance of UserTaskObjectList
     *
     * @param parent parent for all tasks in this collection. UserTask or
     * UserTaskList
     */
    public UserTaskObjectList(Object parent) {
        assert parent instanceof UserTask || parent instanceof UserTaskList;
        this.parent = parent;
    }
    
    /**
     * Returns the user task with the specified index
     *
     * TODO: use get(int) instead
     * 
     * @param index index of the task
     * @see #get(int)
     */
    public UserTask getUserTask(int index) {
        return (UserTask) get(index);
    }
    
    public void add(int index, UserTask element) {
        UserTask ut = (UserTask) element;
        if (ut.getParent() != null)
            ut.getParent().getSubtasks().remove(ut);
        else if (ut.getList() != null)
            ut.getList().getSubtasks().remove(ut);
        if (parent instanceof UserTask) {
            ut.setParent((UserTask) parent);
            ut.setList(((UserTask) parent).getList());
        } else {
            ut.setParent(null);
            ut.setList((UserTaskList) parent);
        }
        super.add(index, element);
    }
    
    public UserTask remove(int index) {
        UserTask element = super.remove(index);
        ((UserTask) element).setParent(null);
        ((UserTask) element).setList(null);
        return element;
    }    
    
    /**
     * Removes completed tasks (recursively).
     */
    public void purgeCompletedItems() {
        if (size() == 0)
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

    @Override
    protected void fireEvent(ObjectListEvent e) {
        UserTaskList list;
        if (parent instanceof UserTaskList)
            list = (UserTaskList) parent;
        else
            list = ((UserTask) parent).getList();
        super.fireEvent(e);
        list.fireEvent(e);
    }    
}
