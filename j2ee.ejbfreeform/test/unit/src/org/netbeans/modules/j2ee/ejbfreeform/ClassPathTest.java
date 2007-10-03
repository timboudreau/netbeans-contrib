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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
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
        assertNotNull("Must have built ant/freeform unit tests first, INCLUDING copying non-*.java resources to the classes build directory",
            ClassPathTest.class.getResource("/META-INF/services/org.openide.modules.InstalledFileLocator"));
        assertNotNull("Must have built ant/freeform unit tests first, INCLUDING copying non-*.java resources to the classes build directory",
            ClassPathTest.class.getResource("/META-INF/services/org.netbeans.modules.java.platform.JavaPlatformProvider"));
        setUpProject();
    }
    
    protected void tearDown() throws Exception {
    }
    
    public void testCompileClasspath() {
        ClassPathProvider cpp = (ClassPathProvider) ejbFF.getLookup().lookup(ClassPathProvider.class);
        FileObject prjDir = ejbFF.getProjectDirectory();
        FileObject fo = prjDir.getFileObject("src/java");
        ClassPath cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertEquals("one entry for src/java", 1, cp.entries().size());
        assertEquals("one root for src/java", 1, cp.getRoots().length);
        assertNotNull("found IAction in " + cp, cp.findResource("org/netbeans/modules/test/j2ee/ejbfreeform/IAction.class"));
        fo = prjDir.getFileObject("src/ws");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertEquals("no entry for src/ws", 0, cp.entries().size());
        assertEquals("one root for src/ws", 0, cp.getRoots().length);
        fo = prjDir.getFileObject("src/beans");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertEquals("no entry for src/beans", 0, cp.entries().size());
        assertEquals("one root for src/beans", 0, cp.getRoots().length);
        fo = prjDir.getFileObject("test");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertEquals("one entry for test", 1, cp.entries().size());
        assertEquals("one root for test", 1, cp.getRoots().length);
        assertNotNull("found IAction in " + cp, cp.findResource("org/netbeans/modules/test/j2ee/ejbfreeform/IAction.class"));
        fo = prjDir.getFileObject("resources");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertNull("null cp for resources", cp);
        fo = prjDir.getFileObject("conf");
        cp = cpp.findClassPath(fo, ClassPath.COMPILE);
        assertNull("no entry for conf", cp);
    }
    
    public void testSourcePath() {
        ClassPathProvider cpp = (ClassPathProvider) ejbFF.getLookup().lookup(ClassPathProvider.class);
        FileObject prjDir = ejbFF.getProjectDirectory();
        FileObject fo = prjDir.getFileObject("src/java");
        ClassPath cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertEquals("one entry for src/java", 1, cp.entries().size());
        assertEquals("one root for src/java", 1, cp.getRoots().length);
        fo = prjDir.getFileObject("src/ws");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertEquals("no entry for src/ws", 1, cp.entries().size());
        assertEquals("one root for src/ws", 1, cp.getRoots().length);
        fo = prjDir.getFileObject("src/beans");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertEquals("no entry for src/beans", 1, cp.entries().size());
        assertEquals("one root for src/beans", 1, cp.getRoots().length);
        fo = prjDir.getFileObject("test");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertEquals("one entry for test", 1, cp.entries().size());
        assertEquals("one root for test", 1, cp.getRoots().length);
        fo = prjDir.getFileObject("resources");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertNull("null cp for resources", cp);
        fo = prjDir.getFileObject("conf");
        cp = cpp.findClassPath(fo, ClassPath.SOURCE);
        assertNull("no entry for conf", cp);
    }
    
    public void testExecutePath() {
        ClassPathProvider cpp = (ClassPathProvider) ejbFF.getLookup().lookup(ClassPathProvider.class);
        FileObject prjDir = ejbFF.getProjectDirectory();
        FileObject fo = prjDir.getFileObject("src/java");
        ClassPath cp = cpp.findClassPath(fo, ClassPath.EXECUTE);
        assertEquals("two entries for src/java", 2, cp.entries().size());
        assertEquals("two roots for src/java", 2, cp.getRoots().length);
        fo = prjDir.getFileObject("src/ws");
        cp = cpp.findClassPath(fo, ClassPath.EXECUTE);
        assertEquals("no entry for src/ws", 1, cp.entries().size());
        assertEquals("one root for src/ws", 1, cp.getRoots().length);
        fo = prjDir.getFileObject("src/beans");
        cp = cpp.findClassPath(fo, ClassPath.EXECUTE);
        assertEquals("no entry for src/beans", 1, cp.entries().size());
        assertEquals("one root for src/beans", 1, cp.getRoots().length);
        fo = prjDir.getFileObject("test");
        cp = cpp.findClassPath(fo, ClassPath.EXECUTE);
        assertEquals("two entries for test", 2, cp.entries().size());
        assertEquals("two roots for test", 2, cp.getRoots().length);
        fo = prjDir.getFileObject("resources");
        cp = cpp.findClassPath(fo, ClassPath.EXECUTE);
        assertNull("null cp for resources", cp);
        fo = prjDir.getFileObject("conf");
        cp = cpp.findClassPath(fo, ClassPath.EXECUTE);
        assertNull("no entry for conf", cp);
    }
    
    protected boolean runInEQ() {
        return false;
    }
    
    public void testBootClasspath() throws Exception {
        FileObject prjDir = ejbFF.getProjectDirectory();
        ClassPath cp = ClassPath.getClassPath(prjDir.getFileObject("src/java"), ClassPath.BOOT);
        assertNotNull("have some BOOT classpath for src/java", cp);
        assertEquals("and it is JDK 1.4", "1.4", specOfBootClasspath(cp));
        ClassPath cp2 = ClassPath.getClassPath(prjDir.getFileObject("src/beans"), ClassPath.BOOT);
        assertNotNull("have some BOOT classpath for src/beans", cp2);
        assertEquals("and it is JDK 1.4", "1.4", specOfBootClasspath(cp2));
        /* Not actually required:
        assertEquals("same BOOT classpath for all files (since use same spec level)", cp, cp2);
         */
        cp = ClassPath.getClassPath(buildXml, ClassPath.BOOT);
        assertNull("have no BOOT classpath for build.xml", cp);
    }
    
    private static String specOfBootClasspath(ClassPath cp) {
        List/*<ClassPath.Entry>*/ entries = cp.entries();
        if (entries.size() != 1) {
            return null;
        }
        ClassPath.Entry entry = (ClassPath.Entry)entries.get(0);
        String u = entry.getURL().toExternalForm();
        // Cf. DummyJavaPlatformProvider.
        Pattern p = Pattern.compile("jar:file:/c:/java/([0-9.]+)/jre/lib/rt\\.jar!/");
        Matcher m = p.matcher(u);
        if (m.matches()) {
            return m.group(1);
        } else {
            return null;
        }
    }
    
}
