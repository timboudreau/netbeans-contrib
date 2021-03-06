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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.log;

/**
 *
 * @author  mkleint, Richard Gregor
 */

import org.openide.util.*;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.FileInfoContainer;
import org.netbeans.modules.vcscore.util.table.*;

import java.util.*;
import java.io.File;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.GridBagConstraints;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.Dimension;
import java.lang.reflect.Method;
import javax.accessibility.*;


public class LogInfoPanel extends javax.swing.JPanel {
    private LogInformation logInfo;
    private boolean alreadyChanging = false;   
    private TableInfoModel symNamesModel;
    private TableInfoModel revisionModel;
    
    private javax.swing.JSplitPane sppMain;
    private javax.swing.JScrollPane spSymNames;
    private javax.swing.JTable tblSymNames;
    private javax.swing.JScrollPane spMain;
    private javax.swing.JTable tblRevisions;
    private JPanel listPanel;
    
   /**
    * Creates new form LogInfoPanel
    *
    */
    public LogInfoPanel(boolean isTag) {
        initComponents();
        initAccessibility();
        lblRepository.setDisplayedMnemonic(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblRepository.mnemonic").charAt(0)); //NOI18N
        lblRepository.setLabelFor(txRepository);
        lblLogMessage.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblLogMessage.mnemonic").charAt(0)); //NOI18N
        lblLogMessage.setLabelFor(taRevLog);
        listPanel = new JPanel(new java.awt.BorderLayout(0, 2));
        JLabel lblList = new JLabel(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblList.text")); //NOI18N
        lblList.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblList.mnemonic").charAt(0)); //NOI18N
        lblList.setLabelFor(tblSymNames);
        listPanel.add(lblList, java.awt.BorderLayout.NORTH);
        if (isTag) {            
            JSplitPane split = new JSplitPane();
            split.add(initSymNames(),JSplitPane.LEFT);
            split.add(initRevisionList(),JSplitPane.RIGHT);  
            split.setDividerLocation(200);
            listPanel.add(split, java.awt.BorderLayout.CENTER);
        } else {
            listPanel.add(initRevisionList(), java.awt.BorderLayout.CENTER);
            initSymNames();
        }
        GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.ipady = 0;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 11, 11);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 0.5;
        add(listPanel, gridBagConstraints1);
        fillSymNames();
    }
    
    
    private JComponent initSymNames() {
        spSymNames = new javax.swing.JScrollPane();
        tblSymNames = new javax.swing.JTable();
        JTableHeader head = tblSymNames.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(tblSymNames);
        head.addMouseListener(listen);
        tblSymNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSymNames.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                symNamesValueChanged();
            }
        });
        
        spSymNames.setViewportView(tblSymNames);
        AccessibleContext context = tblSymNames.getAccessibleContext();
        context.setAccessibleDescription(NbBundle.getMessage(LogInfoPanel.class, "ACSD_LogInfoPanel.tblSymNames"));
        context.setAccessibleName(NbBundle.getMessage(LogInfoPanel.class, "ACSN_LogInfoPanel.tblSymNames"));
        
        
        return spSymNames;
    }
    
    private JComponent initRevisionList() {
        spMain = new javax.swing.JScrollPane();
        spMain.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tblRevisions = new javax.swing.JTable();
        tblRevisions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRevisions.setRowSelectionAllowed(true);
        tblRevisions.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            //TODO
            public void valueChanged(ListSelectionEvent e) {
                int rowIndex = tblRevisions.getSelectedRow();
                if (rowIndex < 0) {
                    taRevLog.setText(""); // NOI18N
                    return;
                }
                String revision = (String)tblRevisions.getModel().getValueAt(rowIndex,0);
                // set the log message for the revision
                String text = logInfo.getRevision(revision).getMessage();
                taRevLog.setText(text);
            }
            
        });
        AccessibleContext context = tblRevisions.getAccessibleContext();
        context.setAccessibleDescription(NbBundle.getMessage(LogInfoPanel.class, "ACSD_LogInfoPanel.tblRevisions"));
        context.setAccessibleName(NbBundle.getMessage(LogInfoPanel.class, "ACSN_LogInfoPanel.tblRevisions"));
        
        JTableHeader head = tblRevisions.getTableHeader();
        head.setUpdateTableInRealTime(true);
        ColumnSortListener listen = new ColumnSortListener(tblRevisions);
        head.addMouseListener(listen);
        fillRevisions();
       // tblRevisions.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        spMain.setViewportView(tblRevisions);
        return spMain;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        pnlHead = new javax.swing.JPanel();
        lblRepository = new javax.swing.JLabel();
        txRepository = new javax.swing.JTextField();
        lblHeadRev = new javax.swing.JLabel();
        lblBranch = new javax.swing.JLabel();
        lblSelRev = new javax.swing.JLabel();
        lblTotalRev = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        txHeadRev = new javax.swing.JTextField();
        txBranch = new javax.swing.JTextField();
        txSelRev = new javax.swing.JTextField();
        txTotalRev = new javax.swing.JTextField();
        lblWorkFile = new javax.swing.JLabel();
        txWorkFile = new javax.swing.JTextField();
        lblLocks = new javax.swing.JLabel();
        txLocks = new javax.swing.JTextField();
        txDescription = new javax.swing.JTextField();
        spRevLog = new javax.swing.JScrollPane();
        taRevLog = new javax.swing.JEditorPane();
        lblLogMessage = new javax.swing.JLabel();

        jMenu1.setText("Menu");
        jMenuBar1.add(jMenu1);

        setLayout(new java.awt.GridBagLayout());

        pnlHead.setLayout(new java.awt.GridBagLayout());

        lblRepository.setLabelFor(txRepository);
        lblRepository.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblRepository.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(lblRepository, gridBagConstraints);
        lblRepository.getAccessibleContext().setAccessibleDescription(null);

        txRepository.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        pnlHead.add(txRepository, gridBagConstraints);
        txRepository.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.repFile"));
        txRepository.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.repFile"));

        lblHeadRev.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.lblHeadRevision_mnc").charAt(0));
        lblHeadRev.setLabelFor(txHeadRev);
        lblHeadRev.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblHeadRev.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(lblHeadRev, gridBagConstraints);
        lblHeadRev.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.headRev"));

        lblBranch.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.lblBranch_mnc").charAt(0));
        lblBranch.setLabelFor(txBranch);
        lblBranch.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblBranch.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 0);
        pnlHead.add(lblBranch, gridBagConstraints);

        lblSelRev.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.lblSelectedRevisions_mnc").charAt(0));
        lblSelRev.setLabelFor(txSelRev);
        lblSelRev.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblSelRev.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 0);
        pnlHead.add(lblSelRev, gridBagConstraints);

        lblTotalRev.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.lblTotalRev_mnc").charAt(0));
        lblTotalRev.setLabelFor(txTotalRev);
        lblTotalRev.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblTotalRev.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 0);
        pnlHead.add(lblTotalRev, gridBagConstraints);

        lblDescription.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.lblDescription_mnc").charAt(0));
        lblDescription.setLabelFor(txDescription);
        lblDescription.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblDescription.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 0);
        pnlHead.add(lblDescription, gridBagConstraints);

        txHeadRev.setEditable(false);
        txHeadRev.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(txHeadRev, gridBagConstraints);
        txHeadRev.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.repRevision"));
        txHeadRev.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.HeadRev"));

        txBranch.setEditable(false);
        txBranch.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(txBranch, gridBagConstraints);
        txBranch.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.Branch"));
        txBranch.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.Branch"));

        txSelRev.setEditable(false);
        txSelRev.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(txSelRev, gridBagConstraints);
        txSelRev.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.Rev"));
        txSelRev.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.Rev"));

        txTotalRev.setEditable(false);
        txTotalRev.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        pnlHead.add(txTotalRev, gridBagConstraints);
        txTotalRev.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.out"));
        txTotalRev.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.out"));

        lblWorkFile.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.lblFileName_mnc").charAt(0));
        lblWorkFile.setLabelFor(txWorkFile);
        lblWorkFile.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblWorkFile.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        pnlHead.add(lblWorkFile, gridBagConstraints);
        lblWorkFile.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.Filename"));

        txWorkFile.setEditable(false);
        txWorkFile.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(12, 13, 0, 0);
        pnlHead.add(txWorkFile, gridBagConstraints);
        txWorkFile.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel"));
        txWorkFile.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.filename"));

        lblLocks.setDisplayedMnemonic(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.lblLocks_mnc").charAt(0));
        lblLocks.setLabelFor(txLocks);
        lblLocks.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblLocks.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 17, 0, 0);
        pnlHead.add(lblLocks, gridBagConstraints);
        lblLocks.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.locks"));

        txLocks.setEditable(false);
        txLocks.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        pnlHead.add(txLocks, gridBagConstraints);
        txLocks.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.locks"));
        txLocks.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.locks"));

        txDescription.setEditable(false);
        txDescription.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 11);
        pnlHead.add(txDescription, gridBagConstraints);
        txDescription.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.Description"));
        txDescription.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACSD_LogInfoPanel.Description"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(pnlHead, gridBagConstraints);

        taRevLog.setEditable(false);
        taRevLog.setFont(new java.awt.Font("Default", java.awt.Font.PLAIN, taRevLog.getFont().getSize() - 1));
        spRevLog.setViewportView(taRevLog);
        taRevLog.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("ACS_LogInfoPanel.Log"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(spRevLog, gridBagConstraints);

        lblLogMessage.setLabelFor(taRevLog);
        lblLogMessage.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/visualizers/log/Bundle").getString("LogInfoPanel.lblLogMessage.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 2, 0);
        add(lblLogMessage, gridBagConstraints);

    }//GEN-END:initComponents
    
  private void lstSymNamesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstSymNamesValueChanged
      // Add your handling code here:
/*      if (alreadyChanging) {
          alreadyChanging = false;
          return;
      }
      String selName = (String)lstSymNames.getSelectedValue();
      String revName = logInfo.getRevisionForSymName(selName);
      LogInformation.Revision rev = logInfo.getRevision(revName);
      RevisionModel model = (RevisionModel)tblRevisions.getModel();
      int index = model.getRevisionIndex(rev.getNumber());
//      tblRevisions.getSelectionModel().setSelectionInterval(index, index);
      alreadyChanging = true;
      tblRevisions.changeSelection(index,0, false, false);
 */
  }//GEN-LAST:event_lstSymNamesValueChanged
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblBranch;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblHeadRev;
    private javax.swing.JLabel lblLocks;
    private javax.swing.JLabel lblLogMessage;
    private javax.swing.JLabel lblRepository;
    private javax.swing.JLabel lblSelRev;
    private javax.swing.JLabel lblTotalRev;
    private javax.swing.JLabel lblWorkFile;
    private javax.swing.JPanel pnlHead;
    private javax.swing.JScrollPane spRevLog;
    private javax.swing.JEditorPane taRevLog;
    private javax.swing.JTextField txBranch;
    private javax.swing.JTextField txDescription;
    private javax.swing.JTextField txHeadRev;
    private javax.swing.JTextField txLocks;
    private javax.swing.JTextField txRepository;
    private javax.swing.JTextField txSelRev;
    private javax.swing.JTextField txTotalRev;
    private javax.swing.JTextField txWorkFile;
    // End of variables declaration//GEN-END:variables
    
    private void initAccessibility() {
        
        AccessibleContext context = this.getAccessibleContext();
        context.setAccessibleDescription(NbBundle.getMessage(LogInfoPanel.class, "ACSD_LogInfoPanel"));
        
        context = txRepository.getAccessibleContext();
        context.setAccessibleDescription(NbBundle.getMessage(LogInfoPanel.class, "ACSD_LogInfoPanel.txRepository"));
        
        SelectAllListener focusListener = new SelectAllListener();
        
        txTotalRev.addFocusListener(focusListener);
        txWorkFile.addFocusListener(focusListener);
        txBranch.addFocusListener(focusListener);
        txDescription.addFocusListener(focusListener);
        txHeadRev.addFocusListener(focusListener);
        txLocks.addFocusListener(focusListener);
        txSelRev.addFocusListener(focusListener);
        txRepository.addFocusListener(focusListener);
    }

    class SelectAllListener implements java.awt.event.FocusListener{
        public void focusLost(java.awt.event.FocusEvent e){}
        public void focusGained(java.awt.event.FocusEvent e){
            ((javax.swing.JTextField)e.getComponent()).selectAll();
        }
    }
   
    
    private void symNamesValueChanged() {
        int row = tblSymNames.getSelectedRow();
        if (row < 0) return;
        if (!(tblSymNames.getModel() instanceof TableInfoModel)) return;
        if (!(tblRevisions.getModel() instanceof TableInfoModel)) return;
        TableInfoModel mod = (TableInfoModel)tblSymNames.getModel();
        if (row >= mod.getRowCount()) {
            tblRevisions.clearSelection();
            return;
        }
        String symNameRev = (String)mod.getValueAt(row, 1);
        TableInfoModel revModel = (TableInfoModel)tblRevisions.getModel();
        int revRow = -1;
        for (int i = 0; i < revModel.getRowCount(); i++) {
            String revis = (String)revModel.getValueAt(i,0);
            if (revis.equals(symNameRev)) {
                revRow = i;
            }
        }
        if (revRow == -1) {
            tblRevisions.clearSelection();
            return;
        }
        //      tblRevisions.setRowSelectionAllowed(true);
        tblRevisions.changeSelection(revRow,0,false,false);
    }
    
    public void setData(LogInformation info) {
        if(info == null)
            return;
        tblRevisions.clearSelection();
        tblSymNames.clearSelection();
        logInfo = info;
        txWorkFile.setText(logInfo.getFile().getName());
        txRepository.setText(logInfo.getRepositoryFilename());
        txHeadRev.setText(logInfo.getHeadRevision());
        txBranch.setText(logInfo.getBranch());
        txLocks.setText(logInfo.getLocks());
        txSelRev.setText(logInfo.getSelectedRevisions());
        txTotalRev.setText(logInfo.getTotalRevisions());
     /*   if (command.isHeaderOnly()) {
            pnlHead.remove(txDescription);
            pnlHead.remove(lblDescription);
        } else {*/
            txDescription.setText(logInfo.getDescription());
       // }
        // populating the list of symbolic names
        symNamesModel.clear();
      /*  if (command.isNoTags()) {
            //          sppMain.remove(spSymNames);
            //          sppMain.setDividerSize(1);
            //            sppMain.remove(spMain);
            //            sppMain.setLeftComponent(spMain);
        } else {*/
           Iterator it = info.getAllSymbolicNames().iterator();            
            while (it.hasNext()) {
                symNamesModel.addElement(it.next());
            }
            
            java.util.Collections.sort(symNamesModel.getList(), symNamesModel);
            // find the previsously selected row.
            tblSymNames.tableChanged(new TableModelEvent(symNamesModel));
            tblSymNames.repaint();
            
       // }
        revisionModel.clear();
     /*   if (command.isHeaderAndDescOnly() || command.isHeaderOnly()) {

            remove(spRevLog);
            
        } else {*/
            Iterator ite = logInfo.getRevisionList().iterator();
            while (ite.hasNext()) {
                revisionModel.addElement(ite.next());
            }
            java.util.Collections.sort(revisionModel.getList(), revisionModel);
            // find the previsously selected row.
            tblRevisions.tableChanged(new TableModelEvent(revisionModel));
            tblRevisions.repaint();
        //}
    }
    
    private void fillSymNames() {
        // setting the model....
        symNamesModel = new TableInfoModel();
        Class classa = LogInformation.SymName.class;
        String  column1 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.SymNamesColumn"); // NOI18N
        String  column2 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.Rev2Column"); // NOI18N
        try {
            Method method1 = classa.getMethod("getName", null);     // NOI18N
            Method method2 = classa.getMethod("getRevision", null);     // NOI18N
            symNamesModel.setColumnDefinition(0, column1, method1, true, null);
            symNamesModel.setColumnDefinition(1, column2, method2, true, new RevisionComparator());
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }        
        tblSymNames.setModel(symNamesModel);
    }
    
    private void fillRevisions() {
        // setting the model....
        revisionModel = new TableInfoModel();
        Class classa = LogInformation.Revision.class;
        String  column1 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.RevisionColumn"); // NOI18N
        String  column2 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.AuthorColumn"); // NOI18N
        String  column3 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.DateColumn"); // NOI18N
        String  column4 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.StateColumn"); // NOI18N
        String  column5 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.LinesColumn"); // NOI18N
        String  column6 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.LogMessage"); // NOI18N
        
        try {
            Method method1 = classa.getMethod("getNumber", null);     // NOI18N
            Method method2 = classa.getMethod("getAuthor", null);     // NOI18N
            Method method3 = classa.getMethod("getDateString", null);     // NOI18N
            Method method4 = classa.getMethod("getState", null);     // NOI18N
            Method method5 = classa.getMethod("getLines", null);     // NOI18N
            Method method6 = classa.getMethod("getMessage", null);     // NOI18N
            revisionModel.setColumnDefinition(0, column1, method1, true, new RevisionComparator());
            revisionModel.setColumnDefinition(1, column2, method2, true, null);
            revisionModel.setColumnDefinition(3, column3, method3, true, null);
            revisionModel.setColumnDefinition(4, column4, method4, true, null);
            revisionModel.setColumnDefinition(5, column5, method5, true, null);
            revisionModel.setColumnDefinition(2, column6, method6, true, new MessageComparator());
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
        // populationg revisions
        tblRevisions.setModel(revisionModel);
        createRevisionsColumnModel();
    }
    
    private void createRevisionsColumnModel() {
        TableColumnModel model = new DefaultTableColumnModel();
        // revision
        TableColumn col = new TableColumn();
        col.setIdentifier("Revision"); // NOI18N
        col.setModelIndex(0);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.RevisionColumn")); // NOI18N
        col.setMaxWidth(100);
        col.setMinWidth(50);
        col.setPreferredWidth(70);
        model.addColumn(col);
        // author
        col = new TableColumn();
        col.setIdentifier("Author"); // NOI18N
        col.setModelIndex(1);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.AuthorColumn")); // NOI18N
        col.setMaxWidth(150);
        col.setMinWidth(50);
        col.setPreferredWidth(75);
        model.addColumn(col);
        // message
        col = new TableColumn();
        col.setIdentifier("Message"); // NOI18N
        col.setModelIndex(2);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.LogMessage")); // NOI18N
        col.setMaxWidth(500);
        col.setMinWidth(50);
        col.setPreferredWidth(150);
        model.addColumn(col);
        // date
        col = new TableColumn();
        col.setIdentifier("Date"); // NOI18N
        col.setModelIndex(3);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.DateColumn")); // NOI18N
        col.setMaxWidth(120);
        col.setMinWidth(60);
        col.setPreferredWidth(110);
        model.addColumn(col);
        // state
        col = new TableColumn();
        col.setIdentifier("State"); // NOI18N
        col.setModelIndex(4);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.StateColumn")); // NOI18N
        col.setMaxWidth(100);
        col.setMinWidth(40);
        col.setPreferredWidth(50);
        model.addColumn(col);
        // lines
        col = new TableColumn();
        col.setIdentifier("Lines"); // NOI18N
        col.setModelIndex(5);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.LinesColumn")); // NOI18N
        col.setMaxWidth(100);
        col.setMinWidth(40);
        col.setPreferredWidth(85);
        model.addColumn(col);
        tblRevisions.setColumnModel(model);
    }
/*
  private void addSortingToRevisions(List listWithData) {
      JTableHeader head = tblRevisions.getTableHeader();
      head.setUpdateTableInRealTime(true);
      LogRevisionModel model = (LogRevisionModel)tblRevisions.getModel();
      ColumnSortListener listen = new ColumnSortListener(tblRevisions);
      head.addMouseListener(listen);
 
  }
 */
    /** in this method the displayer should use the data returned by the command to
     * produce it's own data structures/ fill in UI components
     * @param resultList - the data from the command. It is assumed the Displayer
     * knows what command the data comes from and most important in what format.
     * (which FileInfoContainer class is used).
     */
    public void setDataToDisplay(LogInformation info) {
        setData(info);
    }  
    
    public File getFileDisplayed() {
        if (logInfo == null) return null;
        return logInfo.getFile();
    }
    
    public Object getComparisonData() {
        return logInfo;
    }
    
    public JComponent getComponent() {
        return this;
    }
    
    /*
    // --------------------------------------------
    // persistantDisplayer stuff..
    // --------------------------------------------
    
    public boolean equalDisplayedData(File file, Class type, Object comparisonData) {
        if (!getClass().equals(type)) return false;
        if (logInfo == null || logInfo.getFile() == null
        || !logInfo.getFile().equals(file)) {
            return false;
        }
        return true;
    }
    
    
    public void closeNotify() {
        
    }  */     
     
    class MessageComparator implements  TableInfoComparator {
        
        public String getDisplayValue(Object obj, Object rowObject) {
            String message = obj.toString();
            int index = message.indexOf('\n');
            if (index > 0) {
                return message.substring(0,index);
            }
            return message;
        }
        
        public int compare(java.lang.Object obj, java.lang.Object obj1) {
            if (obj == null) return -1;
            if (obj1 == null) return 1;
            String str1 = obj.toString();
            String str2 = obj1.toString();
            return str1.compareTo(str2);
        }
        
    }
    
}
