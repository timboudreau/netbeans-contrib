/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs.options;

import java.io.File;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.modules.nodejs.DefaultExectable;
import org.netbeans.modules.nodejs.ui.DowngradeValidator;
import org.netbeans.modules.nodejs.ui.UiUtil;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationUI;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;

final class NodePanel extends JPanel implements ValidationUI, DocumentListener {

    private final NodeOptionsPanelController controller;
    private final ValidationGroup g;
    private final DefaultExectable exe = new DefaultExectable();

    @SuppressWarnings("LeakingThisInConstructor")
    NodePanel(NodeOptionsPanelController controller) {
        this.controller = controller;
        g = ValidationGroup.create(this);
        initComponents();
        g.add(portField, Validators.REQUIRE_NON_EMPTY_STRING, Validators.REQUIRE_VALID_NUMBER, Validators.REQUIRE_VALID_INTEGER, Validators.REQUIRE_NON_NEGATIVE_NUMBER);
        g.add(binaryField, Validators.REQUIRE_NON_EMPTY_STRING, Validators.FILE_MUST_EXIST, Validators.FILE_MUST_BE_FILE);
        g.add(sourcesField, new DowngradeValidator<String>(Validators.merge(Validators.REQUIRE_NON_EMPTY_STRING, Validators.FILE_MUST_EXIST, new FileOrArchiveValidator())));
        UiUtil.prepareComponents(this);
        portField.getDocument().addDocumentListener(this);
        binaryField.getDocument().addDocumentListener(this);
    }

    private static boolean containsJsFiles(File folder) {
        if (folder.isDirectory()) {
            File f = new File(folder, "http.js");
            if (f.exists()) {
                return true;
            }
        }
        return false;
    }

    private static final class FileOrArchiveValidator implements Validator<String> {

        @Override
        public boolean validate(Problems prblms, String string, String model) {
            File f = new File(string);
            if (f.isDirectory()) {
                boolean jsFound = containsJsFiles(f);
                File child = new File(f, "lib");
                jsFound = child.exists() && containsJsFiles(child);
                if (!jsFound) {
                    prblms.add(NbBundle.getMessage(NodePanel.class, "NO_JS_FILES", f.getName()));
                }
                return true;
            }
            if (!f.getName().endsWith(".zip") && !f.getName().endsWith(".jar")) {
                prblms.add(NbBundle.getMessage(NodePanel.class, "NOT_FILE_OR_ARCHIVE", model));
                return false;
            }
            return true;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        problemLabel = g.createProblemLabel();
        binaryLabel = new javax.swing.JLabel();
        binaryField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        portLabel = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();
        sourceLabel = new javax.swing.JLabel();
        sourcesField = new javax.swing.JTextField();
        browseForSources = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(problemLabel, org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.problemLabel.text")); // NOI18N

        binaryLabel.setLabelFor(binaryField);
        org.openide.awt.Mnemonics.setLocalizedText(binaryLabel, org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.binaryLabel.text")); // NOI18N

        binaryField.setText(org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.binary.text")); // NOI18N
        binaryField.setName("binary"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        portLabel.setLabelFor(portField);
        org.openide.awt.Mnemonics.setLocalizedText(portLabel, org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.portLabel.text")); // NOI18N

        portField.setText(org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.port.text")); // NOI18N
        portField.setName("port"); // NOI18N

        sourceLabel.setLabelFor(sourcesField);
        org.openide.awt.Mnemonics.setLocalizedText(sourceLabel, org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.sourceLabel.text")); // NOI18N

        sourcesField.setText(org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.sources.text")); // NOI18N
        sourcesField.setName("sources"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseForSources, org.openide.util.NbBundle.getMessage(NodePanel.class, "NodePanel.browseForSources.text")); // NOI18N
        browseForSources.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseForSourcesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(problemLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(binaryLabel)
                            .addComponent(portLabel)
                            .addComponent(sourceLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(portField, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addComponent(binaryField, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addComponent(sourcesField, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(browseButton)
                    .addComponent(browseForSources))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(binaryLabel)
                    .addComponent(binaryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(portLabel)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourcesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseForSources)
                    .addComponent(sourceLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                .addComponent(problemLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String s = exe.askUserForExecutableLocation();
        if (s != null) {
            binaryField.setText(s);
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void browseForSourcesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseForSourcesActionPerformed
        File where = new FileChooserBuilder(NodePanel.class).setApproveText(NbBundle.getMessage(NodePanel.class, "BROWSE_FOR_SOURCES_APPROVE")).setTitle(NbBundle.getMessage(NodePanel.class, "BROWSE_FOR_SOURCES")).showOpenDialog();
        if (where != null) {
            sourcesField.setText(where.getAbsolutePath());
        }
    }//GEN-LAST:event_browseForSourcesActionPerformed

    void load() {
        g.modifyComponents(new Runnable() {

            @Override
            public void run() {
                String s = exe.getNodeExecutable(false);
                if (s != null) {
                    binaryField.setText(s);
                } else {
                    binaryField.setText("");
                }
                portField.setText("" + exe.getDefaultPort());
                s = exe.getSourcesLocation();
                if (s != null) {
                    sourcesField.setText(s);
                }
            }
        });
    }

    void store() {
        exe.setNodeExecutable(binaryField.getText());
        exe.setDefaultPort(Integer.parseInt(portField.getText()));
        exe.setSourcesLocation(sourcesField.getText());
    }

    boolean valid() {
        return controller.isValid();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField binaryField;
    private javax.swing.JLabel binaryLabel;
    private javax.swing.JButton browseButton;
    private javax.swing.JButton browseForSources;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JLabel problemLabel;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JTextField sourcesField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void clearProblem() {
        controller.setValid(true);
    }

    @Override
    public void setProblem(Problem prblm) {
        controller.setValid(!prblm.isFatal());
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        controller.changed();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        controller.changed();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        controller.changed();
    }
}
