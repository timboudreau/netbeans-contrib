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

package org.netbeans.modules.php.prado.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.JTextComponent;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.api.CodeCompletionContext;
import org.netbeans.modules.gsf.api.CodeCompletionHandler;
import org.netbeans.modules.gsf.api.CodeCompletionResult;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.CompletionProposal;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.Index;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.gsf.api.ParameterInfo;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.php.editor.index.IndexedFunction;
import org.netbeans.modules.php.editor.index.PHPIndex;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.parser.PHPParseResult;
import org.netbeans.modules.php.editor.parser.astnodes.BodyDeclaration.Modifier;
import org.netbeans.modules.php.prado.lexer.LexerUtilities;
import org.netbeans.modules.php.prado.lexer.TemplateControlTokenId;

/**
 *
 * @author Petr Pisl
 */
public class PageCodeCompletion implements CodeCompletionHandler {

    

    public CodeCompletionResult complete(CodeCompletionContext context) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (context.getQueryType() != QueryType.COMPLETION) {
            return CodeCompletionResult.NONE;
        }

        List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
        BaseDocument document = (BaseDocument) context.getInfo().getDocument();
        document.readLock();
        int offset = context.getCaretOffset();
        try {
            TokenSequence<TemplateControlTokenId> tokenSequence = LexerUtilities.getTemplateControlTokenSequence(document, offset);
            if (tokenSequence != null) {
                tokenSequence.move(offset);
                if (!tokenSequence.moveNext() && !tokenSequence.movePrevious()){
                    return CodeCompletionResult.NONE;
                }
                Token<TemplateControlTokenId> token = tokenSequence.token();
                boolean whitespace = false;
                while (token != null && token.id() == TemplateControlTokenId.T_WHITESPACE) {
                    token = tokenSequence.movePrevious() ? tokenSequence.token() : null;
                    whitespace = true;
                }
                String prefix = null;
                if (token != null && token.id() == TemplateControlTokenId.T_PROPERTY) {
                    prefix = token.text().toString().substring(0, context.getCaretOffset() - tokenSequence.offset());
                }
                else {
                    if (token == null || (token != null && token.id() == TemplateControlTokenId.T_SEPARATOR)
                            || (token != null && token.id() == TemplateControlTokenId.T_VALUE && whitespace)) {
                        prefix = "";
                    }
                }
                if (prefix != null) {
                    addTemplateProperties(proposals, context, "TPage", prefix);  //NOI18N
                    return new PradoCompletionResult(context, proposals);
                }

            }
        }
        finally {
            document.readUnlock();
        }

        return CodeCompletionResult.NONE;
    }

    public String document(CompilationInfo info, ElementHandle element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ElementHandle resolveLink(String link, ElementHandle originalHandle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPrefix(CompilationInfo info, int caretOffset, boolean upToOffset) {
        // now I compute prefix in the complete methoed, which is wrong.
        return "";
    }

    public QueryType getAutoQuery(JTextComponent component, String typedText) {
        return QueryType.NONE;
    }

    public String resolveTemplateVariable(String variable, CompilationInfo info, int caretOffset, String name, Map parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<String> getApplicableTemplates(CompilationInfo info, int selectionBegin, int selectionEnd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ParameterInfo parameters(CompilationInfo info, int caretOffset, CompletionProposal proposal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addTemplateProperties(final List<CompletionProposal> proposals, final CodeCompletionContext context, final String className, final String prefix) {
        List<String> properties = CompletionUtils.getComponentOrdinalProperties(context.getInfo(), className, prefix, true);
        for (String property : properties) {
            proposals.add(new PradoCompletionItem(context, property, prefix));
        }
    }
}
