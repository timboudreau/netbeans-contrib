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

package org.netbeans.signatures;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds all top-level classes detected in a classpath.
 * @author Jesse Glick
 */
public class ClassScanner {
    
    private ClassScanner() {}
    
    /**
     * Looks for all classes in a classpath.
     * @param cp a set of classpath entries (currently only JARs are supported); must be modifiable if <code>classPathExtensions</code> is set
     * @param publicPackagesOnly if true, check for <code>OpenIDE-Module-Public-Packages</code> and only include classes from matching packages
     * @param classPathExtensions if true, check for <code>Class-Path</code> and traverse extension JARs automatically (additions will be made to <code>cp</code>)
     */
    public static Collection<String> findTopLevelClasses(Set<File> cp, boolean publicPackagesOnly, boolean classPathExtensions) throws IOException {
        SortedSet<String> classes = new TreeSet<String>();
        for (File jar : new ArrayList<File>(cp)) {
            traverse(jar, classes, cp, publicPackagesOnly, classPathExtensions, null);
        }
        return classes;
    }
    
    private static void traverse(File jar, Set<String> classes, Set<File> knownJars, boolean publicPackagesOnly, boolean classPathExtensions, String[] pubpkgs) throws IOException {
        if (!jar.isFile()) {
            throw new IllegalArgumentException("XXX directory CP entries not yet supported: " + jar);
        }
        JarFile jf = new JarFile(jar);
        try {
            if (pubpkgs == null && publicPackagesOnly) {
                Manifest mf = jf.getManifest();
                if (mf != null) {
                    String pp = mf.getMainAttributes().getValue("OpenIDE-Module-Public-Packages");
                    if (pp != null) {
                        pp = pp.trim();
                        if (pp.equals("-")) {
                            return;
                        }
                        pubpkgs = pp.split(" *[, ] *");
                    }
                }
            }
            if (classPathExtensions) {
                Manifest mf = jf.getManifest();
                if (mf != null) {
                    String ext = mf.getMainAttributes().getValue("Class-Path");
                    if (ext != null) {
                        for (String reluri : ext.trim().split("[ ,]+")) {
                            File extjar = new File(jar.toURI().resolve(reluri));
                            if (extjar.exists() && knownJars.add(extjar)) {
                                traverse(extjar, classes, knownJars, publicPackagesOnly, classPathExtensions, pubpkgs);
                            }
                        }
                    }
                }
            }
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String path = entry.getName();
                if (!path.endsWith(".class")/* || path.contains("$")*/) {
                    continue;
                }
                String pkg = path.contains("/") ? path.replaceFirst("/[^/]+$", "").replace('/', '.') : "";
                if (pubpkgs != null) {
                    boolean included = false;
                    for (String pubpkg : pubpkgs) {
                        Matcher m = Pattern.compile("(.+)\\.(\\*\\*?)").matcher(pubpkg);
                        if (!m.matches()) {
                            throw new IOException("Bad OpenIDE-Module-Public-Packages entry '" + pubpkg + "' in " + jar);
                        }
                        String prefix = m.group(1);
                        if (m.group(2).length() == 1) {
                            included = prefix.equals(pkg);
                        } else {
                            included = prefix.equals(pkg) || pkg.startsWith(prefix + ".");
                        }
                        if (included) {
                            break;
                        }
                    }
                    if (!included) {
                        continue;
                    }
                }
                classes.add(path.replaceAll("\\.class$", "").replace('/', '.'));
            }
        } finally {
            jf.close();
        }
    }
    
}
