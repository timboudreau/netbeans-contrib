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
import org.netbeans.modules.ada.editor.ast.nodes.Block;
import org.netbeans.modules.ada.editor.ast.nodes.Comment;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.PackageName;
import org.netbeans.modules.ada.editor.ast.nodes.Program;
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
    public void visit(Block node) {
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
    public void visit(Identifier node) {
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
    public void visit(PackageBody node) {
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
    public void visit(Program node) {
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
    public void visit(ASTNode node) {
        path.addFirst(node);
        super.visit(node);
        path.removeFirst();
    }
}
