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

package complete.cvs_profile;

import complete.GenericStub.GenericNode;
import java.io.*;
import java.lang.reflect.Field;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSAddAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSCommitAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSRemoveAction;
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
import util.History;



public class JellyAddFeatures extends CVSStub {
    
    public JellyAddFeatures(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyAddFeatures("configure"));
        suite.addTest(new JellyAddFeatures("testAddCommit"));
//        suite.addTest(new JellyAddFeatures("testForceCommit")); // fails due to bug #27997
        suite.addTest(new JellyAddFeatures("testRemoveCommit"));
        suite.addTest(new JellyAddFeatures("testRunProgram"));
        suite.addTest(new JellyAddFeatures("testToStandardOutput"));
//        suite.addTest(new JellyAddFeatures("testQuiet")); // fails due to bug #28517
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
    
    static String tInitDir = "initdir";
    GenericNode InitDir, SubInitDir, Text1, Text2, Text3, Text4, Text5, Text6;
    
    static boolean text1InDefaultGroup = false;

    protected void prepareServer (String dir) {
        new File(dir + "/" + tInitDir).mkdirs();
    }
    
    public void createStructure () {
        InitDir = new GenericNode (root, tInitDir);
        SubInitDir = new GenericNode (InitDir, "subdir");
        Text1 = new GenericNode (InitDir, "text1");
        Text2 = new GenericNode (InitDir, "text2");
        Text3 = new GenericNode (SubInitDir, "text3");
        Text4 = new GenericNode (InitDir, "text4");
        Text5 = new GenericNode (InitDir, "text5");
        Text6 = new GenericNode (InitDir, "text6");
    }
    
    public void configure () {
        super.configure();
    }

    public void testAddCommit () {
        Text1.save ("");
        Text2.save ("");
        SubInitDir.mkdirs ();
        Text3.save ("");
        Text6.save ("");

        root.cvsNode ().cVSRefreshRecursively();
        root.waitHistory ("Refresh Recursively");
        Text1.cvsNode ();
        Text2.cvsNode ();
        Text6.cvsNode ();

        new CVSAddAction ().perform (new Node[] {
            Text1.cvsNode (),
            Text2.cvsNode (),
            Text6.cvsNode (),
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
        waitCommand("Add", new GenericNode[] { Text1, Text2, Text6 });
        waitCommand("Commit", new GenericNode[] { Text1, Text2, Text6 });
        Text1.waitStatus ("Up-to-date; 1.1");
        Text2.waitStatus ("Up-to-date; 1.1");
        Text6.waitStatus ("Up-to-date; 1.1");

        SubInitDir.cvsNode ().cVSAdd();
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
        SubInitDir.waitHistory ("Add");
        SubInitDir.waitHistory ("Commit");
        SubInitDir.waitStatus (null);
        Text3.waitStatus("Up-to-date; 1.1");
    }
    
    public void testForceCommit () {
        Text1.waitStatus("Up-to-date; 1.1");
        Text2.waitStatus("Up-to-date; 1.1");
        Text3.waitStatus("Up-to-date; 1.1");
        new CVSCommitAction ().perform (new Node[] {
            Text1.cvsNode (),
            Text2.cvsNode (),
            Text3.cvsNode (),
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
        waitCommand("Remove", new GenericNode [] { Text1, Text2, Text3 });
        Text1.waitStatus("Up-to-date; 1.2");
        Text2.waitStatus("Up-to-date; 1.2");
        Text3.waitStatus("Up-to-date; 1.2");
    }

    public void testRemoveCommit () {
        new CVSRemoveAction ().perform (new Node[] {
            Text1.cvsNode (),
            Text2.cvsNode (),
        });
        CVSRemoveDialog rd = new CVSRemoveDialog ();
        rd.no();
        rd.waitClosed();
        Text1.waitStatus("Up-to-date", false);
        Text2.waitStatus("Up-to-date", false);
        new CVSRemoveAction ().perform (new Node[] {
            Text1.cvsNode (),
            Text2.cvsNode (),
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
        waitCommand ("Remove", new GenericNode [] { Text1, Text2 } );
        waitCommand ("Commit", new GenericNode [] { Text1, Text2 } );
        InitDir.cvsNode ().waitChildNotPresent(Text1.name ());
        InitDir.cvsNode ().waitChildNotPresent(Text2.name ());

        SubInitDir.cvsNode ().cVSRemove();
        rd = new CVSRemoveDialog ();
        rd.no();
        rd.waitClosed();
        SubInitDir.waitStatus(null);
        Text3.waitStatus("Up-to-date", false);
        SubInitDir.cvsNode ().cVSRemove();
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
        SubInitDir.waitHistory("Remove");
        SubInitDir.waitHistory("Commit");
        SubInitDir.cvsNode ().waitChildNotPresent(Text3.name ());
    }

    public void testRunProgram () {
        Text4.save ("");
        Text5.save ("");
        
        Field f;
        try {
            f = VcsRuntimeCommand.class.getDeclaredField("executor");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new AssertionFailedErrorException("NoSuchFieldException while setting test case up", e);
        } catch (SecurityException e) {
            throw new AssertionFailedErrorException("SecurityException while setting test case up", e);
        }

        root.cvsNode ().cVSRefreshRecursively();
        root.waitHistory("Refresh Recursively");
        Text4.cvsNode ();
        
        Text4.cvsNode ().cVSAdd ();
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
        Text4.waitHistory ("Add");
        Text4.waitHistory ("Commit");
        Text4.waitStatus("Up-to-date; 1.1");
        VcsRuntimeCommand rc = (VcsRuntimeCommand) history.getWaitCommand("Commit Command", Text4.history ());
        VcsCommandExecutor ce;
        try {
            ce = (VcsCommandExecutor) f.get(rc);
        } catch (IllegalAccessException e) {
            throw new AssertionFailedErrorException("IllegalAccessException while getting executor field value", e);
        }
        assertTrue ("Execution String does not contain No-Run argument", ce.getExec ().indexOf ("-n") >= 0);
        
        Text5.cvsNode ().cVSAdd ();
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
        Text5.waitHistory ("Add");
        Text5.waitHistory ("Commit");
        Text5.waitStatus("Up-to-date; 1.1");
        rc = (VcsRuntimeCommand) history.getWaitCommand("Commit Command", Text5.history ());
        try {
            ce = (VcsCommandExecutor) f.get(rc);
        } catch (IllegalAccessException e) {
            throw new AssertionFailedErrorException("IllegalAccessException while getting executor field value", e);
        }
        assertTrue ("Execution String contains No-Run argument", ce.getExec ().indexOf ("-n") < 0);
    }
    
    public void testToStandardOutput () {
        Text4.waitStatus("Up-to-date; 1.1");
        Text4.cvsNode ().cVSUpdate ();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.checkSendUpdatesToStandardOutput(true);
        up.oK();
        up.waitClosed ();
        Text4.waitHistory("Update");
        
        closeAllVCSOutputs();
        viewOutput("UPDATE_CMD", Text4);
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
        Text4.waitStatus("Up-to-date; 1.1");
        Text4.cvsNode ().cVSStatus ();
        CVSStatusFileAdvDialog stat = new CVSStatusFileAdvDialog ();
        stat.setQuietness(CVSStatusFileAdvDialog.ITEM_BESOMEWHATQUIET);
        stat.oK();
        stat.waitClosed();
        RuntimeCommand rc = history.getWaitCommand("Status", Text4.history ());
        assertTrue("Status file command failed", History.resultCommand (rc));
        
        closeAllVCSOutputs();
        viewOutput(rc);
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("Status");
        assertTrue ("Execution string does not contain -q", coo.txtExecutionString().getText ().indexOf ("-q") >= 0);
        closeAllVCSOutputs();
        
        Text4.waitStatus("Up-to-date; 1.1");
        Text4.cvsNode ().cVSStatus ();
        stat = new CVSStatusFileAdvDialog ();
        stat.setQuietness(CVSStatusFileAdvDialog.ITEM_BEREALLYQUIET);
        stat.oK();
        stat.waitClosed();
        rc = history.getWaitCommand("Status", Text4.history ());
        assertTrue("Status file command failed", History.resultCommand (rc));
        
        closeAllVCSOutputs();
        viewOutput(rc);
        coo = new VCSCommandsOutputOperator ("Status");
        assertTrue ("Execution string does not contain -Q", coo.txtExecutionString().getText ().indexOf ("-Q") >= 0);
        closeAllVCSOutputs();
        
        Text5.waitStatus("Up-to-date; 1.1");
        new DeleteAction ().perform (Text5.cvsNode ());
        assertConfirmObjectDeletionYes (null);
        Text5.waitStatus("Needs Update", false); // sometimes fails due to bug #28399
        Text5.cvsNode ().cVSUpdate ();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.setQuietness(CVSUpdateFileAdvDialog.ITEM_BEREALLYQUIET);
        up.oK();
        up.waitClosed();
        Text5.waitHistory ("Update");
        
        closeAllVCSOutputs();
        viewOutput("UPDATE_CMD", Text5);
        coo = new VCSCommandsOutputOperator ("UPDATE_CMD");
        System.out.println(coo.tabbedPane().getTitleAt(0));
        System.out.println(coo.tabbedPane().isEnabledAt(0));
        assertTrue ("Standard Output contains text", !coo.tabbedPane().isEnabledAt(0));
        closeAllVCSOutputs();
    }
    
    public void testRemovePreview () {
        Text5.waitStatus("Up-to-date; 1.1");
        Text5.cvsNode ().cVSRemove ();
        CVSRemoveDialog rem = new CVSRemoveDialog ();
        rem.yes ();
        rem.waitClosed();
        CVSRemoveFileAdvDialog rr = new CVSRemoveFileAdvDialog ();
        rr.checkDoNotMakeChangesPreview(true);
        rr.oK ();
        rr.waitClosed ();
        Text5.waitHistory ("Remove");
        Text5.waitStatus("Up-to-date; 1.1");
    }
    
    public void testRemoveDebug () {
        Text5.waitStatus("Up-to-date; 1.1");
        Text5.cvsNode ().cVSRemove ();
        CVSRemoveDialog rem = new CVSRemoveDialog ();
        rem.yes ();
        rem.waitClosed();
        CVSRemoveFileAdvDialog rr = new CVSRemoveFileAdvDialog ();
        rr.checkDoNotMakeChangesPreview(true);
        rr.checkShowTraceOfCommandExecution(true);
        rr.oK ();
        rr.waitClosed ();
        RuntimeCommand rc = history.getWaitCommand("Remove", Text5.history ());
        assertTrue("Remove file command failed", History.resultCommand(rc));
        Text5.waitStatus("Up-to-date; 1.1");
        
        closeAllVCSOutputs ();
        viewOutput(rc);
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
        Text5.save ("T5-1");
        
        root.cvsNode ().cVSRefreshRecursively();
        root.waitHistory ("Refresh Recursively");
        Text5.waitStatus("Locally Modified; 1.1");

        Text5.cvsNode ().cVSCommit ();
        CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog();
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Next commit of text5");
        com.checkDisableHistoryLogging(true);
        com.oK();
        com.waitClosed();
        Text5.waitHistory("Commit");
        Text5.waitStatus("Up-to-date; 1.2");
        
        BufferedReader br = null;
        try {
            br = new BufferedReader (new FileReader (root.file () + "/../server/CVSROOT/history"));
            for (;;) {
                String str = br.readLine ();
                if (str == null)
                    break;
                assertTrue ("History contains information about commiting version 1.2 of text5 file", str.indexOf ("1.2") < 0  ||  str.indexOf (Text5.name ()) < 0);
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
        new DeleteAction ().perform (Text4.cvsNode ());
        assertConfirmObjectDeletionYes (null);
        Text4.waitStatus("Needs Update; 1.1");
        Text4.cvsNode ().cVSUpdate ();
        CVSUpdateFolderAdvDialog up = new CVSUpdateFolderAdvDialog ();
        up.checkCheckOutFilesAsReadOnly(true);
        up.oK ();
        up.waitClosed ();
        Text4.waitHistory ("Update");
        Text4.waitStatus("Up-to-date; 1.1");
        assertTrue ("File is not read-only", !new File (Text4.file ()).canWrite ());
    }
    
    public void testCompressionLevel () {
        closeAllVCSWindows();
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
            Text6.waitStatus("Up-to-date; 1." + a);
            Text6.save ("T6-" + a);
            Text6.cvsNode ().cVSRefresh ();
            Text6.waitHistory ("Refresh");
            
            Text6.cvsNode ().cVSCommit ();
            CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog ();
            com.setCompressionLevel("" + a);
            com.oK ();
            com.waitClosed();
            Text6.waitHistory ("Commit");
            Text6.waitStatus("Up-to-date; 1." + (a + 1));

            VcsRuntimeCommand rc = (VcsRuntimeCommand) history.getWaitCommand("Commit Command", Text6.history ());
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
        new FilesystemNode(repository.tree(), root.node ()).unmount();
        new Node (repository.tree(), "").waitChildNotPresent(root.node ());
    }

}
