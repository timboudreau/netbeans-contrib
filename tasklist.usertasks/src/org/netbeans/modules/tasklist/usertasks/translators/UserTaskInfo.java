/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.translators;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.TreeAbstraction;

/**
 * Information about a task.
 */
final class UserTaskInfo {
    /** UserTask, UserTaskList or null for "all task lists". */
    public Object object;

    /** spent times in milliseconds. */
    public long[] spentTimes;

    /** nested tasks. */
    public List<UserTaskInfo> children = new ArrayList<UserTaskInfo>();
    
    public TreeAbstraction<UserTaskInfo> createTreeInterface() {
        return new TreeAbstraction<UserTaskInfo>() {
            public UserTaskInfo getRoot() {
                return UserTaskInfo.this;
            }

            public int getChildCount(UserTaskInfo obj) {
                return obj.children.size();
            }

            public UserTaskInfo getChild(UserTaskInfo obj, int index) {
                return obj.children.get(index);
            }
        };
    }
}

