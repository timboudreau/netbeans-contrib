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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.api.jackpot;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import java.util.List;

/**
 * Determines whether two trees deeply match each other, using a 
 * fast-fail algorithm.  Trees are deblocked before comparison; for
 * example, the tree for "if (test) { return; }" will match
 * "if (test) return;".
 * 
 * @see org.netbeans.api.jackpot.ConversionOperations#deblock
 */
public final class TreeMatcher {
    
    /**
     * Returns true if two trees match.
     * 
     * @param a the first tree
     * @param b the tree to compare to a
     * @return true if the trees match
     */
    public static final boolean matches(Tree a, Tree b) {
	Matcher matcher = new Matcher();
        matcher.match(a, b);
        return matcher.matches;
    }

    /**
     * Returns true if two tree lists match.
     * 
     * @param a the first tree list
     * @param b the tree list to compare to a
     * @return true if the tree lists match
     */
    public static boolean matches(List<? extends Tree> a, List<? extends Tree> b) {
        int n = a.size();
        if (n != b.size())
            return false;
	Matcher matcher = new Matcher();
        for (int i = 0; i < n; i++) {
            matcher.match(a.get(i), b.get(i));
            if (!matcher.matches)
                return false;
        }
	return true;
    }
    
    private static class Matcher implements TreeVisitor<Void,Tree> {
        private boolean matches = true;    

        /** Visitor method: Scan a single tree.
         */
        private void match(Tree tree, Tree other) {
            if (matches) {
                if((tree = QueryOperations.deblock(tree)) != null && 
                   (other = QueryOperations.deblock(other)) != null)
                    if(tree.getKind() != other.getKind())
                        matches = false;
                    else {
                        tree.accept(this, other);
                    }
                else if(other != tree) 
                    matches = false;
            }
        }

        /** Visitor method: scan a list of trees.
         */
        private void match(List<? extends Tree> a, List<? extends Tree> b) {
            if (!matches)
                return;
            if (a == null && b == null)
                return;
            if (a == null || b == null) {
                matches = false;
                return;
            }
            int n = a.size();
            if (n != b.size()) {
                matches = false;
                return;
            }
            for (int i = 0; i < n && matches; i++)
                match(a.get(i), b.get(i));
        }

        private void match(CharSequence s1, CharSequence s2) {
            matches = matches && (s1 == null && s2 == null) || (s1 != null && s1.equals(s2));
        }

    /****************************************************************************
     * Visitor methods
     ****************************************************************************/

        public Void visitCompilationUnit(CompilationUnitTree tree, Tree p) {
            CompilationUnitTree other = (CompilationUnitTree)p;
            match(tree.getPackageAnnotations(), other.getPackageAnnotations());
            match(tree.getPackageName(), other.getPackageName());
            match(tree.getImports(), other.getImports());
            match(tree.getTypeDecls(), other.getTypeDecls());
            return null;
        }

        public Void visitImport(ImportTree tree, Tree p) {
            ImportTree other = (ImportTree)p;
            match(tree.getQualifiedIdentifier(), other.getQualifiedIdentifier());
            return null;
        }

        public Void visitClass(ClassTree tree, Tree p) {
            ClassTree other = (ClassTree)p;
            match(tree.getSimpleName(), other.getSimpleName());
            match(tree.getModifiers(), other.getModifiers());
            match(tree.getTypeParameters(), other.getTypeParameters());
            match(tree.getExtendsClause(), other.getExtendsClause());
            match(tree.getImplementsClause(), other.getImplementsClause());
            match(tree.getMembers(), other.getMembers());
            return null;
        }

        public Void visitMethod(MethodTree tree, Tree p) {
            MethodTree other = (MethodTree)p;
            match(tree.getName(), other.getName());
            match(tree.getModifiers(), other.getModifiers());
            match(tree.getTypeParameters(), other.getTypeParameters());
            match(tree.getParameters(), other.getParameters());
            match(tree.getReturnType(), other.getReturnType());
            match(tree.getThrows(), other.getThrows());
            match(tree.getDefaultValue(), other.getDefaultValue());
            match(tree.getBody(), other.getBody());
            return null;
        }

        public Void visitVariable(VariableTree tree, Tree p) {
            VariableTree other = (VariableTree)p;
            match(tree.getName(), other.getName());
            match(tree.getModifiers(), other.getModifiers());
            match(tree.getType(), other.getType());
            match(tree.getInitializer(), other.getInitializer());
            return null;
        }

        public Void visitAnnotation(AnnotationTree tree, Tree p) {
            AnnotationTree other = (AnnotationTree)p;
            match(tree.getAnnotationType(), other.getAnnotationType());
            match(tree.getArguments(), other.getArguments());
            return null;
        }

        public Void visitMethodInvocation(MethodInvocationTree tree, Tree p) {
            MethodInvocationTree other = (MethodInvocationTree)p;
            match(tree.getMethodSelect(), other.getMethodSelect());
            match(tree.getTypeArguments(), other.getTypeArguments());
            match(tree.getArguments(), other.getArguments());
            return null;
        }

        public Void visitAssert(AssertTree tree, Tree p) {
            AssertTree other = (AssertTree)p;
            match(tree.getCondition(), other.getCondition());
            match(tree.getDetail(), other.getDetail());
            return null;
        }

        public Void visitAssignment(AssignmentTree tree, Tree p) {
            AssignmentTree other = (AssignmentTree)p;
            match(tree.getVariable(), other.getVariable());
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitCompoundAssignment(CompoundAssignmentTree tree, Tree p) {
            CompoundAssignmentTree other = (CompoundAssignmentTree)p;
            match(tree.getVariable(), other.getVariable());
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitBinary(BinaryTree tree, Tree p) {
            BinaryTree other = (BinaryTree)p;
            match(tree.getLeftOperand(), other.getLeftOperand());
            match(tree.getRightOperand(), other.getRightOperand());
            return null;
        }

        public Void visitBlock(BlockTree tree, Tree p) {
            BlockTree other = (BlockTree)p;
            match(tree.getStatements(), other.getStatements());
            matches = matches && tree.isStatic() == other.isStatic();
            return null;
        }

        public Void visitBreak(BreakTree tree, Tree p) {
            BreakTree other = (BreakTree)p;
            match(tree.getLabel(), other.getLabel());
            return null;
        }

        public Void visitCase(CaseTree tree, Tree p) {
            CaseTree other = (CaseTree)p;
            match(tree.getExpression(), other.getExpression());
            match(tree.getStatements(), other.getStatements());
            return null;
        }

        public Void visitCatch(CatchTree tree, Tree p) {
            CatchTree other = (CatchTree)p;
            match(tree.getParameter(), other.getParameter());
            match(tree.getBlock(), other.getBlock());
            return null;
        }

        public Void visitConditionalExpression(ConditionalExpressionTree tree, Tree p) {
            ConditionalExpressionTree other = (ConditionalExpressionTree)p;
            match(tree.getCondition(), other.getCondition());
            match(tree.getFalseExpression(), other.getFalseExpression());
            match(tree.getTrueExpression(), other.getTrueExpression());
            return null;
        }

        public Void visitContinue(ContinueTree tree, Tree p) {
            ContinueTree other = (ContinueTree)p;
            match(tree.getLabel(), other.getLabel());
            return null;
        }

        public Void visitDoWhileLoop(DoWhileLoopTree tree, Tree p) {
            DoWhileLoopTree other = (DoWhileLoopTree)p;
            match(tree.getCondition(), other.getCondition());
            match(tree.getStatement(), other.getStatement());
            return null;
        }

        public Void visitErroneous(ErroneousTree tree, Tree p) {
            return null;
        }

        public Void visitExpressionStatement(ExpressionStatementTree tree, Tree p) {
            ExpressionStatementTree other = (ExpressionStatementTree)p;
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitEnhancedForLoop(EnhancedForLoopTree tree, Tree p) {
            EnhancedForLoopTree other = (EnhancedForLoopTree)p;
            match(tree.getVariable(), other.getVariable());
            match(tree.getExpression(), other.getExpression());
            match(tree.getStatement(), other.getStatement());
            return null;
        }

        public Void visitForLoop(ForLoopTree tree, Tree p) {
            ForLoopTree other = (ForLoopTree)p;
            match(tree.getInitializer(), other.getInitializer());
            match(tree.getCondition(), other.getCondition());
            match(tree.getUpdate(), other.getUpdate());
            match(tree.getStatement(), other.getStatement());
            return null;
        }

        public Void visitIdentifier(IdentifierTree tree, Tree p) {
            IdentifierTree other = (IdentifierTree)p;
            match(tree.getName(), other.getName());
            return null;
        }

        public Void visitIf(IfTree tree, Tree p) {
            IfTree other = (IfTree)p;
            match(tree.getCondition(), other.getCondition());
            match(tree.getThenStatement(), other.getThenStatement());
            match(tree.getElseStatement(), other.getElseStatement());
            return null;
        }

        public Void visitArrayAccess(ArrayAccessTree tree, Tree p) {
            ArrayAccessTree other = (ArrayAccessTree)p;
            match(tree.getExpression(), other.getExpression());
            match(tree.getIndex(), other.getIndex());
            return null;
        }

        public Void visitLabeledStatement(LabeledStatementTree tree, Tree p) {
            LabeledStatementTree other = (LabeledStatementTree)p;
            match(tree.getLabel(), other.getLabel());
            match(tree.getStatement(), other.getStatement());
            return null;
        }

        public Void visitLiteral(LiteralTree tree, Tree p) {
            LiteralTree other = (LiteralTree)p;
            Object v1 = tree.getValue();
            Object v2 = other.getValue();
            matches = matches && 
                    (v1 == null && v2 == null || v1 != null && v1.equals(v2));
            return null;
        }

        public Void visitModifiers(ModifiersTree tree, Tree p) {
            ModifiersTree other = (ModifiersTree)p;
            matches = matches && tree.getFlags().equals(other.getFlags());
            match(tree.getAnnotations(), other.getAnnotations());
            return null;
        }

        public Void visitNewArray(NewArrayTree tree, Tree p) {
            NewArrayTree other = (NewArrayTree)p;
            match(tree.getType(), other.getType());
            match(tree.getDimensions(), other.getDimensions());
            match(tree.getInitializers(), other.getInitializers());
            return null;
        }

        public Void visitNewClass(NewClassTree tree, Tree p) {
            NewClassTree other = (NewClassTree)p;
            match(tree.getIdentifier(), other.getIdentifier());
            match(tree.getTypeArguments(), other.getTypeArguments());
            match(tree.getArguments(), other.getArguments());
            match(tree.getClassBody(), other.getClassBody());
            match(tree.getEnclosingExpression(), other.getEnclosingExpression());
            return null;
        }

        public Void visitParenthesized(ParenthesizedTree tree, Tree p) {
            ParenthesizedTree other = (ParenthesizedTree)p;
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitReturn(ReturnTree tree, Tree p) {
            ReturnTree other = (ReturnTree)p;
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitMemberSelect(MemberSelectTree tree, Tree p) {
            MemberSelectTree other = (MemberSelectTree)p;
            match(tree.getIdentifier(), other.getIdentifier());
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitEmptyStatement(EmptyStatementTree tree, Tree p) {
            return null;
        }

        public Void visitSwitch(SwitchTree tree, Tree p) {
            SwitchTree other = (SwitchTree)p;
            match(tree.getCases(), other.getCases());
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitSynchronized(SynchronizedTree tree, Tree p) {
            SynchronizedTree other = (SynchronizedTree)p;
            match(tree.getBlock(), other.getBlock());
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitThrow(ThrowTree tree, Tree p) {
            ThrowTree other = (ThrowTree)p;
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitTry(TryTree tree, Tree p) {
            TryTree other = (TryTree)p;
            match(tree.getBlock(), other.getBlock());
            match(tree.getCatches(), other.getCatches());
            match(tree.getFinallyBlock(), other.getFinallyBlock());
            return null;
        }

        public Void visitParameterizedType(ParameterizedTypeTree tree, Tree p) {
            ParameterizedTypeTree other = (ParameterizedTypeTree)p;
            match(tree.getType(), other.getType());
            match(tree.getTypeArguments(), other.getTypeArguments());
            return null;
        }

        public Void visitArrayType(ArrayTypeTree tree, Tree p) {
            ArrayTypeTree other = (ArrayTypeTree)p;
            match(tree.getType(), other.getType());
            return null;
        }

        public Void visitTypeCast(TypeCastTree tree, Tree p) {
            TypeCastTree other = (TypeCastTree)p;
            match(tree.getType(), other.getType());
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitPrimitiveType(PrimitiveTypeTree tree, Tree p) {
            PrimitiveTypeTree other = (PrimitiveTypeTree)p;
            matches = matches && 
                tree.getPrimitiveTypeKind() == other.getPrimitiveTypeKind();
            return null;
        }

        public Void visitTypeParameter(TypeParameterTree tree, Tree p) {
            TypeParameterTree other = (TypeParameterTree)p;
            match(tree.getName(), other.getName());
            match(tree.getBounds(), other.getBounds());
            return null;
        }

        public Void visitInstanceOf(InstanceOfTree tree, Tree p) {
            InstanceOfTree other = (InstanceOfTree)p;
            match(tree.getType(), other.getType());
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitUnary(UnaryTree tree, Tree p) {
            UnaryTree other = (UnaryTree)p;
            match(tree.getExpression(), other.getExpression());
            return null;
        }

        public Void visitWhileLoop(WhileLoopTree tree, Tree p) {
            WhileLoopTree other = (WhileLoopTree)p;
            match(tree.getCondition(), other.getCondition());
            match(tree.getStatement(), other.getStatement());
            return null;
        }

        public Void visitWildcard(WildcardTree tree, Tree p) {
            WildcardTree other = (WildcardTree)p;
            match(tree.getBound(), other.getBound());
            return null;
        }

        public Void visitOther(Tree tree, Tree p) {
            return null;
        }
    }
}
