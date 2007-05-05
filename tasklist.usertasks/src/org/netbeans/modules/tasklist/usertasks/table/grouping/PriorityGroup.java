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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.tasklist.usertasks.table.grouping;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * Group for a priority.
 *
 * @author tl
 */
public class PriorityGroup extends Group {
    /**
     * Groups for priorities. 
     */
    public static final PriorityGroup[] GROUPS = {
        new PriorityGroup(UserTask.HIGH),
        new PriorityGroup(UserTask.MEDIUM_HIGH),
        new PriorityGroup(UserTask.MEDIUM),
        new PriorityGroup(UserTask.MEDIUM_LOW),
        new PriorityGroup(UserTask.LOW),
    };
    
    private int priority;

    /**
     * Constructor.
     */
    private PriorityGroup(int priority) {
        this.priority = priority;                
    }

    public String getDisplayName() {
        return UserTask.getPriorityName(priority);
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PriorityGroup other = (PriorityGroup) obj;

        if (this.priority != other.priority)
            return false;
        return true;
    }
}
