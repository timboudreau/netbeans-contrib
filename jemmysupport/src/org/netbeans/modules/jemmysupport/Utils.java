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
