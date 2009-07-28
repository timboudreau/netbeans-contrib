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
import org.netbeans.modules.scala.editor.util.Sorter

import _root_.scala.collection.mutable.ArrayBuffer

import xtc.tree.{GNode}


/**
 *
 * @author Caoyuan Deng
 */
object AstScope {
  // * Sinleton EmptyScope
  val EMPTY_SCOPE = new AstScope(Array())
}

class AstScope[T](var boundsTokens:Array[Token[TokenId]]) {

  var boundsToken :Option[Token[TokenId]] = None
  var boundsEndToken :Option[Token[TokenId]] = None
    
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
  
  var bindingDfn :Option[AstDfn[T]] = None
  var parent :Option[AstScope[T]] = None
  private var _subScopes :List[AstScope[T]] = Nil
  private var _dfns :List[AstDfn[T]] = Nil
  private var _refs :List[AstRef[T]] = Nil
  private var scopesSorted :Boolean = false
  private var dfnsSorted :Boolean = false
  private var refsSorted :Boolean = false

  def isRoot = parent match {
    case None => true
    case Some(_) => false
  }

  def isScopesSorted :Boolean = scopesSorted

  def range(th:TokenHierarchy[_]) :OffsetRange = {
    new OffsetRange(boundsOffset(th), boundsEndOffset(th))
  }

  def boundsOffset(th:TokenHierarchy[_]) :Int = boundsToken match {
    case None => -1
    case Some(x) => x.offset(th)
  }

  def boundsEndOffset(th:TokenHierarchy[_]) :Int = boundsEndToken match {
    case None => -1
    case Some(x) => x.offset(th) + x.length
  }
  
  def subScopes :Seq[AstScope[T]] = _subScopes

  def dfns :Seq[AstDfn[T]] = _dfns

  def refs :Seq[AstRef[T]] = _refs

  def addScope(scope:AstScope[T]) :Unit = {
    _subScopes = scope :: _subScopes
    scopesSorted = false
    scope.parent = Some(this)
  }

  /**
   * @param dfn to be added
   * @retrun added successfully or not
   */
  def addDfn(dfn:AstDfn[T]) :Boolean = {
    dfn.idToken match {
      case None => false
      case Some(x) =>
        /** a def will always be added */
        root.tryToPut(x, dfn)
        _dfns = dfn :: _dfns
        dfnsSorted = false
        dfn.enclosingScope = this
        true
    }
  }

  /**
   * @param ref to be added
   * @retrun added successfully or not
   */
  def addRef(ref:AstRef[T]) :Boolean = {
    ref.idToken match {
      case None => false
      case Some(x) =>
        /** if a def or ref that corresponds to this idToekn has been added, this ref won't be added */
        if (root.contains(x)) {
          return false
        }

        root.tryToPut(x, ref)
        _refs = ref :: _refs
        refsSorted = false
        ref.enclosingScope = this
        true
    }
  }

  def findItemAt(th:TokenHierarchy[_], offset:Int) :Option[AstItem[T]] = {
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

  def findItemAt(th:TokenHierarchy[_], token:Token[TokenId]) :Option[AstItem[T]] = {
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

  def findDfnAt[A <: AstDfn[T]](clazz:Class[A], th:TokenHierarchy[_], offset:Int) :Option[A] = {

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
    
  def findRefAt[A <: AstRef[T]](clazz:Class[A], th:TokenHierarchy[_], offset:Int) :Option[A] = {

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
    
  def findOccurrences(item:AstItem[T]) :Seq[AstItem[T]] = {
    var dfn :Option[AstDfn[T]] = item match {
      case x:AstDfn[T] => Some(x)
      case x:AstRef[T] => findDfnOf(x)
    }

    dfn match {
      case None =>
        // dfn may be a remote one, just try to find all same refs
        findAllRefsSameAs(item.asInstanceOf[AstRef[T]])
      case Some(x) =>
        val occurrences = new ArrayBuffer[AstItem[T]]
        occurrences + x
        // @todo ArrayBuffer.++ has strange signature: ++[B >: A](that : Iterable[B]) : ArrayBuffer[B]
        occurrences ++= findRefsOf(x)

        occurrences
    }
  }

  def findDfnOf(item:AstItem[T]) :Option[AstDfn[T]] = item match {
    case dfn:AstDfn[T] => Some(dfn)
    case ref:AstRef[T] => findDfnOf(ref)
  }
  

  private def findDfnOf(ref:AstRef[T]) :Option[AstDfn[T]] = {
    ref.enclosingScope match {
      case None => None
      case Some(x) => x.findDfnOfUpward(ref)
    }
  }

  private def findDfnOfUpward(aRef:AstRef[T]) :Option[AstDfn[T]] = {
    _dfns.find{_ isReferredBy aRef} match {
      case None =>
      case Some(x) => return Some(x)
    }

    /** search upward */
    parent match {
      case None => None
      case Some(x) => x.findDfnOfUpward(aRef)
    }
  }

  def findRefsOf(dfn:AstDfn[T]) :Seq[AstRef[T]] = {
    val result = new ArrayBuffer[AstRef[T]]

    val enclosingScope = dfn.enclosingScope match {
      case None =>
      case Some(x) => x.findRefsOfDownward(dfn, result)
    }
    result
  }

  private def findRefsOfDownward(dfn:AstDfn[T], result:ArrayBuffer[AstRef[T]]) :Unit = {
    // find if there is closest override Def, if so, we shoud bypass it now:
    _dfns.find{x => x != dfn && x.mayEqual(dfn)} match {
      case None =>
      case Some(x) => return
    }

    result ++ _refs.filter{dfn isReferredBy _}

    /** search downward */
    _subScopes.foreach{_.findRefsOfDownward(dfn, result)}
  }

  final def root :AstRootScope[T] = parent match {
    case None => this.asInstanceOf[AstRootScope[T]]
    case Some(x) => x.root
  }

  private def findAllRefsSameAs(ref:AstRef[T]) :Seq[AstRef[T]] = {
    val result = new ArrayBuffer[AstRef[T]]

    result + ref
    root.findAllRefsSameAsDownward(ref, result)

    result
  }

  protected def findAllRefsSameAsDownward(ref:AstRef[T],  result:ArrayBuffer[AstRef[T]]) :Unit = {
    result ++ _refs.filter{ref isOccurrence _}

    /** search downward */
    _subScopes.foreach{_.findAllRefsSameAsDownward(ref, result)}
  }

  private def contains(th:TokenHierarchy[_], offset:Int) :Boolean = {
    offset >= boundsOffset(th) && offset < boundsEndOffset(th)
  }

  def closestScope(th:TokenHierarchy[_], offset:Int) :Option[AstScope[T]] = {
    _subScopes match {
      case Nil if this.contains(th, offset) => Some(this)
        /* we should return None here, since it may under a parent context's call,
         * we shall tell the parent there is none in this and children of this
         */
      case Nil => None
      case _ =>
        /** search children first */
        _subScopes.find{_.contains(th, offset)} match {
          case None => None
          case Some(child) => child.closestScope(th, offset)
        }
    }
  }

  def visibleDfns(kind:ElementKind) :Seq[AstDfn[T]] = {
    val result = new ArrayBuffer[AstDfn[T]]
    visibleDfnsUpward(kind, result)
    result
  }

  private def visibleDfnsUpward(kind:ElementKind, result:ArrayBuffer[AstDfn[T]]) :Unit = {
    result ++ _dfns.filter{_.getKind == kind}

    parent match {
      case None =>
      case Some(x) => x.visibleDfnsUpward(kind, result)
    }
  }

  def enclosingDfn(kinds:Set[ElementKind], th:TokenHierarchy[_], offset:Int) :Option[AstDfn[T]] = {
    closestScope(th, offset) match {
      case None => None
      case Some(x) => x.enclosingDfn(kinds)
    }
  }

  def enclosingDfn(kinds:Set[ElementKind]) :Option[AstDfn[T]] = {
    bindingDfn match {
      case None => parent match {
          case None => None
          case Some(x) => x.enclosingDfn(kinds)
        }
      case Some(x) if kinds.contains(x.getKind) => bindingDfn
      case _ => None
    }
  }

  def enclosingDfn(kind:ElementKind, th:TokenHierarchy[_], offset:Int) :Option[AstDfn[T]] = {
    closestScope(th, offset) match {
      case None => None
      case Some(x) => x.enclosingDfn(kind)
    }
  }

  def enclosingDfn(kind:ElementKind) :Option[AstDfn[T]] = {
    bindingDfn match {
      case None => parent match {
          case None => None
          case Some(x) => x.enclosingDfn(kind)
        }
      case Some(x) if x.getKind == kind => bindingDfn
      case _ => None
    }
  }

  def  visibleDfns[A <: AstDfn[T]](clazz:Class[A]) :ArrayBuffer[A] = {
    val result = new ArrayBuffer[A]
    visibleDfnsUpward(clazz, result)
    result
  }
    
  private final def visibleDfnsUpward[A <: AstDfn[T]](clazz:Class[A], result:ArrayBuffer[A]) :Unit = {
    result ++ _dfns.filter{clazz isInstance _}
    
    parent match {
      case None =>
      case Some(x) => x.visibleDfnsUpward(clazz, result)
    }
  }
    
  def enclosingDfn[A <: AstDfn[T]](clazz:Class[A], th:TokenHierarchy[_], offset:Int) :Option[A]= {
    closestScope(th, offset) match {
      case None => None
      case Some(x) => x.enclosingDfn(clazz)
    }
  }
    
  def enclosingDfn[A <: AstDfn[T]](clazz:Class[A]) :Option[A] = bindingDfn match {
    case None => parent match {
        case None => None
        case Some(x) => x.enclosingDfn(clazz)
      }
    case Some(x) if clazz.isInstance(x) => bindingDfn.asInstanceOf[Option[A]]
    case _ => None
  }

  def findDfnMatched(symbol:GNode) :Option[AstDfn[T]] = {
    val name = symbol.toString
    findDfnMatchedDownside(name, symbol, dfns)
  }

  private def findDfnMatchedDownside(name:String, symbol:GNode, dfns:Seq[AstDfn[T]]) :Option[AstDfn[T]] = {
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

  override def toString() = {
    "Scope: (Binding=" + bindingDfn + "," + ",dfns=" + dfns + ",refs=" + refs + ")"
  }

  // ----- compare functions

  private def compareScope(th:TokenHierarchy[_], o1:AstScope[T], o2:AstScope[T]) :Boolean = {
    o1.boundsOffset(th) < o2.boundsOffset(th)
  }

  private def compareDfn(th:TokenHierarchy[_], o1:AstDfn[T], o2:AstDfn[T]) :Boolean = {
    o1.idOffset(th) < o2.idOffset(th)
  }

  private def compareRef(th:TokenHierarchy[_], o1:AstRef[T], o2:AstRef[T]) :Boolean = {
    o1.idOffset(th) < o2.idEndOffset(th)
  }
}



