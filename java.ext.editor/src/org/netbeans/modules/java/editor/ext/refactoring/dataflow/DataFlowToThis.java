/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.editor.ext.refactoring.dataflow;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.ClassIndex.SearchKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.Task;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Exceptions;

/**
 *
 * @author lahvac
 */
public class DataFlowToThis {

    public static Collection<? extends UseDescription> findWrites(final UseDescription parent) {
        FileObject file = parent.tph.getFileObject();
        final Collection<UseDescription> result = new LinkedList<UseDescription>();

        if (parent.localOnly) {
            JavaSource js = JavaSource.forFileObject(file);

            try {
                js.runUserActionTask(new TaskImpl(parent, result), true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            try {
                ClassPath sp = ClassPath.getClassPath(file, ClassPath.SOURCE);
                FileObject root = sp.findOwnerRoot(file);
                URL rootURL = root.getURL();
                Set<URL> deps = new HashSet<URL>(SourceUtils.getDependentRoots(rootURL));

                deps.add(rootURL);
                
                ClassPath cp = ClassPathSupport.createClassPath(deps.toArray(new URL[0]));
                ClasspathInfo cpInfo = ClasspathInfo.create(ClassPath.EMPTY, ClassPath.EMPTY, cp);
                Set<ElementHandle<TypeElement>> toProcess = new HashSet<ElementHandle<TypeElement>>();
                List<ElementHandle<TypeElement>> queue = new LinkedList<ElementHandle<TypeElement>>();

                queue.add(parent.parent);
                
                while (!queue.isEmpty()) {
                    ElementHandle<TypeElement> eh = queue.remove(0);
                    
                    toProcess.add(eh);

                    queue.addAll(cpInfo.getClassIndex().getElements(eh, EnumSet.of(SearchKind.IMPLEMENTORS), EnumSet.of(SearchScope.SOURCE)));
                }

                queue.addAll(toProcess);

                SearchKind sk;

                switch (parent.referencedKind) {
                    case FIELD:
                    case ENUM_CONSTANT:
                        sk = SearchKind.FIELD_REFERENCES;
                        break;
                    case PARAMETER:
                        assert parent.referencedMethod != null;
                        sk = SearchKind.METHOD_REFERENCES;
                        break;
                    default:
                        throw new IllegalStateException(parent.referencedKind.toString());
                }

                while (!queue.isEmpty()) {
                    ElementHandle<TypeElement> eh = queue.remove(0);

                    toProcess.addAll(cpInfo.getClassIndex().getElements(eh, EnumSet.of(sk), EnumSet.of(SearchScope.SOURCE)));
                }
                
                Set<FileObject> files = new HashSet<FileObject>();

                for (ElementHandle<TypeElement> h : toProcess) {
                    files.add(SourceUtils.getFile(h, cpInfo));
                }

                for (FileObject f : files) {
                    JavaSource js = JavaSource.forFileObject(f);

                    try {
                        js.runUserActionTask(new TaskImpl(parent, result), true);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return result;
    }

    static void findWrites(CompilationInfo info, @NonNull Element el, @NullAllowed TreePath tp, Collection<? super UseDescription> result) {
        if (tp == null || tp.getParentPath().getLeaf().getKind() == Kind.METHOD) {
            if (el.getKind() == ElementKind.PARAMETER) {
                findWritesMethodInvocation(info, el, result);
            } else {
                findWritesToVariable(info, el, tp, result);
            }
        } else {
            findWritesToVariable(info, el, tp, result);
        }
    }
    
    private static void findWritesToVariable(CompilationInfo info, Element el, TreePath tp, Collection<? super UseDescription> result) {
        UseDescription defaultUse = null;
        Iterable<? extends TreePath> constructors;
        Iterable<? extends TreePath> methods;
        Iterable<? extends TreePath> initializers;
        TreePath declarationPath = tp != null ? info.getTrees()./*TODO:*/getPath(el) : null;
        
        switch (el.getKind()) {
            case PARAMETER:
                defaultUse = UseDescription.create(info, declarationPath);
            case LOCAL_VARIABLE:
            case EXCEPTION_PARAMETER:
                constructors = Collections.<TreePath>emptySet();
                methods = Collections.singleton(declarationPath.getParentPath());
                initializers = Collections.<TreePath>emptySet();
                break;

            default:
                if (declarationPath != null) {
                    VariableTree vt = (VariableTree) declarationPath.getLeaf();
                    
                    if (vt.getInitializer() != null) {
                        defaultUse = UseDescription.create(info, new TreePath(declarationPath, vt.getInitializer()));
                    }
                }

                FindMethods fm = new FindMethods();
                
                fm.scan(info.getCompilationUnit(), null);

                constructors = fm.constructors;
                methods = fm.methods;
                initializers = fm.initializers;

        }

        for (TreePath c : constructors) {
            FindWrites f = new FindWrites(info, el, tp, defaultUse);
            BlockTree body = ((MethodTree) c.getLeaf()).getBody();
            TreePath bodyPath = new TreePath(c, body);

            f.scan(new TreePath(bodyPath, body.getStatements().get(0)), null);

            for (TreePath i : initializers) {
                f.scan(i, null);
            }

            for (Tree statement : body.getStatements().subList(0, body.getStatements().size())) {
                f.scan(new TreePath(bodyPath, statement), null);
            }

            result.addAll(f.current);
        }

        if (constructors.iterator().hasNext()) {
            defaultUse = null;
        }

        for (TreePath m : methods) {
            FindWrites f = new FindWrites(info, el, tp, defaultUse);

            f.scan(m, null);

            result.addAll(f.current);
        }
    }
    
    private static final class FindWrites extends TreePathScanner<Void, Void> {

        private final CompilationInfo info;
        private final Element lookingFor;
        private final TreePath stopAt;

        private       Set<UseDescription> current = new HashSet<UseDescription>();
        private       boolean finished;

        public FindWrites(CompilationInfo info, Element lookingFor, TreePath stopAt) {
            this(info, lookingFor, stopAt, null);
        }

        public FindWrites(CompilationInfo info, Element lookingFor, TreePath stopAt, UseDescription defaultUse) {
            this.info = info;
            this.lookingFor = lookingFor;
            this.stopAt = stopAt;

            if (defaultUse != null) {
                current.add(defaultUse);
            }
        }

        @Override
        public Void scan(Tree tree, Void p) {
            if (stopAt != null && tree == stopAt.getLeaf()) {
                finished = true;
            }

            if (finished) {
                return null;
            }

            return super.scan(tree, p);
        }

        @Override
        public Void visitAssignment(AssignmentTree node, Void p) {
            Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node.getVariable()));

            if (lookingFor.equals(e)) {
                current.clear();
                current.add(UseDescription.create(info, new TreePath(getCurrentPath(), node.getExpression())));
            }
            
            return super.visitAssignment(node, p);
        }

        @Override
        public Void visitIf(IfTree node, Void p) {
            scan(node.getCondition(), null);

            Set<UseDescription> currentBak = new HashSet<UseDescription>(current);

            scan(node.getThenStatement(), null);

            Set<UseDescription> afterThen = current;
            current = currentBak;

            scan(node.getElseStatement(), null);

            current.addAll(afterThen);
            
            return null;
        }

        @Override
        public Void visitVariable(VariableTree node, Void p) {
            Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), node));

            if (lookingFor.equals(e) && node.getInitializer() != null) {
                current.clear();
                current.add(UseDescription.create(info, new TreePath(getCurrentPath(), node.getInitializer())));
            }

            return super.visitVariable(node, p);
        }

        //TODO: should not dive into anonymous/local classes

    }

    private static final class FindMethods extends TreePathScanner<Void, Void> {
        private final Set<TreePath> constructors = new HashSet<TreePath>();
        private final Set<TreePath> methods = new HashSet<TreePath>();
        private final Set<TreePath> initializers = new HashSet<TreePath>();

        @Override
        public Void scan(Tree tree, Void p) {
            return super.scan(tree, p);
        }

        @Override
        public Void visitMethod(MethodTree node, Void p) {
            if (node.getReturnType() == null) { //XXX: not guaranteed to be a constructor!
                constructors.add(getCurrentPath());
            } else {
                methods.add(getCurrentPath());
            }
            return super.visitMethod(node, p);
        }

        @Override
        public Void visitBlock(BlockTree node, Void p) {
            if (getCurrentPath().getParentPath().getLeaf().getKind() == Kind.CLASS) {
                if (node.isStatic()) {
                    //static initializer:
                    methods.add(getCurrentPath());
                } else {
                    //instance initializer:
                    initializers.add(getCurrentPath());
                }
            }
            return super.visitBlock(node, p);
        }

    }

    private static void findWritesMethodInvocation(final CompilationInfo info, Element param, final Collection<? super UseDescription> result) {
        final ExecutableElement method = (ExecutableElement) param.getEnclosingElement();

        if (method == null) {
            return ;
        }

        int paramIndex = 0;

        for (VariableElement p : method.getParameters()) {
            if (p.equals(param)) {
                break;
            }
            
            paramIndex++;
        }

        assert paramIndex < method.getParameters().size();

        final int paramFin = paramIndex;

        new TreePathScanner<Void, Void>() {
            @Override
            public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
                handleCurrentPath(node.getArguments());
                return super.visitMethodInvocation(node, p);
            }

            @Override
            public Void visitNewClass(NewClassTree node, Void p) {
                handleCurrentPath(node.getArguments());
                return super.visitNewClass(node, p);
            }

            private void handleCurrentPath(List<? extends ExpressionTree> params) {
                Element e = info.getTrees().getElement(getCurrentPath());

                if (method.equals(e)) {
                    //TODO: varargs:
                    result.add(UseDescription.create(info, new TreePath(getCurrentPath(), params.get(paramFin))));
                }

            }
        }.scan(info.getCompilationUnit(), null);
    }

    private static class TaskImpl implements Task<CompilationController> {

        private final UseDescription parent;
        private final Collection<UseDescription> result;

        public TaskImpl(UseDescription parent, Collection<UseDescription> result) {
            this.parent = parent;
            this.result = result;
        }

        public void run(CompilationController cc) throws Exception {
            cc.toPhase(Phase.RESOLVED);

            Element el;
            TreePath tp;
            
            if (cc.getFileObject().equals(parent.tph.getFileObject())) {
                tp = parent.tph.resolve(cc);
                el = cc.getTrees().getElement(tp);
            } else {
                if (parent.referencedMethod != null) {
                    ExecutableElement ee = parent.referencedMethod.resolve(cc);

                    el = ee.getParameters().get(parent.index);
                    tp = null;
                } else {
                    el = parent.tph.resolveElement(cc);
                    tp = null;
                }
            }
            
            if (el == null) {
                return ;
            }

            findWrites(cc, el, tp, result);
        }
    }
}
