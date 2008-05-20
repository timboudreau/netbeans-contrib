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
package org.netbeans.modules.erlang.platform.index;

import org.netbeans.api.lexer.Language;
import org.netbeans.modules.erlang.editing.Erlang;
import org.netbeans.modules.gsf.spi.DefaultLanguageConfig;

public class ErlangGsfLanguage extends DefaultLanguageConfig {
        
    private Language lexerLanguage;
    
    public ErlangGsfLanguage() {
    }

    @Override
    public String getLineCommentPrefix() {
        return "%"; // NOI18N
    }

    @Override
    public boolean isIdentifierChar(char c) {
        return Character.isJavaIdentifierPart(c) || (// Globals, fields and parameter prefixes (for blocks and symbols)
        c == '$') || (c == '@') || (c == '&') || (c == ':') || (// Function name suffixes
        c == '!') || (c == '?') || (c == '=');
    }

    @Override
    public Language getLexerLanguage() {
        /** 
         * Ugly hacking for waiting for GLF language inited 
         * @see org.netbeans.modules.languages.LanguageImpl#read 
         */
        if (lexerLanguage == null) {
            int counter = 0;
            try {
                while (lexerLanguage == null && counter < 200) {
                    Thread.sleep(100);
                    lexerLanguage = org.netbeans.api.lexer.Language.find(Erlang.MIME_TYPE);
                    counter++;
                }
            } catch (InterruptedException e) {
            }
        }
        return lexerLanguage;
    }

    @Override
    public String getDisplayName() {
        return "Erlang";
    }
    
    @Override
    public String getPreferredExtension() {
        return "erl"; // NOI18N
    }
}


