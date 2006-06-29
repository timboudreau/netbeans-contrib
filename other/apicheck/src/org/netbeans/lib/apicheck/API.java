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
package org.netbeans.lib.apicheck;

import org.netbeans.modules.classfile.Access;
import org.netbeans.modules.classfile.ClassFile;

/** Describes a set of APIs.
 *
 * @author Jaroslav Tulach
 */
final class API {
    /** register of all known classes */
    final java.util.Map<String,Class> classes;

    /** Creates a new instance of API */
    private API () {
        classes = new java.util.HashMap<String,Class>();
    }

    public static API readJARFile (java.io.File f) throws java.io.IOException {
        API api = new API ();
        java.util.jar.JarFile jar = new java.util.jar.JarFile (f);
        java.util.Enumeration en = jar.entries ();
        while (en.hasMoreElements ()) {
            java.util.jar.JarEntry e = (java.util.jar.JarEntry)en.nextElement ();
            if (e.getName ().endsWith (".class")) { // NOI18N
                java.io.InputStream is = jar.getInputStream (e);
                ClassFile c = new ClassFile (is);
                is.close ();
                if ((c.getAccess () & (Access.PUBLIC | Access.PROTECTED)) == 0) {
                    continue;
                }
                
                Class clazz = api.new Class (c.getName ().getType ());
                for (Object obj : c.getMethods ()) {
                    org.netbeans.modules.classfile.Method m = (org.netbeans.modules.classfile.Method)obj;
                    Class.Method method = clazz.new Method (m.getTypeSignature ());
                }
            }
        }
        return api;
    }
    
    
    final class Class extends Object {
        private String type;
        final java.util.Map<String,Method> methods;
                
        public Class (String name) {
            classes.put (name, this);
            type = name;
            methods = new java.util.HashMap<String,Method>();
        }
        
        public Class getSuperclass () {
            return null;
        }
        
        public Class[] getInterfaces () {
            return new Class[0];
        }
        
        final class Method extends Object {
            private String type;
            
            public Method (String type) {
                this.type = type;
            }
            
            public String getName() {
                return "";
            }
            public String getReturnType () {
                return "";
            }
        }
        
    } // end of Clazz
    
    
}
