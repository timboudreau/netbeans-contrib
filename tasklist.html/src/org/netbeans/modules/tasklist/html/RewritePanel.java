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

package org.netbeans.modules.tasklist.html;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.event.*;
import org.openide.loaders.DataObject;

import org.openide.util.NbBundle;

/**
 * Confirmation panel before launching an HTML rewrite;
 * provide the user the capability to set some of jtidy's
 * options
 * <p>
 * @todo Add option to show the new file in a merge dialog?
 *
 * @author  Tor Norbye
 */
public class RewritePanel extends javax.swing.JPanel 
     implements ActionListener {
    
    private RewriteAction action;
    private Document doc;
    private DataObject dobj;

    /** Creates new form RewritePanel */
    public RewritePanel(RewriteAction action, Document doc, DataObject dobj) {
        this.action = action;
        this.doc = doc;
        this.dobj = dobj;
        initComponents();
        xhtmlCB.addActionListener(this);
        previewButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == xhtmlCB) {
            // Cannot choose uppercase tags or strip-end-tags
            // on XML mode
            boolean xml = xhtmlCB.isSelected();
            if (upperRB.isSelected()) {
                upperRB.setSelected(false);
                lowerRB.setSelected(true);
            }
            upperRB.setEnabled(!xml);
            if (omitCB.isSelected()) {
                omitCB.setSelected(false);
            }
            omitCB.setEnabled(!xml);
        } else if (evt.getSource() == previewButton) {
            action.preview(this, doc, dobj);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        caseButtonGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        xhtmlCB = new javax.swing.JCheckBox();
        replaceCB = new javax.swing.JCheckBox();
        lowerRB = new javax.swing.JRadioButton();
        upperRB = new javax.swing.JRadioButton();
        omitCB = new javax.swing.JCheckBox();
        indentCB = new javax.swing.JCheckBox();
        wrapCB = new javax.swing.JCheckBox();
        wrapText = new javax.swing.JTextField();
        previewButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        jLabel1.setText(NbBundle.getMessage(RewritePanel.class, "RewriteSettings")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel1, gridBagConstraints);

        xhtmlCB.setText(NbBundle.getMessage(RewritePanel.class, "ConvertXHTML")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(xhtmlCB, gridBagConstraints);

        replaceCB.setText(NbBundle.getMessage(RewritePanel.class, "ReplaceFont")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(replaceCB, gridBagConstraints);

        lowerRB.setSelected(true);
        lowerRB.setText(NbBundle.getMessage(RewritePanel.class, "Lowercase")); // NOI18N();
        caseButtonGroup.add(lowerRB);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(lowerRB, gridBagConstraints);

        upperRB.setText(NbBundle.getMessage(RewritePanel.class, "Uppercase")); // NOI18N();
        caseButtonGroup.add(upperRB);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(upperRB, gridBagConstraints);

        omitCB.setText(NbBundle.getMessage(RewritePanel.class, "OmitOptional")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        add(omitCB, gridBagConstraints);

        indentCB.setText(NbBundle.getMessage(RewritePanel.class, "Indent")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(indentCB, gridBagConstraints);

        wrapCB.setText(NbBundle.getMessage(RewritePanel.class, "WrapCol")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(wrapCB, gridBagConstraints);

        wrapText.setColumns(3);
        wrapText.setText("68");
        wrapText.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(wrapText, gridBagConstraints);

        previewButton.setText(NbBundle.getMessage(RewritePanel.class, "Preview")); // NOI18N();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(previewButton, gridBagConstraints);

    }//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox indentCB;
    private javax.swing.JCheckBox replaceCB;
    private javax.swing.JRadioButton upperRB;
    private javax.swing.JTextField wrapText;
    private javax.swing.JButton previewButton;
    private javax.swing.JCheckBox omitCB;
    private javax.swing.JCheckBox wrapCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JCheckBox xhtmlCB;
    private javax.swing.ButtonGroup caseButtonGroup;
    private javax.swing.JRadioButton lowerRB;
    // End of variables declaration//GEN-END:variables

   boolean getXHTML() {    
        return xhtmlCB.isSelected();
   }

   boolean getIndent() {    
        return indentCB.isSelected();
   }

   boolean getReplace() {
        return replaceCB.isSelected();
   }

   boolean getOmit() {
        return omitCB.isSelected();
   }

   boolean getUpper() {
        return upperRB.isSelected();
   }

   int getWrapCol() {
       if (!wrapCB.isSelected()) {
           return 0;
       }
       String str = wrapText.getText().trim();
       int col = 68;
       try {
           col = Integer.parseInt(str);
       } catch (Exception e) {
       }
       return col;
   }

    void setXHTML(boolean on) {    
        xhtmlCB.setSelected(on);
   }

   void setIndent(boolean on) {    
        indentCB.setSelected(on);
   }

   void setReplace(boolean on) {
        replaceCB.setSelected(on);
   }

   void setOmit(boolean on) {
        omitCB.setSelected(on);
   }

   void setUpper(boolean on) {
        upperRB.setSelected(on);
        lowerRB.setSelected(!on);
   }

   void setWrapCol(int col) {
       if (col == 0) {
           wrapCB.setSelected(false);
       } else {
           wrapText.setText(Integer.toString(col));
           wrapCB.setSelected(true);
       }
   }

   void setXML(boolean on) {
       // Document is XML - cannot "convert to" XML
       xhtmlCB.setEnabled(!on);
       if (on) {
           upperRB.setSelected(false);
           lowerRB.setSelected(true);
           omitCB.setSelected(false);
           omitCB.setEnabled(false);
           replaceCB.setSelected(false);
           replaceCB.setEnabled(false);
       }
   }
}
