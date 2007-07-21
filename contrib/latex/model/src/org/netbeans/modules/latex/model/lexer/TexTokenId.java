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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.model.lexer;

import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author Jan Lahoda
 */
public enum TexTokenId implements TokenId {
    
    COMMAND("command"),
    COMMENT("comment"),
    COMP_BRACKET_LEFT("brackets"),
    COMP_BRACKET_RIGHT("brackets"),
    MATH("brackets"),
    PARAGRAPH_END("whitespaces"),
    RECT_BRACKET_LEFT("brackets"),
    RECT_BRACKET_RIGHT("brackets"),
    UNKNOWN_CHARACTER("unknown"),
    WHITESPACE("whitespaces"),
    WORD("word"),
    EMBEDD("word");
    
    private String category;
    
    TexTokenId(String category) {
        this.category = category;
    };
    
    public String primaryCategory() {
        return category;
    }
    
}
