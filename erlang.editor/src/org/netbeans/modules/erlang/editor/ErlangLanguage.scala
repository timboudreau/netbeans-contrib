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

import _root_.java.io.File
import _root_.java.util.Collection
import _root_.java.util.Collections
import _root_.java.util.HashMap
import _root_.java.util.Map
import _root_.java.util.Set
import org.netbeans.api.lexer.Language;
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
//import org.openide.modules.InstalledFileLocator

/*
 * Language/lexing configuration for Erlang
 *
 * @author Caoyuan Deng
 */
class ErlangLanguage extends DefaultLanguageConfig {

  //private FileObject jsStubsFO;
  override
  def getLexerLanguage = ErlangTokenId.language
    

  //    @Override
  //    public String getLineCommentPrefix() {
  //        return JsUtils.getLineCommentPrefix();
  //    }
  //
  //    @Override
  //    public boolean isIdentifierChar(char c) {
  //        return JsUtils.isIdentifierChar(c);
  //    }
  //
  //
  //    @Override
  //    public Collection<FileObject> getCoreLibraries() {
  //        FileObject f = getJsStubs();
  //        return f != null ? Collections.singleton(f) : Collections.<FileObject>emptySet();
  //    }

  // TODO - add classpath recognizer for these ? No, don't need go to declaration inside these files...
  //    private FileObject getJsStubs() {
  //        if (jsStubsFO == null) {
  //            // Core classes: Stubs generated for the "builtin" Ruby libraries.
  //            File clusterFile = InstalledFileLocator.getDefault().locate(
  //                    "modules/org-netbeans-modules-javascript-editing.jar", null, false);
  //
  //            if (clusterFile != null) {
  //                File jsStubs =
  //                        new File(clusterFile.getParentFile().getParentFile().getAbsoluteFile(),
  //                        "jsstubs"); // NOI18N
  //                assert jsStubs.exists() && jsStubs.isDirectory() : "No stubs found";
  //                jsStubsFO = FileUtil.toFileObject(jsStubs);
  //            } else {
  //                // During test?
  //                // HACK - TODO use mock
  //                String jsDir = System.getProperty("xtest.js.home");
  //                if (jsDir == null) {
  //                    throw new RuntimeException("xtest.js.home property has to be set when running within binary distribution");
  //                }
  //                File jsStubs = new File(jsDir + File.separator + "jsstubs");
  //                if (jsStubs.exists()) {
  //                    jsStubsFO = FileUtil.toFileObject(jsStubs);
  //                }
  //            }
  //        }
  //
  //        return jsStubsFO;
  //    }
    
  override
  def getDisplayName : String =  "Erlang"
    
  override
  def getPreferredExtension : String = {
    "erl" // NOI18N
  }
    
  def getSourceGroupNames : Map[String, String] = {
    val sourceGroups = new HashMap[String, String]

    sourceGroups.put("J2SEProject", "java") // NOI18N
        
    sourceGroups
  }

  def getBinaryPathIds : Set[String] = {
    // We don't really have libraries in binary form. IDE bundled javascript
    // libraries are simply extracted to a project among its original sources
    // in a special folder.
    Collections.emptySet()
  }

  override
  def getSourcePathIds : Set[String] = {
    // We don't have our own source path id, because javascript files can be
    // anywhere in a project. So, for index search we will use all available
    // sourcepath ids.
    null
  }


  // Service Registrations
    
  //    @Override
  //    public KeystrokeHandler getKeystrokeHandler() {
  //        return new JsKeystrokeHandler();
  //    }

  override def hasFormatter : boolean = false

  //    @Override
  //    public Formatter getFormatter() {
  //        return new JsFormatter();
  //    }

  //    @Override
  //    public Parser getParser() {
  //        return new JsParser();
  //    }
    
  //    @Override
  //    public CodeCompletionHandler getCompletionHandler() {
  //        return new JsCodeCompletion();
  //    }
  //
  //    @Override
  //    public boolean hasStructureScanner() {
  //        return true;
  //    }
  //
  //    @Override
  //    public StructureScanner getStructureScanner() {
  //        return new JsAnalyzer();
  //    }

  //    @Override
  //    public EmbeddingIndexerFactory getIndexerFactory() {
  //        return new JsIndexer.Factory();
  //    }

  //    @Override
  //    public DeclarationFinder getDeclarationFinder() {
  //        return new JsDeclarationFinder();
  //    }
  //
  //    @Override
  //    public SemanticAnalyzer getSemanticAnalyzer() {
  //        return new JsSemanticAnalyzer();
  //    }

  //    @Override
  //    public boolean hasOccurrencesFinder() {
  //        return true;
  //    }

  //    @Override
  //    public OccurrencesFinder getOccurrencesFinder() {
  //        return new JsOccurrenceFinder();
  //    }
  //
  //    @Override
  //    public InstantRenamer getInstantRenamer() {
  //        return new JsRenameHandler();
  //    }
  //
  //    @Override
  //    public IndexSearcher getIndexSearcher() {
  //        return new JsTypeSearcher();
  //    }
}
