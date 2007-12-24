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

package org.netbeans.modules.scala.editing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.text.AbstractDocument;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.languages.CharInput;
import org.netbeans.api.languages.CompletionItem;
import org.netbeans.api.languages.Context;
import org.netbeans.api.languages.Language;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;
import org.netbeans.api.languages.LanguagesManager;
import org.netbeans.api.languages.LibrarySupport;
import org.netbeans.api.languages.SyntaxContext;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Caoyuan Deng
 */
public class Scala {

    private static final String DOC = "org/netbeans/modules/scala/editing/Documentation.xml";
    private static final String MIME_TYPE = "text/x-scala";

    public static Object[] parseXmlStart(CharInput input) {
        if (input.read() != '<') {
            throw new InternalError();
        }

        int start = input.getIndex();
        try {
            Language language = LanguagesManager.get().getLanguage(MIME_TYPE);
            while (!input.eof () && input.next () != '<') {
                if (input.next () == '\r' ||
                    input.next () == '\n'
                ) {
                    input.setIndex (start);
                    return new Object[] {
                        ASTToken.create (language, "js_operator", "", 0, 0, null),
                        null
                    };
                }
                if (input.next () == '\\')
                    input.read ();
                input.read ();
            }
            
            
            
            boolean isStop = false;
            if (input.eof()) {
                isStop = true;
            } else {
                char next = input.next();
                if (next == ' ' || next == '\t' || next == '\r' || next == '\n' || next == '%' || next == '-') {
                    isStop = true;
                }
            }

            if (isStop) {
                input.setIndex(start);
                return new Object[]{
                    ASTToken.create(language, "stop", ".", 0, 0, null),
                    null
                };
            } else {
                input.setIndex(start);
                return new Object[]{
                    ASTToken.create(language, "dot", ".", 0, 0, null),
                    null
                };
            }
        } catch (LanguageDefinitionNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // code completion .........................................................
    
    public static List<CompletionItem> completionItems (Context context) {
        if (context instanceof SyntaxContext) {
            List<CompletionItem> result = new ArrayList<CompletionItem> ();
            return result;
        }
        
        AbstractDocument document = (AbstractDocument) context.getDocument ();
        document.readLock ();
        try {
            TokenSequence tokenSequence = getTokenSequence (context);
            List<CompletionItem> result = new ArrayList<CompletionItem> ();
            Token token = previousToken (tokenSequence);
            String tokenText = token.text ().toString ();
            String libraryContext = null;
            if (tokenText.equals ("new")) {
                result.addAll (getLibrary ().getCompletionItems ("constructor"));
                return result;
            }
            if (tokenText.equals (".")) {
                token = previousToken (tokenSequence);
                if (token.id ().name ().endsWith ("id"))
                    libraryContext = token.text ().toString ();
            } else
            if (token.id ().name ().endsWith ("id") ) {
                token = previousToken (tokenSequence);
                if (token.text ().toString ().equals (".")) {
                    token = previousToken (tokenSequence);
                    if (token.id ().name ().endsWith ("id"))
                        libraryContext = token.text ().toString ();
                } else
                if (token.text ().toString ().equals ("new")) {
                    result.addAll (getLibrary ().getCompletionItems ("constructor"));
                    return result;
                }
            }

            if (libraryContext != null) {
                result.addAll (getLibrary ().getCompletionItems (libraryContext));
                result.addAll (getLibrary ().getCompletionItems ("member"));
            } else
                result.addAll (getLibrary ().getCompletionItems ("root"));
            return result;
        } finally {
            document.readUnlock ();
        }
    }
    
    private static TokenSequence getTokenSequence (Context context) {
        TokenHierarchy tokenHierarchy = TokenHierarchy.get (context.getDocument ());
        TokenSequence ts = tokenHierarchy.tokenSequence ();
        while (true) {
            ts.move (context.getOffset ());
            if (!ts.moveNext ()) return ts;
            TokenSequence ts2 = ts.embedded ();
            if (ts2 == null) return ts;
            ts = ts2;
        }
    }
    
    private static List<CompletionItem> merge (List<CompletionItem> items) {
        Map<String,CompletionItem> map = new HashMap<String,CompletionItem> ();
        Iterator<CompletionItem> it = items.iterator ();
        while (it.hasNext ()) {
            CompletionItem completionItem = it.next ();
            CompletionItem current = map.get (completionItem.getText ());
            if (current != null) {
                String library = current.getLibrary ();
                if (library == null) library = "";
                if (completionItem.getLibrary () != null &&
                    library.indexOf (completionItem.getLibrary ()) < 0
                )
                    library += ',' + completionItem.getLibrary ();
                completionItem = CompletionItem.create (
                    current.getText (),
                    current.getDescription (),
                    library,
                    current.getType (),
                    current.getPriority ()
                );
            }
            map.put (completionItem.getText (), completionItem);
        }
        return new ArrayList<CompletionItem> (map.values ());
    }
    
    private static Token previousToken (TokenSequence ts) {
        do {
            if (!ts.movePrevious ()) return ts.token ();
        } while (
            ts.token ().id ().name ().endsWith ("whitespace") ||
            ts.token ().id ().name ().endsWith ("comment")
        );
        return ts.token ();
    }
    
    // helper methods ..........................................................
    
    
    private static LibrarySupport library;
    
    private static LibrarySupport getLibrary () {
        if (library == null)
            library = LibrarySupport.create (
                Arrays.asList (new String[] {DOC})
            );
        return library;
    }
    
    private static String getParametersAsText (ASTNode params) {
        if (params == null) return "";
        StringBuffer buf = new StringBuffer();
        for (ASTItem item : params.getChildren()) {
            if (item instanceof ASTNode) {
                String nt = ((ASTNode) item).getNT();
                if ("Parameter".equals(nt)) {
                     Iterator<ASTItem> iter = ((ASTNode) item).getChildren().iterator();
                     if (iter.hasNext()) {
                         item = iter.next();
                     }
                }
            }
            if (!(item instanceof ASTToken)) {
                continue;
            }
            ASTToken token = (ASTToken)item;
            String type = token.getTypeName ();
            if ("whitespace".equals(type) || "comment".equals(type)) {
                continue;
            }
            String id = token.getIdentifier();
            buf.append(id);
            if (",".equals(id)) {
                buf.append(' ');
            }
        }
        return buf.toString();
    }    
}
