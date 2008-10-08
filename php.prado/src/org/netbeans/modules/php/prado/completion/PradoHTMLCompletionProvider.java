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
package org.netbeans.modules.php.prado.completion;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.php.prado.lexer.LexerUtilities;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author Petr Pisl
 */
public class PradoHTMLCompletionProvider implements CompletionProvider {

    public PradoHTMLCompletionProvider() {
        System.out.println("#############Vytvorena instance");
    }

    public CompletionTask createTask(int queryType, JTextComponent component) {
        AsyncCompletionTask task = null;
        if ((queryType & COMPLETION_QUERY_TYPE & COMPLETION_ALL_QUERY_TYPE) != 0) {
            task = new AsyncCompletionTask(new Query(), component);
        }
        System.out.println("create new query");
//        else if (queryType == DOCUMENTATION_QUERY_TYPE) {
//            task = new AsyncCompletionTask(new DocQuery(null), component);
//        }
        return task;
    }

    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        System.out.println("getAutoQueryTypes");
        return 0;
    }

    static class Query extends AsyncCompletionQuery {

        @Override
        protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
            System.out.println("query");
            //test whether we are just in text and eventually close the opened completion
            //this is handy after end tag autocompletion when user doesn't complete the
            //end tag and just types a text
            //test whether the user typed an ending quotation in the attribute value
            BaseDocument document = (BaseDocument) doc;
            document.readLock();
            try {
                TokenSequence<HTMLTokenId> ts = LexerUtilities.getHTMLTokenSequence(doc, caretOffset);
                if (ts != null) {
                    System.out.println("in html");
                    ts.move(caretOffset);
                    // to be sure that ts has  a tokens
                    if (ts.movePrevious() || ts.moveNext()) {
                        Token token = ts.token();
                        String preText = "";
                        if ( (caretOffset - ts.offset()) > 0 ) {
                            preText = token.text().subSequence(0, caretOffset - ts.offset() - 1).toString();
                        }
                        //if (preText.endsWith("<")) {
//                            if (token.id() == HTMLTokenId.TEXT) {
                                resultSet.addItem(new PradoHTMLCompletionItem("com:TTest"));
//                            }
                        //}
                        

                    }
                }
                else {
                    System.out.println("ne html");
                }
            } finally {
                document.readUnlock();
            }

        resultSet.finish ();
    }
}

}
