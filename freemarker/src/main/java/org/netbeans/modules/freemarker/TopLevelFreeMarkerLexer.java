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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.freemarker;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Jan Lahoda
 */
public class TopLevelFreeMarkerLexer implements Lexer<TopLevelFreeMarkerTokenId> {

    private LexerInput i;
    private TokenFactory<TopLevelFreeMarkerTokenId> f;
    private Boolean wasOther;

    public TopLevelFreeMarkerLexer(LexerInput i, TokenFactory<TopLevelFreeMarkerTokenId> f, Boolean wasOther) {
        this.i = i;
        this.f = f;
        this.wasOther = wasOther;
    }
    
    private boolean isFreeMarkerDirectiveStart() {
        int read = i.read();
        
        if (read == LexerInput.EOF) {
            return false;
        }
        
        if (read != '<') {
            i.backup(1);
            return false;
        }

        read = i.read();
        
        if (read == LexerInput.EOF) {
            i.backup(1);
            return false;
        }
        
        if (read == '#') {
            i.backup(2);
            return true;
        }
        
        if (read != '/') {
            i.backup(2);
            return false;
        }
        
        read = i.read();

        if (read == LexerInput.EOF) {
            i.backup(2);
            return false;
        }

        i.backup(3);
        
        return read == '#';
    }
    
    private boolean isFreeMarkerVariableStart() {
        int read = i.read();
        
        if (read == LexerInput.EOF) {
            return false;
        }
        
        if (read != '$') {
            i.backup(1);
            return false;
        }

        read = i.read();
        
        if (read == LexerInput.EOF) {
            i.backup(1);
            return false;
        }
        
        i.backup(2);
        
        return read == '{';
    }
    
    public Token<TopLevelFreeMarkerTokenId> nextToken() {
        if (isFreeMarkerDirectiveStart()) {
            int read = i.read();
            
            while (read != '>' && read != LexerInput.EOF)
                read = i.read();
            
            return f.createToken(TopLevelFreeMarkerTokenId.FREEMARKER_DIRECTIVE);
        } else {
            if (isFreeMarkerVariableStart()) {
                int read = i.read();

                while (read != '}' && read != LexerInput.EOF) {
                    read = i.read();
                }

                return f.createToken(TopLevelFreeMarkerTokenId.FREEMARKER_VARIABLE);
            } else {
                while (!isFreeMarkerDirectiveStart() && !isFreeMarkerVariableStart()) {
                    if (i.read() == LexerInput.EOF) {
                        break;
                    }
                }

                if (i.readLength() == 0) {
                    return null;
                } else {
                    boolean wasEOF = i.read() == LexerInput.EOF;
                    
                    if (!wasEOF) i.backup(1);
                    
                    if (wasOther != Boolean.TRUE) {
                        if (wasEOF) {
                            return f.createToken(TopLevelFreeMarkerTokenId.OTHER_PART);
                        } else {
                            return f.createToken(TopLevelFreeMarkerTokenId.OTHER_PART, i.readLength(), PartType.START);
                        }
                    }
                    
                    wasOther = Boolean.TRUE;
                    
                    if (wasEOF) {
                        return f.createToken(TopLevelFreeMarkerTokenId.OTHER_PART, i.readLength(), PartType.END);
                    } else {
                        return f.createToken(TopLevelFreeMarkerTokenId.OTHER_PART, i.readLength(), PartType.MIDDLE);
                    }
                }
            }
        }
    }

    public Object state() {
        return wasOther;
    }

    public void release() {
        i = null;
        f = null;
        wasOther = null;
    }

}
