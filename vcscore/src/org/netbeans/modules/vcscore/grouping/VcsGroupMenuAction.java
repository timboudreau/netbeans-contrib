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
import org.openide.windows.WindowManager;



/** The action that enables access to cvs from tools menu.
*
* @author Milos Kleint
*/
public class VcsGroupMenuAction extends CallableSystemAction  {

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

    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    /** Opens packaging view. */
    public void performAction () {
    }

    // This is going to be called in AWT thread.
    public void actionPerformed(java.awt.event.ActionEvent e) {
        TopComponent panel = GroupExplorerPanel.getDefault();
        panel.open();
        panel.requestActive();
    }
    

    public static class GroupExplorerPanel extends ExplorerPanel {
        
        private static final long serialVersionUID = 7160066451512137154L;
        
        private static GroupExplorerPanel component;
        
        public GroupExplorerPanel() {
            initComponent();
            getAccessibleContext().setAccessibleDescription(
                org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "ACSD_AddVcsGroupAction.dialog"));
            setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/grouping/MainVcsGroupNodeIcon.gif"));
        }
        
        public static synchronized GroupExplorerPanel getDefault() {
            if (component == null) {
                component = new GroupExplorerPanel();
            }
            return component;
        }
       
        protected String preferredID(){
            return "VcsGroupMenuAction_GroupExplorerPanel";
        }
        
        private void initComponent() {
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
        
        public void open() {
            Mode mode = WindowManager.getDefault().findMode("explorer"); // NOI18N
            if (mode != null) {
                mode.dockInto(this);
            }
            super.open();
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
        
        public int getPersistenceType() {
            return PERSISTENCE_ALWAYS;
        }
        
        public void readExternal(java.io.ObjectInput oi) throws java.io.IOException, ClassNotFoundException {
            super.readExternal(oi);
            component = this;
        }
        
    }
    
}

