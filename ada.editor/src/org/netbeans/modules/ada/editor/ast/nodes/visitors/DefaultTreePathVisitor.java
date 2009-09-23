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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.ada.editor.ast.ASTError;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.AbortStatement;
import org.netbeans.modules.ada.editor.ast.nodes.ArrayAccess;
import org.netbeans.modules.ada.editor.ast.nodes.Assignment;
import org.netbeans.modules.ada.editor.ast.nodes.Block;
import org.netbeans.modules.ada.editor.ast.nodes.BlockStatement;
import org.netbeans.modules.ada.editor.ast.nodes.CaseStatement;
import org.netbeans.modules.ada.editor.ast.nodes.CaseWhen;
import org.netbeans.modules.ada.editor.ast.nodes.CodeStatement;
import org.netbeans.modules.ada.editor.ast.nodes.Comment;
import org.netbeans.modules.ada.editor.ast.nodes.DelayStatement;
import org.netbeans.modules.ada.editor.ast.nodes.ExitStatement;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.GotoStatement;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.IfStatement;
import org.netbeans.modules.ada.editor.ast.nodes.LoopStatement;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.NullStatement;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageInstanceCreation;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.PackageName;
import org.netbeans.modules.ada.editor.ast.nodes.PackageRenames;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.QualifiedExpression;
import org.netbeans.modules.ada.editor.ast.nodes.RaiseStatement;
import org.netbeans.modules.ada.editor.ast.nodes.Reference;
import org.netbeans.modules.ada.editor.ast.nodes.ReturnStatement;
import org.netbeans.modules.ada.editor.ast.nodes.Scalar;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramBody;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.SubtypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.TaskName;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.TypeName;
import org.netbeans.modules.ada.editor.ast.nodes.UnaryOperation;
import org.netbeans.modules.ada.editor.ast.nodes.Use;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.With;

/**
 * Based on org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultTreePathVisitor
 * 
 * @author Andrea Lucarelli
 */
public class DefaultTreePathVisitor extends DefaultVisitor {

    private LinkedList<ASTNode> path = new LinkedList<ASTNode>();
    private List<ASTNode> unmodifiablePath;

    public DefaultTreePathVisitor() {
        unmodifiablePath = Collections.unmodifiableList(path);
    }

    /**
     * ... reversed order ....
     * 
     * 
     * @return
     */
    public List<ASTNode> getPath() {
        return unmodifiablePath;
    }

    @Override
    public void visit(ASTError astError) {
        super.visit(astError);
    }

    @Override
    public void visit(AbortStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(ArrayAccess node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Assignment node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Block node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(CodeStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Comment node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(DelayStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(ExitStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(FieldsDeclaration node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(FormalParameter node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(GotoStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Identifier node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(LoopStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(MethodDeclaration node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(NullStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(PackageBody node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(PackageInstanceCreation node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(PackageName node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(PackageSpecification node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Program node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(QualifiedExpression node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(RaiseStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(ReturnStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Reference node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Scalar node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(SingleFieldDeclaration node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(SubprogramBody node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(SubprogramSpecification node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(SubtypeDeclaration node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(TaskName node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(TypeName node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(TypeDeclaration node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Use node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(Variable node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(With node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(BlockStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(CaseStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(CaseWhen node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(IfStatement node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(PackageRenames node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(ASTNode node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }

    @Override
    public void visit(UnaryOperation node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }
}
