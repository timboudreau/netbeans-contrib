/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.antlr.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.CodeCompletionContext;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.CodeCompletionResult;
import org.netbeans.modules.csl.api.CompletionProposal;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.ParameterInfo;
import org.netbeans.modules.csl.spi.DefaultCompletionProposal;
import org.netbeans.modules.csl.spi.DefaultCompletionResult;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileObject;

/**
 *
 * @author marekfukala
 */
public class AntlrCompletionHandler implements CodeCompletionHandler {

    @Override
    public CodeCompletionResult complete(final CodeCompletionContext context) {
        List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
        NbAntlrParserResult result = (NbAntlrParserResult) context.getParserResult();
        final FileObject fileObject = result.getSnapshot().getSource().getFileObject();
        for(final String ruleName : result.getRuleNames()) {
            if(ruleName.toLowerCase().startsWith(context.getPrefix().toLowerCase())) {
                final AntlrElementHandle handle = new AntlrElementHandle(fileObject, ruleName, OffsetRange.NONE);
                proposals.add(new DefaultCompletionProposal() {

                    @Override
                    public int getAnchorOffset() {
                        return context.getCaretOffset() - context.getPrefix().length();
                    }

                    @Override
                    public String getName() {
                        return ruleName;
                    }

                    @Override
                    public ElementKind getKind() {
                        return handle.getKind();
                    }
                    
                    @Override
                    public ElementHandle getElement() {
                        return handle;
                    }
                });
            }
        }
        
        
        return new DefaultCompletionResult(proposals, false);
    }

    @Override
    public String getPrefix(ParserResult info, int caretOffset, boolean upToOffset) {
        NbAntlrParserResult result = (NbAntlrParserResult) info;
        TokenHierarchy<?> th = result.getSnapshot().getTokenHierarchy();
        TokenSequence<AntlrTokenId> ts = th.tokenSequence(AntlrTokenId.language());
        int diff = ts.move(caretOffset);
        if (diff == 0) {
            if (!ts.movePrevious()) {
                return null;
            }
        } else {
            if (!ts.moveNext()) {
                return null;
            }
        }
        Token<AntlrTokenId> curr = ts.token();
        switch(curr.id()) {
            case RULE_REF:
            case TOKEN_REF:
                return diff == 0 
                        ? curr.text().toString() 
                        : ts.token().text().toString().substring(0, diff);
        }
        
        return "";
    }

    @Override
    public String document(ParserResult info, ElementHandle element) {
        return null;
    }

    @Override
    public ElementHandle resolveLink(String link, ElementHandle originalHandle) {
        return null;
    }

    @Override
    public QueryType getAutoQuery(JTextComponent component, String typedText) {
        return QueryType.NONE;
    }

    @Override
    public String resolveTemplateVariable(String variable, ParserResult info, int caretOffset, String name, Map parameters) {
        return null;
    }

    @Override
    public Set<String> getApplicableTemplates(Document doc, int selectionBegin, int selectionEnd) {
        return Collections.emptySet();
    }

    @Override
    public ParameterInfo parameters(ParserResult info, int caretOffset, CompletionProposal proposal) {
        return ParameterInfo.NONE;
    }
    
    
}
