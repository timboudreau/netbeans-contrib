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
package org.netbeans.modules.erlang.platform.gsf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.gsf.CompilationInfo;

import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.erlang.platform.api.RubyInstallation;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Exceptions;


/**
 * Utilities associated with lexing or analyzing the document at the
 * lexical level, unlike AstUtilities which is contains utilities
 * to analyze parsed information about a document.
 *
 * @author Tor Norbye
 * @author Caoyuan Deng
 */
public class LexUtilities {
    private static final String mimeType = "text/erlang";
    
    /** Tokens that match a corresponding END statement. Even though while, unless etc.
     * can be statement modifiers, those luckily have different token ids so are not a problem
     * here.
     */
    private static final Set<String> END_PAIRS = new HashSet<String>();

    /**
     * Tokens that should cause indentation of the next line. This is true for all {@link #END_PAIRS},
     * but also includes tokens like "else" that are not themselves matched with end but also contribute
     * structure for indentation.
     *
     */
    private static final Set<String> INDENT_WORDS = new HashSet<String>();


    
    static {
        END_PAIRS.add("begin");
        END_PAIRS.add("if");
        END_PAIRS.add("case");
        END_PAIRS.add("try");
        END_PAIRS.add("fun");

        INDENT_WORDS.addAll(END_PAIRS);
        // Add words that are not matched themselves with an "end",
        // but which also provide block structure to indented content
        // (usually part of a multi-keyword structure such as if-then-elsif-else-end
        // where only the "if" is considered an end-pair.)
        INDENT_WORDS.add("of");
        INDENT_WORDS.add("->");
        INDENT_WORDS.add("catch");
    }
    
    private LexUtilities() {
    }
    
    /** For a possibly generated offset in an AST, return the corresponding lexing/true document offset */
    public static int getLexerOffset(CompilationInfo info, int astOffset) {
        return info.getPositionManager().getLexicalOffset(info.getParserResult(), astOffset);
    }

    public static OffsetRange getLexerOffsets(CompilationInfo info, OffsetRange astRange) {
        int rangeStart = astRange.getStart();
        int start = info.getPositionManager().getLexicalOffset(info.getParserResult(), rangeStart);
        if (start == rangeStart) {
            return astRange;
        } else if (start == -1) {
            return OffsetRange.NONE;
        } else {
            // Assumes the translated range maintains size
            return new OffsetRange(start, start+astRange.getLength());
        }
    }
    
    
    @SuppressWarnings("unchecked")
    private static TokenSequence<? extends TokenId> findRhtmlDelimited(TokenSequence t, int offset) {
        if (t.language().mimeType().equals(RubyInstallation.RHTML_MIME_TYPE)) {
            t.move(offset);
            if (t.moveNext() && t.token() != null && 
                    "ruby-delimiter".equals(t.token().id().primaryCategory())) { // NOI18N
                // It's a delimiter - move ahead and see if we find it
                if (t.moveNext() && t.token() != null &&
                        "ruby".equals(t.token().id().primaryCategory())) { // NOI18N
                    TokenSequence<? extends TokenId> ets = t.embedded();
                    if (ets != null) {
                        return (TokenSequence<? extends TokenId>)ets;
                    }
                }
            }
        }
        
        return null;
    }
    
    /** Find the ruby token sequence (in case it's embedded in something else at the top level */
    @SuppressWarnings("unchecked")
    public static TokenSequence<?extends TokenId> getTokenSequence(Document doc, int offset) {
        TokenHierarchy<Document> th = TokenHierarchy.get(doc);
        return getTokenSequence(th, offset);
    }
    
    @SuppressWarnings("unchecked")
    public static TokenSequence<?extends TokenId> getTokenSequence(TokenHierarchy<Document> th, int offset) {
        TokenSequence ts = th.tokenSequence();

        if (ts == null) {
            // Possibly an embedding scenario such as an RHTML file
            // First try with backward bias true
            List<TokenSequence<?>> list = th.embeddedTokenSequences(offset, true);

            for (TokenSequence t : list) {
                if (t.language().mimeType().equals(mimeType)) {
                    ts = t;

                    break;
                } else {
                    TokenSequence<? extends TokenId> ets = findRhtmlDelimited(t, offset);
                    if (ets != null) {
                        return ets;
                    }
                }
            }

            if (ts == null) {
                list = th.embeddedTokenSequences(offset, false);

                for (TokenSequence t : list) {
                    if (t.language().mimeType().equals(mimeType)) {
                        ts = t;

                        break;
                    } else {
                        TokenSequence<? extends TokenId> ets = findRhtmlDelimited(t, offset);
                        if (ets != null) {
                            return ets;
                        }
                    }
                }
            }
        }

        return ts;
    }

    public static Token getToken(BaseDocument doc, int offset) {
        TokenSequence ts = getTokenSequence(doc, offset);

        if (ts != null) {
            try {
                ts.move(offset);
            } catch (AssertionError e) {
                DataObject dobj = (DataObject)doc.getProperty(Document.StreamDescriptionProperty);

                if (dobj != null) {
                    Exceptions.attachMessage(e, FileUtil.getFileDisplayName(dobj.getPrimaryFile()));
                }

                throw e;
            }

            if (!ts.moveNext() && !ts.movePrevious()) {
                return null;
            }

            Token token = ts.token();

            return token;
        }

        return null;
    }

    public static char getTokenChar(BaseDocument doc, int offset) {
        Token token = getToken(doc, offset);

        if (token != null) {
            String text = token.text().toString();

            if (text.length() > 0) { // Usually true, but I could have gotten EOF right?

                return text.charAt(0);
            }
        }

        return 0;
    }
    
    public static boolean containsStop(BaseDocument doc, int offset, int endOffset) {
        TokenSequence ts = getTokenSequence(doc, offset);
        if (ts != null) {
            try {
                ts.move(offset);
                do {                    
                    Token token = ts.token();
                    if (token != null && token.id().name().equals("stop")) {
                        return true;
                    }
                } while (ts.moveNext() && ts.offset() <= endOffset);
            } catch (AssertionError e) {
                DataObject dobj = (DataObject)doc.getProperty(Document.StreamDescriptionProperty);

                if (dobj != null) {
                    Exceptions.attachMessage(e, FileUtil.getFileDisplayName(dobj.getPrimaryFile()));
                }

                throw e;
            }
        }
                
        return false;
    }
    
    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    public static OffsetRange findHeredocEnd(TokenSequence<?extends TokenId> ts,  Token<?extends TokenId> startToken) {
        // Look for the end of the given heredoc
        String text = startToken.text().toString();
        assert text.startsWith("<<");
        text = text.substring(2);
        if (text.startsWith("-")) {
            text = text.substring(1);
        }
        if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
            text = text.substring(0, text.length()-2);
        }
        String textn = text+"\n";

        while (ts.moveNext()) {
            Token<?extends TokenId> token = ts.token();
            TokenId id = token.id();
            String textX = token.text().toString();

            if (textX.equals("\"")) {
                if (text.equals(textX) || textn.equals(textX)) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }
            }
        }

        return OffsetRange.NONE;
    }

    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    public static OffsetRange findHeredocBegin(TokenSequence<?extends TokenId> ts,  Token<?extends TokenId> endToken) {
        // Look for the end of the given heredoc
        String text = endToken.text().toString();
        if (text.endsWith("\n")) {
            text = text.substring(0, text.length()-1);
        }
        String textQuotes = "\"" + text + "\"";
        String textSQuotes = "'" + text + "'";

        while (ts.movePrevious()) {
            Token<?extends TokenId> token = ts.token();
            TokenId id = token.id();
            String textX = token.text().toString();
            
            if (textX.equals("\"")) {
                String t = token.text().toString();
                String marker = null;
                if (t.startsWith("<<-")) {
                    marker = t.substring(3);
                } else if (t.startsWith("<<")) {
                    marker = t.substring(2);
                }
                if (marker != null && (text.equals(marker) || textQuotes.equals(marker) || textSQuotes.equals(marker))) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }
            }
        }

        return OffsetRange.NONE;
    }
    
    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    public static OffsetRange findFwd(Document doc, TokenSequence<?extends TokenId> ts, TokenId up,
        TokenId down) {
        int balance = 0;

        while (ts.moveNext()) {
            Token<?extends TokenId> token = ts.token();
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
    public static OffsetRange findBwd(Document doc, TokenSequence<?extends TokenId> ts, TokenId up,
        TokenId down) {
        int balance = 0;

        while (ts.movePrevious()) {
            Token<?extends TokenId> token = ts.token();
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

    /** Find the token that begins a block terminated by "end". This is a token
     * in the END_PAIRS array. Walk backwards and find the corresponding token.
     * It does not use indentation for clues since this could be wrong and be
     * precisely the reason why the user is using pair matching to see what's wrong.
     */
    public static OffsetRange findBegin(Document doc, TokenSequence<?extends TokenId> ts) {
        int balance = 0;

        while (ts.movePrevious()) {
            Token<?extends TokenId> token = ts.token();
            TokenId id = token.id();
            String textX = token.text().toString();
            
            if (isBeginToken(id, doc, ts)) {
                // No matching dot for "do" used in conditionals etc.)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance--;
            } else if (textX.equals("end")) {
                balance++;
            }
        }

        return OffsetRange.NONE;
    }

    public static OffsetRange findEnd(Document doc, TokenSequence<?extends TokenId> ts) {
        int balance = 0;

        while (ts.moveNext()) {
            Token<?extends TokenId> token = ts.token();
            TokenId id = token.id();
            String textX = token.text().toString();
            
            if (isBeginToken(id, doc, ts)) {
                balance--;
            } else if (textX.equals("end")) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset(), ts.offset() + token.length());
                }

                balance++;
            }
        }

        return OffsetRange.NONE;
    }
    

    /**
     * Return true iff the given token is a token that should be matched
     * with a corresponding "end" token, such as "begin", "def", "module",
     * etc.
     */
    public static boolean isBeginToken(TokenId id, Document doc, int offset) {
        return END_PAIRS.contains(id);
    }

    /**
     * Return true iff the given token is a token that should be matched
     * with a corresponding "end" token, such as "begin", "def", "module",
     * etc.
     */
    public static boolean isBeginToken(TokenId id, Document doc, TokenSequence<?extends TokenId> ts) {
        return END_PAIRS.contains(id);
    }
    
    /**
     * Return true iff the given token is a token that indents its content,
     * such as the various begin tokens as well as "else", "when", etc.
     */
    public static boolean isIndentToken(TokenId id) {
        return INDENT_WORDS.contains(id);
    }

    /** Compute the balance of begin/end tokens on the line */
    public static int getBeginEndLineBalance(BaseDocument doc, int offset) {
        try {
            int begin = Utilities.getRowStart(doc, offset);
            int end = Utilities.getRowEnd(doc, offset);

            TokenSequence<?extends TokenId> ts = LexUtilities.getTokenSequence(doc, begin);
            if (ts == null) {
                return 0;
            }

            ts.move(begin);

            if (!ts.moveNext()) {
                return 0;
            }

            int balance = 0;

            do {
                Token<?extends TokenId> token = ts.token();
                TokenId id = token.id();
                String text = token.text().toString();
                
                if (isBeginToken(id, doc, ts)) {
                    balance++;
                } else if (text.equals("end")) {
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
    public static int getLineBalance(BaseDocument doc, int offset, TokenId up, TokenId down) {
        try {
            int begin = Utilities.getRowStart(doc, offset);
            int end = Utilities.getRowEnd(doc, offset);

            TokenSequence<?extends TokenId> ts = LexUtilities.getTokenSequence(doc, begin);
            if (ts == null) {
                return 0;
            }

            ts.move(begin);

            if (!ts.moveNext()) {
                return 0;
            }

            int balance = 0;

            do {
                Token<?extends TokenId> token = ts.token();
                TokenId id = token.id();

                if (id == up) {
                    balance++;
                } else if (id == down) {
                    balance--;
                }
            } while (ts.moveNext() && (ts.offset() <= end));

            return balance;
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);

            return 0;
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
        TokenSequence<?extends TokenId> ts = LexUtilities.getTokenSequence(doc, 0);
        if (ts == null) {
            return 0;
        }

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

    public static int getLineIndent(BaseDocument doc, int offset) {
        try {
            int start = Utilities.getRowStart(doc, offset);
            int end;

            if (Utilities.isRowWhite(doc, start)) {
                end = Utilities.getRowEnd(doc, offset);
            } else {
                end = Utilities.getRowFirstNonWhite(doc, start);
            }

            int indent = Utilities.getVisualColumn(doc, end);

            return indent;
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);

            return 0;
        }
    }

    public static void indent(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append(' ');
        }
    }

    public static String getIndentString(int indent) {
        StringBuilder sb = new StringBuilder(indent);
        indent(sb, indent);

        return sb.toString();
    }

    /**
     * Return true iff the line for the given offset is a Ruby comment line.
     * This will return false for lines that contain comments (even when the
     * offset is within the comment portion) but also contain code.
     */
    public static boolean isCommentOnlyLine(BaseDocument doc, int offset)
        throws BadLocationException {
        int begin = Utilities.getRowFirstNonWhite(doc, offset);

        if (begin == -1) {
            return false; // whitespace only
        }

        if (begin == doc.getLength()) {
            return false;
        }

        return doc.getText(begin, 1).equals("#");
    }

    public static void adjustLineIndentation(BaseDocument doc, int offset, int adjustment) {
        try {
            int lineBegin = Utilities.getRowStart(doc, offset);

            if (adjustment > 0) {
                doc.remove(lineBegin, adjustment);
            } else if (adjustment < 0) {
                doc.insertString(adjustment, LexUtilities.getIndentString(adjustment), null);
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }
    }

    /** Adjust the indentation of the line containing the given offset to the provided
     * indentation, and return the new indent.
     */
    public static int setLineIndentation(BaseDocument doc, int offset, int indent) {
        int currentIndent = getLineIndent(doc, offset);

        try {
            int lineBegin = Utilities.getRowStart(doc, offset);

            if (lineBegin == -1) {
                return currentIndent;
            }

            int adjust = currentIndent - indent;

            if (adjust > 0) {
                // Make sure that we are only removing spaces here
                String text = doc.getText(lineBegin, adjust);

                for (int i = 0; i < text.length(); i++) {
                    if (!Character.isWhitespace(text.charAt(i))) {
                        throw new RuntimeException(
                            "Illegal indentation adjustment: Deleting non-whitespace chars: " +
                            text);
                    }
                }

                doc.remove(lineBegin, adjust);
            } else if (adjust < 0) {
                adjust = -adjust;
                doc.insertString(lineBegin, getIndentString(adjust), null);
            }

            return indent;
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);

            return currentIndent;
        }
    }

    /**
     * Return the string at the given position, or null if none
     */
//    @SuppressWarnings("unchecked")
//    public static String getStringAt(int caretOffset, TokenHierarchy<Document> th) {
//        TokenSequence<?extends TokenId> ts = getRubyTokenSequence(th, caretOffset);
//
//        if (ts == null) {
//            return null;
//        }
//
//        ts.move(caretOffset);
//
//        if (!ts.moveNext() && !ts.movePrevious()) {
//            return null;
//        }
//
//        if (ts.offset() == caretOffset) {
//            // We're looking at the offset to the RIGHT of the caret
//            // and here I care about what's on the left
//            ts.movePrevious();
//        }
//
//        Token<?extends TokenId> token = ts.token();
//
//        if (token != null) {
//            TokenId id = token.id();
//
//            // We're within a String that has embedded Ruby. Drop into the
//            // embedded language and see if we're within a literal string there.
//            if (id == RubyTokenId.EMBEDDED_RUBY) {
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
//            String string = null;
//
//            // Skip over embedded Ruby segments and literal strings until you find the beginning
//            int segments = 0;
//
//            while ((id == RubyTokenId.ERROR) || (id == RubyTokenId.STRING_LITERAL) ||
//                    (id == RubyTokenId.QUOTED_STRING_LITERAL) || (id == RubyTokenId.EMBEDDED_RUBY)) {
//                string = token.text().toString();
//                segments++;
//                ts.movePrevious();
//                token = ts.token();
//                id = token.id();
//            }
//
//            if ((id == RubyTokenId.STRING_BEGIN) || (id == RubyTokenId.QUOTED_STRING_BEGIN)) {
//                if (segments == 1) {
//                    return string;
//                } else {
//                    // Build up the String from the sequence
//                    StringBuilder sb = new StringBuilder();
//
//                    while (ts.moveNext()) {
//                        token = ts.token();
//                        id = token.id();
//
//                        if ((id == RubyTokenId.ERROR) || (id == RubyTokenId.STRING_LITERAL) ||
//                                (id == RubyTokenId.QUOTED_STRING_LITERAL) ||
//                                (id == RubyTokenId.EMBEDDED_RUBY)) {
//                            sb.append(token.text());
//                        } else {
//                            break;
//                        }
//                    }
//
//                    return sb.toString();
//                }
//            }
//        }
//
//        return null;
//    }

    /**
     * Check if the caret is inside a literal string that is associated with
     * a require statement.
     *
     * @return The offset of the beginning of the require string, or -1
     *     if the offset is not inside a require string.
     */
//    public static int getRequireStringOffset(int caretOffset, TokenHierarchy<Document> th) {
//        TokenSequence<?extends TokenId> ts = getRubyTokenSequence(th, caretOffset);
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
//        Token<?extends TokenId> token = ts.token();
//
//        if (token != null) {
//            TokenId id = token.id();
//
//            // Skip over embedded Ruby segments and literal strings until you find the beginning
//            while ((id == RubyTokenId.ERROR) || (id == RubyTokenId.STRING_LITERAL) ||
//                    (id == RubyTokenId.QUOTED_STRING_LITERAL) || (id == RubyTokenId.EMBEDDED_RUBY)) {
//                ts.movePrevious();
//                token = ts.token();
//                id = token.id();
//            }
//
//            int stringStart = ts.offset() + token.length();
//
//            if ((id == RubyTokenId.STRING_BEGIN) || (id == RubyTokenId.QUOTED_STRING_BEGIN)) {
//                // Completion of literal strings within require calls
//                while (ts.movePrevious()) {
//                    token = ts.token();
//
//                    id = token.id();
//
//                    if ((id == RubyTokenId.WHITESPACE) || (id == RubyTokenId.LPAREN) ||
//                            (id == RubyTokenId.STRING_LITERAL) ||
//                            (id == RubyTokenId.QUOTED_STRING_LITERAL)) {
//                        continue;
//                    }
//
//                    if (id == RubyTokenId.IDENTIFIER) {
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

//    public static int getSingleQuotedStringOffset(int caretOffset, TokenHierarchy<Document> th) {
//        return getLiteralStringOffset(caretOffset, th, RubyTokenId.STRING_BEGIN);
//    }
//
//    public static int getDoubleQuotedStringOffset(int caretOffset, TokenHierarchy<Document> th) {
//        return getLiteralStringOffset(caretOffset, th, RubyTokenId.QUOTED_STRING_BEGIN);
//    }
//
//    public static int getRegexpOffset(int caretOffset, TokenHierarchy<Document> th) {
//        return getLiteralStringOffset(caretOffset, th, RubyTokenId.REGEXP_BEGIN);
//    }

    /**
     * Determine if the caret is inside a literal string, and if so, return its starting
     * offset. Return -1 otherwise.
     */
//    @SuppressWarnings("unchecked")
//    private static int getLiteralStringOffset(int caretOffset, TokenHierarchy<Document> th,
//        TokenId begin) {
//        TokenSequence<?extends TokenId> ts = getRubyTokenSequence(th, caretOffset);
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
//        Token<?extends TokenId> token = ts.token();
//
//        if (token != null) {
//            TokenId id = token.id();
//
//            // We're within a String that has embedded Ruby. Drop into the
//            // embedded language and see if we're within a literal string there.
//            if (id == RubyTokenId.EMBEDDED_RUBY) {
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
//
//            // Skip over embedded Ruby segments and literal strings until you find the beginning
//            while ((id == RubyTokenId.ERROR) || (id == RubyTokenId.STRING_LITERAL) ||
//                    (id == RubyTokenId.QUOTED_STRING_LITERAL) ||
//                    (id == RubyTokenId.REGEXP_LITERAL) || (id == RubyTokenId.EMBEDDED_RUBY)) {
//                ts.movePrevious();
//                token = ts.token();
//                id = token.id();
//            }
//
//            if (id == begin) {
//                if (!ts.moveNext()) {
//                    return -1;
//                }
//
//                return ts.offset();
//            }
//        }
//
//        return -1;
//    }

    public static boolean isInsideQuotedString(BaseDocument doc, int offset) {
        TokenSequence<?extends TokenId> ts = LexUtilities.getTokenSequence(doc, offset);

        if (ts == null) {
            return false;
        }

        ts.move(offset);
        Token<?extends TokenId> token = ts.token();
        TokenId id = token.id();
        if (id.name().equals("string")) {
            return true;
        } else {
            return false;
        }
    }

//    public static boolean isInsideRegexp(BaseDocument doc, int offset) {
//        TokenSequence<?extends TokenId> ts = LexUtilities.getRubyTokenSequence(doc, offset);
//
//        if (ts == null) {
//            return false;
//        }
//
//        ts.move(offset);
//
//        if (ts.moveNext()) {
//            Token<?extends TokenId> token = ts.token();
//            TokenId id = token.id();
//            if (id == RubyTokenId.REGEXP_LITERAL || id == RubyTokenId.REGEXP_END) {
//                return true;
//            }
//        }
//        if (ts.movePrevious()) {
//            Token<?extends TokenId> token = ts.token();
//            TokenId id = token.id();
//            if (id == RubyTokenId.REGEXP_LITERAL || id == RubyTokenId.REGEXP_BEGIN) {
//                return true;
//            }
//        }
//        
//        return false;
//    }
}
