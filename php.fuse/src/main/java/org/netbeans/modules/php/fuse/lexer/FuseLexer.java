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

import java.util.ArrayList;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.php.fuse.editor.TmplParseData;
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
    private final InputAttributes inputAttributes;
    private final TmplParseData tmplParseData;

    /**
     * Create new FuseLexer.
     * @param info from which place it should start again.
     */
    public FuseLexer(LexerRestartInfo<FuseTokenId> info) {
        this.input = info.input();
        this.inputAttributes = info.inputAttributes();
        this.tokenFactory = info.tokenFactory();
        assert (info.state() == null); // never set to non-null value in state()
        if (inputAttributes != null) {
            tmplParseData = (TmplParseData) inputAttributes.getValue(LanguagePath.get(FuseTokenId.language()), TmplParseData.class);
        } else {
            tmplParseData = null;
        }
    }

    public Object state() {
        return null; // always in default state after token recognition
    }

    public Token<FuseTokenId> nextToken() {
        boolean endingTag = false;
        while (true) {
            int c = input.read();
            switch (c) {
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

                case '/':
                    endingTag = true;
                    continue;

                case EOF:
                    if (endingTag) {
                        return finishIdentifier(c);
                    } else {
                        return null;
                    }

                default:
                    return finishIdentifier(c); //token(FuseTokenId.ERROR);
            } // end of switch (c)
        } // end of while(true)
    }

    private Token<FuseTokenId> finishIdentifier() {
        return finishIdentifier(input.read());
    }

    private Token<FuseTokenId> finishIdentifier(int c) {
        String lexedText = "";
        while (true) {
            if (c == EOF || !(Character.isJavaIdentifierPart(c))) {
                if (tmplParseData != null) {
                    ArrayList<String> variables = tmplParseData.getVariables();
                    if (variables.contains(lexedText)) {
                        return tokenFactory.createToken(FuseTokenId.TEMPLATE_VAR);
                    }
                }
                return tokenFactory.createToken(FuseTokenId.IDENTIFIER);
            }
            lexedText += (char) c;
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
        if (c == EOF || !(Character.isJavaIdentifierPart(c))) {
            if (endingTag) {
                return tokenFactory.createToken(FuseTokenId.valueOf(keywordId.name() + "_END"));
            } else {
                return tokenFactory.createToken(FuseTokenId.valueOf(keywordId.name()));
            }

        }
        return finishIdentifier();
    }

    public void release() {
    }
}
