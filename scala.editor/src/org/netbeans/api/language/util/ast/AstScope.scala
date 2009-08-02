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
import org.netbeans.modules.csl.api.{ElementKind, OffsetRange}

import _root_.scala.collection.mutable.ArrayBuffer

/**
 *
 * @author Caoyuan Deng
 */
object AstScope {
  val EMPTY = new AstScope(Array())
}

class AstScope(var boundsTokens: Array[Token[TokenId]]) {

  var boundsToken: Option[Token[TokenId]] = None
  var boundsEndToken: Option[Token[TokenId]] = None
    
  if (boundsTokens != null) {
    assert(boundsTokens.length <= 2)
    boundsTokens.length match {
      case 0 =>
      case 1 =>
        boundsToken = Some(boundsTokens(0))
      case 2 =>
        boundsToken = Some(boundsTokens(0))
        boundsEndToken = Some(boundsTokens(1))
    }
  }
  
  var bindingDfn: Option[AstDfn] = None
  var parent: Option[AstScope] = None
  private var _subScopes: List[AstScope] = Nil
  private var _dfns: List[AstDfn] = Nil
  private var _refs: List[AstRef] = Nil
  private var scopesSorted: Boolean = false
  private var dfnsSorted: Boolean = false
  private var refsSorted: Boolean = false

  def isRoot = parent match {
    case Some(_) => false
    case None => true
  }

  def isScopesSorted: Boolean = scopesSorted

  def range(th: TokenHierarchy[_]): OffsetRange = {
    new OffsetRange(boundsOffset(th), boundsEndOffset(th))
  }

  def boundsOffset(th: TokenHierarchy[_]): Int = boundsToken match {
    case Some(x) => x.offset(th)
    case None => -1
  }

  def boundsEndOffset(th: TokenHierarchy[_]): Int = boundsEndToken match {
    case Some(x) => x.offset(th) + x.length
    case None => -1
  }
  
  def subScopes: Seq[AstScope] = _subScopes

  def dfns: Seq[AstDfn] = _dfns

  def refs: Seq[AstRef] = _refs

  def addScope(scope: AstScope): Unit = {
    _subScopes = scope :: _subScopes
    scopesSorted = false
    scope.parent = Some(this)
  }

  /**
   * @param dfn to be added
   * @retrun added successfully or not
   */
  def addDfn(dfn: AstDfn): Boolean = {
    dfn.idToken match {
      case Some(x) =>
        /** a def will always be added */
        root.put(x, dfn)
        _dfns = dfn :: _dfns
        dfnsSorted = false
        dfn.enclosingScope = this
        true
      case None => false
    }
  }

  /**
   * @param ref to be added
   * @retrun added successfully or not
   */
  def addRef(ref: AstRef): Boolean = {
    ref.idToken match {
      case Some(x) =>
        /** if a def or ref that corresponds to this idToekn has been added, this ref won't be added */
        if (root.contains(x)) {
          return false
        }

        root.put(x, ref)
        _refs = ref :: _refs
        refsSorted = false
        ref.enclosingScope = this
        true
      case None => false
    }
  }

  def findItemAt(th: TokenHierarchy[_], offset: Int): Option[AstItem] = {
    // Always seach Ref first, since Ref can be included in Def's range
    if (!refsSorted) {
      _refs sort {compareRef(th, _, _)}
      refsSorted = true
    }
    var lo = 0
    var hi = _refs.size - 1
    while (lo <= hi) {
      var mid = (lo + hi) >> 1
      val middle = _refs(mid)
      if (offset < middle.idOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.idEndOffset(th)) {
        lo = mid + 1
      } else {
        return Some(middle)
      }
    }

    if (!dfnsSorted) {
      _dfns sort {compareDfn(th, _, _)}
      dfnsSorted = true
    }
    lo = 0
    hi = _dfns.size - 1
    while (lo <= hi) {
      var mid = (lo + hi) >> 1
      val middle = _dfns(mid)
      if (offset < middle.idOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.idEndOffset(th)) {
        lo = mid + 1
      } else {
        return Some(middle)
      }
    }

    if (!scopesSorted) {
      _subScopes sort {compareScope(th, _, _)}
      scopesSorted = true
    }
    lo = 0
    hi = _subScopes.size - 1
    while (lo <= hi) {
      var mid = (lo + hi) >> 1
      val middle = _subScopes(mid)
      if (offset < middle.boundsOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.boundsEndOffset(th)) {
        lo = mid + 1
      } else {
        return middle.findItemAt(th, offset)
      }
    }

    None
  }

  def findItemAt(th: TokenHierarchy[_], token: Token[TokenId]): Option[AstItem] = {
    val offset = token.offset(th)
    // Always seach Ref first, since Ref can be included in Def's range
    if (!refsSorted) {
      _refs sort {compareRef(th, _, _)}
      refsSorted = true
    }
    var lo = 0
    var hi = _refs.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = _refs(mid)
      if (offset < middle.idOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.idEndOffset(th)) {
        lo = mid + 1
      } else {
        val idToken = middle.idToken
        if (idToken != null && idToken == token) {
          return Some(middle)
        }
      }
    }

    if (!dfnsSorted) {
      _dfns sort {compareDfn(th, _, _)}
      dfnsSorted = true
    }
    lo = 0
    hi = _dfns.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = _dfns(mid)
      if (offset < middle.idOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.idEndOffset(th)) {
        lo = mid + 1
      } else {
        return Some(middle)
      }
    }

    if (!scopesSorted) {
      _subScopes sort {compareScope(th, _, _)}
      scopesSorted = true
    }
    lo = 0
    hi = _subScopes.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = _subScopes(mid)
      if (offset < middle.boundsOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.boundsEndOffset(th)) {
        lo = mid + 1
      } else {
        return middle.findItemAt(th, offset)
      }
    }

    None
  }

  def findDfnAt[A <: AstDfn](clazz: Class[A], th: TokenHierarchy[_], offset: Int): Option[A] = {

    if (!dfnsSorted) {
      _dfns sort {compareDfn(th, _, _)}
      dfnsSorted = true
    }
    var lo = 0
    var hi = _dfns.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = _dfns(mid)
      if (offset < middle.idOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.idEndOffset(th)) {
        lo = mid + 1
      } else {
        return if (clazz isInstance middle) Some(middle.asInstanceOf[A]) else None
      }
    }
    
    if (!scopesSorted) {
      _subScopes sort {compareScope(th, _, _)}
      scopesSorted = true
    }
    lo = 0
    hi = _subScopes.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = _subScopes(mid)
      if (offset < middle.boundsOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.boundsEndOffset(th)) {
        lo = mid + 1
      } else {
        return middle.findDfnAt(clazz, th, offset)
      }
    }
    
    None
  }
    
  def findRefAt[A <: AstRef](clazz: Class[A], th: TokenHierarchy[_], offset: Int): Option[A] = {

    if (!refsSorted) {
      _refs sort {compareRef(th, _, _)}
      refsSorted = true
    }
    var lo = 0
    var hi = _refs.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = _refs(mid)
      if (offset < middle.idOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.idEndOffset(th)) {
        lo = mid + 1
      } else {
        return if (clazz isInstance middle) Some(middle.asInstanceOf[A]) else None
      }
    }
        
    if (!scopesSorted) {
      _subScopes sort {compareScope(th, _, _)}
      scopesSorted = true
    }
    lo = 0
    hi = _subScopes.size - 1
    while (lo <= hi) {
      val mid = (lo + hi) >> 1
      val middle = _subScopes(mid)
      if (offset < middle.boundsOffset(th)) {
        hi = mid - 1
      } else if (offset >= middle.boundsEndOffset(th)) {
        lo = mid + 1
      } else {
        return middle.findRefAt(clazz, th, offset)
      }
    }
    
    None
  }
    
  def findOccurrences(item: AstItem): Seq[AstItem] = {
    var dfn: Option[AstDfn] = item match {
      case x: AstDfn => Some(x)
      case x: AstRef => findDfnOf(x)
    }

    dfn match {
      case Some(x) =>
        val occurrences = new ArrayBuffer[AstItem]
        occurrences + x
        // @todo ArrayBuffer.++ has strange signature: ++[B >: A](that:  Iterable[B]):  ArrayBuffer[B]
        occurrences ++= findRefsOf(x)

        occurrences
      case None =>
        // dfn may be a remote one, just try to find all same refs
        findAllRefsSameAs(item.asInstanceOf[AstRef])
    }
  }

  def findDfnOf(item: AstItem): Option[AstDfn] = {
    item match {
      case dfn:AstDfn => Some(dfn)
      case ref:AstRef => findDfnOf(ref)
    }
  }
  

  private def findDfnOf(ref: AstRef): Option[AstDfn] = {
    ref.enclosingScope match {
      case Some(x) => x.findDfnOfUpward(ref)
      case None => None
    }
  }

  private def findDfnOfUpward(ref: AstRef): Option[AstDfn] = {
    _dfns.find{_ isReferredBy ref} match {
      case Some(x) => return Some(x)
      case None =>
    }

    /** search upward */
    parent match {
      case Some(x) => x.findDfnOfUpward(ref)
      case None => None
    }
  }

  def findRefsOf(dfn: AstDfn): Seq[AstRef] = {
    val result = new ArrayBuffer[AstRef]

    val enclosingScope = dfn.enclosingScope match {
      case Some(x) => x.findRefsOfDownward(dfn, result)
      case None =>
    }
    result
  }

  private def findRefsOfDownward(dfn: AstDfn, result: ArrayBuffer[AstRef]): Unit = {
    // find if there is closest override Def, if so, we shoud bypass it now:
    _dfns.find{x => x != dfn && x.mayEqual(dfn)} match {
      case Some(x) => return
      case None =>
    }

    result ++ _refs.filter{dfn isReferredBy _}

    /** search downward */
    _subScopes.foreach{_.findRefsOfDownward(dfn, result)}
  }

  final def root: AstRootScope = parent match {
    case Some(x) => x.root
    case None => this.asInstanceOf[AstRootScope]
  }

  private def findAllRefsSameAs(ref: AstRef): Seq[AstRef] = {
    val result = new ArrayBuffer[AstRef]

    result + ref
    root.findAllRefsSameAsDownward(ref, result)

    result
  }

  protected def findAllRefsSameAsDownward(ref: AstRef,  result: ArrayBuffer[AstRef]): Unit = {
    result ++ _refs.filter{ref isOccurrence _}

    /** search downward */
    _subScopes.foreach{_.findAllRefsSameAsDownward(ref, result)}
  }

  private def contains(th: TokenHierarchy[_], offset: Int): Boolean = {
    offset >= boundsOffset(th) && offset < boundsEndOffset(th)
  }

  def closestScope(th: TokenHierarchy[_], offset: Int): Option[AstScope] = {
    _subScopes match {
      case Nil if this.contains(th, offset) => Some(this)
        /* we should return None here, since it may under a parent context's call,
         * we shall tell the parent there is none in this and children of this
         */
      case Nil => None
      case _ =>
        /** search children first */
        _subScopes.find{_.contains(th, offset)} match {
          case Some(child) => child.closestScope(th, offset)
          case None => None
        }
    }
  }

  def visibleDfns(kind: ElementKind): Seq[AstDfn] = {
    val result = new ArrayBuffer[AstDfn]
    visibleDfnsUpward(kind, result)
    result
  }

  private def visibleDfnsUpward(kind: ElementKind, result: ArrayBuffer[AstDfn]): Unit = {
    result ++ _dfns.filter{_.getKind == kind}

    parent match {
      case Some(x) => x.visibleDfnsUpward(kind, result)
      case None =>
    }
  }

  def enclosingDfn(kinds: Set[ElementKind], th: TokenHierarchy[_], offset: Int): Option[AstDfn] = {
    closestScope(th, offset) match {
      case Some(x) => x.enclosingDfn(kinds)
      case None => None
    }
  }

  def enclosingDfn(kinds: Set[ElementKind]): Option[AstDfn] = {
    bindingDfn match {
      case Some(x) if kinds.contains(x.getKind) => bindingDfn
      case None => parent match {
          case Some(x) => x.enclosingDfn(kinds)
          case None => None
        }
      case _ => None
    }
  }

  def enclosingDfn(kind: ElementKind, th: TokenHierarchy[_], offset: Int): Option[AstDfn] = {
    closestScope(th, offset) match {
      case Some(x) => x.enclosingDfn(kind)
      case None => None
    }
  }

  def enclosingDfn(kind: ElementKind): Option[AstDfn] = {
    bindingDfn match {
      case Some(x) if x.getKind == kind => bindingDfn
      case None => parent match {
          case Some(x) => x.enclosingDfn(kind)
          case None => None
        }
      case _ => None
    }
  }

  def  visibleDfns[A <: AstDfn](clazz: Class[A]): ArrayBuffer[A] = {
    val result = new ArrayBuffer[A]
    visibleDfnsUpward(clazz, result)
    result
  }
    
  private final def visibleDfnsUpward[A <: AstDfn](clazz: Class[A], result: ArrayBuffer[A]): Unit = {
    result ++ _dfns.filter{clazz isInstance _}
    
    parent match {
      case Some(x) => x.visibleDfnsUpward(clazz, result)
      case None =>
    }
  }
    
  def enclosingDfn[A <: AstDfn](clazz: Class[A], th: TokenHierarchy[_], offset: Int): Option[A]= {
    closestScope(th, offset) match {
      case Some(x) => x.enclosingDfn(clazz)
      case None => None
    }
  }
    
  def enclosingDfn[A <: AstDfn](clazz: Class[A]): Option[A] = bindingDfn match {
    case Some(x) if clazz.isInstance(x) => bindingDfn.asInstanceOf[Option[A]]
    case None => parent match {
        case Some(x) => x.enclosingDfn(clazz)
        case None => None
      }
    case _ => None
  }

  def findDfnMatched(symbol: AnyRef): Option[AstDfn] = {
    val name = symbol.toString
    findDfnMatchedDownside(name, symbol, dfns)
  }

  private def findDfnMatchedDownside(name: String, symbol: AnyRef, dfns: Seq[AstDfn]): Option[AstDfn] = {
    for (dfn <- dfns) {
      val mySymbol = dfn.symbol
      //            if (symbol.isType()) {
      //                // try to avoid cyclic type refenrence or type doesn't exist AsserError from scala's Types
      //                if (ScalaElement.symbolQualifiedName(mySymbol).equals(ScalaElement.symbolQualifiedName(symbol))) {
      //                    return def
      //                }
      //            } else {
      //                Type type = symbol.tpe()
      //                if (isMatched(mySymbol, name, type)) {
      //                    return def
      //                }
      //            }

      val children = dfn.bindingScope.dfns
      val found = findDfnMatchedDownside(name, symbol, children)
      if (found != None) {
        return found
      }
    }

    None
  }

  override def toString = {
    "Scope: (Binding=" + bindingDfn + "," + ",dfns=" + dfns + ",refs=" + refs + ")"
  }

  // ----- compare functions

  private def compareScope(th: TokenHierarchy[_], o1: AstScope, o2: AstScope): Boolean = {
    o1.boundsOffset(th) < o2.boundsOffset(th)
  }

  private def compareDfn(th: TokenHierarchy[_], o1: AstDfn, o2: AstDfn): Boolean = {
    o1.idOffset(th) < o2.idOffset(th)
  }

  private def compareRef(th: TokenHierarchy[_], o1: AstRef, o2: AstRef): Boolean = {
    o1.idOffset(th) < o2.idEndOffset(th)
  }
}
