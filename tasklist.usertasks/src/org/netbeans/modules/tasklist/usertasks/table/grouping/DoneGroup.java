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
public class DoneGroup extends Group {
    /** Done. */
    public static final DoneGroup DONE = new DoneGroup(true);
    
    /** Undone. */
    public static final DoneGroup UNDONE = new DoneGroup(false);
    
    private boolean done;

    /**
     * Constructor.
     * 
     * @param done true = done
     */
    private DoneGroup(boolean done) {
        this.done = done;                
    }

    public String getDisplayName() {
        if (done)
            return NbBundle.getMessage(DoneGroup.class, "Done"); // NOI18N
        else
            return NbBundle.getMessage(DoneGroup.class, "Undone"); // NOI18N
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DoneGroup other = (DoneGroup) obj;

        if (this.done != other.done)
            return false;
        return true;
    }
}
