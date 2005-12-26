/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.codetemplatetools.ui.view;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class CodeTemplatesPanel extends javax.swing.JPanel {
    
    public static void promptAndInsertCodeTemplate(JEditorPane editorPane) {
        JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(),
        "Templates",
        true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new CodeTemplatesPanel(editorPane));
        dialog.setBounds(200,200, 600, 450);
        dialog.setVisible(true);
        
    }
    
    private JEditorPane editorPane;
    
    /** Creates new form CodeTemplatesPanel */
    public CodeTemplatesPanel(JEditorPane editorPane) {
        initComponents();
        this.editorPane = editorPane;
        loadModel();
        mimeTypeLabel.setText("Mime type: " + editorPane.getContentType());
        templatesList.setCellRenderer(new CodeTemplateListCellRenderer());
        templatesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                CodeTemplate selectedCodeTemplate = (CodeTemplate) templatesList.getSelectedValue();
                showCodeTemplate(selectedCodeTemplate);
                adjustButtonState();
            }
        });
        
        templateTextEditorPane.setContentType(editorPane.getContentType());
        
        insertButon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertTemplate();
            }
        });
        
        newButton.setIcon(Icons.NEW_TEMPLATE_ICON);
        
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CreateCodeTemplatePanel.createCodeTemplate(CodeTemplatesPanel.this.editorPane);
                // New templates may have been added.
                loadModel();
            }
        });
        
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CreateCodeTemplatePanel.modifyCodeTemplate(CodeTemplatesPanel.this.editorPane, (CodeTemplate) templatesList.getSelectedValue());
                // Templates may have been modified.
                loadModel();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTemplate((CodeTemplate) templatesList.getSelectedValue());
            }
        });        
        
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        adjustButtonState();
    }
    
    public void addNotify() {
        super.addNotify();
        
        SwingUtilities.getRootPane(this).setDefaultButton(insertButon);
    }
    
    private void loadModel() {
        Document doc = editorPane.getDocument();
        CodeTemplateManager codeTemplateManager = CodeTemplateManager.get(doc);
        Collection codeTemplatesCollection = codeTemplateManager.getCodeTemplates();
        CodeTemplate[] codeTemplates = (CodeTemplate[]) codeTemplatesCollection.toArray(new CodeTemplate[0]);
        CodeTemplateListModel codeTemplateListModel = new CodeTemplateListModel(codeTemplates);
        templatesList.setModel(codeTemplateListModel);
    }
    
    private void insertTemplate() {
        CodeTemplate selectedCodeTemplate = (CodeTemplate) templatesList.getSelectedValue();
        if (selectedCodeTemplate != null) {
            selectedCodeTemplate.insert(editorPane);
        }
        done();
    }
    
    private void deleteTemplate(CodeTemplate template) {
        if (template == null) {
            return;
        }
        String templateName = template.getAbbreviation();
        if (templateName == null || templateName.length() == 0) {
            return;
        }
        if  (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                    "Delete Code Template : " + templateName + " ?",
                    "Delete Code Template",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        
        Class kitClass = editorPane.getEditorKit().getClass();
        BaseOptions baseOptions = (BaseOptions) BaseOptions.getOptions(kitClass);
        Map abbreviationsMap = baseOptions.getAbbrevMap();
        if (abbreviationsMap == null) {
            // ?
            return;
        } 
        abbreviationsMap.remove(templateName);
        baseOptions.setAbbrevMap(abbreviationsMap);
        loadModel();
    }
    
    private void close() {
        done();
    }
    
    private void done() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.setVisible(false);
            window.dispose();
        }
    }
    
    private void adjustButtonState() {
        insertButon.setEnabled(editorPane.isEditable() && templatesList.getSelectedIndex() != -1);
        deleteButton.setEnabled(templatesList.getSelectedIndex() != -1);
        modifyButton.setEnabled(templatesList.getSelectedIndex() != -1);
    }
    
    private void showCodeTemplate(CodeTemplate codeTemplate) {
        if (codeTemplate == null) {
            templateTextEditorPane.setText("");
        } else {
            templateTextEditorPane.setText(codeTemplate.getParametrizedText());
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        templatesLabel = new javax.swing.JLabel();
        mimeTypeLabel = new javax.swing.JLabel();
        templatesScrollPane = new javax.swing.JScrollPane();
        templatesList = new javax.swing.JList();
        newButton = new javax.swing.JButton();
        modifyButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        templateTextLabel = new javax.swing.JLabel();
        templateTextScrollPane = new javax.swing.JScrollPane();
        templateTextEditorPane = new javax.swing.JEditorPane();
        buttonsPanel = new javax.swing.JPanel();
        insertButon = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        templatesLabel.setDisplayedMnemonic('s');
        templatesLabel.setText("Templates");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templatesLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(mimeTypeLabel, gridBagConstraints);

        templatesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        templatesScrollPane.setViewportView(templatesList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(templatesScrollPane, gridBagConstraints);

        newButton.setMnemonic('N');
        newButton.setText("New...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(newButton, gridBagConstraints);

        modifyButton.setMnemonic('M');
        modifyButton.setText("Modify...");
        modifyButton.setToolTipText("Modify selected template");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(modifyButton, gridBagConstraints);

        deleteButton.setMnemonic('D');
        deleteButton.setText("Delete...");
        deleteButton.setToolTipText("Delete selected template");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(deleteButton, gridBagConstraints);

        templateTextLabel.setDisplayedMnemonic('T');
        templateTextLabel.setText("Template Text");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateTextLabel, gridBagConstraints);

        templateTextEditorPane.setEditable(false);
        templateTextScrollPane.setViewportView(templateTextEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateTextScrollPane, gridBagConstraints);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        insertButon.setMnemonic('I');
        insertButon.setText("Insert");
        insertButon.setToolTipText("Insert selected template");
        buttonsPanel.add(insertButon);

        closeButton.setMnemonic('C');
        closeButton.setText("Close");
        buttonsPanel.add(closeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(buttonsPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton insertButon;
    private javax.swing.JLabel mimeTypeLabel;
    private javax.swing.JButton modifyButton;
    private javax.swing.JButton newButton;
    private javax.swing.JEditorPane templateTextEditorPane;
    private javax.swing.JLabel templateTextLabel;
    private javax.swing.JScrollPane templateTextScrollPane;
    private javax.swing.JLabel templatesLabel;
    private javax.swing.JList templatesList;
    private javax.swing.JScrollPane templatesScrollPane;
    // End of variables declaration//GEN-END:variables
    
    private static class CodeTemplateListModel extends AbstractListModel {
        private CodeTemplate[] codeTemplates;
        
        CodeTemplateListModel(CodeTemplate[] codeTemplates) {
            this.codeTemplates = codeTemplates;
        }
        
        public int getSize() {
            return codeTemplates.length;
        }
        
        public Object getElementAt(int index) {
            return codeTemplates[index];
        }
    }
}
