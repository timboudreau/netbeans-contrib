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

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.util.PNGEncoder;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.properties.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;

/** XTest / JUnit test class performing check of all settings of filesystems mounted
 * using Generic VCS module.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class FilesystemSettings extends JellyTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    
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
        suite.addTest(new FilesystemSettings("testRefreshTime"));
        suite.addTest(new FilesystemSettings("testHideShadowFiles"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Mounts new filesystem using given profile.
     * @param profile Profile that should be used in wizard.
     * @throws Any unexpected exception thrown during test.
     */
    private void mountFilesystem(String profile, String workingDirectory) throws Exception {
        new ActionNoBlock(MOUNT_MENU, null).perform();
        VCSWizardProfile wizard = new VCSWizardProfile();
        wizard.setWorkingDirectory(workingDirectory);
//        wizard.checkOnlyCompatibleProfiles(false);
        wizard.setProfile(profile);
        Thread.sleep(1000);
        wizard.finish();
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

    /** Returns all of node's children as one String devided with commas.
     * @param node The node whose children should be returned in one String.
     * @return Collection of all children of given node.
     */
    private String getChildren(Node node) {
        String[] children = node.getChildren();
        String output = " [";
        int count = children.length;
        for(int i=0; i<count; i++) output = output + (i+1) + ". " + children[i] + ", ";
        return output + "]";
    }

    /** Checks whether "Annotation Pattern" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testAnnotationPattern() throws Exception {
        try {
            System.out.print(".. Testing annotation pattern property ..");
            String workingDirectory = getWorkDir().getAbsolutePath();
            String filesystem = "Empty " + workingDirectory;
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN, workingDirectory);
            createFile(workingDirectory + File.separator + "A_File.java", true);
            createFile(workingDirectory + File.separator + "A_File.class", true);
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            filesystemNode.expand();
            filesystemNode.select();
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property property = new Property(sheet, "Annotation Pattern");
            property.setValue(">> ${fileName} $[? attribute][[Yes]][[No]] <<");
            property = new Property(sheet, "Refresh Time For Local Files [ms]");
            property.setValue("1000");
            Thread.sleep(1000);
            Node fileNode = new Node(filesystemNode, ">> A_File [No] <<");
            fileNode.select();
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath()));
            throw e;
        }
    }
    
    /** Checks whether "Command Notification" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testCommandNotification() throws Exception {
        try {
            System.out.print(".. Testing command notification property ..");
            String workingDirectory = getWorkDir().getAbsolutePath();
            String filesystem = "CVS " + workingDirectory;
            createFile(workingDirectory + File.separator + "A_File.java", true);
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.CVS_UNIX : VCSWizardProfile.CVS_WIN_NT, workingDirectory);
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            Node fileNode = new Node(filesystemNode, "A_File");
            new Action(null, "CVS|Editing|Edit").perform(fileNode);
            new NbDialogOperator("Information").ok();
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property commandNotification = new Property(sheet, "Command Notification");
            commandNotification.setValue("False");
            new Action(null, "CVS|Editing|Edit").perform(fileNode);
            long oldTimeout = JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 5000);
            try {
                new NbDialogOperator("Information").ok();
                JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
                fail("Command Notification property does not work.");
            } catch (org.netbeans.jemmy.TimeoutExpiredException e) {}
            JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "CVS " + getWorkDir().getAbsolutePath()));
            throw e;
        }
    }

    /** Checks whether "Process All Files" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testProcessAllFiles() throws Exception {
        try {
            System.out.print(".. Testing process all files property ..");
            String workingDirectory = getWorkDir().getAbsolutePath();
            String filesystem = "Empty " + workingDirectory;
            createFile(workingDirectory + File.separator + "A_File.java", true);
            createFile(workingDirectory + File.separator + "A_File.class", true);
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN, workingDirectory);
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property processAllFiles = new Property(sheet, "Process All Files");
            processAllFiles.setValue("True");
            Node fileNode = new Node(filesystemNode, "A_File");
            new Action(VERSIONING_MENU+"|Empty|Lock", null).perform(fileNode);
            RuntimeTabOperator explorer = new ExplorerOperator().runtimeTab();
            Node commandsHistory = new Node(explorer.getRootNode(), "VCS Commands|" + filesystem);
            commandsHistory.tree().expandPath(commandsHistory.getTreePath());
            int count = commandsHistory.getChildren().length;
            boolean found = false;
            for(int i=0; i<count; i++) {
                Node command = new Node(commandsHistory, i);
                if (!command.getText().equals("Lock")) continue;
                command.select();
                sheet = new PropertySheetOperator();
                Property files = new Property(sheet, "Processed Files");
                if (files.getValue().equals("A_File.class")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Unable to find Lock command processed on A_File.class.");
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDir().getAbsolutePath()));
            throw e;
        }
    }

    /** Checks whether "Ignored Files" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testIgnoredFiles() throws Exception {
        try {
            System.out.print(".. Testing ignored files property ..");
            String workingDirectory = getWorkDir().getAbsolutePath();
            String filesystem = "Empty " + workingDirectory;
            createFile(workingDirectory + File.separator + "A_File.java", true);
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN, workingDirectory);
            Thread.sleep(2000);
            RepositoryTabOperator explorer = new ExplorerOperator().repositoryTab();
            Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
            filesystemNode.tree().expandPath(filesystemNode.getTreePath());
            Node fileNode = new Node(explorer.getRootNode(), filesystem + "|A_File");
            fileNode.select();
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property ignoredFiles = new Property(sheet, "Ignored Files");
            ignoredFiles.setValue("A_File");
            filesystemNode.select();
            if (fileNode.isPresent()) {
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: A_File node has not disappeared." + children);
            }
            Thread.sleep(1000);
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDir().getAbsolutePath()));
            throw e;
        }
    }

    /** Checks whether "Filter Backup Files" and "Create Backup Files" properties work correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testBackupProperties() throws Exception {
        try {
            System.out.print(".. Testing backup files properties ..");
            String workingDirectory = getWorkDir().getAbsolutePath();
            String filesystem = "Empty " + workingDirectory;
            createFile(workingDirectory + File.separator + "A_File.java", true);
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN, workingDirectory);
            Thread.sleep(2000);
            RepositoryTabOperator explorer = new ExplorerOperator().repositoryTab();
            Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
            filesystemNode.expand();
            Thread.sleep(1000);
            int count = filesystemNode.getChildren().length;
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property property = new Property(sheet, "Filter Backup Files");
            property.setValue("False");
            property = new Property(sheet, "Create Backup Files");
            property.setValue("False");
            property = new Property(sheet, "Refresh Time For Local Files [ms]");
            property.setValue("5000");
            Thread.sleep(5000);
            if (filesystemNode.getChildren().length != count) {
                filesystemNode.select();
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Some backup files appeared unintentionally." + children);
            }
            Node fileNode = new Node(filesystemNode, "A_File");
            new OpenAction().perform(fileNode);
            EditorOperator editor = new EditorOperator(new EditorWindowOperator(), "A_File");
            editor.insert("// The first added line.\n");
            new SaveAction().perform();
            Thread.sleep(10000);
            if (filesystemNode.getChildren().length != count) {
                filesystemNode.select();
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Create backup files does not work." + children);
            }
            new Action("View|Properties", null).perform(filesystemNode);
            sheet = new PropertySheetOperator();
            property = new Property(sheet, "Create Backup Files");
            property.setValue("True");
            editor.insert("// The second added line.\n");
            editor.requestFocus();
            new SaveAction().perform();
            Thread.sleep(10000);
            if (filesystemNode.getChildren().length != (count+1)) {
                filesystemNode.select();
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Backup files properties do not work." + children);
            }
            new Action("View|Properties", null).perform(filesystemNode);
            sheet = new PropertySheetOperator();
            property = new Property(sheet, "Filter Backup Files");
            property.setValue("True");
            Thread.sleep(10000);
            filesystemNode.select();
            if (filesystemNode.getChildren().length != count) {
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Filter backup files does not work." + children);
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            e.printStackTrace(getLog());
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDir().getAbsolutePath()));
            throw e;
        }
    }

    /** Checks whether "Refresh Time For Local Files [ms]" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testRefreshTime() throws Exception {
        try {
            System.out.print(".. Testing refresh time property ..");
            RepositoryTabOperator explorer = new ExplorerOperator().repositoryTab();
            String workingDirectory = getWorkDir().getAbsolutePath();
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN, workingDirectory);
            Thread.sleep(2000);
            String filesystem = "Empty " + workingDirectory;
            Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
            filesystemNode.expand();
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property refreshTime = new Property(sheet, "Refresh Time For Local Files [ms]");
            refreshTime.setValue("1000");
            createFile(workingDirectory + File.separator + "B_File.java", true);
            Thread.sleep(1000);
            String[] children = filesystemNode.getChildren();
            int count = children.length;
            boolean found = false;
            for(int i=0; i<count; i++) if (children[i].startsWith("B_File")) found = true;
            filesystemNode.select();
            if (!found) {
                String childs = getChildren(filesystemNode);
                throw new Exception("Error: Refresh time does not work." + childs);
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDir().getAbsolutePath()));
            throw e;
        }
    }

    /** Checks whether "Hide Shadow Files" property works correctly.
     * throws Exception Any unexpected exception thrown during test.
     */
    public void testHideShadowFiles() throws Exception {
        try {
            System.out.print(".. Testing hide shadow files property ..");
            RepositoryTabOperator explorer = new ExplorerOperator().repositoryTab();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile profilePage = new VCSWizardProfile();
            String workingDirectory = getWorkDir().getAbsolutePath();
            String filesystem = "Empty " + workingDirectory;
            profilePage.setWorkingDirectory(workingDirectory);
            String profile = Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN;
            profilePage.setProfile(profile);
            profilePage.next();
            VCSWizardAdvanced advancedPage = new VCSWizardAdvanced();
            advancedPage.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            new Node(commandEditor.treeCommands(), "Empty|Refresh").select();
            NbDialogOperator dialog = new NbDialogOperator("Command Editor");
            PropertySheetOperator sheet = new PropertySheetOperator(dialog);
            Property property = new Property(sheet, "Exec");
            property.setValue(Utilities.isUnix() ? "sh -c \"echo C_File.java\"" : "cmd /x /c \"echo C_File.java\"");
            property = new Property(sheet, "Data Regex");
            property.setValue("^(.*)");
            property = new Property(sheet, "File Index");
            property.setValue("0");
            commandEditor.ok();
            advancedPage.finish();
            Thread.sleep(2000);
            Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
            filesystemNode.expand();
            new Action(VERSIONING_MENU+"|Empty|Refresh", null).perform(filesystemNode);
            Node fileNode = new Node(filesystemNode, "C_File");
            new Action("View|Properties", null).perform(filesystemNode);
            sheet = new PropertySheetOperator();
            property = new Property(sheet, "Hide Shadow Files");
            property.setValue("True");
            new Action(VERSIONING_MENU+"|Empty|Refresh", null).perform(filesystemNode);
            Thread.sleep(1000);
            String[] children = filesystemNode.getChildren();
            int count = children.length;
            boolean found = false;
            for(int i=0; i<count; i++) if (children[i].startsWith("C_File")) found = true;
            filesystemNode.select();
            if (found) {
                String childs = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Hide shadow files does not work." + childs);
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDir().getAbsolutePath()));
            throw e;
        }
    }
}
