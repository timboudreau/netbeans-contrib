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
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
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
import org.netbeans.modules.javafx.editor.completion.environment.*;
import org.netbeans.spi.editor.completion.CompletionDocumentation;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public final class JavaFXCompletionQuery extends AsyncCompletionQuery implements Task<CompilationController> {
    
    private static final Logger logger = Logger.getLogger(JavaFXCompletionProvider.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    public static final String ERROR = "<error>";
    public static final String INIT = "<init>";
    public static final String SPACE = " ";
    public static final String COLON = ":";
    public static final String SEMI = ";";
    public static final String EMPTY = "";
    public static final String ABSTRACT_KEYWORD = "abstract";
    public static final String AFTER_KEYWORD = "after";
    public static final String AND_KEYWORD = "and";
    public static final String AS_KEYWORD = "as";
    public static final String ASSERT_KEYWORD = "assert";
    public static final String ATTRIBUTE_KEYWORD = "attribute";
    public static final String BEFORE_KEYWORD = "before";
    public static final String BIND_KEYWORD = "bind";
    public static final String BOUND_KEYWORD = "bound";
    public static final String BREAK_KEYWORD = "break";
    public static final String CATCH_KEYWORD = "catch";
    public static final String CLASS_KEYWORD = "class";
    public static final String CONTINUE_KEYWORD = "continue";
    public static final String DELETE_KEYWORD = "delete";
    public static final String ELSE_KEYWORD = "else";
    public static final String EXCLUSIVE_KEYWORD = "exclusive";
    public static final String EXTENDS_KEYWORD = "extends";
    public static final String FALSE_KEYWORD = "false";
    public static final String FINALLY_KEYWORD = "finally";
    public static final String FIRST_KEYWORD = "first";
    public static final String FOR_KEYWORD = "for";
    public static final String FROM_KEYWORD = "from";
    public static final String FUNCTION_KEYWORD = "function";
    public static final String IF_KEYWORD = "if";
    public static final String IMPORT_KEYWORD = "import";
    public static final String INDEXOF_KEYWORD = "indexof";
    public static final String INIT_KEYWORD = "init";
    public static final String IN_KEYWORD = "in";
    public static final String INSERT_KEYWORD = "insert";
    public static final String INSTANCEOF_KEYWORD = "instanceof";
    public static final String INTO_KEYWORD = "into";
    public static final String INVERSE_KEYWORD = "inverse";
    public static final String LAST_KEYWORD = "last";
    public static final String LAZY_KEYWORD = "lazy";
    public static final String LET_KEYWORD = "let";
    public static final String NEW_KEYWORD = "new";
    public static final String NOT_KEYWORD = "not";
    public static final String NULL_KEYWORD = "null";
    public static final String ON_KEYWORD = "on";
    public static final String OR_KEYWORD = "or";
    public static final String OVERRIDE_KEYWORD = "override";
    public static final String PACKAGE_KEYWORD = "package";
    public static final String PRIVATE_KEYWORD = "private";
    public static final String PROTECTED_KEYWORD = "protected";
    public static final String PUBLIC_KEYWORD = "public";
    public static final String READONLY_KEYWORD = "readonly";
    public static final String REPLACE_KEYWORD = "replace";
    public static final String RETURN_KEYWORD = "return";
    public static final String REVERSE_KEYWORD = "reverse";
    public static final String SIZEOF_KEYWORD = "sizeof";
    public static final String STATIC_KEYWORD = "static";
    public static final String STEP_KEYWORD = "step";
    public static final String SUPER_KEYWORD = "super";
    public static final String THEN_KEYWORD = "then";
    public static final String THIS_KEYWORD = "this";
    public static final String THROW_KEYWORD = "throw";
    public static final String TRANSIENT_KEYWORD = "transient";
    public static final String TRUE_KEYWORD = "true";
    public static final String TRY_KEYWORD = "try";
    public static final String TWEEN_KEYWORD = "tween";
    public static final String TYPEOF_KEYWORD = "typeof";
    public static final String VAR_KEYWORD = "var";
    public static final String WHERE_KEYWORD = "where";
    public static final String WHILE_KEYWORD = "while";
    public static final String WITH_KEYWORD = "with";
    
    public static final String[] STATEMENT_KEYWORDS = new String[]{
        FOR_KEYWORD,
        TRY_KEYWORD, 
        WHILE_KEYWORD
    };
    public static final String[] STATEMENT_SPACE_KEYWORDS = new String[]{
        INSERT_KEYWORD,
        NEW_KEYWORD,
        THROW_KEYWORD,
        VAR_KEYWORD
    };
    public static final String[] CLASS_BODY_KEYWORDS = new String[]{
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
    public int queryType;
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
        
        // make sure the init method was called
        if (env.query != this) {
            throw new IllegalStateException("init method not called before resolveCompletion");
        }
        
        env.inside(env.getPath().getLeaf());
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
        Tree t = path.getLeaf();
        JavaFXCompletionEnvironment result = null;
        if (t instanceof JavaFXTree && t.getKind() == Tree.Kind.OTHER) {
            JavaFXTree jfxt = (JavaFXTree) t;
            JavaFXKind k = jfxt.getJavaFXKind();
            log("JavaFXKind: " + k);
            switch (k) {
                case BIND_EXPRESSION:
                    break;
                case BLOCK_EXPRESSION:
                    result = new BlockExpressionEnvironment();
                    break;
                case CLASS_DECLARATION:
                    result = new ClassDeclarationEnvironment();
                    break;
                case FOR_EXPRESSION:
                    result = new ForExpressionEnvironment();
                    break;
                case FOR_EXPRESSION_IN_CLAUSE:
                    result = new ForExpressionInClauseEnvironment();
                    break;
                case FUNCTION_DEFINITION:
                    result = new FunctionDefinitionEnvironment();
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
                    result = new ObjectLiteralPartEnvironment();
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
                    //result = new ClassDeclarationEnvironment();
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
                    result = new CompilationUnitEnvironment();
                    break;
                case IMPORT:
                    result = new ImportTreeEnvironment();
                    break;
                case CLASS:
                    result = new ClassTreeEnvironment();
                    break;
                case VARIABLE:
                    result = new VariableTreeEnvironment();
                    break;
                case METHOD:
                    result = new MethodTreeEnvironment();
                    break;
                case MODIFIERS:
                    result = new ModifiersTreeEnvironment();
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
                    result = new MemberSelectTreeEnvironment();
                    break;
                case METHOD_INVOCATION:
                    result = new MethodInvocationTreeEnvironment();
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
                    result = new IfTreeEnvironment();
                    break;
                case WHILE_LOOP:
                    result = new WhileLoopTreeEnvironment();
                    break;
                case FOR_LOOP:
                    break;
                case SWITCH:
                    result = new SwitchTreeEnvironment();
                    break;
                case CASE:
                    result = new CaseTreeEnvironment();
                    break;
                case PARENTHESIZED:
                    result = new ParenthesizedTreeEnvironment();
                    break;
                case TYPE_CAST:
                    break;
                case INSTANCE_OF:
                    result = new InstanceOfTreeEnvironment();
                    break;
                case ARRAY_ACCESS:
                    result = new ArrayAccessTreeEnvironment();
                    break;
                case NEW_ARRAY:
                    result = new NewArrayTreeEnvironment();
                    break;
                case ASSIGNMENT:
                    result = new AssignmentTreeEnvironment();
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
                    result = new CompoundAssignmentTreeEnvironment();
                    break;
                case PREFIX_INCREMENT:
                case PREFIX_DECREMENT:
                case UNARY_PLUS:
                case UNARY_MINUS:
                case BITWISE_COMPLEMENT:
                case LOGICAL_COMPLEMENT:
                    // TODO: ???
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
                    result = new BinaryTreeEnvironment();
                    break;
                case CONDITIONAL_EXPRESSION:
                    break;
                case EXPRESSION_STATEMENT:
                    // ???
                    break;
            }
        }
        if (result == null) {
            result = new JavaFXCompletionEnvironment();
        }
        result.init(offset, prefix, controller, path, controller.getTrees().getSourcePositions(), this);
        return result;
    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
