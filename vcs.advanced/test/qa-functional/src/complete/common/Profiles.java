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

import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.properties.*;

/** XTest / JUnit test class performing check of profiles management functionality.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class Profiles extends JellyTestCase {
    
    public static String MOUNT_MENU = "Versioning|Mount Version Control|Generic VCS";
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public Profiles(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return Profiles test suite.
     */
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new org.netbeans.junit.NbTestSuite();
        suite.addTest(new Profiles("testProfileCreation"));
        suite.addTest(new Profiles("testProfileUsage"));
        suite.addTest(new Profiles("testProfileDeletion"));
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
    
    /** Checks that it is possible to create new profile in Generic VCS wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testProfileCreation() throws Exception {
        try {
            System.out.print(".. Testing profile creation ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile(VCSWizardProfile.EMPTY_UNIX);
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editVariables();
            VariableEditor variableEditor = new VariableEditor();
            variableEditor.createVariable("Basic", "Test");
            variableEditor.ok();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addCommand("Empty", "Test");
            commandEditor.ok();
            wizardAdvanced.back();
            wizardProfile.saveAs();
            SaveProfileDialog saveProfileDialog = new SaveProfileDialog();
            saveProfileDialog.txtFileName().typeText("myProfile");
            saveProfileDialog.txtProfileLabel().typeText("My Profile");
            saveProfileDialog.save();
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new QuestionDialogOperator("Do you want to overwrite myProfile?").yes(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            wizardProfile.setProfileNoBlock("My Profile");
            try { new QuestionDialogOperator().no(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            wizardProfile.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VariableEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardAdvanced().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new SaveProfileDialog().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that it is possible to use new profile in Generic VCS wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testProfileUsage() throws Exception {
        try {
            System.out.print(".. Testing profile usage ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile("My Profile");
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editVariables();
            VariableEditor variableEditor = new VariableEditor();
            variableEditor.selectVariable("Basic",  "Test");
            variableEditor.cancel();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.selectCommand("Empty|Test");
            commandEditor.cancel();
            wizardAdvanced.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            captureScreen(e);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VariableEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardAdvanced().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Checks that it is possible to delete a profile in Generic VCS wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testProfileDeletion() throws Exception {
        try {
            System.out.print(".. Testing profile deletion ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.checkOnlyCompatibleProfiles(false);
            wizardProfile.setProfile("My Profile");
            wizardProfile.remove();
            new QuestionDialogOperator("Are you sure you want to delete the profile: My Profile?").cancel();
            wizardProfile.setProfileNoBlock("My Profile");
            wizardProfile.remove();
            new QuestionDialogOperator().ok();
            Thread.sleep(2000);
            int count = wizardProfile.cboProfile().getItemCount();
            boolean notFound = true;
            for (int i=0; i<count; i++) {
                String item = (String) wizardProfile.cboProfile().getItemAt(i);
                if (item.equals("My Profile")) notFound=false;
            }
            assertTrue("Error: Can't delete a profile.", notFound);
            wizardProfile.cancel();
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
}