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
public class DurationGroup extends Group {
    /**
     * Group type. 
     * 
     * If you rename members of this enumeration, you have to change also
     * Bundle.properties.
     */
    public enum Type {
        _0_TO_15_MINUTES,
        _15_TO_30_MINUTES,
        _30_TO_60_MINUTES,
        _1_TO_2_HOURS,
        _2_TO_4_HOURS,
        _4_TO_8_HOURS,
        _1_TO_2_DAYS,
        _2_DAYS_TO_1_WEEK,
        _1_WEEK_TO_2_WEEKS,
        _2_WEEKS_TO_1_MONTH,
        _1_MONTH_TO_3_MONTHES,
        _3_MONTHES_TO_6_MONTHES,
        _6_MONTHES_TO_1_YEAR,
        MORE_THAN_1_YEAR
    };
    
    /**
     * Default groups. 
     */
    public static final DurationGroup[] GROUPS = {
        new DurationGroup(Type._0_TO_15_MINUTES),
        new DurationGroup(Type._15_TO_30_MINUTES),
        new DurationGroup(Type._30_TO_60_MINUTES),
        new DurationGroup(Type._1_TO_2_HOURS),
        new DurationGroup(Type._2_TO_4_HOURS),
        new DurationGroup(Type._4_TO_8_HOURS),
        new DurationGroup(Type._1_TO_2_DAYS),
        new DurationGroup(Type._2_DAYS_TO_1_WEEK),
        new DurationGroup(Type._1_WEEK_TO_2_WEEKS),
        new DurationGroup(Type._2_WEEKS_TO_1_MONTH),
        new DurationGroup(Type._1_MONTH_TO_3_MONTHES),
        new DurationGroup(Type._3_MONTHES_TO_6_MONTHES),
        new DurationGroup(Type._6_MONTHES_TO_1_YEAR),
        new DurationGroup(Type.MORE_THAN_1_YEAR)
    };
    
    private Type type;
    
    /**
     * Constructor.
     * 
     * @param type type of the group
     */
    public DurationGroup(Type type) {
        this.type = type;
    }

    public String getDisplayName() {
        return NbBundle.getMessage(DurationGroup.class, type.name());
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DurationGroup other = (DurationGroup) obj;

        if (this.type != other.type &&
            (this.type == null || !this.type.equals(other.type)))
            return false;
        return true;
    }

}
