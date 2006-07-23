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

package org.netbeans.modules.tasklist.usertasks.util;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

/**
 * TreeIntf for UserTaskList.
 *
 * @author tl
 */
public class UTListTreeAbstraction implements TreeAbstraction<Object> {
    private UserTaskList utl;
    
    /**
     * Creates a new instance of UTTree.
     *
     * @param utl a UserTaskList
     */
    public UTListTreeAbstraction(UserTaskList utl) {
        this.utl = utl;
    }

    public int getChildCount(Object obj) {
        if (obj == utl)
            return utl.getSubtasks().size();
        else
            return ((UserTask) obj).getSubtasks().size();
    }

    public Object getChild(Object obj, int index) {
        if (obj == utl)
            return utl.getSubtasks().get(index);
        else
            return ((UserTask) obj).getSubtasks().get(index);
    }

    public UserTaskList getRoot() {
        return utl;
    }
}
