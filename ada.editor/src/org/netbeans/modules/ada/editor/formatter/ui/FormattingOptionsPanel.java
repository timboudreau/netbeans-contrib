/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.ada.editor.formatter.ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import org.netbeans.modules.ada.editor.AdaMimeResolver;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

/**
 *
 * @author  phrebejk
 */
public class FormattingOptionsPanel extends JPanel implements ActionListener, PropertyChangeListener {
    
    private List<FormattingOptionsPanel.Category> categories = new ArrayList<FormattingOptionsPanel.Category>();
    
    private boolean loaded = false;
    
    public FormattingOptionsPanel() {        
        initComponents();
        
        previewPane.setContentType(AdaMimeResolver.MIME_TYPE); // NOI18N
        // Don't highlight caret row 
        previewPane.putClientProperty(
            "HighlightsLayerExcludes", // NOI18N
            "^org\\.netbeans\\.modules\\.editor\\.lib2\\.highlighting\\.CaretRowHighlighting$" // NOI18N
        );
        previewPane.setText("1234567890123456789012345678901234567890"); // NOI18N
        previewPane.setDoubleBuffered(true);
        createCategories();
        
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Category category : categories) {
            model.addElement(category);
        }
        categoryCombo.setModel(model);
    
        categoryCombo.addActionListener(this);
        actionPerformed(new ActionEvent(model, 0, null));
    }
    
    public void load() {
        loaded = false;
        for (Category category : categories) {
            category.update();
        }
        loaded = true;
        repaintPreview();        
    }
    
    void store() {
        for (Category category : categories) {
            category.applyChanges();
        }
        FmtOptions.flush();
        FmtOptions.lastValues = null;
    }
    
    void cancel() {
        FmtOptions.lastValues = null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        optionsPanel = new javax.swing.JPanel();
        categoryLabel = new javax.swing.JLabel();
        categoryCombo = new javax.swing.JComboBox();
        categoryPanel = new javax.swing.JPanel();
        previewPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        previewPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setBorder(null);
        jSplitPane1.setOpaque(false);

        optionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 8));
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new java.awt.GridBagLayout());

        categoryLabel.setLabelFor(categoryCombo);
        org.openide.awt.Mnemonics.setLocalizedText(categoryLabel, org.openide.util.NbBundle.getMessage(FormattingOptionsPanel.class, "LBL_Category")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 6);
        optionsPanel.add(categoryLabel, gridBagConstraints);

        categoryCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        optionsPanel.add(categoryCombo, gridBagConstraints);

        categoryPanel.setOpaque(false);
        categoryPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        optionsPanel.add(categoryPanel, gridBagConstraints);

        jSplitPane1.setLeftComponent(optionsPanel);

        previewPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 0));
        previewPanel.setOpaque(false);
        previewPanel.setPreferredSize(new java.awt.Dimension(300, 20));
        previewPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setDoubleBuffered(true);

        previewPane.setEditable(false);
        jScrollPane1.setViewportView(previewPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        previewPanel.add(jScrollPane1, gridBagConstraints);

        jSplitPane1.setRightComponent(previewPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        add(jSplitPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox categoryCombo;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JEditorPane previewPane;
    private javax.swing.JPanel previewPanel;
    // End of variables declaration//GEN-END:variables
 
    
    private void createCategories() {
        categories.add(FmtTabsIndents.getController());
        // TODO - other categories?
        
        for (Category category : categories) {
            category.addPropertyChangeListener(this);
        }

        
    }
    
    // Change in the combo
    public void actionPerformed(ActionEvent e) {
        Category category = (Category)categoryCombo.getSelectedItem();
        categoryPanel.setVisible(false);
        categoryPanel.removeAll();
        categoryPanel.add(category.getComponent(null), BorderLayout.CENTER);
        categoryPanel.setVisible(true);
        System.out.println("PIPPO3" + loaded);
        if (loaded) {
            repaintPreview();
        }
    }
        
    public static abstract class Category extends OptionsPanelController {
        
        private String displayName;
        
        public Category(String displayNameKey) {
            this.displayName = NbBundle.getMessage( FormattingOptionsPanel.class, displayNameKey  );
        }

        public abstract void storeTo(Preferences preferences);
        
        public abstract void refreshPreview(JEditorPane pane, Preferences p);
        
        @Override
        public String toString() {
            return displayName;
        }
        
        
    }

    // Change in some of the subpanels
    public void propertyChange(PropertyChangeEvent evt) {
        
        if ( !loaded ) {
            return;
        }
                
        // Notify the main controler that the page has changed
//        fopControler.changed();
        
        // Repaint the preview
        repaintPreview();
    }

    // XXX Only temporary
    
    
    private void repaintPreview() { 
        
        Preferences p = new PreviewPreferences();
        
        for (Category category : categories) {
            category.storeTo(p);
        }
        
        Category category = (Category)categoryCombo.getSelectedItem(); 
        jScrollPane1.setIgnoreRepaint(true);
        category.refreshPreview(previewPane, p);
        previewPane.setIgnoreRepaint(false);
        previewPane.scrollRectToVisible(new Rectangle(0,0,10,10) );
        previewPane.repaint(100);
        
        FmtOptions.lastValues = p;
        
    }
    
    public static class PreviewPreferences extends AbstractPreferences {
        
        private Map<String,Object> map = new HashMap<String, Object>();

        public PreviewPreferences() {
            super(null, ""); // NOI18N
        }
        
        protected void putSpi(String key, String value) {
            map.put(key, value);            
        }

        protected String getSpi(String key) {
            return (String)map.get(key);                    
        }

        protected void removeSpi(String key) {
            map.remove(key);
        }

        protected void removeNodeSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected String[] keysSpi() throws BackingStoreException {
            String array[] = new String[map.keySet().size()];
            return map.keySet().toArray( array );
        }

        protected String[] childrenNamesSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected AbstractPreferences childSpi(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void syncSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        protected void flushSpi() throws BackingStoreException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
}
