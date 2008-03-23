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
import java.io.Reader;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.scala.editing.rats.LexerScala;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;
import xtc.parser.Result;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaXmlLexer implements Lexer<ScalaXmlTokenId> {

    private LexerInput input;
    private TokenFactory<ScalaXmlTokenId> tokenFactory;
    private LexerInputReader lexerInputReader;

    public ScalaXmlLexer(LexerRestartInfo<ScalaXmlTokenId> info) {
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        assert (info.state() == null); // passed argument always null

        lexerInputReader = new LexerInputReader(input);
    }

    public Object state() {
        return null;
    }

    public Token<ScalaXmlTokenId> nextToken() {
        Result result = scanOneToken();
        if (result != null) {
            ScalaXmlTokenId tokenId = (ScalaXmlTokenId) result.semanticValue();
            /**
             * lexer may read over token's length for prediction, since it use its own 
             * char buffer, it can remember the extractly position (the position is also 
             * returned in result.index), but it doesn't backup lexer input, so we
             * should do it here:
             */
            int tokenLength = result.index;
            int readLength = input.readLength();
            if (readLength > tokenLength) {
                input.backup(readLength - tokenLength);
            }
            
            if (input.readLength() == 0) {
                /** EOF? */
                return null;
            } else {
                Token<ScalaXmlTokenId> token = tokenFactory.createToken(tokenId);
                return token;
            }
        } else {
            return null;
        }
    }

    private Result scanOneToken() {
        /**
         * We cannot keep an instance lexer, since lexer (sub-class of ParserBase)
         * has internal states which keep the read-in chars, index and others.
         */
        LexerScala lexer = new LexerScala(lexerInputReader, "<current>");

        try {
            Result r = lexer.ptoken(0);
            if (r.hasValue()) {
                return r;
            } else {
                System.err.println(r.parseError().msg);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void release() {
    }

    /**
     * Hacking for <code>xtc.parser.ParserBase</code> of Rats! which use <code>java.io.Reader</code>
     * as the chars input, but uses only {@link java.io.Reader#read()} of all methods in 
     * {@link xtc.parser.ParserBase#character(int)}
     */
    private static class LexerInputReader extends Reader {

        private LexerInput input;

        LexerInputReader(LexerInput input) {
            this.input = input;
        }

        @Override
        public int read() throws IOException {
            int c = input.read();

            if (c == LexerInput.EOF) {
                return -1;
            }

            return c;
        }

        @Override
        public int read(char[] arg0, int arg1, int arg2) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void close() throws IOException {
        }
    }
}
