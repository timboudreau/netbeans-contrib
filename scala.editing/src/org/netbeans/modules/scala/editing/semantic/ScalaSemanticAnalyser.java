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

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.text.Document;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ParseException;
import org.netbeans.api.languages.ParserManager;
import org.netbeans.api.languages.ParserManager.State;
import org.netbeans.api.languages.ParserManagerListener;
import org.netbeans.api.languages.SyntaxContext;
import org.netbeans.api.languages.database.DatabaseManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

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
        initParserManagerListener();
    }

    private ScalaSemanticAnalyser(boolean forIndexing) {
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
     * @Deprecated 
     * Don't use AST feature to process semantic analysis, since AST feature will cause ContextASTEvalutor to set another 
     * rootContext, @see org.netbeans.modules.languages.features.ContextASTEvalutor#afterEvaluation
     * 
     * @Doc
     * This is the method will be called by GLF feature as declared in Erlang.nbs:
     * AST {
     *   process:org.netbeans.modules.scala.editing.semantic.ScalaSemanticAnalyser.process
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
        ScalaSemanticAnalyser analyser = getAnalyser(doc);
        analyser.analyse(astRoot);

    /** this feature call may be void return or ASTNode return, if later,
     * Language engine will accept the ASTNode, otherwise, should keep the
     * original astRoot that was passed in via syntaxtContext
     */
    }

    public static ScalaContext getRootContext(Document doc, ASTNode astRoot) {
        ScalaSemanticAnalyser analyser = getAnalyser(doc);
        ScalaContext rootCtx = analyser.getRootContext();
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

    public static ASTNode getAstRoot(Document doc) {
        return getAnalyser(doc).astRoot;
    }

    public static ScalaContext getCurrentRootCtx(Document doc) {
        ScalaSemanticAnalyser analyser = getAnalyser(doc);
        if (analyser.rootCtx == null) {
            ParserManager parserManager = ParserManager.get(doc);
            waitingForParsingFinished(parserManager);
            if (analyser.rootCtx == null) {
                try {
                    analyser.astRoot = parserManager.getAST();
                } catch (ParseException ex) {
                    Exceptions.printStackTrace(ex);
                }
                analyser.analyse(analyser.astRoot);
            }
        }

        return analyser.rootCtx;
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
            ScalaContext rootCtxInParsing = new ScalaContext(ScalaContext.ROOT, astRoot.getOffset(), astRoot.getEndOffset());
            new SyntaxVisitor(rootCtxInParsing).visit(astRoot);
            new DefinitionVisitor(rootCtxInParsing).visit(astRoot);
            new UsageVisitor(rootCtxInParsing).visit(astRoot);
            //process(rootCtxInParsing, astRoot, rootCtxInParsing);
            rootCtx = rootCtxInParsing;
            DatabaseManager.setRoot(astRoot, rootCtx);
            return rootCtx;
        } else {
            return rootCtx;
        }
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
