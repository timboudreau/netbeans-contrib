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

package org.netbeans.modules.quickfilechooser;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public class Demo extends JPanel {

    public static void main(String[] args) {
        Install.main(null);
        final JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setCurrentDirectory(new File(System.getProperty("java.io.tmpdir")));
        chooser.setFileView(new FileView() {
            public Icon getIcon(File f) {
                if (f.getName().endsWith(".gif") || f.getName().endsWith(".png")) {
                    Icon icon = new ImageIcon(f.getAbsolutePath());
                    if (icon.getIconWidth() == 16 && icon.getIconHeight() == 16) {
                        return icon;
                    }
                }
                return null;
            }
        });
        chooser.setAccessory(new Demo(chooser));
        Dimension d = chooser.getPreferredSize();
        chooser.setPreferredSize(new Dimension(d.width + 200, d.height));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("Selected: " + Arrays.asList(chooser.getSelectedFiles()));
        }
        System.exit(0);
    }

    private final JFileChooser chooser;
    private Demo(JFileChooser c) {
        this.chooser = c;
        initComponents();
        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setText(currentDir, chooser.getCurrentDirectory());
                setText(selectedFile, chooser.getSelectedFile());
                selectedFiles.setModel(new DefaultComboBoxModel(chooser.getSelectedFiles()));
            }
            private void setText(JTextField field, File f) {
                field.setText(f != null ? f.getAbsolutePath() : null);
            }
        });
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modeGroup = new javax.swing.ButtonGroup();
        currentDirLabel = new javax.swing.JLabel();
        currentDir = new javax.swing.JTextField();
        selectedFileLabel = new javax.swing.JLabel();
        selectedFile = new javax.swing.JTextField();
        selectedFilesLabel = new javax.swing.JLabel();
        selectedFilesScroll = new javax.swing.JScrollPane();
        selectedFiles = new javax.swing.JList();
        files = new javax.swing.JRadioButton();
        dirs = new javax.swing.JRadioButton();
        both = new javax.swing.JRadioButton();
        html = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Chooser Properties"));

        currentDirLabel.setDisplayedMnemonic('U');
        currentDirLabel.setLabelFor(currentDir);
        currentDirLabel.setText("Current dir:");

        currentDir.setEditable(false);

        selectedFileLabel.setDisplayedMnemonic('S');
        selectedFileLabel.setLabelFor(selectedFile);
        selectedFileLabel.setText("Selected file:");

        selectedFile.setEditable(false);

        selectedFilesLabel.setDisplayedMnemonic('E');
        selectedFilesLabel.setLabelFor(selectedFiles);
        selectedFilesLabel.setText("Selected files:");

        selectedFilesScroll.setViewportView(selectedFiles);

        modeGroup.add(files);
        files.setMnemonic('F');
        files.setSelected(true);
        files.setText("Files");
        files.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filesActionPerformed(evt);
            }
        });

        modeGroup.add(dirs);
        dirs.setMnemonic('D');
        dirs.setText("Dirs");
        dirs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dirsActionPerformed(evt);
            }
        });

        modeGroup.add(both);
        both.setMnemonic('B');
        both.setText("Both");
        both.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bothActionPerformed(evt);
            }
        });

        html.setMnemonic('H');
        html.setText("HTML Only");
        html.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                htmlActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentDirLabel)
                    .addComponent(selectedFileLabel)
                    .addComponent(selectedFilesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectedFilesScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(selectedFile, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(currentDir, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)))
            .addGroup(layout.createSequentialGroup()
                .addComponent(files)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dirs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(both)
                .addGap(34, 34, 34))
            .addGroup(layout.createSequentialGroup()
                .addComponent(html)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentDirLabel)
                    .addComponent(currentDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectedFileLabel)
                    .addComponent(selectedFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectedFilesLabel)
                    .addComponent(selectedFilesScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(files)
                    .addComponent(dirs)
                    .addComponent(both))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(html)
                .addContainerGap(124, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void dirsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dirsActionPerformed
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }//GEN-LAST:event_dirsActionPerformed

    private void filesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filesActionPerformed
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    }//GEN-LAST:event_filesActionPerformed

    private void bothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bothActionPerformed
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }//GEN-LAST:event_bothActionPerformed

    private void htmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_htmlActionPerformed
        chooser.setFileFilter(html.isSelected() ? new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory() ||
                        pathname.getName().toLowerCase().endsWith(".html");
            }
            public String getDescription() {
                return "HTML Files";
            }
        } : null);
    }//GEN-LAST:event_htmlActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton both;
    private javax.swing.JTextField currentDir;
    private javax.swing.JLabel currentDirLabel;
    private javax.swing.JRadioButton dirs;
    private javax.swing.JRadioButton files;
    private javax.swing.JCheckBox html;
    private javax.swing.ButtonGroup modeGroup;
    private javax.swing.JTextField selectedFile;
    private javax.swing.JLabel selectedFileLabel;
    private javax.swing.JList selectedFiles;
    private javax.swing.JLabel selectedFilesLabel;
    private javax.swing.JScrollPane selectedFilesScroll;
    // End of variables declaration//GEN-END:variables

}
