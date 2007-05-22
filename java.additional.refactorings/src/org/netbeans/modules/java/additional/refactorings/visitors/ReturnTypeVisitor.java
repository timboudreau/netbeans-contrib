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
