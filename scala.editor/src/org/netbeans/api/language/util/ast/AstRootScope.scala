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
class AstRootScope[T](boundsTokens: Array[Token[TokenId]]) extends AstScope[T](boundsTokens) {

  protected var _idTokenToItems: Map[Token[TokenId], List[AstItem[T]]] = Map.empty
  private var sortedTokens: List[Token[TokenId]] = Nil
  private var tokensSorted = false

  def contains(idToken: Token[TokenId]): Boolean = _idTokenToItems.contains(idToken)

  def idTokenToItems(th: TokenHierarchy[_]): Map[Token[TokenId], List[AstItem[T]]] = {
    _idTokenToItems
  }

  private def sortedTokens(th: TokenHierarchy[_]): List[Token[TokenId]] = {
    if (!tokensSorted) {
      sortedTokens = _idTokenToItems.keySet.toList sort {compareToken(th, _, _)}
      tokensSorted = true
      sortedTokens
    } else Nil
  }

  /**
   * each idToken may correspond to more then one AstItems
   */
  protected def put(idToken: Token[TokenId], item: AstItem[T]) = {
    _idTokenToItems += idToken -> (item :: _idTokenToItems.getOrElse(idToken, Nil))
    tokensSorted = false
  }

  override def findItemAt(th: TokenHierarchy[_], offset: Int): Option[AstItem[T]] = {
    findItemsAt(th, offset) match {
      case x :: xs => Some(x)
      case _ => None
    }
  }

  def findItemsAt(th: TokenHierarchy[_], offset: Int): List[AstItem[T]] = {
    val tokens1 = sortedTokens(th)

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
        return _idTokenToItems.get(middle) match {
          case Some(x) => x
          case None => Nil
        }
      }
    }

    Nil
  }

  def findItemsAt(token: Token[TokenId]): List[AstItem[T]] = {
    _idTokenToItems.get(token) match {
      case Some(x) => x
      case None => Nil
    }
  }

  def findAllDfnSyms[A <: AstSymbol[T]](clazz: Class[A]): List[A] = {
    findAllDfnsOf(clazz).map(_.symbol).asInstanceOf[List[A]]
  }

  def findAllDfnsOf[A <: AstSymbol[T]](clazz: Class[A]): List[AstDfn[T]] = {
    var result: List[AstDfn[T]] = Nil
    for (items <- _idTokenToItems.valuesIterator;
         item <- items if item.isInstanceOf[AstDfn[T]] && clazz.isInstance(item.symbol)) {
      result = item.asInstanceOf[AstDfn[T]] :: result
    }
    result
  }

  def findFirstItemWithName(name: String): Option[AstItem[T]] = {
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
