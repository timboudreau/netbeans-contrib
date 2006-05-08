/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.codetemplatetools.actions;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class RegisterManager {
    public static final String[] REGISTERS = new String[] {
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
    };
    
    // Let a system property org.netbeans.modules.codetemplatetools.REGISTER_PREFIX override the prefix for register names
    public static final String REGISTER_PREFIX = System.getProperties().getProperty("org.netbeans.modules.codetemplatetools.REGISTER_PREFIX", "_REG_");
    
    private static String lastUsedRegister = "0";
    
    public static String promptRegister(String prompt) {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.add(new JLabel(prompt), BorderLayout.NORTH);
        JComboBox registersList = new JComboBox(REGISTERS) {
            public void addNotify() {
                super.addNotify();
                SwingUtilities.invokeLater(new Runnable() { public void run() {requestFocus(); }});
            }
        };
        registersList.setSelectedItem(lastUsedRegister);
        panel.add(registersList, BorderLayout.SOUTH);
        if (JOptionPane.showConfirmDialog(
                WindowManager.getDefault().getMainWindow(),
                panel,
                "Select a Register",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {       
            lastUsedRegister = (String) registersList.getSelectedItem();
            return lastUsedRegister;
        }
        return null;
    }
    
    public static void cutToRegister(JEditorPane editorPane) {
        if (!editorPane.isEditable()) {
            beep();
            copyToRegister(editorPane);
            return;
        }
        
        String selection = editorPane.getSelectedText();
        
        if (selection == null) {
            beep();
            return;
        }
        
        String register = promptRegister("Cut to Register:");
        if (register == null) {
            return;
        }
        cutToRegister(editorPane, register);
    }
    
    public static void cutToRegister(JEditorPane editorPane, String register) {
        
        if (!editorPane.isEditable()) {
            beep();
            copyToRegister(editorPane, register);
            return;
        }
        
        String selection = editorPane.getSelectedText();
        
        if (selection == null) {
            beep();
            return;
        }
        
        setRegisterValue(editorPane, register, selection);
        
        editorPane.replaceSelection("");
    }
    
    public static void copyToRegister(JEditorPane editorPane) {
        String selection = editorPane.getSelectedText();
        
        if (selection == null) {
            beep();
            return;
        }
        
        String register = promptRegister("Copy to Register:");
        if (register == null) {
            return;
        }
        copyToRegister(editorPane, register);
    }
    
    public static void copyToRegister(JEditorPane editorPane, String register) {
        String selection = editorPane.getSelectedText();
        
        if (selection == null) {
            beep();
            return;
        }
        
        setRegisterValue(editorPane, register, selection);
    }
    
    public static void pasteFromRegister(JEditorPane editorPane) {
        if (!editorPane.isEditable()) {
            beep();
            return;
        }
        
        String register = promptRegister("Paste from Register:");
        if (register == null) {
            return;
        }
        
        pasteFromRegister(editorPane, register);
    }
    
    public static void pasteFromRegister(JEditorPane editorPane, String register) {
        if (!editorPane.isEditable()) {
            beep();
            return;
        }
        
        register = REGISTER_PREFIX + register;
        Document doc = editorPane.getDocument();
        CodeTemplateManager codeTemplateManager = CodeTemplateManager.get(doc);
        Collection codeTemplatesCollection = codeTemplateManager.getCodeTemplates();
        for (Iterator it = codeTemplatesCollection.iterator(); it.hasNext();) {
            CodeTemplate codeTemplate = (CodeTemplate) it.next();
            if (codeTemplate.getAbbreviation().equals(register)) {
                codeTemplate.insert(editorPane);
                break;
            }
        }        
    }
    
    private static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
        
    private static void setRegisterValue(JEditorPane editorPane, String register, String text) {
        Class kitClass = editorPane.getEditorKit().getClass();
        BaseOptions baseOptions = (BaseOptions) BaseOptions.getOptions(kitClass);
        if (baseOptions == null) {
            beep();
            return;
        }
        Map abbreviationsMap = baseOptions.getAbbrevMap();
        if (abbreviationsMap == null) {
            abbreviationsMap = new HashMap();
        }
        abbreviationsMap.put(REGISTER_PREFIX + register, text);
        baseOptions.setAbbrevMap(abbreviationsMap);
    }
}
