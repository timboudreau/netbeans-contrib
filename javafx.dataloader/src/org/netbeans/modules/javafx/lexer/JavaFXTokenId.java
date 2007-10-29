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
package org.netbeans.modules.javafx.lexer;

import org.netbeans.modules.javafx.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

import java.util.HashMap;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;

import org.netbeans.spi.lexer.LanguageEmbedding;

/**
 * Token ids of java language defined as enum.
 *
 * @author Miloslav Metelka
 */
public enum JavaFXTokenId implements TokenId {

    /*
    FORMAT("keyword")                      // <format : "format"> |
    
    
    
    ALPHA("keyword")                       // <ALPHA : "unitinterval"> |
    AMP("keyword")                         // <AMP : "&"> |    
    COMPLETION("keyword")                  // <COMPLETION: "@?"> |     
    READONLY("keyword")                    // <readonly : "readonly" > |            */

    ERROR(null, "error"),
    IDENTIFIER(null, "identifier"),

/*  Java Keywords
    ABSTRACT("abstract", "keyword"),
    BOOLEAN("boolean", "keyword"),
    BYTE("byte", "keyword"),
    CASE("case", "keyword"),
    CHAR("char", "keyword"),
    CONST("const", "keyword"),
    DEFAULT("default", "keyword"),
    DOUBLE("double", "keyword"),
    ENUM("enum", "keyword"),
    FINAL("final", "keyword"),
    FLOAT("float", "keyword"),
    GOTO("goto", "keyword"),
    IMPLEMENTS("implements", "keyword"),
    INT("int", "keyword"),
    INTERFACE("interface", "keyword"),
    LONG("long", "keyword"),
    NATIVE("native", "keyword"),
    SHORT("short", "keyword"),
    STATIC("static", "keyword"),
    STRICTFP("strictfp", "keyword"),
    SWITCH("switch", "keyword"),
    SYNCHRONIZED("synchronized", "keyword"),
    THROW("throw", "keyword"),
    THROWS("throws", "keyword"),
    TRANSIENT("transient", "keyword"),
    VOID("void", "keyword"),
    VOLATILE("volatile", "keyword"),
*/
/*  Java Operators
    BANGEQ("!=","operator"),
*/
    
//  Mixed Java and FX Keywords
    ASSERT("assert", "keyword"),    
    BREAK("break", "keyword"),
    CATCH("catch", "keyword"),    
    CLASS("class", "keyword"),    
    CONTINUE("continue", "keyword"),
    DO("do", "keyword"),
    DISTINCT("distinct", "keyword"),
    ELSE("else", "keyword"),
    EXTENDS("extends", "keyword"),
    FINALLY("finally", "keyword"),
    FOR("for", "keyword"),
    IF("if", "keyword"),
    IMPORT("import", "keyword"),
    INSTANCEOF("instanceof", "keyword"),
    NEW("new", "keyword"),
    PACKAGE("package", "keyword"),
    PRIVATE("private", "keyword"),
    PROTECTED("protected", "keyword"),
    PUBLIC("public", "keyword"),    
    RETURN("return", "keyword"),
    SUPER("super", "keyword"),
    THIS("this", "keyword"),
    TRY("try", "keyword"),    
    WHILE("while", "keyword"),
    
//  Pure FX Keywords

    AFTER("after", "keyword"), 
    AS("as", "keyword"), 
    ATTRIBUTE("attribute", "keyword"), 
    BEFORE("before", "keyword"), 
    BIND("bind", "keyword"), 
    BY("by", "keyword"),
    DELETE("delete", "keyword"), 
    DURATION("dur", "keyword"),
    EASEBOTH("easeboth", "keyword"),
    EASEIN("easein", "keyword"),
    EASEOUT("easeout", "keyword"),
    FIRST("first", "keyword"), 
    FOREACH("foreach", "keyword"), 
    FPS("fps", "keyword"),
    FROM("from", "keyword"), 
    FUNCTION("function", "keyword"), 
    INDEXOF("indexof", "keyword"), 
    INSERT("insert", "keyword"), 
    IN("in", "keyword"), 
    INTO("into", "keyword"), 
    INVERSE("inverse", "keyword"), 
    LAST("last", "keyword"), 
    LATER("later", "keyword"), 
    LAZY("lazy", "keyword"), 
    LINEAR("linear", "keyword"),
    NODEBUG("nodebug", "keyword"), 
    ON("on", "keyword"), 
    OPERATION("operation", "keyword"), 
    ORDER("order", "keyword"),
    READONLY("readonly", "keyword"),
    REVERSE("reverse", "keyword"),     
    SELECT("select", "keyword"), 
    SIZEOF("sizeof", "keyword"), 
    THEN("then", "keyword"), 
    TRIGGER("trigger", "keyword"), 
    TYPEOF("typeof", "keyword"), 
    VAR("var", "keyword"), 
        
    INT_LITERAL(null, "number"),
    LONG_LITERAL(null, "number"),
    FLOAT_LITERAL(null, "number"),
    DOUBLE_LITERAL(null, "number"),
    CHAR_LITERAL(null, "character"),
    STRING_LITERAL(null, "string"),
    
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
    LTLT("<<", "operator"),
    GTGT(">>", "operator"),
    GTGTGT(">>>", "operator"),
    PLUSEQ("+=", "operator"),
    MINUSEQ("-=", "operator"),
    STAREQ("*=", "operator"),
    SLASHEQ("/=", "operator"),
    AMPEQ("&=", "operator"),
    BAREQ("|=", "operator"),
    CARETEQ("^=", "operator"),
    PERCENTEQ("%=", "operator"),
    LTLTEQ("<<=", "operator"),
    GTGTEQ(">>=", "operator"),
    GTGTGTEQ(">>>=", "operator"),

//  Pure FX operators    
    AND("and", "operator"),
    OR("or", "operator"),
    NOT("not", "operator"),
    XOR("xor", "operator"),
    BANGEQFX("<>", "operator"),
    
    ELLIPSIS("...", null),
    AT("@", null),
    
    WHITESPACE(null, "whitespace"),
    LINE_COMMENT(null, "comment"),
    BLOCK_COMMENT(null, "comment"),
    JAVADOC_COMMENT(null, "comment"),
    
    // Errors
    INVALID_COMMENT_END("*/", "error"),
    FLOAT_LITERAL_INVALID(null, "number");


    private final String fixedText;

    private final String primaryCategory;

    JavaFXTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }

    public String fixedText() {
        return fixedText;
    }

    public String primaryCategory() {
        return primaryCategory;
    }

    private static final Language<JavaFXTokenId> language = new LanguageHierarchy<JavaFXTokenId>() {

        @Override
        protected String mimeType() {
            return "text/x-fx";
        }

        @Override
        protected Collection<JavaFXTokenId> createTokenIds() {
            return EnumSet.allOf(JavaFXTokenId.class);
        }
        
        @Override
        protected Map<String,Collection<JavaFXTokenId>> createTokenCategories() {
            Map<String,Collection<JavaFXTokenId>> cats = new HashMap<String,Collection<JavaFXTokenId>>();
            // Additional literals being a lexical error
            cats.put("error", EnumSet.of(
                JavaFXTokenId.FLOAT_LITERAL_INVALID
            ));
            // Literals category
            EnumSet<JavaFXTokenId> l = EnumSet.of(
                JavaFXTokenId.INT_LITERAL,
                JavaFXTokenId.LONG_LITERAL,
                JavaFXTokenId.FLOAT_LITERAL,
                JavaFXTokenId.DOUBLE_LITERAL,
                JavaFXTokenId.CHAR_LITERAL
            );
            l.add(JavaFXTokenId.STRING_LITERAL);
            cats.put("literal", l);

            return cats;
        }

        @Override
        protected Lexer<JavaFXTokenId> createLexer(LexerRestartInfo<JavaFXTokenId> info) {
            return new JavaFXLexer(info);
        }

        @Override
        protected LanguageEmbedding<? extends TokenId> embedding(
        Token<JavaFXTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            // Test language embedding in the block comment
            switch (token.id()) {
                case JAVADOC_COMMENT:
                    return LanguageEmbedding.create(JavadocTokenId.language(), 3, 2);
                case STRING_LITERAL:
                    return LanguageEmbedding.create(JavaStringTokenId.language(), 1, 1);
            }
            return null; // No embedding
        }

//        protected CharPreprocessor createCharPreprocessor() {
//            return CharPreprocessor.createUnicodeEscapesPreprocessor();
//        }

    }.language();

    public static Language<JavaFXTokenId> language() {
        return language;
    }

}
