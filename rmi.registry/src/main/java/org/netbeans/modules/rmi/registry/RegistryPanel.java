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

package org.netbeans.modules.rmi.registry;

/**
 *
 * @author
 * @version
 */
public class RegistryPanel extends javax.swing.JPanel {

    /** Serial version UID. */
    static final long serialVersionUID = -4885634530381950563L;

    /** Initializes the Form. */
    public RegistryPanel() {
        initComponents ();
        this.getAccessibleContext().setAccessibleDescription(getString("AD_RegistryPanel"));
        jTextField3.setText("" + java.rmi.registry.Registry.REGISTRY_PORT);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 0, 11)));
        setMinimumSize(new java.awt.Dimension(320, 128));
        setPreferredSize(new java.awt.Dimension(320, 128));
        jLabel1.setLabelFor(jTextField2);
        jLabel1.setText(getString("LAB_Host"));
        jLabel1.setDisplayedMnemonic(getMnemonic("LAB_RegistryHost")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 11);
        add(jLabel1, gridBagConstraints);

        jTextField2.setColumns(20);
        jTextField2.setText("localhost");
        jTextField2.getAccessibleContext().setAccessibleDescription(getString("AD_LAB_Host"));
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jTextField2, gridBagConstraints);

        jLabel2.setLabelFor(jTextField3);
        jLabel2.setText(getString("LAB_Port"));
        jLabel2.setDisplayedMnemonic(getMnemonic("LAB_RegistryPort")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 11);
        add(jLabel2, gridBagConstraints);

        jTextField3.setColumns(5);
        jTextField3.setAlignmentX(0.0F);
        jTextField3.setPreferredSize(new java.awt.Dimension(55, 20));
        jTextField3.getAccessibleContext().setAccessibleDescription(getString("AD_LAB_Port"));
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(jTextField3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void jTextField3ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField2ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables

    public String getPort() {
        return jTextField3.getText();
    }

    public String getHost() {
        return jTextField2.getText();
    }

    public boolean isCreateRequired() {
        return false;
    }
    
    /** Localization. */
    private static String getString(String key) {
        return org.openide.util.NbBundle.getBundle(RegistryPanel.class).getString(key);
    }

    private static final String mnemonic_suffix = ".mnemonic"; // NOI18N
    
    private static char getMnemonic(java.lang.String key) {
       return org.openide.util.NbBundle.getBundle (RegistryPanel.class).getString(key + mnemonic_suffix).charAt(0);
    }
    
}
