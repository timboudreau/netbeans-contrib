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

package org.netbeans.modules.jackpot.cmds;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.jackpot.QueryContext;
import org.netbeans.api.jackpot.QueryOperations;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.openide.ErrorManager;

/**
 * Minimizes method access modifiers based on project usage.<p> This transformer
 * demonstrates two-pass operation within the Java Source framework.  The first
 * pass locates all method declarations and invocations, storing them in a
 * map using persistent tree and element references.  This first pass is started 
 * directly by this transformer in its <code>init()</code> method, which is
 * invoked prior to transformer execution.  The second pass runs as a
 * regular transformation, looking up each compilation unit's methods to see
 * what modifications it needs and applies them.  The ordering of these two 
 * passes is important, because only modifications made by the regular 
 * transformation pass are displayed to the user and committed to the source files.
 */
public class MinimizeMethodAccess extends TreePathTransformer<Void,Object> {
    private JavaSource javaSource;
    private Map<ElementHandle<ExecutableElement>,MethodReferences> refs;

    private static final int PUBLIC_USE = 1;
    private static final int PACKAGE_USE = 2;
    private static final int CLASS_USE = 4;

    // property set by PropertySheetInfo
    private boolean convertPackagePrivate = true;
    public boolean getConvertPackagePrivate() {
        return convertPackagePrivate;
    }
    public void setConvertPackagePrivate(boolean b) {
        convertPackagePrivate = b;
    }
    
    @Override
    public void init(QueryContext context, JavaSource javaSource) {
        super.init(context, javaSource);
        this.javaSource = javaSource;
        refs = new HashMap<ElementHandle<ExecutableElement>,MethodReferences>();
        scanMethods();
    }
    
    @Override
    public void destroy() {
        refs = null;
        super.destroy();
    }
    
    private void scanMethods() {
        final ReferenceScanner scanner = new ReferenceScanner();
        CancellableTask<CompilationController> task = new CancellableTask<CompilationController>() {
            private boolean cancelled = false;
            public void run(CompilationController cc) throws IOException {
                if (!cancelled) {
                    cc.toPhase(JavaSource.Phase.RESOLVED);
                    scanner.scan(cc.getCompilationUnit(), cc);
                }
            }
            public void cancel() {
                cancelled = true;
            }
        };

        try {
            javaSource.runUserActionTask(task, false);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }

    @Override
    public Void visitMethod(MethodTree t, Object p) {
        super.visitMethod(t, p);
        WorkingCopy wc = getWorkingCopy();
        Trees trees = wc.getTrees();
        ExecutableElement meth = (ExecutableElement)trees.getElement(getCurrentPath());
        MethodReferences methodRefs = meth != null ? refs.get(ElementHandle.create(meth)) : null;
        if (methodRefs == null)  // true if method isn't "interesting"
            return null;

        Set<Modifier> flags = meth.getModifiers();
        Set<Modifier> newFlags = EnumSet.noneOf(Modifier.class);
        newFlags.addAll(flags);
        TypeElement owner = (TypeElement)meth.getEnclosingElement();

        if (flags.contains(Modifier.PUBLIC)) {
            if (noPublicUsage(methodRefs))
                newFlags.remove(Modifier.PUBLIC);
            else if (onlyProtectedUsage(wc, owner, methodRefs)) {
                newFlags.remove(Modifier.PUBLIC);
                newFlags.add(Modifier.PROTECTED);
            }
        }
        else if (flags.contains(Modifier.PROTECTED) && !onlyProtectedUsage(wc, owner, methodRefs)) {
            newFlags.remove(Modifier.PROTECTED);
        }
        if (convertPackagePrivate && 
                !newFlags.contains(Modifier.PUBLIC) &&
                !newFlags.contains(Modifier.PROTECTED) &&
                !flags.contains(Modifier.PRIVATE) && 
                !flags.contains(Modifier.ABSTRACT) &&
                noPublicUsage(methodRefs) &&
                noPackageUsage(methodRefs))
            newFlags.add(Modifier.PRIVATE);
        
        if (!flags.equals(newFlags)) {
            // Make sure overriding methods don't weaken access.
            ExecutableElement parentMethod = wc.getElementUtilities().getOverriddenMethod(meth);
            if (parentMethod == null || // true if method doesn't override
                    access(newFlags) >= access(parentMethod.getModifiers())) {
                ModifiersTree mods = t.getModifiers();
                ModifiersTree newMods = wc.getTreeMaker().Modifiers(newFlags, mods.getAnnotations());
                StringBuilder sb = new StringBuilder();
                String s = mods.toString();
                sb.append(s.length() > 0 ? s : "package-private");
                sb.append(" => ");
                s = newMods.toString();
                sb.append(s.length() > 0 ? s : "package-private");
                addChange(new TreePath(getCurrentPath(), mods), newMods, sb.toString());
            }
        }
        return null;
    }

    /**
     * See if usage is limited to subclasses.
     */
    private boolean onlyProtectedUsage(WorkingCopy wc, Element owner, MethodReferences methodRefs) {
        Types types = wc.getTypes();
        TypeMirror classType = owner.asType();
        for (Reference r : methodRefs.references) {
            TypeMirror referenceOwner = MinimizeFieldAccess.getClassType(r.element.resolve(wc));
            if (referenceOwner != classType && !types.isSubtype(referenceOwner, classType)) 
                return false;
        }
        return true;
    }
    
    private static boolean noPublicUsage(MethodReferences mr) {
        return (mr.usage & PUBLIC_USE) == 0;
    }
    
    private static boolean noPackageUsage(MethodReferences mr) {
        return (mr.usage & PACKAGE_USE) == 0;
    }

    /** Returns a comparable int corresponding to the flags' access level */
    private int access(Set<Modifier> flags) {
        if (flags.contains(Modifier.PUBLIC))
            return 4;
        if (flags.contains(Modifier.PROTECTED))
            return 2;
        return flags.contains(Modifier.PRIVATE) ? 0 : 1;
    }
    
    private Element getReferringElement(TreePath path, CompilationInfo info) {
        Element e = info.getTrees().getElement(path);
        return e != null ? e : getReferringElement(path.getParentPath(), info);
    }
    
    /**
     * A description of a references to a variable element.
     */
    private static class MethodReferences {
        ExecutableElement var;
        TreePathHandle declaration;
        int usage;
        List<Reference> references;
        
        // these elements are only valid when the ReferenceScanner is scanning
        TypeElement cls;
        PackageElement pkg;
        
        MethodReferences(ExecutableElement var) {
            this.var = var;
            cls = QueryOperations.getDeclaringClass(var);
            pkg = QueryOperations.getDeclaringPackage(cls);
            references = new ArrayList<Reference>();
        }
    }
    
    /**
     * A reference to an element by a single tree node.
     */
    private static class Reference {
        ElementHandle<Element> element;
        TreePathHandle tree;
    }
    
    /**
     * A scanner which finds all variable declarations and references in a project.
     */
    private class ReferenceScanner extends TreePathScanner<Void,CompilationInfo> {
        private TypeElement currentClass;
        private PackageElement currentPackage;
        
        private boolean isInteresting(Element sym, TreePath path, CompilationInfo info) {
            if (sym == null || !(sym instanceof ExecutableElement))
                return false;  

            // ignore compiler-generated methods
            if (info.getElementUtilities().isSynthetic(sym))
                return false;
            SourcePositions srcPos = info.getTrees().getSourcePositions();
            CompilationUnitTree unit = path.getCompilationUnit();
            long treePos = srcPos.getStartPosition(unit, path.getLeaf());
            long parentPos = srcPos.getStartPosition(unit, path.getParentPath().getLeaf());
            if (treePos == parentPos) // true for default constructors
                return false;

            // ignore enum and interface methods
            TypeElement owner = (TypeElement)sym.getEnclosingElement();
            if (owner.getKind() == ElementKind.ENUM || owner.getKind() == ElementKind.INTERFACE)
                return false;
            
            // ignore methods in anonymous classes, since they aren't accessible
            if (owner.getNestingKind() == NestingKind.ANONYMOUS)
                return false; 

            // ignore non-public methods if possible
            Set<Modifier> flags = sym.getModifiers();
            if (!convertPackagePrivate &&
                !flags.contains(Modifier.PUBLIC) && !flags.contains(Modifier.PROTECTED))
                return false;
 
            // ignore public no-arg constructors, which are often invoked by reflection
            ExecutableElement ee = (ExecutableElement)sym;
            if (sym.getKind() == ElementKind.CONSTRUCTOR && 
                flags.contains(Modifier.PUBLIC) && 
                ee.getParameters().isEmpty())
                return false;

            // ignore any methods which override their superclass or implement interface methods.
            ElementUtilities utils = info.getElementUtilities();
            if (utils.overridesMethod(ee) || utils.implementsMethod(ee))
                return false;

            // ignore main methods
            if (flags.contains(Modifier.PUBLIC) && 
                flags.contains(Modifier.STATIC) &&
                "void".equals(ee.getReturnType().toString()) &&
                "main".equals(ee.getSimpleName().toString()))
                return false;
            
            return true;
        }

        private void add(CompilationInfo info) {
            TreePath path = getCurrentPath();
            Element e = info.getTrees().getElement(path);
            if (!isInteresting(e, path, info))
                return;
            assert e instanceof ExecutableElement;
            ExecutableElement var = (ExecutableElement)e;
            TreePathHandle handle = TreePathHandle.create(path, info);
            MethodReferences uses = refs.get(var);
            if (uses == null) {
                uses = new MethodReferences(var);
                if (path.getLeaf() instanceof MethodTree)
                    uses.declaration = handle;
                refs.put(ElementHandle.create(var), uses);
            }
            Reference ref = new Reference();
            ref.element = ElementHandle.create(getReferringElement(path, info));
            ref.tree = handle;
            uses.references.add(ref);
            int flags = currentClass == uses.cls ? CLASS_USE
                   : currentPackage == uses.pkg ? PACKAGE_USE
                   : PUBLIC_USE;
            uses.usage |= flags;
        }

        @Override
        public Void visitClass(ClassTree tree, CompilationInfo info) {
            currentClass = (TypeElement)info.getTrees().getElement(getCurrentPath());
            currentPackage = info.getElements().getPackageOf(currentClass);
            super.visitClass(tree, info);
            currentClass = null;
            currentPackage = null;
            return null;
        }
        
        @Override
        public Void visitMethod(MethodTree tree, CompilationInfo info) {
            Element e = info.getTrees().getElement(getCurrentPath());
            add(info);
            return super.visitMethod(tree, info);
        }
        
        @Override
        public Void visitMethodInvocation(MethodInvocationTree tree, CompilationInfo info) {
            Element e = info.getTrees().getElement(getCurrentPath());
            add(info);
            return super.visitMethodInvocation(tree, info);
        }
        
        @Override
        public Void visitNewClass(NewClassTree tree, CompilationInfo info) {
            Element e = info.getTrees().getElement(getCurrentPath());
            add(info);
            return super.visitNewClass(tree, info);
        }
    }
}
