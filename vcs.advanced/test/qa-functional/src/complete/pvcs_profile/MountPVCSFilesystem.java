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

package complete.pvcs_profile;

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.test.oo.gui.jelly.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.UnmountFSAction;

/** XTest / JUnit test class performing mounting check of PVCS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class MountPVCSFilesystem extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String workingDirectory;
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public MountPVCSFilesystem(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return MountPVCSFilesystem test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new MountPVCSFilesystem("testPVCSSettings"));
        suite.addTest(new MountPVCSFilesystem("testDatabaseSelector"));
        suite.addTest(new MountPVCSFilesystem("testPVCSConnection"));
        suite.addTest(new MountPVCSFilesystem("testUnmountPVCS"));
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
    }
    
    /** Method called after each testcase. Resets Jemmy WaitComponentTimeout.
     */
    protected void tearDown() {
        JellyProperties.setDefaults();
    }
    
    /** Method will create a file and capture the screen.
     */
    private void captureScreen(String reason) throws Exception {
        File file;
        try {
            file = new File(getWorkDirPath() + "/dump.png");
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch(IOException e) { throw new Exception("Error: Can't create dump file."); }
        PNGEncoder.captureScreen(file.getAbsolutePath());
        throw new Exception(reason);
    }
    
    /** Checks that all of PVCS settings are available in wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPVCSSettings() throws Exception {
        System.out.print(".. Testing PVCS settings ..");
        MainFrame.getMainFrame().pushMenuNoBlock(MOUNT_MENU);
        VCSWizardProfile wizard = new VCSWizardProfile();
        String profile = Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT;
        int os = Utilities.getOperatingSystem();
        if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
            profile = VCSWizardProfile.PVCS_WIN_95;
        workingDirectory = getWorkDir().getAbsolutePath();
        wizard.setProfile(profile);
        wizard.lblPVCSProjectDatabase();
        wizard.lblPVCSProjectName();
        wizard.lblPVCSUserName();
        wizard.lblPVCSWorkfilesLocation();
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_PROJECT_DATABASE);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_PROJECT_NAME);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_USER);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_WORKFILES_LOCATION);
        wizard.btBrowse(VCSWizardProfile.INDEX_BT_PVCS_WORKFILES_LOCATION);
        wizard.btSelect(VCSWizardProfile.INDEX_BT_PVCS_PROJECT_DATABASE);
        wizard.btSelect(VCSWizardProfile.INDEX_BT_PVCS_PROJECT_NAME);
        if (profile.indexOf("95/98/ME") != -1) {
            wizard.lblUnixShell();
            wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_SHELL);
            wizard.btBrowse(VCSWizardProfile.INDEX_BT_PVCS_SHELL);
        }
        wizard.cancel();
        System.out.println(". done !");
    }

    /** Checks that it is possible to choose Project Database through its special selector.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testDatabaseSelector() throws Exception {
        System.out.print(".. Testing project database selector ..");
        MainFrame.getMainFrame().pushMenuNoBlock(MOUNT_MENU);
        VCSWizardProfile wizard = new VCSWizardProfile();
        String profile = Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT;
        int os = Utilities.getOperatingSystem();
        if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
            profile = VCSWizardProfile.PVCS_WIN_95;
        wizard.setProfile(profile);
        MainWindowOperator.getDefault().waitStatusText("Command AUTO_FILL_CONFIG finished.");
        wizard.selectProjectDatabase();
        DatabaseSelector selector = new DatabaseSelector();
        selector.pickADatabaseInSubfolderOf();
        selector.browseDatabaseParentFolder();
        new JButtonOperator(new JDialogOperator("Select Directory:"), "Cancel").push();
        selector.selectADatabaseUsedByPVCSGUI();
        Thread.currentThread().sleep(5000);
        String status = MainWindowOperator.getDefault().getStatusText();
        if (status.equals("Command LIST_PROJECT_DB failed."))
            new JButtonOperator( new JDialogOperator("Exception"), "OK").push();
        else if (status.equals("Command LIST_PROJECT_DB finished."))
            selector.lstDatabaseList().clickOnItem(0, 1);
            else captureScreen("Error: Incorrect status \"" + status + "\" reached.");
        selector.databaseLocationPath();
        selector.browseDatabaseLocation();
        JFileChooserOperator fileChooser = new JFileChooserOperator();
        new File(workingDirectory + File.separator + "Repo").mkdirs();
        fileChooser.chooseFile(workingDirectory + File.separator + "Repo");
        selector.ok();
        Thread.currentThread().sleep(5000);
        status = MainWindowOperator.getDefault().getStatusText();
        if (!status.equals("Command AUTO_FILL_CONFIG finished.") && (!status.equals("Command GET_WORK_LOCATION failed.")))
            captureScreen("Error: Incorrect status \"" + status + "\" reached.");
        if (!wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_PROJECT_DATABASE).getText().equals(workingDirectory + File.separator + "Repo"))
            captureScreen("Error: Unable to setup project database through its selector.");
        wizard.cancel();
        System.out.println(". done !");
    }
        
    /** Checks that it is possible to mount PVCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPVCSConnection() throws Exception {
        System.out.print(".. Testing PVCS filesystem connectivity ..");
        MainFrame.getMainFrame().pushMenuNoBlock(MOUNT_MENU);
        VCSWizardProfile wizard = new VCSWizardProfile();
        String profile = Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT;
        int os = Utilities.getOperatingSystem();
        if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
            profile = VCSWizardProfile.PVCS_WIN_95;
        wizard.setProfile(profile);
        MainWindowOperator.getDefault().waitStatusText("Command AUTO_FILL_CONFIG finished.");
        JTextFieldOperator txt = new JTextFieldOperator(wizard, VCSWizardProfile.INDEX_TXT_PVCS_PROJECT_DATABASE);
        txt.clearText();
        txt.typeText(workingDirectory + File.separator + "Repo");
        txt = new JTextFieldOperator(wizard, VCSWizardProfile.INDEX_TXT_PVCS_WORKFILES_LOCATION);
        txt.requestFocus();
        MainWindowOperator.getDefault().waitStatusText("Command AUTO_FILL_CONFIG finished.");
        new File(workingDirectory + File.separator + "Work").mkdirs();
        txt.clearText();
        txt.typeText(workingDirectory + File.separator + "Work");
        wizard.finish();
        Thread.currentThread().sleep(2000);
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        assertNotNull("Error: Can't select filesystem " + filesystem, new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem));
        System.out.println(". done !");
    }

    /** Checks that it is possible to unmount PVCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testUnmountPVCS() throws Exception {
        System.out.print(".. Testing unmount PVCS filesystem action ..");
        String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        new UnmountFSAction().perform(filesystemNode);
        assertTrue("Error: Unable to unmount filesystem.", !filesystemNode.isPresent());
        System.out.println(". done !");
    }
}