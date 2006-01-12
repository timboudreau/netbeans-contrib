/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
