/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.readonlyeditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseAction;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.NbPreferences;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;

/**
 * This action toggle the <code>editable</code> property of the text editors.
 * 
 * @author Sandip V. Chitale (sandip.chitale@sun.com)
 */
public class ToggleReadOnlyAction extends BaseAction implements Presenter.Toolbar {
    private static Icon READONLY_ICON = new ImageIcon(ToggleReadOnlyAction.class.getResource("readonly.png")); // NOI18N
    private static Icon WRITABLE_ICON = new ImageIcon(ToggleReadOnlyAction.class.getResource("writable.png")); // NOI18N

    private JButton toggleReadOnlyButton;

    public ToggleReadOnlyAction() {
        super("toggle-read-only"); // NOI18N      
    }

    public void actionPerformed(ActionEvent evt, JTextComponent target) {
        if (target != null) {
            target.setEditable(!target.isEditable());
            target.getCaret().setVisible(true);
            adjustLabel(target);
        }
    }

    public Component getToolbarPresenter() {
        toggleReadOnlyButton = new JButton(this) {
            @Override
            public void addNotify() {
                super.addNotify();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        TopComponent activatedTopComponent = (TopComponent) SwingUtilities.getAncestorOfClass(TopComponent.class, toggleReadOnlyButton);
                        if (activatedTopComponent == null) {
                            return;
                        }                        
                        Node[] activatedNodes = activatedTopComponent.getActivatedNodes();
                        if (activatedNodes.length == 1) {
                            EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
                            if (ec != null) {
                                JEditorPane[] panes = ec.getOpenedPanes();
                                if (panes != null && panes.length > 0) {
                                    final JTextComponent target = panes[0];
                                    boolean openEditorsInReadOnlyMode = 
                                            NbPreferences.forModule(ReadOnlyEditorPanel.class).getBoolean("openEditorsInReadOnlyMode", false);
                                    target.setEditable(!openEditorsInReadOnlyMode);
                                    target.getCaret().setVisible(true);
                                    adjustLabel(target);
                                    activatedTopComponent.registerKeyboardAction( 
                                            new ActionListener() {
                                                public void actionPerformed(ActionEvent e) {                                              
                                                    target.setEditable(!target.isEditable());
                                                    target.getCaret().setVisible(true);
                                                    adjustLabel(target);
                                                }
                                            }, 
                                            KeyStroke.getKeyStroke(System.getProperty("toggle-read-only", "control released R")),
                                            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                                }
                            }
                        }
                    }
                });
                
            }            
        };
        
        toggleReadOnlyButton.setText("");
        boolean openEditorsInReadOnlyMode = NbPreferences.forModule(ReadOnlyEditorPanel.class).getBoolean("openEditorsInReadOnlyMode", false);
        toggleReadOnlyButton.setIcon(openEditorsInReadOnlyMode ? READONLY_ICON : WRITABLE_ICON);
        toggleReadOnlyButton.setToolTipText("Make " + (openEditorsInReadOnlyMode ? "Writable" : "Readonly")); // NOI18N
        return toggleReadOnlyButton;
    }

    private void adjustLabel(JTextComponent target) {
        if (target != null && toggleReadOnlyButton != null) {
            toggleReadOnlyButton.setIcon((target.isEditable() ? WRITABLE_ICON : READONLY_ICON));
            toggleReadOnlyButton.setToolTipText("Make " + (target.isEditable() ? "Readonly" : "Writable")); // NOI18N
        }
    }
}
