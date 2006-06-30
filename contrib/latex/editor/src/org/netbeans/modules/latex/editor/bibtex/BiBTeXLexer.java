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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.bibtex;

import org.netbeans.api.lexer.Lexer;
import org.netbeans.api.lexer.LexerInput;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.util.IntegerCache;

/**
 *
 * @author Jan Lahoda
 */
/*package private*/ class BiBTeXLexer implements Lexer {

    private static final boolean debug = Boolean.getBoolean("org.netbeans.modules.latex.bibtex.lexer.debug");
    private int state;
    private LexerInput input;

    /** Creates a new instance of BiBTeXLexer */
    public BiBTeXLexer() {
    }

    /**
     * Restart lexer (so that {@link #nextToken()} can be called later)
     * by providing an input and an internal lexer state.
     * 
     * @param input lexer input containing the characters to be scanned.
     *  <BR>It should be saved in an instance variable of the lexer
     *  so that it can be used later in {@link #nextToken()}.
     * 
     *  <P>This parameter can be null if the external environment
     *  wishes to do no more lexing on the previously set lexer input.
     *  <BR>The lexer should respond by clearing all its references to previously
     *  set lexer input so that it can be garbage collected if necessary.
     * 
     * @param state internal lexer's state to which the lexer should be set.
     * It must correspond to one of the values returned
     * from {@link #getState()} (of either this lexer instance
     * or another instance of this lexer class) in the past.
     * <BR>It will be <CODE>null</CODE> when restarting lexer at begining
     * of input - e.g. at begining of a swing document.
     */
    public void restart(LexerInput lexerInput, Object obj) {
        this.input = lexerInput;
        if (obj == null) {
            state = 0;            
        } else {
            state = ((Integer) obj).intValue();
        }
    }
    
    protected boolean isEOF(int read) {
        return read == LexerInput.EOF;
    }

    /**
     * This is a core method of the lexer responsible
     * for returning a token based on characters on input.
     * <BR>Characters can be read by using
     * {@link LexerInput#read()} method. Once the lexer
     * knows that it has read enough characters to recognize
     * a token it calls {@link LexerInput#createToken(TokenId)}
     * or {@link LexerInput#createToken(TokenId, int)}
     * to obtain an instance of a {@link Token} and returns it.
     * <P><B>Note:</B>&nbsp;Lexer must *not* return any other <CODE>Token</CODE> instances than
     * those obtained by calling {@link LexerInput#createToken(TokenId)}.
     * @return token recognized by the lexer
     *  or null if there are no more characters
     *  available on input. All the characters
     *  provided by the {@link LexerInput} must be tokenized
     *  prior to returning null from this method so the following
     *  condition must be true:<PRE>
     *      // readLookahead 1 stands for EOF
     *      (lexerInput.isEOFLookahead() && (lexerInput.getReadLookahead() == 1))
     *  </PRE>
     */
    public Token nextToken() {
        MAIN_LOOP: while (true) {
            int read = input.read();
            
            if (debug) {
                System.err.println("start");
                System.err.println("state=" + state);
                System.err.println("testText=\"" + input.getReadText(0, input.getReadLength()) + "\"");
                System.err.println("readahead=" + input.getReadLength());
                System.err.println("input.getClass()=" + input.getClass());
            }
            
            switch (state) {
                case 0:
                    if (!isEOF(read)) {
                        switch (read) {
                            case '{':
                            case '(':
                                return input.createToken(BiBTeXLanguage.OP_BRAC);
                            
                            case '}':
                            case ')': 
                                return input.createToken(BiBTeXLanguage.CL_BRAC);
                            
                            case '=':
                                return input.createToken(BiBTeXLanguage.EQUALS);
                            
                            case ';':
                            case ',':
                                return input.createToken(BiBTeXLanguage.COMMA);
                                
                            case ' ':
                            case '\t':
                            case '\n':
                                return input.createToken(BiBTeXLanguage.WHITESPACE);
                            case '@':
                                state = 1;
                                continue MAIN_LOOP;
                            case '%':
                                state = 2;
                                continue MAIN_LOOP;
                            case '"':
                                state = 3;
                                continue MAIN_LOOP;
                        }
                        
                        if (Character.isLetter((char) read) || Character.isDigit((char) read)) {
                            state = 4;
                            continue MAIN_LOOP;
                        }
                    } else {
                        state = 0;
                        
                        return null;
                    }
                    
                    return input.createToken(BiBTeXLanguage.UNKNOWN_CHARACTER);
                    
                case 1:
                    if (!isEOF(read)) {
                        if (Character.isLetter((char) read)) {
                            continue MAIN_LOOP;
                        }
                        
                        input.backup(1);
                    }

                    state = 0;
                    
                    return input.createToken(BiBTeXLanguage.TYPE);
                    
                case 2:
                    if (!isEOF(read)) {
                        if (read != '\n') {
                            continue MAIN_LOOP;
                        }
                    }

                    state = 0;
                    
                    return input.createToken(BiBTeXLanguage.COMMENT);
                    
                case 3:
                    if (!isEOF(read)) {
                        if (read != '\n' && read != '"') {
                            continue MAIN_LOOP;
                        }
                        
                        if (read == '"' && input.getReadText(input.getReadLength() - 2, input.getReadLength() - 1).charAt(0) == '\\') {
                            continue MAIN_LOOP;
                        }
                    }

                    state = 0;
                    
                    return input.createToken(BiBTeXLanguage.STRING);
                    
                case 4:
                    if (!isEOF(read)) {
                        if (Character.isLetter((char) read) || Character.isDigit((char) read)) {
                            continue MAIN_LOOP;
                        }
                        
                        input.backup(1);
                    }

                    state = 0;
                    
                    return input.createToken(BiBTeXLanguage.TEXT);
                    
                default:
                    throw new IllegalStateException("Should never get here.");
            }
        }
    }

    /**
     * Get current internal state of this lexer.
     * @return current internal state of this lexer.
     *   <CODE>null</CODE> is valid value
     *   and it means a default state of the lexer.
     */
    public Object getState() {
        return IntegerCache.get(state);//TODO: more efficiently
    }
    
}
