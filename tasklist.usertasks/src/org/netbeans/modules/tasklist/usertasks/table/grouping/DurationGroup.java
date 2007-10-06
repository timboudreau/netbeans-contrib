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
