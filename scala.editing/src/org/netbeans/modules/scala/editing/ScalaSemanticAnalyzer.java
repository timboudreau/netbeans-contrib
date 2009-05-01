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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.scala.editing.ast.AstDef;
import org.netbeans.modules.scala.editing.ast.AstItem;
import org.netbeans.modules.scala.editing.ast.AstRef;
import org.netbeans.modules.scala.editing.ast.AstRootScope;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import scala.tools.nsc.symtab.Types.ImplicitMethodType;
import scala.tools.nsc.symtab.Types.Type;

/**
 *  
 * @author Caoyuan Deng
 */
public class ScalaSemanticAnalyzer extends SemanticAnalyzer {

    private boolean cancelled;
    private Map<OffsetRange, Set<ColoringAttributes>> semanticHighlights;

    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return semanticHighlights;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
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

    public void run(Parser.Result info, SchedulerEvent event) {
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

        AstRootScope rootScope = pResult.rootScope();
        if (rootScope == null) {
            return;
        }

        final TokenHierarchy th = pResult.getSnapshot().getTokenHierarchy();
        final Document doc = info.getSnapshot().getSource().getDocument(true);
        if (doc == null) {
            return;
        }

        Map<OffsetRange, Set<ColoringAttributes>> highlights = new HashMap<OffsetRange, Set<ColoringAttributes>>(100);
        //visitScopeRecursively(doc, th, rootScope, highlights);
        visitItems(th, rootScope, highlights);

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
    private static Set<ColoringAttributes> IMPLICIT_METHOD = new HashSet();


    {
        IMPLICIT_METHOD.add(ColoringAttributes.INTERFACE);
    }

    private void visitItems(TokenHierarchy th, AstRootScope rootScope, Map<OffsetRange, Set<ColoringAttributes>> highlights) {
        for (AstItem item : rootScope.getIdTokenToItem(th).values()) {
            Token hiToken = item.getIdToken();
            if (hiToken == null) {
                continue;
            }

            // token may be xml tokens, @see AstVisit#getTokenId
            TokenId tid = hiToken.id();
            if (tid != ScalaTokenId.Identifier &&
                    tid != ScalaTokenId.This &&
                    tid != ScalaTokenId.Super) {
                continue;
            }

            String name = item.getName();
            if (name.equals("this") || name.equals("super")) {
                continue;
            }

            OffsetRange hiRange = ScalaLexUtilities.getRangeOfToken(th, hiToken);
            if (item instanceof AstRef) {
                AstRef ref = (AstRef) item;
                switch (ref.getKind()) {
                    case CLASS:
                        highlights.put(hiRange, ColoringAttributes.STATIC_SET);
                        break;
                    case MODULE:
                        highlights.put(hiRange, ColoringAttributes.GLOBAL_SET);
                        break;
                    case METHOD:
                        try {
                            Type tpe = ref.getSymbol().tpe();
                            // @todo doesn't work yet
                            if (tpe instanceof ImplicitMethodType) {
                                highlights.put(hiRange, IMPLICIT_METHOD);
                                break;
                            }
                        } catch (Throwable t) {
                        }

                        final String symbolName = ref.getSymbol().nameString();
                        if (symbolName.equals("apply") || symbolName.startsWith("unapply")) {
                            highlights.put(hiRange, ColoringAttributes.STATIC_SET);
                        } else {
                            highlights.put(hiRange, ColoringAttributes.FIELD_SET);
                        }
                        break;
                    default:
                }
            } else {
                AstDef def = (AstDef) item;
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

        }
    }
}
