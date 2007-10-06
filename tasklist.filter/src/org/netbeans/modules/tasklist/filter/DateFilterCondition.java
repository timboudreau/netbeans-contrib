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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;


/**
 * Basic condition class for date comparisons
 *
 * @author Tor Norbye
 * @author tl
 */
public class DateFilterCondition extends OneOfFilterCondition {
    public static final int EQUALS = 0;
    public static final int NOTEQUALS = 1;
    public static final int EARLIERTHAN = 2;
    public static final int LATERTHAN = 3;
    public static final int ISTODAY = 4;
    public static final int ISUNDEFINED = 5;
       
    private static String[] NAME_KEYS = {
        "Equals", // NOI18N
        "NotEquals", // NOI18N
        "EarlierThan", // NOI18N
        "LaterThan", // NOI18N
        "IsToday", // NOI18N
        "IsUndefined" // NOI18N
    };
    
    /**
     * Creates an array of filter conditions for the specified property
     *
     * @param index index of the property
     */
    public static DateFilterCondition[] createConditions() {
        return new DateFilterCondition[] {
            new DateFilterCondition(DateFilterCondition.EARLIERTHAN),
            new DateFilterCondition(DateFilterCondition.LATERTHAN),
            new DateFilterCondition(DateFilterCondition.EQUALS),
            new DateFilterCondition(DateFilterCondition.NOTEQUALS),
            new DateFilterCondition(DateFilterCondition.ISTODAY),
            new DateFilterCondition(DateFilterCondition.ISUNDEFINED)
        };
    };
    
    /** saved constant for comparison */
    private Date constant = new Date();
    
    /** Date format */
    private static SimpleDateFormat sdf = new SimpleDateFormat();
    
    /** contains today's date 00:00 or -1 if not initialized */
    private long today = -1;
    
    /**
     * Creates a condition with the given name.
     *
     * @param prop index of the property this condition uses
     * @param id one of the constants from this class
     */
    public DateFilterCondition(int id) {
        super(NAME_KEYS, id);
    }


    public DateFilterCondition(final DateFilterCondition rhs) {
        super(rhs);
        this.constant = (rhs.constant == null) ? null : (Date)rhs.constant.clone();
    }

  
    public Object clone() {
        return new DateFilterCondition(this);  
    }
    
    /** for deconvertization **/
    DateFilterCondition() {
        super(NAME_KEYS);
        this.constant = null;
    }
    
    public JComponent createConstantComponent() {
        if (getId() != ISTODAY && getId() != ISUNDEFINED) {
            JTextField tf = new JTextField();
            tf.setText(sdf.format(constant));
            tf.setToolTipText(Util.getString("date_desc"));
            return tf;
        } else {
            return null;
        }
    }

    public void getConstantFrom(JComponent cmp) {
        JTextField tf = (JTextField) cmp;
        try {
            constant = sdf.parse(tf.getText());
        } catch (ParseException e) {
            // ignore TODO 
        }
    }
    
    public boolean isTrue(Object obj) {
        int c;
        if (obj != null)
            c = ((Date) obj).compareTo(constant);
        else
            c = 0;
        switch (getId()) {
            case EQUALS:
                return obj != null && c == 0;
            case NOTEQUALS:
                return obj != null && c != 0;
            case EARLIERTHAN:
                return obj != null && c < 0;
            case LATERTHAN:
                return obj != null && c > 0;
            case ISTODAY: {
                if (obj == null)
                    return false;
                
                if (today < 0 || System.currentTimeMillis() - today >= 
                    24 * 60 * 60 * 1000) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    today = cal.getTimeInMillis();
                }
                long diff = ((Date) obj).getTime() - today;
                return diff >= 0 && diff < 24 * 60 * 60 * 1000;
            }
            case ISUNDEFINED:
                return obj == null;
            default:
                throw new InternalError("wrong id");
        }
    }    

    private static class Convertor extends OneOfFilterCondition.Convertor {
        private static final String ELEM_DATE_CONDITION = "DateCondition";
        private static final String ATTR_DATE = "date";
        
        public Convertor() { 
            super(ELEM_DATE_CONDITION, NAME_KEYS);
        }
        
        public static DateFilterCondition.Convertor create() {
            return new DateFilterCondition.Convertor();
        }
        
        protected Object readElement(org.w3c.dom.Element element) throws java.io.IOException, java.lang.ClassNotFoundException {
            DateFilterCondition cond = new DateFilterCondition();
            super.readCondition(element, cond);
            cond.constant = new Date(Long.parseLong(element.getAttribute(ATTR_DATE)));
            return cond;
        }
        
        // write methods for supported condition types
        protected void writeElement(org.w3c.dom.Document document, org.w3c.dom.Element element, Object obj)
        throws java.io.IOException, org.w3c.dom.DOMException {
            DateFilterCondition cond = (DateFilterCondition)obj;
            super.writeElement(document, element, cond);
            element.setAttribute(ATTR_DATE, Long.toString(cond.constant.getTime()));
            
        }
    }
}
