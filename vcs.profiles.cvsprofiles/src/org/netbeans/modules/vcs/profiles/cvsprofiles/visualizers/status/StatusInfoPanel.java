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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.status;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.lang.reflect.Method;
import javax.accessibility.*;
import javax.swing.JComponent;
import javax.swing.JPanel;

//import java.util.ResourceBundle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.VcsCommandsProvider;

import org.netbeans.modules.vcscore.commands.TextOutputCommand;
import org.netbeans.modules.vcscore.util.table.*;
import org.netbeans.modules.vcscore.util.Debug;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.status.StatusInfoPanel.ExtendedRevisionComparator;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.*;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
//import org.openide.filesystems.FileStateInvalidException;

/**
 *
 * @author  Richard Gregor
 */
final class StatusInfoPanel extends JPanel {
    
    private static final String COMMAND_GET_TAGS = "STATUS_GET_TAGS"; // NOI18N
    private static final String COMMAND_DIFF = "DIFF"; // NOI18N
    
    private Color oldColor;
    private StatusInformation statusInfo;   
    private GridBagConstraints spExistingTagsConstraints;
    private GridBagConstraints lblExistingTagsConstraints;
    private GridBagLayout gridBag;
    private TableInfoModel model;
    private VcsCommandsProvider cmdProvider;
    
    /** 
     * Creates new form StatusInfoPanel 
     */
    public StatusInfoPanel(VcsCommandsProvider cmdProvider) {
        this.cmdProvider = cmdProvider;
        initComponents ();      
        pnlGetTags.setVisible(false);
        initAccessibility();
        lblRepFile.setDisplayedMnemonic (NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblRepFile.mnemonic").charAt(0)); // NOI18N
        lblRepFile.setLabelFor (txRepFile);
        btnDiff.setMnemonic (NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.btnDiff.mnemonic").charAt(0)); // NOI18N
        btnAdvanced.setMnemonic (NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.btnAdvanced.mnemonic").charAt(0)); // NOI18N
        lblExistingTags.setDisplayedMnemonic (NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblExistingTags.mnemonic").charAt(0)); // NOI18N
        lblExistingTags.setLabelFor (tblExistingTags);

        oldColor = txRepRev.getForeground();       
        // after setting the components unvisible, the layout gets forgotten :(
        gridBag  = (GridBagLayout)getLayout();
        spExistingTagsConstraints = gridBag.getConstraints(spExistingTags);
        lblExistingTagsConstraints = gridBag.getConstraints(lblExistingTags);
        javax.swing.table.JTableHeader head = tblExistingTags.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(tblExistingTags);
        head.addMouseListener(listen);
        setPreferredSize(new java.awt.Dimension(700, 450));
        setMinimumSize(new java.awt.Dimension(700, 450));        
      
      model = new TableInfoModel();
      Class classa = StatusInformation.SymName.class;
      String  column1 = NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.SymNamesColumn"); // NOI18N
      String  column2 = NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.Rev2Column"); // NOI18N
      try {
          Method method1 = classa.getMethod("getTag", null); // NOI18N
          Method method2 = classa.getMethod("getRevision", null); // NOI18N
          model.setColumnDefinition(0, column1, method1, true, null);
          model.setColumnDefinition(1, column2, method2, true, new ExtendedRevisionComparator());
      } catch (NoSuchMethodException exc) {
          Thread.dumpStack();
      } catch (SecurityException exc2) {
          Thread.dumpStack();
      }
      tblExistingTags.setModel(model);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblFileName = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblRepFile = new javax.swing.JLabel();
        txRepFile = new javax.swing.JTextField();
        lblWorkRev = new javax.swing.JLabel();
        lblRepRev = new javax.swing.JLabel();
        btnDiff = new javax.swing.JButton();
        lblTag = new javax.swing.JLabel();
        lblOptions = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        spExistingTags = new javax.swing.JScrollPane();
        tblExistingTags = new javax.swing.JTable();
        pnlGetTags = new javax.swing.JPanel();
        lblGetTagsRunning = new javax.swing.JLabel();
        lblExistingTags = new javax.swing.JLabel();
        btnAdvanced = new javax.swing.JButton();
        txFileName = new javax.swing.JTextField();
        txStatus = new javax.swing.JTextField();
        txWorkRev = new javax.swing.JTextField();
        txRepRev = new javax.swing.JTextField();
        txTag = new javax.swing.JTextField();
        txOptions = new javax.swing.JTextField();
        txDate = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        setMaximumSize(new java.awt.Dimension(354, 203));
        lblFileName.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("ACS_StatusInfoPanel.lblFilename_mnc").charAt(0));
        lblFileName.setLabelFor(txFileName);
        lblFileName.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblFileName.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(lblFileName, gridBagConstraints);

        lblStatus.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("ACS_StatusInfoPanel.lblStatus_mnc").charAt(0));
        lblStatus.setLabelFor(txStatus);
        lblStatus.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblStatus.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 17, 0, 0);
        add(lblStatus, gridBagConstraints);

        lblRepFile.setLabelFor(txRepFile);
        lblRepFile.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblRepFile.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblRepFile, gridBagConstraints);

        txRepFile.setEditable(false);
        txRepFile.setForeground(new java.awt.Color(102, 102, 158));
        txRepFile.setText("repFile");
        txRepFile.setDisabledTextColor(new java.awt.Color(102, 102, 153));
        txRepFile.setMinimumSize(new java.awt.Dimension(100, 20));
        txRepFile.setPreferredSize(new java.awt.Dimension(300, 20));
        txRepFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txRepFileActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txRepFile, gridBagConstraints);

        lblWorkRev.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("ACS_StatusInfoPanel.lblWorkingRevision").charAt(0));
        lblWorkRev.setLabelFor(txWorkRev);
        lblWorkRev.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblWorkRev.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblWorkRev, gridBagConstraints);

        lblRepRev.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("ACS_StatusInfoPanel.lblRepositoryRevision_mnc").charAt(0));
        lblRepRev.setLabelFor(txRepRev);
        lblRepRev.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblRepRev.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 0);
        add(lblRepRev, gridBagConstraints);

        btnDiff.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.btnDiff.text"));
        btnDiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiffActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 11);
        add(btnDiff, gridBagConstraints);

        lblTag.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("ACS_StatusInfoPanel.lblStickyTag_mnc").charAt(0));
        lblTag.setLabelFor(txTag);
        lblTag.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblTag.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(lblTag, gridBagConstraints);

        lblOptions.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("ACS_StatusInfoPanel.lblStickyOptions").charAt(0));
        lblOptions.setLabelFor(txOptions);
        lblOptions.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblOptions.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 17, 0, 0);
        add(lblOptions, gridBagConstraints);

        lblDate.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("ACS_StatusInfoPanel.lblStickyDate_mnc").charAt(0));
        lblDate.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblDate.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblDate, gridBagConstraints);

        spExistingTags.setMinimumSize(new java.awt.Dimension(100, 100));
        spExistingTags.setPreferredSize(new java.awt.Dimension(200, 200));
        spExistingTags.setViewportView(tblExistingTags);

        lblGetTagsRunning.setForeground(javax.swing.UIManager.getDefaults().getColor("CheckBoxMenuItem.acceleratorForeground"));
        lblGetTagsRunning.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblGetTagsRunning"));
        pnlGetTags.add(lblGetTagsRunning);

        spExistingTags.setViewportView(pnlGetTags);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 11, 0);
        add(spExistingTags, gridBagConstraints);

        lblExistingTags.setLabelFor(tblExistingTags);
        lblExistingTags.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.lblExistingTags.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(lblExistingTags, gridBagConstraints);

        btnAdvanced.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/status/Bundle").getString("StatusInfoPanel.btnAdvanced.text"));
        btnAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdvancedActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 11, 11);
        add(btnAdvanced, gridBagConstraints);

        txFileName.setColumns(20);
        txFileName.setEditable(false);
        txFileName.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(txFileName, gridBagConstraints);

        txStatus.setEditable(false);
        txStatus.setText("jTextField1");
        txStatus.setFont(txStatus.getFont().deriveFont(java.awt.Font.BOLD));
        txStatus.setForeground(java.awt.Color.blue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(12, 11, 0, 0);
        add(txStatus, gridBagConstraints);

        txWorkRev.setEditable(false);
        txWorkRev.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txWorkRev, gridBagConstraints);

        txRepRev.setEditable(false);
        txRepRev.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txRepRev, gridBagConstraints);

        txTag.setEditable(false);
        txTag.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(txTag, gridBagConstraints);

        txOptions.setEditable(false);
        txOptions.setText("jTextField1");
        txOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txOptionsActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(txOptions, gridBagConstraints);

        txDate.setEditable(false);
        txDate.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        add(txDate, gridBagConstraints);

    }//GEN-END:initComponents

    private void txOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txOptionsActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_txOptionsActionPerformed

    private void txRepFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txRepFileActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_txRepFileActionPerformed
    
  private void btnAdvancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdvancedActionPerformed
      spExistingTags.setViewportView(pnlGetTags);      
      pnlGetTags.setVisible(true);
      RequestProcessor.getDefault().post(new Runnable() {
          public void run() {
              File file = statusInfo.getFile();
              Command cmd = cmdProvider.createCommand(COMMAND_GET_TAGS);
              FileObject fo[] = FileUtil.fromFile(file);
              if (fo.length == 1) {
                  cmd.setFiles(fo);
              } else { // there're more then one, or none FileObject. Set simply the file.
                  ((VcsDescribedCommand) cmd).setDiskFiles(new File[] { file });
              }
      
              TextOutputCommand txtCmd = (TextOutputCommand) cmd;      
              CvsStatusVisualizer statVis = new CvsStatusVisualizer();       
              statVis.setFileFromInfo(file);
              txtCmd.addTextOutputListener(statVis);
              txtCmd.addTextErrorListener(statVis);

              CommandTask cmdTask = cmd.execute();
              cmdTask.waitFinished();
              int status = cmdTask.getExitStatus();
              if(status != 0){
                  pnlGetTags.setVisible(false);
                 // lblGetTagsRunning.setText(NbBundle.getMessage(StatusInfoPanel.class, "StatusInfoPanel.getTagsFailed"),NotifyDescriptor.ERROR_MESSAGE);
                  NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getMessage(StatusInfoPanel.class, "StatusInfoPanel.getTagsFailed"),NotifyDescriptor.ERROR_MESSAGE);
                  DialogDisplayer.getDefault().notify(nd);
              }
              final StatusInformation sInfo = statVis.getStatusInfo();
              statusInfo.setFile(sInfo.getFile());
              statusInfo.setRepositoryFileName(sInfo.getRepositoryFileName());
              statusInfo.setRepositoryRevision(sInfo.getRepositoryRevision());
             // statusInfo.setStatus(sInfo.getStatus());
              statusInfo.setStickyDate(sInfo.getStickyDate());
              statusInfo.setStickyOptions(sInfo.getStickyOptions());
              statusInfo.setStickyTag(sInfo.getStickyTag());
              statusInfo.setWorkingRevision(sInfo.getWorkingRevision());
              statusInfo.setAllExistingTags(sInfo.getAllExistingTags());
              javax.swing.SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                      spExistingTags.setViewportView(tblExistingTags);
                      setData(statusInfo);
                  }
              });
          }
      });
  }//GEN-LAST:event_btnAdvancedActionPerformed
  
  private void btnDiffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiffActionPerformed
      RequestProcessor.getDefault().post(new Runnable() {
          public void run() {
              File file = statusInfo.getFile();     
      
              Command cmd = cmdProvider.createCommand(COMMAND_DIFF);
              FileObject fo[] = FileUtil.fromFile(file);
              if (fo.length == 1) {
                  cmd.setFiles(fo);
              } else { // there're more then one, or none FileObject. Set simply the file.
                  ((VcsDescribedCommand) cmd).setDiskFiles(new File[] { file });
              }
              CommandTask cmdTask = cmd.execute();
              cmdTask.waitFinished();
              int status = cmdTask.getExitStatus();
              if (status != 0) {
                  NotifyDescriptor nd = new NotifyDescriptor.Message(NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.diffFailed"),NotifyDescriptor.ERROR_MESSAGE);
                  DialogDisplayer.getDefault().notify(nd);               
              }
          }
      });
  }//GEN-LAST:event_btnDiffActionPerformed
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdvanced;
    private javax.swing.JButton btnDiff;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblExistingTags;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblGetTagsRunning;
    private javax.swing.JLabel lblOptions;
    private javax.swing.JLabel lblRepFile;
    private javax.swing.JLabel lblRepRev;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTag;
    private javax.swing.JLabel lblWorkRev;
    private javax.swing.JPanel pnlGetTags;
    private javax.swing.JScrollPane spExistingTags;
    private javax.swing.JTable tblExistingTags;
    private javax.swing.JTextField txDate;
    private javax.swing.JTextField txFileName;
    private javax.swing.JTextField txOptions;
    private javax.swing.JTextField txRepFile;
    private javax.swing.JTextField txRepRev;
    private javax.swing.JTextField txStatus;
    private javax.swing.JTextField txTag;
    private javax.swing.JTextField txWorkRev;
    // End of variables declaration//GEN-END:variables

  private void initAccessibility() {
      
      AccessibleContext context = this.getAccessibleContext();
      context.setAccessibleDescription(NbBundle.getMessage(StatusInfoPanel.class, "ACSD_StatusInfoPanel"));
      
      context = btnDiff.getAccessibleContext();
      context.setAccessibleDescription(NbBundle.getMessage(StatusInfoPanel.class, "ACSD_StatusInfoPanel.btnDiff"));
      
      context = btnAdvanced.getAccessibleContext();
      context.setAccessibleDescription(NbBundle.getMessage(StatusInfoPanel.class, "ACSD_StatusInfoPanel.btnAdvanced"));
      
      context = txRepFile.getAccessibleContext();
      context.setAccessibleDescription(NbBundle.getMessage(StatusInfoPanel.class, "ACSD_StatusInfoPanel.txRepFile"));
      
      
      SelectAllListener focusListener = new SelectAllListener();
      
      txRepRev.addFocusListener(focusListener);
      txRepFile.addFocusListener(focusListener);
      txTag.addFocusListener(focusListener);
      txStatus.addFocusListener(focusListener);
      txWorkRev.addFocusListener(focusListener);
      txOptions.addFocusListener(focusListener);
      txFileName.addFocusListener(focusListener);
      txDate.addFocusListener(focusListener);      
            
  }
  
  class SelectAllListener implements java.awt.event.FocusListener{
      public void focusLost(java.awt.event.FocusEvent e){}
      public void focusGained(java.awt.event.FocusEvent e){
          ((javax.swing.JTextField)e.getComponent()).selectAll();
      }
  }

  public void setData(StatusInformation info) {      
      statusInfo = info;
      txFileName.setText(info.getFile().getName());      
      txStatus.setText(info.getStatusString());
      String work = info.getWorkingRevision();
      txWorkRev.setText(work);
      String repo = info.getRepositoryRevision();
      txRepFile.setText(info.getRepositoryFileName());
      txRepRev.setText(repo);
      if (work != null && repo != null) {
          if (!repo.equals(work)) { //possible stuff that can be done with the display
              txRepRev.setForeground(java.awt.Color.red);
              txWorkRev.setForeground(java.awt.Color.red);
          } else {
              txRepRev.setForeground(oldColor);
              txWorkRev.setForeground(oldColor);
          }
      }
      boolean uptodate = (info.getStatus() ==StatusInformation.UP_TO_DATE || info.getStatus() == StatusInformation.UNKNOWN);
      btnDiff.setEnabled(!uptodate);
      btnAdvanced.setEnabled(!(info.getStatus() == StatusInformation.UNKNOWN));
      txDate.setText(info.getStickyDate());
      txTag.setText(info.getStickyTag());
      txOptions.setText(info.getStickyOptions());

      model.clear();
      java.util.Iterator it = info.getAllExistingTags().iterator();
      while (it.hasNext()) {
          model.addElement(it.next());
      }
      TableInfoModel model = (TableInfoModel)tblExistingTags.getModel();
      java.util.Collections.sort(model.getList(), model);
        // find the previsously selected row.
      tblExistingTags.tableChanged(new javax.swing.event.TableModelEvent(model));
      tblExistingTags.repaint();
  }

    /** in this method the displayer should use the data returned by the command to
     * produce it's own data structures/ fill in UI components
     * @param resultList - the data from the command. It is assumed the Displayer
     *  knows what command the data comes from and most important in what format.
     * (which FileInfoContainer class is used).
     */
  public void setDataToDisplay(StatusInformation info) {
      setData(info);
  }
  
  public File getFileDisplayed() {
      return statusInfo.getFile();
  }
   
  public Object getComparisonData() {
      return statusInfo;
  }
   
  public JComponent getComponent() {
      return this;
  }

  public void closeNotify() {
      
  }
  
  class ExtendedRevisionComparator extends RevisionComparator {
      public int compare(java.lang.Object obj, java.lang.Object obj1) {
          int result = 0;
          String revStr1 = obj.toString();
          String revStr2 = obj1.toString();
          String substr1 = revStr1.substring(0, revStr1.indexOf(':'));
          String substr2 = revStr2.substring(0, revStr2.indexOf(':'));
          result = substr1.compareTo(substr2);
          if (result == 0) {
              result = super.compare(revStr1.substring(substr1.length() + 1),
                                revStr2.substring(substr2.length() + 1));
          }
          return result;
      }
  }
}
