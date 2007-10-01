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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.lexer.editorbridge.calc.lang;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;


/**
 * Lexer for the Calc Language.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public final class CalcLexer implements Lexer<CalcTokenId> {

    private static final int EOF = LexerInput.EOF;

    private static final Map<String,CalcTokenId> keywords = new HashMap<String,CalcTokenId>();
    static {
        keywords.put(CalcTokenId.E.fixedText(), CalcTokenId.E);
        keywords.put(CalcTokenId.PI.fixedText(), CalcTokenId.PI);
    }
    
    private LexerInput input;
    
    private TokenFactory<CalcTokenId> tokenFactory;

    CalcLexer(LexerRestartInfo<CalcTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        assert (info.state() == null); // passed argument always null
    }
    
    public Token<CalcTokenId> nextToken() {
        while (true) {
            int ch = input.read();
            switch (ch) {
                case '+':
                    return token(CalcTokenId.PLUS);

                case '-':
                    return token(CalcTokenId.MINUS);

                case '*':
                    return token(CalcTokenId.STAR);

                case '/':
                    switch (input.read()) {
                        case '/': // in single-line comment
                            while (true)
                                switch (input.read()) {
                                    case '\r': input.consumeNewline();
                                    case '\n':
                                    case EOF:
                                        return token(CalcTokenId.LINE_COMMENT);
                                }
                        case '*': // in multi-line comment
                            while (true) {
                                ch = input.read();
                                while (ch == '*') {
                                    ch = input.read();
                                    if (ch == '/')
                                        return token(CalcTokenId.BLOCK_COMMENT);
                                    else if (ch == EOF)
                                        return token(CalcTokenId.BLOCK_COMMENT_INCOMPLETE);
                                }
                                if (ch == EOF)
                                    return token(CalcTokenId.BLOCK_COMMENT_INCOMPLETE);
                            }
                    } // end of switch()
                    input.backup(1);
                    return token(CalcTokenId.SLASH);

                case '(':
                    return token(CalcTokenId.LPAREN);

                case ')':
                    return token(CalcTokenId.RPAREN);

                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                case '.':
                    return finishIntOrFloatLiteral(ch);

                case EOF:
                    return null;

                default:
                    if (Character.isWhitespace((char)ch)) {
                        ch = input.read();
                        while (ch != EOF && Character.isWhitespace((char)ch)) {
                            ch = input.read();
                        }
                        input.backup(1);
                        return token(CalcTokenId.WHITESPACE);
                    }

                    if (Character.isLetter((char)ch)) { // identifier or keyword
                        while (true) {
                            if (ch == EOF || !Character.isLetter((char)ch)) {
                                input.backup(1); // backup the extra char (or EOF)
                                // Check for keywords
                                CalcTokenId id = keywords.get(input.readText());
                                if (id == null) {
                                    id = CalcTokenId.IDENTIFIER;
                                }
                                return token(id);
                            }
                            ch = input.read(); // read next char
                        }
                    }

                    return token(CalcTokenId.ERROR);
            }
        }
    }

    public Object state() {
        return null;
    }

    private Token<CalcTokenId> finishIntOrFloatLiteral(int ch) {
        boolean floatLiteral = false;
        boolean inExponent = false;
        while (true) {
            switch (ch) {
                case '.':
                    if (floatLiteral) {
                        return token(CalcTokenId.FLOAT_LITERAL);
                    } else {
                        floatLiteral = true;
                    }
                    break;
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    break;
                case 'e': case 'E': // exponent part
                    if (inExponent) {
                        return token(CalcTokenId.FLOAT_LITERAL);
                    } else {
                        floatLiteral = true;
                        inExponent = true;
                    }
                    break;
                default:
                    input.backup(1);
                    return token(floatLiteral ? CalcTokenId.FLOAT_LITERAL
                            : CalcTokenId.INT_LITERAL);
            }
            ch = input.read();
        }
    }
    
    private Token<CalcTokenId> token(CalcTokenId id) {
        return (id.fixedText() != null)
            ? tokenFactory.getFlyweightToken(id, id.fixedText())
            : tokenFactory.createToken(id);
    }

    public void release() {
    }

}