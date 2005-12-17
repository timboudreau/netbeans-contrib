/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import java.util.Iterator;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Language;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.latex.editor.bibtex.BiBTeXLanguage;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.lexer.editorbridge.TokenRootElement;
import org.netbeans.spi.lexer.inc.OffsetToken;

/**
 *
 * @author  Jan Lahoda
 */
public final class Utilities {
    
    /** Creates a new instance of Utilities */
    private Utilities() {
    }
    
    public static final Token getToken(Document doc, int offset) throws /*BadLocationException, */ClassCastException {
        TokenRootElement tre = getTREImpl(doc);
        
        tre.relocate(offset > 0 ? offset - 1 : 0);
        
        return tre.next();
    }
    
    public static final Token getTokenForIndex(Document doc, int index) {
        TokenRootElement tre = getTREImpl(doc);
        
        return (Token) tre.getElement(index);
    }
    
//    public static final Collection getAllLabels(LaTeXSource source) {
//        return getAllLabelsInfos(source).keySet();
//    }
    
    public static int getTokenIndex(Document doc, int offset) {
        TokenRootElement tre = getTREImpl(doc);
        
        return tre.getElementIndex(offset);
    }
    
    public static int getTokenIndex(Document doc, Token token) {
        return getTokenIndex(doc, ((OffsetToken) token).getOffset());
    }
    
    public static int getTokenOffset(Document doc, Token token) {
        return ((OffsetToken) token).getOffset();
    }
    
    public static String readArgumentContent(Document doc, int index, int limitOffset) {
        //TODO: this implementation is far from perfect (...\section{sf{}}), correct.
        TokenRootElement tre = getTREImpl(doc);
        int              elements = tre.getElementCount();
        
        if (index >= elements)
            return "";
    
        Token brack = (Token) tre.getElement(index++);
        
        assert brack.getId() == TexLanguage.RECT_BRACKET_LEFT || brack.getId() == TexLanguage.COMP_BRACKET_LEFT;
        
        TokenId finish = brack.getId() == TexLanguage.RECT_BRACKET_LEFT ? TexLanguage.RECT_BRACKET_RIGHT : TexLanguage.COMP_BRACKET_RIGHT;
        
        Token actual;
        StringBuffer result = new StringBuffer();
        
        while (index < elements && (actual = (Token) tre.getElement(index)).getId() != finish) {
            if (limitOffset == (-1) || tre.getElementOffset(index) + actual.getText().length() < limitOffset) {
                result.append(actual.getText());
                index++;
            } else {
                result.append(actual.getText().subSequence(0, limitOffset - tre.getElementOffset(index)));
                
                break;
            }
        }
        
        return result.toString();
    }
    
//    public static final Map getAllLabelsInfos(LaTeXSource source) {
//              LaTeXSource.Lock lock   = null;
//        final Map              result = new HashMap();
//        
//        try {
//            lock = source.lock();
//            
//            source.getDocument().traverse(new DefaultTraverseHandler() {
//                private String caption = "";
//                
//                public boolean argumentStart(ArgumentNode arg) {
//                    if (arg.getArgument().hasAttribute("#caption"))
//                        caption = arg.getText().toString(); //ehm. it should be cleared somewhere, but I do not know were it is cleared in LaTeX.
//                    
//                    return true;
//                }
//                
//                public boolean commandStart(CommandNode node) {
//                    if (node.getCommand().isLabelLike()) {
//                        LabelInfo info  = new LabelInfo(node.getArgument(0).getText().toString(), node.getStartingPosition(), caption);
//                        
//                        result.put(info.label, info);
//                    }
//                    
//                    return true;
//                }
//            });
//        } finally {
//            if (lock != null)
//                source.unlock(lock);
//        }
//
//        return result;
//    }
    
//    public static class LabelInfo {
//        public String label;
//        public SourcePosition startingPosition;
//        public String comment;
//        
//        public LabelInfo(String label, SourcePosition startingPosition, String comment) {
//            this.label = label;
//            this.startingPosition = startingPosition;
//            this.comment = comment;
//        }
//    }

    public static final List getAllBibReferences(LaTeXSource source) {
        return AnalyseBib.getDefault().getAllBibReferences(source);
    }

    public static final Token getPreviousToken(Document doc, int offset) {
        int   startingOffset = getStartingOffset(doc, offset);
        Token previous       = getToken         (doc, startingOffset - 1);
        
        return previous;
    }
    
    public static final int getStartingOffset(Document doc, int offset) /*throws BadLocationException*/ {
        TokenRootElement tre = getTREImpl(doc);
        Token orig = getToken(doc, offset);
        
        int newOffset = tre.getElementOffset(tre.getElementIndex(offset - 1));
        
        if (getToken(doc, newOffset) == orig)
            return newOffset;
        
        if (getToken(doc, newOffset + 1) == orig)
            return newOffset + 1;
        
        throw new IllegalStateException("");
    }
    
    public static boolean isTextWord(Token token) {
        return isTextWord(token.getId());
    }
    
    public static boolean isTextWord(TokenId token) {
        return token == TexLanguage.WORD;
    }
    
    public static int getOffsetForLineIndex(BaseDocument doc, int line) throws BadLocationException {
        return org.netbeans.editor.Utilities.getRowStart(doc, 0, line - 1);
    }
    
    public static int getLineIndexForOffset(Document doc, int offset) throws BadLocationException, ClassCastException {
        return org.netbeans.editor.Utilities.getLineOffset((BaseDocument) doc, offset);
    }
    
    public static int countWords(Document doc) {
        TokenRootElement tre      = getTREImpl(doc);
        int              elements = tre.getElementCount();
        int              count    = 0;
        
        for (int cntr = 0; cntr < elements; cntr++) {
            Token current = (Token) tre.getElement(cntr);
	    String text   = current.getText().toString();
            
            if (current.getId() == TexLanguage.WORD) {
	        if (!"a".equals(text) && !"an".equals(text) && !"the".equals(text))
                   count++;
            } //else
//                System.err.println(current.getText());
        }
        
        return count;
    }
    
    public static Iterator getTokenIterator(Document doc) {
        return new TokenIterator(doc);
    }
    
    private static class TokenIterator implements Iterator {
        
        private TokenRootElement tre   = null;
        private int              index = 0;
        
        public TokenIterator(Document doc) {
            tre = getTREImpl(doc);
            index = 0;
        }
        
        public boolean hasNext() {
            return index < tre.getElementCount();
        }
        
        public Object next() {
            return (Token) tre.getElement(index);
        }
        
        public void remove() {
            throw new UnsupportedOperationException("TokenIterator does not support remove().");
        }
        
    }
    
    private static TexTokenRootElement getTREImpl(Document doc, Language language) {
        Object tre = doc.getProperty(TokenRootElement.class);
        
        if (tre instanceof TexTokenRootElement)
            return (TexTokenRootElement) tre;
        else
            return new TexTokenRootElement(doc, language);
    }
    
    public static TexTokenRootElement getTREImpl(Document doc) {
        Object mimeType = doc.getProperty("mime-type");
        
        if (mimeType == null || !(mimeType instanceof String))
            throw new IllegalStateException("Undeterminable mime type.");
        
        if ("text/x-tex".equals(mimeType))
            return getTREImpl(doc, TexLanguage.get());
        
        if ("text/x-bibtex".equals(mimeType))
            return getTREImpl(doc, BiBTeXLanguage.get());
        
        throw new IllegalStateException("Unknown mime type: " + mimeType);
    }
    
}
