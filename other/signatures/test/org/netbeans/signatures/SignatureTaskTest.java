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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import junit.framework.TestCase;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Jesse Glick
 */
public class SignatureTaskTest extends TestCase {
    
    public SignatureTaskTest(String n) {
        super(n);
    }
    
    private File antJar;
    private File antLauncherJar;
    private File antModuleJar;
    private File nbdev;
    
    protected void setUp() throws Exception {
        super.setUp();
        antJar = new File(Class.forName("org.apache.tools.ant.Project").getProtectionDomain().getCodeSource().getLocation().toURI());
        assertTrue(antJar.getAbsolutePath(), antJar.isFile());
        antLauncherJar = new File(antJar.getParentFile(), "ant-launcher.jar");
        assertTrue(antLauncherJar.getAbsolutePath(), antLauncherJar.isFile());
        nbdev = new File(antJar.getParentFile().getParentFile().getParentFile().getParentFile(), "nbbuild/netbeans");
        assertTrue(nbdev.isDirectory());
        antModuleJar = new File(nbdev, "ide8/modules/org-apache-tools-ant-module.jar");
        assertTrue(antModuleJar.getAbsolutePath(), antModuleJar.isFile());
    }
    
    public void testExecuteAnt() throws Exception {
        Project p = new Project();
        p.addBuildListener(new Listener());
        SignatureTask task = new SignatureTask();
        task.setProject(p);
        File out = File.createTempFile("signatures", ".java");
        task.setOut(out);
        FileSet fs = new FileSet();
        fs.setProject(p);
        fs.setDir(antJar.getParentFile());
        fs.setIncludes(antJar.getName());
        task.addFileSet(fs);
        fs = new FileSet();
        fs.setProject(p);
        fs.setDir(antLauncherJar.getParentFile());
        fs.setIncludes(antLauncherJar.getName());
        task.addFileSet(fs);
        task.execute();
        assertTrue(out.isFile());
        Reader r = new FileReader(out);
        try {
            BufferedReader b = new BufferedReader(r);
            while (true) {
                String l = b.readLine();
                assertNotNull("found matching line in " + out, l);
                if (l.equals("{Class _ = org.apache.tools.ant.Task.class;}")) {
                    break;
                }
            }
        } finally {
            r.close();
        }
    }
    
    public void testExecuteNB() throws Exception {
        Project p = new Project();
        p.addBuildListener(new Listener());
        SignatureTask task = new SignatureTask();
        task.setProject(p);
        File out = File.createTempFile("signatures", ".java");
        task.setOut(out);
        FileSet fs = new FileSet();
        fs.setProject(p);
        fs.setDir(nbdev);
        fs.setIncludes("*/modules/*.jar,*/lib/*.jar,*/core/*.jar");
        task.addFileSet(fs);
        task.execute();
        assertTrue(out.isFile());
        Reader r = new FileReader(out);
        try {
            BufferedReader b = new BufferedReader(r);
            while (true) {
                String l = b.readLine();
                assertNotNull("found matching line in " + out, l);
                if (l.equals("{Class _ = javax.help.HelpSet.class;}")) {
                    break;
                }
            }
        } finally {
            r.close();
        }
    }
    
    private static final class Listener implements BuildListener {
        
        Listener() {}
        
        public void buildStarted(BuildEvent event) {}

        public void buildFinished(BuildEvent event) {}

        public void targetStarted(BuildEvent event) {}

        public void targetFinished(BuildEvent event) {}

        public void taskStarted(BuildEvent event) {}

        public void taskFinished(BuildEvent event) {}

        public void messageLogged(BuildEvent event) {
            if (event.getPriority() <= Project.MSG_INFO) {
                System.err.println(event.getMessage());
            }
        }
        
    }

}
