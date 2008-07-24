/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.java;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.autoproject.spi.Cache;
import org.openide.filesystems.FileUtil;
import org.openide.util.test.TestFileUtils;

public class BuildSnifferTest extends NbTestCase {

    public BuildSnifferTest(String n) {
        super(n);
    }

    private String prefix;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        Cache.clear();
        prefix = getWorkDirPath() + File.separator;
    }

    public void testBasicJavac() throws Exception {
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='s'/>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <javac srcdir='s' destdir='c' source='1.5' classpath='x.jar' includeantruntime='false'/>\n" +
                " </target>\n" +
                "</project>\n");
        runAnt();
        assertEquals(prefix + "s", Cache.get(prefix + "s" + JavaCacheConstants.SOURCE));
        assertEquals(prefix + "c", Cache.get(prefix + "s" + JavaCacheConstants.BINARY));
        assertEquals(prefix + "x.jar", Cache.get(prefix + "s" + JavaCacheConstants.CLASSPATH));
        assertEquals("1.5", Cache.get(prefix + "s" + JavaCacheConstants.SOURCE_LEVEL));
    }

    public void testSourceRootCompiledMultiply() throws Exception {
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='s'/>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <javac srcdir='s' destdir='c' classpath='x.jar' includeantruntime='false'/>\n" +
                "  <javac srcdir='s' destdir='c' classpath='y.jar' includeantruntime='false'/>\n" +
                "  <javac srcdir='s' destdir='c' classpath='x.jar' includeantruntime='false'/>\n" +
                " </target>\n" +
                "</project>\n");
        runAnt();
        assertEquals(prefix + "s", Cache.get(prefix + "s" + JavaCacheConstants.SOURCE));
        assertEquals(prefix + "c", Cache.get(prefix + "s" + JavaCacheConstants.BINARY));
        assertEquals(prefix + "x.jar" + File.pathSeparator + prefix + "y.jar", Cache.get(prefix + "s" + JavaCacheConstants.CLASSPATH));
    }

    public void testDestDirUsedMultiply() throws Exception { // #137861
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='s1'/>\n" +
                "  <mkdir dir='s2'/>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <javac srcdir='s1' destdir='c' classpath='x.jar' includeantruntime='false'/>\n" +
                "  <javac srcdir='s2' destdir='c' classpath='x.jar:y.jar' includeantruntime='false'/>\n" +
                " </target>\n" +
                "</project>\n");
        runAnt();
        assertEquals(prefix + "s1" + File.pathSeparator + prefix + "s2", Cache.get(prefix + "s1" + JavaCacheConstants.SOURCE));
        assertEquals(prefix + "s1" + File.pathSeparator + prefix + "s2", Cache.get(prefix + "s2" + JavaCacheConstants.SOURCE));
        assertEquals(prefix + "c", Cache.get(prefix + "s1" + JavaCacheConstants.BINARY));
        assertEquals(prefix + "c", Cache.get(prefix + "s2" + JavaCacheConstants.BINARY));
        assertEquals(prefix + "x.jar" + File.pathSeparator + prefix + "y.jar", Cache.get(prefix + "s1" + JavaCacheConstants.CLASSPATH));
        assertEquals(prefix + "x.jar" + File.pathSeparator + prefix + "y.jar", Cache.get(prefix + "s2" + JavaCacheConstants.CLASSPATH));
    }

    public void testComplexClasspath() throws Exception {
        File lib = new File(getWorkDir(), "lib");
        lib.mkdir();
        for (String jar : new String[] {"b", "ax", "ay", "aw", "c"}) {
            TestFileUtils.writeZipFile(new File(lib, jar + ".jar"), "META-INF/MANIFEST.MF:Manifest-Version: 1.0\n\n");
        }
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='s'/>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <path id='p1'><pathelement location='from-p1.jar'/></path>\n" +
                "  <path id='p2'><pathelement location='from-p2.jar'/></path>\n" +
                "  <javac srcdir='s' destdir='c' classpath='direct1.jar:direct2.jar' classpathref='p1' includeantruntime='false'>\n" +
                "   <classpath>\n" +
                "    <path refid='p2'/>\n" +
                "    <pathelement location='pe-loc.jar'/>\n" +
                "    <pathelement path='pe-path-1.jar:pe-path-2.jar'/>\n" +
                "    <path>\n" +
                "     <fileset dir='lib' includes='a*.jar' excludes='*x.jar'>\n" +
                "       <include name='b*.jar'/>\n" +
                "       <exclude name='*y.jar'/>\n" +
                "     </fileset>\n" +
                "    </path>\n" +
                "    <dirset dir='c'/>\n" +
                "   </classpath>\n" +
                "  </javac>\n" +
                " </target>\n" +
                "</project>\n");
        runAnt();
        SortedSet<String> cp = new TreeSet<String>();
        for (String entry : new String[] {
            "direct1.jar", "direct2.jar", "from-p1.jar", "from-p2.jar",
            "pe-loc.jar", "pe-path-1.jar", "pe-path-2.jar",
            "lib/aw.jar", "lib/b.jar", "c",
        }) {
            cp.add(prefix + entry);
        }
        assertEquals(cp.toString(),
                new TreeSet<String>(Arrays.asList(Cache.get(prefix + "s" + JavaCacheConstants.CLASSPATH).split(File.pathSeparator))).toString());
    }

    public void testAntRuntimeCP() throws Exception {
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='s'/>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <javac srcdir='s' destdir='c' source='1.5' classpath='x.jar'/>\n" +
                " </target>\n" +
                "</project>\n");
        runAnt();
        String cp = Cache.get(prefix + "s" + JavaCacheConstants.CLASSPATH);
        assertTrue(cp, cp.contains(prefix + "x.jar"));
        assertTrue(cp, cp.contains(System.getProperty("java.class.path")));
        // can also contain tools.jar
    }

    public void testMistakenSourceDir() throws Exception {
        write("build.xml",
                "<project default='c'>\n" +
                " <target name='c'>\n" +
                "  <mkdir dir='c'/>\n" +
                "  <javac srcdir='s/pkg' destdir='c' source='1.5' includeantruntime='false'/>\n" +
                " </target>\n" +
                "</project>\n");
        write("s/pkg/Clazz.java", "// my root is s!\npackage pkg;\npublic class Clazz {}\n");
        runAnt();
        assertEquals(prefix + "s", Cache.get(prefix + "s" + JavaCacheConstants.SOURCE));
    }

    private void write(String file, String body) throws IOException {
        TestFileUtils.writeFile(new File(getWorkDir(), file), body);
    }

    private void runAnt() throws IOException {
        int res = ActionUtils.runTarget(FileUtil.toFileObject(new File(getWorkDir(), "build.xml")), null, null).result();
        assertEquals("Ant script failed", 0, res);
    }

}
