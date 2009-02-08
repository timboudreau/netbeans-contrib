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
                boundsToken = boundsTokens(0)
            case 2 =>
                boundsToken = boundsTokens(0)
                boundsEndToken = boundsTokens(1)
        }
    }

    var boundsToken :Token[TokenId] = _
    var boundsEndToken :Token[TokenId] = _
    var bindingDef :AstDef = _
    var parent :AstScope = _
    private var _subScopes :ArrayBuffer[AstScope] = _
    private var _defs :ArrayBuffer[AstDef] = _
    private var _refs :ArrayBuffer[AstRef] = _
    private var scopesSorted :Boolean = _
    private var defsSorted :Boolean = _
    private var refsSorted :Boolean = _

    def isRoot = parent == null

    def isScopesSorted :Boolean = scopesSorted

    def range(th:TokenHierarchy[TokenId]) :OffsetRange = {
        new OffsetRange(boundsOffset(th), boundsEndOffset(th))
    }

    def boundsOffset(th:TokenHierarchy[TokenId]) :Int = boundsToken match {
        case null => -1
        case token => token.offset(th)
    }

    def boundsEndOffset(th:TokenHierarchy[TokenId]) :Int = boundsEndToken match {
        case null => -1
        case token => token.offset(th) + token.length
    }
  
    def subScopes :ArrayBuffer[AstScope] = {
        if (_subScopes == null) new ArrayBuffer else _subScopes
    }

    def defs :ArrayBuffer[AstDef] = {
        if (_defs == null) new ArrayBuffer else _defs
    }

    def refs :ArrayBuffer[AstRef] = {
        if (_refs == null) new ArrayBuffer else _refs
    }

    protected def addScope(scope:AstScope) :Unit = {
        if (_subScopes == null) {
            _subScopes = new ArrayBuffer
        }
        _subScopes += scope
        scopesSorted = false
        scope.parent = this
    }

    /**
     * @param def to be added
     * @retrun added successfully or not
     */
    protected def addDef(adef:AstDef) :Boolean = adef.idToken match {
        case null => false
        case idToken =>
            /** @todo tempary solution */
            //        if (!ScalaLexUtilities.isProperIdToken(idToken.id())) {
            //            return false
            //        }

            /** a def will always be added */
            root.tryToPut(idToken, adef)
            if (_defs == null) {
                _defs = new ArrayBuffer
            }
            _defs += adef
            defsSorted = false
            adef.enclosingScope = this
            true
    }

    /**
     * @param ref to be added
     * @retrun added successfully or not
     */
    def addRef(ref:AstRef) :Boolean = ref.idToken match {
        case null => false
        case idToken =>
            /** @todo tempary solution */
            //        if (!ScalaLexUtilities.isProperIdToken(idToken.id())) {
            //            return false;
            //        }

            /** if a def or ref that corresponds to this idToekn has been added, this ref won't be added */
            if (root.contains(idToken)) {
                return false
            }

            root.tryToPut(idToken, ref)
            if (_refs == null) {
                _refs = new ArrayBuffer
            }
            _refs += ref
            refsSorted = false
            ref.enclosingScope = this
            true
    }

    def findItemAt(th:TokenHierarchy[TokenId], offset:Int) :Option[AstItem] = {
        // Always seach Ref first, since Ref can be included in Def's range
        if (_refs != null) {
            if (!refsSorted) {
                Sorter.sort(_refs){compareRef(th, _, _)}
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
        }

        if (_defs != null) {
            if (!defsSorted) {
                Sorter.sort(_defs){compareDef(th, _, _)}
                defsSorted = true
            }
            var lo = 0
            var hi = _defs.size - 1
            while (lo <= hi) {
                var mid = (lo + hi) >> 1
                val middle = _defs(mid)
                if (offset < middle.idOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.idEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return Some(middle)
                }
            }
        }

        if (_subScopes != null) {
            if (!scopesSorted) {
                Sorter.sort(_subScopes){compareScope(th, _, _)}
                scopesSorted = true
            }
            var lo = 0
            var hi = _subScopes.size - 1
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
        }

        None
    }

    def findItemAt(th:TokenHierarchy[TokenId], token:Token[TokenId]) :Option[AstItem] = {
        val offset = token.offset(th)
        // Always seach Ref first, since Ref can be included in Def's range
        if (_refs != null) {
            if (!refsSorted) {
                Sorter.sort(_refs){compareRef(th, _, _)}
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
        }

        if (_defs != null) {
            if (!defsSorted) {
                Sorter.sort(_defs){compareDef(th, _, _)}
                defsSorted = true
            }
            var lo = 0
            var hi = _defs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = _defs(mid)
                if (offset < middle.idOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.idEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return Some(middle)
                }
            }
        }

        if (_subScopes != null) {
            if (!scopesSorted) {
                Sorter.sort(_subScopes){compareScope(th, _, _)}
                scopesSorted = true
            }
            var lo = 0
            var hi = _subScopes.size - 1
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
        }

        return None
    }

    def findDefAt[T <: AstDef](clazz:Class[T], th:TokenHierarchy[TokenId], offset:Int) :Option[T] = {
        if (_defs != null) {
            if (!defsSorted) {
                Sorter.sort(_defs){compareDef(th, _, _)}
                defsSorted = true
            }
            var lo = 0
            var hi = _defs.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = _defs(mid)
                if (offset < middle.idOffset(th)) {
                    hi = mid - 1
                } else if (offset >= middle.idEndOffset(th)) {
                    lo = mid + 1
                } else {
                    return if (clazz isInstance middle) Some(middle.asInstanceOf[T]) else None
                }
            }
        }
    
        if (_subScopes != null) {
            if (!scopesSorted) {
                Sorter.sort(_subScopes){compareScope(th, _, _)}
                scopesSorted = true
            }
            var lo = 0
            var hi = _subScopes.size - 1
            while (lo <= hi) {
                val mid = (lo + hi) >> 1
                val middle = _subScopes(mid)
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
        if (_refs != null) {
            if (!refsSorted) {
                Sorter.sort(_refs){compareRef(th, _, _)}
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
                    return if (clazz isInstance middle) Some(middle.asInstanceOf[T]) else None
                }
            }
        }
    
    
        if (_subScopes != null) {
            if (!scopesSorted) {
                Sorter.sort(_subScopes){compareScope(th, _, _)}
                scopesSorted = true
            }
            var lo = 0
            var hi = _subScopes.size - 1
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
        }
    
        None
    }
    
    def findOccurrences(item:AstItem) :ArrayBuffer[AstItem] = {
        var aDef :Option[AstDef] = item match {
            case x:AstDef => Some(x)
            case x:AstRef => findDefOf(x)
        }
    
        if (aDef == None) {
            // def maybe remote one, just try to find all same refs
            return findAllRefsSameAs(item.asInstanceOf[AstRef]).asInstanceOf[ArrayBuffer[AstItem]]
        }
    
        val occurrences = new ArrayBuffer[AstItem]
        occurrences += aDef.get
        occurrences ++= findRefsOf(aDef.get)
    
        occurrences
    }

    def findDefOf(item:AstItem) :Option[AstDef] = item match {
        case aDef:AstDef => Some(aDef)
        case aRef:AstRef => findDefOf(aRef)
    }
  

    private def findDefOf(aRef:AstRef) :Option[AstDef] = {
        val closestScope = aRef.enclosingScope
        closestScope.findDefOfUpward(aRef)
    }

    private def findDefOfUpward(aRef:AstRef) :Option[AstDef] = {
        if (_defs != null) {
            _defs.find{_ isReferredBy aRef} match {
                case None =>
                case Some(x) => return Some(x)
            }
        }

        /** search upward */
        if (parent != null) {
            parent.findDefOfUpward(aRef)
        } else None
    }

    def findRefsOf(aDef:AstDef) :ArrayBuffer[AstRef] = {
        val result = new ArrayBuffer[AstRef]

        val enclosingScope = aDef.enclosingScope
        enclosingScope.findRefsOfDownward(aDef, result)

        result
    }

    private def findRefsOfDownward(aDef:AstDef, result:ArrayBuffer[AstRef]) :Unit = {
        // find if there is closest override Def, if so, we shoud bypass it now:
        if (_defs != null) {
            _defs.find{_def => _def != aDef && _def.mayEqual(aDef)} match {
                case None =>
                case Some(x) => return
            }
        }

        if (_refs != null) {
            result ++= _refs.filter{aDef isReferredBy _}
        }

        /** search downward */
        if (_subScopes != null) {
            _subScopes.foreach{_.findRefsOfDownward(aDef, result)}
        }
    }

    final def root :AstRootScope = {
        if (parent == null) this.asInstanceOf[AstRootScope] else parent.root
    }

    private def findAllRefsSameAs(ref:AstRef) :ArrayBuffer[AstRef] = {
        val result = new ArrayBuffer[AstRef]

        result += ref
        root.findAllRefsSameAsDownward(ref, result)

        result
    }

    protected final def findAllRefsSameAsDownward(ref:AstRef,  result:ArrayBuffer[AstRef]) :Unit = {
        if (_refs != null) {
            result ++= _refs.filter{ref.isOccurence(_)}
        }

        /** search downward */
        if (_subScopes != null) {
            _subScopes.foreach{_.findAllRefsSameAsDownward(ref, result)}
        }
    }

    private def contains(th:TokenHierarchy[TokenId], offset:Int) :Boolean = {
        offset >= boundsOffset(th) && offset < boundsEndOffset(th)
    }

    def closestScope(th:TokenHierarchy[TokenId], offset:Int) :AstScope = {
        //var result :Option[AstScope] = None

        val result = if (_subScopes != null) {
            /** search children first */
            _subScopes.find{_.contains(th, offset)} match {
                case None => None
                case Some(child) => Some(child.closestScope(th, offset))
            }
        } else None

        result match {
            case None =>
                if (this.contains(th, offset)) this
                /* we should return null here, since it may under a parent context's call,
                 * we shall tell the parent there is none in this and children of this
                 */
                else null
            case Some(x) => x
        }
    }

    def visibleDefs(kind:ElementKind) :ArrayBuffer[AstDef] = {
        val result = new ArrayBuffer[AstDef]
        visibleDefsUpward(kind, result)
        result
    }

    private final def visibleDefsUpward(kind:ElementKind, result:ArrayBuffer[AstDef]) :Unit = {
        if (_defs != null) {
            result ++= _defs.filter{_.getKind == kind}
        }

        if (parent != null) {
            parent.visibleDefsUpward(kind, result)
        }
    }

    def enclosinDef(kind:ElementKind, th:TokenHierarchy[TokenId], offset:Int) :Option[AstDef] = {
        val scope = closestScope(th, offset)
        scope.enclosingDef(kind)
    }

    def enclosingDef(kind:ElementKind) :Option[AstDef] = {
        if (bindingDef != null && bindingDef.getKind == kind) {
            Some(bindingDef)
        } else {
            if (parent != null) {
                parent.enclosingDef(kind)
            } else None
        }
    }

    def  visibleDefs[T <: AstDef](clazz:Class[T]) :ArrayBuffer[T] = {
        val result = new ArrayBuffer[T]
        visibleDefsUpward(clazz, result)
        result
    }
    
    private final def visibleDefsUpward[T <: AstDef](clazz:Class[T], result:ArrayBuffer[T]) :Unit = {
        if (_defs != null) {
            result ++= _defs.filter{clazz isInstance _}.asInstanceOf[ArrayBuffer[T]]
        }
    
        if (parent != null) {
            parent.visibleDefsUpward(clazz, result)
        }
    }
    
    def enclosinDef[T <: AstDef](clazz:Class[T], th:TokenHierarchy[TokenId], offset:Int) :Option[T]= {
        val scope = closestScope(th, offset)
        scope.enclosingDef(clazz)
    }
    
    def enclosingDef[T <: AstDef](clazz:Class[T]) :Option[T] = {
        if (bindingDef != null && clazz.isInstance(bindingDef)) {
            return Some(bindingDef.asInstanceOf[T])
        } else {
            if (parent != null) {
                return parent.enclosingDef(clazz)
            } else {
                return None
            }
        }
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

