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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.scala.core

import java.io.File
import org.netbeans.modules.csl.api.{Error, OffsetRange}
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.parsing.api.Snapshot
import org.netbeans.modules.scala.core.ast.ScalaRootScope
import org.openide.filesystems.{FileUtil}
import scala.collection.mutable.WeakHashMap
import scala.tools.nsc.io.{PlainFile, VirtualFile}
import scala.tools.nsc.util.{BatchSourceFile, SourceFile}

/**
 *
 * @author Caoyuan Deng
 */
class ScalaParserResult(snapshot: Snapshot,
                        val global: ScalaGlobal,
                        val rootScope: Option[ScalaRootScope],
                        var errors: java.util.List[Error],
                        val srcFile: SourceFile
) extends ParserResult(snapshot) {
  assume(global != null)

  if (ScalaParserResult.debug) {
    ScalaParserResult.unreleasedResults.put(this, srcFile.file.path)
    println("==== unreleased parser results: ")
    for ((k, v) <- ScalaParserResult.unreleasedResults) println(v)
  }

  var source: String = _
  var sanitizedRange = OffsetRange.NONE
  /**
   * Return whether the source code for the parse result was "cleaned"
   * or "sanitized" (modified to reduce chance of parser errors) or not.
   * This method returns OffsetRange.NONE if the source was not sanitized,
   * otherwise returns the actual sanitized range.
   */
  var sanitizedContents: String = _
  var commentsAdded: Boolean = _
  private var sanitized: ScalaParser.Sanitize = _
  private var rootScopeForDebug: Option[ScalaRootScope] = _

  override protected def invalidate: Unit = {
    // XXX: what exactly should we do here?
  }

  override def getDiagnostics: java.util.List[_ <: Error] = {
    if (errors == null) {
      java.util.Collections.emptyList[Error]
    } else {
      errors
    }
  }

  def getRootScopeForDebug: Option[ScalaRootScope] = {
    if (rootScopeForDebug == null) {
      val fo = getSnapshot.getSource.getFileObject
      val file: File = if (fo != null) FileUtil.toFile(fo) else null
      // We should use absolutionPath here for real file, otherwise, symbol.sourcefile.path won't be abs path
      //val filePath = if (file != null) file.getAbsolutePath):  "<current>";
      val th = getSnapshot.getTokenHierarchy

      val global = ScalaGlobal.getGlobal(fo, true)

      val af = if (file != null) new PlainFile(file) else new VirtualFile("<current>", "")
      val srcFile = new BatchSourceFile(af, getSnapshot.getText.toString.toCharArray)
      try {
        //rootScopeForDebug = Some(global.askForDebug(srcFile, th))
        rootScopeForDebug = Some(global.compileSourceForDebug(srcFile, th))
      } catch {
        case ex: AssertionError =>
          // avoid scala nsc's assert error
          ScalaGlobal.resetLate(global, ex)
        case ex: java.lang.Error =>
          // avoid scala nsc's exceptions
        case ex: IllegalArgumentException =>
          // An internal exception thrown by ParserScala, just catch it and notify
        case ex: Exception =>
          // Scala's global throws too many exceptions
          //ex.printStackTrace)
      }
    }

    rootScopeForDebug
  }

  /**
   * Set the range of source that was sanitized, if any.
   */
  def setSanitized(sanitized: ScalaParser.Sanitize, sanitizedRange: OffsetRange, sanitizedContents: String): Unit = {
    this.sanitized = sanitized
    this.sanitizedRange = sanitizedRange
    this.sanitizedContents = sanitizedContents
  }

  def getSanitized: ScalaParser.Sanitize = {
    sanitized
  }

  override def toString = {
    "ParserResult(file=" + getSnapshot.getSource.getFileObject + ",rootScope=" + rootScope
  }
}

object ScalaParserResult {
  // ----- for debug
  private val debug = false
  private val unreleasedResults = new WeakHashMap[ScalaParserResult, String]
}

