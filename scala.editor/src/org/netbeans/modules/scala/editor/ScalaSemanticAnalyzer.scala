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

import javax.swing.text.Document
import org.netbeans.api.lexer.{Token, TokenHierarchy, TokenId}
import org.netbeans.api.language.util.ast.{AstDfn, AstRef, AstItem}
import org.netbeans.modules.csl.api.{ElementKind, Modifier, ColoringAttributes, OffsetRange, SemanticAnalyzer}
import org.netbeans.modules.parsing.spi.{Parser, Scheduler, SchedulerEvent}
import org.netbeans.modules.scala.editor.ast.{ScalaDfns, ScalaRefs, ScalaRootScope}
import org.netbeans.modules.scala.editor.lexer.{ScalaLexUtil, ScalaTokenId}

import scala.tools.nsc.symtab.Flags

/**
 *
 * @author Caoyuan Deng
 */
class ScalaSemanticAnalyzer extends SemanticAnalyzer[ScalaParserResult] {

  private var cancelled: Boolean = _
  private var semanticHighlights: _root_.java.util.Map[OffsetRange, _root_.java.util.Set[ColoringAttributes]] = _

  protected final def isCancelled: Boolean = synchronized {
    cancelled
  }

  protected final def resume: Unit = synchronized {
    cancelled = false
  }

  override def getHighlights: _root_.java.util.Map[OffsetRange, _root_.java.util.Set[ColoringAttributes]] = {
    semanticHighlights
  }

  override def getPriority: Int = 0

  override def getSchedulerClass: Class[_ <: Scheduler] = {
    Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER
  }

  override def cancel: Unit = {
    cancelled = true
  }

  @throws(classOf[Exception])
  override def run(info: ScalaParserResult, event: SchedulerEvent): Unit = {
    resume

    if (isCancelled) {
      return
    }

    val pResult = info match {
      case null => return
      case x: ScalaParserResult => x
    }

    if (isCancelled) {
      return
    }

    val rootScope = pResult.rootScope match {
      case None => return
      case Some(x) => x
    }

    val th = pResult.getSnapshot.getTokenHierarchy
    val doc = info.getSnapshot.getSource.getDocument(true)
    if (doc == null) {
      return
    }

    val highlights = new _root_.java.util.HashMap[OffsetRange, _root_.java.util.Set[ColoringAttributes]](100)
    //visitScopeRecursively(doc, th, rootScope, highlights);
    visitItems(th, rootScope, highlights)

    this.semanticHighlights = if (!highlights.isEmpty) {
      //            if (result.getTranslatedSource() != null) {
      //                Map<OffsetRange, ColoringAttributes> translated = new HashMap<OffsetRange, ColoringAttributes>(2 * highlights.size());
      //                for (Map.Entry<OffsetRange, ColoringAttributes> entry:  highlights.entrySet()) {
      //                    OffsetRange range = LexUtilities.getLexerOffsets(info, entry.getKey());
      //                    if (range != OffsetRange.NONE) {
      //                        translated.put(range, entry.getValue());
      //                    }
      //                }
      //
      //                highlights = translated;
      //            }

      highlights
    } else null
  }

  val DEPRECATED = new _root_.java.util.HashSet[ColoringAttributes]
  DEPRECATED.add(ColoringAttributes.DEPRECATED)
  val IMPLICIT = new _root_.java.util.HashSet[ColoringAttributes]
  IMPLICIT.add(ColoringAttributes.INTERFACE)

  private def visitItems(th: TokenHierarchy[_], rootScope: ScalaRootScope,
                         highlights: _root_.java.util.Map[OffsetRange, _root_.java.util.Set[ColoringAttributes]]): Unit =
  {
    for (items <- rootScope.idTokenToItems(th).valuesIterator;
         item <- items;
         idToken <- item.idToken;
         name = item.getName;
         if name != "this" && name != "super")
    {
      // token may be xml tokens, @see AstVisit#getTokenId
      idToken.id match {
        case ScalaTokenId.Identifier | ScalaTokenId.This | ScalaTokenId.Super =>
          val hiRange = ScalaLexUtil.getRangeOfToken(th, idToken)
          item match {
            case dfn: ScalaDfns#ScalaDfn =>
              dfn.symbol match {
                case sym if sym.isModule =>
                  highlights.put(hiRange, ColoringAttributes.CLASS_SET)
                case sym if sym.isClass =>
                  highlights.put(hiRange, ColoringAttributes.CLASS_SET)
                case sym if sym.isMethod =>
                  highlights.put(hiRange, ColoringAttributes.METHOD_SET)
                case _ =>
              }
            case ref: ScalaRefs#ScalaRef =>
              ref.symbol match {
                case sym if sym.isClass =>
                  highlights.put(hiRange, ColoringAttributes.STATIC_SET)
                case sym if sym.isModule =>
                  highlights.put(hiRange, ColoringAttributes.GLOBAL_SET)
                case sym if sym.isConstructor =>
                  highlights.put(hiRange, ColoringAttributes.STATIC_SET)
                case sym if sym.isMethod  =>
                  if (ref.getKind == ElementKind.RULE) {
                    // * implicit call
                    highlights.put(hiRange, IMPLICIT)
                  } else {
                    highlights.put(hiRange, ColoringAttributes.FIELD_SET)
                  }
                case sym if sym.hasFlag(Flags.IMPLICIT) =>
                  highlights.put(hiRange, IMPLICIT)
                case _ =>
              }
          }

          if (item.getModifiers.contains(Modifier.DEPRECATED)) {
            highlights.put(hiRange, DEPRECATED)
          }
        case _ =>
      }

    }
    
  }
}
