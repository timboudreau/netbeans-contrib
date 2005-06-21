/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Leon Chiver. All Rights Reserved.
 */

package org.netbeans.modules.editor.java.doclet.support;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.CharSeq;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.java.NbJMIPaintComponent;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;

/**
 * @author leon chiver
 */
public class DocletCompletionItem implements CompletionItem {

    private final static int TYPE_ATTRIBUTE = 0;

    private final static int TYPE_ATTRIBUTE_VALUE = 1;

    private final static int TYPE_TEXT = 2;

    private final static int TYPE_SUBSTITUTE = 3;
    
    private int priority;

    private int type;

    private boolean required;

    private String text;
    
    private String displayText;
    
    private String substitutedText;
    
    private static NbJMIPaintComponent.NbStringPaintComponent paintComponent = 
            new NbJMIPaintComponent.NbStringPaintComponent();
    
    public static DocletCompletionItem createAttributeItem(
            String text, String displayText, boolean required, int sortPriority) {
        return new DocletCompletionItem(text, null, displayText, TYPE_ATTRIBUTE, required, sortPriority);
    }
    
    public static DocletCompletionItem createAttributeValueItem(
            String text, String displayText, int sortPriority) {
        return new DocletCompletionItem(text, null, displayText, TYPE_ATTRIBUTE_VALUE, false, sortPriority);
    }
    
    public static DocletCompletionItem createTextItem(
            String text, String displayText, int sortPriority) {
        return new DocletCompletionItem(text, null, displayText, TYPE_TEXT, false, sortPriority);
    }
    
    public static DocletCompletionItem createSubstitutionItem(
            String text, String substitutedText, String displayText, int sortPriority) {
        return new DocletCompletionItem(text, substitutedText, displayText, TYPE_SUBSTITUTE, false, sortPriority);
    }

    private DocletCompletionItem(
            String text, String substitutedText, String displayText, int type, boolean required, int sortPriority) {
        this.text = text;
        this.displayText = displayText;
        this.substitutedText = substitutedText;
        this.type = type;
        this.required = required;
        this.priority = sortPriority;
    }
    
    public CompletionTask createDocumentationTask() {
        return null;
    }
    
    public CompletionTask createToolTipTask() {
        return null;
    }
    
    public void defaultAction(JTextComponent component) {
        BaseDocument doc = Utilities.getDocument(component);
        if (doc == null) {
            return;
        }
        try {
            int caretOffset = component.getCaretPosition();
            int matchLength = getInitialMatchLength(doc, caretOffset);
            CharSeq seq = doc.getText();
            switch (type) {
                case TYPE_ATTRIBUTE:
                    String ins = text.substring(matchLength) + "=\"\"";
                    if (seq.charAt(caretOffset - 1) != ' ') {
                        ins = " " + ins;
                    }
                    doc.insertString(caretOffset, ins, null);
                    component.setCaretPosition(component.getCaretPosition() - 1);
                    break;
                case TYPE_ATTRIBUTE_VALUE:
                    ins = text.substring(matchLength);
                    doc.insertString(caretOffset, ins, null);
                    char nextChar = seq.charAt(caretOffset + ins.length());
                    if (nextChar == '"' || nextChar == '\'') {
                        doc.insertString(component.getCaretPosition() + 1, " ", null);
                        component.setCaretPosition(component.getCaretPosition() + 2);
                    }
                    break;
                case TYPE_TEXT:
                    ins = text.substring(matchLength);
                    doc.insertString(caretOffset, ins, null);
                    break;
                case TYPE_SUBSTITUTE:
                    int offset = caretOffset - substitutedText.length();
                    doc.replace(offset, substitutedText.length(), text, null);
                    break;
            }
            Completion.get().hideCompletion();
        } catch (BadLocationException e) {
            // Do nothing
        }
    }
    
    private int getInitialMatchLength(BaseDocument doc, int caretOffset) throws BadLocationException {
        int matchLength = 0;
        int maxCompletionIndex = text.length() - 1;
        int completionIndex = maxCompletionIndex;
        int docIndex = caretOffset - 1;
        CharSeq docText = doc.getText();
        while (completionIndex >= 0) {
            if (docText.charAt(docIndex) == text.charAt(completionIndex)) {
                docIndex--;
                matchLength++;
                completionIndex--;
            } else {
                docIndex = caretOffset - 1;
                maxCompletionIndex--;
                completionIndex = maxCompletionIndex;
                matchLength = 0;
            }
        }
        return matchLength;
    }
    
    public int getSortPriority() {
        return priority;
    }
    
    public CharSequence getSortText() {
        return text;
    }
    
    public boolean instantSubstitution(JTextComponent component) {
        return true;
    }
    
    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return 400;
    }
    
    public void processKeyEvent(KeyEvent evt) {
        // Do nothing special here, maybe tab completion would be cool
    }
    
    public String getText() {
        return text;
    }
    
    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        paintComponent.setString(displayText);
        if (required) {
            defaultFont = new Font(
                    defaultFont.getName(), defaultFont.getStyle() | Font.BOLD, 
                    defaultFont.getSize() + 2);
        }
        paintComponent.setFont(defaultFont);
        paintComponent.setForeground(required ? Color.RED : defaultColor);
        paintComponent.setBackground(backgroundColor);
        paintComponent.paintComponent(g);
        // TODO - get back to CompletionUtilities when they work how i want them (background color is important)
//        String prefix = "<html>";
//        if (required) {
//            prefix = prefix + "<b>";
//        }
//        CompletionUtilities.renderHtml(null, prefix + displayText, null, g, defaultFont, 
//                defaultColor, width, height, selected);
    }
    
}
