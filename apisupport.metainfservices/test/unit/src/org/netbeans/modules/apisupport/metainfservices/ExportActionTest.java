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

/**
 *
 * @author jarda
 */
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

    public static class R extends Object implements Runnable {
        public void run() {
        }
    }

    public static class Q implements Runnable {
        public void run() {
        }
    }
}
