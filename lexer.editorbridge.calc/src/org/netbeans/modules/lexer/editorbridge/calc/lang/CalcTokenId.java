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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.lexer.editorbridge.calc.lang;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.lexer.editorbridge.calc.CalcDataLoader;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Calc token id definition.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public enum CalcTokenId implements TokenId {

    WHITESPACE(null, "whitespace"),
    LINE_COMMENT(null, "comment"),
    BLOCK_COMMENT(null, "comment"),
    E("e", "keyword"),
    PI("pi", "keyword"),
    IDENTIFIER(null, null),
    INT_LITERAL(null, "number"),
    FLOAT_LITERAL(null, "number"),
    PLUS("+", "operator"),
    MINUS("-", "operator"),
    STAR("*", "operator"),
    SLASH("/", "operator"),
    LPAREN("(", "separator"),
    RPAREN(")", "separator"),
    ERROR(null, "error"),
    BLOCK_COMMENT_INCOMPLETE(null, "comment");


    private final String fixedText;

    private final String primaryCategory;

    private CalcTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }
    
    public String fixedText() {
        return fixedText;
    }

    public String primaryCategory() {
        return primaryCategory;
    }

    private static final Language<CalcTokenId> language = new LanguageHierarchy<CalcTokenId>() {
        @Override
        protected Collection<CalcTokenId> createTokenIds() {
            return EnumSet.allOf(CalcTokenId.class);
        }
        
        @Override
        protected Map<String,Collection<CalcTokenId>> createTokenCategories() {
            Map<String,Collection<CalcTokenId>> cats = new HashMap<String,Collection<CalcTokenId>>();

            // Incomplete literals 
            cats.put("incomplete", EnumSet.of(CalcTokenId.BLOCK_COMMENT_INCOMPLETE));
            // Additional literals being a lexical error
            cats.put("error", EnumSet.of(CalcTokenId.BLOCK_COMMENT_INCOMPLETE));
            
            return cats;
        }

        @Override
        protected Lexer<CalcTokenId> createLexer(LexerRestartInfo<CalcTokenId> info) {
            return new CalcLexer(info);
        }

        @Override
        protected LanguageEmbedding embedding(
        Token<CalcTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            return null; // No embedding
        }

        @Override
        protected String mimeType() {
            return CalcDataLoader.CALC_MIME_TYPE;
        }
        
    }.language();

    public static final Language<CalcTokenId> language() {
        return language;
    }

}
