/* The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package org.netbeans.modules.semicolon;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
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
                if (insertAt >= 0) {
                    String insert = addNewlineAndPositionCaret ? toInsert + '\n'
                            : toInsert;
                    d.insertString(insertAt, insert, null); //NOI18N
                    int caretPos = insertAt + insert.length() + 1;
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
    
    public static InsertAtEndOfCurrentLineAction createOpenBraceAction() {
        return new InsertAtEndOfCurrentLineAction ("{", true, "CTL_OpenBraceAction"); //NOI18N
    }
    
    public static InsertAtEndOfCurrentLineAction createAddI18nAction() {
        return new InsertAtEndOfCurrentLineAction (" //NOI18N", false, "CTL_AddI18nHint"); //NOI18N
    }
}
