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
package org.netbeans.modules.spellchecker.bindings.java;

import java.util.regex.Pattern;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.TokenID;
import org.netbeans.editor.TokenProcessor;
import org.netbeans.editor.ext.java.JavaTokenContext;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.openide.ErrorManager;

/**
 *
 * @author Jan Lahoda
 */
public class JavaTokenList implements TokenList {

    private BaseDocument doc;

    /** Creates a new instance of JavaTokenList */
    public JavaTokenList(BaseDocument doc) {
        this.doc = doc;
    }

    public void setStartOffset(int offset) {
        currentBlockText = null;
        currentOffsetInComment = (-1);
        this.startOffset = this.nextBlockStart = offset;
    }

    public int getCurrentWordStartOffset() {
        return currentWordOffset;
    }

    public CharSequence getCurrentWordText() {
        return currentWord;
    }

    public boolean nextWord() {
        boolean hasNext = nextWordImpl();

        while (hasNext && currentWordOffset < startOffset) {
            hasNext = nextWordImpl();
        }

        return hasNext;
    }

    private int[] findNextJavaDocComment() throws BadLocationException {
        final int[] span = new int[] {-1, -1};

        doc.getSyntaxSupport().tokenizeText(new TokenProcessor() {
            private int bufferStart;
            public int eot(int offset) {
                return 500;
            }
            public void nextBuffer(char[] buffer, int offset, int len, int startPos, int preScan, boolean lastBuffer) {
//                System.err.println("nextBuffer:");
//                System.err.println("offset=" + offset);
//                System.err.println("len=" + len);
//                System.err.println("startPos= " + startPos);
//                System.err.println("preScan=" + preScan);
//                System.err.println("lastBuffer= " + lastBuffer);
                bufferStart = startPos - offset;
            }
            public boolean token(TokenID tokenID, TokenContextPath tokenContextPath, int tokenBufferOffset, int tokenLength) {
                try {
                    if (tokenID == JavaTokenContext.BLOCK_COMMENT) {
//                        System.err.println("block comment:");
//                        System.err.println("tokenBufferOffset=" + tokenBufferOffset);
//                        System.err.println("tokenLength=" + tokenLength);
                        int start = tokenBufferOffset + bufferStart;
                        int end = start + tokenLength;
                        String pattern = doc.getText(start, end - start);

//                        System.err.println("pattern = " + pattern);
                        if (pattern.startsWith("/**")) {
                            span[0] = start;
                            span[1] = end;
                            return false;
                        }
                    }
                    
                    return true;
                } catch (BadLocationException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    return false;
                }
            }
        }, nextBlockStart, doc.getLength(), false);

        return span;
    }
    
    private void handleJavadocTag(CharSequence tag) {
        if ("@see".contentEquals(tag) || "@throws".contentEquals(tag)) {
            //ignore next "word", possibly dotted and hashed
            Pair<CharSequence, Integer> data = wordBroker(currentBlockText, currentOffsetInComment, true);
            
            currentOffsetInComment = data.b + data.a.length();
            return ;
        }
        
        if ("@param".contentEquals(tag)) {
            //ignore next word
            Pair<CharSequence, Integer> data = wordBroker(currentBlockText, currentOffsetInComment, false);
            
            currentOffsetInComment = data.b + data.a.length();
            return ;
        }
        
        if ("@author".contentEquals(tag)) {
            //ignore everything till the end of the line:
            Pair<CharSequence, Integer> data = wordBroker(currentBlockText, currentOffsetInComment, false);
            
            while (data != null) {
                currentOffsetInComment = data.b + data.a.length();
                
                if ('\n' == data.a.charAt(0)) {
                    //continue
                    return ;
                }
                
                data = wordBroker(currentBlockText, currentOffsetInComment, false);
            }
            
            return ;
        }
    }

    private boolean nextWordImpl() {
        try {
            while (true) {
                if (currentBlockText == null) {
                    int[] span = findNextJavaDocComment();

                    if (span[0] == (-1))
                        return false;

                    currentBlockStart = span[0];
                    currentBlockText = doc.getText(span[0], span[1] - span[0]);
                    currentOffsetInComment = 0;

                    nextBlockStart = span[1];
                }

                String pairTag = null;
                Pair<CharSequence, Integer> data = wordBroker(currentBlockText, currentOffsetInComment, false);

                while (data != null) {
                    currentOffsetInComment = data.b + data.a.length();

                    if (pairTag == null) {
                        if (Character.isLetter(data.a.charAt(0)) && !isIdentifierLike(data.a)) {
                            //TODO: check for identifiers:
                            currentWordOffset = currentBlockStart + data.b;
                            currentWord = data.a;
                            return true;
                        }
                        
                        switch (data.a.charAt(0)) {
                            case '@':
                                handleJavadocTag(data.a);
                                break;
                            case '<':
                                if (startsWith(data.a, "<a "))
                                    pairTag = "</a>";
                                if (startsWith(data.a, "<code>"))
                                    pairTag = "</code>";
                                if (startsWith(data.a, "<pre>"))
                                    pairTag = "</pre>";
                                break;
                            case '{':
                                pairTag = "}";
                                break;
                        }
                    } else {
                        if (pairTag.contentEquals(data.a))
                            pairTag = null;
                    }

                    data = wordBroker(currentBlockText, currentOffsetInComment, false);
                }
                
                currentBlockText = null;
            }
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
            return false;
        }
    }

    private static boolean startsWith(CharSequence where, String withWhat) {
        if (where.length() >= withWhat.length()) {
            return withWhat.contentEquals(where.subSequence(0, withWhat.length()));
        }

        return false;
    }

    static boolean isIdentifierLike(CharSequence s) {
        boolean hasCapitalsInside = false;
        int offset = 1;

        while (offset < s.length() && !hasCapitalsInside) {
            hasCapitalsInside |= Character.isUpperCase(s.charAt(offset));

            offset++;
        }

        return hasCapitalsInside;
    }

    private int   currentBlockStart;
    private int   nextBlockStart;
    private String currentBlockText;
    private int currentOffsetInComment;

    private int currentWordOffset;
    private CharSequence currentWord;

    private int startOffset;
    
    private static final Pattern commentPattern = Pattern.compile("/\\*\\*([^*]*(\\*[^/][^*]*)*)\\*/", Pattern.MULTILINE | Pattern.DOTALL); //NOI18N
    private static final Pattern wordPattern = Pattern.compile("[A-Za-z]+"); //NOI18N

    private boolean isLetter(char c) {
        return Character.isLetter(c) || c == '\'';
    }
    
    private Pair<CharSequence, Integer> wordBroker(CharSequence start, int offset, boolean treatSpecialCharactersAsLetterInsideWords) {
        int state = 0;
        int offsetStart = offset;

        while (start.length() > offset) {
            char current = start.charAt(offset);

            switch (state) {
                case 0:
                    if (isLetter(current)) {
                        state = 1;
                        offsetStart = offset;
                        break;
                    }
                    if (current == '@' || current == '#') {
                        state = 2;
                        offsetStart = offset;
                        break;
                    }
                    if (current == '<') {
                        state = 3;
                        offsetStart = offset;
                        break;
                    }
                    if (current == '\n' || current == '}') {
                        return new Pair<CharSequence, Integer>(start.subSequence(offset, offset + 1), offset);
                    }
                    if (current == '{') {
                        state = 4;
                        offsetStart = offset;
                        break;
                    }
                    break;

                case 1:
                    if (!isLetter(current) && ((current != '.' && current != '#') || !treatSpecialCharactersAsLetterInsideWords)) {
                        return new Pair<CharSequence, Integer>(start.subSequence(offsetStart, offset), offsetStart);
                    }

                    break;

                case 2:
                    if (!isLetter(current)) {
                        return new Pair<CharSequence, Integer>(start.subSequence(offsetStart, offset), offsetStart);
                    }

                    break;

                case 3:
                    if (current == '>') {
                        return new Pair<CharSequence, Integer>(start.subSequence(offsetStart, offset + 1), offsetStart);
                    }

                    break;
                    
                case 4:
                    if (current == '@') {
                        state = 2;
                        break;
                    }
                    
                    offset--;
                    state = 0;
                    break;
            }

            offset++;
        }

        if (offset > offsetStart) {
            return new Pair<CharSequence, Integer>(start.subSequence(offsetStart, offset), offsetStart);
        } else {
            return null;
        }
    }

    public void addChangeListener(ChangeListener l) {
        //ignored...
    }

    public void removeChangeListener(ChangeListener l) {
        //ignored...
    }

    private static class Pair<A, B> {

        private A a;
        private B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }
}
