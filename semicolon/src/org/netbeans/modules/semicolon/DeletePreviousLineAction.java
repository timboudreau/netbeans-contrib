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
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.openide.ErrorManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import javax.swing.text.Document;

public final class DeletePreviousLineAction extends CallableSystemAction {

    public void performAction() {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (c instanceof JTextComponent) {
            JTextComponent jtc = (JTextComponent) c;
            try {
                int pos = jtc.getSelectionStart();
                Document d = jtc.getDocument();
                int lineStart = -1;
                int lineEnd = -1;
                if ('\n' == d.getText (pos, 1).charAt(0)) { //NOI18N
                    pos--;
                }
                for (int i = pos; i > 0; i--) {
                    char curr = d.getText(pos, 1).charAt(0);
                    if (curr == '\n' && lineEnd == -1) { //NOI18N
                        lineEnd = pos;
                    } else if ((curr == '\n' || i == 0) && lineStart == -1) { //NOI18N
                        lineStart = pos;
                        break;
                    }
                    pos -= 1;
                }
                if (lineStart > 0 && lineEnd > lineStart) {
                    d.remove(lineStart, lineEnd - lineStart);
                }
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify (e);
            }
        }
    }

    public String getName() {
        return NbBundle.getMessage(DeleteNextLineAction.class, 
                "CTL_DeletePreviousLineAction"); //NOI18N
    }
    
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE); //NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
    
    public boolean isEnabled() {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return c instanceof JTextComponent;
    }
}
