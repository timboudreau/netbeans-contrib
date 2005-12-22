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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.windows.WindowManager;

/**
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class CreateCodeTemplatePanel extends javax.swing.JPanel {
    
    public static void createCodeTemplate(JEditorPane editorPane) {
        JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(),
                "Create Template",
                true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new CreateCodeTemplatePanel(editorPane));
        dialog.setBounds(200,200, 400, 450);
        dialog.setVisible(true);
    }
    
    private JEditorPane editorPane;
    
    /** Creates new form CreateCodeTemplatePanel */
    public CreateCodeTemplatePanel(JEditorPane editorPane) {
        initComponents();
        this.editorPane = editorPane;                
        
        templateTextEditorPane.setContentType(editorPane.getContentType());
        templateTextEditorPane.setText(editorPane.getSelectedText());
        
        showTemplatesButton.setIcon(Icons.SHOW_TEMPLATES_ICON);
        
        showTemplatesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CodeTemplatesPanel.promptAndInsertCodeTemplate(CreateCodeTemplatePanel.this.editorPane);
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTemplate();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        
        templateNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                adjustButtonState();
            }
            public void insertUpdate(DocumentEvent e) {
                adjustButtonState();
            }
            public void removeUpdate(DocumentEvent e) {
                adjustButtonState();
            }
        });
        
        templateTextEditorPane.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                adjustButtonState();
            }
            public void insertUpdate(DocumentEvent e) {
                adjustButtonState();
            }
            public void removeUpdate(DocumentEvent e) {
                adjustButtonState();
            }
        });
        
        adjustButtonState();
    }
    
    public void addNotify() {
        super.addNotify();
        
        SwingUtilities.getRootPane(this).setDefaultButton(saveButton);
    }
    
    private void saveTemplate() {
        try {
            String templateName = templateNameTextField.getText().trim();
            String templateText = templateTextEditorPane.getText();
            if (templateName.length() == 0) {
                return;
            }
            Class kitClass = editorPane.getEditorKit().getClass();
            BaseOptions baseOptions = (BaseOptions) BaseOptions.getOptions(kitClass);
            Map abbreviationsMap = baseOptions.getAbbrevMap();
            if (abbreviationsMap == null) {
                abbreviationsMap = new HashMap();
            } else {
                String existingTemplateText = (String) abbreviationsMap.get(templateName);
                if (existingTemplateText != null) {
                    if  (JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),
                            "Code Template " + templateName + " already exists. Overwrite?",
                            "Overwrite exiting Code Template",
                            JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                        return;
                    }
                    // fall through
                }
            }
            abbreviationsMap.put(templateName, templateText);
            baseOptions.setAbbrevMap(abbreviationsMap);
        } finally {
            done();
        }
    }
    
    private void cancel() {
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
        saveButton.setEnabled(templateNameTextField.getText().trim().length() > 0 && templateTextEditorPane.getText().length() > 0);
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

        templateNameLabel = new javax.swing.JLabel();
        templateNameTextField = new javax.swing.JTextField();
        templateTextLabel = new javax.swing.JLabel();
        templateTextScrollPane = new javax.swing.JScrollPane();
        templateTextEditorPane = new javax.swing.JEditorPane();
        buttonsPanel = new javax.swing.JPanel();
        showTemplatesButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        templateNameLabel.setDisplayedMnemonic('N');
        templateNameLabel.setText("Template Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateNameTextField, gridBagConstraints);

        templateTextLabel.setDisplayedMnemonic('T');
        templateTextLabel.setText("Template Text (Use ${<param-name>} to create parameters)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateTextLabel, gridBagConstraints);

        templateTextScrollPane.setViewportView(templateTextEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateTextScrollPane, gridBagConstraints);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        showTemplatesButton.setMnemonic('w');
        showTemplatesButton.setText("Show Templates...");
        buttonsPanel.add(showTemplatesButton);

        saveButton.setMnemonic('S');
        saveButton.setText("Save");
        saveButton.setToolTipText("Save Code Template");
        buttonsPanel.add(saveButton);

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        buttonsPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(buttonsPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton showTemplatesButton;
    private javax.swing.JLabel templateNameLabel;
    private javax.swing.JTextField templateNameTextField;
    private javax.swing.JEditorPane templateTextEditorPane;
    private javax.swing.JLabel templateTextLabel;
    private javax.swing.JScrollPane templateTextScrollPane;
    // End of variables declaration//GEN-END:variables
}
