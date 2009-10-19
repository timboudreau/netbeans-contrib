/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.php.fuse.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Token ids of Fuse template language
 *
 * @author Martin Fousek
 */
public enum FuseTokenId implements TokenId {

    ERROR(null, "error"),
    IDENTIFIER(null, "identifier"),

    DB_LOOP("db_loop", "keyword"),
    LOOP("loop", "keyword"),
    ELSE("else", "keyword"),
    ITERATOR("iterator", "keyword"),
    IF("if", "keyword"),
    WHILE("while", "keyword"),
    DB_LOOP_END("/db_loop", "keyword"),
    LOOP_END("/loop", "keyword"),
    ITERATOR_END("/iterator", "keyword"),
    IF_END("/if", "keyword"),
    WHILE_END("/while", "keyword"),

    INCLUDE("include", "include"),
    REQUIRE("require", "include"),
    INCLUDE_ONCE("include_once", "include"),
    REQUIRE_ONCE("require_once", "include"),

    TRUE("true", "literal"),
    FALSE("false", "literal"),
    NULL("null", "literal"),
    
    LPAREN("(", "separator"),
    RPAREN(")", "separator"),
    LBRACE("{", "separator"),
    RBRACE("}", "separator"),
    LBRACKET("[", "separator"),
    RBRACKET("]", "separator"),
    SEMICOLON(";", "separator"),
    COMMA(",", "separator"),
    DOT(".", "separator"),

    EQ("=", "operator"),
    GT(">", "operator"),
    LT("<", "operator"),
    BANG("!", "operator"),
    TILDE("~", "operator"),
    QUESTION("?", "operator"),
    COLON(":", "operator"),
    EQEQ("==", "operator"),
    LTEQ("<=", "operator"),
    GTEQ(">=", "operator"),
    BANGEQ("!=","operator"),
    AMPAMP("&&", "operator"),
    BARBAR("||", "operator"),
    PLUSPLUS("++", "operator"),
    MINUSMINUS("--","operator"),
    PLUS("+", "operator"),
    MINUS("-", "operator"),
    STAR("*", "operator"),
    SLASH("/", "operator"),
    AMP("&", "operator"),
    BAR("|", "operator"),
    CARET("^", "operator"),
    PERCENT("%", "operator"),
    LTLT("<-", "operator"),
    GTGT("->", "operator"),
    PLUSEQ("+=", "operator"),
    MINUSEQ("-=", "operator"),
    STAREQ("*=", "operator"),
    SLASHEQ("/=", "operator"),
    AMPEQ("&=", "operator"),
    BAREQ("|=", "operator"),
    CARETEQ("^=", "operator"),
    PERCENTEQ("%=", "operator"),

    INT_LITERAL(null, "number"),
    DOUBLE_LITERAL(null, "number"),
    STRING_LITERAL(null, "string"),
    INCLUDE_LITERAL(null, "include_string"),
    
    WHITESPACE(null, "whitespace");

    private final String fixedText;

    private final String primaryCategory;

    FuseTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }

    public String fixedText() {
        return fixedText;
    }

    public String primaryCategory() {
        return primaryCategory;
    }

    private static final Language<FuseTokenId> language = new LanguageHierarchy<FuseTokenId>() {

        @Override
        protected String mimeType() {
            return "text/fuse-template";
        }

        @Override
        protected Collection<FuseTokenId> createTokenIds() {
            return EnumSet.allOf(FuseTokenId.class);
        }
        
        @Override
        protected Map<String,Collection<FuseTokenId>> createTokenCategories() {
            Map<String,Collection<FuseTokenId>> cats = new HashMap<String,Collection<FuseTokenId>>();
            return cats;
        }

        @Override
        protected Lexer<FuseTokenId> createLexer(LexerRestartInfo<FuseTokenId> info) {
            return new FuseLexer(info);
        }

        @Override
        protected LanguageEmbedding<?> embedding(
        Token<FuseTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            // Test language embedding in the block comment
//            switch (token.id()) {
//                case JAVADOC_COMMENT:
//                    return LanguageEmbedding.create(JavadocTokenId.language(), 3,
//                            (token.partType() == PartType.COMPLETE) ? 2 : 0);
//                case STRING_LITERAL:
//                    return LanguageEmbedding.create(JavaStringTokenId.language(), 1,
//                            (token.partType() == PartType.COMPLETE) ? 1 : 0);
//            }
            return null; // No embedding
        }
    }.language();

    public static Language<FuseTokenId> language() {
        return language;
    }

}
