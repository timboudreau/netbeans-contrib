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
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.CatchTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.TryTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.Utils;

/**
 *
 * @author Tim
 */
public final class ExceptionsVisitor extends TreeScanner<Void, Set<TypeMirrorHandle>> implements TypeVisitor <Void, Set<TypeMirrorHandle>>{
    private final WorkingCopy info;
    private final Set <TypeMirror> types = new HashSet <TypeMirror> ();
    public ExceptionsVisitor(WorkingCopy info) {
        assert info != null;
        this.info = info;
    }
    
    public Void visitTry(TryTree tree, Set<TypeMirrorHandle> thrown) {
        for (CatchTree ct : tree.getCatches()) {
            TypeMirror t = info.getTrees().getTypeMirror(TreePath.getPath(info.getCompilationUnit(), ct.getParameter().getType()));
            for (Iterator<TypeMirrorHandle> it = thrown.iterator(); it.hasNext();) {
                if (info.getTypes().isSubtype(it.next().resolve(info), t)) {
                    it.remove();
                }
            }
        }
        scan(tree.getCatches(), thrown);
        scan(tree.getFinallyBlock(), thrown);
        return super.visitTry(tree, thrown);
    }
    
    private boolean maybeAddType(TypeMirror type, Set <TypeMirrorHandle> set) {
        boolean result = false;
        if (type != null) {
            if (!types.contains(type)) {
                types.add(type);
                if (result = type.getKind() != TypeKind.PACKAGE) {
                    TypeMirror nue = Utils.hackFqn(type,
                            info.getTreeMaker(), info.getTrees(), info);
                    
                    set.add(TypeMirrorHandle.create(nue));
                } else {
                    type.accept(this, set);
                }
            }
        }
        return result;
    }
    
    boolean inThrow = false;
    
    @Override
    public Void visitIdentifier(IdentifierTree tree, Set<TypeMirrorHandle> set) {
        if (inThrow) {
            TreePath path = TreePath.getPath(info.getCompilationUnit(), tree);
            if (path != null && path.getLeaf() != null) {
                TypeMirror mirror = info.getTrees().getTypeMirror(path);
                maybeAddType(mirror, set);
            } else {
                throw new IllegalStateException("no path for " + tree);
            }
        }
        return super.visitIdentifier(tree, set);
    }
    
    @Override
    public Void visitThrow(ThrowTree tree, Set<TypeMirrorHandle> thrown) {
        inThrow = true;
        Void result = super.visitThrow(tree, thrown);
        inThrow = false;
        return result;
    }
    
    public Void visitMethodInvocation(MethodInvocationTree tree, Set<TypeMirrorHandle> thrown) {
        Element el = info.getTrees().getElement(TreePath.getPath(info.getCompilationUnit(), tree));
        if (el != null && el.getKind() == ElementKind.METHOD) {
            extract((ExecutableElement)el, thrown);
        } else {
            throw new IllegalStateException("null element for " + tree);
        }
        return super.visitMethodInvocation(tree, thrown);
    }
    
    private void extract(ExecutableElement el, Set <TypeMirrorHandle> thrown) {
        for (TypeMirror type : el.getThrownTypes()) {
            maybeAddType(type, thrown);
        }
    }
    
    public Void visitNewClass(NewClassTree tree, Set<TypeMirrorHandle> thrown) {
        Element el = info.getTrees().getElement(TreePath.getPath(info.getCompilationUnit(), tree));
        if (el != null && el.getKind() == ElementKind.CONSTRUCTOR) {
            extract((ExecutableElement)el, thrown);
        } else {
            throw new IllegalStateException("null element for " + tree);
        }
        return super.visitNewClass(tree, thrown);
    }
    
    public Void visit(TypeMirror t, Set<TypeMirrorHandle> p) {
        if (t.getKind() != TypeKind.PACKAGE) {
            if (!types.contains(t)) {
                TypeMirrorHandle handle = TypeMirrorHandle.create(t);
                types.add(t);
                p.add(handle);
            }
        } else {
            t.accept(this, p);
        }
        return null;
    }
    
    public Void visit(TypeMirror t) {
        return null;
    }
    
    public Void visitPrimitive(PrimitiveType t, Set<TypeMirrorHandle> p) {
        t.accept(this, p);
        return null;
    }
    
    public Void visitNull(NullType t, Set<TypeMirrorHandle> p) {
        return null;
    }
    
    public Void visitArray(ArrayType t, Set<TypeMirrorHandle> p) {
        return null;
    }
    
    public Void visitDeclared(DeclaredType t, Set<TypeMirrorHandle> p) {
        t.accept(this, p);
        return null;
    }
    
    public Void visitError(ErrorType t, Set<TypeMirrorHandle> p) {
        t.accept(this, p);
        return null;
    }
    
    public Void visitExecutable(ExecutableType t, Set<TypeMirrorHandle> p) {
        for (TypeMirror m : t.getThrownTypes()) {
            maybeAddType(m, p);
        }
        return null;
    }
    
    public Void visitNoType(NoType t, Set<TypeMirrorHandle> p) {
        return null;
    }
    
    public Void visitUnknown(TypeMirror t, Set<TypeMirrorHandle> p) {
        t.accept(this, p);
        return null;
    }

    public Void visitTypeVariable(TypeVariable t, Set<TypeMirrorHandle> p) {
        t.accept(this, p);
        return null;
    }

    public Void visitWildcard(WildcardType t, Set<TypeMirrorHandle> p) {
        t.accept(this, p);
        return null;
    }
}
