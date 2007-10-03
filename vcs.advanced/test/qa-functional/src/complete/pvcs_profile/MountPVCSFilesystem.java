/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package complete.pvcs_profile;

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
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
public class MountPVCSFilesystem extends JellyTestCase {
    
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
     * @param reason Reason of failure.
     * @param dialogs Dialogs that should be closed before exception is thrown.
     */
    private void captureScreen(Exception e) throws Exception {
        File file = null;
        try {
            file = new File(getWorkDirPath() + "/dump.png");
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch(IOException ioe) { assertTrue("Error: Can't create dump file.", false); }
        PNGEncoder.captureScreen(file.getAbsolutePath());
        throw e;
    }
    
    /** Checks that all of PVCS settings are available in wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPVCSSettings() throws Exception {
        try {
            System.out.print(".. Testing PVCS settings ..");
            new org.netbeans.jellytools.actions.ActionNoBlock(MOUNT_MENU, null).perform();
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
        } catch (Exception e) {
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            captureScreen(e);
        }
    }

    /** Checks that it is possible to choose Project Database through its special selector.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testDatabaseSelector() throws Exception {
        try {
            System.out.print(".. Testing project database selector ..");
            new org.netbeans.jellytools.actions.ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
            String profile = Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT;
            int os = Utilities.getOperatingSystem();
            if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
                profile = VCSWizardProfile.PVCS_WIN_95;
            wizard.setProfile(profile);
            Thread.currentThread().sleep(10000);
            wizard.selectProjectDatabase();
            DatabaseSelector selector = new DatabaseSelector();
            selector.pickADatabaseInSubfolderOf();
            selector.browseDatabaseParentFolder();
            new JButtonOperator(new JDialogOperator("Select Directory:"), "Cancel").push();
            selector.selectADatabaseUsedByPVCSGUI();
            Thread.currentThread().sleep(10000);
            selector.databaseLocationPath();
            selector.browseDatabaseLocation();
            JFileChooserOperator fileChooser = new JFileChooserOperator();
            new File(workingDirectory + File.separator + "Repo").mkdirs();
            fileChooser.chooseFile(workingDirectory + File.separator + "Repo");
            selector.ok();
            Thread.currentThread().sleep(10000);
            if (!wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_PROJECT_DATABASE).getText().equals(workingDirectory + File.separator + "Repo"))
                assertTrue("Error: Unable to setup project database through its selector.", false);
            wizard.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new DatabaseSelector().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            captureScreen(e);
        }
    }
        
    /** Checks that it is possible to mount PVCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testPVCSConnection() throws Exception {
        try {
            System.out.print(".. Testing PVCS filesystem connectivity ..");
            new org.netbeans.jellytools.actions.ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
            String profile = Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT;
            int os = Utilities.getOperatingSystem();
            if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
                profile = VCSWizardProfile.PVCS_WIN_95;
            wizard.setProfile(profile);
            Thread.currentThread().sleep(10000);
            JTextFieldOperator txt = new JTextFieldOperator(wizard, VCSWizardProfile.INDEX_TXT_PVCS_PROJECT_DATABASE);
            txt.clearText();
            txt.typeText(workingDirectory + File.separator + "Repo");
            txt = new JTextFieldOperator(wizard, VCSWizardProfile.INDEX_TXT_PVCS_WORKFILES_LOCATION);
            txt.requestFocus();
            Thread.currentThread().sleep(10000);
            new File(workingDirectory + File.separator + "Work").mkdirs();
            txt.clearText();
            txt.typeText(workingDirectory + File.separator + "Work");
            wizard.finish();
            Thread.currentThread().sleep(3000);
            String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
            assertNotNull("Error: Can't select filesystem " + filesystem, new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem));
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            captureScreen(e);
        }
    }

    /** Checks that it is possible to unmount PVCS filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testUnmountPVCS() throws Exception {
        try {
            System.out.print(".. Testing unmount PVCS filesystem action ..");
            String filesystem = "PVCS " + workingDirectory + File.separator + "Work";
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new UnmountFSAction().perform(filesystemNode);
            Thread.currentThread().sleep(5000);
            assertTrue("Error: Unable to unmount filesystem.", !filesystemNode.isPresent());
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            captureScreen(e);
        }
    }
}