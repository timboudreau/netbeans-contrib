/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.generator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/** Component Generator panel
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @author Jiri.Skrivanek@sun.com
 */
public class ComponentGeneratorPanel extends javax.swing.JPanel implements java.beans.PropertyChangeListener, java.beans.VetoableChangeListener, ActionListener {
    
    private static java.awt.Dialog dialog;
    private static ComponentGeneratorPanel panel;
    private String packageName;
    // data folder where to place generated sources
    private DataFolder targetDataFolder;
    private Thread thread;
    private java.util.Properties props;
    // "<default package>"
    private static final String DEFAULT_PACKAGE_LABEL = NbBundle.getBundle("org.netbeans.modules.java.project.Bundle").getString("LBL_DefaultPackage");
    // TopComponent for showing project structure
    private ProjectView projectView;

    
    /** creates ans shows Component Generator dialog
     */
    public static void showDialog(Node[] nodes){
        if (dialog==null) {
            panel = new ComponentGeneratorPanel(nodes);
            dialog = DialogDisplayer.getDefault().createDialog(new DialogDescriptor(panel, NbBundle.getMessage(ComponentGeneratorPanel.class, "Title"), false, new Object[]{DialogDescriptor.CLOSED_OPTION}, null, DialogDescriptor.BOTTOM_ALIGN, new HelpCtx(ComponentGeneratorPanel.class), panel)); // NOI18N
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    panel.actionPerformed(null);
                }
            });
        }
        panel.setSelectedNodes(nodes);
        dialog.setVisible(true);
    }
    
    /** Creates new ComponentGeneratorPanel
     */
    public ComponentGeneratorPanel(Node[] nodes) {
        loadProperties();
        initComponents();
        projectView = new ProjectView();
        projectView.getExplorerManager().setRootContext(createPackagesNode(nodes));
        projectView.getExplorerManager().addVetoableChangeListener(this);
        projectView.getExplorerManager().addPropertyChangeListener(this);
        projectViewPanel.add(projectView, BorderLayout.CENTER);
    }
    
    private class ProjectView extends TopComponent implements ExplorerManager.Provider, Lookup.Provider {
        
        private ExplorerManager manager;
        private BeanTreeView projectTreeView;
    
        public ProjectView() {
            this.setName("");
            this.manager = new ExplorerManager();
            this.setLayout(new java.awt.BorderLayout());
            projectTreeView = new BeanTreeView();
            projectTreeView.setRootVisible(true);
            projectTreeView.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_Package"));
            projectTreeView.setPopupAllowed(false);
            projectTreeView.setAutoscrolls(true);
            this.add(projectTreeView, BorderLayout.CENTER);
        }
        
        public ExplorerManager getExplorerManager() {
            return manager;
        }
        
        public BeanTreeView getBeanTreeView() {
            return projectTreeView;
        }
    }
    
    
    private void setSelectedNodes(Node[] nodes) {
        DataFolder df;
        if (projectView.getBeanTreeView().isEnabled() && nodes != null && nodes.length > 0 &&
                (df=(DataFolder)nodes[0].getCookie(DataFolder.class)) != null) {
            try {
                // create pathList of node selected in Project view
                Node node = nodes[0];
                ArrayList pathList = new ArrayList();
                pathList.add(node.getName());
                while((node = node.getParentNode()) != null) {
                    // ignore root node
                    if(node.getParentNode() != null) {
                        pathList.add(node.getName());
                    }
                }
                // find node in our tree
                Node nodeToSelect = projectView.getExplorerManager().getRootContext();
                for(int i=0;i<pathList.size()-1;i++) {
                    String name = (String)pathList.get(pathList.size()-2-i);
                    nodeToSelect = nodeToSelect.getChildren().findChild(name);
                }
                projectView.getExplorerManager().setSelectedNodes(new Node[] {nodeToSelect});
            }
            catch(Exception e) {
                // ignore
            }
        }
    }
    
    private Node createPackagesNode(Node[] nodes) {
        if(nodes.length == 0) {
            // no project opened => create a dummy node with message "Please, close the dialog and select a project."
            Node node = new AbstractNode(Children.LEAF);
            node.setDisplayName(NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_SelectProjectNode")); // NOI18N
            return node;
        }
        Project project = (Project)nodes[0].getLookup().lookup(Project.class);
        if(project == null) {
            // no project root node is selected
            DataObject dataObject = (DataObject)nodes[0].getLookup().lookup(DataObject.class);
            if(dataObject != null) {
                // find project of selected DataObject
                project = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            }
        }
        if(project != null) {
            LogicalViewProvider lvp = (LogicalViewProvider)project.getLookup().lookup(LogicalViewProvider.class);
            return new DataFolderFilterNode(lvp.createLogicalView().cloneNode());
        } else {
            // no project selected => create a dummy node with message "Please, close the dialog and select a project."
            Node node = new AbstractNode(Children.LEAF);
            node.setDisplayName(NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_SelectProjectNode")); // NOI18N
            return node;
        }
    }
    
    void loadProperties() {
        props = new java.util.Properties();
        try {
            props.load( this.getClass().getClassLoader().getResourceAsStream("org/netbeans/modules/jemmysupport/generator/ComponentGenerator.properties")); // NOI18N
        } catch (Exception e) {
            e.printStackTrace();
            throw new java.lang.reflect.UndeclaredThrowableException(e, NbBundle.getMessage(ComponentGeneratorPanel.class, "MSG_PropertiesNotLoaded")); // NOI18N
        }
    }
    
    void saveProperties() {
        try {
            org.openide.filesystems.FileObject fo=org.openide.filesystems.Repository.getDefault().getDefaultFileSystem().getRoot();
            org.openide.filesystems.FileObject fo2=fo.getFileObject("jemmysupport"); // NOI18N
            if (fo2==null) {
                fo2=fo.createFolder("jemmysupport"); // NOI18N
            }
            fo=fo2.getFileObject("ComponentGenerator","properties"); // NOI18N
            if (fo==null) {
                fo=fo2.createData("ComponentGenerator","properties"); // NOI18N
            }
            props.store(fo.getOutputStream(fo.lock()),NbBundle.getMessage(ComponentGeneratorPanel.class, "MSG_PropertiesTitle")); // NOI18N
        } catch (Exception e) {
            throw new java.lang.reflect.UndeclaredThrowableException(e, NbBundle.getMessage(ComponentGeneratorPanel.class, "MSG_PropertiesNoSaved")); // NOI18N
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        selectLabel = new javax.swing.JLabel();
        projectViewPanel = new javax.swing.JPanel();
        helpLabel = new javax.swing.JLabel();
        helpLabel.setVisible(false);
        stopButton = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        stopButton.setVisible(false);
        screenShot = new javax.swing.JCheckBox();
        showEditor = new javax.swing.JCheckBox();
        mergeConflicts = new javax.swing.JCheckBox();
        cbUseComponentName = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setPreferredSize(new java.awt.Dimension(600, 300));
        selectLabel.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_Package").charAt(0));
        selectLabel.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_Package"));
        selectLabel.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_Package"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(selectLabel, gridBagConstraints);

        projectViewPanel.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 10.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
        add(projectViewPanel, gridBagConstraints);

        helpLabel.setFont(new java.awt.Font("Dialog", 2, 12));
        helpLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        helpLabel.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_Help"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
        add(helpLabel, gridBagConstraints);
        helpLabel.getAccessibleContext().setAccessibleDescription("N/A");

        stopButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_Stop").charAt(0));
        stopButton.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_Stop"));
        stopButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_Stop"));
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 12, 12);
        add(stopButton, gridBagConstraints);

        startButton.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_Start").charAt(0));
        startButton.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_Start"));
        startButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_Start"));
        startButton.setEnabled(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 12, 12);
        add(startButton, gridBagConstraints);

        screenShot.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_ScreenShot").charAt(0));
        screenShot.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_ScreenShot"));
        screenShot.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_ScreenShot"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(screenShot, gridBagConstraints);

        showEditor.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_ShowEditor").charAt(0));
        showEditor.setSelected(true);
        showEditor.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_ShowEditor"));
        showEditor.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_ShowEditor"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(showEditor, gridBagConstraints);

        mergeConflicts.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("MNM_Merge").charAt(0));
        mergeConflicts.setSelected(true);
        mergeConflicts.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("CTL_Merge"));
        mergeConflicts.setToolTipText(java.util.ResourceBundle.getBundle("org/netbeans/modules/jemmysupport/generator/Bundle").getString("TTT_Merge"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
        add(mergeConflicts, gridBagConstraints);

        cbUseComponentName.setMnemonic(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "MNM_UseComponentName").charAt(0));
        cbUseComponentName.setText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "CTL_UseComponentName"));
        cbUseComponentName.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentGeneratorPanel.class, "TTT_UseComponentName"));
        cbUseComponentName.setName("cbUseComponentName");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
        add(cbUseComponentName, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        if (thread!=null) {
            thread.interrupt();
            thread=null;
        }
        stopButton.setVisible(false);
        helpLabel.setVisible(false);
        projectView.getBeanTreeView().setEnabled(true);
        startButton.setVisible(true);
        //        customizeButton.setEnabled(true);
        screenShot.setEnabled(true);
        showEditor.setEnabled(true);
        cbUseComponentName.setEnabled(true);
        mergeConflicts.setEnabled(true);
    }//GEN-LAST:event_stopButtonActionPerformed
    
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        projectView.getBeanTreeView().setEnabled(false);
        startButton.setVisible(false);
        stopButton.setVisible(true);
        //        customizeButton.setEnabled(false);
        screenShot.setEnabled(false);
        showEditor.setEnabled(false);
        cbUseComponentName.setEnabled(false);
        mergeConflicts.setEnabled(false);
        helpLabel.setVisible(true);
        if (thread!=null) {
            thread.interrupt();
        }
        helpLabel.setText(NbBundle.getMessage(ComponentGeneratorPanel.class, "LBL_Help")); // NOI18N
        thread = new Thread(new ComponentGeneratorRunnable(targetDataFolder, packageName, this, props, screenShot.isSelected(), showEditor.isSelected(), mergeConflicts.isSelected(), cbUseComponentName.isSelected()));
        thread.start();
    }//GEN-LAST:event_startButtonActionPerformed
    
    /** Allow only simple selection.
     * @param ev PropertyChangeEvent
     * @throws PropertyVetoException PropertyVetoException
     */
    public void vetoableChange(java.beans.PropertyChangeEvent ev) throws java.beans.PropertyVetoException {
        if (org.openide.explorer.ExplorerManager.PROP_SELECTED_NODES.equals(ev.getPropertyName())) {
            Node n[] = (Node[])ev.getNewValue();
            if (n.length > 1 ) {
                throw new java.beans.PropertyVetoException(NbBundle.getMessage(ComponentGeneratorPanel.class, "MSG_SingleSelection"), ev); // NOI18N
            }
        }
    }
    
    /** Changes in selected node in packages.
     * @param ev PropertyChangeEvent
     */
    public void propertyChange(java.beans.PropertyChangeEvent ev) {
        if (org.openide.explorer.ExplorerManager.PROP_SELECTED_NODES.equals(ev.getPropertyName())) {
            startButton.setEnabled(false);
            Node[] arr = projectView.getExplorerManager().getSelectedNodes();
            if (arr.length == 1) {
                DataFolder df = (DataFolder)arr[0].getCookie(DataFolder.class);
                try {
                    if ((df != null) && (!df.getPrimaryFile().getFileSystem().isReadOnly())) {
                        startButton.setEnabled(true);
                        packageName = arr[0].getDisplayName();
                        if(packageName.equals(DEFAULT_PACKAGE_LABEL)) {
                            // if default package selected
                            packageName = "";
                        }
                        targetDataFolder = df;
                    }
                } catch (org.openide.filesystems.FileStateInvalidException e) {}
            }
        }
    }
    
    /** returns JLabel used as status line
     * @return JLabel used as status line
     */
    public javax.swing.JLabel getHelpLabel() {
        return helpLabel;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbUseComponentName;
    private javax.swing.JLabel helpLabel;
    private javax.swing.JCheckBox mergeConflicts;
    private javax.swing.JPanel projectViewPanel;
    private javax.swing.JCheckBox screenShot;
    private javax.swing.JLabel selectLabel;
    private javax.swing.JCheckBox showEditor;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
    
    /** creates Component Generator dialog for debugging purposes
     * @param args command line arguments
     */
    public static void main(String args[]) {
        showDialog(new Node[0]);
    }
    
    /** Invoked when an action occurs.
     *
     */
    public void actionPerformed(ActionEvent evt) {
        stopButtonActionPerformed(evt);
        dialog.dispose();
        dialog=null;
    }
    
}
