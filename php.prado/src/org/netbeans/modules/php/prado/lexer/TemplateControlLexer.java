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
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Petr Pisl
 */
public class TemplateControlLexer implements Lexer<TemplateControlTokenId>{

    private final TemplateControlColoringLexer scanner;
    private TokenFactory<TemplateControlTokenId> tokenFactory;

    private TemplateControlLexer(LexerRestartInfo<TemplateControlTokenId> info) {
        scanner = new TemplateControlColoringLexer(info, (State)info.state());
        tokenFactory = info.tokenFactory();
    }

    public static synchronized TemplateControlLexer create(LexerRestartInfo<TemplateControlTokenId> info) {
        return new TemplateControlLexer(info);
    }

    public Token<TemplateControlTokenId> nextToken() {
        TemplateControlTokenId tokenId = scanner.nextToken();
        Token<TemplateControlTokenId> token = null;
        if (tokenId != null) {
            token = tokenFactory.createToken(tokenId);
        }
        return token;
    }

    public Object state() {
        return scanner.getState();
    }

    public void release() {}

    public enum State {
        PROPERTY_NAME,
        PROPERTY_VALUE
    }

    private class TemplateControlColoringLexer {

        private State state;
        private LexerInput input;

        public TemplateControlColoringLexer(LexerRestartInfo<TemplateControlTokenId> info, State state) {
            this.input = info.input();
            if (state == null) {
                this.state = State.PROPERTY_NAME;
            }
            else {
                this.state = state;
            }
        }

        State getState() {
            return state;
        }

        public TemplateControlTokenId nextToken() {
            int c = input.read();
            if (c == LexerInput.EOF) {
                return null;
            }
            while (c != LexerInput.EOF) {
                char cc = (char)c;

//                if (cc == ' ') {
//                    return TemplateControlTokenId.T_WHITESPACE;
//                }
                if (state == State.PROPERTY_NAME){
                    if (cc == '=') {
                        if (input.readLength() > 1) {
                            input.backup(1);
                            return TemplateControlTokenId.T_PROPERTY;
                        }
                        else {
                            state = State.PROPERTY_VALUE;
                            return TemplateControlTokenId.T_EQUAL;
                        }
                    }
                }
                c = input.read();
            }
            if (state == State.PROPERTY_NAME) {
                return TemplateControlTokenId.T_PROPERTY;
            }
            else {
                return TemplateControlTokenId.T_VALUE;
            }
        }
    }

}
