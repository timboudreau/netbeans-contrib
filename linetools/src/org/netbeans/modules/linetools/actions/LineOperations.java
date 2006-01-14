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
                boolean selection = false;
                int start = textComponent.getCaretPosition();
                int end = start;

                // check if there is a selection
                if (caret.isSelectionVisible()) {
                    int selStart = caret.getDot();
                    int selEnd = caret.getMark();
                    start = Math.min(selStart, selEnd);
                    end =   Math.max(selStart, selEnd) - 1;
                    selection = true;
                }

                int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                int zeroBaseEndLineNumber = rootElement.getElementIndex(end);

                if (zeroBaseStartLineNumber == -1) {
                    // could not get line number
                    beep();
                    return;
                } else if (zeroBaseStartLineNumber == 0) {
                    // already first line
                    return;
                } else {
                    try {
                        // get line text
                        Element startLineElement = rootElement.getElement(zeroBaseStartLineNumber);
                        int startLineStartOffset = startLineElement.getStartOffset();

                        Element endLineElement = rootElement.getElement(zeroBaseEndLineNumber);
                        int endLineEndOffset = endLineElement.getEndOffset();

                        String linesText = doc.getText(startLineStartOffset, (endLineEndOffset - startLineStartOffset));

                        Element previousLineElement = rootElement.getElement(zeroBaseStartLineNumber - 1);
                        int previousLineStartOffset = previousLineElement.getStartOffset();

                        int column = (selection ? 0 : start - startLineStartOffset);

                        // remove the line
                        doc.remove(startLineStartOffset, Math.min(doc.getLength(),endLineEndOffset) - startLineStartOffset);

                        // insert the text before the previous line
                        doc.insertString(previousLineStartOffset, linesText, null);

                        if (selection) {
                            // select moved lines
                            caret.setDot(previousLineStartOffset + (endLineEndOffset - startLineStartOffset));
                            caret.moveDot(previousLineStartOffset);
                        } else {
                            // set caret position
                            textComponent.setCaretPosition(previousLineStartOffset + column);
                        }
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
                boolean selection = false;
                int start = textComponent.getCaretPosition();
                int end = start;

                // check if there is a selection
                if (caret.isSelectionVisible()) {
                    int selStart = caret.getDot();
                    int selEnd = caret.getMark();
                    start = Math.min(selStart, selEnd);
                    end =   Math.max(selStart, selEnd) - 1;
                    selection = true;
                }

                int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                int zeroBaseEndLineNumber = rootElement.getElementIndex(end);

                if (zeroBaseEndLineNumber == -1) {
                    // could not get line number
                    beep();
                    return;
                } else if (zeroBaseEndLineNumber >= (rootElement.getElementCount() - 2)) {
                    // already last or penultimate line (due to a getLength() bug)
                    return;
                } else {
                    try {
                        // get line text
                        Element startLineElement = rootElement.getElement(zeroBaseStartLineNumber);
                        int startLineStartOffset = startLineElement.getStartOffset();

                        Element endLineElement = rootElement.getElement(zeroBaseEndLineNumber);
                        int endLineEndOffset = endLineElement.getEndOffset();

                        String linesText = doc.getText(startLineStartOffset, (endLineEndOffset - startLineStartOffset));

                        Element nextLineElement = rootElement.getElement(zeroBaseEndLineNumber + 1);
                        int nextLineStartOffset = nextLineElement.getStartOffset();
                        int nextLineEndOffset = nextLineElement.getEndOffset();

                        int column = (selection ? 0 : start - startLineStartOffset);

                        // insert it after next line
                        doc.insertString(nextLineEndOffset, linesText, null);

                        // remove original line
                        doc.remove(startLineStartOffset, (endLineEndOffset - startLineStartOffset));

                        if (selection) {
                            // select moved lines
                            caret.setDot(nextLineEndOffset  - (endLineEndOffset - startLineStartOffset));
                            caret.moveDot(nextLineEndOffset);
                        } else {
                            // set caret position
                            textComponent.setCaretPosition(Math.min(doc.getLength() - 1, nextLineEndOffset + column - (endLineEndOffset - startLineStartOffset)));
                        }
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

                        int column = offset - lineStartOffset;

                        // insert it
                        doc.insertString(lineStartOffset, lineText, null);

                        // set caret position
                        textComponent.setCaretPosition(Math.min(doc.getLength() - 1, lineStartOffset + column));
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
                }  else if (zeroBaseLineNumber >= (rootElement.getElementCount() - 2)) {
                    // already last or penultimate line (due to a getLength() bug)
                    beep();
                    return;
                } else {
                    try {
                        // get line text
                        Element lineElement = rootElement.getElement(zeroBaseLineNumber);
                        int lineStartOffset = lineElement.getStartOffset();
                        int lineEndOffset = lineElement.getEndOffset();
                        String lineText = doc.getText(lineStartOffset, (lineEndOffset - lineStartOffset));

                        int column = offset - lineStartOffset;

                        // insert it after
                        doc.insertString(Math.min(doc.getLength() - 1, lineEndOffset), lineText, null);

                        // set caret position
                        textComponent.setCaretPosition(Math.min(doc.getLength() - 1, lineEndOffset + column));
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
