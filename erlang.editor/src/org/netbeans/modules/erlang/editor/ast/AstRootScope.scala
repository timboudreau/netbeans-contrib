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
package org.netbeans.modules.erlang.editor.ast

import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy}
import scala.collection.mutable.{ArrayBuffer, HashMap}

/**
 *
 * @author Caoyuan Deng
 */
class AstRootScope(boundsTokens:Array[Token[TokenId]]) extends AstScope(boundsTokens) {

    private val _idTokenToItem = new HashMap[Token[TokenId], AstItem]
    private var tokens :List[Token[TokenId]] = Nil
    private var tokensSorted :Boolean = false

    def contains(idToken:Token[TokenId]) :Boolean = _idTokenToItem.contains(idToken)

    def idTokenToItem(th:TokenHierarchy[_]) :HashMap[Token[TokenId], AstItem] = {
        if (!tokensSorted) {
            tokens = _idTokenToItem.keySet.toList.sort{compareToken(th, _, _)}
            tokensSorted = true
        }

        _idTokenToItem
    }

    private def sortedToken(th:TokenHierarchy[_]) :List[Token[TokenId]] = {
        if (!tokensSorted) {
            tokens = _idTokenToItem.keySet.toList.sort{compareToken(th, _, _)}
            tokensSorted = true
        }

        tokens
    }

    /**
     * To make sure each idToken only corresponds to one AstItem, if more than
     * one AstItem point to the same idToken, only the first one will be stored
     */
    protected def tryToPut(idToken:Token[TokenId], item:AstItem) :Boolean = _idTokenToItem.get(idToken) match {
        case None =>
            _idTokenToItem + (idToken -> item)
            tokensSorted = false
            true
        case Some(exsitOne) =>
            // if existOne is dfn and with narrow visible than new one, replace it
            if (item.isInstanceOf[AstDfn]) {
                _idTokenToItem + (idToken -> item)
                tokensSorted = false
                true
            } else false
    }
   

    override
    def findItemAt(th:TokenHierarchy[_], offset:Int) :Option[AstItem] = {
        val tokens1 = sortedToken(th)

        var lo = 0
        var hi = tokens1.size - 1
        while (lo <= hi) {
            val mid = (lo + hi) >> 1
            val middle = tokens1(mid)
            if (offset < middle.offset(th)) {
                hi = mid - 1
            } else if (offset > middle.offset(th) + middle.length) {
                lo = mid + 1
            } else {
                return _idTokenToItem.get(middle)
            }
        }

        None
    }

    def findItemAt(token:Token[TokenId]) :Option[AstItem] = _idTokenToItem.get(token)

    def findFirstItemWithName(name:String) :Option[AstItem] = {
        _idTokenToItem.find{k => k._1.text.toString.equals(name)} match {
            case None => None
            case Some(x) => Some(x._2)
        }
    }

    protected def debugPrintTokens(th:TokenHierarchy[_]) :Unit = {
        sortedToken(th).foreach{token => println("AstItem: " + _idTokenToItem.get(token))}
    }

    private def compareToken(th:TokenHierarchy[_], o1:Token[TokenId], o2:Token[TokenId]) :Boolean = {
        o1.offset(th) < o2.offset(th)
    }
}
