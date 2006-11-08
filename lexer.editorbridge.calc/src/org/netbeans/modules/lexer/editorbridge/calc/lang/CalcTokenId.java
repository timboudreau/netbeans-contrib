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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
        protected Collection<CalcTokenId> createTokenIds() {
            return EnumSet.allOf(CalcTokenId.class);
        }
        
        protected Map<String,Collection<CalcTokenId>> createTokenCategories() {
            Map<String,Collection<CalcTokenId>> cats = new HashMap<String,Collection<CalcTokenId>>();

            // Incomplete literals 
            cats.put("incomplete", EnumSet.of(CalcTokenId.BLOCK_COMMENT_INCOMPLETE));
            // Additional literals being a lexical error
            cats.put("error", EnumSet.of(CalcTokenId.BLOCK_COMMENT_INCOMPLETE));
            
            return cats;
        }

        public Lexer<CalcTokenId> createLexer(LexerRestartInfo<CalcTokenId> info) {
            return new CalcLexer(info);
        }

        public LanguageEmbedding embedding(
        Token<CalcTokenId> token, boolean tokenComplete,
        LanguagePath languagePath, InputAttributes inputAttributes) {
            return null; // No embedding
        }

        public String mimeType() {
            return CalcDataLoader.CALC_MIME_TYPE;
        }
        
    }.language();

    public static final Language<CalcTokenId> language() {
        return language;
    }

}
