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

import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.Set;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.CompilationController;

/**
 *
 * @author Tim
 */
public class ReturnTypeVisitor extends TreePathScanner <Void, Set <TypeMirror>> {
    private final CompilationController copy;
    public ReturnTypeVisitor(CompilationController copy) {
        this.copy = copy;
    }

    @Override
    public Void visitVariable(VariableTree tree, Set<TypeMirror> set) {
        return super.visitVariable(tree, set);
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree tree, Set<TypeMirror> set) {
        return super.visitExpressionStatement(tree, set);
    }

    @Override
    public Void visitNewClass(NewClassTree tree, Set<TypeMirror> set) {
        return super.visitNewClass(tree, set);
    }

    @Override
    public Void visitIdentifier(IdentifierTree tree, Set<TypeMirror> set) {
        return super.visitIdentifier(tree, set);
    }

    boolean inReturn;
    
    @Override
    public Void visitReturn(ReturnTree tree, Set<TypeMirror> set) {
        inReturn = true;
        Void result = super.visitReturn(tree, set);
        tree.getExpression().accept(this, set);
        TreePath path = TreePath.getPath(copy.getCompilationUnit(), tree.getExpression());
        TypeMirror type = copy.getTrees().getTypeMirror(path);
        System.err.println("visit return " + tree);
        if (type != null) {
            set.add(type);
        } else {
            System.err.println("Type on " + tree + " null");
        }
        return result;
    }
    
    public TypeMirror getReturnType(Set <TypeMirror> set) {
        Types types = copy.getTypes();
        TypeMirror result = null;
        for (TypeMirror type : set) {
            if (result == null) {
                result = type;
            } else {
                if (types.isSubtype(type, result)) {
                    result = type;
                }
            }
        }
        return result;
    }
}
