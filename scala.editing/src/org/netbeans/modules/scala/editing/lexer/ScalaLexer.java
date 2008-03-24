/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.scala.editing.rats.LexerScala;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;
import xtc.parser.Result;
import xtc.tree.GNode;
import xtc.util.Pair;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaLexer implements Lexer<ScalaTokenId> {

    private LexerRestartInfo<ScalaTokenId> info;
    private LexerInput input;
    private TokenFactory<ScalaTokenId> tokenFactory;
    private LexerInputReader lexerInputReader;
    private static ScalaLexer cached;
    private List<TokenInfo> tokenStream = new ArrayList<TokenInfo>();
    /** 
     * tokenStream.iterator() always return a new iterator, which point the first
     * item, so we should have a global one.
     */
    private Iterator<TokenInfo> tokenStreamItr = tokenStream.iterator();

    private ScalaLexer() {
    }

    public static synchronized ScalaLexer create(LexerRestartInfo<ScalaTokenId> info) {
        ScalaLexer lexer = cached;

        if (lexer == null) {
            lexer = new ScalaLexer();
        }

        lexer.restart(info);

        return lexer;
    }

    private void restart(LexerRestartInfo<ScalaTokenId> info) {
        this.info = info;
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();

        lexerInputReader = new LexerInputReader(input);
        /** 
         * @Note: it seems input at this time is empty, so we can not do scanning here 
         */
        tokenStream.clear();
        tokenStreamItr = tokenStream.iterator();
    }

    public Object state() {
        return null;
    }

    public Token<ScalaTokenId> nextToken() {
        /** 
         * @Note: don't let Rats! handle EOF, which may not properly handle input's
         * readLength when meets LexerInput.EOF
         */
        if (input.read() == LexerInput.EOF) {
            return null;
        }
        input.backup(1);

        if (!tokenStreamItr.hasNext()) {
            tokenStream.clear();
            scanTokens();
            tokenStreamItr = tokenStream.iterator();
            input.backup(input.readLength());
        }

        if (tokenStreamItr.hasNext()) {
            TokenInfo tokeninfo = tokenStreamItr.next();

            for (int i = 0; i < tokeninfo.length; i++) {
                input.read();
            }
            return tokenFactory.createToken(tokeninfo.id);
        } else {
            assert false : "unrecgnozied input" + input.read();
            return null;
        }
    }

    private Result scanTokens() {
        /**
         * We cannot keep an instance scope lexer, since lexer (sub-class of ParserBase)
         * has internal states which keep the read-in chars, index and others, it really
         * difficult to handle.
         */
        LexerScala scanner = new LexerScala(lexerInputReader, "<current>");
        try {
            Result r = scanner.pToken(0);
            if (r.hasValue()) {
                GNode node = (GNode) r.semanticValue();
                flattenToTokenSteam(node);
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

    private void flattenToTokenSteam(GNode node) {
        assert (node.size() > 0) : "This generic node:" + node.getName() + " is defined with non generic child!";

        for (int i = 0; i < node.size(); i++) {
            Object child = node.get(i);
            if (child == null) {
                /** child may be null */
                continue;
            }
            
            if (child instanceof GNode) {
                flattenToTokenSteam((GNode) child);
            } else if (child instanceof Pair) {
                assert false : "Pair:" + child + " to be process, do you add 'flatten' option on grammar file?";
            } else if (child instanceof String) {
                int length = ((String) child).length();
                TokenInfo tokenInfo = new TokenInfo();
                tokenInfo.length = length;
                try {
                    tokenInfo.id = ScalaTokenId.valueOf(node.getName());
                } catch (IllegalArgumentException ex) {
                    tokenInfo.id = ScalaTokenId.IGNORED;
                }
                //System.out.println("Node=" + node.getName() + ", tokenInfo=" + tokenInfo + "text=" + child);
                tokenStream.add(tokenInfo);
            } else {
                System.out.println("To be process: " + child);
            }
        }
    }

    public void release() {
        cached = null;
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

    private static class TokenInfo {

        int length;
        ScalaTokenId id;

        @Override
        public String toString() {
            return "(id=" + id + ", length=" + length + ")";
        }
    }

}
