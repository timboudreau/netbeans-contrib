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
package org.netbeans.modules.latex.editor;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author  Jan Lahoda
 */
public class TexLexer implements Lexer<TexTokenId> {

    private LexerInput  input;
    private TokenFactory<TexTokenId> factory;
    private int         state;
    
    private static final Logger LOG = Logger.getLogger(TexLexer.class.getName());
    
    public TexLexer(LexerInput input, TokenFactory<TexTokenId> factory, Object obj) {
        this.input = input;
        this.factory = factory;
        if (obj == null) {
            state = 0;
        } else {
            int value = ((Integer) obj).intValue();
            
            state = value;
        }
    }
    
    public Object state() {
        return Integer.valueOf(state);
    }
    
    private static final String SPECIAL_COMMAND_CHARS = "{}\\ []'`^\"~=.()|%"; // NOI18N
    
    protected boolean isEOF(int read) {
        return (read == LexerInput.EOF) || (read == 65535 /*this is some nasty bug. no time for investigate it. it was in my code. should no be needed - remove when everything all is done.*/);
    }
    
    public Token<TexTokenId> nextToken() {
        try {
        while (true) {
            int read = input.read();

            LOG.log(Level.FINE, "start");
            LOG.log(Level.FINE, "state={0}", state);
//                System.err.println("testText=\"" + input.getReadText(0, input.getReadLength()) + "\"");
//                System.err.println("readahead=" + input.getReadLength());
            LOG.log(Level.FINE, "input.getClass()={0}", input.getClass());
            
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
                            
                            return createToken(TexTokenId.COMP_BRACKET_LEFT);
                        }
                        
                        if (read == '}') {
                            state = 0;
                            
                            return createToken(TexTokenId.COMP_BRACKET_RIGHT);
                        }
                        
                        if (read == '[') {
                            state = 0;
                            
                            return createToken(TexTokenId.RECT_BRACKET_LEFT);
                        }
                        
                        if (read == ']') {
                            state = 0;
                            
                            return createToken(TexTokenId.RECT_BRACKET_RIGHT);
                        }
                        
                        if (read == ' ') {
                            state = 0;
                            
                            return createToken(TexTokenId.WHITESPACE);
                        }

                        if (read == '\t') {
                            state = 0;
                            
                            return createToken(TexTokenId.WHITESPACE);
                        }
                    } else {
                        state = 0;
                        
                        return null;
                    }
                    
                    state = 0;
                    return createToken(TexTokenId.UNKNOWN_CHARACTER);

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
                    
//                    if (input.getReadLookahead() == 1 && input.isEOFLookahead() && isEOF(read))
//                        return null;
                    
                    state = 0;
                    
                    return createToken(TexTokenId.WORD);
                
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
                    
		    LOG.log(Level.FINE, "read length={0}", input.readLength());
                    
                    return createToken(TexTokenId.WORD); //Number, in fact.

                case 3:
//                    read = input.read();
                    
//                    System.err.println("read=" + read);
                    if (!isEOF(read)) {
                        if (Character.isLetter((char) read) || read == '*') {
                            break;
                        }
                        
                        if (!(input.readLength() == 2 && SPECIAL_COMMAND_CHARS.indexOf(read) != (-1))) {
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
                    
                    return createToken(TexTokenId.COMMAND);
                    
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
                    return createToken(TexTokenId.COMMENT);
                    
                case 7:
                    if (read == '\n') {
		        state = 8;
			break;
		    }
                    else {
                        state = 0;
                        if (!isEOF(read))
                            input.backup(1);
                        return createToken(TexTokenId.WHITESPACE);
                    }
		case 8:
		    if (read != '\n') {
		        if (!isEOF(read))
			    input.backup(1);
			
			state = 0;
			
                        return createToken(TexTokenId.PARAGRAPH_END);
		    }
		    break;
                    
                    
                case 9:
                    if (read != '$') {
                        if (!isEOF(read))
                            input.backup(1);
                    }
                    
                    Token<TexTokenId> result = createToken(TexTokenId.MATH);
                    
                    state = 0;
                    return result;

                default: throw new IllegalStateException("The execution should never get here.");
            }
        }
        } catch (RuntimeException e) {
//            ErrorManager.getDefault().annotate(e, "An exception occured during nextToken in TexLexer. Text read so far: " + input.getReadText(0, input.getReadLength()));
//	    
//	    System.err.println("!!!!!:");
//	    e.printStackTrace(System.err);
//	    System.err.println("state = " + state);
//	    System.err.println("text = " + input.getReadText(0, input.getReadLength()));
            
            throw e;
        }
    }
    
    private Token<TexTokenId> createToken(TexTokenId id) {
        return factory.createToken(id); //!!!
    }
    
    public void release() {}
    
}
