/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.prado.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.prado.PageLanguage;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Petr Pisl
 */
public enum PageTokenId implements TokenId {

    T_INLINE_HTML(null, "prado"),                   //NOI18N
    T_CONTROL_OPEN_TAG("<%@", "prado-delimiter"),   //NOI18N
    T_CLOSE_TAG("%>", "prado-delimiter"),           //NOI18N
    T_TEMPLATE_CONTROL(null, "prado"),              //NOI18N
    T_PHP(null, "prado");                           //NOI18N
    
    private final String fixedText;
    private final String primaryCategory;

    PageTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }

    public String fixedText() {
        return fixedText;
    }
    
    public String primaryCategory() {
        return primaryCategory;
    }
    private static final Language<PageTokenId> language =
            new LanguageHierarchy<PageTokenId>() {

                @Override
                protected Collection<PageTokenId> createTokenIds() {
                    return EnumSet.allOf(PageTokenId.class);
                }

                @Override
                protected Map<String, Collection<PageTokenId>> createTokenCategories() {
                    Map<String, Collection<PageTokenId>> cats = new HashMap<String, Collection<PageTokenId>>();
                    return cats;
                }

                @Override
                protected Lexer<PageTokenId> createLexer(LexerRestartInfo<PageTokenId> info) {
                    return GSFPageLexer.create(info);
                }

                @Override
                protected String mimeType() {
                    return PageLanguage.PHP_PRADO_MIME_TYPE;
                }
                
                @Override
                protected LanguageEmbedding<?> embedding(Token<PageTokenId> token,
                    LanguagePath languagePath, InputAttributes inputAttributes) {
                    PageTokenId id = token.id();
                    if (id == T_INLINE_HTML) {
                        return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);
                    } 
                    else if (id == T_PHP) {
                        return LanguageEmbedding.create(PHPTokenId.languageInPHP(), 0, 0, true);
                    }
                    else if (id == T_TEMPLATE_CONTROL) {
                        return LanguageEmbedding.create(TemplateControlTokenId.language(), 0, 0, false);
                    }

                    return null; // No embedding
                }
                
            }.language();

    public static Language<PageTokenId> language() {
        System.out.println("PageTokenId.language() created.");
        return language;
    }
}
