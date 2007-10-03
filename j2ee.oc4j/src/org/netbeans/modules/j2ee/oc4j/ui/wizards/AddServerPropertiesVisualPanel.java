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

package org.netbeans.modules.j2ee.oc4j.ui.wizards;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.j2ee.oc4j.ui.wizards.AddServerPropertiesVisualPanel.ServerType;
import org.netbeans.modules.j2ee.oc4j.util.OC4JPluginUtils;
import org.openide.util.NbBundle;

/**
 *
 * @author  pblaha
 */
public class AddServerPropertiesVisualPanel extends javax.swing.JPanel {
    
    private String j2eeLocalHome;
    private final List <ChangeListener> listeners = new ArrayList<ChangeListener>();
    
    /** Creates new form AddServerPropertiesVisualPanel1 */
    public AddServerPropertiesVisualPanel(String j2eeLocalHome) {
        this.j2eeLocalHome = j2eeLocalHome;
        
        setName(NbBundle.getMessage(AddServerLocationVisualPanel.class, "TITLE_Properties"));
        
        initComponents();
        
        DocumentListener changeListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                fireChange();
            }
            
            public void removeUpdate(DocumentEvent e) {
                fireChange();
            }
            
            public void insertUpdate(DocumentEvent e) {
                fireChange();
            }
        };
        
        hostTxt.getDocument().addDocumentListener(changeListener);
        adminPortTxt.getDocument().addDocumentListener(changeListener);
        portTxt.getDocument().addDocumentListener(changeListener);
        passwordTxt.getDocument().addDocumentListener(changeListener);
        
        typeComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                ServerType t = (ServerType) evt.getItem();
                
                if (t.equals(LOCAL)) {
                    hostTxt.setText(LOCALHOST);
                    hostTxt.setEnabled(false);
                    usersComboBox.setEditable(false);
                    webSiteComboBox.setEnabled(true);
                } else {
                    hostTxt.setText("");
                    hostTxt.setEnabled(true);
                    usersComboBox.setEditable(true);
                    webSiteComboBox.setSelectedItem(DEFAULT_WEB_SITE);
                    webSiteComboBox.setEnabled(false);
                    setInitialization(false);
                }
            }
        });
        
        setInitValues();
    }
    
    public void setInitialization(boolean active) {
        initializationPanel.setVisible(active);
    }
    
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    private void fireChange() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        userLbl = new javax.swing.JLabel();
        passwordLbl = new javax.swing.JLabel();
        passwordTxt = new javax.swing.JPasswordField();
        adminPortLbl = new javax.swing.JLabel();
        adminPortTxt = new javax.swing.JTextField();
        portLbl = new javax.swing.JLabel();
        portTxt = new javax.swing.JTextField();
        webSiteLbl = new javax.swing.JLabel();
        webSiteComboBox = new javax.swing.JComboBox();
        usersComboBox = new javax.swing.JComboBox();
        initializationPanel = new javax.swing.JPanel();
        initializeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        typeLbl = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        hostLbl = new javax.swing.JLabel();
        hostTxt = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/oc4j/ui/wizards/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(userLbl, bundle.getString("LBL_USER")); // NOI18N

        passwordLbl.setLabelFor(passwordTxt);
        org.openide.awt.Mnemonics.setLocalizedText(passwordLbl, bundle.getString("LBL")); // NOI18N

        adminPortLbl.setLabelFor(adminPortTxt);
        org.openide.awt.Mnemonics.setLocalizedText(adminPortLbl, bundle.getString("LBL_ADMIN_PORT")); // NOI18N

        portLbl.setLabelFor(portTxt);
        org.openide.awt.Mnemonics.setLocalizedText(portLbl, bundle.getString("LBL_HTTP_PORT")); // NOI18N

        webSiteLbl.setLabelFor(webSiteComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(webSiteLbl, bundle.getString("LBL_WEB_SITE")); // NOI18N

        initializationPanel.setBackground(new java.awt.Color(204, 204, 204));
        initializationPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        initializeButton.setText(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "LBL_InitializationTitle", new Object[] {})); // NOI18N
        initializeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                initializeButtonActionPerformed(evt);
            }
        });

        jLabel1.setText(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "LBL_Initialization", new Object[] {})); // NOI18N

        org.jdesktop.layout.GroupLayout initializationPanelLayout = new org.jdesktop.layout.GroupLayout(initializationPanel);
        initializationPanel.setLayout(initializationPanelLayout);
        initializationPanelLayout.setHorizontalGroup(
            initializationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, initializationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 198, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 24, Short.MAX_VALUE)
                .add(initializeButton)
                .addContainerGap())
        );
        initializationPanelLayout.setVerticalGroup(
            initializationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(initializationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(initializationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(initializeButton)
                    .add(jLabel1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        typeLbl.setDisplayedMnemonic(java.util.ResourceBundle.getBundle("org/netbeans/modules/j2ee/oc4j/ui/wizards/Bundle").getString("LBL_SERVER_TYPE").charAt(0));
        typeLbl.setText(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "LBL_SERVER_TYPE")); // NOI18N

        hostLbl.setText(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "LBL_HOST")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(initializationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(hostLbl)
                            .add(typeLbl))
                        .add(53, 53, 53)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(typeComboBox, 0, 250, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, hostTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(userLbl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(passwordLbl)
                            .add(adminPortLbl)
                            .add(portLbl)
                            .add(webSiteLbl))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(webSiteComboBox, 0, 250, Short.MAX_VALUE)
                            .add(portTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .add(adminPortTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .add(passwordTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .add(usersComboBox, 0, 250, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(typeLbl)
                    .add(typeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hostLbl)
                    .add(hostTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(14, 14, 14)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userLbl)
                    .add(usersComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(passwordLbl)
                    .add(passwordTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(adminPortLbl)
                    .add(adminPortTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(portLbl)
                    .add(portTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(webSiteComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(webSiteLbl))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 92, Short.MAX_VALUE)
                .add(initializationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        passwordTxt.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_PASSWD_TXT")); // NOI18N
        passwordTxt.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_PASSWD_TXT")); // NOI18N
        adminPortTxt.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_ADMIN_PORT_TXT")); // NOI18N
        adminPortTxt.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_ADMIN_PORT_TXT")); // NOI18N
        portTxt.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_HTTP_PORT_TXT")); // NOI18N
        portTxt.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_HTTP_PORT_TXT")); // NOI18N
        webSiteComboBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_WEB_SITES_CMB")); // NOI18N
        webSiteComboBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_WEB_SITES_CMB")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_NAME_PANEL")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "A11Y_DESC_PANEL")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
private void initializeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_initializeButtonActionPerformed
    
    initializeButton.setEnabled(false);
    
    String password = null;
    
    while(password == null || password.equals(ADMIN_USER)) {
        password = OC4JPluginUtils.requestPassword(ADMIN_USER);
    }
    
    if(OC4JPluginUtils.activateUser(j2eeLocalHome, ADMIN_USER, password)) {
        initializeButton.setEnabled(true);
        setInitialization(false);
        passwordTxt.setText(password);
        fireChange();
    }
    
    initializeButton.setEnabled(true);
    fireChange();
}//GEN-LAST:event_initializeButtonActionPerformed

public ServerType getType() {
    return (ServerType) typeComboBox.getSelectedItem();
}

public String getHost() {
    return hostTxt.getText().trim();
}

public String getAdminPort() {
    return adminPortTxt.getText().trim();
}

public String getPassword() {
    return new String(passwordTxt.getPassword());
}

public String getUser() {
    return (String) usersComboBox.getSelectedItem();
}

public String getPort() {
    return portTxt.getText().trim();
}

public String getWebSite() {
    return (String)webSiteComboBox.getSelectedItem();
}

private void setInitValues() {
    usersComboBox.removeAllItems();
    
    portTxt.setText(Integer.toString(OC4JPluginUtils.getHttpPort(j2eeLocalHome, "default")));
    adminPortTxt.setText(Integer.toString(OC4JPluginUtils.getAdminPort(j2eeLocalHome)));
    
    for (String item : OC4JPluginUtils.getWebSites(j2eeLocalHome))
        webSiteComboBox.addItem(item);
    
    for (String item : OC4JPluginUtils.getUsers(j2eeLocalHome)) {
        usersComboBox.addItem(item);
    }
    
    typeComboBox.addItem(LOCAL);
    typeComboBox.addItem(REMOTE);
    
    setInitialization(false);
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adminPortLbl;
    private javax.swing.JTextField adminPortTxt;
    private javax.swing.JLabel hostLbl;
    private javax.swing.JTextField hostTxt;
    private javax.swing.JPanel initializationPanel;
    private javax.swing.JButton initializeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel passwordLbl;
    private javax.swing.JPasswordField passwordTxt;
    private javax.swing.JLabel portLbl;
    private javax.swing.JTextField portTxt;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLbl;
    private javax.swing.JLabel userLbl;
    private javax.swing.JComboBox usersComboBox;
    private javax.swing.JComboBox webSiteComboBox;
    private javax.swing.JLabel webSiteLbl;
    // End of variables declaration//GEN-END:variables
    
    private final static String LOCALHOST = "localhost";
    private final static String ADMIN_USER = "oc4jadmin";
    private final static String DEFAULT_WEB_SITE = "default";
    
    protected final static ServerType LOCAL = new ServerType("Local Server");
    protected final static ServerType REMOTE = new ServerType("Remote Server");
    
    protected final static class ServerType {
        
        private String description;
        
        public ServerType(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
}