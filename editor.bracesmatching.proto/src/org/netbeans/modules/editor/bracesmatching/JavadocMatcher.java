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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editor.bracesmatching;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.java.lexer.JavadocTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.lexer.TokenUtilities;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.netbeans.spi.editor.bracesmatching.support.BracesMatcherSupport;

/**
 *
 * @author Vita Stejskal
 */
public final class JavadocMatcher implements BracesMatcher, BracesMatcherFactory {

    private final MatcherContext context;
    
    private TokenSequence<? extends TokenId> jseq;
    private BracesMatcher defaultMatcher;
    
    public JavadocMatcher() {
        this(null);
    }

    private JavadocMatcher(MatcherContext context) {
        this.context = context;
    }
    
    // -----------------------------------------------------
    // BracesMatcher implementation
    // -----------------------------------------------------
    
    public int[] findOrigin() throws BadLocationException, InterruptedException {
        int caretOffset = context.getCaretOffset();
        TokenHierarchy<Document> th = TokenHierarchy.get(context.getDocument());
        TokenSequence<? extends TokenId> embedded = th.tokenSequence();
        List<TokenSequence<? extends TokenId>> sequences = new ArrayList<TokenSequence<? extends TokenId>>();

        do {
            TokenSequence<? extends TokenId> seq = embedded;
            embedded = null;

            sequences.add(seq);
            
            // Find the token at the caret's position
            seq.move(caretOffset);
            if (seq.moveNext()) {
                // Drill down to the embedded sequence
                embedded = seq.embedded();
            }

        } while (embedded != null);
        
        for(int i = sequences.size() - 1; i >= 0; i--) {
            TokenSequence<? extends TokenId> seq = sequences.get(i);
            if (seq.language() == JavadocTokenId.language()) {
                jseq = seq;
                break;
            }
        }
        
        assert jseq != null : "Not in javadoc"; //NOI18N
        
        // look for tags first
        jseq.move(caretOffset);
        if (jseq.moveNext()) {
            if (isTag(jseq.token())) {
                int s = jseq.offset();
                int e = jseq.offset() + jseq.token().length();
                if (s < caretOffset || !context.isSearchingBackward()) {
                    return new int [] { s, e };
                }
            }

            int limitOffset = context.isSearchingBackward() ? 
                caretOffset - context.getSearchLookahead() : 
                caretOffset + context.getSearchLookahead();

            while(moveTheSequence(jseq, context.isSearchingBackward(), limitOffset)) {
                if (isTag(jseq.token())) {
                    int s = jseq.offset();
                    int e = jseq.offset() + jseq.token().length();
                    return new int [] { s, e };
                }
            }
        }

        int seqS = getSequenceStart(jseq);
        int seqE = getSequenceEnd(jseq);
        if (seqS != -1 && seqE != -1) {
            defaultMatcher = BracesMatcherSupport.defaultMatcher(context, seqS, seqE);
            return defaultMatcher.findOrigin();
        } else {
            return null;
        }
    }

    public int[] findMatches() throws InterruptedException, BadLocationException {
        if (defaultMatcher != null) {
            return defaultMatcher.findMatches();
        }
        
        assert jseq != null : "No javadoc token sequence"; //NOI18N
        
        Token<? extends TokenId> tag = jseq.token();
        assert tag.id() == JavadocTokenId.HTML_TAG : "Wrong token"; //NOI18N
        
        if (isSingleTag(tag)) {
            return new int [] { jseq.offset(), jseq.offset() + jseq.token().length() };
        }
        
        boolean backward = !isOpeningTag(tag);
        int cnt = 0;
        
        while(moveTheSequence(jseq, backward, -1)) {
            if (!isTag(jseq.token())) {
                continue;
            }
            
            if (matchTags(tag, jseq.token())) {
                if ((backward && !isOpeningTag(jseq.token())) ||
                    (!backward && isOpeningTag(jseq.token()))
                ) {
                    cnt++;
                } else {
                    if (cnt == 0) {
                        return new int [] { jseq.offset(), jseq.offset() + jseq.token().length() };
                    } else {
                        cnt--;
                    }
                }
            }
        }
        
        return null;
    }

    // -----------------------------------------------------
    // private implementation
    // -----------------------------------------------------

    private boolean moveTheSequence(TokenSequence<? extends TokenId> seq, boolean backward, int offsetLimit) {
        if (backward) {
            if (seq.movePrevious()) {
                int e = seq.offset() + seq.token().length();
                return offsetLimit == -1 ? true : e > offsetLimit;
            }
        } else {
            if (seq.moveNext()) {
                int s = seq.offset();
                return offsetLimit == -1 ? true : s < offsetLimit;
            }
        }
        return false;
    }

    private static int getSequenceStart(TokenSequence<? extends TokenId> seq) {
        int idx = seq.index();
        seq.moveStart();
        try {
            if (seq.moveNext()) {
                return seq.offset();
            } else {
                return -1;
            }
        } finally {
            seq.moveIndex(idx);
        }
    }
    
    private static int getSequenceEnd(TokenSequence<? extends TokenId> seq) {
        int idx = seq.index();
        seq.moveEnd();
        try {
            if (seq.movePrevious()) {
                return seq.offset() + seq.token().length();
            } else {
                return -1;
            }
        } finally {
            seq.moveIndex(idx);
        }
    }
    
    private static boolean isTag(Token<? extends TokenId> tag) {
        CharSequence s = tag.text();
        int l = s.length();
        
        boolean b = tag.id() == JavadocTokenId.HTML_TAG &&
            l >= 3 &&
            s.charAt(0) == '<' && //NOI18N
            s.charAt(l - 1) == '>'; //NOI18N
        
        if (b) {
            if (s.charAt(1) == '/') { //NOI18N
                b = l >= 4 && Character.isLetterOrDigit(s.charAt(2));
            } else {
                b = Character.isLetterOrDigit(s.charAt(1));
            }
        }
        
        return b;
    }
    
    private static boolean isSingleTag(Token<? extends TokenId> tag) {
        return TokenUtilities.endsWith(tag.text(), "/>"); //NOI18N
    }
    
    private static boolean isOpeningTag(Token<? extends TokenId> tag) {
        return !TokenUtilities.startsWith(tag.text(), "</"); //NOI18N
    }
    
    private static boolean matchTags(Token<? extends TokenId> t1, Token<? extends TokenId> t2) {
        assert t1.length() >= 2 && t1.text().charAt(0) == '<' : t1 + " is not a tag."; //NOI18N
        assert t2.length() >= 2 && t2.text().charAt(0) == '<' : t2 + " is not a tag."; //NOI18N
        
        int idx1 = 1;
        int idx2 = 1;
        
        if (t1.text().charAt(1) == '/') {
            idx1++;
        } 
        
        if (t2.text().charAt(1) == '/') {
            idx2++;
        }
        
        for( ; idx1 < t1.length() && idx2 < t2.length(); idx1++, idx2++) {
            char ch1 = t1.text().charAt(idx1);
            char ch2 = t2.text().charAt(idx2);
            
            if (ch1 != ch2) {
                return false;
            }
            
            if (!Character.isLetterOrDigit(ch1)) {
                return true;
            }
        }
        
        return false;
    }
    
    // -----------------------------------------------------
    // BracesMatcherFactory implementation
    // -----------------------------------------------------
    
    /** */
    public BracesMatcher createMatcher(MatcherContext context) {
        return new JavadocMatcher(context);
    }

}
