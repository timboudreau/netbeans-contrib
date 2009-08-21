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
package org.netbeans.api.language.util.ast

import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy}
import org.netbeans.modules.csl.api.{ElementKind}

/**
 *
 * @author Caoyuan Deng
 */
class AstRootScope(boundsTokens: Array[Token[TokenId]]) extends AstScope(boundsTokens) {

  protected var _idTokenToItems: Map[Token[TokenId], List[AstItem]] = Map.empty
  private var sortedTokens = Array[Token[TokenId]]()
  private var tokensSorted = false

  def contains(idToken: Token[TokenId]): Boolean = _idTokenToItems.contains(idToken)

  def idTokenToItems(th: TokenHierarchy[_]): Map[Token[TokenId], List[AstItem]] = {
    _idTokenToItems
  }

  private def sortedTokens(th: TokenHierarchy[_]): Array[Token[TokenId]] = {
    if (!tokensSorted) {
      sortedTokens = _idTokenToItems.keySet.toArray sortWith {compareToken(th, _, _)}
      tokensSorted = true
    }
    sortedTokens
  }

  /**
   * each idToken may correspond to more then one AstItems
   */
  protected def put(idToken: Token[TokenId], item: AstItem) = {
    _idTokenToItems += idToken -> (item :: _idTokenToItems.getOrElse(idToken, Nil))
    tokensSorted = false
  }

  override def findItemAt(th: TokenHierarchy[_], offset: Int): Option[AstItem] = {
    findItemsAt(th, offset) match {
      case Nil => None
      case xs => Some(xs.reverse.head)
    }
  }

  def findItemsAt(th: TokenHierarchy[_], offset: Int): List[AstItem] = {
    val tokens = sortedTokens(th)

    var lo = 0
    var hi = tokens.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = tokens(mid)
      if (offset < middle.offset(th)) {
        hi = mid - 1
      } else if (offset > middle.offset(th) + middle.length) {
        lo = mid + 1
      } else {
        return _idTokenToItems.get(middle).getOrElse(Nil)
      }
    }

    Nil
  }

  def findNeastItemAt(th: TokenHierarchy[_], offset: Int): Option[AstItem] = {
    val tokens = sortedTokens(th)

    var lo = 0
    var hi = tokens.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = tokens(mid)
      if (offset < middle.offset(th)) {
        hi = mid - 1
      } else if (offset > middle.offset(th) + middle.length) {
        lo = mid + 1
      } else {
        _idTokenToItems.get(middle) match {
          case Some(x :: _) => return Some(x)
          case _ =>
        }
      }
    }

    // * found null, return AstItem at lo, lo is always increasing during above procedure
    if (lo < tokens.size) {
      val neastToken = tokens(lo)
      return _idTokenToItems.get(neastToken) match {
        case Some(x :: _) => Some(x)
        case _ => None
      }
    } else {
      None
    }
  }

  def findItemsAt(token: Token[TokenId]): List[AstItem] = {
    _idTokenToItems.get(token).getOrElse(Nil)
  }

  def findAllDfnSyms[A <: AnyRef](clazz: Class[A]): List[A] = {
    findAllDfnsOf(clazz).map{_.symbol}.asInstanceOf[List[A]]
  }

  def findAllDfnsOf[A <: AnyRef](clazz: Class[A]): List[AstDfn] = {
    var result: List[AstDfn] = Nil
    for (items <- _idTokenToItems.valuesIterator;
         item <- items if item.isInstanceOf[AstDfn] && clazz.isInstance(item.symbol)) {
      result = item.asInstanceOf[AstDfn] :: result
    }
    result
  }

  def findFirstItemWithName(name: String): Option[AstItem] = {
    _idTokenToItems.find{case (token, items) => token.text.toString == name} match {
      case Some((token, x :: xs)) => Some(x)
      case _ => None
    }
  }

  private def compareToken(th: TokenHierarchy[_], o1: Token[TokenId], o2: Token[TokenId]): Boolean = {
    o1.offset(th) < o2.offset(th)
  }

  def debugPrintTokens(th: TokenHierarchy[_]): Unit = {
    sortedTokens(th) foreach {token =>
      println("<" + token + "> ->")
      _idTokenToItems.getOrElse(token, Nil) foreach {println(_)}
      println
    }
    println
  }
}
