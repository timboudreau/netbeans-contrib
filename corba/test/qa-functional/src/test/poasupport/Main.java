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

package test.poasupport;

import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;
import javax.swing.ComboBoxModel;
import javax.swing.JDialog;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NewObjectNameStepOperator;
import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jellytools.RenameDialogOperator;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.actions.SaveAction;
import org.netbeans.jellytools.modules.corba.nodes.POAChildNode;
import org.netbeans.jellytools.modules.corba.nodes.POANode;
import org.netbeans.jellytools.modules.corba.nodes.RootPOANode;
import org.netbeans.jellytools.modules.corba.poasupport.NewChildPOACustomizerDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewChildPOADialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewDefaultServantCustomizerDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewDefaultServantDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewPOAActivatorCustomizerDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewPOAActivatorDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewServantDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewServantManagerCustomizerDialog;
import org.netbeans.jellytools.modules.corba.poasupport.NewServantManagerDialog;
import org.netbeans.jellytools.modules.corba.poasupport.WarningDialog;
import org.netbeans.jellytools.nodes.FolderNode;
import org.netbeans.jellytools.nodes.JavaNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.ComboBoxProperty;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.Operator;
import util.Environment;
import util.Filter;
import util.Helper;
import util.JHelper;

public class Main extends JellyTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testPOA_Templates"));
        test.addTest(new Main("testPOA_CreateServer"));
        test.addTest(new Main("testPOA_AddChildPOA"));
        test.addTest(new Main("testPOA_CreatePOAActivator"));
        test.addTest(new Main("testPOA_CreateServantManager"));
        test.addTest(new Main("testPOA_SetManager"));
        test.addTest(new Main("testPOA_CreateDefaultServant"));
        test.addTest(new Main("testPOA_CreateServant"));
        test.addTest(new Main("testPOA_RenamePOAChild"));
        test.addTest(new Main("testPOA_DumpFile"));
        test.addTest(new Main("testPOA_Proprietary_VB4X"));
        test.addTest(new Main("testPOA_Proprietary_OW2000"));
        return test;
    }
    
    ExplorerOperator exp = null;
    EventTool ev = null;
    PrintStream out = null;
    Filter filter = null;
    Timeouts time;

    public void setUp () {
        time = JemmyProperties.getCurrentTimeouts ();
        Timeouts t = new Timeouts ();
        try { t.loadDebugTimeouts (); } catch (IOException e) {}
        JemmyProperties.setCurrentTimeouts (t);
        exp = new ExplorerOperator ();
        ev = new EventTool ();
        out = getRef ();
        closeAllModal = true;
        filter = new Filter ();
        filter.addFilterAfter ("@author");
        filter.addFilterAfter ("Created on");
        filter.addFilterAfter ("by ");
        filter.addFilterFrom ("//GEN-");
    }
    
    public void tearDown () {
        JemmyProperties.setCurrentTimeouts (time);
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
        while (tok.hasMoreTokens())
            out.println (filter.filter (tok.nextToken ()));
        eo.close ();
    }

    public void generate (String template, String name) {
        out.println ("--------------------------------------------");
        out.println ("---- " + template);
        out.println ();
	
//        NewWizardOperator.create ("CORBA|" + template, "|data|poasupport|generate", name); 

	// jelly2 bug workaround
//        new FolderNode (poaFolder + "|generate").newFromTemplate("CORBA|" + template);
//        NewObjectNameStepOperator nonso = new NewObjectNameStepOperator ();
//        nonso.setName(name);
//        nonso.finish();

        NewWizardOperator.create("CORBA|" + template, poaFolder + "|generate", name);

        ev.waitNoEvent (1000);
        Helper.sleep (1000);
        dumpFile (poaFolder + "|generate|" + name, name);
    }
    
    public void testPOA_Templates () {
        Environment.loadORBEnvironment("JDK14");
        generate ("ServerMain", "GenServer");
        generate ("ClientMain", "GenClient");
        generate ("CallBackClientMain", "GenCallBack");
        generate ("POA|AdapterActivator", "GenAdapter");
        generate ("POA|ServantActivator", "GenServant");
        generate ("POA|ServantLocator", "GenLocator");
        compareReferenceFiles ();
    }
    
    public final String poaFileName = "POAFile1";
    public final String poaFileName1 = "POAFile1_1";
    public final String poaFolder = "|data|poasupport";
    public final String poaNode = poaFolder + "|" + poaFileName;
    public final String poaNode1 = poaFolder + "|" + poaFileName1;
    public final String poaRoot = poaNode + "|class " + poaFileName + "|RootPOA";
    
    public void testPOA_CreateServer () {
        Environment.loadORBEnvironment("OB4X");
        Environment.css.getActiveSetting ().setServerBindingFromString ("IOR to standard output");
//        NewWizardOperator.create ("CORBA|ServerMain", poaFolder, poaFileName);

        // jelly2 bug workaround
//        new FolderNode (poaFolder).newFromTemplate("CORBA|ServerMain");
//        NewObjectNameStepOperator nonso = new NewObjectNameStepOperator ();
//        nonso.setName(poaFileName);
//        nonso.finish();

        NewWizardOperator.create("CORBA|ServderMain", poaFolder, poaFileName);

        ev.waitNoEvent(1000);
        util.Helper.sleep (5000);
        ev.waitNoEvent(1000);
    }
    
    public void testPOA_AddChildPOA () {
        new RootPOANode (exp.repositoryTab ().tree (), poaRoot).addChildPOA();
        NewChildPOADialog di1 = new NewChildPOADialog ();
        JHelper.typeNewText (di1.txtPOAName(), "MyChildPOA_DefaultValues");
        JHelper.typeNewText(di1.txtVariable (), "var_MyChildPOA_DefaultValues");
        di1.oK (); di1.waitClosed();

        new RootPOANode (exp.repositoryTab ().tree (), poaRoot).addChildPOA();
        NewChildPOADialog di2 = new NewChildPOADialog ();
        JHelper.typeNewText(di2.txtPOAName(), "Parent_of_children");
        JHelper.typeNewText(di2.txtVariable (), "var_Parent_of_children");
        di2.oK (); di2.waitClosed();

        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children").addChildPOA();
        NewChildPOADialog di3 = new NewChildPOADialog ();
        JHelper.typeNewText(di3.txtPOAName(), "1.child Parent_of_children");
        JHelper.typeNewText(di3.txtVariable (), "var_1_child_Parent_of_children");
        di3.cboThread().selectItem (di3.ITEM_SINGLE_THREAD_MODEL);
        di3.cboLifespan().selectItem (di3.ITEM_PERSISTENT);
        di3.cboIdUniqueness().selectItem (di3.ITEM_MULTIPLE_ID);
        di3.cboIdAssignment().selectItem (di3.ITEM_USER_ID);
        di3.cboServantRetention().selectItemNoBlock (di3.ITEM_NON_RETAIN);
        WarningDialog di4 = new WarningDialog ();
        di4.selectJComboBox(di4.ITEM_USE_SERVANT_MANAGER);
        di4.oK(); di4.waitClosed();
        di3.oK (); di3.waitClosed();
        
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children").addChildPOA();
        NewChildPOADialog di5 = new NewChildPOADialog ();
        JHelper.typeNewText(di5.txtPOAName(), "2.child Parent_of_children");
        JHelper.typeNewText(di5.txtVariable (), "var_2_child_Parent_of_children");
        di5.cboServantRetention().selectItemNoBlock (di5.ITEM_NON_RETAIN);
        WarningDialog di6 = new WarningDialog ();
        di6.selectJComboBox(di6.ITEM_USE_SERVANT_MANAGER);
        di6.oK(); di6.waitClosed();
        di5.cboImplicitActivation().selectItem (di5.ITEM_IMPLICIT_ACTIVATION);
        di5.oK (); di5.waitClosed();

        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children").addChildPOA();
        NewChildPOADialog di9 = new NewChildPOADialog ();
        di9.cboIdAssignment().selectItem (di1.ITEM_USER_ID);
        di9.cboImplicitActivation().selectItem (di1.ITEM_IMPLICIT_ACTIVATION);
        di9.cancel (); di9.waitClosed();
    }
    
    public void testPOA_CreatePOAActivator () {
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children").addChildPOA();
        NewChildPOADialog di7 = new NewChildPOADialog ();
        JHelper.typeNewText(di7.txtPOAName(), "3.child Parent_of_children");
        JHelper.typeNewText(di7.txtVariable (), "var_3_child_Parent_of_children");
        di7.oK (); di7.waitClosed();

        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|3.child Parent_of_children").customize();
        NewChildPOACustomizerDialog di8 = new NewChildPOACustomizerDialog ();
        di8.cboRequestProcessing().selectItem (di8.ITEM_USE_SERVANT_MANAGER);
        di8.close (); di8.waitClosed();

        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|3.child Parent_of_children").addPOAActivator();
        NewPOAActivatorDialog di1 = new NewPOAActivatorDialog ();
        JHelper.typeNewText(di1.txtVariable (), "var_adapter_activator");
        di1.checkGenerateActivatorInstantiationCode(true);
        di1.oK (); di1.waitClosed();
        
        new POANode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|3.child Parent_of_children|var_adapter_activator").customize();
        NewPOAActivatorCustomizerDialog di2 = new NewPOAActivatorCustomizerDialog ();
        di2.checkGenerateActivatorInstantiationCode(true);
        ev.waitNoEvent(1000);
        di2.cboType().clearText();
        di2.typeType("MyAdapterActivator");
        di2.close (); di2.waitClosed();
    }
    
    public void testPOA_CreateServantManager () {
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|3.child Parent_of_children").addServantManager();
        NewServantManagerDialog di1 = new NewServantManagerDialog ();
        JHelper.typeNewText(di1.txtVariable (), "var_servant_manager");
        di1.checkGenerateServantManagerInstantiationCode(true);
        di1.oK (); di1.waitClosed();
        
        new POANode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|3.child Parent_of_children|var_servant_manager").customize();
        NewServantManagerCustomizerDialog di2 = new NewServantManagerCustomizerDialog ();
        di2.checkGenerateServantManagerInstantiationCode(true);
        ev.waitNoEvent(1000);
        di2.cboType().clearText();
        di2.typeType("MyServantManager");
        di2.close (); di2.waitClosed();
    }
    
    
    public void testPOA_SetManager () {
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children").addChildPOA();
        NewChildPOADialog di7 = new NewChildPOADialog ();
        JHelper.typeNewText(di7.txtPOAName(), "4.child Parent_of_children");
        JHelper.typeNewText(di7.txtVariable (), "var_4_child_Parent_of_children");
        di7.oK (); di7.waitClosed();

        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|4.child Parent_of_children").addChildPOA();
        NewChildPOADialog di8 = new NewChildPOADialog ();
        JHelper.typeNewText(di8.txtPOAName(), "1.child of 4.child Parent_of_children");
        JHelper.typeNewText(di8.txtVariable (), "var_1_4_child_Parent_of_children");
        di8.oK (); di8.waitClosed();
        
        JHelper.closeAllProperties();
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|4.child Parent_of_children|1.child of 4.child Parent_of_children").properties();
        PropertySheetOperator pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, "1.child of 4.child Parent_of_children");
        ComboBoxProperty cbp = new ComboBoxProperty (pso, "POA Manager");
        ComboBoxModel cbm = cbp.comboBox().getModel();
        out.println ("SELECTED: " + cbm.getSelectedItem());
        for (int a = 0; a < cbm.getSize (); a ++)
            out.println ("Item no." + a + ": " + cbm.getElementAt(a));
        cbp.setValue("var_4_child_Parent_of_children");
        Helper.sleep (5000);
        pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, "1.child of 4.child Parent_of_children");
        cbp = new ComboBoxProperty (pso, "POA Manager");
        cbm = cbp.comboBox().getModel();
        out.println ("SELECTED: " + cbm.getSelectedItem());
        pso.close();
        compareReferenceFiles ();
    }

    public void testPOA_CreateDefaultServant () {
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children").addChildPOA();
        NewChildPOADialog di9 = new NewChildPOADialog ();
        JHelper.typeNewText(di9.txtPOAName(), "5.child Parent_of_children");
        JHelper.typeNewText(di9.txtVariable (), "var_5_child_Parent_of_children");
        di9.oK (); di9.waitClosed();

        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|5.child Parent_of_children").customize();
        NewChildPOACustomizerDialog di10 = new NewChildPOACustomizerDialog ();
        di10.cboRequestProcessing().selectItem(di10.ITEM_USE_DEFAULT_SERVANT);
        di10.close (); di10.waitClosed ();
        
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|5.child Parent_of_children").addDefaultServant();
        NewDefaultServantDialog di1 = new NewDefaultServantDialog ();
        JHelper.typeNewText(di1.txtVariable (), "var_default_servant");
        di1.checkGenerateDefaultServantInstantiationCode(true);
        di1.oK(); di1.waitClosed();
        
        new POANode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|5.child Parent_of_children|var_default_servant").customize();
        NewDefaultServantCustomizerDialog di2 = new NewDefaultServantCustomizerDialog ();
        di2.checkGenerateDefaultServantInstantiationCode (true);
        di2.cboType().clearText();
        di2.typeType("HelloWorldImpl");
        di2.close (); di2.waitClosed ();
    }
    
    public void testPOA_CreateServant () {
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children|5.child Parent_of_children").addServant();
        NewServantDialog di1 = new NewServantDialog ();
        JHelper.typeNewText(di1.txtVariable (), "HelloWorld");
        JHelper.typeNewText(di1.txtIDVariable (), "HelloWorld_ID");
        di1.oK(); di1.waitClosed();
    }
    
    public void testPOA_RenamePOAChild () {
        new POAChildNode (exp.repositoryTab ().tree (), poaRoot + "|Parent_of_children").rename();
        RenameDialogOperator rd = new RenameDialogOperator ();
        rd.setNewName("PaReNt");
        rd.ok(); rd.waitClosed();
    }
    
    public void testPOA_DumpFile () {
        new JavaNode (exp.repositoryTab ().tree (), poaNode).open ();
        ev.waitNoEvent(1000);
        
        EditorWindowOperator ewo = new EditorWindowOperator ();
        EditorOperator eo = ewo.getEditor (poaFileName);
        ev.waitNoEvent(1000);
        eo.waitModified(true);
        eo.close(true);
        dumpFile(poaNode, poaFileName);
        ev.waitNoEvent(1000);

        new JavaNode (exp.repositoryTab ().tree (), poaNode).copy();
        ev.waitNoEvent(1000);
        
        new FolderNode (exp.repositoryTab ().tree (), poaFolder).pasteCopy();
        ev.waitNoEvent(1000);

        dumpFile(poaNode1, poaFileName1);
        compareReferenceFiles ();
    }
    
    public void testPOA_Proprietary_VB4X () {
        Environment.loadORBEnvironment("VB4X");

        new JavaNode (exp.repositoryTab ().tree (), "|data|poasupport|additional|VisiBroker4").open ();
        ev.waitNoEvent(1000);
        
        NewChildPOADialog di;
        
        new RootPOANode (exp.repositoryTab ().tree (), "|data|poasupport|additional|VisiBroker4|class VisiBroker4|RootPOA").addChildPOA();
        di = new NewChildPOADialog ();
        di.cboBindSupport().selectItem (NewChildPOADialog.ITEM_BY_INSTANCE);
        di.oK (); di.waitClosed();
        new RootPOANode (exp.repositoryTab ().tree (), "|data|poasupport|additional|VisiBroker4|class VisiBroker4|RootPOA").addChildPOA();
        di = new NewChildPOADialog ();
        di.cboBindSupport().selectItem (NewChildPOADialog.ITEM_BY_POA);
        di.oK (); di.waitClosed();
        new RootPOANode (exp.repositoryTab ().tree (), "|data|poasupport|additional|VisiBroker4|class VisiBroker4|RootPOA").addChildPOA();
        di = new NewChildPOADialog ();
        di.cboBindSupport().selectItem (NewChildPOADialog.ITEM_NONE);
        di.oK (); di.waitClosed();

        EditorWindowOperator ewo = new EditorWindowOperator ();
        EditorOperator eo = ewo.getEditor ("VisiBroker4");
        ev.waitNoEvent(1000);
        eo.waitModified(true);
        eo.close(true);

        dumpFile("|data|poasupport|additional|VisiBroker4", "VisiBroker4");
        compareReferenceFiles ();
    }
    
    public void testPOA_Proprietary_OW2000 () {
        Environment.loadORBEnvironment("OW2000");

        new JavaNode (exp.repositoryTab ().tree (), "|data|poasupport|additional|Orbix2000").open ();
        ev.waitNoEvent(1000);
        
        NewChildPOADialog di;

        new RootPOANode (exp.repositoryTab ().tree (), "|data|poasupport|additional|Orbix2000|class Orbix2000|RootPOA").addChildPOA();
        di = new NewChildPOADialog ();
        di.cboObjectDeactivation().selectItem (NewChildPOADialog.ITEM_DELIVER);
        di.cboPersistenceMode().selectItem(0);
        di.cboWellKnownAddressing().clearText();
        di.cboWellKnownAddressing().typeText("String1_INDIRECT_WellKnownAddressing");
        di.cboWorkQueue().clearText();
        di.cboWorkQueue().typeText("String2_INDIRECT_WorkQueue");
        di.oK (); di.waitClosed();

        new RootPOANode (exp.repositoryTab ().tree (), "|data|poasupport|additional|Orbix2000|class Orbix2000|RootPOA").addChildPOA();
        di = new NewChildPOADialog ();
        di.cboObjectDeactivation().selectItem (NewChildPOADialog.ITEM_DISCARD);
        di.cboPersistenceMode().selectItem(1);
        di.cboWellKnownAddressing().clearText();
        di.cboWellKnownAddressing().typeText("String1_DIRECT_WellKnownAddressing");
        di.cboWorkQueue().clearText();
        di.cboWorkQueue().typeText("String2_DIRECT_WorkQueue");
        di.oK (); di.waitClosed();

        new RootPOANode (exp.repositoryTab ().tree (), "|data|poasupport|additional|Orbix2000|class Orbix2000|RootPOA").addChildPOA();
        di = new NewChildPOADialog ();
        di.cboObjectDeactivation().selectItem (NewChildPOADialog.ITEM_HOLD);
        di.cboWellKnownAddressing().clearText();
        di.cboWellKnownAddressing().typeText("String1_HOLD_WellKnownAddressing");
        di.cboWorkQueue().clearText();
        di.cboWorkQueue().typeText("String2_HOLD_WorkQueue");
        di.oK (); di.waitClosed();

        EditorWindowOperator ewo = new EditorWindowOperator ();
        EditorOperator eo = ewo.getEditor ("Orbix2000");
        ev.waitNoEvent(1000);
        eo.waitModified(true);
        eo.close(true);

        dumpFile("|data|poasupport|additional|Orbix2000", "Orbix2000");
        compareReferenceFiles ();
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
