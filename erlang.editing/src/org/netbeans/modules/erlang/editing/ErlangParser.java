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
package org.netbeans.modules.erlang.editing;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.Language;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;
import org.netbeans.api.languages.LanguagesManager;
import org.netbeans.api.languages.ParseException;
import org.netbeans.api.languages.ParserManager;
import org.netbeans.api.languages.ParserManager.State;
import org.netbeans.api.languages.ParserManagerListener;
import org.openide.util.Exceptions;

/**
 *
 * @author dcaoyuan
 */
public class ErlangParser implements ParserManagerListener {

    private static ErlangParser parser;
    private static String mimeType = "text/erlang";
    
    public static ErlangParser get() throws LanguageDefinitionNotFoundException {
        if (parser == null) {
            parser = new ErlangParser();
        }
        return parser;
    }

    private Language language;
    private boolean finished;

    private ErlangParser() throws LanguageDefinitionNotFoundException {
        language = LanguagesManager.get().getLanguage(mimeType);
    }

    public ASTNode parse(String source, String sourceName) throws ParseException {
        Document doc = new PlainDocument();
        try {
            doc.insertString(0, source, null);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        doc.putProperty("mimeType", mimeType);

        ParserManager parserManager = ParserManager.get(doc);
        parserManager.addListener(this);

        ASTNode astRoot = parserManager.getAST();
        if (astRoot == null) {
            astRoot = ASTNode.create(language, "Root", 0);
        }
        return astRoot;
    }
    
    public void parsed (State state, ASTNode root) {
        if (state == State.OK) {
            finished = true;
        }
        
    } 
}
