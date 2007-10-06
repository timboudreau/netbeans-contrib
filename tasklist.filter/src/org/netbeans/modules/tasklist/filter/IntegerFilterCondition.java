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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
