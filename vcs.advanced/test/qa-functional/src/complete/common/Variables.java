/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package complete.common;

import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.properties.*;

/** XTest / JUnit test class performing check of Variable editor.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class Variables extends JellyTestCase {
    
    public static String MOUNT_MENU = "Versioning|Mount Version Control|Generic VCS";
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public Variables(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return Variables test suite.
     */
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new org.netbeans.junit.NbTestSuite();
        suite.addTest(new Variables("testVariableEditor"));
        suite.addTest(new Variables("testAccessoryVariable"));
        suite.addTest(new Variables("testBasicVariable"));
        suite.addTest(new Variables("testCustomizeBasicVariable"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Method called before each testcase to redirect system output.
     */
    protected void setUp() throws Exception {
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(org.netbeans.jemmy.TestOut.getNullOutput());
    }
    
    /** Checks that variable editor can be invoked and contains all of its components.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testVariableEditor() throws Exception {
        try {
            System.out.print(".. Testing variable editor ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
//            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile(VCSWizardProfile.EMPTY_UNIX);
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editVariables();
            VariableEditor variableEditor = new VariableEditor();
            variableEditor.verify();
            Node node = new Node(variableEditor.treeVariables(), "Basic");
            node.select();
            node = new Node(variableEditor.treeVariables(), "Accessory");
            node.select();
            variableEditor.cancel();
            wizardAdvanced.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VariableEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that accessory variable has its properties, can be created and deleted.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAccessoryVariable() throws Exception {
        try {
            System.out.print(".. Testing accessory variable ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
//            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile(VCSWizardProfile.EMPTY_UNIX);
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editVariables();
            VariableEditor variableEditor = new VariableEditor();
            Node accessoryNode = new Node(variableEditor.treeVariables(), "Accessory");
            Node node = new Node(accessoryNode, "NAME2");
            node.select();
            Thread.sleep(1000);
            new DeleteAction().perform(node);
            NbDialogOperator question = new NbDialogOperator("Confirm Object Deletion");
            new org.netbeans.jemmy.operators.JLabelOperator(question, "Are you sure you want to delete NAME2?");
            question.yes();
            if (node.isPresent()) throw new Exception("Error: Can't delete accessory variable.");
            variableEditor.createVariable("Basic", "TOWN");
            variableEditor.createVariable("Accessory", "INPUT_DESCRIPTOR");
            node = new Node(accessoryNode, "INPUT_DESCRIPTOR");
            node.select();
            PropertySheetOperator sheet = new PropertySheetOperator(variableEditor);
            Property property = new Property(sheet, "Name");
            property.setValue("CONFIG_INPUT_DESCRIPTOR");
            node = new Node(accessoryNode, "CONFIG_INPUT_DESCRIPTOR");
            node.select();
            property = new Property(sheet, "Value");
            property.setValue("ASK_FOR(NAME, \"Married ?\")");
            Node basicNode = new Node(variableEditor.treeVariables(), "Basic");
            if (basicNode.getChildren().length != 0) throw new Exception("Error: CONFIG_INPUT_DESCRIPTOR doesn't collapse basic variables.");
            variableEditor.ok();
            wizardAdvanced.back();
            new org.netbeans.jemmy.operators.JCheckBoxOperator(wizardProfile, "Married ?");
            wizardProfile.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VariableEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that basic variable can be created and deleted.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testBasicVariable() throws Exception {
        try {
            System.out.print(".. Testing basic variable ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
//            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile(VCSWizardProfile.EMPTY_UNIX);
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editVariables();
            VariableEditor variableEditor = new VariableEditor();
            variableEditor.createVariable("Basic", "TOWN");
            Node basicNode = new Node(variableEditor.treeVariables(), "Basic");
            Node node = new Node(basicNode, "TOWN");
            node.select();
            String[] properties = new String[] {"Name", "Label", "Label Mnemonic", "Accessibility Name", "Accessibility Description",
            "Order", "Value", "Variable Selector", "Variable Is a Local File", "Variable Is a Local Folder"};
            int count = properties.length;
            PropertySheetOperator sheet = new PropertySheetOperator(variableEditor);
            for (int i=0; i<count; i++) new Property(sheet, properties[i]).setDefaultValue();
            new DeleteAction().perform(node);
            NbDialogOperator question = new NbDialogOperator("Confirm Object Deletion");
            new org.netbeans.jemmy.operators.JLabelOperator(question, "Are you sure you want to delete TOWN?");
            question.yes();
            if (node.isPresent()) throw new Exception("Error: Can't delete basic variable.");
            variableEditor.ok();
            wizardAdvanced.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VariableEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that basic variable has its properties and appropriate behaviour in profile tab.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCustomizeBasicVariable() throws Exception {
        try {
            System.out.print(".. Testing basic variable customization ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
//            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile(VCSWizardProfile.EMPTY_UNIX);
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editVariables();
            VariableEditor variableEditor = new VariableEditor();
            variableEditor.createVariable("Basic", "VAR");
            new Node(variableEditor.treeVariables(), "Basic|VAR").select();
            PropertySheetOperator sheet = new PropertySheetOperator(variableEditor);
            Property property = new Property(sheet, "Name");
            property.setValue("SLOT");
            property = new Property(sheet, "Label");
            property.setValue("What is this ?");
            new Node(variableEditor.treeVariables(), "Basic|What is this ?").select();
            property = new Property(sheet, "Value");
            property.setValue("Empty slot");
            property = new Property(sheet, "Variable Is a Local File");
            property.setValue("true");
            variableEditor.ok();
            wizardAdvanced.back();
            new org.netbeans.jemmy.operators.JLabelOperator(wizardProfile, "What is this ?:");
            new org.netbeans.jemmy.operators.JTextFieldOperator(wizardProfile, "Empty slot");
            new org.netbeans.jemmy.operators.JButtonOperator(wizardProfile, "Browse...", 1).pushNoBlock();
            new NbDialogOperator("Select File:").cancel();
            wizardProfile.next();
            wizardAdvanced.editVariables();
            variableEditor = new VariableEditor();
            variableEditor.createVariable("Basic", "VAR");
            Node varNode = new Node(variableEditor.treeVariables(), "Basic|VAR");
            varNode.select();
            sheet = new PropertySheetOperator(variableEditor);
            property = new Property(sheet, "Variable Is a Local Folder");
            property.setValue("true");
            new Action(null, "Move Up").perform(varNode);
            variableEditor.ok();
            wizardAdvanced.back();
            new org.netbeans.jemmy.operators.JButtonOperator(wizardProfile, "Browse...", 1).pushNoBlock();
            new NbDialogOperator("Select Directory:").cancel();
            wizardProfile.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VariableEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }
}