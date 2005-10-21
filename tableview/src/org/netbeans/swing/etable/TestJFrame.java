/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */
package org.netbeans.swing.etable;

import java.util.Enumeration;
import java.util.Properties;

/**
 * This class is for testing purposes only. It is public in order to be
 * run using java command. It definitelly does not belong here but it confused
 * the build system when put under test.
 *
 * This class should be deleted or moved before this package becomes part
 * of any API or something.
 *
 * @author  David Strupl
 */
public class TestJFrame extends javax.swing.JFrame {
    
    private Properties savedState = new Properties();
    
    /** Creates new form TestJFrame */
    public TestJFrame() {
        initComponents();
//        eTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
//        eTable1.setFullyNonEditable(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane1 = new javax.swing.JScrollPane();
        eTable1 = new org.netbeans.swing.etable.ETable();
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        eTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"a", "x", "tttttttt", new Integer(5), "aaaaab"},
                {"a2", "y", "ggggggggg", new Integer(10), "aaaaaa"},
                {"b", "z", "nnnnnnnn", new Integer(7), "aaaaaa"},
                {"b3", "w", "mmmmmm", new Integer(1), "aaaaab"},
                {"c", "m", "kkkkkkkkkk", new Integer(10000), "aaaaab"},
                {"c2", "n", "kkkkk", new Integer(4), "aaaaab"},
                {"d", "df", "wwwwwwww", new Integer(17), "aaaaaccc"},
                {"d3", "f", "ggggggggggggg", new Integer(13), "aaaaac"},
                {"e", "mm", "hhhhhhhhhhhh", new Integer(2), "aaaaac"},
                {"e2", "mg", "qqqqqqqqqqq", new Integer(8), "aaaaad"},
                {"f", "q", "ffffffffffffff", new Integer(23), "aaaaa"},
                {"f2", "dfg", "nnnnnnnnnnnn", new Integer(100), "aaaaa"},
                {"g", "dfg", "mmmmmmmmmmm", new Integer(57), "aaaaa"},
                {"g2", "fg", "qqqqqqqqq", new Integer(3), "aaaaa"},
                {"h", "xb", "nnnnnnnnnnn", new Integer(123), "aaaaa"},
                {"h2", "sdf", "asdfasdf", new Integer(321), "aaaaa"},
                {"i", "g", "fads", new Integer(42), "aaaaarrrr"},
                {"i2", "mn", "asdf", new Integer(72), "aaaaa"},
                {"j", "we", "asdfdsafafsdfsd", new Integer(88), "aaaaaaaaa"},
                {"j2", "kl", "asdfasdfafsdasdfasd", new Integer(99), "aaaaa"}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(eTable1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.add(jTextField1);

        jCheckBox1.setText("Apply filter");
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });

        jPanel1.add(jCheckBox1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel2.add(jButton1);

        jButton2.setText("Load");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel2.add(jButton2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

        pack();
    }//GEN-END:initComponents

    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged
        if (jCheckBox1.isSelected()) {
            eTable1.setQuickFilter(0, jTextField1.getText());
        } else {
            eTable1.unsetQuickFilter();
        }
    }//GEN-LAST:event_jCheckBox1StateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (! savedState.isEmpty()) {
            eTable1.readSettings(savedState, "test:");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        savedState = new Properties();
        eTable1.writeSettings(savedState, "test:");
        for (Enumeration en = savedState.keys(); en.hasMoreElements(); ) {
            Object key = en.nextElement();
            System.out.println(key + " : " + savedState.get(key));
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestJFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.swing.etable.ETable eTable1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
    
}
