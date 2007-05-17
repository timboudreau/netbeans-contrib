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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.filter;

import javax.swing.*;

/**
 * Basic condition class for comparisons of boolean values.
 *
 * @author Tor Norbye
 * @author tl
 */
public class BooleanFilterCondition extends OneOfFilterCondition {
    public static final int ISTRUE = 0;
    public static final int ISFALSE = 1;

    private static String[] NAME_KEYS = {
        "IsTrue", // NOI18N
        "IsFalse", // NOI18N
    };

    /**
     * Creates an array of filter conditions for the specified property
     *
     * @param index index of the property
     */
    public static BooleanFilterCondition[] createConditions() {
        return new BooleanFilterCondition[] {
            new BooleanFilterCondition(BooleanFilterCondition.ISTRUE),
            new BooleanFilterCondition(BooleanFilterCondition.ISFALSE)
        };
    };
    
    /**
     * Creates a condition with the given name.
     *
     * @param prop index of the property this condition uses
     * @param id one of the constants from this class
     */
    public BooleanFilterCondition(int id) {
      super(NAME_KEYS, id);
    }

    public BooleanFilterCondition(final BooleanFilterCondition rhs) {
        super(rhs);
    }

    public Object clone() {
        return new BooleanFilterCondition(this);
    }
    
    BooleanFilterCondition() { super(NAME_KEYS);};

    public JComponent createConstantComponent() {
        return null;
    }

    public boolean isTrue(Object obj) {
        boolean n = ((Boolean) obj).booleanValue();
        switch (getId()) {
            case ISTRUE:
                return n == true;
            case ISFALSE:
                return n == false;
            default:
                throw new InternalError("wrong id"); // NOI18N
        }
    }    


  private static class Convertor extends OneOfFilterCondition.Convertor {
    private static final String ELEM_BOOLEAN_CONDITION = "BooleanCondition";

    public Convertor() { super(ELEM_BOOLEAN_CONDITION, NAME_KEYS);}

    public static BooleanFilterCondition.Convertor create() { 
      return new BooleanFilterCondition.Convertor();
    }
    protected OneOfFilterCondition createCondition() { return new BooleanFilterCondition();}

  }

}
