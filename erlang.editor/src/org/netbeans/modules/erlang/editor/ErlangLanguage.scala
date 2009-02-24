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
package org.netbeans.modules.erlang.editor

import _root_.java.util.{Collections}
import org.netbeans.api.lexer.Language
import org.netbeans.modules.csl.api.CodeCompletionHandler
import org.netbeans.modules.csl.api.DeclarationFinder
import org.netbeans.modules.csl.api.Formatter
import org.netbeans.modules.csl.api.IndexSearcher
import org.netbeans.modules.csl.api.InstantRenamer
import org.netbeans.modules.csl.api.KeystrokeHandler
import org.netbeans.modules.csl.api.OccurrencesFinder
import org.netbeans.modules.csl.api.SemanticAnalyzer
import org.netbeans.modules.csl.api.StructureScanner
import org.netbeans.modules.csl.spi.DefaultLanguageConfig
import org.netbeans.modules.parsing.spi.Parser
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory
import org.openide.filesystems.FileObject
import org.openide.filesystems.FileUtil
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId

/*
 * Language/lexing configuration for Erlang
 *
 * @author Caoyuan Deng
 */
class ErlangLanguage extends DefaultLanguageConfig {
    import ErlangLanguage._

    override
    def getLexerLanguage = ErlangTokenId.language

    override
    def getLineCommentPrefix = "%" // NOI18N
 
    override
    def getDisplayName :String =  "Erlang" // NOI18N
    
    override
    def getPreferredExtension :String = "erl" // NOI18N
    
    override
    def getParser = new ErlangParser

    override
    def hasStructureScanner = true

    override
    def getStructureScanner = new ErlangStructureAnalyzer

    override
    def getSemanticAnalyzer = new ErlangSemanticAnalyzer

    override
    def hasOccurrencesFinder = true

    override
    def getOccurrencesFinder = new ErlangOccurrencesFinder

    override
    def getKeystrokeHandler = new ErlangKeystrokeHandler

    override
    def hasFormatter =  true

    override
    def getFormatter = new ErlangFormatter

    override
    def getInstantRenamer = new ErlangInstantRenamer

    override
    def getDeclarationFinder = new ErlangDeclarationFinder

    /** @see org.netbeans.modules.erlang.platform.ErlangPlatformClassPathProvider and ModuleInstall */
    override
    def getLibraryPathIds = Collections.singleton(BOOT_CP)

    override
    def getIndexerFactory = new ErlangIndexer.Factory
}

object ErlangLanguage {
    val BOOT_CP = "ErlangOtpLibBootClassPath"
}
