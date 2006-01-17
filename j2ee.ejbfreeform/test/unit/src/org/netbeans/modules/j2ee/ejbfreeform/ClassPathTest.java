/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author jungi
 */
public class ClassPathTest extends TestBase {
    
    public ClassPathTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        setUpProject();
    }
    
    protected void tearDown() throws Exception {
    }
    
    public void testCompileClasspath() {
        ClassPathProvider cpp = (ClassPathProvider) ejbFF.getLookup().lookup(ClassPathProvider.class);
        FileObject prjDir = ejbFF.getProjectDirectory();
        FileObject fo = prjDir.getFileObject("src/java");
        ClassPath cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertEquals("one entry for src/java", cp.entries().size(), 1);
        assertEquals("one root for src/java", cp.getRoots().length, 1);
        fo = prjDir.getFileObject("src/ws");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertEquals("no entry for src/ws", cp.entries().size(), 0);
        assertEquals("one root for src/ws", cp.getRoots().length, 0);
        fo = prjDir.getFileObject("src/beans");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertEquals("no entry for src/beans", cp.entries().size(), 0);
        assertEquals("one root for src/beans", cp.getRoots().length, 0);
        fo = prjDir.getFileObject("test");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertEquals("one entry for test", cp.entries().size(), 1);
        assertEquals("one root for test", cp.getRoots().length, 1);
        fo = prjDir.getFileObject("resources");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertNull("null cp for resources", cp);
        fo = prjDir.getFileObject("conf");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertNull("no entry for conf", cp);
    }
    
    public void testSourcePath() throws Exception {
        ClassPathProvider cpp = (ClassPathProvider) ejbFF.getLookup().lookup(ClassPathProvider.class);
        FileObject prjDir = ejbFF.getProjectDirectory();
        FileObject fo = prjDir.getFileObject("src/java");
        ClassPath cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertEquals("one entry for src/java", cp.entries().size(), 1);
        assertEquals("one root for src/java", cp.getRoots().length, 1);
        fo = prjDir.getFileObject("src/ws");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertEquals("no entry for src/ws", cp.entries().size(), 1);
        assertEquals("one root for src/ws", cp.getRoots().length, 1);
        fo = prjDir.getFileObject("src/beans");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertEquals("no entry for src/beans", cp.entries().size(), 1);
        assertEquals("one root for src/beans", cp.getRoots().length, 1);
        fo = prjDir.getFileObject("test");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertEquals("one entry for test", cp.entries().size(), 1);
        assertEquals("one root for test", cp.getRoots().length, 1);
        fo = prjDir.getFileObject("resources");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertNull("null cp for resources", cp);
        fo = prjDir.getFileObject("conf");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertNull("no entry for conf", cp);
    }
}
