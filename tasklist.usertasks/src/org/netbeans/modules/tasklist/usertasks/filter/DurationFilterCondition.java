/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.filter;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;

import org.netbeans.modules.tasklist.core.filter.OneOfFilterCondition;
import org.netbeans.modules.tasklist.usertasks.DurationPanel;
import org.openide.util.NbBundle;

/**
 * Condition class for comparisons of durations.
 *
 * @author Tim Lebedkov
 */
public class DurationFilterCondition extends OneOfFilterCondition {
    public static final int EQUALS = 0;
    public static final int NOTEQUALS = 1;
    public static final int LESSTHAN = 2;
    public static final int LESSOREQUALS = 3;
    public static final int GREATERTHAN = 4;
    public static final int GREATEROREQUALS = 5;
       
    /**
     * Creates an array of filter conditions for the specified property
     *
     * @param index index of the property
     */
    public static DurationFilterCondition[] createConditions() {
        return new DurationFilterCondition[] {
            new DurationFilterCondition(DurationFilterCondition.EQUALS),
            new DurationFilterCondition(DurationFilterCondition.NOTEQUALS),
            new DurationFilterCondition(DurationFilterCondition.LESSTHAN),
            new DurationFilterCondition(DurationFilterCondition.LESSOREQUALS),
            new DurationFilterCondition(DurationFilterCondition.GREATERTHAN),
            new DurationFilterCondition(DurationFilterCondition.GREATEROREQUALS)
        };
    };
    
    private static String[] NAME_KEYS = {
        "Equals", // NOI18N
        "NotEquals", // NOI18N
        "LessThan", // NOI18N
        "LessEquals", // NOI18N
        "GreaterThan", // NOI18N
        "GreaterEquals" // NOI18N
    };
    
    /** saved constant for comparison */
    private int constant;
    
    /**
     * Creates a condition with the given name.
     *
     * @param prop index of the property this condition uses
     * @param id one of the constants from this class
     */
    public DurationFilterCondition(int id) {
        super(NAME_KEYS, id);
    }

    /**
     * Copy constructor.
     */
    public DurationFilterCondition( final DurationFilterCondition rhs) {
      super(rhs);
      this.constant = rhs.constant;
    }

    public Object clone() {
      return new DurationFilterCondition(this);
    }

    public JComponent createConstantComponent() {
        DurationPanel dp = new DurationPanel();
        dp.setDuration(constant);
        dp.setBorder(BorderFactory.createCompoundBorder(
            UIManager.getBorder("TextField.border"), // NOI18N
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        dp.setToolTipText(NbBundle.getMessage(
            DurationFilterCondition.class, "duration_desc")); // NOI18N
        return dp;
    }

    public void getConstantFrom(JComponent cmp) {
        DurationPanel dp = (DurationPanel) cmp;
        constant = dp.getDuration();
    }
    
    public boolean isTrue(Object obj) {
        int n = ((Integer) obj).intValue();
        switch (getId()) {
            case EQUALS:
                return constant == n;
            case NOTEQUALS:
                return constant != n;
            case LESSTHAN:
                return n < constant;
            case LESSOREQUALS:
                return n <= constant;
            case GREATERTHAN:
                return n > constant;
            case GREATEROREQUALS:
                return n >= constant;
            default:
                throw new InternalError("wrong id"); // NOI18N
        }
    }    

}
