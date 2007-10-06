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
