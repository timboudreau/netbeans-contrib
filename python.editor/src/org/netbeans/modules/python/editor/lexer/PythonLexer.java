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

package org.netbeans.modules.python.editor.lexer;

import java.io.IOException;
import java.util.logging.Logger;
import org.antlr.runtime.CharStream;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;
import org.openide.util.Exceptions;
import org.python.antlr.PythonTreeWalker.PyLexer;

/**
 * @todo incremental parsing
 * @todo mapping of all tokens
 * @todo myCharBuffer used in Groovy doesn't seem to be needed as length of 
 * the text read from LexerInput and ANTLR token seem to be the same - check it
 * 
 * @author Martin Adamek
 */
public final class PythonLexer implements Lexer<PythonTokenId> {

    private static final Logger LOG = Logger.getLogger(PythonLexer.class.getName());
    
    public static final int ERROR = 333;
    
    private PyLexer scanner;
    private LexerInput lexerInput;
    private TokenFactory<PythonTokenId> tokenFactory;

    public PythonLexer(LexerRestartInfo<PythonTokenId> info) {
        tokenFactory = info.tokenFactory();
        this.lexerInput = info.input();
        this.scanner = new PyLexer((CharStream)null);
        try {
            restart(info);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void restart(LexerRestartInfo<PythonTokenId> info) throws IOException {
        tokenFactory = info.tokenFactory();
        this.lexerInput = info.input();

        CharStream charStream = null;
        if (lexerInput != null) {
            charStream = new LexerInputCharStream(lexerInput);
        }
        scanner.setCharStream(charStream);
        if (charStream != null) {
            scanner.reset();
        }
    }

    private Token<PythonTokenId> createToken(int tokenIntId, int tokenLength) {
        PythonTokenId id = getTokenId(tokenIntId);
        LOG.finest("Creating token: " + id.name() + ", length: " + tokenLength);
        String fixedText = id.fixedText();
        return (fixedText != null) ? tokenFactory.getFlyweightToken(id, fixedText)
                                   : tokenFactory.createToken(id, tokenLength);
    }
    
    public Token<PythonTokenId> nextToken() {
        LOG.finest("");
        try {
            org.antlr.runtime.Token antlrToken = scanner.nextToken();
            
//            System.out.println("### antlrToken: " + antlrToken.getType() + " [" + antlrToken.getText() + "]");
            
            if (antlrToken.getType() == -1) {
                return null;
            }
            
            LOG.finest("Received token from antlr: " + antlrToken);
            if (antlrToken != null) {
                int intId = antlrToken.getType();

                int len = lexerInput.readLengthEOF();// - myCharBuffer.getExtraCharCount();
                if ( antlrToken.getText() != null ) {
                    len = Math.max( len, antlrToken.getText().length() );
                    LOG.finest("Counting length from " + lexerInput.readLengthEOF() + " and ");// + myCharBuffer.getExtraCharCount());
                }
                LOG.finest("Length of token to create: " + len);

                return createToken(intId, len);

            } 
            else { // antlrToken is null
                LOG.finest("Antlr token was null");
                int scannerTextTokenLength = scanner.getText().length();
                if ( scannerTextTokenLength > 0 ) {
                    return createToken(org.python.antlr.PythonLexer.WS, scannerTextTokenLength);
                }
                return null;  // no more tokens from tokenManager
            }
        } catch (Exception e) {
            LOG.finest("Caught exception: " + e);
            int len = lexerInput.readLength();// - myCharBuffer.getExtraCharCount();
            int tokenLength = lexerInput.readLength();
            
            scanner.reset();
            
            while (len < tokenLength) {
                LOG.finest("Consuming character");
//                scannerConsumeChar();
                len++;
            }
            return createToken(PythonTokenId.ERROR.ordinal(), tokenLength);
        }
    }

    public Object state() {
        return null;
    }

    public void release() {
    }

    private PythonTokenId getTokenId(int token) {
        if (token >= org.python.antlr.PythonLexer.T169 && token <= org.python.antlr.PythonLexer.T195) {
            return PythonTokenId.ANY_KEYWORD;
        }
        switch (token) {
            case ERROR:
                return PythonTokenId.ERROR;
            case org.python.antlr.PythonLexer.STRING:
                return PythonTokenId.STRING_LITERAL;
            case org.python.antlr.PythonLexer.INT:
                return PythonTokenId.INT_LITERAL;
            case org.python.antlr.PythonLexer.COMMENT:
                return PythonTokenId.COMMENT;
            case org.python.antlr.PythonLexer.NAME:
                return PythonTokenId.IDENTIFIER;
            case org.python.antlr.PythonLexer.WS:
                return PythonTokenId.WHITESPACE;
            case org.python.antlr.PythonLexer.NEWLINE:
                return PythonTokenId.NEWLINE;
            default:
                return PythonTokenId.ANY_OPERATOR;
        }
    }

}