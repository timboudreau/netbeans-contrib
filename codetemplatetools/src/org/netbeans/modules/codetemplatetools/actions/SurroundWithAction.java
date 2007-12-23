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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.modules.codetemplatetools.SelectionCodeTemplateProcessor;
import org.netbeans.modules.codetemplatetools.ui.view.CodeTemplateListCellRenderer;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.CookieAction;
import org.netbeans.editor.Registry;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.codetemplatetools.ui.view.CodeTemplateListModel;
import org.openide.windows.WindowManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class SurroundWithAction extends CookieAction {
    private static String selectionParameterString = "${" + SelectionCodeTemplateProcessor.SELECTION_PARAMETER;

    boolean isForSelection(CodeTemplate codeTemplate) {
        return codeTemplate.getParametrizedText().indexOf(selectionParameterString) != -1;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        final JTextComponent textComponent = Registry.getMostActiveComponent();
        if (textComponent != null) {
            Document document = textComponent.getDocument();
            CodeTemplateManager codeTemplateManager = CodeTemplateManager.get(document);
            if (codeTemplateManager != null) {
                Collection codeTemplatesCollection = codeTemplateManager.getCodeTemplates();
               Set<CodeTemplate> selectionCodeTemplates = new LinkedHashSet<CodeTemplate>();
                if (textComponent.getSelectedText() == null) {
                    selectionCodeTemplates.addAll(codeTemplatesCollection);
                } else {
                    for (Object ct : codeTemplatesCollection) {
                        CodeTemplate codeTemplate = (CodeTemplate) ct;
                        if (isForSelection(codeTemplate)) {
                            selectionCodeTemplates.add(codeTemplate);
                        }
                    }                    
                    selectionCodeTemplates.addAll(codeTemplatesCollection);
                }
                if (selectionCodeTemplates.size() > 0) {
                    CodeTemplate[] selectionCodeTemplatesArray = selectionCodeTemplates.toArray(new CodeTemplate[0]);
                    final JList templatesList = new JList(){
                        @Override
                        public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
                            ListModel listModel = getModel();
                            if (listModel instanceof CodeTemplateListModel) {
                                prefix = prefix.toLowerCase();
                                CodeTemplateListModel codeTemplateListModel = (CodeTemplateListModel) listModel;
                                int size = codeTemplateListModel.getSize();
                                int select = -1;
                                for (int i = startIndex; i < size; i++) {
                                    CodeTemplate codeTemplate = (CodeTemplate) codeTemplateListModel.getElementAt(i);
                                    if (codeTemplate.getAbbreviation().toLowerCase().startsWith(prefix)) {
                                        select = i;
                                        break;
                                    }
                                }
                                if (select == -1) {
                                    for (int i = 0; i < startIndex; i++) {
                                        CodeTemplate codeTemplate = (CodeTemplate) codeTemplateListModel.getElementAt(i);
                                        if (codeTemplate.getAbbreviation().toLowerCase().startsWith(prefix)) {
                                            select = i;
                                            break;
                                        }
                                    }
                                }
                                if (select != -1) {
                                    return select;
                                }
                            }
                            Toolkit.getDefaultToolkit().beep();
                            return -1;
                        }
                    };
                    
                    templatesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    final CodeTemplateListModel codeTemplatesListModel = new CodeTemplateListModel(selectionCodeTemplatesArray);
                    templatesList.setModel(codeTemplatesListModel);
                    templatesList.setCellRenderer(new CodeTemplateListCellRenderer());

                    final JDialog dialog = new JDialog(WindowManager.getDefault().getMainWindow(),
                            "Select a template to insert",
                            true) {
                        @Override
                        public void addNotify() {
                            super.addNotify();
                            templatesList.requestFocusInWindow();
                        }
                    };                    
                    dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                    dialog.setContentPane(new JScrollPane(templatesList));                    
                    templatesList.registerKeyboardAction(
                            new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    dialog.setVisible(false);
                                    CodeTemplate codeTemplate = (CodeTemplate) templatesList.getSelectedValue();
                                    if (codeTemplate != null) {
                                        codeTemplate.insert(textComponent);
                                    }
                                }
                            }
                            ,KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false)
                            ,JComponent.WHEN_FOCUSED);
                    templatesList.registerKeyboardAction(
                            new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    dialog.setVisible(false);
                                }
                            }
                            ,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false)
                            ,JComponent.WHEN_FOCUSED);
                            
                    templatesList.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() == 2) {
                                dialog.setVisible(false);
                                CodeTemplate codeTemplate = (CodeTemplate) templatesList.getSelectedValue();
                                if (codeTemplate != null) {
                                    codeTemplate.insert(textComponent);
                                }
                            }
                        }
                    });   
                    dialog.setSize(300, 250);
                    dialog.setBounds(Utilities.findCenterBounds(dialog.getSize()));
                    dialog.setVisible(true);
                }
            }
        }
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

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

