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
package org.netbeans.modules.scala.editing.lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.gsf.api.CompilationInfo;

import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.TranslatedSource;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.scala.editing.ScalaMimeResolver;
import org.netbeans.modules.scala.editing.nodes.AstNode;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 * Utilities associated with lexing or analyzing the document at the
 * lexical level, unlike AstUtilities which is contains utilities
 * to analyze parsed information about a document.
 *
 * @author Caoyuan Deng
 * @author Tor Norbye
 */
public class ScalaLexUtilities {

    /** Tokens that match a corresponding END statement. Even though while, unless etc.
     * can be statement modifiers, those luckily have different token ids so are not a problem
     * here.
     */
    private static final Set<ScalaTokenId> END_PAIRS = new HashSet<ScalaTokenId>();
    /**
     * Tokens that should cause indentation of the next line. This is true for all {@link #END_PAIRS},
     * but also includes tokens like "else" that are not themselves matched with end but also contribute
     * structure for indentation.
     *
     */
    private static final Set<ScalaTokenId> INDENT_WORDS = new HashSet<ScalaTokenId>();


    static {
        INDENT_WORDS.add(ScalaTokenId.Class);
        INDENT_WORDS.add(ScalaTokenId.Object);
        INDENT_WORDS.add(ScalaTokenId.Trait);
        INDENT_WORDS.add(ScalaTokenId.Do);
        INDENT_WORDS.add(ScalaTokenId.For);
        INDENT_WORDS.add(ScalaTokenId.While);
        INDENT_WORDS.add(ScalaTokenId.Case);
        INDENT_WORDS.add(ScalaTokenId.If);
        INDENT_WORDS.add(ScalaTokenId.Else);

        INDENT_WORDS.addAll(END_PAIRS);
    // Add words that are not matched themselves with an "end",
    // but which also provide block structure to indented content
    // (usually part of a multi-keyword structure such as if-then-elsif-else-end
    // where only the "if" is considered an end-pair.)

    // XXX What about BEGIN{} and END{} ?
    }

    private ScalaLexUtilities() {
    }

    /** 
     * Return the comment sequence (if any) for the comment prior to the given offset.
     */
//    public static TokenSequence<? extends FortressCommentTokenId> getCommentFor(BaseDocument doc, int offset) {
//        TokenSequence<?extends ScalaTokenId> jts = getTokenSequence(doc, offset);
//        if (jts == null) {
//            return null;
//        }
//        jts.move(offset);
//        
//        while (jts.movePrevious()) {
//            TokenId id = jts.token().id();
//            if (id == ScalaTokenId.BLOCK_COMMENT) {
//                return jts.embedded(FortressCommentTokenId.language());
//            } else if (id != ScalaTokenId.WHITESPACE && id != ScalaTokenId.EOL) {
//                return null;
//            }
//        }
//        
//        return null;
//    }
    /** For a possibly generated offset in an AST, return the corresponding lexing/true document offset */
    public static int getLexerOffset(CompilationInfo info, int astOffset) {
        ParserResult result = info.getEmbeddedResult(ScalaMimeResolver.MIME_TYPE, 0);
        if (result != null) {
            TranslatedSource ts = result.getTranslatedSource();
            if (ts != null) {
                return ts.getLexicalOffset(astOffset);
            }
        }

        return astOffset;
    }

    public static OffsetRange getLexerOffsets(CompilationInfo info, OffsetRange astRange) {
        ParserResult result = info.getEmbeddedResult(ScalaMimeResolver.MIME_TYPE, 0);
        if (result != null) {
            TranslatedSource ts = result.getTranslatedSource();
            if (ts != null) {
                int rangeStart = astRange.getStart();
                int start = ts.getLexicalOffset(rangeStart);
                if (start == rangeStart) {
                    return astRange;
                } else if (start == -1) {
                    return OffsetRange.NONE;
                } else {
                    // Assumes the translated range maintains size
                    return new OffsetRange(start, start + astRange.getLength());
                }
            }
        }

        return astRange;
    }

    /** Find the Fortress token sequence (in case it's embedded in something else at the top level */
    @SuppressWarnings("unchecked")
    public static TokenSequence<ScalaTokenId> getTokenSequence(BaseDocument doc, int offset) {
        TokenHierarchy<Document> th = TokenHierarchy.get((Document) doc);
        return getTokenSequence(th, offset);
    }

    @SuppressWarnings("unchecked")
    public static TokenSequence<ScalaTokenId> getTokenSequence(final TokenHierarchy th, int offset) {
        TokenSequence<ScalaTokenId> ts = th.tokenSequence(ScalaTokenId.language());

        if (ts == null) {
            // Possibly an embedding scenario such as an RHTML file
            // First try with backward bias true
            List<TokenSequence> list = th.embeddedTokenSequences(offset, true);

            for (TokenSequence t : list) {
                if (t.language() == ScalaTokenId.language()) {
                    ts = t;

                    break;
                }
            }

            if (ts == null) {
                list = th.embeddedTokenSequences(offset, false);

                for (TokenSequence t : list) {
                    if (t.language() == ScalaTokenId.language()) {
                        ts = t;

                        break;
                    }
                }
            }
        }

        return ts;
    }

    public static TokenSequence<ScalaTokenId> getPositionedSequence(BaseDocument doc, int offset) {
        return getPositionedSequence(doc, offset, true);
    }

    public static TokenSequence<ScalaTokenId> getPositionedSequence(BaseDocument doc, int offset, boolean lookBack) {
        TokenSequence<ScalaTokenId> ts = getTokenSequence(doc, offset);

        if (ts != null) {
            try {
                ts.move(offset);
            } catch (AssertionError e) {
                DataObject dobj = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);

                if (dobj != null) {
                    Exceptions.attachMessage(e, FileUtil.getFileDisplayName(dobj.getPrimaryFile()));
                }

                throw e;
            }

            if (!lookBack && !ts.moveNext()) {
                return null;
            } else if (lookBack && !ts.moveNext() && !ts.movePrevious()) {
                return null;
            }

            return ts;
        }

        return null;
    }

    public static Token<ScalaTokenId> getToken(BaseDocument doc, int offset) {
        TokenSequence<ScalaTokenId> ts = getPositionedSequence(doc, offset);

        if (ts != null) {
            return ts.token();
        }

        return null;
    }

    public static char getTokenChar(BaseDocument doc, int offset) {
        Token<ScalaTokenId> token = getToken(doc, offset);

        if (token != null) {
            String text = token.text().toString();

            if (text.length() > 0) { // Usually true, but I could have gotten EOF right?

                return text.charAt(0);
            }
        }

        return 0;
    }
    private static final List<ScalaTokenId> WS_COMMENT = Arrays.asList(
            ScalaTokenId.Ws,
            ScalaTokenId.Nl,
            ScalaTokenId.LineComment,
            ScalaTokenId.DocCommentStart,
            ScalaTokenId.DocCommentEnd,
            ScalaTokenId.BlockCommentStart,
            ScalaTokenId.BlockCommentEnd,
            ScalaTokenId.BlockCommentData);

    public static Token<ScalaTokenId> findNextNonWsNonComment(TokenSequence<ScalaTokenId> ts) {
        return findNext(ts, WS_COMMENT);
    }

    public static Token<ScalaTokenId> findPreviousNonWsNonComment(TokenSequence<ScalaTokenId> ts) {
        return findPrevious(ts, WS_COMMENT);
    }
    private static final List<ScalaTokenId> WS = Arrays.asList(
            ScalaTokenId.Ws,
            ScalaTokenId.Nl);

    public static Token<ScalaTokenId> findNextNonWs(TokenSequence<ScalaTokenId> ts) {
        return findNext(ts, WS);
    }

    public static Token<ScalaTokenId> findPreviousNonWs(TokenSequence<ScalaTokenId> ts) {
        return findPrevious(ts, WS);
    }

    public static Token<ScalaTokenId> findNext(TokenSequence<ScalaTokenId> ts, List<ScalaTokenId> ignores) {
        if (ignores.contains(ts.token().id())) {
            while (ts.moveNext() && ignores.contains(ts.token().id())) {
            }
        }
        return ts.token();
    }

    public static Token<ScalaTokenId> findPrevious(TokenSequence<ScalaTokenId> ts, List<ScalaTokenId> ignores) {
        if (ignores.contains(ts.token().id())) {
            while (ts.movePrevious() && ignores.contains(ts.token().id())) {
            }
        }
        return ts.token();
    }

    public static Token<ScalaTokenId> findNext(TokenSequence<ScalaTokenId> ts, ScalaTokenId id) {
        if (ts.token().id() != id) {
            while (ts.moveNext() && ts.token().id() != id) {
            }
        }
        return ts.token();
    }

    public static Token<ScalaTokenId> findNextIn(TokenSequence<ScalaTokenId> ts, List<ScalaTokenId> includes) {
        if (!includes.contains(ts.token().id())) {
            while (ts.moveNext() && !includes.contains(ts.token().id())) {
            }
        }
        return ts.token();
    }

    public static Token<ScalaTokenId> findPrevious(TokenSequence<ScalaTokenId> ts, ScalaTokenId id) {
        if (ts.token().id() != id) {
            while (ts.movePrevious() && ts.token().id() != id) {
            }
        }
        return ts.token();
    }

    public static Token<ScalaTokenId> findNextIncluding(TokenSequence<ScalaTokenId> ts, List<ScalaTokenId> includes) {
        while (ts.moveNext() && !includes.contains(ts.token().id())) {
        }
        return ts.token();
    }

    public static Token<ScalaTokenId> findPreviousIncluding(TokenSequence<ScalaTokenId> ts, List<ScalaTokenId> includes) {
        if (!includes.contains(ts.token().id())) {
            while (ts.movePrevious() && !includes.contains(ts.token().id())) {
            }
        }
        return ts.token();
    }

    static boolean skipParenthesis(TokenSequence<ScalaTokenId> ts) {
        return skipParenthesis(ts, false);
    }

    /**
     * Tries to skip parenthesis 
     */
    public static boolean skipParenthesis(TokenSequence<ScalaTokenId> ts, boolean back) {
        int balance = 0;

        Token<ScalaTokenId> token = ts.token();
        if (token == null) {
            return false;
        }

        TokenId id = token.id();

        // skip whitespace and comment
        if (isWsComment(id)) {
            while ((back ? ts.movePrevious() : ts.moveNext()) && isWsComment(id)) {
            }
        }

        // if current token is not parenthesis
        if (ts.token().id() != (back ? ScalaTokenId.RParen : ScalaTokenId.LParen)) {
            return false;
        }

        do {
            token = ts.token();
            id = token.id();

            if (id == (back ? ScalaTokenId.RParen : ScalaTokenId.LParen)) {
                balance++;
            } else if (id == (back ? ScalaTokenId.LParen : ScalaTokenId.RParen)) {
                if (balance == 0) {
                    return false;
                } else if (balance == 1) {
                    if (back) {
                        ts.movePrevious();
                    } else {
                        ts.moveNext();
                    }
                    return true;
                }

                balance--;
            }
        } while (back ? ts.movePrevious() : ts.moveNext());

        return false;
    }

    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    public static OffsetRange findFwd(BaseDocument doc, TokenSequence<ScalaTokenId> ts, TokenId up,
            TokenId down) {
        int balance = 0;

        while (ts.moveNext()) {
            Token<ScalaTokenId> token = ts.token();
            TokenId id = token.id();

            if (id == up) {
                balance++;
            } else if (id == down) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    /** Search backwards in the token sequence until a token of type <code>up</code> is found */
    public static OffsetRange findBwd(BaseDocument doc, TokenSequence<ScalaTokenId> ts, TokenId up,
            TokenId down) {
        int balance = 0;

        while (ts.movePrevious()) {
            Token<ScalaTokenId> token = ts.token();
            TokenId id = token.id();

            if (id == up) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance++;
            } else if (id == down) {
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    public static OffsetRange findFwd(BaseDocument doc, TokenSequence<ScalaTokenId> ts, String up,
            String down) {
        int balance = 0;

        while (ts.moveNext()) {
            Token<ScalaTokenId> token = ts.token();
            TokenId id = token.id();
            String text = token.text().toString();

            if (text.equals(up)) {
                balance++;
            } else if (text.equals(down)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    /** Search backwards in the token sequence until a token of type <code>up</code> is found */
    public static OffsetRange findBwd(BaseDocument doc, TokenSequence<ScalaTokenId> ts, String up,
            String down) {
        int balance = 0;

        while (ts.movePrevious()) {
            Token<ScalaTokenId> token = ts.token();
            TokenId id = token.id();
            String text = token.text().toString();

            if (text.equals(up)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance++;
            } else if (text.equals(down)) {
                balance--;
            }
        }

        return OffsetRange.NONE;
    }

    /** Find the token that begins a block terminated by "end". This is a token
     * in the END_PAIRS array. Walk backwards and find the corresponding token.
     * It does not use indentation for clues since this could be wrong and be
     * precisely the reason why the user is using pair matching to see what's wrong.
     */
    public static OffsetRange findBegin(BaseDocument doc, TokenSequence<ScalaTokenId> ts) {
        int balance = 0;

        while (ts.movePrevious()) {
            Token<ScalaTokenId> token = ts.token();
            String text = token.text().toString();

            if (isBeginToken(text, doc, ts.offset())) {
                // No matching dot for "do" used in conditionals etc.)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance--;
            } else if (text.equals("end")) {
                balance++;
            }
        }

        return OffsetRange.NONE;
    }

    public static OffsetRange findEnd(BaseDocument doc, TokenSequence<ScalaTokenId> ts) {
        int balance = 0;

        while (ts.moveNext()) {
            Token<ScalaTokenId> token = ts.token();
            String text = token.text().toString();

            if (isBeginToken(text, doc, ts.offset())) {
                balance--;
            } else if (text.equals("end")) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance++;
            }
        }

        return OffsetRange.NONE;
    }

    /** Determine whether "do" is an indent-token (e.g. matches an end) or if
     * it's simply a separator in while,until,for expressions)
     */
    public static boolean isEndmatchingDo(BaseDocument doc, int offset) {
        // In the following case, do is dominant:
        //     expression.do 
        //        whatever
        //     end
        //
        // However, not here:
        //     while true do
        //        whatever
        //     end
        //
        // In the second case, the end matches the while, but in the first case
        // the end matches the do

        // Look at the first token of the current line
        try {
            int first = Utilities.getRowFirstNonWhite(doc, offset);
            if (first != -1) {
                Token<ScalaTokenId> token = getToken(doc, first);
                if (token != null) {
                    String text = token.text().toString();
                    if (text.equals("while") || text.equals("for")) {
                        return false;
                    }
                }
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return true;
    }

    /**
     * Return true iff the given token is a token that should be matched
     * with a corresponding "end" token, such as "begin", "def", "module",
     * etc.
     */
    public static boolean isBeginToken(String tokenText, BaseDocument doc, int offset) {
        return END_PAIRS.contains(tokenText);
    }

    /**
     * Return true iff the given token is a token that should be matched
     * with a corresponding "end" token, such as "begin", "def", "module",
     * etc.
     */
    public static boolean isEndToken(String tokenText, BaseDocument doc, int offset) {
        return END_PAIRS.contains(tokenText);
    }

    private static OffsetRange findMultilineRange(TokenSequence<ScalaTokenId> ts) {
        int startOffset = ts.offset();
        Token<ScalaTokenId> token = ts.token();
        ScalaTokenId id = token.id();
        switch (id) {
            case Else:
                ts.moveNext();
                id = ts.token().id();
                break;
            case If:
            case For:
            case While:
                ts.moveNext();
                if (!skipParenthesis(ts, false)) {
                    return OffsetRange.NONE;
                }
                id = ts.token().id();
                break;
            default:
                return OffsetRange.NONE;
        }

        boolean eolFound = false;
        int lastEolOffset = ts.offset();

        // skip whitespaces and comments
        if (isWsComment(id)) {
            if (ts.token().id() == ScalaTokenId.Nl) {
                lastEolOffset = ts.offset();
                eolFound = true;
            }
            while (ts.moveNext() && isWsComment(ts.token().id())) {
                if (ts.token().id() == ScalaTokenId.Nl) {
                    lastEolOffset = ts.offset();
                    eolFound = true;
                }
            }
        }
        // if we found end of sequence or end of line
        if (ts.token() == null || (ts.token().id() != ScalaTokenId.LBrace && eolFound)) {
            return new OffsetRange(startOffset, lastEolOffset);
        }
        return OffsetRange.NONE;
    }

    public static OffsetRange getMultilineRange(BaseDocument doc, TokenSequence<ScalaTokenId> ts) {
        int index = ts.index();
        OffsetRange offsetRange = findMultilineRange(ts);
        ts.moveIndex(index);
        ts.moveNext();
        return offsetRange;
    }

    /**
     * Return true iff the given token is a token that indents its content,
     * such as the various begin tokens as well as "else", "when", etc.
     */
    public static boolean isIndentToken(Token token) {
        return INDENT_WORDS.contains(token.text().toString());
    }

    /** Compute the balance of begin/end tokens on the line.
     * @param doc the document
     * @param offset The offset somewhere on the line
     * @param upToOffset If true, only compute the line balance up to the given offset (inclusive),
     *   and if false compute the balance for the whole line
     */
    public static int getBeginEndLineBalance(BaseDocument doc, int offset, boolean upToOffset) {
        try {
            int begin = Utilities.getRowStart(doc, offset);
            int end = upToOffset ? offset : Utilities.getRowEnd(doc, offset);

            TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(doc, begin);
            if (ts == null) {
                return 0;
            }

            ts.move(begin);

            if (!ts.moveNext()) {
                return 0;
            }

            int balance = 0;

            do {
                Token<ScalaTokenId> token = ts.token();
                String text = token.text().toString();

                if (isBeginToken(text, doc, ts.offset())) {
                    balance++;
                } else if (isEndToken(text, doc, ts.offset())) {
                    balance--;
                }
            } while (ts.moveNext() && (ts.offset() <= end));

            return balance;
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);

            return 0;
        }
    }

    /** Compute the balance of begin/end tokens on the line */
    public static Stack<Token> getLineBalance(BaseDocument doc, int offset, TokenId up, TokenId down) {
        Stack<Token> balanceStack = new Stack<Token>();
        try {
            int begin = Utilities.getRowStart(doc, offset);
            int end = Utilities.getRowEnd(doc, offset);

            TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(doc, begin);
            if (ts == null) {
                return balanceStack;
            }

            ts.move(begin);

            if (!ts.moveNext()) {
                return balanceStack;
            }

            int balance = 0;

            do {
                Token<ScalaTokenId> token = ts.offsetToken();
                TokenId id = token.id();

                if (id == up) {
                    balanceStack.push(token);
                    balance++;
                } else if (id == down) {
                    if (!balanceStack.empty()) {
                        balanceStack.pop();
                    }
                    balance--;
                }
            } while (ts.moveNext() && (ts.offset() <= end));

            return balanceStack;
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);

            return balanceStack;
        }
    }

    /**
     * The same as braceBalance but generalized to any pair of matching
     * tokens.
     * @param open the token that increses the count
     * @param close the token that decreses the count
     */
    public static int getTokenBalance(BaseDocument doc, TokenId open, TokenId close, int offset)
            throws BadLocationException {
        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(doc, 0);
        if (ts == null) {
            return 0;
        }

        // XXX Why 0? Why not offset?
        ts.moveIndex(0);

        if (!ts.moveNext()) {
            return 0;
        }

        int balance = 0;

        do {
            Token t = ts.token();

            if (t.id() == open) {
                balance++;
            } else if (t.id() == close) {
                balance--;
            }
        } while (ts.moveNext());

        return balance;
    }

    /**
     * The same as braceBalance but generalized to any pair of matching
     * tokens.
     * @param open the token that increses the count
     * @param close the token that decreses the count
     */
    public static int getTokenBalance(BaseDocument doc, String open, String close, int offset)
            throws BadLocationException {
        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(doc, 0);
        if (ts == null) {
            return 0;
        }

        // XXX Why 0? Why not offset?
        ts.moveIndex(0);

        if (!ts.moveNext()) {
            return 0;
        }

        int balance = 0;

        do {
            Token token = ts.token();
            String text = token.text().toString();

            if (text.equals(open)) {
                balance++;
            } else if (text.equals(text)) {
                balance--;
            }
        } while (ts.moveNext());

        return balance;
    }

    /**
     * Return true iff the line for the given offset is a JavaScript comment line.
     * This will return false for lines that contain comments (even when the
     * offset is within the comment portion) but also contain code.
     */
    public static boolean isCommentOnlyLine(BaseDocument doc, int offset)
            throws BadLocationException {
        int begin = Utilities.getRowFirstNonWhite(doc, offset);

        if (begin == -1) {
            return false; // whitespace only

        }

        Token<ScalaTokenId> token = ScalaLexUtilities.getToken(doc, begin);
        if (token != null) {
            return token.id() == ScalaTokenId.LineComment;
        }

        return false;
    }

    /**
     * Return the string at the given position, or null if none
     */
    @SuppressWarnings("unchecked")
    public static String getStringAt(int caretOffset, TokenHierarchy<Document> th) {
        TokenSequence<ScalaTokenId> ts = getTokenSequence(th, caretOffset);

        if (ts == null) {
            return null;
        }

        ts.move(caretOffset);

        if (!ts.moveNext() && !ts.movePrevious()) {
            return null;
        }

        if (ts.offset() == caretOffset) {
            // We're looking at the offset to the RIGHT of the caret
            // and here I care about what's on the left
            ts.movePrevious();
        }

        Token<ScalaTokenId> token = ts.token();

        if (token != null) {
            TokenId id = token.id();

//            // We're within a String that has embedded Js. Drop into the
//            // embedded language and see if we're within a literal string there.
//            if (id == ScalaTokenId.EMBEDDED_RUBY) {
//                ts = (TokenSequence)ts.embedded();
//                assert ts != null;
//                ts.move(caretOffset);
//
//                if (!ts.moveNext() && !ts.movePrevious()) {
//                    return null;
//                }
//
//                token = ts.token();
//                id = token.id();
//            }
//
            String string = null;

            // Skip over embedded Js segments and literal strings until you find the beginning
            int segments = 0;

            while ((id == ScalaTokenId.Error) || (id == ScalaTokenId.StringLiteral)) {
                string = token.text().toString();
                segments++;
                ts.movePrevious();
                token = ts.token();
                id = token.id();
            }

            if (id == ScalaTokenId.STRING_BEGIN) {
                if (segments == 1) {
                    return string;
                } else {
                    // Build up the String from the sequence
                    StringBuilder sb = new StringBuilder();

                    while (ts.moveNext()) {
                        token = ts.token();
                        id = token.id();

                        if ((id == ScalaTokenId.Error) || (id == ScalaTokenId.StringLiteral)) {
                            sb.append(token.text());
                        } else {
                            break;
                        }
                    }

                    return sb.toString();
                }
            }
        }

        return null;
    }

//    /**
//     * Check if the caret is inside a literal string that is associated with
//     * a require statement.
//     *
//     * @return The offset of the beginning of the require string, or -1
//     *     if the offset is not inside a require string.
//     */
//    public static int getRequireStringOffset(int caretOffset, TokenHierarchy<Document> th) {
//        TokenSequence<?extends ScalaTokenId> ts = getTokenSequence(th, caretOffset);
//
//        if (ts == null) {
//            return -1;
//        }
//
//        ts.move(caretOffset);
//
//        if (!ts.moveNext() && !ts.movePrevious()) {
//            return -1;
//        }
//
//        if (ts.offset() == caretOffset) {
//            // We're looking at the offset to the RIGHT of the caret
//            // and here I care about what's on the left
//            ts.movePrevious();
//        }
//
//        Token<?extends ScalaTokenId> token = ts.token();
//
//        if (token != null) {
//            TokenId id = token.id();
//
//            // Skip over embedded Js segments and literal strings until you find the beginning
//            while ((id == ScalaTokenId.ERROR) || (id == ScalaTokenId.STRING_LITERAL)) {
//                ts.movePrevious();
//                token = ts.token();
//                id = token.id();
//            }
//
//            int stringStart = ts.offset() + token.length();
//
//            if (id == ScalaTokenId.STRING_BEGIN) {
//                // Completion of literal strings within require calls
//                while (ts.movePrevious()) {
//                    token = ts.token();
//
//                    id = token.id();
//
//                    if ((id == ScalaTokenId.WHITESPACE) || (id == ScalaTokenId.LPAREN) ||
//                            (id == ScalaTokenId.STRING_LITERAL)) {
//                        continue;
//                    }
//
//                    if (id == ScalaTokenId.IDENTIFIER) {
//                        String text = token.text().toString();
//
//                        if (text.equals("require") || text.equals("load")) {
//                            return stringStart;
//                        } else {
//                            return -1;
//                        }
//                    } else {
//                        return -1;
//                    }
//                }
//            }
//        }
//
//        return -1;
//    }
//
    public static int getSingleQuotedStringOffset(int caretOffset, TokenHierarchy<Document> th) {
        return getLiteralStringOffset(caretOffset, th, ScalaTokenId.STRING_BEGIN);
    }

    public static int getRegexpOffset(int caretOffset, TokenHierarchy<Document> th) {
        return getLiteralStringOffset(caretOffset, th, ScalaTokenId.REGEXP_BEGIN);
    }

    /**
     * Determine if the caret is inside a literal string, and if so, return its starting
     * offset. Return -1 otherwise.
     */
    @SuppressWarnings("unchecked")
    private static int getLiteralStringOffset(int caretOffset, TokenHierarchy<Document> th,
            ScalaTokenId begin) {
        TokenSequence<ScalaTokenId> ts = getTokenSequence(th, caretOffset);

        if (ts == null) {
            return -1;
        }

        ts.move(caretOffset);

        if (!ts.moveNext() && !ts.movePrevious()) {
            return -1;
        }

        if (ts.offset() == caretOffset) {
            // We're looking at the offset to the RIGHT of the caret
            // and here I care about what's on the left
            ts.movePrevious();
        }

        Token<ScalaTokenId> token = ts.token();

        if (token != null) {
            TokenId id = token.id();

//            // We're within a String that has embedded Js. Drop into the
//            // embedded language and see if we're within a literal string there.
//            if (id == ScalaTokenId.EMBEDDED_RUBY) {
//                ts = (TokenSequence)ts.embedded();
//                assert ts != null;
//                ts.move(caretOffset);
//
//                if (!ts.moveNext() && !ts.movePrevious()) {
//                    return -1;
//                }
//
//                token = ts.token();
//                id = token.id();
//            }

            // Skip over embedded Js segments and literal strings until you find the beginning
            while ((id == ScalaTokenId.Error) || (id == ScalaTokenId.StringLiteral) ||
                    (id == ScalaTokenId.REGEXP_LITERAL)) {
                ts.movePrevious();
                token = ts.token();
                id = token.id();
            }

            if (id == begin) {
                if (!ts.moveNext()) {
                    return -1;
                }

                return ts.offset();
            }
        }

        return -1;
    }

    public static OffsetRange getDocCommentRangeBefore(TokenHierarchy th, int lexOffset) {
        TokenSequence<ScalaTokenId> ts = getTokenSequence(th, lexOffset);
        if (ts == null) {
            return OffsetRange.NONE;
        }

        ts.move(lexOffset);
        int offset = -1;
        int endOffset = -1;
        boolean done = false;
        while (ts.movePrevious() && !done) {
            ScalaTokenId id = ts.token().id();

            if (id == ScalaTokenId.DocCommentEnd) {
                Token<ScalaTokenId> token = ts.offsetToken();
                endOffset = token.offset(th) + token.length();
            } else if (id == ScalaTokenId.DocCommentStart) {
                Token<ScalaTokenId> token = ts.offsetToken();
                offset = token.offset(th);
                done = true;
            } else if (!isWsComment(id) && !isKeyword(id)) {
                done = true;
            }
        }

        if (offset != -1 && endOffset != -1) {
            return new OffsetRange(offset, endOffset);
        } else {
            return OffsetRange.NONE;
        }
    }

    public static OffsetRange getDocumentationRange(AstNode node, TokenHierarchy th) {
        int astOffset = node.getPickOffset(th);
        // XXX This is wrong; I should do a
        //int lexOffset = LexUtilities.getLexerOffset(result, astOffset);
        // but I don't have the CompilationInfo in the ParseResult handed to the indexer!!
        int lexOffset = astOffset;
        return getDocCommentRangeBefore(th, lexOffset);
    }

    /**
     * Get the comment block for the given offset. The offset may be either within the comment
     * block, or the comment corresponding to a code node, depending on isAfter.
     * 
     * @param doc The document
     * @param caretOffset The offset in the document
     * @param isAfter If true, the offset is pointing to some code AFTER the code block
     *   such as a method node. In this case it needs to back up to find the comment.
     * @return
     */
    public static OffsetRange getCommentBlock(BaseDocument doc, int caretOffset, boolean isAfter) {
        // Check if the caret is within a comment, and if so insert a new
        // leaf "node" which contains the comment line and then comment block
        try {
            TokenSequence<? extends TokenId> ts = getTokenSequence(doc, caretOffset);
            if (ts == null) {
                return OffsetRange.NONE;
            }
            ts.move(caretOffset);
            if (isAfter) {
                while (ts.movePrevious()) {
                    TokenId id = ts.token().id();
                    if (isComment(id)) {
                        return getCommentBlock(doc, ts.offset(), false);
                    } else if (!((id == ScalaTokenId.Ws) || (id == ScalaTokenId.Nl))) {
                        return OffsetRange.NONE;
                    }
                }
                return OffsetRange.NONE;
            }

            if (!ts.moveNext() && !ts.movePrevious()) {
                return null;
            }
            Token<? extends TokenId> token = ts.token();

            if (token != null && isBlockComment(token.id())) {
                return new OffsetRange(ts.offset(), ts.offset() + token.length());
            }

            if ((token != null) && (token.id() == ScalaTokenId.LineComment)) {
                // First add a range for the current line
                int begin = Utilities.getRowStart(doc, caretOffset);
                int end = Utilities.getRowEnd(doc, caretOffset);

                if (isCommentOnlyLine(doc, caretOffset)) {

                    while (begin > 0) {
                        int newBegin = Utilities.getRowStart(doc, begin - 1);

                        if ((newBegin < 0) || !isCommentOnlyLine(doc, newBegin)) {
                            begin = Utilities.getRowFirstNonWhite(doc, begin);
                            break;
                        }

                        begin = newBegin;
                    }

                    int length = doc.getLength();

                    while (true) {
                        int newEnd = Utilities.getRowEnd(doc, end + 1);

                        if ((newEnd >= length) || !isCommentOnlyLine(doc, newEnd)) {
                            end = Utilities.getRowLastNonWhite(doc, end) + 1;
                            break;
                        }

                        end = newEnd;
                    }

                    if (begin < end) {
                        return new OffsetRange(begin, end);
                    }
                } else {
                    // It's just a line comment next to some code
                    TokenHierarchy<Document> th = TokenHierarchy.get((Document) doc);
                    int offset = token.offset(th);
                    return new OffsetRange(offset, offset + token.length());
                }
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return OffsetRange.NONE;
    }
//    public static boolean isInsideQuotedString(BaseDocument doc, int offset) {
//        TokenSequence<?extends ScalaTokenId> ts = FortressLexUtilities.getTokenSequence(doc, offset);
//
//        if (ts == null) {
//            return false;
//        }
//
//        ts.move(offset);
//
//        if (ts.moveNext()) {
//            Token<?extends ScalaTokenId> token = ts.token();
//            TokenId id = token.id();
//            if (id == ScalaTokenId.QUOTED_STRING_LITERAL || id == ScalaTokenId.QUOTED_STRING_END) {
//                return true;
//            }
//        }
//        if (ts.movePrevious()) {
//            Token<?extends ScalaTokenId> token = ts.token();
//            TokenId id = token.id();
//            if (id == ScalaTokenId.QUOTED_STRING_LITERAL || id == ScalaTokenId.QUOTED_STRING_BEGIN) {
//                return true;
//            }
//        }
//        
//        return false;
//    }
//

    public static boolean isInsideRegexp(BaseDocument doc, int offset) {
        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(doc, offset);

        if (ts == null) {
            return false;
        }

        ts.move(offset);

        if (ts.moveNext()) {
            Token<ScalaTokenId> token = ts.token();
            TokenId id = token.id();
            if (id == ScalaTokenId.REGEXP_LITERAL || id == ScalaTokenId.REGEXP_END) {
                return true;
            }
        }
        if (ts.movePrevious()) {
            Token<ScalaTokenId> token = ts.token();
            TokenId id = token.id();
            if (id == ScalaTokenId.REGEXP_LITERAL || id == ScalaTokenId.REGEXP_BEGIN) {
                return true;
            }
        }

        return false;
    }

    /**
     * Back up to the first space character prior to the given offset - as long as 
     * it's on the same line!  If there's only leading whitespace on the line up
     * to the lex offset, return the offset itself 
     * @todo Rewrite this now that I have a separate newline token, EOL, that I can
     *   break on - no need to call Utilities.getRowStart.
     */
    public static int findSpaceBegin(BaseDocument doc, int lexOffset) {
        TokenSequence ts = ScalaLexUtilities.getTokenSequence(doc, lexOffset);
        if (ts == null) {
            return lexOffset;
        }
        boolean allowPrevLine = false;
        int lineStart;
        try {
            lineStart = Utilities.getRowStart(doc, Math.min(lexOffset, doc.getLength()));
            int prevLast = lineStart - 1;
            if (lineStart > 0) {
                prevLast = Utilities.getRowLastNonWhite(doc, lineStart - 1);
                if (prevLast != -1) {
                    char c = doc.getText(prevLast, 1).charAt(0);
                    if (c == ',') {
                        // Arglist continuation? // TODO : check lexing
                        allowPrevLine = true;
                    }
                }
            }
            if (!allowPrevLine) {
                int firstNonWhite = Utilities.getRowFirstNonWhite(doc, lineStart);
                if (lexOffset <= firstNonWhite || firstNonWhite == -1) {
                    return lexOffset;
                }
            } else {
                // Make lineStart so small that Math.max won't cause any problems
                int firstNonWhite = Utilities.getRowFirstNonWhite(doc, lineStart);
                if (prevLast >= 0 && (lexOffset <= firstNonWhite || firstNonWhite == -1)) {
                    return prevLast + 1;
                }
                lineStart = 0;
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
            return lexOffset;
        }
        ts.move(lexOffset);
        if (ts.moveNext()) {
            if (lexOffset > ts.offset()) {
                // We're in the middle of a token
                return Math.max((ts.token().id() == ScalaTokenId.Ws) ? ts.offset() : lexOffset, lineStart);
            }
            while (ts.movePrevious()) {
                Token token = ts.token();
                if (token.id() != ScalaTokenId.Ws) {
                    return Math.max(ts.offset() + token.length(), lineStart);
                }
            }
        }

        return lexOffset;
    }

    /**
     * Get the documentation associated with the given node in the given document.
     * TODO: handle proper block comments
     */
    public static List<String> gatherDocumentation(CompilationInfo info, BaseDocument baseDoc, int nodeOffset) {
        LinkedList<String> comments = new LinkedList<String>();
        int elementBegin = nodeOffset;
        if (info != null && info.getDocument() == baseDoc) {
            elementBegin = ScalaLexUtilities.getLexerOffset(info, elementBegin);
            if (elementBegin == -1) {
                return null;
            }
        }

        try {
            if (elementBegin >= baseDoc.getLength()) {
                return null;
            }

            // Search to previous lines, locate comments. Once we have a non-whitespace line that isn't
            // a comment, we're done

            int offset = Utilities.getRowStart(baseDoc, elementBegin);
            offset--;

            // Skip empty and whitespace lines
            while (offset >= 0) {
                // Find beginning of line
                offset = Utilities.getRowStart(baseDoc, offset);

                if (!Utilities.isRowEmpty(baseDoc, offset) &&
                        !Utilities.isRowWhite(baseDoc, offset)) {
                    break;
                }

                offset--;
            }

            if (offset < 0) {
                return null;
            }

            while (offset >= 0) {
                // Find beginning of line
                offset = Utilities.getRowStart(baseDoc, offset);

                if (Utilities.isRowEmpty(baseDoc, offset) || Utilities.isRowWhite(baseDoc, offset)) {
                    // Empty lines not allowed within an rdoc
                    break;
                }

                // This is a comment line we should include
                int lineBegin = Utilities.getRowFirstNonWhite(baseDoc, offset);
                int lineEnd = Utilities.getRowLastNonWhite(baseDoc, offset) + 1;
                String line = baseDoc.getText(lineBegin, lineEnd - lineBegin);

                // Tolerate "public", "private" and "protected" here --
                // Test::Unit::Assertions likes to put these in front of each
                // method.
                if (line.startsWith("*")) {
                    // ignore end of block comment: "*/"
                    if (line.length() == 1 || (line.length() > 1 && line.charAt(1) != '/')) {
                        comments.addFirst(line.substring(1).trim());
                    }
                } else {
                    // No longer in a comment
                    break;
                }

                // Previous line
                offset--;
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return comments;
    }

    public static boolean isWsComment(TokenId id) {
        if (isComment(id) || id == ScalaTokenId.Ws || id == ScalaTokenId.Nl) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isComment(TokenId id) {
        if (id == ScalaTokenId.LineComment || isBlockComment(id) || isDocComment(id)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDocComment(TokenId id) {
        if (id == ScalaTokenId.DocCommentStart ||
                id == ScalaTokenId.DocCommentEnd ||
                id == ScalaTokenId.DocCommentData ||
                id == ScalaTokenId.CommentTag) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isBlockComment(TokenId id) {
        if (id == ScalaTokenId.BlockCommentStart ||
                id == ScalaTokenId.BlockCommentEnd ||
                id == ScalaTokenId.BlockCommentData ||
                id == ScalaTokenId.CommentTag) {
            return true;
        } else {
            return false;
        }

    }
    public static List<ScalaTokenId> PotentialIdTokens = Arrays.asList(
            ScalaTokenId.Identifier,
            ScalaTokenId.True,
            ScalaTokenId.False,
            ScalaTokenId.Null,
            ScalaTokenId.XmlAttName,
            ScalaTokenId.XmlAttValue,
            ScalaTokenId.XmlCDData,
            ScalaTokenId.XmlCDEnd,
            ScalaTokenId.XmlComment,
            ScalaTokenId.XmlSTagName,
            ScalaTokenId.XmlSTagName,
            ScalaTokenId.XmlCharData);

    /** Some AstItems have Xml Nl etc type of idToken, here we just pick following as proper one */
    public static boolean isProperIdToken(TokenId id) {
        if (id == ScalaTokenId.Identifier || id == ScalaTokenId.This || id == ScalaTokenId.Super || id == ScalaTokenId.Wild) {
            return true;
        }
        return false;
    }

    public static boolean isKeyword(ScalaTokenId id) {
        return id.primaryCategory().equals("keyword");
    }

    public static OffsetRange getRangeOfToken(TokenHierarchy th, Token token) {
        final int offset = token.offset(th);
        return new OffsetRange(offset, offset + token.length());
    }

    public static BaseDocument getDocument(FileObject fileObject, boolean openIfNecessary) {
        try {
            DataObject dobj = DataObject.find(fileObject);

            EditorCookie ec = dobj.getCookie(EditorCookie.class);
            if (ec != null) {
                return (BaseDocument) (openIfNecessary ? ec.openDocument() : ec.getDocument());
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    public static List<Token> findImportPrefix(TokenHierarchy th, int lexOffset) {
        List<Token> paths = null;

        TokenSequence ts = ScalaLexUtilities.getTokenSequence(th, lexOffset);
        ts.move(lexOffset);

        boolean lbraceMet = false;
        boolean lbraceExpected = false;
        boolean extractBehindComma = false;
        while (ts.isValid() && ts.movePrevious()) {
            Token tk = ts.token();
            TokenId id = tk.id();

            if (id == ScalaTokenId.Import) {
                if (!lbraceExpected || lbraceExpected && lbraceMet) {
                    if (paths != null) {
                        Collections.reverse(paths);
                        return paths;
                    } else {
                        return null;
                    }
                }
            } else if (id == ScalaTokenId.Dot) {
                if (paths == null) {
                    paths = new ArrayList<Token>();
                }
                paths.add(tk);
            } else if (id == ScalaTokenId.Identifier) {
                if (paths == null) {
                    paths = new ArrayList<Token>();
                }
                paths.add(tk);
            } else if (id == ScalaTokenId.LBrace) {
                if (lbraceMet) {
                    // we can only meet LBrace once
                    return null;
                }
                lbraceMet = true;
                if (paths != null) {
                    if (paths.size() > 0) {
                        // keep first met id token only
                        Token idToken = paths.get(0);
                        paths.clear();
                        if (!extractBehindComma) {
                            paths.add(idToken);
                        }
                    }
                }
            } else if (id == ScalaTokenId.Comma) {
                lbraceExpected = true;
                if (paths == null) {
                    extractBehindComma = true;
                }
            } else if (isWsComment(id)) {
                continue;
            } else {
                return null;
            }
        }

        return null;
    }
}
