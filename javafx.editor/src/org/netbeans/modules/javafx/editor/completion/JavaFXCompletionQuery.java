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

import com.sun.javafx.api.tree.JavaFXTree;
import com.sun.javafx.api.tree.JavaFXTree.JavaFXKind;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.tools.javafx.api.JavafxcTrees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.JToolTip;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CompilationController;
import org.netbeans.api.javafx.source.JavaFXSource;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.javafx.source.Task;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

final class JavaFXCompletionQuery extends AsyncCompletionQuery implements Task<CompilationController> {
    
    private static final Logger logger = Logger.getLogger(JavaFXCompletionProvider.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    static final String ERROR = "<error>";
    static final String INIT = "<init>";
    static final String SPACE = " ";
    static final String COLON = ":";
    static final String SEMI = ";";
    static final String EMPTY = "";
    static final String ABSTRACT_KEYWORD = "abstract";
    static final String AFTER_KEYWORD = "after";
    static final String AND_KEYWORD = "and";
    static final String AS_KEYWORD = "as";
    static final String ASSERT_KEYWORD = "assert";
    static final String ATTRIBUTE_KEYWORD = "attribute";
    static final String BEFORE_KEYWORD = "before";
    static final String BIND_KEYWORD = "bind";
    static final String BOUND_KEYWORD = "bound";
    static final String BREAK_KEYWORD = "break";
    static final String CATCH_KEYWORD = "catch";
    static final String CLASS_KEYWORD = "class";
    static final String CONTINUE_KEYWORD = "continue";
    static final String DELETE_KEYWORD = "delete";
    static final String ELSE_KEYWORD = "else";
    static final String EXCLUSIVE_KEYWORD = "exclusive";
    static final String EXTENDS_KEYWORD = "extends";
    static final String FALSE_KEYWORD = "false";
    static final String FINALLY_KEYWORD = "finally";
    static final String FIRST_KEYWORD = "first";
    static final String FOR_KEYWORD = "for";
    static final String FROM_KEYWORD = "from";
    static final String FUNCTION_KEYWORD = "function";
    static final String IF_KEYWORD = "if";
    static final String IMPORT_KEYWORD = "import";
    static final String INDEXOF_KEYWORD = "indexof";
    static final String INIT_KEYWORD = "init";
    static final String IN_KEYWORD = "in";
    static final String INSERT_KEYWORD = "insert";
    static final String INSTANCEOF_KEYWORD = "instanceof";
    static final String INTO_KEYWORD = "into";
    static final String INVERSE_KEYWORD = "inverse";
    static final String LAST_KEYWORD = "last";
    static final String LAZY_KEYWORD = "lazy";
    static final String LET_KEYWORD = "let";
    static final String NEW_KEYWORD = "new";
    static final String NOT_KEYWORD = "not";
    static final String NULL_KEYWORD = "null";
    static final String ON_KEYWORD = "on";
    static final String OR_KEYWORD = "or";
    static final String OVERRIDE_KEYWORD = "override";
    static final String PACKAGE_KEYWORD = "package";
    static final String PRIVATE_KEYWORD = "private";
    static final String PROTECTED_KEYWORD = "protected";
    static final String PUBLIC_KEYWORD = "public";
    static final String READONLY_KEYWORD = "readonly";
    static final String REPLACE_KEYWORD = "replace";
    static final String RETURN_KEYWORD = "return";
    static final String REVERSE_KEYWORD = "reverse";
    static final String SIZEOF_KEYWORD = "sizeof";
    static final String STATIC_KEYWORD = "static";
    static final String STEP_KEYWORD = "step";
    static final String SUPER_KEYWORD = "super";
    static final String THEN_KEYWORD = "then";
    static final String THIS_KEYWORD = "this";
    static final String THROW_KEYWORD = "throw";
    static final String TRANSIENT_KEYWORD = "transient";
    static final String TRUE_KEYWORD = "true";
    static final String TRY_KEYWORD = "try";
    static final String TWEEN_KEYWORD = "tween";
    static final String TYPEOF_KEYWORD = "typeof";
    static final String VAR_KEYWORD = "var";
    static final String WHERE_KEYWORD = "where";
    static final String WHILE_KEYWORD = "while";
    static final String WITH_KEYWORD = "with";
    
    static final String[] STATEMENT_KEYWORDS = new String[]{
        FOR_KEYWORD,
        TRY_KEYWORD, 
        WHILE_KEYWORD
    };
    static final String[] STATEMENT_SPACE_KEYWORDS = new String[]{
        INSERT_KEYWORD,
        NEW_KEYWORD,
        THROW_KEYWORD,
        VAR_KEYWORD
    };
    static final String[] CLASS_BODY_KEYWORDS = new String[]{
        ABSTRACT_KEYWORD,
        ATTRIBUTE_KEYWORD, 
        FUNCTION_KEYWORD,
        PRIVATE_KEYWORD, PROTECTED_KEYWORD, PUBLIC_KEYWORD,
        READONLY_KEYWORD
    };

    static Pattern camelCasePattern = Pattern.compile("(?:\\p{javaUpperCase}(?:\\p{javaLowerCase}|\\p{Digit}|\\.|\\$)*){2,}");
    
    List<JavaFXCompletionItem> results;
    private boolean hasAdditionalItems;
    private JToolTip toolTip;
    private CompletionDocumentation documentation;
    int anchorOffset;
    private int toolTipOffset;
    private JTextComponent component;
    int queryType;
    private int caretOffset;
    private String filterPrefix;
    private boolean hasTask;

    public JavaFXCompletionQuery(int queryType, int caretOffset, boolean hasTask) {
        super();
        this.queryType = queryType;
        this.caretOffset = caretOffset;
        this.hasTask = hasTask;
    }

    @Override
    protected void preQueryUpdate(JTextComponent component) {
        int newCaretOffset = component.getSelectionStart();
        if (newCaretOffset >= caretOffset) {
            try {
                if (isJavaIdentifierPart(component.getDocument().getText(caretOffset, newCaretOffset - caretOffset))) {
                    return;
                }
            } catch (BadLocationException e) {
            }
        }
        Completion.get().hideCompletion();
    }

    @Override
    protected void prepareQuery(JTextComponent component) {
        this.component = component;
    }

    @Override
    protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
        try {
            this.caretOffset = caretOffset;
            if (queryType == JavaFXCompletionProvider.TOOLTIP_QUERY_TYPE || JavaFXCompletionProvider.isJavaFXContext(component, caretOffset)) {
                results = null;
                documentation = null;
                toolTip = null;
                anchorOffset = -1;
                JavaFXSource js = JavaFXSource.forDocument(doc);
                if (js != null) {
                    Future<Void> f = js.runWhenScanFinished(this, true);
                    if (!f.isDone()) {
                        component.putClientProperty("completion-active", Boolean.FALSE);
                        //NOI18N
                        resultSet.setWaitText(NbBundle.getMessage(JavaFXCompletionProvider.class, "scanning-in-progress"));
                        //NOI18N
                        f.get();
                    }
                    if ((queryType & JavaFXCompletionProvider.COMPLETION_QUERY_TYPE) != 0) {
                        if (results != null) {
                            resultSet.addAllItems(results);
                        }
                        resultSet.setHasAdditionalItems(hasAdditionalItems);
                        if (hasAdditionalItems) {
                            resultSet.setHasAdditionalItemsText(NbBundle.getMessage(JavaFXCompletionProvider.class, "JCP-imported-items"));
                        }
                    } else if (queryType == JavaFXCompletionProvider.TOOLTIP_QUERY_TYPE) {
                        if (toolTip != null) {
                            resultSet.setToolTip(toolTip);
                        }
                    } else if (queryType == JavaFXCompletionProvider.DOCUMENTATION_QUERY_TYPE) {
                        if (documentation != null) {
                            resultSet.setDocumentation(documentation);
                        }
                    }
                    if (anchorOffset > -1) {
                        resultSet.setAnchorOffset(anchorOffset);
                    }
                }
            }
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        } finally {
            resultSet.finish();
        }
    }

    @Override
    protected boolean canFilter(JTextComponent component) {
        filterPrefix = null;
        int newOffset = component.getSelectionStart();
        if ((queryType & JavaFXCompletionProvider.COMPLETION_QUERY_TYPE) != 0) {
            int offset = Math.min(anchorOffset, caretOffset);
            if (offset > -1) {
                if (newOffset < offset) {
                    return true;
                }
                if (newOffset >= caretOffset) {
                    try {
                        String prefix = component.getDocument().getText(offset, newOffset - offset);
                        filterPrefix = isJavaIdentifierPart(prefix) ? prefix : null;
                        if (filterPrefix != null && filterPrefix.length() == 0) {
                            anchorOffset = newOffset;
                        }
                    } catch (BadLocationException e) {
                    }
                    return true;
                }
            }
            return false;
        } else if (queryType == JavaFXCompletionProvider.TOOLTIP_QUERY_TYPE) {
            try {
                if (newOffset == caretOffset) {
                    filterPrefix = EMPTY;
                } else if (newOffset - caretOffset > 0) {
                    filterPrefix = component.getDocument().getText(caretOffset, newOffset - caretOffset);
                } else if (newOffset - caretOffset < 0) {
                    filterPrefix = newOffset > toolTipOffset ? component.getDocument().getText(newOffset, caretOffset - newOffset) : null;
                }
            } catch (BadLocationException ex) {
            }
            return filterPrefix != null && filterPrefix.indexOf(',') == -1 && filterPrefix.indexOf('(') == -1 && filterPrefix.indexOf(')') == -1;
        }
        return false;
    }

    @Override
    protected void filter(CompletionResultSet resultSet) {
        try {
            if ((queryType & JavaFXCompletionProvider.COMPLETION_QUERY_TYPE) != 0) {
                if (results != null) {
                    if (filterPrefix != null) {
                        resultSet.addAllItems(getFilteredData(results, filterPrefix));
                        resultSet.setHasAdditionalItems(hasAdditionalItems);
                    } else {
                        Completion.get().hideDocumentation();
                        Completion.get().hideCompletion();
                    }
                }
            } else if (queryType == JavaFXCompletionProvider.TOOLTIP_QUERY_TYPE) {
                resultSet.setToolTip(toolTip);
            }
            resultSet.setAnchorOffset(anchorOffset);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        resultSet.finish();
    }

    public void run(CompilationController controller) throws Exception {
        if (!hasTask || !isTaskCancelled()) {
            if ((queryType & JavaFXCompletionProvider.COMPLETION_QUERY_TYPE) != 0) {
                if (component != null) {
                    component.putClientProperty("completion-active", Boolean.TRUE);
                }
                //NOI18N
                resolveCompletion(controller);
                if (component != null && isTaskCancelled()) {
                    component.putClientProperty("completion-active", Boolean.FALSE);
                }
            }
        }
    }

    private void resolveCompletion(CompilationController controller) throws IOException {
        JavaFXCompletionEnvironment env = getCompletionEnvironment(controller);
        results = new ArrayList<JavaFXCompletionItem>();
        anchorOffset = env.getOffset();
        TreePath path = env.getPath();
        Tree t = path.getLeaf();
        if (t instanceof JavaFXTree && t.getKind() == Tree.Kind.OTHER) {
            JavaFXTree jfxt = (JavaFXTree) t;
            JavaFXKind k = jfxt.getJavaFXKind();
            log("JavaFXKind: " + k);
            switch (k) {
                case BIND_EXPRESSION:
                    break;
                case BLOCK_EXPRESSION:
                    env.insideBlock();
                    break;
                case CLASS_DECLARATION:
                    env.insideClassDeclaration();
                    break;
                case FOR_EXPRESSION:
                    break;
                case FOR_EXPRESSION_IN_CLAUSE:
                    break;
                case FUNCTION_DEFINITION:
                    env.insideFunctionDefinition();
                    break;
                case FUNCTION_VALUE:
                    break;
                case INIT_DEFINITION:
                    break;
                case INSTANTIATE:
                    break;
                case INTERPOLATE:
                    break;
                case INTERPOLATE_VALUE:
                    break;
                case KEYFRAME_LITERAL:
                    break;
                case OBJECT_LITERAL_PART:
                    env.insideObjectLiteralPart();
                    break;
                case ON_REPLACE:
                    break;
                case POSTINIT_DEFINITION:
                    break;
                case SEQUENCE_DELETE:
                    break;
                case SEQUENCE_EMPTY:
                    break;
                case SEQUENCE_EXPLICIT:
                    break;
                case SEQUENCE_INDEXED:
                    break;
                case SEQUENCE_INSERT:
                    break;
                case SEQUENCE_RANGE:
                    break;
                case SEQUENCE_SLICE:
                    break;
                case SET_ATTRIBUTE_TO_OBJECT:
                    break;
                case STRING_EXPRESSION:
                    break;
                case TIME_LITERAL:
                    break;
                case TRIGGER_WRAPPER:
                    break;
                case TYPE_ANY:
                    break;
                case TYPE_CLASS:
                    env.insideClassDeclaration();
                    break;
                case TYPE_FUNCTIONAL:
                    break;
                case TYPE_UNKNOWN:
                    break;
            }
        } else {
            log("Java Kind: " + t.getKind());
            switch (t.getKind()) {
                case COMPILATION_UNIT:
                    env.insideCompilationUnit();
                    break;
                case IMPORT:
                    env.insideImport();
                    break;
                case CLASS:
                    env.insideClass();
                    break;
                case VARIABLE:
                    env.insideVariable();
                    break;
                case METHOD:
                    env.insideMethod();
                    break;
                case MODIFIERS:
                    env.insideModifiers( path);
                    break;
                case ANNOTATION:
                    break;
                case TYPE_PARAMETER:
                    break;
                case PARAMETERIZED_TYPE:
                    break;
                case UNBOUNDED_WILDCARD:
                case EXTENDS_WILDCARD:
                case SUPER_WILDCARD:
                    TreePath parentPath = path.getParentPath();

                    break;
                case BLOCK:
                    //insideBlock(env);
                    break;
                case MEMBER_SELECT:
                    env.insideMemberSelect();
                    break;
                case METHOD_INVOCATION:
                    env.insideMethodInvocation();
                    break;
                case NEW_CLASS:
                    break;
                case ASSERT:
                case RETURN:
                case THROW:
                    break;
                case CATCH:
                    break;
                case IF:
                    env.insideIf();
                    break;
                case WHILE_LOOP:
                    env.insideWhile();
                    break;
                case FOR_LOOP:
                    env.insideFor();
                    break;
                case ENHANCED_FOR_LOOP:
                    env.insideForEach();
                    break;
                case SWITCH:
                    env.insideSwitch();
                    break;
                case CASE:
                    env.insideCase();
                    break;
                case PARENTHESIZED:
                    env.insideParens();
                    break;
                case TYPE_CAST:
                    env.insideExpression( path);
                    break;
                case INSTANCE_OF:
                    env.insideTypeCheck();
                    break;
                case ARRAY_ACCESS:
                    env.insideArrayAccess();
                    break;
                case NEW_ARRAY:
                    env.insideNewArray();
                    break;
                case ASSIGNMENT:
                    env.insideAssignment();
                    break;
                case MULTIPLY_ASSIGNMENT:
                case DIVIDE_ASSIGNMENT:
                case REMAINDER_ASSIGNMENT:
                case PLUS_ASSIGNMENT:
                case MINUS_ASSIGNMENT:
                case LEFT_SHIFT_ASSIGNMENT:
                case RIGHT_SHIFT_ASSIGNMENT:
                case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
                case AND_ASSIGNMENT:
                case XOR_ASSIGNMENT:
                case OR_ASSIGNMENT:
                    env.insideCompoundAssignment();
                    break;
                case PREFIX_INCREMENT:
                case PREFIX_DECREMENT:
                case UNARY_PLUS:
                case UNARY_MINUS:
                case BITWISE_COMPLEMENT:
                case LOGICAL_COMPLEMENT:
                    env.localResult();
                    break;
                case AND:
                case CONDITIONAL_AND:
                case CONDITIONAL_OR:
                case DIVIDE:
                case EQUAL_TO:
                case GREATER_THAN:
                case GREATER_THAN_EQUAL:
                case LEFT_SHIFT:
                case LESS_THAN:
                case LESS_THAN_EQUAL:
                case MINUS:
                case MULTIPLY:
                case NOT_EQUAL_TO:
                case OR:
                case PLUS:
                case REMAINDER:
                case RIGHT_SHIFT:
                case UNSIGNED_RIGHT_SHIFT:
                case XOR:
                    env.insideBinaryTree();
                    break;
                case CONDITIONAL_EXPRESSION:
                    break;
                case EXPRESSION_STATEMENT:
                    env.insideExpressionStatement();
                    break;
            }
        }
        if (LOGGABLE) {
            log("Results: " + results);
        }
    }

    static boolean isJavaIdentifierPart(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!(Character.isJavaIdentifierPart(text.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    private Collection getFilteredData(Collection<JavaFXCompletionItem> data, String prefix) {
        if (prefix.length() == 0) {
            return data;
        }
        List ret = new ArrayList();
        boolean camelCase = prefix.length() > 1 && camelCasePattern.matcher(prefix).matches();
        for (Iterator<JavaFXCompletionItem> it = data.iterator(); it.hasNext();) {
            CompletionItem itm = it.next();
            if (JavaFXCompletionProvider.startsWith(itm.getInsertPrefix().toString(), prefix)) {
                ret.add(itm);
            }
        }
        return ret;
    }

    static Set<? extends TypeMirror> getSmartTypes(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        final CompilationController controller = env.getController();
        TreePath path = env.getPath();
        Tree lastTree = null;
        int dim = 0;
        while (path != null) {
            Tree tree = path.getLeaf();
            switch (tree.getKind()) {
                case VARIABLE:
                    TypeMirror type = controller.getTrees().getTypeMirror(new TreePath(path, ((VariableTree) tree).getType()));
                    if (type == null) {
                        return null;
                    }
                    while (dim-- > 0) {
                        if (type.getKind() == TypeKind.ARRAY) {
                            type = ((ArrayType) type).getComponentType();
                        } else {
                            return null;
                        }
                    }
                    return type != null ? Collections.singleton(type) : null;
                case ASSIGNMENT:
                    type = controller.getTrees().getTypeMirror(new TreePath(path, ((AssignmentTree) tree).getVariable()));
                    if (type == null) {
                        return null;
                    }
                    TreePath parentPath = path.getParentPath();
                    if (parentPath != null && parentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION && type.getKind() == TypeKind.EXECUTABLE) {
                        type = ((ExecutableType) type).getReturnType();
                        while (dim-- > 0) {
                            if (type.getKind() == TypeKind.ARRAY) {
                                type = ((ArrayType) type).getComponentType();
                            } else {
                                return null;
                            }
                        }
                        if (type.getKind() == TypeKind.ARRAY) {
                            type = ((ArrayType) type).getComponentType();
                        }
                    }
                    return type != null ? Collections.singleton(type) : null;
                case RETURN:
                    TreePath methodPath = JavaFXCompletionProvider.getPathElementOfKind(Tree.Kind.METHOD, path);
                    if (methodPath == null) {
                        return null;
                    }
                    Tree retTree = ((MethodTree) methodPath.getLeaf()).getReturnType();
                    if (retTree == null) {
                        return null;
                    }
                    type = controller.getTrees().getTypeMirror(new TreePath(methodPath, retTree));
                    return type != null ? Collections.singleton(type) : null;
                case THROW:
                    methodPath = JavaFXCompletionProvider.getPathElementOfKind(Tree.Kind.METHOD, path);
                    if (methodPath == null) {
                        return null;
                    }
                    HashSet<TypeMirror> ret = new HashSet<TypeMirror>();
                    JavafxcTrees trees = controller.getTrees();
                    for (ExpressionTree thr : ((MethodTree) methodPath.getLeaf()).getThrows()) {
                        type = trees.getTypeMirror(new TreePath(methodPath, thr));
                        if (type != null) {
                            ret.add(type);
                        }
                    }
                    return ret;
                case IF:
                    IfTree iff = (IfTree) tree;
                    return null;
                case WHILE_LOOP:
                    WhileLoopTree wl = (WhileLoopTree) tree;
                    return null;
                case FOR_LOOP:
                    ForLoopTree fl = (ForLoopTree) tree;
                    Tree cond = fl.getCondition();
                    if (lastTree != null) {
                        if (cond instanceof ErroneousTree) {
                            Iterator<? extends Tree> itt = ((ErroneousTree) cond).getErrorTrees().iterator();
                            if (itt.hasNext()) {
                                cond = itt.next();
                            }
                        }
                        return null;
                    }
                    SourcePositions sourcePositions = env.getSourcePositions();
                    CompilationUnitTree root = env.getRoot();
                    if (cond != null && sourcePositions.getEndPosition(root, cond) < offset) {
                        return null;
                    }
                    Tree lastInit = null;
                    for (Tree init : fl.getInitializer()) {
                        if (sourcePositions.getEndPosition(root, init) >= offset) {
                            return null;
                        }
                        lastInit = init;
                    }
                    String text = null;
                    if (lastInit == null) {
                        text = controller.getText().substring((int) sourcePositions.getStartPosition(root, fl), offset).trim();
                        int idx = text.indexOf('(');
                        if (idx >= 0) {
                            text = text.substring(idx + 1);
                        }
                    } else {
                        text = controller.getText().substring((int) sourcePositions.getEndPosition(root, lastInit), offset).trim();
                    }
                    return null;
                case ENHANCED_FOR_LOOP:
                    EnhancedForLoopTree efl = (EnhancedForLoopTree) tree;
                    Tree expr = efl.getExpression();
                    if (lastTree != null) {
                        if (expr instanceof ErroneousTree) {
                            Iterator<? extends Tree> itt = ((ErroneousTree) expr).getErrorTrees().iterator();
                            if (itt.hasNext()) {
                                expr = itt.next();
                            }
                        }
                        if (expr != lastTree) {
                            return null;
                        }
                    } else {
                        sourcePositions = env.getSourcePositions();
                        root = env.getRoot();
                        text = null;
                        if (efl.getVariable() == null) {
                            text = controller.getText().substring((int) sourcePositions.getStartPosition(root, efl), offset).trim();
                            int idx = text.indexOf('(');
                            if (idx >= 0) {
                                text = text.substring(idx + 1);
                            }
                        } else {
                            text = controller.getText().substring((int) sourcePositions.getEndPosition(root, efl.getVariable()), offset).trim();
                        }
                        if (!":".equals(text)) {
                            return null;
                        }
                    }
                    TypeMirror var = efl.getVariable() != null ? controller.getTrees().getTypeMirror(new TreePath(path, efl.getVariable())) : null;
                    return var != null ? Collections.singleton(var) : null;
                case SWITCH:
                    SwitchTree sw = (SwitchTree) tree;
                    if (sw.getExpression() != lastTree) {
                        return null;
                    }
                    ret = new HashSet<TypeMirror>();
                    return ret;
                case METHOD_INVOCATION:
                    return null;
                case NEW_CLASS:
                    return null;
                case NEW_ARRAY:
                    return null;
                case CASE:
                    CaseTree ct = (CaseTree) tree;
                    ExpressionTree exp = ct.getExpression();
                    if (exp != null && env.getSourcePositions().getEndPosition(env.getRoot(), exp) >= offset) {
                        parentPath = path.getParentPath();
                        if (parentPath.getLeaf().getKind() == Tree.Kind.SWITCH) {
                            exp = ((SwitchTree) parentPath.getLeaf()).getExpression();
                            type = controller.getTrees().getTypeMirror(new TreePath(parentPath, exp));
                            return type != null ? Collections.singleton(type) : null;
                        }
                    }
                    return null;
                case ANNOTATION:
                    return null;
                case REMAINDER_ASSIGNMENT:
                case AND_ASSIGNMENT:
                case XOR_ASSIGNMENT:
                case OR_ASSIGNMENT:
                case PREFIX_INCREMENT:
                case PREFIX_DECREMENT:
                case BITWISE_COMPLEMENT:
                case LEFT_SHIFT:
                case RIGHT_SHIFT:
                case UNSIGNED_RIGHT_SHIFT:
                case LEFT_SHIFT_ASSIGNMENT:
                case RIGHT_SHIFT_ASSIGNMENT:
                case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT:
                case AND:
                case OR:
                case XOR:
                case REMAINDER:
                    ret = new HashSet<TypeMirror>();
                    return ret;
                case CONDITIONAL_AND:
                case CONDITIONAL_OR:
                case LOGICAL_COMPLEMENT:
                    return null;
                case PLUS:
                    BinaryTree bt = (BinaryTree) tree;
                    TypeMirror tm = controller.getTrees().getTypeMirror(new TreePath(path, bt.getLeftOperand()));
                    if (tm == null) {
                        return null;
                    }
                    if (tm.getKind().isPrimitive()) {
                        ret = new HashSet<TypeMirror>();
                        return ret;
                    }
                    return Collections.singleton(tm);
                case PLUS_ASSIGNMENT:
                    CompoundAssignmentTree cat = (CompoundAssignmentTree) tree;
                    tm = controller.getTrees().getTypeMirror(new TreePath(path, cat.getVariable()));
                    if (tm == null) {
                        return null;
                    }
                    if (tm.getKind().isPrimitive()) {
                        ret = new HashSet<TypeMirror>();
                        return ret;
                    }
                    return Collections.singleton(tm);
                case MULTIPLY_ASSIGNMENT:
                case DIVIDE_ASSIGNMENT:
                case MINUS_ASSIGNMENT:
                case DIVIDE:
                case EQUAL_TO:
                case GREATER_THAN:
                case GREATER_THAN_EQUAL:
                case LESS_THAN:
                case LESS_THAN_EQUAL:
                case MINUS:
                case MULTIPLY:
                case NOT_EQUAL_TO:
                case UNARY_PLUS:
                case UNARY_MINUS:
                    ret = new HashSet<TypeMirror>();
//                        types = controller.getTypes();
//                        ret.add(types.getPrimitiveType(TypeKind.BYTE));
//                        ret.add(types.getPrimitiveType(TypeKind.CHAR));
//                        ret.add(types.getPrimitiveType(TypeKind.DOUBLE));
//                        ret.add(types.getPrimitiveType(TypeKind.FLOAT));
//                        ret.add(types.getPrimitiveType(TypeKind.INT));
//                        ret.add(types.getPrimitiveType(TypeKind.LONG));
//                        ret.add(types.getPrimitiveType(TypeKind.SHORT));
                    return ret;
                case EXPRESSION_STATEMENT:
                    exp = ((ExpressionStatementTree) tree).getExpression();
                    if (exp.getKind() == Tree.Kind.PARENTHESIZED) {
                        text = controller.getText().substring((int) env.getSourcePositions().getStartPosition(env.getRoot(), exp), offset).trim();
                        if (text.endsWith(")")) {
                            //NOI18N
                            return null;
                        }
                    }
                    break;
                case TYPE_CAST:
                    TypeCastTree tct = (TypeCastTree) tree;
                    if (env.getSourcePositions().getEndPosition(env.getRoot(), tct.getType()) <= offset) {
                        return null;
                    }
                    break;
            }
            lastTree = tree;
            path = path.getParentPath();
        }
        return null;
    }

    private String fullName(Tree tree) {
        switch (tree.getKind()) {
            case IDENTIFIER:
                return ((IdentifierTree) tree).getName().toString();
            case MEMBER_SELECT:
                String sname = fullName(((MemberSelectTree) tree).getExpression());
                return sname == null ? null : sname + '.' + ((MemberSelectTree) tree).getIdentifier();
            default:
                return null;
        }
    }

    private JavaFXCompletionEnvironment getCompletionEnvironment(CompilationController controller) throws IOException {
        controller.toPhase(Phase.PARSED);
        int offset = caretOffset;
        String prefix = null;
        if (offset > 0) {
            TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
            // When right at the token end move to previous token; otherwise move to the token that "contains" the offset
            if (ts.move(offset) == 0 || !ts.moveNext()) {
                ts.movePrevious();
            }
            int len = offset - ts.offset();
            if (len > 0 && (ts.token().id() == JFXTokenId.IDENTIFIER || (ts.token().id().primaryCategory().startsWith("keyword")) || ts.token().id().primaryCategory().equals("literal")) && ts.token().length() >= len) {
                //TODO: Use isKeyword(...) when available
                prefix = ts.token().toString().substring(0, len);
                offset = ts.offset();
            }
        }
        log("getCompletionEnvironment caretOffset: " + caretOffset + " offset: " + offset);
        TreePath path = controller.getTreeUtilities().pathFor(offset);
        return new JavaFXCompletionEnvironment(offset, prefix, controller, path, controller.getTrees().getSourcePositions(), this);
    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
