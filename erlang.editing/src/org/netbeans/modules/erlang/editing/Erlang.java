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

import java.io.File;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.languages.CompletionItem;
import org.netbeans.api.languages.Language;
import org.netbeans.api.languages.LanguageDefinitionNotFoundException;
import org.netbeans.api.languages.LanguagesManager;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.CharInput;
import org.netbeans.api.languages.ASTPath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.languages.Context;
import org.netbeans.api.languages.LibrarySupport;
import org.netbeans.api.languages.SyntaxContext;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.languages.database.DatabaseContext;
import org.netbeans.api.languages.database.DatabaseDefinition;
import org.netbeans.api.languages.database.DatabaseItem;
import org.netbeans.api.languages.database.DatabaseUsage;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.erlang.editing.semantic.ErlContext;
import org.netbeans.modules.erlang.editing.semantic.ErlFunction;
import org.netbeans.modules.erlang.editing.semantic.ErlInclude;
import org.netbeans.modules.erlang.editing.semantic.ErlMacro;
import org.netbeans.modules.erlang.editing.semantic.ErlModule;
import org.netbeans.modules.erlang.editing.semantic.ErlRecord;
import org.netbeans.modules.erlang.editing.semantic.ErlangSemanticAnalyser;
import org.netbeans.modules.erlang.editing.semantic.ErlVariable;
import org.netbeans.modules.erlang.editing.spi.ErlangIndexProvider;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;


/**
 *
 * @author Jan Jancura, Dan Prusa
 * @author Caoyuan Deng
 */
public class Erlang {

    private static final String DOC = "org/netbeans/modules/erlang/resources/Documentation.xml";
    private static final String MIME_TYPE = "text/x-erlang";

    private static Set<Integer> regExp = new HashSet<Integer> ();
    static {
        regExp.add (new Integer (','));
        regExp.add (new Integer (')'));
        regExp.add (new Integer (';'));
        regExp.add (new Integer (']'));
    }
    
    public static Object[] parseRegularExpression (CharInput input) {
        if (input.read () != '/')
            throw new InternalError ();
        int start = input.getIndex ();
        try {
            Language language = LanguagesManager.get ().getLanguage (MIME_TYPE);
            while (!input.eof () &&
                    input.next () != '/'
            ) {
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
            while (input.next () == '/') input.read ();
            while (!input.eof ()) {
                int ch = input.next ();
                if (ch != 'g' && ch != 'i' && ch != 'm')
                    break;
                input.read ();
            }
            int end = input.getIndex ();
            char car = input.eof() ? 0 : input.next();
            boolean newLineDetected = false;
            while (
                !input.eof () && (
                    car == ' ' ||
                    car == '\t' ||
                    car == '\n' ||
                    car == '\r'
                )
            ) {
                newLineDetected = newLineDetected || car == '\n';
                input.read ();
                if (!input.eof()) {
                    car = input.next();
                }
            }
            if (
                !input.eof () && 
                input.next () == '.'
            ) {
                input.read ();
                if (input.next () >= '0' &&
                    input.next () <= '9'
                ) {
                    input.setIndex (start);
                    return new Object[] {
                        ASTToken.create (language, "js_operator", "", 0, 0, null),
                        null
                    };
                } else {
                    input.setIndex (end);
                    return new Object[] {
                        ASTToken.create (language, "js_regularExpression", "", 0, 0, null),
                        null
                    };
                }
            }
            if (
                newLineDetected || input.eof () || regExp.contains (new Integer (input.next ()))
            ) {
                input.setIndex (end);
                return new Object[] {
                    ASTToken.create (language, "js_regularExpression", "", 0, 0, null),
                    null
                };
            }
            input.setIndex (start);
            return new Object[] {
                ASTToken.create (language, "js_operator", "", 0, 0, null),
                null
            };
        } catch (LanguageDefinitionNotFoundException ex) {
            ex.printStackTrace ();
            return null;
        }
    }

    public static Object[] parsePeriod(CharInput input) {
        if (input.read() != '.') {
            throw new InternalError();
        }

        int start = input.getIndex();
        try {
            Language language = LanguagesManager.get().getLanguage(MIME_TYPE);
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
                return new Object[] {
                    ASTToken.create(language, "stop", ".", 0, 0, null), 
                    null
                };
            } else {
                input.setIndex(start);
                return new Object[] {
                    ASTToken.create(language, "dot", ".", 0, 0, null), 
                    null
                };
            }
        } catch (LanguageDefinitionNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** @Todo: the charInput is the pattern part only, not all chars of the file */
    @Deprecated
    public static Object[] parsePound(CharInput input) {
        if (input.read() != '#') {
            throw new InternalError();
        }
        int start = input.getIndex();
        try {
            Language language = LanguagesManager.get().getLanguage(MIME_TYPE);
            if (!input.eof() && input.next() != '!') {
                input.setIndex(start);
                return new Object[] {
                    ASTToken.create(language, "separator", "#", 0, 0, null), 
                    null
                };
            }
            int end = start;
            while (!input.eof() && !(input.next() == '\n' || input.next() == '\r')) {
                input.read();
                end++;
            }
            input.setIndex(end + 1);
            return new Object[] {
                ASTToken.create(language, "escript", "#!...", 0, 0, null), 
                null
            };
        } catch (LanguageDefinitionNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Runnable hyperlink(Context context) {
        if (!(context instanceof SyntaxContext)) {
            return null;
        }
        NbEditorDocument doc = (NbEditorDocument) context.getDocument();
        SyntaxContext scontext = (SyntaxContext) context;
        ASTPath path = scontext.getASTPath();
        return getGotoDeclarationTask(path, doc);
    }

    public static String functionName(SyntaxContext context) {
        ASTPath path = context.getASTPath();
        ASTItem functionClauses = (ASTNode) path.getLeaf();
        ASTItem name = null;
        ASTItem arguments = null;
        for (ASTItem item : functionClauses.getChildren()) {
            if (ErlangSemanticAnalyser.isNode(item, "FunctionClause")) {
                for (ASTItem item1 : item.getChildren()) {
                    if (ErlangSemanticAnalyser.isTokenTypeName(item1, "atom")) {
                        name = item1;
                    } else if (ErlangSemanticAnalyser.isNode(item1, "Exprs")) {
                        arguments = (ASTNode) item1;
                    }
                }
                break;
            }
        }

        if (name != null) {
            String nameStr = ((ASTToken) name).getIdentifier();
            int arityInt = 0;
            if (arguments != null) {
                for (ASTItem item : arguments.getChildren()) {
                    if (ErlangSemanticAnalyser.isNode(item, "Expr")) {
                        arityInt++;
                    }
                }
            }
            return nameStr + "/" + arityInt;
        }
        return "?";
    }

    public static String functionArguments(SyntaxContext context) {
        ASTPath path = context.getASTPath();
        ASTItem functionClause = (ASTNode) path.getLeaf();
        ASTItem name = null;
        ASTItem arguments = null;
        for (ASTItem item : functionClause.getChildren()) {
            if (ErlangSemanticAnalyser.isTokenTypeName(item, "atom")) {
                name = item;
            } else if (ErlangSemanticAnalyser.isNode(item, "Exprs")) {
                arguments = (ASTNode) item;
            }
        }

        if (name != null) {
            String argumentsStr = "";
            int arityInt = 0;
            if (arguments != null) {
                argumentsStr = ((ASTNode) arguments).getAsText();
                for (ASTItem item : arguments.getChildren()) {
                    if (ErlangSemanticAnalyser.isNode(item, "Expr")) {
                        arityInt++;
                    }
                }
            }

            return "(" + argumentsStr + ")";
        }
        return "?";
    }

    // code completion .........................................................
    public static List<CompletionItem> completionItems(Context context) {
        if (context instanceof SyntaxContext) {
            return Collections.<CompletionItem>emptyList();
        }

        AbstractDocument document = (AbstractDocument) context.getDocument();
        document.readLock();
        try {
            Document doc = context.getDocument();
            ErlContext rootCtx = ErlangSemanticAnalyser.getCurrentRootCtx(doc);
            if (rootCtx == null) {
                return Collections.<CompletionItem>emptyList();
            }
            
            String module = rootCtx.getFirstDefinition(ErlModule.class).getName();

            List<CompletionItem> result = new ArrayList<CompletionItem>();
            TokenSequence tokenSequence = getTokenSequence(context);
            Token token = previousToken (tokenSequence);
            int tokenOffset = tokenSequence.offset();
            String tokenText = token.text().toString();
            String libraryContext = null;
            if (tokenText.equals("-")) {
                libraryContext = "attribute";
            } else if (tokenText.equals(":")) {
                Token prevToken = previousToken(tokenSequence);
                if (prevToken.id().name().equals("atom")) {
                    String remoteModule = prevToken.text().toString().trim();
                    result.addAll(ErlangIndexProvider.getDefault().getFunctionCompletionItems(remoteModule));
                    libraryContext = "remote";
                }
            } else if (tokenText.equals(".")) {
                Token prevToken = previousToken(tokenSequence);
                if (prevToken.id().name().equals("atom")) {
                    String recordName = prevToken.text().toString().trim();
                    result.addAll(ErlangIndexProvider.getDefault().getRecordFieldsCompletionItems(module, recordName));
                    libraryContext = "record_field";
                }
            } else if (tokenText.equals("?")) {
                result.addAll(ErlangIndexProvider.getDefault().getMacroCompletionItems(module));
                libraryContext = "macro";
            } else if (tokenText.equals("#")) {
                result.addAll(ErlangIndexProvider.getDefault().getRecordCompletionItems(module));
                libraryContext = "record";
            } else if (token.id().name().equals("atom")) {
                Token prevToken = previousToken(tokenSequence);
                String prevTokenText = prevToken.text().toString();
                if (prevTokenText.equals("-")) {
                    libraryContext = "attribute";
                } else if (prevTokenText.equals(":")) {
                    prevToken = previousToken(tokenSequence);
                    if (prevToken.id().name().equals("atom")) {
                        String remoteModule = prevToken.text().toString().trim();
                        if (remoteModule.equals("erlang")) {
                            libraryContext = "erlang";
                        } else {
                            result.addAll(ErlangIndexProvider.getDefault().getFunctionCompletionItems(remoteModule));
                            libraryContext = "remote";
                        }
                    }
                } else if (prevTokenText.equals("?")) {
                    result.addAll(ErlangIndexProvider.getDefault().getMacroCompletionItems(module));
                    libraryContext = "macro";
                } else if (prevTokenText.equals("#")) {
                    result.addAll(ErlangIndexProvider.getDefault().getRecordCompletionItems(module));
                    libraryContext = "record";
                } else if (prevTokenText.equals(".")) {
                    prevToken = previousToken(tokenSequence);
                    if (prevToken.id().name().equals("atom")) {
                        String recordName = prevToken.text().toString().trim();
                        result.addAll(ErlangIndexProvider.getDefault().getRecordFieldsCompletionItems(module, recordName));
                        libraryContext = "record_field";
                    }
                } else {
                    result.addAll(ErlangIndexProvider.getDefault().getModuleCompletionItems(tokenText));
                    libraryContext = "member";
                }
            } else if (token.id().name().equals("var")) {
                Token prevToken = previousToken(tokenSequence);
                String prevTokenText = prevToken.text().toString();
                if (prevTokenText.equals("?")) {
                    result.addAll(ErlangIndexProvider.getDefault().getMacroCompletionItems(module));
                    libraryContext = "macro";
                } else if (prevTokenText.equals("#")) {
                    result.addAll(ErlangIndexProvider.getDefault().getRecordCompletionItems(module));
                    libraryContext = "record";
                }
            }

            if (libraryContext == null || libraryContext.equals("member")) {
                result.addAll(getLibrary().getCompletionItems("keyword"));
                result.addAll(getLibrary().getCompletionItems("builtin"));
                Collection<CompletionItem> members = getMembers(doc, tokenOffset);
                result.addAll(members);
            } else {
                if (libraryContext.equals("remote") || libraryContext.equals("macro") || libraryContext.equals("record")) {
                    return result;
                } else {
                    result.addAll(getLibrary().getCompletionItems(libraryContext));
                }
            }
            return result;
        } finally {
            document.readUnlock();
        }
        /** @Notice:
         * When under completion for module functions, avoid to add these
         * SyntaxContext related members to completion items.
         */
        //            ASTItem leaf = path.getLeaf();
        //            path.listIterator(path.size() - 2);
        //            if (leaf instanceof ASTToken) {
        //                ASTToken token = (ASTToken) leaf;
        //                if (token.getType().equals("atom") || token.getIdentifier().equals(":")) {
        //                    return Collections.EMPTY_LIST;
        //                } else if (token.getIdentifier().equals("?")) {
        //                    result.addAll(ErlangIndexProvider.getDefault().getModuleDefineCompletionItems(module));
        //                    return result;
        //                }
        //            }
        //
        //            Map<String, CompletionItem> members = getMembers(doc, path, getDocumentName(doc));
        //            result.addAll(members.values());
        //            return result;
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
                if (completionItem.getLibrary() != null && library.indexOf(completionItem.getLibrary()) < 0) {
                    library += ',' + completionItem.getLibrary();
                }
                completionItem = CompletionItem.create(current.getText(), current.getDescription(), library, current.getType(), current.getPriority());
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
        } while (ts.token().id().name().endsWith("whitespace") || ts.token().id().name().endsWith("comment"));
        return ts.token();
    }


    // actions .................................................................
    public static void performDeleteCurrentMethod(ASTNode node, JTextComponent comp) {
        NbEditorDocument doc = (NbEditorDocument) comp.getDocument();
        int position = comp.getCaretPosition();
        ASTPath path = node.findPath(position);
        ASTNode methodNode = null;
        for (Iterator iter = path.listIterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (!(obj instanceof ASTNode)) {
                break;
            }
            ASTNode n = (ASTNode) obj;
            if ("FunctionDeclaration".equals(n.getNT())) {
                // NOI18N
                methodNode = n;
            } // if
        } // for
        if (methodNode != null) {
            try {
                doc.remove(methodNode.getOffset(), methodNode.getLength());
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
    }

    public static boolean enabledDeleteCurrentMethod(ASTNode node, JTextComponent comp) {
        NbEditorDocument doc = (NbEditorDocument) comp.getDocument();
        int position = comp.getCaretPosition();
        ASTPath path = node.findPath(position);
        if (path == null) {
            return false;
        }
        for (Iterator iter = path.listIterator(); iter.hasNext();) {
            Object obj = iter.next();
            if (!(obj instanceof ASTNode)) {
                return false;
            }
            ASTNode n = (ASTNode) obj;
            if ("FunctionDeclaration".equals(n.getNT())) {
                // NOI18N
                return true;
            } // if
        } // for
        return false;
    }

    public static void performRun(ASTNode node, JTextComponent comp) {
        ClassLoader cl = Erlang.class.getClassLoader();
        try {
            //        ScriptEngineManager manager = new ScriptEngineManager ();
            //        ScriptEngine engine = manager.getEngineByMimeType ("text/javascript");
            Class managerClass = cl.loadClass("javax.script.ScriptEngineManager");
            Object manager = managerClass.newInstance();
            Method getEngineByMimeType = managerClass.getMethod("getEngineByMimeType", new Class[]{String.class});
            Object engine = getEngineByMimeType.invoke(manager, new Object[]{"text/x-erlang"});

            Document doc = comp.getDocument();
            DataObject dob = NbEditorUtilities.getDataObject(doc);
            String name = dob.getPrimaryFile().getNameExt();
            SaveCookie saveCookie = dob.getLookup().lookup(SaveCookie.class);
            if (saveCookie != null) {
                try {
                    saveCookie.save();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
            //            ScriptContext context = engine.getContext ();
            Class engineClass = cl.loadClass("javax.script.ScriptEngine");
            Method getContext = engineClass.getMethod("getContext", new Class[]{});
            Object context = getContext.invoke(engine, new Object[]{});

            InputOutput io = IOProvider.getDefault().getIO("Run " + name, false);

            //            context.setWriter (io.getOut ());
            //            context.setErrorWriter (io.getErr ());
            //            context.setReader (io.getIn ());
            Class contextClass = cl.loadClass("javax.script.ScriptContext");
            Method setWriter = contextClass.getMethod("setWriter", new Class[]{Writer.class});
            Method setErrorWriter = contextClass.getMethod("setErrorWriter", new Class[]{Writer.class});
            Method setReader = contextClass.getMethod("setReader", new Class[]{Reader.class});
            setWriter.invoke(context, new Object[]{io.getOut()});
            setErrorWriter.invoke(context, new Object[]{io.getErr()});
            setReader.invoke(context, new Object[]{io.getIn()});

            io.getOut().reset();
            io.getErr().reset();
            io.select();

            //            Object o = engine.eval (doc.getText (0, doc.getLength ()));
            Method eval = engineClass.getMethod("eval", new Class[]{String.class});
            Object o = eval.invoke(engine, new Object[]{doc.getText(0, doc.getLength())});

            if (o != null) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Result: " + o));
            }
        } catch (InvocationTargetException ex) {
            try {
                Class scriptExceptionClass = cl.loadClass("javax.script.ScriptException");
                if (ex.getCause() != null && scriptExceptionClass.isAssignableFrom(ex.getCause().getClass())) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getCause().getMessage()));
                } else {
                    ErrorManager.getDefault().notify(ex);
                }
            } catch (Exception ex2) {
                ErrorManager.getDefault().notify(ex2);
            }
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        //        ScriptEngineManager manager = new ScriptEngineManager ();
        //        ScriptEngine engine = manager.getEngineByMimeType ("text/javascript");
        //        Document doc = comp.getDocument ();
        //        DataObject dob = NbEditorUtilities.getDataObject (doc);
        //        String name = dob.getPrimaryFile ().getNameExt ();
        //        SaveCookie saveCookie = (SaveCookie) dob.getLookup ().lookup (SaveCookie.class);
        //        if (saveCookie != null)
        //            try {
        //                saveCookie.save ();
        //            } catch (IOException ex) {
        //                ErrorManager.getDefault ().notify (ex);
        //            }
        //        try {
        //            ScriptContext context = engine.getContext ();
        //            InputOutput io = IOProvider.getDefault ().getIO ("Run " + name, false);
        //            context.setWriter (io.getOut ());
        //            context.setErrorWriter (io.getErr ());
        //            context.setReader (io.getIn ());
        //            io.select ();
        //            Object o = engine.eval (doc.getText (0, doc.getLength ()));
        //            if (o != null)
        //                DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message ("Result: " + o));
        //        } catch (BadLocationException ex) {
        //            ErrorManager.getDefault ().notify (ex);
        //        } catch (ScriptException ex) {
        //            DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Message (ex.getMessage ()));
        //        }
    }

    public static boolean enabledRun(ASTNode node, JTextComponent comp) {
        try {
            ClassLoader cl = Erlang.class.getClassLoader();
            Class managerClass = cl.loadClass("javax.script.ScriptEngineManager");

            return managerClass != null;
        } catch (ClassNotFoundException ex) {
            return false;
        }
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

    public static boolean isUnusedVariable(Context context) {
        DatabaseDefinition definition = getDefinition(context);
        if (definition == null) {
            return true;
        }
        // each Variable at least is used by itself once.
        return definition instanceof ErlVariable && definition.getUsages().size() <= 1 && !definition.getName().startsWith("_");
    }

    public static boolean isFunctionParameter(Context context) {
        DatabaseDefinition definition = getDefinition(context);
        if (definition == null || !(definition instanceof ErlVariable)) {
            return false;
        }
        ErlVariable variable = (ErlVariable) definition;
        return variable.getContextType() == ErlVariable.Scope.PARAMETER && variable.getUsages().size() > 1;
    }

    public static boolean isLocalVariable(Context context) {
        DatabaseDefinition definition = getDefinition(context);
        if (definition == null || !(definition instanceof ErlVariable)) {
            return false;
        }
        ErlVariable variable = (ErlVariable) definition;
        return variable.getContextType() == ErlVariable.Scope.LOCAL && variable.getUsages().size() > 1;
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
         * per GLF design, some ASTPath will contain embeded path, for example:
         * a <string> token's path may looks like:
         * "ASTPath S, Form, AttributeDeclaration, <string,'"../include/channel_parser.hrl"'>,
         *          S, <s_string,'"../include/channel_parser.hrl"'>"
         * where, if we simply call path.getLeaf(), will return <s-string> rather than <string>,
         * So, we have to iterate path as following:
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
        ErlContext rootCtx = ErlangSemanticAnalyser.getRootContext(doc, astRoot);        
        DatabaseItem dbItem = rootCtx.getDatabaseItem(token.getOffset());
        if (dbItem != null) {
            if (dbItem instanceof DatabaseDefinition) {
                return (DatabaseDefinition) dbItem;
            } else if (dbItem instanceof DatabaseUsage) {
                return ((DatabaseUsage) dbItem).getDefinition();
            }
        }
        return null;
    }

    private static Collection<CompletionItem> membersBuf = new ArrayList<CompletionItem>();

    private static Collection<CompletionItem> getMembers(Document doc, int offset) {
        membersBuf.clear();
        String title = getDocumentName(doc);
        ErlContext rootCtx = ErlangSemanticAnalyser.getCurrentRootCtx(doc);
        if (rootCtx == null) {
            return membersBuf;
        }

        Collection<DatabaseDefinition> definitions = getDefinitionsInScope(rootCtx, offset);
        for (DatabaseDefinition definition : definitions) {
            CompletionItem item = toCompletionItem(definition, title);
            if (item != null) {
                membersBuf.add(item);
            }
        }
        return membersBuf;
    }

    public static Collection<DatabaseDefinition> getDefinitionsInScope(ErlContext rootCtx, int offset) {
        DatabaseContext closestCtx = rootCtx.getClosestContext(offset);
        Collection<DatabaseDefinition> scopeDefinitions = new ArrayList<DatabaseDefinition>();
        closestCtx.collectDefinitionsInScope(scopeDefinitions);
        return scopeDefinitions;
    }
    
    static CompletionItem toCompletionItem(DatabaseDefinition definition, String title) {
        if (definition instanceof ErlVariable) {
            ErlVariable v = (ErlVariable) definition;
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
        } else if (definition instanceof ErlFunction) {
            ErlFunction f = (ErlFunction) definition;
            return CompletionItem.create(f.getName(), null, title, CompletionItem.Type.METHOD, 1);
        } else if (definition instanceof ErlRecord) {
            ErlRecord r = (ErlRecord) definition;
            return CompletionItem.create("#" + r.getName(), null, title, CompletionItem.Type.FIELD, 1);
        } else if (definition instanceof ErlMacro) {
            ErlMacro d = (ErlMacro) definition;
            return CompletionItem.create("?" + d.getName(), null, title, CompletionItem.Type.CONSTANT, 1);
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


    private static LibrarySupport library;

    private static LibrarySupport getLibrary() {
        if (library == null) {
            library = LibrarySupport.create(DOC);
        }
        return library;
    }

    /** @Caoyuan added */
    public static boolean isFunctionCall(Context context) {
        if (!(context instanceof SyntaxContext)) {
            return false;
        }
        SyntaxContext scontext = (SyntaxContext) context;
        ASTPath path = scontext.getASTPath();
        DatabaseDefinition definition = getDefinition(context.getDocument(), path);
        return definition != null && definition instanceof ErlFunction;
    }

    private static Runnable getGotoDeclarationTask(ASTPath path, StyledDocument doc) {
        DatabaseDefinition definition = getDefinition(doc, path);
        if (definition == null) {
            return null;
        }

        int offset = definition.getOffset();

        DataObject dobj = null;
        StyledDocument docToGo = null;
        if ((definition instanceof ErlFunction && ((ErlFunction) definition).getUrl() != null) || definition instanceof ErlInclude) {
            File file = null;
            if (definition instanceof ErlFunction && ((ErlFunction) definition).getUrl() != null) {
                String module = ((ErlFunction) definition).getUrl();
                URL url = ErlangIndexProvider.getDefault().getModuleFileUrl(ErlangIndexProvider.Type.Module, module);
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
                    URL url = ErlangIndexProvider.getDefault().getModuleFileUrl(ErlangIndexProvider.Type.Header, filePath);
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
                    File folder = FileUtil.toFile(dobjInEditing.getFolder().getPrimaryFile());
                    file = new File(folder, filePath);
                    if (file == null || !file.exists()) {
                        File projectRoot = getMainProjectWorkPath();
                        File includeRoot = new File(projectRoot, "include");
                        if (includeRoot != null && includeRoot.exists()) {
                            file = new File(includeRoot, filePath);
                        }
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

    private static File getMainProjectWorkPath() {
        File pwd = null;
        Project mainProject = OpenProjects.getDefault().getMainProject();
        if (mainProject != null) {
            FileObject fo = mainProject.getProjectDirectory();
            if (!fo.isFolder()) {
                fo = fo.getParent();
            }
            pwd = FileUtil.toFile(fo);
        }
        if (pwd == null) {
            String userHome = System.getProperty("user.home");
            pwd = new File(userHome);
        }
        return pwd;
    }
}
