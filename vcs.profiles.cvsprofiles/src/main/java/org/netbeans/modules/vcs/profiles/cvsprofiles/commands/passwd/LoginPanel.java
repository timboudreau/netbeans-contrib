/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */


package org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * LoginPanel.java
 * Created on January 13, 2004, 10:50 AM
 *
 * @author  Richard Gregor
 */

public class LoginPanel extends javax.swing.JPanel {
    public final Object STATUS_CONNECTING = new Integer(0);
    public final Object STATUS_FAILED = new Integer(1);
    
    private static ArrayList lastSuccessfullLoggings = new ArrayList();
    
    private String connectStr = "";
    private int port = 0;
    private PasswdEntry entry = null;
    private boolean loggedIn = false;
    private boolean offline = false;
    private String password = null;
    private Thread loginThread = null;
    private VcsFileSystem fileSystem = null;
    private static javax.swing.JButton offlineButton;
    private static javax.swing.JButton loginButton;
    private static Object[] options = new Object[2];
    private static BtnListener btnListener;
    private static Dialog dialog;    

    /** Creates new form LoginPanel */
    public LoginPanel() {
        initComponents(); 
        loginButton = new JButton(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.loginButton.text"));
        loginButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_LoginDialog.loginButton.textA11yDesc"));
        offlineButton = new JButton(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.offlineButton.text"));
        offlineButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_LoginDialog.offlineButton.textA11yDesc"));
        options[0] = loginButton;
        options[1] = offlineButton;
	btnListener = new BtnListener();
        initAccessibility();
        VcsUtilities.removeEnterFromKeymap(passwordField);        
        
    }
    
    private void initAccessibility() {
        passwordLabel.setDisplayedMnemonic(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.passwordLabel.text_Mnemonic").charAt(0));  // NOI18N
        loginButton.setMnemonic(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.loginButton.text_Mnemonic").charAt(0));  // NOI18N
        offlineButton.setMnemonic(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.offlineButton.text_Mnemonic").charAt(0));  // NOI18N
        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_LoginDialogA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_LoginDialogA11yDesc"));  // NOI18N
        loginLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_LoginDialog.loginLabel.textA11yDesc"));  // NOI18N
        passwordLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_LoginDialog.passwordLabel.textA11yDesc"));  // NOI18N
        passwordField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_passwordFieldA11yName"));  // NOI18N
        passwordField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_LoginDialog.passwordLabel.textA11yDesc"));  // NOI18N
        statusLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("ACS_LoginDialog.statusA11yDesc"));  // NOI18N
        passwordLabel.setLabelFor(passwordField);
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        loginLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        statusLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        loginLabel.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/passwd/Bundle").getString("LoginDialog.loginLabel.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 13, 0);
        add(loginLabel, gridBagConstraints);

        passwordLabel.setText(NbBundle.getBundle("org/netbeans/modules/vcs/profiles/cvsprofiles/commands/passwd/Bundle").getString("LoginDialog.passwordLabel.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 8);
        add(passwordLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        add(passwordField, gridBagConstraints);

        statusLabel.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 6);
        add(statusLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    public Dialog createDialog(VcsFileSystem fileSystem) {              
        this.fileSystem = fileSystem;
        DialogDescriptor dd = new DialogDescriptor(this,org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.title"),
                              true,options,options[0],DialogDescriptor.DEFAULT_ALIGN,null,btnListener);
        dialog = DialogDisplayer.getDefault().createDialog(dd);       
        return dialog;
    }
    
    public void setConnectString(String connectStr) {
        this.connectStr = connectStr;
    }
    
    public void setPort(int port) {
        this.port = port;
    }

    public void setPserverName(String pserverName) {
        loginLabel.setText(java.text.MessageFormat.format(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.loginLabel.text"), new Object[] { pserverName }));        
    }
    
    public void setStatus(Object status) {
        setStatus(status, null);
    }
    
    public void setStatus(Object status, String message) {
        if (status.equals(STATUS_FAILED)) {
            statusLabel.setText(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.status.failed"));
            if (message != null) statusLabel.setToolTipText(message);
        } else if (status.equals(STATUS_CONNECTING)) {
            statusLabel.setText(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.status.connecting"));
        } else if (status instanceof String) {
            statusLabel.setText((String) status);
        }
    }
    
   
   public boolean isLoggedIn() {
       return loggedIn;
   }
   
   public boolean isOffline() {
       return offline;
   }
   
   public String getPassword() {
       return password;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
    
    class BtnListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){            
            if(ev.getSource() == loginButton){
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        CVSPasswd pasFile = new CVSPasswd((String)null);
                        pasFile.loadPassFile();
                        password = new String(passwordField.getPassword());
                        //entry = new PasswdEntry();
                        try {
                            pasFile.remove(connectStr, port);
                        } catch (IllegalArgumentException iaex) {
                            setStatus(iaex.getLocalizedMessage());
                            return;
                        }
                        pasFile.add(connectStr, port, password); //CVSPasswd.scramble(password));
                        pasFile.savePassFile();
                        //boolean setRight = entry.setEntry(connectStr + " " + CVSPasswd.scramble(password));
                        //if (!setRight) {D("wrongly set entry.");}
                        checkLogin();
                    }});
            }
            if(ev.getSource() == offlineButton){
                loggedIn = false;
                if (loginThread != null) {
                    if (loginThread.isAlive()) {
                        loginThread.interrupt();
                    }
                }
                offline = true;
                dialog.setVisible(false);
            }
        }
        
    }
    
    private void checkLogin() {
        lastSuccessfullLoggings.remove(connectStr);
        passwordField.setEnabled(false);
        loginButton.setEnabled(false);
        setStatus(STATUS_CONNECTING, null);
        StringBuffer message = new StringBuffer();
        try {
            CVSPasswd pasFile = new CVSPasswd((String)null);
            pasFile.loadPassFile();
            password = new String(passwordField.getPassword());
            pasFile.remove(connectStr, port);
            pasFile.add(connectStr, port, password);
            loggedIn = CVSPasswd.checkLogin(fileSystem, message);
        } catch (java.net.UnknownHostException exc) {
            setStatus(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.unknownHost"));
            return;
        } catch (java.io.IOException exc) {
            setStatus(org.openide.util.NbBundle.getBundle(LoginPanel.class).getString("LoginDialog.connectionIOError"));
            return;
        } catch (IllegalArgumentException iaex) {
            setStatus(iaex.getLocalizedMessage());
            return ;
        } finally {
            passwordField.setEnabled(true);
            loginButton.setEnabled(true);
        }
        if (!loggedIn) {
            setStatus(STATUS_FAILED, message.toString());
        } else {
            lastSuccessfullLoggings.add(connectStr);
            dialog.setVisible(false);
        }
    }
}
