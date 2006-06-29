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

package org.netbeans.modules.codetemplatetools.ui.view;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.lib.editor.codetemplates.CodeTemplateInsertHandler;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.spi.CodeTemplateParameter;
import org.netbeans.modules.codetemplatetools.SelectionCodeTemplateProcessor;
import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.ErrorManager;
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
        dialog.setBounds(200,200, 600, 450);
        dialog.setVisible(true);
    }
    
    public static void modifyCodeTemplate(JEditorPane editorPane, CodeTemplate codeTemplate) {
        JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(),
        "Modify Template",
        true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new CreateCodeTemplatePanel(editorPane, codeTemplate));
        dialog.setBounds(200,200, 600, 450);
        dialog.setVisible(true);
    }
    
    private JEditorPane editorPane;
    
    private static String[] parameters = new String[] {
        "${" + CodeTemplateParameter.CURSOR_PARAMETER_NAME + "}",
        "${"+ SelectionCodeTemplateProcessor.SELECTION_PARAMETER + " " + CodeTemplateParameter.EDITABLE_HINT_NAME + "=false}",
        "${"+ SelectionCodeTemplateProcessor.CLIPBOARD_CONTENT_PARAMETER + " " + CodeTemplateParameter.EDITABLE_HINT_NAME + "=false}",
    };
    
    /** Creates new form CreateCodeTemplatePanel */
    public CreateCodeTemplatePanel(JEditorPane editorPane) {
        this(editorPane, null);
    }
    
    /** Creates new form CreateCodeTemplatePanel */
    public CreateCodeTemplatePanel(JEditorPane editorPane, CodeTemplate codeTemplate) {
        initComponents();
        this.editorPane = editorPane;
        
        mimeTypeLabel.setText("Mime type: " + editorPane.getContentType());
        
        templateTextEditorPane.setContentType(editorPane.getContentType());
        templateTextEditorPane.setText(editorPane.getSelectedText());
        
        templateTextEditorPane.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                parameterizeButton.setEnabled(templateTextEditorPane.getSelectedText() != null);
            }
        });        
        
        showTemplatesButton.setIcon(Icons.SHOW_TEMPLATES_ICON);
        
        showTemplatesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CodeTemplatesPanel.promptAndInsertCodeTemplate(CreateCodeTemplatePanel.this.editorPane);
            }
        });
        
        insertParameterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String parameter = (String) JOptionPane.showInputDialog(
                WindowManager.getDefault().getMainWindow(),
                "Select a parameter to insert:",
                "Insert parameter",
                JOptionPane.PLAIN_MESSAGE,
                null,
                parameters,
                parameters[0]);
                if (parameter != null) {
                    insertText(parameter);
                }
            }
        });
        
        parameterizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parameterize();
            }
        });
        
        if (codeTemplate == null) {
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveTemplate(false);
                }
            });    
        } else {
            templateNameTextField.setEditable(false);
            templateNameTextField.setText(codeTemplate.getAbbreviation());
            templateTextEditorPane.setText(codeTemplate.getParametrizedText());
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveTemplate(true);
                }
            });
        }
                        
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
        parameterizeButton.setEnabled(templateTextEditorPane.getSelectedText() != null);
    }
    
    public void addNotify() {
        super.addNotify();
        
        SwingUtilities.getRootPane(this).setDefaultButton(saveButton);
    }
    
    private void insertText(String text) {
        try {
            templateTextEditorPane.getDocument().insertString(templateTextEditorPane.getCaretPosition(), text, null);
        } catch (BadLocationException ble) {
            ErrorManager.getDefault().notify(ble);
        }
    }
    
    private void parameterize() {
        String selection = templateTextEditorPane.getSelectedText();
        if (selection == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        int start = Math.min(templateTextEditorPane.getSelectionStart(), templateTextEditorPane.getSelectionEnd());
        int end = Math.max(templateTextEditorPane.getSelectionStart(), templateTextEditorPane.getSelectionEnd());
        try {
            templateTextEditorPane.getDocument().remove(start, (end-start));
            templateTextEditorPane.getDocument().insertString(
                start,
                "${" + selection + "}",
                null);
        } catch (BadLocationException ble) {
            ErrorManager.getDefault().notify(ble);
        }
    }
    
    private void saveTemplate(boolean modifying) {
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
                if (!modifying) {
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
        mimeTypeLabel = new javax.swing.JLabel();
        templateNameTextField = new javax.swing.JTextField();
        templateTextLabel = new javax.swing.JLabel();
        templateTextScrollPane = new javax.swing.JScrollPane();
        templateTextEditorPane = new javax.swing.JEditorPane();
        buttonsPanel = new javax.swing.JPanel();
        showTemplatesButton = new javax.swing.JButton();
        insertParameterButton = new javax.swing.JButton();
        parameterizeButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        templateNameLabel.setDisplayedMnemonic('N');
        templateNameLabel.setText("Template Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(mimeTypeLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateNameTextField, gridBagConstraints);

        templateTextLabel.setDisplayedMnemonic('T');
        templateTextLabel.setText("Template Text (Use ${<param-name>} to create parameters)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateTextLabel, gridBagConstraints);

        templateTextScrollPane.setViewportView(templateTextEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(templateTextScrollPane, gridBagConstraints);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        showTemplatesButton.setMnemonic('w');
        showTemplatesButton.setText("Show Templates...");
        showTemplatesButton.setToolTipText("Show Templates...");
        buttonsPanel.add(showTemplatesButton);

        insertParameterButton.setMnemonic('I');
        insertParameterButton.setText("Insert ${}...");
        insertParameterButton.setToolTipText("Insert Parameter...");
        buttonsPanel.add(insertParameterButton);

        parameterizeButton.setMnemonic('P');
        parameterizeButton.setText("${...}");
        parameterizeButton.setToolTipText("Parameterize the selected text");
        buttonsPanel.add(parameterizeButton);

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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(buttonsPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton insertParameterButton;
    private javax.swing.JLabel mimeTypeLabel;
    private javax.swing.JButton parameterizeButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton showTemplatesButton;
    private javax.swing.JLabel templateNameLabel;
    private javax.swing.JTextField templateNameTextField;
    private javax.swing.JEditorPane templateTextEditorPane;
    private javax.swing.JLabel templateTextLabel;
    private javax.swing.JScrollPane templateTextScrollPane;
    // End of variables declaration//GEN-END:variables
}
