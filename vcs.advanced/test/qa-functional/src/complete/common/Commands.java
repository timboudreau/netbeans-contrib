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

package complete.common;

import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.VCSCommandOperator;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.properties.*;
import org.netbeans.jemmy.operators.*;

/** XTest / JUnit test class performing check of Command editor.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class Commands extends org.netbeans.junit.NbTestCase {
    
    public static String MOUNT_MENU = "Versioning|Mount Version Control|Generic VCS";
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public Commands(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return Commands test suite.
     */
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new org.netbeans.junit.NbTestSuite();
        suite.addTest(new Commands("testCommandEditor"));
        suite.addTest(new Commands("testPopupSubmenu"));
        suite.addTest(new Commands("testPopupDevider"));
        suite.addTest(new Commands("testPopupAction"));
        suite.addTest(new Commands("testCustomizeMenu"));
        suite.addTest(new Commands("testSetupCommand"));
        suite.addTest(new Commands("testInvokeCommand"));
        suite.addTest(new Commands("testConfirmationMessages"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Method called before each testcase. Sets default timeouts, redirects system
     * output and maps main components.
     */
    protected void setUp() throws Exception {
        String workingDir = getWorkDirPath();
        new java.io.File(workingDir).mkdirs();
        java.io.File outputFile = new java.io.File(workingDir + "/output.txt");
        outputFile.createNewFile();
        java.io.File errorFile = new java.io.File(workingDir + "/error.txt");
        errorFile.createNewFile();
        java.io.PrintWriter outputWriter = new java.io.PrintWriter(new java.io.FileWriter(outputFile));
        java.io.PrintWriter errorWriter = new java.io.PrintWriter(new java.io.FileWriter(errorFile));
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(new org.netbeans.jemmy.TestOut(System.in, outputWriter, errorWriter));
    }
    
    /** Method will create a file and capture the screen together with saving the exception.
     */
    private void captureScreen(Exception exc) throws Exception {
        java.io.File dumpFile = new java.io.File("dump.png");
        try {
            dumpFile = new java.io.File(getWorkDirPath() + "/dump.png");
            java.io.File excFile = new java.io.File(getWorkDirPath() + "/exception.txt");
            dumpFile.getParentFile().mkdirs();
            dumpFile.createNewFile();
            java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(excFile));
            exc.printStackTrace(writer);
            writer.flush();
            writer.close();
        } catch(java.io.IOException e) {}
        org.netbeans.jemmy.util.PNGEncoder.captureScreen(dumpFile.getAbsolutePath());
    }
    
    /** Checks that command editor can be invoked and contains all of its components.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCommandEditor() throws Exception {
        try {
            System.out.print(".. Testing command editor ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile(VCSWizardProfile.EMPTY_UNIX);
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.verify();
            Node node = new Node(commandEditor.treeCommands(), "Empty");
            node.select();
            String[] commands = new String[] {"Refresh", "Refresh Recursively", "(separator)", "Check in", "Check out",
            "Lock", "Unlock", "Add", "Remove"};
            int count = commands.length;
            for (int i=0; i<count; i++) {
                node = new Node(commandEditor.treeCommands(), "Empty|" + commands[i]);
                node.select();
            }
            String[] properties = new String[] {"Label", "Name", "Label Mnemonic", "Exec", "Confirmation Message Before Execution",
            "Notification Message After Success", "Notification Message After Failure", "Input Descriptor", "Visible on Folders",
            "Visible on Files", "Visible on Root", "Run on Multiple Files", "Run on Multiple Files In Single Folder Only",
            "Refresh Processed Files", "Refresh Current Folder", "Refresh Parent Folder", "Refresh Even If Failed", "Display Output",
            "Distinguish Binary Files"};
            count = properties.length;
            PropertySheetOperator sheet = new PropertySheetOperator(commandEditor);
            PropertySheetTabOperator sheetTab = sheet.getPropertySheetTabOperator("Properties");
            for (int i=0; i<count; i++) new Property(sheetTab, properties[i]).startEditing();
            properties = new String[] {"Advanced Name", "Supports Advanced Mode", "Hidden", "Hidden Test Expression", "Disabled on Statuses",
            "Process All Files (Including .class)", "Delete Local Unimportant Files After Success", "Keep Hierarchical Order of Files",
            "Do Not Warn of Failure", "Refresh Recursively When Matched", "Refresh Recursively When Not Matched", "Reload Source Editor Content",
            "Data Regex", "Error Regex", "Global Data Regex", "Global Error Regex", "Input", "The Number of File Revisions to Execute On",
            "Is Changing File Revisions", "General Command Action Class Name", "General Command Action Display Name", "Commands Executed After Success",
            "Commands Executed After Failure"};
            sheetTab = sheet.getPropertySheetTabOperator("Expert");
            count = properties.length;
            for (int i=0; i<count; i++) new Property(sheetTab, properties[i]).startEditing();
            properties = new String[] {"File Index", "Removed File Index", "Status Index", "Substitutions of Status Strings", "Locker Index",
            "Revision Index", "Sticky Index", "Time Index", "Date Index", "Size Index", "Additional Attribute Index", "Read Both Data Outputs"};
            sheetTab = sheet.getPropertySheetTabOperator("Refresh Info");
            count = properties.length;
            for (int i=0; i<count; i++) new Property(sheetTab, properties[i]).startEditing();
            commandEditor.cancel();
            wizardAdvanced.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that it is possible to create sub menu of commands.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPopupSubmenu() throws Exception {
        try {
            System.out.print(".. Testing popup submenu ..");
            new java.io.File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile(VCSWizardProfile.EMPTY_UNIX);
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addFolder("Empty", "My Submenu");
            new Node(commandEditor.treeCommands(), "Empty|My Submenu").select();
            commandEditor.ok();
            wizardAdvanced.finish();
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action(null, "Empty|My Submenu").perform(filesystemNode);
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that it is possible to create popup menu devider.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPopupDevider() throws Exception {
        try {
            System.out.print(".. Testing popup devider ..");
            new java.io.File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile(VCSWizardProfile.EMPTY_UNIX);
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addFolder("Empty", "My Submenu");
            commandEditor.addSeparator("Empty|My Submenu");
            new Node(commandEditor.treeCommands(), "Empty|My Submenu|(separator)").select();
            commandEditor.ok();
            wizardAdvanced.finish();
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            javax.swing.MenuElement[] submenu = filesystemNode.callPopup().pushMenu("Empty|My Submenu", "|").getSubElements();
            if (submenu.length != 1) throw new Exception("Error: Popup menu devider is not present.");
            javax.swing.JPopupMenu item = (javax.swing.JPopupMenu) submenu[0];
            if (item.isValid()) throw new Exception("Error: Popup menu devider was not created.");
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that it is possible to create popup menu action.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPopupAction() throws Exception {
        try {
            System.out.print(".. Testing popup action ..");
            new java.io.File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            String profile = VCSWizardProfile.EMPTY_UNIX;
            if (org.openide.util.Utilities.isWindows()) profile = VCSWizardProfile.EMPTY_WIN;
            wizardProfile.setProfile(profile);
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addFolder("Empty", "My Submenu");
            commandEditor.addCommand("Empty|My Submenu", "Test");
            new Node(commandEditor.treeCommands(), "Empty|My Submenu|Test").select();
            commandEditor.ok();
            wizardAdvanced.finish();
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action(null, "Empty|My Submenu|Test").perform(filesystemNode);
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that it is possible to customize popup menu.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCustomizeMenu() throws Exception {
        try {
            System.out.print(".. Testing popup menu customization ..");
            new java.io.File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            String profile = VCSWizardProfile.EMPTY_UNIX;
            if (org.openide.util.Utilities.isWindows()) profile = VCSWizardProfile.EMPTY_WIN;
            wizardProfile.setProfile(profile);
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addFolder("Empty", "My Submenu");
            commandEditor.addCommand("Empty|My Submenu", "Test 1");
            commandEditor.addCommand("Empty|My Submenu", "Test 2");
            new Node(commandEditor.treeCommands(), "Empty|My Submenu|Test 1").select();
            Node test2Node = new Node(commandEditor.treeCommands(), "Empty|My Submenu|Test 2");
            test2Node.select();
            new Action(null, "Move Up").perform(test2Node);
            new CopyAction().perform(new Node(commandEditor.treeCommands(), "Empty|Add"));
            new PasteAction().perform(new Node(commandEditor.treeCommands(), "Empty|My Submenu"));
            new CutAction().perform(new Node(commandEditor.treeCommands(), "Empty|Lock"));
            new PasteAction().perform(new Node(commandEditor.treeCommands(), "Empty|My Submenu"));
            new DeleteAction().perform(new Node(commandEditor.treeCommands(), "Empty|Remove"));
            NbDialogOperator question = new NbDialogOperator("Confirm Object Deletion");
            new JLabelOperator(question, "Are you sure you want to delete Remove?");
            question.yes();
            commandEditor.ok();
            wizardAdvanced.finish();
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            javax.swing.MenuElement[] menu = filesystemNode.callPopup().pushMenu("Empty|My Submenu", "|").getSubElements();
            javax.swing.JPopupMenu submenu = (javax.swing.JPopupMenu) menu[0].getComponent();
            String[] commands = new String[] {"Test 2", "Test 1", "Add", "Lock"};
            int count = commands.length;
            for (int i=0; i<count; i++) {
                javax.swing.JMenuItem command = (javax.swing.JMenuItem) submenu.getComponent(i);
                if (!command.getText().equals(commands[i]))
                    throw new Exception("Error: Can't find \"Empty|My Submenu|" + commands[i] + "\" command.");
            }
            menu = filesystemNode.callPopup().pushMenu("Empty", "|").getSubElements();
            submenu = (javax.swing.JPopupMenu) menu[0].getComponent();
            count = submenu.getComponentCount();
            boolean foundAdd = false;
            for (int i=0; i<count; i++) {
                if (!submenu.getComponent(i).getClass().equals(javax.swing.JMenuItem.class)) continue;
                javax.swing.JMenuItem command = (javax.swing.JMenuItem) submenu.getComponent(i);
                if (command.getText().equals("Lock"))
                    throw new Exception("Error: Cut action doesn't work.");
                if (command.getText().equals("Remove"))
                    throw new Exception("Error: Delete action doesn't work.");
                if (command.getText().equals("Add")) foundAdd = true;
            }
            if (!foundAdd) throw new Exception("Error: Copy action doesn't work.");
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that it is possible to create and setup basic command.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testSetupCommand() throws Exception {
        try {
            System.out.print(".. Testing setup command ..");
            new java.io.File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            String profile = VCSWizardProfile.EMPTY_UNIX;
            if (org.openide.util.Utilities.isWindows()) profile = VCSWizardProfile.EMPTY_WIN;
            wizardProfile.setProfile(profile);
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addFolder("Empty", "My Submenu");
            commandEditor.addCommand("Empty|My Submenu", "Test");
            new Node(commandEditor.treeCommands(), "Empty|My Submenu|Test").select();
            String executionString = "sh -c \"echo Ahoj ${NAME}!\"";
            if (org.openide.util.Utilities.isWindows()) executionString = "cmd /x /c \"echo Ahoj ${NAME}!\"";
            commandEditor.setProperty("Empty|My Submenu|Test", "Properties", "Exec", executionString);
            commandEditor.setProperty("Empty|My Submenu|Test", "Properties", "Display Output", true);
            commandEditor.ok();
            wizardAdvanced.finish();
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action(null, "Empty|My Submenu|Test").perform(filesystemNode);
            VCSCommandsOutputOperator outputWindow = new VCSCommandsOutputOperator("Test");
            String correctExecutionString = "sh -c \"echo Ahoj !\"";
            if (org.openide.util.Utilities.isWindows()) correctExecutionString = "cmd /x /c \"echo Ahoj !\"";
            executionString = outputWindow.txtExecutionString().getText();
            outputWindow.close();
            if (!correctExecutionString.equals(executionString)) throw new Exception("Error: Can't set execution string.");
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that behaviour of new command is correct and variables work.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testInvokeCommand() throws Exception {
        try {
            System.out.print(".. Testing command invokation ..");
            new java.io.File(getWorkDirPath()).mkdirs();
            String fileName = getWorkDirPath() + java.io.File.separator + "A_File.java";
            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName));
            writer.write("public class A_File {\n  public static void main(String[] args) {\n  System.out.print(\"Ahoj \");\n  try {Thread.sleep(5000);} catch(Exception e){}\n  }\n}\n");
            writer.flush();
            writer.close();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            String profile = VCSWizardProfile.EMPTY_UNIX;
            if (org.openide.util.Utilities.isWindows()) profile = VCSWizardProfile.EMPTY_WIN;
            wizardProfile.setProfile(profile);
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editVariables();
            VariableEditor variableEditor = new VariableEditor();
            variableEditor.createVariable("Basic", "MYFILE");
            variableEditor.createVariable("Accessory", "MYNAME");
            new Node(variableEditor.treeVariables(), "Accessory|MYNAME").select();
            variableEditor.setVariableProperty("Accessory", "MYNAME", "Value", "Jirka");
            variableEditor.ok();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addFolder("Empty", "My Submenu");
            commandEditor.addCommand("Empty|My Submenu", "Test");
            new Node(commandEditor.treeCommands(), "Empty|My Submenu|Test").select();
            String executionString = "sh -c \"cd ${ROOTDIR}; java -cp . ${MYFILE}; echo ${MYNAME}!\"";
            String drive = getWorkDirPath().substring(0, 2);
            if (org.openide.util.Utilities.isWindows()) executionString = "cmd /x /c \"" + drive + "&& cd ${ROOTDIR}&& java -cp . ${MYFILE}&& echo ${MYNAME}!\"";
            commandEditor.setProperty("Empty|My Submenu|Test", "Properties", "Exec", executionString);
            commandEditor.setProperty("Empty|My Submenu|Test", "Properties", "Display Output", true);
            commandEditor.ok();
            wizardAdvanced.back();
            wizardProfile.txtJTextField(2).enterText("A_File");
            wizardProfile.finish();
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            filesystemNode.expand();
            Node fileNode = new Node(filesystemNode, "A_File");
            new CompileAction().perform(fileNode);
            Thread.sleep(2000);
            new Action(null, "Empty|My Submenu|Test").perform(filesystemNode);
            MainWindowOperator.getDefault().waitStatusText("Command Test is running ...");
            VCSCommandsOutputOperator outputWindow = new VCSCommandsOutputOperator("Test");
            new JButtonOperator(outputWindow, "Kill").pushNoBlock();
            new QuestionDialogOperator("Are you sure you want to kill the 'Test' command?").cancel();
            MainWindowOperator.getDefault().waitStatusText("Command Test finished.");
            Thread.sleep(2000);
            String output = outputWindow.txtStandardOutput().getText();
            outputWindow.close();
            if (output.indexOf("Ahoj Jirka!") == -1) throw new Exception("Error: Incorrect output reached: " + output);
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VariableEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that all of the confirmation message command properties work correctly.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testConfirmationMessages() throws Exception {
        try {
            System.out.print(".. Testing confirmation message properties ..");
            new java.io.File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            String profile = VCSWizardProfile.EMPTY_UNIX;
            if (org.openide.util.Utilities.isWindows()) profile = VCSWizardProfile.EMPTY_WIN;
            wizardProfile.setProfile(profile);
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addCommand("Empty", "Test");
            new Node(commandEditor.treeCommands(), "Empty|Test").select();
            String executionString = "sh -c \"cd ${PLACE}\"";
            if (org.openide.util.Utilities.isWindows()) executionString = "cmd /x /c \"cd ${PLACE}\"";
            commandEditor.setProperty("Empty|Test", "Properties", "Exec", executionString);
            commandEditor.setProperty("Empty|Test", "Properties", "Input Descriptor", "PROMPT_FOR(PLACE, Folder:)");
            commandEditor.setProperty("Empty|Test", "Properties", "Confirmation Message Before Execution", "Really proceed ?");
            commandEditor.setProperty("Empty|Test", "Properties", "Notification Message After Success", "Good run :-)");
            commandEditor.setProperty("Empty|Test", "Properties", "Notification Message After Failure", "Bad run :-(");
            commandEditor.ok();
            wizardAdvanced.finish();
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action(null, "Empty|Test").perform(filesystemNode);
            new QuestionDialogOperator("Really proceed ?").yes();
            VCSCommandOperator testCommand = new VCSCommandOperator("Test");
            new JTextFieldOperator(testCommand).setText(getWorkDirPath());
            testCommand.ok();
            NbDialogOperator confirmationMessage = new NbDialogOperator("Information");
            new JLabelOperator(confirmationMessage, "Good run :-)");
            confirmationMessage.ok();
            new Action(null, "Empty|Test").perform(filesystemNode);
            new QuestionDialogOperator("Really proceed ?").yes();
            testCommand = new VCSCommandOperator("Test");
            new JTextFieldOperator(testCommand).setText("abcd");
            testCommand.ok();
            confirmationMessage = new NbDialogOperator("Information");
            new JLabelOperator(confirmationMessage, "Bad run :-(");
            confirmationMessage.ok();
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new QuestionDialogOperator().no(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new NbDialogOperator("Information").ok(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }
}