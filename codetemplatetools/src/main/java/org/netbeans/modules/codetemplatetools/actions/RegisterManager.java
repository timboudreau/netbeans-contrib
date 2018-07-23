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
package org.netbeans.modules.codetemplatetools.actions;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.codetemplatetools.ui.view.CodeTemplateUtils;
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
        CodeTemplateUtils.saveTemplate(editorPane, REGISTER_PREFIX + register, text, true);        
    }
}
