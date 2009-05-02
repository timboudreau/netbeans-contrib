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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.editor.util

import _root_.java.util.Collections
import javax.swing.text.{BadLocationException,Document}
import org.netbeans.editor.{BaseDocument,Utilities}
import org.netbeans.api.lexer.{TokenHierarchy}
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.parsing.api.{ParserManager,ResultIterator,Source,UserTask}
import org.netbeans.modules.parsing.spi.{ParseException}

import org.netbeans.modules.erlang.editor.lexer.LexUtil
import org.netbeans.modules.erlang.editor.ErlangParserResult
import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstRootScope}
import org.netbeans.modules.erlang.editor.node.ErlSymbols._
import org.openide.filesystems.{FileObject,FileChangeAdapter,FileEvent,FileRenameEvent}

import scala.collection.mutable.HashMap

/**
 *
 *  @author Caoyuan Deng
 */
object ErlangUtil {
   val FoToRootScope = new HashMap[FileObject, AstRootScope]

   private class FoChangeAdapter extends FileChangeAdapter {
      override
      def fileChanged(fe:FileEvent) :Unit = reset(fe.getFile)

      override
      def fileDeleted(fe:FileEvent) :Unit = reset(fe.getFile)

      override
      def fileRenamed(fe:FileRenameEvent) :Unit = reset(fe.getFile)

      private def reset(fo:FileObject) :Unit = {
         fo.removeFileChangeListener(this)
         FoToRootScope.removeKey(fo)
      }
   }

   def resolveRootScope(fo:FileObject) :Option[AstRootScope] = {
      FoToRootScope.get(fo) match {
         case None =>
            val source = Source.create(fo)
            try {
               ParserManager.parse(Collections.singleton(source), new UserTask {
                     @throws(classOf[Exception])
                     override
                     def run(resultIterator:ResultIterator) :Unit = {
                        resultIterator.getParserResult match {
                           case r:ErlangParserResult =>
                              r.rootScope.foreach{x =>
                                 fo.addFileChangeListener(new FoChangeAdapter)
                                 FoToRootScope + (fo -> x)
                              }
                           case _ =>
                        }
                     }
                  })
            } catch {case e:ParseException =>}
                
            FoToRootScope.get(fo)
         case some => some
      }
   }

   def resolveDfn(fo:FileObject, symbol:ErlSymbol) :Option[AstDfn] = {
      resolveRootScope(fo) match {
         case None => None
         case Some(rootScope) => rootScope.findDfnOfSym(symbol)
      }
   }

   def docComment(doc:BaseDocument, itemOffset:int) :String = {
      val th = TokenHierarchy.get(doc)
      if (th == null) {
         return null
      }

      doc.readLock // Read-lock due to token hierarchy use
      val range = LexUtil.docCommentRangeBefore(th, itemOffset)
      doc.readUnlock

      if (range != OffsetRange.NONE && range.getEnd < doc.getLength) {
         try {
            return doc.getText(range.getStart, range.getLength)
         } catch {case ex:BadLocationException =>}
      }

      null
   }

}
