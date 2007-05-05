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

import org.openide.util.NbBundle;

/**
 * Group for the "done" property.
 *
 * @author tl
 */
public class NotEmptyStringGroup extends Group {
    /** Empty. */
    public static final NotEmptyStringGroup EMPTY = new NotEmptyStringGroup(false);
    
    /** Not empty. */
    public static final NotEmptyStringGroup NON_EMPTY = new NotEmptyStringGroup(true);
    
    private boolean notEmpty;

    /**
     * Constructor.
     * 
     * @param notEmpty true = done
     */
    private NotEmptyStringGroup(boolean notEmpty) {
        this.notEmpty = notEmpty;                
    }

    public String getDisplayName() {
        if (notEmpty)
            return NbBundle.getMessage(NotEmptyStringGroup.class, 
                    "NonEmpty"); // NOI18N
        else
            return NbBundle.getMessage(NotEmptyStringGroup.class, 
                    "Empty"); // NOI18N
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final NotEmptyStringGroup other = (NotEmptyStringGroup) obj;

        if (this.notEmpty != other.notEmpty)
            return false;
        return true;
    }
}
