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

public final class DeleteNextLineAction extends CallableSystemAction {

    public void performAction() {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (c instanceof JTextComponent) {
            JTextComponent jtc = (JTextComponent) c;
            try {
                int pos = jtc.getSelectionEnd();
                Document d = jtc.getDocument();
                int lineStart = -1;
                int lineEnd = -1;
                int len = d.getLength();
                for (int i = 0; i < len; i++) {
                    char curr = d.getText(pos, 1).charAt(0);
                    if (curr == '\n' && lineStart == -1) { //NOI18N
                        lineStart = pos;
                    } else if (curr == '\n' && lineEnd == -1) {
                        lineEnd = pos;
                        break;
                    }
                    pos += 1;
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
        return NbBundle.getMessage(DeleteNextLineAction.class, "CTL_DeleteNextLineAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
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
