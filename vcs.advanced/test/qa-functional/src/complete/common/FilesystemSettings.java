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

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.test.oo.gui.jello.JelloBundle;
import org.netbeans.test.oo.gui.jello.JelloOKOnlyDialog;
import org.netbeans.test.oo.gui.jelly.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.TestOut;
import org.netbeans.test.oo.gui.jelly.vcscore.*;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.wizard.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.properties.*;

/** XTest / JUnit test class performing check of all settings of filesystems mounted
 * using Generic VCS module.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class FilesystemSettings extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String workingDirectory;
    public static String filesystem;
    private APIController api;
    private Explorer explorer;
    private APICommandsHistory history;
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public FilesystemSettings(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition. This suite contains following test cases:
     * testUnmount, testPopupMenu, testRuntimeTab and testFindService.
     * @return GenericVCSAvailabilityTest test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new FilesystemSettings("testAnnotationPattern"));
        suite.addTest(new FilesystemSettings("testCommandNotification"));
        suite.addTest(new FilesystemSettings("testProcessAllFiles"));
        suite.addTest(new FilesystemSettings("testIgnoredFiles"));
        suite.addTest(new FilesystemSettings("testBackupProperties"));
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
    protected void setUp() {
        JellyProperties.setDefaults();
        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
        api = new APIController ();
        api.setOut(new PrintWriter(System.out, true));
        explorer = api.getExplorer();
        history = new APICommandsHistory(api);
    }
    
    /** Method called after each testcase. Resets Jemmy WaitComponentTimeout.
     */
    protected void tearDown() {
        JellyProperties.setDefaults();
    }
    
    /** Mounts new filesystem using given profile.
     * @param profile Profile that should be used in wizard.
     * @throws Any unexpected exception thrown during test.
     */
    private void mountFilesystem(String profile) throws Exception {
        MainFrame.getMainFrame().pushMenuNoBlock(MOUNT_MENU);
        VCSWizardProfile wizard = new VCSWizardProfile();
        wizard.setWorkingDirectory(workingDirectory);
        wizard.checkOnlyCompatibleProfiles(false);
        wizard.setProfile(profile);
        APIController.sleep(1000);
        wizard.finish();
    }
    
    /** Selects desired node in explorer.
     * @param node Path to the node like "CVS D:\test", "A_File [Local]".
     * @param exactly Should the path be compared exactly ?
     * @throws Any unexpected exception thrown during test.
     */
    public void selectNode(String[] node, boolean exactly) throws Exception {
        JTreeOperator operator = explorer.getJTreeOperator();
        javax.swing.tree.TreePath path = operator.findPath(node, exactly, true);
        operator.selectPath(path);
    }

    /** Creates certain testing contents of the filesystem.
     * @throws Any unexpected exception thrown during test.
     */
    public void createContents() throws Exception {
        createFile(workingDirectory + File.separator + "A_File.java", true);
        createFile(workingDirectory + File.separator + "A_File.class", true);
        api.getFilesystemsTab();
        APIController.sleep(20000);
        selectNode(new String[] {filesystem, "A_File"}, false);
    }
    /** Creates new file or directory at given place.
     * @param name Name of directory or file to create.
     * @param isFile Should file be created or directory ?
     * @throws Any unexpected exception thrown during test.
     */
    public void createFile(String name, boolean isFile) throws Exception {
        if (isFile) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(name));
            writer.write("/** This is testing file.\n */\n\n public class Testing_File {\n }\n");
            writer.flush();
            writer.close();
        }
        else new File(name).mkdirs();
    }
    /** Checks whether "Annotation Pattern" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testAnnotationPattern() throws Exception {
        System.out.print(".. Testing annotation pattern property ..");
        workingDirectory = getWorkDir().getAbsolutePath();
        filesystem = "Empty " + workingDirectory;
        mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
        APIController.sleep(2000);
        createContents();
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        explorer.pushPopupMenu("Properties", filesystem);
        PropertiesWindow properties = new PropertiesWindow(filesystem);
        String property = JelloBundle.getString("org.netbeans.modules.vcs.advanced.Bundle", "PROP_annotationPattern");
        properties.edit(property);
        properties.setText(">> ${fileName} $[? attribute][[Yes]][[No]] <<");
        String node = filesystem + "|>> A_File [No] <<";
        assertNotNull("Can't select " + node, api.getFilesystemsTab().selectNode(node));
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        MainFrame.getMainFrame().pushMenu(UNMOUNT_MENU);
        System.out.println(". done !");
    }
    
    /** Checks whether "Command Notification" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testCommandNotification() throws Exception {
        System.out.print(".. Testing command notification property ..");
        filesystem = "CVS " + workingDirectory;
        mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.CVS_UNIX : VCSWizardProfile.CVS_WIN_NT);
        APIController.sleep(2000);
        api.getFilesystemsTab();
        selectNode(new String[] {filesystem, "A_File"}, false);
        MainFrame.getMainFrame().pushMenuNoBlock(VERSIONING_MENU + "|CVS|Editing|Edit");
        new JelloOKOnlyDialog("Information").ok();
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        explorer.pushPopupMenu("Properties", filesystem);
        PropertiesWindow properties = new PropertiesWindow(filesystem);
        String property = JelloBundle.getString("org.netbeans.modules.vcs.advanced.Bundle", "PROP_commandNotification");
        properties.edit(property);
        properties.setFalse();
        selectNode(new String[] {filesystem, "A_File"}, false);
        MainFrame.getMainFrame().pushMenuNoBlock(VERSIONING_MENU + "|CVS|Editing|Edit");
        long oldTimeout = JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 5000);
        try {
            new JDialogOperator("Information");
            new JelloOKOnlyDialog("Information").ok();
            JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            fail("Command Notification property does not work.");
        } catch (org.netbeans.jemmy.TimeoutExpiredException e) {}
        JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
        selectNode(new String[] {filesystem}, true);
        MainFrame.getMainFrame().pushMenu(UNMOUNT_MENU);
        System.out.println(". done !");
    }

    /** Checks whether "Process All Files" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testProcessAllFiles() throws Exception {
        System.out.print(".. Testing process all files property ..");
        filesystem = "Empty " + workingDirectory;
        mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
        APIController.sleep(2000);
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        explorer.pushPopupMenu("Properties", filesystem);
        PropertiesWindow properties = new PropertiesWindow(filesystem);
        String property = JelloBundle.getString("org.netbeans.modules.vcs.advanced.Bundle", "PROP_processAllFiles");
        properties.edit(property);
        properties.setTrue();
        properties.close();
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        selectNode(new String[] {filesystem, "A_File"}, false);
        MainFrame.getMainFrame().pushMenu(VERSIONING_MENU+"|Empty|Lock");
        RuntimeTabOperator explorer = new ExplorerOperator().runtimeTab();
        Node commandsHistory = new Node(explorer.getRootNode(), "VCS Commands|" + filesystem);
        commandsHistory.tree().expandPath(commandsHistory.getTreePath());
        int count = commandsHistory.getChildren().length;
        boolean found = false;
        for(int i=0; i<count; i++) {
            Node command = new Node(commandsHistory, i);
            if (!command.getText().equals("Lock")) continue;
            command.select();
            PropertiesAction propertiesAction = new PropertiesAction();
            propertiesAction.perform(command);
            PropertySheetOperator sheet = new PropertySheetOperator();
            StringProperty files = new StringProperty(sheet.getPropertySheetTabOperator("Properties"), "Processed Files");
            if (files.getStringValue().equals("A_File.class")) {
                found = true;
                sheet.close();
                break;
            }
            sheet.close();
        }
        if (!found) throw new Exception("Error: Unable to find Lock command processed on A_File.class.");
        api.getFilesystemsTab();
        selectNode(new String[] {filesystem}, true);
        MainFrame.getMainFrame().pushMenu(UNMOUNT_MENU);
        System.out.println(". done !");
    }

    /** Checks whether "Ignored Files" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testIgnoredFiles() throws Exception {
        System.out.print(".. Testing ignored files property ..");
        filesystem = "Empty " + workingDirectory;
        mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
        APIController.sleep(2000);
        RepositoryTabOperator explorer = new ExplorerOperator().repositoryTab();
        Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
        filesystemNode.tree().expandPath(filesystemNode.getTreePath());
        Node fileNode = new Node(explorer.getRootNode(), filesystem + "|A_File");
        fileNode.select();
        PropertiesAction propertiesAction = new PropertiesAction();
        propertiesAction.perform(filesystemNode);
        PropertySheetOperator sheet = new PropertySheetOperator();
        StringProperty ignoredFiles = new StringProperty(sheet.getPropertySheetTabOperator("Expert"), "Ignored Files");
        ignoredFiles.setStringValue("A_File");
        sheet.close();
        if (filesystemNode.getChildren().length != 0) throw new Exception("Error: A_File node has not disappeared.");
        api.getFilesystemsTab();
        filesystemNode.select();
        MainFrame.getMainFrame().pushMenu(UNMOUNT_MENU);
        System.out.println(". done !");
    }

    /** Checks whether "Filter Backup Files" and "Create Backup Files" properties work correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testBackupProperties() throws Exception {
        System.out.print(".. Testing backup files properties ..");
        filesystem = "Empty " + workingDirectory;
        mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
        APIController.sleep(2000);
        RepositoryTabOperator explorer = new ExplorerOperator().repositoryTab();
        Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
        filesystemNode.tree().expandPath(filesystemNode.getTreePath());
        if (filesystemNode.getChildren().length != 1) throw new Exception("Error: There are more nodes except A_File.");
        PropertiesAction propertiesAction = new PropertiesAction();
        propertiesAction.perform(filesystemNode);
        PropertySheetOperator sheet = new PropertySheetOperator();
        ComboBoxProperty filterBackupFiles = new ComboBoxProperty(sheet.getPropertySheetTabOperator("Expert"), "Filter Backup Files");
        ComboBoxProperty createBackupFiles = new ComboBoxProperty(sheet.getPropertySheetTabOperator("Expert"), "Create Backup Files");
        filterBackupFiles.setValue("False");
        createBackupFiles.setValue("False");
        APIController.sleep(10000);
        if (filesystemNode.getChildren().length != 1) throw new Exception("Error: Some backup files appeared unintentionally.");
        Node fileNode = new Node(explorer.getRootNode(), filesystem + "|A_File");
        OpenAction openAction = new OpenAction();
        openAction.perform(fileNode);
        sheet.close();
        EditorOperator editor = new EditorOperator("A_File");
        editor.insert("// The first added line.\n");
        new SaveAction().perform();
        APIController.sleep(10000);
        if (filesystemNode.getChildren().length != 1) throw new Exception("Error: Create backup files does not work.");
        propertiesAction.perform(filesystemNode);
        sheet = new PropertySheetOperator();
        createBackupFiles = new ComboBoxProperty(sheet.getPropertySheetTabOperator("Expert"), "Create Backup Files");
        createBackupFiles.setValue("True");
        editor.insert("// The second added line.\n");
        editor.requestFocus();
        new SaveAction().perform();
        APIController.sleep(10000);
        if (filesystemNode.getChildren().length != 2) throw new Exception("Error: Backup files properties do not work.");
        propertiesAction.perform(filesystemNode);
        sheet = new PropertySheetOperator();
        filterBackupFiles = new ComboBoxProperty(sheet.getPropertySheetTabOperator("Expert"), "Filter Backup Files");
        filterBackupFiles.setValue("True");
        APIController.sleep(10000);
        if (filesystemNode.getChildren().length != 1) throw new Exception("Error: Filter backup files does not work.");
        sheet.close();
        api.getFilesystemsTab();
        filesystemNode.select();
        new UnmountFSAction().perform(filesystemNode);
        System.out.println(". done !");
    }
}
