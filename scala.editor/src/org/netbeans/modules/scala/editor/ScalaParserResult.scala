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
package org.netbeans.modules.scala.editor

import _root_.java.io.File
import _root_.java.util.{ArrayList, Collections}
import org.netbeans.api.lexer.TokenHierarchy
import org.netbeans.modules.csl.api.{Error, OffsetRange}
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.parsing.api.Snapshot
import org.netbeans.modules.scala.editor.ast.AstRootScope
import org.netbeans.modules.scala.editor.ast.AstTreeVisitor
import org.openide.filesystems.{FileObject, FileUtil}
import _root_.scala.tools.nsc.CompilationUnits.CompilationUnit
import _root_.scala.tools.nsc.Global
import _root_.scala.tools.nsc.io.{AbstractFile, PlainFile, VirtualFile}
import _root_.scala.tools.nsc.util.BatchSourceFile

/**
 *
 * @author Caoyuan Deng
 */
class ScalaParserResult(val parser:ScalaParser,
                        snapshot:Snapshot, 
                        val rootScope:AstRootScope,
                        var errors:List[Error]) extends ParserResult(snapshot) {

  var source :String = _
  var sanitizedRange = OffsetRange.NONE
  /**
   * Return whether the source code for the parse result was "cleaned"
   * or "sanitized" (modified to reduce chance of parser errors) or not.
   * This method returns OffsetRange.NONE if the source was not sanitized,
   * otherwise returns the actual sanitized range.
   */
  var sanitizedContents :String = _
  private var sanitized :ScalaParser.Sanitize = _
  var commentsAdded :Boolean = _
  private var rootScopeForDebugger :AstRootScope = _

  override protected def invalidate :Unit = {
    // XXX: what exactly should we do here?
  }

  override def getDiagnostics :_root_.java.util.List[_ <: Error] = {
    return if (errors == null) Collections.emptyList[Error] else errors
  }

  def rootScopeForDebugger :AstRootScope = {
    if (rootScopeForDebugger == null) {
      val fo = getSnapshot.getSource.getFileObject
      val file :File = if (fo != null) FileUtil.toFile(fo) else null
      // We should use absolutionPath here for real file, otherwise, symbol.sourcefile.path won't be abs path
      //val filePath = if (file != null) file.getAbsolutePath) : "<current>";
      val th = getSnapshot.getTokenHierarchy

      val global = parser.global

      val af = if (file != null) new PlainFile(file) else new VirtualFile("<current>", "")
      val srcFile = new BatchSourceFile(af, getSnapshot.getText.toString.toCharArray)
      try {
        val unit = ScalaGlobal.compileSourceForDebugger(parser.global, srcFile)
        rootScopeForDebugger = new AstTreeVisitor(global, unit, th, srcFile).getRootScope
      } catch {
        case ex:AssertionError =>
          // avoid scala nsc's assert error
          ScalaGlobal.reset
        case ex:_root_.java.lang.Error =>
          // avoid scala nsc's exceptions
        case ex:IllegalArgumentException =>
          // An internal exception thrown by ParserScala, just catch it and notify
        case ex:Exception =>
          // Scala's global throws too many exceptions
          //ex.printStackTrace();
      }
    }

    rootScopeForDebugger
  }

  /**
   * Set the range of source that was sanitized, if any.
   */
  def setSanitized(sanitized:ScalaParser.Sanitize, sanitizedRange:OffsetRange, sanitizedContents:String) :Unit = {
    this.sanitized = sanitized
    this.sanitizedRange = sanitizedRange
    this.sanitizedContents = sanitizedContents
  }

  def getSanitized :ScalaParser.Sanitize = {
    return sanitized
  }

  override def toString = {
    "ParserResult(file=" + getSnapshot.getSource.getFileObject + ",rootScope=" + rootScope
  }
}
