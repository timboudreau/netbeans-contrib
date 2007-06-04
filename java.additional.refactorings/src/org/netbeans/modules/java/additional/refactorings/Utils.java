/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
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
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
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
            @SuppressWarnings("unchecked")
            T item = (T) path.getLeaf();
            result.add (item);
        }
        return result;
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
}
