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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import junit.framework.TestSuite;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NbFrameOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.modules.vcscore.GroupVerificationOperator;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcscore.VCSGroupsFrameOperator;
import org.netbeans.jellytools.modules.vcscore.VersioningFrameOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.IncludeInVCSGroupAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.IncludeInVCSGroupActionNoBlock;
import org.netbeans.jellytools.modules.vcsgeneric.actions.VCSGenericMountAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.VCSGroupsAction;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSFileNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSVersioningBranchNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSVersioningFileNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSVersioningVersionNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.FilesystemHistoryNode;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardAdvanced;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardProfile;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSAddFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSAddFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSAddTagFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSBranchSelectorDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSCheckoutFolderAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSCommitFileAdvDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSRevisionSelectorDialog;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.CVSUpdateFileAdvDialog;
import org.openide.TopManager;
import org.openide.filesystems.FileSystem;
import org.openide.util.Utilities;
import util.Helper;
import util.History;
import util.StatusBarTracer;

public class JellyGroup extends JellyTestCase {
    
    public static final boolean DEBUG = false;
    
    public static final String TEST_GROUP = "TestGroup";
    public static final String GROUP_DESCRIPTION = "Description of TestGroup";
    
    public JellyGroup(String testName) {
        super(testName);
    }
    
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyGroup("testWorkDir"));
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
        junit.textui.TestRunner.run(suite());
    }
    
    ExplorerOperator exp;
    StatusBarTracer sbt;
    static String serverDirectory;
    static String clientDirectory;
    static String hRoot = ".", nRoot;
    static String tInitDir = "initdir", hInitDir = tInitDir, fInitDir, nInitDir;
    static String tText1 = "text1", hText1 = hInitDir + "/" + tText1, fText1, nText1;
    static String tText2 = "text2", hText2 = hInitDir + "/" + tText2, fText2, nText2;
    static String tText3 = "text3", hText3 = hInitDir + "/" + tText3, fText3, nText3;
    
    static boolean text1InDefaultGroup = false;
    
    PrintStream out;
    PrintStream info;
    static History history;

    public static void closeAllProperties() {
        for (;;) {
            NbFrameOperator fr = NbFrameOperator.find("Propert", 0);
            if (fr == null)
                break;
            fr.close();
            Helper.sleep(1000);
        }
    }
    
    public void waitStatus(String status, String node) {
        assertTrue ("waitStatus(status,node) precondition failed", status == null  ||  status.indexOf(';') < 0);
        waitStatus(status, node, false);
    }
    
    public void waitStatus(String status, String node, boolean withversion) {
        waitStatus (exp.repositoryTab ().tree (), status, node, withversion);
    }
    
    public void waitStatus(JTreeOperator tree, String status, String node, boolean withversion) {
        String ano = null;
        for (int a = 0; a < 15; a ++) {
            Helper.sleep(1000);
            Node n = new Node(tree, node);
            ano = n.getText();
            int i = ano.indexOf('[');
            if (i < 0) {
                if (status == null)
                    return;
                continue;
            }
            ano = ano.substring(i + 1);
            i = ano.lastIndexOf(']');
            if (i < 0)
                continue;
            ano = ano.substring(0, i);
            if (!withversion) {
                i = ano.indexOf(';');
                if (i >= 0)
                    ano = ano.substring(0, i);
            }
            if (ano.equals(status))
                return;
        }
        assertTrue("CVS File Status is not reached: Expected: " + status + " Got: " + ano, false);
    }
    
    public void waitVersion(String version, String node) {
        String ano = null;
        for (int a = 0; a < 15; a ++) {
            Helper.sleep(1000);
            Node n = new Node(exp.repositoryTab().tree(), node);
            ano = n.getText();
            int i = ano.indexOf('(');
            if (i < 0) {
                if (version == null)
                    return;
                continue;
            }
            ano = ano.substring(i + 1);
            i = ano.lastIndexOf(')');
            if (i < 0)
                continue;
            ano = ano.substring(0, i);
            if (ano.equals(version))
                return;
        }
        assertTrue("CVS File Version is not reached: Expected: " + version + " Got: " + ano, false);
    }
    
    boolean equalPaths (String p1, String p2) {
        p1 = p1.replace ('\\', '/');
        p2 = p2.replace ('\\', '/');
        return p1.equalsIgnoreCase(p2);
    }
    
    protected void setUp() throws Exception {
        exp = new ExplorerOperator();
        sbt = new StatusBarTracer();
        out = getRef();
        info = getLog();
    }
    
    public void testWorkDir() {
        String workroot;
        try {
            workroot = getWorkDirPath();
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while getWorkDirPath()", e);
        }
        serverDirectory = workroot + "/server";
        clientDirectory = workroot + "/client";
        
        new File(serverDirectory).mkdirs();
        new File(clientDirectory).mkdirs();
        new File(serverDirectory + "/" + tInitDir).mkdirs();
        
        info.println("Server: " + serverDirectory);
        info.println("Client: " + clientDirectory);
        
        if (!DEBUG) {
            // mount
            new VCSGenericMountAction().perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
            wizard.verify("");
            wizard.setWorkingDirectory(clientDirectory);
            String profile = Utilities.isUnix() ? VCSWizardProfile.CVS_UNIX : VCSWizardProfile.CVS_WIN_NT;
            sbt.start();
            wizard.setProfile(profile);
            sbt.removeText("Command AUTO_FILL_CONFIG finished.");
            sbt.stop();
            wizard.setCVSServerType("local");
            wizard.setCVSServerName("");
            wizard.setCVSUserName("");
            wizard.setCVSRepository(serverDirectory);
            wizard.next();
            VCSWizardAdvanced wizard2 = new VCSWizardAdvanced();
            wizard2.checkAdvancedMode(true);
            wizard2.finish();
            Helper.sleep(5000);
        }
        
        nRoot = "CVS " + clientDirectory;
        boolean found = false;
        info.println("Searching for CVS filesystem: " + nRoot);
        for (int a = 0; a < 10; a ++) {
            Enumeration e = TopManager.getDefault().getRepository().getFileSystems();
            while (e.hasMoreElements()) {
                FileSystem f = (FileSystem) e.nextElement();
                info.println("Is it: " + f.getDisplayName());
                if (equalPaths (f.getDisplayName(), nRoot)) {
                    info.println("Yes");
                    nRoot = f.getDisplayName();
                    info.println("Working Directory nRoot: " + nRoot);
                    history = new History(f);
                    found = true;
                    break;
                }
            }
            if (found == true)
                break;
            Helper.sleep(1000);
        }
        assertTrue("Filesystem not found: Filesystem: " + nRoot, found);
        new CVSFileNode(exp.repositoryTab().tree(), nRoot);
        
        fInitDir = clientDirectory + "/" + tInitDir;
        nInitDir = nRoot + "|" + tInitDir;
        
        fText1 = fInitDir + "/" + tText1;
        nText1 = nInitDir + "|" + tText1;
        
        fText2 = fInitDir + "/" + tText2;
        nText2 = nInitDir + "|" + tText2;
        
        fText3 = fInitDir + "/" + tText3;
        nText3 = nInitDir + "|" + tText3;
        
        if (!DEBUG) {
            closeAllProperties();
            FilesystemHistoryNode cvshistorynode = new FilesystemHistoryNode(exp.runtimeTab().tree(), nRoot);
            cvshistorynode.properties();
            PropertySheetOperator pso = new PropertySheetOperator(PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, nRoot);
            PropertySheetTabOperator pst = pso.getPropertySheetTabOperator("Properties");
            new StringProperty(pst, "Number of Finished Commands To Keep").setValue("200");
            pso.close();
        
            // init
            new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSInit();
            history.waitCommand("Init", ".");

            // checkout
            new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSCheckOut();
            CVSCheckoutFolderAdvDialog co = new CVSCheckoutFolderAdvDialog();
            co.setModuleS(hRoot);
            co.checkPruneEmptyFolders(false);
            co.oK();
            assertTrue("Check Out command failed", history.waitCommand("Check Out", hRoot));

            // workaround - probably jelly issue - if not used, popup menu does not work in versioning frame
            VCSCommandsOutputOperator voo = new VCSCommandsOutputOperator ("CHECKOUT_COMMAND");
            voo.close(); 
            voo.waitClosed();

            // add
            try {
                new File(fText1).createNewFile();
                new File(fText2).createNewFile();
            } catch (IOException e) {
                throw new AssertionFailedErrorException("IOException while setting test case up", e);
            }

            new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSRefreshRecursively();
            assertTrue("Refresh recursively folder command failed", history.waitCommand("Refresh Recursively", hRoot));
            new CVSFileNode(exp.repositoryTab().tree(), nText1);
            new CVSFileNode(exp.repositoryTab().tree(), nText2);

            new CVSFileNode(exp.repositoryTab().tree(), nInitDir).cVSAdd();
            CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
            add.setFileDescription("Initial state");
            add.addAllLocalFilesInFolderContents();
            add.checkAddTheFolderContentsRecursively(true);
            add.oK();
            new NbDialogOperator("Information").ok();
            assertTrue("Add folder recursively command failed", history.waitCommand("Add", hInitDir));
            waitStatus("Locally Added", nText1);
            waitStatus("Locally Added", nText2);

            // commit
            new CVSFileNode(exp.repositoryTab().tree(), nInitDir).cVSCommit ();
            CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog();
            com.txtEnterReason().setCaretPosition(0);
            com.txtEnterReason().typeText("Initial commit");
            com.oK();
            com.waitClosed();
            assertTrue("Commit files command failed", history.waitCommand("Commit", hInitDir));
            waitStatus("Up-to-date; 1.1", nText1, true);
            waitStatus("Up-to-date; 1.1", nText2, true);
        }
    }
    
    public void testDefaultGroup () {
        new VCSGroupsAction ().perform ();
        VCSGroupsFrameOperator vgf = new VCSGroupsFrameOperator ();
        new IncludeInVCSGroupAction ().perform (new CVSFileNode (exp.repositoryTab ().tree (), nText1));
        new Node (vgf.treeVCSGroupsTreeView (), "<Default Group>|" + tText1);
        vgf.removeVCSGroup("<Default Group>");
        Helper.waitNoNode (vgf.treeVCSGroupsTreeView (), "", "<Default Group>");
        new IncludeInVCSGroupAction ().perform (new CVSFileNode (exp.repositoryTab ().tree (), nText1));
        new Node (vgf.treeVCSGroupsTreeView (), "<Default Group>|" + tText1);
        text1InDefaultGroup = true;
    }
    
    public void testGroups () {
        new VCSGroupsAction ().perform ();
        VCSGroupsFrameOperator vgf = new VCSGroupsFrameOperator ();

        vgf.addVCSGroup("TestGroup");
        vgf.addVCSGroup("GroupToRemove");
        new Node (vgf.treeVCSGroupsTreeView (), "GroupToRemove");
        vgf.removeVCSGroup("GroupToRemove");
        Helper.waitNoNode(vgf.treeVCSGroupsTreeView (), "", "GroupToRemove");

        vgf.addVCSGroup("GroupToRename");
        new Node (vgf.treeVCSGroupsTreeView (), "GroupToRename");
        vgf.renameVCSGroup("GroupToRename", "RenamedGroup");
        Helper.waitNoNode (vgf.treeVCSGroupsTreeView (), "", "GroupToRename");
        new Node (vgf.treeVCSGroupsTreeView (), "RenamedGroup");
    }

    public void testAddToGroup () {
        new VCSGroupsAction ().perform ();
        VCSGroupsFrameOperator vgf = new VCSGroupsFrameOperator ();

        new IncludeInVCSGroupAction (TEST_GROUP).perform(
            new CVSFileNode (exp.repositoryTab ().tree (), nText2)
        );
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP + "|" + tText2);

        if (text1InDefaultGroup)
            new CVSFileNode (vgf.treeVCSGroupsTreeView (), "<Default Group>|" + tText1);
        new IncludeInVCSGroupActionNoBlock (TEST_GROUP).perform(
            new CVSFileNode (exp.repositoryTab ().tree (), nText1)
        );
        if (text1InDefaultGroup)
            new NbDialogOperator ("Question").yes ();
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP + "|" + tText1);
        Helper.waitNoNode (vgf.treeVCSGroupsTreeView(), "<Default Group>", tText1);
        
        closeAllProperties();
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP).select (); // stabilization
        Helper.sleep (2000); // stabilization
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP).properties ();
        PropertySheetOperator pso = new PropertySheetOperator (PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, TEST_GROUP);
        PropertySheetTabOperator pst = pso.getPropertySheetTabOperator("Properties");
        new StringProperty(pst, "Description").setValue(GROUP_DESCRIPTION);
        pso.close();
    }
    
    public void saveToFile (String filename, String text) {
        try {
            FileWriter fr = new FileWriter (filename);
            fr.write(text);
            fr.close ();
        } catch (IOException e) {
            new AssertionFailedErrorException ("IOException while saving text into file: File: " + filename + " Text: " + text, e);
        }
    }
    
    public void testCommitGroup () {
        new VCSGroupsAction ().perform ();
        VCSGroupsFrameOperator vgf = new VCSGroupsFrameOperator ();
        new CVSFileNode (exp.repositoryTab ().tree (), nInitDir).cVSRefresh ();
        assertTrue("Refresh directory command failed", history.waitCommand("Refresh", hInitDir));
        waitStatus (vgf.treeVCSGroupsTreeView (), "Up-to-date; 1.1", TEST_GROUP + "|" + tText1, true);
        waitStatus (vgf.treeVCSGroupsTreeView (), "Up-to-date; 1.1", TEST_GROUP + "|" + tText2, true);
        saveToFile (fText1, "text1");
        saveToFile (fText2, "text2");
        new CVSFileNode (exp.repositoryTab ().tree (), nInitDir).cVSRefresh ();
        assertTrue("Refresh directory command failed", history.waitCommand("Refresh", hInitDir));
        waitStatus (vgf.treeVCSGroupsTreeView (), "Locally Modified; 1.1", TEST_GROUP + "|" + tText1, true);
        waitStatus (vgf.treeVCSGroupsTreeView (), "Locally Modified; 1.1", TEST_GROUP + "|" + tText2, true);
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP).select (); // stabilization
	Helper.sleep (2000); // stabilization
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP).cVSCommit ();
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog ();
        StringTokenizer st = new StringTokenizer (co.txtEnterReason().getText (), "\n");
        assertEquals("Invalid description in group commit dialog", GROUP_DESCRIPTION, st.nextToken());
        co.oK ();
        assertTrue("Commit files command failed", history.waitCommand("Commit", hText1 + "\n" + hText2));
        waitStatus(vgf.treeVCSGroupsTreeView (), "Up-to-date; 1.2", TEST_GROUP + "|" + tText1, true);
        waitStatus(vgf.treeVCSGroupsTreeView (), "Up-to-date; 1.2", TEST_GROUP + "|" + tText2, true);
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText1).versioningExplorer();
        VersioningFrameOperator vfo = new VersioningFrameOperator ();
        new Node (vfo.treeVersioningTreeView(), nText1 + " [Up-to-date; 1.2]|1.2  " + GROUP_DESCRIPTION);
        new Node (vfo.treeVersioningTreeView(), nText2 + " [Up-to-date; 1.2]|1.2  " + GROUP_DESCRIPTION);
    }
    
    public void dumpTable (JTableOperator table) {
        int height = table.getRowCount();
        int width = table.getColumnCount();
        out.println("Height: " + height);
        out.println("Width: " + width);
        String[] strs = new String[height];
        for (int a = 0; a < height; a ++) {
            String comp = "";
            for (int b = 0; b < width; b ++) {
                if (b != 0)
                    comp += "    ";
                String str = (table.getValueAt(a, b) != null) ? table.getValueAt(a, b).toString () : "<NULL>";
                int i = str.indexOf (nRoot);
                if (i >= 0)
                    str = str.substring (0, i) + "<FS>" + str.substring (i + nRoot.length());
                comp += str;
            }
            strs[a] = comp;
        }
        java.util.Arrays.sort (strs);
        for (int a = 0; a < height; a ++)
            out.println (a + ". - " + strs[a]);
    }
    
    
    public void testVerifyGroupToAdd () {
        new VCSGroupsAction ().perform ();
        VCSGroupsFrameOperator vgf = new VCSGroupsFrameOperator ();
        
        saveToFile (fText3, "text3");
        new CVSFileNode (exp.repositoryTab ().tree (), nInitDir).cVSRefresh ();
        assertTrue("Refresh directory command failed", history.waitCommand("Refresh", hInitDir));
        new CVSFileNode (exp.repositoryTab ().tree (), nText3).includeInVCSGroup(TEST_GROUP);
        new CVSFileNode (vgf.treeVCSGroupsTreeView(), TEST_GROUP + "|" + tText3);
        
        new Action (null,"Verify").performPopup (new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP));
        GroupVerificationOperator gvo = new GroupVerificationOperator ();
        //Helper.sleep (2000);
        dumpTable (gvo.tabLocalFilesToAdd());
//        gvo.tabLocalFilesToAdd().waitCell (tText3, 0, 0);
//        gvo.tabLocalFilesToAdd().waitCell (tInitDir, 0, 1);
        
        gvo.checkAddFilesToRepository(true);
        gvo.addAllFiles();
        gvo.btCorrectGroup().pushNoBlock();
        
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog ();
        add.oK();
        assertTrue("Add file command failed", history.waitCommand("Add", hText3));
        waitStatus (vgf.treeVCSGroupsTreeView (), "Locally Added", TEST_GROUP + "|" + tText3, false);
        compareReferenceFiles();
    }
    
    public void testVerifyGroupNeedsUpdate () {
        new VCSGroupsAction ().perform ();
        VCSGroupsFrameOperator vgf = new VCSGroupsFrameOperator ();

        new DeleteAction ().performPopup (new CVSFileNode (exp.repositoryTab ().tree (), nText2));
        new NbDialogOperator ("Confirm Object Deletion").yes ();
        
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText2);
        waitStatus (vgf.treeVCSGroupsTreeView(), "Needs Update; 1.2", TEST_GROUP + "|" + tText2, true);
        
        new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP).select (); // stabilization
        Helper.sleep (2000); // stabilization
        new Action (null,"Verify").performPopup (new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP));
        GroupVerificationOperator gvo = new GroupVerificationOperator ();
        //Helper.sleep (2000);
        dumpTable (gvo.tabFilesNeedsUpdate());
        
        gvo.checkSynchronizeWorkingCopyWithRepository(true);
        gvo.updateAllFiles();
        gvo.btCorrectGroup().pushNoBlock();
        
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.oK ();
        
        assertTrue("Update file command failed", history.waitCommand("Update", hText2));
        waitStatus (vgf.treeVCSGroupsTreeView (), "Up-to-date; 1.2", TEST_GROUP + "|" + tText2, true);
        compareReferenceFiles();
    }
    
    public void testVerifyGroupNotChanged () {
        new VCSGroupsAction ().perform ();
        VCSGroupsFrameOperator vgf = new VCSGroupsFrameOperator ();

        new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP).select (); // stabilization
        Helper.sleep (2000); // stabilization
        new Action (null,"Verify").performPopup (new Node (vgf.treeVCSGroupsTreeView (), TEST_GROUP));
        GroupVerificationOperator gvo = new GroupVerificationOperator ();
        //Helper.sleep (2000);
        dumpTable (gvo.tabNotChangedFiles());
        
        gvo.checkRemoveFilesFromGroup(true);
        gvo.removeAllFiles();
        gvo.btCorrectGroup().pushNoBlock();

        Helper.waitNoNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP, tText1);
        Helper.waitNoNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP, tText2);
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText3);
        compareReferenceFiles();
    }
    
    public void testUnmount() {
        new VCSGroupsAction ().perform ();
        VCSGroupsFrameOperator vgf = new VCSGroupsFrameOperator ();

        new CVSFileNode (exp.repositoryTab ().tree (), nText1).includeInVCSGroup(TEST_GROUP);
        new CVSFileNode (exp.repositoryTab ().tree (), nText2).includeInVCSGroup(TEST_GROUP);
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText1);
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText2);
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText3);
        
        new FilesystemNode(exp.repositoryTab().tree(), nRoot).unmount();
        
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText1 + " (Broken link)");
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText2 + " (Broken link)");
        new CVSFileNode (vgf.treeVCSGroupsTreeView (), TEST_GROUP + "|" + tText3 + " (Broken link)");
    }

}
