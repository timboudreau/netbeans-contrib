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

import _root_.java.util.{Set, HashSet}
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy}
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.modules.csl.api.HtmlFormatter
import org.netbeans.modules.csl.api.Modifier
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.spi.ParserResult
import org.openide.filesystems.FileObject

import scala.collection.mutable.ArrayBuffer

import xtc.tree.{GNode}

/**
 * AST Definition
 * 
 * Represents a program element such as a package, class, or method. Each element 
 * represents a static, language-level construct (and not, for example, a runtime 
 * construct of the virtual machine). 
 * 
 * @author Caoyuan Deng
 */
class AstDef(aSymbol:GNode,
             pickToken:Token[TokenId],
             private var _bindingScope:AstScope,
             var kind:ElementKind,
             var fo:FileObject ) extends AstItem(aSymbol, pickToken) with AstElementHandle {

    if (_bindingScope != null) {
        _bindingScope.bindingDef = Some(this)
    }

    private var modifiers :Set[Modifier] = _

    override
    def getFileObject :FileObject = fo

    override
    def getKind :ElementKind = kind

    override
    def getModifiers :Set[Modifier] = {
        if (modifiers == null) {
            modifiers = new HashSet[Modifier]
        }
        modifiers
    }

    override
    def getOffsetRange(result:ParserResult) :OffsetRange = {
        OffsetRange.NONE
    }

    def tpe :String = {
        null
    }

    def enclosedElements :ArrayBuffer[AstDef] = {
        if (_bindingScope != null) {
            _bindingScope.defs
        } else new ArrayBuffer
    }

    def enclosingDef :Option[AstDef] = enclosingScope.get.bindingDef

    override
    def toString = {
        "Def: " + name + " (idToken=" + idToken + ", kind=" + kind +  ")"
    }

    def bindingScope :AstScope = {
        assert(_bindingScope != null, toString + ": Each definition should set binding scope!")
        _bindingScope
    }

    def boundsOffset(th:TokenHierarchy[TokenId]) :Int = {
        bindingScope.boundsOffset(th)
    }

    def boundsEndOffset(th:TokenHierarchy[TokenId]) :Int ={
        bindingScope.boundsEndOffset(th)
    }

    def range(th:TokenHierarchy[TokenId]) :OffsetRange = {
        bindingScope.range(th)
    }

    def isReferredBy(ref:AstRef) :Boolean = {
        if (ref.getName.equals(getName)) {
            //            if ((getSymbol().isClass() || getSymbol().isModule()) && ref.isSameNameAsEnclClass()) {
            //                return true
            //            }

            ref.symbol == symbol
        } else false
    }

    def mayEqual(aDef:AstDef) :Boolean = {
        this == aDef
        //return getName().equals(def.getName())
    }

    def docComment :String = {
        null
        //        BaseDocument srcDoc = getDoc()
        //        if (srcDoc == null) {
        //            return null
        //        }
        //
        //        TokenHierarchy th = TokenHierarchy.get(srcDoc)
        //        if (th == null) {
        //            return null
        //        }
        //
        //        return ScalaUtils.getDocComment(srcDoc, getIdOffset(th))
    }

    //    public BaseDocument getDoc() {
    //        FileObject srcFo = getFileObject()
    //        if (srcFo != null) {
    //            return GsfUtilities.getDocument(srcFo, true)
    //        } else {
    //            return null
    //        }
    //    }

    def htmlFormat(formatter:HtmlFormatter) :Unit = {
        formatter.appendText(getName)
        //htmlFormat(formatter, this, false)
        import ElementKind._
        getKind match {
            case PACKAGE | CLASS | MODULE =>
            case _ =>
                //Type resType = getType().resultType()
                formatter.appendText(" :")
                val atype = tpe
                if (atype != null) {
                    //formatter.appendText(ScalaElement.typeToString(type))
                }
                //formatter.appendText(resType.toString())
                //htmlFormat(formatter, resType, true)
        }
    }

    def packageName :String = {
        null
        // return ScalaElement.symbolQualifiedName(getSymbol().enclosingPackage())
    }

    def qualifiedName :String = {
        null
    }

    def isInherited :Boolean = {
        false
    }

    def isDeprecated :boolean = {
        false
    }

    def isEmphasize :Boolean = {
        false
    }

}
