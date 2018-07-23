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

package org.netbeans.modules.launch4jint;

import java.awt.Color;
import java.awt.Dialog;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/** UI for process of downloading and locating of Launch4j.
 *
 * @author  Dafe Simonek
 */
class LocatePanel extends javax.swing.JPanel implements DocumentListener {
    
    private static URL downloadPage;

    /** Asociation with dialog descriptor that contains this panel */
    private DialogDescriptor dd;

    private Color previousDirLabelColor;
    private Color previousDirTextFieldColor;
    
    /** Creates new form LocatePanel */
    private LocatePanel() {
        initComponents();
        dirTextField.getDocument().addDocumentListener(this);
        previousDirLabelColor = dirLabel.getForeground();
        previousDirTextFieldColor = dirTextField.getForeground();
        dirLabel.setForeground(Color.RED);
        dirTextField.setForeground(Color.RED);
    }

    /** Obtains install directory of Launch4j software.
     * @return Launch4j install dir or null if it can't be specified/found.
     */
    public static String obtainLaunch4jDir () {
        LocatePanel lp = new LocatePanel();
        DialogDescriptor dd = new DialogDescriptor(lp, 
                NbBundle.getBundle(LocatePanel.class).getString("CTL_LocateTitle"));
        lp.asociateDialogDescriptor(dd);
        dd.setValid(false);
        
        Dialog dlg = DialogDisplayer.getDefault().createDialog(dd);
        dlg.setVisible(true);
        
        if (dd.getValue().equals(NotifyDescriptor.OK_OPTION)) {
            return lp.dirTextField.getText();
        }
        // dialog cancelled
        return null;
    }
    
    private URL getDownloadURL () {
        if (downloadPage == null) {
            try {
                downloadPage = new URL("http://launch4j.sourceforge.net/");
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        return downloadPage;
    }
    
    private String askForDir () {
        JFileChooser fch = new JFileChooser();
        fch.setMultiSelectionEnabled(false);
        fch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fch.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fch.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    /******* implementation of DocumentListener *******/
    
    public void insertUpdate(DocumentEvent e) {
        updatePanelOnDirChange();
    }

    public void removeUpdate(DocumentEvent e) {
        updatePanelOnDirChange();
    }

    public void changedUpdate(DocumentEvent e) {
    }
    
    private void updatePanelOnDirChange () {
        if (Launch4jFinder.checkDir(dirTextField.getText())) {
            dd.setValid(true);
            if (previousDirLabelColor != null) {
                dirLabel.setForeground(previousDirLabelColor);
                dirTextField.setForeground(previousDirTextFieldColor);
                previousDirLabelColor = null;
                previousDirTextFieldColor = null;
            }
        } else {
            dd.setValid(false);
            if (previousDirLabelColor == null) {                                  
                previousDirLabelColor = dirLabel.getForeground();
                previousDirTextFieldColor = dirTextField.getForeground();
                dirLabel.setForeground(Color.RED);
                dirTextField.setForeground(Color.RED);
            }
        }
    }

    private void asociateDialogDescriptor(DialogDescriptor dd) {
        this.dd = dd;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        downloadButton = new javax.swing.JButton();
        dirLabel = new javax.swing.JLabel();
        dirTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jTextArea1 = new javax.swing.JTextArea();

        setEnabled(false);
        downloadButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/launch4jint/Bundle").getString("CTL_Download"));
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        dirLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/launch4jint/Bundle").getString("CTL_DirLabel"));

        browseButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/launch4jint/Bundle").getString("CTL_Browse"));
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        jTextArea1.setBackground(java.awt.SystemColor.control);
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(3);
        jTextArea1.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/launch4jint/Bundle").getString("CTL_NoteText"));
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setAutoscrolls(false);
        jTextArea1.setBorder(null);
        jTextArea1.setFocusable(false);
        jTextArea1.setOpaque(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTextArea1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(dirLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dirTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseButton))
                    .add(downloadButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTextArea1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(downloadButton)
                .add(22, 22, 22)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dirLabel)
                    .add(dirTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String newDir = askForDir();
        if (newDir != null) {
            dirTextField.setText(newDir);
        }      
    }//GEN-LAST:event_browseButtonActionPerformed

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        HtmlBrowser.URLDisplayer.getDefault().showURL(getDownloadURL());
    }//GEN-LAST:event_downloadButtonActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton browseButton;
    public javax.swing.JLabel dirLabel;
    public javax.swing.JTextField dirTextField;
    public javax.swing.JButton downloadButton;
    public javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    
}
