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
import java.io.File;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSAddFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSCommitFileAdvDialog;
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
        suite.addTest(new JellyCommands("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    static String serverDirectory;
    static String clientDirectory;
    static History history;
    static MyNode root;
    static MyNode statusDir1, statusDir2, statusFile1, statusFile2;
    
    protected void prepareServer(String dir) {
//        new File(dir + "/initdir").mkdirs();
    }
    
    public void testWorkDir() {
        Configuration conf = super.configureWorkDir ();
        
        serverDirectory = conf.serverDirectory;
        clientDirectory = conf.clientDirectory;
        history = conf.history;
        getLog ().println ("nRoot: " + conf.nRoot);
        root = new MyNode (null, conf.nRoot.substring (4));

        statusDir1 = new MyNode (root, "StatusDir1");
        statusDir2 = new MyNode (statusDir1, "StatusDir2");
        statusFile1 = new MyNode (statusDir1, "File1");
        statusFile2 = new MyNode (statusDir2, "File2");
    }
    
    public void testRelease () {
        MyNode releaseNode = new MyNode (root, "releasedirectory");
        releaseNode.mkdirs ();
        MyNode releaseFileNode = new MyNode (releaseNode, "releasefile");
        releaseFileNode.saveToFile("Init");

        refresh (history, root);
        waitStatus ("Local", releaseNode.node ());
        addDirectory (history, releaseNode);
        
        refresh (history, releaseNode);
        waitStatus ("Local", releaseFileNode.node ());
        addFile (history, releaseFileNode, null);

        commitFile (history, releaseFileNode, null, null);
        waitStatus ("Up-to-date; 1.1", releaseFileNode.node ());

        releaseNode.cvsNode (exp).cVSRelease();
        new NbDialogOperator ("Question").no ();
        waitStatus (null, releaseNode.node ());

        releaseNode.cvsNode (exp).cVSRelease();
        NbDialogOperator dia = new NbDialogOperator ("Question");
        String str = new JLabelOperator (dia, "altered files").getText ();
        assertTrue ("Invalid altered files count: Label: " + str, str.indexOf ("[0]") >= 0);
        dia.yes ();
        dia.waitClosed();
        Helper.waitNoNode (exp.repositoryTab ().tree (), root.node (), releaseNode.name());
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

        statusDir1.cvsNode (exp).cVSLog();
        CVSLogFolderAdvDialog log = new CVSLogFolderAdvDialog ();
        log.oK ();
        log.waitClosed ();
        assertTrue("Log file command failed", history.waitCommand("Log", statusDir1.history ()));
        coo = new VCSCommandsOutputOperator ("Log");
        waitNoEmpty (coo.txtStandardOutput ());
        str = coo.txtStandardOutput().getText ();
        info.println (str);
        filt.filterStringLinesToStream(out, str);
        
        compareReferenceFiles ();
    }
    
    public void testCheckOutHistory () {
        root.cvsNode (exp).cVSHistory ();
        // !!! do it
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
    }

}
