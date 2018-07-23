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

package org.netbeans.modules.tasklist.providers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import org.openide.filesystems.FileObject;
import org.openide.ErrorManager;
import java.lang.reflect.Method;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;


/**
 * Dedicated java source context that is faster than generic one.
 *
 * @author Petr Kuzel
 */
final class JavaSuggestionContext {
    private static boolean lookupAttempted = false;
    private static Method org_netbeans_modules_java_Util_getFileEncoding;
    private static Method org_netbeans_modules_java_Util_getContent;
    
    /**
     * Search methods from o.n.m.java.Util
     *
     * @return true = methods found
     */
    private static boolean findMethods() {
        if (!lookupAttempted) {
            lookupAttempted = true;
            ClassLoader systemClassLoader = 
                (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);
            try {
                Class c = systemClassLoader.
                        loadClass("org.netbeans.modules.java.Util"); // NOI18N
                org_netbeans_modules_java_Util_getFileEncoding = 
                    c.getMethod("getFileEncoding", new Class[] {FileObject.class});
                org_netbeans_modules_java_Util_getContent = 
                    c.getMethod("getContent", new Class[] {FileObject.class, // NOI18N
                        Boolean.TYPE, Boolean.TYPE, String.class});
            } catch (Exception e) {
                System.out.println("org.netbeans.modules.tasklist.providers" + // NOI18N
                        ".JavaSuggestionContext: Methods not found"); // NOI18N
                // ignore
            }
        }
        
        return org_netbeans_modules_java_Util_getFileEncoding != null &&
            org_netbeans_modules_java_Util_getContent != null;
    }
    
    /**
     * @return null if the content cannot be found
     */ 
    static String getContent(FileObject fo) {
        String result = null; // NOI18N
        if (!findMethods()) {
            try {
                char[] buf = new char[1024*64];
                StringBuffer sb = new StringBuffer();
                Reader r = new InputStreamReader(new BufferedInputStream(fo.getInputStream()));
                int len;
                try {
                    while (true) {
                        len = r.read(buf);
                        if (len == -1) break;
                        sb.append(buf, 0, len);
                    }
                    result = sb.toString();
                } finally {
                    r.close();
                }
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }

//            try {
//                DataObject do_ = DataObject.find(fo);
//                result = new SuggestionContext(do_).getCharSequence().toString();
//            } catch (DataObjectNotFoundException e) {
//                ErrorManager.getDefault().notify(e);
//            }
        } else {
            try {
                String encoding = (String) org_netbeans_modules_java_Util_getFileEncoding.
                    invoke(null, new Object[] {fo});

                char[] source = (char[]) org_netbeans_modules_java_Util_getContent.invoke(
                    null, 
                    new Object[] {fo, Boolean.FALSE, Boolean.TRUE, encoding});
                result = new String(source);
            } catch (InvocationTargetException e) {
                ErrorManager.getDefault().notify(e);
            } catch (IllegalArgumentException e) {
                ErrorManager.getDefault().notify(e);
            } catch (IllegalAccessException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        return result;
    }
}
