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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.rules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import org.netbeans.api.jackpot.Query;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.spi.jackpot.ScriptParsingException;
import org.openide.filesystems.FileObject;
import org.openide.modules.InstalledFileLocator;

/**
 * Given a rule script, creates a Query
 * 
 * @author Tom Ball
 */
class QueryFactory {
    /**
     * Returns a Query class instance from a specified rule file.
     */
    @SuppressWarnings("unchecked")
    public static Query getQuery(FileObject fobj, String queryDescription) throws Exception {
        String classpath = jackpotJarsPath();
        ClassLoader jscl = JavaSource.class.getClassLoader();
        ClassLoader cl = new JackpotClassLoader(jscl);
        Class cls = Class.forName("org.netbeans.modules.jackpot.rules.parser.RuleTransformerFactory", true, cl);
        Method m = cls.getMethod("getQuery", FileObject.class, String.class);
        try {
            Object q = m.invoke(null, fobj, classpath);
            return (Query)q;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof ScriptParsingException)
                throw (ScriptParsingException)e.getCause();
            else
                throw e;
        }
    }
    
    private static String jackpotJarsPath() throws IOException {
        InstalledFileLocator locator = InstalledFileLocator.getDefault();
        File jar = locator.locate("modules/org-netbeans-modules-jackpot-rules.jar", // NOI18N
                                  "org.netbeans.modules.jackpot.rules", false);     // NOI18N
        if (jar == null)
            return("");  // InstalledFileLocator not initialized, so rely on classpath instead.
        StringBuffer sb = new StringBuffer();
        sb.append(jar.getCanonicalPath());
        sb.append(File.pathSeparator);
        jar = locator.locate("modules/org-netbeans-modules-jackpot.jar",            // NOI18N
                                  "org.netbeans.modules.jackpot", false);           // NOI18N
        if (jar == null)
            throw new FileNotFoundException("modules/org-netbeans-modules-java-source.jar");
        sb.append(jar.getCanonicalPath());
        sb.append(File.pathSeparator);
        jar = locator.locate("modules/org-netbeans-modules-java-source.jar",        // NOI18N
                                  "org.netbeans.modules.java.source", false);       // NOI18N
        if (jar == null)
            throw new FileNotFoundException("modules/org-netbeans-modules-java-source.jar");
        sb.append(jar.getCanonicalPath());
        sb.append(File.pathSeparator);
        jar = locator.locate("modules/ext/javac-api.jar",                          // NOI18N
                             "org.netbeans.libs.javacapi", false);                 // NOI18N
        if (jar == null)
            throw new FileNotFoundException("modules/ext/javac-api.jar");
        sb.append(jar.getCanonicalPath());
        sb.append(File.pathSeparator);
        jar = locator.locate("modules/ext/javac-impl.jar",                         // NOI18N
                             "org.netbeans.libs.javacimpl", false);                // NOI18N
        if (jar == null)
            throw new FileNotFoundException("modules/ext/javac-impl.jar");
        sb.append(jar.getCanonicalPath());
        return sb.toString();
    }
  
    private static class JackpotClassLoader extends ClassLoader {
        private final PermissionCollection permissions = new Permissions();
        ClassLoader altLoader = QueryFactory.class.getClassLoader();

        public JackpotClassLoader(ClassLoader parent) {
            super(parent);
            permissions.add(new AllPermission());
        }

        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return altLoader.loadClass(name);
        }

        protected PermissionCollection getPermissions(CodeSource codesource) {
            return permissions;
        }    
    }
}
