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

import complete.GenericStub.GenericNode;
import java.io.File;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSAddAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSCommitAction;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.*;


public class JellyOverall extends CVSStub {
    
    public JellyOverall(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyOverall("configure"));
        suite.addTest(new JellyOverall("testInit"));
        suite.addTest(new JellyOverall("testCheckOut"));
        suite.addTest(new JellyOverall("testAdd"));
        //        suite.addTest(new JellyOverall("testAddRecursive")); // fails due to bug #27582
        suite.addTest(new JellyOverall("testAddTextFile"));
        suite.addTest(new JellyOverall("testAddBinaryFile"));
        suite.addTest(new JellyOverall("testCommitFiles"));
        suite.addTest(new JellyOverall("testRefreshFile"));
        suite.addTest(new JellyOverall("testRefreshDirectory"));
        suite.addTest(new JellyOverall("testRefreshRecursively"));
        suite.addTest(new JellyOverall("testAddTag"));
        suite.addTest(new JellyOverall("testCommitToBranch"));
        suite.addTest(new JellyOverall("testUpdate"));
        suite.addTest(new JellyOverall("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    static String tInitDir = "initdir", tInitSubDir = "initsubdir";
    GenericNode InitDir, InitSubDir, Directory, File, SubDir, SubFile, Text1, Text2, Binary;

    public void prepareServer(String dir) {
        new File(dir + "/" + tInitDir).mkdirs();
        new File(dir + "/" + tInitDir + "/" + tInitSubDir).mkdirs();
    }
    
    public void createStructure() {
        InitDir = new GenericNode (root, tInitDir);
        InitSubDir = new GenericNode (InitDir, tInitSubDir);
        Directory = new GenericNode (root, "directory");
        File = new GenericNode (Directory, "file");
        SubDir = new GenericNode (Directory, "subdir");
        SubFile = new GenericNode (SubDir, "subfile");
        Text1 = new GenericNode (InitDir, "text1");
        Text2 = new GenericNode (InitDir, "text2");
        Binary = new GenericNode (InitSubDir, "binary");
    }
    
    public void prepareClient () {
    }
    
    public void configure () {
        super.configure();
    }

    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
        new Node (exp.repositoryTab ().tree (), "").waitChildNotPresent(root.node ());
    }
    
    public void testInit() {
        root.cvsNode ().cVSInit();
        history.getWaitCommand("Init", root.node ());
    }
    
    public void testCheckOut() {
        root.cvsNode ().cVSCheckOut();
        CVSCheckoutFolderAdvDialog co = new CVSCheckoutFolderAdvDialog();
        co.setModuleS(hRoot);
        co.checkPruneEmptyFolders(false);
        sleep(1000);
        getLog().println(co.cbPruneEmptyFolders().isSelected());
        co.oK();
        root.waitHistory("Check Out");
        
        // stabilization
        VCSCommandsOutputOperator voo = new VCSCommandsOutputOperator("CHECKOUT_COMMAND");
        closeAllVCSWindows();
        
        refresh (root);
        InitDir.waitStatus(null);
        sleep(5000); // stabilization - read lock problem
        refreshRecursively (root);
        InitDir.waitStatus(null);
        InitSubDir.waitStatus(null);
    }
    
    public void testAdd() {
        SubDir.mkdirs ();
        File.save ("");
        SubFile.save ("");
        
        refresh (root);
        Directory.cvsNode ();
        SubDir.cvsNode ();
        File.cvsNode ();
        SubFile.cvsNode ();
        //Directory.waitStatus("Local"); // may fail due to #28177
        //SubDir.waitStatus("Local"); // may fail due to #28177
        File.waitStatus("Local");
        SubFile.waitStatus("Local");
        
        Directory.cvsNode ().cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
        add.setFileDescription("Initial state");
        add.oK();
        assertInformationDialog(null);
        Directory.waitHistory ("Add");
        Directory.waitStatus(null);
        //waitStatus("Local"); // may fail due to #28177
        File.waitStatus("Local");
        SubFile.waitStatus("Local");
    }
    
    public void testAddRecursive() {
        Directory.waitStatus(null);
        Directory.cvsNode ().cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
        add.setFileDescription("Initial state");
        add.addAllLocalFilesInFolderContents();
        add.checkAddTheFolderContentsRecursively(true);
        add.oK();
        Directory.waitHistory ("Add");
        Directory.cvsNode ();
        SubDir.cvsNode ();
        File.cvsNode ();
        SubFile.cvsNode ();
        Directory.waitStatus(null);
        SubDir.waitStatus(null);
        File.waitStatus("Locally Added", false);
        SubFile.waitStatus("Locally Added", false);
        assertInformationDialog(null);
    }
    
    public void testAddTextFile() {
        Text1.save ("");
        Text2.save ("");

        refresh (InitDir);
        Text1.cvsNode ();
        Text2.cvsNode ();
        Text1.waitStatus("Local");
        Text2.waitStatus("Local");
        new CVSAddAction().perform(new Node [] {
            Text1.cvsNode (),
            Text2.cvsNode (),
        });
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog();
        add.setFileDescription("Initial state");
        add.oK();
        waitCommand ("Add", new GenericNode [] { Text1, Text2 });
        Text1.waitStatus("Locally Added", false);
        Text2.waitStatus("Locally Added", false);
    }
    
    public void testAddBinaryFile() {
        Binary.save ("");

        refresh (InitSubDir);
        Binary.cvsNode ();
        Binary.waitStatus("Local");
        Binary.cvsNode ().cVSAdd();
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog();
        add.setFileDescription("Initial state");
        add.setKeywordSubstitution(CVSAddFileAdvDialog.ITEM_BINARYKB);
        add.oK();
        Binary.waitHistory ("Add");
        Binary.waitStatus("Locally Added");
    }
    
    public void testCommitFiles() {
        Text1.cvsNode ();
        Text2.cvsNode ();
        Binary.cvsNode ();
        Text1.waitStatus("Locally Added");
        Text2.waitStatus("Locally Added");
        Binary.waitStatus("Locally Added");
        new CVSCommitAction().perform(new Node[] {
            Text1.cvsNode (),
            Text2.cvsNode (),
            Binary.cvsNode (),
        });
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("Initial commit - text1 and text2");
        co.oK();
        co.waitClosed();
        co = new CVSCommitFileAdvDialog();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("Initial commit - binary");
        co.oK();
        co.waitClosed();
        waitCommand ("Commit", new GenericNode [] { Text1, Text2, Binary });
        Text1.waitStatus("Up-to-date; 1.1");
        Text2.waitStatus("Up-to-date; 1.1");
        Binary.waitStatus("Up-to-date; 1.1");
    }
    
    public void testRefreshFile() {
        Text2.cvsNode ();
        Text2.waitStatus("Up-to-date; 1.1");
        Text2.save ("Modified - Text2");
        refresh (Text2);
        Text2.waitStatus("Locally Modified; 1.1");
    }
    
    public void testRefreshDirectory() {
        refresh (root);
        Text1.cvsNode ();
        Binary.cvsNode ();
        InitDir.waitStatus(null);
        InitSubDir.waitStatus(null);
        Text1.waitStatus("Up-to-date; 1.1");
        Binary.waitStatus("Up-to-date; 1.1");
        
        Text1.save ("Modified - Text1");
        Binary.save ("Modified - Binary");
        
        refresh (InitDir);
        InitDir.waitStatus(null);
        InitSubDir.waitStatus(null);
        Text1.waitStatus("Locally Modified; 1.1");
        Binary.waitStatus("Up-to-date; 1.1");
    }
    
    public void testRefreshRecursively() {
        Text1.save ("Modified - Text1");
        Binary.save ("Modified - Binary");
        
        refreshRecursively(InitDir);
        InitDir.waitStatus(null);
        InitSubDir.waitStatus(null);
        Text1.waitStatus("Locally Modified; 1.1");
        Binary.waitStatus("Locally Modified; 1.1");
    }
    
    public void testAddTag() {
        Text2.waitStatus("Locally Modified; 1.1");
        Text2.cvsNode ().cVSBranchingAndTaggingAddTag();
        CVSAddTagFileAdvDialog add = new CVSAddTagFileAdvDialog();
        add.checkBranchTag(true);
        add.setTagName("MyTag");
        add.select();
        CVSRevisionSelectorDialog rev = new CVSRevisionSelectorDialog();
        rev.lstCVSRevisionSelector().clickOnItem("1.1");
        rev.oK();
        add.oK();
        Text2.waitHistory ("Add Tag");
        assertInformationDialog (null);
    }
    
    public void testCommitToBranch() {
        Text2.waitStatus("Locally Modified; 1.1");
        Text2.cvsNode ().cVSCommit();
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog();
        co.select();
        CVSBranchSelectorDialog br = new CVSBranchSelectorDialog();
        br.lstCVSBranchSelector().clickOnItem("1.1.2  MyTag");
        br.oK();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("Initial commit to branch");
        co.oK();
        Text2.waitHistory ("Commit");
        Text2.waitStatus("Up-to-date; 1.1.2.1");
    }
    
    public void testUpdate() {
        new DeleteAction().perform(Binary.cvsNode ());
        assertConfirmObjectDeletionYes (null);
        refresh (InitSubDir); // workaround for issue #27589
        Binary.waitStatus("Needs Update; 1.1");
        Binary.cvsNode ().cVSUpdate();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog();
        up.oK();
        Binary.waitStatus("Up-to-date; 1.1");
    }
    
}
