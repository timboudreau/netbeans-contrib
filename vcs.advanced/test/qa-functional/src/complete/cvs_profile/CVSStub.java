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

import complete.GenericStub;
import complete.GenericStub.GenericNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.VCSGenericMountAction;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardAdvanced;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardProfile;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.modules.vcs.advanced.wizard.mount.MountWizardData;
import org.netbeans.modules.vcs.advanced.wizard.mount.MountWizardIterator;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.*;
import org.openide.filesystems.FileSystem;
import org.openide.util.Utilities;
import util.History;



public abstract class CVSStub extends GenericStub {
    
    public CVSStub(String testName) {
        super(testName);
        nRootPrefix = "CVS ";
    }
    
    static FileSystem sfs = null;
    
    protected FileSystem getFileSystem () {
        return sfs;
    }
    
    protected void setFileSystem (FileSystem fs) {
        sfs = fs;
    }
  
    protected void prepareServer (String dir) {
    }
    
    protected void mountVCSFileSystem () {
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
    }
        
    protected void prepareClient () {
        // init
        root.cvsNode ().cVSInit ();
        root.waitHistory("Init");

        // checkout
        root.cvsNode ().cVSCheckOut();
        CVSCheckoutFolderAdvDialog co = new CVSCheckoutFolderAdvDialog();
        co.setModuleS(hRoot);
        co.checkPruneEmptyFolders(false);
        co.oK();
        root.waitHistory("Check Out");

        // workaround - probably jelly issue - if not used, popup menu does not work in versioning frame
        VCSCommandsOutputOperator voo = new VCSCommandsOutputOperator ("CHECKOUT_COMMAND");
        closeAllVCSOutputs();
    }
    
    protected void refresh (GenericNode node) {
        node.cvsNode ().cVSRefresh ();
        assertTrue("Refresh command failed", history.waitCommand("Refresh", node.history ()));
    }

    protected void refreshRecursively (GenericNode node) {
        node.cvsNode ().cVSRefreshRecursively();
        assertTrue("Refresh recursively command failed", history.waitCommand("Refresh Recursively", node.history ()));
    }

    protected void addDirectory (GenericNode node) {
        node.cvsNode ().cVSAdd();
        CVSAddFolderAdvDialog add = new CVSAddFolderAdvDialog ();
        add.oK ();
        add.waitClosed();
        assertInformationDialog(null);
        assertTrue("Add folder command failed", history.waitCommand("Add", node.history ()));
        node.waitStatus (null);
    }

    protected void addFile (GenericNode node, String desc) {
        node.cvsNode ().cVSAdd();
        CVSAddFileAdvDialog add = new CVSAddFileAdvDialog ();
        if (desc != null) {
            add.txtFileDescription().setCaretPosition(0);
            add.txtFileDescription().typeText(desc);
        }
        add.oK ();
        add.waitClosed();
        assertTrue("Add file command failed", history.waitCommand("Add", node.history ()));
        node.waitStatus ("Locally Added", false);
    }

    protected void commitFile (GenericNode node, String branch, String desc) {
        node.cvsNode ().cVSCommit();
        CVSCommitFileAdvDialog com = new CVSCommitFileAdvDialog ();
        if (branch != null)
            com.setCommitToBranch(branch);
        if (desc != null) {
            com.txtEnterReason().setCaretPosition(0);
            com.txtEnterReason().typeText(desc);
        }
        com.oK ();
        com.waitClosed();
        assertTrue("Commit file command failed", history.waitCommand("Commit", node.history ()));
        node.waitStatus ("Up-to-date", false);
    }
    
    protected void addTagFile (GenericNode node, String name, boolean branch) {
        node.cvsNode().cVSBranchingAndTaggingAddTag();
        CVSAddTagFileAdvDialog add = new CVSAddTagFileAdvDialog ();
        add.checkBranchTag(branch);
        add.setTagName (name);
        add.oK ();
        add.waitClosed ();
        assertInformationDialog(null);
        assertTrue("Add tag file command failed", history.waitCommand("Add Tag", node.history ()));
    }
    
}
