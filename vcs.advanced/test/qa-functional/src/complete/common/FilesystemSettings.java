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
        wizard.finish();
        APIController.sleep(2000);
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
        PropertiesWindow properties = new PropertiesWindow(filesystem);
        String property = JelloBundle.getString("org.netbeans.modules.vcs.advanced.Bundle", "PROP_processAllFiles");
        properties.edit(property);
        properties.setTrue();
        assertNotNull("Can't select " + filesystem, api.getFilesystemsTab().selectNode(filesystem));
        selectNode(new String[] {filesystem, "A_File"}, false);
        MainFrame.getMainFrame().pushMenu(VERSIONING_MENU+"|Empty|Lock");
        assertTrue("Lock command failed", history.isCommandSuccessed(filesystem, "Lock"));
        CommandsHistory commandsHistory = new CommandsHistory();
        if (!commandsHistory.compareProcessedFiles("A_File.class", filesystem, "Lock"))
            throw new Exception("Error: Wrong processed files of Lock command.");
        api.getFilesystemsTab();
        selectNode(new String[] {filesystem}, true);
        MainFrame.getMainFrame().pushMenu(UNMOUNT_MENU);
        System.out.println(". done !");
    }
}