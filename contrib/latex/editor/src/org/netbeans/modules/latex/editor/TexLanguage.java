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
package org.netbeans.modules.latex.editor;

import java.util.Collection;
import java.util.EnumSet;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Jan Lahoda
 */
public class TexLanguage {
    
    private TexLanguage() {
    }
    
    private static Language<TexTokenId> description = new LanguageHierarchy<TexTokenId>() {
        @Override
        protected Lexer<TexTokenId> createLexer(LexerRestartInfo<TexTokenId> info) {
            return new TexLexer(info.input(), info.tokenFactory(), info.state());
        }
        @Override
        protected Collection<TexTokenId> createTokenIds() {
            return EnumSet.allOf(TexTokenId.class);
        }
        @Override
        protected String mimeType() {
            return TexKit.TEX_MIME_TYPE;
        }
    }.language();
    
    public static Language<TexTokenId> description() {
        return description;
    }
    
}