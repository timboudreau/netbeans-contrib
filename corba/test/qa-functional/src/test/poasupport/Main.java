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

import java.io.PrintStream;
import java.util.StringTokenizer;
import javax.swing.ComboBoxModel;
import javax.swing.JDialog;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jellytools.RenameDialogOperator;
import org.netbeans.jellytools.actions.OpenAction;
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
import org.netbeans.jemmy.operators.Operator;
import util.Environment;
import util.Filter;
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
        return test;
    }
    
    ExplorerOperator exp = null;
    EventTool ev = null;
    PrintStream out = null;
    Filter filter = null;

    public void setUp () {
        exp = new ExplorerOperator ();
        ev = new EventTool ();
        out = getRef ();
        closeAllModal = true;
        filter = new Filter ();
        filter.addFilterAfter ("@author");
        filter.addFilterAfter ("Created on");
    }
    
    public void dumpFile (String node, String name) {
        new OpenAction ().perform (new Node (exp.repositoryTab ().tree (), node));
        ev.waitNoEvent (1000);
        EditorWindowOperator ewo = new EditorWindowOperator ();
        EditorOperator eo = ewo.getEditor (name);
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
        NewWizardOperator.create ("CORBA|" + template, "|data|poasupport|generate", name);
        dumpFile ("|data|poasupport|generate|" + name, name);
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
        NewWizardOperator.create ("CORBA|ServerMain", poaFolder, poaFileName);
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
        eo.close(true);
        ev.waitNoEvent(1000);

        new JavaNode (exp.repositoryTab ().tree (), poaNode).copy();
        ev.waitNoEvent(1000);
        
        new FolderNode (exp.repositoryTab ().tree (), poaFolder).pasteCopy();
        ev.waitNoEvent(1000);

        dumpFile(poaNode1, poaFileName1);
        compareReferenceFiles ();
    }
    
/*    
    public JDialog dialog = null;
    
    public void doAction(String node, String menu, String dialogname) throws Exception {
        info.println ("NODE: " + node);
        info.println ("MENU: " + menu);
//        new JFrameOperator(exp.getJFrame()).activate();
        exp.pushPopupMenuNoBlock (menu, poaRoot + node);
//        JHelper.produceAction(JHelper.createPushPopupMenuAction(JHelper.popupTreePath(JHelper.getExplorerTree(), poaRoot + node), menu, "|", true, true));
        dialog = JDialogOperator.waitJDialog(dialogname, true, true);
    }
    
    public void rename(String node, String name) throws Exception {
        info.println ("NODE: " + node);
        info.println ("RENAME: " + name);
        doAction(node, "Rename...", "Rename");
        JHelper.findJTextFieldOperator(dialog, 0).clearText();
        JHelper.findJTextFieldOperator(dialog, 0).typeText(name);
        JHelper.findJButtonOperator(dialog, "OK").push();
    }
    
    public void acceptErrorDialog() throws Exception {
        final JDialog finaldialog = dialog;
        JHelper.produceAction(new com.sun.jemmy.Action() {
            public Object launch(Object obj) {
                try {
                    JHelper.findJTextFieldOperator(finaldialog, 1).clickMouse(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            public String getDescription() {
                return "ClickMouse";
            }
        });
        JHelper.sleep(1000);
        JDialog d = JDialogOperator.findJDialog("Error", true, true);
        if (d != null) {
            JHelper.findJButtonOperator(d, "OK").push();
            JHelper.sleep(1000);
        }
    }
    
    public static String removeDataFromFile(String str) {
        int index, index2;
        index = str.indexOf("Created on ");
        if (index < 0)
            return str;
        index2 = str.indexOf("* with ", index);
        if (index2 < 0)
            return str;
        str = str.substring(0, index + 11) + str.substring(index2);
        
        index = str.indexOf("@author");
        if (index < 0)
            return str;
        index2 = str.indexOf("@version", index);
        if (index2 < 0)
            return str;
        return str.substring(0, index + 7) + str.substring(index2);
    }
    
    public static Node findSubNode (Node node, String name) {
        if (node == null)
            return null;
        Node[] nodes = node.getChildren ().getNodes ();
        for (int a = 0; a < nodes.length; a ++) {
//            System.out.println(">"+nodes [a].getDisplayName ());
            if (nodes [a].getDisplayName ().equals (name))
                return nodes [a];
        }
        return null;
    }
    
    public static void saveDocument (DataObject dao) {
        int a = 0;
        for (;;) {
            try {
                ((EditorCookie) dao.getCookie(EditorCookie.class)).saveDocument();
                return;
            } catch (IOException e) {
                if (++ a > 10)
                    throw new RunException (e);
            }
            try { Thread.sleep (1000); } catch (Exception e) {}
        }
    }
    
    public void runTest() {
        exp = Explorer.find ();
        out.addAfterFilter(Environment.findORBEnvironmentObject ("OB4X").getORBName ());
        ArrayList filter = new ArrayList ();
        filter.add (Environment.findORBEnvironmentObject ("OB4X").getORBName ());
        try {
            Environment.loadORBEnvironment("OB4X");
            String ORBImpl = Environment.findORBEnvironmentObject ("OB4X").getName ();
            String poaRootDir = Environment.getWorkDirDisplayName () + "|data|poasupport";
            String poaFileName = "POAFile1";
            String str;
            JDialog d;
            ComponentOperator co;
            
            poaRoot = poaRootDir + "|" + poaFileName + "|class " + poaFileName + "|RootPOA";

            CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject (CORBASupportSettings.class);
            css.setOrb (ORBImpl);
/ *            new JFrameOperator(JHelper.getProjectSettingsFrame()).activate();
            JHelper.produceAction(JHelper.createPushPopupMenuAction(JHelper.popupTreePath(JHelper.getProjectSettingsTree(), "CORBA Settings"), "Properties", "|", true, true));
            JHelper.sleep(1000);
            co = JHelper.getPropertyEditor(JHelper.getPropertyPanel("CORBA Settings"), "Properties", "ORB Version");
            JHelper.sleep(1000);
            JHelper.setJComboBoxProperty(co, ORBImpl, false, false);* /
            
            css.getActiveSetting ().setServerBindingFromString ("IOR to standard output");
/ *            new JFrameOperator(JHelper.getProjectSettingsFrame()).activate();
            JHelper.produceAction(JHelper.createPushPopupMenuAction(JHelper.popupTreePath(JHelper.getProjectSettingsTree(), "CORBA Settings|" + ORBImpl), "Properties", "|", true, true));
            JHelper.sleep(1000);
            co = JHelper.getPropertyEditor(JHelper.getPropertyPanel(ORBImpl), "Properties", "Server Binding");
            JHelper.sleep(1000);
            JHelper.setJComboBoxProperty(co, "IOR to standard output", false, false);* /

            exp.switchToFilesystemsTab();
            createPOAFile(poaRootDir, poaFileName);
            exp.selectNode(poaRootDir + "|" + poaFileName + "|class " + poaFileName);
            JHelper.sleep (2000);
            exp.selectNode(poaRoot);
            
            doAction("", "Add|Child POA", "New Child POA");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "MyChildPOA_DefaultValues");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "var_MyChildPOA_DefaultValues");
            JHelper.findJButtonOperator(dialog, "OK").push();
            
            doAction("", "Add|Child POA", "New Child POA");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "Parent_of_children");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "var_Parent_of_children");
            JHelper.findJButtonOperator(dialog, "OK").push();
            
            doAction("|Parent_of_children", "Add|Child POA", "New Child POA");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "1.child Parent_of_children");
            //        acceptErrorDialog ();
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "var_1_child_Parent_of_children");
            JHelper.findJComboBoxOperator(dialog, -1, 1).selectItem("SINGLE_THREAD_MODEL", true, true);
            JHelper.findJComboBoxOperator(dialog, -1, 2).selectItem("PERSISTENT", true, true);
            JHelper.findJComboBoxOperator(dialog, -1, 3).selectItem("MULTIPLE_ID", true, true);
            JHelper.findJComboBoxOperator(dialog, -1, 4).selectItem("USER_ID", true, true);
            final JDialog finaldialog1 = dialog;
            JHelper.produceAction(new com.sun.jemmy.Action() {
                public Object launch(Object obj) {
                    try {
                        JHelper.findJComboBoxOperator(finaldialog1, -1, 5).selectItem("NON_RETAIN", true, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                public String getDescription() {
                    return "Select NON_RETAIN";
                }
            });
            d = JDialogOperator.waitJDialog(dialog, "Warning", true, true);
            JHelper.findJComboBoxOperator(d, -1, 0).selectItem("USE_SERVANT_MANAGER", true, true);
            JHelper.findJButtonOperator(d, "OK").push();
            JHelper.findJButtonOperator(dialog, "OK").push();
            
            doAction("|Parent_of_children", "Add|Child POA", "New Child POA");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "2.child Parent_of_children");
            //        acceptErrorDialog ();
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "var_2_child_Parent_of_children");
            final JDialog finaldialog2 = dialog;
            JHelper.produceAction(new com.sun.jemmy.Action() {
                public Object launch(Object obj) {
                    try {
                        JHelper.findJComboBoxOperator(finaldialog2, -1, 5).selectItem("NON_RETAIN", true, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                public String getDescription() {
                    return "Select NON_RETAIN";
                }
            });
            d = JDialogOperator.waitJDialog(dialog, "Warning", true, true);
            JHelper.findJComboBoxOperator(d, -1, 0).selectItem("USE_SERVANT_MANAGER", true, true);
            JHelper.findJButtonOperator(d, "OK").push();
            JHelper.findJComboBoxOperator(dialog, -1, 7).selectItem("IMPLICIT_ACTIVATION", true, true);
            JHelper.findJButtonOperator(dialog, "OK").push();
            
            doAction("|Parent_of_children", "Add|Child POA", "New Child POA");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "3.child Parent_of_children");
            //        acceptErrorDialog ();
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "var_3_child_Parent_of_children");
            JHelper.findJButtonOperator(dialog, "OK").push();
            
            doAction("|Parent_of_children|3.child Parent_of_children", "Customize", "Customizer Dialog");
            JHelper.findJComboBoxOperator(dialog, -1, 6).selectItem("USE_SERVANT_MANAGER", true, true);
            JHelper.findJButtonOperator(dialog, "Close").push();
            
            doAction("|Parent_of_children", "Add|Child POA", "New Child POA");
            JHelper.findJComboBoxOperator(dialog, -1, 4).selectItem("USER_ID", true, true);
            JHelper.findJComboBoxOperator(dialog, -1, 7).selectItem("IMPLICIT_ACTIVATION", true, true);
            JHelper.findJButtonOperator(dialog, "Cancel").push();
            
            doAction("|Parent_of_children|3.child Parent_of_children", "Add|POA Activator", "New POA Activator");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "var_adapter_activator");
            JHelper.findJCheckBoxOperator(dialog, "Generate activator instantiation code", true, true).push();
            JHelper.findJButtonOperator(dialog, "OK").push();
            doAction("|Parent_of_children|3.child Parent_of_children|var_adapter_activator", "Customize", "Customizer Dialog");
            JHelper.produceAction(JHelper.createPushCheckBoxAction(JHelper.findJCheckBoxOperator(dialog, "Generate activator instantiation code", true, true)));
            JHelper.sleep(1000);
            //        JHelper.findJCheckBoxOperator(dialog, "Generate activator instantiation code", true, true).push ();
            JHelper.findJTextFieldOperator(dialog, 1).clearText();
            JHelper.findJTextFieldOperator(dialog, 1).typeText("MyAdapterActivator");
            //        JHelper.findJComboBoxOperator(dialog, -1, 0).clearText ();
            //        JHelper.findJComboBoxOperator(dialog, -1, 0).typeText ("MyAdapterActivator");
            //        JHelper.setText (JHelper.findJComboBoxOperator(dialog, -1, 1), "MyAdapterActivator()");
            JHelper.findJButtonOperator(dialog, "Close").push();
            
            doAction("|Parent_of_children|3.child Parent_of_children", "Add|Servant Manager", "New Servant Manager");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "var_servant_manager");
            JHelper.findJCheckBoxOperator(dialog, "Generate servant manager instantiation code", true, true).push();
            JHelper.findJButtonOperator(dialog, "OK").push();
            doAction("|Parent_of_children|3.child Parent_of_children|var_servant_manager", "Customize", "Customizer Dialog");
            JHelper.produceAction(JHelper.createPushCheckBoxAction(JHelper.findJCheckBoxOperator(dialog, "Generate servant manager instantiation code", true, true)));
            JHelper.sleep(1000);
            //        JHelper.findJCheckBoxOperator(dialog, "Generate servant manager instantiation code", true, true).push ();
            JHelper.findJTextFieldOperator(dialog, 1).clearText();
            JHelper.findJTextFieldOperator(dialog, 1).typeText("MyServantManager");
            //        JHelper.findJComboBoxOperator(dialog, -1, 0).clearText ();
            //        JHelper.findJComboBoxOperator(dialog, -1, 0).typeText ("MyServantManager");
            //        JHelper.setText (JHelper.findJComboBoxOperator(dialog, -1, 1), "MyServantManager()");
            JHelper.findJButtonOperator(dialog, "Close").push();
            
            doAction("|Parent_of_children", "Add|Child POA", "New Child POA");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "4.child Parent_of_children");
            //        acceptErrorDialog ();
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "var_4_child_Parent_of_children");
            JHelper.findJButtonOperator(dialog, "OK").push();
            
            doAction("|Parent_of_children|4.child Parent_of_children", "Add|Child POA", "New Child POA");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "1.child of 4.child Parent_of_children");
            //        acceptErrorDialog ();
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "var_1_4_child_Parent_of_children");
            JHelper.findJButtonOperator(dialog, "OK").push();
            
//            new JFrameOperator(exp.getJFrame()).activate();
/*            JHelper.selectTreePath(JHelper.getExplorerTree(), poaRoot + "|Parent_of_children|4.child Parent_of_children|1.child of 4.child Parent_of_children", "|");
            JHelper.sleep(3000);
            JHelper.setJComboBoxProperty(JHelper.getPropertyEditor(JHelper.getPropertyPanel(), "Properties", "POA Manager"), "var_4_child_Parent_of_children", true, true);
            JHelper.sleep(2000);
            JHelper.selectTreePath(JHelper.getExplorerTree(), poaRoot + "|Parent_of_children|4.child Parent_of_children", "|");
            JHelper.sleep(3000);
            JHelper.setJComboBoxProperty(JHelper.getPropertyEditor(JHelper.getPropertyPanel(), "Properties", "POA Manager"), "var_Parent_of_children", true, true);
            JHelper.sleep(2000);
            JHelper.selectTreePath(JHelper.getExplorerTree(), poaRoot + "|Parent_of_children|4.child Parent_of_children|1.child of 4.child Parent_of_children", "|");
            JHelper.sleep(3000);* /
            //        ref.println (JHelper.getPropertyEditor(JHelper.getPropertyPanel(), "Properties", "POA Manager"));
            
            doAction("|Parent_of_children", "Add|Child POA", "New Child POA");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "5.child Parent_of_children");
            //        acceptErrorDialog ();
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "var_5_child_Parent_of_children");
            JHelper.findJButtonOperator(dialog, "OK").push();
            
            doAction("|Parent_of_children|5.child Parent_of_children", "Customize", "Customizer Dialog");
            JHelper.findJComboBoxOperator(dialog, -1, 6).selectItem("USE_DEFAULT_SERVANT", true, true);
            JHelper.findJButtonOperator(dialog, "Close").push();
            
            doAction("|Parent_of_children|5.child Parent_of_children", "Add|Default Servant", "New Default Servant");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "var_default_servant");
            JHelper.findJCheckBoxOperator(dialog, "Generate default servant instantiation code", true, true).push();
            JHelper.findJButtonOperator(dialog, "OK").push();
            doAction("|Parent_of_children|5.child Parent_of_children|var_default_servant", "Customize", "Customizer Dialog");
            JHelper.produceAction(JHelper.createPushCheckBoxAction(JHelper.findJCheckBoxOperator(dialog, "Generate default servant instantiation code", true, true)));
            JHelper.sleep(1000);
            //        JHelper.findJCheckBoxOperator(dialog, "Generate default servant instantiation code", true, true).push ();
            JHelper.findJTextFieldOperator(dialog, 1).clearText();
            JHelper.findJTextFieldOperator(dialog, 1).typeText("HelloWorldImpl");
            //        JHelper.findJComboBoxOperator(dialog, -1, 0).clearText ();
            //        JHelper.findJComboBoxOperator(dialog, -1, 0).typeText ("HelloWorldImpl");
            //        JHelper.setText (JHelper.findJComboBoxOperator(dialog, -1, 1), "HelloWorldImpl()");
            JHelper.findJButtonOperator(dialog, "Close").push();
            
            doAction("|Parent_of_children", "Add|Servant", "New Servant");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 0), "HelloWorld");
            JHelper.setText(JHelper.findJTextFieldOperator(dialog, 1), "HelloWorld_ID");
            JHelper.findJButtonOperator(dialog, "OK").push();
/*
        new JFrameOperator (JHelper.getExplorerFrame ()).activate ();
        JHelper.selectTreePath(JHelper.getExplorerTree(), poaRoot, "|");
        JHelper.sleep (1000);
        new JFrameOperator (JHelper.getPropertyFrame ()).activate ();
        JHelper.setJTextFieldProperty(JHelper.getPropertyEditor(JHelper.getPropertyPanel(), "Properties", "POA Variable"), "poa_root");
        JHelper.sleep (1000);
        JHelper.getExplorerTree ().clickMouse (1);
 * /
            rename("|Parent_of_children", "PaReNt");
/*            
            new JFrameOperator(JHelper.getExplorerFrame()).activate();
            JHelper.selectTreePath(JHelper.getExplorerTree(), poaRootDir + "|" + poaFileName, "|");
            JHelper.sleep(1000);
            JHelper.getMainMenu().pushMenu("File|Save", "|", true, true);
            JHelper.sleep(1000);
* /            
            FileObject fo = Environment.findWorkFileObject (requiredFolders[0] + "POAFile1.java");
            DataObject dao = DataObject.find (fo);
            JHelper.sleep (5000);
            saveDocument (dao);

            out.println("--------------------------");
//            new JFrameOperator(exp.getJFrame()).activate();
            
//            System.out.println(fo);
            Node n = Helper.findFileObjectNode(fo);
//            System.out.println(n);
            n = findSubNode (n, "class POAFile1");
//            System.out.println(n);
            n = findSubNode (n, "RootPOA");
//            System.out.println(n);
            Helper.printNode(n, out.createPrintWriter(), filter);
            out.closePrintWriter();

//            JHelper.printNodesProperties(JHelper.getExplorerTree(), poaRoot, out.createPrintWriter());
//            out.closePrintWriter();

            out.println("--------------------------");
            JFrame frame;
//            new JFrameOperator(exp.getJFrame ()).activate();
            exp.pushPopupMenu("Open", poaRootDir + "|HelloWorldInterfaceImpl");
            frame = JFrameOperator.waitJFrame("Source Editor", false, false);
            JHelper.sleep (3000);
//            new JFrameOperator(exp.getJFrame ()).activate();
            exp.pushPopupMenu("Open", poaRootDir + "|" + poaFileName);
            frame = JFrameOperator.waitJFrame("Source Editor", false, false);
            JHelper.sleep (3000);
//            JHelper.printComponent (frame);
            JTabbedPane pane = JTabbedPaneOperator.waitJTabbedPane(frame, "", false, false, -1);
            Component com = new JTabbedPaneOperator(pane).selectPage(poaFileName, false, false);
            str = new JTextComponentOperator(JTextComponentOperator.waitJTextComponent((JComponent) com, "", false, false)).getText();
            str = removeDataFromFile(str);
            out.println(str);
/*            
            new JFrameOperator(JHelper.getExplorerFrame()).activate();
            JHelper.popupTreePath(JHelper.getExplorerTree(), poaRootDir + "|" + poaFileName, "|", "Copy", "|");
            new JFrameOperator(JHelper.getExplorerFrame()).activate();
            JHelper.popupTreePath(JHelper.getExplorerTree(), poaRootDir, "|", "Paste|Copy", "|");
            JHelper.sleep(3000);
            JHelper.popupTreePath(JHelper.getExplorerTree(), poaRootDir + "|" + poaFileName + "_1|class "  + poaFileName + "_1|RootPOA|PaReNt", "|", "Delete", "|");
            JHelper.waitClickDialog("Confirm Object Deletion", "Yes");
            JHelper.waitClickDialog("Question", "Yes");
            
            out.println("--------------------------");
            new JFrameOperator(JHelper.getExplorerFrame()).activate();
            JHelper.popupTreePath(JHelper.getExplorerTree(), poaRootDir + "|" + poaFileName + "_1", "|", "Open", "|");
            frame = JFrameOperator.waitJFrame("Source Editor", false, false);
            pane = JTabbedPaneOperator.waitJTabbedPane(frame, "", false, false, -1);
            com = new JTabbedPaneOperator(pane).selectPage(poaFileName + "_1", false, false);
            JHelper.sleep(2000);
            JHelper.printComponent ((JComponent) com);
            str = new JTextComponentOperator(JTextComponentOperator.waitJTextComponent((JComponent) com, "", false, false)).getText();
            str = removeDataFromFile(str);
            out.println(str);
            
            new JFrameOperator(JHelper.getExplorerFrame()).activate();
            JHelper.selectTreePath(JHelper.getExplorerTree(), poaRootDir + "|" + poaFileName + "_1", "|");
            JHelper.sleep(3000);
            JHelper.getMainMenu().pushMenu("File|Save", "|", true, true);* /
        } catch (Exception e) {
            throw new RunException(e);
        }
    }
*/    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
