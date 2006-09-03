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

package org.netbeans.signatures;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
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
    
    public static Collection<String> findTopLevelClasses(boolean publicPackagesOnly, File... cp) throws IOException {
        SortedSet<String> classes = new TreeSet<String>();
        for (File jar : cp) {
            if (!jar.isFile()) {
                throw new IllegalArgumentException("XXX directory CP entries not yet supported: " + jar);
            }
            JarFile jf = new JarFile(jar);
            try {
                String[] pubpkgs = null;
                if (publicPackagesOnly) {
                    Manifest mf = jf.getManifest();
                    String pp = mf.getMainAttributes().getValue("OpenIDE-Module-Public-Packages");
                    if (pp != null) {
                        pp = pp.trim();
                        if (pp.equals("-")) {
                            continue;
                        }
                        pubpkgs = pp.split(" *[, ] *");
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
        return classes;
    }

}
