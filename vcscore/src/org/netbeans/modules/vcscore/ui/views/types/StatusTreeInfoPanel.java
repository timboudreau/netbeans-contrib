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

import java.io.*;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.openide.util.*;
import org.netbeans.modules.vcscore.util.table.*;

import java.lang.reflect.Method;
import org.netbeans.modules.vcscore.ui.views.*;
/**
 *
 * @author  mkleint
 * @version
 */
public class StatusTreeInfoPanel extends AbstractTreeInfoPanel implements ChildrenInfoFilter {
    
    private StatusInfoPanel statPanel;
    
    private javax.swing.JCheckBox cbUptodate;
    private javax.swing.JCheckBox cbModified;
    private javax.swing.JCheckBox cbNeedsPatch;
    private javax.swing.JCheckBox cbNeedsMerge;
    private javax.swing.JCheckBox cbHasConflict;
    private javax.swing.JCheckBox cbLocAdded;
    private javax.swing.JCheckBox cbLocRemoved;
    private javax.swing.JCheckBox cbNeedsCheckout;
    private javax.swing.JCheckBox cbUnknown;
    private javax.swing.JLabel    lblCount;
    private javax.swing.JLabel lblTitle;    
    private int totalCount;
    private int selectedCount;
    
    private JRadioButton btnAll;
    private String btnAll_Title;
    private String btnJustModified_Title;
    private JRadioButton btnJustModified;
    private int currentFilter = FILTER_ALL;
    private static final int FILTER_ALL = 0;
    private static final int FILTER_MODIFIED = 1;
    
    
    /** Creates new form StatusTreeInfoPanel */
    public StatusTreeInfoPanel() {
        super();
        postInit();
    }
    
    
    
    private void initPanelComponents() {
    }    
    
    
    protected JComponent initPanel() {
//        initClearInfo();
        statPanel = new StatusInfoPanel();
//        setClearPanel();
        return statPanel;
    }
    
    
    private void checkBoxChanged() {
        totalCount = 0;
        selectedCount = 0;
//        recreateModel();
        Integer selCount = new Integer(selectedCount);
        Integer totCount = new Integer(totalCount);
        String txt = NbBundle.getMessage(StatusTreeInfoPanel.class, "StatusTreeInfoPanel.lblCount", // NOI18N
                                selCount.toString(), totCount.toString());
        lblCount.setText(txt);
    }
    
    protected boolean hasFilter() {
        return true;
    }
    
    protected JComponent createFilterComponent() {
        JPanel panel = new javax.swing.JPanel();
        cbUptodate = new javax.swing.JCheckBox();
        cbModified = new javax.swing.JCheckBox();
        cbLocAdded = new javax.swing.JCheckBox();
        cbLocRemoved = new javax.swing.JCheckBox();
        cbNeedsCheckout = new javax.swing.JCheckBox();
        cbNeedsMerge = new javax.swing.JCheckBox();
        lblTitle = new javax.swing.JLabel();
        lblCount = new javax.swing.JLabel();
        cbNeedsPatch = new javax.swing.JCheckBox();
        cbHasConflict = new javax.swing.JCheckBox();
        cbUnknown = new javax.swing.JCheckBox();
        panel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;

        lblTitle.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.lblTitle.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
        panel.add(lblTitle, gridBagConstraints1);

        cbUptodate.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUptodate.text")); // NOI18N
        cbUptodate.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUptodate.mnemonic").charAt(0)); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (5, 24, 0, 0);
        panel.add(cbUptodate, gridBagConstraints1);
        
        cbModified.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbModified.text")); // NOI18N
        cbModified.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbModified.mnemonic").charAt(0)); // NOI18N        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (5, 12, 0, 11);
        panel.add(cbModified, gridBagConstraints1);
        
        cbLocAdded.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocAdded.text")); // NOI18N
        cbLocAdded.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocAdded.mnemonic").charAt(0)); // NOI18N        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 24, 0, 0);
        panel.add(cbLocAdded, gridBagConstraints1);
        
        cbLocRemoved.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocRemoved.text")); // NOI18N
        cbLocRemoved.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbLocRemoved.mnemonic").charAt(0)); // NOI18N        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 12, 0, 11);
        panel.add(cbLocRemoved, gridBagConstraints1);
        
        cbNeedsMerge.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsMerge.text")); // NOI18N
        cbNeedsMerge.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsMerge.mnemonic").charAt(0)); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 24, 0, 0);
        panel.add(cbNeedsMerge, gridBagConstraints1);

        cbNeedsPatch.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsPatch.text")); // NOI18N
        cbNeedsPatch.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsPatch.mnemonic").charAt(0)); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 12, 0, 11);
        panel.add(cbNeedsPatch, gridBagConstraints1);

        cbNeedsCheckout.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsCheckout.text")); // NOI18N
        cbNeedsCheckout.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbNeedsCheckout.mnemonic").charAt(0)); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 24, 0, 0);
        panel.add(cbNeedsCheckout, gridBagConstraints1);
        
        cbHasConflict.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbHasConflicts.text")); // NOI18N
        cbHasConflict.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbHasConflicts.mnemonic").charAt(0)); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, 12, 0, 11);
        panel.add(cbHasConflict, gridBagConstraints1);

        cbUnknown.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUnknown.text")); // NOI18N
        cbUnknown.setMnemonic(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.cbUnknown.mnemonic").charAt(0)); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets (0, 24, 0, 0);
        panel.add(cbUnknown, gridBagConstraints1);
        
        lblCount.setText(NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTreeInfoPanel.lblCount")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 6;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 2, 11);
        panel.add(lblCount, gridBagConstraints1);
        
//---- END OF COPIED STUFF ===========================================================================
// ===================*******************************************=====================================
        ActionListener listener = (new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxChanged();
            }
        });
        cbUptodate.addActionListener(listener);
        cbModified.addActionListener(listener);
        cbNeedsPatch.addActionListener(listener);
        cbNeedsMerge.addActionListener(listener);
        cbHasConflict.addActionListener(listener);
        cbLocAdded.addActionListener(listener);
        cbLocRemoved.addActionListener(listener);
        cbNeedsCheckout.addActionListener(listener);
        cbUnknown.addActionListener(listener);
        cbUptodate.setSelected(true);
        cbModified.setSelected(true);
        cbNeedsPatch.setSelected(true);
        cbNeedsMerge.setSelected(true);
        cbHasConflict.setSelected(true);
        cbLocAdded.setSelected(true);
        cbLocRemoved.setSelected(true);
        cbNeedsCheckout.setSelected(true);
        cbUnknown.setSelected(true);
        return panel;
    }
    
/*    public Component getTreeCellRendererComponent(JTree tree, Object value, 
                                                    boolean sel, boolean expanded, 
                                                    boolean leaf, int row, boolean hasFocus) {
      Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if (comp instanceof JLabel) {
          JLabel label = (JLabel) comp;
      }
      return comp;
  }
 */

  
  /** to be overidden in case more than the filemane is to be displaed in the Table
   * it needs to be a tablemodel implementing the CommandTableModel methods
 */
  protected TableInfoModel getTableModel() {
        TableInfoModel model = new TableInfoModel();
        Class classa = FileVcsInfo.class;
        String  column1 = NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTableInfoModel.status"); // NOI18N
        String  column2 = NbBundle.getBundle(StatusTreeInfoPanel.class).getString("StatusTableInfoModel.fileName"); // NOI18N
        try {
            
            Method method1 = classa.getMethod("getAttributeNonNull", new Class[] {String.class}); // NOI18N
            Method method2 = classa.getMethod("getFile", null);     // NOI18N
            model.setColumnDefinition(0, column1, method1, new Object[] { StatusInfoPanel.STATUS}, true, null);
            model.setColumnDefinition(1, column2, method2, true, new FileComparator());
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
        return model;
  }  

  /** the filtering method that tells wheather the info should be included in
   * the set. For unwanted infos returns false.
   */
  public boolean checkFileInfo(FileVcsInfo info) {
      String status = info.getAttributeNonNull(StatusInfoPanel.STATUS);
/*      if (status == FileStatus.UP_TO_DATE && cbUptodate.isSelected()) return true;
      if (status == FileStatus.MODIFIED && cbModified.isSelected()) return true;
      if (status == FileStatus.ADDED && cbLocAdded.isSelected()) return true;
      if (status == FileStatus.REMOVED && cbLocRemoved.isSelected()) return true;
      if (status == FileStatus.NEEDS_CHECKOUT && cbNeedsCheckout.isSelected()) return true;
      if (status == FileStatus.NEEDS_MERGE && cbNeedsMerge.isSelected()) return true;
      if (status == FileStatus.NEEDS_PATCH && cbNeedsPatch.isSelected()) return true;
      if (status == FileStatus.HAS_CONFLICTS && cbHasConflict.isSelected()) return true;
      if (status == FileStatus.UNKNOWN && cbUnknown.isSelected()) return true;
 */
      return false;
  }
  
  
}
