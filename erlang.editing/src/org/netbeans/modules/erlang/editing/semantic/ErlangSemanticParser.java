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
import org.netbeans.api.languages.SyntaxContext;
import org.netbeans.api.languages.database.DatabaseManager;
import org.netbeans.modules.erlang.editing.spi.ErlangIndexProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Caoyuan Deng
 */
public class ErlangSemanticParser {

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

    private static Map<Document, ErlangSemanticParser> docToParser = new WeakHashMap<Document, ErlangSemanticParser>();

    private static ErlangSemanticParser PARSER_FOR_INDEXING = new ErlangSemanticParser(true);

    private Document doc;
    private ASTNode astRoot; // syntax ast root
    private ErlRoot erlRoot; // semantic root
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

    private ErlangSemanticParser(Document doc) {
        this.doc = doc;
    }

    private ErlangSemanticParser(boolean forIndexing) {
        this.doc = null;
        this.forIndexing = forIndexing;
    }

    public ASTNode getAstRoot() {
        return astRoot;
    }

    public ErlRoot getErlRoot() {
        return erlRoot;
    }

    public State getState() {
        ParserManager parserManager = ParserManager.get(doc);
        assert parserManager != null;
        return parserManager.getState();
    }

    public static ErlangSemanticParser getParserForIndexing() {
        return PARSER_FOR_INDEXING;
    }

    public static ErlangSemanticParser getParser(FileObject fo) {
        for (Document doc : docToParser.keySet()) {
            DataObject dobj = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
            if (dobj != null && dobj.getPrimaryFile() == fo) {
                return docToParser.get(doc);
            }
        }

        return null;
    }

    public static ErlangSemanticParser getParser(final Document doc) {
        if (doc == null) {
            return PARSER_FOR_INDEXING;
        }

        ErlangSemanticParser parser = docToParser.get(doc);
        if (parser == null) {
            parser = new ErlangSemanticParser(doc);
            docToParser.put(doc, parser);
        }

        return parser;
    }


    /**
     * This is the method will be called by GLF feature as declared in Erlang.nbs:
     * AST {
     *   process:org.netbeans.modules.languages.erlang.semantic.ErlangSemanticParser.process
     * }
     *
     * @Notice astRoot may be changed after this feature calling? if so, the doc <--> astRoot is useless to prevent redudant parsing.
     */
    public static void process(SyntaxContext syntaxContext) {
        Document doc = syntaxContext.getDocument();
        ASTNode astRoot = (ASTNode) syntaxContext.getASTPath().getRoot();
        /**
         * if our process also changed AST, then we should return it,
         * and, we should process semantic according to the changed astRoot.
         */
        ErlangSemanticParser parser = getParser(doc);
        parser.parse(astRoot);

        /** this feature call may be void return or ASTNode return, if later,
         * Language engine will accept the ASTNode, otherwise, should keep the
         * original astRoot that was passed in via syntaxtContext
         */
    }

    public static ErlRoot getErlRoot(Document doc, ASTNode astRoot) {
        ErlangSemanticParser parser = getParser(doc);
        ErlRoot erlRoot = parser.getErlRoot();
        if (erlRoot != null) {
            /**
             * although we have a syntax parser listener which will redo semantic
             * parser when new syntax happened, but SemanticHilightingsLayer may
             * act before our listener, so, just check if we need do here:
             *
             * check if the AstRoot has been re-parsed, if so, we should
             * remove oldAstRoot and redo semantic parsing
             */
            if (parser.getAstRoot() != astRoot) {
                erlRoot = parser.parse(astRoot);
            }
        } else {
            erlRoot = parser.parse(astRoot);
        }

        return erlRoot;
    }
    
    public static ErlRoot getCurrentErlRoot(Document doc) {
        ErlangSemanticParser parser = getParser(doc);
        return parser.getErlRoot();
    }

    /**
     * @NOTICE we should avoid re-entrant of parse. There is a case may cause that:
     * When document is modified, it will be setDirty in PersistentClassIndex, and request
     * a parseFiles task for updateIndex, thus, a bad cycle calling:
     * parseFiles -> semanticParser.parse -> getDeclaration ->
     * index.gsfSearch -> index.updateDirty -> parseFiles -> semanticParser.parse.
     */
    public ErlRoot parse(ASTNode astRoot) {
        if (this.astRoot != astRoot) {
            this.astRoot = astRoot;
            ErlRoot erlRootInParsing = new ErlRoot(astRoot);
            process(erlRootInParsing, astRoot, erlRootInParsing.getRootContext());
            erlRoot = erlRootInParsing;
            DatabaseManager.setRoot(astRoot, erlRoot.getRootContext());
            return erlRoot;
        } else {
            return erlRoot;
        }
    }

    private void process(ErlRoot erlRoot, ASTItem n, ErlContext currentContext) {
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
                            preProcessAttibuteDeclaration(erlRoot, child);
                        } else if (isNode(child, "FunctionDeclaration")) {
                            preProcessFunctionDeclaration(erlRoot, child);
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
                        postProcessAttibuteDeclaration(erlRoot, item);
                        processedForms.add(form);
                    }
                }
            }
            postProcessForms.removeAll(processedForms);

            /** normal process */
            for (ASTItem form : postProcessForms) {
                process(erlRoot, form, currentContext);
            }
        } else {
            for (ASTItem item : n.getChildren()) {
                if (isNode(item, "FunctionDeclaration")) {
                    /**
                     * only forms that are kind of function declaration are needed
                     * to detailed process now
                     */
                    postProcessFunctionDeclaration(erlRoot, item, currentContext);
                } else if (item instanceof ASTNode) {
                    process(erlRoot, item, currentContext);
                }
            }
        }
    }

    private void preProcessFunctionDeclaration(ErlRoot erlRoot, ASTItem functionDeclaration) {
        for (ASTItem item : functionDeclaration.getChildren()) {
            if (isNode(item, "FunctionClauses")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "FunctionClause")) {
                        preProcessFunctionClause(erlRoot, child);
                    }
                }
            }
        }
    }

    private void preProcessFunctionClause(ErlRoot erlRoot, ASTItem function) {
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
            ErlFunction functionDef = erlRoot.getRootContext().getFunctionInScope(functionName.getIdentifier(), arityInt);
            if (functionDef == null) {
                nameStr = functionName.getIdentifier().trim();
                functionDef = new ErlFunction(nameStr, function.getOffset(), function.getEndOffset(), arityInt);
                erlRoot.getRootContext().addDefinition(functionDef);
            }
            erlRoot.getRootContext().addUsage(functionName, functionDef);
            if (!argumentsStr.equals("")) {
                functionDef.addArgumentsOpt(argumentsStr);
            }
        }
    }


    private void postProcessFunctionDeclaration(ErlRoot erlRoot, ASTItem functionDeclaration, ErlContext currentContext) {
        for (ASTItem item : functionDeclaration.getChildren()) {
            if (isNode(item, "FunctionClauses")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "FunctionClause")) {
                        processFunctionClause(erlRoot, child, currentContext);
                    }
                }
            }
        }
    }

    private void processFunctionClause(ErlRoot erlRoot, ASTItem functionClause, ErlContext currentContext) {
        ErlContext functionContext = new ErlContext(functionClause.getOffset(), functionClause.getEndOffset());
        currentContext.addContext(functionContext);

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
                                ErlVariable variableDef = functionContext.getVariableInScope(varNameStr);
                                if (variableDef == null) {
                                    variableDef = new ErlVariable(varNameStr, varToken.getOffset(), varToken.getEndOffset(), ErlVariable.Scope.PARAMETER);
                                    functionContext.addDefinition(variableDef);
                                }
                                functionContext.addUsage(varToken, variableDef);
                            }
                        }
                    }
                }
            } else if (isNode(item, "ClauseGuard")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "Guard")) {
                        processGuard(erlRoot, child, functionContext);
                    }
                }
            } else if (isNode(item, "FunctionRuleClauseBody")) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "ClauseBody")) {
                        for (ASTItem child1 : child.getChildren()) {
                            if (isNode(child1, "Exprs")) {
                                processExprs(erlRoot, child1, functionContext);
                            }
                        }
                    }
                }
            }
        }
    }

    private void preProcessAttibuteDeclaration(ErlRoot erlRoot, ASTItem attribute) {
        ASTItem attributeName = attribute.getChildren().get(0);
        if (attributeName != null) {
            if (isNode(attributeName, "ModuleAttribute")) {
                processModuleAttribute(erlRoot, attribute);
            } else if (isNode(attributeName, "ImportAttribute")) {
                processImportAttribute(erlRoot, attribute);
            } else if (isNode(attributeName, "RecordAttribute")) {
                processRecordAttribute(erlRoot, attribute);
            } else if (isNode(attributeName, "DefineAttribute")) {
                processMacroAttribute(erlRoot, attribute);
            } else if (isNode(attributeName, "IncludeAttribute")) {
                processIncludeAttribute(erlRoot, attribute);
            } else if (isNode(attributeName, "IncludeLibAttribute")) {
                processIncludeLibAttribute(erlRoot, attribute);
            }
        }
    }

    private void postProcessAttibuteDeclaration(ErlRoot erlRoot, ASTItem attribute) {
        ASTItem attributeName = attribute.getChildren().get(0);
        if (attributeName != null) {
            if (isNode(attributeName, "ExportAttribute")) {
                processExportAttribute(erlRoot, attribute);
            }
        }
    }


    private void processModuleAttribute(ErlRoot erlRoot, ASTItem attribute) {
        List<String> packages = new ArrayList<String>();
        String nameStr = "";
        for (ASTItem item : attribute.getChildren()) {
            if (isTokenTypeName(item, "atom")) {
                ASTToken moduleName = (ASTToken) item;
                nameStr = moduleName.getIdentifier();
                packages.add(nameStr);
            }
        }
        if (packages.size() > 0) {
            /** remove last one, which is module name */
            packages.remove(packages.size() - 1);
        }
        ErlModule moduleDef = new ErlModule(nameStr.toString(), attribute.getOffset(), attribute.getEndOffset());
        for (String packageName : packages) {
            moduleDef.addPackage(packageName);
        }
        erlRoot.getRootContext().addDefinition(moduleDef);
    }


    private void processExportAttribute(ErlRoot erlRoot, ASTItem attribute) {
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "FunctionNames")) {
                ErlExport exportDef = new ErlExport(attribute.getOffset(), attribute.getEndOffset());
                erlRoot.getRootContext().addDefinition(exportDef);
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
                            ErlFunction functionDef = erlRoot.getRootContext().getFunctionInScope(name.getIdentifier(), arityInt);
                            if (functionDef != null) {
                                exportDef.addFunction(functionDef);
                                erlRoot.getRootContext().addUsage(name, functionDef);
                            } else {
                                /** don't know offset of this function, just add it as 0 offset */
                                exportDef.addFunction(new ErlFunction(name.getIdentifier(), arityInt));
                            }
                        }
                    }
                }
            }
        }
    }

    private void processImportAttribute(ErlRoot erlRoot, ASTItem attribute) {
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "FunctionNames")) {
                ErlImport importDef = new ErlImport(attribute.getOffset(), attribute.getEndOffset());
                erlRoot.getRootContext().addDefinition(importDef);
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
                            erlRoot.getRootContext().addDefinition(new ErlFunction(nameStr, functionName.getOffset(), functionName.getEndOffset(), arityInt));
                        }
                    }
                }
            }
        }
    }

    private void processRecordAttribute(ErlRoot erlRoot, ASTItem attribute) {
        ErlRecord recordDef = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "RecordName")) {
                String nameStr = ((ASTNode) item).getAsText();
                recordDef = new ErlRecord(nameStr, attribute.getOffset(), attribute.getEndOffset());
                erlRoot.getRootContext().addDefinition(recordDef);
            } else if (isNode(item, "RecordFieldNames") && recordDef != null) {
                for (ASTItem child : item.getChildren()) {
                    if (isNode(child, "RecordFieldName")) {
                        for (ASTItem child1 : child.getChildren()) {
                            if (isTokenTypeName(child1, "atom")) {
                                String field = ((ASTToken) child1).getIdentifier();
                                recordDef.addField(field);
                            }
                        }
                    }
                }
            }
        }
    }

    private void processMacroAttribute(ErlRoot erlRoot, ASTItem attribute) {
        ErlMacro macroDef = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "MacroName")) {
                String nameStr = ((ASTNode) item).getAsText();
                macroDef = new ErlMacro(nameStr, attribute.getOffset(), attribute.getEndOffset());
                erlRoot.getRootContext().addDefinition(macroDef);
                for (ASTItem child : item.getChildren()) {
                    if (isTokenTypeName(child, "var")) {
                        erlRoot.getRootContext().addUsage((ASTToken) child, macroDef);
                    }
                }
            } else if (isNode(item, "MacroParams") && macroDef != null) {
                for (ASTItem child : item.getChildren()) {
                    if (isTokenTypeName(child, "var")) {
                        String paramNameStr = ((ASTToken) child).getIdentifier();
                        macroDef.addParam(paramNameStr);
                    }
                }
            } else if (isNode(item, "MacroBody") && macroDef != null) {
                String bodyStr = ((ASTNode) item).getAsText();
                /** strip last ")", @see definition of Macro in Erlang.nbs */
                bodyStr = bodyStr.substring(0, bodyStr.length() - 1);
                macroDef.setBody(bodyStr);
            }
        }
    }

    private void processIncludeAttribute(ErlRoot erlRoot, ASTItem attribute) {
        ErlInclude includeDef = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isTokenTypeName(item, "string")) {
                includeDef = new ErlInclude(attribute.getOffset(), attribute.getEndOffset());
                erlRoot.getRootContext().addDefinition(includeDef);
                ASTToken path = (ASTToken) item;
                String pathStr = path.getIdentifier();
                int strLength = pathStr.length();
                if (strLength >= 2) {
                    if (pathStr.charAt(0) == '"' && pathStr.charAt(strLength - 1) == '"') {
                        pathStr = pathStr.substring(1, strLength - 1);
                    }
                }
                includeDef.setPath(pathStr);

                /** add this usage to enable go to declartion */
                erlRoot.getRootContext().addUsage(path, includeDef);
            }
        }
    }

    private void processIncludeLibAttribute(ErlRoot erlRoot, ASTItem attribute) {
        ErlInclude includeDef = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isTokenTypeName(item, "string")) {
                includeDef = new ErlInclude(attribute.getOffset(), attribute.getEndOffset());
                includeDef.setLib(true);
                erlRoot.getRootContext().addDefinition(includeDef);
                ASTToken path = (ASTToken) item;
                String pathStr = path.getIdentifier();
                int strLength = pathStr.length();
                if (strLength >= 2) {
                    if (pathStr.charAt(0) == '"' && pathStr.charAt(strLength - 1) == '"') {
                        pathStr = pathStr.substring(1, strLength - 1);
                    }
                }
                includeDef.setPath(pathStr);

                /** add this usage to enable go to declartion */
                erlRoot.getRootContext().addUsage(path, includeDef);
            }
        }
    }

    private void processGuard(ErlRoot erlRoot, ASTItem guard, ErlContext currentContext) {
        for (ASTItem child : guard.getChildren()) {
            if (isNode(child, "Exprs")) {
                processExprs(erlRoot, child, currentContext);
            }
        }
    }

    private void processClauseGuard(ErlRoot erlRoot, ASTItem clauseGuard, ErlContext currentContext) {
        for (ASTItem child : clauseGuard.getChildren()) {
            if (isNode(child, "Guard")) {
                processGuard(erlRoot, child, currentContext);
            }
        }
    }

    private void processClauseBody(ErlRoot erlRoot, ASTItem clauseBody, ErlContext currentContext) {
        for (ASTItem child : clauseBody.getChildren()) {
            if (isNode(child, "Exprs")) {
                processExprs(erlRoot, child, currentContext);
            }
        }
    }

    private void processCrClauses(ErlRoot erlRoot, ASTItem crClauses, ErlContext currentContext) {
        for (ASTItem child : crClauses.getChildren()) {
            if (isNode(child, "CrClause")) {
                ErlContext crClauseContext = new ErlContext(child.getOffset(), child.getEndOffset());
                currentContext.addContext(crClauseContext);

                for (ASTItem child1 : child.getChildren()) {
                    if (isNode(child1, "Expr")) {
                        /** case match expr */
                        processAnyExpr(erlRoot, child1, crClauseContext, true);
                    } else if (isNode(child1, "ClauseGuard")) {
                        processClauseGuard(erlRoot, child1, crClauseContext);
                    } else if (isNode(child1, "ClauseBody")) {
                        processClauseBody(erlRoot, child1, crClauseContext);
                    }
                }
            }
        }
    }

    private void processExprs(ErlRoot erlRoot, ASTItem exprs, ErlContext currentContext) {
        for (ASTItem child : exprs.getChildren()) {
            if (isNode(child, "Expr")) {
                processAnyExpr(erlRoot, child, currentContext, false);
            }
        }
    }

    private void processAnyExpr(ErlRoot erlRoot, ASTItem expr, ErlContext currentContext, boolean containsVarDef) {
        if (isNode(expr, "FunctionCallExpr")) {
            processFunctionCallExpr(erlRoot, expr, currentContext);
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
                    containsVarDef = true;
                }
                processAnyExpr(erlRoot, leftExpr, currentContext, containsVarDef);
            }
            /** right hand */
            if (rightExpr != null) {
                if (isMatchExpr) {
                    /** This is in a match expr, and right hand, new scoped Var declaration may also occurs */
                    containsVarDef = true;
                }
                processAnyExpr(erlRoot, rightExpr, currentContext, containsVarDef);
            }
        } else if (isNode(expr, "IfExpr")) {
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "IfClauses")) {
                    ErlContext ifContext = new ErlContext(expr.getOffset(), expr.getEndOffset());
                    currentContext.addContext(ifContext);

                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "IfClause")) {
                            for (ASTItem child1 : child.getChildren()) {
                                if (isNode(child1, "Guard")) {
                                    processGuard(erlRoot, child1, ifContext);
                                } else if (isNode(child1, "ClauseBody")) {
                                    processClauseBody(erlRoot, child1, ifContext);
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
                    currentContext.addContext(caseContext);

                    processAnyExpr(erlRoot, item, caseContext, containsVarDef);
                } else if (isNode(item, "CrClauses") && caseContext != null) {
                    processCrClauses(erlRoot, item, caseContext);
                }
            }
        } else if (isNode(expr, "ReceiveExpr")) {
            ASTItem afterExpr = null;
            ASTItem afterClauseBody = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "CrClauses")) {
                    processCrClauses(erlRoot, item, currentContext);
                } else if (isNode(item, "Expr")) {
                    afterExpr = item;
                } else if (isNode(item, "ClauseBody")) {
                    afterClauseBody = item;
                }
            }

            if (afterExpr != null && afterClauseBody != null) {
                ErlContext afterContext = new ErlContext(afterExpr.getOffset(), afterClauseBody.getEndOffset());
                currentContext.addContext(afterContext);

                processAnyExpr(erlRoot, afterExpr, afterContext, true);
                processClauseBody(erlRoot, afterClauseBody, afterContext);
            }
        } else if (isNode(expr, "FunExpr")) {
            boolean remoteFun = false;
            ASTToken remoteName = null;
            ASTToken funName = null;
            ASTToken arity = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "FunClauses")) {
                    ErlContext funContext = new ErlContext(expr.getOffset(), expr.getEndOffset());
                    currentContext.addContext(funContext);

                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "FunClause")) {
                            for (ASTItem child1 : child.getChildren()) {
                                if (isNode(child1, "Exprs")) {
                                    /** arguments */
                                    for (ASTItem child2 : child1.getChildren()) {
                                        if (isNode(child2, "Expr")) {
                                            processAnyExpr(erlRoot, child2, funContext, true);
                                        }
                                    }
                                } else if (isNode(child1, "ClauseGuard")) {
                                    processClauseGuard(erlRoot, child1, funContext);
                                } else if (isNode(child1, "ClauseBody")) {
                                    processClauseBody(erlRoot, child1, funContext);
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
                            ErlFunction functionDef = erlRoot.getRootContext().getFunctionInScope(remoteName.getIdentifier(), funName.getIdentifier(), arityInt);
                            if (functionDef == null) {
                                if (!forIndexing) {
                                    functionDef = ErlangIndexProvider.getDefault().getFunction(remoteName.getIdentifier(), funName.getIdentifier(), arityInt);
                                    if (functionDef != null) {
                                        erlRoot.getRootContext().addDefinition(functionDef);
                                        currentContext.addUsage(funName, functionDef);
                                    }
                                }
                            } else {
                                currentContext.addUsage(funName, functionDef);
                            }
                        } else if (remoteName != null) {
                            funName = remoteName;
                            ErlFunction functionDef = erlRoot.getRootContext().getFunctionInScope(funName.getIdentifier(), arityInt);
                            if (functionDef == null) {
                                /** this is a built-in function call? */
                                functionDef = ErlBuiltIn.getBuiltInFunction(funName.getIdentifier(), arityInt);
                                if (functionDef != null) {
                                    erlRoot.getRootContext().addDefinition(functionDef);
                                    currentContext.addUsage(funName, functionDef);
                                }
                            } else {
                                currentContext.addUsage(funName, functionDef);
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
                    currentContext.addContext(listCompContext);

                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "ListComprehensionExpr")) {
                            int i = 0;
                            for (ASTItem child1 : child.getChildren()) {
                                if (isNode(child1, "Expr")) {
                                    boolean containsVarDecls1 = i == 0;
                                    processAnyExpr(erlRoot, child1, listCompContext, containsVarDecls1);
                                    i++;
                                }
                            }
                        }
                    }
                } else if (isNode(item, "ListTail")) {
                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "Expr")) {
                            processAnyExpr(erlRoot, child, currentContext, containsVarDef);
                        }
                    }
                }
            }

            /* we'll process expr at last, since we should get listCompContext first if Applicable */
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "Expr") || isNode(item, "BinElement")) {
                    if (listCompContext != null) {
                        /** List Comprehension */
                        processAnyExpr(erlRoot, item, listCompContext, false);
                    } else {
                        /** List */
                        processAnyExpr(erlRoot, item, currentContext, containsVarDef);
                    }
                }
            }
        } else if (isNode(expr, "TryExpr")) {
            ErlContext tryContext = new ErlContext(expr.getOffset(), expr.getEndOffset());
            currentContext.addContext(tryContext);

            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "Exprs")) {
                    processExprs(erlRoot, item, tryContext);
                } else if (isNode(item, "CrClauses")) {
                    processCrClauses(erlRoot, item, tryContext);
                } else if (isNode(item, "TryCatch")) {
                    for (ASTItem child : item.getChildren()) {
                        if (isNode(child, "TryClauses")) {
                            for (ASTItem child1 : child.getChildren()) {
                                if (isNode(child1, "TryClause")) {
                                    ErlContext tryClauseContext = new ErlContext(child1.getOffset(), child1.getEndOffset());
                                    tryContext.addContext(tryClauseContext);

                                    for (ASTItem child2 : child1.getChildren()) {
                                        if (isNode(child2, "Expr")) {
                                            processAnyExpr(erlRoot, child2, tryClauseContext, true);
                                        } else if (isNode(child2, "ClauseGuard")) {
                                            processClauseGuard(erlRoot, child2, tryClauseContext);
                                        } else if (isNode(child2, "ClauseBody")) {
                                            processClauseBody(erlRoot, child2, tryClauseContext);
                                        }
                                    }
                                }
                            }
                        } else if (isNode(child, "Exprs")) {
                            processExprs(erlRoot, child, tryContext);
                        }
                    }
                }
            }
        } else if (isNode(expr, "Macro")) {
            for (ASTItem item : expr.getChildren()) {
                if (isTokenTypeName(item, "var") || isTokenTypeName(item, "atom")) {
                    ASTToken macroName = (ASTToken) item;
                    String macroNameStr = macroName.getIdentifier();
                    ErlMacro macroDef = currentContext.getMacroInScope(macroNameStr);
                    if (macroDef == null) {
                        macroDef = ErlMacro.getPreDefined(macroNameStr);
                        if (macroDef != null) {
                            erlRoot.getRootContext().addDefinition(macroDef);
                            currentContext.addUsage(macroName, macroDef);
                        }
                    } else {
                        currentContext.addUsage(macroName, macroDef);
                    }
                }
            }
        } else {
            for (ASTItem item : expr.getChildren()) {
                if (isTokenTypeName(item, "var")) {
                    ASTToken var = (ASTToken) item;
                    if (!(var.getIdentifier().equals("_"))) {
                        ErlVariable variableDef = currentContext.getVariableInScope(var.getIdentifier());
                        if (variableDef == null) {
                            if (containsVarDef) {
                                variableDef = new ErlVariable(var.getIdentifier(), expr.getOffset(), expr.getEndOffset(), ErlVariable.Scope.LOCAL);
                                currentContext.addDefinition(variableDef);
                                currentContext.addUsage(var, variableDef);
                            }
                        } else {
                            currentContext.addUsage(var, variableDef);
                        }
                    }
                } else if (item instanceof ASTNode) {
                    processAnyExpr(erlRoot, item, currentContext, containsVarDef);
                }
            }
        }
    }

    private void processFunctionCallExpr(ErlRoot erlRoot, ASTItem functionCallExpr, ErlContext currentContext) {
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
                        ErlFunction functionDef = erlRoot.getRootContext().getFunctionInScope(remoteName.getIdentifier(), functionCallName.getIdentifier(), arityInt);
                        if (functionDef == null) {
                            if (!forIndexing) {
                                functionDef = ErlangIndexProvider.getDefault().getFunction(remoteName.getIdentifier(), functionCallName.getIdentifier(), arityInt);
                                if (functionDef != null) {
                                    erlRoot.getRootContext().addDefinition(functionDef);
                                    currentContext.addUsage(functionCallName, functionDef);
                                }
                            }
                        } else {
                            currentContext.addUsage(functionCallName, functionDef);
                        }
                    }
                } else {
                    ASTItem callPrimaryExpr = prefixPrimaryExpr;

                    ASTToken functionCallName = getAtomTokenFromPrimaryExpr(callPrimaryExpr);
                    if (functionCallName != null) {
                        ErlFunction functionDef = erlRoot.getRootContext().getFunctionInScope(functionCallName.getIdentifier(), arityInt);
                        if (functionDef == null) {
                            /** Is it a built-in function call? */
                            functionDef = ErlBuiltIn.getBuiltInFunction(functionCallName.getIdentifier(), arityInt);
                            if (functionDef != null) {
                                erlRoot.getRootContext().addDefinition(functionDef);
                                currentContext.addUsage(functionCallName, functionDef);
                            }
                        } else {
                            currentContext.addUsage(functionCallName, functionDef);
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
}
