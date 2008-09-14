/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.prado.lexer;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.php.editor.lexer.PHP5ColoringLexer;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Petr Pisl
 */
public class GSFPageLexer implements Lexer<PageTokenId> {

    private final PageColoringLexer scanner;
    private TokenFactory<PageTokenId> tokenFactory;

    private GSFPageLexer(LexerRestartInfo<PageTokenId> info) {
        scanner = new PageColoringLexer(info, (State)info.state());
        tokenFactory = info.tokenFactory();
    }

    public static synchronized GSFPageLexer create(LexerRestartInfo<PageTokenId> info) {
        return new GSFPageLexer(info);
    }

    public Token<PageTokenId> nextToken() {
        PageTokenId tokenId = scanner.nextToken();
        Token<PageTokenId> token = null;
        if (tokenId != null) {
            token = tokenFactory.createToken(tokenId);
        }
        return token;
    }

    public Object state() {
        return scanner.getState();
    }

    public void release() {
    }

    public enum State {
        OUTER,
        AFTER_GT,
        AFTER_PERCENT,
        IN_TEMPLATE_CONTROL,
        IN_PHP
    }

    public class PageColoringLexer {

        State state;

        LexerInput input;

        public PageColoringLexer(LexerRestartInfo<PageTokenId> info, State state) {
            this.input = info.input();
            if (state == null) {
                this.state = State.OUTER;
            }
            else {
                this.state = state;
            }
        }

        State getState() {
            return state;
        }
        
        public PageTokenId nextToken() {
            int c = input.read();
            if (c == LexerInput.EOF) {
                return null;
            }
            while (c != LexerInput.EOF) {
                char cc = (char) c;
                switch (state) {
                    case OUTER:
                        switch (cc) {
                            case '<':
                                state = State.AFTER_GT;
                                break;
                        }
                        break;
                    case AFTER_GT:
                        switch (cc) {
                            case '%':
                                state = State.AFTER_PERCENT;
                                break;
                            default:
                                state = State.OUTER;
                        }
                        break;
                    case AFTER_PERCENT:
                        switch (cc) {
                            case '@':
                            case '=':
                            case '%':
                                if (input.readLength() > 3) {
                                    input.backup(3);
                                    state = State.OUTER;
                                    return PageTokenId.T_INLINE_HTML;
                                }
                                if (cc == '@') {
                                    state = State.IN_TEMPLATE_CONTROL;
                                }
                                else {
                                    state = State.IN_PHP;
                                }
                                return PageTokenId.T_CONTROL_OPEN_TAG;
                            default:
                                state = State.OUTER;
                        }
                        break;
                    case IN_TEMPLATE_CONTROL:
                    case IN_PHP:
                        int length = input.readLength();
                        if (length > 2 && input.readText().charAt(length-2) == '%'
                                && input.readText().charAt(length-1) == '>') {
                            input.backup(2);
                            if (state == State.IN_TEMPLATE_CONTROL) {
                                return PageTokenId.T_TEMPLATE_CONTROL;
                            }
                            else {
                                return PageTokenId.T_PHP;
                            }
                        }
                        else if (length == 2 && input.readText().charAt(length-2) == '%'
                                && input.readText().charAt(length-1) == '>') {
                            state = State.OUTER;
                            return PageTokenId.T_CLOSE_TAG;
                        }
                        break;
                }
                c = input.read();
            }
            return PageTokenId.T_INLINE_HTML;
        }
    }
}
