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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.editor

import javax.swing.text.Document
import org.netbeans.api.lexer.{Token,TokenHierarchy,TokenId,TokenSequence}
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstItem,AstRootScope}
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId,LexUtil}
import org.netbeans.modules.erlang.editor.node.ErlSymbols._
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.csl.api.{ElementKind,DeclarationFinder,OffsetRange}
import org.openide.filesystems.FileObject

/**
 *
 * @author Caoyuan Deng
 */
class ErlangDeclarationFinder extends DeclarationFinder {
    import DeclarationFinder._

    override
    def getReferenceSpan(document:Document, caretOffset:Int) :OffsetRange = {
        val th = TokenHierarchy.get(document) match {
            case null => return OffsetRange.NONE
            case x => x
        }

        val range = for (ts <- LexUtil.tokenSequence(th, caretOffset)) yield {
            ts.move(caretOffset)
            if (!ts.moveNext && !ts.movePrevious) {
                OffsetRange.NONE
            } else {
                // Determine whether the caret position is right between two tokens
                val isBetween = (caretOffset == ts.offset)

                getReferenceSpan(ts, th, caretOffset) match {
                    case OffsetRange.NONE if isBetween && ts.movePrevious =>
                        // The caret is between two tokens, and the token on the right
                        // wasn't linkable. Try on the left instead.
                        getReferenceSpan(ts, th, caretOffset)
                    case x => x
                }
            }
        }

        range match {
            case None => OffsetRange.NONE
            case Some(x) => x
        }
    }

    private def getReferenceSpan(ts:TokenSequence[TokenId], th:TokenHierarchy[_], lexOffset:Int) :OffsetRange = {
        val token = ts.token
        token.id match {
            case ErlangTokenId.Atom | ErlangTokenId.Var | ErlangTokenId.Rec | ErlangTokenId.Macro =>
                LexUtil.rangeOfToken(th, token)
            case _ => OffsetRange.NONE
        }
    }

    override
    def findDeclaration(result:ParserResult, caretOffset:Int) :DeclarationLocation = {
        val pResult = result match {
            case null => return DeclarationLocation.NONE
            case x:ErlangParserResult => x
        }

        val location = for (rootScope <- pResult.rootScope;
                            th <- LexUtil.tokenHierarchy(pResult);
                            closest <- rootScope.findItemAt(th, caretOffset)
        ) yield rootScope.findDfnOf(closest) match {
            case Some(x) =>
                // local
                val offset = x.idOffset(th)
                new DeclarationLocation(x.getFileObject, offset, x)
            case None => closest.symbol match {
                    // search in remote modules
                    case ErlFunction(Some(module), name, arity) =>
                        val index = ErlangIndex.get(pResult)
                        index.queryFunction(module, name, arity) match {
                            case None => DeclarationLocation.NONE
                            case Some(x) => new DeclarationLocation(x.getFileObject, x.idOffset(th), x)
                        }
                    case _ =>  DeclarationLocation.NONE
                }
        }

        location match {
            case None => DeclarationLocation.NONE
            case Some(x) => x
        }
    }
}
