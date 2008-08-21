/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.openoffice.config;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.Enumeration;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Top-level window containing a tree with configuration root nodes and a table
 * with configuration values for selected tree node.
 *
 * @author S. Aubrecht
 */
class ConfigFrame extends javax.swing.JFrame {
    
    private ConfigManager manager;
    /** Creates new form ConfigFrame */
    public ConfigFrame( ConfigManager manager ) {
        this.manager = manager;
        
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch (Exception ex) {
            //ignore
            ex.printStackTrace();
        }
        
        initComponents();

        jScrollPane1.setVisible( false );
        lblNoData.setVisible( false );
        lblInitial.setVisible( true );

        setPreferredSize( new Dimension( 650, 400 ) );
        
        treeRoots.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        treeRoots.setModel( new DefaultTreeModel( manager.getConfigRootNode() ) );
        treeRoots.setCellRenderer( new NoLeafIconRenderer( treeRoots.getCellRenderer()) );
    }
    
    /**
     * Refresh table content according to currently selected tree node.
     */
    private void switchTableModel() {
        TreePath selPath = treeRoots.getSelectionPath();
        if( null == selPath ) {
            jScrollPane1.setVisible( false );
            lblNoData.setVisible( false );
            lblInitial.setVisible( true );
            btnExport.setEnabled( false );
            return;
        }
        final DefaultMutableTreeNode selNode = (DefaultMutableTreeNode)selPath.getLastPathComponent();
        final ConfigValueFilter filter = checkUserEntriesOnly.isSelected() ? ConfigValueFilter.getUserValuesOnlyFilter() : ConfigValueFilter.getDefaultFilter();
        getRootPane().setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
        
        //disable all controls in the window
        getRootPane().getGlassPane().setVisible(true);
        Runnable runnable = new Runnable() {
            public void run() {
                final ConfigTableModel model = new ConfigTableModel( manager );
                collectDataFromChildren( selNode, model, null, filter );

                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        tblContents.setModel( model );
                        jScrollPane1.setVisible( true );
                        lblNoData.setVisible( false );
                        lblInitial.setVisible( false );
                        getRootPane().setCursor( Cursor.getDefaultCursor() );
                        getRootPane().getGlassPane().setVisible(false);
                        btnExport.setEnabled( treeRoots.getSelectionPath() != null && model.getRowCount() > 0 );
                    }
                });
            }
        };
        new Thread( runnable ).start();
    }
    
    /**
     * Recursively collect configuration value from the given node and all its children.
     * @param node Tree node to get configuration data for.
     * @param model TableModel which keeps the configuration data
     * @param prefix Dot-separated path to the given node
     * @param filter Filter to hide certain configuration values.
     */
    private void collectDataFromChildren( DefaultMutableTreeNode node, ConfigTableModel model, String prefix, ConfigValueFilter filter ) {
        if( node.isLeaf() ) {
            ConfigValueList list = (ConfigValueList)node.getUserObject();
            for( ConfigValue cv : list.getValues( manager.getConfigAccess(), false ) ) {
                if( filter.isDisplayAble( cv ) ) {
                    cv.setDisplayPrefix( prefix );
                    model.add( cv );
                }
            }
        } else {
            for( Enumeration en = node.children(); en.hasMoreElements(); ) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode)en.nextElement();
                String newPrefix = child.getUserObject().toString();
                if( null != prefix )
                    newPrefix = prefix + '.' + newPrefix;
                collectDataFromChildren( child, model, newPrefix, filter );
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnRefreshTable = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeRoots = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblContents = new javax.swing.JTable();
        lblInitial = new javax.swing.JLabel();
        lblNoData = new javax.swing.JLabel();
        btnExport = new javax.swing.JButton();
        checkUserEntriesOnly = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OpenOffice Configuration");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        btnRefreshTable.setText("Refresh");
        btnRefreshTable.setEnabled(false);
        btnRefreshTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshTable(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(btnRefreshTable, gridBagConstraints);

        jSplitPane1.setPreferredSize(new java.awt.Dimension(600, 500));

        jScrollPane2.setPreferredSize(new java.awt.Dimension(120, 300));

        treeRoots.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                rootSelectionChanged(evt);
            }
        });
        jScrollPane2.setViewportView(treeRoots);

        jSplitPane1.setLeftComponent(jScrollPane2);

        jPanel1.setLayout(new java.awt.CardLayout());

        tblContents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblContents);

        jPanel1.add(jScrollPane1, "table");

        lblInitial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInitial.setText("Please select module name from the tree.");
        jPanel1.add(lblInitial, "initial");

        lblNoData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNoData.setText("<no configuration data to display>");
        jPanel1.add(lblNoData, "nodata");

        jSplitPane1.setRightComponent(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jSplitPane1, gridBagConstraints);

        btnExport.setText("Export");
        btnExport.setEnabled(false);
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 0);
        getContentPane().add(btnExport, gridBagConstraints);

        checkUserEntriesOnly.setText("Show user entries only");
        checkUserEntriesOnly.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkUserEntriesOnly.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkUserEntriesOnly.setOpaque(false);
        checkUserEntriesOnly.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkUserEntriesOnlyItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        getContentPane().add(checkUserEntriesOnly, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jLabel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void checkUserEntriesOnlyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkUserEntriesOnlyItemStateChanged
        switchTableModel();
    }//GEN-LAST:event_checkUserEntriesOnlyItemStateChanged

    private void export(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export
        //store the current content of the table as a CSV file
        ConfigDataExporter exporter = new ConfigDataExporter();
        ConfigTableModel model = (ConfigTableModel)tblContents.getModel();
        exporter.export( this, model.getValueList() );
    }//GEN-LAST:event_export

    private void rootSelectionChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_rootSelectionChanged
        btnRefreshTable.setEnabled( treeRoots.getSelectionPath() != null );
        switchTableModel();
    }//GEN-LAST:event_rootSelectionChanged

    private void refreshTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshTable
        switchTableModel();
    }//GEN-LAST:event_refreshTable
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnRefreshTable;
    private javax.swing.JCheckBox checkUserEntriesOnly;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblInitial;
    private javax.swing.JLabel lblNoData;
    private javax.swing.JTable tblContents;
    private javax.swing.JTree treeRoots;
    // End of variables declaration//GEN-END:variables
    

    private static class NoLeafIconRenderer implements TreeCellRenderer {
        
        private final TreeCellRenderer impl;
        
        public NoLeafIconRenderer( TreeCellRenderer impl ) {
            this.impl = impl;
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component res = impl.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if( res instanceof JLabel && leaf )
                ((JLabel)res).setIcon(null);
            return res;
        }
        
    }
}
