/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */



package org.netbeans.modules.vcscore.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.openide.util.NbBundle;

/**
 * OutputPanel.java
 *
 * Created on December 1, 2003, 11:16 AM
 * @author  Richard Gregor
 */
public class OutputPanel extends javax.swing.JPanel {
    private boolean errEnabled = false;
    private JPopupMenu menu;
    private Object eventSource;
    private JMenuItem kill;
    private ArrayList killActionListeners = new ArrayList();
    
    /** Creates new form OutputPanel */
    public OutputPanel() {
        initComponents(); 
        initPopupMenu();
        Font font = btnErr.getFont();
        FontMetrics fm = btnErr.getFontMetrics(font);
        int height = fm.getHeight();
        Dimension dim = toolbar.getPreferredSize();
        toolbar.setPreferredSize(new Dimension(dim.width,height+6));   
        toolbar.setMinimumSize(new Dimension(dim.width,height+6));
        toolbar.setMaximumSize(new Dimension(dim.width,height+6));
        errOutputTextArea.getDocument().addDocumentListener(new DocumentListener(){
           public void changedUpdate(DocumentEvent e){          
               
           }
           public void insertUpdate(DocumentEvent e){
                btnErr.setEnabled(true);
                errEnabled = true;
           }
           public void removeUpdate(DocumentEvent e){
                
           }
  
        });
            
        
    }

    private void initPopupMenu() {
        this.menu = new JPopupMenu();
        JMenuItem discardTab = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab"));//NOI18N
        discardTab.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
               CommandOutputTopComponent.getInstance().discard(OutputPanel.this);              
            }
        });
        JMenuItem discardAll = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardAll"));//NOI18N
        discardAll.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                CommandOutputTopComponent.getInstance().discardAll();
            }
        });
  
        kill = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_Kill"));//NOI18N
        
        
        this.menu.add(discardTab);        
        this.menu.add(discardAll);  
        this.menu.addSeparator();
        this.menu.add(kill);
        
        this.stdOutputTextArea.add(menu);
        this.errOutputTextArea.add(menu);
        
        PopupListener popupListener = new PopupListener();
        this.stdOutputTextArea.addMouseListener(popupListener);
        this.errOutputTextArea.addMouseListener(popupListener);
        this.addMouseListener(popupListener);
        toolbar.addMouseListener(popupListener);
        scroll.addMouseListener(popupListener);
 
    }
    
    public void addKillActionListener(java.awt.event.ActionListener l) {
        kill.addActionListener(l);        
        killActionListeners.add(l);
    }
    
    public void removeKillActionListener(java.awt.event.ActionListener l) {
        kill.removeActionListener(l);
        killActionListeners.remove(l);
    }
        
    public void commandFinished(boolean isFinished) {
        if (isFinished) {                        
            while (killActionListeners.size() > 0) {
                kill.removeActionListener((java.awt.event.ActionListener) killActionListeners.remove(0));
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        kill.setEnabled(false);                        
                        if((stdOutputTextArea.getText().length() == 0)&&(errOutputTextArea.getText().length()>0))
                            btnErrActionPerformed(new ActionEvent(btnErr,ActionEvent.ACTION_PERFORMED,btnErr.getText()));
                    }
                });
            }            
            
        }
    }
    
    class PopupListener extends java.awt.event.MouseAdapter {
        public void mousePressed(java.awt.event.MouseEvent event) {
            if ((event.getModifiers() & java.awt.event.MouseEvent.BUTTON3_MASK) == java.awt.event.MouseEvent.BUTTON3_MASK) {
                OutputPanel.this.eventSource = event.getSource();
                OutputPanel.this.menu.show((java.awt.Component)event.getSource(),event.getX(),event.getY());
            }
        }
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        toolbar = new javax.swing.JToolBar();
        btnStd = new javax.swing.JToggleButton();
        btnErr = new javax.swing.JToggleButton();
        scroll = new javax.swing.JScrollPane();
        errOutputTextArea = new javax.swing.JTextArea();
        stdOutputTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel"));
        getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel"));
        toolbar.setBorder(null);
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setPreferredSize(new java.awt.Dimension(205, 24));
        btnStd.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnStd_mnc").charAt(0));
        btnStd.setSelected(true);
        btnStd.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnStd"));
        btnStd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStdActionPerformed(evt);
            }
        });

        toolbar.add(btnStd);
        btnStd.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnStd"));

        btnErr.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnErr_mnc").charAt(0));
        btnErr.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnErr"));
        btnErr.setEnabled(false);
        btnErr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnErrActionPerformed(evt);
            }
        });

        toolbar.add(btnErr);
        btnErr.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnErr"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 1);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(toolbar, gridBagConstraints);
        toolbar.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.toolbar"));
        toolbar.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.toolbar"));

        errOutputTextArea.setEditable(false);
        scroll.setViewportView(errOutputTextArea);

        stdOutputTextArea.setEditable(false);
        scroll.setViewportView(stdOutputTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 1);
        add(scroll, gridBagConstraints);
        scroll.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.scroll"));
        scroll.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.scroll"));

    }//GEN-END:initComponents

    private void btnErrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnErrActionPerformed
        btnStd.setSelected(false);
        btnErr.setSelected(true);
        setErrorContent();        
    }//GEN-LAST:event_btnErrActionPerformed

    private void setErrorContent(){
        scroll.setViewportView(errOutputTextArea);
    }
    private void btnStdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStdActionPerformed
        if(errEnabled){
            btnErr.setSelected(false);            
        }
        btnStd.setSelected(true);
        setStandardContent();
    }//GEN-LAST:event_btnStdActionPerformed
    
    private void setStandardContent(){
        scroll.setViewportView(stdOutputTextArea);       
    }
    
    public javax.swing.JTextArea getStdOutputArea() {
        return stdOutputTextArea;
    }
    
    public javax.swing.JTextArea getErrOutputArea() {        
        return errOutputTextArea;
    }
    
    protected JPanel getOutputPanel() {
        return this;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnErr;
    private javax.swing.JToggleButton btnStd;
    private javax.swing.JTextArea errOutputTextArea;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTextArea stdOutputTextArea;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
    
}
