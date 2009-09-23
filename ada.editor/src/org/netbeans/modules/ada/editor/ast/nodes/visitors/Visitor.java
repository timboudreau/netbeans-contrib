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

import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
import org.netbeans.modules.ada.editor.ast.nodes.Use;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.With;
import org.netbeans.modules.ada.editor.ast.nodes.Comment;
import org.netbeans.modules.ada.editor.ast.nodes.PackageName;
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
import org.netbeans.modules.ada.editor.ast.nodes.DelayStatement;
import org.netbeans.modules.ada.editor.ast.nodes.ExitStatement;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.GotoStatement;
import org.netbeans.modules.ada.editor.ast.nodes.IfStatement;
import org.netbeans.modules.ada.editor.ast.nodes.LoopStatement;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.NullStatement;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageInstanceCreation;
import org.netbeans.modules.ada.editor.ast.nodes.PackageRenames;
import org.netbeans.modules.ada.editor.ast.nodes.QualifiedExpression;
import org.netbeans.modules.ada.editor.ast.nodes.RaiseStatement;
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

/**
 * Based on org.netbeans.modules.php.editor.parser.astnodes.visitors.Visitor
 * 
 * @author Andrea Lucarelli
 */
public interface Visitor {

    public void visit(ASTError node);

    public void visit(ASTNode node);

    public void visit(AbortStatement node);

	public void visit(ArrayAccess arrayAccess);

    public void visit(Assignment node);

    public void visit(BlockStatement node);

    public void visit(CaseStatement node);

    public void visit(CaseWhen node);

    public void visit(CodeStatement node);

    public void visit(Comment node);

    public void visit(DelayStatement node);

    public void visit(ExitStatement node);

    public void visit(FieldsDeclaration node);

	public void visit(FormalParameter node);

    public void visit(GotoStatement node);

    public void visit(Identifier node);

    public void visit(IfStatement node);

    public void visit(LoopStatement node);

	public void visit(MethodDeclaration node);

    public void visit(NullStatement node);

    public void visit(PackageSpecification node);

    public void visit(PackageBody node);

	public void visit(PackageInstanceCreation node);

    public void visit(PackageName node);

    public void visit(PackageRenames node);

    public void visit(Program node);

    public void visit(QualifiedExpression node);

	public void visit(Scalar node);

	public void visit(SubprogramBody node);

	public void visit(SubprogramSpecification node);

    public void visit(RaiseStatement node);

    public void visit(ReturnStatement node);

    public void visit(SingleFieldDeclaration node);

	public void visit(SubtypeDeclaration node);

    public void visit(TaskName node);

    public void visit(TypeName node);

    public void visit(TypeDeclaration node);

    public void visit(Variable node);

    public void visit(With node);

    public void visit(Use node);

    public void visit(UnaryOperation node);

    public void visit(Block node);
}
