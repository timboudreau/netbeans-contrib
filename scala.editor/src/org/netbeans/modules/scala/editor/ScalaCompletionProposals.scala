/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.scala.editor

import javax.swing.ImageIcon
import javax.swing.text.BadLocationException
import org.netbeans.editor.Utilities
import org.netbeans.modules.csl.api.{CompletionProposal, ElementHandle, ElementKind, HtmlFormatter, Modifier,OffsetRange}
import org.netbeans.modules.csl.spi.ParserResult
import org.openide.filesystems.FileObject
import org.openide.util.{Exceptions, ImageUtilities}

import org.netbeans.api.language.util.ast.{AstElementHandle}

import scala.tools.nsc.symtab.Flags
/**
 *
 * @author Caoyuan Deng
 */
trait ScalaCompletionProposals {self: ScalaGlobal =>

  object ScalaCompletionProposal {
    val KEYWORD = "org/netbeans/modules/scala/editor/resources/scala16x16.png" //NOI18N
    val keywordIcon = ImageUtilities.loadImageIcon(KEYWORD, false)
  }

  abstract class ScalaCompletionProposal(element: AstElementHandle, completer: ScalaCodeCompleter) extends CompletionProposal {

    def getAnchorOffset: Int = {
      completer.anchor
    }

    override def getName: String = {
      element.getName
    }

    override def getInsertPrefix: String = {
      getName
    }

    override def getSortText: String = {
      val name = getName
      name.charAt(0) match {
        case c if c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' => name
        case _ => '~' + name
      }
    }

    def getSortPrioOverride: Int = {
      0
    }

    def getElement: ElementHandle = {
      element
    }

    def getKind: ElementKind = {
      getElement.getKind
    }

    def getIcon: ImageIcon = {
      null
    }

    def getLhsHtml(fm: HtmlFormatter): String = {
      val emphasize = !element.isInherited
      val strike = element.isDeprecated

      if (emphasize) fm.emphasis(true)
      if (strike) fm.deprecated(true)
      
      val kind = getKind
      fm.name(kind, true)
      fm.appendText(getName)
      fm.name(kind, false)

      if (strike) fm.deprecated(false)
      if (emphasize) fm.emphasis(false)

      fm.getText
    }

    override def getRhsHtml(fm: HtmlFormatter): String = {
      element match {
        case x: ScalaElement =>
          val sym = x.symbol

          fm.`type`(true)
          val retType = try {
            sym.tpe.resultType
          } catch {case _ => ScalaGlobal.reset(self); null}

          if (retType != null && !sym.isConstructor) {
            fm.appendText(ScalaUtil.typeToString(retType))
          }
          fm.`type`(false)
        case _ =>
      }

      fm.getText
    }

    override def getModifiers: java.util.Set[Modifier] = {
      element.getModifiers
    }

    override def toString: String = {
      var cls = this.getClass().getName();
      cls = cls.substring(cls.lastIndexOf('.') + 1)

      cls + "(" + getKind + "): " + getName
    }

    def isSmart: Boolean = {
      false
      //return indexedElement != null ? indexedElement.isSmart() : true;
    }

    override def getCustomInsertTemplate: String = {
      null
    }
  }

  case class FunctionProposal(element: ScalaElement, completer: ScalaCodeCompleter) extends ScalaCompletionProposal(element, completer) {

    override def getInsertPrefix: String = {
      getName
    }

    override def getKind: ElementKind = {
      ElementKind.METHOD
    }

    override def getLhsHtml(fm: HtmlFormatter): String = {
      val strike = element.isDeprecated
      val emphasize = !element.isInherited
      if (element.isImplicit) {
        fm.appendHtml("<i>")
      }
      if (strike) {
        fm.deprecated(true)
      }
      if (emphasize) {
        fm.emphasis(true)
      }

      val kind = getKind
      fm.name(kind, true)
      fm.appendText(getName)
      fm.name(kind, false)

      if (emphasize) {
        fm.emphasis(false)
      }
      if (strike) {
        fm.deprecated(false)
      }
      if (element.isImplicit) {
        fm.appendHtml("</i>")
      }
      
      val typeParams = try {
        element.symbol.tpe.typeParams
      } catch {case _ => ScalaGlobal.reset(completer.global); Nil}
      if (!typeParams.isEmpty) {
        fm.appendHtml("[")
        fm.appendText(typeParams.map{_.nameString}.mkString(", "))
        fm.appendHtml("]")
      }

      val paramTypes = try {
        element.symbol.tpe.paramTypes
      } catch {case _ => ScalaGlobal.reset(completer.global); Nil}
      val paramNames = ScalaUtil.paramNames(element.symbol)

      if (!paramTypes.isEmpty) {
        fm.appendHtml("(") // NOI18N

        var i = 0
        val nameItr = if (paramNames == null) Nil else paramNames
        val typeItr = paramTypes.iterator
        while (typeItr.hasNext) {
          val param = typeItr.next
          fm.parameters(true)
          fm.appendText("a" + i)
          //if (nameItr != null && nameItr.hasNext()) {
          //    formatter.appendText(nameItr.next().toString());
          //} else {
          //    formatter.appendText("a" + Integer.toString(i));
          //}
          fm.parameters(false)
          fm.appendText(": ")
          fm.`type`(true)
          fm.appendText(param.toString)
          fm.`type`(false)

          if (typeItr.hasNext) {
            fm.appendText(", ") // NOI18N
          }

          i += 1
        }

        fm.appendHtml(")") // NOI18N
      }

      fm.getText
    }

    def getInsertParams: List[String] = {
      val paramTypes = try {
        element.symbol.tpe.paramTypes
      } catch {case _ => ScalaGlobal.reset(completer.global); Nil}

      paramTypes map {_.typeSymbol.nameString.toLowerCase}
    }

    override def getCustomInsertTemplate: String = {
      val sb = new StringBuilder

      val insertPrefix = getInsertPrefix
      sb.append(insertPrefix)

      val params = getInsertParams
      if (params.isEmpty) {
        return sb.toString
      }

      sb.append("(")

      var id = 1
      val itr = params.iterator
      while (itr.hasNext) {
        val paramDesc = itr.next
        sb.append("${") //NOI18N

        sb.append("scala-cc-") // NOI18N
        id += 1
        sb.append(id)
        
        sb.append(" default=\"") // NOI18N
        val typeIndex = paramDesc.indexOf(':')
        if (typeIndex != -1) {
          sb.append(paramDesc.toArray, 0, typeIndex)
        } else {
          sb.append(paramDesc)
        }
        sb.append("\"") // NOI18N

        sb.append("}") //NOI18N

        if (itr.hasNext) {
          sb.append(", ") //NOI18N
        }
      }

      sb.append(")")
      sb.append("${cursor}") // NOI18N

      // Facilitate method parameter completion on this item
      try {
        ScalaCodeCompleter.callLineStart = Utilities.getRowStart(completer.doc, completer.anchor)
        //ScalaCodeCompletion.callMethod = function;
      } catch {case ble: BadLocationException => Exceptions.printStackTrace(ble)}

      sb.toString
    }
  }

  case class KeywordProposal(keyword: String, description: String, completer: ScalaCodeCompleter) extends ScalaCompletionProposal(null, completer) {
    import KeywordProposal._

    override def getName: String = {
      keyword
    }

    override def getKind: ElementKind = {
      ElementKind.KEYWORD
    }

    override def getLhsHtml(formatter: HtmlFormatter): String = {
      val kind = getKind
      formatter.name(kind, true)
      formatter.appendHtml(getName)
      formatter.name(kind, false)

      formatter.getText
    }

    override def getRhsHtml(fm: HtmlFormatter): String = {
      if (description != null) {
        fm.appendText(description)

        fm.getText
      } else {
        null
      }
    }

    override def getIcon: ImageIcon = {
      ScalaCompletionProposal.keywordIcon
    }

    override def getModifiers: java.util.Set[Modifier] = {
      return java.util.Collections.emptySet[Modifier]
    }

    override def getElement: ElementHandle = {
      PseudoElement(keyword, ElementKind.KEYWORD) // For completion documentation
    }

    override def isSmart: Boolean = {
      false
    }
  }

 
  case class PlainProposal(element: AstElementHandle, completer: ScalaCodeCompleter) extends ScalaCompletionProposal(element, completer) {}

  case class PackageItem(element: AstElementHandle, completer: ScalaCodeCompleter) extends ScalaCompletionProposal(element, completer) {

    override def getKind: ElementKind = {
      ElementKind.PACKAGE
    }

    override def getName: String = {
      val name = element.getName
      val lastDot = name.lastIndexOf('.')
      if (lastDot > 0) {
        name.substring(lastDot + 1, name.length)
      } else name
    }

    override def getLhsHtml(formatter: HtmlFormatter): String = {
      val kind = getKind
      formatter.name(kind, true)
      formatter.appendText(getName)
      formatter.name(kind, false)

      formatter.getText
    }

    override def getRhsHtml(formatter: HtmlFormatter): String = {
      null
    }

    override def isSmart: Boolean = {
      true
    }
  }

  case class TypeProposal(element: AstElementHandle, completer: ScalaCodeCompleter) extends ScalaCompletionProposal(element, completer) {

    override def getKind: ElementKind = {
      ElementKind.CLASS
    }

    override def getName: String = {
      val name = element.qualifiedName
      name.lastIndexOf('.') match {
        case -1 => name
        case i => name.substring(i + 1, name.length)
      }
    }

    override def getLhsHtml(fm: HtmlFormatter): String = {
      val kind = getKind
      val strike = element.isDeprecated
      if (strike) {
        fm.deprecated(true)
      }
      fm.name(kind, true)
      fm.appendText(getName)
      fm.name(kind, false)
      if (strike) {
        fm.deprecated(false)
      }

      fm.getText
    }

    override def getRhsHtml(fm: HtmlFormatter): String = {
      val qname = element.qualifiedName
      val in = qname.lastIndexOf('.') match {
        case -1 => ""
        case i => qname.substring(0, i)
      }
      fm.appendText(in)
      fm.getText
    }
  }

  case class PseudoElement(name: String, kind: ElementKind) extends ElementHandle {

    def getFileObject: FileObject = null

    def getMimeType: String = "text/x-scala"

    def getName :String = name

    def getIn: String = null

    def getKind: ElementKind = kind

    def getModifiers: java.util.Set[Modifier] = java.util.Collections.emptySet[Modifier]

    def signatureEquals(handle: ElementHandle): Boolean = false

    def getOffsetRange(result: ParserResult): OffsetRange = OffsetRange.NONE
  }

}
