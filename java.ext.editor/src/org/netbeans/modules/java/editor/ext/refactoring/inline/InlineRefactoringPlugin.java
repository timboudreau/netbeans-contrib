/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.editor.ext.refactoring.inline;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.api.ProgressEvent;
import org.netbeans.modules.refactoring.java.spi.JavaRefactoringPlugin;
import org.netbeans.modules.refactoring.java.spi.RefactoringVisitor;
import org.netbeans.modules.refactoring.java.spi.ToPhaseException;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;

/**
 *
 * @author lahvac
 */
public class InlineRefactoringPlugin extends JavaRefactoringPlugin {
    
    private final InlineRefactoring refactoring;
    private final TreePathHandle target;

    public InlineRefactoringPlugin(InlineRefactoring refactoring) {
        this.refactoring = refactoring;
        this.target = refactoring.getTarget();
    }

    protected JavaSource getJavaSource(Phase p) {
        switch (p) {
        case PRECHECK:
            ClasspathInfo cpInfo = getClasspathInfo(refactoring);
            return JavaSource.create(cpInfo, target.getFileObject());
        default:
            return JavaSource.forFileObject(target.getFileObject());
        }
    }

    @Override
    protected Problem preCheck(CompilationController info) throws IOException {
        return null;
    }

    @Override
    public Problem checkParameters() {
        //TODO:
        return null;
    }

    @Override
    public Problem fastCheckParameters() {
        return null;
    }

    public Problem prepare(RefactoringElementsBag refactoringElements) {
        fireProgressListenerStart(ProgressEvent.START, 1);
        final TransformerImpl transformer = new TransformerImpl();
        TransformTask transform = new TransformTask(transformer, target);
        Problem problem = createAndAddElements(Collections.singleton(target.getFileObject()), transform, refactoringElements, refactoring);
        fireProgressListenerStop();
        return problem;
    }

    private final class TransformerImpl extends RefactoringVisitor {

        private VariableTree targetVariable;
        private ExpressionTree init;

        @Override
        public void setWorkingCopy(WorkingCopy workingCopy) throws ToPhaseException {
            super.setWorkingCopy(workingCopy);

            TreePath targetPath = target.resolve(workingCopy);
            
            targetVariable = (VariableTree) targetPath.getLeaf();
            init = targetVariable.getInitializer();
        }

        @Override
        public Tree visitIdentifier(IdentifierTree node, Element p) {
            if (p != null && p.equals(workingCopy.getTrees().getElement(getCurrentPath()))) {
                workingCopy.rewrite(node, init);
            }
            
            return super.visitIdentifier(node, p);
        }

        @Override
        public Tree visitVariable(VariableTree node, Element p) {
            if (node != targetVariable) return super.visitVariable(node, p);
            
            Tree parent = getCurrentPath().getParentPath().getLeaf();
            
            switch (parent.getKind()) {
                case BLOCK:
                    List<StatementTree> newStatements = new LinkedList<StatementTree>(((BlockTree) parent).getStatements());
                    
                    newStatements.remove(node);
                    
                    workingCopy.rewrite(parent, make.Block(newStatements, ((BlockTree) parent).isStatic()));
                    break;
                default:
                    throw new UnsupportedOperationException(parent.getKind().toString());
            }
            return null;
        }

    }
}
