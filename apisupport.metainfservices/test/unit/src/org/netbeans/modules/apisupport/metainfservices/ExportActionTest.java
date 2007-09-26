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
