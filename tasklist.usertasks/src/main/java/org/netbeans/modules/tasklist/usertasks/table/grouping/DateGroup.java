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
