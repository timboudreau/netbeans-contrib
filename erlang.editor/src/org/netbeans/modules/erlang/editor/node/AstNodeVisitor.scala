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

import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy,TokenSequence}
import org.netbeans.modules.csl.api.ElementKind

import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstItem,AstRef,AstRootScope,AstScope,AstVisitor}
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId._
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId,LexUtil}
import org.netbeans.modules.erlang.editor.node.ErlSymbols._
import org.openide.filesystems.FileObject

import scala.collection.mutable.{ArrayBuffer,Stack}

import xtc.tree.{GNode,Node}
import xtc.util.Pair



/**
 *
 * @author Caoyuan Deng
 */
class AstNodeVisitor(rootNode:Node, th:TokenHierarchy[_], fo:Option[FileObject]) extends AstVisitor(rootNode, th) {

    private val erlFunctions = new Stack[ErlFunction]
    private val inVarDefs = new Stack[ElementKind]
    private val inFunctionNames = new Stack[Boolean]
    inFunctionNames.push(false)
    private var isFunctionName = false

    override
    def visit(that:GNode) = {
        val forms :Pair[GNode] = that.getList(0)
        eachPair(forms){n =>
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
            case predAttr:GNode if predAttr.getName.equals("PredAttr") =>
                visitPredAttr(predAttr)
            case atomId:GNode =>
                val attr = new AstDfn(idToken(idNode(atomId)), ElementKind.ATTRIBUTE, scope, fo)
                rootScope.addDfn(attr)
            case s:String =>
                // "spec", todo
        }

        scopes.pop
    }

    def visitPredAttr(that:GNode) = {
        val inScope = scopes.top
        val attr = that.getString(0) match {
            case "module" =>
                val atomId1 = that.getGeneric(1)
                val fstTk = idToken(idNode(atomId1))
                val ns :Pair[GNode] = that.getList(2)
                val tks = foldPair(ns){n =>
                    idToken(idNode(n))
                }.toList.reverse

                val (nameTk, pkgPaths) = tks match {
                    case Nil => (fstTk, Nil)
                    case x :: xs => (x, fstTk :: (xs.reverse))
                }
                val attrDfn = new AstDfn(nameTk, ElementKind.MODULE, inScope, fo)
                val erlModule = ErlModule(nameTk.get.text.toString)
                attrDfn.symbol = erlModule
                attrDfn.property("pkg", pkgPaths)
                attrDfn
            case "export" =>
                val functionNames = that.getGeneric(1)
                if (functionNames != null) {
                    val attrDfn = new AstDfn(idToken(idNode(that)), ElementKind.ATTRIBUTE, inScope, fo)
                    val erlFunctions = visitFunctionNames(functionNames)
                    val erlExport = ErlExport(erlFunctions.toList)
                    attrDfn.symbol = erlExport
                    attrDfn
                } else null
            case _ =>
                null
                //new AstDfn(that, idToken(idNode(atomId)), ElementKind.ATTRIBUTE, inScope, fo)
        }
        if (attr != null) rootScope.addDfn(attr)
    }

    def visitFunctionNames(that:GNode) :ArrayBuffer[ErlFunction] = {
        val functionName = that.getGeneric(0)

        val erlFunctions = new ArrayBuffer[ErlFunction]

        val erlFunction = visitFunctionName(functionName)
        erlFunctions + erlFunction

        val ns :Pair[GNode] = that.getList(1)
        erlFunctions ++= foldPair(ns){n =>
            visitFunctionName(n)
        }
        erlFunctions
    }

    def visitFunctionName(that:GNode) :ErlFunction = {
        val arity = that.getGeneric(1)
        val erlFunction = ErlFunction(None, null, arity.getGeneric(0).getString(0).toInt)
        val call = that.getGeneric(0)
        erlFunctions.push(erlFunction)
        call.getName match {
            case "AtomId1" =>
                isFunctionName = true
                visitAtomId1(call)
                isFunctionName = false
            case "MacroId" =>
        }
        erlFunctions.pop
        erlFunction
    }

    def visitFunction(that:GNode) = {
        visitFunctionClauses(that.getGeneric(0))
    }

    def visitFunctionClauses(that:GNode) :ArrayBuffer[AstItem] = {
        val scope = new AstScope(boundsTokens(that))
        rootScope.addScope(scope)
        scopes.push(scope)

        val funClauseDfns = new ArrayBuffer[AstItem]

        val functionClause = that.getGeneric(0)
        val funClauseDfn = visitFunctionClause(functionClause)
        funClauseDfns += funClauseDfn

        val funIdToken = funClauseDfn.idToken
        val arity = funClauseDfn.property("args") match {
            case None => 0
            case Some(x:List[String]) => x.size
        }
        val localName = funIdToken match {
            case None => ""
            case Some(x) => x.text.toString
        }
        val erlFunction = ErlFunction(None, localName, arity)
        val funDfn = new AstDfn(funIdToken, ElementKind.METHOD, scope, fo)
        funDfn.symbol = erlFunction
        rootScope.addDfn(funDfn)

        val ns :Pair[GNode] = that.getList(1)
        funClauseDfns ++= foldPair(ns){n=>
            visitFunctionClause(n)
        }

        scopes.pop
        funClauseDfns
    }

    def visitFunctionClause(that:GNode) :AstDfn = {
        val inScope = scopes.top
        val scope = new AstScope(boundsTokens(that))
        inScope.addScope(scope)
        scopes.push(scope)

        val atomId1 = that.getGeneric(0)
        val dfn = new AstDfn(idToken(idNode(atomId1)), ElementKind.ATTRIBUTE, scope, fo)
        inScope.addDfn(dfn)

        if (that.size == 4) {
            val clauseArgs = that.getGeneric(1)
            inVarDefs.push(ElementKind.PARAMETER)
            val tpes = visitClauseArgs(clauseArgs)
            dfn.property("args", tpes)
            inVarDefs.pop
            val clauseGuard = that.getGeneric(2)
            if (clauseGuard != null) {
                visitClauseGuard(clauseGuard)
            }
            val clauseBody = that.getGeneric(3)
            visitClauseBody(clauseBody)
        }
        
        scopes.pop
        dfn
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

    def visitClauseArgs(that:GNode) :List[String] = {
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

    def visitExpr(that:GNode) :String = {
        val n = that.getGeneric(0)
        n.getName match {
            case "Expr" => visitExpr(n)
            case "Expr100" => visitExpr100(n)
        }
    }

    def visitExpr100(that:GNode) :String = {
        val n = that.getGeneric(0)
        n.getName match {
            case "MatchExpr" => visitMatchExpr(n)
            case "SendExpr" => visitSendExpr(n)
            case "Expr150" => visitExpr150(n)
        }
    }

    def visitMatchExpr(that:GNode) :String = {
        val expr150 = that.getGeneric(0)
        inVarDefs.push(ElementKind.VARIABLE)
        visitExpr150(expr150)
        inVarDefs.pop
        val expr100 = that.getGeneric(1)
        visitExpr100(expr100)
    }

    def visitSendExpr(that:GNode) :String = {
        val expr150 = that.getGeneric(0)
        visitExpr150(expr150)
        val expr100 = that.getGeneric(1)
        visitExpr100(expr100)
        "undefined"
    }

    def visitExpr150(that:GNode) :String = {
        val expr160 = that.getGeneric(0)
        var tpe = visitExpr160(expr160)
        val expr150 = that.getGeneric(1)
        if (expr150 != null) {
            // orelse op
            visitExpr150(expr150)
            tpe = "boolean"
        }
        tpe
    }

    def visitExpr160(that:GNode) :String = {
        val expr200 = that.getGeneric(0)
        var tpe = visitExpr200(expr200)
        val expr160 = that.getGeneric(1)
        if (expr160 != null) {
            // andalso op
            visitExpr160(expr160)
            tpe = "boolean"
        }
        tpe
    }

    def visitExpr200(that:GNode) :String = {
        val expr300 = that.getGeneric(0)
        var tpe = visitExpr300(expr300)
        val expr300_1 = that.getGeneric(1)
        if (expr300_1 != null) {
            // CompOp
            visitExpr300(expr300_1)
            tpe = "boolean"
        }
        tpe
    }

    def visitExpr300(that:GNode) :String = {
        val expr400 = that.getGeneric(0)
        var tpe = visitExpr400(expr400)
        val expr300 = that.getGeneric(1)
        if (expr300 != null) {
            // ListOp
            visitExpr300(expr300)
            tpe = "[...]"
        }
        tpe
    }

    def visitExpr400(that:GNode) :String = {
        val expr500 = that.getGeneric(0)
        var tpe = visitExpr500(expr500)
        val ns :Pair[GNode] = that.getList(1)
        eachPair(ns){n=>
            // AddOp
            tpe = "number"
            visitExpr500(n)
        }
        tpe
    }

    def visitExpr500(that:GNode) :String = {
        val expr600 = that.getGeneric(0)
        var tpe = visitExpr600(expr600)
        val ns :Pair[GNode] = that.getList(1)
        eachPair(ns){n=>
            // MultOp
            tpe = "number"
            visitExpr600(n)
        }
        tpe
    }
    
    def visitExpr600(that:GNode) :String = {
        val prefixOp = that.getGeneric(0)
        val tpe1 = if (prefixOp != null) {
            prefixOp.getString(0) match {
                case "+" | "-" => "number"
                case "bnot" => "int"
                case "not" => "boolean"
            }
        } else ""
        
        val expr700 = that.getGeneric(1)
        val tpe2 = visitExpr700(expr700)
        
        if (prefixOp != null) tpe1 else tpe2
    }

    def visitExpr700(that:GNode) :String = {
        val n = that.getGeneric(0)
        n.getName match {
            case "FunctionCall" =>
                visitFunctionCall(n)
                "..."
            case "RecordExpr" => 
                visitRecordExpr(n)
                "#{...}"
            case "Expr800" => 
                visitExpr800(n)
        }
    }

    def visitExpr800(that:GNode) :String = {
        val expr900 = that.getGeneric(0)
        that.size match {
            case 1 if erlFunctions.isEmpty =>
                // it's a plain expr900
                visitExpr900(expr900)
            case 1 =>
                // it's a local function call name
                inFunctionNames.push(true)
                val tpe = visitExpr900(expr900)
                inFunctionNames.pop
                tpe
            case 2 if erlFunctions.isEmpty =>
                val exprMax = that.getGeneric(1)
                // should not happen? since exprMax is not null, it should be in function call
                visitExpr900(expr900)
                visitExprMax(exprMax)
            case 2 =>
                val exprMax = that.getGeneric(1)
                // in a remote function call
                val erlFunction = erlFunctions.top

                inFunctionNames.push(false)
                val remoteName = visitExpr900(expr900)
                erlFunction.in = Some(remoteName)
                inFunctionNames.pop

                inFunctionNames.push(true)
                val name = visitExprMax(exprMax)
                inFunctionNames.pop
                remoteName + ":" + name
        }
    }

    def visitExpr900(that:GNode) :String = {
        val n = that.getGeneric(0)
        val tpe = n.getName match {
            case "AtomId1" => visitAtomId1(n)
            case "ExprMax" => visitExprMax(n)
        }
        val ns :Pair[GNode] = that.getList(1)
        eachPair(ns){atomId1=>
            visitAtomId1(atomId1)
        }
        tpe
    }

    def visitExprMax(that:GNode) :String = {
        val n = that.getGeneric(0)
        n.getName match {
            case "VarId" => 
                visitVarId(n)
            case "Atomic" => 
                if (inFunctionNames.top) {
                    isFunctionName = true
                }
                val tpe = visitAtomic(n)
                isFunctionName = false
                tpe
            case "List" => visitList(n)
            case "Binary" => 
                visitBinary(n)
                "<<...>>"
            case "ListComprehension" => 
                visitListComprehension(n)
                "[...]"
            case "BinaryComprehension" => 
                visitBinaryComprehension(n)
                "<<...>>"
            case "Tuple" => visitTuple(n)
            case "ParenExpr" => visitParenExpr(n)
            case "BeginExpr" => visitBeginExpr(n)
                "..."
            case "IfExpr" => visitIfExpr(n)
                "..."
            case "CaseExpr" => visitCaseExpr(n)
                "..."
            case "ReceiveExpr" => visitReceiveExpr(n)
                "..."
            case "FunExpr" => visitFunExpr(n)
                "fun"
            case "TryExpr" => visitTryExpr(n)
                "..."
            case "QueryExpr" => visitQueryExpr(n)
                "..."
            case "MacroExpr" => visitMacroExpr(n)
                "..."
        }
    }

    def visitParenExpr(that:GNode) :String = {
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

    def visitList(that:GNode) :String = {
        if (that.size == 2) {
            val expr = that.getGeneric(0)
            val tpe1 = visitExpr(expr)
            val tail = that.getGeneric(1)
            val tpe2 = visitTail(tail)
            "[" + tpe1 + tpe2
        } else "[]"
    }

    def visitTail(that:GNode) :String = that.size match {
        case 0 => "]"
        case 1 =>
            val expr = that.getGeneric(0)
            val tpe = visitExpr(expr)
            "|" + tpe + "]"
        case 2 =>    
            val expr = that.getGeneric(0)
            val tpe1 = visitExpr(expr)
            val tail = that.getGeneric(1)
            val tpe2 = visitTail(tail)
            "," + tpe1 + tpe2
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
        eachPair(ns){n =>
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

    def visitTuple(that:GNode) :String = {
        val exprs = that.getGeneric(0)
        if (exprs != null) {
            val tpes = visitExprs(exprs)
            val sb = new StringBuilder
            sb.append("{")
            val itr = tpes.elements
            while (itr.hasNext) {
                sb.append(itr.next)
                if (itr.hasNext) {
                    sb.append(",")
                }
            }
            sb.append("}")
            sb.toString
        } else "{}"
    }

    def visitRecordExpr(that:GNode) = {}

    def visitRecordTuple(that:GNode) = {}

    def visitRecordFields(that:GNode) = {}

    def visitRecordField(that:GNode) = {}

    def visitFunctionCall(that:GNode) = {
        val expr800 = that.getGeneric(0)
        val erlFunction = ErlFunction(None, null, -1)
        erlFunctions.push(erlFunction)
        visitExpr800(expr800)
        erlFunctions.pop
        val args = that.getGeneric(1)
        val args1 = visitArgumentList(args)
        erlFunction.arity = args1.size
    }

    def visitIfExpr(that:GNode) = {
        val ifClauses = that.getGeneric(0)
        visitIfClauses(ifClauses)
    }

    def visitIfClauses(that:GNode) = {
        val ifClause = that.getGeneric(0)
        visitIfClause(ifClause)
        val ns :Pair[GNode] = that.getList(1)
        eachPair(ns){n=>
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
        eachPair(ns){n=>
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
            val n = that.getGeneric(0)
            n.getName match {
                case "FunctionName" =>
                    visitFunctionName(n)
                case "FunClauses" =>
                    visitFunClauses(n)
            }
        case 2 =>
            val functionName = that.getGeneric(1)
            val erlFunction = visitFunctionName(functionName)

            val remote = that.getGeneric(0)
            remote.getName match {
                case "AtomId1" =>
                    val remoteName = visitAtomId1(remote)
                    erlFunction.in = Some(remoteName)
                case "MacroId" =>
            }
    }

    def visitFunClauses(that:GNode) = {
        val funClause = that.getGeneric(0)
        visitFunClause(funClause)
        val ns :Pair[GNode] = that.getList(1)
        eachPair(ns){n =>
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
        val crClauses = that.getGeneric(1)
        if (crClauses != null) {
            visitCrClauses(crClauses)
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
        eachPair(ns){n =>
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

    def visitArgumentList(that:GNode) :List[String] = {
        val exprs = that.getGeneric(0)
        if (exprs != null) {
            visitExprs(exprs).toList
        } else Nil
    }

    def visitExprs(that:GNode) :ArrayBuffer[String] = {
        val expr = that.getGeneric(0)
        val tpes = new ArrayBuffer[String]
        tpes += visitExpr(expr)
        val ns :Pair[GNode] = that.getList(1)
        eachPair(ns){n =>
            tpes += visitExpr(n)
        }
        tpes
    }

    def visitGuard(that:GNode) = {
        val exprs = that.getGeneric(0)
        visitExprs(exprs)
        val ns :Pair[GNode] = that.getList(1)
        eachPair(ns){n =>
            visitExprs(n)
        }
    }

    def visitAtomic(that:GNode) :String = {
        val n = that.getGeneric(0)
        n.getName match {
            case "AtomId1" => visitAtomId1(n)
            case "Char" => "char"
            case "Float" => "float"
            case "Integer" => "int"
            case "Strings" => "string"
        }
    }

    def visitStrings(that:GNode) = {}

    def visitRule(that:GNode) = {}

    def visitRuleClauses(that:GNode) = {}

    def visitRuleClause(that:GNode) = {}

    def visitRuleBody(that:GNode) = {}

    def visitAtomId1(that:GNode) :String = {
        val inScope = scopes.top
        val idTk = idToken(idNode(that))
        val name = idTk.get.text.toString
        if (isFunctionName) {
            val ref = new AstRef(idTk, ElementKind.CALL)
            inScope.addRef(ref)
            val erlFunction = erlFunctions.top
            erlFunction.name = name
            ref.symbol = erlFunction
        }
        name
    }

    def visitVarId(that:GNode) :String = {
        val inScope = scopes.top
        val idTk = idToken(idNode(that))
        if (inVarDefs.isEmpty) {
            val ref = new AstRef(idTk)
            inScope.addRef(ref)
            idTk.get.text.toString
        } else {
            val dfn = new AstDfn(idTk, inVarDefs.top, new AstScope(boundsTokens(that)), fo)
            inScope.addDfn(dfn)
            idTk.get.text.toString
        }
    }

    /* @Note: bug in scala? when p.head return GNode.fixed1 or etc, f(p.head) will throw ClassCastException
     * You have to explicitly declare the p's type as: Pair[_] (Pair[Any]) before pass it to this function, for example:
     * val p :Pair[_] = gnode.getList(1), or val p = gnode.getList(1).asInstanceOf[Pair[GNode]],
     * a simple val p = that.getList(1) will be inferred as Pair[Nothing]
     */
    def eachPair[A](p:Pair[A])(f:A => Unit) :Unit = p match {
        case Pair.EMPTY =>
        case _ =>
            f(p.head)
            eachPair(p.tail){f}
    }

    def foldPair[A, B](p:Pair[A])(f:A => B) :ArrayBuffer[B] = {
        val acc = new ArrayBuffer[B]
        foldPair(p, acc){f}
    }

    def foldPair[A, B](p:Pair[A], acc:ArrayBuffer[B])(f:A => B) :ArrayBuffer[B] = p match {
        case Pair.EMPTY => acc
        case _ =>
            acc += f(p.head)
            foldPair(p.tail, acc){f}
    }
}
