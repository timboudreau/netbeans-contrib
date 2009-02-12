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

import scala.collection.mutable.{ArrayBuffer, Stack}

import xtc.tree.GNode
import xtc.tree.Node
import xtc.util.Pair



/**
 *
 * @author Caoyuan Deng
 */
class AstNodeVisitor(rootNode:Node, th:TokenHierarchy[_], fo:FileObject) extends AstVisitor(rootNode, th) {

    private val inVarDefs = new Stack[ElementKind]
    private var inFunctionCallNames = new Stack[Boolean]
    inFunctionCallNames.push(false)
    private var isFunctionCallName = false

    override
    def visit(that:GNode) = {
        val forms :Pair[GNode] = that.getList(0)
        loopPair(forms){n =>
            visitForm(n)
        }
    }

    def visitForm(that:GNode) = {
        enter(that)
        val n = that.getGeneric(0)
        n.getName match {
            case "Attribute" => visitAttribute(n)
            case "Function" => visitFunction(n)
            case "Rule" => visitRule(n)
        }
        exit(that)
    }


    def visitAttribute(that:GNode) = {
        val scope = new AstScope(boundsTokens(that))
        rootScope.addScope(scope)
        scopes.push(scope)

        that.get(0) match {
            case atomId:GNode =>
                val attr = new AstDfn(that, idToken(idNode(atomId)), ElementKind.ATTRIBUTE, scope, fo)
                rootScope.addDfn(attr)
        }

        scopes.pop
    }

    def visitFunction(that:GNode) = {
        visitFunctionClauses(that.getGeneric(0))
    }

    def visitFunctionClauses(that:GNode) = {
        val functionClause = that.getGeneric(0)
        visitFunctionClause(functionClause)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n=>
            visitFunctionClause(n)
        }
    }

    def visitFunctionClause(that:GNode) = {
        val scope = new AstScope(boundsTokens(that))
        rootScope.addScope(scope)
        scopes.push(scope)

        val atomId1 = that.getGeneric(0)
        val fun = new AstDfn(that, idToken(idNode(atomId1)), ElementKind.METHOD, scope, fo)
        rootScope.addDfn(fun)

        val clauseArgs = that.getGeneric(1)
        inVarDefs.push(ElementKind.PARAMETER)
        visitClauseArgs(clauseArgs)
        inVarDefs.pop
        val clauseGuard = that.getGeneric(2)
        if (clauseGuard != null) {
            visitClauseGuard(clauseGuard)
        }
        val clauseBody = that.getGeneric(3)
        visitClauseBody(clauseBody)
        
        scopes.pop
    }

    def visitTypeSpec(that:GNode) = {}

    def visitSpecFun(that:GNode) = {}

    def visitTypedAttrVal(that:GNode) = {}

    def visitTypedRecordFields(that:GNode) = {}

    def visitTypedExprs(that:GNode) = {}

    def visitTypedExpr(that:GNode) = {}

    def visitTypeSigs(that:GNode) = {}

    def visitTypeSig(that:GNode) = {}

    def visitTypeGuards(that:GNode) = {}

    def visitTypeGuard(that:GNode) = {}

    def visitTopTypes(that:GNode) = {}

    def visitTopType(that:GNode) = {}

    def visitTopType100(that:GNode) = {}

    def visitType(that:GNode) = {}

    def visitIntType(that:GNode) = {}

    def visitFunType100(that:GNode) = {}

    def visitFunType(that:GNode) = {}

    def visitFieldTypes(that:GNode) = {}

    def visitFieldType(that:GNode) = {}

    def visitBinaryType(that:GNode) = {}
    
    def visitBinBaseType(that:GNode) = {}

    def visitBinUnitType(that:GNode) = {}

    def visitAttrVal(that:GNode) = {}

    def visitClauseArgs(that:GNode) = {
        val args = that.getGeneric(0)
        visitArgumentList(args)
    }

    def visitClauseGuard(that:GNode) = {
        val guard = that.getGeneric(0)
        visitGuard(guard)
    }

    def visitClauseBody(that:GNode) = {
        val exprs = that.getGeneric(0)
        visitExprs(exprs)
    }

    def visitExpr(that:GNode) :Unit = {
        val n = that.getGeneric(0)
        n.getName match {
            case "Expr" => visitExpr(n)
            case "Expr100" => visitExpr100(n)
        }
    }

    def visitExpr100(that:GNode) :Unit = {
        val expr150 = that.getGeneric(0)
        visitExpr150(expr150)
        if (that.size == 2) {
            val expr100 = that.getGeneric(1)
            visitExpr100(expr100)
        }
    }

    def visitExpr150(that:GNode) :Unit = {
        val expr160 = that.getGeneric(0)
        visitExpr160(expr160)
        val expr150 = that.getGeneric(1)
        if (expr150 != null) {
            // orelse op
            visitExpr150(expr150)
        }
    }

    def visitExpr160(that:GNode) :Unit = {
        val expr200 = that.getGeneric(0)
        visitExpr200(expr200)
        val expr160 = that.getGeneric(1)
        if (expr160 != null) {
            // andalso op
            visitExpr160(expr160)
        }
    }

    def visitExpr200(that:GNode) = {
        val expr300 = that.getGeneric(0)
        visitExpr300(expr300)
        val expr300_1 = that.getGeneric(1)
        if (expr300_1 != null) {
            // CompOp
            visitExpr300(expr300_1)
        }
    }

    def visitExpr300(that:GNode) :Unit = {
        val expr400 = that.getGeneric(0)
        visitExpr400(expr400)
        val expr300 = that.getGeneric(1)
        if (expr300 != null) {
            // ListOp
            visitExpr300(expr300)
        }
    }

    def visitExpr400(that:GNode) :Unit = {
        val expr500 = that.getGeneric(0)
        visitExpr500(expr500)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n=>
            // AddOp
            visitExpr500(n)
        }
    }

    def visitExpr500(that:GNode) :Unit = {
        val expr600 = that.getGeneric(0)
        visitExpr600(expr600)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n=>
            // MultOp
            visitExpr600(n)
        }
    }
    
    def visitExpr600(that:GNode) = {
        val expr700 = that.getGeneric(0)
        visitExpr700(expr700)
    }

    def visitExpr700(that:GNode) = {
        val n = that.getGeneric(0)
        n.getName match {
            case "FunctionCall" => visitFunctionCall(n)
            case "RecordExpr" => visitRecordExpr(n)
            case "Expr800" => visitExpr800(n)
        }
    }

    def visitExpr800(that:GNode) = {
        val expr900 = that.getGeneric(0)
        val exprMax = that.getGeneric(1)
        if (exprMax != null) {
            // * functionCallName is exprMax rather than expr900
            if (inFunctionCallNames.top) {
                inFunctionCallNames.push(false)
                visitExpr900(expr900)
                inFunctionCallNames.pop
            } else {
                visitExpr900(expr900)
            }
            
            visitExprMax(exprMax)
        } else {
            visitExpr900(expr900)
        }
    }

    def visitExpr900(that:GNode) = {
        val n = that.getGeneric(0)
        n.getName match {
            case "AtomId1" => visitAtomId1(n)
            case "ExprMax" => visitExprMax(n)
        }
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){atomId1=>
            visitAtomId1(atomId1)
        }
    }

    def visitExprMax(that:GNode) = {
        val n = that.getGeneric(0)
        n.getName match {
            case "VarId" => visitVarId(n)
            case "Atomic" => 
                if (inFunctionCallNames.top) {
                    isFunctionCallName = true
                }
                visitAtomic(n)
                isFunctionCallName = false
            case "List" => visitList(n)
            case "Binary" => visitBinary(n)
            case "ListComprehension" => visitListComprehension(n)
            case "BinaryComprehension" => visitBinaryComprehension(n)
            case "Tuple" => visitTuple(n)
            case "ParenExpr" => visitParenExpr(n)
            case "BeginExpr" => visitBeginExpr(n)
            case "IfExpr" => visitIfExpr(n)
            case "CaseExpr" => visitCaseExpr(n)
            case "ReceiveExpr" => visitReceiveExpr(n)
            case "FunExpr" => visitFunExpr(n)
            case "TryExpr" => visitTryExpr(n)
            case "QueryExpr" => visitQueryExpr(n)
            case "MacroExpr" => visitMacroExpr(n)
        }
    }

    def visitParenExpr(that:GNode) = {
        val expr = that.getGeneric(0)
        visitExpr(expr)
    }

    def visitBeginExpr(that:GNode) = {
        val exprs = that.getGeneric(0)
        visitExprs(exprs)
    }

    def visitMacroExpr(that:GNode) = {
        val macroId = that.getGeneric(0)
        val exprs = that.getGeneric(1)
        if (exprs != null) {
            visitExprs(exprs)
        }
    }

    def visitList(that:GNode) = {
        if (that.size == 2) {
            val expr = that.getGeneric(0)
            visitExpr(expr)
            val tail = that.getGeneric(1)
            visitTail(tail)
        }
    }

    def visitTail(that:GNode) :Unit = that.size match {
        case 0 =>
        case 1 =>
            val expr = that.getGeneric(0)
            visitExpr(expr)
        case 2 =>    
            val expr = that.getGeneric(0)
            visitExpr(expr)
            val tail = that.getGeneric(1)
            visitTail(tail)
    }

    def visitBinary(that:GNode) = {}

    def visitBinElements(that:GNode) = {}

    def visitBinElement(that:GNode) = {}

    def visitBitExpr(that:GNode) = {}
    def visitOptBitsizeExpr(that:GNode) = {}

    def visitOptBitTypeList(that:GNode) = {}

    def visitBitTypeList(that:GNode) = {}

    def visitBitType(that:GNode) = {}

    def visitBitsizeExpr(that:GNode) = {}

    def visitListComprehension(that:GNode) = {
        val expr = that.getGeneric(0)
        visitExpr(expr)
        val lcExprs = that.getGeneric(1)
        visitLcExprs(lcExprs)
    }

    def visitBinaryComprehension(that:GNode) = {}

    def visitLcExprs(that:GNode) = {
        val lcExpr = that.getGeneric(0)
        visitLcExpr(lcExpr)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n =>
            visitLcExpr(n)
        }
    }

    def visitLcExpr(that:GNode) = {
        val n = that.getGeneric(0)
        inVarDefs.push(ElementKind.VARIABLE)
        n.getName match {
            case "Binary" => visitBinary(n)
            case "Expr" => visitExpr(n)
        }
        inVarDefs.pop
        val expr = that.getGeneric(1)
        if (expr != null) {
            visitExpr(expr)
        }
    }

    def visitTuple(that:GNode) = that.size match {
        case 0 =>
        case 1 => 
            val exprs = that.getGeneric(0)
            visitExprs(exprs)
    }

    def visitRecordExpr(that:GNode) = {}

    def visitRecordTuple(that:GNode) = {}

    def visitRecordFields(that:GNode) = {}

    def visitRecordField(that:GNode) = {}

    def visitFunctionCall(that:GNode) = {
        val expr800 = that.getGeneric(0)
        inFunctionCallNames.push(true)
        visitExpr800(expr800)
        inFunctionCallNames.pop
        val args = that.getGeneric(1)
        visitArgumentList(args)
    }

    def visitIfExpr(that:GNode) = {
        val ifClauses = that.getGeneric(0)
        visitIfClauses(ifClauses)
    }

    def visitIfClauses(that:GNode) = {
        val ifClause = that.getGeneric(0)
        visitIfClause(ifClause)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n=>
            visitIfClause(n)
        }
    }

    def visitIfClause(that:GNode) = {
        val guard = that.getGeneric(0)
        visitGuard(guard)
        val clauseBody = that.getGeneric(1)
        visitClauseBody(clauseBody)
    }

    def visitCaseExpr(that:GNode) = {
        val expr = that.getGeneric(0)
        visitExpr(expr)
        val crClauses = that.getGeneric(1)
        visitCrClauses(crClauses)
    }

    def visitCrClauses(that:GNode) = {
        val crClause = that.getGeneric(0)
        visitCrClause(crClause)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n=>
            visitCrClause(n)
        }
    }

    def visitCrClause(that:GNode) = {
        val expr = that.getGeneric(0)
        inVarDefs.push(ElementKind.VARIABLE)
        visitExpr(expr)
        inVarDefs.pop
        val clauseGuard = that.getGeneric(1)
        if (clauseGuard != null) {
            visitClauseGuard(clauseGuard)
        }
        val clauseBody = that.getGeneric(2)
        visitClauseBody(clauseBody)
    }

    def visitReceiveExpr(that:GNode) = that.size match {
        case 1 =>
            val crClauses = that.getGeneric(0)
            visitCrClauses(crClauses)
        case 2 =>
            val expr = that.getGeneric(0)
            visitExpr(expr)
            val clauseBody = that.getGeneric(1)
            visitClauseBody(clauseBody)
        case 3 =>
            val crClauses = that.getGeneric(0)
            visitCrClauses(crClauses)
            val expr = that.getGeneric(1)
            visitExpr(expr)
            val clauseBody = that.getGeneric(2)
            visitClauseBody(clauseBody)
    }

    def visitFunExpr(that:GNode) = that.size match {
        case 1 =>
            val funClauses = that.getGeneric(0)
            visitFunClauses(funClauses)
        case 2 =>
            val callName = that.getGeneric(0)
            callName.getName match {
                case "AtomId1" =>
                    isFunctionCallName = true
                    visitAtomId1(callName)
                    isFunctionCallName = false
                case "MacroId" =>
            }
            val arity = that.getGeneric(1)
            arity
        case 3 =>
            val remoteName = that.getGeneric(0)
            val callName = that.getGeneric(1)
            callName.getName match {
                case "AtomId1" =>
                    isFunctionCallName = true
                    visitAtomId1(callName)
                    isFunctionCallName = false
                case "MacroId" =>
            }
            val arity = that.getGeneric(2)
            arity
    }

    def visitFunClauses(that:GNode) = {
        val funClause = that.getGeneric(0)
        visitFunClause(funClause)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n =>
            visitFunClause(n)
        }
    }

    def visitFunClause(that:GNode) = {
        val args = that.getGeneric(0)
        visitArgumentList(args)
        val clauseGuard = that.getGeneric(1)
        if (clauseGuard != null) {
            visitClauseGuard(clauseGuard)
        }
        val clauseBody = that.getGeneric(2)
        visitClauseBody(clauseBody)
    }

    def visitTryExpr(that:GNode) = {
        val exprs = that.getGeneric(0)
        visitExprs(exprs)
        val crClause = that.getGeneric(1)
        if (crClause != null) {
            visitCrClause(crClause)
        }
        val tryCatch = that.getGeneric(2)
        visitTryCatch(tryCatch)
    }

    def visitTryCatch(that:GNode) = that.size match {
        case 1 =>
            val exprs = that.getGeneric(0)
            visitExprs(exprs)
        case 2 =>
            val tryClauses = that.getGeneric(0)
            visitTryClauses(tryClauses)
            val exprs = that.getGeneric(1)
            if (exprs != null) {
                visitExprs(exprs)
            }
    }

    def visitTryClauses(that:GNode) = {
        val tryClause = that.getGeneric(0)
        visitTryClause(tryClause)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n =>
            visitTryClause(n)
        }
    }

    def visitTryClause(that:GNode) = that.size match {
        case 3 =>
            val expr = that.getGeneric(0)
            visitExpr(expr)
            val clauseGuard = that.getGeneric(1)
            if (clauseGuard != null) {
                visitClauseGuard(clauseGuard)
            }
            val clauseBody = that.getGeneric(2)
            visitClauseBody(clauseBody)
        case 4 =>
            val id = that.getGeneric(0)
            id.getName match {
                case "AtomId1" => visitAtomId1(id)
                case "VarId" => visitVarId(id)
            }
            val expr = that.getGeneric(1)
            visitExpr(expr)
            val clauseGuard = that.getGeneric(2)
            if (clauseGuard != null) {
                visitClauseGuard(clauseGuard)
            }
            val clauseBody = that.getGeneric(3)
            visitClauseBody(clauseBody)
    }

    def visitQueryExpr(that:GNode) = {
        val lc = that.getGeneric(0)
        visitListComprehension(lc)
    }

    def visitArgumentList(that:GNode) = {
        val exprs = that.getGeneric(0)
        if (exprs != null) {
            visitExprs(exprs)
        }
    }

    def visitExprs(that:GNode) = {
        val expr = that.getGeneric(0)
        visitExpr(expr)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n => 
            visitExpr(n)
        }
    }

    def visitGuard(that:GNode) = {
        val exprs = that.getGeneric(0)
        visitExprs(exprs)
        val ns :Pair[GNode] = that.getList(1)
        loopPair(ns){n =>
            visitExprs(n)
        }
    }

    def visitAtomic(that:GNode) = {
        val n = that.getGeneric(0)
        n.getName match {
            case "AtomId1" => visitAtomId1(n)
            case _ =>
        }
    }

    def visitStrings(that:GNode) = {}

    def visitRule(that:GNode) = {}

    def visitRuleClauses(that:GNode) = {}

    def visitRuleClause(that:GNode) = {}

    def visitRuleBody(that:GNode) = {}

    def visitAtomId1(that:GNode) = {
        if (isFunctionCallName) {
            val ref = new AstRef(that, idToken(idNode(that)), ElementKind.METHOD)
            scopes.top.addRef(ref)
        }
    }

    def visitVarId(that:GNode) = {
        val scope = scopes.top
        val id = idNode(that)
        if (inVarDefs.size > 0) {
            val dfn = new AstDfn(that, idToken(id), inVarDefs.top, new AstScope(boundsTokens(that)), fo)
            scope.addDfn(dfn)
        } else {
            val ref = new AstRef(that, idToken(id))
            scope.addRef(ref)
        }
    }

    /* @Note: bug in scala? when p.head return GNode.fixed1 or etc, f(p.head) will throw ClassCastException
     * You have to explicitly declare the p's type as: Pair[GNode] before pass it to this function, for example:
     * val p :Pair[GNode] = gnode.getList(1), or val p :Pair[GNode] = gnode.getList(1).asInstanceOf[Pair[GNode]],
     * a simple val p = that.getList(1) will be inferred as Pair[Nothing]
     */
    def loopPair[T](p:Pair[T])(f:T => Unit) :Unit = p match {
        case Pair.EMPTY =>
        case _ =>
            f(p.head)
            loopPair(p.tail){f}
    }
}
