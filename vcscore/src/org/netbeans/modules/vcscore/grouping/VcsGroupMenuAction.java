/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.grouping;

import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.explorer.*;
import org.openide.nodes.Node;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.explorer.view.*;
import org.openide.DialogDescriptor;
import org.openide.windows.*;
import org.openide.TopManager;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;
import org.openide.awt.JMenuPlus;
import javax.swing.JMenu;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



/** The action that enables access to cvs from tools menu.
*
* @author Milos Kleint
*/
public class VcsGroupMenuAction extends CallableSystemAction  {
    private transient String MODE_NAME = "VcsGroupsMode";//NOI18N
        
    private static final long serialVersionUID = 38657723580032415L;
    
    /** Creates new CvsMenuAction */
    public VcsGroupMenuAction() {
//        System.out.println("Creating CvsMenu action..."); //NOI18N
    }

    /** Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName () {
        return NbBundle.getBundle (VcsGroupMenuAction.class).
               getString ("LBL_VcsGroupMenuAction");//NOI18N
    }

    /** Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx () {
        return new HelpCtx(VcsGroupMenuAction.class);
    }
    
    public boolean isEnabled() {
        VcsGroupSettings settings = (VcsGroupSettings)SharedClassObject.findObject(VcsGroupSettings.class, true);
        return !settings.isDisableGroups();
    }
    

    /** The action's icon location.
    * @return the action's icon location
    */
    protected String iconResource () {
        return "org/netbeans/modules/vcscore/grouping/MainVcsGroupNodeIcon.gif"; // NOI18N
    }

    /** Opens packaging view. */
    public void performAction () {
    }

    public void actionPerformed(java.awt.event.ActionEvent e){    
        //        System.out.println("Performing cvs command.. :)");
        Node root = null;
        root = GroupUtils.getMainVcsGroupNodeInstance();
/*        FileSystem defFs = TopManager.getDefault().getRepository().getDefaultFileSystem();
        FileObject fo = defFs.findResource(MainVcsGroupNode.GROUPS_PATH + "/org-netbeans-modules-vcscore-grouping-MainVcsGroupNode.instance");
        if (fo != null) {
            DataObject dobj;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException exc) {
                dobj = null;
            }
            if (dobj != null && dobj instanceof InstanceDataObject) {
               InstanceDataObject ido = (InstanceDataObject)dobj;
               InstanceCookie cook = (InstanceCookie)ido.getCookie(InstanceCookie.class);
               root = ido.getNodeDelegate();
            }
        } 
        if (root == null) {
            return;
        }        
 */
        ExplorerPanel panel = null;
        Iterator it = TopManager.getDefault().getWindowManager().getRegistry().getOpened().iterator();
        while (it.hasNext()) {
            TopComponent comp = (TopComponent)it.next();
            if (comp instanceof GroupExplorerPanel) {
                panel = (GroupExplorerPanel)comp;
            }
        }
        
        if (panel == null) {
            panel = new GroupExplorerPanel();
            Workspace workspace = TopManager.getDefault().getWindowManager().getCurrentWorkspace();
            String modeName = org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "LBL_MODE.title");//NOI18N
            Mode myMode = workspace.findMode(MODE_NAME);
            
            if (myMode == null) {
                // create new mode for CI and set the bounds properly
                myMode = workspace.createMode(MODE_NAME, modeName, null); //NOI18N
                /*
                Rectangle workingSpace = workspace.getBounds();
                myMode.setBounds(new Rectangle(workingSpace.x +(workingSpace.width * 3 / 10), workingSpace.y,
                                               workingSpace.width * 2 / 10, workingSpace.height / 2));
                 */
            }
            myMode.dockInto(panel);
        }
        panel.getAccessibleContext().setAccessibleDescription(
            org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "ACSD_AddVcsGroupAction.dialog"));
        panel.setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/grouping/MainVcsGroupNodeIcon.gif"));
        panel.open(TopManager.getDefault().getWindowManager().getCurrentWorkspace());
        panel.requestFocus();
        
    }
    

    public static class GroupExplorerPanel extends ExplorerPanel {
        
        private static final long serialVersionUID = 7160066451512137154L;
        
        public void open() {
            if (!isOpened()) {
                Node root = null;
                String modeName = org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "LBL_MODE.title");//NOI18N
                setName(modeName);
                root = GroupUtils.getMainVcsGroupNodeInstance();
                GroupUtils.getDefaultGroupInstance(); // here just to make sure the default is created.
                ExplorerManager manager = getExplorerManager();
                
                manager.setRootContext(root);
                BeanTreeView cvsBeanTreeView = new BeanTreeView();
                initAccessibilityBTV(cvsBeanTreeView);
                add(cvsBeanTreeView);
                ExplorerActions actions = new ExplorerActions();
                actions.attach(manager);
            }
            super.open();
        }
        
        public void open(org.openide.windows.Workspace workspace) {
            if (!isOpened()) {
                Node root = null;
                String modeName = org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "LBL_MODE.title");//NOI18N
                setName(modeName);

                root = GroupUtils.getMainVcsGroupNodeInstance();
                GroupUtils.getDefaultGroupInstance(); // here just to make sure the default is created.
                ExplorerManager manager = getExplorerManager();
                
                manager.setRootContext(root);
                BeanTreeView cvsBeanTreeView = new BeanTreeView();
                initAccessibilityBTV(cvsBeanTreeView);
                add(cvsBeanTreeView);
                ExplorerActions actions = new ExplorerActions();
                actions.attach(manager);
            }
            super.open(workspace);
        }
        
        private void initAccessibilityBTV(BeanTreeView BTV) {
            BTV.getAccessibleContext().setAccessibleName(
                org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "ACSN_AddVcsGroupAction.BTV"));
            BTV.getAccessibleContext().setAccessibleDescription(
                org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "ACSD_AddVcsGroupAction.BTV"));
        }
        
        protected void updateTitle() {
//            super.updateTitle();
        }
        
        
    }
    
}

