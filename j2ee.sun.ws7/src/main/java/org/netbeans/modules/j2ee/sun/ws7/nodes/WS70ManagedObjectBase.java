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

package org.netbeans.modules.j2ee.sun.ws7.nodes;


import org.netbeans.modules.j2ee.sun.ws7.Constants;
import org.netbeans.modules.j2ee.sun.ws7.ui.Util;
import org.netbeans.modules.j2ee.sun.ws7.ide.editors.TaggedValue;
import org.netbeans.modules.j2ee.sun.ws7.ide.editors.TaggedEditor;
import java.awt.Component;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;


import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Base class for the manged objects.
 */
public abstract class WS70ManagedObjectBase implements Node.Cookie, Constants {
   
    public abstract String getDisplayName();
    public abstract Attribute setAttribute(String attribute, Object value) throws Exception;
    public abstract Sheet updateSheet(Sheet sheet);    
    static Map primitivesMap = new HashMap();
    static {
        primitivesMap.put("short", Short.class); // NOI18N
        primitivesMap.put("long", Long.class); // NOI18N
        primitivesMap.put("int", Integer.class); // NOI18N
        primitivesMap.put("boolean", Boolean.class); // NOI18N
        primitivesMap.put("float", Float.class); // NOI18N
        primitivesMap.put("double", Double.class); // NOI18N
        primitivesMap.put("byte", Byte.class); // NOI18N
        primitivesMap.put("char", String.class); // NOI18N
    };        
    
    protected static Class getSupportedType(String type) {
        try {
            Class c = (Class) primitivesMap.get(type);
            
            if (c == null) {
                c = Class.forName(type);
            }
        
            if (c == null) {
                throw new ClassNotFoundException(type);
            }
            
            if (!String.class.isAssignableFrom(c) &&
                !Number.class.isAssignableFrom(c) &&
                !TaggedValue.class.isAssignableFrom(c)) {
                return null;
            }
            
            return c;
        } catch (ClassNotFoundException cnfe) {            
            return null;
        }
    }
    private  static String getLocString(String key) {
        return NbBundle.getMessage(WS70ManagedObjectBase.class, key);
    }
    
    
    

    PropertySupport getStringArrayEditor(final Attribute a,
                                         final AttributeInfo attr,
                                         final String shortDescription,
                                         final Class type,
                                         boolean writable) {
        PropertySupport strArrayEditor = null;

        if (writable) {
            strArrayEditor = createStringArrayWritableProperty(a, attr,
                                                               shortDescription,
                                                               type);
        }
        else {
            strArrayEditor = createStringArrayReadOnlyProperty(a, attr,
                                                               shortDescription,
                                                               type);
        } // end of else
        
        strArrayEditor.setValue("helpID", "AS_RTT_StringArrayEditor"); // NOI18N

        return strArrayEditor;
    }

    String getShortDescription(AttributeInfo attr) {
        String shortDescription = attr.getDescription();

        if (shortDescription == null ||
            shortDescription.trim().equals("")) { // NOI18N
/*            try {
                shortDescription = getLocString("DSC_" + attr.getName()); // NOI18N
            } catch (Exception ex) {
                                // IN case bundle key is missing
                shortDescription = MessageFormat.format(getLocString("Msg_Value"), // NOI18N
                                                        new Object[]{ attr.getName()});
            }
 
 */
            shortDescription = attr.getName();
        }

        return shortDescription;
    }
 
 /*   void setHelpId(Sheet.Set ps) {
        String propSheetHelp = getPropertiesHelpID();

        if (propSheetHelp != null) {
            ps.setValue("helpID", propSheetHelp); // NOI18N
        }
    }
 */   
    PropertySupport createReadOnlyProperty(final Attribute a,
                                           final AttributeInfo attributeInfo,
                                           final String shortDescription) {
        return new PropertySupport.ReadOnly(attributeInfo.getName(),
                                            String.class,
                                            attributeInfo.getName(),
                                            shortDescription) {
                Attribute attribute = a;

                public Object getValue() {
                    Object value = null;

                    try {
                        value = attribute.getValue();

                        if (value != null && !(value instanceof String)) {
                            value = value.toString();
                        }
                    } catch (Exception e) {}

                    return (value == null) ? "" : value; // NOI18N
                }
            };
    }

    PropertySupport createWritableProperty(final Attribute a,
                                           final AttributeInfo attr,
                                           final String shortDescription,
                                           final Class type) {
        return new PropertySupport.ReadWrite(attr.getName(),
                                             type,
                                             attr.getName(),
                                             shortDescription) {
                Attribute attribute = a;

                public Object getValue() {
                    return attribute.getValue();
                }

                public void setValue(Object value) {
                    try {
                        String attributeName =  getName();
                        attribute = setAttribute(getName(),
                                                 value);
                    } catch (Exception e) {
                        Util.showError(e.getLocalizedMessage());
                    }
                }
            };
    }
    
    PropertySupport createStringArrayWritableProperty(final Attribute a,
                                                      final AttributeInfo attr,
                                                      final String shortDescription,
                                                      final Class type) {
        return new PropertySupport.ReadWrite(attr.getName(),
                                             type,
                                             attr.getName(),
                                             shortDescription) {
                Attribute attribute = a;

                public Object getValue() {
                    return attribute.getValue();
                }

                public void setValue(Object value) {
                    try {
                        attribute = setAttribute(getName(),
                                                 value);
                    } catch (Exception e) {
                        Util.showError(e.getLocalizedMessage());
                    }
                }
            };
    }
    
    PropertySupport createStringArrayReadOnlyProperty(final Attribute a,
                                                      final AttributeInfo attr,
                                                      final String shortDescription,
                                                      final Class type) {
        return new PropertySupport.ReadOnly(attr.getName(),
                                            type,
                                            attr.getName(),
                                            shortDescription) {
                Attribute attribute = a;

                public Object getValue() {
                    return attribute.getValue();
                }
            };
    }
    
    
    PropertySupport createTaggedProperty(final Attribute a,
                                         final AttributeInfo attr,
                                         final String shortDescription,
                                         final Class type) {
        return new PropertySupport.ReadWrite(attr.getName(),
                                             type,
                                             attr.getName(),
                                             shortDescription) {
                Attribute attribute = a;

                public Object getValue() {
                    return attribute.getValue();
                }

                public void setValue(Object value) {
                    try {
                        attribute = setAttribute(getName(), value);
                    } catch (Exception e) {
                        Util.showError(e.getLocalizedMessage());
                    }
                }

                public PropertyEditor getPropertyEditor() {
                    return new TaggedEditor(attribute.getValue().getClass());
                }
            };
    }
    public static class Boolean extends TaggedValue {
        private boolean value;
        
        private Boolean(boolean value) {
            this.value = value;
        }
        
        private static final Boolean TRUE =
            new Boolean(true);
        private static final Boolean FALSE =
            new Boolean(false);
        private static final Boolean[] values =
            new Boolean[]{ TRUE, FALSE };
        
        public static TaggedValue getValue(String s) {
            if ("true".equalsIgnoreCase(s) || "on".equalsIgnoreCase(s)) { // NOI18N
                return TRUE;
            } // end of if ("true".equalsIgnoreCase(s) ||
              //     "on".equalsIgnoreCase(s))

            return FALSE;
        }
       public static TaggedValue getValue(java.lang.Boolean b) {
            if (b.toString().equals("true")|| b.toString().equals("on")) { // NOI18N
                return TRUE;
            } 

            return FALSE;
        }
        public static TaggedValue[] getChoices() {
            return values;
        }
        
        public boolean booleanValue() {
            return value;
        }

        public String toString() {
            if (value) {
                return "true";    // NOI18N
            } // end of if (value)
            
            return "false";       // NOI18N
        }
    }            
}
