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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.java.source.support.CaretAwareJavaSourceTaskFactory;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;

import static com.sun.source.tree.Tree.Kind.*;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Lahoda
 */
public class NegateCondition extends AbstractHint {

    public NegateCondition() {
        super(true, false, HintSeverity.CURRENT_LINE_WARNING);
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(NegateCondition.class, "DESC_NegateCondition");
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.of(Kind.IF);
    }

    public List<ErrorDescription> run(CompilationInfo compilationInfo, TreePath treePath) {
        return run(compilationInfo, treePath, CaretAwareJavaSourceTaskFactory.getLastPosition(compilationInfo.getFileObject()));
    }
    
    protected List<ErrorDescription> run(CompilationInfo info, TreePath treePath, int offset) {
        if (!getTreeKinds().contains(treePath.getLeaf().getKind()))
            return null;
        
        IfTree it = (IfTree) treePath.getLeaf();
        ExpressionTree cond = it.getCondition();
        int condStart = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), cond);
        int condEnd   = (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), cond);
        
        
        if (condStart > offset || offset > condEnd) {
            return null;
        }
        
        List<Fix> fix = Collections.<Fix>singletonList(new FixImpl(info.getJavaSource(), TreePathHandle.create(new TreePath(treePath, it.getCondition()), info)));
        String displayName = NbBundle.getMessage(NegateCondition.class, "DN_NegateCondition");
        ErrorDescription error = ErrorDescriptionFactory.createErrorDescription(Severity.HINT, displayName,fix,info.getFileObject(), offset, offset);
        
        return Collections.singletonList(error);
    }

    public String getId() {
        return NegateCondition.class.getName();
    }

    public String getDisplayName() {
        return NbBundle.getMessage(NegateCondition.class, "DN_NegateCondition");
    }

    public void cancel() {
    }

    static final class FixImpl implements Fix {

        private JavaSource js;
        private TreePathHandle condition;

        public FixImpl(JavaSource js, TreePathHandle condition) {
            this.js = js;
            this.condition = condition;
        }
        
        public String getText() {
            return NbBundle.getMessage(NegateCondition.class, "FIX_NegateCondition");
        }

        public ChangeInfo implement() throws Exception {
            js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy wc) throws Exception {
                    wc.toPhase(Phase.PARSED);
                    
                    TreePath tp = condition.resolve(wc);
                    
                    if (tp == null) {
                        return ;
                    }
                    
                    ExpressionTree innerCondition = ((ParenthesizedTree) tp.getLeaf()).getExpression();
                    
                    wc.rewrite(innerCondition, negate(wc, innerCondition));
                }
            }).commit();
            return null;
        }
        
        private static ExpressionTree negate(WorkingCopy wc, ExpressionTree input) {
            TreeMaker make = wc.getTreeMaker();
            
            switch (input.getKind()) {
                case CONDITIONAL_AND:
                    BinaryTree andT = (BinaryTree) input;
                    
                    return make.Binary(Kind.CONDITIONAL_OR, negate(wc, andT.getLeftOperand()), negate(wc, andT.getRightOperand()));
                case CONDITIONAL_OR:
                    BinaryTree orT = (BinaryTree) input;
                    
                    return make.Binary(Kind.CONDITIONAL_AND, negate(wc, orT.getLeftOperand()), negate(wc, orT.getRightOperand()));
                case EQUAL_TO:
                    BinaryTree eqT = (BinaryTree) input;

                    return make.Binary(Kind.NOT_EQUAL_TO, eqT.getLeftOperand(), eqT.getRightOperand());
                    
                case NOT_EQUAL_TO:
                    BinaryTree neqT = (BinaryTree) input;

                    return make.Binary(Kind.EQUAL_TO, neqT.getLeftOperand(), neqT.getRightOperand());
                    
                case METHOD_INVOCATION:
                    return make.Unary(Kind.LOGICAL_COMPLEMENT, input);
                    
                case PARENTHESIZED:
                    ExpressionTree paT = ((ParenthesizedTree) input).getExpression();
                    
                    if (NO_PARETHESIS.contains(paT.getKind()))
                        return make.Parenthesized(negate(wc, paT));
                    else
                        return make.Unary(LOGICAL_COMPLEMENT, input);
                    
                case LOGICAL_COMPLEMENT:
                    ExpressionTree withoutComplement = ((UnaryTree) input).getExpression();
                    
//                    if (withoutComplement.getKind() == Kind.PARENTHESIZED) {
//                        withoutComplement = ((ParenthesizedTree) withoutComplement).getExpression();
//                    }
                    
                    return withoutComplement;
                    
                case IDENTIFIER:
                    return make.Unary(Kind.LOGICAL_COMPLEMENT, input);
                            
                default:
                    return make.Unary(Kind.LOGICAL_COMPLEMENT, make.Parenthesized(input));
            }
        }
        
        private static final Set<Kind> NO_PARETHESIS = EnumSet.of(CONDITIONAL_OR, CONDITIONAL_AND, EQUAL_TO, NOT_EQUAL_TO, METHOD_INVOCATION, PARENTHESIZED, LOGICAL_COMPLEMENT);
        
    }
}
