/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package complete.vss_profile;

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;


/** XTest / JUnit test class performing mounting check of VSS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class MountVSSFilesystem extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String workingDirectory;
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public MountVSSFilesystem(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return MountVSSFilesystem test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        suite.addTest(new MountVSSFilesystem("testVSSSettings"));
        suite.addTest(new MountVSSFilesystem("testVSSConnection"));
        suite.addTest(new MountVSSFilesystem("testUnmountVSS"));
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
    
    /** Checks that all of VSS settings are available in wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testVSSSettings() throws Exception {
        System.out.print(".. Testing VSS settings ..");
        new ActionNoBlock(MOUNT_MENU, "Mount|Version Control|Generic VCS").perform();
        VCSWizardProfile wizard = new VCSWizardProfile();
        String profile = VCSWizardProfile.VSS_WIN_NT;
        int os = Utilities.getOperatingSystem();
        if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
            profile = VCSWizardProfile.VSS_WIN_95;
        workingDirectory = getWorkDir().getAbsolutePath();
        wizard.setProfile(profile);
        wizard.lblWorkingDirectory();
        wizard.lblRelativeMountPoint();
        wizard.lblVSSUserName();
        wizard.lblVSSCommand();
        wizard.lblVSSProject();
        wizard.lblVSSSSDIR();
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_WORKING_DIRECTORY);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_RELATIVE_MOUNTPOINT);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_VSS_USER);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_VSS_EXECUTABLE);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_VSS_PROJECT);
        wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_VSS_SSDIR);
        wizard.btBrowse(VCSWizardProfile.INDEX_BT_WORKING_DIRECTORY);
        wizard.btSelect(VCSWizardProfile.INDEX_BT_RELATIVE_MOUNTPOINT);
        wizard.btBrowse(VCSWizardProfile.INDEX_BT_VSS_EXECUTABLE);
        wizard.btBrowse(VCSWizardProfile.INDEX_BT_VSS_SSDIR);
        if (profile.indexOf("95/98/ME") != -1) {
            wizard.lblUnixShell();
            wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_VSS_SHELL);
            wizard.btBrowse(VCSWizardProfile.INDEX_BT_VSS_SHELL);
        }
        wizard.cancel();
        System.out.println(". done !");
    }

    /** Checks that it is possible to mount VSS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testVSSConnection() throws Exception {
        System.out.print(".. Testing VSS filesystem connectivity ..");
        new ActionNoBlock(MOUNT_MENU, "Mount|Version Control|Generic VCS").perform();
        VCSWizardProfile wizard = new VCSWizardProfile();
        String profile = VCSWizardProfile.VSS_WIN_NT;
        int os = Utilities.getOperatingSystem();
        if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
            profile = VCSWizardProfile.VSS_WIN_95;
        wizard.setProfile(profile);
        new File(workingDirectory + File.separator + "Work").mkdirs();
        wizard.setWorkingDirectory(workingDirectory + File.separator + "Work");
        wizard.finish();
        new JButtonOperator(new NbDialogOperator("Password:"), "OK").push();
        Thread.currentThread().sleep(3000);
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        assertNotNull("Error: Can't select filesystem " + filesystem, new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem));
        System.out.println(". done !");
    }

    /** Checks that it is possible to unmount VSS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testUnmountVSS() throws Exception {
        System.out.print(".. Testing unmount VSS filesystem action ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        new UnmountFSAction().perform(filesystemNode);
        Thread.currentThread().sleep(5000);
        assertTrue("Error: Unable to unmount filesystem.", !filesystemNode.isPresent());
        System.out.println(". done !");
    }
}