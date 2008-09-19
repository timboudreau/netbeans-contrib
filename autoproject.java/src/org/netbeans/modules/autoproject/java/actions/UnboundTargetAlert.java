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

package org.netbeans.modules.autoproject.java.actions;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.apache.tools.ant.module.api.support.AntScriptUtils;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.w3c.dom.Element;

/**
 * Alert in case a common global action is invoked by the user but there is no binding.
 * The user is prompted to pick one.
 * @author Jesse Glick
 */
public final class UnboundTargetAlert extends JPanel implements  ActionListener {

    private final Project project;
    private final String command;
    /** display label of the command */
    private final String label;

    /**
     * Create an alert.
     * @param command an action as in {@link ActionProvider}
     */
    public UnboundTargetAlert(Project project, String command) {
        this.project = project;
        this.command = command;
        label = NbBundle.getMessage(UnboundTargetAlert.class, "CMD_" + command);
        initComponents();
        listTargets();
    }

    /**
     * Populate the combo box with (eligible) build targets.
     */
    private void listTargets() {
        FileObject script = project.getProjectDirectory().getFileObject(scriptField.getText());
        if (script != null) {
            List<String> targets = null;
            try {
                targets = AntScriptUtils.getCallableTargetNames(script);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            Element projectEl = AntScriptUtils.antProjectCookieFor(script).getProjectElement();
            String defaultTarget = projectEl != null ? projectEl.getAttribute("default") : ""; // NOI18N
            if (targets != null) {
                targetCombo.setModel(new DefaultComboBoxModel(targets.toArray(new String[targets.size()])));
                targetCombo.setSelectedItem(guessTarget(command, targets, defaultTarget));
            }
        }
    }

    /** @see org.netbeans.modules.ant.freeform.ui.TargetMappingPanel */
    private static final Map<String,List<String>> DEFAULT_TARGETS = new HashMap<String,List<String>>();
    static {
        DEFAULT_TARGETS.put(ActionProvider.COMMAND_BUILD, Arrays.asList("build", "compile", "jar", "dist", "all", ".*jar.*")); // NOI18N
        DEFAULT_TARGETS.put(ActionProvider.COMMAND_CLEAN, Arrays.asList("clean", ".*clean.*")); // NOI18N
        DEFAULT_TARGETS.put(ActionProvider.COMMAND_RUN, Arrays.asList("run", "start", ".*run.*", ".*start.*")); // NOI18N
        DEFAULT_TARGETS.put(ActionProvider.COMMAND_TEST, Arrays.asList("test", "tests", ".*test.*")); // NOI18N
        DEFAULT_TARGETS.put(JavaProjectConstants.COMMAND_JAVADOC, Arrays.asList("javadoc", "javadocs", "docs", "doc", ".*javadoc.*", ".*doc.*")); // NOI18N
    }
    /**
     * Guess at a likely Ant target for a command.
     * @param command an action as in {@link ActionProvider}
     * @param targets available Ant target names
     * @param defaultTarget the script's default target name, or ""
     * @return the most plausible target to bind, or ""
     */
    private static String guessTarget(String command, List<String> targets, String defaultTarget) {
        if (DEFAULT_TARGETS.containsKey(command)) {
            for (String pattern : DEFAULT_TARGETS.get(command)) {
                for (String target : targets) {
                    if (target.matches(pattern)) {
                        return target;
                    }
                }
            }
        }
        if (command.equals(ActionProvider.COMMAND_BUILD)) {
            return defaultTarget;
        }
        return "";
    }

    /**
     * Just show the dialog but do not do anything about it.
     */
    private boolean displayAlert(String projectDisplayName) {
        String title = NbBundle.getMessage(UnboundTargetAlert.class, "UTA_TITLE", label, projectDisplayName);
        final DialogDescriptor d = new DialogDescriptor(this, title);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        d.setMessageType(NotifyDescriptor.ERROR_MESSAGE);
        d.setValid(!"".equals(targetCombo.getSelectedItem()));
        targetCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                d.setValid(((String) targetCombo.getSelectedItem()).trim().length() > 0);
            }
        });
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        targetCombo.requestFocusInWindow();
        // XXX combo box gets cut off at the bottom unless you do something - why??
        Dimension sz = dlg.getSize();
        dlg.setSize(sz.width, sz.height + 30);
        dlg.setVisible(true);
        return d.getValue() == NotifyDescriptor.OK_OPTION;
    }

    /**
     * Show the alert.
     * If accepted, generate a binding for the command (and add a context menu item for the project).
     * @return true if the alert was accepted and there is now a binding, false if cancelled
     * @throws IOException if there is a problem writing bindings
     */
    public void accepted() {
        String projectDisplayName = ProjectUtils.getInformation(project).getDisplayName();
        if (displayAlert(projectDisplayName)) {
            Cache.put(FileUtil.toFile(project.getProjectDirectory()) + Cache.ACTION + command,
                    "ant:" + scriptField.getText() + ":" + ((String) targetCombo.getSelectedItem()).trim());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        introLabel = new javax.swing.JLabel();
        explanation = new javax.swing.JTextArea();
        scriptLabel = new javax.swing.JLabel();
        scriptField = new javax.swing.JTextField();
        scriptButton = new javax.swing.JButton();
        targetLabel = new javax.swing.JLabel();
        targetCombo = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(introLabel, org.openide.util.NbBundle.getMessage(UnboundTargetAlert.class, "UTA_LBL_intro", label)); // NOI18N

        explanation.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        explanation.setEditable(false);
        explanation.setLineWrap(true);
        explanation.setText(org.openide.util.NbBundle.getMessage(UnboundTargetAlert.class, "UTA_TEXT_explanation", label)); // NOI18N
        explanation.setWrapStyleWord(true);

        scriptLabel.setLabelFor(scriptField);
        org.openide.awt.Mnemonics.setLocalizedText(scriptLabel, org.openide.util.NbBundle.getMessage(UnboundTargetAlert.class, "UnboundTargetAlert.scriptLabel.text")); // NOI18N

        scriptField.setText("build.xml"); // NOI18N
        scriptField.addActionListener(this);

        org.openide.awt.Mnemonics.setLocalizedText(scriptButton, org.openide.util.NbBundle.getMessage(UnboundTargetAlert.class, "UnboundTargetAlert.scriptButton.text")); // NOI18N
        scriptButton.addActionListener(this);

        targetLabel.setLabelFor(targetCombo);
        org.openide.awt.Mnemonics.setLocalizedText(targetLabel, org.openide.util.NbBundle.getMessage(UnboundTargetAlert.class, "UTA_LBL_select")); // NOI18N

        targetCombo.setEditable(true);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(scriptLabel)
                    .add(targetLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(targetCombo, 0, 290, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(scriptField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(scriptButton)))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(introLabel)
                .addContainerGap(112, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(explanation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(introLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(explanation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(scriptLabel)
                    .add(scriptButton)
                    .add(scriptField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(targetCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(targetLabel))
                .addContainerGap())
        );
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == scriptButton) {
            UnboundTargetAlert.this.scriptButtonActionPerformed(evt);
        }
        else if (evt.getSource() == scriptField) {
            UnboundTargetAlert.this.scriptFieldActionPerformed(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void scriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptButtonActionPerformed
        File base = FileUtil.toFile(project.getProjectDirectory());
        JFileChooser chooser = new JFileChooser(base);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                FileObject fo = FileUtil.toFileObject(f);
                return fo != null && fo.getMIMEType().equals("text/x-ant+xml");
            }
            public String getDescription() {
                return NbBundle.getMessage(UnboundTargetAlert.class, "UnboundTargetAlert.ant_scripts");
            }
        });
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            scriptField.setText(PropertyUtils.relativizeFile(base, chooser.getSelectedFile()));
            listTargets();
        }
    }//GEN-LAST:event_scriptButtonActionPerformed

    private void scriptFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptFieldActionPerformed
        listTargets();
    }//GEN-LAST:event_scriptFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea explanation;
    private javax.swing.JLabel introLabel;
    private javax.swing.JButton scriptButton;
    private javax.swing.JTextField scriptField;
    private javax.swing.JLabel scriptLabel;
    private javax.swing.JComboBox targetCombo;
    private javax.swing.JLabel targetLabel;
    // End of variables declaration//GEN-END:variables

}
