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
public class FilesystemSettings extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String workingDirectory;
    public static String filesystem;
    
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
    
    /** Method called before each testcase. Sets default timeouts, redirects system
     * output and maps main components.
     */
    protected void setUp() throws Exception {
        String workingDir = getWorkDirPath();
        new File(workingDir).mkdirs();
        File outputFile = new File(workingDir + "/output.txt");
        outputFile.createNewFile();
        File errorFile = new File(workingDir + "/error.txt");
        errorFile.createNewFile();
        PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFile));
        PrintWriter errorWriter = new PrintWriter(new FileWriter(errorFile));
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(new org.netbeans.jemmy.TestOut(System.in, outputWriter, errorWriter));
    }
    
    /** Mounts new filesystem using given profile.
     * @param profile Profile that should be used in wizard.
     * @throws Any unexpected exception thrown during test.
     */
    private void mountFilesystem(String profile) throws Exception {
        new ActionNoBlock(MOUNT_MENU, null).perform();
        VCSWizardProfile wizard = new VCSWizardProfile();
        wizard.setWorkingDirectory(workingDirectory);
        wizard.checkOnlyCompatibleProfiles(false);
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

    /** Method will create a file and capture the screen.
     */
    private void captureScreen() throws Exception {
        File file;
        try {
            file = new File(getWorkDirPath() + "/dump.png");
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch(IOException e) { throw new Exception("Error: Can't create dump file."); }
        PNGEncoder.captureScreen(file.getAbsolutePath());
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
            workingDirectory = getWorkDir().getAbsolutePath();
            filesystem = "Empty " + workingDirectory;
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            createFile(workingDirectory + File.separator + "A_File.java", true);
            createFile(workingDirectory + File.separator + "A_File.class", true);
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            filesystemNode.expand();
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            StringProperty annotationPattern = new StringProperty(sheet.getPropertySheetTabOperator("Properties"), "Annotation Pattern");
            annotationPattern.setValue(">> ${fileName} $[? attribute][[Yes]][[No]] <<");
            TextFieldProperty refreshTime = new TextFieldProperty(sheet.getPropertySheetTabOperator("Expert"), "Refresh Time For Local Files [ms]");
            refreshTime.setValue("1000");
            new Action("Versioning|Empty|Refresh", "Empty|Refresh").perform(filesystemNode);
            Node fileNode = new Node(filesystemNode, ">> A_File [No] <<");
            fileNode.select();
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen();
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
            filesystem = "CVS " + workingDirectory;
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.CVS_UNIX : VCSWizardProfile.CVS_WIN_NT);
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            Node fileNode = new Node(filesystemNode, "A_File");
            new Action(null, "CVS|Editing|Edit").perform(fileNode);
            new NbDialogOperator("Information").ok();
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            ComboBoxProperty commandNotification = new ComboBoxProperty(sheet.getPropertySheetTabOperator("Properties"), "Command Notification");
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
            captureScreen();
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "CVS " + workingDirectory));
            throw e;
        }
    }

    /** Checks whether "Process All Files" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testProcessAllFiles() throws Exception {
        try {
            System.out.print(".. Testing process all files property ..");
            filesystem = "Empty " + workingDirectory;
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            ComboBoxProperty processAllFiles = new ComboBoxProperty(sheet.getPropertySheetTabOperator("Properties"), "Process All Files");
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
                new Action("View|Properties", null).perform(command);
                sheet = new PropertySheetOperator();
                StringProperty files = new StringProperty(sheet.getPropertySheetTabOperator("Properties"), "Processed Files");
                if (files.getStringValue().equals("A_File.class")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                captureScreen();
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Unable to find Lock command processed on A_File.class.");
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen();
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + workingDirectory));
            throw e;
        }
    }

    /** Checks whether "Ignored Files" property works correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testIgnoredFiles() throws Exception {
        try {
            System.out.print(".. Testing ignored files property ..");
            filesystem = "Empty " + workingDirectory;
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            Thread.sleep(2000);
            RepositoryTabOperator explorer = new ExplorerOperator().repositoryTab();
            Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
            filesystemNode.tree().expandPath(filesystemNode.getTreePath());
            Node fileNode = new Node(explorer.getRootNode(), filesystem + "|A_File");
            fileNode.select();
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            StringProperty ignoredFiles = new StringProperty(sheet.getPropertySheetTabOperator("Expert"), "Ignored Files");
            ignoredFiles.setStringValue("A_File");
            filesystemNode.select();
            if (fileNode.isPresent()) {
                captureScreen();
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: A_File node has not disappeared." + children);
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen();
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + workingDirectory));
            throw e;
        }
    }

    /** Checks whether "Filter Backup Files" and "Create Backup Files" properties work correctly.
     * @throws Any unexpected exception thrown during test.
     */
    public void testBackupProperties() throws Exception {
        try {
            System.out.print(".. Testing backup files properties ..");
            filesystem = "Empty " + workingDirectory;
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            Thread.sleep(2000);
            RepositoryTabOperator explorer = new ExplorerOperator().repositoryTab();
            Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
            filesystemNode.expand();
            Thread.sleep(1000);
            int count = filesystemNode.getChildren().length;
            new Action("View|Properties", null).perform(filesystemNode);
            filesystemNode.select();
            PropertySheetOperator sheet = new PropertySheetOperator();
            PropertySheetTabOperator expertTab;
            try {
                expertTab = sheet.getPropertySheetTabOperator("Expert");
            } catch (org.netbeans.jemmy.TimeoutExpiredException e) {
                captureScreen();
                filesystemNode.select();
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Can't find Expert property tab.");
            }
            ComboBoxProperty filterBackupFiles = new ComboBoxProperty(expertTab, "Filter Backup Files");
            ComboBoxProperty createBackupFiles = new ComboBoxProperty(expertTab, "Create Backup Files");
            TextFieldProperty refreshTime = new TextFieldProperty(expertTab, "Refresh Time For Local Files [ms]");
            filterBackupFiles.setValue("False");
            createBackupFiles.setValue("False");
            refreshTime.setValue("5000");
            Thread.sleep(5000);
            if (filesystemNode.getChildren().length != count) {
                captureScreen();
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
                captureScreen();
                filesystemNode.select();
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Create backup files does not work." + children);
            }
            filesystemNode.select();
            sheet = new PropertySheetOperator();
            try {
                expertTab = sheet.getPropertySheetTabOperator("Expert");
            } catch (org.netbeans.jemmy.TimeoutExpiredException e) {
                captureScreen();
                filesystemNode.select();
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Can't find Expert property tab.");
            }
            createBackupFiles = new ComboBoxProperty(expertTab, "Create Backup Files");
            createBackupFiles.setValue("True");
            editor.insert("// The second added line.\n");
            editor.requestFocus();
            new SaveAction().perform();
            Thread.sleep(10000);
            if (filesystemNode.getChildren().length != (count+1)) {
                captureScreen();
                filesystemNode.select();
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Backup files properties do not work." + children);
            }
            filesystemNode.select();
            sheet = new PropertySheetOperator();
            try {
                expertTab = sheet.getPropertySheetTabOperator("Expert");
            } catch (org.netbeans.jemmy.TimeoutExpiredException e) {
                captureScreen();
                filesystemNode.select();
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Can't find Expert property tab.");
            }
            filterBackupFiles = new ComboBoxProperty(expertTab, "Filter Backup Files");
            filterBackupFiles.setValue("True");
            Thread.sleep(10000);
            filesystemNode.select();
            if (filesystemNode.getChildren().length != count) {
                captureScreen();
                String children = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Filter backup files does not work." + children);
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen();
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + workingDirectory));
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
            String[] filesystems = explorer.getRootNode().getChildren();
            int count = filesystems.length;
            for(int i=0; i<count; i++)
                if (filesystems[i].startsWith("Empty "))
                    new UnmountFSAction().perform(new Node(explorer.getRootNode(), filesystems[i]));
            mountFilesystem(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            Thread.sleep(2000);
            filesystem = "Empty " + workingDirectory;
            Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
            filesystemNode.expand();
            new Action("View|Properties", null).perform(filesystemNode);
            PropertySheetOperator sheet = new PropertySheetOperator();
            PropertySheetTabOperator expertTab;
            try {
                expertTab = sheet.getPropertySheetTabOperator("Expert");
            } catch (org.netbeans.jemmy.TimeoutExpiredException e) {
                captureScreen();
                filesystemNode.select();
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Can't find Expert property tab.");
            }
            TextFieldProperty refreshTime = new TextFieldProperty(expertTab, "Refresh Time For Local Files [ms]");
            refreshTime.setValue("1000");
            createFile(workingDirectory + File.separator + "B_File.java", true);
            Thread.sleep(2000);
            String[] children = filesystemNode.getChildren();
            count = children.length;
            boolean found = false;
            for(int i=0; i<count; i++) if (children[i].startsWith("B_File")) found = true;
            filesystemNode.select();
            if (!found) {
                captureScreen();
                String childs = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Refresh time does not work." + childs);
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen();
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + workingDirectory));
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
            String[] filesystems = explorer.getRootNode().getChildren();
            int count = filesystems.length;
            for(int i=0; i<count; i++)
                if (filesystems[i].startsWith("Empty "))
                    new UnmountFSAction().perform(new Node(explorer.getRootNode(), filesystems[i]));
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile profilePage = new VCSWizardProfile();
            filesystem = "Empty " + workingDirectory;
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
            PropertySheetTabOperator sheetTab = sheet.getPropertySheetTabOperator("Properties");
            TextFieldProperty exec = new TextFieldProperty(sheetTab, "Exec");
            exec.setValue(Utilities.isUnix() ? "sh -c \"echo C_File.java\"" : "cmd /x /c \"echo C_File.java\"");
            TextFieldProperty dataRegex = new TextFieldProperty(sheet.getPropertySheetTabOperator("Expert"), "Data Regex");
            dataRegex.setValue("^(.*)");
            TextFieldProperty fileIndex = new TextFieldProperty(sheet.getPropertySheetTabOperator("Refresh Info"), "File Index");
            fileIndex.setValue("0");
            commandEditor.ok();
            advancedPage.finish();
            Thread.sleep(2000);
            Node filesystemNode = new Node(explorer.getRootNode(), filesystem);
            filesystemNode.expand();
            new Action(VERSIONING_MENU+"|Empty|Refresh", null).perform(filesystemNode);
            Node fileNode = new Node(filesystemNode, "C_File");
            new Action("View|Properties", null).perform(filesystemNode);
            sheet = new PropertySheetOperator();
            PropertySheetTabOperator expertTab;
            try {
                expertTab = sheet.getPropertySheetTabOperator("Expert");
            } catch (org.netbeans.jemmy.TimeoutExpiredException e) {
                captureScreen();
                filesystemNode.select();
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Can't find Expert property tab.");
            }
            ComboBoxProperty hideShadowFiles = new ComboBoxProperty(expertTab, "Hide Shadow Files");
            hideShadowFiles.setValue("True");
            filesystemNode.select();
            new Action(VERSIONING_MENU+"|Empty|Refresh", null).perform(filesystemNode);
            Thread.sleep(1000);
            String[] children = filesystemNode.getChildren();
            count = children.length;
            boolean found = false;
            for(int i=0; i<count; i++) if (children[i].startsWith("C_File")) found = true;
            filesystemNode.select();
            if (found) {
                captureScreen();
                String childs = getChildren(filesystemNode);
                new UnmountFSAction().perform(filesystemNode);
                throw new Exception("Error: Hide shadow files does not work." + childs);
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen();
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + workingDirectory));
            throw e;
        }
    }
}
