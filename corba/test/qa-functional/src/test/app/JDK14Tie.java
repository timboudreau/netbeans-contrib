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

package test.app;

import java.io.IOException;
import java.io.PrintStream;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.NbTestCase;
import org.openide.loaders.DataObject;
import test.app.AppGenerator;
import util.Environment;
import util.NameService;

public class JDK14Tie extends NbTestCase {

    public JDK14Tie(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new JDK14Tie("testJDK14Tie_Create"));
        test.addTest(new JDK14Tie("testJDK14Tie_RunNS"));
        test.addTest(new JDK14Tie("testJDK14Tie_RunFI"));
        test.addTest(new JDK14Tie("testJDK14Tie_RunIO"));
        return test;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    static AppGenerator app = null;
    static boolean done = false;
    
    public void testJDK14Tie_Create() {
        String work;
        try {
            work = Environment.replaceWinSeparator (getWorkDirPath());
        } catch (IOException e) {
            throw new AssertionFailedErrorException (e);
        }
        app = new AppGenerator (getRef (), getLog (), work);
        app.init ("JDK14", true, true, "data/app/jdk14tie/");
        DataObject daoCNS = app.doCNS ();
        app.doModifySource(daoCNS, new String[] {
            "addBefore", "ORB orb = ORB.init(args, null);", "args = new String[] { \"-ORBInitialPort\", \"1052\" };\n",
        });
        app.dumpFile (daoCNS);
        DataObject daoSNS = app.doSNS ();
        app.doModifySource(daoSNS, new String[] {
            "addBefore", "ORB orb = ORB.init(args, null);", "args = new String[] { \"-ORBInitialPort\", \"1052\" };\n",
        });
        app.dumpFile (daoSNS);
        app.dumpFile (app.doCFI ());
        app.dumpFile (app.doSFI ());
        app.dumpFile (app.doCIO ());
        app.dumpFile (app.doSIO ());
        app.doGenerateAndCompile();
        done = true;
    }
    
    public void testJDK14Tie_RunNS () {
        if (!done)
            return;
        NameService ns = new NameService (getRef ());
        ns.start (1052);
        app.setStreams (getRef (), getLog ());
        app.runNS ();
        ns.stop ();
        compareReferenceFiles();
    }
    
    public void testJDK14Tie_RunFI () {
        if (!done)
            return;
        app.setStreams (getRef (), getLog ());
        app.runFI ();
        compareReferenceFiles();
    }
    
    public void testJDK14Tie_RunIO () {
        if (!done)
            return;
        app.setStreams (getRef (), getLog ());
        app.runIO ();
        compareReferenceFiles();
    }
    
}
