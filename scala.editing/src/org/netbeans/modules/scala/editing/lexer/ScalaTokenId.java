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
package org.netbeans.modules.scala.editing.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.scala.editing.ScalaMimeResolver;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * 
 * @author Caoyuan Deng
 */
public enum ScalaTokenId implements TokenId {

    IGNORED("ignored"),
    DEFAULT("identfier"),

    Keyword("keyword"),
    Identifier("identifier"),
    BlockComment("comment"),
    LineComment("comment"),
    Ws("whitespace"),
    Nl("whitespace"),
    IntegerLiteral("number"),
    FloatingPointLiteral("number"),
    CharacterLiteral("character"),
    StringLiteral("string"),
    Operator("operator"),
    Separator("separator"),
    Error("error"),
    EOL("whitespace"),
    
    XmlName("xml"),
    XmlAttValue("string"),
    XmlLt("xml"),
    XmlGt("xml"),
    XmlLtSlash("xml"),
    XmlSlashGt("xml"),
    XmlEq("separator"),
    XmlComment("comment"),
    
    
    NEW("keyword"),
    GLOBAL_VAR("static"),
    CONSTANT("constant"),
    REGEXP_LITERAL("regexp"),
    LPAREN("separator"),
    RPAREN("separator"),
    LBRACE("separator"),
    RBRACE("separator"),
    LBRACKET("separator"),
    RBRACKET("separator"),
    STRING_BEGIN("string"),
    STRING_END("string"),
    REGEXP_BEGIN("regexp"), // or separator,
    REGEXP_END("regexp"),
    // Cheating: out of laziness just map all keywords returning from JRuby
    // into a single KEYWORD token; eventually I will have separate tokens
    // for each here such that the various helper methods for formatting,
    // smart indent, brace matching etc. can refer to specific keywords
    ANY_KEYWORD("keyword"),
    ANY_OPERATOR("operator"),
    DOT("operator"),
    THIS("keyword"),
    FOR("keyword"),
    IF("keyword"),
    ELSE("keyword"),
    WHILE("keyword"),
    CASE("keyword"),
    BREAK("keyword"),
    SWITCH("keyword"),
    COLON("operator"),
    SEMI("operator"),
    // Non-unary operators which indicate a line continuation if used at the end of a line
    NONUNARY_OP("operator");

    private final String primaryCategory;

    ScalaTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String primaryCategory() {
        return primaryCategory;
    }
    private static final Language<ScalaTokenId> language =
            new LanguageHierarchy<ScalaTokenId>() {

                protected String mimeType() {
                    return ScalaMimeResolver.MIME_TYPE;
                }

                protected Collection<ScalaTokenId> createTokenIds() {
                    return EnumSet.allOf(ScalaTokenId.class);
                }

                @Override
                protected Map<String, Collection<ScalaTokenId>> createTokenCategories() {
                    Map<String, Collection<ScalaTokenId>> cats =
                            new HashMap<String, Collection<ScalaTokenId>>();
                    return cats;
                }

                protected Lexer<ScalaTokenId> createLexer(LexerRestartInfo<ScalaTokenId> info) {
                    return ScalaLexer.create(info);
                }

                @Override
                protected LanguageEmbedding<?> embedding(Token<ScalaTokenId> token,
                        LanguagePath languagePath, InputAttributes inputAttributes) {
                    return null; // No embedding

                }
            }.language();

    public static Language<ScalaTokenId> language() {
        return language;
    }
}
