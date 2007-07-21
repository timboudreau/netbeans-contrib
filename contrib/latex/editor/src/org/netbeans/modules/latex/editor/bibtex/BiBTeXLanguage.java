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
package org.netbeans.modules.latex.editor.bibtex;

import java.util.Collection;
import java.util.EnumSet;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.model.lexer.BiBTeXTokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class BiBTeXLanguage {

    private BiBTeXLanguage() {
    }
    
    private static Language<BiBTeXTokenId> description = new LanguageHierarchy<BiBTeXTokenId>() {
        protected Lexer<BiBTeXTokenId> createLexer(LexerRestartInfo<BiBTeXTokenId> info) {
            return new BiBTeXLexer(info.input(), info.tokenFactory(), info.state());
        }
        protected Collection<BiBTeXTokenId> createTokenIds() {
            return EnumSet.allOf(BiBTeXTokenId.class);
        }
        protected LanguageEmbedding embedding(Token<BiBTeXTokenId> token, boolean complete, LanguagePath path, InputAttributes extra) {
            return null;
        }
        protected String mimeType() {
            return "text/x-bibtex";
        }
    }.language();
    
    public static Language<BiBTeXTokenId> description() {
        return description;
    }

}
