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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.annotate;

/**
 * Panel that can display the output of cvs annotate command or similar equivalent in other
 * vcs. How to use:
 * <p>
 * 1. Create the panel + add it into your window/panel.
 * 2.  define how to extract the data. Is done by calling add*ColumnDefinition()  methods.
 * 3.  add data addLine(Object) .. the Object needs to be of the class defined by the add*columnDefinition methods..
 * 4. display the panel.
 *
 * @author  mkleint
 */

import org.openide.util.*;
import java.util.*;
import java.io.File;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.GridBagConstraints;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.Dimension;
import java.lang.reflect.Method;
import org.netbeans.modules.vcscore.util.table.*;

public class AnnotatePanel extends javax.swing.JPanel {

    private TableInfoModel modAnnotations = null;
    
    private static final int REVISION_COLUMN = 1;
    private static final int AUTHOR_COLUMN = 2;
    private static final java.awt.Color colorBoth = new java.awt.Color(255, 160, 180);
    private static final java.awt.Color colorRev = new java.awt.Color(180, 255, 180);
    private static final java.awt.Color colorAuth = new java.awt.Color(160, 200, 255);
    
    
    /** Creates new form AnnotatenfoPanel */
    DefaultComboBoxModel revModel;
    DefaultComboBoxModel authModel;
    String noRevisionSelected;
    String noAuthorSelected;
    List revSet;
    List authSet;
    
    public AnnotatePanel() {
        initComponents ();
        setPreferredSize(new java.awt.Dimension(750, 400));
        setMinimumSize(new java.awt.Dimension(750, 400));          
        tblAnnotat.setShowGrid(false);
        tblAnnotat.setBorder(null);
        tblAnnotat.setRowSelectionAllowed(true);
        tblAnnotat.setShowHorizontalLines(false);
        tblAnnotat.setShowVerticalLines(false);
        //tblAnnotat.setSelectionBackground(selectionBackground
        tblAnnotat.setIntercellSpacing(new Dimension(0, 0));
        createModel();
        createColumnModel();
        revModel = new DefaultComboBoxModel();
        authModel = new DefaultComboBoxModel();
        noRevisionSelected = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.noRevisionSelected");
        noAuthorSelected = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.noAuthorSelected");
        revSet = new LinkedList();
        authModel.addElement(noAuthorSelected);
        authSet = new LinkedList();
        cbRevisionList.setModel(revModel);
        cbAuthorList.setModel(authModel);
        String bigger = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.biggerThan");
        String smaller = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.smallerThan");
        String equals = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.equals");
        cbRevisionRange.setModel(new DefaultComboBoxModel(new Object[] {equals, bigger, smaller}));
 
        lblWorkFile.setDisplayedMnemonic(NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.lblWorkFile.Mnem").charAt(0));
        lblRevision.setDisplayedMnemonic(NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.lblRevision.Mnem").charAt(0));
        lblAuthor.setDisplayedMnemonic(NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.lblAuthor.Mnem").charAt(0));
        txWorkFile.addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(java.awt.event.FocusEvent e) {
                txWorkFile.selectAll();
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                txWorkFile.select(1,1);
            }
        }); 
        txWorkFile.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotatePanel.class).getString("ACSD_AnnotatePanel.txWorkFile"));
        spAnnotat.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AnnotatePanel.class).getString("ACSN_AnnotatePanel.spAnnotat"));
        spAnnotat.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotatePanel.class).getString("ACSD_AnnotatePanel.spAnnotat"));
        cbRevisionRange.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AnnotatePanel.class).getString("ACSN_AnnotatePanel.cbRevisionRange"));
        cbRevisionRange.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotatePanel.class).getString("ACSD_AnnotatePanel.cbRevisionRange"));
        cbRevisionList.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AnnotatePanel.class).getString("ACSN_AnnotatePanel.cbRevisionList"));
        cbRevisionList.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotatePanel.class).getString("ACSD_AnnotatePanel.cbRevisionList"));
        cbAuthorList.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AnnotatePanel.class).getString("ACSN_AnnotatePanel.cbAuthorList"));
        cbAuthorList.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotatePanel.class).getString("ACSD_AnnotatePanel.cbAuthorList"));
        tblAnnotat.getAccessibleContext().setAccessibleName(NbBundle.getMessage(AnnotatePanel.class, "ACSN_AnnotatePanel.table"));
        tblAnnotat.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AnnotatePanel.class, "ACSD_AnnotatePanel.table"));
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotatePanel.class).getString("ACSD_AnnotatePanel"));
   }
        
    /**
     * Sets the name (absolute path?) to the file being desplayed.
     */
    public void setFileName(String name) {
        txWorkFile.setText(name);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        pnlHead = new javax.swing.JPanel();
        lblWorkFile = new javax.swing.JLabel();
        txWorkFile = new javax.swing.JTextField();
        cbRevisionList = new javax.swing.JComboBox();
        cbAuthorList = new javax.swing.JComboBox();
        lblRevision = new javax.swing.JLabel();
        lblAuthor = new javax.swing.JLabel();
        cbRevisionRange = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        spAnnotat = new javax.swing.JScrollPane();
        tblAnnotat = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        pnlHead.setLayout(new java.awt.GridBagLayout());

        lblWorkFile.setLabelFor(txWorkFile);
        lblWorkFile.setText(org.openide.util.NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.lblWorkFile.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 5, 0);
        pnlHead.add(lblWorkFile, gridBagConstraints);

        txWorkFile.setEditable(false);
        txWorkFile.setText("jTextField1");
        txWorkFile.setPreferredSize(new java.awt.Dimension(250, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 5, 11);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        pnlHead.add(txWorkFile, gridBagConstraints);

        cbRevisionList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRevisionListActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 11);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHead.add(cbRevisionList, gridBagConstraints);

        cbAuthorList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAuthorListActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 12, 11);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHead.add(cbAuthorList, gridBagConstraints);

        lblRevision.setLabelFor(cbRevisionRange);
        lblRevision.setText(org.openide.util.NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.lblRevision.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 2, 0);
        pnlHead.add(lblRevision, gridBagConstraints);

        lblAuthor.setLabelFor(cbAuthorList);
        lblAuthor.setText(org.openide.util.NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.lblAuthor"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 12, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHead.add(lblAuthor, gridBagConstraints);

        cbRevisionRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRevisionRangeActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlHead.add(cbRevisionRange, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        pnlHead.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(pnlHead, gridBagConstraints);

        spAnnotat.setMinimumSize(new java.awt.Dimension(600, 100));
        spAnnotat.setPreferredSize(new java.awt.Dimension(600, 50));
        spAnnotat.setViewportView(tblAnnotat);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        add(spAnnotat, gridBagConstraints);

    }//GEN-END:initComponents

    private void cbRevisionRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRevisionRangeActionPerformed
        // Add your handling code here:
//        tblAnnotat.revalidate();
        tblAnnotat.repaint();
    }//GEN-LAST:event_cbRevisionRangeActionPerformed

    private void cbRevisionListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRevisionListActionPerformed
        // Add your handling code here:        
        tblAnnotat.repaint();
//        tblAnnotat.revalidate();
    }//GEN-LAST:event_cbRevisionListActionPerformed

    private void cbAuthorListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAuthorListActionPerformed
        // Add your handling code here:
//        tblAnnotat.tableChanged(new TableModelEvent(modAnnotations));
        tblAnnotat.repaint();
    }//GEN-LAST:event_cbAuthorListActionPerformed
    
  private void lstSymNamesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstSymNamesValueChanged
  }//GEN-LAST:event_lstSymNamesValueChanged
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbAuthorList;
    private javax.swing.JPanel pnlHead;
    private javax.swing.JScrollPane spAnnotat;
    private javax.swing.JComboBox cbRevisionRange;
    private javax.swing.JTable tblAnnotat;
    private javax.swing.JLabel lblRevision;
    private javax.swing.JLabel lblWorkFile;
    private javax.swing.JTextField txWorkFile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox cbRevisionList;
    private javax.swing.JLabel lblAuthor;
    // End of variables declaration//GEN-END:variables

    private static final long serialVersionUID = -2618655204542546204L;    

  private void createColumnModel() {
      TableColumnModel model = new DefaultTableColumnModel();
      // linenum
      TableCellRenderer colorRenderer = new ColoringUpdateRenderer();
      TableColumn col = new TableColumn();
      col.setIdentifier("LineNum"); //NOI18N
      col.setModelIndex(0);
      col.setHeaderValue(NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.LineNumColumn"));
      col.setMaxWidth(100);
      col.setMinWidth(50);
      col.setPreferredWidth(50);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      // revision
      col = new TableColumn();
      col.setIdentifier("Revision"); //NOI18N
      col.setModelIndex(1);
      col.setHeaderValue(NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.RevisionColumn"));
      col.setMaxWidth(100);
      col.setMinWidth(50);
      col.setPreferredWidth(50);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      // author
      col = new TableColumn();
      col.setIdentifier("Author"); //NOI18N
      col.setModelIndex(2);
      col.setHeaderValue(NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.AuthorColumn"));
      col.setMaxWidth(150);
      col.setMinWidth(50);
      col.setPreferredWidth(75);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      // date
      col = new TableColumn();
      col.setIdentifier("Date"); //NOI18N
      col.setModelIndex(3);
      col.setHeaderValue(NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.DateColumn"));
      col.setMaxWidth(120);
      col.setMinWidth(10);
      col.setPreferredWidth(80);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      // message
      col = new TableColumn();
      col.setIdentifier("Text"); //NOI18N
      col.setModelIndex(4);
      col.setHeaderValue(NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.TextColumn"));
      col.setMaxWidth(500);
      col.setMinWidth(50);
      col.setPreferredWidth(200);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      tblAnnotat.setColumnModel(model);
  }        
    
  private void createModel() {
      modAnnotations = new TableInfoModel();
      tblAnnotat.setModel(modAnnotations);
      JTableHeader head = tblAnnotat.getTableHeader();
      head.setUpdateTableInRealTime(true);
      ColumnSortListener listen = new ColumnSortListener(tblAnnotat);
      head.addMouseListener(listen);
      tblAnnotat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  /**
   * Define how to retrieve revision from the row object.
   * @param getter  A method object that is called on the object. It's assumed the method has no parameters and returns Object.
   * @param comparator A comparator that can handle the Object returned by the getter method.               
   */
  public void addRevisionColumnDefinition(Method getter, TableInfoComparator comparator) {
     String label = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.RevisionColumn");
     modAnnotations.setColumnDefinition(1, label, getter, true, comparator);
  }
  /**
   * Define how to retrieve line Number from the row object.
   * @param getter  A method object that is called on the object. It's assumed the method has no parameters and returns Object.
   * @param comparator A comparator that can handle the Object returned by the getter method.               
   */

  public void addLineNumColumnDefinition(Method getter, TableInfoComparator comparator) {
     String label = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.LineNumColumn");
     modAnnotations.setColumnDefinition(0, label, getter, true, comparator);
  }
  /**
   * Define how to retrieve author from the row object.
   * @param getter  A method object that is called on the object. It's assumed the method has no parameters and returns Object.
   * @param comparator A comparator that can handle the Object returned by the getter method.               
   */

  public void addAuthorColumnDefinition(Method getter, TableInfoComparator comparator) {
     String  label = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.AuthorColumn");
     modAnnotations.setColumnDefinition(2, label, getter, true, comparator);
  }
  /**
   * Define how to retrieve date from the row object.
   * @param getter  A method object that is called on the object. It's assumed the method has no parameters and returns Object.
   * @param comparator A comparator that can handle the Object returned by the getter method.               
   */
  
  public void addDateColumnDefinition(Method getter, TableInfoComparator comparator) {
     String label = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.DateColumn");
     modAnnotations.setColumnDefinition(3, label, getter, true, comparator);
  }
  /**
   * Define how to retrieve content (the actual code line) from the row object.
   * @param getter  A method object that is called on the object. It's assumed the method has no parameters and returns Object.
   * @param comparator A comparator that can handle the Object returned by the getter method.               
   */  
  public void addContentColumnDefinition(Method getter, TableInfoComparator comparator) {
     String  label = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotateInfoPanel.TextColumn");
     modAnnotations.setColumnDefinition(4, label, getter, true, comparator);
  }
  
  /**
   * Refreshes the desplay in the table. (performs a sort by the current criteria)
   */
  
  public void doRepaintAndSort() {
       java.util.Collections.sort(modAnnotations.getList(), modAnnotations);
        // find the previsously selected row.
       java.util.Collections.sort(revSet, new RevisionComparator());
       Iterator it = revSet.iterator();
       revModel.removeAllElements();
       revModel.addElement(noRevisionSelected);
       while (it.hasNext()) {
           revModel.addElement(it.next());
       }
       
       tblAnnotat.tableChanged(new TableModelEvent(modAnnotations));
       tblAnnotat.repaint();
  }


/**
 * Appends a row object to the model. Before calling this method for the first time, all the
 * add*ColumnDefinition methods have to be called.
 */
  
  public void addLine(Object line) {
      int count = modAnnotations.getRowCount();
      modAnnotations.addElement(line);
      Object rev = modAnnotations.getValueAt(count, REVISION_COLUMN);
      Object auth = modAnnotations.getValueAt(count, AUTHOR_COLUMN);
      if (!authSet.contains(auth)) {
          authSet.add(auth);
          authModel.removeAllElements();
          Collections.sort(authSet);
          authModel.addElement(noAuthorSelected);
          Iterator it = authSet.iterator();
          while (it.hasNext()) {
              authModel.addElement(it.next());
          }
      }
      if (!revSet.contains(rev)) {
          revSet.add(rev);
//          revModel.removeAllElements();
//          Collections.sort(revSet);
//          Iterator it = revSet.iterator();
//          while (it.hasNext()) {
//              revModel.addElement(it.next());
//          revModel.addElement(rev);
//          }
      }
  }
  
  /**
   * Clears the model.
   */
  
  public void clearAllLines() {
      modAnnotations.clear();
      revModel = new DefaultComboBoxModel();
      authModel = new DefaultComboBoxModel();
      noRevisionSelected = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.noRevisionSelected");
      noAuthorSelected = NbBundle.getBundle(AnnotatePanel.class).getString("AnnotatePanel.noAuthorSelected");
      revModel.addElement(noRevisionSelected);
      revSet = new LinkedList();
      authModel.addElement(noAuthorSelected);
      authSet = new LinkedList();
      cbRevisionList.setModel(revModel);
      cbAuthorList.setModel(authModel);
  }
  
  boolean matchesAuthor(String author) {
      if (cbAuthorList.getSelectedIndex() != 0) {
          if (author.equals(cbAuthorList.getSelectedItem())) {
              return true;
          }
      }
      return false;
  }
  
  boolean matchesRevision(String revision) {
      if (cbRevisionList.getSelectedIndex() != 0) {
          TableInfoComparator comp = new RevisionComparator();
          int result = comp.compare(revision, cbRevisionList.getSelectedItem());
          if (result == 0 && cbRevisionRange.getSelectedIndex() == 0) {
              return true;
          }
          if (result == 1 && cbRevisionRange.getSelectedIndex() == 1) {
              return true;
          }
          if (result == -1 && cbRevisionRange.getSelectedIndex() == 2) {
              return true;
          }
      }
      return false;
  }
  
  /**
   * Utility method for fine-tuning of the table display.
   */
  
  public JTable getTableComponent() {
      return tblAnnotat;
  }
  
   private class ColoringUpdateRenderer extends DefaultTableCellRenderer {

       // a workaround because of bugparade issue : 4336152
        private java.awt.Color almostWhite = new java.awt.Color(254,254,254);  
       
        private static final long serialVersionUID = -8634243127049172822L;
        
        public ColoringUpdateRenderer() {
            super();
//            setOpaque(true);
        }
        
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable jTable, java.lang.Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component retValue;
            
            retValue = super.getTableCellRendererComponent(jTable, obj, isSelected, hasFocus, row, column);

            if (isSelected) return retValue;
            String auth = AnnotatePanel.this.modAnnotations.getValueAt(row, AnnotatePanel.AUTHOR_COLUMN).toString();
            String rev = AnnotatePanel.this.modAnnotations.getValueAt(row, AnnotatePanel.REVISION_COLUMN).toString();
            boolean matchesAuthor = AnnotatePanel.this.matchesAuthor(auth);
            boolean matchesRevision = AnnotatePanel.this.matchesRevision(rev);
            if (matchesAuthor && matchesRevision) {
                retValue.setBackground(colorBoth);
                return retValue;
            }
            if (matchesAuthor) {
                retValue.setBackground(colorAuth);
                return retValue;
            }
            if (matchesRevision) {
                retValue.setBackground(colorRev);
                return retValue;
            } 
            
            retValue.setBackground(almostWhite);
            return retValue;
        }
        
    }  
}
