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
package org.netbeans.modules.javafx.editor.completion;

import com.sun.javafx.api.tree.BlockExpressionTree;
import com.sun.javafx.api.tree.ForExpressionInClauseTree;
import com.sun.javafx.api.tree.ForExpressionTree;
import com.sun.javafx.api.tree.JavaFXTree;
import com.sun.javafx.api.tree.JavaFXTree.JavaFXKind;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.tools.javafx.api.JavafxcTrees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import static javax.lang.model.element.Modifier.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CompilationController;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.lexer.TokenSequence;
import static org.netbeans.modules.javafx.editor.completion.JavaFXCompletionQuery.*;

public class JavaFXCompletionEnvironment<T extends Tree> {
    
    private static final Logger logger = Logger.getLogger(JavaFXCompletionEnvironment.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    protected int offset;
    protected String prefix;
    protected boolean isCamelCasePrefix;
    protected CompilationController controller;
    protected TreePath path;
    protected SourcePositions sourcePositions;
    protected boolean insideForEachExpressiion = false;
    protected Set<? extends TypeMirror> smartTypes = null;
    protected CompilationUnitTree root;
    protected JavaFXCompletionQuery query;

    protected JavaFXCompletionEnvironment() {
    }
    
    /*
     * Thies method must be called after constructor before a call to resolveCompletion
     */
    void init(int offset, String prefix, CompilationController controller, TreePath path, SourcePositions sourcePositions, JavaFXCompletionQuery query) {
        this.offset = offset;
        this.prefix = prefix;
        this.isCamelCasePrefix = prefix != null && prefix.length() > 1 && JavaFXCompletionQuery.camelCasePattern.matcher(prefix).matches();
        this.controller = controller;
        this.path = path;
        this.sourcePositions = sourcePositions;
        this.query = query;
        this.root = path.getCompilationUnit();
    }
    
    /**
     * This method should be overriden in subclasses
     */
    protected void inside(T t) throws IOException {
        log("NOT IMPLEMENTED inside " + t);
    }

    public int getOffset() {
        return offset;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isCamelCasePrefix() {
        return isCamelCasePrefix;
    }

    public CompilationController getController() {
        return controller;
    }

    public CompilationUnitTree getRoot() {
        return root;
    }

    public TreePath getPath() {
        return path;
    }

    public SourcePositions getSourcePositions() {
        return sourcePositions;
    }

    public void insideForEachExpressiion() {
        this.insideForEachExpressiion = true;
    }

    public boolean isInsideForEachExpressiion() {
        return insideForEachExpressiion;
    }

    public Set<? extends TypeMirror> getSmartTypes() throws IOException {
        if (smartTypes == null) {
            Set<? extends TypeMirror> stypes = JavaFXCompletionQuery.getSmartTypes(this);
            if (stypes != null) {
                Iterator<? extends TypeMirror> it = stypes.iterator();
                TypeMirror err = null;
                if (it.hasNext()) {
                    err = it.next();
                    if (it.hasNext() || err.getKind() != TypeKind.ERROR) {
                        err = null;
                    }
                }
                if (err != null) {
                    HashSet<TypeMirror> st = new HashSet<TypeMirror>();
                    smartTypes = st;
                } else {
                    smartTypes = stypes;
                }
            }
        }
        return smartTypes;
    }
    
    /**
     * If the tree is broken we are in fact not in the compilation unit.
     * @param env
     * @return
     */
    protected boolean isTreeBroken() {
        int start = (int) sourcePositions.getStartPosition(root, root);
        int end = (int) sourcePositions.getEndPosition(root, root);
        log("isTreeBroken start: " + start + " end: " + end);
        return start == -1 || end == -1;
    }
    
    protected String fullName(Tree tree) {
        switch (tree.getKind()) {
        case IDENTIFIER:
            return ((IdentifierTree)tree).getName().toString();
        case MEMBER_SELECT:
            String sname = fullName(((MemberSelectTree)tree).getExpression());
            return sname == null ? null : sname + '.' + ((MemberSelectTree)tree).getIdentifier();
        default:
            return null;
        }
    }

    void insideTypeCheck() throws IOException {
        InstanceOfTree iot = (InstanceOfTree) getPath().getLeaf();
        TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(iot, getOffset());
    }


    protected void insideExpression(TreePath exPath) throws IOException {
        Tree et = exPath.getLeaf();
        Tree parent = exPath.getParentPath().getLeaf();
        int endPos = (int) getSourcePositions().getEndPosition(root, et);
        if (endPos != Diagnostic.NOPOS && endPos < offset) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(endPos, offset);
            if (last != null) {
                return;
            }
        }
        log("NOT IMPLEMENTED: insideExpression " + exPath);
        
    }
    
    protected void addResult(JavaFXCompletionItem i) {
        query.results.add(i);
    }
    
    protected void addMembers(final TypeMirror type) throws IOException {
        log("addMembers: " + type);
        getController().toPhase(Phase.ANALYZED);
        
        if (type == null || type.getKind() != TypeKind.DECLARED) {
            log("RETURNING: type.getKind() == " + type.getKind());
            return;
        }

        DeclaredType dt = (DeclaredType)type;
        log("  elementKind == " + dt.asElement().getKind());
        if (dt.asElement().getKind() != ElementKind.CLASS) {
            return;
        }
        Elements elements = getController().getElements();
        for (Element member : elements.getAllMembers((TypeElement) dt.asElement())) {
            log("    member == " + member + " member.getKind() " + member.getKind());
            String s = member.getSimpleName().toString();
            if (JavaFXCompletionProvider.startsWith(s, getPrefix())) {
                if (member.getKind() == ElementKind.METHOD) {
                    addResult(
                        JavaFXCompletionItem.createExecutableItem(
                            (ExecutableElement)member,
                            (ExecutableType)member.asType(),
                            offset, false, false, false, false)
                    );
                }
                if (member.getKind() == ElementKind.FIELD) {
                    addResult(
                        JavaFXCompletionItem.createVariableItem(
                            member.getSimpleName().toString(),
                            offset, false)
                    );
                }
            }
        }
    }

    protected void localResult() throws IOException {
        addLocalMembersAndVars();
    }
    
    protected void addMemberConstantsAndTypes(final TypeMirror type, final Element elem) throws IOException {
        log("addMemberConstantsAndTypes: " + type + " elem: " + elem);
    }

    protected void addLocalMembersAndVars() throws IOException {
        log("addLocalMembersAndVars: " + getPrefix());
        getController().toPhase(Phase.ANALYZED);
        
        for (TreePath tp = getPath(); tp != null; tp = tp.getParentPath()) {
            log("  tree kind: " + tp.getLeaf().getKind());
            Tree t = tp.getLeaf();
            if (t.getKind() != Tree.Kind.OTHER || ! (t instanceof JavaFXTree)) {
                continue;
            }
            JavaFXTree jfxt = (JavaFXTree) t;
            JavaFXKind k = jfxt.getJavaFXKind();
            log("  fx kind: " + k);
            if (k == JavaFXKind.CLASS_DECLARATION) {
                TypeMirror tm = getController().getTrees().getTypeMirror(tp);
                log("  tm == " + tm + " ---- tm.getKind() == " + (tm == null ? "null" : tm.getKind()));
                addMembers(tm);
            }
            if (k == JavaFXKind.BLOCK_EXPRESSION) {
                BlockExpressionTree bet = (BlockExpressionTree)jfxt;
                log("  block expression: " + bet + "\n");
                for (StatementTree st : bet.getStatements()) {
                    TreePath expPath = new TreePath(tp, st);
                    log("    expPath == " + expPath.getLeaf());
                    JavafxcTrees trees = controller.getTrees();
                    Element type = trees.getElement(expPath);
                    if (type == null) {
                        continue;
                    }
                    log("    type.getKind() == " + type.getKind());
                    if (type.getKind() == ElementKind.LOCAL_VARIABLE) {
                        log("    adding " + type.getSimpleName());
                        addResult(JavaFXCompletionItem.createVariableItem(
                            type.getSimpleName().toString(), offset, false));
                    }
                }
            }
            if (k == JavaFXKind.FOR_EXPRESSION) {
                ForExpressionTree fet = (ForExpressionTree)jfxt;
                log("  for expression: " + fet + "\n");
                for (ForExpressionInClauseTree fetic : fet.getInClauses()) {
                    log("  fetic: " + fetic + "\n");
                        addResult(JavaFXCompletionItem.createVariableItem(
                            fetic.getVariable().getName().toString(), offset, false));
                }
            }
        }
    }

    protected void addPackages(String fqnPrefix) {
        log("NOT IMPLEMENTED: addPackages " + fqnPrefix);
    }

    protected List<DeclaredType> getSubtypesOf(DeclaredType baseType) throws IOException {
        log("NOT IMPLEMENTED: getSubtypesOf " + baseType);
        return Collections.emptyList();
    }

    protected void addMethodArguments(MethodInvocationTree mit) throws IOException {
        log("NOT IMPLEMENTED: addMethodArguments " + mit);
    }

    protected void addKeyword(String kw, String postfix, boolean smartType) {
        if (JavaFXCompletionProvider.startsWith(kw, getPrefix())) {
            addResult(JavaFXCompletionItem.createKeywordItem(kw, postfix, query.anchorOffset, smartType));
        }
    }

    protected void addKeywordsForCU() {
        List<String> kws = new ArrayList<String>();
        kws.add(ABSTRACT_KEYWORD);
        kws.add(CLASS_KEYWORD);
        kws.add(VAR_KEYWORD);
        kws.add(FUNCTION_KEYWORD);
        kws.add(PUBLIC_KEYWORD);
        kws.add(IMPORT_KEYWORD);
        boolean beforeAnyClass = true;
        for (Tree t : root.getTypeDecls()) {
            if (t.getKind() == Tree.Kind.CLASS) {
                int pos = (int) sourcePositions.getEndPosition(root, t);
                if (pos != Diagnostic.NOPOS && offset >= pos) {
                    beforeAnyClass = false;
                }
            }
        }
        if (beforeAnyClass) {
            Tree firstImport = null;
            for (Tree t : root.getImports()) {
                firstImport = t;
                break;
            }
            Tree pd = root.getPackageName();
            if ((pd != null && offset <= sourcePositions.getStartPosition(root, root)) || (pd == null && (firstImport == null || sourcePositions.getStartPosition(root, firstImport) >= offset))) {
                kws.add(PACKAGE_KEYWORD);
            }
        }
        for (String kw : kws) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                addResult(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
    }

    protected void addKeywordsForClassBody() {
        for (String kw : CLASS_BODY_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                addResult(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
    }

    protected void addKeywordsForStatement() {
        for (String kw : STATEMENT_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                addResult(JavaFXCompletionItem.createKeywordItem(kw, null, query.anchorOffset, false));
            }
        }
        for (String kw : STATEMENT_SPACE_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                addResult(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
        if (JavaFXCompletionProvider.startsWith(RETURN_KEYWORD, prefix)) {
            TreePath mth = JavaFXCompletionProvider.getPathElementOfKind(Tree.Kind.METHOD, getPath());
            String postfix = SPACE;
            if (mth != null) {
                Tree rt = ((MethodTree) mth.getLeaf()).getReturnType();
                if (rt == null || rt.getKind() == Tree.Kind.PRIMITIVE_TYPE && ((PrimitiveTypeTree) rt).getPrimitiveTypeKind() == TypeKind.VOID) {
                    postfix = SEMI;
                }
            }
            addResult(JavaFXCompletionItem.createKeywordItem(RETURN_KEYWORD, postfix, query.anchorOffset, false));
        }
        TreePath tp = getPath();
        while (tp != null) {
            switch (tp.getLeaf().getKind()) {
                case DO_WHILE_LOOP:
                case ENHANCED_FOR_LOOP:
                case FOR_LOOP:
                case WHILE_LOOP:
                    if (JavaFXCompletionProvider.startsWith(CONTINUE_KEYWORD, prefix)) {
                        addResult(JavaFXCompletionItem.createKeywordItem(CONTINUE_KEYWORD, SEMI, query.anchorOffset, false));
                    }
                case SWITCH:
                    if (JavaFXCompletionProvider.startsWith(BREAK_KEYWORD, prefix)) {
                        addResult(JavaFXCompletionItem.createKeywordItem(BREAK_KEYWORD, SEMI, query.anchorOffset, false));
                    }
                    break;
            }
            tp = tp.getParentPath();
        }
    }

    protected void addValueKeywords() throws IOException {
        boolean smartType = false;
        if (query.queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE) {
            if (smartTypes != null && !smartTypes.isEmpty()) {
                for (TypeMirror st : smartTypes) {
                    if (st.getKind() == TypeKind.BOOLEAN) {
                        smartType = true;
                        break;
                    }
                }
            }
        }
        if (JavaFXCompletionProvider.startsWith(FALSE_KEYWORD, prefix)) {
            addResult(JavaFXCompletionItem.createKeywordItem(FALSE_KEYWORD, null, query.anchorOffset, smartType));
        }
        if (JavaFXCompletionProvider.startsWith(TRUE_KEYWORD, prefix)) {
            addResult(JavaFXCompletionItem.createKeywordItem(TRUE_KEYWORD, null, query.anchorOffset, smartType));
        }
        if (JavaFXCompletionProvider.startsWith(NULL_KEYWORD, prefix)) {
            addResult(JavaFXCompletionItem.createKeywordItem(NULL_KEYWORD, null, query.anchorOffset, false));
        }
        if (JavaFXCompletionProvider.startsWith(NEW_KEYWORD, prefix)) {
            addResult(JavaFXCompletionItem.createKeywordItem(NEW_KEYWORD, SPACE, query.anchorOffset, false));
        }
        if (JavaFXCompletionProvider.startsWith(BIND_KEYWORD, prefix)) {
            addResult(JavaFXCompletionItem.createKeywordItem(BIND_KEYWORD, SPACE, query.anchorOffset, false));
        }
    }

    protected void addClassModifiers(Set<Modifier> modifiers) {
        List<String> kws = new ArrayList<String>();
        if (!modifiers.contains(PUBLIC) && !modifiers.contains(PRIVATE)) {
            kws.add(PUBLIC_KEYWORD);
        }
        if (!modifiers.contains(FINAL) && !modifiers.contains(ABSTRACT)) {
            kws.add(ABSTRACT_KEYWORD);
        }
        kws.add(CLASS_KEYWORD);
        for (String kw : kws) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                addResult(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
    }

    protected void addMemberModifiers(Set<Modifier> modifiers, boolean isLocal) {
        List<String> kws = new ArrayList<String>();
        if (isLocal) {
        } else {
            if (!modifiers.contains(PUBLIC) && !modifiers.contains(PROTECTED) && !modifiers.contains(PRIVATE)) {
                kws.add(PUBLIC_KEYWORD);
                kws.add(PROTECTED_KEYWORD);
                kws.add(PRIVATE_KEYWORD);
            }
            if (!modifiers.contains(FINAL) && !modifiers.contains(ABSTRACT)) {
                kws.add(ABSTRACT_KEYWORD);
            }
            if (!modifiers.contains(STATIC)) {
                kws.add(STATIC_KEYWORD);
            }
            kws.add(READONLY_KEYWORD);
        }
        for (String kw : kws) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                addResult(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
    }
    
    protected void addPackageContent(PackageElement pe, EnumSet<ElementKind> kinds, DeclaredType baseType, boolean insideNew) throws IOException {
            Set<? extends TypeMirror> smartTypes = query.queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE ? getSmartTypes() : null;
            Elements elements = controller.getElements();
            Types types = controller.getTypes();
            JavafxcTrees trees = controller.getTrees();
            for(Element e : pe.getEnclosedElements()) {
                if (e.getKind().isClass() || e.getKind().isInterface()) {
                    String name = e.getSimpleName().toString();
                        if (JavaFXCompletionProvider.startsWith(name, prefix)) {
                            addResult(JavaFXCompletionItem.createTypeItem((TypeElement)e, (DeclaredType)e.asType(), getOffset(), elements.isDeprecated(e), insideNew, false));
                    }
                }
            }
            String pkgName = pe.getQualifiedName() + "."; //NOI18N
            if (prefix != null && prefix.length() > 0)
                pkgName += prefix;
            addPackages(pkgName);
        }
    
    protected TokenSequence<JFXTokenId> findLastNonWhitespaceToken(Tree tree, int position) {
        int startPos = (int) getSourcePositions().getStartPosition(root, tree);
        return findLastNonWhitespaceToken(startPos, position);
    }

    protected TokenSequence<JFXTokenId> findLastNonWhitespaceToken(int startPos, int endPos) {
        TokenSequence<JFXTokenId> ts = getController().getTokenHierarchy().tokenSequence(JFXTokenId.language());
        ts.move(endPos);
        ts = previousNonWhitespaceToken(ts);
        if (ts == null || ts.offset() < startPos) {
            return null;
        }
        return ts;
    }
    
    private TokenSequence<JFXTokenId> findFirstNonWhitespaceToken(Tree tree, int position) {
        int startPos = (int) getSourcePositions().getStartPosition(root, tree);
        return findFirstNonWhitespaceToken(startPos, position);
    }

    protected TokenSequence<JFXTokenId> findFirstNonWhitespaceToken(int startPos, int endPos) {
        TokenSequence<JFXTokenId> ts = getController().getTokenHierarchy().tokenSequence(JFXTokenId.language());
        ts.move(startPos);
        ts = nextNonWhitespaceToken(ts);
        if (ts == null || ts.offset() >= endPos) {
            return null;
        }
        return ts;
    }
    
    protected static TokenSequence<JFXTokenId> nextNonWhitespaceToken(TokenSequence<JFXTokenId> ts) {
        while (ts.moveNext()) {
            switch (ts.token().id()) {
                case WS:
                case LINE_COMMENT:
                case COMMENT:
                case DOC_COMMENT:
                    break;
                default:
                    return ts;
            }
        }
        return null;
    }

    private static TokenSequence<JFXTokenId> previousNonWhitespaceToken(TokenSequence<JFXTokenId> ts) {
        while (ts.movePrevious()) {
            switch (ts.token().id()) {
                case WS:
                case LINE_COMMENT:
                case COMMENT:
                case DOC_COMMENT:
                    break;
                default:
                    return ts;
            }
        }
        return null;
    }
    
    protected static Tree unwrapErrTree(Tree tree) {
        if (tree != null && tree.getKind() == Tree.Kind.ERRONEOUS) {
            Iterator<? extends Tree> it = ((ErroneousTree) tree).getErrorTrees().iterator();
            tree = it.hasNext() ? it.next() : null;
        }
        return tree;
    }


    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
