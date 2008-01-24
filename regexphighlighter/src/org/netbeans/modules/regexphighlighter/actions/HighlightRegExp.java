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

package org.netbeans.modules.regexphighlighter.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.Registry;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;

/**
 * The action to enable and disble highlihting of matching regular expressions in editor windows.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class HighlightRegExp extends BooleanStateAction implements PropertyChangeListener {
    private RegExpHighlighter regExpHighlighter = RegExpHighlighter.getDefault();
    
    private JTextComponent textComponent;
    private JTextField regExpTextField;
    private JToggleButton  matchCaseRegExpToggleButton;
    private JToggleButton  highlightGroupsToggleButton;
    private static Icon HIGHLIGHT_GROUPS_ICON = new ImageIcon(HighlightRegExp.class.getResource("resources/highlightgroups.gif"));
    private static Icon MATCHCASE_ICON        = new ImageIcon(HighlightRegExp.class.getResource("resources/matchcase.gif"));
        
    public HighlightRegExp() {
        addPropertyChangeListener(this);
        
        regExpTextField = new JTextField(15);
        regExpTextField.setToolTipText(NbBundle.getMessage(HighlightRegExp.class, "TOOLTIP_TypeRegExp"));
        regExpTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                processRegExp();
            }
            public void insertUpdate(DocumentEvent e) {
                processRegExp();
            }
            public void removeUpdate(DocumentEvent e) {
                processRegExp();
            }
        });
        
        regExpTextField.registerKeyboardAction(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JTextComponent textComponent = Registry.getMostActiveComponent();
                        if (textComponent == null) {
                            return;
                        }
                        
                        if (!textComponent.isEditable()) {
                            return;
                        }
                        
                        final int offset = textComponent.getCaretPosition();
                        if (offset != -1) {
                            final Document document = textComponent.getDocument();
                            try {
                                NbDocument.runAtomicAsUser((StyledDocument) document, new Runnable() {
                                    public void run() {
                                        try {
                                            document.insertString(offset, "\"" + 
                                                    regExpTextField.getText()
                                                    .replaceAll(Pattern.quote("\\"), Matcher.quoteReplacement("\\\\"))
                                                    .replaceAll(Pattern.quote("\""), Matcher.quoteReplacement("\\\""))
                                                    + "\"", null);
                                        } catch (BadLocationException ex) {
                                            Exceptions.printStackTrace(ex);
                                        }                                    
                                    }
                                });
                            } catch (BadLocationException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK, true),
                JComponent.WHEN_FOCUSED);
        
        highlightGroupsToggleButton = new JToggleButton(HIGHLIGHT_GROUPS_ICON, true);
        highlightGroupsToggleButton.setFocusPainted(false);
        highlightGroupsToggleButton.setMargin(new Insets(1,1,1,1));
        highlightGroupsToggleButton.setToolTipText(NbBundle.getMessage(HighlightRegExp.class, "TOOLTIP_HighlightGroups"));
        highlightGroupsToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                regExpHighlighter.setHighlightGroups(highlightGroupsToggleButton.isSelected());
            }
        });
        regExpHighlighter.setHighlightGroups(highlightGroupsToggleButton.isSelected());
        
        matchCaseRegExpToggleButton = new JToggleButton(MATCHCASE_ICON, false);
        matchCaseRegExpToggleButton.setFocusPainted(false);
        matchCaseRegExpToggleButton.setMargin(new Insets(1,1,1,1));
        matchCaseRegExpToggleButton.setToolTipText(NbBundle.getMessage(HighlightRegExp.class, "TOOLTIP_MatchCase"));
        matchCaseRegExpToggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                regExpHighlighter.setMatchCase(matchCaseRegExpToggleButton.isSelected());
            }
        });
    }
    
    protected void initialize() {
        super.initialize();
        setBooleanState(false);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROP_BOOLEAN_STATE)) {
            regExpHighlighter.setHighlight(getBooleanState());
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(HighlightRegExp.class, "CTL_HighlightRegExp"); // NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/regexphighlighter/actions/resources/highlightregexp.gif"; // NOI18N
    }
    
    public Component getToolbarPresenter() {
        JToolBar regExpToolbar = new JToolBar();
        regExpToolbar.setFloatable(false);
        
        regExpToolbar.add(regExpTextField);
        regExpToolbar.add(matchCaseRegExpToggleButton);
        regExpToolbar.add(highlightGroupsToggleButton);        
        
        Component highlightRegExpToggleButton;
        highlightRegExpToggleButton = super.getToolbarPresenter();
        if (highlightRegExpToggleButton instanceof AbstractButton) {
            ((AbstractButton)highlightRegExpToggleButton).setFocusPainted(false);
            ((AbstractButton)highlightRegExpToggleButton).setMargin(new Insets(1,1,1,1));
        }
        regExpToolbar.add(highlightRegExpToggleButton);                
        return regExpToolbar;
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    private void processRegExp() {
        regExpTextField.setForeground(UIManager.getColor("TextField.foreground"));
        String regExp = regExpTextField.getText();
        if (regExp.length() == 0) {
            regExpHighlighter.setRegExp(regExp);
        } else {
            try {
                regExpHighlighter.compileRegExp(regExp);
                regExpHighlighter.setRegExp(regExp);
            } catch (PatternSyntaxException pse) {
                regExpTextField.setForeground(Color.red);
                regExpHighlighter.setRegExp(null);
                return;
            }
        }
    }
}
