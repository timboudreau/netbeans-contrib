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
package org.netbeans.modules.erlang.editor;

import _root_.java.util.{ArrayList,Collections,HashMap,List,Map,Set,Stack}
import javax.swing.ImageIcon;
import javax.swing.text.{BadLocationException,Document}
import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy,TokenSequence}
import org.netbeans.editor.{BaseDocument,Utilities}
import org.netbeans.modules.csl.api.{ElementHandle,ElementKind,HtmlFormatter,Modifier,OffsetRange,StructureItem,StructureScanner}
import org.netbeans.modules.csl.api.StructureScanner._
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstRootScope,AstScope}
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId,LexUtil}
import org.netbeans.modules.erlang.editor.node.ErlSymbol._
import org.openide.util.Exceptions

import scala.collection.mutable.ArrayBuffer

/**
 *
 * @author Caoyuan Deng
 */
class ErlangStructureAnalyzer extends StructureScanner {

   override
   def getConfiguration :Configuration = null

   override
   def scan(result:ParserResult) :List[StructureItem] = result match {
      case pResult:ErlangParserResult =>
         var items = Collections.emptyList[StructureItem]
         for (rootScope <- pResult.rootScope) {
            items = new ArrayList[StructureItem](rootScope.dfns.size)
            scanTopForms(rootScope, items, pResult)
         }
         items
      case _ => Collections.emptyList[StructureItem]
   }

   private def scanTopForms(scope:AstScope, items:List[StructureItem], pResult:ErlangParserResult) :Unit = {
      for (dfn <- scope.dfns) {
         dfn.getKind match {
            case ElementKind.ATTRIBUTE | ElementKind.METHOD | ElementKind.MODULE => items.add(new ErlangStructureItem(dfn, pResult))
            case _ =>
         }
         // * for Erlang, only visit the rootScope
         //scanTopForms(dfn.bindingScope, items, pResult)
      }
   }

   override
   def folds(result:ParserResult) :Map[String, List[OffsetRange]] = result match {
      case pResult:ErlangParserResult =>
         var folds = Collections.emptyMap[String, List[OffsetRange]]
         for (rootScope <- pResult.rootScope;
              doc <- LexUtil.document(pResult, true);
              th <- LexUtil.tokenHierarchy(pResult);
              ts <- LexUtil.tokenSequence(th, 1)
         ) {
            folds = new HashMap[String, List[OffsetRange]]
            val codefolds = new ArrayList[OffsetRange]
            folds.put("codeblocks", codefolds); // NOI18N

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
                  case ErlangTokenId.LineComment =>
                     val offset = ts.offset
                     if (!startLineCommentSet) {
                        lineCommentStart = offset
                        startLineCommentSet = true
                     }
                     lineCommentEnd = offset

                  case ErlangTokenId.Case | ErlangTokenId.If | ErlangTokenId.Try | ErlangTokenId.Receive =>
                     val blockStart = ts.offset
                     blocks.push(blockStart)

                     startLineCommentSet = false
                        
                  case ErlangTokenId.End if !blocks.empty =>
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
                  val lineCommentsFolds = new ArrayList[OffsetRange];
                  val range = new OffsetRange(lineCommentStart, lineCommentEnd)
                  lineCommentsFolds.add(range)
                  folds.put("comments", lineCommentsFolds) // NOI18N
               }
            } catch {
               case ex:BadLocationException => Exceptions.printStackTrace(ex)
            }
         }

         folds
      case _ => Collections.emptyMap[String, List[OffsetRange]]
   }

   @throws(classOf[BadLocationException])
   private def addCodeFolds(pResult:ErlangParserResult, doc:BaseDocument, defs:ArrayBuffer[AstDfn], codeblocks:List[OffsetRange]) :Unit = {
      import ElementKind._
       
      for (dfn <- defs) {
         val kind = dfn.getKind
         kind match {
            case FIELD | METHOD | CONSTRUCTOR | CLASS | ATTRIBUTE if !dfn.isInstanceOf[ErlRecordField] =>
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

   private class ErlangStructureItem(val dfn:AstDfn, pResult:ParserResult) extends StructureItem {
      import ElementKind._

      override
      def getName :String = dfn.getName

      override
      def getSortText :String = getName

      override
      def getHtml(formatter:HtmlFormatter) :String = {
         dfn.htmlFormat(formatter)
         formatter.getText
      }

      override
      def getElementHandle :ElementHandle = dfn

      override
      def getKind :ElementKind = dfn.getKind
        
      override
      def getModifiers :Set[Modifier] = dfn.getModifiers

      override
      def isLeaf :Boolean = dfn.getKind match {
         case MODULE | CLASS | METHOD => false
         case CONSTRUCTOR | FIELD | VARIABLE | OTHER | PARAMETER | ATTRIBUTE if !dfn.isInstanceOf[ErlRecordField] => true
         case _ => true
      }

      override
      def getNestedItems : List[StructureItem] = {
         val nested = dfn.bindingScope.dfns
         if (nested.size > 0) {
            val children = new ArrayList[StructureItem](nested.size)

            for (child <- nested) {
               child.getKind match {
                  case PARAMETER | VARIABLE | OTHER =>
                  case _ => children.add(new ErlangStructureItem(child, pResult))
               }
            }

            children
         } else Collections.emptyList[StructureItem]
      }

      override
      def getPosition :Long = {
         try {
            LexUtil.tokenHierarchy(pResult) match {
               case None => 0
               case Some(th) => dfn.boundsOffset(th)
            }
         } catch {case ex:Exception => 0}
      }

      override
      def getEndPosition :Long = {
         try {
            LexUtil.tokenHierarchy(pResult) match {
               case None => 0
               case Some(th) => dfn.boundsEndOffset(th)
            }
         } catch {case ex:Exception => 0}
      }

      override
      def equals(o:Any) :Boolean = o match {
         case null => false
         case x:ErlangStructureItem if dfn.getKind == x.dfn.getKind && getName.equals(x.getName) => true
         case _ => false
      }

      override
      def hashCode :Int = {
         var hash = 7
         hash = (29 * hash) + (if (getName != null) getName.hashCode else 0)
         hash = (29 * hash) + (if (dfn.getKind != null) dfn.getKind.hashCode else 0)
         hash
      }

      override
      def toString = getName

      override
      def getCustomIcon :ImageIcon = null
   }
}
