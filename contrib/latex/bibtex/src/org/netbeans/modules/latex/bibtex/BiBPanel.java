/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;


import java.lang.reflect.Field;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.event.ListDataListener;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Jan Lahoda
 */
public class BiBPanel extends javax.swing.JPanel implements ActionListener {
    
    public static final String AUTHOR = "author";
    public static final String TITLE  = "title";
    public static final String YEAR   = "year";
    public static final String BOOK_TITLE = "booktitle";
    public static final String JOURNAL = "journal";
    public static final String PAGES = "pages";
    
    private static final Map NAMES2CODES;
    
    private String type;
    
    static {
        NAMES2CODES = new HashMap();
        
        NAMES2CODES.put(AUTHOR, "Author");
        NAMES2CODES.put(TITLE, "Title");
        NAMES2CODES.put(YEAR, "Year");
        NAMES2CODES.put(BOOK_TITLE, "BookTitle");
        NAMES2CODES.put(JOURNAL, "Journal");
        NAMES2CODES.put(PAGES, "Pages");
    }
    
    /** Creates new form JPanel */
    public BiBPanel() {
        initComponents();
	
	jComboBox1.setModel(new DefaultComboBoxModel(FieldDatabase.getDefault().getKnownTypes().toArray()));
        jComboBox1.addActionListener(this);
//        jComboBox1.getModel().addListDataListener(
    }
    
    private JTextField getTextField(String name) {
        try {
            String codeBase = (String) NAMES2CODES.get(name);
            
            if (codeBase == null)
                return null;
            
            String code = "t" + codeBase;
            
            if (code == null)
                return null;
            
            Field field = getClass().getDeclaredField(code);
            
            return (JTextField) field.get(this);
        } catch (NoSuchFieldException e) {
            //Should never happen:
            e.printStackTrace(System.err);
        } catch (IllegalArgumentException e) {
            //Should never happen:
            e.printStackTrace(System.err);
        } catch (IllegalAccessException e) {
            //Should never happen:
            e.printStackTrace(System.err);
        }
        
        return null;
    }

    private JLabel getLabel(String name) {
        try {
            String codeBase = (String) NAMES2CODES.get(name);
            
            if (codeBase == null)
                return null;
            
            String code = "l" + codeBase;
            
            if (code == null)
                return null;
            
            Field field = getClass().getDeclaredField(code);
            
            return (JLabel) field.get(this);
        } catch (NoSuchFieldException e) {
            //Should never happen:
            e.printStackTrace(System.err);
        } catch (IllegalArgumentException e) {
            //Should never happen:
            e.printStackTrace(System.err);
        } catch (IllegalAccessException e) {
            //Should never happen:
            e.printStackTrace(System.err);
        }
        
        return null;
    }
    
    private void setEnabled(Collection toEnable) {
        Iterator keys = NAMES2CODES.keySet().iterator();
        
        while (keys.hasNext()) {
            String key = (String) keys.next();
            JTextField field = getTextField(key);
	    JLabel     label = getLabel(key);
            
            field.setEnabled(toEnable.contains(key));
            field.setText("");
	    label.setEnabled(toEnable.contains(key));
        }
    }
    
    private void adjustToType(String type) {
        Map map = getContent();

        this.type = type;
        
        Collection c = new ArrayList();
	
	c.addAll(FieldDatabase.getDefault().getRequiredFields(type));
	c.addAll(FieldDatabase.getDefault().getOptionalFields(type));

    	setEnabled(c);
        
        setContent(map);
    }
    
    private void setContent(Map content) {
        Collection c = new ArrayList();
	
	c.addAll(FieldDatabase.getDefault().getRequiredFields(type));
	c.addAll(FieldDatabase.getDefault().getOptionalFields(type));
        
        Iterator keys = c.iterator();
        
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = (String) content.get(key);
            JTextField field = getTextField(key);
            
            if (field != null)
                field.setText(value != null ? value : "");
        }
        
        setVariableTable(content);
    }
    
    public void setContent(PublicationEntry entry) {
        type = entry.getType().toUpperCase();
        
        Collection c = new ArrayList();
	
	c.addAll(FieldDatabase.getDefault().getRequiredFields(type));
	c.addAll(FieldDatabase.getDefault().getOptionalFields(type));
	
	jComboBox1.setSelectedItem(type);
	
	setEnabled(c);
	
        setContent(entry.getContent());
    }
    
    private void setVariableTable(Map content) {
        Collection c = new ArrayList();
	
	c.addAll(FieldDatabase.getDefault().getRequiredFields(type));
	c.addAll(FieldDatabase.getDefault().getOptionalFields(type));

        Set toUse = new HashSet(content.keySet());
        
        toUse.removeAll(c);
        
        System.err.println("toUse = " + toUse );
        Iterator iter = toUse.iterator();
        int rowIndex = 0;
        
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        
        dtm.setRowCount(toUse.size());
        
        while (iter.hasNext()) {
            Object key = iter.next();
            
            jTable1.setValueAt(key, rowIndex, 0);
            jTable1.setValueAt(content.get(key), rowIndex, 1);
            rowIndex++;
        }
    }
    
    private void putVariableTable(Map content) {
        for (int cntr = 0; cntr < jTable1.getRowCount(); cntr++) {
            String key        = (String) jTable1.getValueAt(cntr, 0);
            String contentVal = (String) jTable1.getValueAt(cntr, 1);
            
            if (contentVal != null && contentVal.length() > 0)
                content.put(key, contentVal);
        }
    }
    
    private Map getContent() {
        Collection c = new ArrayList();
	
	c.addAll(FieldDatabase.getDefault().getRequiredFields(type));
	c.addAll(FieldDatabase.getDefault().getOptionalFields(type));
        
        System.err.println("getContent, c= " + c);
        Iterator keys = c.iterator();
        Map result = new HashMap();
        
        while (keys.hasNext()) {
            String key = (String) keys.next();
            System.err.println("key = " + key );
            JTextField field = getTextField(key);
            
            if (field == null || !field.isEnabled())
                continue;
            
            String contentVal = field.getText();
            
            if (contentVal != null && contentVal.length() > 0)
                result.put(key, contentVal);
            
            System.err.println("result = " + result );
        }
        
        putVariableTable(result);
        
        return result;
    }
    
    public void actionPerformed(ActionEvent evt) {
        String wanted = (String) jComboBox1.getSelectedItem();
        
        if (wanted.equals(type))
            return ;
        
        adjustToType(wanted);
    }
    
    public void fillIntoEntry(PublicationEntry entry) {
        entry.setType(type);
        entry.setContent(getContent());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        lTitle = new javax.swing.JLabel();
        tTitle = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        lAuthor = new javax.swing.JLabel();
        tAuthor = new javax.swing.JTextField();
        lJournal = new javax.swing.JLabel();
        tJournal = new javax.swing.JTextField();
        lYear = new javax.swing.JLabel();
        tYear = new javax.swing.JTextField();
        lPages = new javax.swing.JLabel();
        tPages = new javax.swing.JTextField();
        lBookTitle = new javax.swing.JLabel();
        tBookTitle = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        lTitle.setLabelFor(tTitle);
        lTitle.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/latex/bibtex/Bundle").getString("LBL_title"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(lTitle, gridBagConstraints);

        tTitle.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(tTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jComboBox1, gridBagConstraints);

        lAuthor.setLabelFor(tAuthor);
        lAuthor.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/latex/bibtex/Bundle").getString("LBL_author"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(lAuthor, gridBagConstraints);

        tAuthor.setText("jTextField2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(tAuthor, gridBagConstraints);

        lJournal.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/latex/bibtex/Bundle").getString("LBL_journal"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(lJournal, gridBagConstraints);

        tJournal.setText("jTextField3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(tJournal, gridBagConstraints);

        lYear.setLabelFor(tYear);
        lYear.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/latex/bibtex/Bundle").getString("LBL_year"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(lYear, gridBagConstraints);

        tYear.setText("jTextField4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(tYear, gridBagConstraints);

        lPages.setLabelFor(tPages);
        lPages.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/latex/bibtex/Bundle").getString("LBL_pages"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(lPages, gridBagConstraints);

        tPages.setText("jTextField5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(tPages, gridBagConstraints);

        lBookTitle.setLabelFor(tBookTitle);
        lBookTitle.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/latex/bibtex/Bundle").getString("LBL_booktitle"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        add(lBookTitle, gridBagConstraints);

        tBookTitle.setText("jTextField6");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        gridBagConstraints.weightx = 1.0;
        add(tBookTitle, gridBagConstraints);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jScrollPane1, gridBagConstraints);

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lAuthor;
    private javax.swing.JLabel lBookTitle;
    private javax.swing.JLabel lJournal;
    private javax.swing.JLabel lPages;
    private javax.swing.JLabel lTitle;
    private javax.swing.JLabel lYear;
    private javax.swing.JTextField tAuthor;
    private javax.swing.JTextField tBookTitle;
    private javax.swing.JTextField tJournal;
    private javax.swing.JTextField tPages;
    private javax.swing.JTextField tTitle;
    private javax.swing.JTextField tYear;
    // End of variables declaration//GEN-END:variables
    
}
