/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.freemarker;
import java.util.Arrays;
import java.util.Collection;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Jan Lahoda
 */
public enum TopLevelFreeMarkerTokenId implements TokenId {

    FREEMARKER_DIRECTIVE("freemarker"), // NOI18N
    FREEMARKER_VARIABLE("freemarker"), // NOI18N
    OTHER_PART("other"); // NOI18N
    
    private String primaryCategory;
    
    TopLevelFreeMarkerTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }
    
    public String primaryCategory() {
        return primaryCategory;
    }

    private static Language<TopLevelFreeMarkerTokenId> language = new LanguageHierarchy<TopLevelFreeMarkerTokenId>() {
        @Override
        protected Collection<TopLevelFreeMarkerTokenId> createTokenIds() {
            return Arrays.asList(TopLevelFreeMarkerTokenId.values());
        }

        @Override
        protected Lexer<TopLevelFreeMarkerTokenId> createLexer(LexerRestartInfo<TopLevelFreeMarkerTokenId> info) {
            return new TopLevelFreeMarkerLexer(info.input(), info.tokenFactory(), (Boolean) info.state());
        }

        @Override
        protected String mimeType() {
            return MimeTypes.FREEMARKER_TOP_LEVEL;
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<TopLevelFreeMarkerTokenId> token, LanguagePath languagePath, InputAttributes inputAttributes) {
            switch (token.id()) {
                case OTHER_PART:
                    String embeddedMimeType = null;
                    
                    if (inputAttributes != null) {
                        Object value = inputAttributes.getValue(languagePath, MimeTypes.MIME_TYPE_PROPERTY);

                        if (value instanceof String) {
                            embeddedMimeType = MimeTypes.getEmbeddedMimeType((String) value);
                        }
                    }
                    
                    if (embeddedMimeType != null) {
                        Language<?> language = (Language) Language.find(embeddedMimeType);

                        if (language != null) {
                            return LanguageEmbedding.create(language, 0, 0);
                        }
                    }

                    return null;
                case FREEMARKER_DIRECTIVE:
                case FREEMARKER_VARIABLE:
                    Language<?> language = (Language) Language.find(MimeTypes.FREEMARKER);
                    
                    if (language != null) {
                        return LanguageEmbedding.create(language, 0, 0);
                    }
                    return null;
                default:
                    return null;
            }
        }

    }.language();
    
    public static Language<TopLevelFreeMarkerTokenId> language() {
        Thread.dumpStack();
        return language;
    }
}
