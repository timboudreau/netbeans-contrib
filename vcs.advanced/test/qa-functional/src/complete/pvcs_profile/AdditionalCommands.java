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
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.*;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.*;
import org.netbeans.jellytools.util.StringFilter;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.junit.NbTestSuite;
import org.openide.util.Utilities;

public class AdditionalCommands extends PVCSStub {
    
    public AdditionalCommands(String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        complete.GenericStub.DEBUG = true;
        TestSuite suite = new NbTestSuite();
        try { if (Runtime.getRuntime().exec(Utilities.isUnix() ? "sh -c \"vlog\"" : "cmd /x /c \"vlog\"").waitFor() != 0) return suite; } catch (Exception e) { e.printStackTrace (); return suite; }
        suite.addTest(new AdditionalCommands("configure"));
        suite.addTest(new AdditionalCommands("testUnlockSpecificRevision"));
        suite.addTest(new AdditionalCommands("testLockSpecificRevision"));
        suite.addTest(new AdditionalCommands("testUnlockByUser"));
        suite.addTest(new AdditionalCommands("testLockByVersionLabel"));
        suite.addTest(new AdditionalCommands("testUnlockTrunkRevision"));
        suite.addTest(new AdditionalCommands("testLocalRefreshing"));
        suite.addTest(new AdditionalCommands("testRecursiveCheckout"));
        suite.addTest(new AdditionalCommands("testRecursiveRefresh"));
        suite.addTest(new AdditionalCommands("testRecursiveCheckoutByDate"));
        suite.addTest(new AdditionalCommands("testCheckoutNewerRevision"));
        suite.addTest(new AdditionalCommands("testCheckoutOlderRevision"));
        suite.addTest(new AdditionalCommands("testDiffSpecificRevisions"));
        suite.addTest(new AdditionalCommands("testDiffByVersionLabels"));
        suite.addTest(new AdditionalCommands("testDiffNoDifferences"));
        suite.addTest(new AdditionalCommands("testDiffWhitespaces"));
        suite.addTest(new AdditionalCommands("testGenerateDeltaFile"));
        suite.addTest(new AdditionalCommands("testApplyDeltaFile"));
        suite.addTest(new AdditionalCommands("testMergeByRevisions"));
        suite.addTest(new AdditionalCommands("testMergeByVersionLabels"));
        suite.addTest(new AdditionalCommands("testViewSelectedInformation"));
        suite.addTest(new AdditionalCommands("testRemoveParticularRevision"));
        suite.addTest(new AdditionalCommands("testRemoveParticularRevision2"));
        suite.addTest(new AdditionalCommands("testSetPassword"));
        suite.addTest(new AdditionalCommands("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    GenericNode test, another, A_File, B_File, D_File, Formular;
    
    public void createStructure () {
        test = new GenericNode (root, "test");
        another = new GenericNode (test, "another");
        A_File = new GenericNode (root, "A_File", ".java");
        B_File = new GenericNode (test, "B_File", ".java");
        D_File = new GenericNode (another, "D_File", ".java");
//        Formular = new GenericNode (test, "Formular", new String[] { ".java", ".form" }); // bug in add command - serial execution needed
        Formular = new GenericNode (test, "Formular", ".java" ); // workaround for previous line
    }
    
    public void prepareClient () {
        super.prepareClient ();
        
        test.mkdirs();
        another.mkdirs ();
        root.pvcsNode ().pVCSRefresh ();
        history.waitCommand ("Refresh", root.history ());
        test.waitStatus ("Local");
        createProject (test);
        test.pvcsNode ().pVCSRefresh ();
        history.waitCommand ("Refresh", test.history ());
        another.waitStatus ("Local");
        createProject (another);

        NewWizardOperator.create("Java Classes|Main", A_File.parent ().node (), "A_File");
        A_File.waitStatus ("Local");
        addFile (A_File, "A_File add description");
        A_File.waitStatus ("Current; 1.1");

        A_File.save ("Version 1\nLine1\nLine3\nLine4\nLine6\nLine7\nLine8\n");
        refreshFile(A_File);
        A_File.waitStatus ("Locally Modified; 1.1");
//        putFileLabel (A_File, "Revision 1", "First for Diff"); // cause error - issue #28995
        putFileLabel (A_File, "Revision1", "First for Diff"); // workaround
        A_File.waitStatus ("Current; 1.2");

        A_File.save ("Version 2\nLine1\n  Line2\nLine4\n  Line5\nLine6\nLine8\n");
        refreshFile(A_File);
        A_File.waitStatus ("Locally Modified; 1.2");
//        putFileLabel (A_File, "Revision 2", "Second for Diff"); // cause error - issue #28995
        putFileLabel (A_File, "Revision2", "Second for Diff"); // workaround
        A_File.waitStatus ("Current; 1.3");

        A_File.save ("    Version 2\nLine1\n  Line2\n  Line4\nLine5\nLine6\n  Line8\n");
        refreshFile(A_File);
        A_File.waitStatus ("Locally Modified; 1.3");
//        putFileLabel (A_File, "Revision 3", "Third for Diff"); // cause error - issue #28995
        putFileLabel (A_File, "Revision3", "Third for Diff"); // workaround
        A_File.waitStatus ("Current; 1.4");

        NewWizardOperator.create("Java Classes|Class", B_File.parent ().node (), "B_File");
        B_File.waitStatus ("Local");
        addFile (B_File, "B_File add description");
        B_File.waitStatus ("Current; 1.1");

        B_File.save ("Version B 1\nLine1\nLine3\nLine5\n");
        refreshFile(B_File);
        B_File.waitStatus ("Locally Modified; 1.1");
//        putFileLabel (B_File, "Revision B1", "First for Diff"); // cause error - issue #28995
        putFileLabel (B_File, "RevisionB1", "First for Diff"); // workaround
        B_File.waitStatus ("Current; 1.2");

        B_File.save ("Version B 1\nLine1\nLine2\nLine3\nLine5\n");
        refreshFile(B_File);
        B_File.waitStatus ("Locally Modified; 1.2");
//        putFileLabel (B_File, "Revision B2", "Second for Diff"); // cause error - issue #28995
        putFileLabel (B_File, "RevisionB2", "Second for Diff"); // workaround
        B_File.waitStatus ("Current; 1.3");

        B_File.save ("Version B 1\nLine1\nLine3\nLine4\nLine5\n");
        refreshFile(B_File);
        B_File.waitStatus ("Locally Modified; 1.3");
//        putFileLabel (B_File, "Revision B3", "Third for Diff"); // cause error - issue #28995
        putFileLabel (B_File, "RevisionB3", "Third for Diff"); // workaround
        B_File.waitStatus ("Current; 1.4");

        NewWizardOperator.create("Java Classes|Class", D_File.parent ().node (), "D_File");
        D_File.waitStatus ("Local");
        addFile (D_File, "D_File add description");
        D_File.waitStatus ("Current; 1.1");

        putFileVersion (D_File, "2.0", "description of 2.0 version");
        D_File.waitStatus ("Current; 2.1");
        putFileBranch (D_File, "floating_version", "description of floating_version");
        D_File.waitStatus ("Current; 2.0.1.1");

//        NewWizardOperator.create("Java GUI Forms|JFrame", Formular.parent ().node (), "Formular"); // bug in add command - serial execution needed
        NewWizardOperator.create("Java Classes|Main", Formular.parent ().node (), "Formular"); // workaround for previous line
        Formular.waitStatus ("Local");
        addFileLabel (Formular, "My_Version", "Formular add description");
        Formular.waitStatus ("Current; 1.1");

        unlockFile (Formular, null, null, null);
        Formular.waitStatus ("Current");
    }
    
    public void configure () {
        super.configure ();
    }
    
    public void testUnlockSpecificRevision () {
        unlockFile (D_File, "2.0.1.1", null, null);
        D_File.waitStatusLock ("Current");
    }
    
    public void testLockSpecificRevision () {
        lockFile (D_File, "2.0", null);
        D_File.waitStatusLock ("Current; 2.1");
    }
    
    public void testUnlockByUser () {
        String username = getLockText(D_File.pvcsNode ().getText());
        unlockFile (D_File, null, null, username);
        D_File.waitStatusLock ("Current");
    }
    
    public void testLockByVersionLabel () {
        lockFile(Formular, null, "My_Version");
        Formular.waitStatusLock ("Current; 1.1");
    }
    
    public void testUnlockTrunkRevision () {
        unlockFile (Formular, null, null, null);
        Formular.waitStatusLock ("Current");
    }
    
    public void testLocalRefreshing () {
        D_File.delete ();
//        refresh(D_File); // cause error - issue #28925
        refresh (D_File.parent ()); // workaround
        D_File.waitStatus ("Missing");

        getFile (D_File, null, "floating_version");
        D_File.waitStatus ("Current; 2.0.1.1");

        D_File.delete ();
//        refresh(D_File); // cause error - issue #28925
        refresh (D_File.parent ()); // workaround
        D_File.waitStatus ("Missing; 2.0.1.1");
    }
    
    public void testRecursiveCheckout () {
        test.pvcsNode ().pVCSGet();
        GetCommandOperator dia = new GetCommandOperator ("");
        dia.checkGetAllSubdirectories(false);
        dia.ok ();
        dia.waitClosed ();
        assertQuestionYesDialog (null);
//        assertQuestionYesDialog (null);
        assertRetrievingDialog ();
        D_File.waitStatus ("Missing; 2.0.1.1");

        test.pvcsNode ().pVCSGet();
        dia = new GetCommandOperator ("");
        dia.checkGetAllSubdirectories(true);
        dia.ok ();
        dia.waitClosed ();
//        assertQuestionNoDialog (null);
//        assertQuestionNoDialog (null);
        assertRetrievingDialog ();
        D_File.waitStatus ("Current; 2.0.1.1");
    }

    public void testRecursiveRefresh () {
        D_File.delete ();
        test.pvcsNode ().pVCSRefreshRecursively();
// probably bug
//        another.waitHistory ("Refresh"); 
//        assertRetrievingDialog ();
        D_File.waitStatus ("Missing; 2.0.1.1");
    }
    
    public void testRecursiveCheckoutByDate () {
        test.pvcsNode ().pVCSGet ();
        GetCommandOperator dia = new GetCommandOperator ("");
        dia.checkCheckOutByDate(true);
        dia.ok ();
        dia.waitClosed ();
        assertRetrievingDialog ();
        D_File.waitStatus ("Current; 2.0.1.1");
    }
    
    public void testCheckoutNewerRevision () {
        D_File.delete ();
//        refresh(D_File); // cause error - issue #28925
        refresh (D_File.parent ()); // workaround
        D_File.waitStatus ("Missing; 2.0.1.1");
        D_File.pvcsNode().pVCSGet();
        GetCommandOperator dia = new GetCommandOperator ("");
        dia.checkCheckOutByDate(true);
        dia.revisionNewerThanDateTime();
        String date = new SimpleDateFormat ("MM/dd/yy HH:mm:ss").format (new Date (System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        info.println ("Date: " + date);
        dia.setNewerThanDate(date);
        dia.ok ();
        dia.waitClosed ();
        D_File.waitStatus ("Current; 2.0.1.1");
    }
    
    public void testCheckoutOlderRevision () {
        D_File.delete ();
//        refresh(D_File); // cause error - issue #28925
        refresh (D_File.parent ()); // workaround
        D_File.waitStatus ("Missing; 2.0.1.1");
        D_File.pvcsNode().pVCSGet();
        GetCommandOperator dia = new GetCommandOperator ("");
        dia.checkCheckOutByDate(true);
        dia.revisionCheckedInBefore();
        String date = new SimpleDateFormat ("MM/dd/yy HH:mm:ss").format (new Date (System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        info.println ("Date: " + date);
        dia.setCheckedInBefore(date);
        dia.ok ();
        dia.waitClosed ();
        D_File.waitStatus ("Current; 2.0.1.1");
    }
    
    public void testDiffSpecificRevisions () {
        A_File.pvcsNode ().pVCSDiff ();
        DiffCommandOperator diff = new DiffCommandOperator ("");
        diff.setRevision1("1.1");
        diff.setRevision2("1.2");
        diff.ok();
        diff.waitClosed();
        A_File.waitHistory("Diff");
        
        EditorWindowOperator ewo = new EditorWindowOperator ();
        TopComponentOperator tco = new TopComponentOperator (ewo, "Diff: " + A_File.filename (0));
        try {
            out.println ("!!!! ==== Graphical comparing revisions: 1.1 and 1.2 ==== !!!!");
            dumpDiffGraphical (tco);
            out.println ("!!!! ==== Textual comparing revisions: 1.1 and 1.2 ==== !!!!");
            dumpDiffGraphicalTextual(tco);
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }
        compareReferenceFiles();
    }
    
    public void testDiffByVersionLabels () {
        A_File.pvcsNode ().pVCSDiff ();
        DiffCommandOperator diff = new DiffCommandOperator ("");
        diff.versionLabel1();
//        diff.setVersionLabel1("Revision 1"); // cause error - issue #28995
        diff.setVersionLabel1("Revision1"); // workaround
        diff.versionLabel2();
//        diff.setVersionLabel2("Revision 2"); // cause error - issue #28995
        diff.setVersionLabel2("Revision2"); // workaround
        diff.ok();
        diff.waitClosed();
        A_File.waitHistory("Diff");
        
        EditorWindowOperator ewo = new EditorWindowOperator ();
        TopComponentOperator tco = new TopComponentOperator (ewo, "Diff: " + A_File.filename (0));
        try {
            out.println ("!!!! ==== Graphical comparing labels: Revision 1 and Revision 2 ==== !!!!");
            dumpDiffGraphical (tco);
            out.println ("!!!! ==== Textual comparing labels: Revision 1 and Revision 2 ==== !!!!");
            dumpDiffGraphicalTextual(tco);
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }
        compareReferenceFiles();
    }
    
    public void testDiffNoDifferences () {
        A_File.pvcsNode ().pVCSDiff ();
        DiffCommandOperator diff = new DiffCommandOperator ("");
        diff.ok();
        diff.waitClosed();
        assertInformationDialog ("No differences were found in " + A_File.filename (0) + ".");
        A_File.waitHistory("Diff");
    } 
    
    public void testDiffWhitespaces () {
        A_File.pvcsNode ().pVCSDiff ();
        DiffCommandOperator diff = new DiffCommandOperator ("");
        diff.setRevision1("1.2");
        diff.setRevision2("1.3");
        diff.checkIgnoreWhiteSpacesAtBeginningAndEndOfLine(true);
        diff.ok();
        diff.waitClosed();
        assertInformationDialog ("No differences were found in " + A_File.filename (0) + ".");
        A_File.waitHistory("Diff");
    }
    
    public void testGenerateDeltaFile () {
        String deltafilepath = root.file() + "/../deltafile";
        new File (deltafilepath).delete();

        A_File.pvcsNode ().pVCSDiff ();
        DiffCommandOperator diff = new DiffCommandOperator ("");
        diff.setRevision1("1.2");
        diff.setRevision2("1.3");
        diff.checkGenerateDeltaFile(true);
        diff.setDeltaOutputFile(deltafilepath);
        diff.ok();
        diff.waitClosed();
        A_File.waitHistory("Diff");
        
        out.println ("Delta File:");
        out.println (loadBinFile (deltafilepath));
        compareReferenceFiles();
    }
    
    public void testApplyDeltaFile () {
        String deltafilepath = root.file() + "/../deltafile";
        lockFile (A_File, null, null);
        A_File.waitLock (true);
        unlockFile(A_File, null, null, getLockText (A_File.pvcsNode ().getText()));

        A_File.pvcsNode().pVCSGet ();
        GetCommandOperator get = new GetCommandOperator ("");
        get.setSpecificRevision("1.2");
        get.checkLockForTheCurrentUser(true);
        get.ok ();
        get.waitClosed ();
        assertQuestionYesDialog (null);
        A_File.waitHistory("Get");
        A_File.parent ().waitHistory("Refresh");

        A_File.waitStatus ("Locally Modified; 1.2.1.0");
        A_File.pvcsNode ().pVCSApplyDelta();
        
        ApplyDeltaCommandOperator adco = new ApplyDeltaCommandOperator ("");
        adco.setDeltaFile(deltafilepath);
        adco.ok ();
        adco.waitClosed();
        A_File.waitHistory("Apply Delta");
        
        A_File.pvcsNode ().pVCSDiff ();
        DiffCommandOperator diff = new DiffCommandOperator ("");
        diff.ok ();
        diff.waitClosed ();
        assertInformationDialog ("No differences were found in " + A_File.filename (0) + ".");
        A_File.waitHistory ("Diff");
    }
    
    public void testMergeByRevisions () {
        String mergefilepath = getWorkFilePath () + "/mergefile";
        new File (mergefilepath).delete ();
        
        B_File.pvcsNode().pVCSMerge();
        MergeCommandOperator merge = new MergeCommandOperator ("");
        merge.setRevisionNumber("1.1");
        merge.setRevisionNumber2("1.2");
        merge.setRevisionNumber3("1.3");
        merge.setOutputFile(mergefilepath);
        merge.ok ();
        merge.waitClosed ();
        B_File.waitHistory("Merge");
        B_File.parent ().waitHistory("Refresh");

        B_File.waitStatus ("Missing; 1.4");
        new File (B_File.parent ().file (), "B_File.java.orig").delete ();
        refresh (B_File.parent ());

        out.println ("Merge File:");
        out.println (loadBinFile(mergefilepath));
        compareReferenceFiles();
    }

    public void testMergeByVersionLabels () {
        String mergefilepath = getWorkFilePath () + "/mergefile";
        new File (mergefilepath).delete ();

        B_File.pvcsNode().pVCSMerge();
        MergeCommandOperator merge = new MergeCommandOperator ("");
        merge.revisionNumber();
        merge.setRevisionNumber("1.1");
        merge.revisionNumber2();
        merge.setRevisionNumber2("1.2");
        merge.revisionNumber3();
        merge.setRevisionNumber3("1.3");
        merge.setOutputFile(mergefilepath);
        merge.ok ();
        merge.waitClosed ();
        B_File.waitHistory("Merge");
        B_File.parent ().waitHistory("Refresh");

        B_File.waitStatus ("Missing; 1.4");
        new File (B_File.parent ().file (), "B_File.java.orig").delete ();
        refresh (B_File.parent ());

        out.println ("Merge File:");
        out.println (loadBinFile(mergefilepath));
        compareReferenceFiles();
    }
    
    public Date findDate (String text, String rev) {
        int state = 0;
        StringTokenizer st = new StringTokenizer (text, "\n");
        while (st.hasMoreTokens()) {
            String ss = st.nextToken();
            switch (state) {
                case 0:
                    if (ss.startsWith (rev))
                        state = 1;
                    break;
                case 1:
                    if (ss.startsWith ("Checked in:")) {
                        ss = ss.substring("Checked in:".length());
                        SimpleDateFormat sdf = new SimpleDateFormat ("dd MMM yyyy HH:mm:ss");
                        return sdf.parse (ss, new ParsePosition (0));
                    }
                    break;
            }
        }
        throw new AssertionFailedError ("Cannot find text in text: ToFind: " + rev + " In Text: " + text);
    }
    
    public void testViewSelectedInformation () {
        String output;
        
        closeAllVCSOutputs();
        B_File.pvcsNode ().pVCSHistory();
        HistoryCommandOperator hi = new HistoryCommandOperator ("");
        hi.ok();
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("History");
        waitNoEmpty(coo.txtStandardOutput ());
        output = coo.txtStandardOutput().getText ();
        info.println ("Output:");
        info.println (output);

        String username = getLockText (B_File.pvcsNode ().getText ());
        Date date = findDate (output, "Rev 1.2");
        SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yy HH:mm:ss");
        StringFilter sf = new StringFilter ();
        sf.addReplaceAllFilter(username, "<username>");
        sf.addReplaceFilter("", B_File.filename(0), "<filepath>");
        sf.addReplaceFilter ("Checked in:", "", "<check_in_date>");
        sf.addReplaceFilter ("Last modified:", "", "<last_modified>");
        
        coo.close ();
        coo.waitClosed();
        
        B_File.pvcsNode().pVCSHistory();
        hi = new HistoryCommandOperator ("");
        hi.selectReportType(HistoryCommandOperator.ITEM_REVISIONINFORMATIONONLY);
        hi.setRevision("1.2");
        hi.setAuthorS(username);
        hi.setOwnerS(username);
//        hi.setDateFrom(sdf.format (new Date (date.getTime () - 1))); // fails due to issue #29025
//        hi.setDateTo(sdf.format (new Date (date.getTime () + 1))); // fails due to issue #29025
        hi.ok ();
        hi.waitClosed();

        coo = new VCSCommandsOutputOperator ("History");
        waitNoEmpty(coo.txtStandardOutput ());
        output = coo.txtStandardOutput().getText ();
        
        info.println (output);
        printFiltered (output, sf);
        compareReferenceFiles();
        
        coo.close ();
        coo.waitClosed();
    }
    
    public void testRemoveParticularRevision () {
        B_File.pvcsNode ().pVCSRemoveRevision();
        assertQuestionYesDialog (null);
        RemoveCommandOperator rco = new RemoveCommandOperator ("");
        rco.specificRevisionS();
        rco.setBySpecificRevisions("1.2");
        rco.ok ();
        rco.waitClosed();
        B_File.waitHistory("Remove Revision");
        assertInformationDialog (null);

        B_File.pvcsNode ().pVCSHistory ();
        HistoryCommandOperator hi = new HistoryCommandOperator ("");
        hi.selectReportType(HistoryCommandOperator.ITEM_REVISIONINFORMATIONONLY);
        hi.setRevision("1.2");
        hi.ok ();
        hi.waitClosed();

        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("History");
        waitNoEmpty(coo.txtStandardError ());
        String output = coo.txtStandardError().getText ();
        
        info.println (output);
        StringTokenizer st = new StringTokenizer (output, "\n");
        boolean found = false;
        while (st.hasMoreTokens()) {
            found = st.nextToken().indexOf ("cannot find revision 1.2") >= 0;
            if (found)
                break;
        }
        assertTrue ("Cannot find message about revision 1.2", found);
            
        coo.close ();
        coo.waitClosed();
    }
    
    public void testRemoveParticularRevision2 () {
        B_File.pvcsNode ().pVCSRemoveRevision();
        assertQuestionYesDialog (null);
        RemoveCommandOperator rco = new RemoveCommandOperator ("");
        rco.revisionsIdentifiedByVersionLabelS();
//        rco.setByVersionLabels("Revision B1"); // cause error - issue #28995
        rco.setByVersionLabels("RevisionB1"); // workaround
        rco.ok ();
        rco.waitClosed();
        B_File.waitHistory("Remove Revision");
        assertInformationDialog (null);

        B_File.pvcsNode ().pVCSHistory ();
        HistoryCommandOperator hi = new HistoryCommandOperator ("");
        hi.selectReportType(HistoryCommandOperator.ITEM_REVISIONINFORMATIONONLY);
        hi.setRevision("1.1");
        hi.ok ();
        hi.waitClosed();

        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("History");
        waitNoEmpty(coo.txtStandardError ());
        String output = coo.txtStandardError().getText ();
        
        info.println (output);
        StringTokenizer st = new StringTokenizer (output, "\n");
        boolean found = false;
        while (st.hasMoreTokens()) {
            found = st.nextToken().indexOf ("cannot find revision 1.1") >= 0;
            if (found)
                break;
        }
        assertTrue ("Cannot find message about revision 1.1", found);

        coo.close ();
        coo.waitClosed();
    }
    
    public void testSetPassword () {
        closeAllProperties();
        new PropertiesAction ().perform (new Node (exp.repositoryTab().tree (), root.node ()));
        PropertySheetOperator pso = new PropertySheetOperator (root.node ());
        PropertySheetTabOperator psto = new PropertySheetTabOperator (pso, "Expert");
        Property prop = new Property (psto, "Commands");
        prop.openEditor();
        
        NbDialogOperator dia = new NbDialogOperator (NbDialogOperator.waitJDialog ("Commands", true, true));
        JTreeOperator tree = new JTreeOperator (dia);
        new Node (tree, "").performPopupActionNoBlock("Add|Command");
        
        NbDialogOperator dia2 = new NbDialogOperator ("New Command");
        new JTextFieldOperator (dia2).clearText ();
        new JTextFieldOperator (dia2).typeText ("EchoCommand");
        dia2.ok ();
        dia2.waitClosed ();
        
        new Node (tree, "EchoCommand").select ();
        
        pso = new PropertySheetOperator (dia);
        new StringProperty (dia, "Exec").setStringValue(Utilities.isWindows() ? "cmd /x /c \"echo ${PASSWORD}\"" : "echo ${PASSWORD}");
        new ComboBoxProperty (dia, "Display Output").setValue("True");
        dia.ok();
        dia.waitClosed ();

        root.pvcsNode ().pVCSSetPassword();
        NbDialogOperator di = new NbDialogOperator ("Password");
        new JTextFieldOperator (di).clearText ();
        new JTextFieldOperator (di).typeText ("abcd");
        di.ok ();
        di.waitClosed ();
        
        root.pvcsNode().performPopupAction("EchoCommand");
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("EchoCommand");
        waitNoEmpty(coo.txtStandardOutput());
        info.println (coo.txtStandardOutput ().getText ());
        assertTrue ("Cannot find \"abcd\" text in stdout", coo.txtStandardOutput().getText ().indexOf ("abcd") >= 0);
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
        new Node (exp.repositoryTab().tree (), "").waitChildNotPresent(root.node ());
    }

}
