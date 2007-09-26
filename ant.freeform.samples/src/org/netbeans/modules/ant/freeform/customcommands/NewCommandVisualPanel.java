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

package org.netbeans.modules.ant.freeform.customcommands;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.UIResource;
import org.openide.ErrorManager;
import org.openide.awt.Mnemonics;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

public final class NewCommandVisualPanel extends JPanel {
    
    public NewCommandVisualPanel(final NewCommandWizardPanel master, String[] likelyCommandNames) {
        initComponents();
        command.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                master.fireChangeEvent();
            }
        });
        displayName.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }
            public void insertUpdate(DocumentEvent e) {
                master.fireChangeEvent();
            }
            public void removeUpdate(DocumentEvent e) {
                master.fireChangeEvent();
            }
        });
        command.setModel(new DefaultComboBoxModel(likelyCommandNames));
        updateMenus();
    }
    
    private void updateMenus() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        DataFolder menuBarFolder = DataFolder.findFolder(Repository.getDefault().getDefaultFileSystem().findResource("Menu")); // NOI18N
        DataObject[] kids = menuBarFolder.getChildren();
        for (int i = 0; i < kids.length; i++) {
            if (!(kids[i] instanceof DataFolder)) {
                continue;
            }
            model.addElement(kids[i].getPrimaryFile().getNameExt());
            DataObject[] kids2 = ((DataFolder) kids[i]).getChildren();
            for (int j = 0; j < kids2.length; j++) {
                if (!(kids2[j] instanceof DataFolder)) {
                    continue;
                }
                model.addElement(kids[i].getPrimaryFile().getNameExt() + '/' + kids2[j].getPrimaryFile().getNameExt());
            }
        }
        menu.setModel(model);
        menu.setSelectedItem("BuildProject"); // NOI18N
        menu.setRenderer(new MenuListCellRenderer());
        updatePositions();
    }
    
    private void updatePositions() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        FileObject menuFolderFO = Repository.getDefault().getDefaultFileSystem().findResource("Menu/" + getMenu()); // NOI18N
        if (menuFolderFO != null) {
            DataFolder menuFolder = DataFolder.findFolder(menuFolderFO);
            DataObject[] kids = menuFolder.getChildren();
            for (int i = 0; i <= kids.length; i++) {
                String before;
                if (i > 0) {
                    before = findName(kids[i - 1]);
                } else {
                    before = "START"; // XXX I18N
                }
                String after;
                if (i < kids.length) {
                    after = findName(kids[i]);
                } else {
                    after = "END"; // XXX I18N
                }
                model.addElement(before + " \u2194 " + after); // XXX I18N
            }
        }
        position.setModel(model);
    }
    
    private static String findName(DataObject dataObject) {
        InstanceCookie ic = (InstanceCookie) dataObject.getCookie(InstanceCookie.class);
        if (ic != null) {
            Object o;
            try {
                o = ic.instanceCreate();
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                o = null;
            }
            if (o instanceof Action) {
                return trimAmpersands((String) ((Action) o).getValue(Action.NAME));
            }
        }
        return trimAmpersands(dataObject.getNodeDelegate().getDisplayName());
    }
    
    private static String trimAmpersands(String text) {
        int amp = Mnemonics.findMnemonicAmpersand(text);
        if (amp == -1) {
            return text;
        } else {
            return text.substring(0, amp) + text.substring(amp + 1);
        }
    }
    
    private static final class MenuListCellRenderer extends JLabel implements ListCellRenderer, UIResource {
        
        public MenuListCellRenderer () {
            setOpaque(true);
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // #93658: GTK needs name to render cell renderer "natively"
            setName("ComboBox.listRenderer"); // NOI18N
            
            String menu = (String) value;
            DataObject d;
            try {
                d = DataObject.find(Repository.getDefault().getDefaultFileSystem().findResource("Menu/" + menu)); // NOI18N
            } catch (DataObjectNotFoundException e) {
                throw new AssertionError(e);
            }
            
            setText(findName(d));
            
            if ( isSelected ) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());             
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            return this;
        }
        
        // #93658: GTK needs name to render cell renderer "natively"
        public String getName() {
            String name = super.getName();
            return name == null ? "ComboBox.renderer" : name;  // NOI18N
        }
        
    }

    public String getName() {
        return NbBundle.getMessage(NewCommandVisualPanel.class, "NewCommandVisualPanel.name");
    }
    
    String getCommand() {
        return ((String) command.getSelectedItem()).trim();
    }
    
    String getDisplayName() {
        return displayName.getText();
    }
    
    String getMenu() {
        return ((String) menu.getSelectedItem());
    }
    
    int getPosition() {
        return position.getSelectedIndex();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        commandLabel = new javax.swing.JLabel();
        command = new javax.swing.JComboBox();
        displayNameLabel = new javax.swing.JLabel();
        displayName = new javax.swing.JTextField();
        displayNameLabelSuffix = new javax.swing.JLabel();
        menuLabel = new javax.swing.JLabel();
        menu = new javax.swing.JComboBox();
        positionLabel = new javax.swing.JLabel();
        position = new javax.swing.JComboBox();
        messageLabel = new javax.swing.JLabel();

        commandLabel.setLabelFor(command);
        org.openide.awt.Mnemonics.setLocalizedText(commandLabel, NbBundle.getMessage(NewCommandVisualPanel.class, "commandLabel"));

        command.setEditable(true);

        displayNameLabel.setLabelFor(displayName);
        org.openide.awt.Mnemonics.setLocalizedText(displayNameLabel, NbBundle.getMessage(NewCommandVisualPanel.class, "displayNameLabel"));

        org.openide.awt.Mnemonics.setLocalizedText(displayNameLabelSuffix, NbBundle.getMessage(NewCommandVisualPanel.class, "displayNameLabelSuffix"));

        menuLabel.setLabelFor(menu);
        org.openide.awt.Mnemonics.setLocalizedText(menuLabel, NbBundle.getMessage(NewCommandVisualPanel.class, "menuLabel"));

        menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuActionPerformed(evt);
            }
        });

        positionLabel.setLabelFor(position);
        org.openide.awt.Mnemonics.setLocalizedText(positionLabel, NbBundle.getMessage(NewCommandVisualPanel.class, "positionLabel"));

        org.openide.awt.Mnemonics.setLocalizedText(messageLabel, NbBundle.getMessage(NewCommandVisualPanel.class, "messageLabel"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(displayNameLabel)
                            .add(commandLabel)
                            .add(menuLabel)
                            .add(positionLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(position, 0, 230, Short.MAX_VALUE)
                            .add(menu, 0, 230, Short.MAX_VALUE)
                            .add(command, 0, 230, Short.MAX_VALUE)
                            .add(displayName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(displayNameLabelSuffix))
                    .add(messageLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(commandLabel)
                    .add(command, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(displayNameLabel)
                    .add(displayNameLabelSuffix)
                    .add(displayName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(menuLabel)
                    .add(menu, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(positionLabel)
                    .add(position, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(messageLabel)
                .addContainerGap(158, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuActionPerformed
        updatePositions();
    }//GEN-LAST:event_menuActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox command;
    private javax.swing.JLabel commandLabel;
    private javax.swing.JTextField displayName;
    private javax.swing.JLabel displayNameLabel;
    private javax.swing.JLabel displayNameLabelSuffix;
    private javax.swing.JComboBox menu;
    private javax.swing.JLabel menuLabel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JComboBox position;
    private javax.swing.JLabel positionLabel;
    // End of variables declaration//GEN-END:variables
    
}
