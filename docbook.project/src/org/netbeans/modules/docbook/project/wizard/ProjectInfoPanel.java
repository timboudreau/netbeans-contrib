/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.docbook.project.wizard;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationListener;
import org.netbeans.validation.api.ui.ValidationUI;
import org.openide.WizardDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;

/**
 *
 * @author  Tim Boudreau
 */
public class ProjectInfoPanel extends javax.swing.JPanel implements FocusListener, ValidationUI {
    private final ValidationGroup group = ValidationGroup.create(this);
    public ProjectInfoPanel() {
        initComponents();
        articleButton.putClientProperty ("kind", ProjectKind.Article); //NOI18N
        slidesButton.putClientProperty ("kind", ProjectKind.Slides); //NOI18N
        bookButton.putClientProperty ("kind", ProjectKind.Book); //NOI18N
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JTextComponent) {
                c[i].addFocusListener(this);
            }
            String nm = c[i].getName();
            if (nm != null) {
                ValidationListener.setComponentName((JComponent) c[i],
                        NbBundle.getMessage(ProjectInfoPanel.class, nm));
            }
            if (c[i] instanceof JLabel) {
                JLabel l = (JLabel) c[i];
                Mnemonics.setLocalizedText(l, l.getText());
            } else if (c[i] instanceof AbstractButton) {
                AbstractButton ab = (AbstractButton) c[i];
                Mnemonics.setLocalizedText(ab, ab.getText());
            }
        }
        group.add(new AbstractButton[] { bookButton, slidesButton, articleButton },
                Validators.oneButtonMustBeSelected(
                NbBundle.getMessage(ProjectInfoPanel.class, "ERR_SELECT_KIND"))); //NOI18N
        
        group.add(name, Validators.REQUIRE_NON_EMPTY_STRING, 
                Validators.REQUIRE_VALID_FILENAME,
                Validators.FILE_MUST_NOT_EXIST);
        group.add(location, Validators.REQUIRE_NON_EMPTY_STRING,
                Validators.FILE_MUST_EXIST,
                Validators.FILE_MUST_BE_DIRECTORY);
        group.add(title, Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(subtitle, Validators.REQUIRE_NON_EMPTY_STRING);
        group.add(author, Validators.REQUIRE_NON_EMPTY_STRING);
    }

    void save (WizardDescriptor wiz) {
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JTextComponent) {
                JTextComponent jtc = (JTextComponent) c[i];
                String key = jtc.getName();
                if (key != null) {
                    wiz.putProperty(key, jtc.getText());
                }
            }
        }
        wiz.putProperty("kind", getKind()); //NOI18N
        wiz.setValid(group.validateAll() == null);
    }

    private ProjectKind getKind() {
        if (bookButton.isSelected()) {
            return ProjectKind.Book;
        } else if (slidesButton.isSelected()) {
            return ProjectKind.Slides;
        } else if (articleButton.isSelected()) {
            return ProjectKind.Article;
        } else {
            return null;
        }
    }

    WizardDescriptorAdapter delegate;
    void load (final WizardDescriptor wiz) {
        delegate = new WizardDescriptorAdapter(wiz);
        group.modifyComponents(new Runnable() {
            public void run() {
                Component[] c = getComponents();
                for (int i = 0; i < c.length; i++) {
                    if (c[i] instanceof JTextComponent) {
                        JTextComponent jtc = (JTextComponent) c[i];
                        String key = jtc.getName();
                        if (key != null) {
                            String val = (String) wiz.getProperty(key);
                            if (val != null) {
                                jtc.setText(val);
                            }
                        }
                    }
                }
                ProjectKind kind = (ProjectKind) wiz.getProperty("kind"); //NOI18N
                if (kind != null) {
                    for (AbstractButton b : new AbstractButton[] { articleButton, bookButton, slidesButton }) {
                        if (kind.equals(b.getClientProperty("kind"))) { //NOI18N
                            b.setSelected(true);
                            break;
                        }
                    }
                }
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        namelbl = new javax.swing.JLabel();
        name = new javax.swing.JTextField();
        loclbl = new javax.swing.JLabel();
        location = new javax.swing.JTextField();
        browse = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        title = new javax.swing.JTextField();
        subtitleLabel = new javax.swing.JLabel();
        subtitle = new javax.swing.JTextField();
        authorLabel = new javax.swing.JLabel();
        author = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        kindLabel = new javax.swing.JLabel();
        kindsPanel = new javax.swing.JPanel();
        bookButton = new javax.swing.JRadioButton();
        slidesButton = new javax.swing.JRadioButton();
        articleButton = new javax.swing.JRadioButton();

        namelbl.setLabelFor(name);
        namelbl.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.namelbl.text")); // NOI18N

        name.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.name.text")); // NOI18N
        name.setName(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.name.name")); // NOI18N

        loclbl.setLabelFor(location);
        loclbl.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.loclbl.text")); // NOI18N

        location.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.location.text")); // NOI18N
        location.setName(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.location.name")); // NOI18N

        browse.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.browse.text")); // NOI18N
        browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseActionPerformed(evt);
            }
        });

        titleLabel.setLabelFor(title);
        titleLabel.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.titleLabel.text")); // NOI18N

        title.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.title.text")); // NOI18N
        title.setName(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.title.name")); // NOI18N

        subtitleLabel.setLabelFor(subtitle);
        subtitleLabel.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.subtitleLabel.text")); // NOI18N

        subtitle.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.subtitle.text")); // NOI18N
        subtitle.setName(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.subtitle.name")); // NOI18N

        authorLabel.setLabelFor(author);
        authorLabel.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.authorLabel.text")); // NOI18N

        author.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.author.text")); // NOI18N
        author.setName(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.author.name")); // NOI18N

        kindLabel.setLabelFor(bookButton);
        kindLabel.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.kindLabel.text")); // NOI18N

        kindsPanel.setLayout(new java.awt.GridLayout());

        buttonGroup1.add(bookButton);
        bookButton.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.bookButton.text")); // NOI18N
        kindsPanel.add(bookButton);

        buttonGroup1.add(slidesButton);
        slidesButton.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.slidesButton.text")); // NOI18N
        slidesButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        kindsPanel.add(slidesButton);

        buttonGroup1.add(articleButton);
        articleButton.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.articleButton.text")); // NOI18N
        articleButton.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        kindsPanel.add(articleButton);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(namelbl)
                            .add(titleLabel)
                            .add(subtitleLabel)
                            .add(authorLabel)
                            .add(loclbl)
                            .add(kindLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(kindsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                            .add(name, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(location, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(browse))
                            .add(title, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                            .add(subtitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                            .add(author, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(kindLabel)
                    .add(kindsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(namelbl)
                    .add(name, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(location, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browse)
                    .add(loclbl))
                .add(20, 20, 20)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(titleLabel)
                    .add(title, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(subtitleLabel)
                    .add(subtitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(authorLabel)
                    .add(author, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {kindLabel, kindsPanel}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents

    private void browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseActionPerformed
        File file = new FileChooserBuilder(ProjectInfoPanel.class).setDirectoriesOnly(true).setApproveText("Select").showOpenDialog();
        if (file != null) {
            location.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_browseActionPerformed

    public void focusGained(FocusEvent e) {
        JTextComponent c = (JTextComponent) e.getComponent();
        c.selectAll();
    }

    public Problem check() {
        return group.validateAll();
    }

    public void focusLost(FocusEvent e) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton articleButton;
    private javax.swing.JTextField author;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JRadioButton bookButton;
    private javax.swing.JButton browse;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel kindLabel;
    private javax.swing.JPanel kindsPanel;
    private javax.swing.JTextField location;
    private javax.swing.JLabel loclbl;
    private javax.swing.JTextField name;
    private javax.swing.JLabel namelbl;
    private javax.swing.JRadioButton slidesButton;
    private javax.swing.JTextField subtitle;
    private javax.swing.JLabel subtitleLabel;
    private javax.swing.JTextField title;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

    public void clearProblem() {
        if (delegate != null) {
            delegate.clearProblem();
        }
        if (cl != null) {
            cl.stateChanged(null);
        }
    }

    public void setProblem(Problem prblm) {
        if (delegate != null) {
            delegate.setProblem(prblm);
        }
        if (cl != null) {
            cl.stateChanged(null);
        }
    }

    private ChangeListener cl;
    void addChangeListener(ChangeListener aThis) {
        cl = aThis;
    }
}
