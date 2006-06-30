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

/*
 * TaggedEditor.java
 */

package org.netbeans.modules.j2ee.sun.ws7.ide.editors;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;

/**
 *
 * @author Administrator
 */
    public class TaggedEditor extends PropertyEditorSupport {
        private Object curr_Sel;
        private Class clazz;
        private Method getValue;
        private Method getChoices;
        private String[] tags;

        public TaggedEditor(Class clazz) {
            this.clazz = clazz;
            curr_Sel = null;

            try {
                getValue = clazz.getMethod("getValue", // NOI18N
                                           new Class[] { String.class });
                getChoices = clazz.getMethod("getChoices", null); // NOI18N
            } catch (NoSuchMethodException e) {
                                // Should not happen
                e.printStackTrace();
            } // end of try-catch
        }

        public String getAsText() {
            return curr_Sel.toString();
        }

        public void setAsText(String string)
            throws IllegalArgumentException {
            if ((string == null) || string.equals("")) { // NOI18N
                throw new IllegalArgumentException();
            }
            else {
                try {
                    curr_Sel = getValue.
                        invoke(null, new Object[]{ string });
                } catch (Exception e) {
                                // Should not happen
                } // end of try-catch
            } // end of else
            
            this.firePropertyChange();
        }

        public void setValue (Object val) {
            if (!clazz.isInstance(val)) {
                throw new IllegalArgumentException();
            }

            curr_Sel = val;
        }

        public Object getValue() {
            return curr_Sel;
        }

        public String getJavaInitializationString() {
            return getAsText();
        }

        public String[] getTags() {
            if (tags == null) {
                TaggedValue[] tagObjs = new TaggedValue[0];
                
                try {
                    tagObjs =
                        (TaggedValue[])getChoices.invoke(null, null);                     
                } catch (Exception e) {
                    e.printStackTrace();
                                // Should not happen
                } // end of try-catch
                
                tags = new String[tagObjs.length];
            
                for (int i = 0; i < tagObjs.length; i++) {
                    tags[i] = tagObjs[i].toString();
                } // end of for (int i = 0; i < tagObjs.length; i++)
            } // end of if (tags == null)
            
            return tags;
        }
    }
