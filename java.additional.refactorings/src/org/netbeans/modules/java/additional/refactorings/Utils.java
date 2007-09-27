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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.SearchKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Various static utility methods that are helpful for writing refactorings
 *
 * @author Tim Boudreau
 */
public abstract class Utils {
    private Utils() {}
    
    public static List <TypeMirror> toTypeMirrors (Iterable <? extends TypeMirrorHandle> types, CompilationInfo info) {
        List <TypeMirror> result = new ArrayList <TypeMirror> ();
        for (TypeMirrorHandle h : types) {
            result.add (h.resolve(info));
        }
        return result;
    }
    
    public static List <TypeMirrorHandle> toTypeMirrorHandles (Iterable <? extends TypeMirror> types) {
        List <TypeMirrorHandle> result = new ArrayList <TypeMirrorHandle> ();
        for (TypeMirror h : types) {
            result.add (TypeMirrorHandle.create(h));
        }
        return result;
    }
        
    public static <T extends Tree> List <T> toTrees (Iterable <? extends TreePathHandle> handles, CompilationInfo info) {
        List <T> result = new ArrayList <T> (handles instanceof Collection ? 
            ((Collection)handles).size() : 11);
        for (TreePathHandle handle : handles) {
            TreePath path = handle.resolve(info);
            if (path == null) {
                try {
                    JavaSource src = JavaSource.forFileObject(handle.getFileObject());
                    TreePathResolver res = new TreePathResolver(handle);
                    if (res.cancelled) break;
                    path = res.path;
                    src.runUserActionTask(res, true);
                } catch (IOException ex) {
                    Exceptions.printStackTrace( ex );
                }
            }
            if (path != null) {
            @SuppressWarnings("unchecked")
            T item = (T) path.getLeaf();
            result.add (item);
        }
        }
        return result;
    }
    
    private static final class TreePathResolver implements CancellableTask <CompilationController> {
        private volatile boolean cancelled = false;
        TreePath path;
        private final TreePathHandle handle;
        TreePathResolver (TreePathHandle handle) {
            this.handle = handle;
        }
        public void cancel() {
            cancelled = true;
        }

        public void run(CompilationController cc) throws Exception {
            if (cancelled) return;
            path = handle.resolve(cc);
        }

    }

    public static List <TreePathHandle> toHandles (TreePath parent, Iterable <? extends Tree> trees, CompilationInfo info) {
        List <TreePathHandle> result = new ArrayList <TreePathHandle> (
                trees instanceof Collection ? ((Collection)trees).size() : 11);
        for (Tree tree : trees) {
            TreePath path = TreePath.getPath(parent, tree);
            TreePathHandle handle = TreePathHandle.create(path, info);
            result.add (handle);
            assert handle.resolve(info) != null : "Newly created TreePathHandle resolves to null"; //NOI18N
            assert handle.resolve(info).getLeaf() != null : "Newly created TreePathHandle.getLeaf() resolves to null"; //NOI18N            
        }
        return result;
    }
    
    public static List <TreePathHandle> toHandles (Iterable <? extends Tree> trees, CompilationInfo info) {
        List <TreePathHandle> result = new ArrayList <TreePathHandle> (trees instanceof Collection ? 
            ((Collection)trees).size() : 11);
        for (Tree tree : trees) {
            TreePath path = TreePath.getPath(info.getCompilationUnit(), tree);            
            if (path == null) {
                throw new IllegalArgumentException (tree + " does not belong to " + //NOI18N
                        "the same compilation unit passed to this method"); //NOI18N
            }
            TreePathHandle handle = TreePathHandle.create(path, info);
            result.add (handle);
            assert handle.resolve(info) != null : "Newly created TreePathHandle resolves to null"; //NOI18N
            assert handle.resolve(info).getLeaf() != null : "Newly created TreePathHandle.getLeaf() resolves to null"; //NOI18N            
        }
        return result;
    }
    
    public static <T extends Element> List <ElementHandle<T>> toHandles (Iterable <? extends T> elements) {
        List <ElementHandle<T>> result = new ArrayList <ElementHandle<T>> (elements instanceof 
                Collection ? ((Collection)elements).size() : 11);
        for (T element : elements) {
            ElementHandle<T> handle = ElementHandle.<T>create(element);
            assert handle != null : "Couldn't create handle for " + element; //NOI18N
            result.add (handle);
        }
        return result;
    }
    
    public static <T extends Element> Map <T, ElementHandle<T>> mapHandles (Iterable <? extends T> elements) {
        Map <T, ElementHandle<T>> result = new HashMap <T, ElementHandle<T>> (elements instanceof 
                Collection ? ((Collection)elements).size() : 11);
        for (T element : elements) {
            ElementHandle<T> handle = ElementHandle.<T>create(element);
            assert handle != null : "Couldn't create handle for " + element; //NOI18N
            result.put (element, handle);
        }
        return result;
    }
    
    
    public static <T extends Element> List <T> fromElementHandles (Iterable <ElementHandle<T>> handles, CompilationInfo info) {
        List <T> result = new ArrayList <T> (handles instanceof Collection ? ((Collection)handles).size() : 0);
        for (ElementHandle<? extends T> h : handles) {
            T element = h.resolve(info);
            assert element != null : element + " resolves to null"; //NOI18N            
            result.add (element);
        }
        return result;
    }
    
    public static TypeMirror hackFqn (TypeMirror mirror, TreeMaker maker, Trees trees, CompilationInfo info) {
        //Hack to eliminate FQNs
        Tree typeTree = maker.Type(mirror);
        TreePath path = TreePath.getPath (info.getCompilationUnit(), typeTree);
        TypeMirror result = null;
        if (path != null) {
            try {
                result = trees.getTypeMirror(path);
            } catch (NullPointerException npe) {
                Exceptions.printStackTrace(npe);
            }
        }
        return result == null ? mirror : result;
    }
    
    public static Tree cloneTree (Tree old, Element element, TreeMaker maker, WorkingCopy wc) {
        Tree result = null;
        switch (element.getKind()) {
            case METHOD :
            {
                MethodTree mt = (MethodTree) old;
                List <? extends VariableTree> parameters = new ArrayList <VariableTree> (mt.getParameters());
                List <? extends TypeParameterTree> typeParameters = new ArrayList <TypeParameterTree> (mt.getTypeParameters());
                List <? extends ExpressionTree> throes = new ArrayList <ExpressionTree> (mt.getThrows());
                Tree ret = mt.getReturnType();
                BlockTree body = mt.getBody();
                ExpressionTree defaultValue = (ExpressionTree) 
                        mt.getDefaultValue();
                String name = mt.getName().toString();
                ModifiersTree modifiers = mt.getModifiers();
                result = maker.Method(
                        modifiers, name, ret, typeParameters, parameters, 
                        throes, body, defaultValue);
            }   
                break;
            case CONSTRUCTOR :
            {
                MethodTree mt  = (MethodTree) old;
                List <? extends VariableTree> parameters = new ArrayList <VariableTree> (mt.getParameters());
                List <? extends TypeParameterTree> typeParameters = new ArrayList <TypeParameterTree> (mt.getTypeParameters());
                List <? extends ExpressionTree> throes = new ArrayList <ExpressionTree> (mt.getThrows());
                BlockTree body = mt.getBody();
                
                CompilationUnitTree cut = wc.getCompilationUnit();
                SourcePositions sp = wc.getTrees().getSourcePositions();
                int start = (int) sp.getStartPosition(cut, body);
                int end = (int) sp.getEndPosition(cut, body);
                // get body text from source text
                String bodyText = wc.getText().substring(start, end);                
                
                ExpressionTree defaultValue = (ExpressionTree) 
                        mt.getDefaultValue();
                ModifiersTree modifiers = mt.getModifiers();
                result = maker.Method(
                        modifiers, "<init>", null, 
                        typeParameters, parameters, 
                        throes, bodyText, defaultValue);
            }
                break;
            case FIELD :
                VariableTree ft = (VariableTree) old;
                VariableElement ve = (VariableElement) element;
                //XXX bug in Retouche:  If we use ft.getType(), we will
                //get an FQN;  if we create a new type object, works correctly.
                Tree typeTree = maker.Type(ve.asType());
                ModifiersTree mt = ft.getModifiers();
                result = maker.Variable(mt, ft.getName().toString(), 
                        typeTree, ft.getInitializer());
                break;
            case CLASS :
                ClassTree ct = (ClassTree) old;
                ModifiersTree modifiers = ct.getModifiers();
                String simpleName = ct.getSimpleName().toString();
                List <? extends TypeParameterTree> typeParameters = 
                        new ArrayList <TypeParameterTree> (ct.getTypeParameters());
                List <? extends Tree> implementsClauses = 
                        new ArrayList <Tree> (ct.getImplementsClause());
                List <? extends Tree> memberDecls = 
                        new ArrayList <Tree> (ct.getMembers());
                
                result = maker.Class(modifiers, 
                        simpleName, 
                        typeParameters, 
                        result, 
                        implementsClauses, 
                        memberDecls);
                
                break;
            default :
                result = null;
        }
        return result;
    }        
    
    public static List <TreePath> toPaths (TreePath parent, List <? extends Tree> trees, CompilationInfo info) {
        List <TreePath> result = new ArrayList <TreePath> (trees.size());
        for (Tree tree : trees) {
            TreePath path = TreePath.getPath(parent, tree);
            result.add (path);
        }
        return result;
    }    
    
    public static List <TreePathHandle> fromElements(Iterable <? extends Element> elements, CompilationInfo info) {
        List <TreePathHandle> result = new ArrayList <TreePathHandle> (elements 
                instanceof Collection ? ((Collection) elements).size() : 11);
        for (Element e : elements) {
            TreePathHandle handle = TreePathHandle.create(e, info);
            result.add (handle);
        }
        return result;
    }
    
    public static List <TreePath> toPaths(Iterable <? extends Element> elements, Trees trees) {
        List <TreePath> result = new ArrayList <TreePath> (elements instanceof Collection ? 
            ((Collection)elements).size() : 11);
        for (Element e : elements) {
            TreePath path = trees.getPath(e);
            result.add (path);
        }
        return result;
    }    
    
    public static <T extends Element> List <T> toElements(Iterable <? extends TreePathHandle> handles, CompilationInfo info) {
        List <T> result = new ArrayList <T> (handles instanceof Collection ? 
            ((Collection)handles).size() : 11);
        for (TreePathHandle handle : handles) {
            @SuppressWarnings("unchecked")
            T element = (T) handle.resolveElement(info);
            result.add (element);
        }
        return result;
    }
    
    public static boolean isStatic (Element el, Scope scope, CompilationController cc) {
        return cc.getTreeUtilities().isStaticContext(scope) ||
                el.getModifiers().contains (Modifier.STATIC);
    }
    
    public static BlockTree findBlockTree (Tree tree) {
        BlockTree result = null;
        if (tree instanceof MethodTree) {
            MethodTree mt = (MethodTree) tree;
            result = mt.getBody();
        } else if (tree instanceof BlockTree) {
            result = (BlockTree) tree;
        }
        return result;
    }
    
    public static <T extends Tree> T resolveTreePathHandle (final TreePathHandle handle) throws IOException {
        FileObject fob = handle.getFileObject();
        JavaSource src = JavaSource.forFileObject(fob);
        TreeFinder<T> finder = new TreeFinder<T>(handle);
        src.runUserActionTask(finder, true);
        return finder.tree;
    }
    
    public static <T extends Tree> Iterable <T> resolveTreePathHandles (Iterable<TreePathHandle> handles) throws IOException {
        List <T> result = new ArrayList <T> (handles instanceof Collection ? ((Collection) handles).size() : 10);
        for (TreePathHandle handle : handles) {
            T tree = Utils.<T>resolveTreePathHandle(handle);
            result.add (tree);
        }
        return result;
    }
    
    
    private static class TreeFinder <T extends Tree> implements CancellableTask <CompilationController> {
        T tree;
        private final TreePathHandle handle;
        boolean cancelled;
        TreeFinder (TreePathHandle handle) {
            this.handle = handle;
        }
        public void cancel() {
            cancelled = true;
        }

        @SuppressWarnings("unchecked")
        public void run(CompilationController cc) throws Exception {
            cc.toPhase (Phase.RESOLVED);
            TreePath path = handle.resolve(cc);
            assert path != null : "Null path for " + handle; //NOI18N
            tree = (T) path.getLeaf();
        }
    }
    
    private static class TreeFromElementFinder implements CancellableTask <CompilationController> {
        private volatile boolean cancelled;
        Tree tree;
        TreePathHandle handle;
        private final ElementHandle element;
        TreeFromElementFinder (ElementHandle element) {
            this.element = element;
        }
        
        public void cancel() {
            cancelled = true;
        }

        @SuppressWarnings("unchecked")
        public void run(CompilationController cc) throws Exception {
            if (cancelled) return;
            cc.toPhase(Phase.RESOLVED);
            Element e = element.resolve(cc);
            tree = cc.getTrees().getTree(e);
            assert tree != null : "Got null tree for " + element + " on " + cc.getFileObject().getPath();
            if (cancelled) return;
            CompilationUnitTree unit = cc.getCompilationUnit();
            TreePath path = TreePath.getPath(unit, tree);
            assert path != null : "Got null tree path for " + cc.getFileObject().getPath() + " tree is " + tree;
            handle = TreePathHandle.create(path, cc);
        }
    }

    public static Collection<TreePathHandle> getOverridingMethodHandles (ExecutableElement e, CompilationController cc) throws IOException {
        Collection <ElementHandle<ExecutableElement>> mtds = getOverridingMethods (e, cc);
        Set <TreePathHandle> result = new HashSet <TreePathHandle> ();
        ElementHandle<ExecutableElement> toFind = ElementHandle.<ExecutableElement>create(e);
        for (ElementHandle<ExecutableElement> element : mtds) {
            FileObject fob = SourceUtils.getFile(element, cc.getClasspathInfo());
            JavaSource src = JavaSource.forFileObject(fob);
            assert src.getFileObjects().contains(fob);
            TreeFromElementFinder finder = new TreeFromElementFinder (element);
            src.runUserActionTask(finder, false);
            if (finder.handle != null) {
                result.add (finder.handle);
            }
        }
        return result;
    }
    
    public static Collection<ElementHandle<ExecutableElement>> getOverridingMethods(ExecutableElement e, CompilationInfo info) {
        //Copied from RetoucheUtils
        Collection<ElementHandle<ExecutableElement>> result = new ArrayList <ElementHandle<ExecutableElement>> ();
        TypeElement parentType = (TypeElement) e.getEnclosingElement();
        //XXX: Fixme IMPLEMENTORS_RECURSIVE were removed
        Set<ElementHandle<TypeElement>> subTypes = info.getClasspathInfo().getClassIndex().getElements(ElementHandle.create(parentType),  EnumSet.of(ClassIndex.SearchKind.IMPLEMENTORS),EnumSet.of(ClassIndex.SearchScope.SOURCE));
        for (ElementHandle<TypeElement> subTypeHandle: subTypes){
            TypeElement type = subTypeHandle.resolve(info);
            for (ExecutableElement method: ElementFilter.methodsIn(type.getEnclosedElements())) {
                if (info.getElements().overrides(method, e, type)) {
                    result.add(ElementHandle.<ExecutableElement>create(method));
                }
            }
        }
        return result;
    }    
    
    public static Collection <TreePathHandle> getInvocationsOf(ElementHandle e, CompilationController wc) throws IOException {
        assert e != null;
        assert wc != null;
        wc.toPhase (Phase.RESOLVED);
        Element element = e.resolve(wc);
        TypeElement type = wc.getElementUtilities().enclosingTypeElement(element);
        ElementHandle<TypeElement> elh = ElementHandle.<TypeElement>create(type);
        assert elh != null;
        //XXX do I want the enclosing type element for elh here?
        Set <ElementHandle<TypeElement>> classes = wc.getClasspathInfo().getClassIndex().getElements(elh, EnumSet.<SearchKind>of (SearchKind.METHOD_REFERENCES), EnumSet.<SearchScope>of(SearchScope.SOURCE));
        List <TreePathHandle> result = new ArrayList <TreePathHandle> ();
        for (ElementHandle<TypeElement> h : classes) {
            result.addAll (getReferencesToMember(h, wc.getClasspathInfo(), e));
        }
        return result;
    }
    
    /**
     * Get all of the references to the given member element (which may be part of another type) on
     * the passed element.
     * @param on A type which presumably refers to the passed element
     * @param toFind An element, presumably a field or method, of some type (not necessarily the passed one)
     */ 
    public static Collection <TreePathHandle> getReferencesToMember (ElementHandle<TypeElement> on, ClasspathInfo info, ElementHandle toFind) throws IOException {
        FileObject ob = SourceUtils.getFile(on, info);
        assert ob != null : "SourceUtils.getFile(" + on + ") returned null"; //NOI18N        
        JavaSource src = JavaSource.forFileObject(ob);
        InvocationScanner scanner = new InvocationScanner (toFind);
        src.runUserActionTask(scanner, true);
        return scanner.usages;
    }
    
    private static final class InvocationScanner extends TreePathScanner <Tree, ElementHandle> implements CancellableTask <CompilationController> {
        private CompilationController cc;
        private final ElementHandle toFind;
        InvocationScanner (ElementHandle toFind) {
            this.toFind = toFind;
        }

        @Override
        public Tree visitMemberSelect(MemberSelectTree node, ElementHandle p) {
            assert cc != null;
            Element e = p.resolve(cc);
            addIfMatch(getCurrentPath(), node, e);
            return super.visitMemberSelect(node, p);
        }
        
        private void addIfMatch(TreePath path, Tree tree, Element elementToFind) {
            if (cc.getTreeUtilities().isSynthetic(path))
                return;
            
            Element el = cc.getTrees().getElement(path);
            if (el==null)
                return;

            if (elementToFind.getKind() == ElementKind.METHOD && el.getKind() == ElementKind.METHOD) {
                if (el.equals(elementToFind) || cc.getElements().overrides(((ExecutableElement) el), (ExecutableElement) elementToFind, (TypeElement) elementToFind.getEnclosingElement())) {
                    addUsage(getCurrentPath());
                }
            } else if (el.equals(elementToFind)) {
                addUsage(getCurrentPath());
            }
        }
        
        Set <TreePathHandle> usages = new HashSet <TreePathHandle> ();
        void addUsage (TreePath path) {
            usages.add (TreePathHandle.create(path, cc));
        }

        boolean cancelled;
        public void cancel() {
            cancelled = true;
        }

        public void run(CompilationController cc) throws Exception {
            if (cancelled) return;
            cc.toPhase(Phase.RESOLVED);
            if (cancelled) return;
            this.cc = cc;
            try {
                TreePath path = new TreePath (cc.getCompilationUnit());
                scan (path, toFind);
            } finally {
                this.cc = null;
            }
        }
    }
    
    private static Collection<ExecutableElement> getOverriddenMethods(ExecutableElement e, TypeElement parent, CompilationInfo info) {
        //Copied from RetoucheUtils
        ArrayList<ExecutableElement> result = new ArrayList<ExecutableElement>();
        TypeMirror sup = parent.getSuperclass();
        if (sup.getKind() == TypeKind.DECLARED) {
            TypeElement next = (TypeElement) ((DeclaredType)sup).asElement();
            ExecutableElement overriden = getMethod(e, next, info);
                result.addAll(getOverriddenMethods(e,next, info));
            if (overriden!=null) {
                result.add(overriden);
            }
        }
        for (TypeMirror tm:parent.getInterfaces()) {
            TypeElement next = (TypeElement) ((DeclaredType)tm).asElement();
            ExecutableElement overriden2 = getMethod(e, next, info);
            result.addAll(getOverriddenMethods(e,next, info));
            if (overriden2!=null) {
                result.add(overriden2);
            }
        }
        return result;
    }    
    
    private static ExecutableElement getMethod(ExecutableElement method, TypeElement type, CompilationInfo info) {
        //Copied from RetoucheUtils
        for (ExecutableElement met: ElementFilter.methodsIn(type.getEnclosedElements())){
            if (info.getElements().overrides(method, met, type)) {
                return met;
            }
        }
        return null;
    }
    
    public static <R, D> Map <TreePathHandle, Map<TreeVisitor<R,D>, R>> runAgainstSources (Iterable<TreePathHandle> handles, D arg, TreeVisitor<R,D>... visitors) throws IOException {
        Map <TreePathHandle, Map<TreeVisitor<R,D>, R>> results = new HashMap<TreePathHandle, Map<TreeVisitor<R, D>, R>>();
        for (TreePathHandle handle : handles) {
            FileObject fob = handle.getFileObject();
            JavaSource src = JavaSource.forFileObject(fob);
            MultiVisitorRunner<R, D> runner = new MultiVisitorRunner <R, D> (handle, arg, visitors);
            src.runUserActionTask(runner, true);
            results.put (handle, runner.results);
        }
        return results;
    }
    
    public static void runAgainstSources (Iterable <TreePathHandle> handles, CancellableTask<CompilationController> c) throws IOException {
        for (TreePathHandle handle : handles) {
            FileObject fob = handle.getFileObject();
            JavaSource src = JavaSource.forFileObject(fob);
            src.runUserActionTask(c, true);
        }
    }
    
    public static <T> void runAgainstSources (Iterable <TreePathHandle> handles,TreePathHandleTask<T> t, T arg) throws IOException {
        for (TreePathHandle handle : handles) {
            FileObject file = handle.getFileObject();
            t.handle = handle;
            t.arg = arg;
            t.file = file;
            JavaSource src = JavaSource.forFileObject(file);
            src.runUserActionTask(t, true);
        }
        t.handle = null;
        t.arg = null;
        t.file = null;
    }
    
    public abstract static class TreePathHandleTask<T> implements CancellableTask <CompilationController> {
        protected boolean cancelled;
        private TreePathHandle handle;
        private T arg;
        private FileObject file;
        public void cancel() {
            cancelled = true;
        }

        public final void run(CompilationController cc) throws Exception {
            cc.toPhase (Phase.RESOLVED);
            run (cc, handle, file, arg);
        }
        
        public abstract void run (CompilationController cc, TreePathHandle handle, FileObject file, T arg);
    }
    
    private static class MultiVisitorRunner <R, D> implements CancellableTask <CompilationController> {
        private final Map <TreeVisitor<R,D>, R> results = new HashMap <TreeVisitor<R,D>, R> ();
        private final TreeVisitor<R,D>[] visitors;
        private final D arg;
        private TreePathHandle handle;
        MultiVisitorRunner (TreePathHandle handle, D arg, TreeVisitor<R, D>... visitors) {
            this.visitors = visitors;
            this.handle = handle;
            this.arg = arg;
        }
        
        R getResult (TreeVisitor visitor) {
            return results.get(visitor);
        }

        volatile boolean cancelled;
        public void cancel() {
            cancelled = true;
        }

        public void run(CompilationController cc) throws Exception {
            if (cancelled) return;
            TreePath path = handle.resolve(cc);
            if (arg instanceof ParameterChangeContext) {
                ParameterChangeContext pcc = (ParameterChangeContext) arg;
                pcc.changeData.scanContext.setCompilationInfo(cc);
            }
            for (TreeVisitor<R,D> v : visitors) {
                if (cancelled) return;
                R result;
                if (v instanceof TreePathScanner) {
                    @SuppressWarnings("unchecked") //NOI18N
                    TreePathScanner<R,D> scanner = (TreePathScanner<R,D>) v;
                    result = scanner.scan(path, arg);
                } else if (v instanceof TreeScanner) {
                    @SuppressWarnings("unchecked") //NOI18N
                    TreeScanner<R,D> scanner = (TreeScanner<R,D>) v;
                    result = scanner.scan(path.getLeaf(), arg);
                } else {
                    result = path.getLeaf().accept(v, arg);
                }
                results.put (v, result);
            }
        }
    }

    /**
     * finds the nearest enclosing ClassTree on <code>path</code> that
     * is class or interface or enum or annotation type and is or is not annonymous.
     * In case no ClassTree is found the first top level ClassTree is returned.
     *
     * Especially useful for selecting proper tree to refactor.
     *
     * @param javac javac
     * @param path path to search
     * @param isClass stop on class
     * @param isInterface  stop on interface
     * @param isEnum stop on enum
     * @param isAnnotation stop on annotation type
     * @param isAnonymous check if class or interface is annonymous
     * @return path to the enclosing ClassTree
     */
    public static TreePath findEnclosingClass(CompilationInfo javac, TreePath path, boolean isClass, boolean isInterface, boolean isEnum, boolean isAnnotation, boolean isAnonymous) {
        Tree selectedTree = path.getLeaf();
        TreeUtilities utils = javac.getTreeUtilities();
        while(true) {
            if (Tree.Kind.CLASS == selectedTree.getKind()) {
                ClassTree classTree = (ClassTree) selectedTree;
                if (isEnum && utils.isEnum(classTree)
                        || isInterface && utils.isInterface(classTree)
                        || isAnnotation && utils.isAnnotation(classTree)
                        || isClass && !(utils.isInterface(classTree) || utils.isEnum(classTree) || utils.isAnnotation(classTree))) {

                    Tree.Kind parentKind = path.getParentPath().getLeaf().getKind();
                    if (isAnonymous || Tree.Kind.NEW_CLASS != parentKind) {
                        break;
}
                }
            }

            path = path.getParentPath();
            if (path == null) {
                selectedTree = javac.getCompilationUnit().getTypeDecls().get(0);
                path = javac.getTrees().getPath(javac.getCompilationUnit(), selectedTree);
                break;
            }
            selectedTree = path.getLeaf();
        }
        return path;
    }

    public static boolean isParentPath (TreePath targetParent, TreePath test) {
        assert test != null;
        assert targetParent != null;
        assert test.getLeaf() != null;
        assert targetParent.getLeaf() != null;
        if (test.getLeaf().equals(targetParent.getLeaf())) {
            return true;
        }
        do {
            test = test.getParentPath();
        } while (test != null && !test.getLeaf().equals(targetParent));
        boolean result = test != null;
        System.err.println("ipp " + result);
        return result;
    }

//    private static boolean isEnclosedBy (TypeElement el, TypeElement maybeParent, ElementUtilities utils) {
//        if (el.equals(maybeParent)) return true;
//        TypeElement test = el;
//        boolean result = false;
//        do {
//            System.err.println("Check " + test.getQualifiedName());
//            Element e = test.getEnclosingElement();
//            if (e == null || e.getKind() == ElementKind.PACKAGE) {
//                System.err.println("hit package, done");
//                break;
//            }
//            test = utils.enclosingTypeElement(e);
//            System.err.println("Now " + test.getQualifiedName());
//            result = maybeParent.equals(test);
//            if (result) {
//                break;
//            }
//        } while (test != null);
//
//        return result;
//    }
    
    private static boolean isEnclosedBy (TypeElement el, TypeElement maybeParent, CompilationInfo compiler) {
        ElementUtilities utils = compiler.getElementUtilities();
        boolean result = false;
        Element current = el;
        ElementHandle<TypeElement> b = ElementHandle.<TypeElement>create(maybeParent);
        while (current.getKind() != ElementKind.PACKAGE) {
            result = elementsEqual (current, maybeParent, compiler);
            if (result) {
                break;
            }
            current = current.getEnclosingElement();
        }
        return result;
    }
    

    /**
     * Get the String needed to qualify a reference to a member of an outer
     * class from a member of an inner class.
     */
    /*
    public static String getQualification(TreePath pathToMemberSelect, TypeElement ownerOfMemberSelect, Element selected, ParameterChangeContext ctx) {
        CompilationInfo compiler = ctx.changeData.getCompilationInfo();
        Trees trees = compiler.getTrees();
        TreePath pathToOwnerOfSelectedMember = Utils.findEnclosingClass(compiler, pathToMemberSelect, true, true, false, false, false);
        TypeElement ownerType = (TypeElement) trees.getElement(pathToOwnerOfSelectedMember);
        TreePath pathToOwnerOfMemberSelect = Utils.findEnclosingClass(compiler, pathToOwnerOfSelectedMember, true, false, true, false, true);
        String result = null;


        boolean enclosed = isEnclosedBy (ownerOfMemberSelect, ownerType, compiler.getElementUtilities());
//        if (Utils.isParentPath (pathToOwnerOfSelectedMember, pathToOwnerOfMemberSelect)) {
        if (enclosed) {
            System.err.println("GQ: " + pathToMemberSelect.getLeaf().toString() + " selected is " + compiler.getElementUtilities().getFullName(selected) + " in " + compiler.getElementUtilities().getFullName(ownerOfMemberSelect));
            //If not isParentPath, it is a member of some other class caught in our
            //net by accident - return null to not use it further
            boolean statik = selected.getModifiers().contains(Modifier.STATIC);
            if (statik) {
                System.err.println("   A");
                //XXX this will always generate FQNs.  We should check to see if
                //they share either class file or package
                result = ownerType.getQualifiedName().toString();
            } else {
                switch (ownerOfMemberSelect.getNestingKind()) {
                    case TOP_LEVEL :
                        result = "this";
                        System.err.println("   B");
                        break;
                    case ANONYMOUS :
                    case LOCAL :
                        System.err.println("   C");
                        List <String> names = new ArrayList <String> ();
                        names.add ("this");
                        TreePath pathToParentClass = Utils.findEnclosingClass(compiler, pathToOwnerOfSelectedMember, true, true, false, false, false);
                        int len = 5;
                        do {
                            if (pathToParentClass != null) {
                                TypeElement owner = (TypeElement) trees.getElement(pathToParentClass);
                                String s = owner.getSimpleName().toString();
                                names.add (0, s);
                                System.err.println("  app " + s);
                                len += s.length() + 1;
                                if (owner.getNestingKind() == NestingKind.MEMBER ||
                                        owner.getNestingKind() == NestingKind.TOP_LEVEL) {
                                    break;
                                }
                            }
                        } while (pathToParentClass != null && pathToParentClass.getLeaf().equals(pathToOwnerOfSelectedMember.getLeaf()));
                        StringBuilder sb = new StringBuilder(len);
                        for (Iterator<String> it = names.iterator(); it.hasNext();) {
                            String s = it.next();
                            sb.append (s);
                            if (it.hasNext()) {
                                sb.append ('.'); //NOI18N
                            }
                        }
                        result = sb.toString();
                        break;
                    case MEMBER :
                        System.err.println("   D");
                        break;
                    default :
                        throw new AssertionError();
                }
            }
        }
        System.err.println("Generated qualifier " + result + " for reference to "
                + compiler.getElementUtilities().getFullName(selected) + " in "
                + compiler.getElementUtilities().getFullName(ownerOfMemberSelect));

        return result;
    }
     */

    public static String getQualification(TreePath pathToMemberSelect, TypeElement ownerOfMemberSelect, Element selected, ParameterChangeContext ctx) {
        CompilationInfo compiler = ctx.changeData.getCompilationInfo();
        Trees trees = compiler.getTrees();
        ElementUtilities utils = compiler.getElementUtilities();

        TreePath pathToOwnerOfSelectedMember = Utils.findEnclosingClass(compiler, pathToMemberSelect, true, true, false, false, false);
        TypeElement ownerType = (TypeElement) trees.getElement(pathToOwnerOfSelectedMember);

        TreePath pathToOwnerOfMemberSelect = Utils.findEnclosingClass(compiler, pathToOwnerOfSelectedMember, true, false, true, false, true);
        String result = null;
        
        //XXX something is backwards here
//        TypeElement hold = ownerOfMemberSelect;
//        ownerOfMemberSelect = ownerType;
//        ownerType = hold;
        
//        boolean enclosed = isEnclosedBy (ownerOfMemberSelect, ownerType, compiler);
        boolean enclosed = isParentPath (pathToOwnerOfSelectedMember, 
                pathToOwnerOfMemberSelect);
        boolean isSuperType = isSupertype (ownerOfMemberSelect, ownerType, compiler);
        System.err.println("Generate qualifier for reference to "
            + compiler.getElementUtilities().getFullName(selected) + "\n   belonging to "
            + compiler.getElementUtilities().getFullName(ownerType) + "\n   in "
            + compiler.getElementUtilities().getFullName(ownerOfMemberSelect) +
            "\n   enclosed? " + enclosed + "\n   supertype? " + isSuperType);
        boolean same = pathToOwnerOfSelectedMember.getLeaf().equals(pathToOwnerOfMemberSelect.getLeaf());
        if (same || enclosed || isSuperType) {
            boolean statik = selected.getModifiers().contains(Modifier.STATIC);
            if (statik) {
                System.err.println("   a");
                PackageElement ownerPackage = compiler.getElements().getPackageOf(ownerType);
                PackageElement selectPackage = compiler.getElements().getPackageOf(ownerOfMemberSelect);
                boolean fqn = ownerPackage != null && !ownerPackage.equals(selectPackage);
                result = fqn ? ownerOfMemberSelect.getQualifiedName().toString() : ownerOfMemberSelect.getSimpleName().toString();
            } else if (same) {
                result = "this"; //NOI18N                
            } else {
                Element enc = ownerType.getEnclosingElement();
//                Element enc = ownerOfMemberSelect.getEnclosingElement();
                System.err.println("   d");
                TypeElement owner = utils.enclosingTypeElement(enc);
                List <String> strings = new ArrayList<String> ();
                strings.add ("this"); //NOI18N
                do {
                    strings.add (0, owner.getSimpleName().toString());
                    TypeElement last = owner;
                    Element el = owner.getEnclosingElement();
                    if (el == null || el.getKind() == ElementKind.PACKAGE) break;
                    owner = utils.enclosingTypeElement(el);
                    if (last == owner) break;
                } while (ownerType != null && ownerType.getNestingKind() != NestingKind.TOP_LEVEL);
                StringBuilder sb = new StringBuilder();
                for (Iterator<String> it = strings.iterator(); it.hasNext();) {
                    sb.append (it.next());
                    if (it.hasNext()) {
                        sb.append ('.'); //NOI18N
                    }
                }
                result = sb.toString();
            }
        }
        System.err.println("Generated qualifier " + result + " for reference to "
        + compiler.getElementUtilities().getFullName(selected) + " in "
        + compiler.getElementUtilities().getFullName(ownerOfMemberSelect));

        return result;
    }

    public static boolean isSupertype (TypeElement ownerOfMemberSelect, TypeElement memberOwner, CompilationInfo compiler) {
        boolean result = false;
        TypeElement supertype = ownerOfMemberSelect;
        while (supertype != null) {
            result = elementsEqual (supertype, memberOwner, compiler);
            List <? extends TypeMirror> ifaces = supertype.getInterfaces();
            for (TypeMirror t : ifaces) {
                Element e = compiler.getTypes().asElement(t);
                result = elementsEqual (supertype, e, compiler);
            }
            if (result) {
                break;
            }
            TypeMirror type = supertype.getSuperclass();
            if (type.getKind() == TypeKind.NONE) {
                break;
            } else {
                Element e = compiler.getTypes().asElement(
                        supertype.getSuperclass());
                //XXX need to check interfaces to
                if (e instanceof TypeElement) {
                    supertype = (TypeElement) e;
                } else {
                    supertype = null;
                }
            }
        }
        return result;
    }
    
    public static boolean elementsEqual (Element a, Element b, CompilationInfo info) {
        boolean result;
        if (a == b) {
            result = true;
        } else {
            if (a.getKind() == b.getKind()) {
                ElementHandle e1 = ElementHandle.create (a);
                ElementHandle e2 = ElementHandle.create (b);
                result = e1.equals (e2);
            } else {
                result = false;
            }
        }
        return result;
    }

}
