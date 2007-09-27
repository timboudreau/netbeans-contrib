
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
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gdelib.fields;

import com.sun.tthub.gdelib.GDERuntimeException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.text.SimpleDateFormat;
/**
 *
 * @author Hareesh Ravindran
 */
public class ObjectArrayUtil {
    
    /** Creates a new instance of ObjectArrayUtil */
    public ObjectArrayUtil() {}
    
    public static String getObjArrString(Object[] objArr, 
                        String dataType, char delimiter) {
        StringBuffer buffer = new StringBuffer("");        
        if(objArr == null)
            return buffer.toString();
        if(objArr.length < 1)
            return buffer.toString();
        for(int i = 0; i < objArr.length - 1; ++i) {
            buffer.append(getObjStr(objArr[i], dataType));
            buffer.append(delimiter);
        }
        // append the final value
        buffer.append(getObjStr(objArr[objArr.length - 1], dataType));
        return buffer.toString();
    }
    
    private static String getObjStr(Object obj, String dataType) {
        if(obj ==  null)
            return "";
        if(SimpleDataTypes.TYPE_STRING.equals(dataType)) {
            return obj.toString();
        } else if(SimpleDataTypes.TYPE_BOOLEAN_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_BOOLEAN.equals(dataType)) {
            return Boolean.toString(((Boolean)obj).booleanValue());
        } else if (SimpleDataTypes.TYPE_DOUBLE_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_DOUBLE.equals(dataType)) {
            return Double.toString(((Double)obj).doubleValue());
        } else if (SimpleDataTypes.TYPE_FLOAT_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_FLOAT.equals(dataType)) {
            return Float.toString(((Float)obj).floatValue());
        } else if (SimpleDataTypes.TYPE_INTEGER_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_INTEGER.equals(dataType)) {
            return Integer.toString(((Integer)obj).intValue());
        } else if (SimpleDataTypes.TYPE_LONG_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_LONG.equals(dataType)) {
            return Long.toString(((Long)obj).longValue());
        } else if (SimpleDataTypes.TYPE_DATE.equals(dataType)) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            return format.format((Date) obj);
        } 
        return "";
    }
    
    public static Object getFirstNonNullElement(Object[] objArr) {
        for(int i = 0; i < objArr.length; ++i) {
            if(objArr[i] != null)
                return objArr[i];
        }
        return null;
    }
    
    public static String getObjectDataType(Object[] objArr) {
    Object obj = getFirstNonNullElement(objArr);
        return (obj == null) ? null : obj.getClass().getName();
    }
    
    public static Object[] cloneObjectArr(Object[] objArr) 
                                throws CloneNotSupportedException {
        String dataType = getObjectDataType(objArr);
        Object[] newObjArr = new Object[objArr.length];
        for(int i = 0; i < objArr.length; ++i) {
            newObjArr[i] = (dataType != null) ? 
                cloneObject(objArr[i], dataType) : null;
        }
        return newObjArr;
    }
    
    public static Object cloneObject(Object obj, String dataType) 
                                throws CloneNotSupportedException {
        if(obj ==  null)
            return null;
        if(SimpleDataTypes.TYPE_STRING.equals(dataType)) {
            return new String((String)obj);
        } else if(SimpleDataTypes.TYPE_BOOLEAN_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_BOOLEAN.equals(dataType)) {            
            return new Boolean(((Boolean)obj).booleanValue());
        } else if (SimpleDataTypes.TYPE_DOUBLE_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_DOUBLE.equals(dataType)) {
            return new Double(((Double)obj).doubleValue());
        } else if (SimpleDataTypes.TYPE_FLOAT_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_FLOAT.equals(dataType)) {
            return new Float(((Float)obj).floatValue());
        } else if (SimpleDataTypes.TYPE_INTEGER_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_INTEGER.equals(dataType)) {
            return new Integer(((Integer)obj).intValue());
        } else if (SimpleDataTypes.TYPE_LONG_OBJ.equals(dataType) ||
                    SimpleDataTypes.TYPE_LONG.equals(dataType)) {
            return new Long(((Long)obj).longValue());
        } else if (SimpleDataTypes.TYPE_DATE.equals(dataType)) {
            return new Date(((Date) obj).getTime());
        } 
        
        // Check if the data type supports the cloneable interface. Otherwise 
        // throw an exception. If the object supports reflection, invoke the
        // clone method using java reflection and throw any errors obtained on
        // cloning the object.
        
        Class cls = obj.getClass();
        if(Cloneable.class.isAssignableFrom(cls)) {
            Class[] params = new Class[0];
            Object[] paramObjs = new Object[0];
            try {
                Method method = cls.getDeclaredMethod("clone", params);
                
                return method.invoke(obj, paramObjs);
            } catch(NoSuchMethodException ex) {
                throw new GDERuntimeException("Failed to find the method " +
                        "clone() in the object.", ex);
            } catch(IllegalAccessException ex){
                throw new GDERuntimeException("Failed to execute the method " +
                        "clone() in the object.", ex);
            } catch(InvocationTargetException ex) {
                throw new GDERuntimeException("The clone method threw an " +
                        "execption on the user defined object", ex);
            }
        } else {
            throw new CloneNotSupportedException("The specified object does not " +
                    "implement the Cloneable interface.");
        }
    }
}
