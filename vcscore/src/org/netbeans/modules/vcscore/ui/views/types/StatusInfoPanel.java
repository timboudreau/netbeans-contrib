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

import javax.swing.JPanel;
import javax.swing.JComponent;
import org.netbeans.modules.vcscore.util.table.*;
import java.io.File;
import org.netbeans.modules.vcscore.util.Debug;
import java.lang.reflect.Method;
import org.openide.util.NbBundle;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import org.netbeans.modules.vcscore.ui.views.actions.*;
import org.netbeans.modules.vcscore.ui.views.*;
import javax.accessibility.*;

import java.util.ResourceBundle;
import org.openide.explorer.*;
import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.TopManager;
import org.netbeans.modules.vcscore.grouping.*;
import java.beans.PropertyVetoException;

/**
 *
 * @author  mkleint
 * @version
 */
public class StatusInfoPanel extends SingleNodeView  {
    
    public static final String STATUS      = "STATUS.STATUS"; //NOI18N
    public static final String WORK_REV    = "STATUS.WORK_REVISION"; //NOI18N
    public static final String REPO_FILE   = "STATUS.REPO_FILE"; //NOI18N
    public static final String REPO_REV    = "STATUS.REPO_REVISION"; //NOI18N
    public static final String SYMNAME_REV = "STATUS.SYMNAME.REVISION"; //NOI18N
    public static final String SYMNAME_TAG = "STATUS.SYMNAME.TAG"; //NOI18N
    public static final String TAGS_LIST   = "STATUS.SYMNAMES.LIST"; //NOI18N
    public static final String STICKY_DATE = "STATUS.STICKY.DATE"; //NOI18N
    public static final String STICKY_TAG  = "STATUS.STICKY.TAG"; //NOI18N
    public static final String STICKY_OPTION = "STATUS.STICKY.OPTION"; //NOI18N
    
    
    public static final String TYPE = "STATUS"; //NOI18N
    
    Color oldColor;
    private FileVcsInfo statusInfo;
    private FileVcsInfo clearStatusInfo;
    
//    private TableInfoModel model;
    /** Creates new form StatusInfoPanel */
    public StatusInfoPanel() {
        initComponents ();
        initAccessibility();
        lblRepFile.setDisplayedMnemonic (NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblRepFile.mnemonic").charAt(0)); // NOI18N
        lblRepFile.setLabelFor (txRepFile);
//        btnDiff.setMnemonic (NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.btnDiff.mnemonic").charAt(0)); // NOI18N
//        btnAdvanced.setMnemonic (NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.btnAdvanced.mnemonic").charAt(0)); // NOI18N
        lblExistingTags.setDisplayedMnemonic (NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblExistingTags.mnemonic").charAt(0)); // NOI18N
        lblExistingTags.setLabelFor (tblExistingTags);

        oldColor = txRepRev.getForeground();
        // after setting the components unvisible, the layout gets forgotten :(
        GridBagConstraints spExistingTagsConstraints;
        GridBagConstraints lblExistingTagsConstraints;
        GridBagLayout gridBag;
        gridBag  = (GridBagLayout)getLayout();
        spExistingTagsConstraints = gridBag.getConstraints(spExistingTags);
        lblExistingTagsConstraints = gridBag.getConstraints(lblExistingTags);
        setPreferredSize(new java.awt.Dimension(700, 450));
        setMinimumSize(new java.awt.Dimension(700, 450));        
      
      TableInfoModel model = new TableInfoModel();
      Class classa = FileVcsInfo.CompositeItem.class;
      String  column1 = NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.SymNamesColumn"); // NOI18N
      String  column2 = NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.Rev2Column"); // NOI18N
      try {
          Method method1 = classa.getMethod("getAttributeNonNull", new Class[] {String.class}); // NOI18N
          model.setColumnDefinition(0, column1, method1, new Object[] { SYMNAME_TAG}, true, null);
          model.setColumnDefinition(1, column2, method1, new Object[] { SYMNAME_REV}, true, new ExtendedRevisionComparator());
      } catch (NoSuchMethodException exc) {
          Thread.dumpStack();
      } catch (SecurityException exc2) {
          Thread.dumpStack();
      }
/*      
      tblExistingTags.setModel(model);
 */
      tblExistingTags.setVisible(false);
      TableView view = new TableView( StatusInfoPanel.TAGS_LIST, model);
      CallableSystemAction[] actions = new CallableSystemAction[3];
      actions[0] = (CallableSystemAction)SharedClassObject.findObject(OpenRevisionAction.class, true);
      actions[1] = (CallableSystemAction)SharedClassObject.findObject(UpdateRevisionAction.class, true);
      actions[2] = (CallableSystemAction)SharedClassObject.findObject(DiffRevisionAction.class, true);
      view.setAdditionalActions(actions);
      
      this.remove(spExistingTags);
      add(view, spExistingTagsConstraints);
//      spExistingTags.setViewportView(view);
      initClearInfo();  
    }
    
    private void initClearInfo() {
        clearStatusInfo = FileVcsInfoFactory.createBlankFileVcsInfo(StatusInfoPanel.TYPE, new File(""));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lblFileName = new javax.swing.JLabel();
        txFileName = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        txStatus = new javax.swing.JLabel();
        lblRepFile = new javax.swing.JLabel();
        txRepFile = new javax.swing.JTextField();
        lblWorkRev = new javax.swing.JLabel();
        txWorkRev = new javax.swing.JLabel();
        lblRepRev = new javax.swing.JLabel();
        txRepRev = new javax.swing.JLabel();
        lblTag = new javax.swing.JLabel();
        txTag = new javax.swing.JLabel();
        lblOptions = new javax.swing.JLabel();
        txOptions = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        txDate = new javax.swing.JLabel();
        spExistingTags = new javax.swing.JScrollPane();
        tblExistingTags = new javax.swing.JTable();
        lblExistingTags = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setMaximumSize(new java.awt.Dimension(354, 203));
        lblFileName.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblFileName.text"));
        lblFileName.setLabelFor(txFileName);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(lblFileName, gridBagConstraints);

        txFileName.setBackground(java.awt.Color.gray);
        txFileName.setForeground(java.awt.Color.black);
        txFileName.setText("text - filename");
        txFileName.setMaximumSize(new java.awt.Dimension(150, 16));
        txFileName.setMinimumSize(new java.awt.Dimension(120, 16));
        txFileName.setPreferredSize(new java.awt.Dimension(120, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(txFileName, gridBagConstraints);

        lblStatus.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblStatus.text"));
        lblStatus.setLabelFor(txStatus);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblStatus, gridBagConstraints);

        txStatus.setBackground(java.awt.Color.gray);
        txStatus.setForeground(java.awt.Color.blue);
        txStatus.setText("text - status");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txStatus, gridBagConstraints);

        lblRepFile.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblRepFile.text"));
        lblRepFile.setLabelFor(txRepFile);
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
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 0, 11);
        add(txRepFile, gridBagConstraints);

        lblWorkRev.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblWorkRev.text"));
        lblWorkRev.setLabelFor(lblWorkRev);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblWorkRev, gridBagConstraints);

        txWorkRev.setForeground(java.awt.Color.black);
        txWorkRev.setText("work. rev.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txWorkRev, gridBagConstraints);

        lblRepRev.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblRepRev.text"));
        lblRepRev.setLabelFor(txRepRev);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblRepRev, gridBagConstraints);

        txRepRev.setForeground(java.awt.Color.black);
        txRepRev.setText("rep. revis.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txRepRev, gridBagConstraints);

        lblTag.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblTag.text"));
        lblTag.setLabelFor(txTag);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblTag, gridBagConstraints);

        txTag.setForeground(java.awt.Color.black);
        txTag.setText("tag");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 51;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txTag, gridBagConstraints);

        lblOptions.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblOptions.text"));
        lblOptions.setLabelFor(txOptions);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblOptions, gridBagConstraints);

        txOptions.setForeground(java.awt.Color.black);
        txOptions.setText("options");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txOptions, gridBagConstraints);

        lblDate.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblDate.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblDate, gridBagConstraints);

        txDate.setForeground(java.awt.Color.black);
        txDate.setText("date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(txDate, gridBagConstraints);

        spExistingTags.setMinimumSize(new java.awt.Dimension(100, 100));
        spExistingTags.setPreferredSize(new java.awt.Dimension(200, 200));
        spExistingTags.setViewportView(tblExistingTags);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 11, 11);
        add(spExistingTags, gridBagConstraints);

        lblExistingTags.setText(org.openide.util.NbBundle.getBundle(StatusInfoPanel.class).getString("StatusInfoPanel.lblExistingTags.text"));
        lblExistingTags.setLabelFor(tblExistingTags);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 0);
        add(lblExistingTags, gridBagConstraints);

    }//GEN-END:initComponents

    private void txRepFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txRepFileActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_txRepFileActionPerformed
        
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblWorkRev;
    private javax.swing.JLabel txOptions;
    private javax.swing.JLabel txTag;
    private javax.swing.JLabel txWorkRev;
    private javax.swing.JLabel txRepRev;
    private javax.swing.JLabel lblRepFile;
    private javax.swing.JTable tblExistingTags;
    private javax.swing.JLabel lblRepRev;
    private javax.swing.JLabel lblExistingTags;
    private javax.swing.JLabel txStatus;
    private javax.swing.JTextField txRepFile;
    private javax.swing.JLabel txDate;
    private javax.swing.JLabel txFileName;
    private javax.swing.JLabel lblOptions;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblTag;
    private javax.swing.JScrollPane spExistingTags;
    // End of variables declaration//GEN-END:variables

  private static final ResourceBundle bundle = NbBundle.getBundle(StatusInfoPanel.class);   // NOI18N
  
  private void initAccessibility() {
        
        AccessibleContext context = this.getAccessibleContext();
        context.setAccessibleDescription(bundle.getString("ACSD_StatusInfoPanel")); //NOI18N

        
/*        context = btnDiff.getAccessibleContext();
        context.setAccessibleDescription(bundle.getString("ACSD_StatusInfoPanel.btnDiff"));
        
        context = btnAdvanced.getAccessibleContext();
        context.setAccessibleDescription(bundle.getString("ACSD_StatusInfoPanel.btnAdvanced"));
  */      
        context = txRepFile.getAccessibleContext();
        context.setAccessibleDescription(bundle.getString("ACSD_StatusInfoPanel.txRepFile")); //NOI18N
        
  }

  private void setData(FileVcsInfo info) {
      statusInfo = info;
      txFileName.setText(info.getFile().getName());
      txStatus.setText(info.getAttributeNonNull(STATUS));
      String work = info.getAttributeNonNull(WORK_REV);
      txWorkRev.setText(work);
      txRepFile.setText(info.getAttributeNonNull(REPO_FILE));
      String repo = info.getAttributeNonNull(REPO_REV);
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
      txDate.setText(info.getAttributeNonNull(STICKY_DATE));
      txTag.setText(info.getAttributeNonNull(STICKY_TAG));
      txOptions.setText(info.getAttributeNonNull(STICKY_OPTION));
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
              setData(clearStatusInfo);
          }
      } else {
          setData(clearStatusInfo);
      }
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
