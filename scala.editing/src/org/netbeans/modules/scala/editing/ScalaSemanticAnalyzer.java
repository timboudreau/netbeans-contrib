/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.scala.editing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.ColoringAttributes;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.SemanticAnalyzer;
import org.netbeans.modules.scala.editing.ast.AstDef;
import org.netbeans.modules.scala.editing.ast.AstRef;
import org.netbeans.modules.scala.editing.ast.AstRootScope;
import org.netbeans.modules.scala.editing.ast.AstScope;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;

/**
 *  
 * @author Caoyuan Deng
 */
public class ScalaSemanticAnalyzer implements SemanticAnalyzer {

    private boolean cancelled;
    private Map<OffsetRange, Set<ColoringAttributes>> semanticHighlights;

    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return semanticHighlights;
    }

    protected final synchronized boolean isCancelled() {
        return cancelled;
    }

    protected final synchronized void resume() {
        cancelled = false;
    }

    public void cancel() {
        cancelled = true;
    }

    public void run(CompilationInfo info) throws Exception {
        resume();

        if (isCancelled()) {
            return;
        }

        ScalaParserResult pResult = AstUtilities.getParserResult(info);
        if (pResult == null) {
            return;
        }

        if (isCancelled()) {
            return;
        }

        AstRootScope rootScope = pResult.getRootScope();
        if (rootScope == null) {
            return;
        }

        final TokenHierarchy th = pResult.getTokenHierarchy();
        final Document doc = info.getDocument();
        if (doc == null) {
            return;
        }

        Map<OffsetRange, Set<ColoringAttributes>> highlights = new HashMap<OffsetRange, Set<ColoringAttributes>>(100);
        visitScopeRecursively(doc, th, rootScope, highlights);

        if (highlights.size() > 0) {
//            if (result.getTranslatedSource() != null) {
//                Map<OffsetRange, ColoringAttributes> translated = new HashMap<OffsetRange, ColoringAttributes>(2 * highlights.size());
//                for (Map.Entry<OffsetRange, ColoringAttributes> entry : highlights.entrySet()) {
//                    OffsetRange range = LexUtilities.getLexerOffsets(info, entry.getKey());
//                    if (range != OffsetRange.NONE) {
//                        translated.put(range, entry.getValue());
//                    }
//                }
//
//                highlights = translated;
//            }

            this.semanticHighlights = highlights;
        } else {
            this.semanticHighlights = null;
        }
    }

    private void visitScopeRecursively(Document doc, TokenHierarchy th, AstScope scope, Map<OffsetRange, Set<ColoringAttributes>> highlights) {

        for (AstRef ref : scope.getRefs()) {
            Token hiToken = ref.getIdToken();
            if (hiToken == null) {
                continue;
            }

            String name = ref.getName();
            if (name.equals("this") || name.equals("super")) {
                continue;
            }

            OffsetRange hiRange = ScalaLexUtilities.getRangeOfToken(th, hiToken);
            switch (ref.getKind()) {
                case CLASS:
                    highlights.put(hiRange, ColoringAttributes.CLASS_SET);
                    break;
                case MODULE:
                    highlights.put(hiRange, ColoringAttributes.CLASS_SET);
                    break;
                case METHOD:
                    highlights.put(hiRange, ColoringAttributes.FIELD_SET);
                    break;
                default:
            }
        }

        for (AstDef def : scope.getDefs()) {
            Token idToken = def.getIdToken();
            if (idToken == null) {
                continue;
            }

            String name = def.getName();
            if (name.equals("this") || name.equals("super")) {
                continue;
            }

            OffsetRange hiRange = ScalaLexUtilities.getRangeOfToken(th, def.getIdToken());
            switch (def.getKind()) {
                case MODULE:
                    highlights.put(hiRange, ColoringAttributes.CLASS_SET);
                    break;
                case CLASS:
                    highlights.put(hiRange, ColoringAttributes.CLASS_SET);
                    break;
                case METHOD:
                    highlights.put(hiRange, ColoringAttributes.METHOD_SET);
                    break;
//                case FIELD:
//                    highlights.put(idRange, ColoringAttributes.FIELD_SET);
//                    break;
                default:
            }
        }

        for (AstScope child : scope.getSubScopes()) {
            visitScopeRecursively(doc, th, child, highlights);
        }
    }
}
