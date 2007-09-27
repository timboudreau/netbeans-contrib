/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.model.lexer.TexTokenId;

/**
 *
 * @author  Jan Lahoda
 */
public final class Utilities {
    
    /** Creates a new instance of Utilities */
    private Utilities() {
    }
    
    public static final Token<TexTokenId> getToken(Document doc, int offset) throws /*BadLocationException, */ClassCastException {
        TokenSequence<TexTokenId> ts = TokenHierarchy.get(doc).tokenSequence(TexLanguage.description());
        
        ts.move(offset > 0 ? offset - 1 : 0); //TODO: -1??/
        
        return ts.token();
    }
    
//    public static final Token getTokenForIndex(Document doc, int index) {
//        TokenRootElement tre = getTREImpl(doc);
//        
//        return (Token) tre.getElement(index);
//    }
    
//    public static final Collection getAllLabels(LaTeXSource source) {
//        return getAllLabelsInfos(source).keySet();
//    }
    
//    public static int getTokenIndex(Document doc, int offset) {
//        TokenRootElement tre = getTREImpl(doc);
//        
//        return tre.getElementIndex(offset);
//    }
    
//    public static int getTokenIndex(Document doc, Token token) {
//        return getTokenIndex(doc, ((OffsetToken) token).getOffset());
//    }
//    
//    public static int getTokenOffset(Document doc, Token token) {
//        return ((OffsetToken) token).getOffset();
//    }
    
//    public static String readArgumentContent(Document doc, int startOffset, int limitOffset) {
//        //TODO: this implementation is far from perfect (...\section{sf{}}), correct.
//        TokenRootElement tre = getTREImpl(doc);
//        int              elements = tre.getElementCount();
//        
//        if (index >= elements)
//            return "";
//    
//        Token brack = (Token) tre.getElement(index++);
//        
//        assert brack.getId() == TexLanguage.RECT_BRACKET_LEFT || brack.getId() == TexLanguage.COMP_BRACKET_LEFT;
//        
//        TokenId finish = brack.getId() == TexLanguage.RECT_BRACKET_LEFT ? TexLanguage.RECT_BRACKET_RIGHT : TexLanguage.COMP_BRACKET_RIGHT;
//        
//        Token actual;
//        StringBuffer result = new StringBuffer();
//        
//        while (index < elements && (actual = (Token) tre.getElement(index)).getId() != finish) {
//            if (limitOffset == (-1) || tre.getElementOffset(index) + actual.getText().length() < limitOffset) {
//                result.append(actual.getText());
//                index++;
//            } else {
//                result.append(actual.getText().subSequence(0, limitOffset - tre.getElementOffset(index)));
//                
//                break;
//            }
//        }
//        
//        return result.toString();
//    }
    
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

    public static final Token getPreviousToken(Document doc, int offset) {
        int   startingOffset = getStartingOffset(doc, offset);
        Token previous       = getToken         (doc, startingOffset - 1);
        
        return previous;
    }
    
    public static final int getStartingOffset(Document doc, int offset) /*throws BadLocationException*/ {
        TokenHierarchy<Document> h = TokenHierarchy.get(doc);
        TokenSequence ts = h.tokenSequence(TexLanguage.description());
        
        ts.move(offset);
        
        Token orig = ts.token();
        int newOffset = ts.offset();
        
        if (getToken(doc, newOffset) == orig)
            return newOffset;
        
        if (getToken(doc, newOffset + 1) == orig)
            return newOffset + 1;
        
        throw new IllegalStateException("");
    }
    
    public static boolean isTextWord(Token token) {
        return isTextWord(token.id());
    }
    
    public static boolean isTextWord(TokenId token) {
        return token == TexTokenId.WORD;
    }
    
    public static int getOffsetForLineIndex(BaseDocument doc, int line) throws BadLocationException {
        return org.netbeans.editor.Utilities.getRowStart(doc, 0, line - 1);
    }
    
    public static int getLineIndexForOffset(Document doc, int offset) throws BadLocationException, ClassCastException {
        return org.netbeans.editor.Utilities.getLineOffset((BaseDocument) doc, offset);
    }
    
    public static int countWords(Document doc) {
        TokenHierarchy<Document> h = TokenHierarchy.get(doc);
        TokenSequence<TexTokenId> ts = h.tokenSequence(TexLanguage.description());
        int count = 0;
        
        while (ts.moveNext()) {
            Token current = ts.token();
            
            if (isTextWord(current)) {
                String text   = current.text().toString();
	        if (!"a".equals(text) && !"an".equals(text) && !"the".equals(text))
                   count++;
            } //else
//                System.err.println(current.getText());
        }
        
        return count;
    }
    
//    public static Iterator getTokenIterator(Document doc) {
//        return new TokenIterator(doc);
//    }
//    
//    private static class TokenIterator implements Iterator {
//        
//        private int              index = 0;
//        
//        public TokenIterator(Document doc) {
//            tre = getTREImpl(doc);
//            index = 0;
//        }
//        
//        public boolean hasNext() {
//            return index < tre.getElementCount();
//        }
//        
//        public Object next() {
//            return (Token) tre.getElement(index);
//        }
//        
//        public void remove() {
//            throw new UnsupportedOperationException("TokenIterator does not support remove().");
//        }
//        
//    }
    
//    private static TexTokenRootElement getTREImpl(Document doc, Language language) {
//        Object tre = doc.getProperty(TokenRootElement.class);
//        
//        if (tre instanceof TexTokenRootElement)
//            return (TexTokenRootElement) tre;
//        else
//            return new TexTokenRootElement(doc, language);
//    }
//    
//    public static TexTokenRootElement getTREImpl(Document doc) {
//        Object mimeType = doc.getProperty("mime-type");
//        
//        if (mimeType == null || !(mimeType instanceof String))
//            throw new IllegalStateException("Undeterminable mime type.");
//        
//        if ("text/x-tex".equals(mimeType))
//            return getTREImpl(doc, TexLanguage.get());
//        
//        if ("text/x-bibtex".equals(mimeType))
//            return getTREImpl(doc, BiBTeXLanguage.get());
//        
//        throw new IllegalStateException("Unknown mime type: " + mimeType);
//    }
    
}
