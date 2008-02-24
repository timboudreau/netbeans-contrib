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
package org.netbeans.modules.erlang.editing.semantic;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.text.Document;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.api.languages.ParserManager;
import org.netbeans.api.languages.ParserManager.State;
import org.netbeans.api.languages.ParserManagerListener;
import org.netbeans.api.languages.SyntaxContext;
import org.netbeans.api.languages.database.DatabaseManager;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.erlang.editing.spi.ErlangIndexProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlangSemanticAnalyser {

    /**
     * JavaScript module use doc as the key, so, if the doc is modified, it do not
     * process semantic parsing any more, it just iterate the old JSRoot context to
     * get usages by comparing token name only.
     * But this is not suitable for Erlang, in Erlang, the usage is difficult to
     * identified by names, it's always better to process usages during semantic
     * parsing. So, we always check if root ASTNode has been changed (re-parsed).
     *
     * And, it seems that although docToErlRoot is WeakHashMap, but it seems the
     * old root ASTNode won't be released by language engine even a new root ASTNode
     * has been created. memory leak?
     * Whatever, we will delete old root ASTNode manually here to force only one
     * astRoot of each doc exists in Erlang.java's context.
     */

    private static Map<Document, ErlangSemanticAnalyser> docToAnalyser = new WeakHashMap<Document, ErlangSemanticAnalyser>();

    private static ErlangSemanticAnalyser ANALYSER_FOR_INDEXING = new ErlangSemanticAnalyser(true);

    private Document doc;
    private FileObject fo;
    private ASTNode astRoot;
    private ErlContext rootCtx;    
    /**
     * @NOTICE we should avoid re-entrant of parse. There is a case may cause that:
     * When document is modified, it will be setDirty in PersistentClassIndex, and request
     * a parseFiles task for updateIndex, thus, a bad cycle calling:
     * parseFiles -> semanticParser.parse -> ErlangIndexProvider.getDefault().getFunction ->
     * index.gsfSearch -> index.updateDirty -> parseFiles -> semanticParser.parse.
     *
     * Here's a ugly hacking, we just setForIndexing to avoid this re-entrant
     */
    private boolean forIndexing;

    private ParserManager parserManager;
    private ParserManagerListener parserManagerListener;
    
    private static ErlVariable WILD_VAR = new ErlVariable("_", 0, 0, ErlVariable.Scope.LOCAL);
    
    private ErlangSemanticAnalyser(Document doc) {
        this.doc = doc;
        this.fo = NbEditorUtilities.getFileObject(doc);
        initParserManagerListener();
    }

    private ErlangSemanticAnalyser(boolean forIndexing) {
        this.doc = null;
        this.forIndexing = forIndexing;
    }

    private void initParserManagerListener() {
        parserManager = ParserManager.get(doc);
        parserManagerListener = new ParserManagerListener() {

            public void parsed(State state, ASTNode root) {
                if (state == State.PARSING) {
                    return;
                } else {
                    analyse(root);
                }
            }
        };
        parserManager.addListener(parserManagerListener);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (parserManager != null && parserManagerListener != null) {
            parserManager.removeListener(parserManagerListener);
        }
    }    
    
    public ASTNode getAstRoot() {
        return astRoot;
    }

    public ErlContext getRootContext() {
        return rootCtx;
    }
    
    public static ErlangSemanticAnalyser getAnalyserForIndexing() {
        return ANALYSER_FOR_INDEXING;
    }

    public static ErlangSemanticAnalyser getAnalyser(FileObject fo) {
        for (Document doc : docToAnalyser.keySet()) {
            DataObject dobj = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
            if (dobj != null && dobj.getPrimaryFile() == fo) {
                return docToAnalyser.get(doc);
            }
        }

        return null;
    }

    public static ErlangSemanticAnalyser getAnalyser(final Document doc) {
        if (doc == null) {
            return ANALYSER_FOR_INDEXING;
        }

        ErlangSemanticAnalyser analyser = docToAnalyser.get(doc);
        if (analyser == null) {
            analyser = new ErlangSemanticAnalyser(doc);
            docToAnalyser.put(doc, analyser);
        }

        return analyser;
    }


    /**
     * @Deprecated 
     * Don't use AST feature to process semantic analysis, since AST feature will cause ContextASTEvalutor to set another 
     * rootContext, @see org.netbeans.modules.languages.features.ContextASTEvalutor#afterEvaluation
     *
     * This is the method will be called by GLF feature as declared in Erlang.nbs:
     * AST {
     *   process:org.netbeans.modules.erlang.semantic.ErlangSemanticParser.process
     * }
     *
     * @Notice astRoot may be changed after this feature calling? if so, the doc <--> astRoot is useless to prevent redudant parsing.
     */
    @Deprecated
    public static void process(SyntaxContext syntaxContext) {
        Document doc = syntaxContext.getDocument();
        ASTNode astRoot = (ASTNode) syntaxContext.getASTPath().getRoot();
        /**
         * if our process also changed AST, then we should return it,
         * and, we should process semantic according to the changed astRoot.
         */
        ErlangSemanticAnalyser analyser = getAnalyser(doc);
        analyser.analyse(astRoot);

        /** this feature call may be void return or ASTNode return, if later,
         * Language engine will accept the ASTNode, otherwise, should keep the
         * original astRoot that was passed in via syntaxtContext
         */
    }


    public static ErlContext getRootContext(Document doc, ASTNode astRoot) {
        ErlangSemanticAnalyser analyser = getAnalyser(doc);
        ErlContext rootCtx = analyser.getRootContext();
        if (rootCtx != null) {
            /**
             * although we have a syntax parser listener which will redo semantic
             * parser when new syntax happened, but SemanticHilightingsLayer may
             * act before our listener, so, just check if we need do here:
             *
             * check if the AstRoot has been re-parsed, if so, we should
             * remove oldAstRoot and redo semantic parsing
             */
            if (analyser.getAstRoot() != astRoot) {
                rootCtx = analyser.analyse(astRoot);
            }
        } else {
            rootCtx = analyser.analyse(astRoot);
        }

        return rootCtx;
    }
    
    
    public static ErlContext getCurrentRootCtx(Document doc) {
        ErlangSemanticAnalyser analyser = getAnalyser(doc);
        if (analyser.rootCtx == null) {
             ParserManager parserManager = ParserManager.get(doc);
             waitingForParsingFinished(parserManager);
        }
        return analyser.rootCtx;
    }

    /**
     * @NOTICE we should avoid re-entrant of parse. There is a case may cause that:
     * When document is modified, it will be setDirty in PersistentClassIndex, and request
     * a parseFiles task for updateIndex, thus, a bad cycle calling:
     * parseFiles -> semanticParser.parse -> getDeclaration ->
     * index.gsfSearch -> index.updateDirty -> parseFiles -> semanticParser.parse.
     */
    public ErlContext analyse(ASTNode astRoot) {
        if (this.astRoot != astRoot) {
            this.astRoot = astRoot;
            ErlContext rootCtxInParsing = new ErlContext(astRoot.getOffset(), astRoot.getEndOffset());
            process(rootCtxInParsing, astRoot, rootCtxInParsing);
            rootCtx = rootCtxInParsing;
            DatabaseManager.setRoot(astRoot, rootCtx);
            return rootCtx;
        } else {
            return rootCtx;
        }
    }

    private void process(ErlContext rootCtx, ASTItem n, ErlContext currCtx) {
        if (isNode(n, "S")) {
            /**
             * pre-process
             * We should precess function declaration and imported functions first
             * so all function calls can refer to them.
             */
            Collection<ASTItem> postProcessForms = new ArrayList<ASTItem>();
            for (ASTItem item : n.getChildren()) {
                if (isNode(item, "Form")) {
                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "AttributeDeclaration")) {
                            preProcessAttibuteDeclaration(rootCtx, child);
                        } else if (isNode(child, "FunctionDeclaration")) {
                            preProcessFunctionDeclaration(rootCtx, child);
                        }
                    }
                    /** add all forms for post-precessing */
                    postProcessForms.add(item);
                }
            }

            /** post-process attributes, such as export */
            Collection<ASTItem> processedForms = new ArrayList<ASTItem>();
            for (ASTItem form : postProcessForms) {
                for (ASTItem item : form.getChildren()) {
                    if (isNode(item, "AttributeDeclaration")) {
                        postProcessAttibuteDeclaration(rootCtx, item);
                        processedForms.add(form);
                    }
                }
            }
            postProcessForms.removeAll(processedForms);

            /** normal process */
            for (ASTItem form : postProcessForms) {
                process(rootCtx, form, currCtx);
            }
        } else {
            for (ASTItem item : n.getChildren()) {
                if (isNode(item, "FunctionDeclaration")) {
                    /**
                     * only forms that are kind of function declaration are needed
                     * to detailed process now
                     */
                    postProcessFunctionDeclaration(rootCtx, item, currCtx);
                } else if (item instanceof ASTNode) {
                    process(rootCtx, item, currCtx);
                }
            }
        }
    }

    private void preProcessFunctionDeclaration(ErlContext rootCtx, ASTItem functionDeclaration) {
        for (ASTItem item : functionDeclaration.getChildren()) {
            if (isNode(item, "FunctionClauses")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "FunctionClause")) {
                        preProcessFunctionClause(rootCtx, child);
                    }
                }
            }
        }
    }

    private void preProcessFunctionClause(ErlContext rootCtx, ASTItem function) {
        String nameStr = "";
        String argumentsStr = "";
        int arityInt = 0;
        ASTToken functionName = null;
        for (ASTItem item : function.getChildren()) {
            if (isTokenTypeName(item, "atom")) {
                functionName = (ASTToken) item;
            } else if (isNode(item, "Exprs")) {
                /** arguments */
                argumentsStr = ((ASTNode) item).getAsText().trim();
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "Expr")) {
                        arityInt++;
                    }
                }
            }
        }
        if (functionName != null) {
            ErlFunction functionDfn = rootCtx.getFunctionInScope(functionName.getIdentifier(), arityInt);
            if (functionDfn == null) {
                nameStr = functionName.getIdentifier().trim();
                functionDfn = new ErlFunction(nameStr, functionName.getOffset(), functionName.getEndOffset(), arityInt);
                rootCtx.addDefinition(functionDfn);
            }
            rootCtx.addUsage(functionName, functionDfn);
            if (!argumentsStr.equals("")) {
                functionDfn.addArgumentsOpt(argumentsStr);
            }
        }
    }


    private void postProcessFunctionDeclaration(ErlContext rootCtx, ASTItem functionDeclaration, ErlContext currCtx) {
        for (ASTItem item : functionDeclaration.getChildren()) {
            if (isNode(item, "FunctionClauses")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "FunctionClause")) {
                        processFunctionClause(rootCtx, child, currCtx);
                    }
                }
            }
        }
    }

    private void processFunctionClause(ErlContext rootCtx, ASTItem functionClause, ErlContext currCtx) {
        ErlContext functionContext = new ErlContext(functionClause.getOffset(), functionClause.getEndOffset());
        currCtx.addContext(functionContext);

        for (ASTItem item : functionClause.getChildren()) {
            if (isNode(item, "Exprs")) {
                /** arguments */
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "Expr")) {
                        ASTItem expr = child;
                        List<ASTToken> tokensCollector = new ArrayList<ASTToken>();
                        findTokensInExprByType(expr, "var", tokensCollector);
                        for (ASTToken varToken : tokensCollector) {
                            String varNameStr = varToken.getIdentifier();
                            if (!varNameStr.equals("_")) {                                
                                ErlVariable variableDfn = functionContext.getVariableInScope(varNameStr);
                                if (variableDfn == null) {
                                    variableDfn = new ErlVariable(varNameStr, varToken.getOffset(), varToken.getEndOffset(), ErlVariable.Scope.PARAMETER);
                                    functionContext.addDefinition(variableDfn);
                                }
                                functionContext.addUsage(varToken, variableDfn);
                            }
                        }
                    }
                }
            } else if (isNode(item, "ClauseGuard")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "Guard")) {
                        processGuard(rootCtx, child, functionContext);
                    }
                }
            } else if (isNode(item, "FunctionRuleClauseBody")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "ClauseBody")) {
                        for (ASTItem child1 : child.getChildren()) {
                            if (isNode(child1, "Exprs")) {
                                processExprs(rootCtx, child1, functionContext);
                            }
                        }
                    }
                }
            }
        }
    }

    private void preProcessAttibuteDeclaration(ErlContext rootCtx, ASTItem attribute) {
        ASTItem attributeName = attribute.getChildren().get(0);
        if (attributeName != null) {
            if (isNode(attributeName, "ModuleAttribute")) {
                processModuleAttribute(rootCtx, attribute);
            } else if (isNode(attributeName, "ImportAttribute")) {
                processImportAttribute(rootCtx, attribute);
            } else if (isNode(attributeName, "RecordAttribute")) {
                processRecordAttribute(rootCtx, attribute);
            } else if (isNode(attributeName, "DefineAttribute")) {
                processMacroAttribute(rootCtx, attribute);
            } else if (isNode(attributeName, "IncludeAttribute")) {
                processIncludeAttribute(rootCtx, attribute);
            } else if (isNode(attributeName, "IncludeLibAttribute")) {
                processIncludeLibAttribute(rootCtx, attribute);
            }
        }
    }

    private void postProcessAttibuteDeclaration(ErlContext rootCtx, ASTItem attribute) {
        ASTItem attributeName = attribute.getChildren().get(0);
        if (attributeName != null) {
            if (isNode(attributeName, "ExportAttribute")) {
                processExportAttribute(rootCtx, attribute);
            }
        }
    }


    private void processModuleAttribute(ErlContext rootCtx, ASTItem attribute) {
        List<String> packages = new ArrayList<String>();
        String nameStr = "";
        for (ASTItem item : attribute.getChildren()) {
            if (isTokenTypeName(item, "atom")) {
                ASTToken nameToken = (ASTToken) item;
                nameStr = nameToken.getIdentifier();
                packages.add(nameStr);
            }
        }
        if (packages.size() > 0) {
            /** remove last one, which is module name */
            packages.remove(packages.size() - 1);
        }
        ErlModule moduleDfn = new ErlModule(nameStr.toString(), attribute.getOffset(), attribute.getEndOffset());
        for (String packageName : packages) {
            moduleDfn.addPackage(packageName);
        }
        rootCtx.addDefinition(moduleDfn);
    }


    private void processExportAttribute(ErlContext rootCtx, ASTItem attribute) {
        ErlExport exportDfn = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "ExportAttribute")) {
                exportDfn = new ErlExport(item.getOffset(), item.getEndOffset());
                rootCtx.addDefinition(exportDfn);                
            } else if (isNode(item, "FunctionNames")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "FunctionName")) {
                        ASTItem functionName = child;
                        ASTToken name = null;
                        ASTToken arity = null;
                        for (ASTItem child1 : functionName.getChildren()) {
                            if (isTokenTypeName(child1, "atom")) {
                                name = (ASTToken) child1;
                            } else if (isTokenTypeName(child1, "integer")) {
                                arity = (ASTToken) child1;
                            }
                        }
                        if (name != null && arity != null) {
                            int arityInt = Integer.parseInt(arity.getIdentifier());
                            ErlFunction functionDef = rootCtx.getFunctionInScope(name.getIdentifier(), arityInt);
                            if (functionDef != null) {
                                exportDfn.addFunction(functionDef);
                                rootCtx.addUsage(name, functionDef);
                            } else {
                                /** don't know offset of this function, just add it as 0 offset */
                                exportDfn.addFunction(new ErlFunction(name.getIdentifier(), arityInt));
                            }
                        }
                    }
                }
            }
        }
    }

    private void processImportAttribute(ErlContext rootCtx, ASTItem attribute) {
        ErlImport importDfn = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "ImportAttribute")) {
                importDfn = new ErlImport(item.getOffset(), item.getEndOffset());
                rootCtx.addDefinition(importDfn);
            } else if (isTokenTypeName(item, "atom")) {
                ASTToken nameToken = (ASTToken) item;
                String nameStr = nameToken.getIdentifier();
                importDfn.addPackage(nameStr);
            } else if (isNode(item, "FunctionNames")) {
                rootCtx.addDefinition(importDfn);
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "FunctionName")) {
                        ASTItem functionName = child;
                        ASTToken name = null;
                        ASTToken arity = null;
                        for (ASTItem child1 : functionName.getChildren()) {
                            if (isTokenTypeName(child1, "atom")) {
                                name = (ASTToken) child1;
                            } else if (isTokenTypeName(child1, "integer")) {
                                arity = (ASTToken) child1;
                            }
                        }
                        if (name != null && arity != null) {
                            String nameStr = name.getIdentifier();
                            int arityInt = Integer.parseInt(arity.getIdentifier());
                            /** @TODO add item to erlImport instead of context */
                            rootCtx.addDefinition(new ErlFunction(nameStr, functionName.getOffset(), functionName.getEndOffset(), arityInt));
                        }
                    }
                }
            }
        }
    }

    private void processRecordAttribute(ErlContext rootCtx, ASTItem attribute) {
        ErlRecord recordDfn = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "RecordName")) {
                String nameStr = ((ASTNode) item).getAsText();
                recordDfn = new ErlRecord(nameStr, item.getOffset(), item.getEndOffset());
                rootCtx.addDefinition(recordDfn);
            } else if (isNode(item, "RecordFieldNames") && recordDfn != null) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "RecordFieldName")) {
                        for (ASTItem child1 : child.getChildren()) {
                            if (isTokenTypeName(child1, "atom")) {
                                String field = ((ASTToken) child1).getIdentifier();
                                recordDfn.addField(field);
                            }
                        }
                    }
                }
            }
        }
    }

    private void processMacroAttribute(ErlContext rootCtx, ASTItem attribute) {
        ErlMacro macroDfn = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "MacroName")) {
                String nameStr = ((ASTNode) item).getAsText();
                macroDfn = new ErlMacro(nameStr, item.getOffset(), item.getEndOffset());
                rootCtx.addDefinition(macroDfn);
                for (ASTItem child : item.getChildren()) {
                    if (isTokenTypeName(child, "var")) {
                        rootCtx.addUsage((ASTToken) child, macroDfn);
                    }
                }
            } else if (isNode(item, "MacroParams") && macroDfn != null) {
                for (ASTItem child : item.getChildren()) {
                    if (isTokenTypeName(child, "var")) {
                        String paramNameStr = ((ASTToken) child).getIdentifier();
                        macroDfn.addParam(paramNameStr);
                    }
                }
            } else if (isNode(item, "MacroBody") && macroDfn != null) {
                String bodyStr = ((ASTNode) item).getAsText();
                /** strip last ")", @see definition of Macro in Erlang.nbs */
                bodyStr = bodyStr.substring(0, bodyStr.length() - 1);
                macroDfn.setBody(bodyStr);
            }
        }
    }

    private void processIncludeAttribute(ErlContext rootCtx, ASTItem attribute) {
        ErlInclude includeDfn = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isTokenTypeName(item, "string")) {
                ASTToken path = (ASTToken) item;
                String pathStr = path.getIdentifier();
                int strLength = pathStr.length();
                if (strLength >= 2) {
                    if (pathStr.charAt(0) == '"' && pathStr.charAt(strLength - 1) == '"') {
                        pathStr = pathStr.substring(1, strLength - 1);
                    }
                }
                /** includeDef point to a remote file, so, set offset to 0 */
                includeDfn = new ErlInclude(0, 0);
                rootCtx.addDefinition(includeDfn);

                includeDfn.setPath(pathStr);
                if (! forIndexing) {
                    /** @TODO search in project's -i paths and search in these include paths */
                    URL url = ErlangIndexProvider.getDefault().get(fo).getModuleFileUrl(ErlangIndexProvider.Type.Header, pathStr);
                    includeDfn.setSourceFileUrl(url);
                }
                /** add this usage to enable go to declartion */
                rootCtx.addUsage(path, includeDfn);
            }
        }
    }

    private void processIncludeLibAttribute(ErlContext rootCtx, ASTItem attribute) {
        ErlInclude includeDfn = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isTokenTypeName(item, "string")) {
                ASTToken path = (ASTToken) item;
                String pathStr = path.getIdentifier();
                int strLength = pathStr.length();
                if (strLength >= 2) {
                    if (pathStr.charAt(0) == '"' && pathStr.charAt(strLength - 1) == '"') {
                        pathStr = pathStr.substring(1, strLength - 1);
                    }
                }
                /** includeDef point to a remote file, so, set offset to 0 */
                includeDfn = new ErlInclude(0, 0);
                rootCtx.addDefinition(includeDfn);
                
                includeDfn.setLib(true);
                includeDfn.setPath(pathStr);
                if (! forIndexing) {
                    URL url = ErlangIndexProvider.getDefault().get(fo).getModuleFileUrl(ErlangIndexProvider.Type.Header, pathStr);
                    includeDfn.setSourceFileUrl(url);
                }
                /** add this usage to enable go to declartion */
                rootCtx.addUsage(path, includeDfn);
            }
        }
    }

    private void processGuard(ErlContext rootCtx, ASTItem guard, ErlContext currCtx) {
        for (ASTItem child : guard.getChildren()) {
            if (isNode(child, "Exprs")) {
                processExprs(rootCtx, child, currCtx);
            }
        }
    }

    private void processClauseGuard(ErlContext rootCtx, ASTItem clauseGuard, ErlContext currCtx) {
        for (ASTItem child : clauseGuard.getChildren()) {
            if (isNode(child, "Guard")) {
                processGuard(rootCtx, child, currCtx);
            }
        }
    }

    private void processClauseBody(ErlContext rootCtx, ASTItem clauseBody, ErlContext currCtx) {
        for (ASTItem child : clauseBody.getChildren()) {
            if (isNode(child, "Exprs")) {
                processExprs(rootCtx, child, currCtx);
            }
        }
    }

    private void processCrClauses(ErlContext rootCtx, ASTItem crClauses, ErlContext currCtx) {
        for (ASTItem child : crClauses.getChildren()) {
            if (isNode(child, "CrClause")) {
                ErlContext crClauseContext = new ErlContext(child.getOffset(), child.getEndOffset());
                currCtx.addContext(crClauseContext);

                for (ASTItem child1 : child.getChildren()) {
                    if (isNode(child1, "Expr")) {
                        /** case match expr */
                        processAnyExpr(rootCtx, child1, crClauseContext, true);
                    } else if (isNode(child1, "ClauseGuard")) {
                        processClauseGuard(rootCtx, child1, crClauseContext);
                    } else if (isNode(child1, "ClauseBody")) {
                        processClauseBody(rootCtx, child1, crClauseContext);
                    }
                }
            }
        }
    }

    private void processExprs(ErlContext rootCtx, ASTItem exprs, ErlContext currCtx) {
        for (ASTItem child : exprs.getChildren()) {
            if (isNode(child, "Expr")) {
                processAnyExpr(rootCtx, child, currCtx, false);
            }
        }
    }

    private void processAnyExpr(ErlContext rootCtx, ASTItem expr, ErlContext currCtx, boolean containsVarDfn) {
        if (isNode(expr, "FunctionCallExpr")) {
            processFunctionCallExpr(rootCtx, expr, currCtx);
        }

        if (isNode(expr, "MatchSendExpr")) {
            boolean isMatchExpr = false;
            ASTItem leftExpr = null;
            ASTItem rightExpr = null;
            for (ASTItem item : expr.getChildren()) {
                if (item instanceof ASTNode) {
                    if (leftExpr == null) {
                        leftExpr = item;
                    } else {
                        rightExpr = item;
                    }
                } else if (isToken(item, "=")) {
                    isMatchExpr = true;
                } else if (isToken(item, "!")) {
                    isMatchExpr = false;
                }
            }
            /** left hand */
            if (leftExpr != null) {
                if (isMatchExpr) {
                    /** This has been in a match expr, and left hand, new scoped Var declaration occurs */
                    containsVarDfn = true;
                }
                processAnyExpr(rootCtx, leftExpr, currCtx, containsVarDfn);
            }
            /** right hand */
            if (rightExpr != null) {
                if (isMatchExpr) {
                    /** This is in a match expr, and right hand, new scoped Var declaration may also occurs */
                    containsVarDfn = true;
                }
                processAnyExpr(rootCtx, rightExpr, currCtx, containsVarDfn);
            }
        } else if (isNode(expr, "IfExpr")) {
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "IfClauses")) {
                    ErlContext ifContext = new ErlContext(expr.getOffset(), expr.getEndOffset());
                    currCtx.addContext(ifContext);

                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "IfClause")) {
                            for (ASTItem child1 : child.getChildren()) {
                                if (isNode(child1, "Guard")) {
                                    processGuard(rootCtx, child1, ifContext);
                                } else if (isNode(child1, "ClauseBody")) {
                                    processClauseBody(rootCtx, child1, ifContext);
                                }
                            }
                        }
                    }
                }
            }
        } else if (isNode(expr, "CaseExpr")) {
            ErlContext caseContext = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "Expr")) {
                    caseContext = new ErlContext(expr.getOffset(), expr.getEndOffset());
                    currCtx.addContext(caseContext);

                    processAnyExpr(rootCtx, item, caseContext, containsVarDfn);
                } else if (isNode(item, "CrClauses") && caseContext != null) {
                    processCrClauses(rootCtx, item, caseContext);
                }
            }
        } else if (isNode(expr, "ReceiveExpr")) {
            ASTItem afterExpr = null;
            ASTItem afterClauseBody = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "CrClauses")) {
                    processCrClauses(rootCtx, item, currCtx);
                } else if (isNode(item, "Expr")) {
                    afterExpr = item;
                } else if (isNode(item, "ClauseBody")) {
                    afterClauseBody = item;
                }
            }

            if (afterExpr != null && afterClauseBody != null) {
                ErlContext afterContext = new ErlContext(afterExpr.getOffset(), afterClauseBody.getEndOffset());
                currCtx.addContext(afterContext);

                processAnyExpr(rootCtx, afterExpr, afterContext, true);
                processClauseBody(rootCtx, afterClauseBody, afterContext);
            }
        } else if (isNode(expr, "FunExpr")) {
            boolean remoteFun = false;
            ASTToken remoteName = null;
            ASTToken funName = null;
            ASTToken arity = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "FunClauses")) {
                    ErlContext funContext = new ErlContext(expr.getOffset(), expr.getEndOffset());
                    currCtx.addContext(funContext);

                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "FunClause")) {
                            for (ASTItem child1 : child.getChildren()) {
                                if (isNode(child1, "Exprs")) {
                                    /** arguments */
                                    for (ASTItem child2 : child1.getChildren()) {
                                        if (isNode(child2, "Expr")) {
                                            processAnyExpr(rootCtx, child2, funContext, true);
                                        }
                                    }
                                } else if (isNode(child1, "ClauseGuard")) {
                                    processClauseGuard(rootCtx, child1, funContext);
                                } else if (isNode(child1, "ClauseBody")) {
                                    processClauseBody(rootCtx, child1, funContext);
                                }
                            }
                        }
                    }
                    break;
                } else {
                    if (isTokenTypeName(item, "atom")) {
                        if (remoteFun) {
                            funName = (ASTToken) item;
                        } else {
                            remoteName = (ASTToken) item;
                        }
                    } else if (isToken(item, ":")) {
                        remoteFun = true;
                    } else if (isTokenTypeName(item, "integer")) {
                        arity = (ASTToken) item;
                    }
                    if (arity != null) {
                        int arityInt = Integer.parseInt(arity.getIdentifier());
                        if (remoteFun && remoteName != null && funName != null) {
                            ErlFunction functionDfn = rootCtx.getFunctionInScope(remoteName.getIdentifier(), funName.getIdentifier(), arityInt);
                            if (functionDfn == null) {
                                if (! forIndexing) {
                                    functionDfn = ErlangIndexProvider.getDefault().get(fo).getFunction(remoteName.getIdentifier(), funName.getIdentifier(), arityInt);
                                    if (functionDfn != null) {
                                        rootCtx.addDefinition(functionDfn);
                                        currCtx.addUsage(funName, functionDfn);
                                    }
                                }
                            } else {
                                currCtx.addUsage(funName, functionDfn);
                            }
                        } else if (remoteName != null) {
                            funName = remoteName;
                            ErlFunction functionDfn = rootCtx.getFunctionInScope(funName.getIdentifier(), arityInt);
                            if (functionDfn == null) {
                                /** this is a built-in function call? */
                                functionDfn = ErlBuiltIn.getBuiltInFunction(funName.getIdentifier(), arityInt);
                                if (functionDfn != null) {
                                    rootCtx.addDefinition(functionDfn);
                                    currCtx.addUsage(funName, functionDfn);
                                }
                            } else {
                                currCtx.addUsage(funName, functionDfn);
                            }
                        }
                    }
                }
            }
        } else if (isNode(expr, "List") || isNode(expr, "Binary")) {
            ErlContext listCompContext = null;
            for (ASTItem item : expr.getChildren()) {
                /** test if it's listComprehensionExprsNode, and got the Var declarations first */
                if (isNode(item, "ListComprehensionExprs")) {
                    listCompContext = new ErlContext(expr.getOffset(), expr.getEndOffset());
                    currCtx.addContext(listCompContext);

                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "ListComprehensionExpr")) {
                            int i = 0;
                            for (ASTItem child1 : child.getChildren()) {
                                if (isNode(child1, "Expr")) {
                                    boolean containsVarDecls1 = i == 0;
                                    processAnyExpr(rootCtx, child1, listCompContext, containsVarDecls1);
                                    i++;
                                }
                            }
                        }
                    }
                } else if (isNode(item, "ListTail")) {
                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "Expr")) {
                            processAnyExpr(rootCtx, child, currCtx, containsVarDfn);
                        }
                    }
                }
            }

            /* we'll process expr at last, since we should get listCompContext first if Applicable */
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "Expr") || isNode(item, "BinElement")) {
                    if (listCompContext != null) {
                        /** List Comprehension */
                        processAnyExpr(rootCtx, item, listCompContext, false);
                    } else {
                        /** List */
                        processAnyExpr(rootCtx, item, currCtx, containsVarDfn);
                    }
                }
            }
        } else if (isNode(expr, "TryExpr")) {
            ErlContext tryContext = new ErlContext(expr.getOffset(), expr.getEndOffset());
            currCtx.addContext(tryContext);

            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "Exprs")) {
                    processExprs(rootCtx, item, tryContext);
                } else if (isNode(item, "CrClauses")) {
                    processCrClauses(rootCtx, item, tryContext);
                } else if (isNode(item, "TryCatch")) {
                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "TryClauses")) {
                            for (ASTItem child1 : child.getChildren()) {
                                if (isNode(child1, "TryClause")) {
                                    ErlContext tryClauseContext = new ErlContext(child1.getOffset(), child1.getEndOffset());
                                    tryContext.addContext(tryClauseContext);

                                    for (ASTItem child2 : child1.getChildren()) {
                                        if (isNode(child2, "Expr")) {
                                            processAnyExpr(rootCtx, child2, tryClauseContext, true);
                                        } else if (isNode(child2, "ClauseGuard")) {
                                            processClauseGuard(rootCtx, child2, tryClauseContext);
                                        } else if (isNode(child2, "ClauseBody")) {
                                            processClauseBody(rootCtx, child2, tryClauseContext);
                                        }
                                    }
                                }
                            }
                        } else if (isNode(child, "Exprs")) {
                            processExprs(rootCtx, child, tryContext);
                        }
                    }
                }
            }
        } else if (isNode(expr, "Macro")) {
            for (ASTItem item : expr.getChildren()) {
                if (isTokenTypeName(item, "var") || isTokenTypeName(item, "atom")) {
                    ASTToken macroName = (ASTToken) item;
                    String macroNameStr = macroName.getIdentifier();
                    ErlMacro macroDfn = currCtx.getMacroInScope(macroNameStr);
                    if (macroDfn == null) {
                        macroDfn = ErlMacro.getPreDefined(macroNameStr);
                        if (macroDfn != null) {
                            rootCtx.addDefinition(macroDfn);
                            currCtx.addUsage(macroName, macroDfn);
                        }
                    } else {
                        currCtx.addUsage(macroName, macroDfn);
                    }
                }
            }
        } else {
            for (ASTItem item : expr.getChildren()) {
                if (isTokenTypeName(item, "var")) {
                    ASTToken var = (ASTToken) item;
                    if (! var.getIdentifier().equals("_")) {
                        ErlVariable variableDfn = currCtx.getVariableInScope(var.getIdentifier());
                        if (variableDfn == null) {
                            if (containsVarDfn) {
                                variableDfn = new ErlVariable(var.getIdentifier(), expr.getOffset(), expr.getEndOffset(), ErlVariable.Scope.LOCAL);
                                currCtx.addDefinition(variableDfn);
                                currCtx.addUsage(var, variableDfn);
                            }
                        } else {
                            currCtx.addUsage(var, variableDfn);
                        }
                    } else {
                         currCtx.addUsage(var, WILD_VAR);
                    }
                } else if (item instanceof ASTNode) {
                    processAnyExpr(rootCtx, item, currCtx, containsVarDfn);
                }
            }
        }
    }

    private void processFunctionCallExpr(ErlContext rootCtx, ASTItem functionCallExpr, ErlContext currCtx) {
        boolean isFunctionCall = false;
        ASTItem remoteExpr = null;
        ASTItem arguments = null;
        for (ASTItem item : functionCallExpr.getChildren()) {
            if (isToken(item, "(")) {
                isFunctionCall = true;
            } else if (isNode(item, "Exprs")) {
                arguments = item;
            } else if (isNode(item, "RemoteExpr")) {
                remoteExpr = item;
            }
        }
        if (isFunctionCall) {
            int arityInt = 0;
            if (arguments != null) {
                for (ASTItem child : arguments.getChildren()) {
                    if (isNode(child, "Expr")) {
                        arityInt++;
                    }
                }
            }

            boolean isRemoteFunctionCall = false;
            ASTItem recordFieldExpr = null;
            ASTItem remoteCallPrimaryExpr = null;
            for (ASTItem item : remoteExpr.getChildren()) {
                if (isNode(item, "RecordFieldExpr")) {
                    recordFieldExpr = item;
                } else if (isNode(item, "PrimaryExpr")) {
                    remoteCallPrimaryExpr = item;
                    isRemoteFunctionCall = true;
                }
            }


            ASTItem prefixPrimaryExpr = null;
            for (ASTItem item : recordFieldExpr.getChildren()) {
                if (isNode(item, "PrimaryExpr")) {
                    prefixPrimaryExpr = item;
                }
            }

            if (prefixPrimaryExpr != null) {
                if (isRemoteFunctionCall) {
                    ASTItem remotePrimaryExpr = prefixPrimaryExpr;
                    ASTItem callPrimaryExpr = remoteCallPrimaryExpr;

                    ASTToken remoteName = getAtomTokenFromPrimaryExpr(remotePrimaryExpr);
                    ASTToken functionCallName = getAtomTokenFromPrimaryExpr(callPrimaryExpr);
                    if (remoteName != null && functionCallName != null) {
                        /** @TODO use actaul arity instead of 0 */
                        ErlFunction functionDfn = rootCtx.getFunctionInScope(remoteName.getIdentifier(), functionCallName.getIdentifier(), arityInt);
                        if (functionDfn == null) {
                            if (! forIndexing) {
                                functionDfn = ErlangIndexProvider.getDefault().get(fo).getFunction(remoteName.getIdentifier(), functionCallName.getIdentifier(), arityInt);
                                if (functionDfn != null) {
                                    rootCtx.addDefinition(functionDfn);
                                    currCtx.addUsage(functionCallName, functionDfn);
                                }
                            }
                        } else {
                            currCtx.addUsage(functionCallName, functionDfn);
                        }
                    }
                } else {
                    ASTItem callPrimaryExpr = prefixPrimaryExpr;

                    ASTToken functionCallName = getAtomTokenFromPrimaryExpr(callPrimaryExpr);
                    if (functionCallName != null) {
                        ErlFunction functionDfn = rootCtx.getFunctionInScope(functionCallName.getIdentifier(), arityInt);
                        if (functionDfn == null) {
                            /** Is it a built-in function call? */
                            functionDfn = ErlBuiltIn.getBuiltInFunction(functionCallName.getIdentifier(), arityInt);
                            if (functionDfn != null) {
                                rootCtx.addDefinition(functionDfn);
                                currCtx.addUsage(functionCallName, functionDfn);
                            }
                        } else {
                            currCtx.addUsage(functionCallName, functionDfn);
                        }
                    }
                }
            }
        }
    }


    /** helper ... */

    private static void findTokensInExprByType(ASTItem expr, String type, List<ASTToken> tokensCollecter) {
        if (isNode(expr, "Macro")) {
            return;
        }
        for (ASTItem child : expr.getChildren()) {
            if (isTokenTypeName(child, type)) {
                tokensCollecter.add((ASTToken) child);
            } else if (child instanceof ASTNode) {
                findTokensInExprByType(child, type, tokensCollecter);
            }
        }
    }

    /**
     * will also check if item is null
     */
    public static final boolean isNode(ASTItem item, String nt) {
        return item != null && item instanceof ASTNode && ((ASTNode) item).getNT().equals(nt);
    }

    /**
     * will also check if item is null
     */
    public static final boolean isToken(ASTItem item, String id) {
        return item != null && item instanceof ASTToken && ((ASTToken) item).getIdentifier().equals(id);
    }

    /**
     * will also check if item is null
     */
    public static final boolean isTokenTypeName(ASTItem item, String type) {
        return item != null && item instanceof ASTToken && ((ASTToken) item).getTypeName().equals(type);
    }

    private static ASTToken getAtomTokenFromPrimaryExpr(ASTItem primaryExpr) {
        for (ASTItem item : primaryExpr.getChildren()) {
            if (isTokenTypeName(item, "atom")) {
                return (ASTToken) item;
            }
        }
        return null;
    }

    private static List<String> pathNames = new ArrayList<String>();
    private static List<Integer> pathPositions = new ArrayList<Integer>();

    //private static final String xpathRegrex = "((\\.)?(([a-z]|[A-Z])([a-z]|[A-Z]|[0-9])*(\\[([0-9]+)\\])?))+";
    //private static final Pattern xpathPattern = Pattern.compile(xpathRegrex);
    public static ASTItem path(ASTItem fromItem, String relativePath) {
        pathNames.clear();
        pathPositions.clear();
        String[] elements = relativePath.split(".");
        for (String element : elements) {
            int position1 = element.indexOf('[');
            int position2 = element.indexOf(']');
            int position = 0;
            if (position1 > 0 && position2 > 0) {
                position = Integer.parseInt(element.substring(position1 + 1, position2));
            }
            pathNames.add(element);
            pathPositions.add(position);
        }
        return searchItem(fromItem, pathNames, pathPositions);
    }

    private static ASTItem searchItem(ASTItem fromItem, List<String> pathNames, List<Integer> pathPositions) {
        if (pathNames.size() == 0) {
            return null;
        }
        String nameWant = pathNames.get(0);
        int positionWant = pathPositions.get(0);
        int position = 0;
        for (ASTItem child : fromItem.getChildren()) {
            String name = child instanceof ASTToken ? ((ASTToken) child).getIdentifier() : ((ASTNode) child).getNT();

            if (name.equals(nameWant)) {
                if (position == positionWant) {
                    if (pathNames.size() == 1) {
                        return child;
                    } else {
                        pathNames.remove(0);
                        pathPositions.remove(0);
                        return searchItem(child, pathNames, pathPositions);
                    }
                } else {
                    position++;
                }
            }
        }
        return null;
    }

    private static void waitingForParsingFinished(ParserManager parserManager) {
        int counter = 0;
        try {
            while (((parserManager.getState() == ParserManager.State.NOT_PARSED) ||
                    (parserManager.getState() == ParserManager.State.PARSING)) && counter < 200) {
                Thread.sleep(100);
                counter++;
            }
        } catch (InterruptedException e) {
        }
    }
    
}
