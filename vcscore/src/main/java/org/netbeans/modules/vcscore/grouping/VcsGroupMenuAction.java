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
        return "org/netbeans/modules/vcscore/grouping/vcs_groups.png"; // NOI18N
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
        
        private GroupExplorerPanel() {
            initComponent();
            getAccessibleContext().setAccessibleDescription(
                org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "ACSD_AddVcsGroupAction.dialog"));
            setIcon(org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/grouping/vcs_groups.png"));
            setToolTipText(org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "GroupExplorerPanel_tltp"));
        }
        
        public static synchronized GroupExplorerPanel getDefault() {
            if (component == null) {
                component = new GroupExplorerPanel();
            }
            return component;
        }
       
        protected String preferredID(){
            return "VcsGroupMenuAction_GroupExplorerPanel"; // NOI18N
        }
        
        private void initComponent() {
            Node root = null;
            final String modeName = org.openide.util.NbBundle.getMessage(VcsGroupMenuAction.class, "LBL_MODE.title");//NOI18N
            setName(modeName);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setToolTipText(modeName);
                }
            });
            
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

