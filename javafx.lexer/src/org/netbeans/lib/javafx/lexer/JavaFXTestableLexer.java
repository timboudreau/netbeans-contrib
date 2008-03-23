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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.javafx.lexer.JavaFXTokenId;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * This class can be used as super class for a Lexer to make its implementation
 * testable via JUnit tests.
 * This class incapsulate functionality of all classes that need to be 
 * instantiated to instantiate a Lexer class.
 * <ol>
 *   <li>The class LexerInput is final and has only a constructor with default
 * (i.e. package local) visibility. Therefore, an instance of the class can't be 
 * created. </li>
 * </ol>
 * @author Victor G. Vasilyev
 */
public abstract class JavaFXTestableLexer implements TestableLexer {

    private static final boolean DEBUG = false;
    private static final Logger LOG = DEBUG ?
            Logger.getLogger(JavaFXTestableLexer.class.getName()) : null;

    private final LexerInput input;
    
    private final TokenFactory<JavaFXTokenId> tokenFactory;
    
    public JavaFXTestableLexer(LexerInput input, TokenFactory<JavaFXTokenId> tokenFactory) {
        this.input = input;
        this.tokenFactory = tokenFactory; 
    }
    
    /**
     * For test purpose only.
     * @param state
     */
    JavaFXTestableLexer() {
        this.input = null;
        this.tokenFactory = null;
    }
    
    public int inputRead() {
        return input.read();
    }
    
    public void inputBackup(int count) {
        input.backup(count);
    }
    
    public int inputReadLength() {
        return input.readLength();
    }
    
    public boolean inputConsumeNewline() {
        return input.consumeNewline();
    }
    
    public Token<JavaFXTokenId> tokenFactoryCreateToken(JavaFXTokenId id) {
        if (DEBUG) {
            LOG.log(Level.INFO, "new JavaFXTokenId id=[{0}]", id);
        }
        return tokenFactory.createToken(id);
    }

    public Token<JavaFXTokenId> tokenFactoryCreateToken(JavaFXTokenId id,
            int tokenLenght, PartType partType) {
        if (DEBUG) {
            LOG.log(Level.INFO, "new JavaFXTokenId id=[{0}]", id);
        }
        return tokenFactory.createToken(id, tokenLenght, partType);
    }
    
    public Token<JavaFXTokenId> tokenFactoryGetFlyweightToken(JavaFXTokenId id, String text) {
        if (DEBUG) {
            LOG.log(Level.INFO, "new JavaFXTokenId id=[{0}]", id);
        }
        return tokenFactory.getFlyweightToken(id, text);
    }


    public Token<JavaFXTokenId> token(JavaFXTokenId id) {
        String fixedText = id.fixedText();
        if (DEBUG) {
            LOG.log(Level.INFO, "new JavaFXTokenId id=[{0}] fixedText =[{1}]", 
                    new Object[] { id, fixedText });
        }
        return (fixedText != null)
                ? tokenFactory.getFlyweightToken(id, fixedText)
                : tokenFactory.createToken(id);
    }

}
