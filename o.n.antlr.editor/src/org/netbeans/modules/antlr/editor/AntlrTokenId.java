/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * or only the GPL Version , indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.antlr.editor;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import static org.netbeans.modules.antlr.editor.AntlrTokenIdCategory.*;
import org.netbeans.antlr.editor.gen.ANTLRv3Lexer;

/**
 * Token ids of CSS language
 *
 * @author Marek Fukala
 */
public enum AntlrTokenId implements TokenId {

    EOF(ANTLRv3Lexer.EOF, OTHERS),
    DOC_COMMENT(ANTLRv3Lexer.DOC_COMMENT, COMMENTS),
    PARSER(ANTLRv3Lexer.PARSER, KEYWORDS),
    LEXER(ANTLRv3Lexer.LEXER, KEYWORDS),
    RULE(ANTLRv3Lexer.RULE, OTHERS),
    BLOCK(ANTLRv3Lexer.BLOCK, OTHERS),
    OPTIONAL(ANTLRv3Lexer.OPTIONAL, OTHERS),
    CLOSURE(ANTLRv3Lexer.CLOSURE, OTHERS),
    POSITIVE_CLOSURE(ANTLRv3Lexer.POSITIVE_CLOSURE, OTHERS),
    SYNPRED(ANTLRv3Lexer.SYNPRED, OTHERS),
    RANGE(ANTLRv3Lexer.RANGE, OTHERS),
    CHAR_RANGE(ANTLRv3Lexer.CHAR_RANGE, OTHERS),
    EPSILON(ANTLRv3Lexer.EPSILON, OTHERS),
    ALT(ANTLRv3Lexer.ALT, OTHERS),
    EOR(ANTLRv3Lexer.EOR, OTHERS),
    EOB(ANTLRv3Lexer.EOB, OTHERS),
    EOA(ANTLRv3Lexer.EOA, OTHERS),
    ID(ANTLRv3Lexer.ID, OTHERS),
    ARG(ANTLRv3Lexer.ARG, OTHERS),
    ARGLIST(ANTLRv3Lexer.ARGLIST, OTHERS),
    RET(ANTLRv3Lexer.RET, OTHERS),
    LEXER_GRAMMAR(ANTLRv3Lexer.LEXER_GRAMMAR, OTHERS),
    PARSER_GRAMMAR(ANTLRv3Lexer.PARSER_GRAMMAR, OTHERS),
    TREE_GRAMMAR(ANTLRv3Lexer.TREE_GRAMMAR, OTHERS),
    COMBINED_GRAMMAR(ANTLRv3Lexer.COMBINED_GRAMMAR, OTHERS),
    INITACTION(ANTLRv3Lexer.INITACTION, OTHERS),
    LABEL(ANTLRv3Lexer.LABEL, OTHERS),
    TEMPLATE(ANTLRv3Lexer.TEMPLATE, OTHERS),
    SCOPE(ANTLRv3Lexer.SCOPE, OTHERS),
    SEMPRED(ANTLRv3Lexer.SEMPRED, OTHERS),
    GATED_SEMPRED(ANTLRv3Lexer.GATED_SEMPRED, OTHERS),
    SYN_SEMPRED(ANTLRv3Lexer.SYN_SEMPRED, OTHERS),
    BACKTRACK_SEMPRED(ANTLRv3Lexer.BACKTRACK_SEMPRED, OTHERS),
    FRAGMENT(ANTLRv3Lexer.FRAGMENT, OTHERS),
    TREE_BEGIN(ANTLRv3Lexer.TREE_BEGIN, OTHERS),
    ROOT(ANTLRv3Lexer.ROOT, OTHERS),
    BANG(ANTLRv3Lexer.BANG, OTHERS),
    REWRITE(ANTLRv3Lexer.REWRITE, OTHERS),
    TOKENS(ANTLRv3Lexer.TOKENS, OTHERS),
    TOKEN_REF(ANTLRv3Lexer.TOKEN_REF, AntlrTokenIdCategory.TOKENS), 
    STRING_LITERAL(ANTLRv3Lexer.STRING_LITERAL, STRINGS),
    CHAR_LITERAL(ANTLRv3Lexer.CHAR_LITERAL, STRINGS),
    ACTION(ANTLRv3Lexer.ACTION, OTHERS),
    OPTIONS(ANTLRv3Lexer.OPTIONS, OTHERS),
    INT(ANTLRv3Lexer.INT, OTHERS),
    ARG_ACTION(ANTLRv3Lexer.ARG_ACTION, OTHERS),
    RULE_REF(ANTLRv3Lexer.RULE_REF, RULES),
    DOUBLE_QUOTE_STRING_LITERAL(ANTLRv3Lexer.DOUBLE_QUOTE_STRING_LITERAL, STRINGS),
    DOUBLE_ANGLE_STRING_LITERAL(ANTLRv3Lexer.DOUBLE_ANGLE_STRING_LITERAL, STRINGS),
    SRC(ANTLRv3Lexer.SRC, OTHERS),
    SL_COMMENT(ANTLRv3Lexer.SL_COMMENT, COMMENTS),
    ML_COMMENT(ANTLRv3Lexer.ML_COMMENT, COMMENTS),
    LITERAL_CHAR(ANTLRv3Lexer.LITERAL_CHAR, STRINGS),
    ESC(ANTLRv3Lexer.ESC, OTHERS),
    XDIGIT(ANTLRv3Lexer.XDIGIT, OTHERS),
    NESTED_ARG_ACTION(ANTLRv3Lexer.NESTED_ARG_ACTION, OTHERS),
    ACTION_STRING_LITERAL(ANTLRv3Lexer.ACTION_STRING_LITERAL, STRINGS),
    ACTION_CHAR_LITERAL(ANTLRv3Lexer.ACTION_CHAR_LITERAL, STRINGS),
    NESTED_ACTION(ANTLRv3Lexer.NESTED_ACTION, OTHERS),
    ACTION_ESC(ANTLRv3Lexer.ACTION_ESC, OTHERS),
    WS_LOOP(ANTLRv3Lexer.WS_LOOP, OTHERS),
    WS(ANTLRv3Lexer.WS, OTHERS),
    
    
    T__65(ANTLRv3Lexer.T__65, OTHERS),
    T__66(ANTLRv3Lexer.T__66, OTHERS),
    T__67(ANTLRv3Lexer.T__67, OTHERS),
    T__68(ANTLRv3Lexer.T__68, OTHERS),
    T__69(ANTLRv3Lexer.T__69, OTHERS),
    T__70(ANTLRv3Lexer.T__70, OTHERS),
    T__71(ANTLRv3Lexer.T__71, OTHERS),
    T__72(ANTLRv3Lexer.T__72, OTHERS),
    T__73(ANTLRv3Lexer.T__73, OTHERS),
    T__74(ANTLRv3Lexer.T__74, OTHERS),
    T__75(ANTLRv3Lexer.T__75, OTHERS),
    T__76(ANTLRv3Lexer.T__76, OTHERS),
    T__77(ANTLRv3Lexer.T__77, OTHERS),
    T__78(ANTLRv3Lexer.T__78, OTHERS),
    T__79(ANTLRv3Lexer.T__79, OTHERS),
    T__80(ANTLRv3Lexer.T__80, OTHERS),
    T__81(ANTLRv3Lexer.T__81, OTHERS),
    T__82(ANTLRv3Lexer.T__82, OTHERS),
    T__83(ANTLRv3Lexer.T__83, OTHERS),
    T__84(ANTLRv3Lexer.T__84, OTHERS),
    T__85(ANTLRv3Lexer.T__85, OTHERS),
    T__86(ANTLRv3Lexer.T__86, OTHERS),
    T__87(ANTLRv3Lexer.T__87, OTHERS),
    T__88(ANTLRv3Lexer.T__88, OTHERS),
    T__89(ANTLRv3Lexer.T__89, OTHERS),
    T__90(ANTLRv3Lexer.T__90, OTHERS),
    T__91(ANTLRv3Lexer.T__91, OTHERS),
    T__92(ANTLRv3Lexer.T__92, OTHERS),
    T__93(ANTLRv3Lexer.T__93, OTHERS);
    private static final Map<Integer, AntlrTokenId> codesMap = new HashMap<Integer, AntlrTokenId>();

    static {
        for (AntlrTokenId id : values()) {
            codesMap.put(id.code, id);
        }
    }

    public static AntlrTokenId forTokenTypeCode(int tokenTypeCode) {
        AntlrTokenId tid = codesMap.get(tokenTypeCode);
        assert tid != null : "No AntlrTokenId found for ANLR token code " + tokenTypeCode;
        return tid;
    }
    private final AntlrTokenIdCategory primaryCategory;
    private final int code;
    private static final Language<AntlrTokenId> language = new AntlrLanguageHierarchy().language();

    AntlrTokenId(int code, AntlrTokenIdCategory primaryCategory) {
        this.primaryCategory = primaryCategory;
        this.code = code;
    }

    /**
     * Gets a LanguageDescription describing a set of token ids that comprise
     * the given language.
     *
     * @return non-null LanguageDescription
     */
    public static Language<AntlrTokenId> language() {
        return language;
    }

    /**
     * Get name of primary token category into which this token belongs.
     * <br/>
     * Other token categories for this id can be defined in the language
     * hierarchy.
     *
     * @return name of the primary token category into which this token belongs
     * or null if there is no primary category for this token.
     */
    @Override
    public String primaryCategory() {
        return primaryCategory.name().toLowerCase();
    }

    /**
     * same as primaryCategory() but returns CssTokenIdCategory enum member
     */
    public AntlrTokenIdCategory getTokenCategory() {
        return primaryCategory;
    }

    /**
     * Verifies whether the given input text is lexed as on token of this type.
     *
     * If some part of the input text is not matched by the css token the method
     * returns false.
     *
     * @since 1.12
     * @param input source code to be lexed
     * @return true if the whole source code is lexed as a token of this type.
     */
    public boolean matchesInput(CharSequence input) {
        TokenHierarchy<CharSequence> th = TokenHierarchy.create(input, AntlrTokenId.language());
        TokenSequence<AntlrTokenId> ts = th.tokenSequence(AntlrTokenId.language());
        ts.moveStart();
        if (!ts.moveNext()) {
            return false;
        }
        org.netbeans.api.lexer.Token<AntlrTokenId> t = ts.token();
        return !ts.moveNext() && t.id() == this;
    }
}
