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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NbFrameOperator;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcscore.VersioningFrameOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.VCSGenericMountAction;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardAdvanced;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardProfile;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.vcs.advanced.wizard.mount.MountWizardData;
import org.netbeans.modules.vcs.advanced.wizard.mount.MountWizardIterator;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.*;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.util.Utilities;
import util.Helper;
import util.History;


public class JellyBranch extends JellyTestCase {
    
    public JellyBranch(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyBranch("testWorkDir"));
        suite.addTest(new JellyBranch("testMount"));
        suite.addTest(new JellyBranch("testInit"));
        suite.addTest(new JellyBranch("testCheckOut"));
        suite.addTest(new JellyBranch("testAdd"));
        suite.addTest(new JellyBranch("testCommit"));
        suite.addTest(new JellyBranch("testVersioning"));
        suite.addTest(new JellyBranch("testAddTag"));
        suite.addTest(new JellyBranch("testAddBranchTag"));
        suite.addTest(new JellyBranch("testCommitToBranch"));
        suite.addTest(new JellyBranch("testUpdateHead"));
        suite.addTest(new JellyBranch("testRemoveStickyTagFromHead"));
        suite.addTest(new JellyBranch("testCommitHead"));
        suite.addTest(new JellyBranch("testUpdateFromBranch"));
        suite.addTest(new JellyBranch("testRemoveStickyTagFromBranch"));
        suite.addTest(new JellyBranch("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    ExplorerOperator exp;
    static String serverDirectory;
    static String clientDirectory;
    static String hRoot = ".", nRoot;
    static String tInitDir = "initdir", hInitDir = tInitDir, fInitDir, nInitDir;
    static String tInitSubDir = "initsubdir", hInitSubDir = hInitDir + "/" + tInitSubDir, fInitSubDir, nInitSubDir;
    static String tDirectory = "directory", hDirectory = tDirectory, fDirectory, nDirectory;
    static String tFile = "file", hFile = hDirectory + "/" + tFile, fFile, nFile;
    static String tSubDir = "subdir", fSubDir, nSubDir;
    static String tSubFile = "subfile", fSubFile, nSubFile;
    static String tText1 = "text1", hText1 = hInitDir + "/" + tText1, fText1, nText1;
    static String tText2 = "text2", hText2 = hInitDir + "/" + tText2, fText2, nText2;
    static String tBinary = "binary", hBinary = hInitSubDir + "/" + tBinary, fBinary, nBinary;
    
    PrintStream out;
    PrintStream info;
    static History history;

    public static void closeAllWindows(String titlepart) {
        for (;;) {
            NbFrameOperator fr = NbFrameOperator.find(titlepart, 0);
            if (fr == null)
                break;
            fr.close();
            Helper.sleep(1000);
        }
    }

    public static void closeAllProperties() {
        closeAllWindows ("Propert");
    }
    
    public static void closeAllVersionings() {
        closeAllWindows ("Versioning");
    }
    
    public void waitStatus(String status, String node) {
        assertTrue ("waitStatus(status,node) precondition failed", status == null  ||  status.indexOf(';') < 0);
        waitStatus(status, node, false);
    }
    
    public void waitStatus(String status, String node, boolean withversion) {
        String ano = null;
        for (int a = 0; a < 15; a ++) {
            Helper.sleep(1000);
            Node n = new Node(exp.repositoryTab().tree(), node);
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
        if (Utilities.isUnix ()) {
            serverDirectory = serverDirectory.replace ('\\', '/');
            clientDirectory = clientDirectory.replace ('\\', '/');
        } else {
            serverDirectory = serverDirectory.replace ('/', '\\');
            clientDirectory = clientDirectory.replace ('/', '\\');
        }
        
        new File(serverDirectory).mkdirs();
        new File(clientDirectory).mkdirs();
        new File(serverDirectory + "/" + tInitDir).mkdirs();
        new File(serverDirectory + "/" + tInitDir + "/" + tInitSubDir).mkdirs();
        
        info.println("Server: " + serverDirectory);
        info.println("Client: " + clientDirectory);
    }
    
    public void testMount() {
        new VCSGenericMountAction().perform();
        VCSWizardProfile wizard = new VCSWizardProfile();
        wizard.verify("");
        wizard.setWorkingDirectory(clientDirectory);
        String profile = Utilities.isUnix() ? VCSWizardProfile.CVS_UNIX : VCSWizardProfile.CVS_WIN_NT;
            
        MountWizardIterator mwi = MountWizardIterator.singleton();
        MountWizardData mwd = mwi.getData();
        Method m;
        try {
            m = MountWizardData.class.getDeclaredMethod("getFileSystem", new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new AssertionFailedErrorException (e);
        }
        Object o;
        try {
            m.setAccessible(true);
            o = m.invoke(mwd, new Object[0]);
        } catch (IllegalAccessException e) {
            throw new AssertionFailedErrorException (e);
        } catch (InvocationTargetException e) {
            throw new AssertionFailedErrorException (e);
        }
        FileSystem fs = (FileSystem) o;
        history = new History (fs);
        history.breakpoint();

        wizard.setProfile(profile);
        history.waitCommand("AUTO_FILL_CONFIG", "");

        wizard.setCVSServerType("local");
        wizard.setCVSServerName("");
        wizard.setCVSUserName("");
        wizard.setCVSRepository(serverDirectory);
        wizard.next();
        VCSWizardAdvanced wizard2 = new VCSWizardAdvanced();
        wizard2.checkAdvancedMode(true);
        wizard2.finish();
        Helper.sleep(5000);
        
        nRoot = "CVS " + clientDirectory;
        boolean found = false;
        info.println("Searching for CVS filesystem: " + nRoot);
        for (int a = 0; a < 10; a ++) {
            Enumeration e = Repository.getDefault().getFileSystems();
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
        
        fInitSubDir = fInitDir + "/" + tInitSubDir;
        nInitSubDir = nInitDir + "|" + tInitSubDir;
        
        fDirectory = clientDirectory + "/" + tDirectory;
        nDirectory = nRoot + "|" + tDirectory;
        
        fFile = fDirectory + "/" + tFile;
        nFile = nDirectory + "|" + tFile;
        
        fSubDir = fDirectory + "/" + tSubDir;
        nSubDir = nDirectory + "|" + tSubDir;
        
        fSubFile = fSubDir + "/" + tSubFile;
        nSubFile = nSubDir + "|" + tSubFile;
        
        fText1 = fInitDir + "/" + tText1;
        nText1 = nInitDir + "|" + tText1;
        
        fText2 = fInitDir + "/" + tText2;
        nText2 = nInitDir + "|" + tText2;
        
        fBinary = fInitSubDir + "/" + tBinary;
        nBinary = nInitSubDir + "|" + tBinary;
        
        closeAllProperties();
        FilesystemHistoryNode cvshistorynode = new FilesystemHistoryNode(exp.runtimeTab().tree(), nRoot);
        cvshistorynode.properties();
        PropertySheetOperator pso = new PropertySheetOperator(PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, nRoot);
        PropertySheetTabOperator pst = pso.getPropertySheetTabOperator("Properties");
        new StringProperty(pst, "Number of Finished Commands To Keep").setValue("200");
        pso.close();
    }
    
    public void testUnmount() {
        new FilesystemNode(exp.repositoryTab().tree(), nRoot).unmount();
    }
    
    public void testInit() {
        new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSInit();
        history.waitCommand("Init", ".");
    }
    
    public void testCheckOut() {
        new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSCheckOut();
        CVSCheckoutFolderAdvDialog co = new CVSCheckoutFolderAdvDialog();
        co.setModuleS(hRoot);
        co.checkPruneEmptyFolders(false);
        Helper.sleep(1000);
        getLog ().println (co.cbPruneEmptyFolders ().isSelected ());
        co.oK();
        assertTrue("Check Out command failed", history.waitCommand("Check Out", hRoot));
        
        // workaround - probably jelly issue - if not used, popup menu does not work in versioning frame
        VCSCommandsOutputOperator voo = new VCSCommandsOutputOperator ("CHECKOUT_COMMAND");
        voo.close(); 
        voo.waitClosed();
        
        waitStatus(null, nInitDir);
        new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSRefreshRecursively();
        assertTrue("Refresh recursively folder command failed", history.waitCommand("Refresh Recursively", hRoot));
        waitStatus(null, nInitDir);
        waitStatus(null, nInitSubDir);
    }

    public void testAdd() {
        try {
            new File(fDirectory).mkdirs();
            new File(fSubDir).mkdirs(); 
            new File(fFile).createNewFile();
            new File(fSubFile).createNewFile();
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while setting test case up", e);
        }
        
        new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSRefreshRecursively();
        assertTrue("Refresh recursively folder command failed", history.waitCommand("Refresh Recursively", hRoot));
        new CVSFileNode(exp.repositoryTab().tree(), nDirectory);
        new CVSFileNode(exp.repositoryTab().tree(), nSubDir);
        new CVSFileNode(exp.repositoryTab().tree(), nFile);
        new CVSFileNode(exp.repositoryTab().tree(), nSubFile);
        waitStatus("Local", nDirectory);
        waitStatus("Local", nSubDir);
        waitStatus("Local", nFile);
        waitStatus("Local", nSubFile);
        
/* commented due to issue #27582 - failure of add command
        new CVSFileNode(exp.repositoryTab().tree(), nDirectory).cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
        add.setFileDescription("Initial state");
        add.addAllLocalFilesInFolderContents();
        add.oK();
        new NbDialogOperator("Information").ok();
        assertTrue("Add folder command failed", history.waitCommand("Add", hDirectory));
        waitStatus(null, nDirectory);
        waitStatus(null, nSubDir);
        waitStatus("Locally Added", nFile);
        waitStatus("Local", nSubFile);
*/
        // workaround for issue #27582
        new CVSFileNode(exp.repositoryTab().tree(), nDirectory).cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
        add.setFileDescription("Initial state");
        add.oK();
        new NbDialogOperator("Information").ok();
        assertTrue("Add folder command failed", history.waitCommand("Add", hDirectory));
        waitStatus(null, nDirectory);
        waitStatus("Local", nSubDir);
        waitStatus("Local", nFile);
        waitStatus("Local", nSubFile);
        new CVSFileNode(exp.repositoryTab().tree(), nFile).cVSAdd();
        CVSAddFileAdvDialog addf = new CVSAddFileAdvDialog();
        addf.setFileDescription("Initial state");
        addf.oK();
        assertTrue("Add folder command failed", history.waitCommand("Add", hFile));
        waitStatus(null, nDirectory);
        waitStatus("Local", nSubDir);
        waitStatus("Locally Added", nFile);
        waitStatus("Local", nSubFile);
    }
    
    public void testCommit () {
        waitStatus("Locally Added", nFile);
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSCommit();
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog ();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("Initial commit");
        co.oK ();
        assertTrue ("Commit file command failed", history.waitCommand("Commit", hFile));
        waitStatus("Up-to-date; 1.1", nFile, true);
    }

    public void testVersioning () {
        closeAllVersionings ();
        new CVSFileNode (exp.repositoryTab().tree (), nRoot).versioningExplorer();
        VersioningFrameOperator vfo = new VersioningFrameOperator ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile).refreshRevisions();
        assertTrue ("Refresh revisions command failed", history.waitCommand("REVISION_LIST", hFile));
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile);
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), nFile + "|1.1  Initial commit");
    }

    public void testAddTag () {
        closeAllVersionings ();
        new CVSFileNode (exp.repositoryTab().tree (), nFile).versioningExplorer();
        VersioningFrameOperator vfo = new VersioningFrameOperator ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile).cVSBranchingAndTaggingAddTag();
        CVSAddTagFileAdvDialog at = new CVSAddTagFileAdvDialog ();
        at.setTagName("Tag1");
        at.oK ();
        assertTrue ("Add tag command failed", history.waitCommand("Add Tag", hFile));
        new NbDialogOperator ("Information").ok ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile).refreshRevisions();
//        new org.netbeans.jellytools.actions.Action (null, "Refresh Revisions").performPopup (new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile));
        assertTrue ("Refresh revisions command failed", history.waitCommand("REVISION_LIST", hFile));
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), nFile + "|1.1  Initial commit");
    }
    
    public void testAddBranchTag () {
        VersioningFrameOperator vfo = new VersioningFrameOperator ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile).cVSBranchingAndTaggingAddTag();
        CVSAddTagFileAdvDialog at = new CVSAddTagFileAdvDialog ();
        at.select ();
        CVSRevisionSelectorDialog rev = new CVSRevisionSelectorDialog ();
        rev.lstCVSRevisionSelector().clickOnItem("1.1  Tag1");
        rev.oK ();
        at.setTagName("Tag2");
        at.checkBranchTag(true);
        at.oK ();
        assertTrue ("Add branch tag command failed", history.waitCommand("Add Tag", hFile));
        new NbDialogOperator ("Information").ok ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile).refreshRevisions();
        assertTrue ("Refresh revisions command failed", history.waitCommand("REVISION_LIST", hFile));
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1]");
        Node nn = new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1]|1.1  Initial commit");
        String[] strs = nn.getChildren();
        if (strs != null) {
            info.println ("--- Children Count: " + strs.length + " ---");
            for (int a = 0; a < strs.length; a ++)
                info.println (strs[a]);
        } else
            info.println ("--- No Children ---");
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1]|1.1  Initial commit").expand ();
        new CVSVersioningBranchNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1]|1.1  Initial commit|1.1.2 (Tag2)");
    }
    
    public void saveToFile (String filename, String text) throws IOException {
        FileWriter fr = new FileWriter (filename);
        fr.write(text);
        fr.close ();
    }
    
    public void testCommitToBranch () {
        try {
            saveToFile (fFile, "text 1.1.2.1\n");
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while setting test case up", e);
        }
        new CVSFileNode (exp.repositoryTab().tree (), nDirectory).cVSRefresh ();
        assertTrue ("Refresh folder command failed", history.waitCommand("Refresh", hDirectory));

        closeAllVersionings ();
        new CVSFileNode (exp.repositoryTab().tree (), nFile).versioningExplorer();
        VersioningFrameOperator vfo = new VersioningFrameOperator ();
        new CVSVersioningFileNode (vfo.treeVersioningTreeView(), nDirectory).cVSRefresh();
        assertTrue ("Refresh folder command failed", history.waitCommand("Refresh", hDirectory));
        new CVSVersioningFileNode (vfo.treeVersioningTreeView(), nFile + " [Locally Modified; 1.1]").cVSCommit();
        
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog ();
        co.select ();
        CVSBranchSelectorDialog bs = new CVSBranchSelectorDialog ();
        bs.lstCVSBranchSelector().clickOnItem ("1.1.2  Tag2");
        bs.oK ();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("Branch Commit");
        co.oK ();

        assertTrue ("Commit file command failed", history.waitCommand("Commit", hFile));
        waitStatus ("Up-to-date; 1.1.2.1", nFile, true);
        waitVersion (null, nFile);
        new CVSFileNode (exp.repositoryTab().tree (), nDirectory).cVSRefresh ();
        assertTrue ("Refresh folder command failed", history.waitCommand("Refresh", hDirectory));
        waitStatus ("Up-to-date; 1.1.2.1", nFile, true);
        waitVersion ("Tag2", nFile);

        // stabilization
        closeAllVersionings ();
        new CVSFileNode (exp.repositoryTab().tree (), nFile).versioningExplorer();
        vfo = new VersioningFrameOperator ();

        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1.2.1] (Tag2)").refreshRevisions();
        assertTrue ("Refresh revisions command failed", history.waitCommand("REVISION_LIST", hFile));
        new CVSVersioningFileNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1.2.1] (Tag2)");
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1.2.1] (Tag2)|1.1  Initial commit");
        new CVSVersioningBranchNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1.2.1] (Tag2)|1.1  Initial commit|1.1.2 (Tag2)");
        new CVSVersioningVersionNode (vfo.treeVersioningTreeView (), nFile + " [Up-to-date; 1.1.2.1] (Tag2)|1.1  Initial commit|1.1.2 (Tag2)|1.1.2.1  Branch Commit");
    }
    
    public void testUpdateHead () {
        new CVSFileNode (exp.repositoryTab().tree (), nFile);
        waitStatus ("Up-to-date; 1.1.2.1", nFile, true);
        waitVersion ("Tag2", nFile);
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSUpdate ();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.select();
        CVSRevisionSelectorDialog rev = new CVSRevisionSelectorDialog ();
        rev.lstCVSRevisionSelector().clickOnItem ("HEAD");
        rev.oK ();
        up.oK ();
        assertTrue ("Update from head command failed", history.waitCommand("Update", hFile));
        waitStatus ("Up-to-date; 1.1", nFile, true);
        waitVersion ("HEAD", nFile);
    }
    
    public void testRemoveStickyTagFromHead () {
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSRefresh();
        assertTrue ("Refresh file command failed", history.waitCommand("Refresh", hFile));
        waitStatus ("Up-to-date; 1.1", nFile, true);
        waitVersion ("HEAD", nFile);
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSBranchingAndTaggingRemoveStickyTag();
        assertTrue ("Remove sticky tag command failed", history.waitCommand("Remove Sticky Tag", hFile));
        new NbDialogOperator ("Information").ok ();
        waitStatus ("Up-to-date; 1.1", nFile, true);
        waitVersion (null, nFile);
    }
    
    public void testCommitHead () {
        try {
            saveToFile (fFile, "text 1.2\n");
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while setting test case up", e);
        }
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSRefresh();
        assertTrue ("Refresh file command failed", history.waitCommand("Refresh", hFile));
        waitStatus ("Locally Modified; 1.1", nFile, true);
        waitVersion (null, nFile);
        
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSCommit();
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog ();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("HEAD Commit");
        co.oK ();
        assertTrue ("Refresh file command failed", history.waitCommand("Commit", hFile));
        waitStatus ("Up-to-date; 1.2", nFile, true);
        waitVersion (null, nFile);
    }
    
    public void testUpdateFromBranch () {
        waitStatus ("Up-to-date; 1.2", nFile, true);
        waitVersion (null, nFile);
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSUpdate();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog ();
        up.select();
        CVSRevisionSelectorDialog rev = new CVSRevisionSelectorDialog ();
        rev.lstCVSRevisionSelector().clickOnItem ("1.1.0.2  Tag2");
        rev.oK ();
        up.oK ();
        assertTrue ("Update from branch command failed", history.waitCommand ("Update", hFile));
        waitStatus ("Up-to-date; 1.1.2.1", nFile, true);
        waitVersion ("Tag2", nFile);
    }
    
    public void testRemoveStickyTagFromBranch () {
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSRefresh();
        assertTrue ("Refresh file command failed", history.waitCommand("Refresh", hFile));
        waitStatus ("Up-to-date; 1.1.2.1", nFile, true);
        waitVersion ("Tag2", nFile);
        new CVSFileNode (exp.repositoryTab().tree (), nFile).cVSBranchingAndTaggingRemoveStickyTag();
        assertTrue ("Remove sticky tag command failed", history.waitCommand("Remove Sticky Tag", hFile));
        new NbDialogOperator ("Information").ok ();
        waitStatus ("Up-to-date; 1.2", nFile, true);
        waitVersion (null, nFile);
    }
    
}
