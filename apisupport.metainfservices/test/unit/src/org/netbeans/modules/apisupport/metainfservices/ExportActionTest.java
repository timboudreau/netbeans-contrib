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
package org.netbeans.modules.apisupport.metainfservices;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Set;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

public class ExportActionTest extends NbTestCase {
    
    public ExportActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        clearWorkDir();
    }

    protected void tearDown() throws Exception {
    }

    public void testGenerateFiles() throws Exception {
        LocalFileSystem lfs = new LocalFileSystem();
        lfs.setRootDirectory(getWorkDir());

        FileSystem fs = lfs;
        FileObject src = FileUtil.createFolder(fs.getRoot(), "src");

        ArrayList<String> files = new ArrayList<String>();
        files.add("META-INF/services/java.lang.Object");
        files.add("META-INF/services/java.lang.Runnable");

        ExportAction.createFiles(R.class.getName(), files, src);

        URLClassLoader loader = new URLClassLoader(new URL[] { src.getURL() }, getClass().getClassLoader());
        Lookup l = Lookups.metaInfServices(loader);

        Runnable r = l.lookup(Runnable.class);

        assertNotNull("Runnable found", r);
        assertEquals("It is my class", R.class, r.getClass());


        ExportAction.createFiles(Q.class.getName(), files, src);

        l = Lookups.metaInfServices(loader);
        Set<Class<? extends Runnable>> all = l.lookupResult(Runnable.class).allClasses();

        assertEquals(2, all.size());
        assertTrue("Q is there", all.contains(Q.class));
        assertTrue("R is there", all.contains(R.class));
    }

    /* XXX would need to be rewritten:
    public void testRemovesAnnotations() throws Exception {
        JavaClassImpl impl = new JavaClassImpl();
        impl.setName("org.tst.Test");
        impl.setSimpleName("Test");

        JavaClassImpl par = new JavaClassImpl();
        par.setName("org.par.Parent <X,Y>");
        par.setSimpleName("Parent <X,Y>");

        impl.setSuperClass(par);

        JavaClassImpl obj = new JavaClassImpl();
        obj.setName("java.lang.Object");
        obj.setSimpleName("Object");

        par.setSuperClass(obj);

        ArrayList<String> names = new ArrayList<String>();
        ExportAction.findInterfaces(impl, names);

        assertEquals("Three", 3, names.size());

        for (String n : names) {
            if (n.indexOf("<") >= 0) {
                fail("Contains wrong char: " + n);
            }
            if (n.endsWith(" ")) {
                fail("Ends with space:[" + n + "]");
            }
        }
    }
     */

    public static class R extends Object implements Runnable {
        public void run() {
        }
    }

    public static class Q implements Runnable {
        public void run() {
        }
    }

}
