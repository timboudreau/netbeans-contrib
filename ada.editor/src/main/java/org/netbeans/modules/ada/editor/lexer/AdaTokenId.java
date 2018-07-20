/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.editor.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.ada.editor.AdaMimeResolver;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Andrea Lucarelli
 */
public enum AdaTokenId implements TokenId {

    UNKNOWN_TOKEN(null, "errors"), //NOI18N

    ABORT("abort", "keyword"), //NOI18N
    ABS("abs", "keyword"), //NOI18N
    ABSTRACT("abstract", "keyword"), //NOI18N
    ACCESS("access", "keyword"), //NOI18N
    ACCEPT("accept", "keyword"), //NOI18N
    ALIASED("aliased", "keyword"), //NOI18N
    ALL("all", "keyword"), //NOI18N
    AND("and", "keyword"), //NOI18N
    ARRAY("array", "keyword"), //NOI18N
    AT("at", "keyword"), //NOI18N
    BEGIN("begin", "keyword"), //NOI18N
    BODY("body", "keyword"), //NOI18N
    CASE("case", "keyword"), //NOI18N
    CONSTANT("constant", "keyword"), //NOI18N
    DECLARE("declare", "keyword"), //NOI18N
    DELAY("delay", "keyword"), //NOI18N
    DELTA("delta", "keyword"), //NOI18N
    DIGITS("digits", "keyword"), //NOI18N
    DO("do", "keyword"), //NOI18N
    ELSE("else", "keyword"), //NOI18N
    ELSIF("elsif", "keyword"), //NOI18N
    END("end", "keyword"), //NOI18N
    END_CASE("end case", "keyword"), //NOI18N
    END_IF("end if", "keyword"), //NOI18N
    END_LOOP("end loop", "keyword"), //NOI18N
    ENTRY("entry", "keyword"), //NOI18N
    EXCEPTION("exception", "keyword"), //NOI18N
    EXIT("exit", "keyword"), //NOI18N
    FOR("for", "keyword"), //NOI18N
    FUNCTION("function", "keyword"), //NOI18N
    GENERIC("generic", "keyword"), //NOI18N
    GOTO("goto", "keyword"), //NOI18N
    IF("if", "keyword"), //NOI18N
    IN("in", "keyword"), //NOI18N
    IS("is", "keyword"), //NOI18N
    LIMITED("limited", "keyword"), //NOI18N
    LOOP("loop", "keyword"), //NOI18N
    MOD("mod", "keyword"), //NOI18N
    NEW("new", "keyword"), //NOI18N
    NOT("not", "keyword"), //NOI18N
    NULL("null", "keyword"), //NOI18N
    OF("of", "keyword"), //NOI18N
    OR("or", "keyword"), //NOI18N
    OTHERS("others", "keyword"), //NOI18N
    OUT("out", "keyword"), //NOI18N
    PACKAGE("package", "keyword"), //NOI18N
    PRAGMA("pragma", "keyword"), //NOI18N
    PRIVATE("private", "keyword"), //NOI18N
    PROCEDURE("procedure", "keyword"), //NOI18N
    PROTECTED("protected", "keyword"), //NOI18N
    RETURN("return", "keyword"), //NOI18N
    REVERSE("reverse", "keyword"), //NOI18N
    RAISE("raise", "keyword"), //NOI18N
    RANGE("range", "keyword"), //NOI18N
    RECORD("record", "keyword"), //NOI18N
    REM("rem", "keyword"), //NOI18N
    RENAMES("renames", "keyword"), //NOI18N
    REQUEUE("requeue", "keyword"), //NOI18N
    SELECT("select", "keyword"), //NOI18N
    SEPARATE("separate", "keyword"), //NOI18N
    SUBTYPE("subtype", "keyword"), //NOI18N
    TAGGED("tagged", "keyword"), //NOI18N
    TASK("task", "keyword"), //NOI18N
    TERMINATE("terminate", "keyword"), //NOI18N
    THEN("then", "keyword"), //NOI18N
    TYPE("type", "keyword"), //NOI18N
    UNTIL("until", "keyword"), //NOI18N
    USE("use", "keyword"), //NOI18N
    WHEN("when", "keyword"), //NOI18N
    WHILE("while", "keyword"), //NOI18N
    WITH("with", "keyword"), //NOI18N
    XOR("xor", "keyword"), //NOI18N

    BOOLEAN("true", "literal"), //NOI18N
    CHARACTER("true", "literal"), //NOI18N
    FLOAT("true", "literal"), //NOI18N
    INTEGER("true", "literal"), //NOI18N
    WIDE_CHARECTER("true", "literal"), //NOI18N
    TRUE("true", "literal"), //NOI18N
    FALSE("false", "literal"), //NOI18N

    TICK("'", "separator"), //NOI18N
    LPAREN("(", "separator"), //NOI18N
    RPAREN(")", "separator"), //NOI18N
    COMMA(",", "separator"), //NOI18N
    SEMICOLON(";", "separator"), //NOI18N
    DOT(".", "separator"), //NOI18N
    DOT_DOT("..", "separator"), //NOI18N
    ARROW("=>", "separator"), //NOI18N
            
    EQ("=", "operator"), //NOI18N
    GT(">", "operator"), //NOI18N
    LT("<", "operator"), //NOI18N
    AMP("&", "operator"), //NOI18N
    MINUS("-", "operator"), //NOI18N
    STAR("*", "operator"), //NOI18N
    PLUS("+", "operator"), //NOI18N
    SLASH("/", "operator"), //NOI18N
    COLON(":", "operator"), //NOI18N
    BAR("|", "operator"), //NOI18N
    EXPON("**", "operator"), //NOI18N
    INEQ("/=", "operator"), //NOI18N
    GTEQ(">=", "operator"), //NOI18N
    LTEQ("<=", "operator"), //NOI18N
    LTLT("<<", "operator"), //NOI18N
    GTGT(">>", "operator"), //NOI18N
    BOX("<>", "operator"), //NOI18N
    ASSIGNMENT(":=", "operator"), //NOI18N

    DECIMAL_LITERAL(null, "number"), //NOI18N
    BASED_LITERAL(null, "number"), //NOI18N
    STRING_LITERAL(null, "string"), //NOI18N
    CHAR_LITERAL(null, "character"), //NOI18N
    WHITESPACE(null, "whitespace"), //NOI18N
    COMMENT(null, "comment"), //NOI18N
    ATTRIBUTE(null, "attribute"), //NOI18N
    IDENTIFIER(null, "identifier"); //NOI18N

    private final String fixedText;
    private final String primaryCategory;

    AdaTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }

    public String fixedText() {
        return fixedText;
    }

    public String primaryCategory() {
        return primaryCategory;
    }
    private static final Language<AdaTokenId> language =
            new LanguageHierarchy<AdaTokenId>() {

                @Override
                protected Collection<AdaTokenId> createTokenIds() {
                    return EnumSet.allOf(AdaTokenId.class);
                }

                @Override
                protected Map<String, Collection<AdaTokenId>> createTokenCategories() {
                    Map<String, Collection<AdaTokenId>> cats = new HashMap<String, Collection<AdaTokenId>>();
                    return cats;
                }

                @Override
                protected Lexer<AdaTokenId> createLexer(LexerRestartInfo<AdaTokenId> info) {
                    return AdaLexer.create(info);
                }

                @Override
                protected String mimeType() {
                    return AdaMimeResolver.ADA_MIME_TYPE;
                }

            }.language();

    public static Language<AdaTokenId> language() {
        return language;
    }
}
