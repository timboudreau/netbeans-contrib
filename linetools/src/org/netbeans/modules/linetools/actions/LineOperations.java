/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.linetools.actions;

import java.awt.Toolkit;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.openide.ErrorManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class LineOperations {

    static final void moveLineUp(JTextComponent textComponent) {
        if (textComponent.isEditable()) {
            Document doc = textComponent.getDocument();
            if (doc instanceof BaseDocument) {
                ((BaseDocument)doc).atomicLock();
            }
            try {
                Element rootElement = doc.getDefaultRootElement();
                Caret caret = textComponent.getCaret();
                int offset = textComponent.getCaretPosition();
                int zeroBaseLineNumber = rootElement.getElementIndex(offset);
                if (zeroBaseLineNumber == -1) {
                    // could not get line number
                    beep();
                    return;
                } else if (zeroBaseLineNumber == 0) {
                    // already first line
                    return;
                } else {
                    try {
                        // get line text
                        Element lineElement = rootElement.getElement(zeroBaseLineNumber);
                        int lineStartOffset = lineElement.getStartOffset();
                        int lineEndOffset = lineElement.getEndOffset();
                        String lineText = doc.getText(lineStartOffset, (lineEndOffset - lineStartOffset));

                        // remove the line
                        doc.remove(lineStartOffset, (Math.min(doc.getLength() - 1, lineEndOffset) - lineStartOffset));

                        // insert the text before the previous line
                        Element previousLineElement = rootElement.getElement(zeroBaseLineNumber - 1);
                        int previousLineStartOffset = previousLineElement.getStartOffset();
                        doc.insertString(previousLineStartOffset, lineText, null);

                        // set caret position
                        textComponent.setCaretPosition(previousLineStartOffset);
                    } catch (BadLocationException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            } finally {
                if (doc instanceof BaseDocument) {
                    ((BaseDocument)doc).atomicUnlock();
                }
            }
        } else {
            beep();
        }
    }

    static final void moveLineDown(JTextComponent textComponent) {
        if (textComponent.isEditable()) {
            Document doc = textComponent.getDocument();
            if (doc instanceof BaseDocument) {
                ((BaseDocument)doc).atomicLock();
            }
            try {
                Element rootElement = doc.getDefaultRootElement();
                Caret caret = textComponent.getCaret();
                int offset = textComponent.getCaretPosition();
                int zeroBaseLineNumber = rootElement.getElementIndex(offset);
                if (zeroBaseLineNumber == -1) {
                    // could not get line number
                    beep();
                    return;
                } else if (zeroBaseLineNumber >= (rootElement.getElementCount() - 1)) {
                    // already last line
                    return;
                } else {
                    try {
                        // get line text
                        Element lineElement = rootElement.getElement(zeroBaseLineNumber);
                        int lineStartOffset = lineElement.getStartOffset();
                        int lineEndOffset = lineElement.getEndOffset();
                        String lineText = doc.getText(lineStartOffset, (lineEndOffset - lineStartOffset));

                        // insert it after next line
                        Element nextLineElement = rootElement.getElement(zeroBaseLineNumber + 1);
                        int nextLineStartOffset = nextLineElement.getStartOffset();
                        int nextLineEndOffset = nextLineElement.getEndOffset();
                        doc.insertString(nextLineEndOffset, lineText, null);

                        // remove original line
                        doc.remove(lineStartOffset, (lineEndOffset - lineStartOffset));

                        // set caret position
                        textComponent.setCaretPosition(Math.min(doc.getLength() - 1, nextLineEndOffset - (lineEndOffset - lineStartOffset)));
                    } catch (BadLocationException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            } finally {
                if (doc instanceof BaseDocument) {
                    ((BaseDocument)doc).atomicUnlock();
                }
            }
        } else {
            beep();
        }
    }

    static final void copyLineUp(JTextComponent textComponent) {
        if (textComponent.isEditable()) {
            Document doc = textComponent.getDocument();
            if (doc instanceof BaseDocument) {
                ((BaseDocument)doc).atomicLock();
            }
            try {
                Element rootElement = doc.getDefaultRootElement();
                Caret caret = textComponent.getCaret();
                int offset = textComponent.getCaretPosition();
                int zeroBaseLineNumber = rootElement.getElementIndex(offset);
                if (zeroBaseLineNumber == -1) {
                    // could not get line number
                    beep();
                    return;
                } else {
                    try {
                        // get line text
                        Element lineElement = rootElement.getElement(zeroBaseLineNumber);
                        int lineStartOffset = lineElement.getStartOffset();
                        int lineEndOffset = lineElement.getEndOffset();
                        String lineText = doc.getText(lineStartOffset, (lineEndOffset - lineStartOffset));

                        // insert it
                        doc.insertString(lineStartOffset, lineText, null);

                        // set caret position
                        textComponent.setCaretPosition(Math.min(doc.getLength() - 1, lineStartOffset));
                    } catch (BadLocationException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            } finally {
                if (doc instanceof BaseDocument) {
                    ((BaseDocument)doc).atomicUnlock();
                }
            }
        } else {
            beep();
        }
    }

    static final void copyLineDown(JTextComponent textComponent) {
        if (textComponent.isEditable()) {
            Document doc = textComponent.getDocument();
            if (doc instanceof BaseDocument) {
                ((BaseDocument)doc).atomicLock();
            }
            try {
                Element rootElement = doc.getDefaultRootElement();
                Caret caret = textComponent.getCaret();
                int offset = textComponent.getCaretPosition();
                int zeroBaseLineNumber = rootElement.getElementIndex(offset);
                if (zeroBaseLineNumber == -1) {
                    // could not get line number
                    beep();
                    return;
                } else {
                    try {
                        // get line text
                        Element lineElement = rootElement.getElement(zeroBaseLineNumber);
                        int lineStartOffset = lineElement.getStartOffset();
                        int lineEndOffset = lineElement.getEndOffset();
                        String lineText = doc.getText(lineStartOffset, (lineEndOffset - lineStartOffset));

                        // insert it after
                        doc.insertString(Math.min(doc.getLength() - 1, lineEndOffset), lineText, null);

                        // set caret position
                        textComponent.setCaretPosition(Math.min(doc.getLength() - 1, lineEndOffset));
                    } catch (BadLocationException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            } finally {
                if (doc instanceof BaseDocument) {
                    ((BaseDocument)doc).atomicUnlock();
                }
            }
        } else {
            beep();
        }
    }

    private static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
}