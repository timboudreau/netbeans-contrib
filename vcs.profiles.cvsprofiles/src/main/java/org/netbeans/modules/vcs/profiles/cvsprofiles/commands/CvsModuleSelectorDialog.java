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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.awt.Point;
import java.util.*;
import javax.swing.table.*;
import javax.swing.*;

import org.openide.*;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;
import org.openide.DialogDisplayer;


/**
 *
 * @author  Martin Entlicher
 */
public class CvsModuleSelectorDialog extends javax.swing.JPanel {

    private Vector modules = null;
    //private volatile InformationDialog waitDlg = null;
    private javax.swing.ButtonGroup bg;
    private CvsModuleSelector model;
    private String[] args;

    private static HashMap cash = new HashMap ();

    static final long serialVersionUID = 1987612235843595460L;
    /** Creates new form CvsModuleSelectorDialog */
    public CvsModuleSelectorDialog(CvsModuleSelector model, String[] args) {
        super ();
        this.model = model;
        this.args = args;
        initComponents ();
        rbCheckoutModule.setMnemonic(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.rbCheckoutModule.text_Mnemonic").charAt(0));  // NOI18N
        rbIgnore.setMnemonic(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.rbIgnore.text_Mnemonic").charAt(0));  // NOI18N
        refresh.setMnemonic(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.refresh_Mnemonic").charAt(0));  // NOI18N
        tblModules.getTableHeader().setReorderingAllowed(true);
        javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup ();
        bg.add (rbCheckoutModule);
        bg.add (rbIgnore);
        initData();
        initAccessibility();
    }
    
    private void initAccessibility()
    {
        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("ACS_CvsModuleSelectorDialogA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("ACS_CvsModuleSelectorDialogA11yDesc"));  // NOI18N
        lblWarning.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("ACS_CvsModuleSelectorDialog.lbWarning.textA11yDesc"));  // NOI18N
        lblOptions.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("ACS_CvsModuleSelectorDialog.lblOptions.textA11yDesc"));  // NOI18N
        statusLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("ACS_CvsModuleSelectorDialog.statusLabelA11yDesc"));  // NOI18N
        tblModules.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("ACS_CvsModuleSelectorDialog.tableA11yName"));  // NOI18N
        tblModules.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("ACS_CvsModuleSelectorDialog.tableA11yDesc"));  // NOI18N
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblWarning = new javax.swing.JLabel();
        lblOptions = new javax.swing.JLabel();
        rbCheckoutModule = new javax.swing.JRadioButton();
        rbIgnore = new javax.swing.JRadioButton();
        statusLabel = new javax.swing.JLabel();
        refresh = new javax.swing.JButton();
        listScrollPane = new javax.swing.JScrollPane();
        tblModules = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        taPaths = new javax.swing.JTextArea();
        lbPaths = new javax.swing.JLabel();
        lbModules = new javax.swing.JLabel();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(440, 469));
        lblWarning.setLabelFor(lblOptions);
        lblWarning.setText(org.openide.util.NbBundle.getMessage(CvsModuleSelectorDialog.class, "CvsModuleSelectorDialog.lbWarning.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        add(lblWarning, gridBagConstraints);

        lblOptions.setLabelFor(rbCheckoutModule);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        add(lblOptions, gridBagConstraints);

        rbCheckoutModule.setSelected(true);
        rbCheckoutModule.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        add(rbCheckoutModule, gridBagConstraints);

        rbIgnore.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        add(rbIgnore, gridBagConstraints);

        statusLabel.setLabelFor(listScrollPane);
        statusLabel.setText("kuk");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 6);
        add(statusLabel, gridBagConstraints);

        refresh.setText(org.openide.util.NbBundle.getMessage(CvsModuleSelectorDialog.class, "CvsModuleSelectorDialog.refresh"));
        refresh.setToolTipText(org.openide.util.NbBundle.getMessage(CvsModuleSelectorDialog.class, "ACS_CvsModuleSelectorDialog.refreshA11yDesc"));
        refresh.addActionListener(formListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 12);
        add(refresh, gridBagConstraints);

        tblModules.setPreferredScrollableViewportSize(new java.awt.Dimension(300, 200));
        listScrollPane.setViewportView(tblModules);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 6, 12);
        add(listScrollPane, gridBagConstraints);

        taPaths.setEditable(false);
        jScrollPane1.setViewportView(taPaths);
        taPaths.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/Bundle").getString("ACS_CvsModuleSelectorDialog.taPaths_Name"));
        taPaths.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/Bundle").getString("ACS_CvsModuleSelectorDialog.taPaths_Desc"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        add(jScrollPane1, gridBagConstraints);

        lbPaths.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/Bundle").getString("ACS_CvsModuleSelectorDialog_paths_mnc").charAt(0));
        lbPaths.setLabelFor(taPaths);
        lbPaths.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/Bundle").getString("CvsSelectorModuleDialog.pathsLBL"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        add(lbPaths, gridBagConstraints);

        lbModules.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/Bundle").getString("CvsSelectorModuleDialog.lbModules_mnc").charAt(0));
        lbModules.setLabelFor(tblModules);
        lbModules.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/Bundle").getString("CvsSelectorModuleDialog.lbModules"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        add(lbModules, gridBagConstraints);
        lbModules.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/Bundle").getString("CvsSelectorModuleDialog.lbModules"));

    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == rbCheckoutModule) {
                CvsModuleSelectorDialog.this.rbCheckoutModuleActionPerformed(evt);
            }
            else if (evt.getSource() == rbIgnore) {
                CvsModuleSelectorDialog.this.rbIgnoreActionPerformed(evt);
            }
            else if (evt.getSource() == refresh) {
                CvsModuleSelectorDialog.this.refreshModules(evt);
            }
        }
    }//GEN-END:initComponents

    private void refreshModules(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshModules
        // Add your handling code here:
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Thread("ModuleSelector-Command") {
                    public void run() {
                        waitingForModules();
                        Vector modules = CvsModuleSelectorDialog.this.model.getModulesList (CvsModuleSelectorDialog.this.args);
                        boolean cmdSuccess = (modules != null);
                        Hashtable vars = CvsModuleSelectorDialog.this.model.getVariables();
                        cash.put(createModuleKey(vars), modules);
                        /*
                        VcsFileSystem fs = CvsModuleSelectorDialog.this.model.getFileSystem();
                        if (fs instanceof CvsFileSystem) {
                            CvsFileSystem cvsFs = (CvsFileSystem)fs;
                            String key = ":"+cvsFs.getCvsServerType()+":"+cvsFs.getCvsUserName()+"@"+cvsFs.getCvsServer()+":"+cvsFs.getCvsRoot();
                            cash.put (key, modules);
                        }
                         */
                        setModules(modules, cmdSuccess);    
                   }
                }.start();
            }
        });
    }//GEN-LAST:event_refreshModules

  private void rbIgnoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbIgnoreActionPerformed
// Add your handling code here:
      tblModules.setEnabled(false);
      tblModules.getSelectionModel().clearSelection();
  }//GEN-LAST:event_rbIgnoreActionPerformed

  private void rbCheckoutModuleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCheckoutModuleActionPerformed
// Add your handling code here:
      tblModules.setEnabled(true);
  }//GEN-LAST:event_rbCheckoutModuleActionPerformed

  
    private String createModuleKey(Hashtable vars) {
        String userName = (String) vars.get("CVS_USERNAME");
        if (userName == null) userName = "";
        String server = (String) vars.get("CVS_SERVER");
        if (server == null) server = "";
        String key = ":"+vars.get("SERVERTYPE")+":"+userName+"@"+server+":"+vars.get("CVS_REPOSITORY");
        return key;
    }
    
    
    public void calledAsCommand(boolean yeah) {
        if (yeah) {
            lblWarning.setVisible(false);
            lblOptions.setVisible(false);
            rbCheckoutModule.setVisible(false);
            rbIgnore.setVisible(false);
        } else {
            lblWarning.setVisible(true);
            lblOptions.setVisible(true);
            rbCheckoutModule.setVisible(true);
            rbIgnore.setVisible(true);
        }
    }
    
    public void waitingForModules() {
        statusLabel.setText(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.waitingForModules"));
        /*
        if (waitDlg == null) {
          javax.swing.SwingUtilities.invokeLater(new Runnable () {
            public void run () {
              waitDlg = new InformationDialog(new java.awt.Frame(), true, "Please wait, modules are loading ...");
              MiscStuff.centerWindow(waitDlg);
              waitDlg.show();
            }
          });
    }
         */
    }
    
    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     *
    private void initColumnSizes(JTable table, TableModel model) {
        TableColumn column = null;
        java.awt.Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        int columns = model.getColumnCount();
     
        for (int i = 0; i < columns; i++) {
            column = table.getColumnModel().getColumn(i);
     
            comp = column.getHeaderRenderer().
                             getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
            /*
            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, model.getValueAt(, i),
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
     *
            column.setPreferredWidth(headerWidth); //Math.max(headerWidth, cellWidth));
        }
    }
     */
    
    public void setModules(Vector modules, boolean success) {
        this.modules = modules;
        //if (waitDlg != null) waitDlg.setVisible(false);
        if (success) statusLabel.setText(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.modulesLoaded"));
        else statusLabel.setText(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.modulesLoadError"));
        if (modules == null || modules.size() == 0) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.noModules"));
                    DialogDisplayer.getDefault().notify(nd);
                }
            });
            rbCheckoutModule.setEnabled(false);            
        } else {
            Vector columns = new Vector();
            columns.add(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.columnModule"));            
            columns.add(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.columnStatus"));
            columns.add(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.columnType"));
            columns.add(org.openide.util.NbBundle.getBundle(CvsModuleSelectorDialog.class).getString("CvsModuleSelectorDialog.columnPaths"));
            DefaultTableModel model = new MyDefaultTableModel(modules, columns);
            TableSorter sorter = new TableSorter(model);
            tblModules.setModel(sorter);
            tblModules.getSelectionModel().addListSelectionListener(
              new javax.swing.event.ListSelectionListener() {
                  public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                      changePaths(e);
                  }
            });
            TableColumn col = tblModules.getColumnModel().getColumn(1);
            col.setPreferredWidth(40);
            col = tblModules.getColumnModel().getColumn(0);
            col.setPreferredWidth(120);
            col = tblModules.getColumnModel().getColumn(2);
            col.setPreferredWidth(80); 
            sorter.addMouseListenerToHeaderInTable(tblModules);
            sorter.sortByColumn(0, true); 
            listScrollPane.validate();           
        }
    }
    
    public void changePaths(javax.swing.event.ListSelectionEvent event) {
        if (tblModules.getSelectedRowCount() != 1) {
            taPaths.setText(""); //NOI18N
            return;
        }
        int first = tblModules.getSelectedRow();
        String path = (String)tblModules.getModel().getValueAt(first,3);
        path.trim();
        if (path != null) {
            taPaths.setText(path.replace(' ', '\n'));
        }else
            taPaths.setText("");        
    }
    
    public String[] getSelection() {
        if (tblModules.getSelectedRowCount() == 0) {return null;}
        int[] select = tblModules.getSelectedRows();
        TableModel model = tblModules.getModel();
        Vector selStr = new Vector();
        for (int ind = 0; ind < select.length; ind++) {
            String mod = (String)model.getValueAt(select[ind], 0);
            selStr.add(mod);
        }
        String[] modulesStrs = (String[]) selStr.toArray(new String[0]);
        return modulesStrs;
    }
    
    private void initData() {
        Vector modules = null;
        Hashtable vars = this.model.getVariables();
        modules = (Vector) cash.get(createModuleKey(vars));
        if (modules == null) {
            this.refreshModules(null);
        }
        else {
            setModules(modules,true);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbModules;
    private javax.swing.JLabel lbPaths;
    private javax.swing.JLabel lblOptions;
    private javax.swing.JLabel lblWarning;
    private javax.swing.JScrollPane listScrollPane;
    private javax.swing.JRadioButton rbCheckoutModule;
    private javax.swing.JRadioButton rbIgnore;
    private javax.swing.JButton refresh;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTextArea taPaths;
    private javax.swing.JTable tblModules;
    // End of variables declaration//GEN-END:variables

    
    private static class MyDefaultTableModel extends DefaultTableModel {
       
       static final long serialVersionUID = 4907156662613927521L;       

      public MyDefaultTableModel (Vector data, Vector columns) {
        super(data, columns);   
      }    
           
      public boolean isCellEditable(int row, int column) {
        return false;  
      }    
 
   }    
    
}

