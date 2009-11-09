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
package org.netbeans.modules.php.fuse.lexer;

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Martin Fousek
 */
public class FuseTopLexer implements Lexer<FuseTopTokenId> {

    private final FuseTopColoringLexer scanner;
    private TokenFactory<FuseTopTokenId> tokenFactory;

    private FuseTopLexer(LexerRestartInfo<FuseTopTokenId> info) {
        State state = info.state() == null? State.INIT : (State)info.state();
        this.tokenFactory = info.tokenFactory();
        scanner = new FuseTopColoringLexer(info, state);
    }

    public static synchronized FuseTopLexer create(LexerRestartInfo<FuseTopTokenId> info) {
        return new FuseTopLexer(info);
    }

    public Token<FuseTopTokenId> nextToken() {
        FuseTopTokenId tokenId = scanner.nextToken();
        Token<FuseTopTokenId> token = null;
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

    private enum State {
        INIT,
        OUTER,
        AFTER_LB,
        IN_FUSE_DELIMITER,
        IN_END_DELIMITER,
        AFTER_FUSE_DELIMITER,
        IN_FUSE
    }

    private class FuseTopColoringLexer {

        private State state;
        private final LexerInput input;

        public FuseTopColoringLexer(LexerRestartInfo<FuseTopTokenId> info, State state) {
            this.input = info.input();
            this.state = state;
        }

        public FuseTopTokenId nextToken() {
            int c = input.read();
            CharSequence text;
            int textLength;
            if (c == LexerInput.EOF) {
                return null;
            }
            while (c != LexerInput.EOF) {
                char cc = (char) c;
                text = input.readText();
                textLength = text.length();
                switch (state) {
                    case INIT:
                    case OUTER:
                        if (cc == '<') {
                            state = State.AFTER_LB;
                        }
                        break;
                    case AFTER_LB:
                        switch (cc) {
                            case '{':
                                state = State.IN_FUSE_DELIMITER;
                                if (textLength > 2) {
                                    input.backup(2);
                                    return FuseTopTokenId.T_HTML;
                                } else if (textLength == 2) {
                                    input.backup(2);
                                } 
                                break;
                            default:
                                state = State.OUTER;
                        }
                        break;
                    case IN_FUSE_DELIMITER:
                        if (input.readLength() == 2) {
                            state = State.IN_FUSE;
                            return FuseTopTokenId.T_FUSE_OPEN_DELIMITER;
                        }
                        break;
                    case AFTER_FUSE_DELIMITER:
                        if (input.readLength() == 2) {
                            state = State.OUTER;
                            return FuseTopTokenId.T_FUSE_CLOSE_DELIMITER;
                        }
                        break;
                    case IN_FUSE:
                        switch (cc) {
                            case '}':
                                state = State.IN_END_DELIMITER;
                                break;
                        }
                        break;
                    case IN_END_DELIMITER:
                        if (cc == '>') {
                            if (textLength == 2) {
                                state = State.OUTER;
                                return FuseTopTokenId.T_FUSE_CLOSE_DELIMITER;
//                                }
                            } else {
                                state = State.AFTER_FUSE_DELIMITER;
                                input.backup(2);
                                return FuseTopTokenId.T_FUSE;
                            }
                        }
                }
                c = input.read();
            }

            switch (state) {
                case IN_FUSE:
                    return FuseTopTokenId.T_FUSE;
                case IN_FUSE_DELIMITER:
                    return FuseTopTokenId.T_FUSE_OPEN_DELIMITER;
                case AFTER_FUSE_DELIMITER:
                    return FuseTopTokenId.T_FUSE_CLOSE_DELIMITER;
                default:
                    return FuseTopTokenId.T_HTML;
            }
        }

        Object getState() {
            return state;
        }
    }
}
