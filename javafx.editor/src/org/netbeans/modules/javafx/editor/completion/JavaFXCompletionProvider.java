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
import com.sun.source.tree.*;
import com.sun.source.util.*;

import com.sun.source.util.TaskEvent.Kind;
import com.sun.tools.javafx.api.JavafxcTrees;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.lang.model.element.*;
import static javax.lang.model.element.Modifier.*;
import javax.lang.model.type.*;
import javax.swing.JToolTip;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.tools.Diagnostic;

import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.*;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.spi.editor.completion.*;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Dusan Balek
 */
public class JavaFXCompletionProvider implements CompletionProvider {
    
    private static final boolean autoMode = Boolean.getBoolean("org.netbeans.modules.editor.java.completionAutoMode");

    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        if (".".equals(typedText) || (autoMode && JavaFXCompletionQuery.isJavaIdentifierPart(typedText))) {
            if (isJavaFXContext(component, component.getSelectionStart() - 1))
                return COMPLETION_QUERY_TYPE;
        }
        return 0;
    }
    private static final String ERROR = "<error>"; //NOI18N
    public static boolean startsWith(String theString, String prefix) {
        if (theString == null || theString.length() == 0 || ERROR.equals(theString))
            return false;
        if (prefix == null || prefix.length() == 0)
            return true;
        return theString.startsWith(prefix);
    }
    public static TreePath getPathElementOfKind(Tree.Kind kind, TreePath path) {
        return getPathElementOfKind(EnumSet.of(kind), path);
    }
    
    public static TreePath getPathElementOfKind(EnumSet<Tree.Kind> kinds, TreePath path) {
        while (path != null) {
            if (kinds.contains(path.getLeaf().getKind()))
                return path;
            path = path.getParentPath();
        }
        return null;        
    }        

    public static boolean isJavaFXContext(final JTextComponent component, final int offset) {
        Document doc = component.getDocument();
        if (doc instanceof AbstractDocument) {
            ((AbstractDocument)doc).readLock();
        }
        try {
            TokenSequence<JFXTokenId> ts = getJavaFXTokenSequence(TokenHierarchy.get(doc), offset);
            if (ts == null) {
                return false;
            }
            if (!ts.moveNext() && !ts.movePrevious()) {
                return true;
            }
            if (offset == ts.offset()) {
                return true;
            }
            switch (ts.token().id()) {
                case FLOATING_POINT_LITERAL:
                    if (ts.token().text().charAt(0) == '.') {
                        break;
                    }
                case DOC_COMMENT:
                case STRING_LITERAL:
                case LINE_COMMENT:
                case COMMENT:
                    return false;
            }
            return true;
        } finally {
            if (doc instanceof AbstractDocument) {
                ((AbstractDocument) doc).readUnlock();
            }
        }
    }
     public static TokenSequence<JFXTokenId> getJavaFXTokenSequence(final TokenHierarchy hierarchy, final int offset) {
        if (hierarchy != null) {
            TokenSequence<?> ts = hierarchy.tokenSequence();
            while(ts != null && (offset == 0 || ts.moveNext())) {
                ts.move(offset);
                if (ts.language() == JFXTokenId.language())
                    return (TokenSequence<JFXTokenId>)ts;
                if (!ts.moveNext() && !ts.movePrevious())
                    return null;
                ts = ts.embedded();
            }
        }
        return null;
    }
    public CompletionTask createTask(int type, JTextComponent component) {
        if ((type & COMPLETION_QUERY_TYPE) != 0 || type == TOOLTIP_QUERY_TYPE || type == DOCUMENTATION_QUERY_TYPE)
            return new AsyncCompletionTask(new JavaFXCompletionQuery(type, component.getSelectionStart(), true), component);
        return null;
    }
    
    public static List<? extends CompletionItem> query(JavaFXSource source, int queryType, int offset, int substitutionOffset) throws IOException {
        assert source != null;
        assert (queryType & COMPLETION_QUERY_TYPE) != 0;
        JavaFXCompletionQuery query = new JavaFXCompletionQuery(queryType, offset, false);
        source.runUserActionTask(query, false);
        if (offset != substitutionOffset) {
            for (JavaFXCompletionItem jci : query.results) {
                jci.substitutionOffset += (substitutionOffset - offset);
            }
        }
        return query.results;
    }
    
    static final class JavaFXCompletionQuery extends AsyncCompletionQuery implements Task<CompilationController> {
        
        private static final String ERROR = "<error>"; //NOI18N
        private static final String INIT = "<init>"; //NOI18N
        private static final String SPACE = " "; //NOI18N
        private static final String COLON = ":"; //NOI18N
        private static final String SEMI = ";"; //NOI18N
        private static final String EMPTY = ""; //NOI18N
        
        private static final String ABSTRACT_KEYWORD = "abstract"; //NOI18N
        private static final String AFTER_KEYWORD = "after"; //NOI18N
        private static final String AND_KEYWORD = "and"; //NOI18N
        private static final String AS_KEYWORD = "as"; //NOI18N
        private static final String ASSERT_KEYWORD = "assert"; //NOI18N
        private static final String ATTRIBUTE_KEYWORD = "attribute"; //NOI18N
        private static final String BEFORE_KEYWORD = "before"; //NOI18N
        private static final String BIND_KEYWORD = "bind"; //NOI18N
        private static final String BOUND_KEYWORD = "bound"; //NOI18N
        private static final String BREAK_KEYWORD = "break"; //NOI18N
        private static final String CATCH_KEYWORD = "catch"; //NOI18N
        private static final String CLASS_KEYWORD = "class"; //NOI18N
        private static final String CONTINUE_KEYWORD = "continue"; //NOI18N
        private static final String DELETE_KEYWORD = "delete"; //NOI18N
        private static final String ELSE_KEYWORD = "else"; //NOI18N
        private static final String EXCLUSIVE_KEYWORD = "exclusive"; //NOI18N
        private static final String EXTENDS_KEYWORD = "extends"; //NOI18N
        private static final String FALSE_KEYWORD = "false"; //NOI18N
        private static final String FINALLY_KEYWORD = "finally"; //NOI18N
        private static final String FIRST_KEYWORD = "first"; //NOI18N
        private static final String FOR_KEYWORD = "for"; //NOI18N
        private static final String FROM_KEYWORD = "from"; //NOI18N
        private static final String FUNCTION_KEYWORD = "function"; //NOI18N
        private static final String IF_KEYWORD = "if"; //NOI18N
        private static final String IMPORT_KEYWORD = "import"; //NOI18N
        private static final String INDEXOF_KEYWORD = "indexof"; //NOI18N
        private static final String INIT_KEYWORD = "init"; //NOI18N
        private static final String IN_KEYWORD = "in"; //NOI18N
        private static final String INSERT_KEYWORD = "insert"; //NOI18N
        private static final String INSTANCEOF_KEYWORD = "instanceof"; //NOI18N
        private static final String INTO_KEYWORD = "into"; //NOI18N
        private static final String INVERSE_KEYWORD = "inverse"; //NOI18N
        private static final String LAST_KEYWORD = "last"; //NOI18N
        private static final String LAZY_KEYWORD = "lazy"; //NOI18N
        private static final String LET_KEYWORD = "let"; //NOI18N
        private static final String NEW_KEYWORD = "new"; //NOI18N
        private static final String NOT_KEYWORD = "not"; //NOI18N
        private static final String NULL_KEYWORD = "null"; //NOI18N
        private static final String ON_KEYWORD = "on"; //NOI18N
        private static final String OR_KEYWORD = "or"; //NOI18N
        private static final String OVERRIDE_KEYWORD = "override"; //NOI18N  
        private static final String PACKAGE_KEYWORD = "package"; //NOI18N
        private static final String PRIVATE_KEYWORD = "private"; //NOI18N
        private static final String PROTECTED_KEYWORD = "protected"; //NOI18N
        private static final String PUBLIC_KEYWORD = "public"; //NOI18N
        private static final String READONLY_KEYWORD = "readonly"; //NOI18N
        private static final String REPLACE_KEYWORD = "replace"; //NOI18N
        private static final String RETURN_KEYWORD = "return"; //NOI18N
        private static final String REVERSE_KEYWORD = "reverse"; //NOI18N
        private static final String SIZEOF_KEYWORD = "sizeof"; //NOI18N
        private static final String STATIC_KEYWORD = "static"; //NOI18N
        private static final String STEP_KEYWORD = "step"; //NOI18N
        private static final String SUPER_KEYWORD = "super"; //NOI18N
        private static final String THEN_KEYWORD = "then"; //NOI18N
        private static final String THIS_KEYWORD = "this"; //NOI18N
        private static final String THROW_KEYWORD = "throw"; //NOI18N
        private static final String TRANSIENT_KEYWORD = "transient"; //NOI18N
        private static final String TRUE_KEYWORD = "true"; //NOI18N
        private static final String TRY_KEYWORD = "try"; //NOI18N
        private static final String TWEEN_KEYWORD = "tween"; //NOI18N
        private static final String TYPEOF_KEYWORD = "typeof"; //NOI18N
        private static final String VAR_KEYWORD = "var"; //NOI18N
        private static final String WHERE_KEYWORD = "where"; //NOI18N
        private static final String WHILE_KEYWORD = "while"; //NOI18N
        private static final String WITH_KEYWORD = "with"; //NOI18N
        
        private static final String JAVA_LANG_OBJECT = "java.lang.Object"; //NOI18N
        private static final String JAVA_LANG_ITERABLE = "java.lang.Iterable"; //NOI18N

        private static final String[] STATEMENT_KEYWORDS = new String[] {
            FOR_KEYWORD, TRY_KEYWORD, WHILE_KEYWORD
        };
        
        private static final String[] STATEMENT_SPACE_KEYWORDS = new String[] {
            ASSERT_KEYWORD, NEW_KEYWORD, THROW_KEYWORD
        };
        
        private static final String[] BLOCK_KEYWORDS = new String[] {
            ASSERT_KEYWORD, CLASS_KEYWORD, NEW_KEYWORD, THROW_KEYWORD
        };

        private static final String[] CLASS_BODY_KEYWORDS = new String[] {
            ABSTRACT_KEYWORD, ATTRIBUTE_KEYWORD,
            CLASS_KEYWORD, PRIVATE_KEYWORD, PROTECTED_KEYWORD,
            PUBLIC_KEYWORD, STATIC_KEYWORD, TRANSIENT_KEYWORD
        };
        
        private List<JavaFXCompletionItem> results;
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
        
        private JavaFXCompletionQuery(int queryType, int caretOffset, boolean hasTask) {
            this.queryType = queryType;
            this.caretOffset = caretOffset;
            this.hasTask = hasTask;
        }

        @Override
        protected void preQueryUpdate(JTextComponent component) {
            int newCaretOffset = component.getSelectionStart();
            if (newCaretOffset >= caretOffset) {
                try {
                    if (isJavaIdentifierPart(component.getDocument().getText(caretOffset, newCaretOffset - caretOffset)))
                        return;
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
                if (queryType == TOOLTIP_QUERY_TYPE || isJavaFXContext(component, caretOffset)) {
                    results = null;
                    documentation = null;
                    toolTip = null;
                    anchorOffset = -1;
                    JavaFXSource js = JavaFXSource.forDocument(doc);
                    if (js != null) {
                        Future<Void> f = js.runWhenScanFinished(this, true);
                        if (!f.isDone()) {
                            component.putClientProperty("completion-active", Boolean.FALSE); //NOI18N
                            resultSet.setWaitText(NbBundle.getMessage(JavaFXCompletionProvider.class, "scanning-in-progress")); //NOI18N
                            f.get();
                        }
                        if ((queryType & COMPLETION_QUERY_TYPE) != 0) {
                            if (results != null)
                                resultSet.addAllItems(results);
                            resultSet.setHasAdditionalItems(hasAdditionalItems);
                            if (hasAdditionalItems)
                                resultSet.setHasAdditionalItemsText(NbBundle.getMessage(JavaFXCompletionProvider.class, "JCP-imported-items")); //NOI18N
                        } else if (queryType == TOOLTIP_QUERY_TYPE) {
                            if (toolTip != null)
                                resultSet.setToolTip(toolTip);
                        } else if (queryType == DOCUMENTATION_QUERY_TYPE) {
                            if (documentation != null)
                                resultSet.setDocumentation(documentation);
                        }
                        if (anchorOffset > -1)
                            resultSet.setAnchorOffset(anchorOffset);
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
            if ((queryType & COMPLETION_QUERY_TYPE) != 0) {
                int offset = Math.min(anchorOffset, caretOffset);
                if (offset > -1) {
                    if (newOffset < offset)
                        return true;
                    if (newOffset >= caretOffset) {
                        try {
                            String prefix = component.getDocument().getText(offset, newOffset - offset);
                            filterPrefix = isJavaIdentifierPart(prefix) ? prefix : null;
                            if (filterPrefix != null && filterPrefix.length() == 0)
                                anchorOffset = newOffset;
                        } catch (BadLocationException e) {}
                        return true;
                    }
                }
                return false;
            } else if (queryType == TOOLTIP_QUERY_TYPE) {
                try {
                    if (newOffset == caretOffset)
                        filterPrefix = EMPTY;
                    else if (newOffset - caretOffset > 0)
                        filterPrefix = component.getDocument().getText(caretOffset, newOffset - caretOffset);
                    else if (newOffset - caretOffset < 0)
                        filterPrefix = newOffset > toolTipOffset ? component.getDocument().getText(newOffset, caretOffset - newOffset) : null;
                } catch (BadLocationException ex) {}
                return (filterPrefix != null && filterPrefix.indexOf(',') == -1 && filterPrefix.indexOf('(') == -1 && filterPrefix.indexOf(')') == -1); // NOI18N
            }
            return false;
        }
        
        @Override
        protected void filter(CompletionResultSet resultSet) {
            try {
                if ((queryType & COMPLETION_QUERY_TYPE) != 0) {
                    if (results != null) {
                        if (filterPrefix != null) {
                            resultSet.addAllItems(getFilteredData(results, filterPrefix));
                            resultSet.setHasAdditionalItems(hasAdditionalItems);
                        } else {
                            Completion.get().hideDocumentation();
                            Completion.get().hideCompletion();
                        }
                    }
                } else if (queryType == TOOLTIP_QUERY_TYPE) {
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
                if ((queryType & COMPLETION_QUERY_TYPE) != 0) {
                    if (component != null)
                        component.putClientProperty("completion-active", Boolean.TRUE); //NOI18N
                    resolveCompletion(controller);
                    if (component != null && isTaskCancelled())
                        component.putClientProperty("completion-active", Boolean.FALSE); //NOI18N
                }
            }
        }
        
        private void resolveCompletion(CompilationController controller) throws IOException {
            Env env = getCompletionEnvironment(controller, true);
            results = new ArrayList<JavaFXCompletionItem>();
            anchorOffset = env.getOffset();
            TreePath path = env.getPath();
            Tree t = path.getLeaf();
            if (t instanceof JavaFXTree && t.getKind() == Tree.Kind.OTHER) {
                JavaFXTree jfxt = (JavaFXTree)t;
                JavaFXKind k = jfxt.getJavaFXKind();
                switch (k)  {
                    case BIND_EXPRESSION :
                        // inside bind
                        break;
                    case BLOCK_EXPRESSION :
                        // inside block
                        break;
                    case CLASS_DECLARATION:
                        break;
                    case FOR_EXPRESSION: 
                        break;
                    case FOR_EXPRESSION_IN_CLAUSE:
                        break;
                    case FUNCTION_DEFINITION: 
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
                        break;
                    case TYPE_FUNCTIONAL:
                        break;
                    case TYPE_UNKNOWN:
                        break;
                }
            } else {
                switch(t.getKind()) {
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
                        insideBlock(env);
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
        }
        
        private void insideCompilationUnit(Env env) throws IOException {
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
                TokenSequence<JFXTokenId> first = findFirstNonWhitespaceToken(env, (int)sourcePositions.getEndPosition(root, pkg), offset);
                if (first != null && first.token().id() == JFXTokenId.SEMI)
                    addKeywordsForCU(env);
            }
        }
        
        private void insideImport(Env env) {
            int offset = env.getOffset();
            String prefix = env.getPrefix();
            ImportTree im = (ImportTree)env.getPath().getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            if (offset <= sourcePositions.getStartPosition(root, im.getQualifiedIdentifier())) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, im, offset);
                if (last != null && last.token().id() == JFXTokenId.IMPORT && startsWith(STATIC_KEYWORD, prefix))
                    addKeyword(env, STATIC_KEYWORD, SPACE, false);
                addPackages(env, prefix);
            }            
        }
        
        private void insideClass(Env env) throws IOException {
            int offset = env.getOffset();
            TreePath path = env.getPath();
            ClassTree cls = (ClassTree)path.getLeaf();
            CompilationController controller = env.getController();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            int startPos = (int)sourcePositions.getEndPosition(root, cls.getModifiers());
            if (startPos <= 0)
                startPos = (int)sourcePositions.getStartPosition(root, cls);
            String headerText = controller.getText().substring(startPos, offset);
            int idx = headerText.indexOf('{'); //NOI18N
            if (idx >= 0) {
                addKeywordsForClassBody(env);
                return;
            }
            TreeUtilities tu = controller.getTreeUtilities();
            Tree lastImpl = null;
            for (Tree impl : cls.getImplementsClause()) {
                int implPos = (int)sourcePositions.getEndPosition(root, impl);
                if (implPos == Diagnostic.NOPOS || offset <= implPos)
                    break;
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
                int extPos = (int)sourcePositions.getEndPosition(root, ext);
                if (extPos != Diagnostic.NOPOS && offset > extPos) {
                    TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, extPos + 1, offset);
                    //addKeyword(env, IMPLEMENTS_KEYWORD, SPACE, false);
                    return;
                }
            }
            TypeParameterTree lastTypeParam = null;
            for (TypeParameterTree tp : cls.getTypeParameters()) {
                int tpPos = (int)sourcePositions.getEndPosition(root, tp);
                if (tpPos == Diagnostic.NOPOS || offset <= tpPos)
                    break;
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
            lastNonWhitespaceToken = findLastNonWhitespaceToken(env, (int)sourcePositions.getStartPosition(root, cls), offset);
            if (path.getParentPath().getLeaf().getKind() == Tree.Kind.COMPILATION_UNIT) {
                addClassModifiers(env, cls.getModifiers().getFlags());
            } else {
                addMemberModifiers(env, cls.getModifiers().getFlags(), false);
            }
        }
        
        private void insideVariable(Env env) throws IOException {
            int offset = env.getOffset();
            TreePath path = env.getPath();
            VariableTree var = (VariableTree)path.getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            boolean isLocal = path.getParentPath().getLeaf().getKind() != Tree.Kind.CLASS;
            Tree type = var.getType();
            int typePos = type.getKind() == Tree.Kind.ERRONEOUS && ((ErroneousTree)type).getErrorTrees().isEmpty() ?
                (int)sourcePositions.getEndPosition(root, type) : (int)sourcePositions.getStartPosition(root, type);            
            if (offset <= typePos) {
                addMemberModifiers(env, var.getModifiers().getFlags(), isLocal);
                ModifiersTree mods = var.getModifiers();
                return;
            }
            Tree init = unwrapErrTree(var.getInitializer());
            if (init == null) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, (int)sourcePositions.getEndPosition(root, type), offset);
                if (last == null) {
                    insideExpression(env, new TreePath(path, type));
                } else if (last.token().id() == JFXTokenId.EQ) {
                    localResult(env);
                    addValueKeywords(env);
                }
            } else {
                int pos = (int)sourcePositions.getStartPosition(root, init);
                if (pos < 0)
                    return;
                if (offset <= pos) {
                    TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, (int)sourcePositions.getEndPosition(root, type), offset);
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
        
        private void insideMethod(Env env) throws IOException {
            int offset = env.getOffset();
            String prefix = env.getPrefix();
            TreePath path = env.getPath();
            MethodTree mth = (MethodTree)path.getLeaf();
            CompilationController controller = env.getController();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            int startPos = (int)sourcePositions.getStartPosition(root, mth);
            Tree retType = mth.getReturnType();
            if (retType == null) {
                int modPos = (int)sourcePositions.getEndPosition(root, mth.getModifiers());
                if (modPos > startPos)
                    startPos = modPos;
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
                startPos = (int)sourcePositions.getEndPosition(root, retType) + 1;
            }
            Tree lastThr = null;
            for (Tree thr: mth.getThrows()) {
                int thrPos = (int)sourcePositions.getEndPosition(root, thr);
                if (thrPos == Diagnostic.NOPOS || offset <= thrPos)
                    break;
                lastThr = thr;
                startPos = thrPos;
            }
            if (lastThr != null) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, startPos, offset);
                if (last != null && last.token().id() == JFXTokenId.COMMA) {
                    if (queryType == COMPLETION_QUERY_TYPE && mth.getBody() != null) {
                        controller.toPhase(Phase.RESOLVED);
                        JavafxcTrees trees = controller.getTrees();
                        for (ExpressionTree thr : mth.getThrows()) {
                            TypeMirror t = trees.getTypeMirror(new TreePath(path, thr));
                            if (thr == lastThr)
                                break;
                        }
                    }
                }
                return;
            }
            String headerText = controller.getText().substring(startPos, offset);
            int parStart = headerText.indexOf('('); //NOI18N
            if (parStart >= 0) {
                int parEnd = headerText.indexOf(')', parStart); //NOI18N
                if (parEnd > parStart) {
                    headerText = headerText.substring(parEnd + 1).trim();
                } else {
                    for (VariableTree param : mth.getParameters()) {
                        int parPos = (int)sourcePositions.getEndPosition(root, param);
                        if (parPos == Diagnostic.NOPOS || offset <= parPos)
                            break;
                        parStart = parPos - startPos;
                    }
                    headerText = headerText.substring(parStart).trim();
                    if ("(".equals(headerText) || ",".equals(headerText)) { //NOI18N
                        addMemberModifiers(env, Collections.<Modifier>emptySet(), true);
                    }
                }
            } else if (retType != null && headerText.trim().length() == 0) {
                insideExpression(env, new TreePath(path, retType));
            }
        }
        
        private void insideModifiers(Env env, TreePath modPath) throws IOException {
            int offset = env.getOffset();
            ModifiersTree mods = (ModifiersTree)modPath.getLeaf();
            Set<Modifier> m = EnumSet.noneOf(Modifier.class);
            TokenSequence<JFXTokenId> ts = env.getController().getTreeUtilities().tokensFor(mods, env.getSourcePositions());
            JFXTokenId lastNonWhitespaceTokenId = null;
            while(ts.moveNext() && ts.offset() < offset) {
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
            };            
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
                addKeywordsForBlock(env);
            }
        }
        
        private void insideBlock(Env env) throws IOException {
            int offset = env.getOffset();
            BlockTree bl = (BlockTree)env.getPath().getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            int blockPos = (int)sourcePositions.getStartPosition(root, bl);
            String text = env.getController().getText().substring(blockPos, offset);
            if (text.indexOf('{') < 0) { //NOI18N
                addMemberModifiers(env, Collections.singleton(STATIC), false);
                return;
            }
            StatementTree last = null;
            for(StatementTree stat : bl.getStatements()) {
                int pos = (int)sourcePositions.getStartPosition(root, stat);
                if (pos == Diagnostic.NOPOS || offset <= pos)
                    break;
                last = stat;
            }
            if (last == null) {
            } else if (last.getKind() == Tree.Kind.TRY) {
                if (((TryTree)last).getFinallyBlock() == null) {
                    addKeyword(env, CATCH_KEYWORD, null, false);
                    addKeyword(env, FINALLY_KEYWORD, null, false);
                    if (((TryTree)last).getCatches().size() == 0)
                        return;
                }
            }
            localResult(env);
            addKeywordsForBlock(env);
        }
        
        private void insideMemberSelect(Env env) throws IOException {
            int offset = env.getOffset();
            String prefix = env.getPrefix();
            TreePath path = env.getPath();
            MemberSelectTree fa = (MemberSelectTree)path.getLeaf();
            CompilationController controller = env.getController();
            CompilationUnitTree root = env.getRoot();
            SourcePositions sourcePositions = env.getSourcePositions();
            int expEndPos = (int)sourcePositions.getEndPosition(root, fa.getExpression());
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
                        if (ts.offset() != expEndPos || ts.token().text().charAt(0) != '.')
                            break;
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
                if (expEndPos <= offset)
                    insideExpression(env, new TreePath(path, fa.getExpression()));
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
        }
        
        private void insideMethodInvocation(Env env) throws IOException {
            TreePath path = env.getPath();
            MethodInvocationTree mi = (MethodInvocationTree)path.getLeaf();
            int offset = env.getOffset();
            TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, mi, offset);
            if (ts == null || (ts.token().id() != JFXTokenId.LPAREN && ts.token().id() != JFXTokenId.COMMA)) {
                SourcePositions sp = env.getSourcePositions();
                CompilationUnitTree root = env.getRoot();
                int lastTokenEndOffset = ts.offset() + ts.token().length();
                for (ExpressionTree arg : mi.getArguments()) {
                    int pos = (int)sp.getEndPosition(root, arg);
                    if (lastTokenEndOffset == pos) {
                        insideExpression(env, new TreePath(path, arg));
                        break;
                    }
                    if (offset <= pos)
                        break;
                }
                return;
            }
            String prefix = env.getPrefix();
            if (prefix == null || prefix.length() == 0)
                addMethodArguments(env, mi);
            addLocalMembersAndVars(env);
            addValueKeywords(env);
        }
        
        private void insideIf(Env env) throws IOException {
            IfTree iff = (IfTree)env.getPath().getLeaf();
            if (env.getSourcePositions().getEndPosition(env.getRoot(), iff.getCondition()) <= env.getOffset()) {
                localResult(env);
                addKeywordsForStatement(env);
            }
        }
        
        private void insideWhile(Env env) throws IOException {
            WhileLoopTree wlt = (WhileLoopTree)env.getPath().getLeaf();
            if (env.getSourcePositions().getEndPosition(env.getRoot(), wlt.getCondition()) <= env.getOffset()) {
                localResult(env);
                addKeywordsForStatement(env);
            }
        }
        
        private void insideFor(Env env) throws IOException {
            int offset = env.getOffset();
            TreePath path = env.getPath();
            ForLoopTree fl = (ForLoopTree)path.getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            Tree lastTree = null;
            int lastTreePos = offset;
            for (Tree update : fl.getUpdate()) {
                int pos = (int)sourcePositions.getEndPosition(root, update);
                if (pos == Diagnostic.NOPOS || offset <= pos)
                    break;
                lastTree = update;
                lastTreePos = pos;
            }
            if (lastTree == null) {
                int pos = (int)sourcePositions.getEndPosition(root, fl.getCondition());
                if (pos != Diagnostic.NOPOS && pos < offset) {
                    lastTree = fl.getCondition();
                    lastTreePos = pos;
                }
            }
            if (lastTree == null) {
                for (Tree init : fl.getInitializer()) {
                    int pos = (int)sourcePositions.getEndPosition(root, init);
                    if (pos == Diagnostic.NOPOS || offset <= pos)
                        break;
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
                            Tree var = ((VariableTree)lastTree).getInitializer();
                            if (var != null)
                                insideExpression(env, new TreePath(new TreePath(path, lastTree), var));
                            break;
                        case EXPRESSION_STATEMENT:
                            Tree exp = unwrapErrTree(((ExpressionStatementTree)lastTree).getExpression());
                            if (exp != null)
                                insideExpression(env, new TreePath(new TreePath(path, lastTree), exp));
                            break;
                        default:
                            insideExpression(env, new TreePath(path, lastTree));
                    }
                }
            }
        }

        private void insideForEach(Env env) throws IOException {
            int offset = env.getOffset();
            TreePath path = env.getPath();
            EnhancedForLoopTree efl = (EnhancedForLoopTree)path.getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            CompilationController controller = env.getController();
            if (sourcePositions.getStartPosition(root, efl.getExpression()) >= offset) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, (int)sourcePositions.getEndPosition(root, efl.getVariable()), offset);
                if (last != null && last.token().id() == JFXTokenId.COLON) {
                    env.insideForEachExpressiion();
                    addKeyword(env, NEW_KEYWORD, SPACE, false);
                    localResult(env);
                }
                return;
            }
            TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, (int)sourcePositions.getEndPosition(root, efl.getExpression()), offset);
            if (last != null && last.token().id() == JFXTokenId.RPAREN) {
                addKeywordsForStatement(env);
            } else {
                env.insideForEachExpressiion();
                addKeyword(env, NEW_KEYWORD, SPACE, false);
            }
            localResult(env);
            
        }
        
        private void insideSwitch(Env env) throws IOException {
            int offset = env.getOffset();
            String prefix = env.getPrefix();
            TreePath path = env.getPath();
            SwitchTree st = (SwitchTree)path.getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            if (sourcePositions.getStartPosition(root, st.getExpression()) < offset) {
                CaseTree lastCase = null;
                for (CaseTree t : st.getCases()) {
                    int pos = (int)sourcePositions.getStartPosition(root, t);
                    if (pos == Diagnostic.NOPOS || offset <= pos)
                        break;
                    lastCase = t;
                }
                if (lastCase != null) {
                    localResult(env);
                    addKeywordsForBlock(env);
                } else {
                    TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, st, offset);
                    if (ts != null && ts.token().id() == JFXTokenId.LBRACE) {
//                        addKeyword(env, CASE_KEYWORD, SPACE, false);
//                        addKeyword(env, DEFAULT_KEYWORD, COLON, false);
                    }
                }
            }
        }
        
        private void insideCase(Env env) throws IOException {
            int offset = env.getOffset();
            TreePath path = env.getPath();
            CaseTree cst = (CaseTree)path.getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            CompilationController controller = env.getController();
            if (cst.getExpression() != null && ((sourcePositions.getStartPosition(root, cst.getExpression()) >= offset) ||
                    (cst.getExpression().getKind() == Tree.Kind.ERRONEOUS && ((ErroneousTree)cst.getExpression()).getErrorTrees().isEmpty() && sourcePositions.getEndPosition(root, cst.getExpression()) >= offset))) {
                TreePath path1 = path.getParentPath();
                if (path1.getLeaf().getKind() == Tree.Kind.SWITCH) {
                    TypeMirror tm = controller.getTrees().getTypeMirror(new TreePath(path1, ((SwitchTree)path1.getLeaf()).getExpression()));
//                    if (tm.getKind() == TypeKind.DECLARED && ((DeclaredType)tm).asElement().getKind() == ENUM) {
//                        addEnumConstants(env, (TypeElement)((DeclaredType)tm).asElement());
//                    } else {
//                        addLocalConstantsAndTypes(env);
//                    }
                }
            } else {
                TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, cst, offset);
                if (ts != null && ts.token().id() == JFXTokenId.COLON) {
                    localResult(env);
                    addKeywordsForBlock(env);
                }
            }
        }
        
        private void insideParens(Env env) throws IOException {
            TreePath path = env.getPath();
            ParenthesizedTree pa = (ParenthesizedTree)path.getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            Tree exp = unwrapErrTree(pa.getExpression());
            if (exp == null || env.getOffset() <= sourcePositions.getStartPosition(root, exp)) {
                HashSet<TypeElement> toExclude = new HashSet<TypeElement>();
                if (queryType == COMPLETION_QUERY_TYPE && path.getParentPath().getLeaf().getKind() != Tree.Kind.SWITCH) {
                    Set<? extends TypeMirror> smarts = env.getSmartTypes();
                    if (smarts != null) {
                        for (TypeMirror smart : smarts) {
                            if (smart != null) {
                                if (smart.getKind() == TypeKind.DECLARED) {
                                    for (DeclaredType subtype : getSubtypesOf(env, (DeclaredType)smart)) {
                                        TypeElement elem = (TypeElement)subtype.asElement();
                                        toExclude.add(elem);
                                    }
                                } else if (smart.getKind() == TypeKind.ARRAY) {
                                    try {
//                                        results.add(JavaFXCompletionItem.createArrayItem((ArrayType)smart, anchorOffset, env.getController().getElements()));                                            
                                    } catch (IllegalArgumentException iae) {}
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
        
        private void insideTypeCheck(Env env) throws IOException {
            InstanceOfTree iot = (InstanceOfTree)env.getPath().getLeaf();
            TokenSequence<JFXTokenId> ts = findLastNonWhitespaceToken(env, iot, env.getOffset());
        }
        
        private void insideArrayAccess(Env env) throws IOException {
            int offset = env.getOffset();
            ArrayAccessTree aat = (ArrayAccessTree)env.getPath().getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            int aaTextStart = (int)sourcePositions.getEndPosition(root, aat.getExpression());
            if (aaTextStart != Diagnostic.NOPOS) {
                Tree expr = unwrapErrTree(aat.getIndex());
                if (expr == null || offset <= (int)sourcePositions.getStartPosition(root, expr)) {
                    String aatText = env.getController().getText().substring(aaTextStart, offset);
                    int bPos = aatText.indexOf('['); //NOI18N
                    if (bPos > -1) {
                        localResult(env);
                        addValueKeywords(env);
                    }
                }
            }
        }
        
        private void insideNewArray(Env env) throws IOException {
            int offset = env.getOffset();
            TreePath path = env.getPath();
            NewArrayTree nat = (NewArrayTree)path.getLeaf();
            if (nat.getInitializers() != null) { // UFFF!!!!
                SourcePositions sourcePositions = env.getSourcePositions();
                CompilationUnitTree root = env.getRoot();
                Tree last = null;
                int lastPos = offset;
                for (Tree init : nat.getInitializers()) {
                    int pos = (int)sourcePositions.getEndPosition(root, init);
                    if (pos == Diagnostic.NOPOS || offset <= pos)
                        break;
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
                    if (nat.getDimensions().size() > 0)
                        insideExpression(env, path);
                    break;
            }
        }
        
        private void insideAssignment(Env env) throws IOException {
            int offset = env.getOffset();
            TreePath path = env.getPath();
            AssignmentTree as = (AssignmentTree)path.getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            int asTextStart = (int)sourcePositions.getEndPosition(root, as.getVariable());
            if (asTextStart != Diagnostic.NOPOS) {
                Tree expr = unwrapErrTree(as.getExpression());
                if (expr == null || offset <= (int)sourcePositions.getStartPosition(root, expr)) {
                    String asText = env.getController().getText().substring(asTextStart, offset);
                    int eqPos = asText.indexOf('='); //NOI18N
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
        
        private void insideCompoundAssignment(Env env) throws IOException {
            int offset = env.getOffset();
            CompoundAssignmentTree cat = (CompoundAssignmentTree)env.getPath().getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            int catTextStart = (int)sourcePositions.getEndPosition(root, cat.getVariable());
            if (catTextStart != Diagnostic.NOPOS) {
                Tree expr = unwrapErrTree(cat.getExpression());
                if (expr == null || offset <= (int)sourcePositions.getStartPosition(root, expr)) {
                    String catText = env.getController().getText().substring(catTextStart, offset);
                    int eqPos = catText.indexOf('='); //NOI18N
                    if (eqPos > -1) {
                        localResult(env);
                        addValueKeywords(env);
                    }
                }
            }
        }
        
        private void insideBinaryTree(Env env) throws IOException {
            int offset = env.getOffset();
            BinaryTree bi = (BinaryTree)env.getPath().getLeaf();
            SourcePositions sourcePositions = env.getSourcePositions();
            CompilationUnitTree root = env.getRoot();
            int pos = (int)sourcePositions.getEndPosition(root, bi.getRightOperand());
            if (pos != Diagnostic.NOPOS && pos < offset)
                return;
            pos = (int)sourcePositions.getEndPosition(root, bi.getLeftOperand());
            if (pos != Diagnostic.NOPOS) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, pos, offset);
                if (last != null) {
                    localResult(env);
                    addValueKeywords(env);
                }
            }
        }

        private void insideExpressionStatement(Env env) throws IOException {
            TreePath path = env.getPath();
            ExpressionStatementTree est = (ExpressionStatementTree)path.getLeaf();
            CompilationController controller = env.getController();
            Tree t = est.getExpression();
            if (t.getKind() == Tree.Kind.ERRONEOUS) {
                Iterator<? extends Tree> it = ((ErroneousTree)t).getErrorTrees().iterator();
                if (it.hasNext()) {
                    t = it.next();
                } else {
                    TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
                    ts.move((int)env.getSourcePositions().getStartPosition(env.getRoot(), est));
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
                        case FOR_LOOP:
                            if (((ForLoopTree)parentTree).getStatement() == est)
                                addKeywordsForStatement(env);
                            else
                                addValueKeywords(env);
                            break;
                        case ENHANCED_FOR_LOOP:
                            if (((EnhancedForLoopTree)parentTree).getStatement() == est)
                                addKeywordsForStatement(env);
                            else
                                addValueKeywords(env);
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
            } else if (t.getKind() == Tree.Kind.MEMBER_SELECT && ERROR.contentEquals(((MemberSelectTree)t).getIdentifier())) {
                controller.toPhase(Phase.ELEMENTS_RESOLVED);
                TreePath expPath = new TreePath(tPath, ((MemberSelectTree)t).getExpression());
                TypeMirror type = controller.getTrees().getTypeMirror(expPath);
                switch (type.getKind()) {
                    case TYPEVAR:
                        type = ((TypeVariable)type).getUpperBound();
                        if (type == null)
                            return;
//                        type = controller.getTypes().capture(type);
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
//                        addMembers(env, type, controller.getTrees().getElement(expPath), EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE, FIELD, METHOD, ENUM_CONSTANT), null, false, false);
                        break;
                    default:
                        Element el = controller.getTrees().getElement(expPath);
                        if (el instanceof PackageElement) {
//                            addPackageContent(env, (PackageElement)el, EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE, FIELD, METHOD, ENUM_CONSTANT), null, false);
                        }
                }
            } else {
                insideExpression(env, tPath);
            }
            
        }
        
        private void insideExpression(Env env, TreePath exPath) throws IOException {
            int offset = env.getOffset();
            String prefix = env.getPrefix();
            Tree et = exPath.getLeaf();
            Tree parent = exPath.getParentPath().getLeaf();
            CompilationController controller = env.getController();
            int endPos = (int)env.getSourcePositions().getEndPosition(env.getRoot(), et);
            if (endPos != Diagnostic.NOPOS && endPos < offset) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, endPos, offset);
                if (last != null)
                    return;
            }
            controller.toPhase(Phase.ELEMENTS_RESOLVED);
            boolean isConst = parent.getKind() == Tree.Kind.VARIABLE && ((VariableTree)parent).getModifiers().getFlags().containsAll(EnumSet.of(FINAL, STATIC));
            if ((parent == null || parent.getKind() != Tree.Kind.PARENTHESIZED) &&
                    (et.getKind() == Tree.Kind.PRIMITIVE_TYPE || et.getKind() == Tree.Kind.ARRAY_TYPE || et.getKind() == Tree.Kind.PARAMETERIZED_TYPE)) {
                TypeMirror tm = controller.getTrees().getTypeMirror(exPath);
                Scope scope = env.getScope();
                final ExecutableElement method = scope.getEnclosingMethod();
                return;
            }
            if (et.getKind() == Tree.Kind.IDENTIFIER) {
                Element e = controller.getTrees().getElement(exPath);
                if (e == null)
                    return;
                TypeMirror tm = controller.getTrees().getTypeMirror(exPath);
                switch (e.getKind()) {
                    case ANNOTATION_TYPE:
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                    case PACKAGE:
                        if (parent == null || parent.getKind() != Tree.Kind.PARENTHESIZED) {
                            Scope scope = env.getScope();
                            final ExecutableElement method = scope.getEnclosingMethod();
                        }
                        break;
                    case ENUM_CONSTANT:
                    case EXCEPTION_PARAMETER:
                    case FIELD:
                    case LOCAL_VARIABLE:
                    case PARAMETER:
                        if (tm.getKind() == TypeKind.DECLARED || tm.getKind() == TypeKind.ARRAY || tm.getKind() == TypeKind.ERROR) {
                            addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                        }
                        break;
                }
                return;
            }
            Tree exp = null;
            if (et.getKind() == Tree.Kind.PARENTHESIZED) {
                exp = ((ParenthesizedTree)et).getExpression();
            } else if (et.getKind() == Tree.Kind.TYPE_CAST) {
                if (env.getSourcePositions().getEndPosition(env.getRoot(), ((TypeCastTree)et).getType()) <= offset)
                    exp = ((TypeCastTree)et).getType();
            } else if (et.getKind() == Tree.Kind.ASSIGNMENT) {
                Tree t = ((AssignmentTree)et).getExpression();
                if (t.getKind() == Tree.Kind.PARENTHESIZED && env.getSourcePositions().getEndPosition(env.getRoot(), t) < offset)
                    exp = ((ParenthesizedTree)t).getExpression();
            }
            if (exp != null) {
                exPath = new TreePath(exPath, exp);
                if (exp.getKind() == Tree.Kind.PRIMITIVE_TYPE || exp.getKind() == Tree.Kind.ARRAY_TYPE || exp.getKind() == Tree.Kind.PARAMETERIZED_TYPE) {
                    localResult(env);
                    addValueKeywords(env);
                    return;
                }
                Element e = controller.getTrees().getElement(exPath);
                if (e == null) {
                    if (exp.getKind() == Tree.Kind.TYPE_CAST) {
                        addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                    }
                    return;
                }
                TypeMirror tm = controller.getTrees().getTypeMirror(exPath);
                switch (e.getKind()) {
                    case ANNOTATION_TYPE:
                    case CLASS:
                    case ENUM:
                    case INTERFACE:
                    case PACKAGE:
                        if (exp.getKind() == Tree.Kind.IDENTIFIER) {
                        } else if (exp.getKind() == Tree.Kind.MEMBER_SELECT) {
                            if (tm.getKind() == TypeKind.ERROR || tm.getKind() == TypeKind.PACKAGE) {
                                addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                            }
                            localResult(env);
                            addValueKeywords(env);
                        } else if (exp.getKind() == Tree.Kind.PARENTHESIZED && (tm.getKind() == TypeKind.DECLARED || tm.getKind() == TypeKind.ARRAY)) {
                            addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                        }
                        break;
                    case ENUM_CONSTANT:
                    case EXCEPTION_PARAMETER:
                    case FIELD:
                    case LOCAL_VARIABLE:
                    case PARAMETER:
                        if (tm.getKind() == TypeKind.DECLARED || tm.getKind() == TypeKind.ARRAY || tm.getKind() == TypeKind.ERROR) {
                            addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                        }
                        break;
                    case CONSTRUCTOR:
                    case METHOD:
                        if (tm.getKind() == TypeKind.DECLARED || tm.getKind() == TypeKind.ARRAY || tm.getKind() == TypeKind.ERROR) {
                            addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                        }
                }
                return;
            }
            Element e = controller.getTrees().getElement(exPath);
            TypeMirror tm = controller.getTrees().getTypeMirror(exPath);
            if (e == null) {
                if (tm.getKind() == TypeKind.DECLARED || tm.getKind() == TypeKind.ARRAY || tm.getKind() == TypeKind.ERROR) {
                    addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                }
                return;
            }
            switch (e.getKind()) {
                case ANNOTATION_TYPE:
                case CLASS:
                case ENUM:
                case INTERFACE:
                case PACKAGE:
                    Scope scope = env.getScope();
                    final ExecutableElement method = scope.getEnclosingMethod();
                    if (et.getKind() == Tree.Kind.MEMBER_SELECT && tm.getKind() == TypeKind.ERROR) {
                        addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                    }
                    break;
                case ENUM_CONSTANT:
                case EXCEPTION_PARAMETER:
                case FIELD:
                case LOCAL_VARIABLE:
                case PARAMETER:
                case CONSTRUCTOR:
                case METHOD:
                    if (tm.getKind() == TypeKind.DECLARED || tm.getKind() == TypeKind.ARRAY || tm.getKind() == TypeKind.ERROR) {
                        addKeyword(env, INSTANCEOF_KEYWORD, SPACE, false);
                    }
            }
        }
        
        private void localResult(Env env) throws IOException {
            addLocalMembersAndVars(env);
        }
        
        private void addLocalConstantsAndTypes(final Env env) throws IOException {
        }
        
        private void addLocalMembersAndVars(final Env env) throws IOException {
        }

        private void addLocalFieldsAndVars(final Env env) throws IOException {
        }
        
        private void addPackages(Env env, String fqnPrefix) {
//            if (fqnPrefix == null)
//                fqnPrefix = EMPTY;
//            for (String pkgName : env.getController().getClasspathInfo().getClassIndex().getPackageNames(fqnPrefix, true,EnumSet.allOf(ClassIndex.SearchScope.class)))
//                if (pkgName.length() > 0)
//                    results.add(JavaFXCompletionItem.createPackageItem(pkgName, anchorOffset, false));
        }
        
        private List<DeclaredType> getSubtypesOf(Env env, DeclaredType baseType) throws IOException {
                return Collections.emptyList();
        }
        
        private void addMethodArguments(Env env, MethodInvocationTree mit) throws IOException {
        }
        
        private void addKeyword(Env env, String kw, String postfix, boolean smartType) {
            if (startsWith(kw, env.getPrefix()))
                results.add(JavaFXCompletionItem.createKeywordItem(kw, postfix, anchorOffset, smartType));
        }
        
        private void addKeywordsForCU(Env env) {
            List<String> kws = new ArrayList<String>();
            int offset = env.getOffset();
            String prefix = env.getPrefix();
            CompilationUnitTree cu = env.getRoot();
            SourcePositions sourcePositions = env.getSourcePositions();
            kws.add(ABSTRACT_KEYWORD);
            kws.add(CLASS_KEYWORD);
//            kws.add(ENUM_KEYWORD);
//            kws.add(FINAL_KEYWORD);
//            kws.add(INTERFACE_KEYWORD);
            boolean beforeAnyClass = true;
            boolean beforePublicClass = true;
            for(Tree t : cu.getTypeDecls()) {
                if (t.getKind() == Tree.Kind.CLASS) {
                    int pos = (int)sourcePositions.getEndPosition(cu, t);
                    if (pos != Diagnostic.NOPOS && offset >= pos) {
                        beforeAnyClass = false;
                        if (((ClassTree)t).getModifiers().getFlags().contains(Modifier.PUBLIC)) {
                            beforePublicClass = false;
                            break;
                        }
                    }
                }
            }
            if (beforePublicClass)
                kws.add(PUBLIC_KEYWORD);
            if (beforeAnyClass) {
                kws.add(IMPORT_KEYWORD);
                Tree firstImport = null;
                for(Tree t : cu.getImports()) {
                    firstImport = t;
                    break;
                }
                Tree pd = cu.getPackageName();
                if ((pd != null && offset <= sourcePositions.getStartPosition(cu, cu)) ||
                        (pd == null && (firstImport == null || sourcePositions.getStartPosition(cu, firstImport) >= offset)))
                    kws.add(PACKAGE_KEYWORD);
            }
            for (String kw : kws) {
                if (startsWith(kw, prefix))
                    results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
        }
        
        private void addKeywordsForClassBody(Env env) {
            String prefix = env.getPrefix();
            for (String kw : CLASS_BODY_KEYWORDS)
                if (startsWith(kw, prefix))
                    results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
        }
        
        private void addKeywordsForBlock(Env env) {
            String prefix = env.getPrefix();
            for (String kw : STATEMENT_KEYWORDS) {
                if (startsWith(kw, prefix))
                    results.add(JavaFXCompletionItem.createKeywordItem(kw, null, anchorOffset, false));
            }
            for (String kw : BLOCK_KEYWORDS) {
                if (startsWith(kw, prefix))
                    results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
            if (startsWith(RETURN_KEYWORD, prefix)) {
                TreePath mth = getPathElementOfKind(Tree.Kind.METHOD, env.getPath());
                String postfix = SPACE;
                if (mth != null) {
                    Tree rt = ((MethodTree)mth.getLeaf()).getReturnType();
                    if (rt == null || (rt.getKind() == Tree.Kind.PRIMITIVE_TYPE && ((PrimitiveTypeTree)rt).getPrimitiveTypeKind() == TypeKind.VOID))
                        postfix = SEMI;
                }
                results.add(JavaFXCompletionItem.createKeywordItem(RETURN_KEYWORD, postfix, anchorOffset, false));
            }
            boolean caseAdded = false;
            boolean breakAdded = false;
            boolean continueAdded = false;
            TreePath tp = env.getPath();
            while (tp != null) {
                switch (tp.getLeaf().getKind()) {
                    case SWITCH:
                        CaseTree lastCase = null;
                        CompilationUnitTree root = env.getRoot();
                        SourcePositions sourcePositions = env.getSourcePositions();
                        for (CaseTree t : ((SwitchTree)tp.getLeaf()).getCases()) {
                            if (sourcePositions.getStartPosition(root, t) >= env.getOffset())
                                break;
                            lastCase = t;
                        }
                        if (! caseAdded && (lastCase == null || lastCase.getExpression() != null)) {
                            caseAdded = true;
//                            if (startsWith(CASE_KEYWORD, prefix))
//                                results.add(JavaFXCompletionItem.createKeywordItem(CASE_KEYWORD, SPACE, anchorOffset, false));
//                            if (startsWith(DEFAULT_KEYWORD, prefix))
//                                results.add(JavaFXCompletionItem.createKeywordItem(DEFAULT_KEYWORD, COLON, anchorOffset, false));
                        }
                        if (!breakAdded && startsWith(BREAK_KEYWORD, prefix)) {
                            breakAdded = true;
                            results.add(JavaFXCompletionItem.createKeywordItem(BREAK_KEYWORD, SEMI, anchorOffset, false));
                        }
                        break;
                    case DO_WHILE_LOOP:
                    case ENHANCED_FOR_LOOP:
                    case FOR_LOOP:
                    case WHILE_LOOP:
                        if (! breakAdded && startsWith(BREAK_KEYWORD, prefix)) {
                            breakAdded = true;
                            results.add(JavaFXCompletionItem.createKeywordItem(BREAK_KEYWORD, SEMI, anchorOffset, false));
                        }
                        if (!continueAdded && startsWith(CONTINUE_KEYWORD, prefix)) {
                            continueAdded = true;
                            results.add(JavaFXCompletionItem.createKeywordItem(CONTINUE_KEYWORD, SEMI, anchorOffset, false));                            
                        }
                        break;
                }
                tp = tp.getParentPath();
            }
        }
        
        private void addKeywordsForStatement(Env env) {
            String prefix = env.getPrefix();
            for (String kw : STATEMENT_KEYWORDS) {
                if (startsWith(kw, prefix))
                    results.add(JavaFXCompletionItem.createKeywordItem(kw, null, anchorOffset, false));
            }
            for (String kw : STATEMENT_SPACE_KEYWORDS) {
                if (startsWith(kw, prefix))
                    results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
            if (startsWith(RETURN_KEYWORD, prefix)) {
                TreePath mth = getPathElementOfKind(Tree.Kind.METHOD, env.getPath());
                String postfix = SPACE;
                if (mth != null) {
                    Tree rt = ((MethodTree)mth.getLeaf()).getReturnType();
                    if (rt == null || rt.getKind() == Tree.Kind.PRIMITIVE_TYPE && ((PrimitiveTypeTree)rt).getPrimitiveTypeKind() == TypeKind.VOID)
                        postfix = SEMI;
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
                        if (startsWith(CONTINUE_KEYWORD, prefix))
                            results.add(JavaFXCompletionItem.createKeywordItem(CONTINUE_KEYWORD, SEMI, anchorOffset, false));
                    case SWITCH:
                        if (startsWith(BREAK_KEYWORD, prefix))
                            results.add(JavaFXCompletionItem.createKeywordItem(BREAK_KEYWORD, SEMI, anchorOffset, false));
                        break;
                }
                tp = tp.getParentPath();
            }
        }
        
        private void addValueKeywords(Env env) throws IOException {
            String prefix = env.getPrefix();
            boolean smartType = false;
            if (queryType == COMPLETION_QUERY_TYPE) {
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
            if (startsWith(FALSE_KEYWORD, prefix))
                results.add(JavaFXCompletionItem.createKeywordItem(FALSE_KEYWORD, null, anchorOffset, smartType));
            if (startsWith(TRUE_KEYWORD, prefix))
                results.add(JavaFXCompletionItem.createKeywordItem(TRUE_KEYWORD, null, anchorOffset, smartType));
            if (startsWith(NULL_KEYWORD, prefix))
                results.add(JavaFXCompletionItem.createKeywordItem(NULL_KEYWORD, null, anchorOffset, false));
            if (startsWith(NEW_KEYWORD, prefix))
                results.add(JavaFXCompletionItem.createKeywordItem(NEW_KEYWORD, SPACE, anchorOffset, false));
        }

        private void addClassModifiers(Env env, Set<Modifier> modifiers) {
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
                if (startsWith(kw, prefix))
                    results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
        }
        
        private void addMemberModifiers(Env env, Set<Modifier> modifiers, boolean isLocal) {
            String prefix = env.getPrefix();
            List<String> kws = new ArrayList<String>();
            if (isLocal) {
//                if (!modifiers.contains(FINAL)) {
//                    kws.add(FINAL_KEYWORD);
//                }
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
                kws.add(CLASS_KEYWORD);
                kws.add(TRANSIENT_KEYWORD);
            }
            for (String kw : kws) {
                if (startsWith(kw, prefix))
                    results.add(JavaFXCompletionItem.createKeywordItem(kw, SPACE, anchorOffset, false));
            }
        }
        
        private boolean isTopLevelClass(Tree tree, CompilationUnitTree root) {
            if (tree.getKind() == Tree.Kind.CLASS || (tree.getKind() == Tree.Kind.EXPRESSION_STATEMENT && ((ExpressionStatementTree)tree).getExpression().getKind() == Tree.Kind.ERRONEOUS)) {
                for (Tree t : root.getTypeDecls())
                    if (tree == t)
                        return true;
            }
            return false;
        }
        private static boolean isJavaIdentifierPart(String text) {
            for (int i = 0; i < text.length(); i++) {
                if (!(Character.isJavaIdentifierPart(text.charAt(i))))
                    return false;
            }
            return true;
        }

        private Collection getFilteredData(Collection<JavaFXCompletionItem> data, String prefix) {
            if (prefix.length() == 0)
                return data;
            List ret = new ArrayList();
            boolean camelCase = prefix.length() > 1 && camelCasePattern.matcher(prefix).matches();
            for (Iterator<JavaFXCompletionItem> it = data.iterator(); it.hasNext();) {
                CompletionItem itm = it.next();
                if (startsWith(itm.getInsertPrefix().toString(), prefix)) {
                    ret.add(itm);
                }
            }
            return ret;
        }
        
        private Set<? extends TypeMirror> getSmartTypes(Env env) throws IOException {
            int offset = env.getOffset();
            final CompilationController controller = env.getController();
            TreePath path = env.getPath();
            Tree lastTree = null;
            int dim = 0;
            while(path != null) {
                Tree tree = path.getLeaf();
                switch(tree.getKind()) {
                    case VARIABLE:
                        TypeMirror type = controller.getTrees().getTypeMirror(new TreePath(path, ((VariableTree)tree).getType()));
                        if (type == null)
                            return null;
                        while(dim-- > 0) {
                            if (type.getKind() == TypeKind.ARRAY)
                                type = ((ArrayType)type).getComponentType();
                            else
                                return null;
                        }
                        return type != null ? Collections.singleton(type) : null;
                    case ASSIGNMENT:
                        type = controller.getTrees().getTypeMirror(new TreePath(path, ((AssignmentTree)tree).getVariable()));
                        if (type == null)
                            return null;
                        TreePath parentPath = path.getParentPath();
                        if (parentPath != null && parentPath.getLeaf().getKind() == Tree.Kind.ANNOTATION && type.getKind() == TypeKind.EXECUTABLE) {
                            type = ((ExecutableType)type).getReturnType();
                            while(dim-- > 0) {
                                if (type.getKind() == TypeKind.ARRAY)
                                    type = ((ArrayType)type).getComponentType();
                                else
                                    return null;
                            }
                            if (type.getKind() == TypeKind.ARRAY)
                                type = ((ArrayType)type).getComponentType();
                        }
                        return type != null ? Collections.singleton(type) : null;
                    case RETURN:
                        TreePath methodPath = getPathElementOfKind(Tree.Kind.METHOD, path);
                        if (methodPath == null)
                            return null;
                        Tree retTree = ((MethodTree)methodPath.getLeaf()).getReturnType();
                        if (retTree == null)
                            return null;
                        type = controller.getTrees().getTypeMirror(new TreePath(methodPath, retTree));
                        return type != null ? Collections.singleton(type) : null;
                    case THROW:
                        methodPath = getPathElementOfKind(Tree.Kind.METHOD, path);
                        if (methodPath == null)
                            return null;
                        HashSet<TypeMirror> ret = new HashSet<TypeMirror>();
                        JavafxcTrees trees = controller.getTrees();
                        for (ExpressionTree thr : ((MethodTree)methodPath.getLeaf()).getThrows()) {
                            type = trees.getTypeMirror(new TreePath(methodPath, thr));
                            if (type != null) {
                                ret.add(type);
                            }
                        }
                        return ret;
                    case IF:
                        IfTree iff = (IfTree)tree;
                        return  null;
                    case WHILE_LOOP:
                        WhileLoopTree wl = (WhileLoopTree)tree;
                        return null;
                    case FOR_LOOP:
                        ForLoopTree fl = (ForLoopTree)tree;
                        Tree cond = fl.getCondition();
                        if (lastTree != null) {
                            if (cond instanceof ErroneousTree) {
                                Iterator<? extends Tree> itt =((ErroneousTree)cond).getErrorTrees().iterator();
                                if (itt.hasNext())
                                    cond = itt.next();
                            }
                            return null;
                        }
                        SourcePositions sourcePositions = env.getSourcePositions();
                        CompilationUnitTree root = env.getRoot();
                        if (cond != null && sourcePositions.getEndPosition(root, cond) < offset)
                            return null;
                        Tree lastInit = null;
                        for (Tree init : fl.getInitializer()) {
                            if (sourcePositions.getEndPosition(root, init) >= offset)
                                return null;
                            lastInit = init;
                        }
                        String text = null;
                        if (lastInit == null) {
                            text = controller.getText().substring((int)sourcePositions.getStartPosition(root, fl), offset).trim();
                            int idx = text.indexOf('('); //NOI18N
                            if (idx >= 0)
                                text = text.substring(idx + 1);
                        } else {
                            text = controller.getText().substring((int)sourcePositions.getEndPosition(root, lastInit), offset).trim();
                        }
                        return  null; //NOI18N
                    case ENHANCED_FOR_LOOP:
                        EnhancedForLoopTree efl = (EnhancedForLoopTree)tree;
                        Tree expr = efl.getExpression();
                        if (lastTree != null) {
                            if (expr instanceof ErroneousTree) {
                                Iterator<? extends Tree> itt =((ErroneousTree)expr).getErrorTrees().iterator();
                                if (itt.hasNext())
                                    expr = itt.next();
                            }
                            if(expr != lastTree)
                                return null;
                        } else {
                            sourcePositions = env.getSourcePositions();
                            root = env.getRoot();
                            text = null;
                            if (efl.getVariable() == null) {
                                text = controller.getText().substring((int)sourcePositions.getStartPosition(root, efl), offset).trim();
                                int idx = text.indexOf('('); //NOI18N
                                if (idx >= 0)
                                    text = text.substring(idx + 1);
                            } else {
                                text = controller.getText().substring((int)sourcePositions.getEndPosition(root, efl.getVariable()), offset).trim();
                            }
                            if (!":".equals(text))
                                return null;
                        }
                        TypeMirror var = efl.getVariable() != null ? controller.getTrees().getTypeMirror(new TreePath(path, efl.getVariable())) : null;
                        return var != null ? Collections.singleton(var) : null;
                    case SWITCH:
                        SwitchTree sw = (SwitchTree)tree;
                        if (sw.getExpression() != lastTree)
                            return null;
                        ret = new HashSet<TypeMirror>();
                        return ret;
                    case METHOD_INVOCATION:
                        return null;
                    case NEW_CLASS:
                        return null;
                    case NEW_ARRAY:
                        return null;
                    case CASE:
                        CaseTree ct = (CaseTree)tree;
                        ExpressionTree exp = ct.getExpression();
                        if (exp != null && env.getSourcePositions().getEndPosition(env.getRoot(), exp) >= offset) {
                            parentPath = path.getParentPath();
                            if (parentPath.getLeaf().getKind() == Tree.Kind.SWITCH) {
                                exp = ((SwitchTree)parentPath.getLeaf()).getExpression();
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
                        BinaryTree bt = (BinaryTree)tree;
                        TypeMirror tm = controller.getTrees().getTypeMirror(new TreePath(path, bt.getLeftOperand()));
                        if (tm == null)
                            return null;
                        if (tm.getKind().isPrimitive()) {
                            ret = new HashSet<TypeMirror>();
                            return ret;
                        }
                        return Collections.singleton(tm);
                    case PLUS_ASSIGNMENT:
                        CompoundAssignmentTree cat = (CompoundAssignmentTree)tree;
                        tm = controller.getTrees().getTypeMirror(new TreePath(path, cat.getVariable()));
                        if (tm == null)
                            return null;
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
                        exp = ((ExpressionStatementTree)tree).getExpression();
                        if (exp.getKind() == Tree.Kind.PARENTHESIZED) {
                            text = controller.getText().substring((int)env.getSourcePositions().getStartPosition(env.getRoot(), exp), offset).trim();
                            if (text.endsWith(")")) //NOI18N
                                return null;
                        }
                        break;
                    case TYPE_CAST:
                        TypeCastTree tct = (TypeCastTree)tree;
                        if (env.getSourcePositions().getEndPosition(env.getRoot(), tct.getType()) <= offset)
                            return null;
                        break;
                }
                lastTree = tree;
                path = path.getParentPath();
            }
            return null;
        }
        
        private TokenSequence<JFXTokenId> findFirstNonWhitespaceToken(Env env, Tree tree, int position) {
            int startPos = (int)env.getSourcePositions().getStartPosition(env.getRoot(), tree);
            return findFirstNonWhitespaceToken(env, startPos, position);
        }
        
        private TokenSequence<JFXTokenId> findFirstNonWhitespaceToken(Env env, int startPos, int endPos) {
            TokenSequence<JFXTokenId> ts = env.getController().getTokenHierarchy().tokenSequence(JFXTokenId.language());
            ts.move(startPos);
            ts = nextNonWhitespaceToken(ts);
            if (ts == null || ts.offset() >= endPos)
                return null;
            return ts;
        }
        
        private TokenSequence<JFXTokenId> nextNonWhitespaceToken(TokenSequence<JFXTokenId> ts) {
            while(ts.moveNext()) {
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
        
        private TokenSequence<JFXTokenId> findLastNonWhitespaceToken(Env env, Tree tree, int position) {
            int startPos = (int)env.getSourcePositions().getStartPosition(env.getRoot(), tree);
            return findLastNonWhitespaceToken(env, startPos, position);
        }
        
        private TokenSequence<JFXTokenId> findLastNonWhitespaceToken(Env env, int startPos, int endPos) {
            TokenSequence<JFXTokenId> ts = env.getController().getTokenHierarchy().tokenSequence(JFXTokenId.language());
            ts.move(endPos);
            ts = previousNonWhitespaceToken(ts);
            if (ts == null || ts.offset() < startPos)
                return null;
            return ts;
        }
        
        private TokenSequence<JFXTokenId> previousNonWhitespaceToken(TokenSequence<JFXTokenId> ts) {
            while(ts.movePrevious()) {
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
        
        private List<Tree> getArgumentsUpToPos(Env env, Iterable<? extends ExpressionTree> args, int startPos, int position) {
            List<Tree> ret = new ArrayList<Tree>();
            CompilationUnitTree root = env.getRoot();
            SourcePositions sourcePositions = env.getSourcePositions();
            for (ExpressionTree e : args) {
                int pos = (int)sourcePositions.getEndPosition(root, e);
                if (pos != Diagnostic.NOPOS && position > pos) {
                    startPos = pos;
                    ret.add(e);
                }
            }
            if (startPos < 0)
                return ret;
            if (position > startPos) {
                TokenSequence<JFXTokenId> last = findLastNonWhitespaceToken(env, startPos, position);
                if (last != null && (last.token().id() == JFXTokenId.LPAREN || last.token().id() == JFXTokenId.COMMA))
                    return ret;
            }
            return null;
        }
        
        private Tree unwrapErrTree(Tree tree) {
            if (tree != null && tree.getKind() == Tree.Kind.ERRONEOUS) {
                Iterator<? extends Tree> it = ((ErroneousTree)tree).getErrorTrees().iterator();
                tree = it.hasNext() ? it.next() : null;
            }
            return tree;
        }
        
        private boolean withinScope(Env env, TypeElement e) throws IOException {
            for (Element encl = env.getScope().getEnclosingClass(); encl != null; encl = encl.getEnclosingElement()) {
                if (e == encl)
                    return true;
            }
            return false;
        }
        
        private String fullName(Tree tree) {
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

        private Env getCompletionEnvironment(CompilationController controller, boolean upToOffset) throws IOException {
            controller.toPhase(Phase.PARSED);
            int offset =caretOffset;
            String prefix = null;
            if (upToOffset && offset > 0) {
                TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
                 // When right at the token end move to previous token; otherwise move to the token that "contains" the offset
                if (ts.move(offset) == 0 || !ts.moveNext())
                    ts.movePrevious();
                int len = offset - ts.offset();
                if (len > 0 && (ts.token().id() == JFXTokenId.IDENTIFIER ||
                        (ts.token().id().primaryCategory().startsWith("keyword")) || ts.token().id().primaryCategory().equals("literal"))
                        && ts.token().length() >= len) { //TODO: Use isKeyword(...) when available
                    prefix = ts.token().toString().substring(0, len);
                    offset = ts.offset();
                }
            }
            TreePath path = controller.getTreeUtilities().pathFor(offset);
            if (upToOffset) {
                TreePath treePath = path;
                while (treePath != null) {
                    TreePath pPath = treePath.getParentPath();
                    TreePath gpPath = pPath != null ? pPath.getParentPath() : null;
                    Env env = getEnvImpl(controller, path, treePath, pPath, gpPath, offset, prefix, upToOffset);
                    if (env != null)
                        return env;
                    treePath = treePath.getParentPath();
                }
            } else {
                if (Phase.RESOLVED.compareTo(controller.getPhase()) > 0) {
                    LinkedList<TreePath> reversePath = new LinkedList<TreePath>();
                    TreePath treePath = path;
                    while (treePath != null) {
                        reversePath.addFirst(treePath);
                        treePath = treePath.getParentPath();
                    }
                    for (TreePath tp : reversePath) {
                        TreePath pPath = tp.getParentPath();
                        TreePath gpPath = pPath != null ? pPath.getParentPath() : null;
                        Env env = getEnvImpl(controller, path, tp, pPath, gpPath, offset, prefix, upToOffset);
                        if (env != null)
                            return env;
                    }
                }
            }
            return new Env(offset, prefix, controller, path, controller.getTrees().getSourcePositions(), null);
        }
        
        private Env getEnvImpl(CompilationController controller, TreePath orig, TreePath path, TreePath pPath, TreePath gpPath, int offset, String prefix, boolean upToOffset) throws IOException {
            return null;
        }
        
        private boolean withinAnonymousOrLocalClass(TreePath path) {
            if (path == null)
                return false;
            TreePath parentPath = path.getParentPath();
            if (path.getLeaf().getKind() == Tree.Kind.CLASS && parentPath.getLeaf().getKind() != Tree.Kind.COMPILATION_UNIT && parentPath.getLeaf().getKind() != Tree.Kind.CLASS)                
                return true;
            return withinAnonymousOrLocalClass(parentPath);
        }
        
        private class SourcePositionsImpl extends TreeScanner<Void, Tree> implements SourcePositions {
            
            private Tree root;
            private SourcePositions original;
            private SourcePositions modified;
            private int startOffset;
            private int endOffset;
            
            private boolean found;
            
            private SourcePositionsImpl(Tree root, SourcePositions original, SourcePositions modified, int startOffset, int endOffset) {
                this.root = root;
                this.original = original;
                this.modified = modified;
                this.startOffset = startOffset;
                this.endOffset = endOffset;
            }
            
            public long getStartPosition(CompilationUnitTree compilationUnitTree, Tree tree) {
                if (tree == root)
                    return startOffset;
                found = false;
                scan(root, tree);
                return found ? modified.getStartPosition(compilationUnitTree, tree) + startOffset : original.getStartPosition(compilationUnitTree, tree);
            }

            public long getEndPosition(CompilationUnitTree compilationUnitTree, Tree tree) {
                if (endOffset >= 0 && (tree == root))
                    return endOffset;
                found = false;
                scan(root, tree);
                return found ? modified.getEndPosition(compilationUnitTree, tree) + startOffset : original.getEndPosition(compilationUnitTree, tree);
            }

            public Void scan(Tree node, Tree p) {
                if (node == p)
                    found = true;
                else
                    super.scan(node, p);
                return null;
            }
        }
                
        private static Pattern camelCasePattern = Pattern.compile("(?:\\p{javaUpperCase}(?:\\p{javaLowerCase}|\\p{Digit}|\\.|\\$)*){2,}"); // NOI18N
        
        private class Env {
            private int offset;
            private String prefix;
            private boolean isCamelCasePrefix;
            private CompilationController controller;
            private TreePath path;
            private SourcePositions sourcePositions;
            private Scope scope;
            private Collection<? extends Element> refs = null;
            private boolean insideForEachExpressiion = false;
            private Set<? extends TypeMirror> smartTypes = null;
     
    
            
            private Env(int offset, String prefix, CompilationController controller, TreePath path, SourcePositions sourcePositions, Scope scope) {
                this.offset = offset;
                this.prefix = prefix;
                this.isCamelCasePrefix = prefix != null && prefix.length() > 1 && camelCasePattern.matcher(prefix).matches();
                this.controller = controller;
                this.path = path;
                this.sourcePositions = sourcePositions;
                this.scope = scope;
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
            
            public Scope getScope() throws IOException {
                if (scope == null) {
                    controller.toPhase(Phase.ELEMENTS_RESOLVED);
                    scope = controller.getTreeUtilities().scopeFor(offset);
                }
                return scope;
            }

            public void insideForEachExpressiion() {
                this.insideForEachExpressiion = true;
            }

            public boolean isInsideForEachExpressiion() {
                return insideForEachExpressiion;
            }
            
            public Set<? extends TypeMirror> getSmartTypes() throws IOException {
                if (smartTypes == null) {
                    smartTypes = JavaFXCompletionQuery.this.getSmartTypes(this);
                    if(smartTypes != null) {
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
        }
    }
}
