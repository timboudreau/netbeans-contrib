/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
import javax.swing.text.{BadLocationException, Document}
import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy, TokenSequence}
import org.netbeans.api.language.util.ast.{AstDfn, AstScope}
import org.netbeans.editor.{BaseDocument, Utilities}
import org.netbeans.modules.csl.api.{ElementHandle, ElementKind, Modifier, OffsetRange,
                                     HtmlFormatter, StructureItem, StructureScanner}
import org.netbeans.modules.csl.api.StructureScanner._
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.scala.editor.ast.{ScalaDfns, ScalaRootScope}
import org.netbeans.modules.scala.editor.lexer.{ScalaTokenId, ScalaLexUtil}
import org.openide.util.Exceptions

import _root_.scala.collection.mutable.{ArrayBuffer, Stack}

/**
 *
 * @author Caoyuan Deng
 */
class ScalaStructureAnalyzer extends StructureScanner {

  override def getConfiguration: Configuration = null

  override def scan(result: ParserResult): _root_.java.util.List[StructureItem] = {
    result match {
      case pResult: ScalaParserResult =>
        val rootScope = pResult.rootScope match {
          case None => return _root_.java.util.Collections.emptyList[StructureItem]
          case Some(x) => x
        }

        val items = new _root_.java.util.ArrayList[StructureItem]
        scanTopForms(rootScope, items, pResult)
      
        items
      case _ => _root_.java.util.Collections.emptyList[StructureItem]
    }
  }

  private def scanTopForms(scope: AstScope, items: _root_.java.util.List[StructureItem], pResult: ScalaParserResult): Unit = {
    scope.dfns foreach {
      case dfn: ScalaDfns#ScalaDfn => dfn.getKind match {
          case ElementKind.CLASS | ElementKind.MODULE =>
            (dfn.enclosingScope, dfn.enclosingDfn) match {
              case (Some(x), _) if x.isRoot => items.add(new ScalaStructureItem(dfn, pResult))
              case (_, Some(x)) if x.getKind == ElementKind.PACKAGE => items.add(new ScalaStructureItem(dfn, pResult))
              case _ =>
            }
          case _ =>
        }
        scanTopForms(dfn.bindingScope, items, pResult)
    }
  }

  override def folds(result: ParserResult): _root_.java.util.Map[String, _root_.java.util.List[OffsetRange]] = {
    result match {
      case pResult: ScalaParserResult =>
        var folds = _root_.java.util.Collections.emptyMap[String, _root_.java.util.List[OffsetRange]]
        for (rootScope <- pResult.rootScope;
             doc <- ScalaLexUtil.getDocument(pResult.getSnapshot.getSource.getFileObject, true);
             th = pResult.getSnapshot.getTokenHierarchy;
             ts <- ScalaLexUtil.getTokenSequence(th, 1))
        {
          folds = new _root_.java.util.HashMap[String, _root_.java.util.List[OffsetRange]]
          val codefolds = new _root_.java.util.ArrayList[OffsetRange]
          folds.put("codeblocks", codefolds) // NOI18N

          // * Read-lock due to Token hierarchy use
          doc.readLock

          addCodeFolds(pResult, doc, rootScope.dfns, codefolds)

          var lineCommentStart = 0
          var lineCommentEnd = 0
          var startLineCommentSet = false

          val comments = new Stack[Array[Integer]]
          val blocks = new Stack[Integer]

          while (ts.isValid && ts.moveNext) {
            val token = ts.token
            token.id match {
              case ScalaTokenId.LineComment =>
                val offset = ts.offset
                if (!startLineCommentSet) {
                  lineCommentStart = offset
                  startLineCommentSet = true
                }
                lineCommentEnd = offset

              case ScalaTokenId.Case | ScalaTokenId.If | ScalaTokenId.Try  =>
                val blockStart = ts.offset
                blocks.push(blockStart)

                startLineCommentSet = false

              case ScalaTokenId.RBrace if !blocks.isEmpty =>
                val blockStart = blocks.pop.asInstanceOf[Int]
                val blockRange = new OffsetRange(blockStart, ts.offset + token.length)
                codefolds.add(blockRange)

                startLineCommentSet = false
              case _ =>
                startLineCommentSet = false
            }
          }

          doc.readUnlock

          try {
            /** @see GsfFoldManager#addTree() for suitable fold names. */
            lineCommentEnd = Utilities.getRowEnd(doc, lineCommentEnd)

            if (Utilities.getRowCount(doc, lineCommentStart, lineCommentEnd) > 1) {
              val lineCommentsFolds = new _root_.java.util.ArrayList[OffsetRange];
              val range = new OffsetRange(lineCommentStart, lineCommentEnd)
              lineCommentsFolds.add(range)
              folds.put("comments", lineCommentsFolds) // NOI18N
            }
          } catch {
            case ex: BadLocationException => Exceptions.printStackTrace(ex)
          }
        }

        folds
      case _ => _root_.java.util.Collections.emptyMap[String, _root_.java.util.List[OffsetRange]]
    }
  }
  
  @throws(classOf[BadLocationException])
  private def addCodeFolds(pResult: ScalaParserResult, doc: BaseDocument, defs: Seq[AstDfn],
                           codeblocks: _root_.java.util.List[OffsetRange]): Unit = {
    import ElementKind._
       
    for (dfn <- defs) {
      dfn.getKind match {
        case FIELD | METHOD | CONSTRUCTOR | CLASS | ATTRIBUTE =>
          var range = dfn.getOffsetRange(pResult)
          var start = range.getStart
          // * start the fold at the end of the line behind last non-whitespace, should add 1 to start after "->"
          start = Utilities.getRowLastNonWhite(doc, start) + 1
          val end = range.getEnd
          if (start != -1 && end != -1 && start < end && end <= doc.getLength) {
            range = new OffsetRange(start, end)
            codeblocks.add(range)
          }
        case _ =>
      }
    
      val children = dfn.bindingScope.dfns
      addCodeFolds(pResult, doc, children, codeblocks)
    }
  }

  private class ScalaStructureItem(val dfn: ScalaDfns#ScalaDfn, pResult: ScalaParserResult) extends StructureItem {
    import ElementKind._

    override def getName: String = dfn.getName

    override def getSortText: String = getName

    override def getHtml(formatter:HtmlFormatter): String = {
      dfn.htmlFormat(formatter)
      formatter.getText
    }

    override def getElementHandle: ElementHandle = dfn

    override def getKind: ElementKind = dfn.getKind
        
    override def getModifiers: _root_.java.util.Set[Modifier] = dfn.getModifiers

    override def isLeaf: Boolean = {
      dfn.getKind match {
        case MODULE | CLASS | METHOD => false
        case CONSTRUCTOR | FIELD | VARIABLE | OTHER | PARAMETER | ATTRIBUTE => true
        case _ => true
      }
    }

    override def getNestedItems: _root_.java.util.List[StructureItem] = {
      val nested = dfn.bindingScope.dfns
      if (!nested.isEmpty) {
        val children = new _root_.java.util.ArrayList[StructureItem]

        nested foreach {
          case child: ScalaDfns#ScalaDfn => child.getKind match {
              case PARAMETER | OTHER =>
              case _ => children.add(new ScalaStructureItem(child, pResult))
            }
        }

        children
      } else _root_.java.util.Collections.emptyList[StructureItem]
    }

    override def getPosition: Long = {
      try {
        pResult.getSnapshot.getTokenHierarchy match {
          case null => 0
          case th => dfn.boundsOffset(th)
        }
      } catch {case ex:Exception => 0}
    }

    override def getEndPosition: Long = {
      try {
        pResult.getSnapshot.getTokenHierarchy match {
          case null => 0
          case th => dfn.boundsEndOffset(th)
        }
      } catch {case ex: Exception => 0}
    }

    override def equals(o: Any): Boolean = o match {
      case null => false
      case x:ScalaStructureItem if dfn.getKind == x.dfn.getKind && getName.equals(x.getName) => true
      case _ => false
    }

    override def hashCode: Int = {
      var hash = 7
      hash = (29 * hash) + (if (getName != null) getName.hashCode else 0)
      hash = (29 * hash) + (if (dfn.getKind != null) dfn.getKind.hashCode else 0)
      hash
    }

    override def toString = getName

    override def getCustomIcon: ImageIcon = null
  }
}
