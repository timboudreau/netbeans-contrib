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
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.vcscore.commands.CommandOutputCollector;
import org.netbeans.modules.vcscore.commands.CommandOutputTopComponent;
import org.netbeans.modules.vcscore.commands.RegexErrorListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.SaveToFilePanel;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * AbstractOutputPanel.java
 *
 * Created on December 21, 2003, 11:16 AM
 * @author  Richard Gregor
 */
public abstract class AbstractOutputPanel extends javax.swing.JPanel {
    
    private JPopupMenu menu;
    private Object eventSource;
   // private JMenuItem kill;
    private ArrayList killActionListeners = new ArrayList();
    private JTextArea stdDataOutput;
    private JTextArea errDataOutput;
    private CommandOutputCollector outputCollector;
    
    /** Creates new form OutputPanel */
    public AbstractOutputPanel() {
        initComponents(); 
        initPopupMenu();
        if (Boolean.getBoolean("netbeans.vcs.dev")) {
            addDataOutputButtons();
        }
        Font font = btnErr.getFont();
        FontMetrics fm = btnErr.getFontMetrics(font);
        int height = fm.getHeight();
        Dimension dim = toolbar.getPreferredSize();
        toolbar.setPreferredSize(new Dimension(dim.width,height+6));   
        toolbar.setMinimumSize(new Dimension(dim.width,height+6));
        toolbar.setMaximumSize(new Dimension(dim.width,height+6));
        dim = btnStop.getPreferredSize();
        btnStop.setPreferredSize(new Dimension(dim.width,height+6));
        btnStop.setMinimumSize(new Dimension(dim.width,height+6));
        btnStop.setMaximumSize(new Dimension(dim.width,height+6));       
        if (getErrOutputArea() != null) {
            getErrOutputArea().getDocument().addDocumentListener(new OutputButtonEnabler(btnErr));
        }
        if (btnDataStd != null && getDataStdOutputArea() != null) {
            getDataStdOutputArea().getDocument().addDocumentListener(new OutputButtonEnabler(btnDataStd));
        }
        if (btnDataErr != null && getDataErrOutputArea() != null) {
            getDataErrOutputArea().getDocument().addDocumentListener(new OutputButtonEnabler(btnDataErr));
        }
        setStandardContent();
    }
    
    /**
     * Set the output collector.
     */
    public void setOutputCollector(CommandOutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }
    
    /**
     * Get the output collector.
     */
    protected CommandOutputCollector getOutputCollector() {
        return outputCollector;
    }
    
    protected void initPopupMenu() {
        this.menu = new JPopupMenu();
        JMenuItem discardTab = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardTab"));//NOI18N
        discardTab.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                CommandOutputTopComponent.getInstance().discard(AbstractOutputPanel.this);
            }
        });
        JMenuItem discardAll = new JMenuItem(NbBundle.getBundle(OutputPanel.class).getString("CMD_DiscardAll"));//NOI18N
        discardAll.addActionListener( new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                CommandOutputTopComponent.getInstance().discardAll();
            }
        });
        JMenuItem save = new JMenuItem (NbBundle.getBundle (OutputPanel.class).getString("CMD_Save"));//NOI18N
        save.addActionListener ( new java.awt.event.ActionListener () {
           public void actionPerformed (java.awt.event.ActionEvent event) {
               saveToFile();
           }
        });
        
        this.menu.add(save);
        this.menu.addSeparator();
        this.menu.add(discardTab);
        this.menu.add(discardAll);
         
        if(getStdOutputArea() != null)
            getStdOutputArea().add(menu);
        if(getErrOutputArea() != null)
            getErrOutputArea().add(menu);
        
        PopupListener popupListener = new PopupListener();
        getErrComponent().addMouseListener(popupListener);
        getStdComponent().addMouseListener(popupListener);
        this.addMouseListener(popupListener);
        toolbar.addMouseListener(popupListener);
        scroll.addMouseListener(popupListener);
        
    }
    
    private void saveToFile() {
        SaveToFilePanel pnl = new SaveToFilePanel();
        pnl.setCurrentPanel(btnStd.isSelected() ? 0 : (btnErr.isSelected() ? 1 : 0));//jTabbedPane1.getSelectedIndex());
        java.io.File file = null;
        NotifyDescriptor descriptor = new DialogDescriptor(pnl, NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFile.title"));//NOI18N
        boolean ok = false;
        while (!ok) {
            ok = true;
            Object retVal = DialogDisplayer.getDefault().notify(descriptor);
            if (retVal.equals(NotifyDescriptor.OK_OPTION)) {
                java.io.File init = new java.io.File(pnl.getFile());
                if (init.exists()) {
                    NotifyDescriptor mess = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(SaveToFilePanel.class, "SaveToFile.fileExistsQuestion", init.getName()), //NOI18N
                    NotifyDescriptor.YES_NO_OPTION);
                    Object rVal = DialogDisplayer.getDefault().notify(mess);
                    if (!rVal.equals(NotifyDescriptor.YES_OPTION)) {
                        ok = false;
                        continue;
                    }
                    file = init;
                } else {
                    java.io.File parent = init.getParentFile();
                    if (!parent.exists()) parent.mkdirs();
                    file = init;
                }
            } else {
                return;
            }
        }
        final java.io.File finFile = file;
        final SaveToFilePanel finPnl = pnl;
        
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                java.io.BufferedWriter writer = null;
                try {
                    writer = new java.io.BufferedWriter(new java.io.FileWriter(finFile));
                    if (finPnl.includeStdOut()) {
                        if (outputCollector != null) {
                            final java.io.BufferedWriter fwriter = writer;
                            outputCollector.addTextOutputListener(new TextOutputListener() {
                                public void outputLine(String line) {
                                    try {
                                        fwriter.write(line);
                                        fwriter.newLine();
                                    } catch (IOException ioex) {
                                    }
                                }
                            }, false);
                        } else {
                            javax.swing.JTextArea outputArea = getStdOutputArea();
                            if (outputArea != null) {
                                writer.write(outputArea.getDocument().getText(0, outputArea.getDocument().getLength()));
                                writer.newLine();
                            }
                        }
                    }
                    if (finPnl.includeStdErr()) {
                        if (outputCollector != null) {
                            final java.io.BufferedWriter fwriter = writer;
                            outputCollector.addTextErrorListener(new TextErrorListener() {
                                public void outputLine(String line) {
                                    try {
                                        fwriter.write(line);
                                        fwriter.newLine();
                                    } catch (IOException ioex) {
                                    }
                                }
                            }, false);
                        } else {
                            javax.swing.JTextArea outputArea = getErrOutputArea();
                            writer.write(outputArea.getDocument().getText(0, outputArea.getDocument().getLength()));
                            writer.newLine();
                        }
                    }
                    if (finPnl.includeDatOut()) {
                        if (outputCollector != null) {
                            final java.io.BufferedWriter fwriter = writer;
                            outputCollector.addRegexOutputListener(new RegexOutputListener() {
                                public void outputMatchedGroups(String[] elements) {
                                    try {
                                        fwriter.write(VcsUtilities.arrayToString(elements));
                                        fwriter.newLine();
                                    } catch (IOException ioex) {
                                    }
                                }
                            }, false);
                        } else {
                            javax.swing.JTextArea outputArea = getDataStdOutputArea();
                            writer.write(outputArea.getDocument().getText(0, outputArea.getDocument().getLength()));
                            writer.newLine();
                        }
                    }
                    if (finPnl.includeDatErr()) {
                        if (outputCollector != null) {
                            final java.io.BufferedWriter fwriter = writer;
                            outputCollector.addRegexErrorListener(new RegexErrorListener() {
                                public void outputMatchedGroups(String[] elements) {
                                    try {
                                        fwriter.write(VcsUtilities.arrayToString(elements));
                                        fwriter.newLine();
                                    } catch (IOException ioex) {
                                    }
                                }
                            }, false);
                        } else {
                            javax.swing.JTextArea outputArea = getDataErrOutputArea();
                            writer.write(outputArea.getDocument().getText(0, outputArea.getDocument().getLength()));
                            writer.newLine();
                        }
                    }
                } catch (Exception exc) {
                   ErrorManager.getDefault().notify(
                        ErrorManager.getDefault().annotate(exc,
                            NbBundle.getBundle(SaveToFilePanel.class).getString("SaveToFile.errorWhileWriting"))); //NOI18N
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (java.io.IOException ioex) {}
                    }
                }
            }
        }, 0);
    }
    
    public void addKillActionListener(java.awt.event.ActionListener l) {        
        btnStop.addActionListener(l);
        killActionListeners.add(l);
    }
    
    public void removeKillActionListener(java.awt.event.ActionListener l) {       
        btnStop.removeActionListener(l);
        killActionListeners.remove(l);
    }
    
    public void commandFinished(final int exit) {
        while (killActionListeners.size() > 0) {
            java.awt.event.ActionListener l = (java.awt.event.ActionListener)killActionListeners.remove(0);            
            btnStop.removeActionListener(l);
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){                    
                    btnStop.setEnabled(false);
                    if(exit == 0)
                        lblStatus.setText(NbBundle.getBundle(OutputPanel.class).getString("OutputPanel.StatusFinished"));
                    else
                        lblStatus.setText(NbBundle.getBundle(OutputPanel.class).getString("OutputPanel.StatusFailed"));                 
                    progress.setIndeterminate(false);
                    progress.setValue(100);
                    progress.setVisible(false);
                    btnStop.setVisible(false);
                    if (!isStdOutput() && isErrOutput())
                        btnErrActionPerformed(new ActionEvent(btnErr,ActionEvent.ACTION_PERFORMED,btnErr.getText()));
                }
            });
        }
        
        
    }    
       
    
    class PopupListener extends java.awt.event.MouseAdapter {
        public void mousePressed(java.awt.event.MouseEvent event) {
            if ((event.getModifiers() & java.awt.event.MouseEvent.BUTTON3_MASK) == java.awt.event.MouseEvent.BUTTON3_MASK) {
                AbstractOutputPanel.this.eventSource = event.getSource();
                AbstractOutputPanel.this.menu.show((java.awt.Component)event.getSource(),event.getX(),event.getY());
            }
        }
    }
    
    private static class OutputButtonEnabler extends Object implements DocumentListener {
        
        javax.swing.JToggleButton btn;
        
        public OutputButtonEnabler(javax.swing.JToggleButton button) {
            this.btn = button;
        }
        
        public void changedUpdate(DocumentEvent e) {}
        
        public void insertUpdate(DocumentEvent e) {
            btn.setEnabled(true);
        }
        
        public void removeUpdate(DocumentEvent e) {}

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
        rightPanel = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        separator = new javax.swing.JSeparator();
        btnStop = new javax.swing.JButton();
        scroll = new javax.swing.JScrollPane();

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

        rightPanel.setLayout(new java.awt.GridBagLayout());

        lblStatus.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.StatusRunning"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        rightPanel.add(lblStatus, gridBagConstraints);

        progress.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 6);
        gridBagConstraints.weightx = 0.2;
        rightPanel.add(progress, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.8;
        rightPanel.add(jPanel2, gridBagConstraints);

        separator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        rightPanel.add(separator, gridBagConstraints);

        toolbar.add(rightPanel);

        btnStop.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnStop"));
        btnStop.setRolloverEnabled(true);
        toolbar.add(btnStop);
        btnStop.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnStop"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 1);
        add(toolbar, gridBagConstraints);
        toolbar.getAccessibleContext().setAccessibleName(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.toolbar"));
        toolbar.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.toolbar"));

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
        if (btnDataStd != null) btnDataStd.setSelected(false);
        if (btnDataErr != null) btnDataErr.setSelected(false);
        setErrorContent();        
    }//GEN-LAST:event_btnErrActionPerformed

    private void setErrorContent(){
        scroll.setViewportView(getErrComponent());
    }
    private void btnStdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStdActionPerformed
        btnErr.setSelected(false);            
        btnStd.setSelected(true);
        if (btnDataStd != null) btnDataStd.setSelected(false);
        if (btnDataErr != null) btnDataErr.setSelected(false);
        setStandardContent();
    }//GEN-LAST:event_btnStdActionPerformed

    private void setStandardContent(){
        scroll.setViewportView(getStdComponent());       
    }

    private void addDataOutputButtons() {
        btnDataStd = new javax.swing.JToggleButton();
        btnDataErr = new javax.swing.JToggleButton();
        
        btnDataStd.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnDataStd_mnc").charAt(0));
        btnDataStd.setEnabled(false);
        btnDataStd.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnDataStd"));
        btnDataStd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataStdActionPerformed(evt);
            }
        });

        toolbar.add(btnDataStd, 2);
        btnDataStd.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnDataStd"));

        btnDataErr.setMnemonic(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACS_OutputPanel.btnDataErr_mnc").charAt(0));
        btnDataErr.setText(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("OutputPanel.btnDataErr"));
        btnDataErr.setEnabled(false);
        btnDataErr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataErrActionPerformed(evt);
            }
        });

        toolbar.add(btnDataErr, 3);
        btnDataErr.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle("org/netbeans/modules/vcscore/ui/Bundle").getString("ACSD_OutputPanel.btnDataErr"));

    }
    
    private void btnDataStdActionPerformed(java.awt.event.ActionEvent evt) {
        btnStd.setSelected(false);
        btnErr.setSelected(false);
        btnDataStd.setSelected(true);
        btnDataErr.setSelected(false);
        setDataStandardContent();
    }

    private void setDataStandardContent(){
        scroll.setViewportView(getDataStdComponent());       
    }

    private void btnDataErrActionPerformed(java.awt.event.ActionEvent evt) {
        btnStd.setSelected(false);
        btnErr.setSelected(false);
        btnDataStd.setSelected(false);
        btnDataErr.setSelected(true);
        setDataErrorContent();        
    }

    private void setDataErrorContent(){
        scroll.setViewportView(getDataErrComponent());
    }
    
    public javax.swing.JTextArea getStdOutputArea(){
        if(getStdComponent() instanceof javax.swing.JTextArea)
            return (JTextArea)getStdComponent();
        else
            return null;
    }
    
    public javax.swing.JTextArea getErrOutputArea(){
        if(getErrComponent() instanceof javax.swing.JTextArea)
            return (JTextArea)getErrComponent();
        else
            return null;
    }
    
    public javax.swing.JTextArea getDataStdOutputArea(){
        if(getDataStdComponent() instanceof javax.swing.JTextArea)
            return (JTextArea)getDataStdComponent();
        else
            return null;
    }
    
    public javax.swing.JTextArea getDataErrOutputArea(){
        if(getDataErrComponent() instanceof javax.swing.JTextArea)
            return (JTextArea)getDataErrComponent();
        else
            return null;
    }
    
    
    protected JPanel getOutputPanel() {
        return this;
    }
    
    /**
     *Return true in case command finished with some std output
     *
     */
    protected abstract boolean isStdOutput();
    
    /**
     *Return true in case command finished with some err output
     *
     */
    protected abstract boolean isErrOutput();
    
    
    protected abstract JComponent getErrComponent();
    
    protected abstract JComponent getStdComponent();
    
    /**
     * The component that display standard data output.
     * Returns a JTextArea by default.
     * Subclasses can return a different component here.
     */
    protected JComponent getDataStdComponent() {
        if(stdDataOutput == null){
            stdDataOutput = new JTextArea();
            stdDataOutput.setEditable(false);
        }
        return stdDataOutput;
    }
    
    /**
     * The component that display error data output.
     * Returns a JTextArea by default.
     * Subclasses can return a different component here.
     */
    protected JComponent getDataErrComponent() {
        if(errDataOutput == null){
            errDataOutput = new JTextArea();
            errDataOutput.setEditable(false);
        }
        return errDataOutput;
    }
      
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnErr;
    private javax.swing.JToggleButton btnStd;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progress;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JSeparator separator;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables
    
    private javax.swing.JToggleButton btnDataStd;
    private javax.swing.JToggleButton btnDataErr;
}
