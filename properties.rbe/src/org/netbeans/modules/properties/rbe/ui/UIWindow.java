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
 * Contributor(s): Denis Stepanov
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.properties.rbe.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.properties.rbe.model.BundleProperty;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * The UI window
 * @author  Denis Stepanov <denis.stepanov at gmail.com>
 */
public class UIWindow extends javax.swing.JPanel implements PropertyChangeListener {

    private ImprovedBeanTreeView treeView;
    private ExplorerManager explorer;
    private RBE rbe;

    /** Creates new form NewJPanel */
    public UIWindow(RBE rbe) {
        this.rbe = rbe;
        initComponents();

        searchTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                searchTextFieldTextChanged(e);
            }

            public void removeUpdate(DocumentEvent e) {
                searchTextFieldTextChanged(e);
            }

            public void changedUpdate(DocumentEvent e) {
                searchTextFieldTextChanged(e);
            }
        });


        treeView = new ImprovedBeanTreeView();
        treeView.setRootVisible(false);

        treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.PAGE_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        treePanel.add(treeView);
    }

    @Override
    public void addNotify() {

        ExplorerManager.Provider provider = (ExplorerManager.Provider) SwingUtilities.getAncestorOfClass(ExplorerManager.Provider.class, this);
        if (provider == null) {
            throw new IllegalArgumentException("Cannot find an Explorer provider!");
        } else {
            explorer = provider.getExplorerManager();
        }
        explorer.addPropertyChangeListener(this);
        updateBeanTree();
        super.addNotify();
    }

    protected void updateBeanTree() {
        explorer.setRootContext(new AbstractNode(Children.create(new BundlePropertyNodeFactory(rbe), true)) {
        });
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            updateSelectedProperty();
        }
    }

    protected void updateSelectedProperty() {
        BundlePropertyNode bundlePropertyNode = null;
        if (explorer.getSelectedNodes().length == 1) {
            Node selectedNode = explorer.getSelectedNodes()[0];
            if (selectedNode instanceof BundlePropertyNode) {
                bundlePropertyNode = (BundlePropertyNode) selectedNode;
            }
        }
        selectProperty(bundlePropertyNode);
    }

    public void selectProperty(BundlePropertyNode bundlePropertyNode) {
        rightPanel.removeAll();
        if (bundlePropertyNode != null) {
            for (Locale locale : bundlePropertyNode.getProperty().getBundle().getLocales()) {
                rightPanel.add(new UIPropertyPanel(locale, bundlePropertyNode.getProperty().getLocaleRepresentation().get(locale), bundlePropertyNode.getProperty().getBundle()));
            }
        }
        rightPanel.updateUI();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        changeModeButton = new javax.swing.JButton();
        expandAllButton = new javax.swing.JButton();
        collapseAllButton = new javax.swing.JButton();
        treePanel = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        createButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        rightPanel = new javax.swing.JPanel();

        splitPane.setContinuousLayout(true);

        leftPanel.setPreferredSize(new java.awt.Dimension(270, 200));

        toolbar.setFloatable(false);
        toolbar.setMinimumSize(new java.awt.Dimension(50, 31));
        toolbar.setPreferredSize(new java.awt.Dimension(50, 31));

        changeModeButton.setText(org.openide.util.NbBundle.getMessage(UIWindow.class, "UIWindow.changeModeButton.text")); // NOI18N
        changeModeButton.setFocusable(false);
        changeModeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        changeModeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        changeModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeModeButtonActionPerformed(evt);
            }
        });
        toolbar.add(changeModeButton);

        expandAllButton.setText(org.openide.util.NbBundle.getMessage(UIWindow.class, "UIWindow.expandAllButton.text")); // NOI18N
        expandAllButton.setEnabled(false);
        expandAllButton.setFocusable(false);
        expandAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expandAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandAllButtonActionPerformed(evt);
            }
        });
        toolbar.add(expandAllButton);

        collapseAllButton.setText(org.openide.util.NbBundle.getMessage(UIWindow.class, "UIWindow.collapseAllButton.text")); // NOI18N
        collapseAllButton.setEnabled(false);
        collapseAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        collapseAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collapseAllButtonActionPerformed(evt);
            }
        });
        toolbar.add(collapseAllButton);

        org.jdesktop.layout.GroupLayout treePanelLayout = new org.jdesktop.layout.GroupLayout(treePanel);
        treePanel.setLayout(treePanelLayout);
        treePanelLayout.setHorizontalGroup(
            treePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 270, Short.MAX_VALUE)
        );
        treePanelLayout.setVerticalGroup(
            treePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 510, Short.MAX_VALUE)
        );

        searchTextField.setText(org.openide.util.NbBundle.getMessage(UIWindow.class, "UIWindow.searchTextField.text")); // NOI18N

        createButton.setText(org.openide.util.NbBundle.getMessage(UIWindow.class, "UIWindow.createButton.text")); // NOI18N
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout leftPanelLayout = new org.jdesktop.layout.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, leftPanelLayout.createSequentialGroup()
                .add(searchTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(createButton))
            .add(treePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(leftPanelLayout.createSequentialGroup()
                .add(toolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(treePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(leftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createButton)
                    .add(searchTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        splitPane.setLeftComponent(leftPanel);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        rightPanel.setLayout(new javax.swing.BoxLayout(rightPanel, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane1.setViewportView(rightPanel);

        splitPane.setRightComponent(jScrollPane1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(splitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(splitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void expandAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandAllButtonActionPerformed
    treeView.expandAll();
}//GEN-LAST:event_expandAllButtonActionPerformed

private void collapseAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collapseAllButtonActionPerformed
    treeView.collapseAll();
}//GEN-LAST:event_collapseAllButtonActionPerformed

    private void searchTextFieldTextChanged(DocumentEvent e) {
        if (searchTextField.getText().length() > 0) {
            Node node = getNode(explorer.getRootContext(), searchTextField.getText());
            try {
                explorer.setSelectedNodes(new Node[]{node});
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

private void changeModeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeModeButtonActionPerformed
    if (rbe.getMode() == RBE.DisplayMode.FLAT) {
        changeModeButton.setText("Flat");
        rbe.setMode(RBE.DisplayMode.TREE);
        collapseAllButton.setEnabled(true);
        expandAllButton.setEnabled(true);
    } else {
        changeModeButton.setText("Tree");
        rbe.setMode(RBE.DisplayMode.FLAT);
        collapseAllButton.setEnabled(false);
        expandAllButton.setEnabled(false);
    }
    updateBeanTree();
}//GEN-LAST:event_changeModeButtonActionPerformed

private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
    rbe.getBundle().addProperty(searchTextField.getText());
//    rbe.getBundle().save();
}//GEN-LAST:event_createButtonActionPerformed

    protected Node getNode(Node root, String prefix) {
        if (root.getChildren().getNodes().length == 0) {
            return root;
        }
        for (Node node : root.getChildren().getNodes()) {
            BundleProperty property = node.getLookup().lookup(BundleProperty.class);
            if (property != null && property.getFullname().startsWith(prefix)) {
                return node;
            }
            Node subnode = getNode(node, prefix);
            property = subnode.getLookup().lookup(BundleProperty.class);
            if (property != null && property.getFullname().startsWith(prefix)) {
                return subnode;
            }
        }
        return root;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton changeModeButton;
    private javax.swing.JButton collapseAllButton;
    private javax.swing.JButton createButton;
    private javax.swing.JButton expandAllButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JPanel treePanel;
    // End of variables declaration//GEN-END:variables
}

class ImprovedBeanTreeView extends BeanTreeView {

    /** 
     * Collapses all paths.
     */
    public void collapseAll() {
        int i = tree.getRowCount() - 1;
        while (i >= 0) {
            tree.collapseRow(i--);
            if (i >= tree.getRowCount()) {
                i = tree.getRowCount() - 1;
            }
        }
    }
}