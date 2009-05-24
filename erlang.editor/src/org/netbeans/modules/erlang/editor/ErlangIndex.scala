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

import _root_.java.io.{File,IOException}
import _root_.java.net.{MalformedURLException,URL}
import _root_.java.util.{Collection,Collections}
import org.netbeans.modules.csl.api.CompletionProposal
import org.netbeans.modules.csl.spi.{GsfUtilities,ParserResult}
import org.netbeans.modules.parsing.spi.{Parser}
import org.netbeans.modules.parsing.spi.indexing.support.{IndexResult,QuerySupport}
import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstItem}
import org.netbeans.modules.erlang.editor.lexer.LexUtil
import org.netbeans.modules.erlang.editor.node.ErlSymbol._
import org.netbeans.modules.erlang.editor.util.ErlangUtil
import org.openide.filesystems.{FileObject,FileUtil}
import org.openide.util.Exceptions

import scala.collection.mutable.{ArrayBuffer,HashSet,HashMap}
import scala.collection.jcl.{CollectionWrapper}

/**
 *
 * @author Caoyuan Deng
 */
class ErlangIndex(querySupport:QuerySupport) {
   import ErlangIndex._

   private def query(key:String, name:String, kind:QuerySupport.Kind, fieldsToLoad:String*) :Array[IndexResult] = {
      try {
         val r = querySupport.query(key, name, kind, fieldsToLoad:_*)
         r.toArray(new Array[IndexResult](r.size))
      } catch {case ex:IOException => ex.printStackTrace; EMPTY_INDEX_RESULT}
   }

   private def queryFiles(name:String, kind:QuerySupport.Kind, fieldsToLoad:String*) :Array[IndexResult] = {
      query(ErlangIndexer.FIELD_FQN_NAME, name, kind, fieldsToLoad:_*)
   }

   def queryPersistentUrl(fqn:String) :URL = {
      var url = moduleToUrlBuf.get(fqn) match {
         case Some(x) => return x
         case None =>
            for (r <- queryFiles(fqn, QuerySupport.Kind.EXACT); url = r.getUrl if url != null) {
               moduleToUrlBuf.put(fqn, url)
               return url
            }
      }
      null
   }

   def queryModules(fqn:String) :Array[String] = {
      for (r <- queryFiles(fqn, QuerySupport.Kind.PREFIX, ErlangIndexer.FIELD_FQN_NAME)) yield {
         r.getValue(ErlangIndexer.FIELD_FQN_NAME)
      }
   }

   def queryFunctions(fqn:String) :Array[AstDfn] = {
      functionsBuf.clear
      for (r <- queryFiles(fqn, QuerySupport.Kind.EXACT, ErlangIndexer.FIELD_FUNCTION)) {
         val signatures = r.getValues(ErlangIndexer.FIELD_FUNCTION)
         if (signatures != null) {
            val fo = FileUtil.toFileObject(new File(r.getUrl.toURI))
            for (signature <- signatures) {
               val symbol = createFuntion(signature)
               ErlangUtil.resolveDfn(fo, symbol).foreach{functionsBuf + _}
            }
         }
      }
      functionsBuf.toArray
   }

   def queryFunction(module:String, functionName:String, arity:Int) :Option[AstDfn] = {
      for (r <- queryFiles(module, QuerySupport.Kind.EXACT, ErlangIndexer.FIELD_FUNCTION)) {
         val signatures = r.getValues(ErlangIndexer.FIELD_FUNCTION)
         if (signatures != null) {
            val fo = FileUtil.toFileObject(new File(r.getUrl.toURI))
            for (signature <- signatures) {
               createFuntion(signature) match {
                  case symbol@ErlFunction(_, `functionName`, `arity`) =>
                     return ErlangUtil.resolveDfn(fo, symbol)
                  case _ =>
               }
            }
         }
      }
      None
   }

   def queryRecord(includes:Seq[ErlInclude], recordName:String) :Option[AstDfn] = {
      for (include <- includes) {
         val name = if (include.isLib) "lib;" + include.path.replace('/', '.') else include.path.replace('/', '.')
         for (r <- queryFiles(name, QuerySupport.Kind.EXACT, ErlangIndexer.FIELD_RECORD)) {
            val signatures = r.getValues(ErlangIndexer.FIELD_RECORD)
            if (signatures != null) {
               val fo = FileUtil.toFileObject(new File(r.getUrl.toURI))
               for (signature <- signatures) {
                  createRecord(signature) match {
                     case symbol@ErlRecord(`recordName`, _) =>
                        return ErlangUtil.resolveDfn(fo, symbol)
                     case _ =>
                  }
               }
            }
         }
      }
      None
   }

   def queryMacro(includes:Seq[ErlInclude], macroName:String) :Option[AstDfn] = {
      /** search including headfiles */
      for (include <- includes) {
         val name = if (include.isLib) "lib;" + include.path.replace('/', '.') else include.path.replace('/', '.')
         for (r <- queryFiles(name, QuerySupport.Kind.EXACT, ErlangIndexer.FIELD_MACRO)) {
            val signatures = r.getValues(ErlangIndexer.FIELD_MACRO)
            if (signatures != null) {
               val fo = FileUtil.toFileObject(new File(r.getUrl.toURI))
               for (signature <- signatures) {
                  createMacro(signature) match {
                     case symbol@ErlMacro(`macroName`, _, _) =>
                        return ErlangUtil.resolveDfn(fo, symbol)
                     case _ =>
                  }
               }
            }
         }
      }
      None
   }

   private def createFuntion(signature:String) :ErlFunction = {
      val groups = signature.split(";")
      val (name, arity, offset, endOffset) = groups match {
         case Array(nameX, _, arityX, offsetX, endOffsetX, _*) => (nameX, arityX.toInt, offsetX.toInt, endOffsetX.toInt)
         case Array(nameX, _*) => (nameX, 0, 0, 0)
      }
      val function = ErlFunction(None, name, arity)
      val args = for (i <- 5 until groups.length) yield groups(i)
      function
   }

   private def createInclude(signature:String) :ErlInclude = {
      val groups = signature.split(";")
      val (path, isLib, offset, endOffset) = groups match {
         case Array(pathX, _, isLibX, offsetX, endOffsetX, _*) => (pathX, isLibX.toBoolean, offsetX.toInt, endOffsetX.toInt)
         case Array(pathX, _*) => (pathX, false, 0, 0)
      }
      val include = ErlInclude(isLib, path)
      val url = queryPersistentUrl(path)
      include
   }

   private def createRecord(signature:String) :ErlRecord = {
      val groups = signature.split(";")
      val (name, arity, offset, endOffset) = groups match {
         case Array(nameX, _, arityX, offsetX, endOffsetX, _*) => (nameX, arityX.toInt, offsetX.toInt, endOffsetX.toInt)
         case Array(nameX, _*) => (nameX, 0, 0, 0)
      }
      val fields = (5 until groups.length).map{i => ErlRecordField(name, groups(i))}.toArray
      val record = ErlRecord(name, fields)
      record
   }

   private def createMacro(signature:String) :ErlMacro = {
      val groups = signature.split(";")
      val (name, arity, offset, endOffset) = groups match {
         case Array(nameX, _, arityX, offsetX, endOffsetX, _*) => (nameX, arityX.toInt, offsetX.toInt, endOffsetX.toInt)
         case Array(nameX, _*) => (nameX, 0, 0, 0)
      }
      val params = (5 until groups.length - 1).map{i => groups(i)}.toArray
      val body = groups(groups.length - 1)
      val macro = ErlMacro(name, params.toList, body)
      macro
   }
}

object ErlangIndex {
   val moduleToUrlBuf = new HashMap[String, URL]
   val definesBuf   = new ArrayBuffer[AstDfn]
   val functionsBuf = new ArrayBuffer[AstDfn]
   val includesBuf  = new ArrayBuffer[AstDfn]
   val recordsBuf   = new ArrayBuffer[AstDfn]

   val FIELDS_TO_LOAD_ALL = Array(ErlangIndexer.FIELD_FQN_NAME,
                                  ErlangIndexer.FIELD_FUNCTION)
    
   val EMPTY_INDEX_RESULT = Array[IndexResult]()
    
   private val EMPTY_INDEX = new ErlangIndex(null)

   def get(roots:Collection[FileObject]) :ErlangIndex = {
      try {
         new ErlangIndex(QuerySupport.forRoots(ErlangIndexer.NAME,
                                               ErlangIndexer.VERSION,
                                               roots.toArray(new Array[FileObject](roots.size)):_*))
      } catch {case ioe:IOException => EMPTY_INDEX}
   }
    
   def get(result:ParserResult) :ErlangIndex = {
      LexUtil.fileObject(result) match {
         case None => null
         case Some(fo) =>
            get(QuerySupport.findRoots(fo,
                                       Collections.singleton(ErlangLanguage.SOURCE),
                                       Collections.singleton(ErlangLanguage.BOOT),
                                       Collections.emptySet[String]))
      }
   }
}
