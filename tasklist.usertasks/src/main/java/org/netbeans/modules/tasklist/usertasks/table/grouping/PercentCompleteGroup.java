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
 * Group for a completion percentage (java.lang.Integer)
 *
 * @author tl
 */
public class PercentCompleteGroup extends Group {
    /**
     * Default groups. 
     */
    public static final PercentCompleteGroup[] GROUPS = {
        new PercentCompleteGroup(0, 9),
        new PercentCompleteGroup(10, 19),
        new PercentCompleteGroup(20, 29),
        new PercentCompleteGroup(30, 39),
        new PercentCompleteGroup(40, 49),
        new PercentCompleteGroup(50, 59),
        new PercentCompleteGroup(60, 69),
        new PercentCompleteGroup(70, 79),
        new PercentCompleteGroup(80, 89),
        new PercentCompleteGroup(90, 100),
    };
    
    private int from, to;

    /**
     * Constructor.
     * 
     * @param from minimum percentage (inclusively)
     * @param to maximum percentage (inclusively)
     */
    public PercentCompleteGroup(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public String getDisplayName() {
        return NbBundle.getMessage(PercentCompleteGroup.class, 
                "FromTo", from, to); // NOI18N
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PercentCompleteGroup other = (PercentCompleteGroup) obj;

        if (this.from != other.from)
            return false;
        if (this.to != other.to)
            return false;
        return true;
    }
}
