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
        while (true) {
            int c = input.read();
            switch (c) {
                // Keywords lexing
                case 'i':
                    switch (c = input.read()) {
                        case 'n':
                            if ((c = input.read()) == 'c' && (c = input.read()) == 'l' && (c = input.read()) == 'u' && (c = input.read()) == 'd' && (c = input.read()) == 'e') {
                                if ((c = input.read()) == '_' && (c = input.read()) == 'o' && (c = input.read()) == 'n' && (c = input.read()) == 'c' && (c = input.read()) == 'e') {
                                    return keywordOrIdentifier(FuseTokenId.INCLUDE_ONCE);
                                }
                                else if (c == ' '){
                                    input.backup(1);
                                    return keywordOrIdentifier(FuseTokenId.INCLUDE);
                                }
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'r':
                    switch (c = input.read()) {
                        case 'e':
                            if ((c = input.read()) == 'q' && (c = input.read()) == 'u' && (c = input.read()) == 'i' && (c = input.read()) == 'r' && (c = input.read()) == 'e') {
                                if ((c = input.read()) == '_' && (c = input.read()) == 'o' && (c = input.read()) == 'n' && (c = input.read()) == 'c' && (c = input.read()) == 'e') {
                                    return keywordOrIdentifier(FuseTokenId.REQUIRE_ONCE);
                                }
                                else if (c == ' '){
                                    input.backup(1);
                                    return keywordOrIdentifier(FuseTokenId.REQUIRE);
                                }
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'I':
                    switch (c = input.read()) {
                        case 'F':
                            return keywordOrIdentifier(FuseTokenId.IF);
                        case 'T':
                            if ((c = input.read()) == 'E' && (c = input.read()) == 'R' && (c = input.read()) == 'A' && (c = input.read()) == 'T' && (c = input.read()) == 'O' && (c = input.read()) == 'R') {
                                return keywordOrIdentifier(FuseTokenId.ITERATOR);
                            }
                            break;
                    }
                    return finishIdentifier(c);

                case 'D':
                    switch (c = input.read()) {
                        case 'B':
                            if ((c = input.read()) == '_' && (c = input.read()) == 'L' && (c = input.read()) == 'O' && (c = input.read()) == 'O' && (c = input.read()) == 'P') {
                                return keywordOrIdentifier(FuseTokenId.DB_LOOP);
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
                        return keywordOrIdentifier(FuseTokenId.LOOP);
                    }
                    return finishIdentifier(c);

                case 'W':
                    if ((c = input.read()) == 'H' && (c = input.read()) == 'I' && (c = input.read()) == 'L' && (c = input.read()) == 'E') {
                        return keywordOrIdentifier(FuseTokenId.WHILE);
                    }
                    return finishIdentifier(c);

                // Rest of letters starting identifiers
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case 'A':
                case 'B':
                case 'C':
                case 'F':
                case 'G':
                case 'H':
                case 'J':
                case 'K':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'X':
                case 'Y':
                case 'Z':
                    return finishIdentifier();

                case '\t':
                case '\n':
                case 0x0b:
                case '\f':
                case '\r':
                case 0x1c:
                case 0x1d:
                case 0x1e:
                case 0x1f:
                    return finishWhitespace();
                case ' ':
                    c = input.read();
                    if (c == EOF || !Character.isWhitespace(c)) { // Return single space as flyweight token
                        input.backup(1);
                        return tokenFactory.getFlyweightToken(FuseTokenId.WHITESPACE, " ");
                    }
                    return finishWhitespace();

                case EOF:
                    return null;

                default:
                    return token(FuseTokenId.ERROR);
            } // end of switch (c)
        } // end of while(true)
    }

    private int translateSurrogates(int c) {
        if (Character.isHighSurrogate((char) c)) {
            int lowSurr = input.read();
            if (lowSurr != EOF && Character.isLowSurrogate((char) lowSurr)) {
                // c and lowSurr form the integer unicode char.
                c = Character.toCodePoint((char) c, (char) lowSurr);
            } else {
                // Otherwise it's error: Low surrogate does not follow the high one.
                // Leave the original character unchanged.
                // As the surrogates do not belong to any
                // specific unicode category the lexer should finally
                // categorize them as a lexical error.
                input.backup(1);
            }
        }
        return c;
    }

    private Token<FuseTokenId> finishWhitespace() {
        while (true) {
            int c = input.read();
            // There should be no surrogates possible for whitespace
            // so do not call translateSurrogates()
            if (c == EOF || !Character.isWhitespace(c)) {
                input.backup(1);
                return tokenFactory.createToken(FuseTokenId.WHITESPACE);
            }
        }
    }

    private boolean foundWhitespace(int c) {
        switch(c) {
            case '\t':
            case '\n':
            case 0x0b:
            case '\f':
            case '\r':
            case 0x1c:
            case 0x1d:
            case 0x1e:
            case 0x1f:
            case ' ':
                return true;
        }
        return false;
    }

    private Token<FuseTokenId> finishIdentifier() {
        return finishIdentifier(input.read());
    }

    private Token<FuseTokenId> finishIdentifier(int c) {
        while (true) {
            if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
                // For surrogate 2 chars must be backed up
                input.backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
                return tokenFactory.createToken(FuseTokenId.IDENTIFIER);
            }
            c = input.read();
        }
    }

    private Token<FuseTokenId> keywordOrIdentifier(FuseTokenId keywordId) {
        return keywordOrIdentifier(keywordId, input.read());
    }

    private Token<FuseTokenId> keywordOrIdentifier(FuseTokenId keywordId, int c) {
        // Check whether the given char is non-ident and if so then return keyword
        if (c == EOF || !Character.isJavaIdentifierPart(c = translateSurrogates(c))) {
            // For surrogate 2 chars must be backed up
            input.backup((c >= Character.MIN_SUPPLEMENTARY_CODE_POINT) ? 2 : 1);
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
