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
package org.netbeans.modules.erlang.editor

import _root_.java.util.{HashMap,List,Map}
import javax.swing.text.Document
import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy}
import _root_.org.netbeans.modules.csl.api.{ColoringAttributes,OccurrencesFinder,OffsetRange, ElementKind}
import org.netbeans.modules.parsing.spi.Parser
import org.netbeans.modules.parsing.spi.{Scheduler,SchedulerEvent}
//import org.netbeans.editor.BaseDocument
import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstItem,AstRef,AstRootScope}
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId,LexUtil}
import org.openide.filesystems.FileObject

/**
 *
 * @author Caoyuan Deng
 */
class ErlangOccurrencesFinder extends OccurrencesFinder[ErlangParserResult] {

  protected var cancelled = false
  private var caretPosition = 0
  private var occurrences :Map[OffsetRange, ColoringAttributes] = _
  private var file :FileObject = _

  protected def isCancelled = synchronized {cancelled}

  protected def resume :Unit = synchronized {cancelled = false}

  override def getPriority = 0

  override def getSchedulerClass = Scheduler.CURSOR_SENSITIVE_TASK_SCHEDULER

  override def getOccurrences :Map[OffsetRange, ColoringAttributes] = occurrences

  override def cancel :Unit = synchronized {cancelled = true}

  override def setCaretPosition(position:Int) :Unit = {this.caretPosition = position}

  def run(pResult:ErlangParserResult, event:SchedulerEvent) :Unit = {
    resume
    // * clean old occurrences if any
    this.occurrences = null
    if (pResult == null || isCancelled) {
      return
    }

    val currentFile = pResult.getSnapshot.getSource.getFileObject
    if (currentFile != file) {
      // Ensure that we don't reuse results from a different file
      occurrences = null
      file = currentFile
    }

    for (rootScope <- pResult.rootScope;
         th <- LexUtil.tokenHierarchy(pResult);
         doc <- LexUtil.document(pResult, true);
         // * we'll find item by offset of item's idToken, so, use caretPosition directly
         item <- rootScope.findItemAt(th, caretPosition);
         idToken <- item.idToken
    ) {
      var highlights = new HashMap[OffsetRange, ColoringAttributes](100)

      val astOffset = LexUtil.astOffset(pResult, caretPosition)
      if (astOffset == -1) {
        return
      }

      // * When we sanitize the line around the caret, occurrences
      // * highlighting can get really ugly
      val blankRange = pResult.sanitizedRange

      if (blankRange.containsInclusive(astOffset)) {
        return
      }

      // * test if document was just closed?
      LexUtil.document(pResult, true) match {
        case None => return
        case _ =>
      }

      try {
        doc.readLock
        val length = doc.getLength
        val astRange = LexUtil.rangeOfToken(th, idToken)
        val lexRange = LexUtil.lexerOffsets(pResult, astRange)
        var lexStartPos = lexRange.getStart
        var lexEndPos = lexRange.getEnd

        // If the buffer was just modified where a lot of text was deleted,
        // the parse tree positions could be pointing outside the valid range
        if (lexStartPos > length) {
          lexStartPos = length
        }
        if (lexEndPos > length) {
          lexEndPos = length
        }

        LexUtil.token(doc, caretPosition) match {
          case None => return
          case token => // valid token, go on
        }
      } finally {
        doc.readUnlock
      }

      val occurrences = rootScope.findOccurrences(item)
      for (item1 <- occurrences;
           idToken1 <- item1.idToken
      ) {

        // detect special case for function
        val functionDfn = item1 match {
          case aDfn:AstDfn => aDfn.functionDfn
          case _ => None
        }

        functionDfn match {
          case Some(x) =>
            if (x != item1) {
              // we should refind occrrunces of functionDfn to get all scope refs
              val occurrences1 = rootScope.findOccurrences(x)
              for (item2 <- occurrences1;
                   idToken2 <- item2.idToken
              ) {
                highlights.put(LexUtil.rangeOfToken(th, idToken2), ColoringAttributes.MARK_OCCURRENCES)
              }
            }
                        
            for (clause <- x.functionClauses;
                 clauseIdToken <- clause.idToken
            ) {
              highlights.put(LexUtil.rangeOfToken(th, clauseIdToken), ColoringAttributes.MARK_OCCURRENCES)
            }
          case None =>
            highlights.put(LexUtil.rangeOfToken(th, idToken1), ColoringAttributes.MARK_OCCURRENCES)
        }
      }

      if (isCancelled) {
        return
      }

      if (highlights.size > 0) {
        val translated = new HashMap[OffsetRange, ColoringAttributes](2 * highlights.size)
        val entries = highlights.entrySet.iterator
        while (entries.hasNext) {
          val entry = entries.next
          LexUtil.lexerOffsets(pResult, entry.getKey) match {
            case OffsetRange.NONE =>
            case range => translated.put(range, entry.getValue)
          }
        }

        highlights = translated

        this.occurrences = highlights
      } else {
        this.occurrences = null
      }
    }
  }
}