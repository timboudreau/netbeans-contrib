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
import java.util.*;
import java.io.File;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableColumnModel;

import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.awt.Dimension;
import java.lang.reflect.Method;
import org.netbeans.modules.vcscore.util.table.*;
import org.netbeans.modules.vcscore.ui.views.*;

import org.openide.nodes.*;

public class AnnotateInfoPanel extends SingleNodeView {

    public static final String ANNOTATIONS_LIST = "ANNOTATIONS_LIST"; //NOI18N
    public static final String ANNOT_CONTENT = "ANNOT_CONTENT"; //NOI18N
    public static final String ANNOT_DATE_STRING = "ANNOT_DATE_STRING"; //NOI18N
    public static final String ANNOT_AUTHOR = "ANNOT_AUTHOR"; //NOI18N
    public static final String ANNOT_REVISION = "ANNOT_REVISION"; //NOI18N
    public static final String ANNOT_LINE_NUMBER = "ANNOT_LINE_NUMBER"; //NOI18N
    
    public static final String TYPE ="ANNOTATE"; //NOI18N
    
    private TableInfoModel modAnnotations = null;
    
    private static final int REVISION_COLUMN = 1;
    private static final int AUTHOR_COLUMN = 2;
    private static final java.awt.Color colorBoth = new java.awt.Color(255, 160, 180);
    private static final java.awt.Color colorRev = new java.awt.Color(180, 255, 180);
    private static final java.awt.Color colorAuth = new java.awt.Color(160, 200, 255);
 
    private FileVcsInfo currentInfo;
    private FileVcsInfo clearInfo;
    
    
    /** Creates new form AnnotatenfoPanel */
    DefaultComboBoxModel revModel;
    DefaultComboBoxModel authModel;
    String noRevisionSelected;
    String noAuthorSelected;
    List revSet;
    List authSet;
    TableView tblAnnotat;
    
    public AnnotateInfoPanel() {
        initComponents ();
        setPreferredSize(new java.awt.Dimension(750, 400));
        setMinimumSize(new java.awt.Dimension(750, 400));          
/*        tblAnnotat.setShowGrid(false);
        tblAnnotat.setBorder(null);
        tblAnnotat.setRowSelectionAllowed(true);
        tblAnnotat.setShowHorizontalLines(false);
        tblAnnotat.setShowVerticalLines(false);
        //tblAnnotat.setSelectionBackground(selectionBackground
        tblAnnotat.setIntercellSpacing(new Dimension(0, 0));
 */
        
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 11, 11);
        modAnnotations = createModel();
        tblAnnotat = new AnnotateTableView(ANNOTATIONS_LIST, modAnnotations);
        tblAnnotat.setColumnModel(createColumnModel());
        add(tblAnnotat, gridBagConstraints);
        
        revModel = new DefaultComboBoxModel();
        authModel = new DefaultComboBoxModel();
        noRevisionSelected = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.noRevisionSelected");
        noAuthorSelected = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.noAuthorSelected");
        revSet = new LinkedList();
        authModel.addElement(noAuthorSelected);
        authSet = new LinkedList();
        cbRevisionList.setModel(revModel);
        cbAuthorList.setModel(authModel);
        String bigger = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.biggerThan");
        String smaller = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.smallerThan");
        String equals = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.equals");
        cbRevisionRange.setModel(new DefaultComboBoxModel(new Object[] {equals, bigger, smaller}));
 
        lblWorkFile.setDisplayedMnemonic(NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.lblWorkFile.Mnem").charAt(0));
        lblRevision.setDisplayedMnemonic(NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.lblRevision.Mnem").charAt(0));
        lblAuthor.setDisplayedMnemonic(NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.lblAuthor.Mnem").charAt(0));
        txWorkFile.addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(java.awt.event.FocusEvent e) {
                txWorkFile.selectAll();
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                txWorkFile.select(1,1);
            }
        }); 
        txWorkFile.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotateInfoPanel.class).getString("ACSD_AnnotatePanel.txWorkFile"));
//        spAnnotat.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AnnotatePanel.class).getString("ACSN_AnnotatePanel.spAnnotat"));
//        spAnnotat.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotatePanel.class).getString("ACSD_AnnotatePanel.spAnnotat"));
        cbRevisionRange.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AnnotateInfoPanel.class).getString("ACSN_AnnotatePanel.cbRevisionRange"));
        cbRevisionRange.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotateInfoPanel.class).getString("ACSD_AnnotatePanel.cbRevisionRange"));
        cbRevisionList.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AnnotateInfoPanel.class).getString("ACSN_AnnotatePanel.cbRevisionList"));
        cbRevisionList.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotateInfoPanel.class).getString("ACSD_AnnotatePanel.cbRevisionList"));
        cbAuthorList.getAccessibleContext().setAccessibleName(NbBundle.getBundle(AnnotateInfoPanel.class).getString("ACSN_AnnotatePanel.cbAuthorList"));
        cbAuthorList.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotateInfoPanel.class).getString("ACSD_AnnotatePanel.cbAuthorList"));
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(AnnotateInfoPanel.class).getString("ACSD_AnnotatePanel"));
        
        
        initClearInfo();
   }
    
    private void initClearInfo() {
        clearInfo = FileVcsInfoFactory.createBlankFileVcsInfo(AnnotateInfoPanel.TYPE, new File(""));
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

        setLayout(new java.awt.GridBagLayout());

        pnlHead.setLayout(new java.awt.GridBagLayout());

        lblWorkFile.setText(org.openide.util.NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.lblWorkFile.text"));
        lblWorkFile.setLabelFor(txWorkFile);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 5, 0);
        pnlHead.add(lblWorkFile, gridBagConstraints);

        txWorkFile.setEditable(false);
        txWorkFile.setText("jTextField1");
        txWorkFile.setPreferredSize(new java.awt.Dimension(250, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 6, 5, 11);
        pnlHead.add(txWorkFile, gridBagConstraints);

        cbRevisionList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRevisionListActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 11);
        pnlHead.add(cbRevisionList, gridBagConstraints);

        cbAuthorList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAuthorListActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 12, 11);
        pnlHead.add(cbAuthorList, gridBagConstraints);

        lblRevision.setText(org.openide.util.NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.lblRevision.text"));
        lblRevision.setLabelFor(cbRevisionRange);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 2, 0);
        pnlHead.add(lblRevision, gridBagConstraints);

        lblAuthor.setText(org.openide.util.NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotatePanel.lblAuthor"));
        lblAuthor.setLabelFor(cbAuthorList);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        pnlHead.add(lblAuthor, gridBagConstraints);

        cbRevisionRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRevisionRangeActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlHead.add(cbRevisionRange, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(pnlHead, gridBagConstraints);

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
    private javax.swing.JLabel lblRevision;
    private javax.swing.JPanel pnlHead;
    private javax.swing.JLabel lblAuthor;
    private javax.swing.JComboBox cbRevisionList;
    private javax.swing.JTextField txWorkFile;
    private javax.swing.JComboBox cbAuthorList;
    private javax.swing.JComboBox cbRevisionRange;
    private javax.swing.JLabel lblWorkFile;
    // End of variables declaration//GEN-END:variables

    private static final long serialVersionUID = -2618655204542546204L;    

  private TableColumnModel createColumnModel() {
      TableColumnModel model = new DefaultTableColumnModel();
      // linenum
      TableCellRenderer colorRenderer = new ColoringUpdateRenderer();
      TableColumn col = new TableColumn();
      col.setIdentifier("LineNum"); //NOI18N
      col.setModelIndex(0);
      col.setHeaderValue(NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.LineNumColumn"));
      col.setMaxWidth(100);
      col.setMinWidth(50);
      col.setPreferredWidth(50);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      // revision
      col = new TableColumn();
      col.setIdentifier("Revision"); //NOI18N
      col.setModelIndex(1);
      col.setHeaderValue(NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.RevisionColumn"));
      col.setMaxWidth(100);
      col.setMinWidth(50);
      col.setPreferredWidth(50);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      // author
      col = new TableColumn();
      col.setIdentifier("Author"); //NOI18N
      col.setModelIndex(2);
      col.setHeaderValue(NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.AuthorColumn"));
      col.setMaxWidth(150);
      col.setMinWidth(50);
      col.setPreferredWidth(75);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      // date
      col = new TableColumn();
      col.setIdentifier("Date"); //NOI18N
      col.setModelIndex(3);
      col.setHeaderValue(NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.DateColumn"));
      col.setMaxWidth(120);
      col.setMinWidth(10);
      col.setPreferredWidth(80);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      // message
      col = new TableColumn();
      col.setIdentifier("Text"); //NOI18N
      col.setModelIndex(4);
      col.setHeaderValue(NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.TextColumn"));
      col.setMaxWidth(500);
      col.setMinWidth(50);
      col.setPreferredWidth(200);
      col.setCellRenderer(colorRenderer);
      model.addColumn(col);
      return model;
  }        
    
  private TableInfoModel createModel() {
      TableInfoModel modAnnotations = new TableInfoModel();
      Class classa = FileVcsInfo.CompositeItem.class;
      String label0 = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.LineNumColumn");
      String label1 = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.AuthorColumn");
      String label2 = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.RevisionColumn");
      String label3 = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.DateColumn");
      String label4 = NbBundle.getBundle(AnnotateInfoPanel.class).getString("AnnotateInfoPanel.TextColumn");
        try {
            Method method1 = classa.getMethod("getAttribute", new Class[] {String.class}); // NOI18N
            Method method2 = classa.getMethod("getAttributeNonNull", new Class[] {String.class}); // NOI18N
            modAnnotations.setColumnDefinition(0, label0, method1, new Object[] { ANNOT_LINE_NUMBER }, true, new IntegerComparator());
            modAnnotations.setColumnDefinition(1, label1, method2, new Object[] { ANNOT_REVISION }, true, new RevisionComparator());
            modAnnotations.setColumnDefinition(2, label2, method2, new Object[] { ANNOT_AUTHOR }, true, null);
            modAnnotations.setColumnDefinition(3, label3, method2, new Object[] { ANNOT_DATE_STRING }, true, new DateComparator());
            modAnnotations.setColumnDefinition(4, label4, method2, new Object[] { ANNOT_CONTENT }, false, null);
        } catch (NoSuchMethodException exc) {
            Thread.dumpStack();
        } catch (SecurityException exc2) {
            Thread.dumpStack();
        }
      return modAnnotations;
  }
  
  private void setComboModel(List list, DefaultComboBoxModel model) {
      model.removeAllElements();
      Iterator it = list.iterator();
      while (it.hasNext()) {
          model.addElement(it.next());
      }
  }

  
   private void setData(FileVcsInfo info) {
        currentInfo = info;
        if (info.getFile() != null) {
            txWorkFile.setText(info.getFile().getAbsolutePath());
        } else {
            txWorkFile.setText("");
        }
        FileVcsInfo.Composite comp = (FileVcsInfo.Composite)info.getAttribute(ANNOTATIONS_LIST);
        revSet.clear();
        authSet.clear();
        if (comp != null) {
            for (int i = 0; i < comp.getCount(); i++) {
                FileVcsInfo.CompositeItem item = comp.getRow(i);
                String rev = (String)item.getAttribute(ANNOT_REVISION);
                String auth = (String)item.getAttribute(ANNOT_AUTHOR);
                if (rev != null && (!revSet.contains(rev))) {
                    revSet.add(rev);
                }
                if (auth != null && (!authSet.contains(auth))) {
                    authSet.add(auth);
                }
            }
            Collections.sort(revSet, new RevisionComparator());
            Collections.sort(authSet);
            revSet.add(0, this.noRevisionSelected);
            authSet.add(0, this.noAuthorSelected);
            
        } else {
            revSet.add(this.noRevisionSelected);
            authSet.add(this.noAuthorSelected);
        }
        setComboModel(revSet, revModel);
        setComboModel(authSet, authModel);
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
  
  /*
   public JTable getTableComponent() {
      return tblAnnotat;
  }
   */
  
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
            String auth = AnnotateInfoPanel.this.modAnnotations.getValueAt(row, AnnotateInfoPanel.AUTHOR_COLUMN).toString();
            String rev = AnnotateInfoPanel.this.modAnnotations.getValueAt(row, AnnotateInfoPanel.REVISION_COLUMN).toString();
            boolean matchesAuthor = AnnotateInfoPanel.this.matchesAuthor(auth);
            boolean matchesRevision = AnnotateInfoPanel.this.matchesRevision(rev);
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
   
   
   public static class AnnotateTableView extends TableView {
    
      public AnnotateTableView(String compositeAttributeName, TableInfoModel model) {
          super(compositeAttributeName, model);
      }
       
       /** Creates the list that will display the data.
        */
       protected JTable createTable() {
           JTable retValue;
           retValue = super.createTable();
           retValue.setShowGrid(false);
           retValue.setBorder(null);
           retValue.setRowSelectionAllowed(true);
           retValue.setShowHorizontalLines(false);
           retValue.setShowVerticalLines(false);
           //retValue.setSelectionBackground(selectionBackground
           retValue.setIntercellSpacing(new Dimension(0, 0));
           return retValue;
       }
       
   }
}
