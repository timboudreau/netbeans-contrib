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
package org.netbeans.modules.searchandreplace;

import java.awt.Component;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.searchandreplace.model.SearchDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 *
 * @author  Timothy Boudreau
 */
class SearchInput extends javax.swing.JPanel implements DocumentListener {
    private final boolean enableSearchIgnored;
    private static SearchDescriptor lastSearchDescriptor =
            new SearchDescriptor ("", null, false, true, false,
            true, false);

    /** Creates new form SearchInput */
    public SearchInput(boolean enableSearchIgnored, ChangeListener cl) {
        initComponents();
        Component[] c = getComponents();
        for (int i=0; i < c.length; i++) {
            setup (c[i]);
        }
        this.enableSearchIgnored = enableSearchIgnored;
        init();
        this.change = cl;
        searchFor.getDocument().addDocumentListener(this);
    }

    private void setup (Component jc) {
        if (jc instanceof JLabel) {
            JLabel jl = (JLabel) jc;
            jl.setDisplayedMnemonicIndex(Mnemonics.findMnemonicAmpersand(
                    jl.getText()));
            Mnemonics.setLocalizedText(jl, jl.getText());
        } else if (jc instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) jc;
            b.setDisplayedMnemonicIndex(Mnemonics.findMnemonicAmpersand(
                    b.getText()));
            Mnemonics.setLocalizedText(b, b.getText());
        }
    }

    private void init() {
        //To control initialization settings on first dlg show,
        //modify the initial parameters of lastSearchDescriptor above

        shouldReplace.setSelected (lastSearchDescriptor.isShouldReplace());
        searchFor.setText (lastSearchDescriptor.getSearchText());
        replaceWith.setText (lastSearchDescriptor.getReplaceText());
        replaceWith.setEnabled (lastSearchDescriptor.isShouldReplace());
        caseBox.setSelected (lastSearchDescriptor.isCaseSensitive());
        binaryBox.setSelected (lastSearchDescriptor.isIncludeBinaryFiles());
        subfolderBox.setSelected (lastSearchDescriptor.isIncludeSubfolders());
        includeIgnoredBox.setSelected(enableSearchIgnored &&
                lastSearchDescriptor.isIncludeIgnored());
        includeIgnoredBox.setEnabled(enableSearchIgnored);
    }

    public SearchDescriptor getSearchDescriptor() {
        lastSearchDescriptor = new SearchDescriptor (
                searchFor.getText(),
                replaceWith.getText(),
                shouldReplace.isSelected(),
                caseBox.isSelected(),
                binaryBox.isSelected(),
                subfolderBox.isSelected(),
                includeIgnoredBox.isSelected());
        return lastSearchDescriptor;
    }

    public boolean hasSearchText() {
        return !"".equals(searchFor.getText());
    }

    public void removeNotify() {
        super.removeNotify();
        lastSearchDescriptor = getSearchDescriptor();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        searchFor = new javax.swing.JTextField();
        replaceWith = new javax.swing.JTextField();
        includeIgnoredBox = new javax.swing.JCheckBox();
        shouldReplace = new javax.swing.JCheckBox();
        caseBox = new javax.swing.JCheckBox();
        subfolderBox = new javax.swing.JCheckBox();
        binaryBox = new javax.swing.JCheckBox();

        jLabel1.setLabelFor(searchFor);
        jLabel1.setText("&Search For");

        searchFor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchForFocusGained(evt);
            }
        });

        replaceWith.setEnabled(false);
        replaceWith.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                replaceWithFocusGained(evt);
            }
        });

        includeIgnoredBox.setText("&Include files ignored by VCS");
        includeIgnoredBox.setToolTipText(NbBundle.getMessage(SearchInput.class, "TIP_IncludeVCS"));
        includeIgnoredBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        includeIgnoredBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        shouldReplace.setText("&Replace With");
        shouldReplace.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        shouldReplace.setMargin(new java.awt.Insets(0, 0, 0, 0));
        shouldReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shouldReplaceActionPerformed(evt);
            }
        });

        caseBox.setText(NbBundle.getMessage(SearchInput.class, "LBL_CASE"));
        caseBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        caseBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        subfolderBox.setSelected(true);
        subfolderBox.setText(NbBundle.getMessage(SearchInput.class, "LBL_Recurse"));
        subfolderBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        subfolderBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        binaryBox.setText(NbBundle.getMessage(SearchInput.class, "LBL_BINARY"));
        binaryBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        binaryBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(shouldReplace))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(searchFor, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .add(replaceWith, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(caseBox)
                            .add(binaryBox))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 46, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(subfolderBox)
                            .add(includeIgnoredBox))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(searchFor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(replaceWith, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(shouldReplace))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(includeIgnoredBox)
                    .add(caseBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(subfolderBox)
                    .add(binaryBox))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void replaceWithFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_replaceWithFocusGained
        searchForFocusGained (evt);
    }//GEN-LAST:event_replaceWithFocusGained

    private void searchForFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchForFocusGained
        JTextField jtf = (JTextField) evt.getSource();
        jtf.select(0, jtf.getText().length());
    }//GEN-LAST:event_searchForFocusGained

    private void shouldReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shouldReplaceActionPerformed
        replaceWith.setEnabled (shouldReplace.isSelected());
        //If they've checked the replace-with box,
        //they probably want to type the text
        if (replaceWith.isEnabled()) {
            replaceWith.requestFocus();
        }
    }//GEN-LAST:event_shouldReplaceActionPerformed

    public void insertUpdate(DocumentEvent e) {
        change();
    }

    public void removeUpdate(DocumentEvent e) {
        change();
    }

    public void changedUpdate(DocumentEvent e) {
        change();
    }

    private ChangeListener change;
    private void change() {
        if (change != null) {
            change.stateChanged(new ChangeEvent(this));
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox binaryBox;
    private javax.swing.JCheckBox caseBox;
    private javax.swing.JCheckBox includeIgnoredBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField replaceWith;
    private javax.swing.JTextField searchFor;
    private javax.swing.JCheckBox shouldReplace;
    private javax.swing.JCheckBox subfolderBox;
    // End of variables declaration//GEN-END:variables

}
