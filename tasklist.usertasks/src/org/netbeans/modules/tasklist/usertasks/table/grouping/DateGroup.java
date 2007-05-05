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
 * Group for durations.
 *
 * @author tl
 */
public class DateGroup extends Group {
    /**
     * Group type. 
     * 
     * If you rename members of this enumeration, you have to change also
     * Bundle.properties.
     */
    public enum Type {
        UNDEFINED, // for millis <= 0
        PAST,
        LAST_MONTH,
        LAST_WEEK,
        YESTERDAY,
        TODAY,
        TOMORROW,
        THIS_WEEK,
        THIS_MONTH,
        FUTURE
    }
  
    /**
     * Default groups. 
     */
    public static final DateGroup[] GROUPS = {
        new DateGroup(Type.UNDEFINED),
        new DateGroup(Type.PAST),
        new DateGroup(Type.LAST_MONTH),
        new DateGroup(Type.LAST_WEEK),
        new DateGroup(Type.YESTERDAY),
        new DateGroup(Type.TODAY),
        new DateGroup(Type.TOMORROW),
        new DateGroup(Type.THIS_WEEK),
        new DateGroup(Type.THIS_MONTH),
        new DateGroup(Type.FUTURE),
    };
    
    private Type type;
    
    /**
     * Constructor.
     * 
     * @param type type of the group
     */
    public DateGroup(Type type) {
        this.type = type;
    }

    public String getDisplayName() {
        return NbBundle.getMessage(DateGroup.class, type.name());
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DateGroup other = (DateGroup) obj;

        if (this.type != other.type)
            return false;
        return true;
    }
}
