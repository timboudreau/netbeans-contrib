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
package org.netbeans.modules.scala.editing.semantic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.netbeans.modules.scala.editing.semantic.Template.Kind;
import org.netbeans.modules.scala.editing.spi.ScalaIndexProvider;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaSemanticAnalyser {

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
    private static Map<Document, ScalaSemanticAnalyser> docToAnalyser = new WeakHashMap<Document, ScalaSemanticAnalyser>();
    private static ScalaSemanticAnalyser ANALYSER_FOR_INDEXING = new ScalaSemanticAnalyser(true);
    private Document doc;
    private ASTNode astRoot;
    private ScalaContext rootCtx;
    /**
     * @NOTICE we should avoid re-entrant of analyse. There is a case may cause that:
     * When document is modified, it will be setDirty in PersistentClassIndex, and request
     * a parseFiles task for updateIndex, thus, a bad cycle calling:
     * parseFiles -> semanticParser.analyse -> ScalaIndexProvider.getDefault().getFunction ->
     * index.gsfSearch -> index.updateDirty -> parseFiles -> semanticParser.analyse.
     *
     * Here's a ugly hacking, we just setForIndexing to avoid this re-entrant
     */
    private boolean forIndexing;
    private ParserManager parserManager;
    private ParserManagerListener parserManagerListener;
    private Map<ASTItem, String> astItemToType = new HashMap<ASTItem, String>();

    private ScalaSemanticAnalyser(Document doc) {
        this.doc = doc;
        //initParserManagerListener
    }
    
    private ScalaSemanticAnalyser(boolean forIndexing) {
        this.doc = null;
        this.forIndexing = forIndexing;
    }

    @Deprecated
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

    public ScalaContext getRootContext() {
        return rootCtx;
    }

//    public State getState() {
//        assert parserManager != null;
//        return parserManager.getState();
//    }

    public static ScalaSemanticAnalyser getAnalyserForIndexing() {
        return ANALYSER_FOR_INDEXING;
    }

    public static ScalaSemanticAnalyser getAnalyser(FileObject fo) {
        for (Document doc : docToAnalyser.keySet()) {
            DataObject dobj = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
            if (dobj != null && dobj.getPrimaryFile() == fo) {
                return docToAnalyser.get(doc);
            }
        }

        return null;
    }

    public static ScalaSemanticAnalyser getAnalyser(final Document doc) {
        if (doc == null) {
            return ANALYSER_FOR_INDEXING;
        }

        ScalaSemanticAnalyser analyser = docToAnalyser.get(doc);
        if (analyser == null) {
            analyser = new ScalaSemanticAnalyser(doc);
            docToAnalyser.put(doc, analyser);
        }

        return analyser;
    }

    /**
     * This is the method will be called by GLF feature as declared in Erlang.nbs:
     * AST {
     *   process:org.netbeans.modules.scala.editing.semantic.ScalaSemanticAnalyser.process
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
        ScalaSemanticAnalyser analyser = getAnalyser(doc);
        analyser.analyse(astRoot);

    /** this feature call may be void return or ASTNode return, if later,
     * Language engine will accept the ASTNode, otherwise, should keep the
     * original astRoot that was passed in via syntaxtContext
     */
    }

    public static ScalaContext getRootContext(Document doc, ASTNode astRoot) {
        ScalaSemanticAnalyser analyser = getAnalyser(doc);
        ScalaContext ctxRoot = analyser.getRootContext();
        if (ctxRoot != null) {
            /**
             * although we have a syntax parser listener which will redo semantic
             * parser when new syntax happened, but SemanticHilightingsLayer may
             * act before our listener, so, just check if we need do here:
             *
             * check if the AstRoot has been re-parsed, if so, we should
             * remove oldAstRoot and redo semantic parsing
             */
            if (analyser.getAstRoot() != astRoot) {
                ctxRoot = analyser.analyse(astRoot);
            }
        } else {
            ctxRoot = analyser.analyse(astRoot);
        }

        return ctxRoot;
    }

    public static ASTNode getAstRoot(Document doc) {
        return getAnalyser(doc).astRoot;
    }

    public static ScalaContext getCurrentRootCtx(Document doc) {
        return getAnalyser(doc).rootCtx;
    }

    public static Map<ASTItem, String> getTypeMap(Document doc) {
        return getAnalyser(doc).astItemToType;
    }

    /**
     * @NOTICE we should avoid re-entrant of analyse. There is a case may cause that:
     * When document is modified, it will be setDirty in PersistentClassIndex, and request
     * a parseFiles task for updateIndex, thus, a bad cycle calling:
     * parseFiles -> semanticParser.analyse -> getDeclaration ->
     * index.gsfSearch -> index.updateDirty -> parseFiles -> semanticParser.analyse.
     */
    public ScalaContext analyse(ASTNode astRoot) {
        if (this.astRoot != astRoot) {
            this.astRoot = astRoot;
            ScalaContext rootCtxInParsing = new ScalaContext(astRoot.getOffset(), astRoot.getEndOffset());
            process(rootCtxInParsing, astRoot, rootCtxInParsing);
            rootCtx = rootCtxInParsing;
            DatabaseManager.setRoot(astRoot, rootCtx);
            return rootCtx;
        } else {
            return rootCtx;
        }
    }

    private Packaging currPackage = null;
    private void process(ScalaContext rootCtx, ASTItem n, ScalaContext currCtx) {
        if (isNode(n, "TopStats")) {
            /**
             * pre-process
             * We should precess function declaration and imported functions first
             * so all function calls can refer to them.
             */
            Collection<ASTItem> postProcessTopStats = new ArrayList<ASTItem>();
            for (ASTItem item : n.getChildren()) {
                if (isNode(item, "TopStat")) {
                    for (ASTItem item1 : item.getChildren()) {
                        if (isNode(item1, "Packaging")) {
                            currPackage = processPackaging(rootCtx, item1);
                            currCtx.addDefinition(currPackage);
                            for (ASTItem item2 : item1.getChildren()) {
                                if (isNode(item2, "TopStats")) {
                                    ScalaContext ctx = new ScalaContext(item1.getOffset(), item1.getEndOffset());
                                    currCtx.addContext(ctx);
                                    process(rootCtx, item2, ctx);
                                }
                            }
                        } else if (isNode(item1, "Import")) {
                            processImportStat(rootCtx, item1, rootCtx);
                        } else if (isNode(item1, "TopTmplDef")) {

                        }
                    }
                    /** add all forms for post-precessing */
                    postProcessTopStats.add(item);
                }
            }

            /** post-process attributes, such as export */
            Collection<ASTItem> processedTopStats = new ArrayList<ASTItem>();
            for (ASTItem form : postProcessTopStats) {
                for (ASTItem item : form.getChildren()) {
                    if (isNode(item, "AttributeDeclaration")) {
                        postProcessAttibuteDeclaration(rootCtx, item);
                        processedTopStats.add(form);
                    }
                }
            }
            postProcessTopStats.removeAll(processedTopStats);

            /** normal process */
            for (ASTItem TopStat : postProcessTopStats) {
                process(rootCtx, TopStat, currCtx);
            }
        } else {
            for (ASTItem item : n.getChildren()) {
                if (isNode(item, "TopTmplDef")) {
                    Map<ASTItem, ScalaContext> pendingItems = new HashMap<ASTItem, ScalaContext>();
                    ScalaContext newCtx = new ScalaContext(item.getOffset(), item.getEndOffset());
                    currCtx.addContext(newCtx);
                    pendingItems.putAll(processDclDef(rootCtx, item, newCtx));
                    processPendingItems(rootCtx, pendingItems);
                } else if (item instanceof ASTNode) {
                    process(rootCtx, item, currCtx);
                }
            }
        }
    }

    /** process TemplateStats || BlockStats || CaseBlockStats */
    private void processBlockStates(ScalaContext rootCtx, ASTItem blockStats, ScalaContext currCtx) {
        Map<ASTItem, ScalaContext> pendingItems = new HashMap<ASTItem, ScalaContext>();
        ScalaContext newCtx = new ScalaContext(blockStats.getOffset(), blockStats.getEndOffset());
        currCtx.addContext(newCtx);
        for (ASTItem item1 : blockStats.getChildren()) {
            if (isNode(item1, "TemplateStat") || isNode(item1, "BlockStat") || isNode(item1, "CaseBlockStat")) {
                for (ASTItem item2 : item1.getChildren()) {
                    if (isNode(item2, "Import")) {
                        for (ASTItem item3 : item2.getChildren()) {
                            if (isNode(item3, "ImportStat")) {
                                processImportStat(rootCtx, item3, newCtx);
                            }
                        }
                    } else if (isNode(item2, "DclDefInTemplate") || isNode(item2, "DclDefInBlock") || isNode(item2, "DclDefInCaseBlock")) {
                        pendingItems.putAll(processDclDef(rootCtx, item2, newCtx));
                    } else if (isNode(item2, "ExprInTemplate") || isNode(item2, "ExprInBlock") || isNode(item2, "ExprInCaseBlock")) {
                        pendingItems.put(item2, newCtx);
                    }
                }
            }
        }
        processPendingItems(rootCtx, pendingItems);
    }

    private Map<ASTItem, ScalaContext> processDclDef(ScalaContext rootCtx, ASTItem dclDef, ScalaContext currCtx) {
        Map<ASTItem, ScalaContext> pendingItems = new HashMap<ASTItem, ScalaContext>();
        for (ASTItem item : dclDef.getChildren()) {
            if (isNode(item, "ValDclDef") || isNode(item, "VarDclDef")) {
                pendingItems.putAll(processVarValDclDef(rootCtx, item, currCtx));
            } else if (isNode(item, "FunDclDef")) {
                Function funDfn = null;
                for (ASTItem item1 : item.getChildren()) {
                    if (isNode(item1, "NameId")) {
                        ASTToken nameToken = getIdTokenFromNameId(item1);
                        funDfn = new Function(nameToken.getIdentifier(), nameToken.getOffset(), nameToken.getEndOffset(), 0);
                        currCtx.addDefinition(funDfn);
                        currCtx.addUsage(nameToken, funDfn);
                        break;
                    }
                }
                pendingItems.putAll(processFunDclDef(rootCtx, item, currCtx, funDfn));
            } else if (isNode(item, "TmplDef")) {
                ASTItem defStat = null;
                Kind kind = null;
                for (ASTItem item1 : item.getChildren()) {
                    if (isNode(item1, "ObjectDef")) {
                        defStat = item1;
                        kind = Kind.OBJECT;
                    } else if (isNode(item1, "ClassDef")) {
                        defStat = item1;
                        kind = Kind.CLASS;
                    } else if (isNode(item1, "TraitDef")) {
                        defStat = item1;
                        kind = Kind.TRAIT;
                    }
                }

                Template tmplDfn = null;
                for (ASTItem item1 : defStat.getChildren()) {
                    if (isNode(item1, "NameId")) {
                        ASTToken nameToken = getIdTokenFromNameId(item1);
                        tmplDfn = new Template(nameToken.getIdentifier(), nameToken.getOffset(), nameToken.getEndOffset(), kind, currPackage);                        
                        currCtx.addDefinition(tmplDfn);
                        currCtx.addUsage(nameToken, tmplDfn);
                        break;
                    }
                }
                pendingItems.putAll(processTmplDef(rootCtx, defStat, currCtx, tmplDfn));
            } else if (isNode(item, "TypeDclDef")) {
                Type typeDfn = null;
                for (ASTItem item1 : item.getChildren()) {
                    if (isNode(item1, "NameId")) {
                        ASTToken nameToken = getIdTokenFromNameId(item1);
                        typeDfn = new Type(nameToken.getIdentifier(), nameToken.getOffset(), nameToken.getEndOffset());
                        currCtx.addDefinition(typeDfn);
                        currCtx.addUsage(nameToken, typeDfn);
                        break;
                    }
                }
                pendingItems.putAll(processTypeDclDef(rootCtx, item, currCtx, typeDfn));
            }
        }
        return pendingItems;
    }

    private Map<ASTItem, ScalaContext> processTmplDef(ScalaContext rootCtx, ASTItem defStat, ScalaContext currCtx, Template tmplDfn) {
        Map<ASTItem, ScalaContext> pendingItems = new HashMap<ASTItem, ScalaContext>();
        ScalaContext newCtx = new ScalaContext(defStat.getOffset(), defStat.getEndOffset());
        currCtx.addContext(newCtx);

        Kind kind = tmplDfn.getKind();
        if (kind == Kind.OBJECT || kind == Kind.CLASS) {
            for (ASTItem item1 : defStat.getChildren()) {
                if (isNode(item1, "ClassTemplateOpt")) {
                    for (ASTItem item2 : item1.getChildren()) {
                        if (isNode(item2, "Template")) {
                            pendingItems.put(item2, newCtx);
                        } else if (isNode(item2, "ClassParents")) {
                            for (ASTItem item3 : item2.getChildren()) {
                                if (isNode(item3, "AnnotType")) {
                                    processAnyType(rootCtx, item3, newCtx);
                                }
                            }
                        }
                    }
                } else if (isNode(item1, "ClassParamClauses")) {
                    List<ASTItem> nameIds = query(item1, "ClassParams/ClassParam/NameId");
                    for (ASTItem nameId : nameIds) {
                        ASTToken varToken = getIdTokenFromNameId(nameId);
                        Var varDfn = new Var(varToken.getIdentifier(), varToken.getOffset(), varToken.getEndOffset(), Var.Scope.PARAMETER);
                        newCtx.addDefinition(varDfn);
                        newCtx.addUsage(varToken, varDfn);
                    }
                    List<ASTItem> types = query(item1, "ClassParams/ClassParam/ParamType/Type");
                    for (ASTItem type : types) {
                        processAnyType(rootCtx, type, currCtx);
                    }
                }
            }
        } else {
            // Trait
            for (ASTItem item1 : defStat.getChildren()) {
                if (isNode(item1, "TraitTemplateOpt")) {
                    for (ASTItem item2 : item1.getChildren()) {
                        if (isNode(item2, "Template")) {
                            pendingItems.put(item2, newCtx);
                        } else if (isNode(item2, "TraitParents")) {
                            for (ASTItem item3 : item2.getChildren()) {
                                if (isNode(item3, "AnnotType")) {
                                    processAnyType(rootCtx, item3, newCtx);
                                }
                            }
                        }
                    }
                }
            }

        }


        return pendingItems;
    }

    private Map<ASTItem, ScalaContext> processVarValDclDef(ScalaContext rootCtx, ASTItem nodeContainsDclDef, ScalaContext currCtx) {
        Map<ASTItem, ScalaContext> pendingItems = new HashMap<ASTItem, ScalaContext>();
        List<ASTItem> patterns = new ArrayList<ASTItem>();
        if (isNode(nodeContainsDclDef, "ValDclDef") || isNode(nodeContainsDclDef, "VarDclDef")) {
            for (ASTItem item1 : nodeContainsDclDef.getChildren()) {
                if (isNode(item1, "PatDef")) {
                    for (ASTItem item2 : item1.getChildren()) {
                        if (isNode(item2, "Pattern2")) {
                            patterns.add(item2);
                        }
                    }

                } else {
                    pendingItems.put(item1, currCtx);
                }
            }
        } else if (isNode(nodeContainsDclDef, "Generator") || isNode(nodeContainsDclDef, "ValDefInEnumerator")) {
            for (ASTItem item : nodeContainsDclDef.getChildren()) {
                if (isNode(item, "Pattern1")) {
                    for (ASTItem item1 : item.getChildren()) {
                        if (isNode(item1, "Pattern3")) {
                            for (ASTItem item2 : item1.getChildren()) {
                                if (isNode(item2, "SimplePattern")) {
                                    patterns.add(item2);
                                }
                            }
                        }
                    }
                } else {
                    pendingItems.put(item, currCtx);
                }
            }
        } else if (isNode(nodeContainsDclDef, "CasePattern0")) {
            patterns.add(nodeContainsDclDef);
        }

        for (ASTItem pettern : patterns) {
            for (ASTItem item : pettern.getChildren()) {
                if (isNode(item, "StableId")) {
                    ASTToken varToken = getLeafId(item);
                    if (varToken != null) {
                        String nameStr = varToken.getIdentifier();
                        if (!nameStr.equals("_")) {
                            Var varDfn = currCtx.getVariableInScope(nameStr);
                            if (varDfn == null) {
                                varDfn = new Var(nameStr, varToken.getOffset(), varToken.getEndOffset(), Var.Scope.PARAMETER);
                                currCtx.addDefinition(varDfn);
                                currCtx.addUsage(varToken, varDfn);
                            } else {
                            // error
                            }
                        }
                    }
                } else if (isNode(item, "TuplePattern")) {
                    List<ASTItem> stableIds = query(item, "Patterns/Pattern/Pattern1/Pattern3/SimplePattern/StableId");
                    for (ASTItem stableId : stableIds) {
                        ASTToken varToken = getLeafId(stableId);
                        if (varToken != null) {
                            String nameStr = varToken.getIdentifier();
                            if (!nameStr.equals("_")) {
                                Var varDfn = currCtx.getVariableInScope(nameStr);
                                if (varDfn == null) {
                                    varDfn = new Var(nameStr, varToken.getOffset(), varToken.getEndOffset(), Var.Scope.PARAMETER);
                                    currCtx.addDefinition(varDfn);
                                    currCtx.addUsage(varToken, varDfn);
                                } else {
                                // error
                                }
                            }
                        }
                    }
                } else if (isNode(item, "Patterns")) {
                    List<ASTItem> stableIds = query(item, "Pattern/Pattern1/Pattern3/SimplePattern/StableId");
                    for (ASTItem stableId : stableIds) {
                        ASTToken varToken = getLeafId(stableId);
                        if (varToken != null) {
                            String nameStr = varToken.getIdentifier();
                            if (!nameStr.equals("_")) {
                                Var varDfn = currCtx.getVariableInScope(nameStr);
                                if (varDfn == null) {
                                    varDfn = new Var(nameStr, varToken.getOffset(), varToken.getEndOffset(), Var.Scope.PARAMETER);
                                    currCtx.addDefinition(varDfn);
                                    currCtx.addUsage(varToken, varDfn);
                                } else {
                                // error
                                }
                            }
                        }
                    }
                }
            }
        }

        return pendingItems;
    }

    private Map<ASTItem, ScalaContext> processFunDclDef(ScalaContext rootCtx, ASTItem funDclDef, ScalaContext currCtx, Function funDfn) {
        Map<ASTItem, ScalaContext> pendingItems = new HashMap<ASTItem, ScalaContext>();
        ScalaContext newCtx = new ScalaContext(funDclDef.getOffset(), funDclDef.getEndOffset());
        currCtx.addContext(newCtx);
        for (ASTItem item : funDclDef.getChildren()) {
            if (isNode(item, "FunTypeParamClause")) {
                List<ASTItem> typeParams = query(item, "TypeParam");
                for (ASTItem typeParam : typeParams) {
                    for (ASTItem item1 : typeParam.getChildren()) {
                        if (isNode(item1, "NameId")) {
                            ASTToken nameToken = getIdTokenFromNameId(item1);
                            Type typeDfn = new Type(nameToken.getIdentifier(), nameToken.getOffset(), nameToken.getEndOffset());
                            newCtx.addDefinition(typeDfn);
                            newCtx.addUsage(nameToken, typeDfn);
                        } else if (isNode(item1, "Type")) {
                            processAnyType(rootCtx, item1, newCtx);
                        } else if (isNode(item1, "TypeParamClause")) {
                        /** @todo */
                        }
                    }
                }
            } else if (isNode(item, "ParamClauses")) {
                List<ASTItem> nameIds = query(item, "ParamClause/Params/Param/NameId");
                for (ASTItem nameId : nameIds) {
                    ASTToken varToken = getIdTokenFromNameId(nameId);
                    Var varDfn = new Var(varToken.getIdentifier(), varToken.getOffset(), varToken.getEndOffset(), Var.Scope.PARAMETER);
                    newCtx.addDefinition(varDfn);
                    newCtx.addUsage(varToken, varDfn);
                }
                List<ASTItem> types = query(item, "ParamClause/Params/Param/ParamType");
                for (ASTItem type : types) {
                    processAnyType(rootCtx, type, newCtx);
                }
            } else if (isNode(item, "Type")) {
                processAnyType(rootCtx, item, newCtx);
            } else {
                pendingItems.put(item, newCtx);
            }
        }
        return pendingItems;
    }

    private Map<ASTItem, ScalaContext> processTypeDclDef(ScalaContext rootCtx, ASTItem typeDclDef, ScalaContext currCtx, Type typeDfn) {
        Map<ASTItem, ScalaContext> pendingItems = new HashMap<ASTItem, ScalaContext>();
        for (ASTItem item : typeDclDef.getChildren()) {
            if (isNode(item, "TypeParamClause")) {
                List<ASTItem> nameIds = query(item, "VariantTypeParam/TypeParam/NameId");
                for (ASTItem nameId : nameIds) {
                    ASTToken nameToken = getIdTokenFromNameId(nameId);
                /** @todo */
//                    Var varDfn = new Var(nameToken.getIdentifier(), nameToken.getOffset(), nameToken.getEndOffset(), Var.Scope.PARAMETER);
//                    ctx.addDefinition(varDfn);
//                    ctx.addUsage(nameToken, varDfn);
                }
            } else if (isNode(item, "Type")) {
                processAnyType(rootCtx, item, currCtx);
            } else {
                pendingItems.put(item, currCtx);
            }
        }
        return pendingItems;
    }

    private void processAnyType(ScalaContext rootCtx, ASTItem type, ScalaContext currCtx) {
        for (ASTItem item : type.getChildren()) {
            if (isNode(item, "TypeStableId")) {
                List<ASTItem> nameIds = query(item, "TypeId/PathId/NameId");
                if (nameIds.size() > 0) {
                    // @todo should process package here
                    ASTItem lastNameId = nameIds.get(nameIds.size() - 1);
                    ASTToken idToken = getIdTokenFromNameId(lastNameId);
                    Type typeDfn = currCtx.getDefinitionInScopeByName(Type.class, idToken.getIdentifier());
                    if (typeDfn != null) {
                        currCtx.addUsage(idToken, typeDfn);
                    } else {
                        Template tmplDfn = currCtx.getDefinitionInScopeByName(Template.class, idToken.getIdentifier());
                        if (tmplDfn != null) {
                            currCtx.addUsage(idToken, tmplDfn);
                        }
                    }
                }
            } else if (item instanceof ASTNode) {
                processAnyType(rootCtx, item, currCtx);
            }
        }
    }

    private Packaging processPackaging(ScalaContext ctxRoot, ASTItem packaging) {
        Packaging packageDfn = new Packaging("Packaging", packaging.getOffset(), packaging.getEndOffset());
        List<ASTItem> paths = query(packaging, "QualId/NameId");
        for (ASTItem path : paths) {
            String pathStr = getIdTokenFromNameId(path).getIdentifier();
            packageDfn.addPath(pathStr);        
        }
        return packageDfn;
    }

    private void postProcessAttibuteDeclaration(ScalaContext ctxRoot, ASTItem attribute) {
        ASTItem attributeName = attribute.getChildren().get(0);
        if (attributeName != null) {
            if (isNode(attributeName, "ExportAttribute")) {
                processExportAttribute(ctxRoot, attribute);
            }
        }
    }

    private void processModuleAttribute(ScalaContext rootCtx, ASTItem attribute) {
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
        Packaging moduleDef = new Packaging(nameStr.toString(), attribute.getOffset(), attribute.getEndOffset());
        for (String packageName : packages) {
            moduleDef.addPath(packageName);
        }
        rootCtx.addDefinition(moduleDef);
    }

    private void processExportAttribute(ScalaContext rootCtx, ASTItem attribute) {
        for (ASTItem item : attribute.getChildren()) {
            if (isNode(item, "FunctionNames")) {
                ErlExport exportDef = new ErlExport(attribute.getOffset(), attribute.getEndOffset());
                rootCtx.addDefinition(exportDef);
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
                            Function functionDef = rootCtx.getFunctionInScope(name.getIdentifier(), arityInt);
                            if (functionDef != null) {
                                exportDef.addFunction(functionDef);
                                rootCtx.addUsage(name, functionDef);
                            } else {
                                /** don't know offset of this function, just add it as 0 offset */
                                exportDef.addFunction(new Function(name.getIdentifier(), arityInt));
                            }
                        }
                    }
                }
            }
        }
    }

    private void processImportStat(ScalaContext rootCtx, ASTItem importStat, ScalaContext currCtx) {
        for (ASTItem item : importStat.getChildren()) {
            if (isNode(item, "FunctionNames")) {
                Import importDfn = new Import(importStat.getOffset(), importStat.getEndOffset());
                currCtx.addDefinition(importDfn);
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
                            rootCtx.addDefinition(new Function(nameStr, functionName.getOffset(), functionName.getEndOffset(), arityInt));
                        }
                    }
                }
            }
        }
    }

    private void processIncludeAttribute(ScalaContext rootCtx, ASTItem attribute) {
        ErlInclude includeDef = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isTokenTypeName(item, "string")) {
                includeDef = new ErlInclude(attribute.getOffset(), attribute.getEndOffset());
                rootCtx.addDefinition(includeDef);
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
                rootCtx.addUsage(path, includeDef);
            }
        }
    }

    private void processIncludeLibAttribute(ScalaContext rootCtx, ASTItem attribute) {
        ErlInclude includeDef = null;
        for (ASTItem item : attribute.getChildren()) {
            if (isTokenTypeName(item, "string")) {
                includeDef = new ErlInclude(attribute.getOffset(), attribute.getEndOffset());
                includeDef.setLib(true);
                rootCtx.addDefinition(includeDef);
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
                rootCtx.addUsage(path, includeDef);
            }
        }
    }

    private void processGuard(ScalaContext rootCtx, ASTItem guard, ScalaContext currentContext) {
        for (ASTItem child : guard.getChildren()) {
            if (isNode(child, "Exprs")) {
                processExprs(rootCtx, child, currentContext);
            }
        }
    }

    private void processClauseGuard(ScalaContext rootCtx, ASTItem clauseGuard, ScalaContext currentContext) {
        for (ASTItem child : clauseGuard.getChildren()) {
            if (isNode(child, "Guard")) {
                processGuard(rootCtx, child, currentContext);
            }
        }
    }

    private void processClauseBody(ScalaContext rootCtx, ASTItem clauseBody, ScalaContext currentContext) {
        for (ASTItem child : clauseBody.getChildren()) {
            if (isNode(child, "Exprs")) {
                processExprs(rootCtx, child, currentContext);
            }
        }
    }

    private void processExprs(ScalaContext rootCtx, ASTItem exprs, ScalaContext currentContext) {
        for (ASTItem child : exprs.getChildren()) {
            if (isNode(child, "Expr")) {
                processAnyExpr(rootCtx, child, currentContext, false);
            }
        }
    }

    private void processAnyExpr(ScalaContext rootCtx, ASTItem expr, ScalaContext currCtx, boolean containsVarDef) {
        if (isNode(expr, "TemplateStats") || isNode(expr, "BlockStats") || isNode(expr, "CaseBlockStats")) {
            processBlockStates(rootCtx, expr, currCtx);
        } else if (isNode(expr, "SimpleExpr")) {
            processSimpleExpr(rootCtx, expr, currCtx);
        } else if (isNode(expr, "Expr") || isNode(expr, "ExprInParen") || isNode(expr, "ExprInTemplate") || isNode(expr, "ExprInBlock") || isNode(expr, "ExprInCaseBlock")) {
            ASTItem postfixExpr = null;
            ASTItem funExprTail = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "PostfixExpr")) {
                    postfixExpr = item;
                } else if (isNode(item, "FunExprTail") || isNode(item, "FunExprTailInTemplate") || isNode(item, "FunExprTailInBlock") || isNode(item, "FunExprTailInCaseBlock")) {
                    funExprTail = item;
                }
            }

            if (postfixExpr != null && funExprTail != null) {
                // This is an anonymous function expr, the postfixExpt should be only in form of:
                // (Bindings | id)
                ScalaContext newCtx = new ScalaContext(expr.getOffset(), expr.getEndOffset());
                currCtx.addContext(newCtx);
                List<ASTItem> nameIds = query(postfixExpr, "PrefixExpr/SimpleExpr/TypedPathId/PathId/NameId");
                if (nameIds.size() > 0) {
                    ASTToken varToken = getIdTokenFromNameId(nameIds.get(0));
                    Var varDfn = new Var(varToken.getIdentifier(), varToken.getOffset(), varToken.getEndOffset(), Var.Scope.LOCAL);
                    newCtx.addDefinition(varDfn);
                    newCtx.addUsage(varToken, varDfn);
                } else {
                    // Bindings = "(" Binding ("," Binding)* ")"; Binding = NameId [":" Type]
                    nameIds = query(postfixExpr, "PrefixExpr/SimpleExpr/TypedParenExpr/ParenExpr/ExprInParen/PostfixExpr/PrefixExpr/SimpleExpr/TypedPathId/PathId/NameId");
                    for (ASTItem nameId : nameIds) {
                        ASTToken varToken = getIdTokenFromNameId(nameId);
                        Var varDfn = new Var(varToken.getIdentifier(), varToken.getOffset(), varToken.getEndOffset(), Var.Scope.LOCAL);
                        newCtx.addDefinition(varDfn);
                        newCtx.addUsage(varToken, varDfn);
                    }
                }
                processAnyExpr(rootCtx, funExprTail, newCtx, false);
            } else {
                // re-process all children exprs:
                for (ASTItem item : expr.getChildren()) {
                    processAnyExpr(rootCtx, item, currCtx, false);
                }
            }
        } else if (isNode(expr, "ValDclDef") || isNode(expr, "VarDclDef")) {
            Map<ASTItem, ScalaContext> pendingItems = processVarValDclDef(rootCtx, expr, currCtx);
            processPendingItems(rootCtx, pendingItems);
        } else if (isNode(expr, "ForExpr")) {
            Map<ASTItem, ScalaContext> pendingItems = new HashMap<ASTItem, ScalaContext>();
            ScalaContext ctx = new ScalaContext(expr.getOffset(), expr.getEndOffset());
            currCtx.addContext(ctx);
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "Enumerators")) {
                    for (ASTItem item1 : item.getChildren()) {
                        if (isNode(item1, "Generator")) {
                            pendingItems.putAll(processVarValDclDef(rootCtx, item1, ctx));
                        } else if (isNode(item1, "Enumerator")) {
                            for (ASTItem item2 : item1.getChildren()) {
                                if (isNode(item2, "Generator")) {
                                    pendingItems.putAll(processVarValDclDef(rootCtx, item2, ctx));
                                } else if (isNode(item2, "ValDefInEnumerator")) {
                                    pendingItems.putAll(processVarValDclDef(rootCtx, item2, ctx));
                                } else {
                                    pendingItems.put(item2, ctx);
                                }
                            }
                        } else {
                            pendingItems.put(item1, ctx);
                        }
                    }
                } else {
                    pendingItems.put(item, ctx);
                }
                processPendingItems(rootCtx, pendingItems);
            }
        } else if (isNode(expr, "CaseClause")) {
            ScalaContext newCtx = null;
            ASTItem casePattern = null;
            ASTItem guard = null;
            ASTItem caseBlockStats = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "CasePattern")) {
                    casePattern = item;
                } else if (isNode(item, "Guard")) {
                    guard = item;
                } else if (isNode(item, "CaseBlockStats")) {
                    caseBlockStats = item;
                }
            }
            if (casePattern != null && caseBlockStats != null) {
                newCtx = new ScalaContext(expr.getOffset(), expr.getEndOffset());
                currCtx.addContext(newCtx);
                List<ASTItem> casePattern0s = query(casePattern, "CasePattern1/CasePattern0");
                for (ASTItem casePattern0 : casePattern0s) {
                    processVarValDclDef(rootCtx, casePattern0, newCtx);
                }
                if (guard != null) {
                    processAnyExpr(rootCtx, guard, newCtx, false);
                }
                processAnyExpr(rootCtx, caseBlockStats, newCtx, false);
            }
        } else if (isNode(expr, "MatchSendExpr")) {
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
                processAnyExpr(rootCtx, leftExpr, currCtx, containsVarDef);
            }
            /** right hand */
            if (rightExpr != null) {
                if (isMatchExpr) {
                    /** This is in a match expr, and right hand, new scoped Var declaration may also occurs */
                    containsVarDef = true;
                }
                processAnyExpr(rootCtx, rightExpr, currCtx, containsVarDef);
            }
        } else if (isNode(expr, "IfExpr")) {
            int exprCount = 0;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "Expr")) {
                    exprCount++; // count the postion of Expr for future usage
                    processAnyExpr(rootCtx, item, currCtx, false);
                }
            }
        } else if (isNode(expr, "FunExpr")) {
            boolean remoteFun = false;
            ASTToken remoteName = null;
            ASTToken funName = null;
            ASTToken arity = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "FunClauses")) {
                    ScalaContext funContext = new ScalaContext(expr.getOffset(), expr.getEndOffset());
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
                            Function functionDef = rootCtx.getFunctionInScope(remoteName.getIdentifier(), funName.getIdentifier(), arityInt);
                            if (functionDef == null) {
                                if (!forIndexing) {
                                    functionDef = ScalaIndexProvider.getDefault().getFunction(remoteName.getIdentifier(), funName.getIdentifier(), arityInt);
                                    if (functionDef != null) {
                                        rootCtx.addDefinition(functionDef);
                                        rootCtx.addUsage(funName, functionDef);
                                    }
                                }
                            } else {
                                rootCtx.addUsage(funName, functionDef);
                            }
                        } else if (remoteName != null) {
                            funName = remoteName;
                            Function functionDef = rootCtx.getFunctionInScope(funName.getIdentifier(), arityInt);
                            if (functionDef == null) {
                                /** this is a built-in function call? */
                                functionDef = ErlBuiltIn.getBuiltInFunction(funName.getIdentifier(), arityInt);
                                if (functionDef != null) {
                                    rootCtx.addDefinition(functionDef);
                                    rootCtx.addUsage(funName, functionDef);
                                }
                            } else {
                                rootCtx.addUsage(funName, functionDef);
                            }
                        }
                    }
                }
            }
        } else if (isNode(expr, "TryExpr")) {
            ScalaContext newCtx = new ScalaContext(expr.getOffset(), expr.getEndOffset());
            currCtx.addContext(newCtx);

            ASTItem tryBlockStats = null;
            ASTItem catchCaseClauses = null;
            ASTItem finallyExpr = null;
            for (ASTItem item : expr.getChildren()) {
                if (isNode(item, "BlockStats")) {
                    tryBlockStats = item;
                } else if (isNode(item, "CaseClauses")) {
                    catchCaseClauses = item;
                } else if (isNode(item, "Expr")) {
                    finallyExpr = item;
                }
            }
            if (tryBlockStats != null) {
                processAnyExpr(rootCtx, tryBlockStats, newCtx, false);
            }
            if (catchCaseClauses != null) {
                processAnyExpr(rootCtx, catchCaseClauses, newCtx, false);
            }
            if (finallyExpr != null) {
                processAnyExpr(rootCtx, finallyExpr, newCtx, false);
            }
        } else if (isNode(expr, "Macro")) {
            for (ASTItem item : expr.getChildren()) {
                if (isTokenTypeName(item, "var") || isTokenTypeName(item, "atom")) {
                    ASTToken macroName = (ASTToken) item;
                    String macroNameStr = macroName.getIdentifier();
                    ErlMacro macroDef = currCtx.getMacroInScope(macroNameStr);
                    if (macroDef == null) {
                        macroDef = ErlMacro.getPreDefined(macroNameStr);
                        if (macroDef != null) {
                            rootCtx.addDefinition(macroDef);
                            rootCtx.addUsage(macroName, macroDef);
                        }
                    } else {
                        rootCtx.addUsage(macroName, macroDef);
                    }
                }
            }

        } else if (isNode(expr, "Literal")) {
            for (ASTItem item1 : expr.getChildren()) {
                if (isTokenTypeName(item1, "integer")) {
                    astItemToType.put(expr, "Integer");
                    astItemToType.put(item1, "Integer");
                } else if (isTokenTypeName(item1, "float")) {
                    astItemToType.put(expr, "Float");
                    astItemToType.put(item1, "Float");
                } else if (isTokenTypeName(item1, "char")) {
                    astItemToType.put(expr, "Char");
                    astItemToType.put(item1, "Char");
                } else if (isTokenTypeName(item1, "string")) {
                    astItemToType.put(expr, "String");
                    astItemToType.put(item1, "String");
                } else if (isTokenTypeName(item1, "true") || isTokenTypeName(item1, "false")) {
                    astItemToType.put(expr, "Boolean");
                    astItemToType.put(item1, "Boolean");
                } else if (isTokenTypeName(item1, "null")) {
                    astItemToType.put(expr, "Null");
                    astItemToType.put(item1, "Null");
                } else {

                }
            }
//        } else if (isNode(expr, "Type")) {
//            processAnyType(rootCtx, expr, currCtx);
        } else {
            for (ASTItem item : expr.getChildren()) {
                if (isTokenTypeName(item, "var_identifier") | isTokenTypeName(item, "upper_identifier")) {
                    ASTToken var = (ASTToken) item;
                    if (!(var.getIdentifier().equals("_"))) {
                        Var varDfn = currCtx.getVariableInScope(var.getIdentifier());
                        if (varDfn == null) {
                            if (containsVarDef) {
                                varDfn = new Var(var.getIdentifier(), expr.getOffset(), expr.getEndOffset(), Var.Scope.LOCAL);
                                currCtx.addDefinition(varDfn);
                                currCtx.addUsage(var, varDfn);
                            }
                        } else {
                            currCtx.addUsage(var, varDfn);
                        }
                    }
                } else if (item instanceof ASTNode) {
                    processAnyExpr(rootCtx, item, currCtx, containsVarDef);
                }
            }
        }
    }

    private void processSimpleExpr(ScalaContext rootCtx, ASTItem simpleExpr, ScalaContext currCtx) {
        boolean isFunCall = false;
        boolean isLocalCall = false;
        boolean isNewExpr = false;
        boolean isVar = false;
        List<ASTItem> typedPathIds = null;
        List<ASTItem> argsChain = null;
        List<ASTItem> children = simpleExpr.getChildren();
        List<ASTItem> pendingItems = new ArrayList<ASTItem>();
        for (ASTItem item : children) {
            if (isNode(item, "TypedPathId")) {
                if (typedPathIds == null) {
                    typedPathIds = new ArrayList<ASTItem>();
                }
                typedPathIds.add(item);
            } else if (isNode(item, "TypedArguments")) {
                isFunCall = true;
                if (argsChain == null) {
                    argsChain = new ArrayList<ASTItem>();
                }
                argsChain.add(item);
                pendingItems.add(item);
            } else if (isNode(item, "NewExpr")) {
                isNewExpr = true;
                pendingItems.add(item);
            } else {
                pendingItems.add(item);
            }
        }

        isLocalCall = isFunCall && typedPathIds != null && typedPathIds.size() > 0 && isNode(children.get(0), "TypedPathId");
        isVar = !isLocalCall && typedPathIds != null && typedPathIds.size() > 0 && isNode(children.get(0), "TypedPathId");
        if (isLocalCall) {
            int arityInt = 0;
            if (argsChain != null && argsChain.size() > 0) {
                // @todo infer arg type
                for (ASTItem child : argsChain.get(0).getChildren()) {
                    if (isNode(child, "Expr")) {
                        arityInt++;
                    }
                }
            }

            ASTItem typedPathId = typedPathIds.get(0);
            ASTItem nameId = null;
            for (ASTItem item : typedPathId.getChildren()) {
                if (isNode(item, "PathId")) {
                    for (ASTItem item1 : item.getChildren()) {
                        if (isNode(item1, "NameId")) {
                            nameId = item1;
                            break;
                        }
                    }
                    break;
                }
            }
            if (nameId == null) {
                return;
            } // @todo process this super ?
            ASTToken funName = getIdTokenFromNameId(nameId);
            if (funName != null) {
                /** @todo get all functions with the same name, then find the same Type params one */
                Function funDfn = currCtx.getDefinitionInScopeByName(Function.class, funName.getIdentifier());
                if (funDfn == null) {
                    /** Is it a imported function call? */
                    funDfn = ErlBuiltIn.getBuiltInFunction(funName.getIdentifier(), arityInt);
                    if (funDfn != null) {
                        currCtx.addDefinition(funDfn);
                        currCtx.addUsage(funName, funDfn);
                    } else {
                        // It may be a apply/update function of var?
                        isVar = true;
                    }
                } else {
                    currCtx.addUsage(funName, funDfn);
                }
            }

        }

        if (isVar) {
            ASTItem typedPathId = typedPathIds.get(0);
            ASTItem nameId = null;
            for (ASTItem item : typedPathId.getChildren()) {
                if (isNode(item, "PathId")) {
                    for (ASTItem item1 : item.getChildren()) {
                        if (isNode(item1, "NameId")) {
                            nameId = item1;
                            break;
                        }
                    }
                    break;
                }
            }
            if (nameId == null) {
                return;
            } // @todo process this super ?
            ASTToken varId = getIdTokenFromNameId(nameId);
            if (varId != null && !(varId.getIdentifier().equals("_"))) {
                Var varDfn = currCtx.getVariableInScope(varId.getIdentifier());
                if (varDfn != null) {
                    currCtx.addUsage(varId, varDfn);
                } else {
                    Template tmplDfn = currCtx.getDefinitionInScopeByName(Template.class, varId.getIdentifier());
                    if (tmplDfn != null && (tmplDfn.getKind() == Kind.OBJECT || tmplDfn.getKind() == Kind.CLASS)) {
                        currCtx.addUsage(varId, tmplDfn);
                    }
                }
            }
        } else {

        }

        if (isNewExpr) {
            List<String> namespace = new ArrayList<String>();
            List<ASTItem> typeIds = query(simpleExpr, "NewExpr/ClassParents/AnnotType/SimpleType/TypeStableId/TypeId/PathId");
            for (ASTItem typeId : typeIds) {
                for (ASTItem item : typeId.getChildren()) {
                    if (isNode(item, "NameId")) {
                        ASTToken tmplId = getIdTokenFromNameId(item);
                        namespace.add(tmplId.getIdentifier());
                        Template tmplDfn = currCtx.getDefinitionInScopeByName(Template.class, tmplId.getIdentifier());
                        if (tmplDfn != null && (tmplDfn.getKind() == Kind.CLASS || tmplDfn.getKind() == Kind.TRAIT)) {
                            currCtx.addUsage(tmplId, tmplDfn);
                        }
                    } else if (isTokenTypeName(item, "this")) {
                        namespace.add("this");
                    } else if (isTokenTypeName(item, "super")) {
                        namespace.add("super");
                    }
                }
            }
        }

        processPendingItems(rootCtx, pendingItems, currCtx);
    }

    /** helper ... */
    private void processPendingItems(ScalaContext rootCtx, List<ASTItem> items, ScalaContext ctx) {
        for (ASTItem item : items) {
            processAnyExpr(rootCtx, item, ctx, false);
        }
    }

    private void processPendingItems(ScalaContext rootCtx, Map<ASTItem, ScalaContext> items) {
        for (Entry<ASTItem, ScalaContext> item : items.entrySet()) {
            processAnyExpr(rootCtx, item.getKey(), item.getValue(), false);
        }
    }

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

    private static ASTToken getIdTokenFromNameId(ASTItem NameId) {
        for (ASTItem item1 : NameId.getChildren()) {
            if (isNode(item1, "PlainId")) {
                for (ASTItem item2 : item1.getChildren()) {
                    return (ASTToken) item2;
                }
            } else if (isToken(item1, "bquote_identifier")) {
                return (ASTToken) item1;

            }
        }
        return null;
    }

    private static ASTToken getLeafId(ASTItem stableId) {
        ASTItem id = null;
        /** get the latest PathId */
        for (ASTItem item : stableId.getChildren()) {
            if (isNode(item, "PathId")) {
                id = item;
            }
        }

        if (id != null) {
            for (ASTItem item : id.getChildren()) {
                if (isNode(item, "NameId")) {
                    return getIdTokenFromNameId(item);
                }
            }
        }

        if (id != null && id instanceof ASTToken) {
            return (ASTToken) id;
        } else {
            return null;
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

    //private static final String xpathRegrex = "((\\.)?(([a-z]|[A-Z])([a-z]|[A-Z]|[0-9])*(\\[([0-9]+)\\])?))+";
    //private static final Pattern xpathPattern = Pattern.compile(xpathRegrex);
    public static List<ASTItem> query(ASTItem fromItem, String relativePath) {
        List<String> pathNames = new ArrayList<String>();
        List<Integer> pathPositions = new ArrayList<Integer>();
        String[] elements = relativePath.split("/");
        for (String element : elements) {
            int pos1 = element.indexOf('[');
            int pos2 = element.indexOf(']');
            int pos = (pos1 > 0 && pos2 > 0) ? Integer.parseInt(element.substring(pos1 + 1, pos2)) : -1;
            pathNames.add(element);
            pathPositions.add(pos);
        }
        List<ASTItem> fromItems = new ArrayList<ASTItem>();
        fromItems.add(fromItem);
        return query(fromItems, 0, pathNames, pathPositions);
    }

    private static List<ASTItem> query(List<ASTItem> fromItems, int fromDepth, List<String> pathNames, List<Integer> pathPositions) {
        if (pathNames.size() == 0) {
            return Collections.<ASTItem>emptyList();
        }
        List<ASTItem> result = new ArrayList<ASTItem>();
        String wantedName = pathNames.get(fromDepth);
        int wantedPos = pathPositions.get(fromDepth);
        for (ASTItem fromItem : fromItems) {
            int pos = 0;
            for (ASTItem child : fromItem.getChildren()) {
                String name = child instanceof ASTToken ? ((ASTToken) child).getIdentifier() : ((ASTNode) child).getNT();
                if (name.equals(wantedName)) {
                    if (pos == wantedPos || wantedPos == -1) {
                        result.add(child);
                    } else {
                        pos++;
                    }
                }
            }
        }
        fromDepth++;
        if (fromDepth == pathNames.size()) { // reach leaf now            
            return result;
        } else {
            return query(result, fromDepth, pathNames, pathPositions);
        }
    }
}
