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

import complete.GenericStub.GenericNode;
import java.io.File;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.modules.vcscore.GroupVerificationOperator;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.PVCSAddAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.PVCSGetAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.RefreshRevisionsAction;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.PVCSFileNode;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.*;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;
import org.netbeans.jellytools.util.StringFilter;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.vcscore.runtime.RuntimeCommand;
import org.openide.util.Utilities;


public class StubAllTogether extends PVCSStub {
    
    public StubAllTogether(String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        complete.GenericStub.DEBUG = true;
        TestSuite suite = new NbTestSuite();
        try { if (Runtime.getRuntime().exec(Utilities.isUnix() ? "sh -c \"vlog\"" : "cmd /x /c \"vlog\"").waitFor() != 0) return suite; } catch (Exception e) { e.printStackTrace (); return suite; }
        suite.addTest(new StubAllTogether("configure"));
        suite.addTest(new StubAllTogether("testCreateProject"));
        suite.addTest(new StubAllTogether("testAddSingleFile"));
        suite.addTest(new StubAllTogether("testAddMultipleFiles"));
        suite.addTest(new StubAllTogether("testAddFileWithLock"));
        
        suite.addTest(new StubAllTogether("testCheckoutFile"));
        suite.addTest(new StubAllTogether("testModifyFile"));
        suite.addTest(new StubAllTogether("testViewDifferences"));
        suite.addTest(new StubAllTogether("testCheckinFile"));
        suite.addTest(new StubAllTogether("testViewHistory"));
        suite.addTest(new StubAllTogether("testUnlockFile"));
        suite.addTest(new StubAllTogether("testGetMissingFile"));
        suite.addTest(new StubAllTogether("testRemoveRevision"));
        suite.addTest(new StubAllTogether("testCreateOwnRevision"));
        suite.addTest(new StubAllTogether("testCheckoutRevision"));
        suite.addTest(new StubAllTogether("testLockFile"));
        suite.addTest(new StubAllTogether("testCreateBranch"));
        suite.addTest(new StubAllTogether("testVersioningExplorer"));
        
        suite.addTest(new StubAllTogether("testViewOldRevision"));
        suite.addTest(new StubAllTogether("testCompareRevisions"));
        suite.addTest(new StubAllTogether("testAddToGroup"));
        suite.addTest(new StubAllTogether("testCheckinGroup"));
        suite.addTest(new StubAllTogether("testVerifyGroup"));
        
        suite.addTest(new StubAllTogether("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    GenericNode test, another, A_File, B_File, D_File, C_File;
    static String username = null;
    
    public void createStructure () {
        test = new GenericNode (root, "test");
        another = new GenericNode (test, "another");
        A_File = new GenericNode (root, "A_File", ".java");
        B_File = new GenericNode (test, "B_File", ".java");
        D_File = new GenericNode (another, "D_File", ".java");
//        C_File = new GenericNode (test, "C_File", new String[] { ".java", ".form" }); // bug in add command - serial execution needed
        C_File = new GenericNode (test, "C_File", ".java"); // workaround
    }
    
    public void configure () {
        new File (getWorkFilePath() + File.separator + "client" + File.separator + "test").mkdirs (); // workaround for unreported problem in prepareClient method
        super.configure ();
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
        new Node (exp.repositoryTab().tree (), "").waitChildNotPresent(root.node ());
    }
    
    public void testCreateProject () {
        another.mkdirs ();
//        root.pvcsNode ().pVCSRefresh (); // unreported problem - sometimes does not work - workaround in configure test case
//        history.waitCommand ("Refresh", root.history ());
        test.waitStatus ("Local");
        createProject (test);
        another.waitStatus ("Local");
        createProject (another);
    }
    
    public void testAddSingleFile () {
//        NewWizardOperator.create("Java Classes|Main", A_File.parent ().node (), A_File.name ());
        A_File.save ("/** This is testing file.\n */\n\n public class Testing_File {\n }\n");
        refresh (A_File.parent ());
        
        A_File.waitStatus ("Local");
        A_File.pvcsNode ().pVCSAdd ();
        AddCommandOperator addCommand = new AddCommandOperator(A_File.filename (0));
        addCommand.setWorkfileDescription("Auto-generated class file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.ok();
        addCommand.waitClosed ();
        A_File.waitHistory ("Add");
        A_File.waitStatus ("Current");
    }
    
    public void testAddMultipleFiles () {
        B_File.save ("B_File - Initial content");
        refresh (B_File.parent ());
        B_File.waitStatus ("Local");
        D_File.save ("B_File - Initial content");
        D_File.parent ().pvcsNode ().pVCSRefresh();
        history.waitCommand("Refresh", D_File.parent ().history ());
        D_File.waitStatus ("Local");

        new PVCSAddAction ().perform (new Node [] {
            B_File.pvcsNode (),
            D_File.pvcsNode (),
        });
        AddCommandOperator addCommand = new AddCommandOperator(B_File.filename(0) + " ...");
        addCommand.setWorkfileDescription("Auto-generated class file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.ok();
        addCommand.waitClosed ();
        RuntimeCommand breakpoint = history.getBreakpoint();
        B_File.waitHistory ("Add");
        history.setBreakpoint (breakpoint);
        D_File.waitHistory ("Add");
        history.breakpoint();
        B_File.waitStatus ("Current");
        D_File.waitStatus ("Current");
    }
    
    public void testAddFileWithLock() {
        C_File.save ("Initial save");
//        C_File.save (1, ""); // bug in add command - serial execution needed
        refresh (C_File.parent ());
        C_File.waitStatus ("Local");
        C_File.pvcsNode ().pVCSAdd ();
        AddCommandOperator addCommand = new AddCommandOperator(C_File.filename(0));
        addCommand.setWorkfileDescription("Auto-generated form file.");
        addCommand.setChangeDescription("Initial revision.");
        addCommand.setAssignAVersionLabel("My_Version");
        addCommand.checkKeepTheRevisionLocked(true);
        addCommand.checkFloatLabelWithTheTipRevision(true);
        addCommand.ok();
        addCommand.waitClosed ();
        C_File.waitHistory ("Add");
        C_File.waitStatus ("Current; 1.1");
        C_File.waitLock (true);
    }
    
    public void testCheckoutFile () {
        A_File.waitStatus ("Current");
        A_File.pvcsNode ().pVCSGet ();
        GetCommandOperator getCommand = new GetCommandOperator(A_File.filename (0));
        getCommand.checkLockForTheCurrentUser(true);
        getCommand.checkCheckOutWritableWorkfile(true);
        getCommand.ok();
        getCommand.waitClosed ();
        A_File.waitHistory ("Get");
        A_File.waitStatus ("Current; 1.1");
        username = getLockText(A_File.pvcsNode ().getText());
        A_File.waitLock (true);
        new DeleteAction().perform(A_File.pvcsNode ());
        assertConfirmObjectDeletionNo (null);
        refresh (A_File.parent ());
        A_File.waitStatus ("Current; 1.1");
    }
    
    public void testModifyFile() {
        A_File.waitStatus ("Current; 1.1");
        A_File.save ("/** This is testing A_File.java file.\n */\n public class Testing_File {\n     int i;\n }\n");
//        new OpenAction().perform(A_File.pvcsNode ());
        refreshFile(A_File);
        A_File.waitStatus ("Locally Modified; 1.1");
    }

    public void testViewDifferences() {
        A_File.waitStatus ("Locally Modified; 1.1");
        A_File.pvcsNode ().pVCSDiff ();
        DiffCommandOperator diff = new DiffCommandOperator (A_File.filename (0));
        diff.ok ();
        diff.waitClosed ();

        EditorWindowOperator ewo = new EditorWindowOperator ();
        TopComponentOperator tco = new TopComponentOperator (ewo, "Diff: " + A_File.filename (0));
        try {
            out.println ("!!!! ==== Comparing revisions: HEAD and Local ==== !!!!");
            dumpDiffGraphicalGraphical (tco);
            compareReferenceFiles();
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }
    }
    
    public void testCheckinFile() {
        A_File.waitStatus ("Locally Modified; 1.1");
        A_File.pvcsNode ().pVCSPut ();
        PutCommandOperator putCommand = new PutCommandOperator(A_File.filename (0));
        putCommand.setChangeDescription("Three lines have changed.");
        putCommand.ok();
        putCommand.waitClosed ();
        A_File.waitHistory ("Put");
        A_File.waitStatus ("Current");
        A_File.waitLock (false);
        assertTrue ("A_File remained read-write after check in", A_File.isNotWriteable());
    }
    
    public void testViewHistory() {
        assertNotNull ("UserName field is null", username);
        StringFilter sf = new StringFilter ();
        sf.addReplaceAllFilter(username, "<username>");
        sf.addReplaceFilter("Archive:", A_File.filename(0) + "-arc", "<filepath>");
        sf.addReplaceFilter("Archive created:", "", "<created>");
        sf.addReplaceFilter ("Checked in:", "", "<check_in_date>");
        sf.addReplaceFilter ("Last modified:", "", "<last_modified>");

        closeAllVCSWindows();
        A_File.waitStatus ("Current");
        A_File.pvcsNode ().pVCSHistory ();
        HistoryCommandOperator hi = new HistoryCommandOperator ("");
        hi.ok();
        hi.waitClosed ();
        A_File.waitHistory ("History");
        
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator("History");
        waitNoEmpty(coo.txtStandardOutput ());
        String str = coo.txtStandardOutput().getText ();
        info.println (str);
        printFiltered (str, sf);

        compareReferenceFiles();
        coo.close();
        coo.waitClosed();
    }

    public void testUnlockFile() {
        C_File.waitStatus ("Current; 1.1");
        C_File.waitLock (true);
        unlockFile (C_File, null, null, null);
        C_File.waitStatus ("Current");
        C_File.waitLock (false);
    }

    public void testGetMissingFile() {
        C_File.waitStatus ("Current");
        new DeleteAction().perform(C_File.pvcsNode ());
        assertConfirmObjectDeletionYes(null);
        refresh (C_File.parent ());
        C_File.waitStatus ("Missing");
        Node C_File_Java = new Node (C_File.parent ().pvcsNode (), C_File.filename (0) + " [Missing]");
//        Node C_File_Form = new Node (C_File.parent ().pvcsNode (), C_File.filename (1) + " [Missing]"); // bug in add command - serial execution needed
        new PVCSGetAction ().perform (new Node [] {
            C_File_Java,
//            C_File_Form, // bug in add command - serial execution needed
        });
        
        GetCommandOperator get = new GetCommandOperator (C_File.filename (0));
        get.ok ();
        get.waitClosed ();
        C_File.waitHistory ("Get");

        C_File.waitStatus ("Current");
        C_File.waitLock (false);
        assertTrue ("C_File remained read-write after check out", C_File.isNotWriteable());
    }

    public void testRemoveRevision() {
        D_File.waitStatus ("Current");
        D_File.pvcsNode ().pVCSRemoveRevision();
        assertQuestionYesDialog("Are you sure you want to remove the last revision of the file \"" + D_File.filename (0) + "\"?");
        RemoveCommandOperator removeCommand = new RemoveCommandOperator(D_File.filename (0));
        removeCommand.ok();
        removeCommand.waitClosed ();
        D_File.waitHistory("Remove Revision");
        assertInformationDialog("The last revision of the file \"D_File.java\" was removed successfully.");
        String status = Utilities.isWindows() ? "Locally Modified" : "Current"; // Workaround until #27634 is fixed.
        D_File.waitStatus (status);
    }

    public void testCreateOwnRevision() {
        String status = Utilities.isWindows() ? "Locally Modified" : "Current"; // Workaround until #27634 is fixed.
        D_File.waitStatus (status);
        D_File.pvcsNode ().pVCSPut ();
        PutCommandOperator putCommand = new PutCommandOperator(D_File.filename (0));
        putCommand.setChangeDescription("Assigning own number.");
        putCommand.checkCheckTheWorkfileInAndImmediatelyOut(true);
        putCommand.setAssignARevisionNumber("2.0");
        putCommand.setAssignAVersionLabel("My_Version");
        putCommand.ok();
        putCommand.waitClosed ();
        D_File.waitHistory ("Put");
        D_File.waitStatus ("Current");
    }

    public void testCheckoutRevision() {
        D_File.waitStatus ("Current");
        D_File.pvcsNode ().pVCSGet ();
        GetCommandOperator getCommand = new GetCommandOperator(D_File.filename (0));
        getCommand.setSpecificRevision("2.0");
        getCommand.checkSetTheDateAndTimeOfTheFileToTheCurrentTime(true);
        getCommand.ok();
        getCommand.waitClosed ();
        D_File.waitHistory ("Get");
        D_File.pvcsNode ();
        Date fileTime = new Date(new File (D_File.file ()).lastModified());
        Date currentTime = new Date();
        long timeGap = currentTime.getTime() - fileTime.getTime();
        assertTrue ("Unable to set current time during checkout.", timeGap <= 10000);
    }

    public void testLockFile() {
        D_File.waitStatus ("Current");
        D_File.waitLock (false);
        lockFile(D_File, null, null);
        D_File.waitStatus ("Current; 2.1");
        D_File.waitLock (true);
    }

    public void testCreateBranch() {
        D_File.waitStatus ("Current; 2.1");
        D_File.waitLock (true);
        D_File.pvcsNode ().pVCSPut ();
        PutCommandOperator putCommand = new PutCommandOperator(D_File.filename (0));
        putCommand.setChangeDescription("Starting new branch.");
        putCommand.checkCheckInTheWorkfileEvenIfUnchanged(true);
        putCommand.checkApplyALockOnCheckout(true);
        putCommand.setAssignAVersionLabel("MyBranch");
        putCommand.checkFloatLabelWithTheTipRevision(true);
        putCommand.checkStartABranch(true);
        putCommand.ok();
        putCommand.waitClosed ();
        D_File.waitHistory ("Put");
        D_File.waitStatus ("Current; 2.0.1.1");
        D_File.waitLock (true);
    }

    public void testVersioningExplorer() {
        closeAllVCSWindows();
        D_File.waitStatus ("Current; 2.0.1.1");
        D_File.waitLock (true);
        D_File.pvcsNode ().versioningExplorer();
        newVersioningFrame();
        D_File.pvcsVersioningNode();
        D_File.pvcsVersioningNode("|2.0  Assigning own number.|2.0.1|2.0.1.0  Starting new branch.").select();
        closeAllVCSWindows();
    }

    public void testViewOldRevision() {
        closeAllVCSWindows();
        A_File.waitStatus ("Current");
        A_File.pvcsNode ().versioningExplorer();
        newVersioningFrame ();
        A_File.pvcsVersioningNode("|1.1  Three lines have changed.").select();
        new OpenAction().perform(A_File.pvcsVersioningNode("|1.0  Initial revision."));
        String editorContents = new EditorOperator("A_File.java 1.0").getText();
        info.println ("==== A_File 1.0 getText ====");
        info.println (editorContents);
        info.println ("========");
        assertEquals ("Incorrect version of A_File was opened. - A_File.java 1.0 - not excepted file content", editorContents, "/** This is testing file.\n */\n\n public class Testing_File {\n }\n");
        closeAllVCSWindows();
    }

    public void testCompareRevisions() {
        closeAllVCSWindows();
        A_File.waitStatus ("Current");
        A_File.pvcsNode ().versioningExplorer();
        newVersioningFrame();
        A_File.pvcsVersioningNode("|1.0  Initial revision.");
        new Action (null, "Diff").perform (
            A_File.pvcsVersioningNode("|1.0  Initial revision.")
        );
        EditorWindowOperator ewo = new EditorWindowOperator ();
        TopComponentOperator tco = new TopComponentOperator (ewo, "Diff: " + A_File.filename (0));
        try {
            out.println ("!!!! ==== Comparing revisions: 1.0 and Local ==== !!!!");
            dumpDiffGraphicalGraphical (tco);
            compareReferenceFiles();
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }
//        String headRevisionContents = "/** This is testing file.\n */\n\n public class Testing_File {\n\n }\n";
//        String workingRevisionContents = "/** This is testing A_File.java file.\n */\n\n public class Testing_File {\n     int i;\n }\n";
    }

    public void testAddToGroup() {
        closeAllVCSWindows();
        B_File.waitStatus ("Current");
        B_File.pvcsNode ().includeInVCSGroupDefaultGroup();
        openGroupsFrame();
        B_File.pvcsGroupNode(DEFAULT_GROUP, " [Current]").select ();
    }

    public void testCheckinGroup() {
        closeAllVCSWindows();
        B_File.waitStatus ("Current");
        B_File.pvcsNode ().pVCSGet ();
        GetCommandOperator getCommand = new GetCommandOperator(B_File.filename (0));
        getCommand.checkLockForTheCurrentUser(true);
        getCommand.checkCheckOutWritableWorkfile(true);
        getCommand.ok();
        getCommand.waitClosed ();
        B_File.waitHistory ("Get");
        B_File.save ("/** This is testing B_File.java file.\n */\n public class B_File {\n     int i = 1;\n }\n");
        openGroupsFrame();

        closeAllProperties();
        new PVCSFileNode (vgf.treeVCSGroupsTreeView(), DEFAULT_GROUP).select (); // stabilization
        sleep (2000); // stabilization
        new PVCSFileNode (vgf.treeVCSGroupsTreeView(), DEFAULT_GROUP).properties ();
        PropertySheetOperator pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, DEFAULT_GROUP);
        PropertySheetTabOperator pst = pso.getPropertySheetTabOperator("Properties");
        new StringProperty(pst, "Description").setValue("Checked in from VCS group.");
        sleep (2000); // stabilization
        info.println ("Description property: " + new StringProperty (pst, "Description").getStringValue ());
        sleep (2000); // stabilization
        pso.close ();
        waitIsShowing(pso.getSource ());
        sleep (2000); // stabilization
        
        closeAllVCSWindows();
        openGroupsFrame();
        new PVCSFileNode (vgf.treeVCSGroupsTreeView(), DEFAULT_GROUP).pVCSPut ();
        PutCommandOperator putCommand = new PutCommandOperator(B_File.filename (0));
        String changeDescription = putCommand.getChangeDescription();
        out.println ("DEFAULT_GROUP - Put Description: " + changeDescription); // "Checked in from VCS group.\n"
        putCommand.ok();
        putCommand.waitClosed ();
        B_File.waitHistory ("Put");
        
        closeAllVCSWindows ();
        B_File.pvcsNode ().versioningExplorer();
        newVersioningFrame();
        B_File.pvcsVersioningNode(".java [Current]");
        new RefreshRevisionsAction ().perform (B_File.pvcsVersioningNode ());
        B_File.waitHistory ("REVISION_LIST");
        B_File.pvcsVersioningNode(".java [Current]|1.1  " + changeDescription).select();
        
        compareReferenceFiles();
    }

    public void testVerifyGroup() {
        closeAllVCSWindows();
        openGroupsFrame();
        vgf.verifyVCSGroup(DEFAULT_GROUP);
        GroupVerificationOperator verifyDialog = new GroupVerificationOperator();
        verifyDialog.checkRemoveFilesFromGroup(true);
        dumpVerifyGroupTable(verifyDialog.tabNotChangedFiles());
        verifyDialog.correctGroup();
        verifyDialog.waitClosed ();
        new Node (vgf.treeVCSGroupsTreeView(), DEFAULT_GROUP).waitChildNotPresent(B_File.name ());
    }

}
