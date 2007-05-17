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
 * Basic condition class for comparisons of integer values.
 *
 * @author Tor Norbye
 * @author tl
 */
public class IntegerFilterCondition extends OneOfFilterCondition {
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
    public static IntegerFilterCondition[] createConditions() {
        return new IntegerFilterCondition[] {
            new IntegerFilterCondition(IntegerFilterCondition.EQUALS),
            new IntegerFilterCondition(IntegerFilterCondition.NOTEQUALS),
            new IntegerFilterCondition(IntegerFilterCondition.LESSTHAN),
            new IntegerFilterCondition(IntegerFilterCondition.LESSOREQUALS),
            new IntegerFilterCondition(IntegerFilterCondition.GREATERTHAN),
            new IntegerFilterCondition(IntegerFilterCondition.GREATEROREQUALS)
        };
    };
    
    private static String[] NAME_KEYS = {
        "Equals", // NOI18N
        "NotEquals", // NOI18N
        "LessThan", // NOI18N
        "LessEquals", // NOI18N
        "GreaterThan", // NOI18N
        "GreaterEquals", // NOI18N
    };
    
    /** saved constant for comparison */
    private int constant;
    
    /**
     * Creates a condition with the given name.
     *
     * @param prop index of the property this condition uses
     * @param id one of the constants from this class
     */
    public IntegerFilterCondition(int id) {
        super(NAME_KEYS, id);
    }
    
    public IntegerFilterCondition(final IntegerFilterCondition rhs) {
        super(rhs);
        this.constant = rhs.constant;
    }
    
    public Object clone() {
        return new IntegerFilterCondition(this);
    }

    /** for deconvertization **/
    private IntegerFilterCondition() {super(NAME_KEYS); this.constant = -1; }

    public JComponent createConstantComponent() {
        JTextField tf = new JTextField();
        tf.setText(String.valueOf(constant));
        tf.setToolTipText(Util.getString("int_desc"));
        return tf;
    }

    public void getConstantFrom(JComponent cmp) {
        JTextField tf = (JTextField) cmp;
        try {
            constant = Integer.parseInt(tf.getText());
        } catch (NumberFormatException e) {
            // ignore TODO
        }
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
                throw new InternalError("wrong id");
        }
    }    

  private static class Convertor extends OneOfFilterCondition.Convertor {
    private static final String ELEM_INTEGER_CONDITION = "IntegerCondition";
    private static final String ATTR_CONSTANT = "constant";

    public Convertor() { super(ELEM_INTEGER_CONDITION, NAME_KEYS);}
    public static IntegerFilterCondition.Convertor create() { 
      return new IntegerFilterCondition.Convertor();
    }

    protected Object readElement(org.w3c.dom.Element element) 
      throws java.io.IOException, java.lang.ClassNotFoundException 
    {
      IntegerFilterCondition cond = new IntegerFilterCondition();
      super.readCondition(element, cond);
      cond.constant = Integer.parseInt(element.getAttribute(ATTR_CONSTANT));
      return cond;
    }
   
    // write methods for supported condition types
    protected void writeElement(org.w3c.dom.Document document, org.w3c.dom.Element element, Object obj) 
      throws java.io.IOException, org.w3c.dom.DOMException 
    {
      IntegerFilterCondition cond = (IntegerFilterCondition)obj;
      super.writeCondition(document, element, cond);
      element.setAttribute(ATTR_CONSTANT, Integer.toString(cond.constant));
    }   
  }

}
