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

package org.netbeans.modules.tasklist.usertasks.table;

import java.util.List;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * Grouped UserTasks:
 *
 * @author tl
 */
public class UTGroup {
    private List<UserTask> tasks;
    private String displayName;

    /**
     * Constructor.
     * 
     * @param displayName display name
     * @param tasks tasks for this group
     */
    public UTGroup(String displayName, List<UserTask> tasks) {
        this.displayName = displayName;
        this.tasks = tasks;
    }

    /**
     * Returns tasks from this group.
     * 
     * @return tasks 
     */
    public List<UserTask> getTasks() {
        return tasks;
    }

    /**
     * Returns display name for this group.
     * 
     * @return display name 
     */
    public String getDisplayName() {
        return displayName;
    }
}
