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

/**
 * Implementation of the class Group for a small number of objects that
 * define a group by itself (e.g. priority).
 *
 * @author tl
 */
public class ValueGroup extends Group {
    private Comparable groupValue;
    
    /**
     * Constructor.
     * 
     * @param groupValue a value (!= null). toString() will be used for
     * display name
     */
    public ValueGroup(Comparable groupValue) {
        this.groupValue = groupValue;
    }

    public String getDisplayName() {
        return groupValue.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ValueGroup other = (ValueGroup) obj;

        if (this.groupValue != other.groupValue &&
            (this.groupValue == null ||
             !this.groupValue.equals(other.groupValue)))
            return false;
        return true;
    }
}
