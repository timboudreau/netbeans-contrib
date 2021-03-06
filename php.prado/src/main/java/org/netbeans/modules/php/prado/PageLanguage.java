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

package org.netbeans.modules.php.prado;

import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParseEvent;
import org.netbeans.modules.gsf.api.ParseListener;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.PositionManager;
import org.netbeans.modules.gsf.api.SemanticAnalyzer;
import org.netbeans.modules.gsf.api.StructureScanner;
import org.netbeans.modules.php.prado.completion.PageCodeCompletion;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.gsf.api.CodeCompletionHandler;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.spi.DefaultLanguageConfig;
import org.netbeans.modules.php.prado.gsf.PageSemanticAnalyzer;
import org.netbeans.modules.php.prado.gsf.PradoStructureScanner;
import org.netbeans.modules.php.prado.lexer.PageTokenId;



/**
 *
 * @author Petr Pisl
 */
public class PageLanguage extends DefaultLanguageConfig {
    
    public static final String PHP_PRADO_MIME_TYPE = "text/x-prado"; // NOI18N
  
    @Override
    public Language getLexerLanguage() {
        return PageTokenId.language();
    }

    @Override
    public String getDisplayName() {
        // TODO have to be done correctly from bundle.
        return "Prado Page File";   //NOI18N
    }

    @Override
    public CodeCompletionHandler getCompletionHandler() {
        return new PageCodeCompletion();
    }

    @Override
    public boolean hasStructureScanner() {
        return true;
    }

    @Override
    public StructureScanner getStructureScanner() {
        return new PradoStructureScanner();
    }

    @Override
    public Parser getParser() {
        return new PradoParser();
        //return null;
    }

    @Override
    public SemanticAnalyzer getSemanticAnalyzer() {
        return new PageSemanticAnalyzer();
    }

    public static String getComponentPrefix() {
        return "com";  //NOI18N
    }


    private class PradoParser implements Parser {

        public void parseFiles(Job request) {
            ParseListener listener = request.listener;


            for (ParserFile file : request.files) {
                ParseEvent beginEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, null);
                listener.started(beginEvent);
                ParseEvent doneEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, new PradoParserResult(this, file));
                listener.finished(doneEvent);
            }
        }

        public PositionManager getPositionManager() {
            return new PradoPositionManager();
        }

    }

    private class PradoParserResult extends ParserResult {

        public PradoParserResult(PradoParser parser, ParserFile file) {
            super(parser, file, PHP_PRADO_MIME_TYPE);
        }

        @Override
        public AstTreeNode getAst() {
            return null;
        }
    }

    private class PradoPositionManager implements PositionManager {


        public PradoPositionManager() {

        }

        public OffsetRange getOffsetRange(CompilationInfo info, ElementHandle object) {

            return OffsetRange.NONE;
        }

    }
}
