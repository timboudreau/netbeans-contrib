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
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.StringTokenizer;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.modules.vcscore.AnnotateCommandOperator;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSAddFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSAnnotateAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSCommitFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSExportFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSExportFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSHistoryAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSImportFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSLogFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSLogFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSStatusFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSStatusFolderAdvDialog;
import util.Filter;
import util.Helper;
import util.History;

public class JellyCommands extends JellyStub {
    
    public JellyCommands(String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        JellyStub.DEBUG = true;
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyCommands("testWorkDir"));
        suite.addTest(new JellyCommands("testRelease"));
        suite.addTest(new JellyCommands("testStatusLogPrepare"));
        suite.addTest(new JellyCommands("testStatus"));
        suite.addTest(new JellyCommands("testDefaultLog"));
        suite.addTest(new JellyCommands("testCheckOutHistory"));
        suite.addTest(new JellyCommands("testDefaultAnnotate"));
        suite.addTest(new JellyCommands("testDefaultImport"));
        suite.addTest(new JellyCommands("testDefaultExport"));
        suite.addTest(new JellyCommands("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    static String serverDirectory;
    static String clientDirectory;
    static History history;
    static String username;
    static MyNode root, initdir;
    static MyNode statusDir1, statusDir2, statusFile1, statusFile2;
    static MyNode imdir, imfile;
    
    protected void prepareServer(String dir) {
        new File(dir + "/initdir").mkdirs();
    }
    
    public void testWorkDir() {
        Configuration conf = super.configureWorkDir ();
        
        serverDirectory = conf.serverDirectory;
        clientDirectory = conf.clientDirectory;
        history = conf.history;
        getLog ().println ("nRoot: " + conf.nRoot);
        root = new MyNode (null, conf.nRoot.substring (4));
        initdir = new MyNode (root, "initdir");
        
        statusDir1 = new MyNode (root, "StatusDir1");
        statusDir2 = new MyNode (statusDir1, "StatusDir2");
        statusFile1 = new MyNode (statusDir1, "File1");
        statusFile2 = new MyNode (statusDir2, "File2");

        imdir = new MyNode (root, "imdir");
        imfile = new MyNode (imdir, "imfile");
    }
    
    public void testRelease () {
        String str, s;
        MyNode releaseNode = new MyNode (root, "releasedirectory");
        releaseNode.mkdirs ();
        MyNode releaseFileNode1 = new MyNode (releaseNode, "releasefile1");
        releaseFileNode1.saveToFile("Init1");
        MyNode releaseFileNode2 = new MyNode (releaseNode, "releasefile2");
        releaseFileNode2.saveToFile("Init2");

        refresh (history, root);
//        waitStatus ("Local", releaseNode.node ());
        addDirectory (history, releaseNode);
        
        refresh (history, releaseNode);
        waitStatus ("Local", releaseFileNode1.node ());
        addFile (history, releaseFileNode1, null);
        commitFile (history, releaseFileNode1, null, null);
        waitStatus ("Up-to-date; 1.1", releaseFileNode1.node ());
        
        releaseFileNode1.saveToFile("Mod1");
        refresh (history, releaseNode);
        waitStatus ("Locally Modified; 1.1", releaseFileNode1.node ());
        waitStatus ("Local", releaseFileNode2.node ());

        releaseNode.cvsNode (exp).cVSRelease();
        new NbDialogOperator ("Question").no ();
        waitStatus (null, releaseNode.node ());

        releaseNode.cvsNode (exp).cVSRelease();
        NbDialogOperator dia = new NbDialogOperator ("Question");
        str = new JLabelOperator (dia, "M ").getText ();
        s = "M " + releaseFileNode1.name ();
        assertTrue ("ReleaseFileNode1 is not listed: Label: " + str, str.indexOf (s) >= 0);
        str = new JLabelOperator (dia, "? ").getText ();
        s = "? " + releaseFileNode2.name ();
        assertTrue ("ReleaseFileNode2 is not listed: Label: " + str, str.indexOf (s) >= 0);
        str = new JLabelOperator (dia, "altered files").getText ();
        assertTrue ("Invalid altered files count: Label: " + str, str.indexOf ("[1]") >= 0);
        str = new JLabelOperator (dia, releaseNode.name ()).getText ();
        dia.yes ();
        dia.waitClosed();
//        Helper.waitNoNode (exp.repositoryTab ().tree (), root.node (), releaseNode.name()); # fails due to bug #28223
        for (int a = 0; a < 60; a ++) {
            Helper.sleep (1000);
            if (!new File (releaseFileNode1.file ()).exists()  &&  !new File (releaseFileNode2.file ()).exists())
                return;
        }
        assertTrue ("Timeout: ReleaseFileNodes still exists", false);
    }
    
    public void testStatusLogPrepare () {
        statusDir1.mkdirs ();
        statusDir2.mkdirs ();
        statusFile1.saveToFile("Init1");
        statusFile2.saveToFile("Init2");
        refresh(history, root);
        addDirectory (history, statusDir1);
        addDirectory (history, statusDir2);
        addFile (history, statusFile1, "Init1");
        addFile (history, statusFile2, "Init2");
        commitFile (history, statusFile1, null, "HEAD1");
        waitStatus ("Up-to-date; 1.1", statusFile1.node ());
        commitFile (history, statusFile2, null, "HEAD2");
        waitStatus ("Up-to-date; 1.1", statusFile2.node ());
        addTagFile (history, statusFile1, "BranchTag1", true);
        addTagFile (history, statusFile2, "BranchTag2", true);
        statusFile1.saveToFile ("Mod1");
        statusFile2.saveToFile("Mod2");
        commitFile (history, statusFile1, "BranchTag1", "BranchCommit1");
        commitFile (history, statusFile2, "BranchTag2", "BranchCommit2");
    }
    
    public void testStatus () {
        VCSCommandsOutputOperator coo;
        CVSStatusFileAdvDialog stat1;
        CVSStatusFolderAdvDialog stat2;
        String str;
        Filter filt = new Filter ();
        filt.addFilterAfter("1.1.2.1");

        info.println ("File1 - Brief");
        out.println ("File1 - Brief");
        closeAllVCSOutputs();
        statusFile1.cvsNode (exp).cVSStatus();
        stat1 = new CVSStatusFileAdvDialog ();
        stat1.oK ();
        stat1.waitClosed ();
        assertTrue("Status file command failed", history.waitCommand("Status", statusFile1.history ()));
        coo = new VCSCommandsOutputOperator ("Status");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);

        info.println ("File1 - Verbose");
        out.println ("File1 - Verbose");
        closeAllVCSOutputs();
        statusFile1.cvsNode (exp).cVSStatus();
        stat1 = new CVSStatusFileAdvDialog ();
        stat1.checkVerboseFormatTagInfo(true);
        stat1.oK ();
        stat1.waitClosed ();
        assertTrue("Status file command failed", history.waitCommand("Status", statusFile1.history ()));
        coo = new VCSCommandsOutputOperator ("Status");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);

        info.println ("Dir1 - Recursive - Brief");
        out.println ("Dir1 - Recursive - Brief");
        closeAllVCSOutputs();
        statusDir1.cvsNode (exp).cVSStatus();
        stat2 = new CVSStatusFolderAdvDialog ();
        stat2.checkProcessDirectoriesRecursively(true);
        stat2.checkVerboseFormatTagInfo(false);
        stat2.oK ();
        stat2.waitClosed ();
        assertTrue("Status file command failed", history.waitCommand("Status", statusDir1.history ()));
        coo = new VCSCommandsOutputOperator ("Status");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);

        info.println ("Dir1 - Recursive - Verbose");
        out.println ("Dir1 - Recursive - Verbose");
        closeAllVCSOutputs();
        statusDir1.cvsNode (exp).cVSStatus();
        stat2 = new CVSStatusFolderAdvDialog ();
        stat2.checkProcessDirectoriesRecursively(true);
        stat2.checkVerboseFormatTagInfo(true);
        stat2.oK ();
        stat2.waitClosed ();
        assertTrue("Status file command failed", history.waitCommand("Status", statusDir1.history ()));
        coo = new VCSCommandsOutputOperator ("Status");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);

        info.println ("Dir1 - Brief");
        out.println ("Dir1 - Brief");
        closeAllVCSOutputs();
        statusDir1.cvsNode (exp).cVSStatus();
        stat2 = new CVSStatusFolderAdvDialog ();
        stat2.checkProcessDirectoriesRecursively(false);
        stat2.checkVerboseFormatTagInfo(false);
        stat2.oK ();
        stat2.waitClosed ();
        assertTrue("Status file command failed", history.waitCommand("Status", statusDir1.history ()));
        coo = new VCSCommandsOutputOperator ("Status");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);

        info.println ("Dir1 - Verbose");
        out.println ("Dir1 - Verbose");
        closeAllVCSOutputs();
        statusDir1.cvsNode (exp).cVSStatus();
        stat2 = new CVSStatusFolderAdvDialog ();
        stat2.checkProcessDirectoriesRecursively(false);
        stat2.checkVerboseFormatTagInfo(true);
        stat2.oK ();
        stat2.waitClosed ();
        assertTrue("Status file command failed", history.waitCommand("Status", statusDir1.history ()));
        coo = new VCSCommandsOutputOperator ("Status");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);

        compareReferenceFiles();
    }
    
    public void testDefaultLog () {
        VCSCommandsOutputOperator coo;
        String str;
        Filter filt = new Filter ();
        filt.addFilterAfter("RCS file: ");
        filt.addFilterBetween("date: ", ";");
        filt.addFilterBetween("author: ", ";");
        closeAllVCSOutputs();

        statusDir1.cvsNode (exp).cVSLog();
        CVSLogFolderAdvDialog log = new CVSLogFolderAdvDialog ();
        log.oK ();
        log.waitClosed ();
        assertTrue("Log directory command failed", history.waitCommand("Log", statusDir1.history ()));
        coo = new VCSCommandsOutputOperator ("Log");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);
        
        compareReferenceFiles ();
    }
    
    public void testCheckOutHistory () {
        VCSCommandsOutputOperator coo;
        String str;
        closeAllVCSOutputs();

        root.cvsNode (exp).cVSHistory ();
        CVSHistoryAdvDialog hi = new CVSHistoryAdvDialog ();
        hi.ok ();
        hi.waitClosed();
        assertTrue("History command failed", history.waitCommand("History", root.history ()));
        coo = new VCSCommandsOutputOperator ("History");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput ().getText ();
        info.println (str);
        StringTokenizer st = new StringTokenizer (str, "\n");
        int c = st.countTokens();
        assertTrue ("Invalid lines count: Count: " + c + " Text: " + str, c == 1);
        str = st.nextToken();
        assertTrue ("History does not contain =.=", str.indexOf ("=.=") >= 0);
        assertTrue ("History does not contain O at the beginning", str.startsWith ("O"));
        assertTrue ("History does not contain * at the end", str.endsWith ("*"));
    }
    
    public void dumpTable (JTableOperator table) {
        for (int a = 0; a < table.getRowCount(); a ++) {
            Object o0 = table.getValueAt(a, 0);
            Object o1 = table.getValueAt(a, 1);
            Object o4 = table.getValueAt(a, 4);
            out.println ("Line: " + a + " ---- " + o0 + "\t" + o1 + "\t" + o4);
        }
    }

    static Color annoWhite = new Color (254, 254, 254);
    static Color annoGreen = new Color (180, 255, 180);
    static Color annoBlue = new Color (160, 200, 255);
    static Color annoRed = new Color (255, 160, 180);
    
    public void dumpTableColors (JTableOperator table) {
        for (int a = 0; a < table.getRowCount(); a ++) {
            Object o0 = table.getValueAt(a, 0);
            TableCellRenderer tcr = table.getCellRenderer(a, 0);
            Component c = tcr.getTableCellRendererComponent((JTable) table.getSource (), o0, false, false, a, 0);
            Color col = c.getBackground ();
            String str = col.toString ();
            if (annoWhite.equals (col))
                str = "White";
            else if (annoGreen.equals (col))
                str = "Green";
            else if (annoBlue.equals (col))
                str = "Blue";
            else if (annoRed.equals (col))
                str = "Red";
            out.println ("Line: " + a + " ---- " + str);
        }
    }
    
    protected void testAnnoVersion (AnnotateCommandOperator aco, String equ) {
        out.println ("==== RevisionEquals: " + equ + " ====");
        aco.selectFilterRevision(equ);
        out.println ("--- Revision: " + AnnotateCommandOperator.ITEM_NOREVISIONSELECTED);
        aco.selectSelectRevision(AnnotateCommandOperator.ITEM_NOREVISIONSELECTED);
        dumpTableColors(aco.tabTableOfFileAnnotations());
        out.println ("--- Revision: 1.1");
        aco.selectSelectRevision("1.1");
        dumpTableColors(aco.tabTableOfFileAnnotations());
        out.println ("--- Revision: 1.2");
        aco.selectSelectRevision("1.2");
        dumpTableColors(aco.tabTableOfFileAnnotations());
        out.println ("--- Revision: 1.3");
        aco.selectSelectRevision("1.3");
        dumpTableColors(aco.tabTableOfFileAnnotations());
    }
    
    public void testDefaultAnnotate () {
        closeAllVCSOutputs();
        MyNode annofile = new MyNode (initdir, "annofile");
        if (!JellyStub.DEBUG) {
            annofile.saveToFile ("Commit-1.1 - Line1\nCommit-1.1 - Line2\nCommit-1.1 - Line4\nCommit-1.1 - Line5\n");
            refresh(history, initdir);
            annofile.cvsNode (exp);
            addFile (history, annofile, "InitialState");
            commitFile (history, annofile, null, "Commit_1.1");
            waitStatus ("Up-to-date; 1.1", annofile.node ());
            annofile.saveToFile ("Commit-1.1 - Line1\nCommit-1.1 - Line2 - Modified-1.2\nCommit-1.2 - Line3 - Added-1.2\nCommit-1.1 - Line4\nCommit-1.1 - Line5 - Modified-1.2\n");
            commitFile (history, annofile, null, "Commit_1.2");
            waitStatus ("Up-to-date; 1.2", annofile.node ());
            annofile.saveToFile ("Commit-1.3 - Line0 - Added-1.3\nCommit-1.1 - Line1\nCommit-1.1 - Line2 - Modified-1.2 - Modified-1.3\nCommit-1.2 - Line3 - Added-1.2\nCommit-1.1 - Line4\nCommit-1.1 - Line5 - Modified-1.2\n");
            commitFile (history, annofile, null, "Commit_1.3");
            waitStatus ("Up-to-date; 1.3", annofile.node ());
        }
        annofile.cvsNode (exp).cVSAnnotate();
        CVSAnnotateAdvDialog anno = new CVSAnnotateAdvDialog ();
        anno.oK ();
        anno.waitClosed ();
        assertTrue("Annotate command failed", history.waitCommand("Annotate", annofile.history ()));
        AnnotateCommandOperator aco = new AnnotateCommandOperator (annofile.name ());
        
        dumpTable (aco.tabTableOfFileAnnotations ());
        dumpTableColors (aco.tabTableOfFileAnnotations ());
        
        out.println ("==== Author: " + AnnotateCommandOperator.ITEM_NOAUTHORSELECTED + " ====");
        aco.selectFilterByAuthor(AnnotateCommandOperator.ITEM_NOAUTHORSELECTED);
        testAnnoVersion (aco, AnnotateCommandOperator.ITEM_EQUALS);
        testAnnoVersion (aco, AnnotateCommandOperator.ITEM_NEWERTHAN);
        testAnnoVersion (aco, AnnotateCommandOperator.ITEM_OLDERTHAN);
        out.println ("==== Author: <AUTHOR> ====");
        info.println ("User author: " + aco.cboFilterByAuthor().getItemAt(1));
        aco.selectFilterByAuthor((String) aco.cboFilterByAuthor().getItemAt(1));
        testAnnoVersion (aco, AnnotateCommandOperator.ITEM_EQUALS);
        testAnnoVersion (aco, AnnotateCommandOperator.ITEM_NEWERTHAN);
        testAnnoVersion (aco, AnnotateCommandOperator.ITEM_OLDERTHAN);

        aco.close ();
//        aco.waitClosed ();
        compareReferenceFiles();
    }
    
    public void testDefaultImport () {
        NbDialogOperator dia;
        String str, s;
        imdir.mkdirs ();
        imfile.saveToFile("Init");
        refresh(history, root);
        imdir.cvsNode (exp).cVSImport();
        CVSImportFolderAdvDialog imp = new CVSImportFolderAdvDialog ();
        imp.setLoggingMessage("LoggingMessage");
/*        imp.oK ();
        
        dia = new NbDialogOperator ("Warning");
        str = new JLabelOperator (dia).getText ();
        s = "The value of Vendor Tag: should not be empty.";
        assertEquals("Invalid warning dialog message: Expect: " + s + ", Got: " + str, s, str);
        dia.ok();*/
        imp.setVendorTag("VendorTag");
/*        imp.oK ();
        
        dia = new NbDialogOperator ("Warning");
        str = new JLabelOperator (dia).getText ();
        s = "The value of Vendor Tag: should not be empty.";
        assertEquals("Invalid warning dialog message: Expect: " + s + ", Got: " + str, s, str);
        dia.ok();*/
        imp.setReleaseTag("ReleaseTag");
        imp.oK ();

        imp.waitClosed ();
        new NbDialogOperator ("Information").ok ();
        assertTrue("Import command failed", history.waitCommand("Import", imdir.history ()));
        refresh (history, root);
        waitStatus (null, imdir.node ());
        refresh (history, imdir);
        waitStatus ("Up-to-date; 1.1.1.1", imfile.node ());
        
        VCSCommandsOutputOperator coo;
        Filter filt = new Filter ();
        filt.addFilterAfter("RCS file: ");
        filt.addFilterBetween("date: ", ";");
        filt.addFilterBetween("author: ", ";");
        closeAllVCSOutputs();

        imfile.cvsNode (exp).cVSLog();
        CVSLogFileAdvDialog log = new CVSLogFileAdvDialog ();
        log.oK ();
        log.waitClosed ();
        assertTrue("Log file command failed", history.waitCommand("Log", imfile.history ()));
        coo = new VCSCommandsOutputOperator ("Log");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);
        
        compareReferenceFiles ();
    }
    
    public void testDefaultExport () {
        MyNode exported1 = new MyNode (root, "exported1");
        MyNode exdir1 = new MyNode (exported1, "imdir");
        MyNode exfile1 = new MyNode (exdir1, "imfile");
        MyNode exported2 = new MyNode (root, "exported2");
        MyNode exdir2 = new MyNode (exported2, "imdir");
        MyNode exfile2 = new MyNode (exdir2, "imfile");

        imdir.cvsNode (exp).cVSExport ();
        CVSExportFolderAdvDialog exp1 = new CVSExportFolderAdvDialog ();
        exp1.setFolderToExportTo(exported1.file ());
        exp1.setRevisionOrTag("HEAD");
        exported1.mkdirs();
        exp1.oK ();
        exp1.waitClosed ();
        assertTrue("Export folder command failed", history.waitCommand("Export", imdir.history ()));
        refresh (history, root);
        exported1.cvsNode (exp);
        exdir1.cvsNode (exp);
        exfile1.cvsNode (exp);
        
        imfile.cvsNode (exp).cVSExport ();
        CVSExportFileAdvDialog exp2 = new CVSExportFileAdvDialog ();
        exp2.setFolderToExportTo(exported2.file ());
        exp2.setRevisionOrTag("HEAD");
        exported2.mkdirs();
        exp2.oK ();
        exp2.waitClosed ();
        assertTrue("Export file command failed", history.waitCommand("Export", imfile.history ()));
        refresh (history, root);
        exported2.cvsNode (exp);
        exdir2.cvsNode (exp);
        exfile2.cvsNode (exp);
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
    }

}
