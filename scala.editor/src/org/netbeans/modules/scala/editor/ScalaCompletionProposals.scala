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
import org.netbeans.modules.csl.api.{CompletionProposal, ElementHandle, ElementKind, HtmlFormatter, Modifier}
import org.openide.util.Exceptions

import org.netbeans.api.language.util.ast.{AstElementHandle}

/**
 *
 * @author Caoyuan Deng
 */
trait ScalaCompletionProposals {self: ScalaGlobal =>
  
  abstract class ScalaCompletionProposal(element: AstElementHandle, request: ScalaCodeCompletion.CompletionRequest) extends CompletionProposal {

    def getAnchorOffset: Int = {
      request.anchor
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

    def getLhsHtml(formatter: HtmlFormatter): String = {
      val emphasize = !element.isInherited
      val strike = element.isDeprecated

      if (emphasize) formatter.emphasis(true)
      if (strike) formatter.deprecated(true)
      
      val kind = getKind
      formatter.name(kind, true)
      formatter.appendText(getName)
      formatter.name(kind, false)

      if (strike) formatter.deprecated(false)
      if (emphasize) formatter.emphasis(false)

      formatter.getText
    }

    override def getRhsHtml(formatter: HtmlFormatter): String = {
      element match {
        case x: ScalaElement =>
          val sym = x.symbol

          formatter.`type`(true)
          val retType =  try {
            sym.tpe.resultType
          } catch {case ex: Throwable => ScalaGlobal.reset(self); null}

          if (retType != null && !sym.isConstructor) {
            formatter.appendText(ScalaUtil.typeToString(retType))
          }
          formatter.`type`(false)
        case _ =>
      }

      formatter.getText
    }

    override def getModifiers: _root_.java.util.Set[Modifier] = {
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

  case class FunctionProposal(element: ScalaElement, request: ScalaCodeCompletion.CompletionRequest) extends ScalaCompletionProposal(element, request) {

    private val methodType: Type = element.symbol.tpe

    override def getInsertPrefix: String = {
      getName
    }

    override def getKind: ElementKind = {
      ElementKind.METHOD
    }

    override def getLhsHtml(formatter: HtmlFormatter): String = {
      val strike = element.isDeprecated
      val emphasize = !element.isInherited
      if (strike) {
        formatter.deprecated(true)
      }
      if (emphasize) {
        formatter.emphasis(true)
      }

      val kind = getKind
      formatter.name(kind, true)
      formatter.appendText(getName)
      formatter.name(kind, false)

      if (emphasize) {
        formatter.emphasis(false)
      }
      if (strike) {
        formatter.deprecated(false)
      }

      val typeParams = methodType.typeParams
      if (!typeParams.isEmpty) {
        formatter.appendHtml("[")
        formatter.appendText(typeParams.elements.map{_.nameString}.mkString(", "))
        formatter.appendHtml("]")
      }

      val paramTypes = methodType.paramTypes
      val paramNames = element.paramNames

      if (!paramTypes.isEmpty) {
        formatter.appendHtml("(") // NOI18N

        var i = 0
        val nameItr = if (paramNames == null) Nil else paramNames
        val typeItr = paramTypes.iterator
        while (typeItr.hasNext) {
          val param = typeItr.next
          formatter.parameters(true)
          formatter.appendText("a" + Integer.toString(i))
          //if (nameItr != null && nameItr.hasNext()) {
          //    formatter.appendText(nameItr.next().toString());
          //} else {
          //    formatter.appendText("a" + Integer.toString(i));
          //}
          formatter.parameters(false)
          formatter.appendText(": ")
          formatter.`type`(true)
          formatter.appendText(param.toString)
          formatter.`type`(false)

          if (typeItr.hasNext) {
            formatter.appendText(", ") // NOI18N
          }

          i += 1
        }

        formatter.appendHtml(")") // NOI18N
      }

      formatter.getText
    }

    def getInsertParams: List[String] = {
      methodType.paramTypes map {_.typeSymbol.nameString.toLowerCase}
    }

    override def getCustomInsertTemplate: String = {
      val sb = new StringBuilder

      val insertPrefix = getInsertPrefix
      sb.append(insertPrefix)

      val params = getInsertParams
      if (params.isEmpty) {
        return sb.toString
      }

      val startDelimiter = "("
      val endDelimiter = ")"

      sb.append(startDelimiter)

      var id = 1
      val itr = params.iterator
      while (itr.hasNext) {
        val paramDesc = itr.next
        sb.append("${") //NOI18N
        // Ensure that we don't use one of the "known" logical parameters
        // such that a parameter like "path" gets replaced with the source file
        // path!

        sb.append("js-cc-") // NOI18N
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
      sb.append(endDelimiter)

      sb.append("${cursor}") // NOI18N

      // Facilitate method parameter completion on this item
      try {
        ScalaCodeCompletion.CompletionRequest.callLineStart = Utilities.getRowStart(request.doc, request.anchor)
        //ScalaCodeCompletion.callMethod = function;
      } catch {case ble: BadLocationException => Exceptions.printStackTrace(ble)}

      sb.toString
    }
  }

  object KeywordProposal {
    private val KEYWORD = "org/netbeans/modules/scala/editor/resources/scala16x16.png" //NOI18N
    private val keywordIcon: ImageIcon = new ImageIcon(org.openide.util.Utilities.loadImage(KEYWORD))

  }
  case class KeywordProposal(keyword: String, description: String, request: ScalaCodeCompletion.CompletionRequest) extends ScalaCompletionProposal(null, request) {
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

    override def getRhsHtml(formatter: HtmlFormatter): String = {
      if (description != null) {
        formatter.appendText(description)

        formatter.getText
      } else {
        null
      }
    }

    override def getIcon: ImageIcon = {
      keywordIcon
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

 
  case class PlainProposal(element: AstElementHandle, request: ScalaCodeCompletion.CompletionRequest) extends ScalaCompletionProposal(element, request) {}

  case class PackageItem(element: AstElementHandle, request: ScalaCodeCompletion.CompletionRequest) extends ScalaCompletionProposal(element, request) {

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

  class TypeProposal(element: AstElementHandle, request: ScalaCodeCompletion.CompletionRequest) extends ScalaCompletionProposal(element, request) {

    override def getKind: ElementKind = {
      ElementKind.CLASS
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
      val strike = element.isDeprecated
      if (strike) {
        formatter.deprecated(true)
      }
      formatter.name(kind, true)
      formatter.appendText(getName)
      formatter.name(kind, false)
      if (strike) {
        formatter.deprecated(false)
      }

      formatter.getText
    }

    override def getRhsHtml(formatter: HtmlFormatter): String = {
      null
    }
  }

  case class PseudoElement(name: String, kind: ElementKind) extends ElementHandle {
    import org.netbeans.modules.csl.api.OffsetRange
    import org.netbeans.modules.csl.spi.ParserResult
    import org.openide.filesystems.FileObject

    def getFileObject :FileObject = null

    def getMimeType :String = "text/x-scala"

    def getName :String = name

    def getIn : String = null

    def getKind :ElementKind = kind

    def getModifiers :_root_.java.util.Set[Modifier] = _root_.java.util.Collections.emptySet[Modifier]

    def signatureEquals(handle:ElementHandle) :Boolean = false

    def getOffsetRange(result:ParserResult) :OffsetRange = OffsetRange.NONE
  }

}
