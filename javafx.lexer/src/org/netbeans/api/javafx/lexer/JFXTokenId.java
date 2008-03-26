package org.netbeans.api.javafx.lexer;

import org.netbeans.api.java.lexer.JavaStringTokenId;
import org.netbeans.api.lexer.*;
import org.netbeans.lib.javafx.lexer.JFXLexer;
import org.netbeans.spi.lexer.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * @author Rastislav Komara (<a href="mailto:rastislav.komara@sun.com">RKo</a>)
 * @todo documentation
 */
public enum JFXTokenId implements TokenId {
    PACKAGE("keyword", 26),
    FUNCTION("keyword", 16),
    LT("operator", 85),
    STAR("operator", 91),
    WHILE("keyword-directive", 41),
    TranslationKeyBody("operator", 150),
    NEW("keyword", 22),
    INDEXOF("keyword", 61),
    SEQ_EXPLICIT(null, 129),
    PARAM(null, 106),
    TIME_LITERAL("time", 155),
    TYPE_UNKNOWN("error", 134),
    NOT("keyword", 23),
    FUNC_EXPR(null, 107),
    RBRACE_QUOTE_STRING_LITERAL("string", 146),
    BREAK("keyword-directive", 10),
    STATEMENT(null, 108),
    MODIFIER(null, 104),
    LBRACKET("separator", 44),
    RPAREN("separator", 77),
    IMPORT("keyword", 18),
    STRING_LITERAL("string", 142),
    FLOATING_POINT_LITERAL("number", 160),
    INSERT("keyword", 20),
    SUBSUB("operator", 46),
    Digits("number", 153),
    BIND("keyword", 8),
    STAREQ("operator", 96),
    OBJECT_LIT_PART("string", 127),
    THIS("keyword", 36),
    RETURN("keyword-directive", 32),
    DoubleQuoteBody("string", 140),
    TRANSLATION_KEY("literal", 151),
    VAR("keyword", 40),
    SUPER("keyword", 33),
    LAST("keyword", 65),
    EQ("operator", 83),
    COMMENT("comment", 165),
    INTO("keyword", 63),
    QUES("operator", 100),
    BOUND("keyword", 9),
    EQEQ("operator", 82),
    MISSING_NAME(null, 111),
    RBRACE("separator", 148),
    POUND("operator", 42),
    LINE_COMMENT("comment", 166),
    STATIC("keyword", 35),
    PRIVATE("keyword", 28),
    SEQ_INDEX(null, 123),
    NULL("literal", 24),
    ELSE("keyword-directive", 54),
    ON("keyword", 67),
    DELETE("keyword", 13),
    SLASHEQ("operator", 97),
    TYPE_FUNCTION(null, 132),
    ASSERT("keyword-directive", 6),
    TRY("keyword-directive", 38),
    TYPED_ARG_LIST(null, 136),
    TYPE_ANY(null, 133),
    SLICE_CLAUSE(null, 112),
    NAMED_TWEEN(null, 139),
    INVERSE("keyword", 64),
    WS("whitespace", 164),
    RangeDots("separator", 159),
    TYPEOF("keyword", 73),
    OR("keyword", 68),
    JavaIDDigit("identifier", 162),
    SIZEOF("keyword", 34),
    GT("operator", 84),
    CATCH("keyword-directive", 53),
    FROM("keyword", 59),
    REVERSE("keyword", 70),
    FALSE("literal", 14),
    INIT("keyword", 19),
    Letter("identifier", 161),
    DECIMAL_LITERAL("number", 152),
    THROW("keyword-directive", 37),
    LAST_TOKEN(null, 167),
    PROTECTED("keyword", 29),
    WHERE("keyword", 75),
    CLASS("keyword", 11),
    SEQ_SLICE_EXCLUSIVE(null, 125),
    ON_REPLACE_SLICE(null, 113),
    PLUSPLUS("operator", 45),
    LBRACE("separator", 145),
    TYPE_NAMED(null, 131),
    ATTRIBUTE("keyword", 7),
    LTEQ("operator", 87),
    SUBEQ("operator", 95),
    OBJECT_LIT(null, 126),
    Exponent("number", 154),
    FOR("keyword-directive", 15),
    STEP("keyword", 71),
    SUB("operator", 90),
    DOTDOT("operator", 76),
    ABSTRACT("keyword", 5),
    EXCLUSIVE("keyword", 55),
    NextIsPercent("string", 143),
    AND("keyword", 50),
    TYPE_ARG(null, 135),
    HexDigit("number", 157),
    PLUSEQ("operator", 94),
    LPAREN("separator", 43),
    IF("keyword-directive", 17),
    EXPR_LIST(null, 118),
    AS("keyword", 51),
    SLASH("operator", 92),
    IN("keyword", 60),
    THEN("keyword", 72),
    CONTINUE("keyword-directive", 12),
    COMMA("separator", 80),
    IDENTIFIER("identifier", 163),
    SUCHTHAT_BLOCK(null, 138),
    REPLACE("keyword", 69),
    TWEEN("keyword", 101),
    QUOTE_LBRACE_STRING_LITERAL("string", 144),
    DOC_COMMENT("comment", 137),
    POSTINCR(null, 121),
    SEMI_INSERT_START("string", 4),
    PIPE("operator", 47),
    PLUS("operator", 89),
    HEX_LITERAL("number", 158),
    EMPTY_FORMAT_STRING(null, 130),
    RBRACKET("separator", 78),
    DOT("separator", 81),
    RBRACE_LBRACE_STRING_LITERAL("string", 147),
    EXPRESSION(null, 109),
    WITH("keyword", 74),
    PERCENT("operator", 93),
    LAZY("keyword", 66),
    LTGT("operator", 86),
    NEGATIVE(null, 120),
    ON_REPLACE(null, 114),
    OCTAL_LITERAL("number", 156),
    ON_INSERT_ELEMENT(null, 116),
    BEFORE("keyword", 52),
    INSTANCEOF("keyword", 62),
    FUNC_APPLY(null, 119),
    AFTER("keyword", 49),
    GTEQ("operator", 88),
    CLASS_MEMBERS(null, 105),
    MODULE(null, 103),
    READONLY("keyword", 31),
    TRUE("literal", 39),
    SEMI("separator", 79),
    COLON("operator", 99),
    POSTINIT(null, 27),
    SEMI_INSERT_END(null, 48),
    PERCENTEQ("operator", 98),
    FINALLY("keyword-directive", 57),
    OVERRIDE("keyword", 25),
    FORMAT_STRING_LITERAL("string", 149),
    BLOCK(null, 110),
    SEQ_EMPTY(null, 128),
    SEQ_SLICE(null, 124),
    ON_REPLACE_ELEMENT(null, 115),
    POSTDECR(null, 122),
    SUCHTHAT("operator", 102),
    PUBLIC("keyword", 30),
    EXTENDS("keyword", 56),
    SingleQuoteBody("string", 141),
    LET("keyword", 21),
    ON_DELETE_ELEMENT(null, 117),
    FIRST("keyword", 58),
    UNKNOWN(null, 200);

    public static final String UNIVERSAL_CATEGORY = "DEMONIC";
    private final String primaryCategory;
    private final int tokenType;
    private static JFXTokenId[] typeToId;

    static {
        try {
            final JFXTokenId[] tokenIds = JFXTokenId.values();
            Arrays.sort(tokenIds, new Comparator<JFXTokenId>() {
                public int compare(JFXTokenId o1, JFXTokenId o2) {
                    return o1.getTokenType() - o2.getTokenType();
                }
            });
            final JFXTokenId tid = tokenIds[tokenIds.length - 1];
            final int type = tid.getTokenType();
            typeToId = new JFXTokenId[type + 2];
            for (JFXTokenId jfxTokenId : tokenIds) {
                if (jfxTokenId.getTokenType() > 0) {
                    typeToId[jfxTokenId.getTokenType()] = jfxTokenId;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public static JFXTokenId getId(int tokenType) {
        if (tokenType >= 0 && tokenType < typeToId.length) {
            return typeToId[tokenType];
        } else {
            return UNKNOWN;
        }
    }

    JFXTokenId(String primaryCategory, int tokenType) {
        this.primaryCategory = primaryCategory;
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public int getTokenType() {
        return tokenType;
    }

    public String primaryCategory() {
        return primaryCategory != null ? primaryCategory : UNIVERSAL_CATEGORY;
    }


    private static final Language<JFXTokenId> language = new LanguageHierarchy<JFXTokenId>() {
        JFXLexer lexer;

        protected Collection<JFXTokenId> createTokenIds() {
            return Arrays.asList(JFXTokenId.values());
        }

        protected Lexer<JFXTokenId> createLexer(LexerRestartInfo<JFXTokenId> info) {
            if (lexer == null) {
                lexer = new JFXLexer();
            }
            try {
                lexer.restart(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lexer;
        }

        @Override
        protected String mimeType() {
            return "text/x-fx";
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<JFXTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            switch (token.id()) {
                case COMMENT:
                    final StringBuilder tt = token.text() != null ? new StringBuilder(token.text()) : null;
                    if (tt != null && tt.toString().trim().startsWith("/**")) {
                        return LanguageEmbedding.create(org.netbeans.api.java.lexer.JavadocTokenId.language(), 3,
                                (token.partType() == PartType.COMPLETE) ? 2 : 0);
                    } else {
                        return null;
                    }
                case QUOTE_LBRACE_STRING_LITERAL:
                case RBRACE_QUOTE_STRING_LITERAL:
                case RBRACE_LBRACE_STRING_LITERAL:
                case DoubleQuoteBody:
                case SingleQuoteBody:
                case STRING_LITERAL:
                    return LanguageEmbedding.create(JavaStringTokenId.language(), 1,
                            (token.partType() == PartType.COMPLETE) ? 1 : 0);
            }
            return null; // No embedding
        }

        @Override
        protected EmbeddingPresence embeddingPresence(JFXTokenId id) {
            switch (id) {
                case COMMENT:
                    return EmbeddingPresence.ALWAYS_QUERY;
                default:
                    return EmbeddingPresence.NONE;
            }
        }

    }.language();

    public static Language<JFXTokenId> language() {
        return language;
    }


}
