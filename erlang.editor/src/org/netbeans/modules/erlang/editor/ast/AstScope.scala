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

import com.ericsson.otp.erlang.OtpErlangObject
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy}
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.erlang.editor.util.Sorter

import scala.collection.mutable.ArrayBuffer

/**
 *
 * @author Caoyuan Deng
 */
class AstScope(boundsTokens:Array[Token[TokenId]]) {

    if (boundsTokens != null) {
        assert(boundsTokens.length <= 2)
        boundsTokens.length match {
            case 1 =>
                boundsToken = Some(boundsTokens(0))
            case 2 =>
                boundsToken = Some(boundsTokens(0))
                boundsEndToken = Some(boundsTokens(1))
        }
    }

    var boundsToken :Option[Token[TokenId]] = None
    var boundsEndToken :Option[Token[TokenId]] = None
    var bindingDef :Option[AstDef] = None
    var parent :Option[AstScope] = None
    private var _subScopes :Option[ArrayBuffer[AstScope]] = None
    private var _defs :Option[ArrayBuffer[AstDef]] = None
    private var _refs :Option[ArrayBuffer[AstRef]] = None
    private var scopesSorted :Boolean = false
    private var defsSorted :Boolean = false
    private var refsSorted :Boolean = false

    def isRoot = parent match {
        case None => true
        case Some(_) => false
    }

    def isScopesSorted :Boolean = scopesSorted

    def range(th:TokenHierarchy[TokenId]) :OffsetRange = {
        new OffsetRange(boundsOffset(th), boundsEndOffset(th))
    }

    def boundsOffset(th:TokenHierarchy[TokenId]) :Int = boundsToken match {
        case None => -1
        case Some(x) => x.offset(th)
    }

    def boundsEndOffset(th:TokenHierarchy[TokenId]) :Int = boundsEndToken match {
        case None => -1
        case Some(x) => x.offset(th) + x.length
    }
  
    def subScopes :ArrayBuffer[AstScope] = _subScopes match {
        case None => new ArrayBuffer
        case Some(x) => x
    }

    def defs :ArrayBuffer[AstDef] = _defs match {
        case None => new ArrayBuffer
        case Some(x) => x
    }

    def refs :ArrayBuffer[AstRef] = _refs match {
        case None => new ArrayBuffer
        case Some(x) => x
    }

    protected def addScope(scope:AstScope) :Unit = {
        if (_subScopes == None) {
            _subScopes = Some(new ArrayBuffer)
        }
        _subScopes.get += scope
        scopesSorted = false
        scope.parent = Some(this)
    }

    /**
     * @param def to be added
     * @retrun added successfully or not
     */
    protected def addDef(adef:AstDef) :Boolean = adef.idToken match {
        case None => false
        case Some(x) =>
            /** @todo tempary solution */
            //        if (!ScalaLexUtilities.isProperIdToken(idToken.id())) {
            //            return false
            //        }

            /** a def will always be added */
            root.tryToPut(x, adef)
            if (_defs == None) {
                _defs = Some(new ArrayBuffer)
            }
            _defs.get += adef
            defsSorted = false
            adef.enclosingScope = this
            true
    }

    /**
     * @param ref to be added
     * @retrun added successfully or not
     */
    def addRef(ref:AstRef) :Boolean = ref.idToken match {
        case None => false
        case Some(x) =>
            /** @todo tempary solution */
            //        if (!ScalaLexUtilities.isProperIdToken(idToken.id())) {
            //            return false;
            //        }

            /** if a def or ref that corresponds to this idToekn has been added, this ref won't be added */
            if (root.contains(x)) {
                return false
            }

            root.tryToPut(x, ref)
            if (_refs == None) {
                _refs = Some(new ArrayBuffer)
            }
            _refs.get += ref
            refsSorted = false
            ref.enclosingScope = this
            true
    }

    def findItemAt(th:TokenHierarchy[TokenId], offset:Int) :Option[AstItem] = {
        // Always seach Ref first, since Ref can be included in Def's range
        for (xs <- _refs) {
            if (!refsSorted) {
                Sorter.sort(xs){compareRef(th, _, _)}
                refsSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                var mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.idOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.idEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return Some(middle)
                }
            }
        }

        for (xs <- _defs) {
            if (!defsSorted) {
                Sorter.sort(xs){compareDef(th, _, _)}
                defsSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                var mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.idOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.idEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return Some(middle)
                }
            }
        }

        for (xs <- _subScopes) {
            if (!scopesSorted) {
                Sorter.sort(xs){compareScope(th, _, _)}
                scopesSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                var mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.boundsOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.boundsEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return middle.findItemAt(th, offset)
                }
            }
        }

        None
    }

    def findItemAt(th:TokenHierarchy[TokenId], token:Token[TokenId]) :Option[AstItem] = {
        val offset = token.offset(th)
        // Always seach Ref first, since Ref can be included in Def's range
        for (xs <- _refs) {
            if (!refsSorted) {
                Sorter.sort(xs){compareRef(th, _, _)}
                refsSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = xs(mid)
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
        }

        for (xs <- _defs) {
            if (!defsSorted) {
                Sorter.sort(xs){compareDef(th, _, _)}
                defsSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.idOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.idEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return Some(middle)
                }
            }
        }

        for (xs <- _subScopes) {
            if (!scopesSorted) {
                Sorter.sort(xs){compareScope(th, _, _)}
                scopesSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.boundsOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.boundsEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return middle.findItemAt(th, offset)
                }
            }
        }

        return None
    }

    def findDefAt[T <: AstDef](clazz:Class[T], th:TokenHierarchy[TokenId], offset:Int) :Option[T] = {

        for (xs <- _defs) {
            if (!defsSorted) {
                Sorter.sort(xs){compareDef(th, _, _)}
                defsSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.idOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.idEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return if (clazz isInstance middle) Some(middle.asInstanceOf[T]) else None
                }
            }
        }
    
        for (xs <- _subScopes) {
            if (!scopesSorted) {
                Sorter.sort(xs){compareScope(th, _, _)}
                scopesSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.boundsOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.boundsEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return middle.findDefAt(clazz, th, offset)
                }
            }
        }
    
        None
    }
    
    def findRefAt[T <: AstRef](clazz:Class[T], th:TokenHierarchy[TokenId], offset:Int) :Option[T] = {

        for (xs <- _refs) {
            if (!refsSorted) {
                Sorter.sort(xs){compareRef(th, _, _)}
                refsSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.idOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.idEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return if (clazz isInstance middle) Some(middle.asInstanceOf[T]) else None
                }
            }
        }
        
        for (xs <- _subScopes) {
            if (!scopesSorted) {
                Sorter.sort(xs){compareScope(th, _, _)}
                scopesSorted = true
            }
            var lo = 0
            var hi = xs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = xs(mid)
                if (offset < middle.boundsOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.boundsEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return middle.findRefAt(clazz, th, offset)
                }
            }
        }
    
        None
    }
    
    def findOccurrences(item:AstItem) :ArrayBuffer[AstItem] = {
        var aDef :Option[AstDef] = item match {
            case x:AstDef => Some(x)
            case x:AstRef => findDefOf(x)
        }

        aDef match {
            case None =>
                // def maybe remote one, just try to find all same refs
                findAllRefsSameAs(item.asInstanceOf[AstRef]).asInstanceOf[ArrayBuffer[AstItem]]
            case _ =>
                val occurrences = new ArrayBuffer[AstItem]
                occurrences += aDef.get
                occurrences ++= findRefsOf(aDef.get)

                occurrences
        }
    }

    def findDefOf(item:AstItem) :Option[AstDef] = item match {
        case aDef:AstDef => Some(aDef)
        case aRef:AstRef => findDefOf(aRef)
    }
  

    private def findDefOf(aRef:AstRef) :Option[AstDef] = aRef.enclosingScope match {
        case None => None
        case Some(x) => x.findDefOfUpward(aRef)
    }

    private def findDefOfUpward(aRef:AstRef) :Option[AstDef] = {
        for (xs <- _defs) {
            xs.find{_ isReferredBy aRef} match {
                case None =>
                case Some(x) => return Some(x)
            }
        }

        /** search upward */
        parent match {
            case None => None
            case Some(x) => x.findDefOfUpward(aRef)
        }
    }

    def findRefsOf(aDef:AstDef) :ArrayBuffer[AstRef] = {
        val result = new ArrayBuffer[AstRef]

        val enclosingScope = aDef.enclosingScope match {
            case None =>
            case Some(x) => x.findRefsOfDownward(aDef, result)
        }
        result
    }

    private def findRefsOfDownward(aDef:AstDef, result:ArrayBuffer[AstRef]) :Unit = {
        // find if there is closest override Def, if so, we shoud bypass it now:
        for (xs <- _defs) {
            xs.find{_def => _def != aDef && _def.mayEqual(aDef)} match {
                case None =>
                case Some(x) => return
            }
        }

        for (xs <- _refs) {
            result ++= xs.filter{aDef isReferredBy _}
        }

        /** search downward */
        for (xs <- _subScopes) {
            xs.foreach{_.findRefsOfDownward(aDef, result)}
        }
    }

    final def root :AstRootScope = parent match {
        case None => this.asInstanceOf[AstRootScope]
        case Some(x) => x.root
    }

    private def findAllRefsSameAs(ref:AstRef) :ArrayBuffer[AstRef] = {
        val result = new ArrayBuffer[AstRef]

        result += ref
        root.findAllRefsSameAsDownward(ref, result)

        result
    }

    protected def findAllRefsSameAsDownward(ref:AstRef,  result:ArrayBuffer[AstRef]) :Unit = {
        for (xs <- _refs) {
            result ++= xs.filter{ref.isOccurence(_)}
        }

        /** search downward */
        for (xs <- _subScopes) {
            xs.foreach{_.findAllRefsSameAsDownward(ref, result)}
        }
    }

    private def contains(th:TokenHierarchy[TokenId], offset:Int) :Boolean = {
        offset >= boundsOffset(th) && offset < boundsEndOffset(th)
    }

    def closestScope(th:TokenHierarchy[TokenId], offset:Int) :Option[AstScope] = _subScopes match {
        case Some(xs) =>
            /** search children first */
            xs.find{_.contains(th, offset)} match {
                case None => None
                case Some(child) => child.closestScope(th, offset)
            }
        case None if this.contains(th, offset) => Some(this)
            /* we should return None here, since it may under a parent context's call,
             * we shall tell the parent there is none in this and children of this
             */
        case None => None
    } 

    def visibleDefs(kind:ElementKind) :ArrayBuffer[AstDef] = {
        val result = new ArrayBuffer[AstDef]
        visibleDefsUpward(kind, result)
        result
    }

    private def visibleDefsUpward(kind:ElementKind, result:ArrayBuffer[AstDef]) :Unit = {
        for (xs <- _defs) {
            result ++= xs.filter{_.getKind == kind}
        }

        for (x <- parent) {
            x.visibleDefsUpward(kind, result)
        }
    }

    def enclosinDef(kind:ElementKind, th:TokenHierarchy[TokenId], offset:Int) :Option[AstDef] = {
        closestScope(th, offset) match {
            case None => None
            case Some(x) => x.enclosingDef(kind)
        }
    }

    def enclosingDef(kind:ElementKind) :Option[AstDef] = bindingDef match {
        case None => parent match {
                case None => None
                case Some(x) => x.enclosingDef(kind)
            }
        case Some(x) if x.getKind == kind => bindingDef
        case _ => None
    }

    def  visibleDefs[T <: AstDef](clazz:Class[T]) :ArrayBuffer[T] = {
        val result = new ArrayBuffer[T]
        visibleDefsUpward(clazz, result)
        result
    }
    
    private final def visibleDefsUpward[T <: AstDef](clazz:Class[T], result:ArrayBuffer[T]) :Unit = {
        for (xs <- _defs) {
            result ++= xs.filter{clazz isInstance _}.asInstanceOf[ArrayBuffer[T]]
        }
    
        for (x <- parent) {
            x.visibleDefsUpward(clazz, result)
        }
    }
    
    def enclosinDef[T <: AstDef](clazz:Class[T], th:TokenHierarchy[TokenId], offset:Int) :Option[T]= {
        closestScope(th, offset) match {
            case None => None
            case Some(x) => x.enclosingDef(clazz)
        }
    }
    
    def enclosingDef[T <: AstDef](clazz:Class[T]) :Option[T] = bindingDef match {
        case None => parent match {
                case None => None
                case Some(x) => x.enclosingDef(clazz)
            }
        case Some(x) if clazz.isInstance(x) => bindingDef.asInstanceOf[Option[T]]
        case _ => None
    }

    def findDefMatched(symbol:OtpErlangObject) :Option[AstDef] = {
        val name = symbol.toString
        findDefMatchedDownside(name, symbol, defs)
    }

    private def findDefMatchedDownside(name:String, symbol:OtpErlangObject, defs:ArrayBuffer[AstDef]) :Option[AstDef] = {
        for (aDef <- defs) {
            val mySymbol = aDef.symbol
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

            val children = aDef.bindingScope.defs
            val found = findDefMatchedDownside(name, symbol, children)
            if (found != None) {
                return found
            }
        }

        return None
    }

    override
    def toString() = {
        "Scope: (Binding=" + bindingDef + "," + ",defs=" + defs + ",refs=" + refs + ")"
    }

    // ----- compare functions

    private def compareScope(th:TokenHierarchy[TokenId], o1:AstScope, o2:AstScope) :Boolean = {
        o1.boundsOffset(th) < o2.boundsOffset(th)
    }

    private def compareDef(th:TokenHierarchy[TokenId], o1:AstDef, o2:AstDef) :Boolean = {
        o1.idOffset(th) < o2.idOffset(th)
    }

    private def compareRef(th:TokenHierarchy[TokenId], o1:AstRef, o2:AstRef) :Boolean = {
        o1.idOffset(th) < o2.idEndOffset(th)
    }
}

