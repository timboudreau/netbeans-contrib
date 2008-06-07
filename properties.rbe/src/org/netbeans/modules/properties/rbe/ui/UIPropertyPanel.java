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
 * Contributor(s): Denis Stepanov
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.properties.rbe.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;
import org.netbeans.modules.properties.rbe.model.Bundle;
import org.netbeans.modules.properties.rbe.model.LocaleProperty;
import org.openide.util.NbBundle;

/**
 *
 * @author  Denis Stepanov <denis.stepanov at gmail.com>
 */
public class UIPropertyPanel extends javax.swing.JPanel {

    /** Creates new form Property panel */
    public UIPropertyPanel(Locale locale, final LocaleProperty value) {
        initComponents();
        if (Bundle.DEFAULT_LOCALE.equals(locale)) {
            titleLabel.setText(NbBundle.getMessage(ResourceBundleEditorComponent.class, "DefaultLocale"));
        } else {
            titleLabel.setText(getLocaleTitle(locale));
        }
        if (value != null && value.isCreated()) {
            textArea.setText(value.getValue());
            //Using focus listener to update item value after change.
            //First attempt was to do it with document listener and update it in runtime,
            //but becouse of some synchronization issues, this didn't worked well
            textArea.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    value.setValue(textArea.getText());
                }
            });

        } else {
            textArea.setEnabled(false);
        }

    }

    protected String getLocaleTitle(Locale locale) {
        return String.format("%s (%s)%s",
            locale.getDisplayLanguage(),
            locale.getLanguage(),
            locale.getDisplayCountry().length() > 0 ? " - " + locale.getDisplayCountry() : "");
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        jPanel1 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        toolBar.setFloatable(false);
        toolBar.setOrientation(1);

        jPanel1.setOpaque(false);

        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(org.openide.util.NbBundle.getMessage(UIPropertyPanel.class, "UIPropertyPanel.titleLabel.text")); // NOI18N
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(titleLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        toolBar.add(jPanel1);

        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(textArea);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(toolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textArea;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
