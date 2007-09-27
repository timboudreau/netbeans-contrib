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

package org.netbeans.api.jackpot;

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.TreePath;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.netbeans.api.java.source.CompilationInfo;
import static com.sun.source.tree.Tree.Kind.*;

/**
 * Utility methods for common query tasks.
 * 
 * @author James Gosling, Tom Ball
 */
public class QueryOperations {
    CompilationInfo info;
    
    /** 
     * Create a QueryOperations instance.
     * 
     * @param info the CompilationInfo used by the calling Query.
     */
    public QueryOperations(CompilationInfo info) {
        this.info = info;
    }
    
    /**
     * Removes unnecessary surrounding braces or parentheses.  For
     * a TreeBlock with a single statement this will return that
     * statement.  For a ParenthesizedExpression, the expression
     * will be returned.  For all other cases the specified tree
     * will be returned.
     * 
     * @param t the tree to be deblocked
     * @return the resulting tree, or <b>t</b> if it was not deblocked
     */
    public static Tree deblock(Tree t) {
	while (t!=null)
	    switch(t.getKind()) {
	    case BLOCK: {
                BlockTree b = (BlockTree)t;
                if (b.isStatic())
                    return t;
		List<? extends StatementTree> stats = b.getStatements();
		if(stats.size() == 1) 
                    t = stats.get(0);
		else 
                    return t;
		break;
	    }
	    case PARENTHESIZED: 
                t = ((ParenthesizedTree)t).getExpression(); 
                break;
	    default: 
                return t;
	    }
	return null;
    }
    
    /**
     * Returns true if the tree is free of side effects.
     * 
     * @param t the tree to inspect
     * @return true if the tree contains no nodes which cause side-effects
     */
    public static boolean sideEffectFree(Tree t) {
        if(t==null) return true;
        if (t instanceof LiteralTree || t instanceof IdentifierTree)
            return true;
        if(t instanceof BinaryTree) {
            BinaryTree b = (BinaryTree) t;
            return sideEffectFree(b.getLeftOperand()) && sideEffectFree(b.getRightOperand());
        }
        Tree.Kind kind = t.getKind();
        if (t instanceof UnaryTree)
            return (kind == PLUS || kind == MINUS || kind == LOGICAL_COMPLEMENT || kind == BITWISE_COMPLEMENT)
                && sideEffectFree(((UnaryTree)t).getExpression());
        switch (kind) {
            default: return false;
            case PARENTHESIZED: return sideEffectFree(((ParenthesizedTree)t).getExpression());
            case MEMBER_SELECT: return sideEffectFree(((MemberSelectTree)t).getExpression());
            case TYPE_CAST: return sideEffectFree(((TypeCastTree)t).getExpression());
            case INSTANCE_OF: return sideEffectFree(((InstanceOfTree)t).getExpression());
            case ARRAY_ACCESS: {
                ArrayAccessTree ix = (ArrayAccessTree) t;
                return sideEffectFree(ix.getExpression()) && sideEffectFree(ix.getIndex());
            }
            case CONDITIONAL_EXPRESSION: {
                ConditionalExpressionTree c = (ConditionalExpressionTree) t;
                return sideEffectFree(c.getCondition()) && 
                        sideEffectFree(c.getTrueExpression()) && 
                        sideEffectFree(c.getFalseExpression());
            }
        }
    }
    
    /**
     * Returns true if the list is free of side effects.
     * 
     * @param list the list of trees to inspect
     * @return true if the list has no trees with side-effects
     */
    public static boolean sideEffectFree(List<Tree> list) {
        for (Tree t : list)
            if (!sideEffectFree(t))
                return false;
        return true;
    }
    
    /**
     * Returns whether a tree is an empty block or statement.
     * 
     * @param t the tree to inspect
     * @return true if the tree is empty
     */
    public static boolean isEmpty(Tree t) {
        if(t==null) return true;
        switch(t.getKind()) {
            default:
                return false;
            case BLOCK:
                for (StatementTree stat : ((BlockTree)t).getStatements())
                    if (!isEmpty(stat))
                        return false;
                return true;
            case EMPTY_STATEMENT:
                return true;
        }
    }
    
    /**
     * Return true if the tree is an IdentifierTree defining the string "true",
     * a Boolean.TRUE literal, or a ParenthesizedExpression surrounding one
     * of these.
     * 
     * @param tree the tree to inspect
     * @return true if the tree evaluates to Boolean.true
     */
    public static boolean isTrue(Tree tree) {
	if(tree==null) return false;
	switch(tree.getKind()) {
            case IDENTIFIER: {
                CharSequence nm = ((IdentifierTree)tree).getName();
                return "true".contentEquals(nm);
            }
            case BOOLEAN_LITERAL: 
                return ((LiteralTree)tree).getValue()==Boolean.TRUE;
            case PARENTHESIZED: 
                return isTrue(((ParenthesizedTree)tree).getExpression());
	}
	return false;
    }
        
    /**
     * Return true if the tree is an IdentifierTree defining the string "false",
     * a Boolean.FALSE literal, or a ParenthesizedExpression surrounding one
     * of these.
     * 
     * @param tree the tree to inspect
     * @return true if the tree evaluates to Boolean.false
     */
    public static boolean isFalse(Tree tree) {
	if(tree==null) return false;
	switch(tree.getKind()) {
            case IDENTIFIER: {
                CharSequence nm = ((IdentifierTree)tree).getName();
                return "false".contentEquals(nm);
            }
            case BOOLEAN_LITERAL: 
                return ((LiteralTree)tree).getValue()==Boolean.FALSE;
            case PARENTHESIZED: 
                return isFalse(((ParenthesizedTree)tree).getExpression());
	}
	return false;
    }
    
    /**
     * Returns the number of statements in the tree.  If it is not a block,
     * returns one for itself (0 for a null tree).  Note:  this routine does
     * not recurse into the statements to find sub-statements.
     * 
     * @param a the tree to inspect
     * @return the number of statements in this tree
     */
    public static int blockLength(Tree a) {
        if (a==null)
            return 0;
        if (!(a instanceof BlockTree))
            return 1;
        return ((BlockTree)a).getStatements().size();
    }
    
    /**
     * Returns a statement at a specified index in another statement.  For
     * BlockTree instances this is equivalent to tree.getStatements().get(index).
     * For other statements, any index but zero will return null.
     * 
     * @param index the statement index
     * @param a the statement from which the sub-statement is fetched
     * @return the sub-statement, or null if there is no statement at that index
     */
    public static StatementTree getStatement(int index, StatementTree a) {
        if (a != null)
            if (a instanceof BlockTree) {
                List<? extends StatementTree> stats = ((BlockTree)a).getStatements();
                return index < stats.size() ? stats.get(index) : null;
            } else
                return index != 0 ? null : a;
        return null;
    }
    
    /**
     * Returns true if the tree defines a constant variable, which in this
     * context means that it is either a final instance variable, a String
     * or a literal.
     * 
     * @param path the tree to inspect 
     * @return true if the tree is constant
     */
    public boolean isConstant(TreePath path) { 
        Element e = info.getTrees().getElement(path);
        if (e == null)
            return false;
        Set<Modifier> flags = e.getModifiers();
        if (e instanceof VariableElement && flags.contains(Modifier.FINAL))
            return true;
        if (e.asType().toString().equals("java.lang.String"))
            return true;
        return path.getLeaf() instanceof LiteralTree;
    }

    /**
     * Returns the class in which this element was declared.
     * @param e the element
     * @return its declaring class
     */
    public static TypeElement getDeclaringClass(Element e) {
        if (e == null || e instanceof TypeElement)
            return (TypeElement)e;
        return getDeclaringClass(e.getEnclosingElement());
    }

    /**
     * Returns the package in which this element's declaring class is a member of.
     * @param e the element
     * @return its package
     */
    public static PackageElement getDeclaringPackage(Element e) {
        if (e == null || e instanceof PackageElement)
            return (PackageElement)e;
        return getDeclaringPackage(e.getEnclosingElement());
    }
}
