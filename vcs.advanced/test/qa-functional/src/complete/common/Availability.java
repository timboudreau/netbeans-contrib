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
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.util.PNGEncoder;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.vcscore.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.properties.*;
import org.netbeans.test.oo.gui.jelly.SearchResults;
import org.netbeans.test.oo.gui.jelly.vcscore.SearchVCSFilesystem;


/** XTest / JUnit test class performing availability check of all basic features
 * of Generic VCS module.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class Availability extends JellyTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String FIND_SERVICE = "Find...";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public Availability(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition. This suite contains following test cases:
     * testUnmount, testPopupMenu, testRuntimeTab and testFindService.
     * @return Availability test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new Availability("testVersioningMenu"));
        suite.addTest(new Availability("testUnmount"));
        suite.addTest(new Availability("testPopupMenu"));
        suite.addTest(new Availability("testFindService"));
        suite.addTest(new Availability("testRuntimeTab"));
        suite.addTest(new Availability("testToolbar"));
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
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(org.netbeans.jemmy.TestOut.getNullOutput());
    }
    
    /** Method will create a file and capture the screen.
     */
    private void captureScreen(Exception exc) throws Exception {
        File dumpFile, excFile;
        try {
            dumpFile = new File(getWorkDirPath() + "/dump.png");
            excFile = new File(getWorkDirPath() + "/exception.txt");
            dumpFile.getParentFile().mkdirs();
            dumpFile.createNewFile();
            PrintWriter writer = new PrintWriter(new FileWriter(excFile));
            exc.printStackTrace(writer);
            writer.flush();
            writer.close();
        } catch(IOException e) { throw new Exception("Error: Can't create dump file."); }
        PNGEncoder.captureScreen(dumpFile.getAbsolutePath());
    }
    
    /** Checks that "Versioning" main menu contains item for mounting Generic VCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testVersioningMenu() throws Exception {
        try {
            System.out.print(".. Testing versioning menu ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
            wizard.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }
    
    /** Unmounts the filesystem mounted in testVersioningMenu test case.
     * throws Exception Any unexpected exception thrown during test.
     */
    public void testUnmount() throws Exception {
        try {
            System.out.print(".. Testing unmount action ..");
            new File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
            wizard.setProfile(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            wizard.setWorkingDirectory(getWorkDirPath());
            wizard.finish();
            Thread.sleep(2000);
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new UnmountFSAction().perform(filesystemNode);
            Thread.currentThread().sleep(5000);
            assertTrue("Error: Unable to unmount filesystem.", !filesystemNode.isPresent());
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }
    
    /** Checks that popup menu contains all of item appropriate for mounted Generic VCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPopupMenu() throws Exception {
        try {
            System.out.print(".. Testing popup menu ..");
            new File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
            wizard.setProfile(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            wizard.setWorkingDirectory(getWorkDirPath());
            wizard.finish();
            Thread.sleep(2000);
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action(null, "Empty|Refresh Recursively").perform(filesystemNode);
            new NbDialogOperator("Retrieving...").closeByButton();
            String[] commands = new String[] {"Refresh", "Check In", "Check Out", "Lock", "Unlock", "Add", "Remove"};
            for (int i=0; i<commands.length; i++) {
                new Action(null, "Empty|" + commands[i]).perform(filesystemNode);
                Thread.sleep(1000);
                String status = MainWindowOperator.getDefault().getStatusText();
                if (!(status.equals("Command " + commands[i] + " finished.") | status.equals("Command " + commands[i] + " failed.")))
                    throw new Exception("Error: Incorrect status bar text reached: " + status);
            }
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }
    
    /** Checks that there is additional search service available allowing to find files by their status.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testFindService() throws Exception {
        try {
            System.out.print(".. Testing search service ..");
            new File(getWorkDirPath()).mkdirs();
            new File(getWorkDirPath() + File.separator + "A_File.java").createNewFile();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
            wizard.setProfile(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            wizard.setWorkingDirectory(getWorkDirPath());
            wizard.finish();
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath());
            filesystemNode.expand();
            new ActionNoBlock("Edit|" + FIND_SERVICE, FIND_SERVICE).perform(filesystemNode);
            SearchVCSFilesystem searchDialog = new SearchVCSFilesystem();
            String[] statuses = new String[] {"Dead", "Ignored", "Local", "Locally Modified", "Not in Synch", "Unknown"};
            searchDialog.selectStatuses(statuses);
            statuses = new String[] {"Local"};
            searchDialog.selectStatuses(statuses);
            searchDialog.checkReverseMatch(true);
            searchDialog.search();
            SearchResults resultWindow = new SearchResults();
            if (!resultWindow.werefound(new String[] {"A_File"}, false, true))
                throw new Exception("Error: Can't find A_File file using search service.");
            resultWindow.close();
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new SearchResults().close(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }
    
    /** Checks that there is history of VCS commands under "Runtime" tab of explorer with proper functionality.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testRuntimeTab() throws Exception {
        try {
            System.out.print(".. Testing runtime tab ..");
            new File(getWorkDirPath()).mkdirs();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
            wizard.setProfile(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            wizard.setWorkingDirectory(getWorkDirPath());
            wizard.finish();
            Thread.sleep(2000);
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action(VERSIONING_MENU + "|Empty|Lock", "Empty|Lock").perform(filesystemNode);
            Node commandNode = new Node(new ExplorerOperator().runtimeTab().getRootNode(), "VCS Commands|" + filesystem + "|Lock");
            commandNode.select();
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property property = new Property(sheet, "Status");
            if (!property.getValue().equals("Finished"))
                throw new Exception("Error: Incorrect status of Lock command.");
            property = new Property(sheet, "Command Name");
            if (!property.getValue().equals("LOCK"))
                throw new Exception("Error: Incorrect command name of Lock command.");
            String executionString = Utilities.isUnix() ? "echo put your LOCK command here" : "cmd /X /C \"echo put your LOCK command here\"";
            property = new Property(sheet, "Execution String");
            if (!property.getValue().equals(executionString))
                throw new Exception("Error: Incorrect execution string of Lock command.");
            property = new Property(sheet, "Processed Files");
            if (!property.getValue().equals("."))
                throw new Exception("Error: Incorrect processed files of Lock command.");
            new Action(null, "View Output").performPopup(commandNode);
            VCSCommandsOutputOperator outputWindow = new VCSCommandsOutputOperator("Lock");
            
            
            // !!! Workaround for defect #32466 "jemmy eats some EVENTs (espec. HierarchyEvent)"
            outputWindow.close();
            new Action(null, "View Output").performPopup(commandNode);
            outputWindow = new VCSCommandsOutputOperator("Lock");
            
            String output = outputWindow.txtStandardOutput().getText();
            outputWindow.close();
            if (!output.equals("put your LOCK command here\n"))
                throw new Exception("Error: Incorrect standard output of Lock command: " + output);
            filesystemNode = new Node(new ExplorerOperator().runtimeTab().getRootNode(), "VCS Commands|" + filesystem);
            filesystemNode.select();
            property = new Property(sheet, "Number Of Finished Commands To Keep");
            property.setValue("1");
            filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new Action(VERSIONING_MENU + "|Empty|Add", "Empty|Add").perform(filesystemNode);
            filesystemNode = new Node(new ExplorerOperator().runtimeTab().getRootNode(), "VCS Commands|" + filesystem);
            new Node(filesystemNode, "Add");
            if ( filesystemNode.getChildren().length != 1)
                throw new Exception("Error: Incorrect number of kept commands.");
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem));
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }
    
    /** Tries to invoke an action through versioning toolbar.
     * throws Exception Any unexpected exception thrown during test.
     */
    public void testToolbar() throws Exception {
        try {
            System.out.print(".. Testing toolbar buttons ..");
            new File(getWorkDirPath()).mkdirs();
            new File(getWorkDirPath() + File.separator + "A_File.java").createNewFile();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile profilePage = new VCSWizardProfile();
            profilePage.setWorkingDirectory(getWorkDirPath());
            String profile = Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN;
            profilePage.setProfile(profile);
            profilePage.next();
            VCSWizardAdvanced advancedPage = new VCSWizardAdvanced();
            advancedPage.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.selectCommand("Empty|Add");
            NbDialogOperator dialog = new org.netbeans.jellytools.NbDialogOperator("Command Editor");
            PropertySheetOperator sheet = new PropertySheetOperator(dialog);
            Property property = new Property(sheet, "General Command Action Class Name");
            property.setValue("org.netbeans.modules.vcscore.actions.AddCommandAction");
            commandEditor.ok();
            advancedPage.finish();
            Thread.sleep(2000);
            String filesystem = "Empty " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            filesystemNode.expand();
            new Node(filesystemNode, "A_File").select();
            MainWindowOperator window = MainWindowOperator.getDefault();
            window.pushToolbarPopupMenu("Versioning");
            window.getToolbarButton(window.getToolbar("Versioning"), "VCS Add").push();
            Thread.sleep(2000);
            window.pushToolbarPopupMenu("Versioning");
            MainWindowOperator.getDefault().waitStatusText("Command Add finished.");
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
}
