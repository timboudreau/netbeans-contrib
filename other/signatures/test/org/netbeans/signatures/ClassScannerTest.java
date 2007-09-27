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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

/**
 * @author Jesse Glick
 */
public class ClassScannerTest extends TestCase {
    
    public ClassScannerTest(String n) {
        super(n);
    }
    
    private File antJar;
    private File antModuleJar;
    private File javaHelpJar;
    
    protected void setUp() throws Exception {
        super.setUp();
        antJar = new File(Class.forName("org.apache.tools.ant.Project").getProtectionDomain().getCodeSource().getLocation().toURI());
        assertTrue(antJar.getAbsolutePath(), antJar.isFile());
        antModuleJar = new File(antJar.getParentFile().getParentFile().getParentFile().getParentFile(), "nbbuild/netbeans/ide8/modules/org-apache-tools-ant-module.jar");
        assertTrue(antModuleJar.getAbsolutePath(), antModuleJar.isFile());
        javaHelpJar = new File(antJar.getParentFile().getParentFile().getParentFile().getParentFile(), "nbbuild/netbeans/platform7/modules/org-netbeans-modules-javahelp.jar");
        assertTrue(javaHelpJar.getAbsolutePath(), javaHelpJar.isFile());
    }

    public void testFindPackages() throws Exception {
        Collection<String> antClasses = ClassScanner.findTopLevelClasses(Collections.singleton(antJar), true, true);
        //System.out.println(antClasses);
        assertTrue(antClasses.contains("org.apache.tools.ant.Project"));
        assertTrue(antClasses.contains("org.apache.tools.ant.filters.TokenFilter$Filter"));
        Collection<String> antModuleClasses = ClassScanner.findTopLevelClasses(Collections.singleton(antModuleJar), true, true);
        assertFalse(antModuleClasses.contains("org.apache.tools.ant.module.run.StandardLogger"));
        //System.out.println(antModuleClasses);
        antModuleClasses = ClassScanner.findTopLevelClasses(Collections.singleton(antModuleJar), false, true);
        assertTrue(antModuleClasses.contains("org.apache.tools.ant.module.run.StandardLogger"));
        //System.out.println(antModuleClasses);
    }
    
    public void testClassPathExtensions() throws Exception {
        Set<File> cp = new HashSet<File>(Collections.singleton(javaHelpJar));
        Collection<String> c = ClassScanner.findTopLevelClasses(cp, true, true);
        assertTrue(c.contains("org.netbeans.api.javahelp.Help"));
        assertFalse(c.contains("org.netbeans.modules.javahelp.JavaHelp"));
        assertTrue(c.contains("javax.help.HelpSet"));
        assertTrue(c.contains("javax.help.event.HelpModelEvent"));
        assertFalse(c.contains("com.sun.java.help.impl.Tag"));
        assertEquals(2, cp.size());
        assertTrue(cp.remove(javaHelpJar));
        assertTrue(cp.iterator().next().getName().startsWith("jh"));
        
        cp = new HashSet<File>(Collections.singleton(javaHelpJar));
        c = ClassScanner.findTopLevelClasses(cp, false, true);
        assertTrue(c.contains("org.netbeans.api.javahelp.Help"));
        assertTrue(c.contains("org.netbeans.modules.javahelp.JavaHelp"));
        assertTrue(c.contains("javax.help.HelpSet"));
        assertTrue(c.contains("javax.help.event.HelpModelEvent"));
        assertTrue(c.contains("com.sun.java.help.impl.Tag"));
        assertEquals(2, cp.size());
        
        cp = new HashSet<File>(Collections.singleton(javaHelpJar));
        c = ClassScanner.findTopLevelClasses(cp, true, false);
        assertTrue(c.contains("org.netbeans.api.javahelp.Help"));
        assertFalse(c.contains("org.netbeans.modules.javahelp.JavaHelp"));
        assertFalse(c.contains("javax.help.HelpSet"));
        assertFalse(c.contains("javax.help.event.HelpModelEvent"));
        assertFalse(c.contains("com.sun.java.help.impl.Tag"));
        assertEquals(1, cp.size());
        
        cp = new HashSet<File>(Collections.singleton(javaHelpJar));
        c = ClassScanner.findTopLevelClasses(cp, false, false);
        assertTrue(c.contains("org.netbeans.api.javahelp.Help"));
        assertTrue(c.contains("org.netbeans.modules.javahelp.JavaHelp"));
        assertFalse(c.contains("javax.help.HelpSet"));
        assertFalse(c.contains("javax.help.event.HelpModelEvent"));
        assertFalse(c.contains("com.sun.java.help.impl.Tag"));
        assertEquals(1, cp.size());
    }
    
}
