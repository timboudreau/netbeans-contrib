/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.api.javafx.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.PartType;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.lib.javafx.lexer.JavaFXLexer;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Token ids of the JavaFX language defined as enum.
 * 
 * This implementation is based on the JavaFX ANTLR gramar 
 * <a href="https://openjfx-compiler.dev.java.net/source/browse/openjfx-compiler/trunk/src/share/classes/com/sun/tools/javafx/antlr/v3.g?rev=1927&view=markup">
 * v3.g rev 1927</a>.
 *
 * @author Miloslav Metelka
 * @author Victor G. Vasilyev
 * 
 * @todo ordinal() of all tokens should be matched with the JavaFX ANTLR gramar.
 * Dummy tokens can be used for it.
 */
public enum JavaFXTokenId implements TokenId {
    
//*** TOKENS
//* these tokens can start a statement/definition -- can insert semi-colons before
    ABSTRACT("abstract", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    ASSERT("assert", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),
    ATTRIBUTE("attribute", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    BIND("bind", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    BOUND("bound", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    BREAK("break", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),
    CLASS("class", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    CONTINUE("continue", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),
    DELETE("delete", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    FALSE("false", Category.LITERAL, CanStartStatement.YES, JavaLike.YES),
    FOR("for", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),
    FUNCTION("function", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    IF("if", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),
    IMPORT("import", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    INIT("init", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    INSERT("insert", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    LET("let", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    NEW("new", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    NOT("not", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    NULL("null", Category.LITERAL, CanStartStatement.YES, JavaLike.YES),
    OVERRIDE("override", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    PACKAGE("package", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    POSTINIT("postinit", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    PRIVATE("private", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    PROTECTED("protected", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    PUBLIC("public", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    READONLY("readonly", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    RETURN("return", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),
    SUPER("super", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    SIZEOF("sizeof", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    STATIC("static", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    THIS("this", Category.KEYWORD, CanStartStatement.YES, JavaLike.YES),
    THROW("throw", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),
    TRY("try", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),
    TRUE("true", Category.LITERAL, CanStartStatement.YES, JavaLike.YES),
    VAR("var", Category.KEYWORD, CanStartStatement.YES, JavaLike.NO),
    WHILE("while", Category.DIRECTIVE, CanStartStatement.YES, JavaLike.YES),

    POUND("#", Category.OPERATOR, CanStartStatement.YES, JavaLike.NO),
    LPAREN("(", Category.SEPARATOR, CanStartStatement.YES, JavaLike.YES),
    LBRACKET("[", Category.SEPARATOR, CanStartStatement.YES, JavaLike.YES),
    PLUSPLUS("++", Category.OPERATOR, CanStartStatement.YES, JavaLike.YES),
    /** Java-like, but the JavaFX grammar defines it as <code>SUBSUB</code> */
    MINUSMINUS("--",Category.OPERATOR, CanStartStatement.YES, JavaLike.YES),
    /** Java-like, but the JavaFXTokenId defines it as <code>BAR</code> */
    PIPE("|", Category.OPERATOR, CanStartStatement.YES, JavaLike.YES), 
//* cannot start a statement
    AFTER("after", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    AND("and", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    AS("as", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    BEFORE("before", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    CATCH("catch", Category.DIRECTIVE, CanStartStatement.NO, JavaLike.YES),
    ELSE("else", Category.DIRECTIVE, CanStartStatement.NO, JavaLike.YES),
    EXCLUSIVE("exclusive",Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    EXTENDS("extends", Category.KEYWORD, CanStartStatement.NO, JavaLike.YES),
    FINALLY("finally", Category.DIRECTIVE, CanStartStatement.NO, JavaLike.YES),
    FIRST("first", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    FROM("from", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    IN("in", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    INDEXOF("indexof", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    INSTANCEOF("instanceof", Category.KEYWORD, CanStartStatement.NO, JavaLike.YES),
    INTO("into", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    INVERSE("inverse", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    LAST("last", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    LAZY("lazy", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    ON("on", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    OR("or", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    REPLACE("replace", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    REVERSE("reverse", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    STEP("step", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    THEN("then", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    TYPEOF("typeof", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    WITH("with", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    WHERE("where", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    
    DOTDOT("..", Category.SEPARATOR, CanStartStatement.NO, JavaLike.NO),
    RPAREN(")", Category.SEPARATOR, CanStartStatement.NO, JavaLike.YES),
    RBRACKET("]", Category.SEPARATOR, CanStartStatement.NO, JavaLike.YES),
    /** Java-like, but the JavaFX grammar defines it as <code>SEMI</code> */
    SEMICOLON(";", Category.SEPARATOR, CanStartStatement.NO, JavaLike.YES),
    COMMA(",", Category.SEPARATOR, CanStartStatement.NO, JavaLike.YES),
    DOT(".", Category.SEPARATOR, CanStartStatement.NO, JavaLike.YES),
    EQ("=", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    EQEQ("==", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    GT(">", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    LT("<", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    LTGT("<>", Category.OPERATOR, CanStartStatement.NO, JavaLike.NO),
    LTEQ("<=", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    GTEQ(">=", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    PLUS("+", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    /** Java-like, but the JavaFX grammar defines it as <code>SUB</code> */
    MINUS("-", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    STAR("*", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    SLASH("/", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    PERCENT("%", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    PLUSEQ("+=", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    /** Java-like, but the JavaFX grammar defines it as <code>SUBEQ</code> */
    MINUSEQ("-=", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    STAREQ("*=", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    SLASHEQ("/=", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    PERCENTEQ("%=", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    COLON(":", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    /** Java-like, but the JavaFX grammar defines it as <code>QUES</code> */
    QUESTION("?", Category.OPERATOR, CanStartStatement.NO, JavaLike.YES),
    TWEEN("tween", Category.KEYWORD, CanStartStatement.NO, JavaLike.NO),
    SUCHTHAT("=>", Category.OPERATOR, CanStartStatement.NO, JavaLike.NO),
//***
//*** LEXER RULES
    /** In contrast to Java, it is a lexer rule, but not a separator */
    LBRACE("{", Category.SEPARATOR, CanStartStatement.NO, JavaLike.YES),
    /** In contrast to Java, it is a lexer rule, but not a separator */
    RBRACE("}", Category.SEPARATOR, CanStartStatement.NO, JavaLike.YES),
//* Literals
    DECIMAL_LITERAL(null, Category.NUMBER, CanStartStatement.NO, JavaLike.NO),
    TIME_LITERAL(null, Category.TIME, CanStartStatement.NO, JavaLike.NO),
    OCTAL_LITERAL(null, Category.NUMBER, CanStartStatement.NO, JavaLike.NO),
    HEX_LITERAL(null, Category.NUMBER, CanStartStatement.NO, JavaLike.YES),
    /** Note, it isn't the same as <code>JavaFXTokenId.FLOAT_LITERAL</code>! */
    FLOATING_POINT_LITERAL(null, Category.NUMBER, CanStartStatement.NO, JavaLike.YES),
    STRING_LITERAL(null, Category.STRING, CanStartStatement.NO, JavaLike.YES),
    QUOTE_LBRACE_STRING_LITERAL(null, Category.STRING, CanStartStatement.NO, JavaLike.NO),
    RBRACE_QUOTE_STRING_LITERAL(null, Category.STRING, CanStartStatement.NO, JavaLike.NO),
    RBRACE_LBRACE_STRING_LITERAL(null, Category.STRING, CanStartStatement.NO, JavaLike.NO),
    FORMAT_STRING_LITERAL(null, Category.FORMAT, CanStartStatement.NO, JavaLike.NO),
    TRANSLATION_KEY(null, Category.I18N_ARTIFACT, CanStartStatement.NO, JavaLike.NO),   
//* Whitespces
    /** Java-like, but the JavaFX grammar defines it as <code>WS</code> */
    WHITESPACE(null, Category.WHITESPACE, CanStartStatement.NO, JavaLike.YES), 
//* Comments
    LINE_COMMENT(null, Category.COMMENT, CanStartStatement.NO, JavaLike.YES),
    /** Java-like, but the JavaFX grammar defines it as <code>COMMENT</code> */
    BLOCK_COMMENT(null, Category.COMMENT, CanStartStatement.NO, JavaLike.YES),
    /** Java-like, but the JavaFX grammar defines it as <code>DOC_COMMENT</code> */
    JAVADOC_COMMENT(null, Category.COMMENT, CanStartStatement.NO, JavaLike.YES),
//* Identifiers 
    IDENTIFIER(null, Category.IDENTIFIER, CanStartStatement.NO, JavaLike.YES),
//* Errors
    INVALID_COMMENT_END("*/", Category.ERROR, CanStartStatement.NO, JavaLike.YES),
    /**  Note, it isn't the same as <code>JavaFXTokenId.FLOAT_LITERAL_INVALID</code>! */
    FLOATING_POINT_LITERAL_INVALID(null, Category.ERROR, CanStartStatement.NO, JavaLike.NO),
    ERROR(null, Category.ERROR, CanStartStatement.NO, JavaLike.YES);

//***
//*** these tokens are Java specific and they aren't used in the JavaFX 
    // BOOLEAN("boolean", Category.KEYWORD), // Java-like
    // BYTE("byte", Category.KEYWORD), // Java-like
    // CASE("case", Category.DIRECTIVE), // Java-like
    // CHAR("char", Category.KEYWORD), // Java-like
    // CONST("const", Category.KEYWORD), // Java-like
    // DEFAULT("default", Category.DIRECTIVE), // Java-like
    // DO("do", Category.DIRECTIVE), // Java-like
    // DOUBLE("double", Category.KEYWORD), // Java-like
    // ENUM("enum", Category.KEYWORD), // Java-like
    // FINAL("final", Category.KEYWORD), // Java-like
    // FLOAT("float", Category.KEYWORD), // Java-like
    // GOTO("goto", Category.DIRECTIVE), // Java-like
    // IMPLEMENTS("implements", Category.KEYWORD), // Java-like
    // INT("int", Category.KEYWORD), // Java-like
    // INTERFACE("interface", Category.KEYWORD), // Java-like
    // LONG("long", Category.KEYWORD), // Java-like
    // NATIVE("native", Category.KEYWORD), // Java-like
    // SHORT("short", Category.KEYWORD), // Java-like
    // STATIC("static", Category.KEYWORD), // Java-like
    // STRICTFP("strictfp", Category.KEYWORD), // Java-like
    // SWITCH("switch", Category.DIRECTIVE), // Java-like
    // SYNCHRONIZED("synchronized", Category.KEYWORD), // Java-like
    // THROWS("throws", Category.KEYWORD), // Java-like
    // TRANSIENT("transient", Category.KEYWORD), // Java-like
    // VOID("void", Category.KEYWORD), // Java-like
    // VOLATILE("volatile", Category.KEYWORD), // Java-like
    
    // BANG("!", Category.OPERATOR), // Java-like
    // TILDE("~", Category.OPERATOR), // Java-like
    // BANGEQ("!=",Category.OPERATOR), // Java-like
    // AMPAMP("&&", Category.OPERATOR), // Java-like
    // BARBAR("||", Category.OPERATOR), // Java-like
    // AMP("&", Category.OPERATOR), // Java-like
    // CARET("^", Category.OPERATOR), // Java-like
    // LTLT("<<", Category.OPERATOR), // Java-like
    // GTGT(">>", Category.OPERATOR), // Java-like
    // GTGTGT(">>>", Category.OPERATOR), // Java-like
    // AMPEQ("&=", Category.OPERATOR), // Java-like
    // BAREQ("|=", Category.OPERATOR), // Java-like
    // CARETEQ("^=", Category.OPERATOR), // Java-like
    // LTLTEQ("<<=", Category.OPERATOR), // Java-like
    // GTGTEQ(">>=", Category.OPERATOR), // Java-like
    // GTGTGTEQ(">>>=", Category.OPERATOR), // Java-like
    // ELLIPSIS("...", "special"), // Java-like

    // AT("@", "special"), // Java-like

    // INT_LITERAL(null, Category.NUMBER), // Java-like
    // LONG_LITERAL(null, Category.NUMBER), // Java-like
    // FLOAT_LITERAL(null, Category.NUMBER), // Java-like
    // DOUBLE_LITERAL(null, Category.NUMBER), // Java-like
    // CHAR_LITERAL(null, "character"), // Java-like

    // FLOAT_LITERAL_INVALID(null, Category.NUMBER), // Java-like
//***
    
    /** Used by lexer for production of flyweight tokens */
    private final String fixedText;
    
    private final Category category;
    private final CanStartStatement canStartStatement;
    private final JavaLike isJavaLike;

    JavaFXTokenId(String fixedText, Category category, 
                CanStartStatement c, JavaLike isJavaLike) {
        this.fixedText = fixedText;
        this.category = category;
        this.canStartStatement = c;
        this.isJavaLike = isJavaLike;
    }

    public String fixedText() {
        return fixedText;
    }

    public String primaryCategory() {
        return category.primaryCategory();
    }

    private static final Language<JavaFXTokenId> language = 
                                          new LanguageHierarchy<JavaFXTokenId>() {

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
            Map<String,Collection<JavaFXTokenId>> cats = 
                    new HashMap<String,Collection<JavaFXTokenId>>();
            // Additional literals being a lexical error
            cats.put("error", EnumSet.of(
                FLOATING_POINT_LITERAL_INVALID
            ));
            // Literals category
            EnumSet<JavaFXTokenId> l = EnumSet.of(
                DECIMAL_LITERAL,
                TIME_LITERAL,
                OCTAL_LITERAL,
                HEX_LITERAL,
                FLOATING_POINT_LITERAL 
            );
            l.add(STRING_LITERAL);
            l.add(QUOTE_LBRACE_STRING_LITERAL);
            l.add(RBRACE_QUOTE_STRING_LITERAL);
            l.add(RBRACE_LBRACE_STRING_LITERAL);
            l.add(FORMAT_STRING_LITERAL);
            l.add(TRANSLATION_KEY);
            cats.put(Category.LITERAL.primaryCategory(), l);

            return cats;
        }

        @Override
        protected Lexer<JavaFXTokenId> createLexer(
                                           LexerRestartInfo<JavaFXTokenId> info) {
            return new JavaFXLexer(info);
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<JavaFXTokenId> token, 
                                              LanguagePath languagePath, 
                                              InputAttributes inputAttributes) {
            // Test language embedding in the block comment
            switch (token.id()) {
                case JAVADOC_COMMENT:
                    return LanguageEmbedding.create(JavadocTokenId.language(), 
                                3,
                               (token.partType() == PartType.COMPLETE) ? 2 : 0);
                case STRING_LITERAL:
                    return LanguageEmbedding.create(JavaStringTokenId.language(),
                            1,
                            (token.partType() == PartType.COMPLETE) ? 1 : 0);
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

    public enum Category {
        COMMENT("comment"),
        ERROR("error"),
        FORMAT("format"),
        I18N_ARTIFACT("i18n-artifact"),
        IDENTIFIER("identifier"),
        KEYWORD("keyword"),
        DIRECTIVE("keyword-directive"),
        LITERAL("keyword-literal"),
        NUMBER("number"),
        OPERATOR("operator"),
        SEPARATOR("separator"),
        STRING("string"),
        TIME("time"),
        WHITESPACE("whitespace"),
        ;
       private final String primaryCategory;

       Category(String primaryCategory) {
           this.primaryCategory = primaryCategory;
       }
       
       public String primaryCategory() {
            return primaryCategory;
       }

    }
    
    public enum CanStartStatement {
        /** 
         * a token can start a statement/definition -- 
         * can insert semi-colons before 
         */
        YES,
        /** 
         * a token cann't start a statement 
         */
        NO;
    }
    
    public enum JavaLike {
        /** 
         * The same token is defined in the 
         * {@link org.netbeans.api.java.lexer.JavaFXTokenId}.
         * 
         * Note, the token can have another identifier.
         */
        YES,
        /** 
         * It is a JavaFX specific token.
         */
        NO;
    }
    
}
