/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor;

import org.netbeans.api.lexer.Lexer;
import org.netbeans.api.lexer.LexerInput;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.swing.TokenElement;
import org.netbeans.modules.lexer.editorbridge.OffsetTokenElement;
import org.netbeans.modules.lexer.editorbridge.TokenRootElement;
import org.netbeans.spi.lexer.util.IntegerCache;

/**
 *
 * @author  Jan Lahoda
 */
public class TexLexer implements Lexer {
    
    private LexerInput  input;
    private int         state;
    
    private static final boolean debug = Boolean.getBoolean("org.netbeans.modules.latex.lexer.debug");
    private static final boolean fastMathStop = Boolean.getBoolean("org.netbeans.modules.latex.lexer.math") || true;
    
    /** Creates a new instance of TexLexer */
    public TexLexer() {
        input = null;
        state = 0;
    }
    
    private static final Integer[][] integers = new Integer[][] {
        new Integer[] {new Integer(0), new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Integer(6), new Integer(7), new Integer(8), new Integer(9), },
        new Integer[] {new Integer(20), new Integer(21), new Integer(22), new Integer(23), new Integer(24), new Integer(25), new Integer(26), new Integer(27), new Integer(28), new Integer(29), },
    };
    
    public Object getState() {
        return integers[isInMath ? 1 : 0][state];
//        return IntegerCache.get(state + (isInMath ? 20 : 0));
    }
    
    private static final String SPECIAL_COMMAND_CHARS = "{}\\";
    
    protected boolean isEOF(int read) {
        return (read == LexerInput.EOF) || (read == 65535 /*this is some nasty bug. no time for investigate it. it was in my code. should no be needed - remove when everything all is done.*/);
    }
    
    private static final Integer minus = new Integer(-1);
    
    public Token nextToken() {
        try {
//        Vector args     = null;
        Token tokenForTest = null;
        
        while (true) {
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
//                    read = input.read();
                    
//                    System.err.println("state=0");
                    
//                    System.err.println("read=" + read);
                    
                    if (!isEOF(read)) {
                        if (Character.isLetter((char) read)) {
                            state = 1;
                            break;
                        }
                        
                        if (Character.isDigit((char) read)) {
                            state = 2;
                            break;
                        }
                        
                        if (read == '\\') {
                            state = 3;
                            break;
                        }
                        
                        if (read == '%') {
                            state = 4;
                            break;
                        }
                        
                        if (read == '$') {
                            state = 9;
                            break;
                        }
                        
                        if (read == '\n') {
                            state = 7;
                            break;
                        }
                        
                        if (read == '{') {
                            state = 0;
                            
                            return createToken(TexLanguage.COMP_BRACKET_LEFT);
                        }
                        
                        if (read == '}') {
                            state = 0;
                            
                            return createToken(TexLanguage.COMP_BRACKET_RIGHT);
                        }
                        
                        if (read == '[') {
                            state = 0;
                            
                            return createToken(TexLanguage.RECT_BRACKET_LEFT);
                        }
                        
                        if (read == ']') {
                            state = 0;
                            
                            return createToken(TexLanguage.RECT_BRACKET_RIGHT);
                        }
                        
                        if (read == ' ') {
                            state = 0;
                            
                            return createToken(TexLanguage.WHITESPACE);
                        }

                        if (read == '\t') {
                            state = 0;
                            
                            return createToken(TexLanguage.WHITESPACE);
                        }
                    } else {
                        state = 0;
                        
                        return null;
                    }
                    
                    state = 0;
                    return createToken(TexLanguage.UNKNOWN_CHARACTER);

                case 1:
//                    read = input.read();
                    
//                    System.err.println("read=" + read);
                    if (!isEOF(read)) {
                        if (Character.isLetter((char) read)) {
                            break;
                        }
                        
                        input.backup(1);
                    }

//                    Integer type = Dictionary.getDefault().findWord(sb.toString());
                    
                    if (input.getReadLookahead() == 1 && input.isEOFLookahead() && isEOF(read))
                        return null;
                    
                    state = 0;
                    
                    return createToken(TexLanguage.WORD);
                
                case 2:
//                    read = input.read();
                    
//                    System.err.println("read=" + read);
                    if (!isEOF(read)) {
                        if (Character.isDigit((char) read)) {
                            break;
                        }
                        
                        input.backup(1);
                    }
                    
                    state = 0;
                    
		    if (debug)
                        System.err.println(input.getReadLength());
                    return createToken(TexLanguage.WORD); //Number, in fact.

                case 3:
//                    read = input.read();
                    
//                    System.err.println("read=" + read);
                    if (!isEOF(read)) {
                        if (Character.isLetter((char) read) || read == '*') {
                            break;
                        }
                        
                        if (!(input.getReadLength() == 2 && SPECIAL_COMMAND_CHARS.indexOf(read) != (-1))) {
                            input.backup(1);
                        }
/*                        if (read == '{')
                            state = 5;
                        
                        if (read == '[')
                            state = 6;*/
                        
//                        state = 0;//!!!!
                    } else {
                        read  = '\0';
                    }
                    
                    state = 0; //!!!
                    
                    return createToken(TexLanguage.COMMAND);
                    
                case 4:
//                    read = input.read();
                    
//                    System.err.println("read=" + read);
                    if (!isEOF(read)) {
                        if (read != '\n') {
                            break;
                        }
                        
                        input.backup(1);
                    }
                    
                    state = 0;
                    return createToken(TexLanguage.COMMENT);
                    
                case 7:
                    if (read == '\n') {
		        state = 8;
			break;
		    }
                    else {
                        state = 0;
                        if (!isEOF(read))
                            input.backup(1);
                        return createToken(TexLanguage.WHITESPACE);
                    }
		case 8:
		    if (read != '\n') {
		        if (fastMathStop)
			   isInMath = false;

		        if (!isEOF(read))
			    input.backup(1);
			
			state = 0;
			
                        return createToken(TexLanguage.PARAGRAPH_END);
		    }
		    break;
                    
                    
                case 9:
                    if (read != '$') {
                        input.backup(1);
                    }
                    
                    Token result;
                    
                    if (isInMath) {
                        result = createToken(TexLanguage.MATH);
                        isInMath = false;
                    } else {
                        isInMath = true;
                        result = createToken(TexLanguage.MATH);
                    }
                    
                    state = 0;
                    return result;

                default: throw new IllegalStateException("The execution should never get here.");
            }
        }
        } catch (RuntimeException e) {
            ErrorManager.getDefault().annotate(e, "An exception occured during nextToken in TexLexer. Text read so far: " + input.getReadText(0, input.getReadLength()));
	    
	    System.err.println("!!!!!:");
	    e.printStackTrace(System.err);
	    System.err.println("state = " + state);
	    System.err.println("text = " + input.getReadText(0, input.getReadLength()));
            
            throw e;
        }
    }
    
    private boolean isInMath = false;
    
    private Token createToken(TokenId id) {
        Token token = input.createToken(id); //!!!
        
        if (isInMath)
            TokenAttributes.setTokenAttribute(token, TokenAttributesNames.IS_IN_MATH, Boolean.TRUE);
        
        return token;
    }
    
    public void restart(LexerInput lexerInput, Object obj) {
        this.input = lexerInput;
        if (obj == null) {
            state = 0;            
        } else {
            int value = ((Integer) obj).intValue();
            
            if (value >= 20) {
                isInMath = true;
                value -= 20;
            } else {
                isInMath = false;
            }
            
            state = value;
        }
    }
    
}
