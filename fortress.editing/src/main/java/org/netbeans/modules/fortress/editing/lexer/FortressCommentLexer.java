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

package org.netbeans.modules.fortress.editing.lexer;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.lexer.TokenUtilities;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * Lexical analyzer for JavaScript comments
 * It is supposed to support 3 standards: DOJO/JSDoc/ScriptDoc, note that
 * JSDoc/ScriptDoc are JavaDoc-like, while DOJO is not.
 * 
 * @todo this is initial version, copy of JavaDoc, so it works somehow nicely
 * with JSDoc/ScriptDoc
 * @todo introduce lexer per standard or lex all standards in one lexer?
 * @todo recognizes email address as doc tag
 *
 * @author Miloslav Metelka
 * @author Martin Adamek
 */

public class FortressCommentLexer implements Lexer<FortressCommentTokenId> {
    public static final String AT_RETURN = "@return";

    private static final int EOF = LexerInput.EOF;

    private LexerInput input;
    
    private TokenFactory<FortressCommentTokenId> tokenFactory;
    
    public FortressCommentLexer(LexerRestartInfo<FortressCommentTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        assert (info.state() == null); // passed argument always null
    }
    
    public Object state() {
        return null;
    }
    
    public Token<FortressCommentTokenId> nextToken() {
        int ch = input.read();
        
        if (ch == EOF) {
            return null;
        }
        
        if (Character.isJavaIdentifierStart(ch)) {
            //TODO: EOF
            while (Character.isJavaIdentifierPart(input.read()))
                ;
            
            input.backup(1);
            return token(FortressCommentTokenId.IDENT);
        }
        
        if ("@<.#{}".indexOf(ch) == (-1)) {
            //TODO: EOF
            ch = input.read();
            
            while (!Character.isJavaIdentifierStart(ch) && "@<.#{}".indexOf(ch) == (-1) && ch != EOF)
                ch = input.read();
            
            if (ch != EOF)
                input.backup(1);
            return token(FortressCommentTokenId.OTHER_TEXT);
        }
        
        switch (ch) {
            case '@':
                while (true) {
                    ch = input.read();
                    
                    if (!Character.isLetter(ch)) {
                        input.backup(1);
                        return tokenFactory.createToken(FortressCommentTokenId.TAG, input.readLength());
                    }
                }
            case '<':
                while (true) {
                    ch = input.read();
                    if (ch == '>' || ch == EOF) {
                        return token(FortressCommentTokenId.HTML_TAG);
                    }
                }
            case '{':
                return token(FortressCommentTokenId.LCURL);
            case '}':
                return token(FortressCommentTokenId.RCURL);
            case '.':
                return token(FortressCommentTokenId.DOT);
            case '#':
                return token(FortressCommentTokenId.HASH);
        } // end of switch (ch)
        
        assert false;
        
        return null;
    }

    private Token<FortressCommentTokenId> token(FortressCommentTokenId id) {
        return tokenFactory.createToken(id);
    }

    public void release() {
    }

    /** 
     * Find the return and parameter types for the following function, according to the documentation.
     * This function will return a map of parameter names and the corresponding type string.
     * The type string can be null (for known parameters with unknown types), or a type string, or some
     * set of types separated by |.
     * The return value is using the special key AT_RETURN.
     */
    public static Map<String, String> findFunctionTypes(TokenSequence<? extends FortressCommentTokenId> ts) {
        Map<String, String> result = new HashMap<String, String>();
        
        while (ts.moveNext()) {
            Token<? extends FortressCommentTokenId> token = ts.token();
            TokenId id = token.id();
            if (id == FortressCommentTokenId.TAG) {
                CharSequence text = token.text();
                if (TokenUtilities.textEquals("@param", text) ||  // NOI18N
                        TokenUtilities.textEquals("@argument", text)) { // NOI18N
                    int index = ts.index();
                    String type = nextType(ts);
                    String name = nextIdent(ts);
                    if (name != null) {
                        result.put(name, type);
                    } else {
                        ts.moveIndex(index);
                        ts.moveNext();
                    }
                } else if (TokenUtilities.textEquals("@type", text)) { // NOI18N)
                    String type = nextIdentGroup(ts);
                    if (type != null) {
                        result.put(AT_RETURN,type); // NOI18N
                    }
                } else if (TokenUtilities.textEquals(AT_RETURN,text) || // NOI18N
                        TokenUtilities.textEquals("@returns", text)) { // NOI18N
                    // There can be both @return and @type where one of them specifies
                    // the type so don't overwrite the map entry unconditionally
                    String type = nextType(ts);
                    if (type != null) {
                        result.put(AT_RETURN,type); // NOI18N
                    }
                } else if (TokenUtilities.textEquals("@namespace", text) || // NOI18N
                        TokenUtilities.textEquals("@extends", text) || // NOI18N
                        TokenUtilities.textEquals("@class", text)) { // NOI18N
                    String arg = nextIdentGroup(ts);
                    if (arg != null) {
                        result.put(text.toString(), arg);
                    }
                } else if (TokenUtilities.textEquals("@private", text) || // NOI18N
                        TokenUtilities.textEquals("@constructor", text) || // NOI18N
                        TokenUtilities.textEquals("@ignore", text) || // NOI18N
                        TokenUtilities.textEquals("@deprecated", text)) { // NOI18N
                    result.put(text.toString(), ""); // NOI18N
                }
            }
        }
        
        return result;
    }

    /**
     * Searches for closest (to current token) type definition in curly braces.
     * Skips sequence IGNORED - LCURL - IGNORED where IGNORED is token ignored by {@link #nextNonIgnored}
     * All IDENT tokens found in LCURL - RCURL range are returned separated by '|' character
     * @param ts token sequence to perform the search from current token
     * @return found IDENT token or null if no such token exists
     */
    private static String nextType(TokenSequence<? extends FortressCommentTokenId> ts) {
        StringBuilder sb = new StringBuilder();
        // find next token which is not OTHER_TEXT
        Token<? extends FortressCommentTokenId> nextToken = nextNonIgnored(ts);
        // if it is left curly brace try to find next IDENT token
        if (nextToken != null && nextToken.id() == FortressCommentTokenId.LCURL) {
            boolean newToken = true;
            while (ts.moveNext() && ts.token().id() != FortressCommentTokenId.RCURL) {
                TokenId tid = ts.token().id();
                if (tid == FortressCommentTokenId.IDENT || tid == FortressCommentTokenId.DOT) {
                    if (newToken) {
                        if (sb.length() > 0) { sb.append('|'); }
                    }
                    newToken = false;
                    sb.append(ts.token().text().toString());
                } else {
                    newToken = true;
                }
            }
            if (ts.token() != null && ts.token().id() == FortressCommentTokenId.RCURL) {
                return sb.toString();
            }
        }
        return null;
    }
    
    /**
     * Searches for closest (to current token) IDENT token.
     * Skips tokens ignored by {@link #nextNonIgnored} if there are any.
     * @param ts token sequence to perform the search from current token
     * @return found IDENT token or null if no such token exists
     */
    private static String nextIdent(TokenSequence<? extends FortressCommentTokenId> ts) {
        // find next token which is not OTHER_TEXT
        Token<? extends FortressCommentTokenId> nextToken = nextNonIgnored(ts);
        // if it is IDENT token return its text
        if (nextToken != null && nextToken.id() == FortressCommentTokenId.IDENT) {
            return nextToken.text().toString();
        }
        return null;
    }

    /**
     * Find the next dot-joined group of idents.
     * Skips tokens ignored by {@link #nextNonIgnored} if there are any.
     * @param ts token sequence to perform the search from current token
     * @return found IDENT token group or null if no such token exists
     */
    private static String nextIdentGroup(TokenSequence<? extends FortressCommentTokenId> ts) {
        // find next token which is not OTHER_TEXT
        Token<? extends FortressCommentTokenId> nextToken = nextNonIgnored(ts);
        // if it is IDENT token return its text
        if (nextToken != null && nextToken.id() == FortressCommentTokenId.IDENT) {
            // Peek to see if we have a dot next to it
            if (ts.moveNext()) {
                if (ts.token().id() == FortressCommentTokenId.DOT) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(nextToken.text());
                    
                    boolean goback = true;
                    while (ts.token().id() == FortressCommentTokenId.DOT ||
                            ts.token().id() == FortressCommentTokenId.IDENT) {
                        sb.append(ts.token().text());
                        if (!ts.moveNext()) {
                            goback = false;
                            break;
                        }
                    }
                    if (goback) {
                        ts.movePrevious();
                    }
                    return sb.toString();
                }
                
                ts.movePrevious();
            }
            return nextToken.text().toString();
        }
        return null;
    }
    
    /**
     * Searches for next token that is not OTHER_TEXT or RCURLY
     */
    private static Token<? extends FortressCommentTokenId> nextNonIgnored(TokenSequence<? extends FortressCommentTokenId> ts) {
        while (ts.moveNext() && (
                ts.token().id() == FortressCommentTokenId.OTHER_TEXT ||
                ts.token().id() == FortressCommentTokenId.RCURL)) {
        }
        return ts.token();
    }
    
}
