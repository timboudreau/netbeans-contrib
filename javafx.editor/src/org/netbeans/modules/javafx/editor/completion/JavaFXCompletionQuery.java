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
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
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
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javafx.api.JavafxcTrees;
import com.sun.tools.javafx.tree.JFXBlockExpression;
import com.sun.tools.javafx.tree.JFXFunctionDefinition;
import com.sun.tools.javafx.tree.JFXType;
import com.sun.tools.javafx.tree.JFXVar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.lang.model.element.Modifier;
import static javax.lang.model.element.Modifier.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.swing.JToolTip;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.tools.Diagnostic;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CompilationController;
import org.netbeans.api.javafx.source.JavaFXSource;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.javafx.source.Task;
import org.netbeans.api.javafx.source.TreeUtilities;
import org.netbeans.api.lexer.Token;
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

    private static final String ERROR = "<error>";
    private static final String INIT = "<init>";
    private static final String SPACE = " ";
    private static final String COLON = ":";
    private static final String SEMI = ";";
    private static final String EMPTY = "";
    private static final String ABSTRACT_KEYWORD = "abstract";
    private static final String AFTER_KEYWORD = "after";
    private static final String AND_KEYWORD = "and";
    private static final String AS_KEYWORD = "as";
    private static final String ASSERT_KEYWORD = "assert";
    private static final String ATTRIBUTE_KEYWORD = "attribute";
    private static final String BEFORE_KEYWORD = "before";
    private static final String BIND_KEYWORD = "bind";
    private static final String BOUND_KEYWORD = "bound";
    private static final String BREAK_KEYWORD = "break";
    private static final String CATCH_KEYWORD = "catch";
    private static final String CLASS_KEYWORD = "class";
    private static final String CONTINUE_KEYWORD = "continue";
    private static final String DELETE_KEYWORD = "delete";
    private static final String ELSE_KEYWORD = "else";
    private static final String EXCLUSIVE_KEYWORD = "exclusive";
    private static final String EXTENDS_KEYWORD = "extends";
    private static final String FALSE_KEYWORD = "false";
    private static final String FINALLY_KEYWORD = "finally";
    private static final String FIRST_KEYWORD = "first";
    private static final String FOR_KEYWORD = "for";
    private static final String FROM_KEYWORD = "from";
    private static final String FUNCTION_KEYWORD = "function";
    private static final String IF_KEYWORD = "if";
    private static final String IMPORT_KEYWORD = "import";
    private static final String INDEXOF_KEYWORD = "indexof";
    private static final String INIT_KEYWORD = "init";
    private static final String IN_KEYWORD = "in";
    private static final String INSERT_KEYWORD = "insert";
    private static final String INSTANCEOF_KEYWORD = "instanceof";
    private static final String INTO_KEYWORD = "into";
    private static final String INVERSE_KEYWORD = "inverse";
    private static final String LAST_KEYWORD = "last";
    private static final String LAZY_KEYWORD = "lazy";
    private static final String LET_KEYWORD = "let";
    private static final String NEW_KEYWORD = "new";
    private static final String NOT_KEYWORD = "not";
    private static final String NULL_KEYWORD = "null";
    private static final String ON_KEYWORD = "on";
    private static final String OR_KEYWORD = "or";
    private static final String OVERRIDE_KEYWORD = "override";
    private static final String PACKAGE_KEYWORD = "package";
    private static final String PRIVATE_KEYWORD = "private";
    private static final String PROTECTED_KEYWORD = "protected";
    private static final String PUBLIC_KEYWORD = "public";
    private static final String READONLY_KEYWORD = "readonly";
    private static final String REPLACE_KEYWORD = "replace";
    private static final String RETURN_KEYWORD = "return";
    private static final String REVERSE_KEYWORD = "reverse";
    private static final String SIZEOF_KEYWORD = "sizeof";
    private static final String STATIC_KEYWORD = "static";
    private static final String STEP_KEYWORD = "step";
    private static final String SUPER_KEYWORD = "super";
    private static final String THEN_KEYWORD = "then";
    private static final String THIS_KEYWORD = "this";
    private static final String THROW_KEYWORD = "throw";
    private static final String TRANSIENT_KEYWORD = "transient";
    private static final String TRUE_KEYWORD = "true";
    private static final String TRY_KEYWORD = "try";
    private static final String TWEEN_KEYWORD = "tween";
    private static final String TYPEOF_KEYWORD = "typeof";
    private static final String VAR_KEYWORD = "var";
    private static final String WHERE_KEYWORD = "where";
    private static final String WHILE_KEYWORD = "while";
    private static final String WITH_KEYWORD = "with";
    
    private static final String[] STATEMENT_KEYWORDS = new String[]{
        FOR_KEYWORD,
        TRY_KEYWORD, 
        WHILE_KEYWORD
    };
    private static final String[] STATEMENT_SPACE_KEYWORDS = new String[]{
        INSERT_KEYWORD,
        NEW_KEYWORD,
        THROW_KEYWORD,
        VAR_KEYWORD
    };
    private static final String[] CLASS_BODY_KEYWORDS = new String[]{
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
    private int anchorOffset;
    private int toolTipOffset;
    private JTextComponent component;
    private int queryType;
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
                    insideBlock(env);
                    break;
                case CLASS_DECLARATION:
                    insideClassDeclaration(env);
                    break;
                case FOR_EXPRESSION:
                    break;
                case FOR_EXPRESSION_IN_CLAUSE:
                    break;
                case FUNCTION_DEFINITION:
                    insideFunctionDefinition(env);
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
                    insideObjectLiteralPart(env);
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
                    insideClassDeclaration(env);
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
                    insideCompilationUnit(env);
                    break;
                case IMPORT:
                    insideImport(env);
                    break;
                case CLASS:
                    insideClass(env);
                    break;
                case VARIABLE:
                    insideVariable(env);
                    break;
                case METHOD:
                    insideMethod(env);
                    break;
                case MODIFIERS:
                    insideModifiers(env, path);
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
                    insideMemberSelect(env);
                    break;
                case METHOD_INVOCATION:
                    insideMethodInvocation(env);
                    break;
                case NEW_CLASS:
                    break;
                case ASSERT:
                case RETURN:
                case THROW:
                    localResult(env);
                    addValueKeywords(env);
                    break;
                case CATCH:
                    break;
                case IF:
                    insideIf(env);
                    break;
                case WHILE_LOOP:
                    insideWhile(env);
                    break;
                case FOR_LOOP:
                    insideFor(env);
                    break;
                case ENHANCED_FOR_LOOP:
                    insideForEach(env);
                    break;
                case SWITCH:
                    insideSwitch(env);
                    break;
                case CASE:
                    insideCase(env);
                    break;
                case PARENTHESIZED:
                    insideParens(env);
                    break;
                case TYPE_CAST:
                    insideExpression(env, path);
                    break;
                case INSTANCE_OF:
                    insideTypeCheck(env);
                    break;
                case ARRAY_ACCESS:
                    insideArrayAccess(env);
                    break;
                case NEW_ARRAY:
                    insideNewArray(env);
                    break;
                case ASSIGNMENT:
                    insideAssignment(env);
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
                    insideCompoundAssignment(env);
                    break;
                case PREFIX_INCREMENT:
                case PREFIX_DECREMENT:
                case UNARY_PLUS:
                case UNARY_MINUS:
                case BITWISE_COMPLEMENT:
                case LOGICAL_COMPLEMENT:
                    localResult(env);
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
                    insideBinaryTree(env);
                    break;
                case CONDITIONAL_EXPRESSION:
                    break;
                case EXPRESSION_STATEMENT:
                    insideExpressionStatement(env);
                    break;
            }
        }
        if (LOGGABLE) {
            log("Results: " + results);
        }
    }

    private void insideFunctionDefinition(JavaFXCompletionEnvironment env) throws IOException {
        JFXFunctionDefinition def = (JFXFunctionDefinition) env.getPath().getLeaf();
        int offset = env.getOffset();
        TreePath path = env.getPath();
        CompilationController controller = env.getController();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int startPos = (int) sourcePositions.getStartPosition(root, def);
        JFXType retType = def.getJFXReturnType();
        if (retType == null) {
            int modPos = (int) sourcePositions.getEndPosition(root, def.getModifiers());
            if (modPos > startPos) {
                startPos = modPos;
            }
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, startPos, offset);
            if (last == null) {
                addMemberModifiers(env, def.getModifiers().getFlags(), false);
                return;
            }
        } else {
            if (offset <= sourcePositions.getStartPosition(root, retType)) {
                addMemberModifiers(env, def.getModifiers().getFlags(), false);
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
                    addMemberModifiers(env, Collections.<Modifier>emptySet(), true);
                }
            }
        } else if (retType != null && headerText.trim().length() == 0) {
            insideExpression(env, new TreePath(path, retType));
        }
        int bodyPos = (int) sourcePositions.getStartPosition(root, def.getBodyExpression());
        log("bodyPos: " + bodyPos);
        if (offset > bodyPos) {
            insideFunctionBlock(env, def.getBodyExpression().getStatements());
        }
    }
    
    private void insideObjectLiteralPart(JavaFXCompletionEnvironment env) {
    }

    private void insideClassDeclaration(JavaFXCompletionEnvironment env) {
        addKeywordsForClassBody(env);
    }

    private void insideCompilationUnit(JavaFXCompletionEnvironment env) throws IOException {
        if (isTreeBroken(env)) {
            // don't do anything in this case
            return;
        }
        int offset = env.getOffset();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        Tree pkg = root.getPackageName();
        if (pkg == null || offset <= sourcePositions.getStartPosition(root, root)) {
            addKeywordsForCU(env);
            return;
        }
        if (offset <= sourcePositions.getStartPosition(root, pkg)) {
            addPackages(env, env.getPrefix());
        } else {
            TokenSequence<JFXTokenId> first = findFirstNonWhitespaceToken(env, (int) sourcePositions.getEndPosition(root, pkg), offset);
            if (first != null && first.token().id() == JFXTokenId.SEMI) {
                addKeywordsForCU(env);
            }
        }
    }

    /**
     * If the tree is broken we are in fact not in the compilation unit.
     * @param env
     * @return
     */
    private boolean isTreeBroken(JavaFXCompletionEnvironment env) {
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int start = (int) sourcePositions.getStartPosition(root, root);
        int end = (int) sourcePositions.getEndPosition(root, root);
        log("isTreeBroken start: " + start + " end: " + end);
        return start == -1 || end == -1;
    }
    
    private void insideImport(JavaFXCompletionEnvironment env) {
        int offset = env.getOffset();
        String prefix = env.getPrefix();
        ImportTree im = (ImportTree) env.getPath().getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        if (offset <= sourcePositions.getStartPosition(root, im.getQualifiedIdentifier())) {
            addPackages(env, prefix);
        }
    }

    private void insideClass(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        TreePath path = env.getPath();
        ClassTree cls = (ClassTree) path.getLeaf();
        CompilationController controller = env.getController();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int startPos = (int) sourcePositions.getEndPosition(root, cls.getModifiers());
        if (startPos <= 0) {
            startPos = (int) sourcePositions.getStartPosition(root, cls);
        }
        String headerText = controller.getText().substring(startPos, offset);
        int idx = headerText.indexOf('{');
        //NOI18N
        if (idx >= 0) {
            addKeywordsForClassBody(env);
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
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, startPos, offset);
            if (last != null && last.token().id() == JFXTokenId.COMMA) {
            }
            return;
        }
        Tree ext = cls.getExtendsClause();
        if (ext != null) {
            int extPos = (int) sourcePositions.getEndPosition(root, ext);
            if (extPos != Diagnostic.NOPOS && offset > extPos) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, extPos + 1, offset);
                //addKeyword(env, IMPLEMENTS_KEYWORD, SPACE, false);
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
            TokenSequence<JFXTokenId> first = findFirstNonWhitespaceToken(env, startPos, offset);
            if (first != null && first.token().id() == JFXTokenId.GT) {
                first = nextNonWhitespaceToken(first);
                if (first != null && first.offset() < offset) {
                    if (first.token().id() == JFXTokenId.EXTENDS) {
                        return;
                    }
                }
            } else {
                if (lastTypeParam.getBounds().isEmpty()) {
                    addKeyword(env, EXTENDS_KEYWORD, SPACE, false);
                }
            }
            return;
        }
        TokenSequence<JFXTokenId> lastNonWhitespaceToken = findLastNonWhitespaceToken(env, startPos, offset);
        if (lastNonWhitespaceToken != null) {
            switch (lastNonWhitespaceToken.token().id()) {
                case EXTENDS:
                    break;
                case IDENTIFIER:
                    break;
            }
            return;
        }
        lastNonWhitespaceToken = findLastNonWhitespaceToken(env, (int) sourcePositions.getStartPosition(root, cls), offset);
        if (path.getParentPath().getLeaf().getKind() == Tree.Kind.COMPILATION_UNIT) {
            addClassModifiers(env, cls.getModifiers().getFlags());
        } else {
            addMemberModifiers(env, cls.getModifiers().getFlags(), false);
        }
    }

    private void insideVariable(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        TreePath path = env.getPath();
        VariableTree var = (VariableTree) path.getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        boolean isLocal = path.getParentPath().getLeaf().getKind() != Tree.Kind.CLASS;
        Tree type = var.getType();
        int typePos = type.getKind() == Tree.Kind.ERRONEOUS && ((ErroneousTree) type).getErrorTrees().isEmpty() ? (int) sourcePositions.getEndPosition(root, type) : (int) sourcePositions.getStartPosition(root, type);
        if (offset <= typePos) {
            addMemberModifiers(env, var.getModifiers().getFlags(), isLocal);
            ModifiersTree mods = var.getModifiers();
            return;
        }
        Tree init = unwrapErrTree(var.getInitializer());
        if (init == null) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, (int) sourcePositions.getEndPosition(root, type), offset);
            if (last == null) {
                insideExpression(env, new TreePath(path, type));
            } else if (last.token().id() == JFXTokenId.EQ) {
                localResult(env);
                addValueKeywords(env);
            }
        } else {
            int pos = (int) sourcePositions.getStartPosition(root, init);
            if (pos < 0) {
                return;
            }
            if (offset <= pos) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, (int) sourcePositions.getEndPosition(root, type), offset);
                if (last == null) {
                    insideExpression(env, new TreePath(path, type));
                } else if (last.token().id() == JFXTokenId.EQ) {
                    localResult(env);
                    addValueKeywords(env);
                }
            } else {
                insideExpression(env, new TreePath(path, init));
            }
        }
    }

    private void insideMethod(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        String prefix = env.getPrefix();
        TreePath path = env.getPath();
        MethodTree mth = (MethodTree) path.getLeaf();
        CompilationController controller = env.getController();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int startPos = (int) sourcePositions.getStartPosition(root, mth);
        Tree retType = mth.getReturnType();
        if (retType == null) {
            int modPos = (int) sourcePositions.getEndPosition(root, mth.getModifiers());
            if (modPos > startPos) {
                startPos = modPos;
            }
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, startPos, offset);
            if (last == null) {
                addMemberModifiers(env, mth.getModifiers().getFlags(), false);
                return;
            }
        } else {
            if (offset <= sourcePositions.getStartPosition(root, retType)) {
                addMemberModifiers(env, mth.getModifiers().getFlags(), false);
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
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, startPos, offset);
            if (last != null && last.token().id() == JFXTokenId.COMMA) {
                if (queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE && mth.getBody() != null) {
                    controller.toPhase(Phase.RESOLVED);
                    JavafxcTrees trees = controller.getTrees();
                    for (ExpressionTree thr : mth.getThrows()) {
                        TypeMirror t = trees.getTypeMirror(new TreePath(path, thr));
                        if (thr == lastThr) {
                            break;
                        }
                    }
                }
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
                    addMemberModifiers(env, Collections.<Modifier>emptySet(), true);
                }
            }
        } else if (retType != null && headerText.trim().length() == 0) {
            insideExpression(env, new TreePath(path, retType));
        }
    }

    private void insideModifiers(JavaFXCompletionEnvironment env, TreePath modPath) throws IOException {
        int offset = env.getOffset();
        ModifiersTree mods = (ModifiersTree) modPath.getLeaf();
        Set<Modifier> m = EnumSet.noneOf(Modifier.class);
        final TokenSequence<?> idTokenSequence = env.getController().getTreeUtilities().tokensFor(mods, env.getSourcePositions());
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
        if (isTopLevelClass(parent, env.getRoot())) {
            addClassModifiers(env, m);
        } else if (parent.getKind() != Tree.Kind.VARIABLE || grandParent == null || grandParent.getKind() == Tree.Kind.CLASS) {
            addMemberModifiers(env, m, false);
        } else if (parent.getKind() == Tree.Kind.VARIABLE && grandParent.getKind() == Tree.Kind.METHOD) {
            addMemberModifiers(env, m, true);
        } else {
            localResult(env);
            addKeywordsForStatement(env);
        }
    }   
    
    private void insideFunctionBlock(JavaFXCompletionEnvironment env, com.sun.tools.javac.util.List<JCStatement> statements) throws IOException {
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int offset = env.getOffset();
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
                addKeyword(env, CATCH_KEYWORD, null, false);
                addKeyword(env, FINALLY_KEYWORD, null, false);
                if (((TryTree) last).getCatches().size() == 0) {
                    return;
                }
            }
        }
        localResult(env);
        addKeywordsForStatement(env);
    }
    private void insideBlock(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        JFXBlockExpression bl = (JFXBlockExpression) env.getPath().getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int blockPos = (int) sourcePositions.getStartPosition(root, bl);
        String text = env.getController().getText().substring(blockPos, offset);
        if (text.indexOf('{') < 0) {
            //NOI18N
            addMemberModifiers(env, Collections.singleton(STATIC), false);
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
                addKeyword(env, CATCH_KEYWORD, null, false);
                addKeyword(env, FINALLY_KEYWORD, null, false);
                if (((TryTree) last).getCatches().size() == 0) {
                    return;
                }
            }
        }
        localResult(env);
        addKeywordsForStatement(env);
    }

    private void insideMemberSelect(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        String prefix = env.getPrefix();
        TreePath path = env.getPath();
        MemberSelectTree fa = (MemberSelectTree) path.getLeaf();
        CompilationController controller = env.getController();
        CompilationUnitTree root = env.getRoot();
        SourcePositions sourcePositions = env.getSourcePositions();
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
                insideExpression(env, new TreePath(path, fa.getExpression()));
            }
            log("insideMemberSelect returning !afterDot");
            return;
        }
        if (openLtNum > 0) {
            switch (lastNonWhitespaceTokenId) {
                case QUES:
                    addKeyword(env, EXTENDS_KEYWORD, SPACE, false);
                    addKeyword(env, SUPER_KEYWORD, SPACE, false);
                    break;
                case LT:
                case COLON:
                case EXTENDS:
                case SUPER:
                    break;
            }
        }
        addLocalMembersAndVars(env);
    }

    private void insideMethodInvocation(JavaFXCompletionEnvironment env) throws IOException {
        TreePath path = env.getPath();
        MethodInvocationTree mi = (MethodInvocationTree) path.getLeaf();
        int offset = env.getOffset();
        TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, mi, offset);
        if (ts == null || (ts.token().id() != JFXTokenId.LPAREN && ts.token().id() != JFXTokenId.COMMA)) {
            SourcePositions sp = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            int lastTokenEndOffset = ts.offset() + ts.token().length();
            for (ExpressionTree arg : mi.getArguments()) {
                int pos = (int) sp.getEndPosition(root, arg);
                if (lastTokenEndOffset == pos) {
                    insideExpression(env, new TreePath(path, arg));
                    break;
                }
                if (offset <= pos) {
                    break;
                }
            }
            return;
        }
        String prefix = env.getPrefix();
        if (prefix == null || prefix.length() == 0) {
            addMethodArguments(env, mi);
        }
        addLocalMembersAndVars(env);
        addValueKeywords(env);
    }

    private void insideIf(JavaFXCompletionEnvironment env) throws IOException {
        IfTree iff = (IfTree) env.getPath().getLeaf();
        if (env.getSourcePositions().getEndPosition(env.getRoot(), iff.getCondition()) <= env.getOffset()) {
            localResult(env);
            addKeywordsForStatement(env);
        }
    }

    private void insideWhile(JavaFXCompletionEnvironment env) throws IOException {
        WhileLoopTree wlt = (WhileLoopTree) env.getPath().getLeaf();
        if (env.getSourcePositions().getEndPosition(env.getRoot(), wlt.getCondition()) <= env.getOffset()) {
            localResult(env);
            addKeywordsForStatement(env);
        }
    }

    private void insideFor(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        TreePath path = env.getPath();
        ForLoopTree fl = (ForLoopTree) path.getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
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
            addLocalFieldsAndVars(env);
        } else {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, lastTreePos, offset);
            if (last != null && last.token().id() == JFXTokenId.SEMI) {
                localResult(env);
                addValueKeywords(env);
            } else if (last != null && last.token().id() == JFXTokenId.RPAREN) {
                localResult(env);
                addKeywordsForStatement(env);
            } else {
                switch (lastTree.getKind()) {
                    case VARIABLE:
                        Tree var = ((VariableTree) lastTree).getInitializer();
                        if (var != null) {
                            insideExpression(env, new TreePath(new TreePath(path, lastTree), var));
                        }
                        break;
                    case EXPRESSION_STATEMENT:
                        Tree exp = unwrapErrTree(((ExpressionStatementTree) lastTree).getExpression());
                        if (exp != null) {
                            insideExpression(env, new TreePath(new TreePath(path, lastTree), exp));
                        }
                        break;
                    default:
                        insideExpression(env, new TreePath(path, lastTree));
                }
            }
        }
    }

    private void insideForEach(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        TreePath path = env.getPath();
        EnhancedForLoopTree efl = (EnhancedForLoopTree) path.getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        CompilationController controller = env.getController();
        if (sourcePositions.getStartPosition(root, efl.getExpression()) >= offset) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, (int) sourcePositions.getEndPosition(root, efl.getVariable()), offset);
            if (last != null && last.token().id() == JFXTokenId.COLON) {
                env.insideForEachExpressiion();
                addKeyword(env, NEW_KEYWORD, SPACE, false);
                localResult(env);
            }
            return;
        }
        TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, (int) sourcePositions.getEndPosition(root, efl.getExpression()), offset);
        if (last != null && last.token().id() == JFXTokenId.RPAREN) {
            addKeywordsForStatement(env);
        } else {
            env.insideForEachExpressiion();
            addKeyword(env, NEW_KEYWORD, SPACE, false);
        }
        localResult(env);
    }

    private void insideSwitch(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        String prefix = env.getPrefix();
        TreePath path = env.getPath();
        SwitchTree st = (SwitchTree) path.getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
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
                localResult(env);
                addKeywordsForStatement(env);
            } else {
                TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, st, offset);
                if (ts != null && ts.token().id() == JFXTokenId.LBRACE) {
                }
            }
        }
    }

    private void insideCase(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        TreePath path = env.getPath();
        CaseTree cst = (CaseTree) path.getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        CompilationController controller = env.getController();
        if (cst.getExpression() != null && ((sourcePositions.getStartPosition(root, cst.getExpression()) >= offset) || (cst.getExpression().getKind() == Tree.Kind.ERRONEOUS && ((ErroneousTree) cst.getExpression()).getErrorTrees().isEmpty() && sourcePositions.getEndPosition(root, cst.getExpression()) >= offset))) {
            TreePath path1 = path.getParentPath();
            if (path1.getLeaf().getKind() == Tree.Kind.SWITCH) {
                TypeMirror tm = controller.getTrees().getTypeMirror(new TreePath(path1, ((SwitchTree) path1.getLeaf()).getExpression()));
            }
        } else {
            TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, cst, offset);
            if (ts != null && ts.token().id() == JFXTokenId.COLON) {
                localResult(env);
                addKeywordsForStatement(env);
            }
        }
    }

    private void insideParens(JavaFXCompletionEnvironment env) throws IOException {
        TreePath path = env.getPath();
        ParenthesizedTree pa = (ParenthesizedTree) path.getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        Tree exp = unwrapErrTree(pa.getExpression());
        if (exp == null || env.getOffset() <= sourcePositions.getStartPosition(root, exp)) {
            HashSet<TypeElement> toExclude = new HashSet<TypeElement>();
            if (queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE && path.getParentPath().getLeaf().getKind() != Tree.Kind.SWITCH) {
                Set<? extends TypeMirror> smarts = env.getSmartTypes();
                if (smarts != null) {
                    for (TypeMirror smart : smarts) {
                        if (smart != null) {
                            if (smart.getKind() == TypeKind.DECLARED) {
                                for (DeclaredType subtype : getSubtypesOf(env, (DeclaredType) smart)) {
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
            addLocalMembersAndVars(env);
            addValueKeywords(env);
        } else {
            insideExpression(env, new TreePath(path, exp));
        }
    }

    private void insideTypeCheck(JavaFXCompletionEnvironment env) throws IOException {
        InstanceOfTree iot = (InstanceOfTree) env.getPath().getLeaf();
        TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, iot, env.getOffset());
    }

    private void insideArrayAccess(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        ArrayAccessTree aat = (ArrayAccessTree) env.getPath().getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int aaTextStart = (int) sourcePositions.getEndPosition(root, aat.getExpression());
        if (aaTextStart != Diagnostic.NOPOS) {
            Tree expr = unwrapErrTree(aat.getIndex());
            if (expr == null || offset <= (int) sourcePositions.getStartPosition(root, expr)) {
                String aatText = env.getController().getText().substring(aaTextStart, offset);
                int bPos = aatText.indexOf('[');
                //NOI18N
                if (bPos > -1) {
                    localResult(env);
                    addValueKeywords(env);
                }
            }
        }
    }

    private void insideNewArray(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        TreePath path = env.getPath();
        NewArrayTree nat = (NewArrayTree) path.getLeaf();
        if (nat.getInitializers() != null) {
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
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
                TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, lastPos, offset);
                if (ts != null && ts.token().id() == JFXTokenId.COMMA) {
                    TreePath parentPath = path.getParentPath();
                    TreePath gparentPath = parentPath.getParentPath();
                    if (parentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION || gparentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION) {
                        addLocalConstantsAndTypes(env);
                    } else {
                        localResult(env);
                        addValueKeywords(env);
                    }
                }
                return;
            }
        }
        TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, nat, offset);
        switch (ts.token().id()) {
            case LBRACKET:
            case LBRACE:
                TreePath parentPath = path.getParentPath();
                TreePath gparentPath = parentPath.getParentPath();
                if (parentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION || gparentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION) {
                    addLocalConstantsAndTypes(env);
                } else {
                    localResult(env);
                    addValueKeywords(env);
                }
                break;
            case RBRACKET:
                if (nat.getDimensions().size() > 0) {
                    insideExpression(env, path);
                }
                break;
        }
    }

    private void insideAssignment(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        TreePath path = env.getPath();
        AssignmentTree as = (AssignmentTree) path.getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int asTextStart = (int) sourcePositions.getEndPosition(root, as.getVariable());
        if (asTextStart != Diagnostic.NOPOS) {
            Tree expr = unwrapErrTree(as.getExpression());
            if (expr == null || offset <= (int) sourcePositions.getStartPosition(root, expr)) {
                String asText = env.getController().getText().substring(asTextStart, offset);
                int eqPos = asText.indexOf('=');
                if (eqPos > -1) {
                    if (path.getParentPath().getLeaf().getKind() == Tree.Kind.ANNOTATION) {
                        addLocalConstantsAndTypes(env);
                    } else {
                        localResult(env);
                        addValueKeywords(env);
                    }
                }
            } else {
                insideExpression(env, new TreePath(path, expr));
            }
        }
    }

    private void insideCompoundAssignment(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        CompoundAssignmentTree cat = (CompoundAssignmentTree) env.getPath().getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int catTextStart = (int) sourcePositions.getEndPosition(root, cat.getVariable());
        if (catTextStart != Diagnostic.NOPOS) {
            Tree expr = unwrapErrTree(cat.getExpression());
            if (expr == null || offset <= (int) sourcePositions.getStartPosition(root, expr)) {
                String catText = env.getController().getText().substring(catTextStart, offset);
                int eqPos = catText.indexOf('=');
                //NOI18N
                if (eqPos > -1) {
                    localResult(env);
                    addValueKeywords(env);
                }
            }
        }
    }

    private void insideBinaryTree(JavaFXCompletionEnvironment env) throws IOException {
        int offset = env.getOffset();
        BinaryTree bi = (BinaryTree) env.getPath().getLeaf();
        SourcePositions sourcePositions = env.getSourcePositions();
        CompilationUnitTree root = env.getRoot();
        int pos = (int) sourcePositions.getEndPosition(root, bi.getRightOperand());
        if (pos != Diagnostic.NOPOS && pos < offset) {
            return;
        }
        pos = (int) sourcePositions.getEndPosition(root, bi.getLeftOperand());
        if (pos != Diagnostic.NOPOS) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, pos, offset);
            if (last != null) {
                localResult(env);
                addValueKeywords(env);
            }
        }
    }

    private void insideExpressionStatement(JavaFXCompletionEnvironment env) throws IOException {
        TreePath path = env.getPath();
        ExpressionStatementTree est = (ExpressionStatementTree) path.getLeaf();
        CompilationController controller = env.getController();
        Tree t = est.getExpression();
        if (t.getKind() == Tree.Kind.ERRONEOUS) {
            Iterator<? extends Tree> it = ((ErroneousTree) t).getErrorTrees().iterator();
            if (it.hasNext()) {
                t = it.next();
            } else {
                TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
                ts.move((int) env.getSourcePositions().getStartPosition(env.getRoot(), est));
                ts.movePrevious();
                switch (ts.token().id()) {
                    case FOR:
                    case IF:
                    case WHILE:
                        return;
                }
                localResult(env);
                Tree parentTree = path.getParentPath().getLeaf();
                switch (parentTree.getKind()) {
                    case FOR_LOOP:env.getPath().getLeaf();
                        if (((ForLoopTree) parentTree).getStatement() == est) {
                            addKeywordsForStatement(env);
                        } else {
                            addValueKeywords(env);
                        }
                        break;
                    case ENHANCED_FOR_LOOP:
                        if (((EnhancedForLoopTree) parentTree).getStatement() == est) {
                            addKeywordsForStatement(env);
                        } else {
                            addValueKeywords(env);
                        }
                        break;
                    case VARIABLE:
                        addValueKeywords(env);
                        break;
                    default:
                        addKeywordsForStatement(env);
                        break;
                }
                return;
            }
        }
        TreePath tPath = new TreePath(path, t);
        if (t.getKind() == Tree.Kind.MODIFIERS) {
            insideModifiers(env, tPath);
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
            insideExpression(env, tPath);
        }
    }

    private void insideExpression(JavaFXCompletionEnvironment env, TreePath exPath) throws IOException {
        int offset = env.getOffset();
        String prefix = env.getPrefix();
        Tree et = exPath.getLeaf();
        Tree parent = exPath.getParentPath().getLeaf();
        CompilationController controller = env.getController();
        int endPos = (int) env.getSourcePositions().getEndPosition(env.getRoot(), et);
        if (endPos != Diagnostic.NOPOS && endPos < offset) {
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, endPos, offset);
            if (last != null) {
                return;
            }
        }
    }

    private void localResult(JavaFXCompletionEnvironment env) throws IOException {
        addLocalMembersAndVars(env);
    }

    private void addLocalConstantsAndTypes(final JavaFXCompletionEnvironment env) throws IOException {
        log("addLocalConstantsAndTypes: " + env.getPrefix());
    }

    private void addLocalMembersAndVars(final JavaFXCompletionEnvironment env) throws IOException {
        log("addLocalMembersAndVars: " + env.getPrefix());
    }

    private void addLocalFieldsAndVars(final JavaFXCompletionEnvironment env) throws IOException {
        log("addLocalFieldsAndVars: " + env.getPrefix());
    }

    private void addPackages(JavaFXCompletionEnvironment env, String fqnPrefix) {
    }

    private List<DeclaredType> getSubtypesOf(JavaFXCompletionEnvironment env, DeclaredType baseType) throws IOException {
        return Collections.emptyList();
    }

    private void addMethodArguments(JavaFXCompletionEnvironment env, MethodInvocationTree mit) throws IOException {
    }

    private void addKeyword(JavaFXCompletionEnvironment env, String kw, String postfix, boolean smartType) {
        if (JavaFXCompletionProvider.startsWith(kw, env.getPrefix())) {
            results.add(JavaFXCompletionItem.createKeywordItem(kw, postfix, anchorOffset, smartType));
        }
    }

    private void addKeywordsForCU(JavaFXCompletionEnvironment env) {
        List<String> kws = new ArrayList<String>();
        int offset = env.getOffset();
        String prefix = env.getPrefix();
        CompilationUnitTree cu = env.getRoot();
        SourcePositions sourcePositions = env.getSourcePositions();
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
                results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
        }
    }

    private void addKeywordsForClassBody(JavaFXCompletionEnvironment env) {
        String prefix = env.getPrefix();
        for (String kw : CLASS_BODY_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
        }
    }

    private void addKeywordsForStatement(JavaFXCompletionEnvironment env) {
        String prefix = env.getPrefix();
        for (String kw : STATEMENT_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                results.add(JavaFXCompletionItem.createKeywordItem(kw, null, anchorOffset, false));
            }
        }
        for (String kw : STATEMENT_SPACE_KEYWORDS) {
            if (JavaFXCompletionProvider.startsWith(kw, prefix)) {
                results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
        }
        if (JavaFXCompletionProvider.startsWith(RETURN_KEYWORD, prefix)) {
            TreePath mth = JavaFXCompletionProvider.getPathElementOfKind(Tree.Kind.METHOD, env.getPath());
            String postfix = SPACE;
            if (mth != null) {
                Tree rt = ((MethodTree) mth.getLeaf()).getReturnType();
                if (rt == null || rt.getKind() == Tree.Kind.PRIMITIVE_TYPE && ((PrimitiveTypeTree) rt).getPrimitiveTypeKind() == TypeKind.VOID) {
                    postfix = SEMI;
                }
            }
            results.add(JavaFXCompletionItem.createKeywordItem(RETURN_KEYWORD, postfix, anchorOffset, false));
        }
        TreePath tp = env.getPath();
        while (tp != null) {
            switch (tp.getLeaf().getKind()) {
                case DO_WHILE_LOOP:
                case ENHANCED_FOR_LOOP:
                case FOR_LOOP:
                case WHILE_LOOP:
                    if (JavaFXCompletionProvider.startsWith(CONTINUE_KEYWORD, prefix)) {
                        results.add(JavaFXCompletionItem.createKeywordItem(CONTINUE_KEYWORD, SEMI, anchorOffset, false));
                    }
                case SWITCH:
                    if (JavaFXCompletionProvider.startsWith(BREAK_KEYWORD, prefix)) {
                        results.add(JavaFXCompletionItem.createKeywordItem(BREAK_KEYWORD, SEMI, anchorOffset, false));
                    }
                    break;
            }
            tp = tp.getParentPath();
        }
    }

    private void addValueKeywords(JavaFXCompletionEnvironment env) throws IOException {
        String prefix = env.getPrefix();
        boolean smartType = false;
        if (queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE) {
            Set<? extends TypeMirror> smartTypes = env.getSmartTypes();
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
            results.add(JavaFXCompletionItem.createKeywordItem(FALSE_KEYWORD, null, anchorOffset, smartType));
        }
        if (JavaFXCompletionProvider.startsWith(TRUE_KEYWORD, prefix)) {
            results.add(JavaFXCompletionItem.createKeywordItem(TRUE_KEYWORD, null, anchorOffset, smartType));
        }
        if (JavaFXCompletionProvider.startsWith(NULL_KEYWORD, prefix)) {
            results.add(JavaFXCompletionItem.createKeywordItem(NULL_KEYWORD, null, anchorOffset, false));
        }
        if (JavaFXCompletionProvider.startsWith(NEW_KEYWORD, prefix)) {
            results.add(JavaFXCompletionItem.createKeywordItem(NEW_KEYWORD, SPACE, anchorOffset, false));
        }
        if (JavaFXCompletionProvider.startsWith(BIND_KEYWORD, prefix)) {
            results.add(JavaFXCompletionItem.createKeywordItem(BIND_KEYWORD, SPACE, anchorOffset, false));
        }
    }

    private void addClassModifiers(JavaFXCompletionEnvironment env, Set<Modifier> modifiers) {
        String prefix = env.getPrefix();
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
                results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
        }
    }

    private void addMemberModifiers(JavaFXCompletionEnvironment env, Set<Modifier> modifiers, boolean isLocal) {
        String prefix = env.getPrefix();
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
                results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
        }
    }

    private boolean isTopLevelClass(Tree tree, CompilationUnitTree root) {
        if (tree.getKind() == Tree.Kind.CLASS || (tree.getKind() == Tree.Kind.EXPRESSION_STATEMENT && ((ExpressionStatementTree) tree).getExpression().getKind() == Tree.Kind.ERRONEOUS)) {
            for (Tree t : root.getTypeDecls()) {
                if (tree == t) {
                    return true;
                }
            }
        }
        return false;
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

    private TokenSequence<JFXTokenId> findFirstNonWhitespaceToken(JavaFXCompletionEnvironment env, Tree tree, int position) {
        int startPos = (int) env.getSourcePositions().getStartPosition(env.getRoot(), tree);
        return findFirstNonWhitespaceToken(env, startPos, position);
    }

    private TokenSequence<JFXTokenId> findFirstNonWhitespaceToken(JavaFXCompletionEnvironment env, int startPos, int endPos) {
        TokenSequence<JFXTokenId> ts = env.getController().getTokenHierarchy().tokenSequence(JFXTokenId.language());
        ts.move(startPos);
        ts = nextNonWhitespaceToken(ts);
        if (ts == null || ts.offset() >= endPos) {
            return null;
        }
        return ts;
    }

    private TokenSequence<JFXTokenId> nextNonWhitespaceToken(TokenSequence<JFXTokenId> ts) {
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

    private TokenSequence<JFXTokenId> findLastNonWhitespaceToken(JavaFXCompletionEnvironment env, Tree tree, int position) {
        int startPos = (int) env.getSourcePositions().getStartPosition(env.getRoot(), tree);
        return findLastNonWhitespaceToken(env, startPos, position);
    }

    private TokenSequence<JFXTokenId> findLastNonWhitespaceToken(JavaFXCompletionEnvironment env, int startPos, int endPos) {
        TokenSequence<JFXTokenId> ts = env.getController().getTokenHierarchy().tokenSequence(JFXTokenId.language());
        ts.move(endPos);
        ts = previousNonWhitespaceToken(ts);
        if (ts == null || ts.offset() < startPos) {
            return null;
        }
        return ts;
    }

    private TokenSequence<JFXTokenId> previousNonWhitespaceToken(TokenSequence<JFXTokenId> ts) {
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

    private Tree unwrapErrTree(Tree tree) {
        if (tree != null && tree.getKind() == Tree.Kind.ERRONEOUS) {
            Iterator<? extends Tree> it = ((ErroneousTree) tree).getErrorTrees().iterator();
            tree = it.hasNext() ? it.next() : null;
        }
        return tree;
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
        return new JavaFXCompletionEnvironment(offset, prefix, controller, path, controller.getTrees().getSourcePositions());
    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
