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

package org.openoffice.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * 
 *
 * @author S. Aubrecht
 */
public class ConfigFrame extends javax.swing.JFrame {
    
    private ConfigManager manager;
    /** Creates new form ConfigFrame */
    public ConfigFrame( ConfigManager manager ) {
        this.manager = manager;
        initComponents();
        
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch (Exception ex) {
            //ignore
            ex.printStackTrace();
        }

        jScrollPane1.setVisible( false );
        lblNoData.setVisible( false );
        lblInitial.setVisible( true );

        setPreferredSize( new Dimension( 650, 400 ) );
        
        treeRoots.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        treeRoots.setModel( new DefaultTreeModel( manager.getConfigRootNode() ) );
    }
    
    private void switchTableModel( boolean forceRefresh ) {
        TreePath selPath = treeRoots.getSelectionPath();
        if( null == selPath )
            return;
        final DefaultMutableTreeNode selNode = (DefaultMutableTreeNode)selPath.getLastPathComponent();
        getRootPane().setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) );
        
        getRootPane().getGlassPane().setVisible(true);
        Runnable runnable = new Runnable() {
            public void run() {
                final ConfigTableModel model = new ConfigTableModel();
                collectDataFromChildren( selNode, model, null );

                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        tblContents.setModel( model );
                        jScrollPane1.setVisible( true );
                        lblNoData.setVisible( false );
                        lblInitial.setVisible( false );
                        getRootPane().setCursor( Cursor.getDefaultCursor() );
                        getRootPane().getGlassPane().setVisible(false);
                    }
                });
            }
        };
        new Thread( runnable ).start();
    }
    
    private void collectDataFromChildren( DefaultMutableTreeNode node, ConfigTableModel model, String prefix ) {
        if( node.isLeaf() ) {
            ConfigValueList list = (ConfigValueList)node.getUserObject();
            for( ConfigValue cv : list.getValues( false ) ) {
                cv.setPrefix( prefix );
                model.add( cv );
            }
        } else {
            for( Enumeration en = node.children(); en.hasMoreElements(); ) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode)en.nextElement();
                String newPrefix = child.getUserObject().toString();
                if( null != prefix )
                    newPrefix = prefix + '.' + newPrefix;
                collectDataFromChildren( child, model, newPrefix );
            }
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

        btnRefreshTable = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeRoots = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblContents = new javax.swing.JTable();
        lblInitial = new javax.swing.JLabel();
        lblNoData = new javax.swing.JLabel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OpenOffice Configuration");
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jSplitPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rootSelectionChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_rootSelectionChanged
        btnRefreshTable.setEnabled( treeRoots.getSelectionPath() != null );
        switchTableModel(false);
    }//GEN-LAST:event_rootSelectionChanged

    private void refreshTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshTable
        switchTableModel( true );
    }//GEN-LAST:event_refreshTable
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefreshTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lblInitial;
    private javax.swing.JLabel lblNoData;
    private javax.swing.JTable tblContents;
    private javax.swing.JTree treeRoots;
    // End of variables declaration//GEN-END:variables
    

    private static class TableModelComparator implements Comparator<TableModel> {
        public int compare(TableModel o1, TableModel o2) {
            return o1.toString().compareTo( o2.toString() );
        }
    }
}
