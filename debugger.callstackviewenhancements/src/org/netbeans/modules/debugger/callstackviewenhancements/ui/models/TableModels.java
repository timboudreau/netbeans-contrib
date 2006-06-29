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

package org.netbeans.modules.debugger.callstackviewenhancements.ui.models;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import org.netbeans.api.debugger.Watch;
import org.netbeans.api.debugger.jpda.CallStackFrame;
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
    
    private static class CallStackViewEnhancementsTableModel implements TableModel {
        public Object getValueAt(Object row, String columnID) throws
                UnknownTypeException {
            if (row instanceof CallStackFrame) {
                CallStackFrame callStackFrame = (CallStackFrame) row;
                com.sun.jdi.Method method = Utils.getMethod(callStackFrame);
                if (columnID.equals(Constants.CALL_STACK_FRAME_MODIFIERS_COLUMN_ID)) {
                    if (method != null) {
                        return Modifier.toString(method.modifiers());
                    }
                    return "";
                } else if (columnID.equals(Constants.CALL_STACK_FRAME_METHOD_SIGNATURE_COLUMN_ID)) {
                    if (method != null) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(method.name());
                        stringBuffer.append("(");
                        List arguments = method.argumentTypeNames();
                        int argumentCount = arguments.size();
                        for(int i = 0; i < argumentCount; i++) {
                            String argument = (String) arguments.get(i);
                            if (i > 0) {
                                stringBuffer.append(", ");
                            }
                            stringBuffer.append(argument);
                        }
                        stringBuffer.append("):");
                        stringBuffer.append(method.returnTypeName());
                        return stringBuffer.toString();
                    }
                    return "";
                } else if (columnID.equals(Constants.CALL_STACK_FRAME_DECLARING_CLASS_COLUMN_ID)) {
                    return callStackFrame.getClassName();
                } else if (columnID.equals(Constants.CALL_STACK_FRAME_THIS_CLASS_COLUMN_ID)) {
                    This thisOfCallStackFrame = callStackFrame.getThisVariable();
                    if (thisOfCallStackFrame != null) {
                        return thisOfCallStackFrame.getType();
                    }
                } else if (columnID.equals(Constants.CALL_STACK_FRAME_LOCATION_PATH_COLUMN_ID)) {
                    String location = Utils.getLocation(callStackFrame);
                    if (location != null) {
                        return location;
                    }
                    return "";
                }
            }
            
//            throw new UnknownTypeException(row);
            return "";
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
    
    public static TableModel createCallStackViewEnhancementsTableModel() {
        return new CallStackViewEnhancementsTableModel();
    }
}
