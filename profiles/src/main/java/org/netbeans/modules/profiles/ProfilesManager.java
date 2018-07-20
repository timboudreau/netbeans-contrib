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

package org.netbeans.modules.profiles;

import java.awt.Dialog;
import javax.swing.*;
import java.awt.event.*;
import java.lang.ref.WeakReference;
import javax.swing.text.DefaultEditorKit;
import java.beans.*;
import java.lang.ref.Reference;
import org.openide.loaders.DataObject;

import org.openide.util.*;
import org.openide.explorer.*;
import org.openide.explorer.view.BeanTreeView;
import org.openide.*;
import org.openide.nodes.*;

/**
 * This class provides the UI for managing profiles.
 *
 * @author Jaroslav Tulach
 */

public class ProfilesManager extends JPanel
implements ExplorerManager.Provider, Lookup.Provider {
    private static Reference<Dialog> dialogRef; // is weak reference necessary?

    private ExplorerManager explorerManager;
    private Lookup lookup;

    // ------------

    public static void showProfilesManager() {
        Dialog dialog = null;
        if (dialogRef != null)
            dialog = dialogRef.get();
        if (dialog == null) {
            JButton closeButton = new JButton();
            org.openide.awt.Mnemonics.setLocalizedText(
                closeButton, NbBundle.getMessage(ProfilesManager.class, "CTL_Close_Button")); // NOI18N
            DialogDescriptor dd = new DialogDescriptor(
                new ProfilesManager(),
                NbBundle.getMessage(ProfilesManager.class, "CTL_ProfilesManager_Title"), // NOI18N
                false,
                new Object[] { closeButton },
                closeButton,
                DialogDescriptor.DEFAULT_ALIGN,
                null,
                null);
            dialog = DialogDisplayer.getDefault().createDialog(dd);
            dialogRef = new WeakReference<Dialog>(dialog);
        }
        dialog.setVisible(true);
    }

    /**
     * Creates new ProfilesManager 
     */
    public ProfilesManager() {
        explorerManager = new ExplorerManager();

        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(explorerManager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(explorerManager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(explorerManager));
        map.put("delete", ExplorerUtils.actionDelete(explorerManager, true)); // NOI18N

        lookup = ExplorerUtils.createLookup(explorerManager, map);

        explorerManager.setRootContext(Profiles.getProfilesNode());

        initComponents();

        BeanTreeView treeView = new BeanTreeView();
//        treeView.getAccessibleContext().setAccessibleName(
//            NbBundle.getMessage(ProfilesManager.class, "???")); // NOI18N
//        treeView.getAccessibleContext().setAccessibleDescription(
//            NbBundle.getMessage(ProfilesManager.class, "???")); // NOI18N
        treePanel.add(treeView, java.awt.BorderLayout.CENTER);
        captionLabel.setLabelFor(treeView);

        explorerManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                if (ExplorerManager.PROP_SELECTED_NODES.equals(ev.getPropertyName()))
                    updateInfoLabel(explorerManager.getSelectedNodes());
            }
        });
        
        activateProfile.setEnabled(false);
        newCategoryButton.setEnabled(false);
        removeButton.setEnabled(false);
        exportButton.setEnabled(false);
    }

    public void addNotify() {
        super.addNotify();
        ExplorerUtils.activateActions(explorerManager, true);
    }

    public void removeNotify() {
        ExplorerUtils.activateActions(explorerManager, false);
        super.removeNotify();
    }

    // ExplorerManager.Provider
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    // Lookup.Provider from TopComponent
    public Lookup getLookup() {
        return lookup;
    }

    // -------

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        captionLabel = new javax.swing.JLabel();
        treePanel = new javax.swing.JPanel();
        infoLabel = new javax.swing.JLabel();
        activateProfile = new javax.swing.JButton();
        saveAs = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        newCategoryButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(captionLabel, org.openide.util.NbBundle.getMessage(ProfilesManager.class, "CTL_Caption"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 10);
        add(captionLabel, gridBagConstraints);

        treePanel.setLayout(new java.awt.BorderLayout());

        treePanel.setBorder(new javax.swing.border.EtchedBorder());
        treePanel.setPreferredSize(new java.awt.Dimension(288, 336));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
        add(treePanel, gridBagConstraints);

        infoLabel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        add(infoLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(activateProfile, org.openide.util.NbBundle.getMessage(ProfilesManager.class, "CTL_AddLibrary_Button"));
        activateProfile.setToolTipText(org.openide.util.NbBundle.getMessage(ProfilesManager.class, "HINT_AddLibrary"));
        activateProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                activateProfileActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(28, 12, 0, 10);
        add(activateProfile, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(saveAs, org.openide.util.NbBundle.getMessage(ProfilesManager.class, "CTL_AddProject_Button"));
        saveAs.setToolTipText(org.openide.util.NbBundle.getMessage(ProfilesManager.class, "HINT_AddProject"));
        saveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(28, 12, 0, 10);
        add(saveAs, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(exportButton, org.openide.util.NbBundle.getMessage(ProfilesManager.class, "CTL_Export_Button"));
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 10);
        add(exportButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(moveDownButton, org.openide.util.NbBundle.getMessage(ProfilesManager.class, "CTL_MoveDown_Button"));
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 10);
        add(moveDownButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(ProfilesManager.class, "CTL_Remove_Button"));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 10);
        add(removeButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(newCategoryButton, org.openide.util.NbBundle.getMessage(ProfilesManager.class, "CTL_NewCategory_Button"));
        newCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCategoryButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 10);
        add(newCategoryButton, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Node[] selected = explorerManager.getSelectedNodes();
        if (selected.length == 0)
            return;

        // first user confirmation...
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(ProfilesManager.class, "MSG_ConfirmProfilesDelete"), // NOI18N
            NbBundle.getMessage(ProfilesManager.class, "CTL_ConfirmDeleteTitle"), // NOI18N
            NotifyDescriptor.YES_NO_OPTION);

        if (NotifyDescriptor.YES_OPTION.equals(
                    DialogDisplayer.getDefault().notify(desc)))
        {
            try {
                for (int i=0; i < selected.length; i++)
                selected[i].destroy();
            }
            catch (java.io.IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
        moveNode(false);
    }//GEN-LAST:event_moveDownButtonActionPerformed

    private void exportButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        org.openide.filesystems.FileObject profile = null;
        
        Node[] nodes = explorerManager.getSelectedNodes();
        if (nodes.length == 1) {
            DataObject obj = nodes[0].getCookie(DataObject.class);
            if (obj != null) {
                org.openide.filesystems.FileObject fo = obj.getPrimaryFile();
                if (fo.hasExt("profile")) {
                    profile = fo;
                }
            }
        }                         
        
        if (profile == null) {
            return;
        }
        
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setSelectedFile(new java.io.File("nbprofile-" + profile.getName() + ".jar"));
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                Profiles.exportProfile(profile, chooser.getSelectedFile());
            } catch (java.io.IOException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }        
        
    }//GEN-LAST:event_exportButtonActionPerformed

    private void newCategoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCategoryButtonActionPerformed
    
    }//GEN-LAST:event_newCategoryButtonActionPerformed

    private void saveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsActionPerformed
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine (
            NbBundle.getMessage(ProfilesManager.class, "CTL_NameOfProfileSave"), 
            NbBundle.getMessage(ProfilesManager.class, "CTL_TitleOfProfileSave")
        );
        
        Object res = DialogDisplayer.getDefault().notify(nd);
        
        if (NotifyDescriptor.OK_OPTION == res) {
            Profiles.saveAs (nd.getInputText());
        }
        
    }//GEN-LAST:event_saveAsActionPerformed

    private void activateProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activateProfileActionPerformed
        Node[] nodes = explorerManager.getSelectedNodes();
        if (nodes.length == 1) {
            DataObject obj = nodes[0].getCookie(DataObject.class);
            if (obj != null) {
                org.openide.filesystems.FileObject fo = obj.getPrimaryFile();
                if (fo.hasExt("profile")) {
                    Profiles.activateProfile (fo);
                }
            }
        }
    }//GEN-LAST:event_activateProfileActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton activateProfile;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel captionLabel;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton newCategoryButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton saveAs;
    private javax.swing.JPanel treePanel;
    // End of variables declaration//GEN-END:variables

    private void moveNode(boolean up) {
        final Node[] selected = explorerManager.getSelectedNodes();
        if (selected.length != 1)
            return;

        Node node = selected[0];
        Node parent = node.getParentNode();
        if (parent == null)
            return;

        Index indexCookie = parent.getCookie(Index.class);
        if (indexCookie == null)
            return;

        int index = movePossible(node, parent, up);
        if (index != -1) {
            if (up)
                indexCookie.moveUp(index);
            else 
                indexCookie.moveDown(index);
        }
    }

    private static int movePossible(Node node, Node parentNode, boolean up) {
        if (parentNode == null)
            return -1;

        Node[] nodes = parentNode.getChildren().getNodes();
        for (int i=0; i < nodes.length; i++)
            if (nodes[i].getName().equals(node.getName()))
                return (up && i > 0) || (!up && i+1 < nodes.length) ? i : -1;

        return -1;
    }

    private void updateInfoLabel(org.openide.nodes.Node[] nodes) {
        boolean enable = false;
        if (nodes.length == 1) {
            DataObject obj = nodes[0].getCookie(DataObject.class);
            if (obj != null) {
                org.openide.filesystems.FileObject fo = obj.getPrimaryFile();
                if (fo.hasExt("profile")) {
                    enable = true;
                }
            }
        }
        
        activateProfile.setEnabled(enable);
        exportButton.setEnabled(enable);
    }
}
