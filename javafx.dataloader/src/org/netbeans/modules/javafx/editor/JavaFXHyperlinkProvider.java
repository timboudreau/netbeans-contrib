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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.javafx.editor;

import java.util.EnumSet;
import java.util.Set;
import javax.swing.text.Document;

import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;

import javax.swing.text.Document;

import org.netbeans.api.gsf.OffsetRange;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.javafx.lexer.JavaFXTokenId;

/**
 * Implementation of the hyperlink provider for generic languages.
 * <br>
 * The hyperlinks are constructed for identifiers.
 * <br>
 * The click action corresponds to performing the goto-declaration action.
 *
 * @author Jan Lahoda
 */
public final class JavaFXHyperlinkProvider implements HyperlinkProviderExt {
    /** Creates a new instance of GsfHyperlinkProvider */
    public JavaFXHyperlinkProvider() {
    }

    public Set<HyperlinkType> getSupportedHyperlinkTypes() {
        return EnumSet.of(HyperlinkType.GO_TO_DECLARATION);
    }

    public boolean isHyperlinkPoint(Document doc, int offset, HyperlinkType type) {
        return getHyperlinkSpan(doc, offset, type) != null;
    }

    public int[] getHyperlinkSpan(Document doc, int offset, HyperlinkType type) {
        OffsetRange range = getReferenceSpan(doc, offset);
        if (range != OffsetRange.NONE) {
            return new int[] { range.getStart(), range.getEnd() };
        }
        
        return null;
    }

    public void performClickAction(Document doc, int offset, HyperlinkType type) {
        JavaFXGoToDeclarationAction.gotoDeclaration(doc, offset);
    }

    public String getTooltipText(Document doc, int offset, HyperlinkType type) {
        //return GoToSupport.getGoToElementTooltip(doc, offset);
        return null;
    }
    
    public OffsetRange getReferenceSpan(Document document, int lexOffset) {
        TokenHierarchy<Document> th = TokenHierarchy.get(document);
        
        BaseDocument doc = (BaseDocument)document;
        
        TokenSequence<?extends JavaFXTokenId> ts = th.tokenSequence(JavaFXTokenId.language());

        if (ts == null) {
            return OffsetRange.NONE;
        }

        ts.move(lexOffset);

        if (!ts.moveNext() && !ts.movePrevious()) {
            return OffsetRange.NONE;
        }

        // Determine whether the caret position is right between two tokens
        boolean isBetween = (lexOffset == ts.offset());

        OffsetRange range = getReferenceSpan(ts, th, lexOffset);

        if ((range == OffsetRange.NONE) && isBetween) {
            // The caret is between two tokens, and the token on the right
            // wasn't linkable. Try on the left instead.
            if (ts.movePrevious()) {
                range = getReferenceSpan(ts, th, lexOffset);
            }
        }

        return range;
    }

    private OffsetRange getReferenceSpan(TokenSequence<?> ts,
        TokenHierarchy<Document> th, int lexOffset) {
        Token<?> token = ts.token();
        TokenId id = token.id();

        // TODO: Tokens.SUPER, Tokens.THIS, Tokens.SELF ...
        if (id == JavaFXTokenId.IDENTIFIER) {
            return new OffsetRange(ts.offset(), ts.offset() + token.length());
        }

        // Look for embedded RDoc comments:
        TokenSequence<?> embedded = ts.embedded();

        if (embedded != null) {
            ts = embedded;
            embedded.move(lexOffset);

            if (embedded.moveNext()) {
                Token<?> embeddedToken = embedded.token();

                // etc.) to follow there
                OffsetRange range = getReferenceSpan(embedded, th, lexOffset);

                if (range != OffsetRange.NONE) {
                    return range;
                }
            }
        }

        return OffsetRange.NONE;
    }
}
