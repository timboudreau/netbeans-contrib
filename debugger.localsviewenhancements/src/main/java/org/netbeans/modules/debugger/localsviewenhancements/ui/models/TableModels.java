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

package org.netbeans.modules.debugger.localsviewenhancements.ui.models;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.netbeans.api.debugger.Watch;
import org.netbeans.api.debugger.jpda.Field;
import org.netbeans.api.debugger.jpda.LocalVariable;
import org.netbeans.api.debugger.jpda.ObjectVariable;
import org.netbeans.api.debugger.jpda.This;
import org.netbeans.api.debugger.jpda.Variable;
import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.UnknownTypeException;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class TableModels {
    
    /** Creates a new instance of TableModels */
    public TableModels() {
    }
    
    private static class LocalsViewEnhancementsTableModel implements TableModel {
        public Object getValueAt(Object row, String columnID) throws
                UnknownTypeException {
//          System.out.println("row class= " + row.getClass().getName());
            if (columnID.equals(Constants.LOCALS_MODIFIERS_COLUMN_ID)) {
                if (row instanceof Field) {
                    String modifiersString = "";
                    Class rowClass = row.getClass();
                    java.lang.reflect.Field fieldField = null;
                    
                    while (rowClass != null) {
                        try {
                            fieldField = rowClass.getDeclaredField("field");
                        } catch (SecurityException ex) {
                            break;
                        } catch (NoSuchFieldException ex) {
                        }
                        rowClass = rowClass.getSuperclass();
                    }
                    
                    if (fieldField != null) {
                        fieldField.setAccessible(true);
                        try {
                            Object fieldFieldValue = fieldField.get(row);
                            Class fieldFieldValueClass = fieldFieldValue.getClass();
                            try {
                                Method method = fieldFieldValueClass.getMethod("modifiers", new Class[0]);
                                try {
                                    Integer modifiersInteger = (Integer) method.invoke(fieldFieldValue, new Object[0]);
                                    modifiersString = Modifier.toString(modifiersInteger.intValue()) + " ";
                                } catch (IllegalArgumentException ex) {
                                } catch (InvocationTargetException ex) {
                                } catch (IllegalAccessException ex) {
                                }
                            } catch (SecurityException ex) {
                            } catch (NoSuchMethodException ex) {
                            }
                        } catch (IllegalArgumentException ex) {
                        } catch (IllegalAccessException ex) {
                        }
                    }
                    return modifiersString;
                }
                return "";
            } else if (columnID.equals(Constants.LOCALS_DECLARED_TYPE_COLUMN_ID)) {
                if (row instanceof LocalVariable) {
                    return ((LocalVariable) row).getDeclaredType();
                } else if (row instanceof This) {
                    return ((This) row).getType();
                }  else if (row instanceof Field) {
                    return ((Field) row).getDeclaredType();
                }
                return "";
            } else if (columnID.equals(Constants.LOCALS_DECLARED_IN_COLUMN_ID)) {
                if (row instanceof Field) {                    
                    return ((Field) row).getClassName();
                }
                return "";
            } else if (columnID.equals(Constants.LOCALS_OBJECT_ID_COLUMN_ID)) {
                 if (row instanceof ObjectVariable) {
                     long uniqueId = ((ObjectVariable) row).getUniqueID();
                     if (uniqueId > 0) {
                        return "#" + String.valueOf(uniqueId);
                     }
                }
                return "";
            }
            
            throw new UnknownTypeException(row);
            //return "";
        }
        
        public boolean isReadOnly(Object row, String columnID) throws
                UnknownTypeException {
            return true;
        }
        
        public void setValueAt(Object row, String columnID, Object value)
        throws UnknownTypeException {
            throw new UnknownTypeException(row);
        }
        
        /**
         * Registers given listener.
         *
         * @param l the listener to add
         */
        public void addModelListener(ModelListener l) {
        }
        
        /**
         * Unregisters given listener.
         *
         * @param l the listener to remove
         */
        public void removeModelListener(ModelListener l) {
        }
    }
    
    public static TableModel createLocalsViewEnhancementsTableModel() {
        return new LocalsViewEnhancementsTableModel();
    }
}
