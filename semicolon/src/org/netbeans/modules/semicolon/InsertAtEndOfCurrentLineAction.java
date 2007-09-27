/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s): */
package org.netbeans.modules.semicolon;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Generic action to insert at end of lines
 *
 * @author Tim Boudreau
 */
public class InsertAtEndOfCurrentLineAction extends AbstractAction {
    protected final String toInsert;
    protected final boolean addNewlineAndPositionCaret;
    protected final String bundleKey;
    
    public InsertAtEndOfCurrentLineAction (String toInsert, 
            boolean addNewlineAndPositionCaret, String bundleKey) {
        this.toInsert = toInsert;
        this.addNewlineAndPositionCaret = addNewlineAndPositionCaret;
        this.bundleKey = bundleKey;
        putValue("noIconInMenu", Boolean.TRUE); //NOI18N
    }
    
    public void actionPerformed (ActionEvent ae) {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (c instanceof JTextComponent) {
            JTextComponent jtc = (JTextComponent) c;
            try {
                int pos = jtc.getSelectionEnd();
                Document d = jtc.getDocument();
                int insertAt = -1;
                int len = d.getLength();
                for (int i = 0; i < len; i++) {
                    char curr = d.getText(pos, 1).charAt(0);
                    if (curr == '\n') { //NOI18N
                        insertAt = pos;
                        break;
                    }
                    pos += 1;
                }
                int lineStart = jtc.getSelectionEnd();
                int firstNonWsChar = lineStart;
                for (int i = lineStart; i >= 0; i--) {
                    char curr = d.getText(i, 1).charAt(0);
                    if (!Character.isWhitespace(curr)) {
                        firstNonWsChar = i;
                    }
                    if (curr == '\n') {
                        lineStart = i + 1;
                        break;
                    }
                }
                int whitespaceToInsert = firstNonWsChar - lineStart;
                if (insertAt >= 0) {
                    String insert = addNewlineAndPositionCaret ? toInsert + '\n'
                            : toInsert;
                    if (whitespaceToInsert > 0) {
                        char[] cc = new char[whitespaceToInsert];
                        Arrays.fill (cc, ' ');
                        insert += new String(cc);
                    }
                    d.insertString(insertAt, insert, null); //NOI18N
                    int caretPos = insertAt + insert.length();
                    if (addNewlineAndPositionCaret && caretPos < d.getLength()) {
                        jtc.setSelectionStart (caretPos);
                    }
                }
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify (e);
            }
        }
    }
    
    public boolean isEnabled() {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return c instanceof JTextComponent;
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
    
    public String getName() {
        return NbBundle.getMessage(InsertAtEndOfCurrentLineAction.class, 
                bundleKey);
    }
    
    public static InsertAtEndOfCurrentLineAction createSemicolonAction() {
        return new InsertAtEndOfCurrentLineAction (";", false, "CTL_LineCompletionAction"); //NOI18N
    }
    
    public static InsertAtEndOfCurrentLineAction createSemicolonNewlineAction() {
        return new InsertAtEndOfCurrentLineAction (";", true, "CTL_LineCompletionActionNL"); //NOI18N
    }
    
    public static InsertAtEndOfCurrentLineAction createOpenBraceAction() {
        return new InsertAtEndOfCurrentLineAction ("{", true, "CTL_OpenBraceAction"); //NOI18N
    }
    
    public static InsertAtEndOfCurrentLineAction createAddI18nAction() {
        return new InsertAtEndOfCurrentLineAction (" //NOI18N", false, "CTL_AddI18nHint"); //NOI18N
    }
}
