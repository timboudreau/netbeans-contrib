
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

import com.sun.tthub.gdelib.GDEException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Hareesh Ravindran
 */
public final class GenFieldUtil {
    
    /**
     * Creates a new instance of GenFieldUtil
     */
    public GenFieldUtil() {}
    
    /**
     * This function returns FieldMetaData objects representing all the getter 
     * methods within the class. The 'get' prefix of the method is eliminated 
     * while retrieving the methods. 
     *
     * @return The Collection of FieldMetaData objects representing the 
     *      object
     */    
    public static Collection getProperties(FieldMetaData metaData,
                    ClassLoader classLoader) throws GDEException {
        
        if(metaData.getFieldDataTypeNature() 
                        == DataTypeNature.NATURE_SIMPLE) {
            throw new GDEException("The getProperties() cannot be called on" +
                    "a field meata data for a simple data type.");
        }                
        return getProperties(metaData.getFieldDataType(), classLoader);
    }    
    
    /**
     * Loads the specified class using the class loader. If the class loader
     * is able to load the class, it returns the class object. Otherwise it
     * throws a GDEException. The Class loader should already have loaded
     * the class before this method is invoked.
     */
    public static Class loadClass(String className, 
                        ClassLoader classLoader) throws GDEException {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new GDEException("The specified class '" + 
                    className + "' cannot be loaded by the class loader.", ex);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new GDEException("The specified class '" +
                    className + "' cannot be loaded by the class loader", ex);
        }                
    }
    
    public static Collection getProperties(String className, 
                        ClassLoader classLoader) throws GDEException {
        Class cls = loadClass(className, classLoader);
        Method[] methods = cls.getDeclaredMethods();
        Collection coll = new ArrayList();
        for(int i = 0; i < methods.length; ++i) {
            String methodName = methods[i].getName();
            // if the method name starts with a 'get', add the name of the method
            // after removing the 'get' prefix to the collection to be returned.
            if(methodName.startsWith("get")) {
                Class returnType = methods[i].getReturnType();
                FieldMetaData data = new FieldMetaData(
                        methodName.substring(3), returnType.getName());
                coll.add(data);
            }
        }
        return coll;        
    }
    
  /**
     * This is a recursive function that generates the FieldInfo map for the
     * field meta data represented by this class. The method will check if the 
     * data type of the field is of 'SIMPLE' nature, if so, it creates a default
     * SimpleEntryFieldDisplayInfo object using the 
     * createDefaultSimpleFieldDisplayInfo() method. If it finds that the field 
     * is a complex one, it will iterate through each field of the complex data 
     * structure and will use the createDefaultComplexEntryFieldInfo() method on
     * each fieldMeta data encountered, to create a ComplexEntryFieldDisplayInfo.
     *
     * The special cases of a complex data type containing a reference to itself,
     * is not handled in this fucntion, currently.
     *
     * @return The map containing the field names of the complex data object as
     *      keys and the FieldInfo objects for the corresponding fields as 
     *      values.
     * @throws GDEException if this method is invoked with a FieldMeta data for
     *      a simple data type.
     */    
    public static Map getDefaultFieldInfoMap(FieldMetaData metaData, 
                        ClassLoader classLoader) throws GDEException {        
        if(metaData.getFieldDataTypeNature() 
                        == DataTypeNature.NATURE_SIMPLE) {
            throw new GDEException("The getFieldInfoMap() cannot be called on " +
                    "a field meata data for a simple data type. " +
                    " Current FieldMetaData is: [" + metaData + "]");
        }                
        Collection fields = getProperties(metaData, classLoader);
        Map  map = new HashMap(fields.size());                
        for(Iterator it = fields.iterator(); it.hasNext(); ){
            
            FieldMetaData innerMetaData = (FieldMetaData) it.next();
            
            System.out.println(innerMetaData.getFieldName()+"-"+innerMetaData.getFieldDataTypeNature()+"\n");
            
            if(innerMetaData.getFieldDataTypeNature() == 
                                DataTypeNature.NATURE_SIMPLE) {
                FieldInfo info = new FieldInfo(innerMetaData, 
                        new FieldMetaDataProcessor(innerMetaData, classLoader
                        ).createDefaultSimpleEntryFieldDisplayInfo());
                map.put(innerMetaData.getFieldName(), info);            
            } else {
                FieldMetaDataProcessor processor = 
                        new FieldMetaDataProcessor(innerMetaData, classLoader);
                FieldInfo info = new FieldInfo(innerMetaData, new 
                        FieldMetaDataProcessor(innerMetaData, classLoader
                        ).createDefaultComplexEntryFieldDisplayInfo());
                map.put(innerMetaData.getFieldName(), info);
            }
        }            
        return map;
    }    
    
    public static Map getDefaultFieldInfoMap(String className, 
                        ClassLoader classLoader) throws GDEException{
        FieldMetaData metaData = new FieldMetaData(className, className);
        return getDefaultFieldInfoMap(metaData, classLoader);        
    }    
    
    
}
