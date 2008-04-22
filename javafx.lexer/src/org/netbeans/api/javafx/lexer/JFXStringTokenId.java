/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.netbeans.api.javafx.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.lib.javafx.lexer.JFXStringLexer;

/**
 * Token ids for java string language
 * (embedded in java string or character literals).
 *
 * @author Miloslav Metelka
 * @author Rastislav Komara (<a href="mailto:moonko@netbeans.org">RKo</a>)
 * @version 1.00
 */
public enum JFXStringTokenId implements TokenId {

    TEXT("string"), //NOI18N
    BACKSPACE("string-escape"), //NOI18N
    FORM_FEED("string-escape"), //NOI18N
    NEWLINE("string-escape"), //NOI18N
    CR("string-escape"), //NOI18N
    TAB("string-escape"), //NOI18N
    CODE_OPENING_BRACE("symbol"), //NOI18N
    CODE_ENCLOSING_BRACE("symbol"), //NOI18N
    CODE_OPENING_BRACE_ESCAPE("string-escape"), //NOI18N
    CODE_ENCLOSING_BRACE_ESCAPE("string-escape"), //NOI18N
    SINGLE_QUOTE("string-escape"), //NOI18N
    DOUBLE_QUOTE("string-escape"), //NOI18N
    BACKSLASH("string-escape"), //NOI18N
    OCTAL_ESCAPE("string-escape"), //NOI18N
    OCTAL_ESCAPE_INVALID("string-escape-invalid"), //NOI18N
    UNICODE_ESCAPE("string-escape"), //NOI18N
    UNICODE_ESCAPE_INVALID("string-escape-invalid"), //NOI18N
    ESCAPE_SEQUENCE_INVALID("string-escape-invalid"); //NOI18N

    private final String primaryCategory;

    JFXStringTokenId() {
        this(null);
    }

    JFXStringTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String primaryCategory() {
        return primaryCategory;
    }

    private static final Language<JFXStringTokenId> language = new MyLanguageHierarchy(false).language();
    private static final Language<JFXStringTokenId> language_STO = new MyLanguageHierarchy(true).language();

    public static Language<JFXStringTokenId> language() {
        return language(false);
    }

    public static Language<JFXStringTokenId> language(boolean forceStringOnly) {
        return forceStringOnly ? language_STO : language;
    }

    private static class MyLanguageHierarchy extends LanguageHierarchy<JFXStringTokenId> {
        private final boolean forceStringOnly;

        private MyLanguageHierarchy(boolean forceStringOnly) {
            this.forceStringOnly = forceStringOnly;
        }

        @Override
        protected Collection<JFXStringTokenId> createTokenIds() {
            return EnumSet.allOf(JFXStringTokenId.class);
        }

        @Override
        protected Map<String, Collection<JFXStringTokenId>> createTokenCategories() {
            return null; // no extra categories
        }

        @Override
        protected Lexer<JFXStringTokenId> createLexer(LexerRestartInfo<JFXStringTokenId> info) {
            return new JFXStringLexer(info, forceStringOnly);
        }

        @Override
        protected String mimeType() {
            return "text/x-java-string"; //NOI18N
        }
    }
}