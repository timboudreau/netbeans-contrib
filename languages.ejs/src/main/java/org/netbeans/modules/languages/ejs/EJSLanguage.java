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
package org.netbeans.modules.languages.ejs;


import org.netbeans.api.lexer.Language;
import org.netbeans.modules.gsf.api.DeclarationFinder;
import org.netbeans.modules.gsf.api.IndexSearcher;
import org.netbeans.modules.gsf.api.Indexer;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.StructureScanner;
import org.netbeans.modules.gsf.api.StructureScanner.Configuration;
import org.netbeans.modules.javascript.editing.JsAnalyzer;
import org.netbeans.modules.javascript.editing.JsLanguage;
import org.netbeans.modules.javascript.editing.JsonAnalyzer;
import org.netbeans.modules.languages.ejs.lexer.api.EJSTokenId;

public class EJSLanguage extends JsLanguage {
    public EJSLanguage() {
    }

    @Override
    public Language getLexerLanguage() {
        return EJSTokenId.language();
    }

    @Override
    public String getDisplayName() {
        return "EJS";
    }

    @Override
    public String getPreferredExtension() {
        return "ejs"; // NOI18N
    }

    @Override
    public Indexer getIndexer() {
        // No JSON indexing
        return null;
    }

    @Override
    public IndexSearcher getIndexSearcher() {
        return null;
    }

    @Override
    public Parser getParser() {
        // We don't parse EJS directly; embedded sections will be parsed
        // by the embedded JavaScript language
        return null;
    }

    @Override
    public boolean hasStructureScanner() {
        return true;
    }

    @Override
    public StructureScanner getStructureScanner() {
        return new EjsScanner();
    }

    private class EjsScanner extends JsAnalyzer {
        @Override
        public Configuration getConfiguration() {
            return new Configuration(false, false, 0);
        }
    }

    @Override
    public DeclarationFinder getDeclarationFinder() {
        return null;
    }
}
