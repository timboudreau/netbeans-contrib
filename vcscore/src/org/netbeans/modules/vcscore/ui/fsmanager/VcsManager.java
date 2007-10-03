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

package org.netbeans.modules.vcscore.ui.fsmanager;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.tree.TreeSelectionModel;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.filesystems.FileSystem;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.SharedClassObject;

import org.netbeans.modules.vcscore.actions.VcsMountFromTemplateAction;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.registry.RecognizedFS;
import org.netbeans.modules.vcscore.ui.fsmanager.VcsChildren.FSInfoBeanNode;
import org.netbeans.modules.vcscore.versioning.VersioningRepository;
import org.netbeans.modules.vcscore.versioning.VersioningRepositoryEvent;
import org.netbeans.modules.vcscore.versioning.VersioningRepositoryListener;
import org.netbeans.modules.vcscore.versioning.impl.VersioningExplorer;

/**
 * Vcs Manager
 *
 * @author  Richard Gregor
 */
public class VcsManager extends JPanel implements ExplorerManager.Provider, PropertyChangeListener {
    
    /** generated Serialized Version UID */
    static final long serialVersionUID = -3249241888704331940L;
    
    private static VcsManager instance;
    
    private Node node = null;
    private ExplorerManager manager = null;
    private Node.Property[] properties;
    
    /** Creates new VcsManager */
    private VcsManager() {
        initComponents();
        treeTableView1.setPopupAllowed(false);
        treeTableView1.setRootVisible(false);
        treeTableView1.setDefaultActionAllowed(true);
        treeTableView1.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        properties = new Node.Property[]{
            new PropertySupport.ReadOnly(
            "displayType", // NOI18N
            String.class,
            NbBundle.getMessage(VcsManager.class, "LBL_VcsNodeType"),// NOI18N
            NbBundle.getMessage(VcsManager.class, "HINT_VcsNodeType")// NOI18N
            ) {
                public Object getValue() {
                    return null;
                }             
                
            }/*,
            new PropertySupport.ReadWrite(
            "control", //NOI18N
            Boolean.TYPE,
            NbBundle.getMessage(VcsManager.class, "LBL_VcsNodeControl"), //NOI18N
            NbBundle.getMessage(VcsManager.class, "HINT_VcsNodeControl")
            ){
                public Object getValue(){
                    return null;
                }
                public void setValue(Object obj){
                    //
                }
            }*/
        };
        
        treeTableView1.setProperties(properties);
        treeTableView1.setTableColumnPreferredWidth(0,40);
        treeTableView1.setTreePreferredWidth(180);
        initActions();
        getExplorerManager().addPropertyChangeListener(this);
        initAccessibility();
    }
    
    /** Get the instance of VcsManager */
    public static synchronized VcsManager getInstance() {
        if (instance == null) {
            instance = new VcsManager();
        }
        return instance;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        tableLabel = new javax.swing.JLabel();
        treeTableView1 = new org.openide.explorer.view.TreeTableView();
        newButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        explArea = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        setMinimumSize(new java.awt.Dimension(460, 164));
        setPreferredSize(new java.awt.Dimension(500, 330));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        tableLabel.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("ACS_lblVersionedDirs_mnc").charAt(0));
        tableLabel.setLabelFor(treeTableView1);
        tableLabel.setText(org.openide.util.NbBundle.getMessage(VcsManager.class, "VCSManager.lblVersionedDirs"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 2, 0);
        jPanel1.add(tableLabel, gridBagConstraints);

        treeTableView1.setBorder(new javax.swing.border.EtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(treeTableView1, gridBagConstraints);

        newButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("ACS_btnNew_mnc").charAt(0));
        newButton.setText(org.openide.util.NbBundle.getMessage(VcsManager.class, "VCSManager.btnNew"));
        newButton.setToolTipText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("New_btn_tooltip"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel1.add(newButton, gridBagConstraints);

        removeButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("ACS_VcsManager.btnRemove_mnc").charAt(0));
        removeButton.setText(org.openide.util.NbBundle.getMessage(VcsManager.class, "VCSManager.btnRemove"));
        removeButton.setToolTipText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("Remove_btn_tootip"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel1.add(removeButton, gridBagConstraints);

        editButton.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("ACS_VcsManager.lblEdit_mnc").charAt(0));
        editButton.setText(org.openide.util.NbBundle.getMessage(VcsManager.class, "VCSManager.btnEdit"));
        editButton.setToolTipText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("Edit_btn_tooltip"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel1.add(editButton, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        explArea.setEditable(false);
        explArea.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("explArea_txt"));
        explArea.setOpaque(false);
        add(explArea, java.awt.BorderLayout.NORTH);
        explArea.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("ACS_explArea"));
        explArea.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/fsmanager/Bundle").getString("ACSD_explArea"));

    }//GEN-END:initComponents
    
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(VcsManager.class, "ACSD.VCSManager"));
        setName(NbBundle.getMessage(VcsManager.class, "ACSN.VCSManager"));        
        newButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(VcsManager.class, "ACSD.VCSManager.recognize"));
        removeButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(VcsManager.class, "ACSD.VCSManager.unrecognize"));
        editButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(VcsManager.class, "ACSD.VCSManager.customize"));
        treeTableView1.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(VcsManager.class, "ACSD.VCSManager.treeTableView1"));
        //mnemonics
        tableLabel.setDisplayedMnemonic(NbBundle.getMessage(VcsManager.class, "VCSManager.tableLabel.Mnem").charAt(0));        
    
    }
    
    /** Get the explorer manager.
     * @return the manager
     */
    public ExplorerManager getExplorerManager() {
        if(manager == null){
            manager = new ExplorerManager(); 
            Node node = getNode();
            manager.setRootContext(node);
        }
        return manager;
    }

    
    /**
     * Returns the vcs node
     */
    public Node getNode(){
        if(node == null)
            node = new VcsNode();
        return node;
    }
    
    private CustomizeAction customizeAction;
            
    private void initActions(){
        newButton.setAction(new RecognizeAction());
        removeButton.setAction(new UnrecognizeAction());
        editButton.setAction(customizeAction = new CustomizeAction());
    }
    
    public Action getCustomizeAction() {
        return customizeAction;
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        
    }    
    
    private class RecognizeAction extends AbstractAction{
        
        /** generated Serialized Version UID */
        static final long serialVersionUID = 4451020155975578178L;       
        
        
        public RecognizeAction(){
            super(NbBundle.getMessage(VcsManager.class, "VCSManager.btnNew"));
            putValue(Action.MNEMONIC_KEY, new Integer(NbBundle.getMessage(VcsManager.class, "ACS_btnNew_mnc").charAt(0)));
        }
        

        public void actionPerformed(ActionEvent e){
            addVersioningOpenerListener();
            ((VcsMountFromTemplateAction) SharedClassObject.findObject (VcsMountFromTemplateAction.class, true)).actionPerformed(e);
        }      
    
    }
    
    private class UnrecognizeAction extends AbstractAction implements PropertyChangeListener {
        /** generated Serialized Version UID */
        static final long serialVersionUID = -1839566741005638541L;
        
        public UnrecognizeAction() {
            super(NbBundle.getMessage(VcsManager.class, "VCSManager.btnRemove"));
            putValue(Action.MNEMONIC_KEY, new Integer(NbBundle.getMessage(VcsManager.class, "ACS_VcsManager.btnRemove_mnc").charAt(0)));
            getExplorerManager().addPropertyChangeListener(this);
        }
        
        public boolean isEnabled() {
            Node[] selectedNodes = getExplorerManager().getSelectedNodes();
            if (selectedNodes.length == 0) return false;
            FSInfo info = (FSInfo)((FSInfoBeanNode) selectedNodes[0]).getInfo();
            if (info == null) return false;
            return RecognizedFS.getDefault().isManuallyRecognized(info);
        }
        
        public void actionPerformed(ActionEvent e) {
            Node[] selectedNodes = getExplorerManager().getSelectedNodes();
            if (selectedNodes.length == 0) return ;
            final FSInfo[] infos = new FSInfo[selectedNodes.length];
            int sn = 0;
            for (int i = 0; i < selectedNodes.length; i++) {
                infos[sn] = (FSInfo)((FSInfoBeanNode) selectedNodes[i]).getInfo();
                if (infos[sn] != null) sn++;
            }
            if (sn == 0) return ;
            final int length = sn;
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    Object option;
                    if (length == 1) {
                        option = DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Confirmation(
                            NbBundle.getMessage(VcsManager.class, "MSG_ConfirmFSRemoval", infos[0].getFSRoot().getAbsolutePath()),
                            NbBundle.getMessage(VcsManager.class, "VCSManager.btnRemove"),
                            NotifyDescriptor.YES_NO_OPTION));
                    } else {
                        option = DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Confirmation(
                            NbBundle.getMessage(VcsManager.class, "MSG_ConfirmMultiFSRemoval", new Integer(length)),
                            NbBundle.getMessage(VcsManager.class, "VCSManager.btnRemove"),
                            NotifyDescriptor.YES_NO_OPTION));
                    }
                    if (NotifyDescriptor.YES_OPTION == option) {
                        for (int i = 0; i < length; i++) {
                            FSRegistry.getDefault().unregister(infos[i]);
                        }
                    }
                }
            });
        }
        
        public void propertyChange(PropertyChangeEvent e) {
            firePropertyChange("enabled", null, isEnabled() ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    
    private class CustomizeAction extends AbstractAction implements PropertyChangeListener {
        /** generated Serialized Version UID */
        static final long serialVersionUID = -3243779028704471940L;
        
        public CustomizeAction(){
            super(NbBundle.getMessage(VcsManager.class, "VCSManager.btnEdit"));
            putValue(Action.MNEMONIC_KEY, new Integer(NbBundle.getMessage(VcsManager.class, "ACS_VcsManager.lblEdit_mnc").charAt(0)));
            getExplorerManager().addPropertyChangeListener(this);
        }
        
        public boolean isEnabled() {
            return getExplorerManager().getSelectedNodes().length == 1;
        }
        
        public void actionPerformed(ActionEvent e){
            Node[] selectedNodes = getExplorerManager().getSelectedNodes();
            if (selectedNodes.length == 0) return ;
            //DataFolder df = (DataFolder) selectedNodes[0].getCookie(DataFolder.class);
            //if (df == null) return ;            
            FSInfo info = (FSInfo)((FSInfoBeanNode) selectedNodes[0]).getInfo();
            if(info == null)
                return;
            FileSystem vcsFs = info.getFileSystem();  
            if (vcsFs == null) {
                return ;
            }
            try {
                BeanNode bn = new BeanNode(vcsFs);
                Component cust = bn.getCustomizer();
                if (cust == null) {
                    PropertySheet ps = new PropertySheet();
                    ps.setNodes(new BeanNode[] {bn});
                    cust = ps;
                }
                if (cust instanceof Window) {
                    cust.show();
                } else {
                    DialogDescriptor dd = new DialogDescriptor(cust,
                    NbBundle.getMessage(VcsManager.class, "LAB_FS_Customizer"));
                    javax.swing.JButton close = new javax.swing.JButton(NbBundle.getMessage(VcsManager.class, "LBL_VcsManagerClose"));
                    close.setMnemonic(NbBundle.getMessage(VcsManager.class, "LBL_VcsManagerClose_mnc").charAt(0));
                    Object[] options = new Object[]{close};
                    dd.setOptions(options);                   
                    dd.setValue(options[0]);
                    dd.setClosingOptions(options);
                    DialogDisplayer.getDefault().createDialog(dd).show();
                }
            } catch (IntrospectionException exc) {
                DialogDisplayer.getDefault().notify(new Message(NbBundle.getMessage(VcsManager.class, "MSG_NO_FS_Customizer")));
            }
        }
        
        public void propertyChange(PropertyChangeEvent e) {
            firePropertyChange("enabled", null, isEnabled() ? Boolean.TRUE : Boolean.FALSE);
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editButton;
    private javax.swing.JTextArea explArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton newButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JLabel tableLabel;
    private org.openide.explorer.view.TreeTableView treeTableView1;
    // End of variables declaration//GEN-END:variables
    
    private static boolean versioningOpenerListenerAdded = false;
    
    public static void addVersioningOpenerListener() {
        if (!versioningOpenerListenerAdded) {
            VersioningRepository.getRepository().addRepositoryListener(new VersioningOpenerListener());
            versioningOpenerListenerAdded = true;
        }
    }
    
    private static final class VersioningOpenerListener extends Object implements VersioningRepositoryListener {
        
        public void versioningSystemAdded(VersioningRepositoryEvent re) {
            if (re.getRepository().getVersioningFileSystems().size() == 1) {
                // The first versioning filesystem was just added
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        VersioningExplorer.getRevisionExplorer().open();
                    }
                });
            }
        }
        
        public void versioningSystemRemoved(VersioningRepositoryEvent re) {
            // Should we close VersioningExplorer when the last VFS is removed?
        }
        
    }
}
