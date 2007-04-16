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
        updateMessageText("");
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
    public void updateMessageText(final String msg) {
        if(SwingUtilities.isEventDispatchThread()) {
            downloadStatusLabel.setText(msg);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    downloadStatusLabel.setText(msg);
                }
            });
        }
    }
    
    public void updateStatusText(final String status) {
        statusText = status;
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
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        hk2HomeLabel = new javax.swing.JLabel();
        hk2HomeTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        downloadButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        downloadStatusLabel = new javax.swing.JLabel();

        hk2HomeLabel.setText(org.openide.util.NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_InstallLocation")); // NOI18N

        browseButton.setText(org.openide.util.NbBundle.getMessage(AddServerLocationVisualPanel.class, "LBL_BrowseButton")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        downloadButton.setText("[download/cancel]"); // NOI18N
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        downloadStatusLabel.setText("[download status]"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(downloadStatusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(downloadStatusLabel)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(hk2HomeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hk2HomeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(browseButton))
            .add(layout.createSequentialGroup()
                .add(downloadButton)
                .addContainerGap())
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hk2HomeLabel)
                    .add(browseButton)
                    .add(hk2HomeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(downloadButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(223, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        if(retriever == null) {
            retriever = new Retriever(new File(hk2HomeTextField.getText()), 
                    V3_DOWNLOAD_URL, this);
            new Thread(retriever).start();
            updateCancelState(true);
        } else {
            retriever.stopRetrieval();
        }
}//GEN-LAST:event_downloadButtonActionPerformed

private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String newLoc = browseHomeLocation();
        if(newLoc != null && newLoc.length() > 0) {
            hk2HomeTextField.setText(newLoc);
        }
}//GEN-LAST:event_browseButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton downloadButton;
    private javax.swing.JLabel downloadStatusLabel;
    private javax.swing.JLabel hk2HomeLabel;
    private javax.swing.JTextField hk2HomeTextField;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
}
