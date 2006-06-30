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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.editor;

import org.netbeans.api.lexer.Lexer;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.AbstractLanguage;

public class TexLanguage extends AbstractLanguage {

    /** Lazily initialized singleton instance of this language. */
    private static TexLanguage INSTANCE;

    /** @return singleton instance of this language. */
    public static synchronized TexLanguage get() {
        if (INSTANCE == null)
            INSTANCE = new TexLanguage();

        return INSTANCE;
    }

    public static final int WHITESPACE_INT = 1;
    public static final int COMMENT_INT = 2;
    public static final int WORD_INT = 3;
    public static final int COMMAND_INT = 4;
    public static final int COMMAND_ARGUMENT_MANDATORY_INT = 5;
    public static final int COMMAND_ARGUMENT_NON_MANDATORY_INT = 6;
    public static final int UNKNOWN_CHARACTER_INT = 7;
    public static final int MATH_INT = 8;
    public static final int COMP_BRACKET_LEFT_INT = 9;
    public static final int COMP_BRACKET_RIGHT_INT = 10;
    public static final int RECT_BRACKET_LEFT_INT = 11;
    public static final int RECT_BRACKET_RIGHT_INT = 12;
    public static final int PARAGRAPH_END_INT = 13;


    public static final TokenId COMMAND = new TokenId("command", COMMAND_INT, new String[]{"command"});
    public static final TokenId COMMAND_ARGUMENT_MANDATORY = new TokenId("command-argument-mandatory", COMMAND_ARGUMENT_MANDATORY_INT, new String[]{"command"});
    public static final TokenId COMMAND_ARGUMENT_NON_MANDATORY = new TokenId("command-argument-non-mandatory", COMMAND_ARGUMENT_NON_MANDATORY_INT, new String[]{"command"});
    public static final TokenId COMMENT = new TokenId("comment", COMMENT_INT, new String[]{"comment"});
    public static final TokenId COMP_BRACKET_LEFT = new TokenId("comp-bracket-left", COMP_BRACKET_LEFT_INT, new String[]{"brackets"});
    public static final TokenId COMP_BRACKET_RIGHT = new TokenId("comp-bracket-right", COMP_BRACKET_RIGHT_INT, new String[]{"brackets"});
    public static final TokenId MATH = new TokenId("math", MATH_INT, new String[]{"brackets"});
    public static final TokenId PARAGRAPH_END = new TokenId("paragraph-end", PARAGRAPH_END_INT, new String[]{"whitespaces"});
    public static final TokenId RECT_BRACKET_LEFT = new TokenId("rect-bracket-left", RECT_BRACKET_LEFT_INT, new String[]{"brackets"});
    public static final TokenId RECT_BRACKET_RIGHT = new TokenId("rect-bracket-right", RECT_BRACKET_RIGHT_INT, new String[]{"brackets"});
    public static final TokenId UNKNOWN_CHARACTER = new TokenId("unknown-character", UNKNOWN_CHARACTER_INT, new String[]{"unknown"});
    public static final TokenId WHITESPACE = new TokenId("whitespace", WHITESPACE_INT, new String[]{"whitespaces"});
    public static final TokenId WORD = new TokenId("word", WORD_INT, new String[]{"word"});

    TexLanguage() {
    }

    public Lexer createLexer() {
        return new TexLexer();
    }

}
