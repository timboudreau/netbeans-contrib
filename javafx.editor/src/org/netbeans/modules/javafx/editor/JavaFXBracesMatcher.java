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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.javafx.editor;

import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.netbeans.spi.editor.bracesmatching.support.BracesMatcherSupport;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vita Stejskal
 */
public final class JavaFXBracesMatcher implements BracesMatcher, BracesMatcherFactory {
    private static final char[] PAIRS = new char[]{'(', ')', '[', ']', '{', '}'}; //NOI18N
    private static final JFXTokenId[] PAIR_TOKEN_IDS = new JFXTokenId[]{
            JFXTokenId.LPAREN, JFXTokenId.RPAREN,
            JFXTokenId.LBRACKET, JFXTokenId.RBRACKET,
            JFXTokenId.LBRACE, JFXTokenId.RBRACE,
    };


    private final MatcherContext context;

    private int originOffset;
    private char originChar;
    private char matchingChar;
    private boolean backward;

    public JavaFXBracesMatcher() {
        this(null);
    }

    private JavaFXBracesMatcher(MatcherContext context) {
        this.context = context;
    }

    // -----------------------------------------------------
    // BracesMatcher implementation
    // -----------------------------------------------------

    public int[] findOrigin() throws BadLocationException, InterruptedException {
        int[] origin = BracesMatcherSupport.findChar(
                context.getDocument(),
                context.getSearchOffset(),
                context.getLimitOffset(),
                PAIRS
        );

        if (origin != null) {
            originOffset = origin[0];
            originChar = PAIRS[origin[1]];
            matchingChar = PAIRS[origin[1] + origin[2]];
            backward = origin[2] < 0;
            return new int[]{originOffset, originOffset + 1};
        } else {
            return null;
        }
    }

    public int[] findMatches() throws InterruptedException, BadLocationException {
        TokenHierarchy<Document> th = TokenHierarchy.get(context.getDocument());
        List<TokenSequence<? extends TokenId>> sequences = getEmbeddedTokenSequences(
                th, originOffset, backward, JFXTokenId.language());

        if (!sequences.isEmpty()) {
            // Check special tokens
            TokenSequence<? extends TokenId> seq = sequences.get(sequences.size() - 1);
            seq.move(originOffset);
            if (seq.moveNext()) {
                int offset = -1;
                final TokenId id = seq.token().id();
                if (id == JFXTokenId.STRING_LITERAL || id == JFXTokenId.COMMENT || id == JFXTokenId.LINE_COMMENT) {
                    offset = BracesMatcherSupport.matchChar(
                            context.getDocument(),
                            backward ? originOffset : originOffset + 1,
                            backward ? seq.offset() : seq.offset() + seq.token().length(),
                            originChar,
                            matchingChar);

                } else if (id == JFXTokenId.QUOTE_LBRACE_STRING_LITERAL ||
                        id == JFXTokenId.RBRACE_QUOTE_STRING_LITERAL ||
                        id == JFXTokenId.RBRACE_LBRACE_STRING_LITERAL) {
                    offset = BracesMatcherSupport.matchChar(
                            context.getDocument(),
                            backward ? originOffset : originOffset + 1,
                            backward ? Utilities.getRowStart((BaseDocument) context.getDocument(), originOffset)
                                    : Utilities.getRowEnd((BaseDocument) context.getDocument(), originOffset),
                            originChar,
                            matchingChar);
                }
                if (offset != -1) {
                    return new int[]{offset, offset + 1};
//                } else {
//                    return null;
                }
            }

            // We are in plain java

            List<TokenSequence<?>> list;
            if (backward) {
                list = th.tokenSequenceList(seq.languagePath(), 0, originOffset);
            } else {
                list = th.tokenSequenceList(seq.languagePath(), originOffset + 1, context.getDocument().getLength());
            }

            JFXTokenId originId = getTokenId(originChar);
            JFXTokenId lookingForId = getTokenId(matchingChar);
            int counter = 0;

            for (TokenSequenceIterator tsi = new TokenSequenceIterator(new ArrayList<TokenSequence<? extends TokenId>>(list), backward); tsi.hasMore();) {
                TokenSequence<? extends TokenId> sq = tsi.getSequence();

                if (originId == sq.token().id()) {
                    counter++;
                } else if (lookingForId == sq.token().id()) {
                    if (counter == 0) {
                        return new int[]{sq.offset(), sq.offset() + sq.token().length()};
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

    private JFXTokenId getTokenId(char ch) {
        for (int i = 0; i < PAIRS.length; i++) {
            if (PAIRS[i] == ch) {
                return PAIR_TOKEN_IDS[i];
            }
        }
        return null;
    }

    public static List<TokenSequence<? extends TokenId>> getEmbeddedTokenSequences(
            TokenHierarchy<?> th, int offset, boolean backwardBias, Language<? extends TokenId> language) {
        List<TokenSequence<?>> sequences = th.embeddedTokenSequences(offset, backwardBias);

        for (int i = sequences.size() - 1; i >= 0; i--) {
            TokenSequence<? extends TokenId> seq = sequences.get(i);
            if (seq.language() == language) {
                break;
            } else {
                sequences.remove(i);
            }
        }

        return new ArrayList<TokenSequence<? extends TokenId>>(sequences);
    }

    private static final class TokenSequenceIterator {

        private final List<TokenSequence<? extends TokenId>> list;
        private final boolean backward;

        private int index;

        public TokenSequenceIterator(List<TokenSequence<? extends TokenId>> list, boolean backward) {
            this.list = list;
            this.backward = backward;
            this.index = -1;
        }

        public boolean hasMore() {
            return backward ? hasPrevious() : hasNext();
        }

        public TokenSequence<? extends TokenId> getSequence() {
            assert index >= 0 && index < list.size() : "No sequence available, call hasMore() first."; //NOI18N
            return list.get(index);
        }

        private boolean hasPrevious() {
            boolean anotherSeq = false;

            if (index == -1) {
                index = list.size() - 1;
                anotherSeq = true;
            }

            for (; index >= 0; index--) {
                TokenSequence<? extends TokenId> seq = list.get(index);
                if (anotherSeq) {
                    seq.moveEnd();
                }

                if (seq.movePrevious()) {
                    return true;
                }

                anotherSeq = true;
            }

            return false;
        }

        private boolean hasNext() {
            boolean anotherSeq = false;

            if (index == -1) {
                index = 0;
                anotherSeq = true;
            }

            for (; index < list.size(); index++) {
                TokenSequence<? extends TokenId> seq = list.get(index);
                if (anotherSeq) {
                    seq.moveStart();
                }

                if (seq.moveNext()) {
                    return true;
                }

                anotherSeq = true;
            }

            return false;
        }
    } // End of TokenSequenceIterator class

    // -----------------------------------------------------
    // BracesMatcherFactory implementation
    // -----------------------------------------------------

    /** */
    public BracesMatcher createMatcher(MatcherContext context) {
        return new JavaFXBracesMatcher(context);
    }

}
