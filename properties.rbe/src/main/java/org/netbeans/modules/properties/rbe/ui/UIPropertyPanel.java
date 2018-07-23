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

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.netbeans.modules.properties.rbe.model.Bundle;
import org.netbeans.modules.properties.rbe.model.LocaleProperty;
import org.openide.util.NbBundle;

/**
 *
 * @author  Denis Stepanov <denis.stepanov at gmail.com>
 */
public class UIPropertyPanel extends javax.swing.JPanel {

    protected LocaleProperty localeProperty;

    /** Creates new form Property panel */
    public UIPropertyPanel(final LocaleProperty localeProperty) {
        this.localeProperty = localeProperty;

        initComponents();
        if (Bundle.DEFAULT_LOCALE.equals(localeProperty.getLocale())) {
            titleLabel.setText(NbBundle.getMessage(ResourceBundleEditorComponent.class, "DefaultLocale"));
        } else {
            titleLabel.setText(getLocaleTitle(localeProperty.getLocale()));
        }
        if (localeProperty.getProperty().isExists()) {
            textArea.setText(localeProperty.getValue());
            textArea.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    localeProperty.setValue(textArea.getText());
                }
            });

        } else {
            textArea.setEnabled(false);
        }

        toolBar.setLayout(new CardLayout());
        updateCommentStatus(localeProperty);
    }

    protected void updateCommentStatus(final LocaleProperty value) {
        if (value.getComment() != null && value.getComment().length() > 0) {
            commentButton.setFont(new Font(commentButton.getFont().getName(),
                    commentButton.getFont().getStyle() | Font.BOLD, commentButton.getFont().getSize()));
        } else {
            commentButton.setFont(new Font(commentButton.getFont().getName(),
                    commentButton.getFont().getStyle() & ~Font.BOLD, commentButton.getFont().getSize()));
        }
    }

    protected String getLocaleTitle(Locale locale) {
        String title = String.format("%s (%s)", locale.getDisplayLanguage(), locale.getLanguage());
        if (locale.getDisplayCountry().length() > 0) {
            title += String.format(" - %s (%s)", locale.getDisplayCountry(), locale.getCountry());
        }
        if (locale.getDisplayVariant().length() > 0) {
            title += String.format(" - %s (%s)", locale.getDisplayVariant(), locale.getVariant());
        }
        return title;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new JToolBar();
        jPanel1 = new JPanel();
        titleLabel = new JLabel();
        commentButton = new JButton();
        jScrollPane1 = new JScrollPane();
        textArea = new JTextArea();

        toolBar.setFloatable(false);
        toolBar.setOrientation(1);
        toolBar.setOpaque(false);

        jPanel1.setOpaque(false);

        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setText(NbBundle.getMessage(UIPropertyPanel.class, "UIPropertyPanel.titleLabel.text")); // NOI18N
        titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        commentButton.setIcon(new ImageIcon(getClass().getResource("/org/netbeans/modules/properties/rbe/resources/comment.png"))); // NOI18N
        commentButton.setText(NbBundle.getMessage(UIPropertyPanel.class, "UIPropertyPanel.commentButton.text_1")); // NOI18N
        commentButton.setHorizontalTextPosition(SwingConstants.CENTER);
        commentButton.setMaximumSize(new Dimension(25, 25));
        commentButton.setMinimumSize(new Dimension(25, 25));
        commentButton.setPreferredSize(new Dimension(25, 25));
        commentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                commentButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(titleLabel, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(commentButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))

        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(GroupLayout.BASELINE)
                .add(commentButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .add(titleLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))

        );

        toolBar.add(jPanel1);

        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(textArea);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(toolBar, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
            .add(jScrollPane1, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)

        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(toolBar, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(jScrollPane1, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))

        );
    }// </editor-fold>//GEN-END:initComponents

    private void commentButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_commentButtonActionPerformed
        UICommentWindow commentWindow = new UICommentWindow(JOptionPane.getFrameForComponent(this), localeProperty);
        commentWindow.setVisible(true);
        updateCommentStatus(localeProperty);
    }//GEN-LAST:event_commentButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton commentButton;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JTextArea textArea;
    private JLabel titleLabel;
    private JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
