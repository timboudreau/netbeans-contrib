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

import com.sun.source.tree.IfTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import org.netbeans.api.jackpot.ConversionOperations;
import org.netbeans.api.jackpot.QueryOperations;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CompilationInfo;

/**
 * Simplify if statements for the following cases:
 * <ul>
 * <li>for "<code>if(true)</code>" statements (or their equivalents), replace the if
 * statement with its then statement.</li>
 * <li>for "<code>if(false)</code>" statements (or their equivalents), replace the if
 * statement with its else statement.</li>
 * <li>for if statements where the then statement doesn't continue to the
 * following statement (such as "<code>if (cond) return;</code> or 
 * <code>if (cond) { doSomething(); break; }</code>)", move the else statement to
 * follow the if statement.</li>
 * <li>for if statements where the else statement doesn't continue to the following
 * statement, invert the condition, switch the then and else statements, and then
 * move the else statement to follow the if statement.</li>
 * 
 * @author James Gosling, Tom Ball
 */
public class SimplifyIfStatements extends TreePathTransformer<Void,Object> {
    
    private ConversionOperations ops;
    
    @Override
    public void attach(CompilationInfo info) {
	super.attach(info);
        ops = new ConversionOperations(getWorkingCopy());
    }
    
    /**
     * Test if statements for simplication.
     * 
     * @param tree the if statement to inspect
     * @param p unused
     * @return null
     */
    @Override
    public Void visitIf(IfTree tree, Object p) {
        super.visitIf(tree, p);
        StatementTree newTree = tree;
        if(QueryOperations.sideEffectFree(tree.getCondition())) {
            Object bool = ops.eval(tree.getCondition());
            if(bool == Boolean.TRUE) {
                newTree = tree.getThenStatement();
                addChange(getCurrentPath(), newTree, "Eliminated 'if(true)'");
                return null;
            }
            if(bool == Boolean.FALSE) {
                newTree = tree.getElseStatement();
                addChange(getCurrentPath(), newTree, "Eliminated 'if(false)'");
                return null;
            }
        }
        String resultMsg = null;
        if(tree.getElseStatement()!=null && nonFlowExit(tree.getThenStatement())) {
            newTree = ops.If(tree.getCondition(),tree.getThenStatement(),null);
            newTree = ops.block(newTree,tree.getElseStatement());
            resultMsg = "Flattened 'if()nonflow'";
        } else if(tree.getThenStatement()==null) {
            if(tree.getElseStatement()==null && ops.sideEffectFree(tree.getCondition()))
                newTree=tree;
            else
                newTree = ops.If(ops.not(tree.getCondition()),tree.getElseStatement(),null);
        } else if(nonFlowExit(tree.getElseStatement())) {
            newTree = ops.If(ops.not(tree.getCondition()),tree.getElseStatement(),null);
            newTree = ops.block(newTree,tree.getThenStatement());
            resultMsg = "Flattened 'if()... else nonflow'";
        }
        if (newTree != tree) {
            addChange(getCurrentPath(), newTree, resultMsg);
        }
        return null;
    }
    
    private boolean nonFlowExit(Tree t) {
        return (SimplifyLoops.howExits(t) & SimplifyLoops.FLOWEXIT) == 0;
    }
}
