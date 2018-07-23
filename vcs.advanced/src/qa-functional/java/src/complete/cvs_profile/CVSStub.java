/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package complete.cvs_profile;

import complete.GenericStub;
import complete.GenericStub.GenericNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JTabbedPane;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.CustomizeAction;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.VCSFilesystemCustomizerDialog;
import org.netbeans.jellytools.modules.vcsgeneric.actions.VCSGenericMountAction;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.GenericVCSMountCVSProfileWizard;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.GenericVCSMountWizard;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardAdvanced;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardProfile;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.modules.vcs.advanced.wizard.mount.MountWizardData;
import org.netbeans.modules.vcs.advanced.wizard.mount.MountWizardIterator;
import org.netbeans.jellytools.modules.vcsgeneric.cvs_profile.*;
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
/*        new Action ("Versioning|Mount Version Control", null).performMenu (); // workaround for issue #31026
        new Action ("Tools", null).performMenu (); // workaround for issue #31026
        sleep (10000);
        new Action ("Versioning|Mount Version Control", null).performMenu (); // workaround for issue #31026
        new Action ("Tools", null).performMenu (); // workaround for issue #31026*/
        new VCSGenericMountAction().perform();
        GenericVCSMountWizard genericwizard = new GenericVCSMountWizard();
        genericwizard.verify();
        genericwizard.setWorkingDirectory("");
        genericwizard.txtWorkingDirectory().typeText (clientDirectory);

        String profile = "CVS";

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
        history = new History (fs, info);
        history.breakpoint();

        genericwizard.cboProfileComboBox().setVerification(false);
        genericwizard.selectProfileComboBox(profile);
        genericwizard.cboProfileComboBox().setVerification(true);
        history.waitCommand("AUTO_FILL_CONFIG", "");
        sleep (5000);

        GenericVCSMountCVSProfileWizard wizard = new GenericVCSMountCVSProfileWizard ();
        wizard.verify ();

        // stabilization
        wizard.setWorkingDirectory("");
        wizard.txtWorkingDirectory().typeText (clientDirectory);
        
        wizard.setCVSServerName(""); // workaround for issue #37380
        wizard.setUserName(""); // workaround for issue #37380
        wizard.selectCVSServerType("local");
        wizard.setRepositoryPath ("");
        wizard.txtRepositoryPath ().typeText (serverDirectory);
        wizard.useCommandLineCVSClient(); // workaround for issue #
        wizard.finish();
        
        closeAllProperties();
    }
        
    protected void prepareClient () {
        // advanced mode
        new CustomizeAction ().performPopup (root.cvsNode ());
        VCSFilesystemCustomizerDialog dia = new VCSFilesystemCustomizerDialog ();
        dia.checkAdvancedMode(true);
        dia.close ();
        dia.waitClosed ();

        // init
        root.cvsNode ().cVSInit ();
        history.getWaitCommand("Init", root.history ());

        // checkout
        root.cvsNode ().cVSCheckOut();
        CVSCheckoutFolderAdvDialog co = new CVSCheckoutFolderAdvDialog();
        co.setModuleS(hRoot);
        co.checkPruneEmptyFolders(false);
        co.oK();
        root.waitHistory("Check Out");

        // workaround - probably jelly issue - if not used, popup menu does not work in versioning frame
        // !!! error - commented because of issue #37837
        // VCSCommandsOutputOperator voo = new VCSCommandsOutputOperator ("Check Out");
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
//        assertInformationDialog(null);
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
