/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.cmds;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import javax.lang.model.element.Element;
import org.netbeans.api.jackpot.TreePathQuery;
import org.netbeans.api.java.source.CompilationInfo;
import org.openide.util.NbBundle;


/**
 * Report statement which assign a variable to itself.  For example,
 * <pre><code>
 *   public SomeConstructor(String msg) {
 *      this.message = message;
 *   }
 * </code></pre>
 * The above compiles but is obviously a mistake; either <code>msg</code>
 * should be changed to <code>message</code> to follow the pattern where 
 * constructor parameters have the same names as the instance variables they
 * initialize, change the assignment to <code>this.message = msg;</code>, or
 * delete the assignment statement since it does nothing.
 */
public class FindSelfAssignments extends TreePathQuery<Void,Object> {
    private Trees trees;

    @Override
    public void attach(CompilationInfo info) {
        super.attach(info);
        trees = info.getTrees();
    }
    
    /**
     * Check self-assignments, which generally are mistakes.
     * @param tree the assignment tree
     * @param p unused
     * @return null
     */
    @Override
    public Void visitAssignment(AssignmentTree tree, Object p) {
        boolean match = false;
        Tree lhs = tree.getVariable();
        TreePath lhsPath = new TreePath(getCurrentPath(), lhs);
        Element lhsElement = trees.getElement(lhsPath);
        if (lhsElement != null) { // true for method invocations and array elements
            Tree rhs = tree.getExpression();
            TreePath rhsPath = new TreePath(getCurrentPath(), rhs);
            Element rhsElement = trees.getElement(new TreePath(getCurrentPath(), rhs));
            if (lhsElement == rhsElement) {
                if (lhs instanceof MemberSelectTree && rhs instanceof MemberSelectTree) {
                    // check for foo.mumble == bar.mumble
                    TreePath expressionPath = new TreePath(lhsPath, ((MemberSelectTree)lhs).getExpression());
                    lhsElement = trees.getElement(expressionPath);
                    expressionPath = new TreePath(rhsPath, ((MemberSelectTree)rhs).getExpression());
                    rhsElement = trees.getElement(expressionPath);
                    match = lhsElement == rhsElement;
                }
                else if (lhs instanceof MemberSelectTree && rhs instanceof IdentifierTree)
                    // check for this.mumble == mumble
                    match = matchExprToThis((MemberSelectTree)lhs);
                else if (rhs instanceof MemberSelectTree && lhs instanceof IdentifierTree)
                    // check for mumble == this.mumble;
                    match = matchExprToThis((MemberSelectTree)rhs);
                else
                    match = true;
            }
        }
        if (match) {
            String msg = NbBundle.getMessage(FindSelfAssignments.class, "FindSelfAssignments.found", lhs.toString());
            addResult(msg);
        }
        return super.visitAssignment(tree, p);
    }

    /**
     * Returns true if the expression part of a select tree is "this", such as
     * "this.mumble".
     */
    private boolean matchExprToThis(final MemberSelectTree t) {
        ExpressionTree expr = t.getExpression();
        if (expr instanceof MemberSelectTree)
            return "this".equals(((MemberSelectTree)expr).getIdentifier().toString());
        else if (expr instanceof IdentifierTree)
            return "this".equals(((IdentifierTree)expr).getName().toString());
        return false;
    }
}
