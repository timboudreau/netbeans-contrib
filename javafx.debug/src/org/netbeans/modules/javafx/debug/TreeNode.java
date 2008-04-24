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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.javafx.debug;

import com.sun.javafx.api.tree.BindExpressionTree;
import com.sun.javafx.api.tree.BlockExpressionTree;
import com.sun.javafx.api.tree.ClassDeclarationTree;
import com.sun.javafx.api.tree.ForExpressionInClauseTree;
import com.sun.javafx.api.tree.ForExpressionTree;
import com.sun.javafx.api.tree.FunctionDefinitionTree;
import com.sun.javafx.api.tree.FunctionValueTree;
import com.sun.javafx.api.tree.IndexofTree;
import com.sun.javafx.api.tree.InitDefinitionTree;
import com.sun.javafx.api.tree.InstantiateTree;
import com.sun.javafx.api.tree.InterpolateTree;
import com.sun.javafx.api.tree.InterpolateValueTree;
import com.sun.javafx.api.tree.JavaFXTreePathScanner;
import com.sun.javafx.api.tree.JavaFXVariableTree;
import com.sun.javafx.api.tree.ObjectLiteralPartTree;
import com.sun.javafx.api.tree.OnReplaceTree;
import com.sun.javafx.api.tree.SequenceDeleteTree;
import com.sun.javafx.api.tree.SequenceEmptyTree;
import com.sun.javafx.api.tree.SequenceExplicitTree;
import com.sun.javafx.api.tree.SequenceIndexedTree;
import com.sun.javafx.api.tree.SequenceInsertTree;
import com.sun.javafx.api.tree.SequenceRangeTree;
import com.sun.javafx.api.tree.SequenceSliceTree;
import com.sun.javafx.api.tree.SetAttributeToObjectTree;
import com.sun.javafx.api.tree.StringExpressionTree;
import com.sun.javafx.api.tree.TimeLiteralTree;
import com.sun.javafx.api.tree.TriggerTree;
import com.sun.javafx.api.tree.TypeAnyTree;
import com.sun.javafx.api.tree.TypeClassTree;
import com.sun.javafx.api.tree.TypeFunctionalTree;
import com.sun.javafx.api.tree.TypeUnknownTree;
import com.sun.javafx.api.tree.JavaFXTree;
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
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.MethodInvocationTree;
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
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javafx.tree.JavafxPretty;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.javafx.source.CompilationInfo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Lahoda
 */
public class TreeNode extends AbstractNode implements OffsetProvider {
    
    private TreePath tree;
    private CompilationInfo info;
    private boolean synthetic;
    
    public static Node getTree(CompilationInfo info, TreePath tree) {
        List<Node> result = new ArrayList<Node>();
        
        new FindChildrenTreeVisitor(info).scan(tree, result);
        
        return result.get(0);
    }
    
    private static String treeToString(CompilationInfo info, TreePath tp) {
        Tree t = tp.getLeaf();
        JavaFXTree.JavaFXKind k = null;
        StringWriter s = new StringWriter();
        try {
            new JavafxPretty(s, false).printExpr((JCTree)t);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        if (t instanceof JavaFXTree && t.getKind() == Kind.OTHER) {
            JavaFXTree jfxt = (JavaFXTree)t;
            k = jfxt.getJavaFXKind();
        }
        String res = null;
        if (k != null) {
            res = k.toString();
        } else {
            res = String.valueOf(t.getKind());
            // XXX:debugging
            if (res.equals("null")) {
                System.err.println("Tree with null kind:" + t.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(t)));
//                System.err.println("  is " + t);
            }
        }
        SourcePositions pos = info.getTrees().getSourcePositions();
        res = res + '[' + pos.getStartPosition(tp.getCompilationUnit(), t) + ',' + 
                pos.getEndPosition(tp.getCompilationUnit(), t) + "]:" + s.toString();
        return res;
    }

    /** Creates a new instance of TreeNode */
    public TreeNode(CompilationInfo info, TreePath tree, List<Node> nodes) {
        super(nodes.isEmpty() ? Children.LEAF: new NodeChilren(nodes));
        this.tree = tree;
        this.info = info;
        // TODO:
//        this.synthetic = info.getTreeUtilities().isSynthetic(tree);
        setDisplayName(treeToString(info, tree)); //NOI18N
        setIconBaseWithExtension("org/netbeans/modules/java/debug/resources/tree.png"); //NOI18N
    }

    @Override
    public String getHtmlDisplayName() {
        if (synthetic) {
            return "<html><font color='#808080'>" + translate(getDisplayName()); //NOI18N
        }
        
        return null;
    }
            
    private static String[] c = new String[] {"&", "<", ">", "\""}; // NOI18N
    private static String[] tags = new String[] {"&amp;", "&lt;", "&gt;", "&quot;"}; // NOI18N
    
    private String translate(String input) {
        for (int cntr = 0; cntr < c.length; cntr++) {
            input = input.replaceAll(c[cntr], tags[cntr]);
        }
        
        return input;
    }
    
    public int getStart() {
        return (int)info.getTrees().getSourcePositions().getStartPosition(tree.getCompilationUnit(), tree.getLeaf());
    }

    public int getEnd() {
        return (int)info.getTrees().getSourcePositions().getEndPosition(tree.getCompilationUnit(), tree.getLeaf());
    }

    public int getPreferredPosition() {
        return -1;
    }
    
    private static final class NodeChilren extends Children.Keys {
        
        public NodeChilren(List<Node> nodes) {
            setKeys(nodes);
        }
        
        protected Node[] createNodes(Object key) {
            return new Node[] {(Node) key};
        }
        
    }
    
    private static class FindChildrenTreeVisitor extends JavaFXTreePathScanner<Void, List<Node>> {
        
        private CompilationInfo info;
        
        public FindChildrenTreeVisitor(CompilationInfo info) {
            this.info = info;
        }

        @Override
        public Void visitBindExpression(BindExpressionTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitBindExpression(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitBlockExpression(BlockExpressionTree tree, List<Node> d) {
                        List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitBlockExpression(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitClassDeclaration(ClassDeclarationTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitClassDeclaration(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitForExpression(ForExpressionTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitForExpression(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitForExpressionInClause(ForExpressionInClauseTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitForExpressionInClause(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitFunctionDefinition(FunctionDefinitionTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitFunctionDefinition(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitFunctionValue(FunctionValueTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitFunctionValue(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitIndexof(IndexofTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitIndexof(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitInitDefinition(InitDefinitionTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitInitDefinition(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitInterpolate(InterpolateTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitInterpolate(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitInterpolateValue(InterpolateValueTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitInterpolateValue(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitObjectLiteralPart(ObjectLiteralPartTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitObjectLiteralPart(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        public Void visitVariable(JavaFXVariableTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitVariable/*Declaration*/(tree, below);
            // XXX: Won't be there, just JFXC-1119 workaround
//            super.scan(tree.getOnReplaceTree(), below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitOnReplace(OnReplaceTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitOnReplace(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitPostInitDefinition(InitDefinitionTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitPostInitDefinition(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSequenceDelete(SequenceDeleteTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSequenceDelete(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSequenceEmpty(SequenceEmptyTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSequenceEmpty(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSequenceExplicit(SequenceExplicitTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSequenceExplicit(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSequenceIndexed(SequenceIndexedTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSequenceIndexed(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSequenceInsert(SequenceInsertTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSequenceInsert(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSequenceRange(SequenceRangeTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSequenceRange(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSequenceSlice(SequenceSliceTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSequenceSlice(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSetAttributeToObject(SetAttributeToObjectTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSetAttributeToObject(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitStringExpression(StringExpressionTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitStringExpression(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTimeLiteral(TimeLiteralTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitOther(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTrigger(TriggerTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitTrigger(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTypeAny(TypeAnyTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitTypeAny(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTypeClass(TypeClassTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitTypeClass(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTypeFunctional(TypeFunctionalTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitTypeFunctional(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTypeUnknown(TypeUnknownTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitTypeUnknown(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitOther(Tree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitOther(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }
        
        @Override
        public Void visitAnnotation(AnnotationTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            //???
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitAnnotation(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitMethodInvocation(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitAssert(AssertTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitAssert(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitAssignment(AssignmentTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitAssignment(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitCompoundAssignment(CompoundAssignmentTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitCompoundAssignment(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitBinary(BinaryTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitBinary(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitBlock(BlockTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitBlock(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitBreak(BreakTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitBreak(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitCase(CaseTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitCase(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitCatch(CatchTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitCatch(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitClass(ClassTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitClass(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitConditionalExpression(ConditionalExpressionTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitConditionalExpression(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitInstantiate(InstantiateTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitInstantiate(tree, below);

            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitContinue(ContinueTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitContinue(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitDoWhileLoop(DoWhileLoopTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitDoWhileLoop(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitErroneous(ErroneousTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            scan(((com.sun.tools.javac.tree.JCTree.JCErroneous)tree).errs, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitExpressionStatement(ExpressionStatementTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitExpressionStatement(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitEnhancedForLoop(EnhancedForLoopTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitEnhancedForLoop(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitForLoop(ForLoopTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitForLoop(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitIdentifier(IdentifierTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitIdentifier(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitIf(IfTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitIf(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitImport(ImportTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitImport(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitArrayAccess(ArrayAccessTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitArrayAccess(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitLabeledStatement(LabeledStatementTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitLabeledStatement(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitLiteral(LiteralTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitLiteral(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitMethod(MethodTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitMethod(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitModifiers(ModifiersTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitModifiers(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitNewArray(NewArrayTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitNewArray(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitNewClass(NewClassTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitNewClass(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitParenthesized(ParenthesizedTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitParenthesized(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitReturn(ReturnTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitReturn(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitMemberSelect(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitEmptyStatement(EmptyStatementTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitEmptyStatement(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSwitch(SwitchTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSwitch(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitSynchronized(SynchronizedTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitSynchronized(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitThrow(ThrowTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitThrow(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitCompilationUnit(CompilationUnitTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitCompilationUnit(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTry(TryTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitTry(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitParameterizedType(ParameterizedTypeTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitParameterizedType(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitArrayType(ArrayTypeTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitArrayType(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTypeCast(TypeCastTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitTypeCast(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitPrimitiveType(PrimitiveTypeTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitPrimitiveType(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitTypeParameter(TypeParameterTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitTypeParameter(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitInstanceOf(InstanceOfTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitInstanceOf(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitUnary(UnaryTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitUnary(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitVariable(VariableTree tree, List<Node> d) {
            // XXX: Won't be there, just JFXC-1119 workaround
/*            if (tree instanceof JavaFXVariableTree) {
                visitVariableDeclaration((JavaFXVariableTree)tree, d);
                return null;
            }*/
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingElement(below);
            addCorrespondingType(below);
            addCorrespondingComments(below);
            
            super.visitVariable(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitWhileLoop(WhileLoopTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitWhileLoop(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }

        @Override
        public Void visitWildcard(WildcardTree tree, List<Node> d) {
            List<Node> below = new ArrayList<Node>();
            
            addCorrespondingType(below);
            addCorrespondingComments(below);
            super.visitWildcard(tree, below);
            
            d.add(new TreeNode(info, getCurrentPath(), below));
            return null;
        }
        
        private void addCorrespondingElement(List<Node> below) {
            Element el = info.getTrees().getElement(getCurrentPath());
            
            if (el != null) {
                below.add(new ElementNode(info, el, Collections.EMPTY_LIST));
            } else {
                below.add(new NotFoundElementNode(NbBundle.getMessage(TreeNode.class, "Cannot_Resolve_Element")));
            }
        }

        private void addCorrespondingType(List<Node> below) {
            TypeMirror tm = info.getTrees().getTypeMirror(getCurrentPath());
            
            if (tm != null) {
                below.add(new TypeNode(tm));
            } else {
                below.add(new NotFoundTypeNode(NbBundle.getMessage(TreeNode.class, "Cannot_Resolve_Type")));
            }
        }
        
        private void addCorrespondingComments(List<Node> below) {
// TODO:
//            below.add(new CommentsNode(NbBundle.getMessage(TreeNode.class, "NM_Preceding_Comments"), info.getTreeUtilities().getComments(getCurrentPath().getLeaf(), true)));
//            below.add(new CommentsNode(NbBundle.getMessage(TreeNode.class, "NM_Trailing_Comments"), info.getTreeUtilities().getComments(getCurrentPath().getLeaf(), false)));
        }
    }
    
    private static class NotFoundElementNode extends AbstractNode {
        
        public NotFoundElementNode(String name) {
            super(Children.LEAF);
            setName(name);
            setDisplayName(name);
            setIconBaseWithExtension("org/netbeans/modules/java/debug/resources/element.png"); //NOI18N
        }
        
    }
    
    private static class TypeNode extends AbstractNode {
        
        public TypeNode(TypeMirror type) {
            super(Children.LEAF);
            setDisplayName(type.getKind().toString() + ":" + type.toString()); //NOI18N
            setIconBaseWithExtension("org/netbeans/modules/java/debug/resources/type.png"); //NOI18N
        }
        
    }
    
    private static class NotFoundTypeNode extends AbstractNode {
        
        public NotFoundTypeNode(String name) {
            super(Children.LEAF);
            setName(name);
            setDisplayName(name);
            setIconBaseWithExtension("org/netbeans/modules/java/debug/resources/type.png"); //NOI18N
        }
        
    }    
}
