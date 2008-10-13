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
package org.netbeans.modules.php.prado.gsf;

import org.netbeans.modules.php.prado.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.ext.html.parser.AstNode;
import org.netbeans.editor.ext.html.parser.AstNodeUtils;
import org.netbeans.editor.ext.html.parser.AstNodeVisitor;
import org.netbeans.editor.ext.html.parser.SyntaxElement;
import org.netbeans.modules.gsf.api.ColoringAttributes;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.SemanticAnalyzer;
import org.netbeans.modules.gsf.api.TranslatedSource;
import org.netbeans.modules.html.editor.gsf.HtmlParserResult;

/**
 *
 * @author Petr Pisl
 */
public class PageSemanticAnalyzer implements SemanticAnalyzer {

    private boolean cancelled;
    private Map<OffsetRange, Set<ColoringAttributes>> semanticHighlights;

    public PageSemanticAnalyzer() {
        semanticHighlights = null;
    }

    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return semanticHighlights;
    }

    public void cancel() {
        cancelled = true;
    }
    private final String componentPrefix = PageLanguage.getComponentPrefix() + ":"; //NOI18N

    public void run(CompilationInfo info) throws Exception {
        resume();

        if (isCancelled()) {
            return;
        }

        ParserResult result = info.getEmbeddedResult(HTMLTokenId.language().mimeType(), 0);
        if (result == null) {
            return;
        }
        HtmlParserResult htmlResult = (HtmlParserResult) result;
        System.out.println("html result: " + htmlResult);
        final TranslatedSource source = htmlResult.getTranslatedSource();
        final BaseDocument document = (BaseDocument) info.getDocument();
        semanticHighlights = new HashMap<OffsetRange, Set<ColoringAttributes>>();
        //the document is touched during the ast tree visiting, we need to lock it
        document.readLock();
        try {
            for (SyntaxElement element : htmlResult.elementsList()) {
                if (element.type() == SyntaxElement.TYPE_TAG || element.type() == SyntaxElement.TYPE_ENDTAG) {
                    SyntaxElement.Tag tag = (SyntaxElement.Tag) element;
                    if (tag.getName().startsWith(componentPrefix)) {
                        int tagStart = tag.offset() + tag.text().indexOf(tag.getName());
                        int startOffest = documentPosition(tagStart, source);
                        int endOffset = documentPosition(tagStart + tag.getName().length(), source);
                        System.out.println(tag.getName() + " [" + startOffest + ", " + endOffset + "]");
                        semanticHighlights.put(new OffsetRange(startOffest, endOffset), ColoringAttributes.CUSTOM1_SET);
                    }
                }
            }
        } finally {
            document.readUnlock();
        }
    }

    protected final synchronized boolean isCancelled() {
        return cancelled;
    }

    protected final synchronized void resume() {
        cancelled = false;
    }

    public static int documentPosition(int astOffset, TranslatedSource source) {
        return source == null ? astOffset : source.getLexicalOffset(astOffset);
    }
}

