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
