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
import java.io.File;
import java.util.StringTokenizer;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.modules.vcscore.GroupVerificationOperator;
import org.netbeans.jellytools.modules.vcscore.VCSGroupsFrameOperator;
import org.netbeans.jellytools.modules.vcscore.VersioningFrameOperator;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSFileNode;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSAddFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSAddFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSCommitFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSUpdateFileAdvDialog;

public class JellyGroup extends CVSStub {
    
    public static final String TEST_GROUP = "TestGroup";
    public static final String GROUP_DESCRIPTION = "Description of " + TEST_GROUP;
    
    public JellyGroup(String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        complete.GenericStub.DEBUG = true;
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyGroup("configure"));
//        suite.addTest(new JellyGroup("testDefaultGroup")); // cause destabilization
        suite.addTest(new JellyGroup("testGroups"));
        suite.addTest(new JellyGroup("testAddToGroup"));
        suite.addTest(new JellyGroup("testCommitGroup"));
        suite.addTest(new JellyGroup("testVerifyGroupToAdd"));
        suite.addTest(new JellyGroup("testVerifyGroupNeedsUpdate"));
        suite.addTest(new JellyGroup("testVerifyGroupNotChanged"));
        suite.addTest(new JellyGroup("testUnmount"));

        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    static String tInitDir = "initdir";
    GenericNode InitDir, Text1, Text2, Text3;
    
    static boolean text1InDefaultGroup = false;

    protected void prepareServer (String dir) {
        new File(dir + "/" + tInitDir).mkdirs();
    }
    
    public void createStructure () {
        InitDir = new GenericNode (root, tInitDir);
        Text1 = new GenericNode (InitDir, "text1");
        Text2 = new GenericNode (InitDir, "text2");
        Text3 = new GenericNode (InitDir, "text3");
    }

    public void prepareClient () {
        super.prepareClient ();
        Text1.save ("");
        Text2.save ("");

        refreshRecursively (root);
        Text1.cvsNode ();
        Text2.cvsNode ();

        InitDir.cvsNode ().cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
        add.setFileDescription("Initial state");
        add.addAllLocalFilesInFolderContents();
        add.checkAddTheFolderContentsRecursively(true);
        add.oK();
        assertInformationDialog(null);
        InitDir.waitHistory ("Add");
        Text1.waitStatus("Locally Added");
        Text2.waitStatus("Locally Added");

        InitDir.cvsNode ().cVSCommit();
        CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog();
        com.txtEnterReason().setCaretPosition(0);
        com.txtEnterReason().typeText("Initial commit");
        com.oK();
        com.waitClosed();
        InitDir.waitHistory ("Commit");
        Text1.waitStatus("Up-to-date; 1.1");
        Text2.waitStatus("Up-to-date; 1.1");
    }
    
    public void configure () {
        super.configure();
    }

    public void testDefaultGroup () {
        closeAllVCSWindows();
        openGroupsFrame();
        Text1.cvsNode ().includeInVCSGroupDefaultGroup();
        vgf.removeVCSGroup(DEFAULT_GROUP);
        new Node (vgf.treeVCSGroupsTreeView (), "").waitChildNotPresent(DEFAULT_GROUP);
        Text1.cvsNode ().includeInVCSGroupDefaultGroup();
        Text1.cvsGroupNode (DEFAULT_GROUP);
        text1InDefaultGroup = true;
    }
    
    public void addVCSGroup (VCSGroupsFrameOperator vgf, String name) {
        new Action (null, "Add VCS Group").performPopup(new Node (vgf.treeVCSGroupsTreeView(), ""));
        NbDialogOperator dia = new NbDialogOperator ("Add VCS Group");
        JTextFieldOperator text = new JTextFieldOperator (dia);
        text.clearText();
        text.typeText(name);
        dia.ok ();
        dia.waitClosed();
    }
    
    public void testGroups () {
        closeAllVCSWindows();
        openGroupsFrame();

        addVCSGroup(vgf, TEST_GROUP);

        closeAllVCSWindows(); // stabilization
        openGroupsFrame(); // stabilization
        addVCSGroup(vgf, "GroupToRename");
        new Node (vgf.treeVCSGroupsTreeView (), "GroupToRename");

        closeAllVCSWindows(); // stabilization
        openGroupsFrame(); // stabilization
        vgf.renameVCSGroup("GroupToRename", "RenamedGroup");
        new Node (vgf.treeVCSGroupsTreeView (), "").waitChildNotPresent("GroupToRename");
        new Node (vgf.treeVCSGroupsTreeView (), "RenamedGroup");

        closeAllVCSWindows(); // stabilization
        openGroupsFrame(); // stabilization
        addVCSGroup(vgf, "GroupToRemove");
        new Node (vgf.treeVCSGroupsTreeView (), "GroupToRemove");

        closeAllVCSWindows(); // stabilization
        openGroupsFrame(); // stabilization
        vgf.removeVCSGroup("GroupToRemove");
        new Node (vgf.treeVCSGroupsTreeView (), "").waitChildNotPresent("GroupToRemove");
    }

    public void testAddToGroup () {
        closeAllVCSWindows();
        openGroupsFrame();

        Text2.cvsNode ().includeInVCSGroup(TEST_GROUP);
        Text2.cvsGroupNode(TEST_GROUP);

        if (text1InDefaultGroup)
            Text1.cvsGroupNode (DEFAULT_GROUP);
        Text1.cvsNode ().includeInVCSGroup(TEST_GROUP);
        if (text1InDefaultGroup)
            new NbDialogOperator ("Question").yes ();
        Text1.cvsGroupNode (TEST_GROUP);
        new Node (vgf.treeVCSGroupsTreeView(), DEFAULT_GROUP).waitChildNotPresent(Text1.name ());
        
        closeAllProperties();
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP).select (); // stabilization
        sleep (2000); // stabilization
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP).properties ();
        PropertySheetOperator pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, TEST_GROUP);
        PropertySheetTabOperator pst = pso.getPropertySheetTabOperator("Properties");
        new StringProperty(pst, "Description").setStringValue(GROUP_DESCRIPTION);
        sleep (2000); // stabilization
        //pso.close();
        
        closeAllProperties();
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP).select (); // stabilization
        sleep (2000); // stabilization
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP).properties ();
        pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, TEST_GROUP);
        pst = pso.getPropertySheetTabOperator("Properties");
        info.println ("Group Name: " + new StringProperty(pst, "Group Name").getStringValue());
        info.println ("Description: " + new StringProperty(pst, "Description").getStringValue());
        //pso.close();
    }
    
    public void testCommitGroup () {
        closeAllVCSWindows();
        openGroupsFrame();
        refresh (InitDir);
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text1.name (), "Up-to-date; 1.1", true);
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text2.name (), "Up-to-date; 1.1", true);
        Text1.save ("text1");
        Text2.save ("text2");
        refresh (InitDir);
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text1.name (), "Locally Modified; 1.1", true);
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text2.name (), "Locally Modified; 1.1", true);
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP).select (); // stabilization
	sleep (2000); // stabilization
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP).cVSCommit ();
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog ();
        String str = co.txtEnterReason().getText ();
        info.println (str);
        StringTokenizer st = new StringTokenizer (str, "\n");
        boolean validDescription = GROUP_DESCRIPTION.equals (st.nextToken());
//        assertEquals("Invalid description in group commit dialog", GROUP_DESCRIPTION, st.nextToken());
        if (!validDescription) try {
            PNGEncoder.captureScreen(getWorkDir().getAbsolutePath() + File.separatorChar + "invalidDesc.png");
        } catch (Exception e) {}
        co.oK ();
        co.waitClosed ();
        waitCommand ("Commit", new GenericNode [] { Text1, Text2 });
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text1.name (), "Up-to-date; 1.2", true);
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text2.name (), "Up-to-date; 1.2", true);
        closeAllVersionings(); // stabilization
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text1.name (), "Up-to-date; 1.2", true); // stabilization
        Text1.cvsGroupNode (TEST_GROUP).versioningExplorer(); // stabilization
        newVersioningFrame ();
        if (validDescription)
            Text1.cvsVersioningNode (" [Up-to-date; 1.2]|1.2  " + GROUP_DESCRIPTION);
        else
            Text1.cvsVersioningNode (" [Up-to-date; 1.2]|1.2");
        closeAllVersionings(); // stabilization
        waitNodeStatus(vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text2.name (), "Up-to-date; 1.2", true); // stabilization
        Text2.cvsGroupNode (TEST_GROUP).versioningExplorer(); // stabilization
        vfo = new VersioningFrameOperator (); // stabilization
        if (validDescription)
            Text2.cvsVersioningNode (" [Up-to-date; 1.2]|1.2  " + GROUP_DESCRIPTION);
        else
            Text2.cvsVersioningNode (" [Up-to-date; 1.2]|1.2");
        // assertTrue ("Invalid description in group commit dialog", validDescription); // fails due to issue #28679
    }
    
    public void testVerifyGroupToAdd () {
        closeAllVCSWindows();
        openGroupsFrame();
        Text3.save ("text3");
        refresh (InitDir);
        Text3.cvsNode ().includeInVCSGroup(TEST_GROUP);
        Text3.cvsGroupNode (TEST_GROUP);
        
        new Action (null,"Verify").performPopup (new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP));
        GroupVerificationOperator gvo = new GroupVerificationOperator ();
        //sleep (2000);
        dumpVerifyGroupTable (gvo.tabLocalFilesToAdd());
//        gvo.tabLocalFilesToAdd().waitCell (tText3, 0, 0);
//        gvo.tabLocalFilesToAdd().waitCell (tInitDir, 0, 1);
        
        gvo.checkAddFilesToRepository(true);
        gvo.addAllFiles();
        gvo.btCorrectGroup().pushNoBlock();
        
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog ();
        add.oK();
        Text3.waitHistory ("Add");
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text3.name (), "Locally Added", false);
        compareReferenceFiles();
    }
    
    public void testVerifyGroupNeedsUpdate () {
        closeAllVCSWindows();
        openGroupsFrame ();

        new DeleteAction ().performPopup (
            Text2.cvsNode ()
        );
        assertConfirmObjectDeletionYes (null);
        
        Text2.cvsGroupNode (TEST_GROUP);
        waitNodeStatus (vgf.treeVCSGroupsTreeView(), TEST_GROUP + "|" + Text2.name (), "Needs Update", false);
        
        new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP).select (); // stabilization
        sleep (2000); // stabilization
        new Action (null,"Verify").performPopup (new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP));
        GroupVerificationOperator gvo = new GroupVerificationOperator ();
        //sleep (2000);
        dumpVerifyGroupTable (gvo.tabFilesNeedsUpdate());
        
        gvo.checkSynchronizeWorkingCopyWithRepository(true);
        gvo.updateAllFiles();
        gvo.btCorrectGroup().pushNoBlock();
        
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.oK ();
        
        Text2.waitHistory ("Update");
        waitNodeStatus (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + Text2.name (), "Up-to-date", false);
        compareReferenceFiles();
    }
    
    public void testVerifyGroupNotChanged () {
        closeAllVCSWindows();
        openGroupsFrame();

        new Action (null,"Verify").performPopup (new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP));
        GroupVerificationOperator gvo = new GroupVerificationOperator ();
        //sleep (2000);
        dumpVerifyGroupTable (gvo.tabNotChangedFiles());
        
        gvo.checkRemoveFilesFromGroup(true);
        gvo.removeAllFiles();
        gvo.btCorrectGroup().pushNoBlock();

        new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP).waitChildNotPresent(Text1.name ());
        new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP).waitChildNotPresent(Text2.name ());
        Text3.cvsGroupNode(TEST_GROUP);
        compareReferenceFiles();
    }
    
    public void testUnmount() {
        closeAllVCSWindows();
        openGroupsFrame ();

        Text1.cvsNode ().includeInVCSGroup(TEST_GROUP);
        Text2.cvsNode ().includeInVCSGroup(TEST_GROUP);
        Text1.cvsGroupNode (TEST_GROUP);
        Text2.cvsGroupNode (TEST_GROUP);
        Text3.cvsGroupNode (TEST_GROUP);
        
        new FilesystemNode(exp.repositoryTab().tree(), root.node ()).unmount();
        new Node (exp.repositoryTab ().tree (), "").waitChildNotPresent(root.node ());
        
        Text1.cvsGroupNode (TEST_GROUP, " (Broken link)");
        Text2.cvsGroupNode (TEST_GROUP, " (Broken link)");
        Text3.cvsGroupNode (TEST_GROUP, " (Broken link)");
    }

}
