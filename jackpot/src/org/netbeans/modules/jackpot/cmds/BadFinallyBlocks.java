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

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.TryTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import org.netbeans.api.jackpot.TreePathQuery;

/**
 * Reports try statements which have finally blocks that have throws or
 * return statements, which is considered a poor practice.
 */
public class BadFinallyBlocks extends TreePathQuery<Void,Object> {
    
    /**
     * Check <code>finally</code> blocks for throws and return statements.  
     * A <code>finally</code> block doesn't have its own <code>Tree</code> 
     * type, so <code>TryTree</code> nodes are visited.
     * 
     * @param tree the <code>try</code> statement to inspect
     * @param p unused
     * @return null
     */
    @Override
    public Void visitTry(TryTree tree, Object p) {
	super.visitTry(tree, p);
	if (tree.getFinallyBlock() != null) {
	    TreePathScanner<Void,Object> scanner = new TreePathScanner<Void,Object>() {
                @Override
		public Void visitReturn(ReturnTree tree, Object p) {
		    addResult("return in finally block");
                    return null;
		}
                @Override
		public Void visitThrow(ThrowTree tree, Object p) {
		    addResult("throw in finally block");
                    return null;
		}

                /** 
                 * Don't scan class or new class declarations, as 
                 * any return or throw statements in them are valid.
                 */
                @Override
                public Void visitClass(ClassTree tree, Object p) {
                    return null;
                }
                @Override
                public Void visitNewClass(NewClassTree tree, Object p) {
                    return null;
                }
	    };
            TreePath scanPath = new TreePath(getCurrentPath(), tree.getFinallyBlock());
            scanner.scan(scanPath, p);
	}
        return null;
    }
}
