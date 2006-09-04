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
