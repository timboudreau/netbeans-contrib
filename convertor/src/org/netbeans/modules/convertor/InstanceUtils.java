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

package org.netbeans.modules.convertor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Properties;
import org.netbeans.api.convertor.ConvertorException;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author  David Konecny
 */
public class InstanceUtils {

    private InstanceUtils() {
    }

    public static Object newValue(String val, Properties properties) {
        val =  Utilities.translate(val);
        try {
            Class c = findClass(val);
            if (properties == null) {
                return c.newInstance();
            } else {
                Constructor con = c.getConstructor(new Class[]{Properties.class});
                return con.newInstance(new Object[]{properties});
            }
        } catch (Exception e) {
            ConvertorException e2 = new ConvertorException("Cannot instantiate class "+val); // NOI18N
            ErrorManager.getDefault().annotate(e2, e);
            throw e2;
        }
    }
    
    public static Object methodValue(String val, Properties properties) {
        int sepIdx = val.lastIndexOf('.');
        if (sepIdx == -1) {
            throw new ConvertorException("Cannot call method "+val+", because it is not fully qualified."); // NOI18N
        }
        
        String methodName = val.substring(sepIdx+1);
        String className =  val.substring(0,sepIdx);
        
        try {
            Class cls =  findClass(className);
            if (properties == null) {
                Method method = cls.getMethod(methodName, new Class[]{});
                return method.invoke(null, new Object[]{});
            } else {
                Method method = cls.getMethod(methodName, new Class[]{Properties.class});
                return method.invoke(null, new Object[]{properties});
            }
        } catch (Exception e) {
            ConvertorException e2 = new ConvertorException("Cannot instantiate method "+val); // NOI18N
            ErrorManager.getDefault().annotate(e2, e);
            throw e2;
        }
    }

    public static Class findClass(String name) throws ClassNotFoundException {
        ClassLoader c = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
        if (c == null) {
            return Class.forName(name, true, null);
        } else {
            return Class.forName(name, true, c);
        }
    }

    
}
