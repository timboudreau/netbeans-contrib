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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.PVCSAddAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.PVCSGetAction;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.*;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.AddCommandOperator;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.*;
import org.netbeans.jellytools.util.StringFilter;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.junit.NbTestSuite;
import org.openide.util.Utilities;

public class StubAllTogether extends PVCSStub {
    
    public StubAllTogether(String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        complete.GenericStub.DEBUG = true;
        TestSuite suite = new NbTestSuite();
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
/*        suite.addTest(new StubAllTogether("testRemoveRevision"));
        suite.addTest(new StubAllTogether("testCreateOwnRevision"));
        suite.addTest(new StubAllTogether("testCheckoutRevision"));
        suite.addTest(new StubAllTogether("testLockFile"));
        suite.addTest(new StubAllTogether("testCreateBranch"));
        suite.addTest(new StubAllTogether("testVersioningExplorer"));
        
        suite.addTest(new AdditionalFeatures("testViewOldRevision"));
        suite.addTest(new AdditionalFeatures("testCompareRevisions"));
        suite.addTest(new AdditionalFeatures("testAddToGroup"));
        suite.addTest(new AdditionalFeatures("testCheckinGroup"));
        suite.addTest(new AdditionalFeatures("testVerifyGroup"));
*/        
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
        super.configure ();
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
        new Node (exp.repositoryTab().tree (), "").waitChildNotPresent(root.node ());
    }

    public void testCreateProject () {
        another.mkdirs ();
        refresh (root);
        test.waitStatus ("Local");
        createProject (test);
        another.waitStatus ("Local");
        createProject (another);
    }
    
    public void testAddSingleFile () {
//        NewWizardOperator.create("Java Classes|Main", A_File.parent ().node (), A_File.name ());
        A_File.save ("/** This is testing file.\n */\n\n public class Testing_File {\n\n }\n");
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
        refresh (D_File.parent ());
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
        B_File.waitHistory ("Add");
        D_File.waitHistory ("Add");
        B_File.waitStatus ("Current");
        D_File.waitStatus ("Current");
    }
    
    public void testAddFileWithLock() throws Exception {
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
    
    public void testModifyFile() throws Exception {
        A_File.waitStatus ("Current; 1.1");
        A_File.save ("/** This is testing A_File.java file.\n */\n public class Testing_File {\n     int i;\n }\n");
//        new OpenAction().perform(A_File.pvcsNode ());
        refreshFile(A_File);
        A_File.waitStatus ("Locally Modified; 1.1");
    }

    public void testViewDifferences() throws Exception {
        A_File.waitStatus ("Locally Modified; 1.1");
        A_File.pvcsNode ().pVCSDiff ();
        DiffCommandOperator diff = new DiffCommandOperator (A_File.filename (0));
        diff.ok ();
        diff.waitClosed ();

        EditorWindowOperator ewo = new EditorWindowOperator ();
        TopComponentOperator tco = new TopComponentOperator (ewo, "Diff: " + A_File.filename (0));
        try {
            out.println ("!!!! ==== Comparing revisions: HEAD and Local ==== !!!!");
            dumpDiffGraphical (tco);
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }

        compareReferenceFiles();
    }
    
    public void testCheckinFile() throws Exception {
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
    
    public void testViewHistory() throws Exception {
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

    public void testUnlockFile() throws Exception {
        C_File.waitStatus ("Current; 1.1");
        C_File.waitLock (true);
        unlockFile (C_File, null, null, null);
        C_File.waitStatus ("Current");
        C_File.waitLock (false);
    }

    public void testGetMissingFile() throws Exception {
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

        C_File.waitStatus ("Current");
        C_File.waitLock (false);
        assertTrue ("C_File remained read-write after check in", C_File.isNotWriteable());
    }

}
