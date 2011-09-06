/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.yii.ui.wizards;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.modules.php.yii.YiiExtensions;
import org.netbeans.modules.php.yii.YiiProjectConfigurationImpl;
import org.netbeans.modules.php.yii.YiiScript;
import org.netbeans.modules.php.yii.extensions.api.YiiExtensionProvider;
import org.netbeans.modules.php.yii.extensions.api.YiiProjectConfiguration;
import org.netbeans.modules.php.yii.ui.options.YiiOptions;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Gevik Babakhani <gevik@netbeans.org>
 */
public class NewProjectConfigurationPanel extends JPanel implements ChangeListener, TableModelListener {

    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private YiiProjectConfiguration projectConfig;
    private ExtensionTableModel model;
    private YiiExtensionProvider currentExtension;

    /** Creates new form NewProjectConfigurationPanel */
    public NewProjectConfigurationPanel() {
        initComponents();
        projectConfig = new YiiProjectConfigurationImpl();
        optionsLabel.setMaximumSize(optionsLabel.getPreferredSize());

        model = new ExtensionTableModel();
        extensionsTable.setModel(model);

        createExtensionsList();
        initTableVisualProperties();
    }

    private void initTableVisualProperties() {
        extensionsTable.getModel().addTableModelListener(this);
        //extensionsTable.getSelectionModel().addListSelectionListener(this);

        extensionsTable.setRowHeight(extensionsTable.getRowHeight() + 4);
        extensionsTable.setIntercellSpacing(new Dimension(0, 0));
        // set the color of the table's JViewport
        extensionsTable.getParent().setBackground(extensionsTable.getBackground());
        extensionsTable.getColumnModel().getColumn(0).setMaxWidth(30);
    }

    private void activateExtension() {
        if (currentExtension != null) {
            currentExtension.removeChangeListener(this);
        }

        if (extensionsTable.getSelectedRow() == -1) {
            configPanelHolder.removeAll();
            configPanelHolder.repaint();
            configPanelHolder.revalidate();
        } else {
            ExtensionModelItem item = model.getItem(extensionsTable.getSelectedRow());

            configPanelHolder.removeAll();
            currentExtension = item.getProvider();
            currentExtension.addChangeListener(this);
            JPanel component = currentExtension.getConfigPanel();
            if (component != null) {
                configPanelHolder.add(component, BorderLayout.CENTER);
                enableComponents(component, item.isSelected());
            }
            configPanelHolder.revalidate();
            configPanelHolder.repaint();

        }

        fireChange();
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

    private void enableComponents(Container root, boolean enabled) {
        root.setEnabled(enabled);
        for (Component child : root.getComponents()) {
            if (child instanceof Container) {
                enableComponents((Container) child, enabled);
            } else {
                child.setEnabled(enabled);
            }
        }
    }

    public YiiProjectConfiguration getProjectConfiguration() {
        return projectConfig;
    }

    @Override
    public void addNotify() {
        YiiOptions.getInstance().addChangeListener(this);
        super.addNotify();
    }

    @Override
    public void removeNotify() {
        YiiOptions.getInstance().removeChangeListener(this);
        super.removeNotify();
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public String getErrorMessage() {
        return null;
    }

    public String getWarningMessage() {
        return null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        optionsLabel = new javax.swing.JLabel();
        extensionsScrollPanel = new javax.swing.JScrollPane();
        extensionsTable = new javax.swing.JTable();
        configPanelHolder = new javax.swing.JPanel();

        optionsLabel.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.optionsLabel.text")); // NOI18N
        optionsLabel.setToolTipText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.optionsLabel.toolTipText")); // NOI18N
        optionsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                optionsLabelMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                optionsLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(424, Short.MAX_VALUE)
                .addComponent(optionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(optionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        extensionsScrollPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        extensionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        extensionsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        extensionsTable.setShowHorizontalLines(false);
        extensionsTable.setShowVerticalLines(false);
        extensionsTable.setTableHeader(null);
        extensionsScrollPanel.setViewportView(extensionsTable);

        configPanelHolder.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        configPanelHolder.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(extensionsScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(configPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(extensionsScrollPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addComponent(configPanelHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void optionsLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optionsLabelMousePressed
        OptionsDisplayer.getDefault().open(YiiScript.getOptionsPath());
}//GEN-LAST:event_optionsLabelMousePressed

    private void optionsLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optionsLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_optionsLabelMouseEntered
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel configPanelHolder;
    private javax.swing.JScrollPane extensionsScrollPanel;
    private javax.swing.JTable extensionsTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel optionsLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void stateChanged(ChangeEvent e) {
        fireChange();
    }

    private void createExtensionsList() {
        for (YiiExtensionProvider item : YiiExtensions.getExtensions()) {
            model.addItem(new ExtensionModelItem(item));
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        activateExtension();
    }

    private static final class ExtensionTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 8082636013224696L;
        private final DefaultListModel model;

        public ExtensionTableModel() {
            model = new DefaultListModel();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return model.size();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return YiiExtensionProvider.class;
                default:
                    assert false : "Unknown column index: " + columnIndex;
                    break;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public Object getValueAt(int row, int column) {
            ExtensionModelItem item = getItem(row);
            switch (column) {
                case 0:
                    return item.isSelected();
                case 1:
                    return item.getProvider().getName();
                default:
                    assert false : "Unknown column index: " + column;
                    break;
            }
            return "";
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            ExtensionModelItem item = getItem(row);
            switch (column) {
                case 0:
                    item.setSelected((Boolean) value);
                    break;
                case 1:
                    // nothing needed
                    break;
                default:
                    assert false : "Unknown column index: " + column;
                    break;
            }
            fireTableCellUpdated(row, column);
        }

        ExtensionModelItem getItem(int index) {
            return (ExtensionModelItem) model.get(index);
        }

        void addItem(ExtensionModelItem item) {
            model.addElement(item);
        }
    }

    private static final class ExtensionModelItem {

        private Boolean selected;
        private boolean valid = true;
        private YiiExtensionProvider provider;

        public ExtensionModelItem(YiiExtensionProvider provider) {
            assert provider != null;

            this.provider = provider;
            setSelected(Boolean.FALSE);
        }

        public YiiExtensionProvider getProvider() {
            return provider;
        }

        public Boolean isSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }
}
