/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70AddServerChoiceVisualPanel.java 
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.openide.util.NbBundle;
import org.openide.WizardDescriptor;
/**
 *
 * @author  Administrator
 */
public class WS70AddServerChoiceVisualPanel extends javax.swing.JPanel {    
    private String installDirName;    
    private String hostName;
    private String portNumber;
    private String userName;
    private String password;
    private final List listeners = new ArrayList();
    /**
     * Creates new form WS70AddServerChoiceVisualPanel
     */
    public WS70AddServerChoiceVisualPanel() {
        initComponents();
    }
    public String getAdminUserName(){
        return jUserNameTxt.getText().trim();
    }
    public String getAdminPassword(){
        char[] password = jPasswordTxt.getPassword();
        return String.valueOf(password);
    }
    
    public String getAdminHost(){
        return jAdminHostTxt.getText().trim();        
    }
    public String getAdminPort(){
        return jAdminPortTxt.getText().trim();        
    }
    

    public String getServerLocation(){
        return jLocationTxt.getText().trim();
    }
    
    public boolean isValid(WizardDescriptor wizard){        
        if(!validateDirctory()){
            jLocationTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
            return false;
        }        
        if(!validateAdminHost()){
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_HOSTNAME"));
            jAdminHostTxt.setFocusable(true);
            return false;
        }
        if(!validateAdminPort()){
            jUserNameTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_VALID_PORT"));
            return false;
        }
        if(!validateUserName()){
            jUserNameTxt.setFocusable(true);
            wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE
                    , NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_ENTER_USERNAME"));
            return false;
        }
        wizard.putProperty(WS70ServerUIWizardIterator.PROP_ERROR_MESSAGE, null);
        return true;
    }
    private boolean validateAdminHost(){
        if(hostName==null || hostName.length()<=0){            
            return false;
        }
        return true;
    }    
    private boolean validateAdminPort(){
        if(portNumber==null || portNumber.length()<=0){
            return false;
        }
        int port;
        try{
            port = Integer.parseInt(portNumber);
        }catch(NumberFormatException e){
            port = -1;
        }
        if(port<=0 || port>65535){            
            return false;
        }
        return true;
    }
    private boolean validateUserName(){
        if(userName==null || userName.length()<=0){
            return false;
        }
        return true;
    }        
    private boolean validateDirctory() {
        if(installDirName==null){
            return false;
        }
        if(installDirName.equals("")){
            return false;
        }
        
        if(!isValidServer(new File(installDirName))){
            Util.showError(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
            jLocationTxt.requestFocusInWindow();
            return false;
        }
        return true;
    }
    
    private boolean isValidServer(File dir){        
        FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().equals("admin-server"); //NO I18N
            }
        };
        File[] adminFolders = dir.listFiles( filter );
        if ( adminFolders == null || adminFolders.length == 0 ){
            return false;
        }
        
        File[] configFolders = adminFolders[0].listFiles( new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.getName().equals("config");//NO I18N
            }
        } );

        if ( configFolders == null || configFolders.length == 0 ){
            return false;
        }
        
        File[] serverFiles = configFolders[0].listFiles( new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().equals("server.xml");//NO I18N
            }
        } );
        if ( serverFiles == null || serverFiles.length == 0 ){
            return false;
        }        
        return true;
    }
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public String getName(){
        return NbBundle.getMessage(WS70AddServerChoiceVisualPanel.class, "LBL_AddServerWizardTitle");
    }
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }    
    private void fireChange() {
        ChangeEvent event = new ChangeEvent(this);
        ArrayList tempList;

        synchronized(listeners) {
            tempList = new ArrayList(listeners);
        }

        Iterator iter = tempList.iterator();
        while (iter.hasNext())
            ((ChangeListener)iter.next()).stateChanged(event);
    }    

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jServerInstructionsLbl = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jDirectoryLbl = new javax.swing.JLabel();
        jLocationTxt = new javax.swing.JTextField();
        jBrowseBtn = new javax.swing.JButton();
        jAdminHostLbl = new javax.swing.JLabel();
        jAdminHostTxt = new javax.swing.JTextField();
        jAdminPortLbl = new javax.swing.JLabel();
        jAdminPortTxt = new javax.swing.JTextField();
        jUserNameLbl = new javax.swing.JLabel();
        jUserNameTxt = new javax.swing.JTextField();
        jPasswordLbl = new javax.swing.JLabel();
        jPasswordTxt = new javax.swing.JPasswordField();
        jAdminInstructionsLbl = new javax.swing.JLabel();

        jServerInstructionsLbl.setText(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("LBL_AddServerVisualPanelTitle"));
        jServerInstructionsLbl.getAccessibleContext().setAccessibleName(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_NAME_AddServerVisualPanelTitle"));
        jServerInstructionsLbl.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_DESC_AddServerVisualPanelTitle"));

        jDirectoryLbl.setDisplayedMnemonic(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_Inst_Dir_Mnem").charAt(0));
        jDirectoryLbl.setLabelFor(jLocationTxt);
        jDirectoryLbl.setText(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("LBL_AddServerVisualPanel_Directory"));
        jDirectoryLbl.getAccessibleContext().setAccessibleName(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_NAME_AddServerVisualPanel_Directory"));
        jDirectoryLbl.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("A11Y_DESC_AddServerVisualPanel_Directory"));
        jDirectoryLbl.getAccessibleContext().setAccessibleParent(this);

        jLocationTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jLocationTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jLocationTxtFocusLost(evt);
            }
        });

        jBrowseBtn.setMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Browse_Mnem").charAt(0));
        jBrowseBtn.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanel_Browse"));
        jBrowseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowseBtnActionPerformed(evt);
            }
        });

        jBrowseBtn.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanel_Browse"));
        jBrowseBtn.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanel_Browse"));
        jBrowseBtn.getAccessibleContext().setAccessibleParent(this);

        jAdminHostLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Host_Mnem").charAt(0));
        jAdminHostLbl.setLabelFor(jAdminHostTxt);
        jAdminHostLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelHost"));
        jAdminHostLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelHost"));
        jAdminHostLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelHost"));
        jAdminHostLbl.getAccessibleContext().setAccessibleParent(this);

        jAdminHostTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jAdminHostTxtCaretUpdate(evt);
            }
        });

        jAdminPortLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Port_Mnem").charAt(0));
        jAdminPortLbl.setLabelFor(jAdminPortTxt);
        jAdminPortLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelPort"));
        jAdminPortLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelPort"));
        jAdminPortLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelPort"));
        jAdminPortLbl.getAccessibleContext().setAccessibleParent(this);

        jAdminPortTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jAdminPortTxtCaretUpdate(evt);
            }
        });

        jUserNameLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_UserName_Mnem").charAt(0));
        jUserNameLbl.setLabelFor(jUserNameTxt);
        jUserNameLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelUserName"));
        jUserNameLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelUserName"));
        jUserNameLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelUserName"));
        jUserNameLbl.getAccessibleContext().setAccessibleParent(this);

        jUserNameTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jUserNameTxtCaretUpdate(evt);
            }
        });

        jPasswordLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_Password_Mnem").charAt(0));
        jPasswordLbl.setLabelFor(jPasswordTxt);
        jPasswordLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelPassword"));
        jPasswordLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelPassword"));
        jPasswordLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelPassword"));
        jPasswordLbl.getAccessibleContext().setAccessibleParent(this);

        jPasswordTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jPasswordTxtFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPasswordTxtFocusLost(evt);
            }
        });

        jAdminInstructionsLbl.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("LBL_AddServerVisualPanelAdmin"));
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleName(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_NAME_AddServerVisualPanelAdmin"));
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleDescription(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/sun/ws7/ui/Bundle").getString("A11Y_DESC_AddServerVisualPanelAdmin"));
        jAdminInstructionsLbl.getAccessibleContext().setAccessibleParent(this);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(23, 23, 23)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jServerInstructionsLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 350, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                            .add(jDirectoryLbl)
                            .add(16, 16, 16)
                            .add(jLocationTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 176, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jBrowseBtn))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jAdminHostLbl)
                                .add(jAdminPortLbl)
                                .add(jUserNameLbl)
                                .add(jPasswordLbl))
                            .add(31, 31, 31)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jAdminPortTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                .add(jAdminHostTxt)
                                .add(jUserNameTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                                .add(jPasswordTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))))
                    .add(jAdminInstructionsLbl))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(25, 25, 25)
                .add(jServerInstructionsLbl)
                .add(16, 16, 16)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLocationTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jDirectoryLbl)
                    .add(jBrowseBtn))
                .add(21, 21, 21)
                .add(jAdminInstructionsLbl)
                .add(26, 26, 26)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jAdminHostLbl)
                    .add(jAdminHostTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jAdminPortLbl)
                    .add(jAdminPortTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jUserNameTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jUserNameLbl))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jPasswordTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPasswordLbl))
                .addContainerGap(17, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLocationTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLocationTxtFocusLost
        String dirName = jLocationTxt.getText().trim();
        if (dirName.equals(this.installDirName) ) {
            return;
        }
        installDirName = dirName;        
        fireChange();        
    }//GEN-LAST:event_jLocationTxtFocusLost

    private void jLocationTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLocationTxtFocusGained
        String dirName = jLocationTxt.getText().trim();
        if (dirName.equals(this.installDirName) ) {
            return;
        }
        installDirName = dirName;        
        fireChange();        
    }//GEN-LAST:event_jLocationTxtFocusGained

    private void jAdminHostTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jAdminHostTxtCaretUpdate
        String host = jAdminHostTxt.getText().trim();
        if (host.equals(this.hostName) ) {
            return;
        }
        hostName = host;
        fireChange();          
    }//GEN-LAST:event_jAdminHostTxtCaretUpdate

    private void jAdminPortTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jAdminPortTxtCaretUpdate
       String port = jAdminPortTxt.getText().trim();
        if (port.equals(this.portNumber)) {
            return;
        }
        portNumber = port;
        fireChange();            
    }//GEN-LAST:event_jAdminPortTxtCaretUpdate

    private void jUserNameTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jUserNameTxtCaretUpdate
        String uname = jUserNameTxt.getText().trim();
        if (uname.equals(this.userName) ) {
            return;
        }
        userName = uname;
        fireChange();
    }//GEN-LAST:event_jUserNameTxtCaretUpdate

    private void jPasswordTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordTxtFocusLost
        String passwd = String.copyValueOf(jPasswordTxt.getPassword());
        if (passwd.equals(this.password) ) {
            return;
        }
        password = passwd;
        fireChange();        
    }//GEN-LAST:event_jPasswordTxtFocusLost

    private void jPasswordTxtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPasswordTxtFocusGained
        String passwd = String.copyValueOf(jPasswordTxt.getPassword());
        if (passwd.equals(this.password) ) {
            return;
        }
        password = passwd;
        fireChange();        
    }//GEN-LAST:event_jPasswordTxtFocusGained

    private void jBrowseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowseBtnActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File installDir = null;
        if (installDir != null )
            chooser.setSelectedFile(installDir);
        boolean repeatchooser = true;
        while ( repeatchooser ) {
            repeatchooser = false;
            if ( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
                installDir = chooser.getSelectedFile();
                if (isValidServer(installDir)){
                    jLocationTxt.setText(installDir.getAbsolutePath());
                    installDirName = installDir.getAbsolutePath();
                } else {
                    Util.showError(NbBundle.getBundle(WS70AddServerChoiceVisualPanel.class).getString("MSG_INVALID_SERVER_DIRECTORY"));
                    jLocationTxt.setText( "" );  // NOI18N
                    installDir = null;
                    repeatchooser = true;                    
                }
            }
        }
    }//GEN-LAST:event_jBrowseBtnActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jAdminHostLbl;
    private javax.swing.JTextField jAdminHostTxt;
    private javax.swing.JLabel jAdminInstructionsLbl;
    private javax.swing.JLabel jAdminPortLbl;
    private javax.swing.JTextField jAdminPortTxt;
    private javax.swing.JButton jBrowseBtn;
    private javax.swing.JLabel jDirectoryLbl;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jLocationTxt;
    private javax.swing.JLabel jPasswordLbl;
    private javax.swing.JPasswordField jPasswordTxt;
    private javax.swing.JLabel jServerInstructionsLbl;
    private javax.swing.JLabel jUserNameLbl;
    private javax.swing.JTextField jUserNameTxt;
    // End of variables declaration//GEN-END:variables
    
}
