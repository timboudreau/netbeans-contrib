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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.languages.ejs.lexer.api;


import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.languages.ejs.lexer.EJSLexer;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Token Ids for EJS (.ejs)
 *
 * @author Marek Fukala
 */

public enum EJSTokenId implements TokenId {

    HTML("html"),
    JAVASCRIPT("javascript"),
    DELIMITER("ejs-delimiter");

    public static final String MIME_TYPE = "text/x-ejs"; // NOI18N
    
    private final String primaryCategory;

    EJSTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String primaryCategory() {
        return primaryCategory;
    }

    // Token ids declaration
    private static final Language<EJSTokenId> language = new LanguageHierarchy<EJSTokenId>() {
        protected Collection<EJSTokenId> createTokenIds() {
            return EnumSet.allOf(EJSTokenId.class);
        }
        
        protected Map<String,Collection<EJSTokenId>> createTokenCategories() {
            //Map<String,Collection<EJSTokenId>> cats = new HashMap<String,Collection<EJSTokenId>>();
            // Additional literals being a lexical error
            //cats.put("error", EnumSet.of());
            return null;
        }
        
        public Lexer<EJSTokenId> createLexer(LexerRestartInfo<EJSTokenId> info) {
            return new EJSLexer(info);
        }
        
        @Override
        protected LanguageEmbedding embedding(Token<EJSTokenId> token,
                                  LanguagePath languagePath, InputAttributes inputAttributes) {
            switch(token.id()) {
                 case HTML:
                    // XXX Should I pass in a fourth argument, joinsections=true?
                    return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0);
                case JAVASCRIPT:
                    // XXX Should I pass in a fourth argument, joinsections=true?
                    

                    return LanguageEmbedding.create(org.netbeans.api.lexer.Language.find("text/javascript") , 0, 0);
 //ludo                   return LanguageEmbedding.create(EJSTokenId.language(), 0, 0);
                default:
                    return null;
            }
        }
        
        public String mimeType() {
            return EJSTokenId.MIME_TYPE;
        }
    }.language();
    
    public static Language<EJSTokenId> language() {
        return language;
    }
}
