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
