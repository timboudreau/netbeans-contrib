/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
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
        sbt.waitText ("Finished.");
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
        sbt.removeText ("Compiling data.cwfunc.jdk14.AppCallBackClient");
        out.println (sbt.removeText ("AppCallBackClient.", false));
        sbt.clear ();
        
        new JavaNode (exp.repositoryTab().tree (), "|data|cwfunc|jdk14|AppClient").compile ();
        sbt.removeText ("Compiling AppClient...");
        sbt.removeText ("Compiling data.cwfunc.jdk14.AppClient");
        out.println (sbt.removeText ("AppClient.", false));
        sbt.clear ();
        
        new JavaNode (exp.repositoryTab().tree (), "|data|cwfunc|jdk14|AppServer").compile ();
        sbt.removeText ("Compiling AppServer...");
        sbt.removeText ("Compiling data.cwfunc.jdk14.AppServer");
        out.println (sbt.removeText ("AppServer.", false));
        sbt.clear ();
        
        compareReferenceFiles();
    }
    
}
