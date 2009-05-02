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

import _root_.java.util.{Collections,HashSet,Set}

import javax.swing.text.Document
import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy}
import org.netbeans.modules.csl.api.{ElementKind,InstantRenamer,OffsetRange}
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.erlang.editor.ast.{AstDfn,AstItem,AstRootScope}
import org.netbeans.modules.erlang.editor.lexer.LexUtil
import org.openide.util.NbBundle;

/**
 * Handle instant rename
 *
 * @author Caoyuan Deng
 */
class ErlangInstantRenamer extends InstantRenamer {
   import ElementKind._
   def isRenameAllowed(result:ParserResult, caretOffset:Int, explanationRetValue:Array[String]) :Boolean = {
      val pResult = result match {
         case null => return false
         case x:ErlangParserResult => x
      }

      val bool = for (rootScope <- pResult.rootScope;
                      th <- LexUtil.tokenHierarchy(pResult);
                      doc <- LexUtil.document(pResult, true);
                      closest <- rootScope.findItemAt(th, caretOffset)
      ) yield rootScope.findDfnOf(closest) match {
         case None => false
         case Some(x) => x.getKind match {
               case FIELD | PARAMETER | VARIABLE | METHOD | CALL => true
               case _ => false
            }
      }
        
      bool match {
         case None => false
         case Some(x) => x
      }
   }

   def getRenameRegions(result:ParserResult, caretOffset:Int) :Set[OffsetRange] = {
      val pResult = result match {
         case null => return Collections.emptySet[OffsetRange]
         case x:ErlangParserResult => x
      }

      val regions = for (rootScope <- pResult.rootScope;
                         th <- LexUtil.tokenHierarchy(pResult);
                         doc <- LexUtil.document(pResult, true);
                         closest <- rootScope.findItemAt(th, caretOffset)
      ) yield {
         var regions1 = new HashSet[OffsetRange]
         val occurrences = rootScope.findOccurrences(closest)

         for (item <- occurrences;
              idToken <- item.idToken
         ) {
            // detect special case for function
            val functionDfn = item match {
               case aDfn:AstDfn => aDfn.functionDfn
               case _ => None
            }
            functionDfn match {
               case Some(x) =>
                  for (clause <- x.functionClauses;
                       clauseIdToken <- clause.idToken
                  ) {
                     regions1.add(LexUtil.rangeOfToken(th, clauseIdToken))
                  }
               case _ =>
                  regions1.add(LexUtil.rangeOfToken(th, idToken))
            }
         }

         if (regions1.size > 0) {
            val translated = new HashSet[OffsetRange](2 * regions1.size)
            val entries = regions1.iterator
            while (entries.hasNext) {
               LexUtil.lexerOffsets(pResult, entries.next) match {
                  case OffsetRange.NONE =>
                  case lexRange => translated.add(lexRange)
               }
            }
            translated
         } else regions1
      }
        
      regions match {
         case None => Collections.emptySet[OffsetRange]
         case Some(x) => x
      }
   }
}