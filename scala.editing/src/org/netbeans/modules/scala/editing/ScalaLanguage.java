/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
package org.netbeans.modules.scala.editing;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.gsf.api.CodeCompletionHandler;
import org.netbeans.modules.gsf.api.DeclarationFinder;
import org.netbeans.modules.gsf.api.Formatter;
import org.netbeans.modules.gsf.api.Indexer;
import org.netbeans.modules.gsf.api.InstantRenamer;
import org.netbeans.modules.gsf.api.KeystrokeHandler;
import org.netbeans.modules.gsf.api.OccurrencesFinder;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.SemanticAnalyzer;
import org.netbeans.modules.gsf.api.StructureScanner;
import org.netbeans.modules.gsf.spi.DefaultLanguageConfig;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

public class ScalaLanguage extends DefaultLanguageConfig {

    private static FileObject scalaStubsFo;

    public ScalaLanguage() {
    }

    @Override
    public String getLineCommentPrefix() {
        return "//";
    }

    @Override
    public boolean isIdentifierChar(char c) {
        return Character.isJavaIdentifierPart(c) || (// Globals, fields and parameter prefixes (for blocks and symbols)
                c == '$') || (c == '@') || (c == '&') || (// Function name suffixes
                c == '!') || (c == '?') || (c == '=');

    }

    @Override
    public Language getLexerLanguage() {
        return ScalaTokenId.language();
    }

    @Override
    public Collection<FileObject> getCoreLibraries() {
        return Collections.singletonList(getScalaStubFo());
    }

    /** Don't need go to declaration inside these files...
     * 
     * @return scalaStubs's fo
     */
    public static FileObject getScalaStubFo() {
        if (scalaStubsFo == null) {
            // Core classes: Stubs for the "builtin" Scala libraries (Any, AnyRef, AnyVal, Int etc).
            File clusterFile = InstalledFileLocator.getDefault().locate(
                    "modules/org-netbeans-modules-scala-editing.jar", null, false);

            if (clusterFile != null) {
                File scalaStubs =
                        new File(clusterFile.getParentFile().getParentFile().getAbsoluteFile(),
                        "scalastubs"); // NOI18N
                assert scalaStubs.exists() && scalaStubs.isDirectory() : "No stubs found";
                scalaStubsFo = FileUtil.toFileObject(scalaStubs);
            } else {
                // During test?
                // HACK - TODO use mock
                String scalaDir = System.getProperty("xtest.scala.home");
                if (scalaDir == null) {
                    throw new RuntimeException("xtest.scala.home property has to be set when running within binary distribution");
                }
                File scalaStubs = new File(scalaDir + File.separator + "scalastubs");
                if (scalaStubs.exists()) {
                    scalaStubsFo = FileUtil.toFileObject(scalaStubs);
                }
            }
        }

        return scalaStubsFo;
    }

    @Override
    public String getDisplayName() {
        return "Scala";
    }

    @Override
    public String getPreferredExtension() {
        return "scala"; // NOI18N

    }

    // Service Registrations
    
    @Override
    public KeystrokeHandler getKeystrokeHandler() {
        return new ScalaBracketCompleter();
    }

    @Override
    public boolean hasFormatter() {
        return true;
    }

    @Override
    public Formatter getFormatter() {
        return new ScalaFormatter();
    }

    @Override
    public Parser getParser() {
        return new ScalaParser();
    }

    @Override
    public CodeCompletionHandler getCompletionHandler() {
        return new ScalaCodeCompletion();
    }

    @Override
    public boolean hasStructureScanner() {
        return true;
    }

    @Override
    public StructureScanner getStructureScanner() {
        return new ScalaStructureAnalyzer();
    }

    @Override
    public Indexer getIndexer() {
        return new ScalaIndexer();
    }

    @Override
    public DeclarationFinder getDeclarationFinder() {
        return new ScalaDeclarationFinder();
    }

    @Override
    public SemanticAnalyzer getSemanticAnalyzer() {
        return new ScalaSemanticAnalyzer();
    }

    @Override
    public boolean hasOccurrencesFinder() {
        return true;
    }

    @Override
    public OccurrencesFinder getOccurrencesFinder() {
        return new ScalaOccurrencesFinder();
    }

    @Override
    public InstantRenamer getInstantRenamer() {
        return new ScalaInstantRenamer();
    }
}
