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

package complete.pvcs_profile;

import complete.GenericStub;
import complete.GenericStub.GenericNode;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.ListModel;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.modules.vcsgeneric.actions.VCSGenericMountAction;
import org.netbeans.jellytools.modules.vcsgeneric.pvcs.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardAdvanced;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.VCSWizardProfile;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.modules.vcs.advanced.wizard.mount.MountWizardData;
import org.netbeans.modules.vcs.advanced.wizard.mount.MountWizardIterator;
import org.openide.filesystems.FileSystem;
import org.openide.util.Utilities;
import util.History;


public abstract class PVCSStub extends GenericStub {
    
    public PVCSStub(String testName) {
        super(testName);
        nRootPrefix = "PVCS ";
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

        String profile = Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT;
        int os = Utilities.getOperatingSystem();
        if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
            profile = VCSWizardProfile.PVCS_WIN_95;
//        history.print ();
        wizard.setProfile(profile);
        waitCommand ("AUTO_FILL_CONFIG", "");
//        history.print ();

        JTextFieldOperator txt = new JTextFieldOperator(wizard, VCSWizardProfile.INDEX_TXT_PVCS_PROJECT_DATABASE);
        txt.setText("");
        txt.typeText(serverDirectory);
        txt = new JTextFieldOperator(wizard, VCSWizardProfile.INDEX_TXT_PVCS_WORKFILES_LOCATION);
//        history.print ();
        txt.requestFocus();

        waitCommand ("AUTO_FILL_CONFIG", "");
//        history.print ();

        txt.setText ("");
        txt.typeText (clientDirectory);

        wizard.next();
        VCSWizardAdvanced wizard2 = new VCSWizardAdvanced();
        wizard2.checkAdvancedMode(true);
        wizard2.finish();
    }
    
    protected void prepareClient () {
        history.waitCommand("Refresh", root.history ());
        root.pvcsNode().pVCSCreateProjectDatabase();
        root.waitHistory("Create Project Database");
    }
    
    protected void refresh (GenericNode node) {
        node.pvcsNode ().pVCSRefresh ();
        node.waitHistory ("Refresh");
    }
    
    protected void refreshFile (GenericNode node) {
        node.pvcsNode ().pVCSRefresh ();
        node.parent ().waitHistory ("Refresh");
    }
    
    protected void createProject (GenericNode node) {
        node.pvcsNode ().pVCSCreateProject ();
        node.waitHistory ("Create Project");
        node.parent ().waitHistory ("Refresh");
        node.waitStatus ("Current");
    }
    
    protected void addFile (GenericNode node, String desc) {
        node.pvcsNode ().pVCSAdd();
        AddCommandOperator add = new AddCommandOperator ("");
        add.setWorkfileDescription(desc);
        add.setChangeDescription("Initial state");
        add.checkKeepTheRevisionLocked(true);
        add.ok ();
        add.waitClosed();
        node.waitHistory ("Add");
        node.parent ().waitHistory ("Refresh");
    }
    
    protected void addFileLabel (GenericNode node, String label, String desc) {
        node.pvcsNode ().pVCSAdd();
        AddCommandOperator add = new AddCommandOperator ("");
        add.setWorkfileDescription(desc);
        add.setChangeDescription("Initial state");
        add.setAssignAVersionLabel(label);
        add.checkKeepTheRevisionLocked(true);
        add.ok ();
        add.waitClosed();
        node.waitHistory ("Add");
        node.parent ().waitHistory ("Refresh");
    }
    
    protected void getFile (GenericNode node, String revision, String label) {
        node.pvcsNode().pVCSGet ();
        GetCommandOperator get = new GetCommandOperator ("");
        if (revision != null)
            get.setSpecificRevision(revision);
        if (label != null)
            get.setVersionLabel(label);
        get.checkLockForTheCurrentUser(true);
        get.ok ();
        get.waitClosed ();
        node.waitHistory("Get");
        node.parent ().waitHistory("Refresh");
    }
    
    protected void putFileVersion (GenericNode node, String version, String desc) {
        node.pvcsNode ().pVCSPut();
        PutCommandOperator put = new PutCommandOperator ("");
        if (desc != null)
            put.setChangeDescription(desc);
        put.checkCheckInTheWorkfileEvenIfUnchanged(true);
        put.setAssignARevisionNumber(version);
        put.checkApplyALockOnCheckout(true);
        put.ok ();
        put.waitClosed();
    }
    
    protected void putFileBranch (GenericNode node, String branch, String desc) {
        node.pvcsNode ().pVCSPut();
        PutCommandOperator put = new PutCommandOperator ("");
        if (desc != null)
            put.setChangeDescription(desc);
        put.checkCheckInTheWorkfileEvenIfUnchanged(true);
        put.setAssignAVersionLabel(branch);
        put.checkFloatLabelWithTheTipRevision(true);
        put.checkStartABranch(true);
        put.checkApplyALockOnCheckout(true);
        put.ok ();
        put.waitClosed();
    }
    
    protected void putFileLabel (GenericNode node, String label, String desc) {
        node.pvcsNode ().pVCSPut();
        PutCommandOperator put = new PutCommandOperator ("");
        if (desc != null)
            put.setChangeDescription(desc);
        put.checkCheckInTheWorkfileEvenIfUnchanged(true);
        put.setAssignAVersionLabel(label);
        put.checkApplyALockOnCheckout(true);
        put.ok ();
        put.waitClosed();
    }
    
    protected void lockFile (GenericNode node, String revision, String label) {
        node.pvcsNode().pVCSLock();
        LockCommandOperator lock = new LockCommandOperator ("");
        if (revision != null) {
            lock.specificRevisionS();
            lock.setSpecificRevisionS(revision);
        }
        if (label != null) {
            lock.revisionsIdentifiedByVersionLabelS();
            lock.setRevisionsIdentifiedByVersionLabelS(label);
        }
        lock.ok ();
        lock.waitClosed ();
    }

    protected void unlockFile (GenericNode node, String revision, String label, String user) {
        node.pvcsNode().pVCSUnlock();
        UnlockCommandOperator unlock = new UnlockCommandOperator ("");
        if (revision != null) {
            unlock.specificRevisionS();
            unlock.setSpecificRevisionS(revision);
        }
        if (label != null) {
            unlock.revisionsIdentifiedByVersionLabelS();
            unlock.setRevisionsIdentifiedByVersionLabelS(label);
        }
        if (user != null) {
            unlock.allLocksByUser();
            unlock.setAllLocksByUser(user);
        }
        unlock.ok ();
        unlock.waitClosed ();
    }
    
    protected void deleteFile (GenericNode node) {
        new DeleteAction ().perform (new Node (exp.repositoryTab().tree (), node.node ()));
        NbDialogOperator dia = new NbDialogOperator ("Confirm Object Deletion");
        dia.yes ();
        dia.waitClosed ();
        node.waitStatus("Missing", false);
    }

    protected void assertRetrievingDialog () {
        NbDialogOperator dia = new NbDialogOperator ("Retrieving...");
        for (int a = 60; a >= 0; a --) {
            try { Thread.sleep (1000); } catch (InterruptedException e) {}
            if (JButtonOperator.findJButton ((Container) dia.getSource(), "Close", true, true) != null) {
                info.println ("==== Retrieving Dialog text ====");
                JListOperator list = new JListOperator (dia);
                ListModel lm = list.getModel();
                for (int b = 0; b < lm.getSize(); b ++)
                    info.println (lm.getElementAt(b));
                info.println ("==== End ====");
                new JButtonOperator (dia, "Close").push ();
                return;
            }
        }
        assertTrue ("Timeout: Wait until retrieving finished", false);
    }
    
}
