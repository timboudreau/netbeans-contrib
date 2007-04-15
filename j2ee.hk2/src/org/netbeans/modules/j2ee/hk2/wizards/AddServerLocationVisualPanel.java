/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.hk2.wizards;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.NbBundle;

/**
 * @author pblaha
 * @author peterw99
 */
public class AddServerLocationVisualPanel extends javax.swing.JPanel implements Retriever.Updater {

    private static final String V3_DOWNLOAD_URL = 
            "http://download.java.net/maven/glassfish/com/sun/enterprise/glassfish/pe/10.0-SNAPSHOT/pe-10.0-SNAPSHOT.zip";
    
    private final Set <ChangeListener> listeners = new HashSet<ChangeListener>();
    private Retriever retriever;
    private volatile String statusText;

    /** Creates new form AddServerLocationVisualPanel */
    public AddServerLocationVisualPanel() {
        initComponents();
        hk2HomeTextField.setText("/Users/ludo/gfv3/v3");
        setName(NbBundle.getMessage(AddServerLocationVisualPanel.class, "TITLE_ServerLocation"));
        hk2HomeTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                fireChangeEvent();
            }
            public void insertUpdate(DocumentEvent e) {
                fireChangeEvent();
            }
            public void removeUpdate(DocumentEvent e) {
                fireChangeEvent();
            }                    
        });
        updateCancelState(false);
    }
    
    /**
     * 
     * @return 
     */
    public String getHk2HomeLocation() {
        return hk2HomeTextField.getText();
    }
    
    /**
     * 
     * @return
     */
    public String getStatusText() {
        return statusText;
    }
    
    /**
     * 
     * @param l 
     */
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    /**
     * 
     * @param l 
     */
    public void removeChangeListener(ChangeListener l ) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    private void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged (ev);
        }
    }
    
    private String browseHomeLocation(){
        String hk2Location = null;
        JFileChooser chooser = getJFileChooser();
        int returnValue = chooser.showDialog(this, NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_ChooseButton")); //NOI18N
        
        if(returnValue == JFileChooser.APPROVE_OPTION){
            hk2Location = chooser.getSelectedFile().getAbsolutePath();
        }
        return hk2Location;
    }
    
    private JFileChooser getJFileChooser(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_ChooserName")); //NOI18N
        chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setApproveButtonMnemonic("Choose_Button_Mnemonic".charAt(0)); //NOI18N
        chooser.setMultiSelectionEnabled(false);
        chooser.addChoosableFileFilter(new DirFilter());
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setApproveButtonToolTipText(NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_ChooserName")); //NOI18N
        chooser.getAccessibleContext().setAccessibleName(NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_ChooserName")); //NOI18N
        chooser.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_ChooserName")); //NOI18N

        // set the current directory
        File currentLocation = new File(hk2HomeTextField.getText());
        if (currentLocation.exists() && currentLocation.isDirectory()) {
            chooser.setCurrentDirectory(currentLocation.getParentFile());
            chooser.setSelectedFile(currentLocation);
        }
        
        return chooser;
    }
    
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        hk2HomeLbl = new javax.swing.JLabel();
        hk2HomeTextField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        downloadButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        hk2HomeLbl.setLabelFor(hk2HomeTextField);
        org.openide.awt.Mnemonics.setLocalizedText(hk2HomeLbl, NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_InstallLocation"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(hk2HomeLbl, gridBagConstraints);
        hk2HomeTextField.setColumns(15);
        hk2HomeTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_InstallLocation"));
        hk2HomeTextField.getAccessibleContext().setAccessibleName(
                NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_InstallLocation"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(hk2HomeTextField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_BrowseButton"));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(jButton1, gridBagConstraints);
        jButton1.getAccessibleContext().setAccessibleName(NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_BrowseButton"));
        jButton1.getAccessibleContext().setAccessibleDescription("ACSD_Browse_Button_InstallLoc");

        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(downloadButton, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
        jPanel1.getAccessibleContext().setAccessibleName("TITLE_AddServerLocationPanel");
        jPanel1.getAccessibleContext().setAccessibleDescription("AddServerLocationPanel_Desc");        
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String newLoc = browseHomeLocation();
        if(newLoc != null && newLoc.length() > 0) {
            hk2HomeTextField.setText(newLoc);
        }
    }
    
    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(retriever == null) {
            retriever = new Retriever(new File(hk2HomeTextField.getText()), 
                    V3_DOWNLOAD_URL, this);
            new Thread(retriever).start();
            updateCancelState(true);
        } else {
            retriever.stopRetrieval();
        }
    }
    
    public void removeNotify() {
        // !PW Is there a better place for this?  If the retriever is still running
        // the user must have hit cancel on the wizard, so tell the retriever thread
        // to shut down and clean up.
        if(retriever != null) {
            retriever.stopRetrieval();
        }
        super.removeNotify();
    }

    // ------------------------------------------------------------------------
    // Updater implementation
    // ------------------------------------------------------------------------
    public void updateStatusText(final String text) {
        statusText = text;
        fireChangeEvent();
    }

    public void clearCancelState() {
        updateCancelState(false);
        retriever = null;
    }
    
    private void updateCancelState(boolean state) {
        final String buttonText = state ? "Cancel Download" : "Download V3 Now...";
        if(SwingUtilities.isEventDispatchThread()) {
            downloadButton.setText(buttonText);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    downloadButton.setText(buttonText);
                }
            });
        }
    }
    // ------------------------------------------------------------------------
    
    private static class DirFilter extends javax.swing.filechooser.FileFilter {
        
        public boolean accept(File f) {
            if(!f.exists() || !f.canRead() || !f.isDirectory() ) {
                return false;
            }else{
                return true;
            }
        }
        
        public String getDescription() {
            return NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_DirType");
        }
        
    }
    
    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JButton downloadButton;
    private javax.swing.JLabel hk2HomeLbl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField hk2HomeTextField;
    // End of variables declaration   
}