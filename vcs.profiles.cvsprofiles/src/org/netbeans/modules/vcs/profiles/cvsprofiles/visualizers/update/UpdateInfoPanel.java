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


package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

import java.awt.BorderLayout;
import org.netbeans.lib.cvsclient.command.FileInfoContainer;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.openide.util.NbBundle;
import org.netbeans.lib.cvsclient.event.MessageEvent;
import org.netbeans.lib.cvsclient.event.EnhancedMessageEvent;
import org.openide.DialogDescriptor;
import javax.accessibility.*;

import java.awt.Dialog;
import java.awt.Dimension;
import java.io.File;
import java.lang.reflect.Method;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update.GrowingTableInfoModel;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update.TypeComparator;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update.UpdateInformation;
import org.netbeans.modules.vcscore.util.table.*;
import org.openide.DialogDisplayer;


public class UpdateInfoPanel extends JPanel{
    
    private static final int MAX_SCROLLBAR_GAP = 50;
    
    GrowingTableInfoModel model;
    long currentTimeStamp;
    long firedTimeStamp = 0;
    int addedCount = 0;
    int totalCount = 0;
    int lastSelection = -1;
    int lastHBar = 0;
    private String labelString;
    private boolean wasSending;
    private java.io.File sendingDir = null;
    private org.netbeans.lib.cvsclient.command.Command currentCom;
    private ActionListener stopActionListener;
    private CommandTask task;
    private StringBuffer buff;
    private OutputVisualizer visualizer;
    private JTextArea errTextArea;
    private boolean errEnabled = false;
    
    /** Creates new form UpdateInfoPanel */
    public UpdateInfoPanel(OutputVisualizer visualizer) {
        super();
        this.visualizer = visualizer;
        initComponents();
        errTextArea = new JTextArea();
        errTextArea.setEditable(false);
        errTextArea.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent e){
                
            }
            public void insertUpdate(DocumentEvent e){
                btnErr.setEnabled(true);
                errEnabled = true;
            }
            public void removeUpdate(DocumentEvent e){
                
            }
            
        });
        initAccessibility();
 
        setPreferredSize(new java.awt.Dimension(450, 200));
        setMinimumSize(new java.awt.Dimension(450, 200));
        labelString = NbBundle.getBundle(UpdateInfoPanel.class).getString("UpdateInfoPanel.lblSending.text"); // NOI18N
        // setting the model....
        model = new GrowingTableInfoModel();
        Class classa = UpdateInformation.class;
        String  column1 = NbBundle.getBundle(UpdateInfoPanel.class).getString("UpdateTableInfoModel.type"); // NOI18N
        String  column2 = NbBundle.getBundle(UpdateInfoPanel.class).getString("UpdateTableInfoModel.fileName"); // NOI18N
        String  column3 = NbBundle.getBundle(UpdateInfoPanel.class).getString("UpdateTableInfoModel.path"); // NOI18N
        try {
            Method method1 = classa.getMethod("getType", null);     // NOI18N
            Method method2 = classa.getMethod("getFile", null);     // NOI18N
            model.setColumnDefinition(0, column1, method1, true, new TypeComparator());
            model.setColumnDefinition(1, column2, method2, true, new FileComparator());
            model.setColumnDefinition(2, column3, method2, true, null);
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
        TableCellRenderer renderer = new ColoringUpdateRenderer(model);
        tblUpdates.setDefaultRenderer(tblUpdates.getColumnClass(0), renderer);
        tblUpdates.setDefaultRenderer(tblUpdates.getColumnClass(1), renderer);
        tblUpdates.setDefaultRenderer(tblUpdates.getColumnClass(2), renderer);
    }
    
    public void setVcsTask(CommandTask task){
        this.task = task;
    }
    
    public void setLog(StringBuffer buff){
        this.buff = buff;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        spCentral = new javax.swing.JScrollPane();
        tblUpdates = new javax.swing.JTable();
        toolbar = new javax.swing.JToolBar();
        btnStd = new javax.swing.JToggleButton();
        btnErr = new javax.swing.JToggleButton();

        setLayout(new java.awt.GridBagLayout());

        spCentral.setPreferredSize(new java.awt.Dimension(250, 60));
        tblUpdates.setModel(new javax.swing.table.DefaultTableModel(
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
        spCentral.setViewportView(tblUpdates);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 12, 11);
        add(spCentral, gridBagConstraints);

        toolbar.setBorder(null);
        toolbar.setRollover(true);
        btnStd.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnStd_mnc").charAt(0));
        btnStd.setSelected(true);
        btnStd.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnStd"));
        btnStd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStdActionPerformed(evt);
            }
        });

        toolbar.add(btnStd);
        btnStd.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnStd"));

        btnErr.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnErr_mnc").charAt(0));
        btnErr.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnErr"));
        btnErr.setEnabled(false);
        btnErr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnErrActionPerformed(evt);
            }
        });

        toolbar.add(btnErr);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        add(toolbar, gridBagConstraints);

    }//GEN-END:initComponents

    private void btnStdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStdActionPerformed
        if(errEnabled){
            btnErr.setSelected(false);
        }
        btnStd.setSelected(true);
        setStandardContent();
    }//GEN-LAST:event_btnStdActionPerformed

    private void btnErrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnErrActionPerformed
        btnStd.setSelected(false);
        btnErr.setSelected(true);
        setErrorContent();
    }//GEN-LAST:event_btnErrActionPerformed
    
    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        
    }//GEN-LAST:event_btnEditActionPerformed
    
    private void setErrorContent(){
        spCentral.setViewportView(errTextArea);
    }
    
    public JTextArea getErrOutputArea(){
        return errTextArea;
    }
    
    private void setStandardContent(){
        spCentral.setViewportView(tblUpdates);
            
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnErr;
    private javax.swing.JToggleButton btnStd;
    private javax.swing.JScrollPane spCentral;
    private javax.swing.JTable tblUpdates;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
    
    
    private void initAccessibility() {
        
        AccessibleContext context = this.getAccessibleContext();
        context.setAccessibleName(NbBundle.getBundle(UpdateInfoPanel.class).getString("ACSD_UpdateInfoPanel"));

        context = tblUpdates.getAccessibleContext();
        context.setAccessibleName(NbBundle.getBundle(UpdateInfoPanel.class).getString("ACSN_UpdateInfoPanel.tblUpdates"));
        context.setAccessibleDescription(NbBundle.getBundle(UpdateInfoPanel.class).getString("ACSD_UpdateInfoPanel.tblUpdates"));
       
        context = errTextArea.getAccessibleContext();
        context.setAccessibleName(NbBundle.getBundle(UpdateInfoPanel.class).getString("ACSN_UpdateInfoPanel.errTextArea"));
        context.setAccessibleDescription(NbBundle.getBundle(UpdateInfoPanel.class).getString("ACSD_UpdateInfoPanel.errTextArea"));
    }
    
    
    /** this method should be used with Choosers to display the dialog..
     * then later the displayOutputData will just present the datat gotten from server
     * Reason: to have something displayer right away..
     * This method is to be called when creating the command and Chooser.
     */
    public void displayFrameWork() {
        tblUpdates.setModel(model);
        TableColumn col = tblUpdates.getColumnModel().getColumn(0);
        col.setMaxWidth(40);
        
        stopActionListener = new StopActionListener();
        
    }
    
    protected void shutDownCommand() {
        // we can do that because it's running from other thread then command and won't kill itself
        if(this.task.isRunning())
            this.task.stop();
    }
    
    
    /** Does the actual display - docking into the javacvs Mode,
     *  displaying as single Dialog.. whatever.
     */
    private void displayOutputData() {
        JTableHeader head = tblUpdates.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(tblUpdates);
        head.addMouseListener(listen);
    }
    
    
    public void showExecutionFailed() {
     /*   if (wasSending) {
            lblSending.setText(" "); // NOI18N
            wasSending = false;
        }*/
        displayOutputData();
    }
    
    public void showFinishedCommand() {
     /*   if (wasSending) {
            lblSending.setText(" "); // NOI18N
            wasSending = false;
        }*/
        displayOutputData();
    }
    
    public void showStartCommand() {
        displayFrameWork();
    }
    
    public void showFileInfoGenerated(UpdateInformation info) {
      /*  if (wasSending) {
            lblSending.setText(" "); // NOI18N
            wasSending = false;
        }*/
        if (info instanceof UpdateInformation) {
            model.addElement(info);
            currentTimeStamp = System.currentTimeMillis();
            addedCount = addedCount + 1;
            totalCount = totalCount + 1;
            long tpDiff = currentTimeStamp - firedTimeStamp;
            if (totalCount < 100 || (addedCount > 5 && tpDiff > 500)) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        tblUpdates.changeSelection(model.getRowCount(), 0, false, false);
                    }
                });
                firedTimeStamp = System.currentTimeMillis();
                addedCount = 0;
            }
        }
    }
 
    
    private class ColoringUpdateRenderer extends DefaultTableCellRenderer {
        
        private TableInfoModel tableModel;
        
        public ColoringUpdateRenderer(TableInfoModel mod) {
            super();
            tableModel = mod;
        }
        
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable jTable, java.lang.Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component retValue;
            
            retValue = super.getTableCellRendererComponent(jTable, obj, isSelected, hasFocus, row, column);
            if ((isSelected) != true) {
                UpdateInformation info = (UpdateInformation)tableModel.getElementAt(row);
                if(info == null)
                    return retValue;
                String type = info.getType();
                if (type.equals("C")) { // NOI18N
                    retValue.setForeground(java.awt.Color.red);
                } else if (type.equals("A") || type.equals("R") ||  // NOI18N
                type.equals("M") || type.equals("G")) { // NOI18N
                    retValue.setForeground(java.awt.Color.blue);
                } else {
                    retValue.setForeground(java.awt.Color.black);
                }
            }
            return retValue;
        }
        
    }
    
    public class StopActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            shutDownCommand();
        }
    }
    
    public class CloseActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
           // visualizer.close();
        }
    }    

}
