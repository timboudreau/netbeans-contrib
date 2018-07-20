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
package org.netbeans.modules.rats.editor

import _root_.java.util.{Collections}
import org.netbeans.api.lexer.Language
import org.netbeans.modules.csl.api.{CodeCompletionHandler,
                                     DeclarationFinder,
                                     Formatter,
                                     IndexSearcher,
                                     InstantRenamer,
                                     KeystrokeHandler,
                                     OccurrencesFinder,
                                     SemanticAnalyzer,
                                     StructureScanner}
import org.netbeans.modules.csl.spi.DefaultLanguageConfig
import org.netbeans.modules.parsing.spi.Parser
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory
import org.openide.filesystems.{FileObject,
                                FileUtil}
import org.netbeans.modules.rats.editor.lexer.RatsTokenId

/**
 * Language/lexing configuration for Rats
 *
 * @author Caoyuan Deng
 */
class RatsLanguage extends DefaultLanguageConfig {
  import RatsLanguage._

  override def getLexerLanguage = RatsTokenId.language

  override def getLineCommentPrefix = "//" // NOI18N
 
  override def getDisplayName :String =  "Rats" // NOI18N
    
  override def getPreferredExtension :String = "rats" // NOI18N

  /**
   * @see org.netbeans.modules.rats.platform.RatsPlatformClassPathProvider and ModuleInstall
   */
  override def getLibraryPathIds = Collections.singleton(BOOT)

  override def getSourcePathIds = Collections.singleton(SOURCE)
    
  override def getParser = new RatsParser
  
  //   override def hasStructureScanner = true
  //
  //   override def getStructureScanner = new RatsStructureAnalyzer
  
  override def getSemanticAnalyzer = new RatsSemanticAnalyzer
  //
  //   override def hasOccurrencesFinder = true
  //
  //   override def getOccurrencesFinder = new RatsOccurrencesFinder
  //
  //   override def getKeystrokeHandler = new RatsKeystrokeHandler
  //
  //   override def hasFormatter =  true
  //
  //   override def getFormatter = new RatsFormatter
  //
  //   override def getInstantRenamer = new RatsInstantRenamer
  //
  //   override def getDeclarationFinder = new RatsDeclarationFinder
  //
  //   override def getIndexerFactory = new RatsIndexer.Factory
  //
  //   override def getCompletionHandler = new RatsCodeCompletion
}

object RatsLanguage {
  val BOOT    = "rats/classpath/boot"
  val COMPILE = "rats/classpath/compile"
  val EXECUTE = "rats/classpath/execute"
  val SOURCE  = "rats/classpath/source"
}
