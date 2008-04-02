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

    IGNORED(null, "ignored"),
    
    Keyword(null, "keyword"),
    
    Identifier(null, "identifier"),
    
    DocCommentStart(null, "comment"),
    DocCommentEnd(null, "comment"),    
    DocCommentData(null, "comment"),
    BlockCommentStart(null, "comment"),
    BlockCommentEnd(null, "comment"),    
    BlockCommentData(null, "comment"),    
    LineComment(null, "comment"),    
    
    Ws(null, "whitespace"),
    Nl(null, "whitespace"),
    
    IntegerLiteral(null, "number"),
    FloatingPointLiteral(null, "number"),
    CharacterLiteral(null, "character"),
    StringLiteral(null, "string"),
    Operator(null, "operator"),
    Separator(null, "separator"),
    Error(null, "error"),

    LParen("(", "separator"),
    RParen(")", "separator"),
    LBrace("{", "separator"),
    RBrace("}", "separator"),
    LBracket("[", "separator"),
    RBracket("]", "separator"),
    Comma(",", "separator"),
    Dot(".", "separator"),
    Semicolon(";", "separator"),
    Bar("|", "separator"),
    
    XmlEmptyTagName(null, "xml"),
    XmlSTagName(null, "xml"),
    XmlETagName(null, "xml"),
    XmlAttName(null, "xml"),
    XmlAttValue(null, "string"),
    XmlLt("<", "xml"),
    XmlGt(">", "xml"),
    XmlLtSlash("</", "xml"),
    XmlSlashGt("/>", "xml"),
    XmlCharData(null, "xmlchardata"),
    XmlEq("=", "separator"),
    XmlComment(null, "comment"),
    XmlWs(null, "whitespace"),
    XmlCDStart(null, "comment"),
    XmlCDEnd(null, "comment"),
    XmlCDData(null, "xmlcddata"),
    
    Abstract("abstract", "keyword"),
    Case("case", "keyword"),
    Catch("catch", "keyword"),
    Class("class", "keyword"),
    Def("def", "keyword"),
    Do("do", "keyword"),
    Else("else", "keyword"),
    Extends("extends", "keyword"),
    False("false", "keyword"),
    Final("final", "keyword"),
    Finally("finally", "keyword"),
    For("for", "keyword"),
    ForSome("forSome", "keyword"),
    If("if", "keyword"),
    Implicit("implicit", "keyword"),
    Import("import", "keyword"),
    Lazy("lazy", "keyword"),
    Match("match", "keyword"),
    New("new", "keyword"),
    Null("null", "keyword"),
    Object("object", "keyword"),
    Override("override", "keyword"),
    Package("package", "keyword"),
    Private("private", "keyword"),
    Protected("protected", "keyword"),
    Requires("requires", "keyword"),
    Return("return", "keyword"),
    Sealed("sealed", "keyword"),
    Super("super", "keyword"),
    This("this", "keyword"),
    Throw("throw", "keyword"),
    Trait("trait", "keyword"),
    Try("try", "keyword"),
    True("true", "keyword"),
    Type("type", "keyword"),
    Val("val", "keyword"),
    Var("var", "keyword"),
    While("while", "keyword"),
    With("with", "keyword"),
    Yield("yield", "keyword"),
    Wild("_", "keyword"),
    RArrow(null, "keyword"), // "=>" or "\u21D2", no fixed
    LArrow("<-", "keyword"),
    UBound("<:", "keyword"),
    VBound("<%", "keyword"),
    LBound(">:", "keyword"),
    Eq("=", "keyword"),
    Colon(":", "keyword"),
    Pound("#", "keyword"),
    At("@", "keyword"),

    GLOBAL_VAR(null, "static"),
    CONSTANT(null, "constant"),
    REGEXP_LITERAL(null, "regexp"),
    STRING_BEGIN(null, "string"),
    STRING_END(null, "string"),
    REGEXP_BEGIN(null, "regexp"), // or separator,
    REGEXP_END(null, "regexp"),
    // Cheating: out of laziness just map all keywords returning from JRuby
    // into a single KEYWORD token; eventually I will have separate tokens
    // for each here such that the various helper methods for formatting,
    // smart indent, brace matching etc. can refer to specific keywords
    ANY_KEYWORD(null, "keyword"),
    ANY_OPERATOR(null, "operator"),

    SEMI(null, "operator"),
    // Non-unary operators which indicate a line continuation if used at the end of a line
    NONUNARY_OP(null, "operator");

    private final String fixedText;
    private final String primaryCategory;

    ScalaTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }

    public String fixedText() {
        return fixedText;
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
