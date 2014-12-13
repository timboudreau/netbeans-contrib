/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javahints;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.java.hints.JavaFix;
import org.openide.util.NbBundle.Messages;

public class InitializeFinalField implements ErrorRule<Void> {

    private static final Set<String> ERROR_CODES = new HashSet<String>(Arrays.asList(
            "compiler.err.var.might.not.have.been.initialized")); // NOI18N

    @Override
    public Set<String> getCodes() {
        return ERROR_CODES;
    }

    @Override
    public List<Fix> run(final CompilationInfo info, String diagnosticKey, int offset, TreePath treePath, Data<Void> data) {
        TreePath path = info.getTreeUtilities().pathFor(offset - 1);
        if (path.getParentPath() != null && path.getParentPath().getLeaf().getKind() == Kind.METHOD) {
            ExecutableElement constr = (ExecutableElement) info.getTrees().getElement(path.getParentPath());

            if (constr.getKind() != ElementKind.CONSTRUCTOR)
                return null;

            //TODO: should use flow?
            final Set<VariableElement> uninits = new HashSet<VariableElement>();

            for (VariableElement field : ElementFilter.fieldsIn(constr.getEnclosingElement().getEnclosedElements())) {
                if (field.getModifiers().contains(Modifier.FINAL)) {
                    uninits.add(field);
                }
            }

            new TreePathScanner<Void, Void>() {
                @Override
                public Void visitAssignment(AssignmentTree node, Void p) {
                    uninits.remove(info.getTrees().getElement(new TreePath(getCurrentPath(), node.getVariable())));
                    return super.visitAssignment(node, p);
                }
            }.scan(path, null);

            List<Fix> fixes = new ArrayList<Fix>();

            for (VariableElement uninit : uninits) {
                fixes.add(new AddConstructorParameter(info, path.getParentPath(), uninit).toEditorFix());
            }

            return fixes;
        }

        return null;
    }

    @Override
    public String getId() {
        return InitializeFinalField.class.getName();
    }

    @Override
    @Messages("DN_InitializeFinalField=Initialize field from a new constructor parameter")
    public String getDisplayName() {
        return Bundle.DN_InitializeFinalField();
    }

    @Override
    public void cancel() {
    }

    private static final class AddConstructorParameter extends JavaFix {

        private final ElementHandle<VariableElement> uninitializedField;
        private final String fieldName;

        public AddConstructorParameter(CompilationInfo info, TreePath tp, VariableElement uninitializedField) {
            super(info, tp);
            this.uninitializedField = ElementHandle.create(uninitializedField);
            this.fieldName = uninitializedField.getSimpleName().toString();
        }

        @Override
        @Messages("FIX_InitializeField=Initialize {0} from a new constructor parameter")
        public String getText() {
            return Bundle.FIX_InitializeField(fieldName);
        }

        @Override
        protected void performRewrite(TransformationContext ctx) throws Exception {
            TreePath constrPath = ctx.getPath();
            VariableElement field = uninitializedField.resolve(ctx.getWorkingCopy());
            MethodTree constr = (MethodTree) constrPath.getLeaf();
            TreeMaker make = ctx.getWorkingCopy().getTreeMaker();
            //TODO: check clashes
            //TODO: use the verbatim field's type?
            VariableTree newParam = make.Variable(make.Modifiers(EnumSet.noneOf(Modifier.class)), field.getSimpleName(), make.Type(field.asType()), null);
            ctx.getWorkingCopy().rewrite(constr, make.addMethodParameter(constr, newParam));
            StatementTree assgn = make.ExpressionStatement(make.Assignment(make.MemberSelect(make.Identifier("this"),
                                                                                             field.getSimpleName()),
                                                                           make.Identifier(field.getSimpleName())));
            ctx.getWorkingCopy().rewrite(constr.getBody(), make.addBlockStatement(constr.getBody(), assgn));
        }

    }

}
