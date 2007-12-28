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

package org.netbeans.modules.javahints.options;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Lahoda
 */
public class CodeStylePanel extends javax.swing.JPanel {
    
    private Map<FileObject, String> file2Text;
    private FileObject currentFile;
    private boolean isCurrentModified;
    private boolean updateInProgress;
    
    /** Creates new form CodeStylePanel */
    public CodeStylePanel(final CodeStyleOptionsPanelController c) {
        initComponents();
        
        cancel();
        editor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (updateInProgress) return ;
                isCurrentModified = true;
                c.changed();
            }
            public void removeUpdate(DocumentEvent e) {
                if (updateInProgress) return ;
                isCurrentModified = true;
                c.changed();
            }
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    void cancel() {
        file2Text = new HashMap<FileObject, String>();
        isCurrentModified = false;
        selectionChanged(null);
    }

    void store() {
        selectionChanged(null);
        for (Map.Entry<FileObject, String> fileAndText : file2Text.entrySet()) {
            Writer w = null;
            Reader r = new StringReader(fileAndText.getValue());

            try {
                w = new OutputStreamWriter(fileAndText.getKey().getOutputStream(), "UTF-8");

                int read;

                while ((read = r.read()) != (-1)) {
                    w.append((char) read);
                }
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            } finally {
                try {
                    if (w != null) {
                        w.close();
                    }
                    r.close();
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
            }
        }
        
        cancel();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        snipetSelector = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        editor = new javax.swing.JEditorPane();

        snipetSelector.setModel(getModel());
        snipetSelector.setRenderer(new RendererImpl());
        snipetSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectionChanged(evt);
            }
        });

        editor.setContentType(org.openide.util.NbBundle.getBundle(CodeStylePanel.class).getString("CodeStylePanel.editor.contentType")); // NOI18N
        jScrollPane1.setViewportView(editor);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, snipetSelector, 0, 376, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(snipetSelector, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectionChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectionChanged
        updateInProgress = true;
        
        try {
            Object selected = snipetSelector.getSelectedItem();

            if (selected instanceof FileObject) {
                FileObject file = (FileObject) selected;

                if (isCurrentModified && currentFile != null) {
                    file2Text.put(currentFile, editor.getText());
                }

                currentFile = file;

                String content = file2Text.get(file);

                if (content == null) {
                    isCurrentModified = false;

                    StringWriter w = new StringWriter();
                    Reader r = null;

                    try {
                        r = new InputStreamReader(file.getInputStream(), "UTF-8");

                        int read;

                        while ((read = r.read()) != (-1)) {
                            w.append((char) read);
                        }

                        w.close();
                        content = w.toString();
                    } catch (IOException e) {
                        Exceptions.printStackTrace(e);
                    } finally {
                        try {
                            if (r != null) {
                                r.close();
                            }
                            w.close();
                        } catch (IOException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                } else {
                    isCurrentModified = true;
                }

                if (content != null) {
                    editor.setText(content);
                }
            }
        } finally {
            updateInProgress = false;
        }
    }//GEN-LAST:event_selectionChanged
    
    private List<FileObject> listFiles() {
        FileObject codestyleDir = Repository.getDefault().getDefaultFileSystem().findResource("org.netbeans.modules.java.codestyle");
        
        if (codestyleDir == null) {
            return Collections.emptyList();
        }
        
        return Arrays.asList(codestyleDir.getChildren());
    }
    
    private ComboBoxModel getModel() {
        DefaultComboBoxModel cbm = new DefaultComboBoxModel();
        
        for (FileObject f : listFiles()) {
            cbm.addElement(f);
        }
        
        return cbm;
    }
    
    private static String getFileName(FileObject file) {
        Object bundle = file.getAttribute("SystemFileSystem.localizingBundle");

        if (bundle instanceof String) {
            try {
                ResourceBundle b = NbBundle.getBundle((String) bundle);
                return b.getString(file.getPath());
            } catch (MissingResourceException e) {
                Logger.getLogger(CodeStylePanel.class.getName()).log(Level.FINE, null, e);
            }
        }
        
        return FileUtil.getFileDisplayName(file);
    }
    
    private final class RendererImpl extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof FileObject) {
                boolean isModified = file2Text.containsKey(value) || (value == snipetSelector.getSelectedItem() && isCurrentModified);
                value = getFileName((FileObject) value) + (isModified ? "*" : ""); // NOI18N
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane editor;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox snipetSelector;
    // End of variables declaration//GEN-END:variables
    
}
