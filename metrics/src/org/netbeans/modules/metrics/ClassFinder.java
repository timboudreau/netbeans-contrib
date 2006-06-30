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

package org.netbeans.modules.metrics;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.openide.filesystems.FileObject;

import java.util.*;

/**
 * Utility method for locating the class files associated with a given Java
 * source file.
 *
 * @author tball
 */
public final class ClassFinder {

    /**
     * Returns the list (set, actually) of classes created by a specified
     * Java source file during compilation.
     */
    public static List /*<FileObject>*/ getCompiledClasses(FileObject f) {
        List classes = new ArrayList();
        ClassPath cp = ClassPath.getClassPath(f, ClassPath.SOURCE);
        String clsName = cp.getResourceName(f, '/', false) + ".class";
        cp = ClassPath.getClassPath(f, ClassPath.EXECUTE);
        if (cp != null) { // true for core classes
            FileObject cls = cp.findResource(clsName);
            if (cls != null) {
                classes.add(cls);
                FileObject pkg = cls.getParent();
                String baseName = cls.getName() + '$';
                FileObject[] children = pkg.getChildren();
                for (int i = 0; i < children.length; i++) {
                    FileObject child = children[i];
                    if (child.getName().startsWith(baseName))
                        classes.add(child);
                }
            }
        }
        return classes;
    }

    /**
     * Find a specified resource on a ClassPath type.
     */
    public static FileObject findResource(String resName, String pathType) {
        Set paths = GlobalPathRegistry.getDefault().getPaths(pathType);
        for (Iterator i = paths.iterator(); i.hasNext();) {
            ClassPath cp = (ClassPath)i.next();
            FileObject fo = cp.findResource(resName);
            if (fo != null)
                return fo;
        }
        return null;
    }

    private ClassFinder() {
        // don't instantiate
    }
}
