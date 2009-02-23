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

import _root_.java.util.{Collections,Set}
import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy}
import org.netbeans.modules.csl.api.{ElementKind,ElementHandle,Modifier,OffsetRange}
import org.netbeans.modules.csl.spi.{ParserResult}
import org.netbeans.modules.erlang.editor.ErlangMimeResolver
import org.openide.filesystems.{FileObject}

import scala.collection.mutable.{HashMap}

/**
 *
 * @author Caoyuan Deng
 */
trait AstItem extends ForElementHandle {

    def make(idToken:Option[Token[TokenId]], kind:ElementKind) :Unit = {
        this.idToken = idToken
        this.kind = kind
    }

    var resultType :String = _
    /**
     * @Note:
     * 1. Not all AstItem has pickToken, such as Expr etc.
     * 2. Due to strange behavior of StructureAnalyzer, we can not rely on
     *    pickToken's text as name, pickToken may be <null> and pickToken.text()
     *    will return null when an Identifier token modified, seems sync issue
     */
    private var _symbol :AstSym = AstSym()
    private var _idToken :Option[Token[TokenId]] = None
    private var _name :String = _
    private var _enclosingScope :Option[AstScope] = _
    private var properties :Option[HashMap[String, Any]] = None
    var kind :ElementKind = ElementKind.OTHER

    def symbol = _symbol
    def symbol_=(symbol:AstSym) = {
        this._symbol = symbol
        symbol.item = this
    }

    def idToken = _idToken
    def idToken_=(idToken:Option[Token[TokenId]]) = idToken.foreach{x =>
        this._idToken = idToken; name = x.text.toString
    }

    def name = _name
    def name_=(name:String) = this._name = name
    def name_=(idToken:Token[TokenId]) = {
        if (idToken == null) {
            _name = "" // should not happen?
        }
        
        try {
            _name = idToken.text.toString
        } catch {
            case ex:Exception =>
                val l = idToken.length
                val sb = new StringBuilder(l)
                var i = 0
                while (i < l) {
                    sb.append(" ")
                    i += 1
                }
                _name = sb.toString
                println("NPE in AstItem#getName:" + idToken.id)
        }
    }

    def idOffset(th:TokenHierarchy[_]) = idToken match {
        case None =>
            assert(false, getName + ": Should implement offset(th)")
            -1
        case Some(x) => x.offset(th)
    }

    def idEndOffset(th:TokenHierarchy[_]) :Int = idToken match {
        case None =>
            assert(false, name + ": Should implement getIdEndOffset(th)")
            -1
        case Some(x) => x.offset(th) + x.length
    }

    def binaryName = name

    def enclosingDfn[A <: AstDfn](clazz:Class[A]) :Option[A] = {
        enclosingScope.get.enclosingDfn(clazz)
    }

    /**
     * @Note: enclosingScope will be set when call
     *   {@link AstScope#addElement(Element)} or {@link AstScope#addMirror(Mirror)}
     */
    def enclosingScope_=(enclosingScope:AstScope) :AstItem = {
        enclosingScope match {
            case null => this._enclosingScope = None
            case _ => this._enclosingScope = Some(enclosingScope)
        }
        this
    }

    /**
     * @return the scope that encloses this item
     */
    def enclosingScope :Option[AstScope] = {
        assert(_enclosingScope != None, name + ": Each item should set enclosing scope!, except native TypeRef")
        _enclosingScope
    }

    def property(k:String, v:Any) :Unit = {
        if (properties == None) {
            properties = Some(new HashMap)
        }
        for (_properties <- properties) {
            _properties + (k -> v)
        }
    }

    def property(k:String) :Option[Any] = {
        for (_properties <- properties) {
            return _properties.get(k)
        }
        None
    }
}

/**
 * Wrap functions that implemented some ElementHandle's methods
 */
trait ForElementHandle {self:AstItem =>
    
    def getMimeType :String = ErlangMimeResolver.MIME_TYPE

    def getName = self.name

    def getIn :String = ""

    def getKind :ElementKind = self.kind

    def signatureEquals(handle:ElementHandle) = false

    def getModifiers :Set[Modifier] = Collections.emptySet[Modifier]

    def getOffsetRange(result:ParserResult) :OffsetRange = OffsetRange.NONE
}
