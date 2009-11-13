/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.php.fuse.lexer;

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * Lexical analyzer for FUSE tmpl templates
 * 
 * @author Martin Fousek
 */
public class FuseLexer implements Lexer<FuseTokenId> {

    private static final int EOF = LexerInput.EOF;
    private final LexerInput input;
    private boolean afterInclude = false;
    private final TokenFactory<FuseTokenId> tokenFactory;

    public FuseLexer(LexerRestartInfo<FuseTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        assert (info.state() == null); // never set to non-null value in state()
    }

    public Object state() {
        return null; // always in default state after token recognition
    }

    public Token<FuseTokenId> nextToken() {
        boolean endingTag = false;
        while (true) {
            int c = input.read();
            switch (c) {
//                case '"': // string literal
//                    while (true)
//                        switch (input.read()) {
//                            case '"': // NOI18N
//                                if (afterInclude) {
//                                    afterInclude = false;
//                                    return token(FuseTokenId.INCLUDE_LITERAL);
//                                }
//                                else {
//                                    return token(FuseTokenId.STRING_LITERAL);
//                                }
//                        }
//
//                case '\'': // char literal
//                    while (true)
//                        switch (input.read()) {
//                            case '\'': // NOI18N
//                                if (afterInclude) {
//                                    afterInclude = false;
//                                    return token(FuseTokenId.INCLUDE_LITERAL);
//                                }
//                                else {
//                                    return token(FuseTokenId.STRING_LITERAL);
//                                }
//                        }
//
//                case '=':
//                    if (input.read() == '=')
//                        return token(FuseTokenId.EQEQ);
//                    input.backup(1);
//                    return token(FuseTokenId.EQ);
//
//                case '>':
//                    switch (input.read()) {
//                        case '>': // after >>
//                            input.backup(1);
//                            return token(FuseTokenId.GTGT);
//                        case '=': // >=
//                            return token(FuseTokenId.GTEQ);
//                    }
//                    input.backup(1);
//                    return token(FuseTokenId.GT);
//
//                case '<':
//                    switch (input.read()) {
//                        case '<': // after <<
//                            input.backup(1);
//                            return token(FuseTokenId.LTLT);
//                        case '=': // <=
//                            return token(FuseTokenId.LTEQ);
//                    }
//                    input.backup(1);
//                    return token(FuseTokenId.LT);
//
//                case '+':
//                    switch (input.read()) {
//                        case '+':
//                            return token(FuseTokenId.PLUSPLUS);
//                        case '=':
//                            return token(FuseTokenId.PLUSEQ);
//                    }
//                    input.backup(1);
//                    return token(FuseTokenId.PLUS);
//
//                case '-':
//                    switch (input.read()) {
//                        case '-':
//                            return token(FuseTokenId.MINUSMINUS);
//                        case '=':
//                            return token(FuseTokenId.MINUSEQ);
//                    }
//                    input.backup(1);
//                    return token(FuseTokenId.MINUS);
//
//                case '*':
//                    switch (input.read()) {
//                        case '=':
//                            return token(FuseTokenId.STAREQ);
//                    }
//                    input.backup(1);
//                    return token(FuseTokenId.STAR);
//
//                case '|':
//                    switch (input.read()) {
//                        case '|':
//                            return token(FuseTokenId.BARBAR);
//                        case '=':
//                            return token(FuseTokenId.BAREQ);
//                    }
//                    input.backup(1);
//                    return token(FuseTokenId.BAR);
//
//                case '&':
//                    switch (input.read()) {
//                        case '&':
//                            return token(FuseTokenId.AMPAMP);
//                        case '=':
//                            return token(FuseTokenId.AMPEQ);
//                    }
//                    input.backup(1);
//                    return token(FuseTokenId.AMP);
//
//                case '%':
//                    if (input.read() == '=')
//                        return token(FuseTokenId.PERCENTEQ);
//                    input.backup(1);
//                    return token(FuseTokenId.PERCENT);
//
//                case '^':
//                    if (input.read() == '=')
//                        return token(FuseTokenId.CARETEQ);
//                    input.backup(1);
//                    return token(FuseTokenId.CARET);
//
//                case '!':
//                    if (input.read() == '=')
//                        return token(FuseTokenId.BANGEQ);
//                    input.backup(1);
//                    return token(FuseTokenId.BANG);
//
//                case '.':
//                    c = input.read();
//                    if ('0' <= c && c <= '9') { // float literal
//                        return finishNumberLiteral(input.read(), true);
//                    } else
//                        input.backup(1);
//                    return token(FuseTokenId.DOT);
//
//                case '~':
//                    return token(FuseTokenId.TILDE);
//                case ',':
//                    return token(FuseTokenId.COMMA);
//                case ';':
//                    return token(FuseTokenId.SEMICOLON);
//                case ':':
//                    return token(FuseTokenId.COLON);
//                case '?':
//                    return token(FuseTokenId.QUESTION);
//                case '(':
//                    return token(FuseTokenId.LPAREN);
//                case ')':
//                    return token(FuseTokenId.RPAREN);
//                case '[':
//                    return token(FuseTokenId.LBRACKET);
//                case ']':
//                    return token(FuseTokenId.RBRACKET);
//                case '{':
//                    return token(FuseTokenId.LBRACE);
//                case '}':
//                    return token(FuseTokenId.RBRACE);
//
//                // Numbers lexing
//                case '0': case '1': case '2': case '3': case '4':
//                case '5': case '6': case '7': case '8': case '9':
//                    return finishNumberLiteral(input.read(), false);
//
//                // Keywords lexing
//                case 'f':
//                   switch (c = input.read()) {
//                        case 'a':
//                            if ((c = input.read()) == 'l' && (c = input.read()) == 's' && (c = input.read()) == 'e') {
//                                return keywordOrIdentifier(FuseTokenId.FALSE);
//                            }
//                            break;
//                    }
//                    return finishIdentifier(c);
//
//                case 'i':
//                    switch (c = input.read()) {
//                        case 'n':
//                            if ((c = input.read()) == 'c' && (c = input.read()) == 'l' && (c = input.read()) == 'u' && (c = input.read()) == 'd' && (c = input.read()) == 'e') {
//                                if ((c = input.read()) == '_' && (c = input.read()) == 'o' && (c = input.read()) == 'n' && (c = input.read()) == 'c' && (c = input.read()) == 'e') {
//                                    afterInclude = true;
//                                    return keywordOrIdentifier(FuseTokenId.INCLUDE_ONCE);
//                                }
//                                else if (c == ' '){
//                                    input.backup(1);
//                                    afterInclude = true;
//                                    return keywordOrIdentifier(FuseTokenId.INCLUDE);
//                                }
//                            }
//                            break;
//                    }
//                    return finishIdentifier(c);
//
//                case 'n':
//                   switch (c = input.read()) {
//                        case 'u':
//                            if ((c = input.read()) == 'l' && (c = input.read()) == 'l') {
//                                return keywordOrIdentifier(FuseTokenId.NULL);
//                            }
//                            break;
//                    }
//                    return finishIdentifier(c);
//
//                case 't':
//                   switch (c = input.read()) {
//                        case 'r':
//                            if ((c = input.read()) == 'u' && (c = input.read()) == 'e') {
//                                return keywordOrIdentifier(FuseTokenId.TRUE);
//                            }
//                            break;
//                    }
//                    return finishIdentifier(c);
//
//                case 'r':
//                    switch (c = input.read()) {
//                        case 'e':
//                            if ((c = input.read()) == 'q' && (c = input.read()) == 'u' && (c = input.read()) == 'i' && (c = input.read()) == 'r' && (c = input.read()) == 'e') {
//                                if ((c = input.read()) == '_' && (c = input.read()) == 'o' && (c = input.read()) == 'n' && (c = input.read()) == 'c' && (c = input.read()) == 'e') {
//                                    afterInclude = true;
//                                    return keywordOrIdentifier(FuseTokenId.REQUIRE_ONCE);
//                                }
//                                else if (c == ' '){
//                                    input.backup(1);
//                                    afterInclude = true;
//                                    return keywordOrIdentifier(FuseTokenId.REQUIRE);
//                                }
//                            }
//                            break;
//                    }
//                    return finishIdentifier(c);
//
                case 'I':
                    switch (c = input.read()) {
                        case 'F':
                            return keywordOrIdentifier(FuseTokenId.IF, endingTag);
                        case 'T':
                            if ((c = input.read()) == 'E' && (c = input.read()) == 'R' && (c = input.read()) == 'A' && (c = input.read()) == 'T' && (c = input.read()) == 'O' && (c = input.read()) == 'R') {
                                return keywordOrIdentifier(FuseTokenId.ITERATOR, endingTag);
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'D':
                    switch (c = input.read()) {
                        case 'B':
                            if ((c = input.read()) == '_' && (c = input.read()) == 'L' && (c = input.read()) == 'O' && (c = input.read()) == 'O' && (c = input.read()) == 'P') {
                                return keywordOrIdentifier(FuseTokenId.DB_LOOP, endingTag);
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'E':
                    switch (c = input.read()) {
                        case 'L':
                            if ((c = input.read()) == 'S' && (c = input.read()) == 'E') {
                                return keywordOrIdentifier(FuseTokenId.ELSE);
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'L':
                    if ((c = input.read()) == 'O' && (c = input.read()) == 'O' && (c = input.read()) == 'P') {
                        return keywordOrIdentifier(FuseTokenId.LOOP, endingTag);
                    }
                    return finishIdentifier(c);

                case 'W':
                    if ((c = input.read()) == 'H' && (c = input.read()) == 'I' && (c = input.read()) == 'L' && (c = input.read()) == 'E') {
                        return keywordOrIdentifier(FuseTokenId.WHILE, endingTag);
                    }
                    return finishIdentifier(c);

                // Rest of letters starting identifiers
//                case 'a':
//                case 'b':
//                case 'c':
//                case 'd':
//                case 'e':
//                case 'g':
//                case 'h':
//                case 'j':
//                case 'k':
//                case 'l':
//                case 'm':
//                case 'o':
//                case 'p':
//                case 'q':
//                case 's':
//                case 'u':
//                case 'v':
//                case 'w':
//                case 'x':
//                case 'y':
//                case 'z':
//                case 'A':
//                case 'B':
//                case 'C':
//                case 'F':
//                case 'G':
//                case 'H':
//                case 'J':
//                case 'K':
//                case 'M':
//                case 'N':
//                case 'O':
//                case 'P':
//                case 'Q':
//                case 'R':
//                case 'S':
//                case 'T':
//                case 'U':
//                case 'V':
//                case 'X':
//                case 'Y':
//                case 'Z':
//                case '$':
//                case '_':
//                case '@':
//                    return finishIdentifier();

                case '/':
                    endingTag = true;
                    continue;
//                case '\t':
//                case '\n':
//                case 0x0b:
//                case '\f':
//                case '\r':
//                case 0x1c:
//                case 0x1d:
//                case 0x1e:
//                case 0x1f:
//                    return finishWhitespace();
//                case ' ':
//                    c = input.read();
//                    if (c == EOF || !Character.isWhitespace(c)) { // Return single space as flyweight token
//                        input.backup(1);
//                        return tokenFactory.getFlyweightToken(FuseTokenId.WHITESPACE, " ");
//                    }
//                    return finishWhitespace();

                case EOF:
                    if (endingTag)
                        return finishIdentifier();
                    else
                        return null;

                default:
                    return finishIdentifier(); //token(FuseTokenId.ERROR);
            } // end of switch (c)
        } // end of while(true)
    }

    private Token<FuseTokenId> finishWhitespace() {
        while (true) {
            int c = input.read();
            if (c == EOF || !Character.isWhitespace(c)) {
                input.backup(1);
                return tokenFactory.createToken(FuseTokenId.WHITESPACE);
            }
        }
    }

    private Token<FuseTokenId> finishNumberLiteral(int c, boolean inFraction) {
        while (true) {
            switch (c) {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    break;
                default:
                    input.backup(1);
                    return token(inFraction ? FuseTokenId.DOUBLE_LITERAL
                            : FuseTokenId.INT_LITERAL);
            }
            c = input.read();
        }
    }

    private Token<FuseTokenId> finishIdentifier() {
        return finishIdentifier(input.read());
    }

    private Token<FuseTokenId> finishIdentifier(int c) {
        while (true) {
            if (c == EOF || !(Character.isJavaIdentifierPart(c) || c == '$' || c == '_')) {
                // For surrogate 2 chars must be backed up
                input.backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
                return tokenFactory.createToken(FuseTokenId.IDENTIFIER);
            }
            c = input.read();
        }
    }

    private Token<FuseTokenId> keywordOrIdentifier(FuseTokenId keywordId, boolean endingTag) {
        return keywordOrIdentifier(keywordId, input.read(), endingTag);
    }

    private Token<FuseTokenId> keywordOrIdentifier(FuseTokenId keywordId) {
        return keywordOrIdentifier(keywordId, input.read(), false);
    }

    private Token<FuseTokenId> keywordOrIdentifier(FuseTokenId keywordId, int c, boolean endingTag) {
        // Check whether the given char is non-ident and if so then return keyword
        if (c == EOF || !(Character.isJavaIdentifierPart(c) || c == '$' || c == '_')) {
            // For surrogate 2 chars must be backed up
            input.backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
            if (endingTag) {
                return token(FuseTokenId.valueOf(keywordId.toString()+"_END"));
            }
            else
            return token(keywordId);
        } else // c is identifier part
        {
            return finishIdentifier();
        }
    }

    private Token<FuseTokenId> token(FuseTokenId id) {
        String fixedText = id.fixedText();
        return (fixedText != null)
                ? tokenFactory.getFlyweightToken(id, fixedText)
                : tokenFactory.createToken(id);
    }

    public void release() {
    }
}
