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

package org.netbeans.modules.regexphighlighter.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.text.NbDocument;
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
        regExpToolbar.add(new JSeparator());
        
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
