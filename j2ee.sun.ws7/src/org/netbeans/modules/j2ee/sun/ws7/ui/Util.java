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

package org.netbeans.modules.j2ee.sun.ws7.ui;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.SwingUtilities;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.StatusDisplayer;

public class Util {
    /**
     * Creates a new instance of Util
     */
    public Util() {
    }

    ///Numeric Document
    public static NumericDocument getNumericDocument() {
        return new NumericDocument();
    }

    public static class NumericDocument extends PlainDocument {
        private Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {
            char[] s = str.toCharArray();
            char[] r = new char[s.length];
            int j = 0;
            for (int i = 0; i < r.length; i++) {
                if (Character.isDigit(s[i])) {
                    r[j++] = s[i];
                } else {
                    toolkit.beep();
                }
            }
            super.insertString(offs, new String(r, 0, j), a);
        }
    } // class NumericDocument
    
    public static void showInformation(String msg) {
        internalShowMessage(msg, null, NotifyDescriptor.INFORMATION_MESSAGE);
    }
    
    public static void showInformation(String msg,  String title) {
        internalShowMessage(msg, title, NotifyDescriptor.INFORMATION_MESSAGE);
    }
    
    public static  Object showWarning(final String msg) {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.OK_CANCEL_OPTION,
                                                               NotifyDescriptor.WARNING_MESSAGE);
        return DialogDisplayer.getDefault().notify(d);
    }
    
    public static Object   showWarning(final String msg, final String title) {
        NotifyDescriptor d = new NotifyDescriptor.Confirmation(msg, title, NotifyDescriptor.OK_CANCEL_OPTION,
                                                               NotifyDescriptor.WARNING_MESSAGE);
        return DialogDisplayer.getDefault().notify(d);
    }
    
    public static void showError(String msg) {
        internalShowMessage(msg, null, NotifyDescriptor.ERROR_MESSAGE);
    }
    
    public static void showError(String msg, String title){
        internalShowMessage(msg, title, NotifyDescriptor.ERROR_MESSAGE);
    }
    
    public static void setStatusBar(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    StatusDisplayer.getDefault().setStatusText(msg);
                }
            });
    }

    private static void internalShowMessage(final String msg, final String title, final int type) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    NotifyDescriptor d = new NotifyDescriptor.Message(msg, type);

                    if (title != null) {
                        d.setTitle(title); 
                    } // end of if (title != null)
                    
                    DialogDisplayer.getDefault().notify(d);
                }
            });
    }
}
