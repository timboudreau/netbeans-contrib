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
import org.netbeans.test.oo.gui.jelly.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.test.oo.gui.jelly.vcscore.*;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.wizard.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.MainWindowOperator;

/** XTest / JUnit test class performing availability check of all basic features
 * of Generic VCS module.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class Availability extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String FIND_SERVICE = "Find...";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String workingDirectory;
    private APIController api;
    private Explorer explorer;
    private APICommandsHistory history;
    
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
        suite.addTest(new Availability("testFindService"));
        suite.addTest(new Availability("testPopupMenu"));
        suite.addTest(new Availability("testRuntimeTab"));
        suite.addTest(new Availability("testToolbar"));
        suite.addTest(new Availability("testUnmount"));
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
    
    /** Checks that "Versioning" main menu contains item for mounting Generic VCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testVersioningMenu() throws Exception {
        System.out.print(".. Testing versioning menu ..");
        MainFrame.getMainFrame().pushMenuNoBlock(MOUNT_MENU);
        VCSWizardProfile wizard = new VCSWizardProfile();
        String profile = Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN;
        workingDirectory = getWorkDir().getAbsolutePath();
        wizard.setWorkingDirectory(workingDirectory);
        wizard.setProfile(profile);
        wizard.finish();
        APIController.sleep(2000);
        String filesystem = "Empty " + workingDirectory;
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        System.out.println(". done !");
    }

    /** Checks that there is additional search service available allowing to find files by their status.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testFindService() throws Exception {
        System.out.print(".. Testing search service ..");
        String filesystem = "Empty " + workingDirectory;
        BufferedWriter file = null;
        try {
            file = new BufferedWriter(new FileWriter(workingDirectory + File.separator + "A_File.java"));
            file.write("/** This is testing file.\n */\n\n public class A_File {\n }\n");
            file.flush();
            file.close();
        } catch (IOException e) {
            throw new Exception("Error: Can't create local A_File.java file.");
        }
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        explorer.pushPopupMenu("Empty|Refresh", filesystem);
        APIController.sleep(2000);
        explorer.pushPopupMenuNoBlock(FIND_SERVICE, filesystem);
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
        System.out.println(". done !");
    }
    
    /** Checks that popup menu contains all of item appropriate for mounted Generic VCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPopupMenu() throws Exception {
        System.out.print(".. Testing popup menu ..");
        assertNotNull("Can't select \"Filesystems\" node.", api.getFilesystemsTab().selectNode(""));
        explorer.pushPopupMenuNoBlock("Mount|Version Control|Generic VCS", "");
        VCSWizardProfile wizard = new VCSWizardProfile();
        wizard.close();
        String filesystem = "Empty " + workingDirectory;
        explorer.pushPopupMenu("Empty", filesystem);
        JPopupMenuOperator rootMenu = new JPopupMenuOperator();
        String[] rootMenuItems = new String[] {"Empty", "Versioning Explorer"};
        int count = rootMenuItems.length;
        for(int i=0; i<count; i++)
            new JMenuItemOperator(rootMenu, rootMenuItems[i]);
        JPopupMenuOperator emptyMenu = new JPopupMenuOperator(new JMenuOperator(rootMenu, "Empty").getPopupMenu());
        String[] emptyMenuItems = new String[] {"Refresh", "Refresh Recursively", "Check In", "Check Out", "Lock",
            "Unlock", "Add", "Remove"};
        count = emptyMenuItems.length;
        for(int i=0; i<count; i++)
            new JMenuItemOperator(emptyMenu, emptyMenuItems[i]);
        System.out.println(". done !");
    }

    /** Checks that there is history of VCS commands under "Runtime" tab of explorer with proper functionality.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testRuntimeTab() throws Exception {
        System.out.print(".. Testing runtime tab ..");
        String profile = "Empty";
        String filesystem = profile + " " + workingDirectory;
        String command = "Lock";
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        MainFrame.getMainFrame().pushMenu(VERSIONING_MENU+"|" + profile + "|" + command);
        api.getRuntimeTab();
        assertTrue(command + " command failed.", history.isCommandSuccessed(filesystem, command));
        CommandsHistory commandsHistory = new CommandsHistory();
        if (!commandsHistory.compareStatus("Finished", filesystem, command))
            throw new Exception("Error: Wrong status of " + command + " command.");
        if (!commandsHistory.compareCommandName("LOCK", filesystem, command))
            throw new Exception("Error: Wrong command name of " + command + " command.");
        String executionString = Utilities.isUnix() ? "echo put your LOCK command here" : "cmd /X /C \"echo put your LOCK command here\"";
        if (!commandsHistory.compareExecutionString(executionString, filesystem, command))
            throw new Exception("Error: Wrong execution string of " + command + " command.");
        if (!commandsHistory.compareProcessedFiles(".", filesystem, command))
            throw new Exception("Error: Wrong processed files of " + command + " command.");
        commandsHistory.viewOutput(filesystem, command);
        TabbedOutputOfVCSCommandsFrame outputWindow = new TabbedOutputOfVCSCommandsFrame();
        String desiredOutput = "put your LOCK command here";
        if (!outputWindow.containsStandardOutput(desiredOutput)) {
            String output = "Contains: |" + outputWindow.txtStandardOutput().getText() + "| instead of |" + desiredOutput + "|";
            throw new Exception("Error: Wrong standard output of " + command + " command.\n" + output);
        }
        outputWindow.close();
        history.setNumberOfCommands(filesystem, 1);
        command = "Add";
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        MainFrame.getMainFrame().pushMenu(VERSIONING_MENU+"|" + profile + "|" + command);
        assertTrue(command + " command failed", history.isCommandSuccessed(filesystem, command));
        String fs = JelloBundle.getString("org.netbeans.modules.vcscore.runtime.Bundle", "CTL_VcsRuntime") + "|" + filesystem;
        assertNotNull("Can't select history of " + filesystem, api.getRuntimeTab().selectNode(fs));
        int numberOfCommands = commandsHistory.countCommands(filesystem);
        if ( numberOfCommands != 1)
            throw new Exception("Error: Wrong number of kept commands. Currently: " + numberOfCommands);
        api.getFilesystemsTab();
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        MainFrame.getMainFrame().pushMenuNoBlock(UNMOUNT_MENU);
        System.out.println(". done !");
    }

    /** Unmounts the filesystem mounted in testVersioningMenu test case.
     * throws Exception Any unexpected exception thrown during test.
     */
    public void testToolbar() throws Exception {
        System.out.print(".. Testing toolbar buttons ..");
        MainFrame.getMainFrame().pushMenuNoBlock(MOUNT_MENU);
        VCSWizardProfile profilePage = new VCSWizardProfile();
        profilePage.setWorkingDirectory(workingDirectory);
        String profile = Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN;
        profilePage.setProfile(profile);
        profilePage.next();
        VCSWizardAdvanced advancedPage = new VCSWizardAdvanced();
        advancedPage.editCommands();
        CommandEditor commandEditor = new CommandEditor();
        commandEditor.setProperty("Empty|Add", CommandEditor.TAB_EXPERT, "General Command Action Class Name", "org.netbeans.modules.vcscore.actions.AddCommandAction");
        commandEditor.ok();
        advancedPage.finish();
        APIController.sleep(2000);
        explorer.show();
        String filesystem = "Empty " + workingDirectory;
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem + "|A_File"));
        MainWindowOperator window = MainWindowOperator.getDefault();
        window.pushToolbarPopupMenu("Versioning");
        window.getToolbarButton(window.getToolbar("Versioning"), "VCS Add").push();
        APIController.sleep(2000);
        assertTrue("Unable to execute command through toolbar.", window.getStatusText().equals("Command Add finished."));
        System.out.println(". done !");
    }

    /** Unmounts the filesystem mounted in testVersioningMenu test case.
     * throws Exception Any unexpected exception thrown during test.
     */
    public void testUnmount() throws Exception {
        System.out.print(".. Testing unmount action ..");
        String filesystem = "Empty " + workingDirectory;
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        MainFrame.getMainFrame().pushMenuNoBlock(UNMOUNT_MENU);
        System.out.println(". done !");
    }
}