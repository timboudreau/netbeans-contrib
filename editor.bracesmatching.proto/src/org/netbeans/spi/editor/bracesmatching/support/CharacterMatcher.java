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
package org.netbeans.spi.editor.bracesmatching.support;

import org.netbeans.modules.editor.bracesmatching.*;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 *
 * @author Vita Stejskal
 */
/* package */ final class CharacterMatcher implements BracesMatcher {

    private static final Logger LOG = Logger.getLogger(CharacterMatcher.class.getName());
    
    private final MatcherContext context;
    private final char [] charsA;
    private final char [] charsB;
    private final int lowerBound;
    private final int upperBound;
    
    private int originOffset;
    private char origin;
    private char lookingFor;
    private boolean backward;
    
    public CharacterMatcher(MatcherContext context, int lowerBound, int upperBound, char... matchingPairs) {
        this.context = context;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        
        assert matchingPairs.length % 2 == 0 : "The matchingPairs parameter must contain even number of characters."; //NOI18N
        int size = matchingPairs.length / 2;
        this.charsA = new char [size];
        this.charsB = new char [size];
        for (int i = 0; i < size; i++) {
            charsA[i] = matchingPairs[2 * i];
            charsB[i] = matchingPairs[2 * i + 1];
        }
    }
    
    // -----------------------------------------------------
    // BracesMatcher implementation
    // -----------------------------------------------------
    
    public int [] findOrigin() throws BadLocationException {
        Document doc = context.getDocument();
        int offset = context.getSearchOffset();
        int lookahead = context.getSearchLookahead();
        
        if (context.isSearchingBackward()) {
            // Maybe restrict the lookahead by the lowerBound
            if (lowerBound >= 0 && offset - lookahead < lowerBound) {
                lookahead = offset - lowerBound;
            }
            
            // check the character at the left from the caret
            Segment text = new Segment();
            doc.getText(offset - lookahead, lookahead, text);

            for(int i = lookahead - 1; i >= 0; i--) {
                if (detectOrigin(text.array[text.offset + i])) {
                    originOffset = offset - (lookahead - i);
                    return new int [] { originOffset, originOffset + 1 };
                }
            }
        } else {
            // Maybe restrict the lookahead by the lowerBound
            if (upperBound >= 0 && offset + lookahead > upperBound) {
                lookahead = upperBound - offset;
            }
            
            // check the character at the right from the caret
            Segment text = new Segment();
            doc.getText(offset, lookahead, text);

            for(int i = 0 ; i < lookahead; i++) {
                if (detectOrigin(text.array[text.offset + i])) {
                    originOffset = offset + i;
                    return new int [] { originOffset, originOffset + 1 };
                }
            }
        }
        
        return null;
    }

    public int [] findMatches() throws BadLocationException {
        Document doc = context.getDocument();
        Segment text = new Segment();
        
        if (backward) {
            int startOffset = lowerBound >= 0 ? lowerBound : 0;
            doc.getText(startOffset, originOffset - startOffset, text);
            
            int counter = 0;
            
            for(char ch = text.last(); Segment.DONE != ch; ch = text.previous()) {
                if (origin == ch) {
                    counter++;
                } else if (lookingFor == ch) {
                    if (counter == 0) {
                        int match = text.getIndex() - text.offset + startOffset;
                        return new int [] { match, match + 1 };
                    } else {
                        counter--;
                    }
                }
            }
        } else {
            int startOffset = originOffset + 1;
            doc.getText(startOffset, (upperBound >= 0 ? upperBound : doc.getLength()) - startOffset, text);
            
            int counter = 0;
            
            for(char ch = text.first(); Segment.DONE != ch; ch = text.next()) {
                if (origin == ch) {
                    counter++;
                } else if (lookingFor == ch) {
                    if (counter == 0) {
                        int match = text.getIndex() - text.offset + startOffset;
                        return new int [] { match, match + 1 };
                    } else {
                        counter--;
                    }
                }
            }
        }
        
        return null;
    }

    // -----------------------------------------------------
    // private implementation
    // -----------------------------------------------------
    
    private boolean detectOrigin(char ch) {
        int idx = find(charsA, ch);
        if (idx != -1) {
            origin = charsA[idx];
            lookingFor = charsB[idx];
            backward = false;
        } else {
            idx = find(charsB, ch);
            if (idx != -1) {
                origin = charsB[idx];
                lookingFor = charsA[idx];
                backward = true;
            }
        }
        return idx != -1;
    }
    
    private int find(char [] chars, char ch) {
        for(int i = 0; i < chars.length; i++) {
            if (chars[i] == ch) {
                return i;
            }
        }
        return -1;
    }
}
