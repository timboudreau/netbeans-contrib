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

package org.netbeans.modules.vcscore.grouping;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;
import java.io.*;
import org.openide.DialogDisplayer;

/** Action sensitive to the node selection that does something useful.
 *
 * @author  builder
 */
public class AddVcsGroupAction extends NodeAction implements Runnable {

    private static final long serialVersionUID = -3385132838696775732L;
    
    private String newName;
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction (Node[] nodes) {
        // do work based on the current node selection, e.g.:
        NotifyDescriptor.InputLine line = new NotifyDescriptor.InputLine( 
             NbBundle.getBundle(AddVcsGroupAction.class).getString("AddVcsGroupAction.groupName"), //NOI18N
             NbBundle.getBundle(AddVcsGroupAction.class).getString("AddVcsGroupAction.groupNameTitle"));//NOI18N
        Object retValue = DialogDisplayer.getDefault().notify(line);
        if (!retValue.equals(DialogDescriptor.OK_OPTION)) return;
        
        this.newName = line.getInputText();
        org.openide.util.RequestProcessor.getDefault().post(this);
    }

    /**
     * Perform the actual addition.
     */
    public void run() {
        if (newName == null) return ;
        DataFolder rootFolder = GroupUtils.getMainVcsGroupFolder();
        if (rootFolder != null) {
            FileObject fo = rootFolder.getPrimaryFile();
            String foldName = FileUtil.findFreeFolderName(fo, "group");//NOI18N
            FileObject props = fo.getFileObject(foldName, VcsGroupNode.PROPFILE_EXT);
            PrintWriter writer = null;
            FileLock lock = null;
            try {
                if (props == null) {
                    props = fo.createData(foldName, VcsGroupNode.PROPFILE_EXT);
                }
                lock = props.lock();
                writer = new PrintWriter(props.getOutputStream(lock));
                writer.println(VcsGroupNode.PROP_NAME + "=" + newName);//NOI18N
                FileObject group = rootFolder.getPrimaryFile().createFolder(foldName);
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            } finally {
                if (writer != null) {
                    writer.close();
                }
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
             
        // ...
    }
    
    protected boolean enable (Node[] nodes) {
        // e.g.:
        return true;
    }

    public String getName () {
        return NbBundle.getMessage(AddVcsGroupAction.class, "LBL_AddVcsGroupAction");//NOI18N
    }

    protected String iconResource () {
        return "org/netbeans/modules/vcscore/grouping/AddVcsGroupActionIcon.gif";//NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (AddVcsGroupAction.class);
    }

    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
    protected void initialize () {
	super.initialize ();
     * putProperty (Action.SHORT_DESCRIPTION, NbBundle.getMessage (AddVcsGroupAction.class, "HINT_Action"));
    }
    */

}
