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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.netbeans.modules.properties.rbe.model.Bundle;
import org.netbeans.modules.properties.rbe.model.BundleProperty;
import org.netbeans.modules.properties.rbe.model.LocaleProperty;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * The UI window
 * @author  Denis Stepanov <denis.stepanov at gmail.com>
 */
public class UIWindow extends javax.swing.JPanel implements PropertyChangeListener {

    private ImprovedBeanTreeView treeView;
    private ExplorerManager explorer;
    private RBE rbe;
    /** The selected property node */
    private BundlePropertyNode selectedPropertyNode = null;

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
        super.addNotify();
        explorer = ExplorerManager.find(this);
        explorer.addPropertyChangeListener(this);
        updateBeanTree();
    }

    protected void updateBeanTree() {
        explorer.setRootContext(getRootNode());
    }

    protected AbstractNode getRootNode() {
        switch (rbe.getMode()) {
            case FLAT:
                return new FlatRootNode(rbe);
            case TREE:
                return new AbstractNode(Children.create(new BundlePropertyNodeFactory(rbe), true));
        }
        return new AbstractNode(Children.LEAF);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
            updateSelectedProperty();
        }
    }

    protected void updateSelectedProperty() {
        if (explorer.getSelectedNodes().length == 1) {
            Node selectedNode = explorer.getSelectedNodes()[0];
            if (selectedNode instanceof BundlePropertyNode) {
                selectProperty((BundlePropertyNode) selectedNode);
                searchTextField.setText(((BundlePropertyNode) selectedNode).getName());
            }
        }
    }

    public void selectProperty(final BundlePropertyNode bundlePropertyNode) {
        rightPanel.removeAll();
        if (bundlePropertyNode != null) {
            selectedPropertyNode = bundlePropertyNode;
            for (Locale locale : bundlePropertyNode.getProperty().getBundle().getLocales()) {
                LocaleProperty value = bundlePropertyNode.getProperty().getLocalProperty(locale);
                //TODO: create if value is null
                rightPanel.add(new UIPropertyPanel(value));
            }
        } else {
            selectedPropertyNode = null;
        }
        rightPanel.updateUI();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new JSplitPane();
        leftPanel = new JPanel();
        toolbar = new JToolBar();
        changeModeButton = new JButton();
        expandAllButton = new JButton();
        collapseAllButton = new JButton();
        treePanel = new JPanel();
        searchTextField = new JTextField();
        createButton = new JButton();
        jScrollPane1 = new JScrollPane();
        rightPanel = new JPanel();

        splitPane.setContinuousLayout(true);

        leftPanel.setPreferredSize(new Dimension(270, 200));

        toolbar.setFloatable(false);
        toolbar.setMinimumSize(new Dimension(50, 31));
        toolbar.setPreferredSize(new Dimension(50, 31));

        changeModeButton.setText(NbBundle.getMessage(UIWindow.class, "UIWindow.changeModeButton.text")); // NOI18N
        changeModeButton.setFocusable(false);
        changeModeButton.setHorizontalTextPosition(SwingConstants.CENTER);
        changeModeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        changeModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                changeModeButtonActionPerformed(evt);
            }
        });
        toolbar.add(changeModeButton);

        expandAllButton.setText(NbBundle.getMessage(UIWindow.class, "UIWindow.expandAllButton.text")); // NOI18N
        expandAllButton.setEnabled(false);
        expandAllButton.setFocusable(false);
        expandAllButton.setHorizontalTextPosition(SwingConstants.CENTER);
        expandAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                expandAllButtonActionPerformed(evt);
            }
        });
        toolbar.add(expandAllButton);

        collapseAllButton.setText(NbBundle.getMessage(UIWindow.class, "UIWindow.collapseAllButton.text")); // NOI18N
        collapseAllButton.setEnabled(false);
        collapseAllButton.setHorizontalTextPosition(SwingConstants.CENTER);
        collapseAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                collapseAllButtonActionPerformed(evt);
            }
        });
        toolbar.add(collapseAllButton);

        GroupLayout treePanelLayout = new GroupLayout(treePanel);
        treePanel.setLayout(treePanelLayout);

        treePanelLayout.setHorizontalGroup(
            treePanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 270, Short.MAX_VALUE)

        );
        treePanelLayout.setVerticalGroup(
            treePanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 534, Short.MAX_VALUE)

        );

        searchTextField.setText(NbBundle.getMessage(UIWindow.class, "UIWindow.searchTextField.text")); // NOI18N
        createButton.setText(NbBundle.getMessage(UIWindow.class, "UIWindow.createButton.text")); // NOI18N
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        GroupLayout leftPanelLayout = new GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(toolbar, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
            .add(GroupLayout.TRAILING, leftPanelLayout.createSequentialGroup()
                .add(searchTextField, GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(createButton))
            .add(treePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)

        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(leftPanelLayout.createSequentialGroup()
                .add(toolbar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(treePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(leftPanelLayout.createParallelGroup(GroupLayout.BASELINE)
                    .add(createButton)
                    .add(searchTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))

        );

        splitPane.setLeftComponent(leftPanel);

        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.LINE_AXIS));
        jScrollPane1.setViewportView(rightPanel);

        splitPane.setRightComponent(jScrollPane1);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(7, 7, 7)
                .add(splitPane, GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                .add(7, 7, 7))

        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(7, 7, 7)
                .add(splitPane, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .add(7, 7, 7))

        );
    }// </editor-fold>//GEN-END:initComponents

private void expandAllButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_expandAllButtonActionPerformed
    treeView.expandAll();
}//GEN-LAST:event_expandAllButtonActionPerformed

private void collapseAllButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_collapseAllButtonActionPerformed
    treeView.collapseAll();
}//GEN-LAST:event_collapseAllButtonActionPerformed

    protected void searchTextFieldTextChanged(DocumentEvent e) {
        selectNode(searchTextField.getText());
    }

    protected void selectNode(String key) {
        if (key.length() > 0) {
            Node node = getNode(explorer.getRootContext(), key);
            try {
                explorer.removePropertyChangeListener(this);
                explorer.setSelectedNodes(new Node[]{node});
                explorer.addPropertyChangeListener(this);
                if (node instanceof BundlePropertyNode) {
                    selectProperty((BundlePropertyNode) node);
                }
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

private void changeModeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_changeModeButtonActionPerformed
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
    if (selectedPropertyNode != null) {
        //because factory is running asyn. its works randomly :-/
        selectNode(selectedPropertyNode.getProperty().getKey());
    }
}//GEN-LAST:event_changeModeButtonActionPerformed

private void createButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
    rbe.getBundle().createProperty(searchTextField.getText());
    selectNode(searchTextField.getText());
}//GEN-LAST:event_createButtonActionPerformed

    protected Node getNode(Node root, String prefix) {
        if (root.getChildren().getNodes().length == 0) {
            return root;
        }
        for (Node node : root.getChildren().getNodes()) {
            BundleProperty property = node.getLookup().lookup(BundleProperty.class);
            if (property != null && property.getKey().startsWith(prefix)) {
                return node;
            }
            Node subnode = getNode(node, prefix);
            property = subnode.getLookup().lookup(BundleProperty.class);
            if (property != null && property.getKey().startsWith(prefix)) {
                return subnode;
            }
        }
        return root;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton changeModeButton;
    private JButton collapseAllButton;
    private JButton createButton;
    private JButton expandAllButton;
    private JScrollPane jScrollPane1;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JTextField searchTextField;
    private JSplitPane splitPane;
    private JToolBar toolbar;
    private JPanel treePanel;
    // End of variables declaration//GEN-END:variables
}

class FlatRootNode extends AbstractNode implements PropertyChangeListener {

    private RBE rbe;

    public FlatRootNode(RBE rbe) {
        super(Children.create(new BundlePropertyNodeFactory(rbe), true));
        this.rbe = rbe;
        rbe.getBundle().addPropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (Bundle.PROPERTY_PROPERTIES.equals(evt.getPropertyName())) {
            setChildren(Children.create(new BundlePropertyNodeFactory(rbe), true));
        }
    }
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