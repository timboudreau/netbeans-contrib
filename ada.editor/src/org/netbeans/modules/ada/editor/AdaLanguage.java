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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.ada.editor;

import org.netbeans.modules.ada.editor.navigator.AdaInstantRenamer;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.CodeCompletionHandler;
import org.netbeans.modules.gsf.api.Indexer;
import org.netbeans.modules.gsf.api.SemanticAnalyzer;
import org.netbeans.modules.gsf.api.StructureScanner;
import org.netbeans.modules.gsf.api.DeclarationFinder;
import org.netbeans.modules.gsf.api.OccurrencesFinder;
import org.netbeans.modules.gsf.api.Formatter;
import org.netbeans.modules.gsf.api.KeystrokeHandler;
import org.netbeans.modules.gsf.api.InstantRenamer;
import org.netbeans.modules.gsf.api.HintsProvider;
import org.netbeans.modules.gsf.spi.DefaultLanguageConfig;
import org.netbeans.modules.ada.editor.lexer.AdaTokenId;
import org.netbeans.modules.ada.editor.parser.AdaStructureScanner;
import org.netbeans.modules.ada.editor.parser.AdaParser;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaLanguage extends DefaultLanguageConfig {

    public AdaLanguage() {
    }

    @Override
    public Language getLexerLanguage() {
        return AdaTokenId.language();
    }

    @Override
    public String getDisplayName() {
        return "Ada";
    }

    //
    // Service Registrations
    //
    
    @Override
    public Parser getParser() {
        return new AdaParser();
    }

    @Override
    public boolean hasFormatter() {
        return false;
    }

    @Override
    public Formatter getFormatter() {
        return null;
        //return new AdaFormatter();
    }

    @Override
    public KeystrokeHandler getKeystrokeHandler() {
        return null;
        //return new AdaBracketCompleter();
    }

    @Override
    public CodeCompletionHandler getCompletionHandler() {
        return null;
        //return new AdaCodeCompletion();
    }

    @Override
    public SemanticAnalyzer getSemanticAnalyzer() {
        return null;
        //return new AdaSemanticAnalyzer();
    }

    @Override
    public boolean hasOccurrencesFinder() {
        return false;
    }

    @Override
    public OccurrencesFinder getOccurrencesFinder() {
        return null;
        //return new AdaOccurrencesFinder();
    }

    @Override
    public StructureScanner getStructureScanner() {
        return new AdaStructureScanner();
    }

    @Override
    public Indexer getIndexer() {
        return null;
//        return new AdaIndexer();
    }

    @Override
    public boolean hasHintsProvider() {
        return false;
    }

    @Override
    public HintsProvider getHintsProvider() {
        return null;
        //return new AdaHintsProvider();
    }

    @Override
    public DeclarationFinder getDeclarationFinder() {
        return null;
//        return new AdaDeclarationFinder();
    }

    @Override
    public InstantRenamer getInstantRenamer() {
        return new AdaInstantRenamer();
    }

}
