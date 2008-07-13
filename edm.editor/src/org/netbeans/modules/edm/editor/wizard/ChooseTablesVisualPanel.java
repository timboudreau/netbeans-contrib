/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this 
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */
package org.netbeans.modules.edm.editor.wizard;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.modules.mashup.db.ui.AxionDBConfiguration;
import org.netbeans.modules.sql.framework.model.DBMetaDataFactory;
import org.netbeans.modules.sql.framework.common.utils.DBExplorerUtil;

/**
 *
 * @author karthikeyan s
 */
public final class ChooseTablesVisualPanel extends JPanel {
    
    private ChooseTablesWizardPanel owner;
    
    private String selectedUrl;
    
    private boolean canAdvance = false;
    
    private Map<String, String> userMap = new HashMap<String, String>();
    
    private Map<String, String> passwdMap = new HashMap<String, String>();
    
    private DBMetaDataFactory meta = new DBMetaDataFactory();
    
    DatabaseConnection conn = null;
    
    private Map<String, String> driverMap = new HashMap<String, String>();
    
    private String jdbcUrl;
    
    /**
     * Creates new form ChooseTableVisualPanel
     */
    public ChooseTablesVisualPanel(ChooseTablesWizardPanel panel) {
        owner = panel;
        initComponents();
        connectionList.setModel(new DefaultListModel());
        tableList.setModel(new DefaultListModel());
        connectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectButton.setEnabled(false);
        removeButton.setEnabled(false);
        populateDBList();
        populateConnections();
        connectionList.addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent e) {
            }
            
            public void mousePressed(MouseEvent e) {
            }
            
            public void mouseReleased(MouseEvent e) {
                DefaultListModel model = (DefaultListModel) connectionList.getModel();
                String jdbcUrl = (String) connectionList.getSelectedValue();
                DatabaseConnection dbConn = null;
                DatabaseConnection dbConns[] = ConnectionManager.getDefault().getConnections();
                for(DatabaseConnection dc : dbConns) {
                    if(dc.getDatabaseURL().equals(jdbcUrl)) {
                        dbConn = dc;
                        break;
                    }
                }
                
                conn = dbConn;
                ConnectionManager.getDefault().showConnectionDialog(conn);
                try {
                    userMap.put(conn.getDatabaseURL(), conn.getUser());
                    passwdMap.put(conn.getDatabaseURL(), conn.getPassword());
                    driverMap.put(conn.getDatabaseURL(), conn.getDriverClass());
                    meta.connectDB(conn.getJDBCConnection());
                    String[] schemas = meta.getSchemas();
                    schemaCombo.removeAllItems();
                    for(String schema : schemas) {
                        schemaCombo.addItem(schema);
                    }
                    if(schemaCombo.getItemCount() != 0) {
                        String schema = (String) schemaCombo.getItemAt(0);
                        populateTable(schema);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            public void mouseEntered(MouseEvent e) {
            }
            
            public void mouseExited(MouseEvent e) {
            }
            
        });
    }
    
    public String getName() {
        return "Choose Tables";
    }
    
    private void populateDBList(){
        Set<String> urls = new HashSet<String>();
        AxionDBConfiguration config = new AxionDBConfiguration();
        File f = new File(config.getLocation());
        File[] db = null;
        if(f.exists()) {
            db = f.listFiles();
            for(int i = 0; i < db.length; i++) {
                String ver = null;
                try {
                    ver = db[i].getCanonicalPath() + File.separator + db[i].getName().toUpperCase() + ".VER";
                    File version = new File(ver);
                    if(version.exists()) {
                        String url = "jdbc:axiondb:" + db[i].getName()+ ":" + 
                                config.getLocation() + db[i].getName();
                        urls.add(url);
                        DatabaseConnection con = ConnectionManager.getDefault().getConnection(url);
                        if(con == null) {
                            DBExplorerUtil.createConnection("org.axiondb.jdbc.AxionDriver", url, "sa", "sa");
                        }
                    }
                } catch (Exception ex) {
                    //ignore
                }
            }
        }
        DatabaseConnection[] dbconns = ConnectionManager.getDefault().getConnections();
        for (DatabaseConnection dbconn: dbconns) {
            if (dbconn.getDriverClass().equals("org.axiondb.jdbc.AxionDriver")) {
                urls.add(dbconn.getDatabaseURL());
            }
        }
        for(String url: urls) {
            connectionCombo.addItem(url);
        }
        if(connectionCombo.getItemCount() == 0 ) {
            error.setText("No Mashup Database found.");
        }
    }    
    
    public String getMashupConnection() {
        return (String) connectionCombo.getSelectedItem();
    }
    
    public DefaultTableModel getTables() {
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        Vector<String> userVector = new Vector<String>();
        Vector<String> passVector = new Vector<String>();
        Vector<String> driverVector = new Vector<String>();
        for(int i = 0; i < model.getRowCount(); i++) {
            String url = (String) model.getValueAt(i, 2);
            userVector.add(userMap.get(url));
            passVector.add(passwdMap.get(url));            
            driverVector.add(driverMap.get(url));
        }
        model.addColumn("user", userVector);
        model.addColumn("pass", passVector);
        model.addColumn("driver", driverVector);
        return model;
    }
    
    public void cleanup() {
        try {
            if(meta != null) {
                meta.disconnectDB();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        connectionList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableList = new javax.swing.JList();
        schemaCombo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        error = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        selectButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        connectionCombo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Table Selection"));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Table Name", "Schema", "Connection Url"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jScrollPane3.setViewportView(jTable1);

        connectionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(connectionList);

        jScrollPane2.setViewportView(tableList);

        schemaCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schemaComboActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Schema");

        error.setForeground(new java.awt.Color(255, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(selectButton, "Select");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, "Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, selectButton)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, removeButton)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(selectButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setForeground(new java.awt.Color(0, 0, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Tables");

        jLabel3.setForeground(new java.awt.Color(0, 0, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Mashup Database Connections");

        jLabel4.setForeground(new java.awt.Color(0, 51, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "Database Connections");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(connectionCombo, 0, 444, Short.MAX_VALUE)
                    .add(error, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 232, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(schemaCombo, 0, 147, Short.MAX_VALUE)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 357, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jLabel3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(layout.createSequentialGroup()
                            .add(46, 46, 46)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(jLabel1)
                                .add(schemaCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 117, Short.MAX_VALUE))
                        .add(layout.createSequentialGroup()
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel4)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                    .add(layout.createSequentialGroup()
                        .add(76, 76, 76)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 131, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(error, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(connectionCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int[] rows = jTable1.getSelectedRows();
        final DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for(int row : rows) {
            model.removeRow(row);
        }
        Runnable run = new Runnable(){
            public void run() {
                jTable1.setModel(model);
            }
        };
        SwingUtilities.invokeLater(run);
        owner.fireChangeEvent();//GEN-LAST:event_removeButtonActionPerformed
    }                                            
    
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        final DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        Object[] tables = (Object[]) tableList.getSelectedValues();
        String schema = (String) schemaCombo.getSelectedItem();
        String jdbcUrl = (String) connectionList.getSelectedValue();
        for(Object table : tables) {
            Vector<String> row = new Vector<String>();
            row.add(table.toString());
            row.add(schema);
            row.add(jdbcUrl);
            model.addRow(row);
        }
        if(model.getRowCount() != 0) {
            removeButton.setEnabled(true);
            error.setText("");
            if(connectionCombo.getModel().getSize() != 0 &&
                    connectionCombo.getSelectedItem() != null) {
                canAdvance = true;    
            }            
        } else {
            removeButton.setEnabled(false);
            error.setText("No table available for processing.");
            canAdvance = false;
        }
        Runnable run = new Runnable(){
            public void run() {
                jTable1.setModel(model);
            }
        };
        SwingUtilities.invokeLater(run);
        owner.fireChangeEvent();//GEN-LAST:event_selectButtonActionPerformed
    }                                            
    
    private void schemaComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schemaComboActionPerformed
        JComboBox combo = (JComboBox)evt.getSource();
        String schema = (String) combo.getSelectedItem();
        populateTable(schema);//GEN-LAST:event_schemaComboActionPerformed
    }                                           
    
    private void populateTable(String schema) {
        if(conn != null) {
            try {
                DefaultListModel model = (DefaultListModel) tableList.getModel();
                model.clear();
                meta.connectDB(conn.getJDBCConnection());
                String[][] tables = meta.getTablesAndViews("", schema, "", false);
                String[] currTable = null;
                if (tables != null) {
                    for (int i = 0; i < tables.length; i++) {
                        currTable = tables[i];
                        model.addElement(currTable[DBMetaDataFactory.NAME]);
                    }
                }
                if(model.getSize() != 0) {
                    selectButton.setEnabled(true);
                } else {
                    selectButton.setEnabled(false);
                }
                tableList.setModel(model);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void populateConnections() {
        DefaultListModel model = (DefaultListModel) connectionList.getModel();
        model.clear();
        driverMap.clear();
        DatabaseConnection connections[] = ConnectionManager.getDefault().getConnections();
        for(DatabaseConnection conn : connections) {
            model.addElement(conn.getDatabaseURL());
        }
        setModel(connectionList, model);
    }
    
    private void setModel(final JList list, final DefaultListModel model) {
        Runnable run = new Runnable(){
            public void run() {
                list.setModel(model);
            }
        };
        SwingUtilities.invokeLater(run);
    }
    
    public Map<String, String> getUserMap() {
        return userMap;
    }
    
    public Map<String, String> getPasswordMap() {
        return passwdMap;
    }
    
    public Map<String, String> getDriverMap() {
        return driverMap;
    }
    
    public boolean canAdvance() {
        return (jTable1.getModel().getRowCount() != 0 && error.getText().trim().equals(""));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox connectionCombo;
    private javax.swing.JList connectionList;
    private javax.swing.JLabel error;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton removeButton;
    private javax.swing.JComboBox schemaCombo;
    private javax.swing.JButton selectButton;
    private javax.swing.JList tableList;
    // End of variables declaration//GEN-END:variables
}