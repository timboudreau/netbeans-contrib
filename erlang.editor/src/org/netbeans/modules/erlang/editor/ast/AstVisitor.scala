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
package org.netbeans.modules.erlang.editor.ast


import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy,TokenSequence}
import xtc.tree.Annotation
import xtc.tree.GNode
import xtc.tree.Location
import xtc.tree.Node
import xtc.tree.Visitor
import xtc.util.Pair

import org.netbeans.modules.erlang.editor.ast._

import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId, LexUtil}
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId._

import scala.collection.mutable.{ArrayBuffer, Stack}

/**
 *
 * @author Caoyuan Deng
 */
abstract class AstVisitor(rootNode:Node, th:TokenHierarchy[_]) extends Visitor {

    val rootScope :AstRootScope = new AstRootScope(boundsTokens(rootNode))

    private var indentLevel :Int = 0
    protected val astPath = new Stack[GNode]
    protected val scopes = new Stack[AstScope]

    scopes += rootScope

    def visit(node:GNode) {
        enter(node)
        visitChildren(node)
        exit(node)
    }

    protected def enter(node:GNode) {
        indentLevel += 1
        astPath.push(node)
    }

    protected def exit(node:GNode) {
        indentLevel -= 1
        astPath.pop
    }

    protected def visitNodeOnly(node:GNode) {
        enter(node)
        dispatch(node)
        exit(node)
    }


    protected def visitChildren(node:GNode) {
        val itr = node.iterator
        while (itr.hasNext) {
            itr.next match {
                case x:GNode => dispatch(x)
                case x:Pair[_] => visitPair(x)
                case _ => 
            }
        }
    }

    private def visitPair(pair:Pair[_]) {
        //println(indent + "[")
        indentLevel += 1
        val itr = pair.iterator
        while (itr.hasNext) {
            itr.next match {
                case x:GNode => dispatch(x)
                case x:Pair[_] => visitPair(x)
                case _ =>
            }
        }
        indentLevel -= 1
        //println(indent + "]")
    }

    override
    def visit(a:Annotation) : Object = {
        println(indent + "@" + a.toString)
        null
    }

    // --- Simple visit functons, which won't do dispatch visit
    
    def simpleVisit(node:GNode) {
        enter(node)
        simpleVisitChildren(node)
        exit(node)
    }


    protected def simpleVisitChildren(node:GNode) {
        val itr = node.iterator
        while (itr.hasNext) {
            itr.next match {
                case x:GNode => simpleVisit(x)
                case x:Pair[_] => simpleVisitPair(x)
                case _ =>
            }
        }
    }

    private def simpleVisitPair(pair:Pair[_]) {
        //println(indent + "[")
        indentLevel += 1
        val itr = pair.iterator
        while (itr.hasNext) {
            itr.next match {
                case x:GNode => simpleVisit(x)
                case x:Pair[_] => simpleVisitPair(x)
                case _ =>
            }
        }
        indentLevel -= 1
        //println(indent + "]")
    }


    // --- Token helpers

    protected def boundsTokens(node:Node) :Array[Token[TokenId]] = {
        val loc = node.getLocation
        val ts = LexUtil.tokenSequence(th, loc.offset).get

        ts.move(loc.offset)
        if (!ts.moveNext && !ts.movePrevious) {
            assert(false, "Should not happen!")
        }

        var startToken = LexUtil.findNextNonWs(ts)
        if (startToken.isFlyweight) {
            startToken = ts.offsetToken
        }

        ts.move(loc.endOffset)
        if (!ts.movePrevious && !ts.moveNext) {
            assert(false, "Should not happen!")
        }
        var endToken = LexUtil.findPreviousNonWs(ts)
        if (endToken.isFlyweight) {
            endToken = ts.offsetToken
        }

        Array(startToken, endToken)
    }

    protected def idNode(that:Node) :Node = that.get(0) match {
        case _:String => that
        case node:Node => idNode(node)
    }

    /**
     * @Note: nameNode may contains preceding void productions, and may also contains
     * following void productions, but nameString has stripped the void productions,
     * so we should adjust nameRange according to name and its length.
     */
    protected def idToken(idNode:Node) :Option[Token[TokenId]] = {
        val loc = idNode.getLocation
        val ts = LexUtil.tokenSequence(th, loc.offset).get
        
        ts.move(loc.offset)
        if (!ts.moveNext && !ts.movePrevious) {
            assert(false, "Should not happen!")
        }

        val token = idNode.getName match {
            case "VarId"     => LexUtil.findNext(ts, ErlangTokenId.Var)
            case "RecId"     => LexUtil.findNext(ts, ErlangTokenId.Rec)
            case "AtomId"    => LexUtil.findNext(ts, ErlangTokenId.Atom)
            case "MacroId"   => LexUtil.findNext(ts, ErlangTokenId.Macro)
            case "PredAttr"  => LexUtil.findNext(ts, ErlangTokenId.Atom)
            case "Attribute" => LexUtil.findNext(ts, ErlangTokenId.Atom)
        }

        token match {
            case null => None
            case x if x.isFlyweight => ts.offsetToken match {
                    case null => None
                    case x1 => Some(x1)
                }
            case x => Some(x)
        }
    }
    
    protected def astPathString :String = {
        val sb = new StringBuilder

        val itr = astPath.elements
        while (itr.hasNext) {
            sb.append(itr.next.getName)
            if (itr.hasNext) {
                sb.append(".")
            }
        }

        sb.toString
    }

    protected def findNearsetNode(name:String) :GNode = {
        var result:GNode = null

        val itr = astPath.elements
        while (itr.hasNext) {
            val node = itr.next
            if (node.getName.equals(name)) {
                result = node
            }
        }

        result
    }

    private def indent :String = {
        val sb = new StringBuilder(indentLevel)
        var i = 0
        while (i < indentLevel) {
            sb.append("  ")
            i += 1
        }
        sb.toString
    }
}
