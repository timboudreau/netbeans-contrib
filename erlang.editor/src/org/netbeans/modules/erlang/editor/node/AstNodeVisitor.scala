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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.editor.node

import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy, TokenSequence}
import org.netbeans.modules.csl.api.ElementKind

import org.netbeans.modules.erlang.editor.ast.{AstDfn, AstItem, AstRef, AstRootScope, AstScope, AstVisitor}
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId._
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId, LexUtil}
import org.openide.filesystems.FileObject

import scala.collection.mutable.ArrayBuffer

import xtc.tree.GNode
import xtc.tree.Node
import xtc.util.Pair



/**
 *
 * @author Caoyuan Deng
 */
class AstNodeVisitor(rootNode:Node, th:TokenHierarchy[_], fo:FileObject) extends AstVisitor(rootNode, th) {

    def visitS(that:GNode) = {
        val formNodes = that.getList(0).iterator
        while(formNodes.hasNext) {
            visitForm(formNodes.next)
        }
    }

    def visitForm(that:GNode) = {
        enter(that)

        val scope = new AstScope(boundsTokens(that))
        rootScope.addScope(scope)

        scopes.push(scope)
        visitNodeOnly(that.getGeneric(0))
        
        exit(that)
        scopes.pop
    }


    def visitAttribute(that:GNode) = {
        that.get(0) match {
            case atomId:GNode =>
                val attr = new AstDfn(that, idToken(idNode(atomId)), scopes.top, ElementKind.ATTRIBUTE, fo)
                rootScope.addDfn(attr)
        }
    }

    def visitFunction(that:GNode) = {
        visitFunctionClauses(that.getGeneric(0))
    }

    def visitFunctionClauses(that:GNode) = {
        val fstClauseNode = that.getGeneric(0)
        visitFunctionClause(fstClauseNode)
    }

    def visitFunctionClause(that:GNode) = {
        val id = idNode(that.getGeneric(0))
        val fun = new AstDfn(that, idToken(id), scopes.top, ElementKind.METHOD, fo)
        rootScope.addDfn(fun)
    }

    def visitRule(that:GNode) = {

    }
}
