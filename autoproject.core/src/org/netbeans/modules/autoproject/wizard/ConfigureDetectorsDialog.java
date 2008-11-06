/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.wizard;

import org.netbeans.modules.autoproject.core.AutomaticProjectFactory;

public class ConfigureDetectorsDialog extends javax.swing.JPanel {

    public ConfigureDetectorsDialog() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        noOption = new javax.swing.JRadioButton();
        yesOption = new javax.swing.JRadioButton();
        explanation = new javax.swing.JLabel();

        buttonGroup.add(noOption);
        noOption.setSelected(!AutomaticProjectFactory.isAutomaticDetectionMode());
        org.openide.awt.Mnemonics.setLocalizedText(noOption, org.openide.util.NbBundle.getMessage(ConfigureDetectorsDialog.class, "ConfigureDetectorsDialog.noOption.text")); // NOI18N
        noOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noOptionActionPerformed(evt);
            }
        });

        buttonGroup.add(yesOption);
        yesOption.setSelected(AutomaticProjectFactory.isAutomaticDetectionMode());
        org.openide.awt.Mnemonics.setLocalizedText(yesOption, org.openide.util.NbBundle.getMessage(ConfigureDetectorsDialog.class, "ConfigureDetectorsDialog.yesOption.text")); // NOI18N
        yesOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yesOptionActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(explanation, org.openide.util.NbBundle.getMessage(ConfigureDetectorsDialog.class, "ConfigureDetectorsDialog.explanation.text")); // NOI18N
        explanation.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(explanation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                    .add(noOption)
                    .add(yesOption))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(noOption)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(yesOption)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(explanation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void noOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noOptionActionPerformed
        AutomaticProjectFactory.setAutomaticDetectionMode(false);
    }//GEN-LAST:event_noOptionActionPerformed

    private void yesOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yesOptionActionPerformed
        AutomaticProjectFactory.setAutomaticDetectionMode(true);
    }//GEN-LAST:event_yesOptionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JLabel explanation;
    private javax.swing.JRadioButton noOption;
    private javax.swing.JRadioButton yesOption;
    // End of variables declaration//GEN-END:variables

}
