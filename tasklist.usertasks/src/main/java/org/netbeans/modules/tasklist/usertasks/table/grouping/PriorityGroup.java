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
