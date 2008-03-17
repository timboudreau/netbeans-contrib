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

package org.netbeans.lib.javafx.lexer;

import org.netbeans.api.javafx.lexer.JavaFXTokenId;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.LexerInput;

/**
 *
 * @author Victor G. Vasilyev
 */
public class JavaFXLexerTestAdapter extends JavaFXLexer implements TestableLexer {
        
    private String source;

    /**
     * Current reading index in the operation.
     * At all times it must be &gt;=0.
     */
    private int readIndex;
    
    /**
     * Maximum index from which the char was fetched for current
     * (or previous) tokens recognition.
     * <br>
     * The index is updated lazily - only when EOF is reached
     * and when backup() is called.
     */
    private int lookaheadIndex;
    
    private TestableToken testableToken;
    
    
    public JavaFXLexerTestAdapter(Object state, int version) {
        super(state, version);       
    }
    
    public void setSource(String source) {
        System.out.println("Source: [" + source + "]");
        this.source = source;
        readIndex = 0;
    }
    
    public TestableToken getTestableToken() {
        return testableToken;
    }

    @Override
    public int inputRead() {
        int c = read(readIndex++);
        if (c == LexerInput.EOF) {
            lookaheadIndex = readIndex; // count EOF char into lookahead
            readIndex--; // readIndex must not include EOF
        }
        return c;
    }
    
    @Override
    public void inputBackup(int count) {
        backup(count);
    }
    
    @Override
    public int inputReadLength() {
        return readIndex();
    }

    @Override
    public boolean inputConsumeNewline() {
        if (inputRead() == '\n') {
            return true;
        } else {
            backup(1);
            return false;
        }
    }
    
    @Override
    public Token<JavaFXTokenId> tokenFactoryCreateToken(JavaFXTokenId id) {
        testableToken = new TestableToken(id);
        return null;
    }

    @Override
    public Token<JavaFXTokenId> tokenFactoryCreateToken(JavaFXTokenId id,
            int tokenLenght, PartType partType) {
        testableToken = new TestableToken(id, tokenLenght, partType);
        return null;
    }
    
    @Override
    public Token<JavaFXTokenId> tokenFactoryGetFlyweightToken(JavaFXTokenId id, String text) {
        testableToken = new TestableToken(id, text);
        return null;
    }


    @Override
    public Token<JavaFXTokenId> token(JavaFXTokenId id) {
        testableToken = new TestableToken(id);
        return null;
    }
    
    private int read(int index) {
        if(index >= source.length()) { // >=,but not >
            return LexerInput.EOF;
        }
        return source.charAt(index);
    }


    private void backup(int count) {
        if (lookaheadIndex < readIndex) {
            lookaheadIndex = readIndex;
        }
        readIndex -= count;
    }
    
    private int readIndex() {
        return readIndex;
    }
    
    
}
