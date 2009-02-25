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

import _root_.java.util.{Collections,Set,HashSet}
import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy}
import org.netbeans.editor.{BaseDocument}
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.modules.csl.api.HtmlFormatter
import org.netbeans.modules.csl.api.Modifier
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.spi.{GsfUtilities,ParserResult}
import org.openide.filesystems.FileObject

import org.netbeans.modules.erlang.editor.lexer.LexUtil

import scala.collection.mutable.ArrayBuffer

/**
 * AST Definition
 * 
 * Represents a program element such as a package, class, or method. Each element 
 * represents a static, language-level construct (and not, for example, a runtime 
 * construct of the virtual machine). 
 * 
 * @author Caoyuan Deng
 */
class AstDfn(_idToken:Option[Token[TokenId]],
             _kind:ElementKind,
             private var _bindingScope:AstScope,
             var fo:Option[FileObject]
) extends AstItem with AstElementHandle with LanguageAstDfn {
    
    // we allow _bindingScope to be set later
    if (_bindingScope != null) {
        _bindingScope.bindingDfn = Some(this)
    }

    make(_idToken, _kind)

    private var modifiers :Set[Modifier] = _

    override
    def getFileObject :FileObject = fo.getOrElse(null)

    override
    def getKind :ElementKind = super[AstItem].getKind

    override
    def getModifiers :Set[Modifier] = modifiers match {
        case null => Collections.emptySet[Modifier]
        case _ => modifiers
    }

    override
    def getOffsetRange(pResult:ParserResult) :OffsetRange = LexUtil.tokenHierarchy(pResult) match {
        case None => OffsetRange.NONE
        case Some(th) => new OffsetRange(boundsOffset(th), boundsEndOffset(th))
    }

    def tpe :String = {
        "NoType"
    }

    def enclosedElements :ArrayBuffer[AstDfn] = {
        if (_bindingScope != null) {
            _bindingScope.dfns
        } else new ArrayBuffer
    }

    def enclosingDfn :Option[AstDfn] = enclosingScope.get.bindingDfn

    override
    def toString = {
        "Def: " + name + " (idToken=" + idToken + ", kind=" + _kind +  ")"
    }

    def bindingScope :AstScope = {
        assert(_bindingScope != null, toString + ": Each definition should set binding scope!")
        _bindingScope
    }

    def boundsOffset(th:TokenHierarchy[_]) :Int = {
        bindingScope.boundsOffset(th)
    }

    def boundsEndOffset(th:TokenHierarchy[_]) :Int = {
        bindingScope.boundsEndOffset(th)
    }

    def range(th:TokenHierarchy[_]) :OffsetRange = {
        bindingScope.range(th)
    }

    def mayEqual(dfn:AstDfn) :Boolean = {
        this == dfn
        //return getName().equals(def.getName())
    }

    def docComment :String = {
        null
    }

    def doc :Option[BaseDocument] = fo match {
        case None => None
        case Some(x) => GsfUtilities.getDocument(x, true) match {
                case null => None
                case docx => Some(docx)
            }
    }

    def packageName :String = {
        null
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

/**
 * Erlang special functions
 */
trait LanguageAstDfn {self:AstDfn =>
    import ElementKind._
    import org.netbeans.modules.erlang.editor.node.ErlSymbols._

    /** @Note: do not call ref.getKind here, which will recursively call this function, use ref.kind ! */
    def isReferredBy(ref:AstRef) :Boolean = (ref.kind, getKind) match {
        case (CALL, METHOD) => (ref.symbol, symbol) match {
                case (ErlFunction(_, nameX, arityX), ErlFunction(_, nameY, arityY))
                    if nameX.equals(nameY) && arityX == arityY => true
                case _ => false
            }
        case _ =>
            if (ref.getName.equals(getName)) {
                ref.symbol == self.asInstanceOf[AstItem].symbol
            } else false
    }

    def htmlFormat(formatter:HtmlFormatter) :Unit = getKind match {
        case PACKAGE | CLASS | MODULE => formatter.appendText(getName)
        case METHOD => symbol match {
                case ErlFunction(_, name, arity) =>
                    formatter.appendText(name)
                    formatter.appendText("/")
                    formatter.appendText(arity.toString)
                case _ =>
                    formatter.appendText(getName)
                    formatter.appendText("/?")
            }
        case ATTRIBUTE if isFunctionClause => property("args") match {
                case Some(args:List[String]) =>
                    formatter.appendText("(")
                    val itr = args.elements
                    while (itr.hasNext) {
                        formatter.appendText(itr.next)
                        if (itr.hasNext) {
                            formatter.appendText(", ")
                        }
                    }
                    formatter.appendText(")")
                case _ => formatter.appendText("()")
            }
        case ATTRIBUTE => formatter.appendText(getName)
        case _ => formatter.appendText(getName)
    }
    

    def isFunctionClause = {
        //* is it FunctionClause of enclosingDfn ?
        enclosingDfn.filter{_.getKind == METHOD}.isDefined && self.getKind == ATTRIBUTE
    }

    def functionDfn :Option[AstDfn] = self.getKind match {
        case ElementKind.METHOD => Some(self)
        case ElementKind.ATTRIBUTE if self.isFunctionClause =>  self.enclosingDfn
        case _ => None
    }

    def functionClauses :List[AstDfn] = functionDfn match {
        case None => Nil
        case Some(x) => x.bindingScope.dfns.filter{_.getKind == ElementKind.ATTRIBUTE}.toList
    }
}

