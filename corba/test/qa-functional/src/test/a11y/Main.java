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

package test.a11y;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.swing.JDialog;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.SaveAction;
import org.netbeans.jellytools.modules.corba.actions.CORBAWizardAction;
import org.netbeans.jellytools.modules.corba.nodes.CORBAInterfaceRepositoryNode;
import org.netbeans.jellytools.modules.corba.nodes.CORBANamingServiceNode;
import org.netbeans.jellytools.modules.corba.nodes.NamingContextNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.operators.JButtonOperator;
import util.Environment;

public class Main extends org.netbeans.jellytools.JellyTestCase {
    
    /** constructor required by JUnit
     * @param testName method name to be used as testcase
     */
    public Main(String name) {
        super(name);
    }
    
    /** method used for explicit testsuite definition
     */
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testA11Y_IOR"));
        test.addTest(new Main("testA11Y_POA"));
        test.addTest(new Main("testA11Y_NS"));
        test.addTest(new Main("testA11Y_IR"));
        test.addTest(new Main("testA11Y_IW"));
        test.addTest(new Main("testA11Y_CW"));
        return test;
    }
/*
    PrintStream jemmyLog, jemmyLog2;
 
    protected void setUp() throws IOException {
        JemmyProperties.setCurrentOutput(new TestOut(System.in, jemmyLog = getLog("jemmy"), jemmyLog2 = getLog("jemmy2")));
        new EventTool().waitNoEvent(500);
    }
 
    protected void tearDown() {
        if (jemmyLog != null)
            try { jemmyLog.close(); } catch (IOException e) {}
        if (jemmyLog2 != null)
            try { jemmyLog2.close(); } catch (IOException e) {}
    }
 
    void fail(Exception e) {
        info.println("Unexpected exception:");
        e.printStackTrace(info);
        if (captureScreen) {
            try {
                PNGEncoder.captureScreen(getWorkDirPath()+File.separator+"screen.png");
            } catch (Exception e1) {}
        }
        if (dumpScreen) {
            try {
                Dumper.dumpAll(getWorkDirPath()+File.separator+"screen.xml");
            } catch (Exception e2) {}
        }
        throw new AssertionFailedErrorException(e);
    }
 */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    java.io.PrintStream out;
    java.io.PrintStream info;
    
    public void testA11Y(java.awt.Component comp, boolean close) {
        testA11Y(comp, close, null);
    }
    
    public void testA11Y(java.awt.Component comp, boolean close, String user) {
        new EventTool().waitNoEvent(1000);
        out = getRef();
        info = getLog();
        
        org.netbeans.a11y.TestSettings ts = new org.netbeans.a11y.TestSettings();
        ts.setDefaultSettings();
		ts.tabTraversal = false; //
        if (comp instanceof JDialog)
            ts.setWindowTitle(((JDialog) comp).getTitle());
        if (close)
            ts.setCancelLabel("Close");
        ts.setExcludedClasses ("javax.swing.JScrollPane$ScrollBar");
        org.netbeans.a11y.AccessibilityTester at = new org.netbeans.a11y.AccessibilityTester(comp, ts);
        at.startTests();
        org.netbeans.a11y.TextReport treport = new org.netbeans.a11y.TextReport(at, ts);
        if (comp instanceof JDialog)
            out.println("---> Testing Window: " + ((JDialog) comp).getTitle());
        else
            out.println("---> Testing Component");
        treport.getReport(new PrintWriter(out, true));
        if (user != null) {
            PrintStream l = null;
            try {
                l = getLog(user);
                treport.getReport(new PrintWriter(l, true));
            } finally {
                if (l != null)
                    l.close();
            }
        }
    }
    
    ExplorerOperator exp = null;
    String rootDir = "qa-functional|data|a11y";
    JDialog testDialog = null;
    JDialog testDialog2 = null;
    int timeout;
    
    public void setUp() {
        closeAllModal = true;
        exp = new ExplorerOperator();
    }
    
    public void testA11Y_IOR() {
        Node n1 = new Node(exp.repositoryTab().tree(), rootDir + "|a1");
        Node n2 = new Node(exp.repositoryTab().tree(), rootDir + "|a2");
        new Action(null, "Merge IORs...").performPopup(new Node [] {n1, n2});
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("New IOR File", true, true);
        testA11Y(testDialog, false, "MergeIOR");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
		compareReferenceFiles ();
    }
    
    public void testA11Y_POA() {
        Environment.loadORBEnvironment("OB4X");
        
        new EventTool().waitNoEvent(1000);
        NewWizardOperator.create("CORBA|ServerMain", rootDir, "ServerMain_0");
        new EventTool().waitNoEvent(1000);
        
        String rootPOA = rootDir + "|ServerMain_0|class ServerMain_0|RootPOA";
        
        new Node(exp.repositoryTab().tree(), rootPOA).select();
        new Node(exp.repositoryTab().tree(), rootPOA).performPopupActionNoBlock("Add|Child POA");
        new EventTool().waitNoEvent(1000);
/*        timeout = 3;
        for (;;) {
            try {
                exp.pushPopupMenuNoBlock("Add|Child POA", rootPOA);
                break;
            } catch (org.netbeans.jemmy.TimeoutExpiredException tee) {
                timeout --;
                if (timeout == 0)
                    throw new RunException(tee);
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        }*/
        testDialog = JDialogOperator.waitJDialog("New Child POA", true, true);
        testA11Y(testDialog, false, "NewChildPOA");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "OK", true, true)).push();
        
        new Node(exp.repositoryTab().tree(), rootPOA + "|MyPOA1").select();
        new Node(exp.repositoryTab().tree(), rootPOA + "|MyPOA1").performPopupActionNoBlock("Add|Servant");
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("New Servant", true, true);
        testA11Y(testDialog, false);
        new JCheckBoxOperator(JCheckBoxOperator.findJCheckBox(testDialog, "", false, false, 0)).push();
        testA11Y(testDialog, false, "POAServant");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "OK", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        new Node(exp.repositoryTab().tree(), rootPOA + "|MyPOA1").performPopupAction("Customize");
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("Customizer Dialog", true, true);
        testA11Y(testDialog, true, "CustomizeChildPOA");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Close", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        new Node(exp.repositoryTab().tree(), rootPOA + "|MyPOA1|MyServantID1").performPopupAction("Customize");
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("Customizer Dialog", true, true);
        testA11Y(testDialog, true);
        new JCheckBoxOperator(JCheckBoxOperator.findJCheckBox(testDialog, "", false, false, 0)).push();
        testA11Y(testDialog, true, "CustomizePOAServant");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Close", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        new SaveAction().perform(new Node(exp.repositoryTab().tree(), rootDir + "|ServerMain"));
        new EventTool().waitNoEvent(1000);
        
        Environment.loadORBEnvironment("OW2000");
        new Node(exp.repositoryTab().tree(), rootDir + "|ServerMain_1|class ServerMain_1|RootPOA").select();
        new Node(exp.repositoryTab().tree(), rootDir + "|ServerMain_1|class ServerMain_1|RootPOA").performPopupActionNoBlock("Add|Child POA");
        // at this time, every windows are repainted and previously showed popup menu disappear, so previous action could not be finished - probably jellytool2 error
        util.Helper.sleep(2000);
        new EventTool().waitNoEvent(1000);
/*        timeout = 3;
        for (;;) {
            try {
                exp.pushPopupMenuNoBlock("Add|Child POA", rootDir + "|ServerMain_1|class ServerMain_1|RootPOA");
                break;
            } catch (org.netbeans.jemmy.TimeoutExpiredException tee) {
                timeout --;
                if (timeout == 0)
                    throw new RunException(tee);
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        }*/
        testDialog = JDialogOperator.waitJDialog("New Child POA", true, true);
        testA11Y(testDialog, false, "NewChildPOAOW");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        new Node(exp.repositoryTab().tree(), rootDir + "|ServerMain_1|class ServerMain_1|RootPOA|MyPOA1").select();
        new Node(exp.repositoryTab().tree(), rootDir + "|ServerMain_1|class ServerMain_1|RootPOA|MyPOA1").performPopupActionNoBlock("Customize");
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("Customizer Dialog", true, true);
        testA11Y(testDialog, true);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Close", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        Environment.loadORBEnvironment("VB4X");
        new Node(exp.repositoryTab().tree(), rootDir + "|ServerMain_2|class ServerMain_2|RootPOA").performPopupActionNoBlock("Add|Child POA");
        new EventTool().waitNoEvent(1000);
/*        timeout = 3;
        for (;;) {
            try {
                exp.pushPopupMenuNoBlock("Add|Child POA", rootDir + "|ServerMain_2|class ServerMain_2|RootPOA");
                break;
            } catch (org.netbeans.jemmy.TimeoutExpiredException tee) {
                timeout --;
                if (timeout == 0)
                    throw new RunException(tee);
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        }*/
        testDialog = JDialogOperator.waitJDialog("New Child POA", true, true);
        testA11Y(testDialog, false, "NewChildPOAVB");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        new EventTool().waitNoEvent(1000);
        new Node(exp.repositoryTab().tree(), rootDir + "|ServerMain_2|class ServerMain_2|RootPOA|MyPOA1").select();
        new Node(exp.repositoryTab().tree(), rootDir + "|ServerMain_2|class ServerMain_2|RootPOA|MyPOA1").performPopupActionNoBlock("Customize");
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("Customizer Dialog", true, true);
        testA11Y(testDialog, true);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Close", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        Environment.loadORBEnvironment("OB4X");
        compareReferenceFiles ();
    }
    
    public void testA11Y_NS() {
        new CORBANamingServiceNode(exp.runtimeTab ().tree ()).bindNewContext();
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("CORBA Panel", true, true);
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false, "BindNewContext");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        new CORBANamingServiceNode(exp.runtimeTab ().tree ()).fromInitialReferences();
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("Resolve Initial References", true, true);
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false, "FromInitialRefNS");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        new CORBANamingServiceNode(exp.runtimeTab ().tree ()).startLocal();
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("Local Name Service", true, true);
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false, "StartLocal");
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog, "", false, false, 0)).clearText();
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog, "", false, false, 0)).typeText("A11YTest");
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog, "", false, false, 2)).clearText();
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog, "", false, false, 2)).typeText("9001");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "OK", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        //        try { Thread.currentThread().sleep(2000); } catch (Exception e) {}
        //        testDialog = JDialogOperator.findJDialog("Information", true, true);
        //        if (testDialog != null)
        //            new JButtonOperator(JButtonOperator.findJButton(testDialog, "OK", true, true)).push();
        
        new NamingContextNode(exp.runtimeTab ().tree (), "|A11YTest").createNewContext();
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("CORBA Panel", true, true);
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        new NamingContextNode(exp.runtimeTab ().tree (), "|A11YTest").bindNewContext();
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("CORBA Panel", true, true);
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        new NamingContextNode(exp.runtimeTab ().tree (), "|A11YTest").bindNewObject();
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("CORBA Panel", true, true);
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        new EventTool().waitNoEvent(1000);
        
        new NamingContextNode(exp.runtimeTab ().tree (), "|A11YTest").unbindContext();
        new EventTool().waitNoEvent(1000);
        compareReferenceFiles ();
    }
    
    public void testA11Y_IR() {
        new CORBAInterfaceRepositoryNode(exp.runtimeTab ().tree ()).addInterfaceRepository();
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("CORBA Panel", true, true);
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false, "AddIR");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        
        new CORBAInterfaceRepositoryNode(exp.runtimeTab ().tree ()).fromInitialReferences();
        new EventTool().waitNoEvent(1000);
        testDialog = JDialogOperator.waitJDialog("Resolve Initial References", true, true);
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false, "FromInitialRefIR");
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        compareReferenceFiles ();
    }
    
    public void testA11Y_IW() {
        // IW
        NewWizardOperator nwo = NewWizardOperator.invoke(new Node (exp.repositoryTab ().tree(), rootDir), "CORBA|Empty");
        new EventTool().waitNoEvent(1000);
        nwo.next();
        testDialog = (JDialog) nwo.getSource();
        
        testA11Y(testDialog, false, "IDLSource");

        new org.netbeans.jemmy.operators.JRadioButtonOperator(org.netbeans.jemmy.operators.JRadioButtonOperator.findJRadioButton(testDialog, "Interface Repository", true, true)).push();
        nwo.btNext ().pushNoBlock ();
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false, "ImportIDL");
        nwo.btBack().pushNoBlock ();
        new EventTool().waitNoEvent(1000);
 
        new org.netbeans.jemmy.operators.JRadioButtonOperator(org.netbeans.jemmy.operators.JRadioButtonOperator.findJRadioButton(testDialog, "IDL Wizard", true, true)).push();
        nwo.btNext ().pushNoBlock ();
        new EventTool().waitNoEvent(1000);
        testA11Y(testDialog, false, "DesignIDL");
        
        selectTreeNode(nwo, "");
        
        simpleTestIW(testDialog, "Alias", "Alias");
        simpleTestIW(testDialog, "Constant", "Constant");
        simpleTestIW(testDialog, "Enum", "Enum", "Enum Entry");
        simpleTestIW(testDialog, "Exception", "Exception");
        simpleTestIW(testDialog, "Forward", "Forward Declaration");
        simpleTestIW(testDialog, "Interface", "Interface");
        simpleTestIW(testDialog, "Module", "Module");
        simpleTestIW(testDialog, "Structure", "Structure");
        simpleTestIW(testDialog, "Union", "Union");
        simpleTestIW(testDialog, "ValueBox", "ValueBox");
        simpleTestIW(testDialog, "ValueType", "Valuetype");
        
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, 0)).selectItem("Interface", true, true);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Create...", true, true)).pushNoBlock();
        testDialog2 = JDialogOperator.waitJDialog("Create Interface", true, true);
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 0)).typeText("A");
        new JButtonOperator(JButtonOperator.findJButton(testDialog2, "Ok", true, true)).push();
        waitClose("Create Interface");
        selectTreeNode(nwo, "A");
        simpleTestIW(testDialog, "Attribute", "Attribute");
        simpleTestIW(testDialog, "Operation", "Operation");
        
        selectTreeNode(nwo, "");
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, 0)).selectItem("Valuetype", true, true);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Create...", true, true)).pushNoBlock();
        testDialog2 = JDialogOperator.waitJDialog("Create Valuetype", true, true);
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 0)).typeText("B");
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 1)).typeText("A");
        testA11Y(testDialog2, false);
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 1)).clearText();
        new JButtonOperator(JButtonOperator.findJButton(testDialog2, "Ok", true, true)).push();
        waitClose("Create Interface");
        selectTreeNode(nwo, "B");
        simpleTestIW(testDialog, "Value", "Value");
        simpleTestIW(testDialog, "Factory", "Valuetype Factory");
        
        selectTreeNode(nwo, "");
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, 0)).selectItem("Structure", true, true);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Create...", true, true)).pushNoBlock();
        testDialog2 = JDialogOperator.waitJDialog("Create Structure", true, true);
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 0)).typeText("C");
        testA11Y(testDialog2, false);
        new JButtonOperator(JButtonOperator.findJButton(testDialog2, "Ok", true, true)).push();
        waitClose("Create Structure");
        selectTreeNode(nwo, "C");
        simpleTestIW(testDialog, "StructureMember", "Member");
        
        selectTreeNode(nwo, "");
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, 0)).selectItem("Union", true, true);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Create...", true, true)).pushNoBlock();
        testDialog2 = JDialogOperator.waitJDialog("Create Union", true, true);
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 0)).typeText("D");
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 1)).typeText("long");
        testA11Y(testDialog2, false);
        new JButtonOperator(JButtonOperator.findJButton(testDialog2, "Ok", true, true)).push();
        waitClose("Create Union");
        selectTreeNode(nwo, "D");
        simpleTestIW(testDialog, "UnionMember", "Member");
        simpleTestIW(testDialog, "UnionDefault", "Default Member", "Union Default Member");
        
        selectTreeNode(nwo, "");
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, 0)).selectItem("Enum", true, true);
        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Create...", true, true)).pushNoBlock();
        testDialog2 = JDialogOperator.waitJDialog("Create Enum Entry", true, true);
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 0)).typeText("E");
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog2, "", false, false, 1)).typeText("ee");
        testA11Y(testDialog2, false);
        new JButtonOperator(JButtonOperator.findJButton(testDialog2, "Ok", true, true)).push();
        waitClose("Create Enum Entry");
        selectTreeNode(nwo, "E");
        simpleTestIW(testDialog, "EnumEntry", "Enum Entry", "Entry");
        
        nwo.next ();
        testA11Y(testDialog, false, "IDLFinish");
        nwo.cancel ();
        compareReferenceFiles ();
    }
    
    public void testA11Y_CW () {
        new CORBAWizardAction ().perform (new Node (exp.repositoryTab().tree (), rootDir + "|Simple"));
        WizardOperator wo = new WizardOperator ("CORBA Wizard");
        testDialog = (JDialog) wo.getSource ();
        
        testA11Y(testDialog, false, "CWPackage");
        wo.next ();
        testA11Y(testDialog, false, "CWComponents");
        new JCheckBoxOperator(JCheckBoxOperator.findJCheckBox(testDialog, "Create Implementation", true, true)).push();
        wo.next ();
        testA11Y(testDialog, false);
        wo.next ();
        testA11Y(testDialog, false);
        wo.back ();
        wo.back ();
 
        new JCheckBoxOperator(JCheckBoxOperator.findJCheckBox(testDialog, "Create Server", true, true)).push();
        wo.next ();
        testA11Y(testDialog, false, "CWORB");
        wo.next ();
        testA11Y(testDialog, false, "RootInterface1");
        wo.back ();
        wo.back ();
 
        new JCheckBoxOperator(JCheckBoxOperator.findJCheckBox(testDialog, "Create Client", true, true)).push();
        new JCheckBoxOperator(JCheckBoxOperator.findJCheckBox(testDialog, "Create Call-back Client", true, true)).push();
        wo.next ();
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, -1, 0)).selectItem("OrbixWeb 3.2", true, true);
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, -1, 1)).selectItem("Naming Service", true, true);
        wo.next ();
        testA11Y(testDialog, false, "RootInterface2");
        wo.btNext().pushNoBlock ();
        testA11Y(testDialog, false, "CWNS");
        wo.back ();
        wo.back ();
 
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, -1, 1)).selectItem("IOR to file", true, true);
        wo.next ();
        wo.btNext().pushNoBlock ();
        testA11Y(testDialog, false, "CWFI");
        wo.back ();
        wo.back ();
 
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, -1, 1)).selectItem("IOR to standard output", true, true);
        wo.next ();
        wo.btNext().pushNoBlock ();
        testA11Y(testDialog, false, "CWIO");
        wo.back ();
        wo.back ();
 
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(testDialog, "", false, false, -1, 1)).selectItem("Proprietary Binder", true, true);
        wo.next ();
        wo.btNext().pushNoBlock ();
        testA11Y(testDialog, false, "CWPB");
 
        new JTextFieldOperator(JTextFieldOperator.findJTextField(testDialog, "", false, false, 0)).typeText("Name");
        wo.next ();
        testA11Y(testDialog, false, "CWFinish");

        new JButtonOperator(JButtonOperator.findJButton(testDialog, "Cancel", true, true)).push();
        compareReferenceFiles ();
    }

    public void simpleTestIW(JDialog dia, String user, String name) {
        simpleTestIW(dia, user, name, name);
    }
 
    public void simpleTestIW(JDialog dia, String user, String name, String title) {
        new JComboBoxOperator(JComboBoxOperator.findJComboBox(dia, "", false, false, 0)).selectItem(name, true, true);
        new JButtonOperator(JButtonOperator.findJButton(dia, "Create...", true, true)).pushNoBlock();
        JDialog testDialog2 = JDialogOperator.waitJDialog("Create " + title, true, true);
        testA11Y(testDialog2, false, user);
        new JButtonOperator(JButtonOperator.findJButton(testDialog2, "Cancel", true, true)).push();
        waitClose("Create " + title);
    }
 
    public void waitClose(String name) {
        for (int a = 0; a < 10; a ++) {
            try { Thread.sleep(500); } catch (Exception e) {}
            if (JDialogOperator.findJDialog(name, true, true) == null)
                return;
        }
        assertTrue ("Dialog \"" + name + "\" is still visible", false);
    }
 
    public void selectTreeNode(ContainerOperator dia, String node) {
        new Node (new JTreeOperator (dia), node).select ();
    }

}
