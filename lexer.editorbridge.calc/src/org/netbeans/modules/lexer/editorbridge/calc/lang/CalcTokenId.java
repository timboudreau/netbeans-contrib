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

package org.netbeans.modules.lexer.editorbridge.calc.lang;

import org.netbeans.api.lexer.TokenId;

/**
 * Calc token id definition.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public enum CalcTokenId implements TokenId {

    WHITESPACE(null, "whitespace"),
    SL_COMMENT(null, "comment"),
    ML_COMMENT(null, "comment"),
    E("e", "keyword"),
    PI("pi", "keyword"),
    IDENTIFIER(null, null),
    INT_LITERAL(null, "number"),
    FLOAT_LITERAL(null, "number"),
    PLUS("+", "operator"),
    MINUS("-", "operator"),
    STAR("*", "operator"),
    SLASH("/", "operator"),
    LPAREN("(", "separator"),
    RPAREN(")", "separator"),
    ERROR(null, "error"),
    ML_COMMENT_INCOMPLETE(null, "comment");


    private final String fixedText;

    private final String primaryCategory;

    private CalcTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }
    
    public String fixedText() {
        return fixedText;
    }

    public String primaryCategory() {
        return primaryCategory;
    }

}
