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
package org.netbeans.modules.codetemplatetools.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.codetemplatetools.SelectionCodeTemplateProcessor;
import org.openide.awt.DynamicMenuContent;
import org.openide.awt.Mnemonics;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.netbeans.editor.Registry;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.codetemplatetools.ui.view.Icons;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class SurroundWithAction extends CookieAction {
    private static String selectionParameterString = "${" + SelectionCodeTemplateProcessor.SELECTION_PARAMETER;
    boolean isForSelection(CodeTemplate codeTemplate) {
        return codeTemplate.getParametrizedText().indexOf(selectionParameterString) != -1;
    }
    
    private SurroundWithTemplatesMenu surroundWithTemplatesMenu;
    
    protected void performAction(Node[] activatedNodes) {
    }
    
    public JMenuItem getMenuPresenter() {
        JMenu menu = new SurroundWithTemplatesMenu();
        String label = NbBundle.getMessage(SurroundWithAction.class, "CTL_SurroundWithAction");
        Mnemonics.setLocalizedText(menu, label);
        return menu;        
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(SurroundWithAction.class, "CTL_SurroundWithAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            EditorCookie.class
        };
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    private class SurroundWithTemplatesMenu extends JMenu implements DynamicMenuContent {
        public JComponent[] getMenuPresenters() {
            // Clean
            removeAll();

            JTextComponent textComponent = Registry.getMostActiveComponent();
            if (textComponent != null) {
                Document document = textComponent.getDocument();
                CodeTemplateManager codeTemplateManager = CodeTemplateManager.get(document);
                if (codeTemplateManager != null) {
                    Collection codeTemplatesCollection = codeTemplateManager.getCodeTemplates();
                    CodeTemplate[] codeTemplates = (CodeTemplate[]) codeTemplatesCollection.toArray(new CodeTemplate[0]);
                    for (int i = 0; i < codeTemplates.length; i++) {
                        CodeTemplate codeTemplate = codeTemplates[i];
                        if (isForSelection(codeTemplate)) {
                            add(new InsertCodeTemplateAction(textComponent, codeTemplate));
                        }
                    }
                }
            }
            return new JComponent[] {this};
        }

        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }
    }
    
    private static class InsertCodeTemplateAction extends AbstractAction {
        private JTextComponent textComponent;
        private CodeTemplate codeTemplate;
        
        InsertCodeTemplateAction(JTextComponent textComponent, CodeTemplate codeTemplate) {
            super(codeTemplate.getAbbreviation(), Icons.TEMPLATE_FOR_SELECTION_ICON);
            this.textComponent = textComponent;
            this.codeTemplate = codeTemplate;
            setEnabled(textComponent.isEditable() && textComponent.getSelectedText() != null);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (textComponent.isEditable()) {
                codeTemplate.insert(textComponent);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}

