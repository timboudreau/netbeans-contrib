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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.editor

import _root_.java.util.{HashMap,HashSet,Map,Set}
import javax.swing.text.Document
import org.netbeans.api.lexer.Token
import org.netbeans.api.lexer.TokenHierarchy
import org.netbeans.api.lexer.TokenId
import org.netbeans.modules.csl.api.{ColoringAttributes,ElementKind,OffsetRange,SemanticAnalyzer}
import org.netbeans.modules.parsing.spi.Parser
import org.netbeans.modules.parsing.spi.{Scheduler,SchedulerEvent,ParserResultTask}
import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstItem,AstRef,AstRootScope}
import org.netbeans.modules.erlang.editor.lexer.LexUtil
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId

/**
 *
 * @author Caoyuan Deng
 */
class ErlangSemanticAnalyzer extends SemanticAnalyzer[ErlangParserResult] {

    private var cancelled = false
    private var semanticHighlights :Map[OffsetRange, Set[ColoringAttributes]] = _

    protected def isCancelled :Boolean = synchronized {cancelled}

    protected def resume :Unit = synchronized {cancelled = false}

    override
    def getHighlights :Map[OffsetRange, Set[ColoringAttributes]] = semanticHighlights

    override
    def getPriority = 0

    override
    def getSchedulerClass = Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER
    
    override
    def cancel :Unit = {cancelled = true}

    @throws(classOf[Exception])
    override
    def run(pResult:ErlangParserResult, event:SchedulerEvent) :Unit = {
        resume
        semanticHighlights = null

        if (pResult == null || isCancelled) {
            return
        }

        for (rootScope <- pResult.rootScope;
             th <- LexUtil.tokenHierarchy(pResult);
             doc <- LexUtil.document(pResult, true)
        ) {
            var highlights = new HashMap[OffsetRange, Set[ColoringAttributes]](100)
            visitItems(th.asInstanceOf[TokenHierarchy[TokenId]], rootScope, highlights)

            this.semanticHighlights = if (highlights.size > 0) highlights else null
        }
    }

    private def visitItems(th:TokenHierarchy[TokenId], rootScope:AstRootScope, highlights:Map[OffsetRange, Set[ColoringAttributes]]) :Unit = {
        import ElementKind._
        for (item <- rootScope.idTokenToItem(th).values;
             hiToken <- item.idToken
        ) {
            
            val hiRange = LexUtil.rangeOfToken(th, hiToken.asInstanceOf[Token[TokenId]])
            item match {
                case dfn:AstDfn => dfn.getKind match {
                        case MODULE =>
                            highlights.put(hiRange, ColoringAttributes.CLASS_SET)
                        case CLASS =>
                            highlights.put(hiRange, ColoringAttributes.CLASS_SET)
                        case ATTRIBUTE =>
                            highlights.put(hiRange, ColoringAttributes.STATIC_SET)
                        case METHOD =>
                            highlights.put(hiRange, ColoringAttributes.METHOD_SET)
                        case PARAMETER =>
                            highlights.put(hiRange, ColoringAttributes.PARAMETER_SET)
                        case _ =>
                    }
                case ref:AstRef => ref.getKind match {
                        case METHOD =>
                            highlights.put(hiRange, ColoringAttributes.FIELD_SET)
                        case PARAMETER =>
                            highlights.put(hiRange, ColoringAttributes.PARAMETER_SET)
                        case _ =>
                    }
            }
        }
    }
}