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
package org.netbeans.lib.apicheck;

import java.util.*;
import junit.framework.*;
import java.io.File;
import org.apache.tools.ant.BuildException;

/**
 *
 * @author Jaroslav Tulach
 */
public class SignatureCheckTest extends org.netbeans.junit.NbTestCase {
    private SignatureCheck gen;
    private SignatureCheck check;
    private org.apache.tools.ant.Project project;

    public SignatureCheckTest (String testName) {
        super (testName);
    }

    protected void setUp () throws java.lang.Exception {
        org.apache.tools.ant.Project p = new org.apache.tools.ant.Project ();
        p.init ();
        
        gen = new SignatureCheck ();
        gen.setProject (p);
        check = new SignatureCheck ();
        check.setProject (p);
        
        project = p;
    }

    protected void tearDown () throws java.lang.Exception {
    }

    public static junit.framework.Test suite () {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(SignatureCheckTest.class);
        
        return suite;
    }

    public void testMissingMethod () throws Exception {
        File sig = File.createTempFile ("sig", ".sig");
        gen.setOutput (sig);
        gen.setJar (genererateJar (new String[] {
            "X.java", "public class X extends Object { public void getX () { } }",
        }));
        gen.execute ();
        
        check.setOutput (File.createTempFile ("out", ".sig"));
        check.setInput (sig);
        check.setJar (genererateJar (new String[] {
            "X.java", "public class X extends Object {  }",
        }));
        check.execute ();
        
        Set s = check.getIncompatibleChanges ();
        if (s.size () != 1) {
            fail ("Wrong incompatible changes:\n" + s);
        }
    }
    
    protected final File genererateJar (String[] files) throws Exception {
        File root = new File (getWorkDir (), "src");

        assertTrue ("Arguments must be paired (name, content)", files.length % 2 == 0);
        for (int i = 0; i < files.length; i += 2) {
            File src = new File (root, files[i]);
            src.getParentFile ().mkdirs ();
            java.io.FileWriter w = new java.io.FileWriter (src);
            w.write (files[i + 1]);
            w.close ();
        }
        
        File build = new File (getWorkDir (), "build/classes");
        build.mkdirs ();
        
        org.apache.tools.ant.taskdefs.Javac javac = new org.apache.tools.ant.taskdefs.Javac ();
        javac.setDestdir (build);
        javac.setSrcdir (new org.apache.tools.ant.types.Path (project, root.getPath ()));
        javac.setProject (project);
        javac.execute ();
        
        File jar = File.createTempFile ("classes", ".jar");
        org.apache.tools.ant.taskdefs.Jar jarIt = new org.apache.tools.ant.taskdefs.Jar ();
        org.apache.tools.ant.types.FileSet fs = new org.apache.tools.ant.types.FileSet ();
        fs.setDir (build);
        jarIt.addFileset (fs);
        jarIt.setProject (project);
        jarIt.setJarfile (jar);
        jarIt.execute ();
        
        return jar;
    }
}
