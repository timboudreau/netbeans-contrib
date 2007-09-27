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

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.jackpot.QueryContext;
import org.netbeans.api.jackpot.QueryOperations;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.openide.ErrorManager;

/**
 * Minimizes field access modifiers based on project usage.<p> This transformer
 * demonstrates two-pass operation within the Java Source framework.  The first
 * pass locates all variable declarations and references, and stores them in a
 * map using persistent tree and element references.  This pass is started 
 * directly by this transformer in its <code>init()</code> method, which is
 * invoked prior to transformer execution.  The second pass runs as a
 * regular transformation, looking up each compilation unit's variables to see
 * what modifications it needs and applies them.  The ordering of these two 
 * passes is important, because only modifications made by the regular 
 * transformation pass are displayed to the user and committed to the source files.
 */
public class MinimizeFieldAccess extends TreePathTransformer<Void,Object> {
    private JavaSource javaSource;
    private Map<ElementHandle<VariableElement>,VariableReferences> refs;

    private static final int SETUSE = 1;
    private static final int GETUSE = 2;
    private static final int CLASSSHIFT = 4;
    private static final int PACKAGESHIFT = 2;
    private static final int WORLDSHIFT = 0;
    private static final int DECLARATION = 1 << 6;
    private static final int worldMask =   (SETUSE | GETUSE) << WORLDSHIFT;
    private static final int packageMask = (SETUSE | GETUSE) << PACKAGESHIFT;

    // properties set by PropertySheetInfo
    private boolean ignorePackagePrivate;
    private boolean ignoreConstants;
    public boolean getIgnorePackagePrivate() {
        return ignorePackagePrivate;
    }
    public void setIgnorePackagePrivate(boolean b) {
        ignorePackagePrivate = b;
    }
    public boolean getIgnoreConstants() {
        return ignoreConstants;
    }
    public void setIgnoreConstants(boolean b) {
        ignoreConstants = b;
    }
    
    @Override
    public void init(QueryContext context, JavaSource javaSource) {
        super.init(context, javaSource);
        this.javaSource = javaSource;
        refs = new HashMap<ElementHandle<VariableElement>,VariableReferences>();
        scanFields();
    }
    
    @Override
    public void destroy() {
        refs = null;
        super.destroy();
    }
    
    @Override
    public Void visitVariable(VariableTree tree, Object unused) {
        WorkingCopy wc = getWorkingCopy();
        VariableElement var = (VariableElement)wc.getTrees().getElement(getCurrentPath());
        VariableReferences varRefs = var != null ? refs.get(ElementHandle.create(var)) : null;

        if (varRefs != null) { // if "interesting" variable
            Set<Modifier> flags = var.getModifiers();
            Set<Modifier> newFlags = EnumSet.noneOf(Modifier.class);
            newFlags.addAll(flags);

            TypeElement owner = QueryOperations.getDeclaringClass(var);
            if (flags.contains(Modifier.PUBLIC) && hasPublicUsage(varRefs.usage)) {
                // see if public usage is limited to subclasses
                boolean onlyProtected = true;
                Types types = wc.getTypes();
                TypeMirror classType = owner.asType();
                for (Reference r : varRefs.references)
                    if (!types.isSubtype(getClassType(r.element.resolve(wc)), classType)) {
                        onlyProtected = false;
                        break;
                    }
                if (onlyProtected) {
                    newFlags.remove(Modifier.PUBLIC);
                    newFlags.add(Modifier.PROTECTED);
                }
            }
            if ((flags.contains(Modifier.PUBLIC) || flags.contains(Modifier.PROTECTED)) 
                    && !hasPublicUsage(varRefs.usage)) {
                newFlags.remove(Modifier.PUBLIC);
                newFlags.remove(Modifier.PROTECTED);
            }
            if (!ignorePackagePrivate && !hasPublicUsage(varRefs.usage) && !hasPackageUsage(varRefs.usage)) {
                assert !newFlags.contains(Modifier.PUBLIC) && !newFlags.contains(Modifier.PROTECTED);
                newFlags.add(Modifier.PRIVATE);
            }
            if (!flags.equals(newFlags))  {
                ModifiersTree oldMods = tree.getModifiers();
                ModifiersTree newMods = wc.getTreeMaker().Modifiers(newFlags, oldMods.getAnnotations());
                String note = oldMods.toString() + " => " + newMods.toString();
                addChange(new TreePath(getCurrentPath(), oldMods), newMods, note);
            }
        }
        return super.visitVariable(tree, unused);
    }
    
    private void scanFields() {
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
    
    private Element getReferringElement(TreePath path, CompilationInfo info) {
        Element e = info.getTrees().getElement(path);
        return e != null ? e : getReferringElement(path.getParentPath(), info);
    }
    
    static TypeMirror getClassType(Element e) {
        while (!(e instanceof TypeElement))
            e = e.getEnclosingElement();
        return e.asType();
    }
    
    static boolean hasPublicUsage(int usage) {
        return (usage & worldMask) != 0;
    }
    
    static boolean hasPackageUsage(int usage) {
        return (usage & packageMask) != 0;
    }
    
    /**
     * A description of a references to a variable element.
     */
    private static class VariableReferences {
        VariableElement var;
        TreePathHandle declaration;
        int usage;
        List<Reference> references;
        
        // these elements are only valid when the ReferenceScanner is scanning
        TypeElement cls;
        PackageElement pkg;
        
        VariableReferences(VariableElement var) {
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
        
        private boolean isInteresting(Element sym, CompilationInfo info) {
            // ignore non-instance variables
            if (sym == null || !(sym instanceof VariableElement) || !(sym.getEnclosingElement() instanceof TypeElement))
                return false;  
            TypeElement owner = (TypeElement)sym.getEnclosingElement();

            // ignore compiler-generated fields
            if (info.getElementUtilities().isSynthetic(sym))
                return false;

            // ignore enum fields, whose flags are synthetic
            if (owner.getKind() == ElementKind.ENUM)
                return false;  

            // ignore fields in anonymous classes, since they aren't accessible
            if (owner.getNestingKind() == NestingKind.ANONYMOUS)
                return false; 

            // ignore static constants if possible
            Set<Modifier> flags = sym.getModifiers();
            if (ignoreConstants &&
                flags.contains(Modifier.STATIC) && flags.contains(Modifier.FINAL))
                return false;

            // ignore non-public fields if possible
            if (ignorePackagePrivate &&
                !flags.contains(Modifier.PUBLIC) && !flags.contains(Modifier.PROTECTED))
                return false;
            return true;
        }

        private void add(Element e, TreePath path, int usage, CompilationInfo info) {
            if (!isInteresting(e, info))
                return;
            assert e instanceof VariableElement;
            VariableElement var = (VariableElement)e;
            TreePathHandle handle = TreePathHandle.create(path, info);
            VariableReferences uses = refs.get(var);
            if (uses == null) {
                uses = new VariableReferences(var);
                if ((usage & DECLARATION) != 0) {
                    assert path.getLeaf() instanceof VariableTree;
                    uses.declaration = handle;
                }
                refs.put(ElementHandle.create(var), uses);
            }
            Reference ref = new Reference();
            ref.element = ElementHandle.create(getReferringElement(path, info));
            ref.tree = handle;
            uses.references.add(ref);
            int flags = usage <<
                    (currentClass == uses.cls   ? CLASSSHIFT
                   : currentPackage == uses.pkg ? PACKAGESHIFT
                   : WORLDSHIFT);
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
        public Void visitAssignment(AssignmentTree tree, CompilationInfo info) {
            Tree lhs = tree.getVariable();
            if (lhs instanceof MemberSelectTree) {
                MemberSelectTree t = (MemberSelectTree)lhs;
                scan(t.getExpression(), info);
                TreePath path = new TreePath(getCurrentPath(), t);
                Element e = info.getTrees().getElement(path);
                add(e, path, SETUSE, info);
            } else if (lhs instanceof IdentifierTree) {
                TreePath path = getCurrentPath();
                Element e = info.getTrees().getElement(path);
                add(e, path, SETUSE, info);
            } else
                scan(lhs, info);
            return scan(tree.getExpression(), info);
        }

        @Override
        public Void visitCompoundAssignment(CompoundAssignmentTree tree, CompilationInfo info) {
            Tree lhs = tree.getVariable();
            if (lhs instanceof MemberSelectTree) {
                MemberSelectTree t = (MemberSelectTree)lhs;
                scan(t.getExpression(), info);
                TreePath path = new TreePath(getCurrentPath(), t);
                Element e = info.getTrees().getElement(path);
                add(e, path, SETUSE | GETUSE, info);
            } else if (lhs instanceof IdentifierTree) {
                TreePath path = new TreePath(getCurrentPath(), lhs);
                Element e = info.getTrees().getElement(path);
                add(e, path, SETUSE | GETUSE, info);
            } else
                scan(lhs, info);
            return scan(tree.getExpression(), info);
        }

        @Override
        public Void visitUnary(UnaryTree tree, CompilationInfo info) {
            Tree arg = tree.getExpression();
            switch (tree.getKind()) {
              case PREFIX_INCREMENT:
              case PREFIX_DECREMENT:
              case POSTFIX_INCREMENT:
              case POSTFIX_DECREMENT:
                if (arg instanceof MemberSelectTree) {
                    MemberSelectTree t = (MemberSelectTree)arg;
                    scan(t.getExpression(), info);
                    TreePath path = new TreePath(getCurrentPath(), t);
                    Element e = info.getTrees().getElement(path);
                    add(e, path, SETUSE | GETUSE, info);
                } else if (arg instanceof IdentifierTree) {
                    TreePath path = new TreePath(getCurrentPath(), arg);
                    Element e = info.getTrees().getElement(path);
                    add(e, path, SETUSE | GETUSE, info);
                } else
                    scan(arg, info);
                break;
              default:
                scan(arg, info);
            }
            return null;
        }

        @Override
        public Void visitIdentifier(IdentifierTree tree, CompilationInfo info) {
            TreePath path = getCurrentPath();
            Element e = info.getTrees().getElement(path);
            add(e, path, GETUSE, info);
            return super.visitIdentifier(tree, info);
        }

        @Override
        public Void visitVariable(VariableTree tree, CompilationInfo info) {
            TreePath path = getCurrentPath();
            Element e = info.getTrees().getElement(path);
            int flags = DECLARATION;
            if (tree.getInitializer() != null)
                flags |= SETUSE;
            add(e, path, flags, info);
            scan(tree.getInitializer(), info);
            return null;
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree tree, CompilationInfo info) {
            TreePath path = getCurrentPath();
            Element e = info.getTrees().getElement(path);
            add(e, path, GETUSE, info);
            return super.visitMemberSelect(tree, info);
        }
    }
}
