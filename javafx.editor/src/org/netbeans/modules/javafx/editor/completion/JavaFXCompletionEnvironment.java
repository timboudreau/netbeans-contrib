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

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javafx.tree.JFXBlockExpression;
import com.sun.tools.javafx.tree.JFXFunctionDefinition;
import com.sun.tools.javafx.tree.JFXType;
import com.sun.tools.javafx.tree.JFXVar;
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
import javax.lang.model.element.Modifier;
import static javax.lang.model.element.Modifier.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.Diagnostic;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CompilationController;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.javafx.source.TreeUtilities;
import org.netbeans.api.lexer.TokenSequence;
import static org.netbeans.modules.javafx.editor.completion.JavaFXCompletionQuery.*;

class JavaFXCompletionEnvironment {
    
    private static final Logger logger = Logger.getLogger(JavaFXCompletionEnvironment.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    private int offset;
    private String prefix;
    private boolean isCamelCasePrefix;
    private CompilationController controller;
    private TreePath path;
    private SourcePositions sourcePositions;
    private boolean insideForEachExpressiion = false;
    private Set<? extends TypeMirror> smartTypes = null;
    private JavaFXCompletionQuery query;

    JavaFXCompletionEnvironment(int offset, String prefix, CompilationController controller, TreePath path, SourcePositions sourcePositions, JavaFXCompletionQuery query) {
        super();
        this.offset = offset;
        this.prefix = prefix;
        this.isCamelCasePrefix = prefix != null && prefix.length() > 1 && JavaFXCompletionQuery.camelCasePattern.matcher(prefix).matches();
        this.controller = controller;
        this.path = path;
        this.sourcePositions = sourcePositions;
        this.query = query;
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
        return path.getCompilationUnit();
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
            smartTypes = JavaFXCompletionQuery.getSmartTypes(this);
            if (smartTypes != null) {
                Iterator<? extends TypeMirror> it = smartTypes.iterator();
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
                }
            }
        }
        return smartTypes;
    }
    
    void insideFunctionDefinition() throws IOException {
        JFXFunctionDefinition def = (JFXFunctionDefinition) getPath().getLeaf();
        int offset = getOffset();
        TreePath path = getPath();
        CompilationController controller = getController();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int startPos = (int) sourcePositions.getStartPosition(root, def);
        JFXType retType = def.getJFXReturnType();
        if (retType == null) {
            int modPos = (int) sourcePositions.getEndPosition(root, def.getModifiers());
            if (modPos > startPos) {
                startPos = modPos;
            }
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(startPos, offset);
            if (last == null) {
                addMemberModifiers(def.getModifiers().getFlags(), false);
                return;
            }
        } else {
            if (offset <= sourcePositions.getStartPosition(root, retType)) {
                addMemberModifiers(def.getModifiers().getFlags(), false);
                return;
            }
            startPos = (int) sourcePositions.getEndPosition(root, retType) + 1;
        }
        log("start: " + startPos);
        log("offset: " + offset);
        String headerText = controller.getText().substring(startPos, offset > startPos ? offset : startPos);
        int parStart = headerText.indexOf('(');
        log("parStart: " + parStart);
        if (parStart >= 0) {
            int parEnd = headerText.indexOf(')', parStart);
            if (parEnd > parStart) {
                headerText = headerText.substring(parEnd + 1).trim();
            } else {
                for (JFXVar param : def.getParameters()) {
                    int parPos = (int) sourcePositions.getEndPosition(root, param);
                    if (parPos == Diagnostic.NOPOS || offset <= parPos) {
                        break;
                    }
                    parStart = parPos - startPos;
                }
                headerText = headerText.substring(parStart).trim();
                if ("(".equals(headerText) || ",".equals(headerText)) {
                    addMemberModifiers(Collections.<Modifier>emptySet(), true);
                }
            }
        } else if (retType != null && headerText.trim().length() == 0) {
            insideExpression(new TreePath(path, retType));
        }
        int bodyPos = (int) sourcePositions.getStartPosition(root, def.getBodyExpression());
        log("bodyPos: " + bodyPos);
        if (offset > bodyPos) {
            insideFunctionBlock(def.getBodyExpression().getStatements());
        }
    }
    
    void insideObjectLiteralPart() {
    }

    void insideClassDeclaration() {
        addKeywordsForClassBody();
    }

    void insideCompilationUnit() throws IOException {
        if (isTreeBroken()) {
            // don't do anything in this case
            return;
        }
        int offset = getOffset();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        Tree pkg = root.getPackageName();
        if (pkg == null || offset <= sourcePositions.getStartPosition(root, root)) {
            addKeywordsForCU();
            return;
        }
        if (offset <= sourcePositions.getStartPosition(root, pkg)) {
            addPackages(getPrefix());
        } else {
            TokenSequence<JFXTokenId> first = findFirstNonWhitespaceToken((int) sourcePositions.getEndPosition(root, pkg), offset);
            if (first != null && first.token().id() == JFXTokenId.SEMI) {
                addKeywordsForCU();
            }
        }
    }

    /**
     * If the tree is broken we are in fact not in the compilation unit.
     * @param env
     * @return
     */
    boolean isTreeBroken() {
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int start = (int) sourcePositions.getStartPosition(root, root);
        int end = (int) sourcePositions.getEndPosition(root, root);
        log("isTreeBroken start: " + start + " end: " + end);
        return start == -1 || end == -1;
    }
    
    void insideImport() {
        int offset = getOffset();
        String prefix = getPrefix();
        ImportTree im = (ImportTree) getPath().getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        if (offset <= sourcePositions.getStartPosition(root, im.getQualifiedIdentifier())) {
            addPackages(prefix);
        }
    }

    void insideClass() throws IOException {
        int offset = getOffset();
        TreePath path = getPath();
        ClassTree cls = (ClassTree) path.getLeaf();
        CompilationController controller = getController();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int startPos = (int) sourcePositions.getEndPosition(root, cls.getModifiers());
        if (startPos <= 0) {
            startPos = (int) sourcePositions.getStartPosition(root, cls);
        }
        String headerText = controller.getText().substring(startPos, offset);
        int idx = headerText.indexOf('{');
        //NOI18N
        if (idx >= 0) {
            addKeywordsForClassBody();
            return;
        }
        TreeUtilities tu = controller.getTreeUtilities();
        Tree lastImpl = null;
        for (Tree impl : cls.getImplementsClause()) {
            int implPos = (int) sourcePositions.getEndPosition(root, impl);
            if (implPos == Diagnostic.NOPOS || offset <= implPos) {
                break;
            }
            lastImpl = impl;
            startPos = implPos;
        }
        if (lastImpl != null) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(startPos, offset);
            if (last != null && last.token().id() == JFXTokenId.COMMA) {
            }
            return;
        }
        Tree ext = cls.getExtendsClause();
        if (ext != null) {
            int extPos = (int) sourcePositions.getEndPosition(root, ext);
            if (extPos != Diagnostic.NOPOS && offset > extPos) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(extPos + 1, offset);
                //addKeyword(IMPLEMENTS_KEYWORD, SPACE, false);
                return;
            }
        }
        TypeParameterTree lastTypeParam = null;
        for (TypeParameterTree tp : cls.getTypeParameters()) {
            int tpPos = (int) sourcePositions.getEndPosition(root, tp);
            if (tpPos == Diagnostic.NOPOS || offset <= tpPos) {
                break;
            }
            lastTypeParam = tp;
            startPos = tpPos;
        }
        if (lastTypeParam != null) {
            TokenSequence<JFXTokenId> first = findFirstNonWhitespaceToken(startPos, offset);
            if (first != null && first.token().id() == JFXTokenId.GT) {
                first = nextNonWhitespaceToken(first);
                if (first != null && first.offset() < offset) {
                    if (first.token().id() == JFXTokenId.EXTENDS) {
                        return;
                    }
                }
            } else {
                if (lastTypeParam.getBounds().isEmpty()) {
                    addKeyword(EXTENDS_KEYWORD, SPACE, false);
                }
            }
            return;
        }
        TokenSequence<JFXTokenId> lastNonWhitespaceToken = findLastNonWhitespaceToken(startPos, offset);
        if (lastNonWhitespaceToken != null) {
            switch (lastNonWhitespaceToken.token().id()) {
                case EXTENDS:
                    break;
                case IDENTIFIER:
                    break;
            }
            return;
        }
        lastNonWhitespaceToken = findLastNonWhitespaceToken((int) sourcePositions.getStartPosition(root, cls), offset);
        if (path.getParentPath().getLeaf().getKind() == Tree.Kind.COMPILATION_UNIT) {
            addClassModifiers(cls.getModifiers().getFlags());
        } else {
            addMemberModifiers(cls.getModifiers().getFlags(), false);
        }
    }

    void insideVariable() throws IOException {
        int offset = getOffset();
        TreePath path = getPath();
        VariableTree var = (VariableTree) path.getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        boolean isLocal = path.getParentPath().getLeaf().getKind() != Tree.Kind.CLASS;
        Tree type = var.getType();
        int typePos = type.getKind() == Tree.Kind.ERRONEOUS && ((ErroneousTree) type).getErrorTrees().isEmpty() ? (int) sourcePositions.getEndPosition(root, type) : (int) sourcePositions.getStartPosition(root, type);
        if (offset <= typePos) {
            addMemberModifiers(var.getModifiers().getFlags(), isLocal);
            ModifiersTree mods = var.getModifiers();
            return;
        }
        Tree init = unwrapErrTree(var.getInitializer());
        if (init == null) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken((int) sourcePositions.getEndPosition(root, type), offset);
            if (last == null) {
                insideExpression(new TreePath(path, type));
            } else if (last.token().id() == JFXTokenId.EQ) {
                localResult();
                addValueKeywords();
            }
        } else {
            int pos = (int) sourcePositions.getStartPosition(root, init);
            if (pos < 0) {
                return;
            }
            if (offset <= pos) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken((int) sourcePositions.getEndPosition(root, type), offset);
                if (last == null) {
                    insideExpression(new TreePath(path, type));
                } else if (last.token().id() == JFXTokenId.EQ) {
                    localResult();
                    addValueKeywords();
                }
            } else {
                insideExpression(new TreePath(path, init));
            }
        }
    }

    void insideMethod() throws IOException {
        int offset = getOffset();
        String prefix = getPrefix();
        TreePath path = getPath();
        MethodTree mth = (MethodTree) path.getLeaf();
        CompilationController controller = getController();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int startPos = (int) sourcePositions.getStartPosition(root, mth);
        Tree retType = mth.getReturnType();
        if (retType == null) {
            int modPos = (int) sourcePositions.getEndPosition(root, mth.getModifiers());
            if (modPos > startPos) {
                startPos = modPos;
            }
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(startPos, offset);
            if (last == null) {
                addMemberModifiers(mth.getModifiers().getFlags(), false);
                return;
            }
        } else {
            if (offset <= sourcePositions.getStartPosition(root, retType)) {
                addMemberModifiers(mth.getModifiers().getFlags(), false);
                return;
            }
            startPos = (int) sourcePositions.getEndPosition(root, retType) + 1;
        }
        Tree lastThr = null;
        for (Tree thr : mth.getThrows()) {
            int thrPos = (int) sourcePositions.getEndPosition(root, thr);
            if (thrPos == Diagnostic.NOPOS || offset <= thrPos) {
                break;
            }
            lastThr = thr;
            startPos = thrPos;
        }
        if (lastThr != null) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(startPos, offset);
            if (last != null && last.token().id() == JFXTokenId.COMMA) {
                // TODO:
            }
            return;
        }
        String headerText = controller.getText().substring(startPos, offset);
        int parStart = headerText.indexOf('(');
        if (parStart >= 0) {
            int parEnd = headerText.indexOf(')', parStart);
            if (parEnd > parStart) {
                headerText = headerText.substring(parEnd + 1).trim();
            } else {
                for (VariableTree param : mth.getParameters()) {
                    int parPos = (int) sourcePositions.getEndPosition(root, param);
                    if (parPos == Diagnostic.NOPOS || offset <= parPos) {
                        break;
                    }
                    parStart = parPos - startPos;
                }
                headerText = headerText.substring(parStart).trim();
                if ("(".equals(headerText) || ",".equals(headerText)) {
                    addMemberModifiers(Collections.<Modifier>emptySet(), true);
                }
            }
        } else if (retType != null && headerText.trim().length() == 0) {
            insideExpression(new TreePath(path, retType));
        }
    }

    void insideModifiers(TreePath modPath) throws IOException {
        int offset = getOffset();
        ModifiersTree mods = (ModifiersTree) modPath.getLeaf();
        Set<Modifier> m = EnumSet.noneOf(Modifier.class);
        final TokenSequence<?> idTokenSequence = getController().getTreeUtilities().tokensFor(mods, getSourcePositions());
        TokenSequence<JFXTokenId> ts = (TokenSequence<JFXTokenId>) idTokenSequence;
        JFXTokenId lastNonWhitespaceTokenId = null;
        while (ts.moveNext() && ts.offset() < offset) {
            lastNonWhitespaceTokenId = ts.token().id();
            switch (lastNonWhitespaceTokenId) {
                case PUBLIC:
                    m.add(PUBLIC);
                    break;
                case PROTECTED:
                    m.add(PROTECTED);
                    break;
                case PRIVATE:
                    m.add(PRIVATE);
                    break;
                case STATIC:
                    m.add(STATIC);
                    break;
                case ABSTRACT:
                    m.add(ABSTRACT);
                    break;
            }
        }
        TreePath parentPath = modPath.getParentPath();
        Tree parent = parentPath.getLeaf();
        TreePath grandParentPath = parentPath.getParentPath();
        Tree grandParent = grandParentPath != null ? grandParentPath.getLeaf() : null;
        if (parent.getKind() == Tree.Kind.CLASS) {
            addClassModifiers(m);
        } else if (parent.getKind() != Tree.Kind.VARIABLE || grandParent == null || grandParent.getKind() == Tree.Kind.CLASS) {
            addMemberModifiers(m, false);
        } else if (parent.getKind() == Tree.Kind.VARIABLE && grandParent.getKind() == Tree.Kind.METHOD) {
            addMemberModifiers(m, true);
        } else {
            localResult();
            addKeywordsForStatement();
        }
    }   
    
    void insideFunctionBlock(com.sun.tools.javac.util.List<JCStatement> statements) throws IOException {
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int offset = getOffset();
        StatementTree last = null;
        for (StatementTree stat : statements) {
            int pos = (int) sourcePositions.getStartPosition(root, stat);
            if (pos == Diagnostic.NOPOS || offset <= pos) {
                break;
            }
            last = stat;
        }
        if (last == null) {
        } else if (last.getKind() == Tree.Kind.TRY) {
            if (((TryTree) last).getFinallyBlock() == null) {
                addKeyword(CATCH_KEYWORD, null, false);
                addKeyword(FINALLY_KEYWORD, null, false);
                if (((TryTree) last).getCatches().size() == 0) {
                    return;
                }
            }
        }
        localResult();
        addKeywordsForStatement();
    }
    
    void insideBlock() throws IOException {
        int offset = getOffset();
        JFXBlockExpression bl = (JFXBlockExpression) getPath().getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int blockPos = (int) sourcePositions.getStartPosition(root, bl);
        String text = getController().getText().substring(blockPos, offset);
        if (text.indexOf('{') < 0) {
            //NOI18N
            addMemberModifiers(Collections.singleton(STATIC), false);
            return;
        }
        StatementTree last = null;
        for (StatementTree stat : bl.getStatements()) {
            int pos = (int) sourcePositions.getStartPosition(root, stat);
            if (pos == Diagnostic.NOPOS || offset <= pos) {
                break;
            }
            last = stat;
        }
        if (last == null) {
        } else if (last.getKind() == Tree.Kind.TRY) {
            if (((TryTree) last).getFinallyBlock() == null) {
                addKeyword(CATCH_KEYWORD, null, false);
                addKeyword(FINALLY_KEYWORD, null, false);
                if (((TryTree) last).getCatches().size() == 0) {
                    return;
                }
            }
        }
        localResult();
        addKeywordsForStatement();
    }

    void insideMemberSelect() throws IOException {
        int offset = getOffset();
        String prefix = getPrefix();
        TreePath path = getPath();
        MemberSelectTree fa = (MemberSelectTree) path.getLeaf();
        CompilationController controller = getController();
        CompilationUnitTree root = getRoot();
        SourcePositions sourcePositions = getSourcePositions();
        int expEndPos = (int) sourcePositions.getEndPosition(root, fa.getExpression());
        boolean afterDot = false;
        boolean afterLt = false;
        int openLtNum = 0;
        JFXTokenId lastNonWhitespaceTokenId = null;
        TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
        ts.move(expEndPos);
        while (ts.moveNext()) {
            if (ts.offset() >= offset) {
                break;
            }
            switch (ts.token().id()) {
                case FLOATING_POINT_LITERAL:
                    if (ts.offset() != expEndPos || ts.token().text().charAt(0) != '.') {
                        break;
                    }
                case DOT:
                    afterDot = true;
                    break;
                case LT:
                    afterLt = true;
                    openLtNum++;
                    break;
                case GT:
                    openLtNum--;
                    break;
            }
            switch (ts.token().id()) {
                case WS:
                case LINE_COMMENT:
                case COMMENT:
                case DOC_COMMENT:
                    break;
                default:
                    lastNonWhitespaceTokenId = ts.token().id();
            }
        }
        if (!afterDot) {
            log("insideMemberSelect expEndPos: " + expEndPos + " offset: " + offset);
            if (expEndPos <= offset) {
                insideExpression(new TreePath(path, fa.getExpression()));
            }
            log("insideMemberSelect returning !afterDot");
            return;
        }
        if (openLtNum > 0) {
            switch (lastNonWhitespaceTokenId) {
                case QUES:
                    addKeyword(EXTENDS_KEYWORD, SPACE, false);
                    addKeyword(SUPER_KEYWORD, SPACE, false);
                    break;
                case LT:
                case COLON:
                case EXTENDS:
                case SUPER:
                    break;
            }
        }
        addLocalMembersAndVars();
    }

    void insideMethodInvocation() throws IOException {
        TreePath path = getPath();
        MethodInvocationTree mi = (MethodInvocationTree) path.getLeaf();
        int offset = getOffset();
        TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(mi, offset);
        if (ts == null || (ts.token().id() != JFXTokenId.LPAREN && ts.token().id() != JFXTokenId.COMMA)) {
            SourcePositions sp = getSourcePositions();
            CompilationUnitTree root = getRoot();
            int lastTokenEndOffset = ts.offset() + ts.token().length();
            for (ExpressionTree arg : mi.getArguments()) {
                int pos = (int) sp.getEndPosition(root, arg);
                if (lastTokenEndOffset == pos) {
                    insideExpression(new TreePath(path, arg));
                    break;
                }
                if (offset <= pos) {
                    break;
                }
            }
            return;
        }
        String prefix = getPrefix();
        if (prefix == null || prefix.length() == 0) {
            addMethodArguments(mi);
        }
        addLocalMembersAndVars();
        addValueKeywords();
    }

    void insideIf() throws IOException {
        IfTree iff = (IfTree) getPath().getLeaf();
        if (getSourcePositions().getEndPosition(getRoot(), iff.getCondition()) <= getOffset()) {
            localResult();
            addKeywordsForStatement();
        }
    }

    void insideWhile() throws IOException {
        WhileLoopTree wlt = (WhileLoopTree) getPath().getLeaf();
        if (getSourcePositions().getEndPosition(getRoot(), wlt.getCondition()) <= getOffset()) {
            localResult();
            addKeywordsForStatement();
        }
    }

    void insideFor() throws IOException {
        int offset = getOffset();
        TreePath path = getPath();
        ForLoopTree fl = (ForLoopTree) path.getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        Tree lastTree = null;
        int lastTreePos = offset;
        for (Tree update : fl.getUpdate()) {
            int pos = (int) sourcePositions.getEndPosition(root, update);
            if (pos == Diagnostic.NOPOS || offset <= pos) {
                break;
            }
            lastTree = update;
            lastTreePos = pos;
        }
        if (lastTree == null) {
            int pos = (int) sourcePositions.getEndPosition(root, fl.getCondition());
            if (pos != Diagnostic.NOPOS && pos < offset) {
                lastTree = fl.getCondition();
                lastTreePos = pos;
            }
        }
        if (lastTree == null) {
            for (Tree init : fl.getInitializer()) {
                int pos = (int) sourcePositions.getEndPosition(root, init);
                if (pos == Diagnostic.NOPOS || offset <= pos) {
                    break;
                }
                lastTree = init;
                lastTreePos = pos;
            }
        }
        if (lastTree == null) {
            addLocalFieldsAndVars();
        } else {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(lastTreePos, offset);
            if (last != null && last.token().id() == JFXTokenId.SEMI) {
                localResult();
                addValueKeywords();
            } else if (last != null && last.token().id() == JFXTokenId.RPAREN) {
                localResult();
                addKeywordsForStatement();
            } else {
                switch (lastTree.getKind()) {
                    case VARIABLE:
                        Tree var = ((VariableTree) lastTree).getInitializer();
                        if (var != null) {
                            insideExpression(new TreePath(new TreePath(path, lastTree), var));
                        }
                        break;
                    case EXPRESSION_STATEMENT:
                        Tree exp = unwrapErrTree(((ExpressionStatementTree) lastTree).getExpression());
                        if (exp != null) {
                            insideExpression(new TreePath(new TreePath(path, lastTree), exp));
                        }
                        break;
                    default:
                        insideExpression(new TreePath(path, lastTree));
                }
            }
        }
    }

    void insideForEach() throws IOException {
        int offset = getOffset();
        TreePath path = getPath();
        EnhancedForLoopTree efl = (EnhancedForLoopTree) path.getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        CompilationController controller = getController();
        if (sourcePositions.getStartPosition(root, efl.getExpression()) >= offset) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken((int) sourcePositions.getEndPosition(root, efl.getVariable()), offset);
            if (last != null && last.token().id() == JFXTokenId.COLON) {
                insideForEachExpressiion();
                addKeyword(NEW_KEYWORD, SPACE, false);
                localResult();
            }
            return;
        }
        TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken((int) sourcePositions.getEndPosition(root, efl.getExpression()), offset);
        if (last != null && last.token().id() == JFXTokenId.RPAREN) {
            addKeywordsForStatement();
        } else {
            insideForEachExpressiion();
            addKeyword(NEW_KEYWORD, SPACE, false);
        }
        localResult();
    }

    void insideSwitch() throws IOException {
        int offset = getOffset();
        String prefix = getPrefix();
        TreePath path = getPath();
        SwitchTree st = (SwitchTree) path.getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        if (sourcePositions.getStartPosition(root, st.getExpression()) < offset) {
            CaseTree lastCase = null;
            for (CaseTree t : st.getCases()) {
                int pos = (int) sourcePositions.getStartPosition(root, t);
                if (pos == Diagnostic.NOPOS || offset <= pos) {
                    break;
                }
                lastCase = t;
            }
            if (lastCase != null) {
                localResult();
                addKeywordsForStatement();
            } else {
                TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(st, offset);
                if (ts != null && ts.token().id() == JFXTokenId.LBRACE) {
                }
            }
        }
    }

    void insideCase() throws IOException {
        int offset = getOffset();
        TreePath path = getPath();
        CaseTree cst = (CaseTree) path.getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        CompilationController controller = getController();
        if (cst.getExpression() != null && ((sourcePositions.getStartPosition(root, cst.getExpression()) >= offset) || (cst.getExpression().getKind() == Tree.Kind.ERRONEOUS && ((ErroneousTree) cst.getExpression()).getErrorTrees().isEmpty() && sourcePositions.getEndPosition(root, cst.getExpression()) >= offset))) {
            TreePath path1 = path.getParentPath();
            if (path1.getLeaf().getKind() == Tree.Kind.SWITCH) {
                TypeMirror tm = controller.getTrees().getTypeMirror(new TreePath(path1, ((SwitchTree) path1.getLeaf()).getExpression()));
            }
        } else {
            TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(cst, offset);
            if (ts != null && ts.token().id() == JFXTokenId.COLON) {
                localResult();
                addKeywordsForStatement();
            }
        }
    }

    void insideParens() throws IOException {
        TreePath path = getPath();
        ParenthesizedTree pa = (ParenthesizedTree) path.getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        Tree exp = unwrapErrTree(pa.getExpression());
        if (exp == null || getOffset() <= sourcePositions.getStartPosition(root, exp)) {
            HashSet<TypeElement> toExclude = new HashSet<TypeElement>();
            if (query.queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE && path.getParentPath().getLeaf().getKind() != Tree.Kind.SWITCH) {
                Set<? extends TypeMirror> smarts = getSmartTypes();
                if (smarts != null) {
                    for (TypeMirror smart : smarts) {
                        if (smart != null) {
                            if (smart.getKind() == TypeKind.DECLARED) {
                                for (DeclaredType subtype : getSubtypesOf((DeclaredType) smart)) {
                                    TypeElement elem = (TypeElement) subtype.asElement();
                                    toExclude.add(elem);
                                }
                            } else if (smart.getKind() == TypeKind.ARRAY) {
                                try {
                                } catch (IllegalArgumentException iae) {
                                }
                            }
                        }
                    }
                }
            }
            addLocalMembersAndVars();
            addValueKeywords();
        } else {
            insideExpression(new TreePath(path, exp));
        }
    }

    void insideTypeCheck() throws IOException {
        InstanceOfTree iot = (InstanceOfTree) getPath().getLeaf();
        TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(iot, getOffset());
    }

    void insideArrayAccess() throws IOException {
        int offset = getOffset();
        ArrayAccessTree aat = (ArrayAccessTree) getPath().getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int aaTextStart = (int) sourcePositions.getEndPosition(root, aat.getExpression());
        if (aaTextStart != Diagnostic.NOPOS) {
            Tree expr = unwrapErrTree(aat.getIndex());
            if (expr == null || offset <= (int) sourcePositions.getStartPosition(root, expr)) {
                String aatText = getController().getText().substring(aaTextStart, offset);
                int bPos = aatText.indexOf('[');
                //NOI18N
                if (bPos > -1) {
                    localResult();
                    addValueKeywords();
                }
            }
        }
    }

    void insideNewArray() throws IOException {
        int offset = getOffset();
        TreePath path = getPath();
        NewArrayTree nat = (NewArrayTree) path.getLeaf();
        if (nat.getInitializers() != null) {
            SourcePositions sourcePositions = getSourcePositions();
            CompilationUnitTree root = getRoot();
            Tree last = null;
            int lastPos = offset;
            for (Tree init : nat.getInitializers()) {
                int pos = (int) sourcePositions.getEndPosition(root, init);
                if (pos == Diagnostic.NOPOS || offset <= pos) {
                    break;
                }
                last = init;
                lastPos = pos;
            }
            if (last != null) {
                TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(lastPos, offset);
                if (ts != null && ts.token().id() == JFXTokenId.COMMA) {
                    TreePath parentPath = path.getParentPath();
                    TreePath gparentPath = parentPath.getParentPath();
                    if (parentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION || gparentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION) {
                        addLocalConstantsAndTypes();
                    } else {
                        localResult();
                        addValueKeywords();
                    }
                }
                return;
            }
        }
        TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(nat, offset);
        switch (ts.token().id()) {
            case LBRACKET:
            case LBRACE:
                TreePath parentPath = path.getParentPath();
                TreePath gparentPath = parentPath.getParentPath();
                if (parentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION || gparentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION) {
                    addLocalConstantsAndTypes();
                } else {
                    localResult();
                    addValueKeywords();
                }
                break;
            case RBRACKET:
                if (nat.getDimensions().size() > 0) {
                    insideExpression(path);
                }
                break;
        }
    }

    void insideAssignment() throws IOException {
        int offset = getOffset();
        TreePath path = getPath();
        AssignmentTree as = (AssignmentTree) path.getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int asTextStart = (int) sourcePositions.getEndPosition(root, as.getVariable());
        if (asTextStart != Diagnostic.NOPOS) {
            Tree expr = unwrapErrTree(as.getExpression());
            if (expr == null || offset <= (int) sourcePositions.getStartPosition(root, expr)) {
                String asText = getController().getText().substring(asTextStart, offset);
                int eqPos = asText.indexOf('=');
                if (eqPos > -1) {
                    if (path.getParentPath().getLeaf().getKind() == Tree.Kind.ANNOTATION) {
                        addLocalConstantsAndTypes();
                    } else {
                        localResult();
                        addValueKeywords();
                    }
                }
            } else {
                insideExpression(new TreePath(path, expr));
            }
        }
    }

    void insideCompoundAssignment() throws IOException {
        int offset = getOffset();
        CompoundAssignmentTree cat = (CompoundAssignmentTree) getPath().getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int catTextStart = (int) sourcePositions.getEndPosition(root, cat.getVariable());
        if (catTextStart != Diagnostic.NOPOS) {
            Tree expr = unwrapErrTree(cat.getExpression());
            if (expr == null || offset <= (int) sourcePositions.getStartPosition(root, expr)) {
                String catText = getController().getText().substring(catTextStart, offset);
                int eqPos = catText.indexOf('=');
                //NOI18N
                if (eqPos > -1) {
                    localResult();
                    addValueKeywords();
                }
            }
        }
    }

    void insideBinaryTree() throws IOException {
        int offset = getOffset();
        BinaryTree bi = (BinaryTree) getPath().getLeaf();
        SourcePositions sourcePositions = getSourcePositions();
        CompilationUnitTree root = getRoot();
        int pos = (int) sourcePositions.getEndPosition(root, bi.getRightOperand());
        if (pos != Diagnostic.NOPOS && pos < offset) {
            return;
        }
        pos = (int) sourcePositions.getEndPosition(root, bi.getLeftOperand());
        if (pos != Diagnostic.NOPOS) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(pos, offset);
            if (last != null) {
                localResult();
                addValueKeywords();
            }
        }
    }

    void insideExpressionStatement() throws IOException {
        TreePath path = getPath();
        ExpressionStatementTree est = (ExpressionStatementTree) path.getLeaf();
        CompilationController controller = getController();
        Tree t = est.getExpression();
        if (t.getKind() == Tree.Kind.ERRONEOUS) {
            Iterator<? extends Tree> it = ((ErroneousTree) t).getErrorTrees().iterator();
            if (it.hasNext()) {
                t = it.next();
            } else {
                TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
                ts.move((int) getSourcePositions().getStartPosition(getRoot(), est));
                ts.movePrevious();
                switch (ts.token().id()) {
                    case FOR:
                    case IF:
                    case WHILE:
                        return;
                }
                localResult();
                Tree parentTree = path.getParentPath().getLeaf();
                switch (parentTree.getKind()) {
                    case FOR_LOOP:getPath().getLeaf();
                        if (((ForLoopTree) parentTree).getStatement() == est) {
                            addKeywordsForStatement();
                        } else {
                            addValueKeywords();
                        }
                        break;
                    case ENHANCED_FOR_LOOP:
                        if (((EnhancedForLoopTree) parentTree).getStatement() == est) {
                            addKeywordsForStatement();
                        } else {
                            addValueKeywords();
                        }
                        break;
                    case VARIABLE:
                        addValueKeywords();
                        break;
                    default:
                        addKeywordsForStatement();
                        break;
                }
                return;
            }
        }
        TreePath tPath = new TreePath(path, t);
        if (t.getKind() == Tree.Kind.MODIFIERS) {
            insideModifiers(tPath);
        } else if (t.getKind() == Tree.Kind.MEMBER_SELECT && ERROR.contentEquals(((MemberSelectTree) t).getIdentifier())) {
            controller.toPhase(Phase.ELEMENTS_RESOLVED);
            TreePath expPath = new TreePath(tPath, ((MemberSelectTree) t).getExpression());
            TypeMirror type = controller.getTrees().getTypeMirror(expPath);
            switch (type.getKind()) {
                case TYPEVAR:
                    type = ((TypeVariable) type).getUpperBound();
                    if (type == null) {
                        return;
                    }
                case ARRAY:
                case DECLARED:
                case BOOLEAN:
                case BYTE:
                case CHAR:
                case DOUBLE:
                case FLOAT:
                case INT:
                case LONG:
                case SHORT:
                case VOID:
                    break;
                default:
            }
        } else {
            insideExpression(tPath);
        }
    }

    void insideExpression(TreePath exPath) throws IOException {
        int offset = getOffset();
        String prefix = getPrefix();
        Tree et = exPath.getLeaf();
        Tree parent = exPath.getParentPath().getLeaf();
        CompilationController controller = getController();
        int endPos = (int) getSourcePositions().getEndPosition(getRoot(), et);
        if (endPos != Diagnostic.NOPOS && endPos < offset) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(endPos, offset);
            if (last != null) {
                return;
            }
        }
    }

    void localResult() throws IOException {
        addLocalMembersAndVars();
    }

    private void addLocalConstantsAndTypes() throws IOException {
        log("addLocalConstantsAndTypes: " + getPrefix());
    }

    private void addLocalMembersAndVars() throws IOException {
        log("addLocalMembersAndVars: " + getPrefix());
    }

    private void addLocalFieldsAndVars() throws IOException {
        log("addLocalFieldsAndVars: " + getPrefix());
    }

    private void addPackages(String fqnPrefix) {
    }

    private List<DeclaredType> getSubtypesOf(DeclaredType baseType) throws IOException {
        return Collections.emptyList();
    }

    private void addMethodArguments(MethodInvocationTree mit) throws IOException {
    }

    private void addKeyword(String kw, String postfix, boolean smartType) {
        if (JavaFXCompletionProvider.startsWith(kw, getPrefix())) {
            query.results.add(JavaFXCompletionItem.createKeywordItem(kw, postfix, query.anchorOffset, smartType));
        }
    }

    private void addKeywordsForCU() {
        List<String> kws = new ArrayList<String>();
        int offset = getOffset();
        String prefix = getPrefix();
        CompilationUnitTree cu = getRoot();
        SourcePositions sourcePositions = getSourcePositions();
        kws.add(ABSTRACT_KEYWORD);
        kws.add(CLASS_KEYWORD);
        kws.add(VAR_KEYWORD);
        kws.add(FUNCTION_KEYWORD);
        kws.add(PUBLIC_KEYWORD);
        kws.add(IMPORT_KEYWORD);
        boolean beforeAnyClass = true;
        for (Tree t : cu.getTypeDecls()) {
            if (t.getKind() == Tree.Kind.CLASS) {
                int pos = (int) sourcePositions.getEndPosition(cu, t);
                if (pos != Diagnostic.NOPOS && offset >= pos) {
                    beforeAnyClass = false;
                }
            }
        }
        if (beforeAnyClass) {
            Tree firstImport = null;
            for (Tree t : cu.getImports()) {
                firstImport = t;
                break;
            }
            Tree pd = cu.getPackageName();
            if ((pd != null && offset <= sourcePositions.getStartPosition(cu, cu)) || (pd == null && (firstImport == null || sourcePositions.getStartPosition(cu, firstImport) >= offset))) {
                kws.add(PACKAGE_KEYWORD);
            }
        }
        for (String kw : kws) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                query.results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
    }

    private void addKeywordsForClassBody() {
        String prefix = getPrefix();
        for (String kw : CLASS_BODY_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                query.results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
    }

    private void addKeywordsForStatement() {
        String prefix = getPrefix();
        for (String kw : STATEMENT_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                query.results.add(JavaFXCompletionItem.createKeywordItem(kw, null, query.anchorOffset, false));
            }
        }
        for (String kw : STATEMENT_SPACE_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                query.results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
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
            query.results.add(JavaFXCompletionItem.createKeywordItem(RETURN_KEYWORD, postfix, query.anchorOffset, false));
        }
        TreePath tp = getPath();
        while (tp != null) {
            switch (tp.getLeaf().getKind()) {
                case DO_WHILE_LOOP:
                case ENHANCED_FOR_LOOP:
                case FOR_LOOP:
                case WHILE_LOOP:
                    if (JavaFXCompletionProvider.startsWith(CONTINUE_KEYWORD, prefix)) {
                        query.results.add(JavaFXCompletionItem.createKeywordItem(CONTINUE_KEYWORD, SEMI, query.anchorOffset, false));
                    }
                case SWITCH:
                    if (JavaFXCompletionProvider.startsWith(BREAK_KEYWORD, prefix)) {
                        query.results.add(JavaFXCompletionItem.createKeywordItem(BREAK_KEYWORD, SEMI, query.anchorOffset, false));
                    }
                    break;
            }
            tp = tp.getParentPath();
        }
    }

    private void addValueKeywords() throws IOException {
        String prefix = getPrefix();
        boolean smartType = false;
        if (query.queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE) {
            Set<? extends TypeMirror> smartTypes = getSmartTypes();
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
            query.results.add(JavaFXCompletionItem.createKeywordItem(FALSE_KEYWORD, null, query.anchorOffset, smartType));
        }
        if (JavaFXCompletionProvider.startsWith(TRUE_KEYWORD, prefix)) {
            query.results.add(JavaFXCompletionItem.createKeywordItem(TRUE_KEYWORD, null, query.anchorOffset, smartType));
        }
        if (JavaFXCompletionProvider.startsWith(NULL_KEYWORD, prefix)) {
            query.results.add(JavaFXCompletionItem.createKeywordItem(NULL_KEYWORD, null, query.anchorOffset, false));
        }
        if (JavaFXCompletionProvider.startsWith(NEW_KEYWORD, prefix)) {
            query.results.add(JavaFXCompletionItem.createKeywordItem(NEW_KEYWORD, SPACE, query.anchorOffset, false));
        }
        if (JavaFXCompletionProvider.startsWith(BIND_KEYWORD, prefix)) {
            query.results.add(JavaFXCompletionItem.createKeywordItem(BIND_KEYWORD, SPACE, query.anchorOffset, false));
        }
    }

    private void addClassModifiers(Set<Modifier> modifiers) {
        String prefix = getPrefix();
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
                query.results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
    }

    private void addMemberModifiers(Set<Modifier> modifiers, boolean isLocal) {
        String prefix = getPrefix();
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
                query.results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, query.anchorOffset, false));
            }
        }
    }
    
    private TokenSequence<JFXTokenId> findLastNonWhitespaceToken(Tree tree, int position) {
        int startPos = (int) getSourcePositions().getStartPosition(getRoot(), tree);
        return findLastNonWhitespaceToken(startPos, position);
    }

    private TokenSequence<JFXTokenId> findLastNonWhitespaceToken(int startPos, int endPos) {
        TokenSequence<JFXTokenId> ts = getController().getTokenHierarchy().tokenSequence(JFXTokenId.language());
        ts.move(endPos);
        ts = previousNonWhitespaceToken(ts);
        if (ts == null || ts.offset() < startPos) {
            return null;
        }
        return ts;
    }
    
    private TokenSequence<JFXTokenId> findFirstNonWhitespaceToken(Tree tree, int position) {
        int startPos = (int) getSourcePositions().getStartPosition(getRoot(), tree);
        return findFirstNonWhitespaceToken(startPos, position);
    }

    private TokenSequence<JFXTokenId> findFirstNonWhitespaceToken(int startPos, int endPos) {
        TokenSequence<JFXTokenId> ts = getController().getTokenHierarchy().tokenSequence(JFXTokenId.language());
        ts.move(startPos);
        ts = nextNonWhitespaceToken(ts);
        if (ts == null || ts.offset() >= endPos) {
            return null;
        }
        return ts;
    }
    
    private static TokenSequence<JFXTokenId> nextNonWhitespaceToken(TokenSequence<JFXTokenId> ts) {
        while (ts.moveNext()) {
            int offset = ts.offset();
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
    
    private static Tree unwrapErrTree(Tree tree) {
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
