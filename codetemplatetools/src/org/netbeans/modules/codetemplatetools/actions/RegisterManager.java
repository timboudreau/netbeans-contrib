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

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
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
    
    public static final String REGISTER_PREFIX = "REG_";
    
    private static String lastUsedRegister = "0";
    
    public static String promptRegister(String prompt) {
        String register = (String) JOptionPane.showInputDialog(
            WindowManager.getDefault().getMainWindow(),
            prompt,
            "Select Register",
            JOptionPane.PLAIN_MESSAGE,
            null,
            REGISTERS,
            lastUsedRegister
        );
        if (register != null) {
            lastUsedRegister = register;
        }
        return register;
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
        
        String register = promptRegister("Select the Register to Cut to:");
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
        
        String register = promptRegister("Select the Register to Copy to:");
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
        
        String register = promptRegister("Select the Register to Paste from:");
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
        
        String registerValue = getRegisterValue(editorPane, register);
        if (registerValue == null) {
            registerValue = "";
        }
        editorPane.replaceSelection(registerValue);
    }
    
    private static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
    
    private static String getRegisterValue(JEditorPane editorPane, String register) {
        Class kitClass = editorPane.getEditorKit().getClass();
        BaseOptions baseOptions = (BaseOptions) BaseOptions.getOptions(kitClass);
        if (baseOptions == null) {
            beep();
            return null;
        }
        
        Map abbreviationsMap = baseOptions.getAbbrevMap();
        if (abbreviationsMap == null) {
            return "";
        }
        return (String) abbreviationsMap.get(REGISTER_PREFIX + register);
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
