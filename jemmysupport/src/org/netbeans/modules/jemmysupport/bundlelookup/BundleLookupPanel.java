/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jemmysupport.bundlelookup;

/*
 * BundleLookupPanel.java
 *
 * Created on December 3, 2001, 10:46 AM
 */
import java.awt.Dialog;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import org.openide.windows.TopComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.ExClipboard;

/** Panel for Resource Bundle Lookup
 * @author <a href="mailto:adam.sotona@sun.com">Adam Sotona</a>
 * @version 0.1
 */
public class BundleLookupPanel extends TopComponent  {

    static BundleLookupPanel panel=null;
    
    static final String[] header={
        NbBundle.getMessage(BundleLookupPanel.class, "LBL_Bundle"), // NOI18N
        NbBundle.getMessage(BundleLookupPanel.class, "LBL_Key"), // NOI18N
        NbBundle.getMessage(BundleLookupPanel.class, "LBL_Text")}; // NOI18N
    
    static boolean enableRegExp=true;
    
    static {
        try {
            Class.forName("java.util.regex.Pattern"); // NOI18N
        } catch (ClassNotFoundException e) {
            enableRegExp=false;
        }
    }

    static class ResultTableModel extends DefaultTableModel {
        
        private static final String upArrow = NbBundle.getMessage(BundleLookup.class, "LBL_UpArrow");
        private static final String downArrow = NbBundle.getMessage(BundleLookup.class, "LBL_DownArrow");
        
        private CellComparator comparator=new CellComparator();
        
        static class CellComparator implements Comparator {
            ArrayList sortOrder=new ArrayList();
            ArrayList ascOrder=new ArrayList();
            public synchronized void sortBy(int column, boolean asc) {
                int i=sortOrder.indexOf(new Integer(column));
                if (i>=0) {
                    sortOrder.remove(i);
                    ascOrder.remove(i);
                }
                sortOrder.add(0, new Integer(column));
                ascOrder.add(0, asc ? Boolean.TRUE : Boolean.FALSE);
            }
            public synchronized int compare(Object obj, Object obj1) {
                if (obj instanceof Vector)
                    obj=((Vector)obj).toArray();
                if (obj1 instanceof Vector)
                    obj1=((Vector)obj1).toArray();
                Iterator it=sortOrder.iterator();
                int i, result;
                for (int index=0; index<sortOrder.size(); index++) {
                    i=((Integer)sortOrder.get(index)).intValue();
                    result=((Object[])obj)[i].toString().compareTo(((Object[])obj1)[i].toString());
                    if (result!=0) 
                        return ((Boolean)ascOrder.get(index)).booleanValue()?result:-result;
                }
                return 0;
            }
        }
        
        ResultTableModel() {
            super(header,0);
        }
        
        /** method returning Result Table column classes
         * @return String.class
         * @param columnIndex column index
         */        
        public Class getColumnClass(int columnIndex) {
            return String.class;
        }

        /** method preventing result table to be edited
         * @param rowIndex row index
         * @param columnIndex column index
         * @return boolean false
         */        
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
        
        public void addRow(Object[] row) {
            int i=Arrays.binarySearch(getDataVector().toArray(), row, comparator);
            if (i<0) i=-i-1;
            super.insertRow(i, row);
        }
        
        public void sort(int column, boolean asc) {
            comparator.sortBy(column, asc);
            Object data[]=getDataVector().toArray();
            Arrays.sort(data, comparator);
            String[] myHeader=new String[header.length];
            for (int i=0; i<header.length; i++) {
                myHeader[i]=header[i];
                if (i==column)
                    myHeader[i]+=asc?downArrow:upArrow; // NOI18N
            }
            setDataVector(convertToVector(data), convertToVector(myHeader));
        }
            

        // There is no-where else to put this. 
        // Add a mouse listener to the Table to trigger a table sort 
        // when a column heading is clicked in the JTable. 
        public void addMouseListenerToHeaderInTable(JTable table) { 
            final ResultTableModel sorter = this; 
            final JTable tableView = table; 
            MouseAdapter listMouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    TableColumnModel columnModel = tableView.getColumnModel();
                    int viewColumn = columnModel.getColumnIndexAtX(e.getX()); 
                    int column = tableView.convertColumnIndexToModel(viewColumn); 
                    if (e.getClickCount()==1 && column != -1) {
                        sort(column, (e.getModifiers()&(e.SHIFT_MASK|e.BUTTON3_MASK))==0); 
                    }
                }
            };
            JTableHeader th = tableView.getTableHeader(); 
            th.addMouseListener(listMouseListener); 
            th.setToolTipText(NbBundle.getMessage(BundleLookupPanel.class, "TTT_Sort"));
        }


        
    }        

    private static Dialog dialog;
    
    /** Creates new BundleLookupPanel
     */
    public BundleLookupPanel() {
        super();
        setName(NbBundle.getMessage(BundleLookupPanel.class, "Title")); // NOI18N
        try {
            setIcon(java.awt.Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/org/netbeans/modules/jemmysupport/bundlelookup/BundleLookupAction.gif"))); // NOI18N
        } catch (Exception e){
            ErrorManager.getDefault().notify(e);
        }
        initComponents();
        TextRegCheck.setEnabled(enableRegExp);
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(BundleLookupPanel.class);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        SearchTextLabel = new javax.swing.JLabel();
        SearchTextField = new javax.swing.JTextField();
        TextCaseCheck = new javax.swing.JCheckBox();
        TextSubstringCheck = new javax.swing.JCheckBox();
        TextRegCheck = new javax.swing.JCheckBox();
        UseFilterCheck = new javax.swing.JCheckBox();
        SearchButton = new javax.swing.JButton();
        StopButton = new javax.swing.JButton();
        StopButton.setVisible(false);
        ScrollPane = new javax.swing.JScrollPane();
        ResultTable = new javax.swing.JTable();
        StatusLabel = new javax.swing.JLabel();
        filterPanel = new javax.swing.JPanel();
        BundleFilter = new javax.swing.JLabel();
        BundleTextField = new javax.swing.JTextField();
        BundleCaseCheck = new javax.swing.JCheckBox();
        BundleSubstringCheck = new javax.swing.JCheckBox();
        BundleRegCheck = new javax.swing.JCheckBox();
        ResultsLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "Title"));
        getAccessibleContext().setAccessibleDescription("N/A");
        SearchTextLabel.setDisplayedMnemonic(NbBundle.getMessage(BundleLookupPanel.class, "MNM_SearchedText").charAt(0));
        SearchTextLabel.setLabelFor(SearchTextField);
        SearchTextLabel.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_SearchedText"));
        SearchTextLabel.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_SearchedText"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        add(SearchTextLabel, gridBagConstraints);

        SearchTextField.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_SearchedText"));
        SearchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchTextFieldActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(SearchTextField, gridBagConstraints);

        TextCaseCheck.setMnemonic(NbBundle.getMessage(BundleLookupPanel.class, "MNM_CaseSensitive").charAt(0));
        TextCaseCheck.setSelected(true);
        TextCaseCheck.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_CaseSensitive"));
        TextCaseCheck.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_CaseSensitive"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(TextCaseCheck, gridBagConstraints);

        TextSubstringCheck.setMnemonic(NbBundle.getMessage(BundleLookupPanel.class, "MNM_Substring").charAt(0));
        TextSubstringCheck.setSelected(true);
        TextSubstringCheck.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_Substring"));
        TextSubstringCheck.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_Substring"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 0);
        add(TextSubstringCheck, gridBagConstraints);

        TextRegCheck.setMnemonic(NbBundle.getMessage(BundleLookupPanel.class, "MNM_RegExp").charAt(0));
        TextRegCheck.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_RegExp"));
        TextRegCheck.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_RegExp"));
        TextRegCheck.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        add(TextRegCheck, gridBagConstraints);

        UseFilterCheck.setMnemonic(NbBundle.getMessage(BundleLookupPanel.class, "MNM_UseFilter").charAt(0));
        UseFilterCheck.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_UseFilter"));
        UseFilterCheck.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_UseFilter"));
        UseFilterCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UseFilterCheckActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 24, 0, 11);
        add(UseFilterCheck, gridBagConstraints);

        SearchButton.setMnemonic(NbBundle.getMessage(BundleLookupPanel.class, "MNM_Search").charAt(0));
        SearchButton.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "CTL_Search"));
        SearchButton.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_Search"));
        SearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 0, 11);
        add(SearchButton, gridBagConstraints);

        StopButton.setMnemonic(NbBundle.getMessage(BundleLookupPanel.class, "MNM_Stop").charAt(0));
        StopButton.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "CTL_Stop"));
        StopButton.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_Stop"));
        StopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StopButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 0, 11);
        add(StopButton, gridBagConstraints);

        ScrollPane.setPreferredSize(new java.awt.Dimension(500, 300));
        ScrollPane.setAutoscrolls(true);
        ResultTableModel model=new ResultTableModel();
        ResultTable.setModel(model);
        model.addMouseListenerToHeaderInTable(ResultTable);
        ResultTable.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_ResultsTable"));
        ResultTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        ResultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ResultTableMouseClicked(evt);
            }
        });

        ScrollPane.setViewportView(ResultTable);
        ResultTable.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_ResultTable"));
        ResultTable.getAccessibleContext().setAccessibleDescription("N/A");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 11);
        add(ScrollPane, gridBagConstraints);
        ScrollPane.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_ResultTable"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 12, 11);
        add(StatusLabel, gridBagConstraints);

        filterPanel.setLayout(new java.awt.GridBagLayout());

        filterPanel.setBorder(new javax.swing.border.LineBorder((java.awt.Color) javax.swing.UIManager.getDefaults().get("Button.darkShadow")));
        BundleFilter.setLabelFor(BundleTextField);
        BundleFilter.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_ResourceBundle"));
        BundleFilter.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_BundleText"));
        BundleFilter.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        filterPanel.add(BundleFilter, gridBagConstraints);

        BundleTextField.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_BundleText"));
        BundleTextField.setEnabled(false);
        BundleTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BundleTextFieldActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        filterPanel.add(BundleTextField, gridBagConstraints);

        BundleCaseCheck.setSelected(true);
        BundleCaseCheck.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_CaseSensitive"));
        BundleCaseCheck.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_CaseSensitive"));
        BundleCaseCheck.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 11, 0);
        filterPanel.add(BundleCaseCheck, gridBagConstraints);

        BundleSubstringCheck.setSelected(true);
        BundleSubstringCheck.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_Substring"));
        BundleSubstringCheck.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_Substring"));
        BundleSubstringCheck.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 11, 0);
        filterPanel.add(BundleSubstringCheck, gridBagConstraints);

        BundleRegCheck.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_RegExp"));
        BundleRegCheck.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_RegExp"));
        BundleRegCheck.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 12, 11, 11);
        filterPanel.add(BundleRegCheck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(24, 12, 0, 11);
        add(filterPanel, gridBagConstraints);

        ResultsLabel.setDisplayedMnemonic(NbBundle.getMessage(BundleLookupPanel.class, "MNM_Results").charAt(0));
        ResultsLabel.setLabelFor(ResultTable);
        ResultsLabel.setText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "LBL_Results", new Object[] {}));
        ResultsLabel.setToolTipText(org.openide.util.NbBundle.getMessage(BundleLookupPanel.class, "TTT_ResultsTable", new Object[] {}));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 12, 0, 12);
        add(ResultsLabel, gridBagConstraints);

    }//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        SearchTextField.requestFocus();
    }//GEN-LAST:event_formComponentShown

    private void StopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StopButtonActionPerformed
        BundleLookup.stop();
    }//GEN-LAST:event_StopButtonActionPerformed

    private static final String copyPrefix = NbBundle.getMessage(BundleLookupPanel.class, "LBL_Copy"); // NOI18N
    
    private void ResultTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ResultTableMouseClicked
        int i;
        if ((evt.getModifiers()==evt.BUTTON3_MASK)&&((i=ResultTable.getSelectedRow())>=0)) {
            ActionListener listener=new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ExClipboard clp=(ExClipboard)Lookup.getDefault().lookup(ExClipboard.class);
                    clp.setContents(new StringSelection(((JMenuItem)evt.getSource()).getText().substring(copyPrefix.length())),null);
                }
            };
            String bundle=ResultTable.getValueAt(i,ResultTable.convertColumnIndexToModel(0)).toString();
            String key=ResultTable.getValueAt(i,ResultTable.convertColumnIndexToModel(1)).toString();
            JPopupMenu menu=new JPopupMenu();
            menu.add(copyPrefix+"java.util.ResourceBundle.getBundle(\""+bundle+"\").getString(\""+key+"\")").addActionListener(listener); // NOI18N
            menu.add(copyPrefix+"org.openide.util.NbBundle.getBundle(\""+bundle+"\").getString(\""+key+"\")").addActionListener(listener); // NOI18N
            menu.add(copyPrefix+"org.netbeans.jellytools.Bundle.getString(\""+bundle+"\", \""+key+"\")").addActionListener(listener); // NOI18N
            menu.add(copyPrefix+"org.netbeans.jellytools.Bundle.getStringTrimmed(\""+bundle+"\", \""+key+"\")").addActionListener(listener); // NOI18N
            menu.show(ResultTable,evt.getX(),evt.getY());
        }
    }//GEN-LAST:event_ResultTableMouseClicked


    private void UseFilterCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UseFilterCheckActionPerformed
        boolean state=UseFilterCheck.isSelected();
        BundleFilter.setEnabled(state);
        BundleTextField.setEnabled(state);
        BundleCaseCheck.setEnabled(state);
        BundleSubstringCheck.setEnabled(state);
        BundleRegCheck.setEnabled(state&&enableRegExp);
    }//GEN-LAST:event_UseFilterCheckActionPerformed

    private void BundleTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BundleTextFieldActionPerformed
        SearchButtonActionPerformed(evt);
    }//GEN-LAST:event_BundleTextFieldActionPerformed

    
    private void SearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButtonActionPerformed
        SearchButton.setVisible(false);
        StopButton.setVisible(true);
        SearchTextField.setEnabled(false);
        TextCaseCheck.setEnabled(false);
        TextSubstringCheck.setEnabled(false);
        TextRegCheck.setEnabled(false);
        UseFilterCheck.setEnabled(false);
        BundleFilter.setEnabled(false);
        BundleTextField.setEnabled(false);
        BundleCaseCheck.setEnabled(false);
        BundleSubstringCheck.setEnabled(false);
        BundleRegCheck.setEnabled(false);
        StatusLabel.setText(NbBundle.getMessage(BundleLookupPanel.class, "LBL_Searching")); // NOI18N
        final ResultTableModel table = (ResultTableModel)ResultTable.getModel();
        table.setRowCount(0);
        Thread t=new Thread(new Runnable() {
            public void run() {
                if (UseFilterCheck.isSelected()) {
                    BundleLookup.lookupText(table, SearchTextField.getText(), 
                        TextCaseCheck.isSelected(), TextSubstringCheck.isSelected(), 
                        TextRegCheck.isSelected(), BundleTextField.getText(), 
                        BundleCaseCheck.isSelected(), BundleSubstringCheck.isSelected(), 
                        BundleRegCheck.isSelected());
                } else {
                    BundleLookup.lookupText(table, SearchTextField.getText(), 
                        TextCaseCheck.isSelected(), TextSubstringCheck.isSelected(), 
                        TextRegCheck.isSelected(), "", true, true, false); // NOI18N
                }
                StatusLabel.setText(NbBundle.getMessage(BundleLookupPanel.class, "LBL_Found",  // NOI18N
                    new Object[] {String.valueOf(table.getRowCount())}));
                StopButton.setVisible(false);
                SearchButton.setVisible(true);
                SearchTextField.setEnabled(true);
                TextCaseCheck.setEnabled(true);
                TextSubstringCheck.setEnabled(true);
                TextRegCheck.setEnabled(enableRegExp);
                UseFilterCheck.setEnabled(true);
                boolean state=UseFilterCheck.isSelected();
                BundleFilter.setEnabled(state);
                BundleTextField.setEnabled(state);
                BundleCaseCheck.setEnabled(state);
                BundleSubstringCheck.setEnabled(state);
                BundleRegCheck.setEnabled(state&&enableRegExp);
            }
        },"SearchThread"); // NOI18N
        t.setDaemon(true);
        t.start();
    }//GEN-LAST:event_SearchButtonActionPerformed

    private void SearchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchTextFieldActionPerformed
        SearchButtonActionPerformed(evt);
    }//GEN-LAST:event_SearchTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ResultsLabel;
    private javax.swing.JLabel BundleFilter;
    private javax.swing.JCheckBox BundleSubstringCheck;
    private javax.swing.JCheckBox BundleCaseCheck;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JButton StopButton;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JCheckBox TextCaseCheck;
    private javax.swing.JTextField SearchTextField;
    private javax.swing.JCheckBox TextSubstringCheck;
    private javax.swing.JLabel SearchTextLabel;
    private javax.swing.JButton SearchButton;
    private javax.swing.JCheckBox BundleRegCheck;
    private javax.swing.JTable ResultTable;
    private javax.swing.JTextField BundleTextField;
    private javax.swing.JCheckBox UseFilterCheck;
    private javax.swing.JLabel StatusLabel;
    private javax.swing.JCheckBox TextRegCheck;
    // End of variables declaration//GEN-END:variables
                       
    /** opens panel
     */    
    public static void openPanel() {
        if (panel==null)
            panel=new BundleLookupPanel();
        panel.open();
    }
                       
    /** opens panel for debugging purposes
     * @param args command line argument
     */    
    public static void main(String args[]) {
        BundleLookupPanel.openPanel();
    }
    
}
