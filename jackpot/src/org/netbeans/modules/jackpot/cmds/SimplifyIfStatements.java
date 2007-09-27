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
