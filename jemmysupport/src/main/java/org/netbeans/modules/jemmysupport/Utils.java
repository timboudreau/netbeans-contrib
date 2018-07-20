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

package org.netbeans.modules.jemmysupport;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import org.openide.ErrorManager;

/**
 * Utilities methods.
 *
 * @author Jiri.Skrivanek@sun.com
 */
public class Utils {

    /** Returns NetBeans SystemClassLoader from threads hierarchy. */
    public static ClassLoader getSystemClassLoader() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        ClassLoader systemClassloader = Thread.currentThread().getContextClassLoader();
        while(!systemClassloader.getClass().getName().endsWith("SystemClassLoader")) { // NOI18N
            tg = tg.getParent();
            if(tg == null) {
                ErrorManager.getDefault().notify(new Exception("NetBeans SystemClassLoader not found!")); // NOI18N
            }
            Thread[] list = new Thread[tg.activeCount()];
            tg.enumerate(list);
            systemClassloader = list[0].getContextClassLoader();
        }
        return systemClassloader;
    }
    
    /** Classloder with overriden getPermissions method because it doesn't
     * have sufficient permissions when run from IDE.
     */
    public static class TestClassLoader extends URLClassLoader {
        
        public TestClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
        
        protected PermissionCollection getPermissions(CodeSource codesource) {
            Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            permissions.setReadOnly();
            return permissions;
        }
        
        protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.netbeans.jemmy") || name.startsWith("org.netbeans.jellytools")) { // NOI18N
                //System.out.println("CLASSNAME="+name);
                // Do not proxy to parent!
                Class c = findLoadedClass(name);
                if (c != null) return c;
                c = findClass(name);
                if (resolve) resolveClass(c);
                return c;
            } else {
                return super.loadClass(name, resolve);
            }
        }
        
        /** Just to make it public. Used in BundleLookupAction. */
        public Package[] getPackages() {
            return super.getPackages();
        }
    }
}
