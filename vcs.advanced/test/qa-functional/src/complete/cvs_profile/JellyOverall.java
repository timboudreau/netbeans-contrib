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
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NbFrameOperator;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSAddAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.CVSCommitAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.VCSGenericMountAction;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSFileNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.FilesystemHistoryNode;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardAdvanced;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardProfile;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileStatusListener;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Utilities;
import util.Helper;
import util.History;
import util.StatusBarTracer;


public class JellyOverall extends JellyTestCase {
    
    public JellyOverall(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyOverall("testWorkDir"));
        suite.addTest(new JellyOverall("testMount"));
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
    
    ExplorerOperator exp;
    StatusBarTracer sbt;
    static FileSystem fs;
    static FileStatusListener fsFSlistener;
    static PrintStream fsFSlog;
    static String serverDirectory;
    static String clientDirectory;
    static String hRoot = ".", nRoot;
    static String tInitDir = "initdir", hInitDir = tInitDir, fInitDir, nInitDir;
    static String tInitSubDir = "initsubdir", hInitSubDir = hInitDir + "/" + tInitSubDir, fInitSubDir, nInitSubDir;
    static String tDirectory = "directory", hDirectory = tDirectory, fDirectory, nDirectory;
    static String tFile = "file", fFile, nFile;
    static String tSubDir = "subdir", fSubDir, nSubDir;
    static String tSubFile = "subfile", fSubFile, nSubFile;
    static String tText1 = "text1", hText1 = hInitDir + "/" + tText1, fText1, nText1;
    static String tText2 = "text2", hText2 = hInitDir + "/" + tText2, fText2, nText2;
    static String tBinary = "binary", hBinary = hInitSubDir + "/" + tBinary, fBinary, nBinary;
    
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
        assertTrue("waitStatus(status,node) precondition failed", status == null  ||  status.indexOf(';') < 0);
        waitStatus(status, node, false);
    }
    
    public void waitStatus(String status, String node, boolean withversion) {
        String ano = null;
        for (int a = 0; a < 60; a ++) {
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
    
    boolean equalPaths(String p1, String p2) {
        p1 = p1.replace('\\', '/');
        p2 = p2.replace('\\', '/');
        return p1.equalsIgnoreCase(p2);
    }
    
    public static void traversFileStatusLogging (FileObject fo, FileStatusEvent e) {
        if (fo == null)
            return;
        if (e.hasChanged(fo)) {
            String str = fo.getName();
            try {
                DataObject dao = DataObject.find (fo);
                if (dao != null) {
                    org.openide.nodes.Node node = dao.getNodeDelegate();
                    if (node != null)
                        str = node.getDisplayName();
                }
            } catch (DataObjectNotFoundException ee) {
            }
            fsFSlog.println (str);
        }
        FileObject[] fos = fo.getChildren();
        if (fos == null)
            return;
        for (int a = 0; a < fos.length; a ++) {
            traversFileStatusLogging (fos[a], e);
        }
    }
    
    public static void startFileStatusLogging(final PrintStream log) {
        stopFileStatusLogging();
        if (fs == null)
            return;
        fsFSlog = log;
        fsFSlistener = new FileStatusListener() {
            public void annotationChanged (FileStatusEvent e) {
                fsFSlog.println ("==== IsNameChange: " + e.isNameChange() + ", IsIconChange: " + e.isIconChange());
                traversFileStatusLogging (fs.getRoot(), e);
            }
        };
        fs.addFileStatusListener(fsFSlistener);
    }
    
    public static void stopFileStatusLogging() {
        if (fs == null)
            return;
        fs.removeFileStatusListener(fsFSlistener);
    }
    
    protected void setUp() {
        exp = new ExplorerOperator();
        sbt = new StatusBarTracer();
        out = getRef();
        info = getLog();
        startFileStatusLogging(getLog("filestatus"));
    }
    
    protected void tearDown() {
        stopFileStatusLogging();
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
        if (Utilities.isUnix()) {
            serverDirectory = serverDirectory.replace('\\', '/');
            clientDirectory = clientDirectory.replace('\\', '/');
        } else {
            serverDirectory = serverDirectory.replace('/', '\\');
            clientDirectory = clientDirectory.replace('/', '\\');
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
        
        nRoot = "CVS " + clientDirectory;
        boolean found = false;
        info.println("Searching for CVS filesystem: " + nRoot);
        for (int a = 0; a < 10; a ++) {
            Enumeration e = Repository.getDefault().getFileSystems();
            while (e.hasMoreElements()) {
                FileSystem f = (FileSystem) e.nextElement();
                info.println("Is it: " + f.getDisplayName());
                if (equalPaths(f.getDisplayName(), nRoot)) {
                    info.println("Yes");
                    nRoot = f.getDisplayName();
                    fs = f;
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
        getLog().println(co.cbPruneEmptyFolders().isSelected());
        co.oK();
        assertTrue("Check Out command failed", history.waitCommand("Check Out", hRoot));
        
        // for assurance only
        VCSCommandsOutputOperator voo = new VCSCommandsOutputOperator("CHECKOUT_COMMAND");
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
        //waitStatus("Local", nDirectory);// may fail due to #28177
        waitStatus("Local", nSubDir);
        waitStatus("Local", nFile);
        waitStatus("Local", nSubFile);
        
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
    }
    
    public void testAddRecursive() {
        waitStatus(null, nDirectory);
        new CVSFileNode(exp.repositoryTab().tree(), nDirectory).cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog();
        add.setFileDescription("Initial state");
        add.addAllLocalFilesInFolderContents();
        add.checkAddTheFolderContentsRecursively(true);
        add.oK();
        assertTrue("Add folder recursive command failed", history.waitCommand("Add", hDirectory));
        new CVSFileNode(exp.repositoryTab().tree(), nDirectory);
        new CVSFileNode(exp.repositoryTab().tree(), nSubDir);
        new CVSFileNode(exp.repositoryTab().tree(), nFile);
        new CVSFileNode(exp.repositoryTab().tree(), nSubFile);
        waitStatus(null, nDirectory);
        waitStatus(null, nSubDir);
        waitStatus("Locally Added", nFile);
        waitStatus("Locally Added", nSubFile);
        new NbDialogOperator("Information").ok();
    }
    
    public void testAddTextFile() {
        try {
            new File(fText1).createNewFile();
            new File(fText2).createNewFile();
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while creating text file", e);
        }
        new CVSFileNode(exp.repositoryTab().tree(), nInitDir).cVSRefresh();
        assertTrue("Refresh directory command failed", history.waitCommand("Refresh", hInitDir));
        new CVSFileNode(exp.repositoryTab().tree(), nText1);
        new CVSFileNode(exp.repositoryTab().tree(), nText2);
        waitStatus("Local", nText1);
        waitStatus("Local", nText2);
        new CVSAddAction().perform(new Node [] {
            new CVSFileNode(exp.repositoryTab().tree(), nText1),
            new CVSFileNode(exp.repositoryTab().tree(), nText2),
        });
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog();
        add.setFileDescription("Initial state");
        add.oK();
        assertTrue("Add text files command failed", history.waitCommand("Add", hText1 + "\n" + hText2));
        waitStatus("Locally Added", nText1);
        waitStatus("Locally Added", nText2);
    }
    
    public void testAddBinaryFile() {
        try {
            new File(fBinary).createNewFile();
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while creating binary file", e);
        }
        new CVSFileNode(exp.repositoryTab().tree(), nInitSubDir).cVSRefresh();
        assertTrue("Refresh directory command failed", history.waitCommand("Refresh", hInitSubDir));
        new CVSFileNode(exp.repositoryTab().tree(), nBinary);
        waitStatus("Local", nBinary);
        new CVSFileNode(exp.repositoryTab().tree(), nBinary).cVSAdd();
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog();
        add.setFileDescription("Initial state");
        add.setKeywordSubstitution(CVSAddFileAdvDialog.ITEM_BINARYKB);
        add.oK();
        assertTrue("Add file command failed", history.waitCommand("Add", hBinary));
        waitStatus("Locally Added", nBinary);
    }
    
    public void testCommitFiles() {
        new CVSFileNode(exp.repositoryTab().tree(), nText1);
        new CVSFileNode(exp.repositoryTab().tree(), nText2);
        new CVSFileNode(exp.repositoryTab().tree(), nBinary);
        waitStatus("Locally Added", nText1);
        waitStatus("Locally Added", nText2);
        waitStatus("Locally Added", nBinary);
        new CVSCommitAction().perform(new Node[] {
            new CVSFileNode(exp.repositoryTab().tree(), nText1),
            new CVSFileNode(exp.repositoryTab().tree(), nText2),
            new CVSFileNode(exp.repositoryTab().tree(), nBinary),
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
        assertTrue("Commit files command failed", history.waitCommand("Commit", hText1 + "\n" + hText2 + "\n" + hBinary));
        waitStatus("Up-to-date; 1.1", nText1, true);
        waitStatus("Up-to-date; 1.1", nText2, true);
        waitStatus("Up-to-date; 1.1", nBinary, true);
    }
    
    public void modifyFile(String filename) throws IOException {
        FileWriter fr = new FileWriter(filename);
        fr.write("Text\n");
        fr.close();
    }
    
    public void testRefreshFile() {
        new CVSFileNode(exp.repositoryTab().tree(), nText2);
        waitStatus("Up-to-date; 1.1", nText2, true);
        try {
            modifyFile(fText2);
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while modifying text2 file", e);
        }
        new CVSFileNode(exp.repositoryTab().tree(), nText2).cVSRefresh();
        assertTrue("Refresh files command failed", history.waitCommand("Refresh", hText2));
        waitStatus("Locally Modified; 1.1", nText2, true);
    }
    
    public void testRefreshDirectory() {
        new CVSFileNode(exp.repositoryTab().tree(), nRoot).cVSRefreshRecursively();
        assertTrue("Refresh files command failed", history.waitCommand("Refresh Recursively", hRoot));
        new CVSFileNode(exp.repositoryTab().tree(), nText1);
        new CVSFileNode(exp.repositoryTab().tree(), nBinary);
        waitStatus(null, nInitDir);
        waitStatus(null, nInitSubDir);
        waitStatus("Up-to-date; 1.1", nText1, true);
        waitStatus("Up-to-date; 1.1", nBinary, true);
        
        try {
            modifyFile(fText1);
            modifyFile(fBinary);
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while modifying text1 and binary files", e);
        }
        
        new CVSFileNode(exp.repositoryTab().tree(), nInitDir).cVSRefresh();
        assertTrue("Refresh directory command failed", history.waitCommand("Refresh", hInitDir));
        waitStatus(null, nInitDir);
        waitStatus(null, nInitSubDir);
        waitStatus("Locally Modified; 1.1", nText1, true);
        waitStatus("Up-to-date; 1.1", nBinary, true);
    }
    
    public void testRefreshRecursively() {
        try {
            modifyFile(fText1);
            modifyFile(fBinary);
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while modifying text1 and binary files", e);
        }
        
        new CVSFileNode(exp.repositoryTab().tree(), nInitDir).cVSRefreshRecursively();
        assertTrue("Refresh recursively files command failed", history.waitCommand("Refresh Recursively", hInitDir));
        waitStatus(null, nInitDir);
        waitStatus(null, nInitSubDir);
        waitStatus("Locally Modified; 1.1", nText1, true);
        waitStatus("Locally Modified; 1.1", nBinary, true);
    }
    
    public void testAddTag() {
        waitStatus("Locally Modified; 1.1", nText2, true);
        new CVSFileNode(exp.repositoryTab().tree(), nText2).cVSBranchingAndTaggingAddTag();
        CVSAddTagFileAdvDialog add = new CVSAddTagFileAdvDialog();
        add.checkBranchTag(true);
        add.setTagName("MyTag");
        add.select();
        CVSRevisionSelectorDialog rev = new CVSRevisionSelectorDialog();
        rev.lstCVSRevisionSelector().clickOnItem("1.1");
        rev.oK();
        add.oK();
        assertTrue("Add Tag command failed", history.waitCommand("Add Tag", hText2));
        new NbDialogOperator("Information").ok();
    }
    
    public void testCommitToBranch() {
        waitStatus("Locally Modified; 1.1", nText2, true);
        new CVSFileNode(exp.repositoryTab().tree(), nText2).cVSCommit();
        CVSCommitFileAdvDialog co = new CVSCommitFileAdvDialog();
        co.select();
        CVSBranchSelectorDialog br = new CVSBranchSelectorDialog();
        br.lstCVSBranchSelector().clickOnItem("1.1.2  MyTag");
        br.oK();
        co.txtEnterReason().setCaretPosition(0);
        co.txtEnterReason().typeText("Initial commit to branch");
        co.oK();
        assertTrue("Commit file command failed", history.waitCommand("Commit", hText2));
        waitStatus("Up-to-date; 1.1.2.1", nText2, true);
    }
    
    public void testUpdate() {
        new DeleteAction().perform(new Node(exp.repositoryTab().tree(), nBinary));
        new NbDialogOperator("Confirm Object Deletion").yes();
        // workaround for issue #27589
        new CVSFileNode(exp.repositoryTab().tree(), nInitSubDir).cVSRefresh();
        assertTrue("Refresh directory command failed", history.waitCommand("Refresh", hInitSubDir));
        //
        waitStatus("Needs Update; 1.1", nBinary, true);
        new CVSFileNode(exp.repositoryTab().tree(), nBinary).cVSUpdate();
        CVSUpdateFileAdvDialog up = new CVSUpdateFileAdvDialog();
        up.oK();
        waitStatus("Up-to-date; 1.1", nBinary, true);
    }
    
}
