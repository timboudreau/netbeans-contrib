
/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.ui.views.types;

/**
 *
 * @author  mkleint
 */

import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.awt.SplittedPanel;
import org.openide.nodes.*;

import org.netbeans.modules.vcscore.ui.views.*;
import org.netbeans.modules.vcscore.ui.views.actions.*;
import org.netbeans.modules.vcscore.util.table.*;

import java.util.*;
import java.beans.*;
import java.io.File;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.awt.Dimension;
import java.lang.reflect.Method;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.accessibility.*;

public class LogInfoPanel extends SingleNodeView {
    
    public static final String REPOSITORY_FILENAME = "REPOSITORY_FILENAME"; //NOI18N
    public static final String ACCESS_LIST = "ACCESS_LIST"; //NOI18N
    public static final String BRANCH = "BRANCH"; //NOI18N
    public static final String DESCRIPTION = "DESCRIPTION"; //NOI18N
    public static final String HEAD_REVISION = "HEAD_REVISION"; //NOI18N
    public static final String KEYWORD_SUBSTITUTION = "KEYWORD_SUBSTITUTION"; //NOI18N
    public static final String LOCKS = "LOCKS"; //NOI18N
    public static final String TOTAL_REVISIONS = "TOTAL_REVISIONS"; //NOI18N
    public static final String SELECTED_REVISIONS = "SELECTED_REVISIONS"; //NOI18N
    public static final String SYM_NAMES_LIST = "SYM_NAMES_LIST"; //NOI18N
    public static final String REVISIONS_LIST = "REVISIONS_LIST"; //NOI18N
    public static final String SYM_NAME_NAME = "SYM_NAMES_NAME"; //NOI18N
    public static final String SYM_NAME_REVISION = "SYM_NAMES_REVISION"; //NOI18N
    public static final String REVISION_AUTHOR = "REVISION_AUTHOR"; //NOI18N
    public static final String REVISION_BRANCHES = "REVISION_BRANCHES"; //NOI18N
    public static final String REVISION_DATE = "REVISION_DATE"; //NOI18N
    public static final String REVISION_LINES = "REVISION_LINES"; //NOI18N
    public static final String REVISION_MESSAGE = "REVISION_MESSAGE";     //NOI18N
    public static final String REVISION_NUMBER = "REVISION_NUMBER"; //NOI18N
    public static final String REVISION_STATE = "REVISION_STATE"; //NOI18N
    
    public static final String TYPE = "LOG"; //NOI18N
    
    private boolean alreadyChanging = false;

    private FileVcsInfo currentInfo;
    
    private TableView tblRevisions;
    
    private FileVcsInfo clearInfo;
    
    private transient PropertyChangeListener messageListener;
    
    /** Creates new form LogInfoPanel */
    
    
    public LogInfoPanel() {
        initComponents();
        initAccessibility();
        lblRepository.setDisplayedMnemonic(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblRepository.mnemonic").charAt(0)); //NOI18N
        lblRepository.setLabelFor(txRepository);
        lblLogMessage.setDisplayedMnemonic(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblLogMessage.mnemonic").charAt(0)); //NOI18N
        lblLogMessage.setLabelFor(taRevLog);

        JPanel listPanel = new JPanel(new java.awt.BorderLayout(0, 2));
        //        if (!comm.isNoTags()) {
        SplittedPanel split = new SplittedPanel();
        split.setPreferredSize(new java.awt.Dimension(600, 350));
        split.setMinimumSize(new java.awt.Dimension(600, 250));
        JComponent symNames = initSymNames();
        JLabel lblList = new JLabel(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblList.text")); //NOI18N
        lblList.setDisplayedMnemonic(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblList.mnemonic").charAt(0)); //NOI18N
        lblList.setLabelFor(symNames);
        listPanel.add(lblList, java.awt.BorderLayout.NORTH);
        split.add(symNames, SplittedPanel.ADD_LEFT);
        
        tblRevisions = (TableView)initRevisionList();
        CallableSystemAction[] actions = new CallableSystemAction[3];
        actions[0] = (CallableSystemAction)SharedClassObject.findObject(OpenRevisionAction.class, true);
        actions[1] = (CallableSystemAction)SharedClassObject.findObject(UpdateRevisionAction.class, true);
        actions[2] = (CallableSystemAction)SharedClassObject.findObject(DiffRevisionAction.class, true);
        tblRevisions.setAdditionalActions(actions);
        split.add(tblRevisions, SplittedPanel.ADD_RIGHT);
        split.setSplitAbsolute(false);
        split.setSplitDragable(true);
        split.setSplitPosition(30);
        listPanel.add(split, java.awt.BorderLayout.CENTER);
/*        } else {
            listPanel.add(initRevisionList(), java.awt.BorderLayout.CENTER);
            initSymNames();
        }
 */
        GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.ipady = 0;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 11, 11);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 0.5;
        add(listPanel, gridBagConstraints1);
        
        setPreferredSize(new java.awt.Dimension(750, 400));
        setMinimumSize(new java.awt.Dimension(750, 400));
        initClearInfo();
        messageListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (FileVcsInfo.Composite.PROPERTY_SELECTED_ITEMS.equals(event.getPropertyName())) {
                    FileVcsInfo.Composite source = (FileVcsInfo.Composite)event.getSource();
                    FileVcsInfo.CompositeItem[] items = source.getSelectedItems();
                    if (source.getType().equals(LogInfoPanel.REVISIONS_LIST)) {
                        if (items != null && items.length == 1) {
                            taRevLog.setText(items[0].getAttributeNonNull(REVISION_MESSAGE));
                        } else {
                            taRevLog.setText(""); //NOI18N
                        }
                    }
                    if (source.getType().equals(SYM_NAMES_LIST)) {
                        if (currentInfo != null && items != null && items.length == 1) {
                            String revision = items[0].getAttributeNonNull(SYM_NAME_REVISION);
                            FileVcsInfo.Composite comp = (FileVcsInfo.Composite)currentInfo.getAttribute(REVISIONS_LIST);
                            if (comp != null) {
                                for (int i = 0; i < comp.getCount(); i++) {
                                    if (comp.getRow(i).getAttributeNonNull(REVISION_NUMBER).equals(revision)) {
                                        tblRevisions.setSelectedItems(new FileVcsInfo.CompositeItem[] {comp.getRow(i)}, true);
                                        return;
                                    }
                                }
                                tblRevisions.setSelectedItems(null, true);
                            }
                            
                        }
                    }
                }
            }
        };
    }
    
    
    
    
    private JComponent initSymNames() {
        TableInfoModel model = new TableInfoModel();
        Class classa = FileVcsInfo.CompositeItem.class;
        String  column1 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.SymNamesColumn"); // NOI18N
        String  column2 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.Rev2Column"); // NOI18N
        try {
            Method method1 = classa.getMethod("getAttributeNonNull", new Class[] {String.class}); // NOI18N
            model.setColumnDefinition(0, column1, method1, new Object[] { SYM_NAME_NAME}, true, null);
            model.setColumnDefinition(1, column2, method1, new Object[] { SYM_NAME_REVISION}, true, new RevisionComparator());
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
        
        TableView view = new TableView(SYM_NAMES_LIST, model);
        return view;
    }
    
    private JComponent initRevisionList() {
        // setting the model....
        TableInfoModel revisionModel = new TableInfoModel();
        Class classa = FileVcsInfo.CompositeItem.class;
        String  column1 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.RevisionColumn"); // NOI18N
        String  column2 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.AuthorColumn"); // NOI18N
        String  column3 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.DateColumn"); // NOI18N
        String  column4 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.StateColumn"); // NOI18N
        String  column5 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.LinesColumn"); // NOI18N
        String  column6 = NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.LogMessage"); // NOI18N
        
        try {
            Method method1 = classa.getMethod("getAttributeNonNull", new Class[] {String.class}); // NOI18N
            revisionModel.setColumnDefinition(0, column1, method1, new Object[] { REVISION_NUMBER }, true, new RevisionComparator());
            revisionModel.setColumnDefinition(1, column2, method1, new Object[] { REVISION_AUTHOR }, true, null);
            revisionModel.setColumnDefinition(2, column3, method1, new Object[] { REVISION_DATE }, true, null);
            revisionModel.setColumnDefinition(3, column4, method1, new Object[] { REVISION_STATE }, true, null);
            revisionModel.setColumnDefinition(4, column5, method1, new Object[] { REVISION_LINES }, true, null);
            revisionModel.setColumnDefinition(5, column6, method1, new Object[] { REVISION_MESSAGE }, true, new MessageComparator());
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
        TableView view = new TableView(REVISIONS_LIST, revisionModel);
        view.setColumnModel(createRevisionsColumnModel());
        return view;
    }
    
    private void initClearInfo() {
        clearInfo = FileVcsInfoFactory.createBlankFileVcsInfo(LogInfoPanel.TYPE, new File(""));
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
        jPanel1 = new javax.swing.JPanel();
        lblWorkFile = new javax.swing.JLabel();
        txWorkFile = new javax.swing.JLabel();
        lblLocks = new javax.swing.JLabel();
        txLocks = new javax.swing.JLabel();
        lblRepository = new javax.swing.JLabel();
        txRepository = new javax.swing.JTextField();
        lblHeadRev = new javax.swing.JLabel();
        txHeadRev = new javax.swing.JLabel();
        lblBranch = new javax.swing.JLabel();
        txBranch = new javax.swing.JLabel();
        lblSelRev = new javax.swing.JLabel();
        txSelRev = new javax.swing.JLabel();
        lblTotalRev = new javax.swing.JLabel();
        txTotalRev = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        txDescription = new javax.swing.JLabel();
        spRevLog = new javax.swing.JScrollPane();
        taRevLog = new javax.swing.JEditorPane();
        lblLogMessage = new javax.swing.JLabel();

        jMenu1.setText("Menu");
        jMenuBar1.add(jMenu1);

        setLayout(new java.awt.GridBagLayout());

        pnlHead.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblWorkFile.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblWorkFile.text"));
        lblWorkFile.setLabelFor(txWorkFile);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        jPanel1.add(lblWorkFile, gridBagConstraints);

        txWorkFile.setForeground(java.awt.Color.black);
        txWorkFile.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        jPanel1.add(txWorkFile, gridBagConstraints);

        lblLocks.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblLocks.text"));
        lblLocks.setLabelFor(txLocks);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 17, 0, 0);
        jPanel1.add(lblLocks, gridBagConstraints);

        txLocks.setForeground(java.awt.Color.black);
        txLocks.setText("jLabel11");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        jPanel1.add(txLocks, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        pnlHead.add(jPanel1, gridBagConstraints);

        lblRepository.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblRepository.text"));
        lblRepository.setLabelFor(txRepository);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(lblRepository, gridBagConstraints);

        txRepository.setEditable(false);
        txRepository.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        pnlHead.add(txRepository, gridBagConstraints);

        lblHeadRev.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblHeadRev.text"));
        lblHeadRev.setLabelFor(txHeadRev);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(lblHeadRev, gridBagConstraints);

        txHeadRev.setForeground(java.awt.Color.black);
        txHeadRev.setText("jLabel6");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(txHeadRev, gridBagConstraints);

        lblBranch.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblBranch.text"));
        lblBranch.setLabelFor(txBranch);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 0);
        pnlHead.add(lblBranch, gridBagConstraints);

        txBranch.setForeground(java.awt.Color.black);
        txBranch.setText("jLabel8");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(txBranch, gridBagConstraints);

        lblSelRev.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblSelRev.text"));
        lblSelRev.setLabelFor(txSelRev);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 0);
        pnlHead.add(lblSelRev, gridBagConstraints);

        txSelRev.setForeground(java.awt.Color.black);
        txSelRev.setText("jLabel16");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        pnlHead.add(txSelRev, gridBagConstraints);

        lblTotalRev.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblTotalRev.text"));
        lblTotalRev.setLabelFor(txTotalRev);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 0, 0);
        pnlHead.add(lblTotalRev, gridBagConstraints);

        txTotalRev.setForeground(java.awt.Color.black);
        txTotalRev.setText("jLabel18");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        pnlHead.add(txTotalRev, gridBagConstraints);

        lblDescription.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblDescription.text"));
        lblDescription.setLabelFor(txDescription);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 0);
        pnlHead.add(lblDescription, gridBagConstraints);

        txDescription.setForeground(java.awt.Color.black);
        txDescription.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 11);
        pnlHead.add(txDescription, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(pnlHead, gridBagConstraints);

        spRevLog.setMinimumSize(new java.awt.Dimension(300, 100));
        spRevLog.setPreferredSize(new java.awt.Dimension(300, 100));
        taRevLog.setEditable(false);
        taRevLog.setFont(new java.awt.Font("Default", java.awt.Font.PLAIN, taRevLog.getFont().getSize() - 1));
        taRevLog.setMinimumSize(new java.awt.Dimension(300, 40));
        spRevLog.setViewportView(taRevLog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(spRevLog, gridBagConstraints);

        lblLogMessage.setText(org.openide.util.NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.lblLogMessage.text"));
        lblLogMessage.setLabelFor(taRevLog);
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
      D.deb("selName=" + selName);
      String revName = logInfo.getRevisionForSymName(selName);
      D.deb("revisionName=" + revName);
      LogInformation.Revision rev = logInfo.getRevision(revName);
      D.deb("Rev=" + rev);
      RevisionModel model = (RevisionModel)tblRevisions.getModel();
      int index = model.getRevisionIndex(rev.getNumber());
//      tblRevisions.getSelectionModel().setSelectionInterval(index, index);
      alreadyChanging = true;
      tblRevisions.changeSelection(index,0, false, false);
 */
  }//GEN-LAST:event_lstSymNamesValueChanged
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblSelRev;
    private javax.swing.JLabel txHeadRev;
    private javax.swing.JLabel lblLogMessage;
    private javax.swing.JLabel lblBranch;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JEditorPane taRevLog;
    private javax.swing.JTextField txRepository;
    private javax.swing.JPanel pnlHead;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel txTotalRev;
    private javax.swing.JLabel lblRepository;
    private javax.swing.JScrollPane spRevLog;
    private javax.swing.JLabel txDescription;
    private javax.swing.JLabel lblTotalRev;
    private javax.swing.JLabel lblLocks;
    private javax.swing.JLabel txWorkFile;
    private javax.swing.JLabel txSelRev;
    private javax.swing.JLabel lblHeadRev;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel txBranch;
    private javax.swing.JLabel txLocks;
    private javax.swing.JLabel lblWorkFile;
    // End of variables declaration//GEN-END:variables
    
    private static final java.util.ResourceBundle bundle = NbBundle.getBundle(LogInfoPanel.class);   // NOI18N
    
    private void initAccessibility() {
        
        AccessibleContext context = this.getAccessibleContext();
        context.setAccessibleDescription(bundle.getString("ACSD_LogInfoPanel")); //NOI18N
        
        context = txRepository.getAccessibleContext();
        context.setAccessibleDescription(bundle.getString("ACSD_LogInfoPanel.txRepository")); //NOI18N

    }
    
/*    private void symNamesValueChanged() {
        int row = tblSymNames.getSelectedRow();
        if (row < 0) return;
        if (!(tblSymNames.getModel() instanceof TableInfoModel)) return;
        if (!(tblRevisions.getModel() instanceof TableInfoModel)) return;
        TableInfoModel mod = (TableInfoModel)tblSymNames.getModel();
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
 */
    
    public void setData(FileVcsInfo info) {
        if (currentInfo != null) {
            FileVcsInfo.Composite comp = (FileVcsInfo.Composite)currentInfo.getAttribute(REVISIONS_LIST);
            if (comp != null) {
                comp.removePropertyChangeListener(messageListener);
            }
            comp = (FileVcsInfo.Composite)currentInfo.getAttribute(SYM_NAMES_LIST);
            if (comp != null) {
                comp.removePropertyChangeListener(messageListener);
            }
        }
        
        currentInfo = info;
        txWorkFile.setText(info.getFile().getName());
        txRepository.setText(info.getAttributeNonNull(REPOSITORY_FILENAME));
        txHeadRev.setText(info.getAttributeNonNull(HEAD_REVISION));
        txBranch.setText(info.getAttributeNonNull(BRANCH));
        txLocks.setText(info.getAttributeNonNull(LOCKS));
        txSelRev.setText(info.getAttributeNonNull(SELECTED_REVISIONS));
        txTotalRev.setText(info.getAttributeNonNull(TOTAL_REVISIONS));

        FileVcsInfo.Composite comp = (FileVcsInfo.Composite)currentInfo.getAttribute(REVISIONS_LIST);
        if (comp != null) {
            comp.addPropertyChangeListener(messageListener);
        }
        comp = (FileVcsInfo.Composite)currentInfo.getAttribute(SYM_NAMES_LIST);
        if (comp != null) {
            comp.addPropertyChangeListener(messageListener);
        }
        txDescription.setText(info.getAttributeNonNull(DESCRIPTION));
    }
    
    
    private TableColumnModel createRevisionsColumnModel() {
        TableColumnModel model = new DefaultTableColumnModel();
        // revision
        TableColumn col = new TableColumn();
        col.setIdentifier("Revision"); // NOI18N
        col.setModelIndex(0);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.RevisionColumn")); // NOI18N
        col.setMaxWidth(100);
        col.setMinWidth(50);
        col.setPreferredWidth(50);
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
        // date
        col = new TableColumn();
        col.setIdentifier("Date"); // NOI18N
        col.setModelIndex(2);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.DateColumn")); // NOI18N
        col.setMaxWidth(120);
        col.setMinWidth(10);
        col.setPreferredWidth(110);
        model.addColumn(col);
        // state
        col = new TableColumn();
        col.setIdentifier("State"); // NOI18N
        col.setModelIndex(3);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.StateColumn")); // NOI18N
        col.setMaxWidth(100);
        col.setMinWidth(30);
        col.setPreferredWidth(50);
        model.addColumn(col);
        // lines
        col = new TableColumn();
        col.setIdentifier("Lines"); // NOI18N
        col.setModelIndex(4);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.LinesColumn")); // NOI18N
        col.setMaxWidth(100);
        col.setMinWidth(1);
        col.setPreferredWidth(70);
        model.addColumn(col);
        // message
        col = new TableColumn();
        col.setIdentifier("Message"); // NOI18N
        col.setModelIndex(5);
        col.setHeaderValue(NbBundle.getBundle(LogInfoPanel.class).getString("LogInfoPanel.LogMessage")); // NOI18N
        col.setMaxWidth(500);
        col.setMinWidth(50);
        col.setPreferredWidth(100);
        model.addColumn(col);
        return model;
    }
    
  /**
   * Overriding the SingleNodeView method, to refresh the display
   */
  public void setContextNode(Node node) {
      super.setContextNode(node);
      Node infoNode = getContextNode();
      if (infoNode != null) {
          FileVcsInfo info = (FileVcsInfo)infoNode.getCookie(FileVcsInfo.class);
          if (info != null && info.getType().equals(TYPE)) {
              setData(info);
          } else {
              setData(clearInfo);
          }
      } else {
          setData(clearInfo);
      }
  }
    

  public void addNotify() {
      super.addNotify();
      if (currentInfo != null) {
          FileVcsInfo.Composite comp = (FileVcsInfo.Composite)currentInfo.getAttribute(REVISIONS_LIST);
          if (comp != null) {
              comp.addPropertyChangeListener(messageListener);
          }
          comp = (FileVcsInfo.Composite)currentInfo.getAttribute(SYM_NAMES_LIST);
          if (comp != null) {
              comp.addPropertyChangeListener(messageListener);
          }
      }
  }
  
  public void removeNotify() {
      super.removeNotify();
      if (currentInfo != null) {
          FileVcsInfo.Composite comp = (FileVcsInfo.Composite)currentInfo.getAttribute(REVISIONS_LIST);
          if (comp != null) {
              comp.removePropertyChangeListener(messageListener);
          }
          comp = (FileVcsInfo.Composite)currentInfo.getAttribute(SYM_NAMES_LIST);
          if (comp != null) {
              comp.removePropertyChangeListener(messageListener);
          }
      }
  }
  
  
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
