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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.highlightboxingunboxingvarargs.impl;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.highlightboxingunboxingvarargs.impl.HighlightBoxingUnboxingVarargsMarkDescriptor.Kind;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class HighlightBoxingUnboxingVarargsTask implements CancellableTask<CompilationInfo> {

    private FileObject fileObject;

    public HighlightBoxingUnboxingVarargsTask(FileObject fileObject) {
        this.fileObject = fileObject;
    }
    protected AtomicBoolean canceled = new AtomicBoolean();

    public void cancel() {
        canceled.set(true);
    }

    protected final synchronized boolean isCancelled() {
        return canceled.get();
    }

    protected final synchronized void resume() {
        canceled.set(false);
    }

    public void run(CompilationInfo compilationInfo) throws Exception {
        resume();

        Document doc = compilationInfo.getDocument();

        if (doc == null) {
            Logger.getLogger(HighlightBoxingUnboxingVarargsTask.class.getName()).log(Level.FINE, "HighlightHighlightUnboxingVarargs: Cannot get document!");
            return;
        }

        if (HighlightBoxingUnboxingVarargs.isHighlightBoxing()
                || HighlightBoxingUnboxingVarargs.isHighlightUnboxing()
                || HighlightBoxingUnboxingVarargs.isHighlightVarargs()) {
            CompilationUnitTree compilationUnitTree = compilationInfo.getCompilationUnit();
            List<HighlightBoxingUnboxingVarargsMarkDescriptor> highlights = new ArrayList<HighlightBoxingUnboxingVarargsMarkDescriptor>();
            new BoxingUnboxingVarargsScanner(compilationInfo, canceled).scan(compilationUnitTree, highlights);

            OffsetsBag bag = new OffsetsBag(doc);

            for (HighlightBoxingUnboxingVarargsMarkDescriptor span : highlights) {
                bag.addHighlight(span.getStartOffset(), span.getEndOffset(), kind2Coloring.get(span.getKind()));
            }

            if (isCancelled()) {
                return;
            }

            HighlighterFactory.getBag(doc).setHighlights(bag);
        } else {
            HighlighterFactory.getBag(doc).clear();
        }
    }

    private static Map<Kind, AttributeSet> kind2Coloring = new EnumMap<Kind, AttributeSet>(Kind.class);

    static void initKind2Coloring() {     
        kind2Coloring.put(Kind.BOXING, AttributesUtilities.createImmutable(StyleConstants.Background,
                HighlightBoxingUnboxingVarargs.getBoxingHighlightBackground(), EditorStyleConstants.Tooltip,
                NbBundle.getMessage(HighlightBoxingUnboxingVarargsTask.class, "LBL_Boxing")));
        kind2Coloring.put(Kind.UNBOXING, AttributesUtilities.createImmutable(StyleConstants.Background,
                HighlightBoxingUnboxingVarargs.getUnboxingHighlightBackground(), EditorStyleConstants.Tooltip,
                NbBundle.getMessage(HighlightBoxingUnboxingVarargsTask.class, "LBL_Unboxing")
                + NbBundle.getMessage(HighlightBoxingUnboxingVarargsTask.class, "WRN_NPE")));
        kind2Coloring.put(Kind.VARARGS, AttributesUtilities.createImmutable(StyleConstants.Background,
                HighlightBoxingUnboxingVarargs.getVarargsHighlightBackground(), EditorStyleConstants.Tooltip,
                NbBundle.getMessage(HighlightBoxingUnboxingVarargsTask.class, "LBL_VarArg")));
    }
    
    static {
        initKind2Coloring();
    }

    private static class BoxingUnboxingVarargsScanner extends TreePathScanner<Void, List<HighlightBoxingUnboxingVarargsMarkDescriptor>> {

        private static final EnumSet<TypeKind> unboxed =
                EnumSet.of(
                TypeKind.BOOLEAN,
                TypeKind.BYTE,
                TypeKind.CHAR,
                TypeKind.DOUBLE,
                TypeKind.FLOAT,
                TypeKind.INT,
                TypeKind.LONG,
                TypeKind.SHORT);

        private static final Set<String> boxed =
                Collections.unmodifiableSet(
                new HashSet<String>(Arrays.asList(
                Boolean.class.getName(),
                Byte.class.getName(),
                Character.class.getName(),
                Double.class.getName(),
                Float.class.getName(),
                Integer.class.getName(),
                Long.class.getName(),
                Number.class.getName(),
                Object.class.getName(),
                Short.class.getName())));
        
        private static boolean isBoxing(CompilationInfo compilationInfo, TypeMirror lhsTypeMirror, TypeMirror rhsTypeMirror) {
            // Is this an unboxed type
            if (unboxed.contains(rhsTypeMirror.getKind())) {
                if (boxed.contains(lhsTypeMirror.toString())) {
                    return (compilationInfo.getTypeUtilities().isCastable(rhsTypeMirror, lhsTypeMirror));
                }                
            }
            return false;
        }
        
        private static boolean isUnboxing(CompilationInfo compilationInfo, TypeMirror lhsTypeMirror, TypeMirror rhsTypeMirror) {
            // Is this an unboxed typecompilationInfo
            if (boxed.contains(rhsTypeMirror.toString())) {
                if (unboxed.contains(lhsTypeMirror.getKind())) {
                    return (compilationInfo.getTypeUtilities().isCastable(rhsTypeMirror, lhsTypeMirror));
                }                
            }
            return false;
        }
                
        private final CompilationInfo compilationInfo;
        private final AtomicBoolean canceled;

        public BoxingUnboxingVarargsScanner(CompilationInfo compilationInfo, AtomicBoolean canceled) {
            this.compilationInfo = compilationInfo;
            this.canceled = canceled;
        }

        @Override
        public Void visitAssignment(AssignmentTree assignmentTree, List<HighlightBoxingUnboxingVarargsMarkDescriptor> highlights) {
            ExpressionTree variable = assignmentTree.getVariable();
            if (variable != null) {
                TypeMirror variableTypeMirror = getTypeMirror(compilationInfo, variable);
                if (variableTypeMirror != null) {
                    ExpressionTree assignment = assignmentTree.getExpression();
                    if (assignment != null) {
                        TypeMirror assignmentTypeMirror = getTypeMirror(compilationInfo, assignment);
                        if (HighlightBoxingUnboxingVarargs.isHighlightBoxing()) {
                            if (isBoxing(compilationInfo, variableTypeMirror, assignmentTypeMirror)) {
                                addHighlightFor(compilationInfo, assignment, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                            } else {
                                if (variableTypeMirror.getKind() == TypeKind.ARRAY) {
                                    ArrayType variableArrayTypeMirror = (ArrayType) variableTypeMirror;
                                    TypeMirror variableComponentTypeMirror = variableArrayTypeMirror.getComponentType();
                                    if (variableComponentTypeMirror.getKind() != TypeKind.ARRAY) {
                                        if (assignment.getKind() == Tree.Kind.NEW_ARRAY) {
                                            NewArrayTree assignmentArray = (NewArrayTree) assignment;
                                            List<? extends ExpressionTree> initializers = assignmentArray.getInitializers();
                                            if (initializers != null) {
                                                for (ExpressionTree initializer : initializers) {
                                                    TypeMirror initializeTypeMirror = getTypeMirror(compilationInfo, initializer);
                                                    if (isBoxing(compilationInfo, variableComponentTypeMirror, initializeTypeMirror)) {
                                                        addHighlightFor(compilationInfo, initializer, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                                                    } 
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (HighlightBoxingUnboxingVarargs.isHighlightUnboxing()) {
                            if (isUnboxing(compilationInfo, variableTypeMirror, assignmentTypeMirror)) {
                                addHighlightFor(compilationInfo, assignment, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.UNBOXING, highlights);
                            } else {
                                if (variableTypeMirror.getKind() == TypeKind.ARRAY) {
                                    ArrayType variableArrayTypeMirror = (ArrayType) variableTypeMirror;
                                    TypeMirror variableComponentTypeMirror = variableArrayTypeMirror.getComponentType();
                                    if (variableComponentTypeMirror.getKind() != TypeKind.ARRAY) {
                                        if (assignment.getKind() == Tree.Kind.NEW_ARRAY) {
                                            NewArrayTree assignmentArray = (NewArrayTree) assignment;
                                            List<? extends ExpressionTree> initializers = assignmentArray.getInitializers();
                                            if (initializers != null) {
                                                for (ExpressionTree initializer : initializers) {
                                                    TypeMirror initializeTypeMirror = getTypeMirror(compilationInfo, initializer);
                                                    if (isUnboxing(compilationInfo, variableComponentTypeMirror, initializeTypeMirror)) {
                                                        addHighlightFor(compilationInfo, initializer, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.UNBOXING, highlights);
                                                    } 
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final Void visitAssignment = super.visitAssignment(assignmentTree, highlights);
            return visitAssignment;
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree methodInvocationTree, List<HighlightBoxingUnboxingVarargsMarkDescriptor> highlights) {
            List<? extends ExpressionTree> arguments = methodInvocationTree.getArguments();
            if (arguments != null) {
                boolean isVarargs = false;
                // If this is a varargs and there are more arguments
                Element methodElement = compilationInfo.getTrees().getElement(getTreePath(compilationInfo, methodInvocationTree));
                if (methodElement instanceof ExecutableElement && ((ExecutableElement)methodElement).isVarArgs()) {
                    isVarargs = true;
                }
                ExpressionTree methodSelectTree = methodInvocationTree.getMethodSelect();
                if (methodSelectTree != null) {
                    TypeMirror typeMirror = compilationInfo.getTrees().getTypeMirror(getTreePath(compilationInfo, methodSelectTree));
                    if (typeMirror != null) {
                        if (typeMirror.getKind() == TypeKind.EXECUTABLE) {
                            ExecutableType executableType = (ExecutableType) typeMirror;
                            List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                            Iterator<? extends ExpressionTree> argumentsIterator = arguments.iterator();
                            int i = 0;
                            for (TypeMirror parameterTypeMirror : parameterTypes) {
                                i++;
                                if (isVarargs && i >= parameterTypes.size()) {
                                    if (parameterTypeMirror.getKind() == TypeKind.ARRAY) {
                                        TypeMirror componentTypeMirror = ((ArrayType)parameterTypeMirror).getComponentType();
                                        if (HighlightBoxingUnboxingVarargs.isHighlightVarargs()) {
                                            int varArgsStart = -1;
                                            int varArgsEnd = -1;
                                            for (int j = (i-1); j < arguments.size(); j++) {
                                                ExpressionTree argument = arguments.get(j);
                                                if (varArgsStart == -1) {
                                                    varArgsStart =
                                                        (int) compilationInfo.getTrees().getSourcePositions().getStartPosition(compilationInfo.getCompilationUnit(), argument);
                                                }
                                                varArgsEnd = (int) compilationInfo.getTrees().getSourcePositions().getEndPosition(compilationInfo.getCompilationUnit(), argument);;                                            
                                            }
                                            if (varArgsStart != -1) {
                                                highlights.add(new HighlightBoxingUnboxingVarargsMarkDescriptor(HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.VARARGS, varArgsStart, varArgsEnd));
                                            }
                                        } else {
                                            // Go through again
                                            argumentsIterator = arguments.iterator();
                                        }
                                        for (int j = (i-1); j < arguments.size(); j++) {
                                            ExpressionTree argument = arguments.get(j);
                                            TypeMirror argumentTypeMirror = getTypeMirror(compilationInfo, argument);
                                            if (argumentTypeMirror != null) {
                                                if (HighlightBoxingUnboxingVarargs.isHighlightBoxing()) {
                                                    if (isBoxing(compilationInfo, componentTypeMirror, argumentTypeMirror)) {
                                                        addHighlightFor(compilationInfo, argument, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                                                    }
                                                }                                    
                                            }
                                        }
                                    }
                                    break;
                                } else {
                                    if (argumentsIterator.hasNext()) {
                                        ExpressionTree argument = argumentsIterator.next();
                                        TypeMirror argumentTypeMirror = getTypeMirror(compilationInfo, argument);
                                        if (argumentTypeMirror != null) {
                                            if (HighlightBoxingUnboxingVarargs.isHighlightBoxing()) {
                                                if (isBoxing(compilationInfo, parameterTypeMirror, argumentTypeMirror)) {
                                                    addHighlightFor(compilationInfo, argument, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                                                }
                                            }
                                            if (HighlightBoxingUnboxingVarargs.isHighlightUnboxing()) {
                                                if (isUnboxing(compilationInfo, parameterTypeMirror, argumentTypeMirror)) {
                                                    addHighlightFor(compilationInfo, argument, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.UNBOXING, highlights);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                }
            }
            final Void visitMethodInvocation = super.visitMethodInvocation(methodInvocationTree, highlights);
            return visitMethodInvocation;
        }

        @Override
        public Void visitNewClass(NewClassTree newClassTree, List<HighlightBoxingUnboxingVarargsMarkDescriptor> highlights) {
            List<? extends ExpressionTree> arguments = newClassTree.getArguments();
            if (arguments != null) {
                TreePath newClassTreePath = getTreePath(compilationInfo, newClassTree);                
                TypeMirror newClassTypeMirror = getTypeMirror(compilationInfo, newClassTree);
                if (newClassTypeMirror != null && newClassTypeMirror.getKind() == TypeKind.DECLARED) {
                    boolean isVarargs = false;
                    // If this is a varargs and there are more arguments
                    Element constructorElement = compilationInfo.getTrees().getElement(newClassTreePath);
                    if (constructorElement instanceof ExecutableElement && ((ExecutableElement)constructorElement).isVarArgs()) {
                        isVarargs = true;
                    }
                    TypeMirror constructorTypeMirror = compilationInfo.getTypes().asMemberOf((DeclaredType)newClassTypeMirror, constructorElement);
                    if (constructorTypeMirror != null) {
                        if (constructorTypeMirror.getKind() == TypeKind.EXECUTABLE) {
                            ExecutableType executableType = (ExecutableType) constructorTypeMirror;
                            List<? extends TypeMirror> parameterTypes = executableType.getParameterTypes();
                            Iterator<? extends ExpressionTree> argumentsIterator = arguments.iterator();
                            int i = 0;
                            for (TypeMirror parameterTypeMirror : parameterTypes) {
                                i++;
                                if (isVarargs && i >= parameterTypes.size()) {
                                    if (parameterTypeMirror.getKind() == TypeKind.ARRAY) {
                                        TypeMirror componentTypeMirror = ((ArrayType)parameterTypeMirror).getComponentType();
                                        if (HighlightBoxingUnboxingVarargs.isHighlightVarargs()) {
                                            int varArgsStart = -1;
                                            int varArgsEnd = -1;
                                            for (int j = (i-1); j < arguments.size(); j++) {
                                                ExpressionTree argument = arguments.get(j);
                                                if (varArgsStart == -1) {
                                                    varArgsStart =
                                                        (int) compilationInfo.getTrees().getSourcePositions().getStartPosition(compilationInfo.getCompilationUnit(), argument);
                                                }
                                                varArgsEnd = (int) compilationInfo.getTrees().getSourcePositions().getEndPosition(compilationInfo.getCompilationUnit(), argument);;                                            
                                            }
                                            if (varArgsStart != -1) {
                                                highlights.add(new HighlightBoxingUnboxingVarargsMarkDescriptor(HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.VARARGS, varArgsStart, varArgsEnd));
                                            }
                                        } else {
                                            // Go through again
                                            argumentsIterator = arguments.iterator();
                                        }
                                        for (int j = (i-1); j < arguments.size(); j++) {
                                            ExpressionTree argument = arguments.get(j);
                                            TypeMirror argumentTypeMirror = getTypeMirror(compilationInfo, argument);
                                            if (argumentTypeMirror != null) {
                                                if (HighlightBoxingUnboxingVarargs.isHighlightBoxing()) {
                                                    if (isBoxing(compilationInfo, componentTypeMirror, argumentTypeMirror)) {
                                                        addHighlightFor(compilationInfo, argument, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                                                    }
                                                }                                    
                                            }
                                        }
                                    }
                                    break;
                                } else {
                                    if (argumentsIterator.hasNext()) {
                                        ExpressionTree argument = argumentsIterator.next();
                                        TypeMirror argumentTypeMirror = getTypeMirror(compilationInfo, argument);
                                        if (argumentTypeMirror != null) {
                                            if (HighlightBoxingUnboxingVarargs.isHighlightBoxing()) {
                                                if (isBoxing(compilationInfo, parameterTypeMirror, argumentTypeMirror)) {
                                                    addHighlightFor(compilationInfo, argument, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                                                }
                                            }
                                            if (HighlightBoxingUnboxingVarargs.isHighlightUnboxing()) {
                                                if (isUnboxing(compilationInfo, parameterTypeMirror, argumentTypeMirror)) {
                                                    addHighlightFor(compilationInfo, argument, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.UNBOXING, highlights);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final Void visitNewClass = super.visitNewClass(newClassTree, highlights);
            return visitNewClass;
        }

        @Override
        public Void visitReturn(ReturnTree returnTree, List<HighlightBoxingUnboxingVarargsMarkDescriptor> highlights) {
            ExpressionTree returnExpressionTree = returnTree.getExpression();
            if (returnExpressionTree != null) {
                TypeMirror returnTypeMirror = getTypeMirror(compilationInfo, returnExpressionTree);
                if (returnTypeMirror != null) {
                    TreePath treePath = getTreePath(compilationInfo, returnTree);
                    TypeMirror methodRreturnTypeMirror = null;
                    while (treePath != null) {
                        Tree leafTree = treePath.getLeaf();
                        if (leafTree.getKind() == Tree.Kind.METHOD) {
                            MethodTree methodTree = (MethodTree) leafTree;
                            Tree returnTypeTree = methodTree.getReturnType();
                            methodRreturnTypeMirror = getTypeMirror(compilationInfo, returnTypeTree);
                            break;
                        }
                        treePath = treePath.getParentPath();
                    }
                    if (methodRreturnTypeMirror != null) {
                        if (HighlightBoxingUnboxingVarargs.isHighlightBoxing()) {
                            if (unboxed.contains(returnTypeMirror.getKind())) {
                                if (isBoxing(compilationInfo, methodRreturnTypeMirror, returnTypeMirror)) {
                                    addHighlightFor(compilationInfo, returnExpressionTree, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                                }
                            }
                        }
                        if (HighlightBoxingUnboxingVarargs.isHighlightUnboxing()) {
                            if (boxed.contains(returnTypeMirror.toString())) {
                                if (isUnboxing(compilationInfo, methodRreturnTypeMirror, returnTypeMirror)) {
                                    addHighlightFor(compilationInfo, returnExpressionTree, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.UNBOXING, highlights);
                                }
                            }
                        }
                    }
                }
            }
            final Void visitReturn = super.visitReturn(returnTree, highlights);
            return visitReturn;
        }

        @Override
        public Void visitVariable(VariableTree variableTree, List<HighlightBoxingUnboxingVarargsMarkDescriptor> highlights) {
            Tree variableType = variableTree.getType();
            if (variableType != null) {
                TypeMirror variableTypeMirror = getTypeMirror(compilationInfo, variableType);
                if (variableTypeMirror != null) {
                    ExpressionTree initializer = variableTree.getInitializer();
                    if (initializer != null) {
                        TypeMirror initializerTypeMirror = getTypeMirror(compilationInfo, initializer);
                        if (initializerTypeMirror != null) {
                            if (HighlightBoxingUnboxingVarargs.isHighlightBoxing()) {                               
                                if (isBoxing(compilationInfo, variableTypeMirror, initializerTypeMirror)) {
                                    addHighlightFor(compilationInfo, initializer, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                                } else {
                                    if (variableTypeMirror.getKind() == TypeKind.ARRAY) {
                                        ArrayType variableArrayTypeMirror = (ArrayType) variableTypeMirror;
                                        TypeMirror variableComponentTypeMirror = variableArrayTypeMirror.getComponentType();
                                        if (variableComponentTypeMirror.getKind() != TypeKind.ARRAY) {
                                            if (initializer.getKind() == Tree.Kind.NEW_ARRAY) {
                                                NewArrayTree initializerArray = (NewArrayTree) initializer;
                                                List<? extends ExpressionTree> inits = initializerArray.getInitializers();
                                                if (inits != null) {
                                                    for (ExpressionTree init : inits) {
                                                        TypeMirror initializeTypeMirror = getTypeMirror(compilationInfo, init);
                                                        if (isBoxing(compilationInfo, variableComponentTypeMirror, initializeTypeMirror)) {
                                                            addHighlightFor(compilationInfo, init, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.BOXING, highlights);
                                                        } 
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (HighlightBoxingUnboxingVarargs.isHighlightUnboxing()) {
                                if (isUnboxing(compilationInfo, variableTypeMirror, initializerTypeMirror)) {
                                    addHighlightFor(compilationInfo, initializer, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.UNBOXING, highlights);
                                } else {
                                    if (variableTypeMirror.getKind() == TypeKind.ARRAY) {
                                        ArrayType variableArrayTypeMirror = (ArrayType) variableTypeMirror;
                                        TypeMirror variableComponentTypeMirror = variableArrayTypeMirror.getComponentType();
                                        if (variableComponentTypeMirror.getKind() != TypeKind.ARRAY) {
                                            if (initializer.getKind() == Tree.Kind.NEW_ARRAY) {
                                                NewArrayTree initializerArray = (NewArrayTree) initializer;
                                                List<? extends ExpressionTree> inits = initializerArray.getInitializers();
                                                if (inits != null) {
                                                    for (ExpressionTree init : inits) {
                                                        TypeMirror initializeTypeMirror = getTypeMirror(compilationInfo, init);
                                                        if (isUnboxing(compilationInfo, variableComponentTypeMirror, initializeTypeMirror)) {
                                                            addHighlightFor(compilationInfo, init, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind.UNBOXING, highlights);
                                                        } 
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            final Void visitVariable = super.visitVariable(variableTree, highlights);
            return visitVariable;
        }

        public Void scan(Tree tree, List<HighlightBoxingUnboxingVarargsMarkDescriptor> p) {
            if (canceled.get()) {
                return null;
            }

            return super.scan(tree, p);
        }

        public Void scan(Iterable<? extends Tree> trees, List<HighlightBoxingUnboxingVarargsMarkDescriptor> p) {
            if (canceled.get()) {
                return null;
            }

            return super.scan(trees, p);
        }
    
        private static TreePath getTreePath(CompilationInfo compilationInfo, Tree tree) {
            return compilationInfo.getTrees().getPath(compilationInfo.getCompilationUnit(), tree);
        }

        private static TypeMirror getTypeMirror(CompilationInfo compilationInfo, Tree tree) {
            return compilationInfo.getTrees().getTypeMirror(getTreePath(compilationInfo, tree));
        }

        private static void addHighlightFor(CompilationInfo compilationInfo, Tree t, HighlightBoxingUnboxingVarargsMarkDescriptor.Kind kind, List<HighlightBoxingUnboxingVarargsMarkDescriptor> highlights) {
            int start = (int) compilationInfo.getTrees().getSourcePositions().getStartPosition(compilationInfo.getCompilationUnit(), t);
            int end   = (int) compilationInfo.getTrees().getSourcePositions().getEndPosition(compilationInfo.getCompilationUnit(), t);

            highlights.add(new HighlightBoxingUnboxingVarargsMarkDescriptor(kind, start, end));
        }
    }
}
