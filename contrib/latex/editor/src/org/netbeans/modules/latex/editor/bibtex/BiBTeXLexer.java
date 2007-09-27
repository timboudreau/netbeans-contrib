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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.bibtex;

import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.model.lexer.BiBTeXTokenId;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Jan Lahoda
 */
/*package private*/ class BiBTeXLexer implements Lexer<BiBTeXTokenId> {

    private static final boolean debug = Boolean.getBoolean("org.netbeans.modules.latex.bibtex.lexer.debug");
    private int state;
    private LexerInput input;
    private TokenFactory<BiBTeXTokenId> factory;

    /** Creates a new instance of BiBTeXLexer */
    public BiBTeXLexer(LexerInput lexerInput, TokenFactory<BiBTeXTokenId> factory, Object obj) {
        this.input = lexerInput;
        this.factory = factory;
        assert obj == null;
    }

    protected boolean isEOF(int read) {
        return read == LexerInput.EOF;
    }

    public Token<BiBTeXTokenId> nextToken() {
        MAIN_LOOP: while (true) {
            int read = input.read();
            
            if (debug) {
                System.err.println("start");
                System.err.println("state=" + state);
                System.err.println("testText=\"" + input.readText(0, input.readLength()) + "\"");
                System.err.println("readahead=" + input.readLength());
                System.err.println("input.getClass()=" + input.getClass());
            }
            
            switch (state) {
                case 0:
                    if (!isEOF(read)) {
                        switch (read) {
                            case '{':
                            case '(':
                                return factory.createToken(BiBTeXTokenId.OP_BRAC);
                            
                            case '}':
                            case ')': 
                                return factory.createToken(BiBTeXTokenId.CL_BRAC);
                            
                            case '=':
                                return factory.createToken(BiBTeXTokenId.EQUALS);
                            
                            case ';':
                            case ',':
                                return factory.createToken(BiBTeXTokenId.COMMA);
                                
                            case ' ':
                            case '\t':
                            case '\n':
                                return factory.createToken(BiBTeXTokenId.WHITESPACE);
                            case '@':
                                state = 1;
                                continue MAIN_LOOP;
                            case '%':
                                state = 2;
                                continue MAIN_LOOP;
                            case '"':
                                state = 3;
                                continue MAIN_LOOP;
                            case '-':
                                return factory.createToken(BiBTeXTokenId.DASH);
                            case '_':
                                return factory.createToken(BiBTeXTokenId.UNDERSCORE);
                        }
                        
                        if (Character.isLetter((char) read) || Character.isDigit((char) read)) {
                            state = 4;
                            continue MAIN_LOOP;
                        }
                    } else {
                        state = 0;
                        
                        return null;
                    }
                    
                    return factory.createToken(BiBTeXTokenId.UNKNOWN_CHARACTER);
                    
                case 1:
                    if (!isEOF(read)) {
                        if (Character.isLetter((char) read)) {
                            continue MAIN_LOOP;
                        }
                        
                        input.backup(1);
                    }

                    state = 0;
                    
                    return factory.createToken(BiBTeXTokenId.TYPE);
                    
                case 2:
                    if (!isEOF(read)) {
                        if (read != '\n') {
                            continue MAIN_LOOP;
                        }
                    }

                    state = 0;
                    
                    return factory.createToken(BiBTeXTokenId.COMMENT);
                    
                case 3:
                    if (!isEOF(read)) {
                        if (read != '\n' && read != '"') {
                            continue MAIN_LOOP;
                        }
                        
                        if (read == '"' && input.readText(input.readLength() - 2, input.readLength() - 1).charAt(0) == '\\') {
                            continue MAIN_LOOP;
                        }
                    }

                    state = 0;
                    
                    return factory.createToken(BiBTeXTokenId.STRING);
                    
                case 4:
                    if (!isEOF(read)) {
                        if (Character.isLetter((char) read) || Character.isDigit((char) read)) {
                            continue MAIN_LOOP;
                        }
                        
                        input.backup(1);
                    }

                    state = 0;
                    
                    return factory.createToken(BiBTeXTokenId.TEXT);
                    
                default:
                    throw new IllegalStateException("Should never get here.");
            }
        }
    }

    public Object state() {
        return null;
    }
    
    public void release() {}
    
}
