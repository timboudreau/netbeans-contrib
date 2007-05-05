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
