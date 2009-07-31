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
package org.netbeans.modules.scala.editor.ast

import _root_.java.util.{Collections, Set, HashSet}
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy}
import org.netbeans.editor.{BaseDocument}
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.modules.csl.api.HtmlFormatter
import org.netbeans.modules.csl.api.Modifier
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.spi.{GsfUtilities,ParserResult}
import org.openide.filesystems.FileObject

import org.netbeans.api.language.util.lex.LexUtil
import org.netbeans.api.language.util.ast.{AstDfn, AstRef, AstScope}
import org.netbeans.modules.scala.editor.ScalaMimeResolver

import _root_.scala.tools.nsc.symtab.Symbols
import _root_.scala.tools.nsc.symtab.Flags

/**
 * Scala AstDfn special functions
 */
object ScalaDfn {
  def apply(symbol: ScalaSymbol,
            _idToken: Option[Token[TokenId]],
            _kind: ElementKind,
            _bindingScope: AstScope[Symbols#Symbol],
            fo: Option[FileObject]) =
  {
    val dfn = new ScalaDfn(_idToken, _kind, _bindingScope, fo)
    dfn.symbol = symbol
    dfn
  }
}

class ScalaDfn(_idToken: Option[Token[TokenId]],
               _kind: ElementKind,
               _bindingScope: AstScope[Symbols#Symbol],
               fo: Option[FileObject]
) extends AstDfn[Symbols#Symbol](_idToken, _kind, _bindingScope, fo) {
  import ElementKind._

  override def getMimeType: String = ScalaMimeResolver.MIME_TYPE

  override def getModifiers: _root_.java.util.Set[Modifier] = {
    if (modifiers != null) {
      return modifiers
    }
    
    modifiers = new _root_.java.util.HashSet

    val sym = symbol.value
    if (sym hasFlag Flags.PROTECTED) {
      modifiers.add(Modifier.PROTECTED)
    } else if (sym hasFlag Flags.PRIVATE) {
      modifiers.add(Modifier.PRIVATE)
    } else {
      modifiers.add(Modifier.PUBLIC)
    }
    
    if (sym hasFlag Flags.MUTABLE)    modifiers.add(Modifier.STATIC) // to use STATIC icon only
    if (sym hasFlag Flags.DEPRECATED) modifiers.add(Modifier.DEPRECATED)
    
    modifiers
  }


  /** @Note: do not call ref.getKind here, which will recursively call this function, use ref.kind ! */
  def isReferredBy(ref: AstRef[Symbols#Symbol]): Boolean = (ref.kind, getKind) match {
    case _ => false // @todo
      //    case (CALL, METHOD) => (ref.symbol, symbol) match {
      //        case (ErlFunction(_, nameX, arityX), ErlFunction(_, nameY, arityY))
      //          if nameX == nameY && arityX == arityY => true
      //        case _ => false
      //      }
      //
      //    case (ATTRIBUTE, ATTRIBUTE) => (ref.symbol, symbol) match {
      //        case (ErlRecord(nameX, fieldsX), ErlRecord(nameY, fieldsY))
      //          if nameX == nameY => true
      //        case (ErlRecordField(nameX, fieldX), ErlRecordField(nameY, fieldY))
      //          if nameX == nameY && fieldX == fieldY=> true
      //        case _ => false
      //      }
      //
      //    case (_, RULE) => false // RULE is spec dfn, don't let it's reffered by anything
      //
      //    case _ =>
      //      if (ref.getName == getName) {
      //        ref.symbol == self.asInstanceOf[AstItem].symbol
      //      } else false
  }
   

  def docComment: String = {
    val srcDoc = doc match {
      case None => return null
      case Some(x) => x
    }

    val th = TokenHierarchy.get(srcDoc)
    if (th == null) {
      return null
    }

    //ErlangGlobal.docComment(srcDoc, idOffset(th))
    return null // todo
  }


  def htmlFormat(formatter: HtmlFormatter): Unit = getKind match {
    case PACKAGE | CLASS | MODULE => formatter.appendText(getName)
    case METHOD | RULE => symbol match {
        //        case ErlFunction(_, name, arity) =>
        //          formatter.appendText(name)
        //          formatter.appendText("/")
        //          formatter.appendText(arity.toString)
        case _ =>
          formatter.appendText(getName)
          formatter.appendText("/?")
      }
    case ATTRIBUTE /*if isFunctionClause*/ => property("args") match {
        case Some(args: List[String]) =>
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
      //case ATTRIBUTE => formatter.appendText(getName)
    case _ => formatter.appendText(getName)
  }
    
}

