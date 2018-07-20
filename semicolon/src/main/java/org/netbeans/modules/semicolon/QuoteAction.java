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
import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Quotes the current string
 *
 * @author Tim Boudreau
 */
public class QuoteAction extends AbstractAction {

    public QuoteAction () {
        putValue("noIconInMenu", Boolean.TRUE); //NOI18N
        putValue(NAME, NbBundle.getMessage(QuoteAction.class, "LBL_QUOTE")); //NOI18N
    }
    
    public void actionPerformed (ActionEvent ae) {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (c instanceof JTextComponent) {
            JTextComponent jtc = (JTextComponent) c;
            try {
                int selStart = jtc.getSelectionStart();
                int selEnd = jtc.getSelectionEnd();
                
                Document d = jtc.getDocument();
                int begin = -1;
                int end = -1;
                int len = d.getLength();
                if (selStart == selEnd) {
                    for (int i=selStart; i < len; i++) {
                        if (isStopChar(d.getText(i, 1).charAt(0))) {
                            end = i - 1;
                            break;
                        }
                    }
                    for (int i=selStart; i >= 0; i--) {
                        if (isStopChar(d.getText(i, 1).charAt(0))) {
                            begin = i + 1;
                            break;
                        }
                    }
                } else {
                    begin = selStart;
                    end = selEnd - 2;
                }
                if (end >= 0 && begin >= 0) {
                    final Document doc = jtc.getDocument();
                    final int s = begin;
                    final int e = end + 1;
                    Runnable r = new Runnable() {
                        public void run() {
                            try {
                                doc.insertString(e, "\"", null); //NOI18N
                                doc.insertString(s, "\"", null); //NOI18N
                            } catch (BadLocationException e) {
                                Exceptions.printStackTrace(e);
                            }
                        }
                    };
//                    doc.render(r); //XXX hangs
                    r.run();
                    jtc.setSelectionStart(s);
                    jtc.setSelectionEnd(e);
                }
            } catch (BadLocationException e) {
                Exceptions.printStackTrace(e);
            }
        }
    }
    
    boolean isStopChar (char c) {
        return Character.isWhitespace(c) || '(' == c || '}' == c || //NOI18N
                '<' == c || '>' == c || ';' == c; //NOI18N
    }
}
