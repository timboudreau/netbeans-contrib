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

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.java.source.Comment;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import static com.sun.source.tree.Tree.Kind;

/**
 * Utility methods for creating and simplifying trees using techniques such 
 * as constant-folding and dead-code analysis.  All of these methods preserve
 * the semantic meaning of the tree, so updating an original tree will not 
 * change its behavior.
 * 
 * @author James Gosling, Tom Ball
 */
public class ConversionOperations extends QueryOperations {
    TreeMaker make;
    TreeUtilities treeUtils;
    
    /** 
     * Create a ConversionOperations instance.
     * 
     * @param workingCopy the workingCopy used by the calling Transformer.
     */
    public ConversionOperations(WorkingCopy workingCopy) {
        super(workingCopy);
        make = workingCopy.getTreeMaker();
        treeUtils = workingCopy.getTreeUtilities();
    }
    
    /**
     * Creates a BlockTree which contains a specified StatementTree.  If
     * the StatementTree is an instanceof BlockTree or null, then the
     * tree itself is returned.
     * 
     * @param a the statement to be blocked
     * @return the returned BlockTree
     */
    public BlockTree block(StatementTree a) {
        if(a instanceof BlockTree) 
            return (BlockTree) a;
        if(a==null) 
            return null;
        return make.Block(Arrays.asList(statement(a)), false);
    }
    
    /**
     * Creates a single statement from two statements.  Either parameter can be 
     * a block or any other statement type.  Blocks are flattened, with any 
     * unreachable statements removed.  If either statement is empty, the other
     * is returned.
     * 
     * @param a the first statement
     * @param b the second statement
     * @return a statement which combines the two parameters
     */
    public StatementTree block(StatementTree a, StatementTree b) {
        a = statement(a);
        b = statement(b);
        if(isEmpty(a)) return b;
        if(isEmpty(b)) return a;
        List<StatementTree> stats = new ArrayList<StatementTree>();
        if(a instanceof BlockTree) {
            stats.addAll(((BlockTree)a).getStatements());
            if (b instanceof BlockTree)
                stats.addAll(((BlockTree)b).getStatements());
            else
                stats.add(b);
        } else {
            stats.add(a);
            if(b instanceof BlockTree)
                stats.addAll(((BlockTree)b).getStatements());
            else
                stats.add(b);
        }
        stats = chopUnreachable(stats);
        return make.Block(stats, false);
    }
    
    /**
     * Removes any trailing statements in a list if they are unreachable during
     * execution.  One common example is a break statement following a return
     * statement in a switch.
     *
     * @param stats the list of statements to inspect
     * @return a shortened list with the unreachable statements removed, or the original list
     */
    public static <T extends Tree> java.util.List<T> chopUnreachable(java.util.List<T> stats) {
        List<T> newStats = new ArrayList<T>();
        boolean foundEndStatement = false;
        for (T t : stats) {
            switch (t.getKind()) {
                case BREAK:
                case CONTINUE:
                case THROW:
                case RETURN:
                    foundEndStatement = true;
                    break;
                default:
                    if (foundEndStatement)
                        // found statement following end, discard rest of list
                        return newStats;
            }
            newStats.add(t);
        }
        return stats;  // unchanged, return original
    }
    
    private static final int COPY_REST = 99999;
    
    /**
     * Create a statement which contains a subset of the statements in a
     * specified tree.  This method is similar to String.substring(n), 
     * with statements instead of characters.
     * <br>
     * Although the parameter and return value are normally
     * BlockTree instances, other statements may be used.
     * 
     * @param t the statement tree used to create the sublist
     * @param index the index of the first statement to be returned
     * @return the sublist
     */
    public StatementTree sublist(StatementTree t, int index) {
        return sublist(t, index, COPY_REST);
    }
    
    /**
     * Create a sublist of a list of statements.
     * 
     * @param t the list used to create the sublist
     * @param index the index of the first statement to be returned
     * @param len the number of statements to include
     * @return the sublist
     */
    public static <T extends Tree> List<T> sublist(List<T> t, int index, int len) {
        if (t.isEmpty())
            return t;
        if (len == 0 || index >= t.size())
            return Collections.<T>emptyList();
        int lastIndex = Math.min(index + len, t.size());
        return t.subList(index, lastIndex);
    }
    
    /**
     * Create a statement which contains a subset of the statements in a
     * specified tree.  This method is similar to String.substring(start, n), 
     * with statements instead of characters.
     * <br>
     * Although the parameter and return value are normally
     * BlockTree instances, other statements may be used.
     * 
     * @param t the statement tree used to create the sublist
     * @param index the index of the first statement to be returned
     * @param len the number of statements to include
     * @return the new sublist
     */
    public StatementTree sublist(StatementTree t,int index, int len) {
        StatementTree ret;
        if (t==null || len <= 1 || !(t instanceof BlockTree))
            ret = len<=0 ? null : getStatement(index, t);
        else {
            BlockTree block = (BlockTree)t;
            List<? extends StatementTree> otail = block.getStatements();
            List<? extends StatementTree> ntail = sublist(otail,index,len);
            ret = ntail==otail ? t : make.Block(ntail, false);
        }
        copyComments(t, ret);
        return ret;
    }
    
    /**
     * Copy all comments associated with one tree to another.  The original
     * tree does not need to have any comments associated with it, however,
     * nor do the trees need to be the same kind of tree.
     * 
     * @param oldTree the tree with comments to copy
     * @param newTree the tree to copy the comments to
     */
    public void copyComments(Tree oldTree, Tree newTree) {
        addComments(oldTree, newTree, true);
        addComments(oldTree, newTree, false);
    }
    
    private void addComments(Tree oldTree, Tree newTree, boolean preceding) {
        List<Comment> comments = treeUtils.getComments(oldTree, preceding);
        if (comments != null) {
            for (Comment comment : comments)
                make.addComment(newTree, comment, preceding);
        }
    }

    /**
     * Create a block from a list of statements.
     * 
     * @param stats the list of statements
     * @param isStatic true if the block has a static modifier
     * @return a block tree
     */
    public BlockTree block(List<StatementTree> stats, boolean isStatic) {
        return make.Block(flatten(stats), isStatic);
    }
    
    private List<StatementTree> flatten(List<StatementTree> l) {
        if(l==null || l.isEmpty())
            return Collections.<StatementTree>emptyList();
        List<StatementTree> newList = new ArrayList<StatementTree>();
        for (StatementTree stat : l) {
            if (isEmpty(stat))
                continue;  // remove empty blocks and statements
            if (stat instanceof BlockTree) {
                // replace block with its statement list unless there is a
                // variable defined within it.
                List<StatementTree> sub =
                        convert(StatementTree.class, ((BlockTree)stat).getStatements());
                sub = flatten(sub);
                boolean hasVariable = false;
                for(StatementTree t : sub)
                    if(t instanceof VariableTree) {
                    hasVariable = true;
                    break;
                    }
                if (hasVariable)
                    newList.add(stat);
                else
                    newList.addAll(sub);
            } else
                newList.add(stat);
        }
        return newList;
    }
    
    /**
     * Returns a statement from an expression.  If the tree is a
     * conditional expression, it is converted to an IfTree.
     * If it is an expression, it is converted into an ExpressTree.
     * Otherwise the tree itself is returned.
     * 
     * @param t the tree to convert into a statement
     * @return the new statement, or t if it is a statement
     * @throws IllegalArgumentException if the tree is not an expression or statment
     */
    public StatementTree statement(Tree t) {
        if(t==null) return null;
        if(t instanceof ConditionalExpressionTree) {
            ConditionalExpressionTree ce = (ConditionalExpressionTree) t;
            t = If(ce.getCondition(),
                    statement(ce.getTrueExpression()),
                    statement(ce.getFalseExpression()));
        } else if (t instanceof ExpressionTree)
            t = make.ExpressionStatement((ExpressionTree)t);
        if (!(t instanceof StatementTree))
            throw new IllegalArgumentException("invalid tree type: " + t.getKind());
        return (StatementTree)t;
    }
    
    /**
     * Returns an AND binary expression tree from left and right operands.  If
     * either of the operands evaluates to Boolean.TRUE or Boolean.FALSE, then
     * the appropriate side is returned instead.  This is similar to what the
     * compiler might do during constant folding.
     * 
     * @param left the left-hand operand
     * @param right the right-hand operand
     * @return the AND expression, or equivalent
     */
    public ExpressionTree and(ExpressionTree left,ExpressionTree right) {
        Object ae = eval(left);
        if(ae==Boolean.TRUE) return right;
        if(ae==Boolean.FALSE) return left;
        Object be = eval(right);
        if(be==Boolean.TRUE) return left;
        if(be==Boolean.FALSE && sideEffectFree(left)) return right;
        return make.Binary(Kind.CONDITIONAL_AND,left,right);
    }
    
    /**
     * Returns an OR binary expression tree from left and right operands.  If
     * either of the operands evaluates to Boolean.TRUE or Boolean.FALSE, then
     * the appropriate side is returned instead.  This is similar to what the
     * compiler might do during constant folding.
     * 
     * @param left the left-hand operand
     * @param right the right-hand operand
     * @return the OR expression, or equivalent
     */
    public ExpressionTree or(ExpressionTree left, ExpressionTree right) {
        Object ae = eval(left);
        if(ae==Boolean.TRUE) return left;
        if(ae==Boolean.FALSE) return right;
        Object be = eval(right);
        if(be==Boolean.TRUE && sideEffectFree(left)) return right;
        if(be==Boolean.FALSE) return left;
        return make.Binary(Kind.CONDITIONAL_OR,left,right);
    }
    
    /**
     * Returns a NOT expression tree from an expression.  If the expression
     * can be simplified, an equivalent expression is returned instead.
     * 
     * @param t the expression to be negated
     * @return the NOT expression, or equivalent
     */
    public ExpressionTree not(ExpressionTree t) {
        if(t==null) return null;
        
        // replace a boolean literal with its opposite
        if (t instanceof LiteralTree) {
            Object o = ((LiteralTree)t).getValue();
            if (o instanceof Boolean)
                return make.Literal(Boolean.valueOf(((Boolean)o).booleanValue()));
        }
        
        // if it's a ! expression, remove the !
        Kind kind = t.getKind();
        if (kind == Kind.LOGICAL_COMPLEMENT)
            return ((UnaryTree)t).getExpression();
        
        Kind otherKind = relInvert.get(kind);
        if (otherKind != null) {
            BinaryTree op = (BinaryTree)t;
            return make.Binary(otherKind, not(op.getLeftOperand()), not(op.getRightOperand()));
        } else
            return make.Unary(Kind.LOGICAL_COMPLEMENT, t);
    }
    
    private static java.util.Map<Kind,Kind> relInvert;
    static {
        relInvert = new java.util.EnumMap<Kind,Kind>(Kind.class);
        relInvert.put(Kind.CONDITIONAL_AND, Kind.CONDITIONAL_OR);
        relInvert.put(Kind.CONDITIONAL_OR, Kind.CONDITIONAL_AND);
        relInvert.put(Kind.EQUAL_TO, Kind.NOT_EQUAL_TO);
        relInvert.put(Kind.NOT_EQUAL_TO, Kind.EQUAL_TO);
        relInvert.put(Kind.GREATER_THAN, Kind.LESS_THAN);
        relInvert.put(Kind.LESS_THAN, Kind.GREATER_THAN);
        relInvert.put(Kind.GREATER_THAN_EQUAL, Kind.LESS_THAN_EQUAL);
        relInvert.put(Kind.LESS_THAN_EQUAL, Kind.GREATER_THAN_EQUAL);
    }

    /**
     * Perform constant-folding on an expression tree, either returning a
     * simpler version of the tree or the tree itself.
     * 
     * @param t the tree to evaluate
     * @return an equivalent tree which has been simplified, or the tree itself
     *         if no folding can be performed.
     */
    public Object eval(ExpressionTree t) {
        if(t==null) return t;
        t = (ExpressionTree)deblock(t);
        try {
            if (t instanceof BinaryTree) {
                BinaryTree tree = (BinaryTree)t;
                Object lhs = eval(tree.getLeftOperand());
                if(lhs==null) return null;
                switch(tree.getKind()) {
                    case OR:  if(lhs==Boolean.TRUE)  return lhs; break;
                    case AND: if(lhs==Boolean.FALSE) return lhs; break;
                }
                Object rhs = eval(tree.getRightOperand());
                if(rhs==null) return null;
                switch(tree.getKind()) {
                    case CONDITIONAL_OR: return rhs;
                    case CONDITIONAL_AND: return rhs;
                    case EQUAL_TO:  return Boolean.valueOf(compare(lhs,rhs)==0);
                    case NOT_EQUAL_TO:  return Boolean.valueOf(compare(lhs,rhs)!=0);
                    case LESS_THAN:  return Boolean.valueOf(compare(lhs,rhs)<0);
                    case LESS_THAN_EQUAL:  return Boolean.valueOf(compare(lhs,rhs)<=0);
                    case GREATER_THAN:  return Boolean.valueOf(compare(lhs,rhs)>0);
                    case GREATER_THAN_EQUAL:  return Boolean.valueOf(compare(lhs,rhs)>=0);
                }
                if(lhs instanceof Integer && rhs instanceof Integer) {
                    int ilhs = ((Integer)lhs).intValue();
                    int irhs = ((Integer)rhs).intValue();
                    int result;
                    switch(tree.getKind()) {
                        default: return null;
                        case OR: result = ilhs|irhs; break;
                        case AND: result = ilhs&irhs; break;
                        case XOR: result = ilhs^irhs; break;
                        case LEFT_SHIFT: result = ilhs<<irhs; break;
                        case RIGHT_SHIFT: result = ilhs>>irhs; break;
                        case UNSIGNED_RIGHT_SHIFT: result = ilhs>>>irhs; break;
                        case PLUS: result = ilhs+irhs; break;
                        case MINUS: result = ilhs-irhs; break;
                        case MULTIPLY: result = ilhs*irhs; break;
                        case DIVIDE: result = ilhs/irhs; break;
                        case REMAINDER: result = ilhs%irhs; break;
                    }
                    return new Integer(result);
                }
            } else if(t instanceof UnaryTree) {
                Object arg = eval(((UnaryTree)t).getExpression());
                if(arg instanceof Integer) {
                    int result;
                    int iarg = ((Integer)arg).intValue();
                    switch(t.getKind()) {
                        case UNARY_PLUS: return arg;
                        case UNARY_MINUS: result = -iarg; break;
                        case LOGICAL_COMPLEMENT:
                        case BITWISE_COMPLEMENT: result = ~iarg; break;
                        default: return null;
                    }
                    return new Integer(result);
                } else if(arg instanceof Float) {
                    float result;
                    float farg = ((Float)arg).floatValue();
                    switch(t.getKind()) {
                        case UNARY_PLUS: return arg;
                        case UNARY_MINUS: result = -farg; break;
                        default: return null;
                    }
                    return new Float(result);
                } else if(arg instanceof Double) {
                    double result;
                    double darg = ((Double)arg).doubleValue();
                    switch(t.getKind()) {
                        case UNARY_PLUS: return arg;
                        case UNARY_MINUS: result = -darg; break;
                        default: return null;
                    }
                    return new Double(result);
                }
            } else if(t instanceof LiteralTree) return ((LiteralTree)t).getValue();
        } catch(Throwable err) {}
        return null;
    }
    
    @SuppressWarnings("unchecked") // doesn't like the Comparable cast for some reason
    private static int compare(Object a, Object b) {
        if(a==b) return 0;
        if(a==null) return -1;
        if(b==null) return 1;
        try {
            if(a.getClass() == b.getClass() && a instanceof Comparable) 
                return ((Comparable)a).compareTo(b);
            if(a instanceof Double || b instanceof Double
                    || a instanceof Float || b instanceof Float) {
                double ad = ((Number)a).doubleValue();
                double bd = ((Number)b).doubleValue();
                return ad<bd ? -1 : ad>bd ? 1 : 0;
            }
            long al = ((Long)a).longValue();
            long bl = ((Long)b).longValue();
            return al<bl ? -1 : al>bl ? 1 : 0;
        } catch(Throwable t) { return -2; }
    }
    
    /**
     * Create an if statement, or a simplified equivalent, from a condition 
     * expression, then and else statements.
     * 
     * @param cond the conditional expression
     * @param thenpart the statement if the condition is true
     * @param elsepart the statement if the condition is false, or null if
     *                 there is no else statement.
     * @return an if statement, or a simplified equivalent
     */
    public StatementTree If(ExpressionTree cond, StatementTree thenpart, StatementTree elsepart) {
        if(isEmpty(thenpart))
            if(isEmpty(elsepart))
                if(sideEffectFree(cond)) return make.EmptyStatement();
                else {if(thenpart==null) thenpart = make.EmptyStatement();} else { cond = not(cond); thenpart = elsepart; elsepart = null; }
        if(isTrue(cond)) return thenpart;
        if(isFalse(cond)) return elsepart;
        return make.If(cond,thenpart,elsepart);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> convert(Class<T> klass, List<?> list) {
        if (list == null)
            return null;
        for (Object o : list)
            klass.cast(o);
        return (List<T>)list;
    }
}
