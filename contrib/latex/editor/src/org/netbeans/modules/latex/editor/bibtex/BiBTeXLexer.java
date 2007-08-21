/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
