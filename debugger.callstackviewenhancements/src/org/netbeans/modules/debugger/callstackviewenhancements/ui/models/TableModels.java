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
