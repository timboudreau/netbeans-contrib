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

package complete.cvs_profile;

import complete.cvs_profile.JellyStub.Configuration;
import java.io.*;
import java.lang.reflect.Field;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSAddAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSCommitAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSRemoveAction;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSFileNode;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.runtime.RuntimeCommand;
import org.netbeans.modules.vcscore.runtime.VcsRuntimeCommand;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.CVSRemoveFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.CVSRemoveFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.*;
import util.Helper;
import util.History;


public class JellyAddFeatures extends JellyStub {
    
    public JellyAddFeatures(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyAddFeatures("testWorkDir"));
        suite.addTest(new JellyAddFeatures("testAddCommit"));
//        suite.addTest(new JellyAddFeatures("testForceCommit")); // fails due to bug #27997
        suite.addTest(new JellyAddFeatures("testRemoveCommit"));
        suite.addTest(new JellyAddFeatures("testRunProgram"));
        suite.addTest(new JellyAddFeatures("testToStandardOutput"));
        suite.addTest(new JellyAddFeatures("testQuiet"));
        suite.addTest(new JellyAddFeatures("testRemovePreview"));
        suite.addTest(new JellyAddFeatures("testRemoveDebug"));
        suite.addTest(new JellyAddFeatures("testNoHistory"));
        suite.addTest(new JellyAddFeatures("testReadOnly"));
        suite.addTest(new JellyAddFeatures("testCompressionLevel"));
        suite.addTest(new JellyAddFeatures("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    static String serverDirectory;
    static String clientDirectory;
    static History history;
    static String nRoot;
    static String tInitDir = "initdir", hInitDir = tInitDir, fInitDir, nInitDir;
    static String tSubInitDir = "subdir", hSubInitDir = tInitDir + "/" + tSubInitDir, fSubInitDir, nSubInitDir;
    static String tText1 = "text1", hText1 = hInitDir + "/" + tText1, fText1, nText1;
    static String tText2 = "text2", hText2 = hInitDir + "/" + tText2, fText2, nText2;
    static String tText3 = "text3", hText3 = hSubInitDir + "/" + tText3, fText3, nText3;
    static String tText4 = "text4", hText4 = hInitDir + "/" + tText4, fText4, nText4;
    static String tText5 = "text5", hText5 = hInitDir + "/" + tText5, fText5, nText5;
    static String tText6 = "text6", hText6 = hInitDir + "/" + tText6, fText6, nText6;
    
    static boolean text1InDefaultGroup = false;

    protected void prepareServer (String dir) {
        new File(dir + "/" + tInitDir).mkdirs();
    }
    
    public void testWorkDir() {
        Configuration conf = super.configureWorkDir ();
        
        nRoot = conf.nRoot;
        serverDirectory = conf.serverDirectory;
        clientDirectory = conf.clientDirectory;
        history = conf.history;
        
        fInitDir = clientDirectory + "/" + tInitDir;
        nInitDir = nRoot + "|" + tInitDir;
        
        fSubInitDir = fInitDir + "/" + tSubInitDir;
        nSubInitDir = nInitDir + "|" + tSubInitDir;
        
        fText1 = fInitDir + "/" + tText1;
        nText1 = nInitDir + "|" + tText1;
        
        fText2 = fInitDir + "/" + tText2;
        nText2 = nInitDir + "|" + tText2;

        fText3 = fSubInitDir + "/" + tText3;
        nText3 = nSubInitDir + "|" + tText3;

        fText4 = fInitDir + "/" + tText4;
        nText4 = nInitDir + "|" + tText4;

        fText5 = fInitDir + "/" + tText5;
        nText5 = nInitDir + "|" + tText5;

        fText6 = fInitDir + "/" + tText6;
        nText6 = nInitDir + "|" + tText6;
    }
    
    public void testAddCommit () {
        try {
            new File(fText1).createNewFile();
            new File(fText2).createNewFile();
            new File(fSubInitDir).mkdirs();
            new File(fText3).createNewFile();
            new File(fText6).createNewFile();
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while setting test case up", e);
        }

        new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSRefreshRecursively();
        assertTrue("Refresh recursively folder command failed", history.waitCommand("Refresh Recursively", hRoot));
        new CVSFileNode(exp.repositoryTab().tree(), nText1);
        new CVSFileNode(exp.repositoryTab().tree(), nText2);
        new CVSFileNode(exp.repositoryTab().tree(), nText6);

        new CVSAddAction ().perform (new Node[] {
            new CVSFileNode(exp.repositoryTab().tree(), nText1),
            new CVSFileNode(exp.repositoryTab().tree(), nText2),
            new CVSFileNode(exp.repositoryTab().tree(), nText6),
        });
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog();
        add.setFileDescription("Initial state");
        add.checkProceedWithCommitIfAddSucceeds(true);
        add.oK();
        add.waitClosed();
        CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog();
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Initial commit of text1 and text2");
        com.oK();
        com.waitClosed();
        assertTrue("Add files command failed", history.waitCommand("Add", hText1 + '\n' + hText2 + '\n' + hText6));
        assertTrue("Commit files command failed", history.waitCommand("Commit", hText1 + '\n' + hText2 + '\n' + hText6));
        waitStatus("Up-to-date; 1.1", nText1, true);
        waitStatus("Up-to-date; 1.1", nText2, true);
        waitStatus("Up-to-date; 1.1", nText6, true);

        new CVSFileNode(exp.repositoryTab().tree(), nSubInitDir).cVSAdd();
        CVSAddFolderAdvDialog addfo = new CVSAddFolderAdvDialog();
        addfo.addAllLocalFilesInFolderContents();
        addfo.setFileDescription("Initial state");
        addfo.checkAddTheFolderContentsRecursively(true);
        addfo.checkProceedWithCommitIfAddSucceeds(true);
        addfo.oK();
        addfo.waitClosed();
        CVSCommitFolderAdvDialog comfo = new CVSCommitFolderAdvDialog();
        comfo.txtEnterReason().setCaretPosition(0);
        comfo.txtEnterReason().typeText("Initial commit");
        comfo.oK();
        comfo.waitClosed();
        assertTrue("Add files command failed", history.waitCommand("Add", hSubInitDir));
        assertTrue("Commit files command failed", history.waitCommand("Commit", hSubInitDir));
        waitStatus(null, nSubInitDir, false);
        waitStatus("Up-to-date; 1.1", nText3, true);
    }
    
    public void testForceCommit () {
        waitStatus("Up-to-date; 1.1", nText1, true);
        waitStatus("Up-to-date; 1.1", nText2, true);
        waitStatus("Up-to-date; 1.1", nText3, true);
        new CVSCommitAction ().perform (new Node[] {
            new CVSFileNode(exp.repositoryTab().tree(), nText1),
            new CVSFileNode(exp.repositoryTab().tree(), nText2),
            new CVSFileNode(exp.repositoryTab().tree(), nText3),
        });
        CVSCommitFileAdvDialog com;
        com = new CVSCommitFileAdvDialog();
        new JCheckBoxOperator (com, "Prompt for Input on Each Successive File").setSelected(true);
        com.checkForceToCommit(true);
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Force commit of text1");
        com.oK();
        com.waitClosed();
        com = new CVSCommitFileAdvDialog();
        new JCheckBoxOperator (com, "Prompt for Input on Each Successive File").setSelected(false);
        com.checkForceToCommit(true);
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Force commit of text2 and test3");
        com.oK();
        com.waitClosed();
        assertTrue("Force commit files command failed", history.waitCommand("Remove", hText1 + '\n' + hText2 + '\n' + hText3));
        waitStatus("Up-to-date; 1.2", nText1, true);
        waitStatus("Up-to-date; 1.2", nText2, true);
        waitStatus("Up-to-date; 1.2", nText3, true);
    }

    public void testRemoveCommit () {
        new CVSRemoveAction ().perform (new Node[] {
            new CVSFileNode(exp.repositoryTab().tree(), nText1),
            new CVSFileNode(exp.repositoryTab().tree(), nText2),
        });
        CVSRemoveDialog rd = new CVSRemoveDialog ();
        rd.no();
        rd.waitClosed();
        waitStatus("Up-to-date", nText1, false);
        waitStatus("Up-to-date", nText2, false);
        new CVSRemoveAction ().perform (new Node[] {
            new CVSFileNode(exp.repositoryTab().tree(), nText1),
            new CVSFileNode(exp.repositoryTab().tree(), nText2),
        });
        rd = new CVSRemoveDialog ();
        rd.yes();
        rd.waitClosed();
        CVSRemoveFileAdvDialog rem = new CVSRemoveFileAdvDialog ();
        rem.checkProceedWithCommitIfRemoveSucceeds(true);
        rem.oK();
        rem.waitClosed();
        CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog();
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Initial commit");
        com.oK();
        com.waitClosed();
        assertTrue("Remove file command failed", history.waitCommand("Remove", hText1 + '\n' + hText2));
        assertTrue("Commit file command failed", history.waitCommand("Commit", hText1 + '\n' + hText2));
        Helper.waitNoNode (exp.repositoryTab ().tree (), nInitDir, tText1);
        Helper.waitNoNode (exp.repositoryTab ().tree (), nInitDir, tText2);

        new CVSFileNode(exp.repositoryTab().tree(), nSubInitDir).cVSRemove();
        rd = new CVSRemoveDialog ();
        rd.no();
        rd.waitClosed();
        waitStatus(null, nSubInitDir, false);
        waitStatus("Up-to-date", nText3, false);
        new CVSFileNode(exp.repositoryTab().tree(), nSubInitDir).cVSRemove();
        rd = new CVSRemoveDialog ();
        rd.yes();
        rd.waitClosed();
        CVSRemoveFolderAdvDialog rem2 = new CVSRemoveFolderAdvDialog ();
        rem2.checkProcessDirectoriesRecursively(true);
        rem2.checkProceedWithCommitIfRemoveSucceeds(true);
        rem2.oK();
        rem2.waitClosed();
        CVSCommitFolderAdvDialog com2 = new CVSCommitFolderAdvDialog();
        com2.txtEnterReason().setCaretPosition(0);
        com2.txtEnterReason().typeText("Initial commit");
        com2.oK();
        com2.waitClosed();
        assertTrue("Remove file command failed", history.waitCommand("Remove", hSubInitDir));
        assertTrue("Commit file command failed", history.waitCommand("Commit", hSubInitDir));
        Helper.waitNoNode (exp.repositoryTab ().tree (), nSubInitDir, tText3);
    }

    public void testRunProgram () {
        try {
            new File(fText4).createNewFile();
            new File(fText5).createNewFile();
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while setting test case up", e);
        }
        
        Field f;
        try {
            f = VcsRuntimeCommand.class.getDeclaredField("executor");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new AssertionFailedErrorException("NoSuchFieldException while setting test case up", e);
        } catch (SecurityException e) {
            throw new AssertionFailedErrorException("SecurityException while setting test case up", e);
        }

        new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSRefreshRecursively();
        assertTrue("Refresh recursively folder command failed", history.waitCommand("Refresh Recursively", hRoot));
        new CVSFileNode(exp.repositoryTab().tree(), nText4);

        new CVSFileNode(exp.repositoryTab().tree(), nText4).cVSAdd ();
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog();
        add.setFileDescription("Initial state");
        add.checkProceedWithCommitIfAddSucceeds(true);
        add.oK();
        add.waitClosed();
        CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog();
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Initial commit of text4");
        com.checkRunTheModuleProgramIfAny(false);
        com.oK();
        com.waitClosed();
        assertTrue("Add file command failed", history.waitCommand("Add", hText4));
        assertTrue("Commit file command failed", history.waitCommand("Commit", hText4));
        waitStatus("Up-to-date; 1.1", nText4, true);
        VcsRuntimeCommand rc = (VcsRuntimeCommand) history.getWaitCommand("Commit Command", hText4);
        VcsCommandExecutor ce;
        try {
            ce = (VcsCommandExecutor) f.get(rc);
        } catch (IllegalAccessException e) {
            throw new AssertionFailedErrorException("IllegalAccessException while getting executor field value", e);
        }
        assertTrue ("Execution String does not contain No-Run argument", ce.getExec ().indexOf ("-n") >= 0);
        
        new CVSFileNode(exp.repositoryTab().tree(), nText5).cVSAdd ();
        add = new CVSAddFileAdvDialog();
        add.setFileDescription("Initial state");
        add.checkProceedWithCommitIfAddSucceeds(true);
        add.oK();
        add.waitClosed();
        com = new CVSCommitFileAdvDialog();
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Initial commit of text5");
        com.checkForceToCommit(true);
        com.checkRunTheModuleProgramIfAny(true);
        com.oK();
        com.waitClosed();
        assertTrue("Add file command failed", history.waitCommand("Add", hText5));
        assertTrue("Commit file command failed", history.waitCommand("Commit", hText5));
        waitStatus("Up-to-date; 1.1", nText5, true);
        rc = (VcsRuntimeCommand) history.getWaitCommand("Commit Command", hText5);
        try {
            ce = (VcsCommandExecutor) f.get(rc);
        } catch (IllegalAccessException e) {
            throw new AssertionFailedErrorException("IllegalAccessException while getting executor field value", e);
        }
        assertTrue ("Execution String contains No-Run argument", ce.getExec ().indexOf ("-n") < 0);
    }
    
    public void testToStandardOutput () {
        waitStatus("Up-to-date; 1.1", nText4, true);
        new CVSFileNode(exp.repositoryTab().tree(), nText4).cVSUpdate ();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.checkSendUpdatesToStandardOutput(true);
        up.oK();
        up.waitClosed ();
        assertTrue("Update file command failed", history.waitCommand("Update", hText4));
        
        closeAllVCSOutputs();
        JellyStub.viewOutput(history, "UPDATE_CMD", hText4);
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("UPDATE_CMD");
        String str = coo.txtStandardError().getText ();
        getLog ().println ("Standard Error:");
        getLog ().println (str);
        assertTrue ("Standard error does not contain this text: Checking out text4", str.indexOf ("Checking out text4") >= 0);
        assertTrue ("Standard error does not contain this text: RCS:", str.indexOf ("RCS:") >= 0);
        assertTrue ("Standard error does not contain this text: VERS: 1.1", str.indexOf ("VERS: 1.1") >= 0);
        closeAllVCSOutputs();
    }
    
    public void testQuiet () {
        String str;
        waitStatus("Up-to-date; 1.1", nText4, true);
        new CVSFileNode(exp.repositoryTab().tree(), nText4).cVSStatus ();
        CVSStatusFileAdvDialog stat = new CVSStatusFileAdvDialog ();
        stat.setQuietness(CVSStatusFileAdvDialog.ITEM_BESOMEWHATQUIET);
        stat.oK();
        stat.waitClosed();
        RuntimeCommand rc = history.getWaitCommand("Status", hText4);
        assertTrue("Status file command failed", History.resultCommand (rc));
        
        closeAllVCSOutputs();
        JellyStub.viewOutput(rc);
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("Status");
        assertTrue ("Execution string does not contain -q", coo.txtExecutionString().getText ().indexOf ("-q") >= 0);
        closeAllVCSOutputs();
        
        waitStatus("Up-to-date; 1.1", nText4, true);
        new CVSFileNode(exp.repositoryTab().tree(), nText4).cVSStatus ();
        stat = new CVSStatusFileAdvDialog ();
        stat.setQuietness(CVSStatusFileAdvDialog.ITEM_BEREALLYQUIET);
        stat.oK();
        stat.waitClosed();
        rc = history.getWaitCommand("Status", hText4);
        assertTrue("Status file command failed", History.resultCommand (rc));
        
        closeAllVCSOutputs();
        JellyStub.viewOutput(rc);
        coo = new VCSCommandsOutputOperator ("Status");
        assertTrue ("Execution string does not contain -Q", coo.txtExecutionString().getText ().indexOf ("-Q") >= 0);
        closeAllVCSOutputs();
        
        waitStatus("Up-to-date; 1.1", nText5, true);
        new DeleteAction ().perform (new CVSFileNode(exp.repositoryTab().tree(), nText5));
        new NbDialogOperator ("Confirm Object Deletion").yes ();
        waitStatus("Needs Update; 1.1", nText5, true);
        new CVSFileNode(exp.repositoryTab().tree(), nText5).cVSUpdate ();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.setQuietness(CVSUpdateFileAdvDialog.ITEM_BEREALLYQUIET);
        up.oK();
        up.waitClosed();
        assertTrue("Update file command failed", history.waitCommand("Update", hText5));
        
        closeAllVCSOutputs();
        JellyStub.viewOutput(history, "UPDATE_CMD", hText5);
        coo = new VCSCommandsOutputOperator ("UPDATE_CMD");
        System.out.println(coo.tabbedPane().getTitleAt(0));
        System.out.println(coo.tabbedPane().isEnabledAt(0));
        assertTrue ("Standard Output contains text", !coo.tabbedPane().isEnabledAt(0));
        closeAllVCSOutputs();
    }
    
    public void testRemovePreview () {
        waitStatus("Up-to-date; 1.1", nText5, true);
        new CVSFileNode(exp.repositoryTab().tree(), nText5).cVSRemove ();
        CVSRemoveDialog rem = new CVSRemoveDialog ();
        rem.yes ();
        rem.waitClosed();
        CVSRemoveFileAdvDialog rr = new CVSRemoveFileAdvDialog ();
        rr.checkDoNotMakeChangesPreview(true);
        rr.oK ();
        rr.waitClosed ();
        assertTrue("Remove file command failed", history.waitCommand("Remove", hText5));
        waitStatus("Up-to-date; 1.1", nText5, true);
    }
    
    public void testRemoveDebug () {
        waitStatus("Up-to-date; 1.1", nText5, true);
        new CVSFileNode(exp.repositoryTab().tree(), nText5).cVSRemove ();
        CVSRemoveDialog rem = new CVSRemoveDialog ();
        rem.yes ();
        rem.waitClosed();
        CVSRemoveFileAdvDialog rr = new CVSRemoveFileAdvDialog ();
        rr.checkDoNotMakeChangesPreview(true);
        rr.checkShowTraceOfCommandExecution(true);
        rr.oK ();
        rr.waitClosed ();
        RuntimeCommand rc = history.getWaitCommand("Remove", hText5);
        assertTrue("Remove file command failed", History.resultCommand(rc));
        waitStatus("Up-to-date; 1.1", nText5, true);
        
        closeAllVCSOutputs ();
        JellyStub.viewOutput(rc);
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("Remove");
        assertTrue ("Execution string does not contain -t", coo.txtExecutionString().getText ().indexOf ("-t") >= 0);
        /*String str = coo.txtStandardOutput().getText ();
        assertTrue ("Standard Output does not contain text: -> main loop with CVSROOT=", str.indexOf ("-> main loop with CVSROOT=") >= 0);
        assertTrue ("Standard Output does not contain text: cvs remove: file `text5' still in working directory", str.indexOf ("cvs remove: file `text5' still in working directory") >= 0);
        assertTrue ("Standard Output does not contain text: cvs remove: cvs remove: 1 file exists; remove it first", str.indexOf ("cvs remove: 1 file exists; remove it first") >= 0);
        assertTrue ("Standard Output does not contain text:  -> Lock_Cleanup()", str.indexOf (" -> Lock_Cleanup()") >= 0);*/
        closeAllVCSOutputs();
    }
    
    public void testNoHistory () {
        try {
            JellyStub.saveToFile (fText5, "T5-1");
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while setting test case up", e);
        }
        new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSRefreshRecursively();
        assertTrue("Refresh recursively folder command failed", history.waitCommand("Refresh Recursively", hRoot));
        waitStatus("Locally Modified; 1.1", nText5, true);

        new CVSFileNode (exp.repositoryTab ().tree (), nText5).cVSCommit ();
        CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog();
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Next commit of text5");
        com.checkDisableHistoryLogging(true);
        com.oK();
        com.waitClosed();
        assertTrue("Commit file command failed", history.waitCommand("Commit", hText5));
        waitStatus("Up-to-date; 1.2", nText5, true);
        
        BufferedReader br = null;
        try {
            br = new BufferedReader (new FileReader (serverDirectory + "/CVSROOT/history"));
            for (;;) {
                String str = br.readLine ();
                if (str == null)
                    break;
                assertTrue ("History contains information about commiting version 1.2 of text5 file", str.indexOf ("1.2") < 0  ||  str.indexOf (tText5) < 0);
            }
        } catch (FileNotFoundException e) {
            throw new AssertionFailedErrorException("FileNotFoundException while setting test case up", e);
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while setting test case up", e);
        } finally {
            if (br != null) try { br.close (); } catch (IOException e) {}
        }
    }
    
    public void testReadOnly () {
        new DeleteAction ().perform (new CVSFileNode (exp.repositoryTab ().tree (), nText4));
        new NbDialogOperator ("Confirm Object Deletion").yes ();
        waitStatus("Needs Update; 1.1", nText4, true);
        new CVSFileNode (exp.repositoryTab ().tree (), nText4).cVSUpdate ();
        CVSUpdateFolderAdvDialog up = new CVSUpdateFolderAdvDialog ();
        up.checkCheckOutFilesAsReadOnly(true);
        up.oK ();
        up.waitClosed ();
        assertTrue("Update file command failed", history.waitCommand("Update", hText4));
        waitStatus("Up-to-date; 1.1", nText4, true);
        assertTrue ("File is not read-only", !new File (fText4).canWrite ());
    }
    
    public void testCompressionLevel () {
        Field f;
        try {
            f = VcsRuntimeCommand.class.getDeclaredField("executor");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new AssertionFailedErrorException("NoSuchFieldException while setting test case up", e);
        } catch (SecurityException e) {
            throw new AssertionFailedErrorException("SecurityException while setting test case up", e);
        }

        for (int a = 1; a <= 9; a ++) {
            waitStatus("Up-to-date; 1." + a, nText6, true);
            try {
                JellyStub.saveToFile (fText6, "T6-" + a);
            } catch (IOException e) {
                throw new AssertionFailedErrorException("IOException while setting test case up", e);
            }
            new CVSFileNode(exp.repositoryTab().tree(), nText6).cVSRefresh ();
            assertTrue("Refresh file command failed: Level: " + a, history.waitCommand("Refresh", hText6));
            
            new CVSFileNode(exp.repositoryTab().tree(), nText6).cVSCommit ();
            CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog ();
            com.setCompressionLevel("" + a);
            com.oK ();
            com.waitClosed();
            assertTrue("Commit file command failed: Level: " + a, history.waitCommand("Commit", hText6));
            waitStatus("Up-to-date; 1." + (a + 1), nText6, true);

            VcsRuntimeCommand rc = (VcsRuntimeCommand) history.getWaitCommand("Commit Command", hText6);
            VcsCommandExecutor ce;
            try {
                ce = (VcsCommandExecutor) f.get(rc);
            } catch (IllegalAccessException e) {
                throw new AssertionFailedErrorException("IllegalAccessException while getting executor field value: Level: " + a, e);
            }
            assertTrue ("Execution String does not contain comp level argument: Level: " + a + " Exec: " + ce.getExec (), ce.getExec ().indexOf ("-z " + a) >= 0);
        }
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), nRoot).unmount();
    }

}
