/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.fuse.ui.options;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.fuse.FuseFramework;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * @author Martin Fousek
 */
public class FuseOptionsPanel extends JPanel {
    private static final long serialVersionUID = -13766303191714740L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    public FuseOptionsPanel() {
        initComponents();

        // not set in Design because of windows (panel too wide then)
//        fuseScriptUsageLabel.setText(NbBundle.getMessage(FuseOptionsPanel.class, "LBL_FuseUsage"));
        errorLabel.setText(" "); // NOI18N

        fuseTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                processUpdate();
            }
            public void removeUpdate(DocumentEvent e) {
                processUpdate();
            }
            public void changedUpdate(DocumentEvent e) {
                processUpdate();
            }
            private void processUpdate() {
                fireChange();
            }
        });
    }

    public String getFuse() {
        return fuseTextField.getText();
    }

    public void setFuse(String fuse) {
        fuseTextField.setText(fuse);
    }

    public void setError(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void setWarning(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.warningForeground")); // NOI18N
        errorLabel.setText(message);
    }

    protected void improveSupportTypeButtonEnabled(boolean setStatus) {
        improveSupportTypeButton.setEnabled(setStatus);
    }

    public void updateSupportType() {
        if (new FuseFramework(getFuse()).isImproved()) {
            supportTypeLabel.setForeground(new Color(24, 114, 24));
            supportTypeLabel.setText(NbBundle.getMessage(FuseOptionsPanel.class, "SupportType_Full"));
            improveSupportTypeButtonEnabled(false);
        } else {
            supportTypeLabel.setForeground(Color.ORANGE);
            supportTypeLabel.setText(NbBundle.getMessage(FuseOptionsPanel.class, "SupportType_Partial"));
            improveSupportTypeButtonEnabled(true);
        }
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    void setUnavailableSupportType() {
        supportTypeLabel.setForeground(Color.RED);
        supportTypeLabel.setText(NbBundle.getMessage(FuseOptionsPanel.class, "SupportType_NA"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fuseLabel = new JLabel();
        fuseTextField = new JTextField();
        browseButton = new JButton();
        searchButton = new JButton();
        noteLabel = new JLabel();
        includePathInfoLabel = new JLabel();
        installationInfoLabel = new JLabel();
        learnMoreLabel = new JLabel();
        errorLabel = new JLabel();
        supportStatusLabel = new JLabel();
        improveSupportTypeButton = new JButton();
        supportTypeLabel = new JLabel();

        setPreferredSize(new Dimension(700, 243));
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        fuseLabel.setLabelFor(fuseTextField);







        Mnemonics.setLocalizedText(fuseLabel, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.fuseLabel.text"));
        Mnemonics.setLocalizedText(browseButton, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.browseButton.text"));
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        Mnemonics.setLocalizedText(searchButton, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.searchButton.text"));
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        Mnemonics.setLocalizedText(noteLabel, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.noteLabel.text"));
        Mnemonics.setLocalizedText(includePathInfoLabel, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.includePathInfoLabel.text"));
        Mnemonics.setLocalizedText(installationInfoLabel, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.installationInfoLabel.text"));
        Mnemonics.setLocalizedText(learnMoreLabel, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.learnMoreLabel.text"));
        learnMoreLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                learnMoreLabelMouseEntered(evt);
            }
            public void mousePressed(MouseEvent evt) {
                learnMoreLabelMousePressed(evt);
            }
        });
        Mnemonics.setLocalizedText(errorLabel, "ERROR");
        Mnemonics.setLocalizedText(supportStatusLabel, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.supportStatusLabel.text"));
        Mnemonics.setLocalizedText(improveSupportTypeButton, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.improveSupportTypeButton.text"));
        improveSupportTypeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                improveSupportTypeButtonActionPerformed(evt);
            }
        });
        Mnemonics.setLocalizedText(supportTypeLabel, NbBundle.getMessage(FuseOptionsPanel.class, "FuseOptionsPanel.supportTypeLabel.text"));
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(errorLabel, GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(includePathInfoLabel))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(installationInfoLabel)
                                .addPreferredGap(LayoutStyle.RELATED, 107, GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(learnMoreLabel)
                                .addPreferredGap(LayoutStyle.RELATED, 381, GroupLayout.PREFERRED_SIZE))
                            .add(noteLabel))
                        .addContainerGap())
                    .add(layout.createParallelGroup(GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                            .add(supportStatusLabel)
                            .add(18, 18, 18)
                            .add(supportTypeLabel)
                            .add(73, 73, 73)
                            .add(improveSupportTypeButton)
                            .addContainerGap())
                        .add(layout.createSequentialGroup()
                            .add(fuseLabel)
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(fuseTextField, GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(browseButton)
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(searchButton)
                            .add(0, 0, 0)))))
        );

        layout.linkSize(new Component[] {browseButton, searchButton}, GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(fuseLabel)
                    .add(fuseTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(searchButton)
                    .add(browseButton))
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(supportStatusLabel)
                    .add(supportTypeLabel)
                    .add(improveSupportTypeButton))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(noteLabel)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(includePathInfoLabel)
                .add(18, 18, 18)
                .add(installationInfoLabel)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(learnMoreLabel)
                .addPreferredGap(LayoutStyle.RELATED, 47, Short.MAX_VALUE)
                .add(errorLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File fuseFramework = new FileChooserBuilder(FuseOptionsPanel.class.getName())
                .setTitle(NbBundle.getMessage(FuseOptionsPanel.class, "LBL_SelectFuse"))
                .setDirectoriesOnly(true)
                .showOpenDialog();
        if (fuseFramework != null) {
            fuseFramework = FileUtil.normalizeFile(fuseFramework);
            fuseTextField.setText(fuseFramework.getAbsolutePath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void searchButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
         String fuseScript = UiUtils.SearchWindow.search(new UiUtils.SearchWindow.SearchWindowSupport() {
            public List<String> detect() {
                return FileUtils.findFileOnUsersPath(FuseFramework.SCRIPT_NAME);
            }

            public String getWindowTitle() {
                return NbBundle.getMessage(FuseOptionsPanel.class, "LBL_FuseScriptsTitle");
            }

            public String getListTitle() {
                return NbBundle.getMessage(FuseOptionsPanel.class, "LBL_FuseScripts");
            }

            public String getPleaseWaitPart() {
                return NbBundle.getMessage(FuseOptionsPanel.class, "LBL_FuseScriptsPleaseWaitPart");
            }

            public String getNoItemsFound() {
                return NbBundle.getMessage(FuseOptionsPanel.class, "LBL_NoFuseScriptsFound");
            }
        });
        if (fuseScript != null) {
            fuseTextField.setText(fuseScript);
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void learnMoreLabelMouseEntered(MouseEvent evt) {//GEN-FIRST:event_learnMoreLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_learnMoreLabelMouseEntered

    private void learnMoreLabelMousePressed(MouseEvent evt) {//GEN-FIRST:event_learnMoreLabelMousePressed
        URL url = null;
        try {
            url = new URL("http://phpfuse.net/wiki/index.php?title=Installation"); // NOI18N
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        assert url != null;
        HtmlBrowser.URLDisplayer.getDefault().showURL(url);
    }//GEN-LAST:event_learnMoreLabelMousePressed

    private void formComponentShown(ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        updateSupportType();
    }//GEN-LAST:event_formComponentShown

    private void improveSupportTypeButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_improveSupportTypeButtonActionPerformed
        // asking for confirmation of copying file
        if (JOptionPane.showConfirmDialog(this, NbBundle.getMessage(FuseOptionsPanel.class, "FullSupportWarning",
                getFuse(), FuseFramework.CMD_INIT_PROJECT),"Improve FUSE support", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            try {
                new FuseFramework(getFuse()).improveFuseSupport();
            } 
            catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, NbBundle.getMessage(FuseOptionsPanel.class, "FileCantBeCreatedException"));
            }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(this, NbBundle.getMessage(FuseOptionsPanel.class, "FileWasntCreatedException"));
            }
            updateSupportType();
        }
    }//GEN-LAST:event_improveSupportTypeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton browseButton;
    private JLabel errorLabel;
    private JLabel fuseLabel;
    private JTextField fuseTextField;
    private JButton improveSupportTypeButton;
    private JLabel includePathInfoLabel;
    private JLabel installationInfoLabel;
    private JLabel learnMoreLabel;
    private JLabel noteLabel;
    private JButton searchButton;
    private JLabel supportStatusLabel;
    protected JLabel supportTypeLabel;
    // End of variables declaration//GEN-END:variables

}
