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

package test.cwfunc;

import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;
import javax.swing.ListModel;
import javax.swing.tree.TreePath;
import junit.framework.Test;
import junit.textui.TestRunner;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.modules.corba.actions.CORBAWizardAction;
import org.netbeans.jellytools.modules.corba.corbawizard.FileBindingStep;
import org.netbeans.jellytools.modules.corba.corbawizard.FinishStep;
import org.netbeans.jellytools.modules.corba.corbawizard.ORBSettingsStep;
import org.netbeans.jellytools.modules.corba.corbawizard.RootInterfacesStep;
import org.netbeans.jellytools.modules.corba.corbawizard.SelectSourceIDLStep;
import org.netbeans.jellytools.modules.corba.corbawizard.TypeAplicationStep;
import org.netbeans.jellytools.modules.corba.nodes.IDLNode;
import org.netbeans.jellytools.nodes.JavaNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.junit.NbTestSuite;
import util.Environment;
import util.Filter;
import util.Helper;
import util.StatusBarTracer;

public class Main extends JellyTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static Test suite() {
        NbTestSuite test = new NbTestSuite();
        test.addTest(new Main("testWizard_Func_JDK14"));
        return test;
    }
    
    ExplorerOperator exp = null;
    EventTool ev = null;
    PrintStream out = null;
    Filter filter = null;
    Timeouts time;
    StatusBarTracer sbt = null;

    public void setUp () {
        time = JemmyProperties.getCurrentTimeouts ();
        Timeouts t = new Timeouts ();
        try { t.loadDebugTimeouts (); } catch (IOException e) {}
//        JemmyProperties.setCurrentTimeouts (t);
        exp = new ExplorerOperator ();
        ev = new EventTool ();
        out = getRef ();
        closeAllModal = false;
        sbt = new StatusBarTracer ();
        filter = new Filter ();
        filter.addFilterAfter ("@author");
        filter.addFilterAfter ("Created on");
        filter.addFilterAfter ("by ");
        filter.addReplace ("", "");
    }
    
    public void tearDown () {
        sbt.stop ();
        JemmyProperties.setCurrentTimeouts (time);
    }
	
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    public void dumpFile (String node, String name) {
        new OpenAction ().perform (new Node (exp.repositoryTab ().tree (), node));
        Helper.sleep (1000);
        ev.waitNoEvent (1000);
        EditorWindowOperator ewo = new EditorWindowOperator ();
        EditorOperator eo = ewo.getEditor (name);
        Helper.sleep (1000);
        ev.waitNoEvent (1000);
        String str = eo.getText ();
        StringTokenizer tok = new StringTokenizer (str, "\n");
        out.println ("=========================================");
        out.println ("---- " + name);
        while (tok.hasMoreTokens())
            out.println (filter.filter (tok.nextToken ()));
        out.println ("-----------------------------------------");
        eo.close ();
    }

    String iorfilename;
    
    public void testWizard_Func_JDK14 () {
        Environment.loadORBEnvironment("JDK14");
        try {
            iorfilename = Helper.replaceAll(getWorkDirPath(), "\\", "/") + "/ior.ior";
        } catch (IOException e) {
            assertTrue ("IOException during getWorkDirPath()", false);
        }
        filter.addReplace (iorfilename, "<FILE>");
        new CORBAWizardAction ().perform(new IDLNode (exp.repositoryTab().tree (), "|data|cwfunc|jdk14|App"));
        
        SelectSourceIDLStep ss = new SelectSourceIDLStep ();
        ss.verify ();
        ss.next ();
        
        TypeAplicationStep ta = new TypeAplicationStep ();
        ta.verify ();
        ta.checkCreateImplementation(true);
        ta.checkTieBased(false);
        ta.checkCreateCallBackClient(true);
        ta.checkCreateClient(true);
        ta.checkCreateServer(true);
        ta.next ();
        
        ORBSettingsStep orb = new ORBSettingsStep ();
        orb.verify ();
        orb.selectChooseORBImplementation(ORBSettingsStep.ITEM_JDK14ORB);
        orb.selectChooseBindingMethod(ORBSettingsStep.ITEM_IORTOFILE);
        orb.next ();
        
        RootInterfacesStep ri = new RootInterfacesStep ();
        ri.verify ();
        ri.next ();
        
        FileBindingStep fi = new FileBindingStep ();
        fi.verify ();
        fi.typeIORFileName(iorfilename);
        fi.next ();
        
        FinishStep fs = new FinishStep ();
        fs.verify ();
        sbt.start ();
        fs.finish();
        
        sbt.waitText ("Compiling ...");
        // sbt.waitText ("Finished."); // unstable - sometimes text is shown in output window/compiler
        sbt.waitText ("Generation Implementation...");
        sbt.waitText ("Generate data.cwfunc.jdk14.HelloImpl ...");
        sbt.waitText ("Successfully Generated Implementation Classes for App.");
        sbt.waitText ("Generation Client...");
        sbt.waitText ("Opening AppClient ...");
        sbt.waitText ("Generation Server...");
        sbt.waitText ("Opening AppServer ...");
        sbt.waitText ("Generation Call-back Client...");
        sbt.waitText ("Opening AppCallBackClient ...");
        sbt.stop ();
        
        dumpFile("|data|cwfunc|jdk14|AppCallBackClient", "AppCallBackClient");
        dumpFile("|data|cwfunc|jdk14|AppClient", "AppClient");
        dumpFile("|data|cwfunc|jdk14|AppServer", "AppServer");
        dumpFile("|data|cwfunc|jdk14|HelloImpl", "HelloImpl");
        
        sbt.clear ();
        sbt.start ();
        
        new JavaNode (exp.repositoryTab().tree (), "|data|cwfunc|jdk14|AppCallBackClient").compile ();
        sbt.removeText ("Compiling AppCallBackClient...");
        sbt.removeText ("Compiling data/cwfunc/jdk14/AppCallBackClient.java");
        out.println (sbt.removeText ("AppCallBackClient.", false, true));
        sbt.clear ();
        
        new JavaNode (exp.repositoryTab().tree (), "|data|cwfunc|jdk14|AppClient").compile ();
        sbt.removeText ("Compiling AppClient...");
        sbt.removeText ("Compiling data/cwfunc/jdk14/AppClient.java");
        out.println (sbt.removeText ("AppClient.", false, true));
        sbt.clear ();
        
        new JavaNode (exp.repositoryTab().tree (), "|data|cwfunc|jdk14|AppServer").compile ();
        sbt.removeText ("Compiling AppServer...");
        sbt.removeText ("Compiling data/cwfunc/jdk14/AppServer.java");
        out.println (sbt.removeText ("AppServer.", false, true));
        sbt.clear ();
        
        compareReferenceFiles();
    }
    
}
