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
package org.netbeans.modules.docbook.project.wizard;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.openide.WizardDescriptor;

/**
 *
 * @author  Tim Boudreau
 */
public class ProjectInfoPanel extends javax.swing.JPanel implements DocumentListener, FocusListener {

    /** Creates new form ProjectInfoPanel */
    public ProjectInfoPanel() {
        initComponents();
        problem.setText (" ");
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JTextComponent) {
                ((JTextComponent) c[i]).getDocument().addDocumentListener(this);
                c[i].addFocusListener(this);
            }
        }
    }

    public String getProblem() {
        String s = problem.getText().trim();
        return s;
    }

    public boolean hasProblem() {
        return getProblem().length() != 0;
    }

    void check() {
        if (containsBadCharacter(name.getText())) {
            setProblem (name.getText() + " contains illegal filename " +
                    "characters");
        }
        File file = new File(location.getText());
        if (!file.exists() || !file.isDirectory()) {
            setProblem ("Folder does not exist");
            return;
        }
        if (!check (title, "Enter a title")) return;
        if (!check (author, "Enter your name")) return;
    }

    private boolean containsBadCharacter (String txt) {
        char[] c = txt.toCharArray();
        for (int i = 0; i < c.length; i++) {
            switch (c[i]) {
                case '\\' :
                case '/' :
                case ';' :
                case ':' :
                case '.' :
                    return true;
                default :
                    continue;
            }
        }
        return false;
    }

    private boolean check (JTextComponent jtc, String err) {
        String txt = jtc.getText();
        boolean result = txt.length() == 0 ? false : true;
        if (!result) {
            setProblem (err);
        } else {
            setProblem ("");
        }
        return result;
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
        wiz.putProperty("kind", kind.getSelectedItem());
        wiz.setValid(!hasProblem());
    }

    boolean updating;
    void load (WizardDescriptor wiz) {
        updating = true;
        try {
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
            String s = (String) wiz.getProperty("kind");
            if (s != null) {
                kind.setSelectedItem(s);
            }
        } finally {
            updating = false;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
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
        kindLabel = new javax.swing.JLabel();
        kind = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        problem = new javax.swing.JLabel();

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

        kindLabel.setLabelFor(kind);
        kindLabel.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.kindLabel.text")); // NOI18N

        kind.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Article", "Slides", "Book" }));
        kind.setName(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.kind.name")); // NOI18N

        problem.setForeground(new java.awt.Color(255, 0, 0));
        problem.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.problem.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(problem, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(namelbl)
                            .add(titleLabel)
                            .add(subtitleLabel)
                            .add(authorLabel)
                            .add(kindLabel)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(loclbl)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(name, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(location, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 235, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                                .add(browse))
                            .add(title, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                            .add(subtitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                            .add(author, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                            .add(kind, 0, 314, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(namelbl)
                    .add(name, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(loclbl)
                    .add(location, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browse))
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
                .add(32, 32, 32)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(kindLabel)
                    .add(kind, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 23, Short.MAX_VALUE)
                .add(problem)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseActionPerformed
        // TODO add your handling code here:
        JFileChooser ch = jfc == null ? (jfc = createFileChooser()) : jfc;
        int val = ch.showDialog(this, "Select");
        if (val == ch.APPROVE_OPTION) {
            location.setText (ch.getSelectedFile().getPath());
            change();
        }
    }//GEN-LAST:event_browseActionPerformed

    private JFileChooser jfc;
    private JFileChooser createFileChooser() {
        JFileChooser result = new JFileChooser();
        result.setMultiSelectionEnabled(false);
        result.setFileSelectionMode(result.DIRECTORIES_ONLY);
        result.setApproveButtonText("Select");
        return result;
    }

    private void change() {
        if (updating) return;
        if (cl!=null) {
            cl.stateChanged (new ChangeEvent(this));
        }
    }

    public void setProblem (String s) {
        problem.setText (s);
        change();
    }

    public void insertUpdate(DocumentEvent e) {
        check();
    }

    public void removeUpdate(DocumentEvent e) {
        check();
    }

    public void changedUpdate(DocumentEvent e) {
        check();
    }

    public void focusGained(FocusEvent e) {
        JTextComponent c = (JTextComponent) e.getComponent();
        c.selectAll();
    }

    public void focusLost(FocusEvent e) {
    }

    ChangeListener cl;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField author;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JButton browse;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox kind;
    private javax.swing.JLabel kindLabel;
    private javax.swing.JTextField location;
    private javax.swing.JLabel loclbl;
    private javax.swing.JTextField name;
    private javax.swing.JLabel namelbl;
    private javax.swing.JLabel problem;
    private javax.swing.JTextField subtitle;
    private javax.swing.JLabel subtitleLabel;
    private javax.swing.JTextField title;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables

}
