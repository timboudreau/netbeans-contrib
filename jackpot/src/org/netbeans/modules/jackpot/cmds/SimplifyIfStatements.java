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

import org.netbeans.api.java.source.transform.Transformer;
import com.sun.source.tree.*;

public class SimplifyIfStatements extends Transformer<Void,Object> {
    
    { queryDescription = "Complex if statements"; }
    
    @Override
    public Void visitIf(IfTree tree, Object p) {
        super.visitIf(tree, p);
        StatementTree newTree = tree;
        if(sideEffectFree(tree.getCondition())) {
            Object bool = eval(tree.getCondition());
            if(bool == Boolean.TRUE) {
                newTree = tree.getThenStatement();
                addResult(newTree, "Eliminated 'if(true)'");
                changes.rewrite(tree, newTree);
                return null;
            }
            if(bool == Boolean.FALSE) {
                newTree = tree.getElseStatement();
                addResult(newTree, "Eliminated 'if(false)'");
                changes.rewrite(tree, newTree);
                return null;
            }
        }
        String resultMsg = null;
        if(tree.getElseStatement()!=null && nonFlowExit(tree.getThenStatement())) {
            newTree = If(tree.getCondition(),tree.getThenStatement(),null);
            newTree = block(newTree,tree.getElseStatement());
            resultMsg = "Flattened 'if()nonflow'";
        } else if(tree.getThenStatement()==null) {
            if(tree.getElseStatement()==null && sideEffectFree(tree.getCondition()))
                newTree=tree;
            else
                newTree = If(not(tree.getCondition()),tree.getElseStatement());
        } else if(nonFlowExit(tree.getElseStatement())) {
            newTree = If(not(tree.getCondition()),tree.getElseStatement(),null);
            newTree = block(newTree,tree.getThenStatement());
            resultMsg = "Flattened 'if()... else nonflow'";
        }
        if (newTree != tree) {
            changes.rewrite(tree, newTree);
            addResult(newTree, resultMsg);
        }
        return null;
    }
    
    private boolean nonFlowExit(Tree t) {
        return (SimplifyLoops.howExits(t) & SimplifyLoops.FLOWEXIT) == 0;
    }
}
