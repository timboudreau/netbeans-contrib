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
package org.netbeans.modules.erlang.editor

import _root_.java.util.{ArrayList,Collections,Iterator,List,Set}
import javax.swing.ImageIcon
import javax.swing.text.BadLocationException
import org.netbeans.editor.Utilities
import org.netbeans.modules.csl.api.{CompletionProposal,ElementHandle,ElementKind,HtmlFormatter,Modifier}
import org.netbeans.modules.erlang.editor.ast.{AstElementHandle,AstDfn}
import org.netbeans.modules.erlang.editor.node.ErlSymbols._
import org.netbeans.modules.erlang.editor.rats.LexerErlang
import org.openide.util.Exceptions

/**
 *
 * @author Caoyuan Deng
 */
abstract class ErlangComplectionProposal(element:ElementHandle, anchor:Int) extends CompletionProposal {
  
    def getAnchorOffset:Int = anchor

    def getName :String = element.getName

    def getInsertPrefix :String = getName

    def getSortText :String = getName

    def getSortPrioOverride :Int = 0

    def getElement :ElementHandle = element

    def getKind :ElementKind = getElement.getKind

    def getIcon :ImageIcon = null

    def getLhsHtml(formatter:HtmlFormatter) : String = {
        val kind = getKind
        val emphasize = element match {
            case x:AstElementHandle if kind != ElementKind.PACKAGE => ! x.isInherited
            case _ => false
        }
        if (emphasize) {
            formatter.emphasis(true)
        }
        val strike = element match {
            case x:AstElementHandle => x.isDeprecated
            case _ => false
        }
        if (strike) {
            formatter.deprecated(true)
        }
        formatter.name(kind, true)
        formatter.appendText(getName)
        formatter.name(kind, false)
        if (strike) {
            formatter.deprecated(false)
        }
        if (emphasize) {
            formatter.emphasis(false)
        }

        element match {
            case x:AstElementHandle if x.tpe.length != 0 =>
                formatter.appendHtml(" :") // NOI18N
                formatter.`type`(true)
                formatter.appendText(x.tpe)
                formatter.`type`(false)
            case _ =>
        }

        return formatter.getText
    }

    def getRhsHtml(formatter:HtmlFormatter) :String = {
        element.getIn match {
            case null =>
            case in => formatter.appendText(in)
        }

        formatter.getText
    }

    def getModifiers :Set[Modifier] = getElement.getModifiers

    override
    def toString = {
        var cls = this.getClass.getName
        cls = cls.substring(cls.lastIndexOf('.') + 1)

        cls + "(" + getKind + "): " + getName
    }

    def isSmart :Boolean = false

    def getCustomInsertTemplate :String = null
}

class FunctionProposal(element:AstDfn, anchor:Int) extends ErlangComplectionProposal(element, anchor) {

    var function :ErlFunction = element.symbol match {
        case x:ErlFunction => x
        case _ => null
    }

    override
    def getInsertPrefix :String = getName

    override
    def getKind :ElementKind = ElementKind.METHOD

    override
    def getLhsHtml(formatter:HtmlFormatter) :String = {
        val kind = getKind
        var strike = element.isDeprecated
        if (strike) {
            formatter.deprecated(true)
        }
        val emphasize = !element.isInherited
        if (emphasize) {
            formatter.emphasis(true)
        }
        formatter.name(kind, true)
        formatter.appendText(getName)
        formatter.name(kind, false)
        if (emphasize) {
            formatter.emphasis(false)
        }
        if (strike) {
            formatter.deprecated(false)
        }
 
        val params :List[String] = getInsertParams
        if (!params.isEmpty()) {
            formatter.appendHtml("(") // NOI18N
            val itr = params.iterator
            while (itr.hasNext) {
                val param = itr.next
                formatter.parameters(true)
                formatter.appendText(param)
                formatter.parameters(false)
                if (itr.hasNext) {
                    formatter.appendText(", ") // NOI18N
                }
            }

            formatter.appendHtml(")") // NOI18N
        }

        formatter.getText
    }

    def getInsertParams :List[String] =  {
        val length = function.arity
        val params = new ArrayList[String](length)
        for (i <- 0 until function.arity) {
            params.add("a" + i)
        }
        params
    }

    override
    def getCustomInsertTemplate :String = {
        val sb = new StringBuilder

        val insertPrefix = getInsertPrefix
        sb.append(insertPrefix)

        val params = getInsertParams
        if (params.isEmpty) {
            return sb.toString
        }

        sb.append("(")

        val itr = params.iterator
        var id = 0
        while (itr.hasNext) {
            val paramDesc = itr.next
            id += 1
            sb.append("${"); //NOI18N
            // Ensure that we don't use one of the "known" logical parameters
            // such that a parameter like "path" gets replaced with the source file
            // path!

            sb.append("js-cc-"); // NOI18N

            sb.append(id)
            sb.append(" default=\"") // NOI18N

            paramDesc.indexOf(':') match {
                case tpeIdx if tpeIdx != -1 => sb.append(paramDesc.toArray, 0, tpeIdx)
                case _ => sb.append(paramDesc)
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
        //        try {
        //            ScalaCodeCompletion.callLineStart = Utilities.getRowStart(request.doc, request.anchor);
        //            ScalaCodeCompletion.callMethod = function;
        //        } catch (BadLocationException ble) {
        //            Exceptions.printStackTrace(ble);
        //        }

        sb.toString
    }
}

class KeywordProposal(keyword:String, description:String, anchor:Int) extends ErlangComplectionProposal(null, anchor) {
    import KeywordProposal._

    override
    def getName :String = keyword

    override
    def getKind :ElementKind = ElementKind.KEYWORD

    override
    def getRhsHtml(formatter:HtmlFormatter) :String = {
        if (description != null) {
            formatter.appendHtml(description)

            formatter.getText
        } else {
            null
        }
    }

    override
    def getIcon :ImageIcon = keywordIcon

    override
    def getModifiers :Set[Modifier] = Collections.emptySet[Modifier]

    override
    def getElement :ElementHandle = PseudoElement(keyword, ElementKind.KEYWORD) // For completion documentation

    override
    def isSmart :Boolean = false
}
object KeywordProposal {
    private val KEYWORD = "org/netbeans/modules/scala/editing/resources/scala16x16.png" //NOI18N
    protected lazy val keywordIcon :ImageIcon = new ImageIcon(org.openide.util.Utilities.loadImage(KEYWORD))
}

class TagProposal(tag:String, description:String, anchor:Int) extends ErlangComplectionProposal(null, anchor) {

    override
    def getName :String = tag

    override
    def getKind :ElementKind = ElementKind.TAG

    override
    def getRhsHtml(formatter:HtmlFormatter) :String = {
        if (description != null) {
            formatter.appendHtml("<i>")
            formatter.appendHtml(description)
            formatter.appendHtml("</i>")

            formatter.getText
        } else null
    }

    override
    def getModifiers :Set[Modifier] = Collections.emptySet[Modifier]

    override
    def getElement :ElementHandle = PseudoElement(tag, ElementKind.TAG) // For completion documentation

    override
    def isSmart:Boolean = true
}

class PlainProposal(element:ElementHandle, anchor:Int) extends ErlangComplectionProposal(element, anchor) {

}

class PackageProposal(element:AstDfn, anchor:Int) extends ErlangComplectionProposal(element, anchor) {

    override
    def getKind :ElementKind =ElementKind.PACKAGE

    override
    def getName :String = {
        val name = element.getName
        name.lastIndexOf('.') match {
            case lastDot if lastDot > 0 => name.substring(lastDot + 1, name.length)
            case _ => name
        }
    }

    override
    def getLhsHtml(formatter:HtmlFormatter) :String = {
        val kind = getKind
        val strike = element.isDeprecated
        if (strike) {
            formatter.deprecated(true);
        }
        formatter.name(kind, true)
        formatter.appendText(getName)
        formatter.name(kind, false)
        if (strike) {
            formatter.deprecated(false)
        }

        formatter.getText
    }

    override
    def isSmart :Boolean = true
}

class TypeProposal(element:AstDfn, anchor:Int) extends ErlangComplectionProposal(element, anchor) {

    override
    def getKind :ElementKind = ElementKind.CLASS

    override
    def getName :String = {
        val name = element.getName
        name.lastIndexOf('.') match {
            case lastDot if lastDot > 0 => name.substring(lastDot + 1, name.length)
            case _ => name
        }
    }

    override
    def getLhsHtml(formatter:HtmlFormatter) :String = {
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
}

case class PseudoElement(name:String, kind:ElementKind) extends ElementHandle {
    import _root_.java.util.{Collections,Set}
    import org.netbeans.modules.csl.api.OffsetRange
    import org.netbeans.modules.csl.spi.ParserResult
    import org.openide.filesystems.FileObject

    def getFileObject :FileObject = null

    def getMimeType :String = "text/x-erlang"

    def getName :String = name

    def getIn : String = null

    def getKind :ElementKind = kind

    def getModifiers :Set[Modifier] = Collections.emptySet[Modifier]

    def signatureEquals(handle:ElementHandle) :Boolean = false

    def getOffsetRange(result:ParserResult) :OffsetRange = OffsetRange.NONE
}

