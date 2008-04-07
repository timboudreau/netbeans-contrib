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
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.*;
//import static org.netbeans.modules.javafx.editor.JavaFXTokenContext.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import java.util.Arrays;


/**
 * This static class groups the whole aspect of bracket
 * completion. It is defined to clearly separate the functionality
 * and keep actions clean.
 * The methods of the class are called from different actions as
 * KeyTyped, DeletePreviousChar.
 *
 * @author pnejedly
 * @author Rastislav Komara (<a href="mailto:rastislav.komara@sun.com">RKo</a>)
 */
class BracketCompletion {

    /**
     * A hook method called after a character was inserted into the
     * document. The function checks for special characters for
     * completion ()[]'"{} and other conditions and optionally performs
     * changes to the doc and or caret (complets braces, moves caret,
     * etc.)
     *
     * @param doc    the document where the change occurred
     * @param dotPos position of the character insertion
     * @param caret  caret
     * @param ch     the character that was inserted
     * @throws BadLocationException if dotPos is not correct
     */
    static void charInserted(BaseDocument doc, int dotPos, Caret caret, char ch) throws BadLocationException {
        if (!completionSettingEnabled()) {
            return;
        }

        if (ch == ')' || ch == ']' || ch == '(' || ch == '[' || ch == '{' || ch == '}') {
            TokenId tidAtDot = tokenAt(doc, dotPos);

            if (tidAtDot == JFXTokenId.RBRACKET || tidAtDot == JFXTokenId.RPAREN) {
                skipClosingBracket(doc, caret, ch);
            } else if (tidAtDot == JFXTokenId.LBRACKET
                    || tidAtDot == JFXTokenId.LPAREN
                    || tidAtDot == JFXTokenId.STRING_LITERAL
                    || tidAtDot == JFXTokenId.QUOTE_LBRACE_STRING_LITERAL
                    || tidAtDot == JFXTokenId.RBRACE_LBRACE_STRING_LITERAL
                    || tidAtDot == JFXTokenId.RBRACE_QUOTE_STRING_LITERAL) {
                completeOpeningBracket(doc, dotPos, caret, ch);
            }
        } else if (ch == ';') {
            moveSemicolon(doc, dotPos, caret);
        }
    }

    private static <T extends TokenId> T tokenAt(BaseDocument doc, int dotPos) {
        TokenSequence<T> seq = getTokenSequence(doc, dotPos);
        return seq.moveNext() ? seq.token().id() : null;
    }

    private static <T extends TokenId> TokenSequence<T> getTokenSequence(BaseDocument doc, int dotPos) {
        TokenHierarchy<BaseDocument> th = TokenHierarchy.get(doc);
        TokenSequence<?> seq =  th.tokenSequence();
        seq.move(dotPos);
        return (TokenSequence<T>) seq;
    }

    private static void moveSemicolon(BaseDocument doc, int dotPos, Caret caret) throws BadLocationException {
        int eolPos = Utilities.getRowEnd(doc, dotPos);

        System.err.println("move semicolon:");

        TokenSequence<?> seq = getTokenSequence(doc, dotPos);

        int lastParenPos = dotPos;
        while (seq.moveNext()) {
            if (seq.offset() >= eolPos) break;
            TokenId tid = seq.token().id();
            System.err.println("  token: " + tid);
            if (tid == JFXTokenId.RPAREN) {
                lastParenPos = seq.offset();
            } else if (tid == JFXTokenId.WS) {
                return;
            }
        }

//        int bolPos = Utilities.getRowStart(doc, dotPos);
        // reset to dot
        seq.move(dotPos);
        seq.moveNext();
        if (posWithinAnyQuote(doc, dotPos)) {
            return;
        }
        doc.remove(dotPos, 1);
        doc.insertString(lastParenPos, ";", null); // NOI18N
        caret.setDot(lastParenPos + 1);
    }

    /**
     * Hook called after a character *ch* was backspace-deleted from
     * *doc*. The function possibly removes bracket or quote pair if
     * appropriate.
     *
     * @param doc    the document
     * @param dotPos position of the change
     * @param ch     the character that was deleted
     * @throws javax.swing.text.BadLocationException if operation is called out of document range.
     */
    static void charBackspaced(BaseDocument doc, int dotPos, char ch) throws BadLocationException {
        if (completionSettingEnabled()) {
            if (ch == '(' || ch == '[' || ch == '{') {
                TokenId tidAtDot = tokenAt(doc, dotPos);
                if ((tidAtDot == JFXTokenId.RBRACKET && tokenBalance(doc, JFXTokenId.LBRACKET, JFXTokenId.RBRACKET) != 0) ||
                        (tidAtDot == JFXTokenId.RPAREN && tokenBalance(doc, JFXTokenId.LPAREN, JFXTokenId.RPAREN) != 0)) {
                    doc.remove(dotPos, 1);
                }
            } else if (ch == '\"') {
                char match[] = doc.getChars(dotPos, 1);
                if (match != null && match[0] == '\"') {
                    doc.remove(dotPos, 1);
                }
            } else if (ch == '\'') {
                char match[] = doc.getChars(dotPos, 1);
                if (match != null && match[0] == '\'') {
                    doc.remove(dotPos, 1);
                }
            }
        }
    }

    /**
     * Resolve whether pairing right curly should be added automatically
     * at the caret position or not.
     * <br>
     * There must be only whitespace or line comment or block comment
     * between the caret position
     * and the left brace and the left brace must be on the same line
     * where the caret is located.
     * <br>
     * The caret must not be "contained" in the opened block comment token.
     *
     * @param doc         document in which to operate.
     * @param caretOffset offset of the caret.
     * @return true if a right brace '}' should be added
     *         or false if not.
     */
    static boolean isAddRightBrace(BaseDocument doc, int caretOffset)
            throws BadLocationException {
        boolean addRightBrace = false;
        if (completionSettingEnabled()) {
            if (caretOffset > 0) {
                // Check whether line ends with '{' ignoring any whitespace
                // or comments
                final TokenSequence<JFXTokenId> ts = getTokenSequence(doc, caretOffset);
                Token<JFXTokenId> token = ts.moveNext() ? ts.token() : null;
               
                addRightBrace = true; // suppose that right brace should be added

                // Disable right brace adding if caret not positioned within whitespace
                // or line comment
                if (token != null) {  //fix #131648
                    int off = (caretOffset - ts.offset());
                    if (off > 0 && off < token.length()) { // caret contained in token
                        switch (token.id()) {
                            case WS:
                            case LINE_COMMENT:
                                break; // the above tokens are OK

                            default:
                                // Disable brace adding for the remaining ones
                                addRightBrace = false;
                        }
                    }
                }

                if (addRightBrace) { // still candidate for adding
                    int caretRowStartOffset = Utilities.getRowStart(doc, caretOffset);

                    // Check whether there are only whitespace or comment tokens
                    // between caret and left brace and check only on the line
                    // with the caret
                    while (token != null && ts.offset() >= caretRowStartOffset) {
                        boolean ignore = false;
                        // Assuming java token context here
                        switch (token.id()) {
                            case WS:
                            case COMMENT:
                            case LINE_COMMENT:
                                // skip
                                ignore = true;
                                break;
                        }

                        if (ignore) {
                            token = ts.movePrevious() ? ts.token() : null;
                        } else { // break on the current token
                            break;
                        }
                    }
                    if ((token == null) || (token.id() != JFXTokenId.LBRACE) || (ts.offset() < caretRowStartOffset)) {
                        addRightBrace = false;
                    }

                }

                if (addRightBrace) { // Finally check the brace balance whether there are any missing right braces
                    addRightBrace = (braceBalance(doc) > 0);
                }
            }
        }
        return addRightBrace;
    }

    /**
     * Returns position of the first unpaired closing paren/brace/bracket from the caretOffset
     * till the end of caret row. If there is no such element, position after the last non-white
     * character on the caret row is returned.
     */
    static int getRowOrBlockEnd(BaseDocument doc, int caretOffset) throws BadLocationException {
        int rowEnd = Utilities.getRowLastNonWhite(doc, caretOffset);
        if (rowEnd == -1 || caretOffset >= rowEnd) {
            return caretOffset;
        }
        rowEnd += 1;
        int parenBalance = 0;
        int braceBalance = 0;
        int bracketBalance = 0;

        TokenSequence<?> seq = getTokenSequence(doc, caretOffset);

        while (seq.moveNext() && seq.offset() < rowEnd) {
            switch ((JFXTokenId) seq.token().id()) {
                case LPAREN:
                    parenBalance++;
                    break;
                case RPAREN:
                    if (parenBalance-- == 0)
                        return seq.offset();
                case LBRACE:
                    braceBalance++;
                    break;
                case RBRACE:
                    if (braceBalance-- == 0)
                        return seq.offset();
                case LBRACKET:
                    bracketBalance++;
                    break;
                case RBRACKET:
                    if (bracketBalance-- == 0)
                        return seq.offset();
            }
        }
        return rowEnd;
    }

    /**
     * Counts the number of braces starting at dotPos to the end of the
     * document. Every occurence of { increses the count by 1, every
     * occurrence of } decreses the count by 1. The result is returned.
     *
     * @param doc document representing source code.
     * @return The number of { - number of } (>0 more { than } ,<0 more } than {)
     */
    private static int braceBalance(BaseDocument doc) {
        return tokenBalance(doc, JFXTokenId.LBRACE, JFXTokenId.RBRACE);
    }

    /**
     * The same as braceBalance but generalized to any pair of matching
     * tokens.
     *
     * @param doc   document representing source code.
     * @param open  the token that increses the count
     * @param close the token that decreses the count
     * @return adjusted balance.
     */
    private static int tokenBalance(BaseDocument doc, TokenId open, TokenId close) {
        TokenHierarchy<BaseDocument> th = TokenHierarchy.get(doc);
        TokenSequence<?> ts = th.tokenSequence();

        int balance = 0;
        while (ts.moveNext()) {
            if (ts.token().id() == open) {
                balance++;
            } else if (ts.token().id() == close) {
                balance--;
            }
        }
        return balance;
    }

    /**
     * A hook to be called after closing bracket ) or ] was inserted into
     * the document. The method checks if the bracket should stay there
     * or be removed and some exisitng bracket just skipped.
     *
     * @param doc     the document
     * @param caret   caret
     * @param bracket the bracket character ']' or ')'
     * @throws javax.swing.text.BadLocationException
     *          if document location is invalid.
     */
    private static void skipClosingBracket(BaseDocument doc, Caret caret, char bracket) throws BadLocationException {

        JFXTokenId bracketId = (bracket == ')') ? JFXTokenId.RPAREN : JFXTokenId.RBRACKET;

        int caretOffset = caret.getDot();
        if (isSkipClosingBracket(doc, caretOffset, bracketId)) {
            doc.remove(caretOffset - 1, 1);
            caret.setDot(caretOffset); // skip closing bracket
        }
    }

    /**
     * Check whether the typed bracket should stay in the document
     * or be removed.
     * <br>
     * This method is called by <code>skipClosingBracket()</code>.
     *
     * @param doc         document into which typing was done.
     * @param caretOffset offset in document
     * @param bracketId   tokenId of bracket type
     * @return true if skip of bracked is required
     * @throws javax.swing.text.BadLocationException
     *          if operation is called on invalid document position.
     */
    static boolean isSkipClosingBracket(BaseDocument doc, int caretOffset, JFXTokenId bracketId) throws BadLocationException {
        //TODO: [RKo] Make this method more readable. Too high CC, nesting and length
        // First check whether the caret is not after the last char in the document
        // because no bracket would follow then so it could not be skipped.
        if (caretOffset == doc.getLength()) {
            return false; // no skip in this case
        }

        boolean skipClosingBracket = false; // by default do not remove

        // Examine token at the caret offset
//        TokenItem token = ((ExtSyntaxSupport) doc.getSyntaxSupport()).getTokenChain(
//                caretOffset, caretOffset + 1);
        final TokenSequence<?> ts = getTokenSequence(doc, caretOffset + 1);
        Token<?> token = ts.moveNext() ? ts.token() : null;

        // Check whether character follows the bracket is the same bracket
        if (token != null && token.id() == bracketId) {
            JFXTokenId leftBracketIntId = (bracketId == JFXTokenId.RPAREN) ? JFXTokenId.LPAREN : JFXTokenId.LBRACKET;

            // Skip all the brackets of the same type that follow the last one

            Token<?> nextToken = ts.moveNext() ? ts.token() : null;
            while (nextToken != null && nextToken.id() == bracketId) {
                nextToken = ts.moveNext() ? ts.token() : null;
            }
            // token var points to the last bracket in a group of two or more right brackets
            // Attempt to find the left matching bracket for it
            // Search would stop on an extra opening left brace if found
            int braceBalance = 0; // balance of '{' and '}'
            int bracketBalance = -1; // balance of the brackets or parenthesis
            token = ts.movePrevious() ? ts.token() : null;
            boolean finished = false;
            while (!finished && token != null) {
                final JFXTokenId tokenIntId = (JFXTokenId) token.id();
                switch (tokenIntId) {
                    case LPAREN:
                    case LBRACKET:
                        if (tokenIntId == bracketId) {
                            bracketBalance++;
                            if (bracketBalance == 0) {
                                if (braceBalance != 0) {
                                    // Here the bracket is matched but it is located
                                    // inside an unclosed brace block
                                    // e.g. ... ->( } a()|)
                                    // which is in fact illegal but it's a question
                                    // of what's best to do in this case.
                                    // We chose to leave the typed bracket
                                    // by setting bracketBalance to 1.
                                    // It can be revised in the future.
                                    bracketBalance = 1;
                                }
                                finished = true;
                            }
                        }
                        break;

                    case RPAREN:
                    case RBRACKET:
                        if (tokenIntId == bracketId) {
                            bracketBalance--;
                        }
                        break;

                    case LBRACE:
                        braceBalance++;
                        if (braceBalance > 0) { // stop on extra left brace
                            finished = true;
                        }
                        break;

                    case RBRACE:
                        braceBalance--;
                        break;

                }

                token = ts.movePrevious() ? ts.token() : null; // done regardless of finished flag state
            }

            if (bracketBalance != 0) { // not found matching bracket
                // Remove the typed bracket as it's unmatched
                skipClosingBracket = true;

            } else {
                // the bracket is matched
                // Now check whether the bracket would be matched
                // when the closing bracket would be removed
                // i.e. starting from the original lastRBracket token
                // and search for the same bracket to the right in the text
                // The search would stop on an extra right brace if found
                braceBalance = 0;
                bracketBalance = 1; // simulate one extra left bracket
                token = ts.moveNext() ? ts.token() : null;
                finished = false;
                while (!finished && token != null) {
                    JFXTokenId tokenIntId = (JFXTokenId) token.id();
                    switch (tokenIntId) {
                        case LPAREN:
                        case LBRACKET:
                            if (tokenIntId == leftBracketIntId) {
                                bracketBalance++;
                            }
                            break;

                        case RPAREN:
                        case RBRACKET:
                            if (tokenIntId == bracketId) {
                                bracketBalance--;
                                if (bracketBalance == 0) {
                                    if (braceBalance != 0) {
                                        // Here the bracket is matched but it is located
                                        // inside an unclosed brace block
                                        // which is in fact illegal but it's a question
                                        // of what's best to do in this case.
                                        // We chose to leave the typed bracket
                                        // by setting bracketBalance to -1.
                                        // It can be revised in the future.
                                        bracketBalance = -1;
                                    }
                                    finished = true;
                                }
                            }
                            break;

                        case LBRACE:
                            braceBalance++;
                            break;

                        case RBRACE:
                            braceBalance--;
                            if (braceBalance < 0) { // stop on extra right brace
                                finished = true;
                            }
                            break;

                    }

                    token = ts.movePrevious() ? ts.token() : null; // done regardless of finished flag state
                }

                // If bracketBalance == 0 the bracket would be matched
                // by the bracket that follows the last right bracket.
                skipClosingBracket = (bracketBalance == 0);
            }
        }
        return skipClosingBracket;
    }

    /**
     * Check for various conditions and possibly add a pairing bracket
     * to the already inserted.
     *
     * @param doc     the document
     * @param dotPos  position of the opening bracket (already in the doc)
     * @param caret   caret
     * @param bracket the bracket that was inserted
     */
    private static void completeOpeningBracket(BaseDocument doc,
                                               int dotPos,
                                               Caret caret,
                                               char bracket) throws BadLocationException {
        if (isCompletablePosition(doc, dotPos + 1)) {
            String matchinBracket = "" + matching(bracket);
            doc.insertString(dotPos + 1, matchinBracket, null);
            caret.setDot(dotPos + 1);
        }
    }

    private static boolean isEscapeSequence(BaseDocument doc, int dotPos) throws BadLocationException {
        if (dotPos <= 0) return false;
        char previousChar = doc.getChars(dotPos - 1, 1)[0];
        return previousChar == '\\';
    }

    /**
     * Check for conditions and possibly complete an already inserted
     * quote .
     *
     * @param doc     the document
     * @param dotPos  position of the opening bracket (already in the doc)
     * @param caret   caret
     * @param bracket the character that was inserted
     */
    static boolean completeQuote(BaseDocument doc, int dotPos, Caret caret,
                                 char bracket) throws BadLocationException {

        if (!completionSettingEnabled()) {
            return false;
        }

        if (isEscapeSequence(doc, dotPos)) { // \" or \' typed
            return false;
        }

        JFXTokenId token = null;
        if (doc.getLength() > dotPos) {
            token = tokenAt(doc, dotPos);
        }

        int lastNonWhite = Utilities.getRowLastNonWhite(doc, dotPos);
        // eol - true if the caret is at the end of line (ignoring whitespaces)
        boolean eol = lastNonWhite < dotPos;

        if (token == JFXTokenId.COMMENT || token == JFXTokenId.LINE_COMMENT) {
            return false;
        } else if (token == JFXTokenId.WS && eol && dotPos - 1 > 0) {
            // check if the caret is at the very end of the line comment
            token = tokenAt(doc, dotPos - 1);
            if (token == JFXTokenId.LINE_COMMENT) {
                return false;
            }
        }

        boolean completablePosition = isQuoteCompletablePosition(doc, dotPos);
        boolean insideString = insideString(token);

        if (!insideString) {
            // check if the caret is at the very end of the line and there
            // is an unterminated string literal
            if (token == JFXTokenId.WS && eol) {
                if (dotPos - 1 > 0) {
                    token = tokenAt(doc, dotPos - 1);
                    insideString = insideString(token);
                }
            }
        }

        if (insideString) {
            if (eol) {
                return false; // do not complete
            } else {
                //#69524
                char chr = doc.getChars(dotPos, 1)[0];
                if (chr == bracket) {
                    doc.insertString(dotPos, "" + bracket, null); //NOI18N
                    doc.remove(dotPos, 1);
                    return true;
                }
            }
        }

        if ((completablePosition && !insideString) || eol) {
            doc.insertString(dotPos, "" + bracket + bracket, null); //NOI18N
            return true;
        }

        return false;
    }

    private static boolean insideString(JFXTokenId token) {
        return token != null && (token == JFXTokenId.STRING_LITERAL
                        || token == JFXTokenId.SingleQuoteBody
                        || token == JFXTokenId.DoubleQuoteBody);
    }

    /**
     * Checks whether dotPos is a position at which bracket and quote
     * completion is performed. Brackets and quotes are not completed
     * everywhere but just at suitable places .
     *
     * @param doc    the document
     * @param dotPos position to be tested
     * @return true if we can use completition.
     * @throws javax.swing.text.BadLocationException if position is out of document range
     */
    private static boolean isCompletablePosition(BaseDocument doc, int dotPos) throws BadLocationException {
        if (dotPos == doc.getLength()) // there's no other character to test
            return true;
        else {
            // test that we are in front of ) , " or '
            char chr = doc.getChars(dotPos, 1)[0];
            return (chr == ')' ||
                    chr == ',' ||
                    chr == '\"' ||
                    chr == '\'' ||
                    chr == ' ' ||
                    chr == ']' ||
                    chr == '}' ||
                    chr == '\n' ||
                    chr == '\t' ||
                    chr == ';');
        }
//        return true;
    }

    private static boolean isQuoteCompletablePosition(BaseDocument doc, int dotPos)
            throws BadLocationException {
        if (dotPos == doc.getLength()) // there's no other character to test
            return true;
        else {
            // test that we are in front of ) , " or ' ... etc.
            int eol = Utilities.getRowEnd(doc, dotPos);
            if (dotPos == eol || eol == -1) {
                return false;
            }
            int firstNonWhiteFwd = Utilities.getFirstNonWhiteFwd(doc, dotPos, eol);
            if (firstNonWhiteFwd == -1) {
                return false;
            }
            char chr = doc.getChars(firstNonWhiteFwd, 1)[0];
            return (chr == ')' ||
                    chr == ',' ||
                    chr == '+' ||
                    chr == '}' ||
                    chr == ';');
        }
    }

    /**
     * Returns true if bracket completion is enabled in options.
     * @return true if bracket completion is enabled
     */
    private static boolean completionSettingEnabled() {
        //return ((Boolean)Settings.getValue(JavaFXEditorKit.class, JavaSettingsNames.PAIR_CHARACTERS_COMPLETION)).booleanV
        return true;
    }

    /**
     * Returns for an opening bracket or quote the appropriate closing
     * character.
     * @param bracket bracket to match
     * @return matching opposite bracket
     */
    private static char matching(char bracket) {
        switch (bracket) {
            case '(':
                return ')';
            case '[':
                return ']';
            case '\"':
                return '\"'; // NOI18N
            case '\'':
                return '\'';
            case '{':
                return '}';
            default:
                return ' ';
        }
    }


    /**
     * posWithinString(doc, pos) iff position *pos* is within a string
     * literal in document doc.
     *
     * @param doc    the document
     * @param dotPos position to be tested
     * @return true if matched.
     */
    static boolean posWithinString(BaseDocument doc, int dotPos) {
        return posWithinQuotes(doc, dotPos, '\"', JFXTokenId.STRING_LITERAL, JFXTokenId.DoubleQuoteBody);
    }

    /**
     * Generalized posWithingString to any token and delimiting
     * character. It works for tokens are delimited by *quote* and
     * extend up to the other *quote* or whitespace in case of an
     * incomplete token.
     *
     * @param doc    the document
     * @param dotPos position to be tested
     * @param quote expected quote
     * @param tokenID id of expected token
     * @return true if matched.
     */
    static boolean posWithinQuotes(BaseDocument doc, int dotPos, char quote, TokenId... tokenID) {
        try {
            JFXTokenId tid = tokenAt(doc, dotPos);
            return tid != null && Arrays.asList(tokenID).contains(tid) && doc.getChars(dotPos - 1, 1)[0] != quote;
        } catch (BadLocationException ex) {
            return false;
        }
    }

    static boolean posWithinAnyQuote(BaseDocument doc, int dotPos) {
        try {
            TokenSequence<JFXTokenId> ts = getTokenSequence(doc, dotPos);
            JFXTokenId tid = ts.moveNext() ? ts.token().id() : null;
            if (insideString(tid)) {
                char[] ch = doc.getChars(dotPos - 1, 1);
                return dotPos - ts.offset()   == 1 || (ch[0] != '\"' && ch[0] != '\'');
            }
            return false;
        } catch (BadLocationException ex) {
            return false;
        }
    }


/*
    static boolean isUnclosedStringAtLineEnd(BaseDocument doc, int dotPos) {
        try {
            MyTokenProcessor proc = new MyTokenProcessor();
            doc.getSyntaxSupport().tokenizeText(proc, Utilities.getRowLastNonWhite(doc, dotPos), doc.getLength(), true);
            return proc.tokenID == STRING_LITERAL;
        } catch (BadLocationException ex) {
            return false;
        }
    }
*/

/*
    */
/**
     * A token processor used to find out the length of a token.
     */
/*
    static class MyTokenProcessor implements TokenProcessor {
        public TokenID tokenID = null;
        public int tokenStart = -1;

        public boolean token(TokenID tokenID, TokenContextPath tcp,
                             int tokBuffOffset, int tokLength) {
            this.tokenStart = tokenBuffer2DocumentOffset(tokBuffOffset);
            this.tokenID = tokenID;

            // System.out.println("token " + tokenID.getName() + " at " + tokenStart + " (" +
            //		 tokBuffOffset + ") len:" + tokLength);


            return false;
        }

        public int eot(int offset) { // System.out.println("EOT");
            return 0;
        }

        public void nextBuffer(char[] buffer, int offset, int len, int startPos, int preScan, boolean lastBuffer) {
            // System.out.println("nextBuffer "+ new String(buffer) + "," + offset + "len: " + len + " startPos:"+startPos + " preScan:" + preScan + " lastBuffer:" + lastBuffer);

            this.bufferStartPos = startPos - offset;
        }

        private int bufferStartPos = 0;

        private int tokenBuffer2DocumentOffset(int offs) {
            return offs + bufferStartPos;
        }
    }
*/

    /**
     * Token processor for finding of balance of brackets and braces.
     */
/*
    private static class BalanceTokenProcessor implements TokenProcessor {

        private TokenID leftTokenID;
        private TokenID rightTokenID;

        private int balance;

        BalanceTokenProcessor(TokenID leftTokenID, TokenID rightTokenID) {
            this.leftTokenID = leftTokenID;
            this.rightTokenID = rightTokenID;
        }

        public boolean token(TokenID tokenID, TokenContextPath tcp,
                             int tokBuffOffset, int tokLength) {

            if (tokenID == leftTokenID) {
                balance++;
            } else if (tokenID == rightTokenID) {
                balance--;
            }

            return true;
        }

        public int eot(int offset) {
            return 0;
        }

        public void nextBuffer(char[] buffer, int offset, int len, int startPos, int preScan, boolean lastBuffer) {
        }

        public int getBalance() {
            return balance;
        }

    }
*/


}
