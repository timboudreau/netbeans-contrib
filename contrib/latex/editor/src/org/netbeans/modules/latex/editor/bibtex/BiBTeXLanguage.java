/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.editor.bibtex;

import org.netbeans.api.lexer.Lexer;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.AbstractLanguage;

public class BiBTeXLanguage extends AbstractLanguage {

    /** Lazily initialized singleton instance of this language. */
    private static BiBTeXLanguage INSTANCE;

    /** @return singleton instance of this language. */
    public static synchronized BiBTeXLanguage get() {
        if (INSTANCE == null)
            INSTANCE = new BiBTeXLanguage();

        return INSTANCE;
    }

    public static final int WHITESPACE_INT = 1;
    public static final int COMMENT_INT = 2;
    public static final int TYPE_INT = 3;
    public static final int OP_BRAC_INT = 4;
    public static final int CL_BRAC_INT = 5;
    public static final int STRING_INT = 6;
    public static final int UNKNOWN_CHARACTER_INT = 7;
    public static final int TEXT_INT = 8;
    public static final int COMMA_INT = 9;
    public static final int EQUALS_INT = 10;


    public static final TokenId CL_BRAC = new TokenId("cl_brac", CL_BRAC_INT, new String[]{"brackets"});
    public static final TokenId COMMA = new TokenId("comma", COMMA_INT, new String[]{"comma"});
    public static final TokenId COMMENT = new TokenId("comment", COMMENT_INT, new String[]{"comment"});
    public static final TokenId EQUALS = new TokenId("equals", EQUALS_INT, new String[]{"equals"});
    public static final TokenId OP_BRAC = new TokenId("op_brac", OP_BRAC_INT, new String[]{"brackets"});
    public static final TokenId STRING = new TokenId("string", STRING_INT, new String[]{"string"});
    public static final TokenId TEXT = new TokenId("text", TEXT_INT, new String[]{"text"});
    public static final TokenId TYPE = new TokenId("type", TYPE_INT, new String[]{"type"});
    public static final TokenId UNKNOWN_CHARACTER = new TokenId("unknown-character", UNKNOWN_CHARACTER_INT, new String[]{"unknown"});
    public static final TokenId WHITESPACE = new TokenId("whitespace", WHITESPACE_INT, new String[]{"whitespaces"});

    BiBTeXLanguage() {
    }

    public Lexer createLexer() {
        return new BiBTeXLexer();
    }

}
