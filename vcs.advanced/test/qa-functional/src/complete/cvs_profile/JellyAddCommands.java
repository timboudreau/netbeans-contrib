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
import complete.cvs_profile.JellyStub.MyNode;
import java.awt.Color;
import java.util.StringTokenizer;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.EditorOperator;
import org.netbeans.jellytools.EditorWindowOperator;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JEditorPaneOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSEditFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSEditorsFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSGraphicalDiffFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSLockFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSPatchFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSTextualDiffFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSUndoEditFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSWatchSetFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSWatchSetFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSWatchersFolderAdvDialog;
import util.Filter;
import util.History;

public class JellyAddCommands extends JellyStub {
    
    public JellyAddCommands(String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        JellyStub.DEBUG = true;
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyAddCommands("testWorkDir"));
        suite.addTest(new JellyAddCommands("testEdit"));
        suite.addTest(new JellyAddCommands("testEditors"));
        suite.addTest(new JellyAddCommands("testUndoEdit"));
        suite.addTest(new JellyAddCommands("testNoEdit"));
        suite.addTest(new JellyAddCommands("testSetWatchers"));
//        suite.addTest(new JellyAddCommands("testLock")); // not testable on local cvs
//        suite.addTest(new JellyAddCommands("testUnlock")); // not testable on local cvs
//        suite.addTest(new JellyAddCommands("testViewBranches")); // not testable component
        suite.addTest(new JellyAddCommands("testDiffPrepare"));
        suite.addTest(new JellyAddCommands("testDefaultDiffGraphical")); // not fully covered
        suite.addTest(new JellyAddCommands("testDefaultDiffGraphicalTextual"));
        suite.addTest(new JellyAddCommands("testDefaultDiffTextual"));
        suite.addTest(new JellyAddCommands("testDefaultPatch"));
        suite.addTest(new JellyAddCommands("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    static String serverDirectory;
    static String clientDirectory;
    static History history;
    static String username;
    static MyNode root;
    static MyNode editdir, editfile;
    static MyNode lockdir, lockfile;
    static MyNode diffdir, difffile;
   
    protected void prepareServer(String dir) {
    }
    
    public void testWorkDir() {
        Configuration conf = super.configureWorkDir ();
        
        serverDirectory = conf.serverDirectory;
        clientDirectory = conf.clientDirectory;
        history = conf.history;
        getLog ().println ("nRoot: " + conf.nRoot);
        root = new MyNode (null, conf.nRoot.substring (4));
        
        editdir = new MyNode (root, "editdir");
        editfile = new MyNode (editdir, "editfile");

        lockdir = new MyNode (root, "lockdir");
        lockfile = new MyNode (lockdir, "lockfile");
        
        diffdir = new MyNode (root, "diffdir");
        difffile = new MyNode (diffdir, "difffile");
    }
    
    public void testEdit () {
        editdir.mkdirs ();
        editfile.saveToFile("Init");
        
        refresh (history, root);
        addDirectory (history, editdir);
        addFile (history, editfile, "InitialState");
        commitFile (history, editfile, null, "InitialCommit");
        waitStatus ("Up-to-date; 1.1", editfile.node ());
        editdir.cvsNode (exp).cVSEditingEdit();
        CVSEditFolderAdvDialog edit = new CVSEditFolderAdvDialog ();
        edit.checkProcessDirectoriesRecursively(true);
        edit.checkSpecifyActionsForTemporaryWatch(true);
        edit.setTemporaryWatch(CVSEditFolderAdvDialog.ITEM_ALL);
        edit.oK ();
        edit.waitClosed ();
        assertTrue("Edit directory command failed", history.waitCommand("Edit", editdir.history ()));
        assertInformationDialog ("The file \"" + editdir.name () + "\" is prepared to edit.");
    }
    
    public void testEditors () {
        closeAllVCSOutputs();
        editdir.cvsNode (exp).cVSEditingEditors();
        CVSEditorsFolderAdvDialog ed = new CVSEditorsFolderAdvDialog ();
        ed.checkProcessDirectoriesRecursively(true);
        ed.oK();
        ed.waitClosed ();
        assertTrue("Editors command failed", history.waitCommand("Editors", editdir.history ()));
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("Editors");
        waitNoEmpty (coo.txtStandardOutput ());
        String str = coo.txtStandardOutput().getText ();
        info.println (str);
        assertTrue ("Editor is not set on editfile: Output: " + str, str.startsWith (editfile.history ()));
    }
    
    public void testUndoEdit () {
        editfile.saveToFile ("Modification");
        refresh (history, editdir);
        waitStatus ("Locally Modified; 1.1", editfile.node ());
        editdir.cvsNode(exp).cVSEditingUndoEdit();
        CVSUndoEditFolderAdvDialog undo = new CVSUndoEditFolderAdvDialog ();
        undo.checkProcessDirectoriesRecursively(true);
        undo.oK ();
        undo.waitClosed ();
        assertQuestionYesDialog ("Are you sure you want to revert the changes in these files: \"" + editfile.history () + "\"?");
        assertTrue("Undo edit directory command failed", history.waitCommand("Undo Edit", editdir.history ()));
        assertInformationDialog("Changes reverted in the file \"" + editdir.history () + "\".");
        waitStatus ("Up-to-date; 1.1", editfile.node ());
    }
    
    public void testNoEdit () {
        editfile.cvsNode(exp).cVSEditingUndoEdit();
        assertInformationDialog ("No files has been changed or the edit command was not issued before.");
    }
    
    public void testSetWatchers () {
        MyNode watchdir = new MyNode (root, "watchdir");
        MyNode watchfile = new MyNode (watchdir, "watchfile");
        if (!JellyStub.DEBUG) {
            watchdir.mkdirs ();
            watchfile.saveToFile("Init");
        
            refresh (history, root);
            addDirectory (history, watchdir);
            addFile (history, watchfile, "InitialState");
            commitFile (history, watchfile, null, "InitialCommit");
            waitStatus ("Up-to-date; 1.1", watchfile.node ());
        }
        watchfile.cvsNode (exp).cVSWatchesSetWatch();
        CVSWatchSetFileAdvDialog wa = new CVSWatchSetFileAdvDialog ();
        wa.checkCommit(true);
        wa.checkEdit(true);
        wa.checkUnedit(true);
        wa.oK ();
        wa.waitClosed ();
        assertTrue("Set watch file command failed", history.waitCommand("Set Watch", watchfile.history ()));
        
        closeAllVCSOutputs ();
        watchfile.cvsNode (exp).cVSWatchesWatchers();
        assertTrue("Watchers file command failed", history.waitCommand("Watchers", watchfile.history ()));
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("Watchers");
        waitNoEmpty (coo.txtStandardOutput ());
        String str = coo.txtStandardOutput().getText ();
        info.println (str);
        StringTokenizer st = new StringTokenizer (str, "\n");
        int c = st.countTokens();
        assertTrue ("Invalid watchers lines: Count: " + c, c == 1);
        str = st.nextToken();
        info.println (str);
        assertTrue ("Watcher on watchfile not exists", str.startsWith (watchfile.name ()));
        c = str.indexOf ("edit");
        if (c < 1  ||  str.charAt (c - 1) == 'n')
            c = -1;
        assertTrue ("Edit watcher does not exist", c >= 0);
        assertTrue ("Unedit watcher does not exist", str.indexOf ("unedit") >= 0);
        assertTrue ("Commit watcher does not exist", str.indexOf ("commit") >= 0);

        watchdir.cvsNode (exp).cVSWatchesSetWatch();
        CVSWatchSetFolderAdvDialog war = new CVSWatchSetFolderAdvDialog ();
        war.setWatchRecursively();
        war.checkCommit(false);
        war.checkEdit(false);
        war.checkUnedit(false);
        war.oK ();
        war.waitClosed ();
        assertTrue("Set watch file command failed", history.waitCommand("Set Watch", watchdir.history ()));
        
        closeAllVCSOutputs ();
        watchdir.cvsNode (exp).cVSWatchesWatchers();
        CVSWatchersFolderAdvDialog wars = new CVSWatchersFolderAdvDialog ();
        wars.checkProcessDirectoriesRecursively(true);
        wars.oK ();
        wars.waitClosed();
        assertTrue("Watchers file command failed", history.waitCommand("Watchers", watchdir.history ()));
        coo = new VCSCommandsOutputOperator ("Watchers");
        assertTrue ("Some watchers exist", !coo.tabbedPane().isEnabledAt(0));
    }
/*    cannot create test due to using of local cvs filesystem
    public void testLock () {
        if (!JellyStub.DEBUG) {
            lockdir.mkdirs ();
            lockfile.saveToFile("Init");
        
            refresh (history, root);
            addDirectory (history, lockdir);
            addFile (history, lockfile, "InitialState");
            commitFile (history, lockfile, null, "InitialCommit");
            waitStatus ("Up-to-date; 1.1", lockfile.node ());
        }
        lockfile.cvsNode (exp).cVSLockingLock();
        CVSLockFileAdvDialog lock = new CVSLockFileAdvDialog ();
        lock.oK ();
        lock.waitClosed();
        assertTrue("Lock file command failed", history.waitCommand("Lock", lockfile.history ()));
        assertInformationDialog("The file \"" + lockfile.name () + "\" was locked successfully.");
        lockfile.saveToFile ("Mod");
        refresh (history, lockdir);
        waitStatus ("Locally Modified; 1.1", lockfile.node ());
        commitFile (history, lockfile, null, "Trying to commit locked file");
        // !!! do it - check for no commit
    }
    
    public void testUnlock () {
        // !!! do it
    }
    
    public void testViewBranches () {
        // !!! do it
    }
*/    
    public void testDiffPrepare () {
        diffdir.mkdirs ();
        difffile.saveToFile ("Line 1\nLine 3\nLine 5\n");
        refresh (history, root);
        addDirectory(history, diffdir);
        addFile (history, difffile, "InitialState");
        commitFile (history, difffile, null, "Commit1");
        waitStatus ("Up-to-date; 1.1", difffile.node ());
        difffile.saveToFile ("Line 2\nLine 3\nLine 4\nLine 5\n");
        commitFile (history, difffile, null, "Commit2");
        waitStatus ("Up-to-date; 1.2", difffile.node ());
        difffile.saveToFile ("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\n");
        commitFile (history, difffile, null, "Commit3");
        waitStatus ("Up-to-date; 1.3", difffile.node ());
    }
    
    static Color annoWhite = new Color (254, 254, 254);
    static Color annoGreen = new Color (180, 255, 180);
    static Color annoBlue = new Color (160, 200, 255);
    static Color annoRed = new Color (255, 160, 180);
    
    public void dumpColors (StyledDocument sd) {
//        !!! do it - not working - problem with StyledDocument, add test of textual diff view too
        int a = 0;
        for (;;) {
            Style st = sd.getLogicalStyle(a);
            if (st == null)
                break;
            Color col = (Color) st.getAttribute(StyleConstants.ColorConstants.Background);
            String str;
            if (annoWhite.equals (col))
                str = "White";
            else if (annoGreen.equals (col))
                str = "Green";
            else if (annoBlue.equals (col))
                str = "Blue";
            else if (annoRed.equals (col))
                str = "Red";
            else
                str = col.toString ();
            out.println ("Line: " + a + " ---- " + str);
            a ++;
        }
    }
    
    public void dumpDiffGraphical (TopComponentOperator tco) {
        new JComboBoxOperator (tco).selectItem("Graphical Diff Viewer");
        JEditorPaneOperator p1 = new JEditorPaneOperator (tco, 0);
        JEditorPaneOperator p2 = new JEditorPaneOperator (tco, 1);
        out.println ("==== Text - Panel 1 ====");
        out.println (p1.getText ());
        out.println ("==== Text - Panel 2 ====");
        out.println (p2.getText ());
        StyledDocument sd1 = (StyledDocument) p1.getDocument();
        StyledDocument sd2 = (StyledDocument) p2.getDocument();
        out.println ("==== Colors - Panel 1 ====");
//        dumpColors(sd1);
        out.println ("==== Colors - Panel 2 ====");
//        dumpColors(sd2);
    }
    
    public void dumpDiffGraphicalTextual (TopComponentOperator tco) {
        new JComboBoxOperator (tco).selectItem("Textual Diff Viewer");
        JEditorPaneOperator p = new JEditorPaneOperator (tco);
        out.println (p.getText ());
    }
    
    public void testDefaultDiffGraphical () {
        difffile.cvsNode (exp).cVSDiffGraphical();
        CVSGraphicalDiffFileAdvDialog gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.oK();
        gr.waitClosed ();
        assertInformationDialog ("No differences were found in " + difffile.name () + ".");
        assertTrue("Diff Graphical file command failed", history.waitCommand("Diff Graphical", difffile.history ()));

        difffile.cvsNode (exp).cVSDiffGraphical();
        gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.setRevisionOrTag1("1.1");
        gr.setRevisionOrTag2("1.2");
        gr.oK ();
        gr.waitClosed ();
        assertTrue("Diff Graphical file command failed", history.waitCommand("Diff Graphical", difffile.history ()));
        
        EditorWindowOperator ewo = new EditorWindowOperator ();
        TopComponentOperator tco = new TopComponentOperator (ewo, "Diff: " + difffile.name ());
        try {
            out.println ("!!!! ==== Comparing revisions: 1.1 and 1.2 ==== !!!!");
            dumpDiffGraphical (tco);
        } finally {
            tco.close();
        }

        difffile.cvsNode (exp).cVSDiffGraphical();
        gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.setRevisionOrTag1("1.2");
        gr.setRevisionOrTag2("1.3");
        gr.oK ();
        gr.waitClosed ();
        assertTrue("Diff Graphical file command failed", history.waitCommand("Diff Graphical", difffile.history ()));
        
        tco = new TopComponentOperator (ewo, "Diff: " + difffile.name ());
        try {
            out.println ("!!!! ==== Comparing revisions: 1.2 and 1.3 ==== !!!!");
            dumpDiffGraphical (tco);
        } finally {
            tco.close();
        }

        compareReferenceFiles();
    }
    
    public void testDefaultDiffGraphicalTextual () {
        difffile.cvsNode (exp).cVSDiffGraphical();
        CVSGraphicalDiffFileAdvDialog gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.setRevisionOrTag1("1.1");
        gr.setRevisionOrTag2("1.2");
        gr.oK ();
        gr.waitClosed ();
        assertTrue("Diff Graphical file command failed", history.waitCommand("Diff Graphical", difffile.history ()));
        
        EditorWindowOperator ewo = new EditorWindowOperator ();
        TopComponentOperator tco = new TopComponentOperator (ewo, "Diff: " + difffile.name ());
        try {
            out.println ("!!!! ==== Comparing revisions: 1.1 and 1.2 ==== !!!!");
            dumpDiffGraphicalTextual (tco);
        } finally {
            tco.close();
        }

        difffile.cvsNode (exp).cVSDiffGraphical();
        gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.setRevisionOrTag1("1.2");
        gr.setRevisionOrTag2("1.3");
        gr.oK ();
        gr.waitClosed ();
        assertTrue("Diff Graphical file command failed", history.waitCommand("Diff Graphical", difffile.history ()));
        
        tco = new TopComponentOperator (ewo, "Diff: " + difffile.name ());
        try {
            out.println ("!!!! ==== Comparing revisions: 1.2 and 1.3 ==== !!!!");
            dumpDiffGraphicalTextual (tco);
        } finally {
            tco.close();
        }

        compareReferenceFiles();
    }
    
    public void testDefaultDiffTextual () {
        VCSCommandsOutputOperator coo;
        String str;
        Filter filt = new Filter ();
        filt.addFilterAfter("RCS file: ");

        difffile.cvsNode (exp).cVSDiffTextual();
        CVSTextualDiffFileAdvDialog diff = new CVSTextualDiffFileAdvDialog ();
        diff.oK();
        diff.waitClosed ();
        assertTrue("Diff Textual file command failed", history.waitCommand("Diff Textual", difffile.history ()));

        closeAllVCSOutputs ();
        difffile.cvsNode (exp).cVSDiffTextual();
        diff = new CVSTextualDiffFileAdvDialog ();
        diff.setRevisionOrTag1("1.1");
        diff.setRevisionOrTag2("1.2");
        diff.oK();
        diff.waitClosed ();
        assertTrue("Diff Textual file command failed", !history.waitCommand("Diff Textual", difffile.history ()));
        coo = new VCSCommandsOutputOperator ("Diff Textual");
        waitNoEmpty(coo.txtStandardOutput());
        str = coo.txtStandardOutput ().getText ();
        info.println (str);
        out.println ("==== Diffing: 1.1 and 1.2 ====");
        filt.filterStringLinesToStream(out, str);
        
        closeAllVCSOutputs ();
        difffile.cvsNode (exp).cVSDiffTextual();
        diff = new CVSTextualDiffFileAdvDialog ();
        diff.setRevisionOrTag1("1.2");
        diff.setRevisionOrTag2("1.3");
        diff.oK();
        diff.waitClosed ();
        assertTrue("Diff Textual file command failed", !history.waitCommand("Diff Textual", difffile.history ()));
        coo = new VCSCommandsOutputOperator ("Diff Textual");
        waitNoEmpty(coo.txtStandardOutput());
        str = coo.txtStandardOutput ().getText ();
        info.println (str);
        out.println ("==== Diffing: 1.2 and 1.3 ====");
        filt.filterStringLinesToStream(out, str);
        
        compareReferenceFiles ();
    }
    
    public void testDefaultPatch () {
        Filter filt = new Filter ();
        filt.addFilterAfter ("*** " + difffile.history () + ":1.2");
        filt.addFilterAfter ("--- " + difffile.history ());

        closeAllVCSOutputs ();
        difffile.cvsNode (exp).cVSPatch ();
        CVSPatchFileAdvDialog diff = new CVSPatchFileAdvDialog ();
        diff.oK();
        diff.waitClosed ();
        assertTrue("Patch file command failed", history.waitCommand("Patch", difffile.history ()));
        VCSCommandsOutputOperator coo = new VCSCommandsOutputOperator ("Patch");
        waitNoEmpty(coo.txtStandardOutput());
        String str = coo.txtStandardOutput ().getText ();
        info.println (str);
        out.println ("==== Patch between: 1.2 and 1.3 ====");
        filt.filterStringLinesToStream(out, str);
        
        compareReferenceFiles ();
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
    }

}
