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

package org.netbeans.modules.linetools.actions;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class LineOperations {
    
    static void exchangeDotAndMark(JEditorPane textComponent) {
        Document doc = textComponent.getDocument();
        Caret caret = textComponent.getCaret();
        // check if there is a selection
        if (caret.isSelectionVisible()) {
            int selStart = caret.getDot();
            int selEnd = caret.getMark();
            caret.setDot(selStart);
            caret.moveDot(selEnd);
        }
    }
    
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
                boolean backwardSelection = false;
                int start = textComponent.getCaretPosition();
                int end = start;
                
                // check if there is a selection
                if (caret.isSelectionVisible()) {
                    int selStart = caret.getDot();
                    int selEnd = caret.getMark();
                    start = Math.min(selStart, selEnd);
                    end =   Math.max(selStart, selEnd) - 1;
                    selection = true;
                    backwardSelection = (selStart >= selEnd);
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
                        
                        int column = start - startLineStartOffset;
                        
                        // remove the line
                        doc.remove(startLineStartOffset, Math.min(doc.getLength(),endLineEndOffset) - startLineStartOffset);
                        
                        // insert the text before the previous line
                        doc.insertString(previousLineStartOffset, linesText, null);
                        
                        if (selection) {
                            // select moved lines
                            if (backwardSelection) {
                                caret.setDot(previousLineStartOffset + column);
                                caret.moveDot(previousLineStartOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                            } else {
                                caret.setDot(previousLineStartOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                caret.moveDot(previousLineStartOffset + column);
                            }
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
                boolean backwardSelection = false;
                int start = textComponent.getCaretPosition();
                int end = start;
                
                // check if there is a selection
                if (caret.isSelectionVisible()) {
                    int selStart = caret.getDot();
                    int selEnd = caret.getMark();
                    start = Math.min(selStart, selEnd);
                    end =   Math.max(selStart, selEnd) - 1;
                    selection = true;
                    backwardSelection = (selStart >= selEnd);
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
                        
                        int column = start - startLineStartOffset;
                        
                        // insert it after next line
                        doc.insertString(nextLineEndOffset, linesText, null);
                        
                        // remove original line
                        doc.remove(startLineStartOffset, (endLineEndOffset - startLineStartOffset));
                        
                        if (selection) {
                            // select moved lines
                            if (backwardSelection) {
                                caret.setDot(nextLineEndOffset  - (endLineEndOffset - startLineStartOffset) + column);
                                caret.moveDot(nextLineEndOffset - (endLineEndOffset - end - 1));
                            } else {
                                caret.setDot(nextLineEndOffset - (endLineEndOffset - end - 1));
                                caret.moveDot(nextLineEndOffset  - (endLineEndOffset - startLineStartOffset) + column);
                            }
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
                boolean selection = false;
                boolean backwardSelection = false;
                int start = textComponent.getCaretPosition();
                int end = start;
                
                // check if there is a selection
                if (caret.isSelectionVisible()) {
                    int selStart = caret.getDot();
                    int selEnd = caret.getMark();
                    start = Math.min(selStart, selEnd);
                    end =   Math.max(selStart, selEnd) - 1;
                    selection = true;
                    backwardSelection = (selStart >= selEnd);
                }
                
                int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                
                if (zeroBaseStartLineNumber == -1) {
                    // could not get line number
                    beep();
                    return;
                } else {
                    try {
                        // get line text
                        Element startLineElement = rootElement.getElement(zeroBaseStartLineNumber);
                        int startLineStartOffset = startLineElement.getStartOffset();
                        
                        Element endLineElement = rootElement.getElement(zeroBaseEndLineNumber);
                        int endLineEndOffset = endLineElement.getEndOffset();
                        
                        String linesText = doc.getText(startLineStartOffset, (endLineEndOffset - startLineStartOffset));
                        
                        int column = start - startLineStartOffset;
                        
                        // insert it
                        doc.insertString(startLineStartOffset, linesText, null);
                        
                        if (selection) {
                            // select moved lines
                            if (backwardSelection) {
                                caret.setDot(startLineStartOffset + column);
                                caret.moveDot(startLineStartOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                            } else {
                                caret.setDot(startLineStartOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                caret.moveDot(startLineStartOffset + column);
                            }
                        } else {
                            // set caret position
                            textComponent.setCaretPosition(startLineStartOffset + column);
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
    
    static final void copyLineDown(JTextComponent textComponent) {
        if (textComponent.isEditable()) {
            Document doc = textComponent.getDocument();
            if (doc instanceof BaseDocument) {
                ((BaseDocument)doc).atomicLock();
            }
            try {
                Element rootElement = doc.getDefaultRootElement();
                
                Caret caret = textComponent.getCaret();
                boolean selection = false;
                boolean backwardSelection = false;
                int start = textComponent.getCaretPosition();
                int end = start;
                
                // check if there is a selection
                if (caret.isSelectionVisible()) {
                    int selStart = caret.getDot();
                    int selEnd = caret.getMark();
                    start = Math.min(selStart, selEnd);
                    end =   Math.max(selStart, selEnd) - 1;
                    selection = true;
                    backwardSelection = (selStart >= selEnd);
                }
                
                int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                
                if (zeroBaseEndLineNumber == -1) {
                    // could not get line number
                    beep();
                    return;
                } else {
                    try {
                        // get line text
                        Element startLineElement = rootElement.getElement(zeroBaseStartLineNumber);
                        int startLineStartOffset = startLineElement.getStartOffset();
                        
                        Element endLineElement = rootElement.getElement(zeroBaseEndLineNumber);
                        int endLineEndOffset = endLineElement.getEndOffset();
                        
                        String linesText = doc.getText(startLineStartOffset, (endLineEndOffset - startLineStartOffset));
                        
                        int column = start - startLineStartOffset;
                        
                        // insert it after next line
                        doc.insertString(endLineEndOffset, linesText, null);
                        
                        if (selection) {
                            // select moved lines
                            if (backwardSelection) {
                                caret.setDot(endLineEndOffset + column);
                                caret.moveDot(endLineEndOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                            } else {
                                caret.setDot(endLineEndOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                caret.moveDot(endLineEndOffset + column);
                            }
                        } else {
                            // set caret position
                            textComponent.setCaretPosition(Math.min(doc.getLength() - 1, endLineEndOffset + column));
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
    
    static final void sortLinesAscending(JTextComponent textComponent) {
        sortLines(textComponent);
    }
    
    static final void sortLinesDescending(JTextComponent textComponent) {
        sortLines(textComponent, true);
    }
    
    static final void sortLines(JTextComponent textComponent) {
        sortLines(textComponent, false);
    }
    
    static final void sortLines(JTextComponent textComponent, boolean descending) {
        Caret caret = textComponent.getCaret();
        if (textComponent.isEditable() && caret.isSelectionVisible()) {
            Document doc = textComponent.getDocument();
            if (doc instanceof BaseDocument) {
                ((BaseDocument)doc).atomicLock();
            }
            try {
                Element rootElement = doc.getDefaultRootElement();
                
                int selStart = caret.getDot();
                int selEnd = caret.getMark();
                int start = Math.min(selStart, selEnd);
                int end =   Math.max(selStart, selEnd) - 1;
                
                int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                
                if (zeroBaseStartLineNumber == -1 || zeroBaseEndLineNumber == -1 || (zeroBaseStartLineNumber == zeroBaseEndLineNumber)) {
                    // could not get line number or same line
                    beep();
                    return;
                }
                
                int startOffset = rootElement.getElement(zeroBaseStartLineNumber).getStartOffset();
                int endOffset = rootElement.getElement(zeroBaseEndLineNumber).getEndOffset();
                
                try {
                    int numberOfLines = zeroBaseEndLineNumber - zeroBaseStartLineNumber + 1;
                    String[] linesText = new String[numberOfLines];
                    for (int i = 0; i < numberOfLines; i++) {
                        // get line text
                        Element lineElement = rootElement.getElement(zeroBaseStartLineNumber + i);
                        int lineStartOffset = lineElement.getStartOffset();
                        int lineEndOffset = lineElement.getEndOffset();
                        
                        linesText[i] = doc.getText(lineStartOffset, (lineEndOffset - lineStartOffset));
                    }
                    
                    if (isRemoveDuplicateLines()) {
                        linesText = (String[]) new TreeSet(Arrays.asList(linesText)).toArray(new String[0]);
                    }
                    
                    if (descending) {
                        Arrays.sort(linesText, REVERSE_STRING_COMPARATOR);
                    } else {
                        Arrays.sort(linesText);
                    }
                    
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < linesText.length; i++) {
                        sb.append(linesText[i]);
                    }
                    
                    // remove the lines
                    doc.remove(startOffset, Math.min(doc.getLength(),endOffset) - startOffset);
                    
                    // insert the sorted text
                    doc.insertString(startOffset, sb.toString(), null);
                    
                } catch (BadLocationException ex) {
                    ErrorManager.getDefault().notify(ex);
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
    
    private static class ReverseComparator implements Comparator {
        private Comparator delegateCompartor;
        
        private ReverseComparator(Comparator comparator) {
            delegateCompartor = comparator;
        }
        public int compare(Object o1, Object o2) {
            return (-1 * delegateCompartor.compare(o1, o2));
        }
    }
    
    /**
     * Holds value of property removeDuplicateLines.
     */
    private static boolean removeDuplicateLines;
    
    /**
     * Getter for property removeDuplicateLines.
     * @return Value of property removeDuplicateLines.
     */
    static boolean isRemoveDuplicateLines() {
        return removeDuplicateLines;
    }
    
    /**
     * Setter for property removeDuplicateLines.
     * @param removeDuplicateLines New value of property removeDuplicateLines.
     */
    static void setRemoveDuplicateLines(boolean removeDuplicateLines) {
        LineOperations.removeDuplicateLines = removeDuplicateLines;
    }
    
    private static ReverseComparator REVERSE_STRING_COMPARATOR = new ReverseComparator(Collator.getInstance());
    
    static void filter(JTextComponent textComponent) {
        Caret caret = textComponent.getCaret();
        if (textComponent.isEditable() && caret.isSelectionVisible()) {
            Document doc = textComponent.getDocument();
            if (doc instanceof BaseDocument) {
                ((BaseDocument)doc).atomicLock();
            }
            try {
                Element rootElement = doc.getDefaultRootElement();
                
                int selStart = caret.getDot();
                int selEnd = caret.getMark();
                int start = Math.min(selStart, selEnd);
                int end =   Math.max(selStart, selEnd) - 1;
                
                int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                
                if (zeroBaseStartLineNumber == -1 || zeroBaseEndLineNumber == -1) {
                    // could not get line number or same line
                    beep();
                    return;
                }
                
                NotifyDescriptor.InputLine filterCommand = new NotifyDescriptor.InputLine("Enter Filter command:",
                        "Filter command"
                        ,NotifyDescriptor.OK_CANCEL_OPTION
                        ,NotifyDescriptor.PLAIN_MESSAGE);
                
                if (DialogDisplayer.getDefault().notify(filterCommand) == NotifyDescriptor.OK_OPTION) {
                    int startOffset = rootElement.getElement(zeroBaseStartLineNumber).getStartOffset();
                    int endOffset = rootElement.getElement(zeroBaseEndLineNumber).getEndOffset();
                    
                    try {
                        int numberOfLines = zeroBaseEndLineNumber - zeroBaseStartLineNumber + 1;
                        String[] linesText = new String[numberOfLines];
                        for (int i = 0; i < numberOfLines; i++) {
                            // get line text
                            Element lineElement = rootElement.getElement(zeroBaseStartLineNumber + i);
                            int lineStartOffset = lineElement.getStartOffset();
                            int lineEndOffset = lineElement.getEndOffset();
                            
                            linesText[i] = doc.getText(lineStartOffset, (lineEndOffset - lineStartOffset - 1));
                        }
                        
                        try {
                            FilterProcess filterProcess = new FilterProcess(filterCommand.getInputText().split(" "));
                            
                            PrintWriter in = filterProcess.exec();
                            for (int i = 0; i < linesText.length; i++) {
                                in.println(linesText[i]);
                            }
                            in.close();
                            if (filterProcess.waitFor() == 0) {
                                linesText = filterProcess.getStdOutOutput();
                                if (linesText != null) {
                                    StringBuffer sb = new StringBuffer();
                                    for (int i = 0; i < linesText.length; i++) {
                                        sb.append(linesText[i] + "\n");
                                    }
                                    
                                    // remove the lines
                                    doc.remove(startOffset, Math.min(doc.getLength(),endOffset) - startOffset);
                                    
                                    // insert the sorted text
                                    doc.insertString(startOffset, sb.toString(), null);
                                }
                            }
                            filterProcess.destroy();
                        } catch (IOException fe) {
                            ErrorManager.getDefault().notify(ErrorManager.USER, fe);
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
    
    static void filterOutput(JTextComponent textComponent) {
        Caret caret = textComponent.getCaret();
        if (textComponent.isEditable() && caret.isSelectionVisible()) {
            Document doc = textComponent.getDocument();
            if (doc instanceof BaseDocument) {
                ((BaseDocument)doc).atomicLock();
            }
            try {
                Element rootElement = doc.getDefaultRootElement();
                
                int selStart = caret.getDot();
                int selEnd = caret.getMark();
                int start = Math.min(selStart, selEnd);
                int end =   Math.max(selStart, selEnd) - 1;
                
                int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                
                if (zeroBaseStartLineNumber == -1 || zeroBaseEndLineNumber == -1) {
                    // could not get line number or same line
                    beep();
                    return;
                }
                
                NotifyDescriptor.InputLine filterCommand = new NotifyDescriptor.InputLine("Enter Filter command (output sent to Output window):",
                        "Filter command"
                        ,NotifyDescriptor.OK_CANCEL_OPTION
                        ,NotifyDescriptor.PLAIN_MESSAGE);
                
                if (DialogDisplayer.getDefault().notify(filterCommand) == NotifyDescriptor.OK_OPTION) {
                    int startOffset = rootElement.getElement(zeroBaseStartLineNumber).getStartOffset();
                    int endOffset = rootElement.getElement(zeroBaseEndLineNumber).getEndOffset();
                    
                    try {
                        int numberOfLines = zeroBaseEndLineNumber - zeroBaseStartLineNumber + 1;
                        String[] linesText = new String[numberOfLines];
                        for (int i = 0; i < numberOfLines; i++) {
                            // get line text
                            Element lineElement = rootElement.getElement(zeroBaseStartLineNumber + i);
                            int lineStartOffset = lineElement.getStartOffset();
                            int lineEndOffset = lineElement.getEndOffset();
                            
                            linesText[i] = doc.getText(lineStartOffset, (lineEndOffset - lineStartOffset - 1));
                        }
                        
                        try {
                            FilterProcess filterProcess = new FilterProcess(filterCommand.getInputText().split(" "));
                            
                            PrintWriter in = filterProcess.exec();
                            for (int i = 0; i < linesText.length; i++) {
                                in.println(linesText[i]);
                            }
                            in.close();
                            if (filterProcess.waitFor() == 0) {
                                InputOutput io = IOProvider.getDefault().getIO(filterCommand.getInputText(), true);
                                linesText = filterProcess.getStdOutOutput();
                                if (linesText != null) {
                                    PrintWriter pw = new PrintWriter(io.getOut());
                                    for (int i = 0; i < linesText.length; i++) {
                                        pw.println(linesText[i]);
                                    }
                                }
                                linesText = filterProcess.getStdErrOutput();
                                if (linesText != null) {
                                    PrintWriter pw = new PrintWriter(io.getErr());
                                    for (int i = 0; i < linesText.length; i++) {
                                        pw.println(linesText[i]);
                                    }
                                }
                            }
                            filterProcess.destroy();
                        } catch (IOException fe) {
                            ErrorManager.getDefault().notify(ErrorManager.USER, fe);
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
    
    private static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
}
