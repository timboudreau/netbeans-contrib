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

package test.poasearch;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.modules.corba.nodes.POAChildNode;
import org.netbeans.jellytools.modules.corba.poasupport.NewDefaultServantDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewPOAActivatorDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewServantDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewServantManagerDialog;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.Repository;
import util.Environment;
import util.Helper;

public class Main extends JellyTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        setupWorkdir ();
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testPOASearch_Servant_Package"));
        test.addTest(new Main("testPOASearch_Servant_Sub"));
        test.addTest(new Main("testPOASearch_Servant_Filesystem"));
        test.addTest(new Main("testPOASearch_Default_Package"));
        test.addTest(new Main("testPOASearch_Default_Sub"));
        test.addTest(new Main("testPOASearch_Default_Filesystem"));
        test.addTest(new Main("testPOASearch_Manager_Package"));
        test.addTest(new Main("testPOASearch_Manager_Sub"));
        test.addTest(new Main("testPOASearch_Manager_Filesystem"));
        test.addTest(new Main("testPOASearch_Activator_Package"));
        test.addTest(new Main("testPOASearch_Activator_Sub"));
        test.addTest(new Main("testPOASearch_Activator_Filesystem"));
        return test;
    }
    
    ExplorerOperator exp = null;
    EventTool ev = null;
    PrintStream out = null;
    Timeouts time;
    
    CORBASupportSettings css;
    static String fsroot;

    public void setUp () {
/*        time = JemmyProperties.getCurrentTimeouts ();
        Timeouts t = new Timeouts ();
        try { t.loadDebugTimeouts (); } catch (IOException e) {}
        JemmyProperties.setCurrentTimeouts (t);*/
        exp = new ExplorerOperator ();
        ev = new EventTool ();
        out = getRef ();
        closeAllModal = true;
        css = (CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true);
        assertNotNull ("Cannot find CORBASupportSettings class", css);
    }
        
    public static void setupWorkdir () {
        try {
            String name = System.getProperty("work.dir") + "/tests/qa-functional/src/data/poasearch";
            System.out.println (name);
            FileSystem fs = Repository.getDefault ().findFileSystem(name);
            if (fs == null) {
                LocalFileSystem lfs = new LocalFileSystem();
                lfs.setRootDirectory(new File(name));
                Repository.getDefault ().addFileSystem(lfs);
                fsroot = lfs.getDisplayName ();
            } else
                fsroot = fs.getDisplayName ();
            System.out.println (fsroot);
        } catch (Exception e) {
            throw new AssertionFailedErrorException ("Error while mounting working filesystem - data/poasearch", e);
        }
        Environment.loadORBEnvironment("JDK14");
    }
    
    public void tearDown () {
//        JemmyProperties.setCurrentTimeouts (time);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public void findServants () {
        ev.waitNoEvent(1000);
        new POAChildNode (exp.repositoryTab().tree (), fsroot + "|main|ServerMain|class ServerMain|RootPOA|Servant").addServant();
        NewServantDialog di = new NewServantDialog ();
        di.verify ();
        di.checkGenerateServantInstantiationCode(true);
        JComboBoxOperator cbo = di.cboType();
        for (int a = cbo.getItemCount() - 1; a >= 0; a --)
            out.println (cbo.getItemAt(a));
        out.println("----");
        cbo.clearText ();
        cbo.typeText("main.ServantImpl");
        cbo = di.cboConstructor();
        for (int a = cbo.getItemCount() - 1; a >= 0; a --)
            out.println (cbo.getItemAt(a));
        di.cancel ();
        di.waitClosed();
        ev.waitNoEvent(1000);
        Helper.sleep (2000);
    }

    public void testPOASearch_Servant_Package () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.PACKAGE);
        findServants ();
        compareReferenceFiles ();
    }

    public void testPOASearch_Servant_Sub () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.PACKAGE_AND_SUB_PACKAGES);
        findServants ();
        compareReferenceFiles ();
    }

    public void testPOASearch_Servant_Filesystem () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.FILESYSTEM);
        findServants ();
        compareReferenceFiles ();
    }

    public void findDefaults () {
        ev.waitNoEvent(1000);
        new POAChildNode (exp.repositoryTab().tree (), fsroot + "|main|ServerMain|class ServerMain|RootPOA|Default").addDefaultServant();
        NewDefaultServantDialog di = new NewDefaultServantDialog ();
        di.verify ();
        di.checkGenerateDefaultServantInstantiationCode(true);
        JComboBoxOperator cbo = di.cboType();
        for (int a = cbo.getItemCount() - 1; a >= 0; a --)
            out.println (cbo.getItemAt(a));
        out.println("----");
        cbo.clearText ();
        cbo.typeText("main.ServantImpl");
        cbo = di.cboConstructor();
        for (int a = cbo.getItemCount() - 1; a >= 0; a --)
            out.println (cbo.getItemAt(a));
        di.cancel ();
        di.waitClosed();
        ev.waitNoEvent(1000);
        Helper.sleep (2000);
    }

    public void testPOASearch_Default_Package () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.PACKAGE);
        findDefaults ();
        compareReferenceFiles ();
    }

    public void testPOASearch_Default_Sub () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.PACKAGE_AND_SUB_PACKAGES);
        findDefaults ();
        compareReferenceFiles ();
    }

    public void testPOASearch_Default_Filesystem () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.FILESYSTEM);
        findDefaults ();
        compareReferenceFiles ();
    }

    public void findManagers () {
        ev.waitNoEvent(1000);
        new POAChildNode (exp.repositoryTab().tree (), fsroot + "|main|ServerMain|class ServerMain|RootPOA|Manager").addServantManager();
        NewServantManagerDialog di = new NewServantManagerDialog ();
        di.verify ();
        di.checkGenerateServantManagerInstantiationCode(true);
        JComboBoxOperator cbo = di.cboType();
        for (int a = cbo.getItemCount() - 1; a >= 0; a --)
            out.println (cbo.getItemAt(a));
        out.println("----");
        cbo.clearText ();
        cbo.typeText("main.MyServantLocator");
        cbo = di.cboConstructor();
        for (int a = cbo.getItemCount() - 1; a >= 0; a --)
            out.println (cbo.getItemAt(a));
        di.cancel ();
        di.waitClosed();
        ev.waitNoEvent(1000);
        Helper.sleep (2000);
    }

    public void testPOASearch_Manager_Package () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.PACKAGE);
        findManagers ();
        compareReferenceFiles ();
    }

    public void testPOASearch_Manager_Sub () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.PACKAGE_AND_SUB_PACKAGES);
        findManagers ();
        compareReferenceFiles ();
    }

    public void testPOASearch_Manager_Filesystem () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.FILESYSTEM);
        findManagers ();
        compareReferenceFiles ();
    }

    public void findActivators () {
        ev.waitNoEvent(1000);
        new POAChildNode (exp.repositoryTab().tree (), fsroot + "|main|ServerMain|class ServerMain|RootPOA|Activator").addPOAActivator();
        NewPOAActivatorDialog di = new NewPOAActivatorDialog ();
        di.verify ();
        di.checkGenerateActivatorInstantiationCode(true);
        JComboBoxOperator cbo = di.cboType();
        for (int a = cbo.getItemCount() - 1; a >= 0; a --)
            out.println (cbo.getItemAt(a));
        out.println("----");
        cbo.clearText ();
        cbo.typeText("main.MyAdapterActivator");
        cbo = di.cboConstructor();
        for (int a = cbo.getItemCount() - 1; a >= 0; a --)
            out.println (cbo.getItemAt(a));
        di.cancel ();
        di.waitClosed();
        ev.waitNoEvent(1000);
        Helper.sleep (2000);
    }

    public void testPOASearch_Activator_Package () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.PACKAGE);
        findActivators ();
        compareReferenceFiles ();
    }

    public void testPOASearch_Activator_Sub () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.PACKAGE_AND_SUB_PACKAGES);
        findActivators ();
        compareReferenceFiles ();
    }

    public void testPOASearch_Activator_Filesystem () {
        css.getActiveSetting().setFindMethod(ORBSettingsBundle.FILESYSTEM);
        findActivators ();
        compareReferenceFiles ();
    }

}
