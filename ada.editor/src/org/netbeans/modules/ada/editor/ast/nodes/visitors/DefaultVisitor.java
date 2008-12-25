/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.editor.ast.nodes.visitors;

import org.netbeans.modules.ada.editor.ast.ASTError;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.Block;
import org.netbeans.modules.ada.editor.ast.nodes.Comment;
import org.netbeans.modules.ada.editor.ast.nodes.EmptyStatement;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.FunctionDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageInstanceCreation;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.PackageName;
import org.netbeans.modules.ada.editor.ast.nodes.ProcedureDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.Reference;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Use;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.With;

/**
 * Based on org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor
 * 
 * @author Andrea Lucarelli
 */
public class DefaultVisitor implements Visitor {

    public void scan(ASTNode node) {
        if (node != null) {
            node.accept(this);
        }
    }

    public void scan(Iterable<? extends ASTNode> nodes) {
        if (nodes != null) {
            for (ASTNode n : nodes) {
                scan(n);
            }
        }
    }

    public void visit(ASTError astError) {
    }

    public void visit(ASTNode node) {
    }

    public void visit(Block node) {
        scan(node.getStatements());
    }

    public void visit(Comment comment) {
    }

    public void visit(EmptyStatement node) {
    }

    public void visit(FieldsDeclaration node) {
        scan(node.getFields());
    }

    public void visit(FunctionDeclaration node) {
        scan(node.getFunctionName());
        scan(node.getFormalParameters());
        scan(node.getBody());
    }

    public void visit(FormalParameter node) {
        scan(node.getParameterName());
        scan(node.getParameterType());
        scan(node.getDefaultValue());
    }

    public void visit(Identifier identifier) {
    }

    public void visit(MethodDeclaration node) {
        if (node.getKind() == MethodDeclaration.Kind.FUNCTION) {
            scan(node.getFunction());
        }
        else {
            scan(node.getProcedure());
        }
    }

    public void visit(PackageBody node) {
        scan(node.getName());
        scan(node.getBody());
    }

    public void visit(PackageInstanceCreation node) {
        scan(node.getPackageName());
        scan(node.ctorParams());
    }

    public void visit(PackageName node) {
        scan(node.getName());
    }

    public void visit(PackageSpecification node) {
        scan(node.getName());
        scan(node.getBody());
    }

    public void visit(ProcedureDeclaration node) {
        scan(node.getProcedureName());
        scan(node.getFormalParameters());
        scan(node.getBody());
    }

    public void visit(Program program) {
        scan(program.getStatements());
    }

    public void visit(Reference node) {
        scan(node.getExpression());
    }

    public void visit(TypeDeclaration node) {
        scan(node.getTypeName());
    }

    public void visit(SingleFieldDeclaration node) {
        scan(node.getName());
        scan(node.getValue());
    }

    public void visit(Use node) {
        scan(node.getPackageName());
    }

    public void visit(Variable node) {
        scan(node.getName());
    }

    public void visit(With node) {
        scan(node.getPackageName());
    }
}
