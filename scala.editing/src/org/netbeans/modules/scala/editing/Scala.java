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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTPath;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.languages.CharInput;
import org.netbeans.api.languages.CompletionItem;
import org.netbeans.api.languages.Context;
import org.netbeans.api.languages.Language;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;
import org.netbeans.api.languages.LanguagesManager;
import org.netbeans.api.languages.LibrarySupport;
import org.netbeans.api.languages.SyntaxContext;
import org.netbeans.api.languages.database.DatabaseDefinition;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.scala.editing.semantic.ContextRoot;
import org.netbeans.modules.scala.editing.semantic.Function;
import org.netbeans.modules.scala.editing.semantic.ErlInclude;
import org.netbeans.modules.scala.editing.semantic.ScalaSemanticAnalyser;
import org.netbeans.modules.scala.editing.semantic.Var;
import org.netbeans.modules.scala.editing.spi.ScalaIndexProvider;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.text.NbDocument;

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
            while (!input.eof() && input.next() != '<') {
                if (input.next() == '\r' ||
                        input.next() == '\n') {
                    input.setIndex(start);
                    return new Object[]{
                        ASTToken.create(language, "js_operator", "", 0, 0, null),
                        null
                    };
                }
                if (input.next() == '\\') {
                    input.read();
                }
                input.read();
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
    public static List<CompletionItem> completionItems(Context context) {
        if (context instanceof SyntaxContext) {
            List<CompletionItem> result = new ArrayList<CompletionItem>();
            return result;
        }

        AbstractDocument doc = (AbstractDocument) context.getDocument();
        doc.readLock();
        try {
            ContextRoot ctxRoot = ScalaSemanticAnalyser.getCurrentCtxRoot(doc);
            TokenSequence tokenSequence = getTokenSequence(context);
            List<CompletionItem> result = new ArrayList<CompletionItem>();
            Token token = previousToken(tokenSequence);
            int tokenOffset = tokenSequence.offset();
            String tokenText = token.text().toString();
            String libraryContext = null;
            
            if (tokenText.equals("new")) {
                result.addAll(getLibrary().getCompletionItems("constructor"));
                return result;
            }
            if (tokenText.equals(":")) {
                result.addAll(getLibrary().getCompletionItems("type"));
                return result;
            }
            
            if (tokenText.equals(".")) {
                token = previousToken(tokenSequence);
                if (token.id().name().endsWith("id")) {
                    libraryContext = token.text().toString();
                }
            } else if (token.id().name().endsWith("id")) {
                token = previousToken(tokenSequence);
                if (token.text().toString().equals(".")) {
                    token = previousToken(tokenSequence);
                    if (token.id().name().endsWith("id")) {
                        libraryContext = token.text().toString();
                    }
                } else if (token.text().toString().equals("new")) {
                    result.addAll(getLibrary().getCompletionItems("constructor"));
                    return result;
                } else if (token.text().toString().equals(":")) {
                    result.addAll(getLibrary().getCompletionItems("type"));
                    return result;
                }
            }

            if (libraryContext != null) {
                result.addAll(getLibrary().getCompletionItems(libraryContext));
                result.addAll(getLibrary().getCompletionItems("member"));
            } else {
                result.addAll(getLibrary().getCompletionItems("root"));
                Collection<CompletionItem> members = getMembers(doc, tokenOffset);
                result.addAll(members);
            }
            return result;
        } finally {
            doc.readUnlock();
        }
    }

    // hyperlink
    public static Runnable hyperlink(Context context) {
        if (!(context instanceof SyntaxContext)) {
            return null;
        }
        NbEditorDocument doc = (NbEditorDocument) context.getDocument();
        SyntaxContext scontext = (SyntaxContext) context;
        ASTPath path = scontext.getASTPath();
        return getGotoDeclarationTask(path, doc);
    }

    public static void performGoToDeclaration(ASTNode node, JTextComponent comp) {
        int position = comp.getCaretPosition();
        ASTPath path = node.findPath(position);
        StyledDocument doc = (StyledDocument) comp.getDocument();
        Runnable task = getGotoDeclarationTask(path, doc);
        if (task != null) {
            task.run();
        }
    }

    public static boolean enabledGoToDeclaration(ASTNode node, JTextComponent comp) {
        NbEditorDocument doc = (NbEditorDocument) comp.getDocument();
        int position = comp.getCaretPosition();
        ASTPath path = node.findPath(position);
        DatabaseDefinition definition = getDefinition(doc, path);
        return definition != null;
    }

    private static DatabaseDefinition getDefinition(Context context) {
        if (!(context instanceof SyntaxContext)) {
            return null;
        }
        SyntaxContext scontext = (SyntaxContext) context;
        ASTPath path = scontext.getASTPath();
        return getDefinition(context.getDocument(), path);
    }

    // helper methods ..........................................................
    public static DatabaseDefinition getDefinition(Document doc, ASTPath path) {
        /**
         * per GLF design, some ASTPath will contain embeded query, for example:
         * a <string> token's query may looks like:
         * "ASTPath S, Form, AttributeDeclaration, <string,'"../include/channel_parser.hrl"'>,
         *          S, <s_string,'"../include/channel_parser.hrl"'>"
         * where, if we simply call query.getLeaf(), will return <s-string> rather than <string>,
         * So, we have to iterate query as following:
         */
        ASTItem leaf = null;
        ListIterator<ASTItem> itr = path.listIterator();
        while (itr.hasNext()) {
            ASTItem item = itr.next();
            if (!itr.hasNext()) {
                leaf = item;
                break;
            } else {
                /** look forward one step */
                ASTItem next = itr.next();
                if (next instanceof ASTNode && ((ASTNode) next).getNT().equals("S")) {
                    leaf = item;
                    break;
                }
                /** rock back */
                itr.previous();
            }
        }
        if (!(leaf instanceof ASTToken)) {
            return null;
        }
        ASTToken token = (ASTToken) leaf;
        ASTNode astRoot = (ASTNode) path.getRoot();
        ContextRoot ctxRoot = ScalaSemanticAnalyser.getCtxRoot(doc, astRoot);
        return ctxRoot.getDefinition(token);
    }

    private static Runnable getGotoDeclarationTask(ASTPath path, StyledDocument doc) {
        DatabaseDefinition definition = getDefinition(doc, path);
        if (definition == null) {
            return null;
        }

        int offset = definition.getOffset();

        DataObject dobj = null;
        StyledDocument docToGo = null;
        if ((definition instanceof Function && ((Function) definition).getFileName() != null) || definition instanceof ErlInclude) {
            File file = null;
            if (definition instanceof Function && ((Function) definition).getFileName() != null) {
                String module = ((Function) definition).getFileName();
                URL url = ScalaIndexProvider.getDefault().getModuleFileUrl(ScalaIndexProvider.Type.Module, module);
                if (url == null) {
                    return null;
                }

                try {
                    file = new File(url.toURI());
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            } else if (definition instanceof ErlInclude && ((ErlInclude) definition).getPath() != null) {
                String filePath = ((ErlInclude) definition).getPath();
                if (((ErlInclude) definition).isLib()) {
                    URL url = ScalaIndexProvider.getDefault().getModuleFileUrl(ScalaIndexProvider.Type.Header, filePath);
                    if (url == null) {
                        return null;
                    }

                    try {
                        file = new File(url.toURI());
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
                /** if include_lib can't find in libs, will also try regular search */
                if (file == null) {
                    DataObject dobjInEditing = NbEditorUtilities.getDataObject(doc);
                    Project project = FileOwnerQuery.getOwner(dobjInEditing.getPrimaryFile());
                    File projectRoot = FileUtil.toFile(project.getProjectDirectory());

                    File includeRoot = new File(projectRoot, "include");
                    if (includeRoot != null && includeRoot.exists()) {
                        file = new File(includeRoot, filePath);
                    }
                }
                /** As we just open a included head file, so set the offset to the begin of it. */
                offset = 0;
            }


            if (file != null && file.exists()) {
                /** convert file to an uni absulte pathed file (../ etc will be coverted) */
                file = FileUtil.normalizeFile(file);
                FileObject fobj = FileUtil.toFileObject(file);
                try {
                    dobj = DataObject.find(fobj);
                } catch (DataObjectNotFoundException ex) {
                    ex.printStackTrace();
                }
                if (dobj != null) {
                    Node nodeOfDobj = dobj.getNodeDelegate();
                    EditorCookie ec = nodeOfDobj.getCookie(EditorCookie.class);
                    try {
                        docToGo = ec.openDocument();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            docToGo = doc;
            dobj = NbEditorUtilities.getDataObject(docToGo);
        }
        if (dobj == null || docToGo == null) {
            return null;
        }


        LineCookie lc = dobj.getCookie(LineCookie.class);
        Line.Set lineSet = lc.getLineSet();
        final Line line = lineSet.getCurrent(NbDocument.findLineNumber(docToGo, offset));
        final int column = NbDocument.findLineColumn(docToGo, offset);
        return new Runnable() {

            public void run() {
                line.show(Line.SHOW_GOTO, column);
            }
        };
    }

    private static TokenSequence getTokenSequence(Context context) {
        TokenHierarchy tokenHierarchy = TokenHierarchy.get(context.getDocument());
        TokenSequence ts = tokenHierarchy.tokenSequence();
        while (true) {
            ts.move(context.getOffset());
            if (!ts.moveNext()) {
                return ts;
            }
            TokenSequence ts2 = ts.embedded();
            if (ts2 == null) {
                return ts;
            }
            ts = ts2;
        }
    }

    private static List<CompletionItem> merge(List<CompletionItem> items) {
        Map<String, CompletionItem> map = new HashMap<String, CompletionItem>();
        Iterator<CompletionItem> it = items.iterator();
        while (it.hasNext()) {
            CompletionItem completionItem = it.next();
            CompletionItem current = map.get(completionItem.getText());
            if (current != null) {
                String library = current.getLibrary();
                if (library == null) {
                    library = "";
                }
                if (completionItem.getLibrary() != null &&
                        library.indexOf(completionItem.getLibrary()) < 0) {
                    library += ',' + completionItem.getLibrary();
                }
                completionItem = CompletionItem.create(
                        current.getText(),
                        current.getDescription(),
                        library,
                        current.getType(),
                        current.getPriority());
            }
            map.put(completionItem.getText(), completionItem);
        }
        return new ArrayList<CompletionItem>(map.values());
    }

    private static Token previousToken(TokenSequence ts) {
        do {
            if (!ts.movePrevious()) {
                return ts.token();
            }
        } while (ts.token().id().name().endsWith("whitespace") ||
                ts.token().id().name().endsWith("comment"));
        return ts.token();
    }

    // helper methods ..........................................................
    private static LibrarySupport library;

    private static LibrarySupport getLibrary() {
        if (library == null) {
            library = LibrarySupport.create(
                    Arrays.asList(new String[]{DOC}));
        }
        return library;
    }

    private static Collection<CompletionItem> membersBuf = new ArrayList<CompletionItem>();
    private static Collection<CompletionItem> getMembers(Document doc, int offset) {
        membersBuf.clear();
        String title = getDocumentName(doc);
        ContextRoot ctxRoot = ScalaSemanticAnalyser.getCurrentCtxRoot(doc);
        if (ctxRoot == null) {
            return membersBuf;
        }

        Collection<DatabaseDefinition> definitions = ctxRoot.getDefinitionsInScope(offset);
        for (DatabaseDefinition definition : definitions) {
            CompletionItem item = toCompletionItem(definition, title);
            if (item != null) {
                membersBuf.add(item);
            }
        }
        return membersBuf;
    }
    
    static CompletionItem toCompletionItem(DatabaseDefinition definition, String title) {
        if (definition instanceof Var) {
            Var v = (Var) definition;
            CompletionItem.Type type = null;
            switch (v.getContextType()) {
                case LOCAL:
                    type = CompletionItem.Type.LOCAL;
                    break;
                case PARAMETER:
                    type = CompletionItem.Type.PARAMETER;
                    break;
                case GLOBAL:
                    type = CompletionItem.Type.FIELD;
                    break;
            }
            return CompletionItem.create(v.getName(), null, title, type, 1);
        } else if (definition instanceof Function) {
            Function f = (Function) definition;
            return CompletionItem.create(f.getName(), null, title, CompletionItem.Type.METHOD, 1);
        }
        return null;
    }

    private static String getDocumentName(Document doc) {
        String name = (String) doc.getProperty("title");
        if (name == null) {
            return null;
        }
        int i = name.lastIndexOf(File.separatorChar);
        if (i > 0) {
            return name.substring(i + 1);
        }
        return name;
    }
    
    private static String getParametersAsText(ASTNode params) {
        if (params == null) {
            return "";
        }
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
            ASTToken token = (ASTToken) item;
            String type = token.getTypeName();
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
