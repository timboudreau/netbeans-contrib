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

package org.netbeans.core.registry.oldformats;

import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.SharedClassObject;
import org.openide.util.Utilities;

import java.io.*;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author copy&pasted from core/settings
 */
public class InstanceUtils {
    private InstanceUtils() {}

    public static Object newValue(String val) throws ClassNotFoundException {
        val =  Utilities.translate(val);
        try {
            Class c = findClass(val);
            if (SharedClassObject.class.isAssignableFrom(c)) {
                Object o = SharedClassObject.findObject(c, false);
                if (null != o) {
                    // instance already exists -> reset it to defaults
                    try {
                        Method method = SharedClassObject.class.getDeclaredMethod("reset", new Class[0]); // NOI18N
                        method.setAccessible(true);
                        method.invoke(o, new Object[0]);
                    } catch (Exception e) {
                        Logger.getLogger(InstanceUtils.class.getName()).log(
                            Level.WARNING, "", e);
                    }
                } else {
                    o = SharedClassObject.findObject(c, true);
                }
                return o;
            } else {
                return c.newInstance();
            }
        } catch (Exception e) {
            ClassNotFoundException e2 = new ClassNotFoundException("Cannot instantiate class "+val);
            e2.initCause(e);
            throw e2;
        }
    }

    public static Object serialValue(String val) throws ClassNotFoundException, IOException {
        if ((val == null) ||(val.length() == 0)) {
            return null;
        }

        byte[] bytes = new byte[val.length()/2];
        int tempI;
        int count = 0;
        try {
            for (int i = 0; i < val.length(); i++) {
                if (Character.isWhitespace(val.charAt(i))) {
                    continue;
                }
                tempI = Integer.parseInt(val.substring(i,i+2),16);
                if (tempI > 127) {
                    tempI -=256;
                }
                bytes[count++] = (byte) tempI;
                i++;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            IOException e2 = new IOException("Cannot read value of <serialvalue> attribute from file XXXX");
            e2.initCause(e);
            throw e2;
        }
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes, 0, count);
        try {
            ObjectInputStream ois = new SpecialObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            IOException e2 = new IOException("Cannot read value of <serialvalue> attribute from file XXXXX");
            e2.initCause(e);
            throw e2;
        }
    }
    
    public static Object methodValue(String className, String methodName, FileObject fo) throws ClassNotFoundException, IOException {
        int idx = methodName.lastIndexOf(".");
         Class cls =  findClass(((idx > 0) ? methodName.substring(0,idx) : className));
         methodName = (idx > 0  ) ? methodName.substring(idx+1) : methodName; 
        
        Object[] paramArray = new Object [] {
            new Class[] {FileObject.class},
            new Class[] {}
        };

        Object[] objectsList = new Object [] {
            new Object[] {fo},
            new Object[] {}            
        };

        for (int i = 0; i < paramArray.length; i++) {
            try {
                if (objectsList[i] == null) {
                    continue;
                }
                Method method = cls.getDeclaredMethod(methodName, (Class[])paramArray [i]);
                if (method != null) {
                    method.setAccessible(true);
                    if (objectsList[i] != null) {
                        return method.invoke(null,(Object[])objectsList[i]);
                    }
                }
            } catch (Exception nsmExc) {
                continue; 
            }
        }
        throw new IOException("Cannot instantiate object by method "+className+"."+methodName+". ");
    }

    private static Class findClass(String name) throws ClassNotFoundException {
        ClassLoader c = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
        if (c == null) {
            return Class.forName(name);
        } else {
            return Class.forName(name, true, c);
        }
    }

    
    /** Stream allowing upgrade to a new class inside the origin .settings file.
     * This class cannot extend NBObjectInputStream because it needs to change
     * its behaviour. See readClassDescriptor for more details.
     */
    private static class SpecialObjectInputStream extends java.io.ObjectInputStream {
        
        // copied from NBObjectInputStream
        public SpecialObjectInputStream(InputStream is) throws IOException {
            super(is);
            try {
                enableResolveObject (true);
            } catch (SecurityException ex) {
                throw new IOException (ex.toString ());
            }
        }

        // copied from NBObjectInputStream
        protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
            ClassLoader cl = getNBClassLoader();
            try {
                return Class.forName(v.getName(), false, cl);
            } catch (ClassNotFoundException cnfe) {
                String msg = "Offending classloader: " + cl; // NOI18N
                Logger.getLogger(getClass().getName()).log(Level.FINE, msg);
                throw cnfe;
            }
        }
        
        /** 
         * The difference from NBObjectInputStream.readClassDescriptor is that
         * conversion of the class is done only when old class does not exist.
         * If it exist then conversion is skipped and client can handle it readResolve
         * method. 
         */
        protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            ObjectStreamClass ose = super.readClassDescriptor();

            String name = ose.getName();
            String newN = org.openide.util.Utilities.translate(name);

            if (name == newN) {
                // no translation
                return ose;
            }

            ClassLoader cl = getNBClassLoader();
            try {
                // Try to load OLD class first.
                // If it succeeds return it. The clients must handle it in readResolve().
                Class origCl = Class.forName(name, false, cl);
                return ObjectStreamClass.lookup(origCl);
            } catch (ClassNotFoundException ex) {
                // ignore. no problem.
            }
            
            // load the new class here
            Class clazz = Class.forName(newN, false, cl);
            ObjectStreamClass newOse = ObjectStreamClass.lookup(clazz);

            // #28021 - it is possible that lookup return null. In that case the conversion
            // table contains class which is not Serializable or Externalizable.
            if (newOse == null) {
                throw new java.io.NotSerializableException(newN);
            }
            
            return newOse;
        }
    
        // copied from NBObjectInputStream
        private static ClassLoader getNBClassLoader() {
            ClassLoader c = (ClassLoader) org.openide.util.Lookup.getDefault().lookup(ClassLoader.class);
            return c != null ? c : ClassLoader.getSystemClassLoader();
        }
        
    }
    
}
