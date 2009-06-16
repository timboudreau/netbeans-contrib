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
package org.netbeans.modules.erlang.editor

import _root_.java.util.Collections
import javax.swing.text.{BadLocationException,Document}
import org.netbeans.editor.{BaseDocument,Utilities}
import org.netbeans.api.lexer.{TokenHierarchy}
import org.netbeans.api.java.classpath.GlobalPathRegistry
import org.netbeans.api.java.classpath.ClassPath
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.parsing.api.{ParserManager,ResultIterator,Source,UserTask}
import org.netbeans.modules.parsing.spi.{ParseException}

import org.netbeans.modules.erlang.editor.lexer.LexUtil
import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstRootScope,AstSym}
import org.netbeans.modules.erlang.editor.node.ErlSymbol._
import org.openide.filesystems.{FileObject,FileChangeAdapter,FileEvent,FileRenameEvent}

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

/**
 *
 *  @author Caoyuan Deng
 */
object ErlangGlobal {
   val FoToRootScope = new HashMap[FileObject, AstRootScope]
   val ModuleToDfns = new HashMap[String, Seq[AstDfn]]

   private class FoChangeAdapter extends FileChangeAdapter {
      override
      def fileChanged(fe:FileEvent) :Unit = reset(fe.getFile)

      override
      def fileDeleted(fe:FileEvent) :Unit = reset(fe.getFile)

      override
      def fileRenamed(fe:FileRenameEvent) :Unit = reset(fe.getFile)

      private def reset(fo:FileObject) :Unit = {
         fo.removeFileChangeListener(this)
         FoToRootScope.get(fo) match {
            case None =>
            case Some(scope) =>
               FoToRootScope.removeKey(fo)
               scope.findAllDfnSyms(classOf[ErlModule]) match {
                  case module :: _ => ModuleToDfns.removeKey(module.name)
                  case _ =>
               }
         }
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
                              for (scope <- r.rootScope) {
                                 fo.addFileChangeListener(new FoChangeAdapter)
                                 FoToRootScope + (fo -> scope)
                                 cacheDfns(scope)
                              }
                           case _ =>
                        }
                     }
                  })
            } catch {case e:ParseException =>}
                
            FoToRootScope.get(fo)
         case x => x
      }
   }

   /** Only cache export functions for .erl file */
   def cacheDfns(rootScope:AstRootScope) :Unit = {
      val module = rootScope.findAllDfnSyms(classOf[ErlModule]) match {
         case x :: _ => x.name
         case _ => return
      }
      
      val exports  = rootScope.findAllDfnsOf(classOf[ErlExport])
      ModuleToDfns += (module -> exports)
   }

   def findFunction(module:String, functionName:String, arity:Int) :Option[AstDfn] = {
      ModuleToDfns.get(module) match {
         case None => None
         case Some(dfns) => dfns.find{x => x.symbol match {
               case ErlFunction(_, `functionName`, `arity`) => true
               case _ => false
            }}
      }
   }

   def resolveDfn(fo:FileObject, symbol:AstSym) :Option[AstDfn] = {
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

   def libFo :Option[FileObject] = {
      val classpaths = GlobalPathRegistry.getDefault.getPaths(ErlangLanguage.BOOT);
      val itr = classpaths.iterator
      if (itr.hasNext) {
         val roots = itr.next.getRoots
         if (roots.size > 0) {
            return Some(roots(0))
         }
      }
      None
   }

   def getClasspathRoots(fo:FileObject, classpathId:String) :Seq[FileObject] = {
      if (fo != null) {
         val classpath = ClassPath.getClassPath(fo, classpathId);
         if (classpath != null) {
            classpath.getRoots()
         } else Array()
      } else {
         var roots = new ArrayBuffer[FileObject]
         val classpaths = GlobalPathRegistry.getDefault.getPaths(classpathId);
         val itr = classpaths.iterator
         while (itr.hasNext) {
            roots ++= itr.next.getRoots
         }
         roots
      }
   }

}
