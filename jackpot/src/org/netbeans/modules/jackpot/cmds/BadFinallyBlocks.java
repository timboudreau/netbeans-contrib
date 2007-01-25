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

import org.netbeans.api.java.source.query.NodeScanner;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.TryTree;
import org.netbeans.api.java.source.query.Query;
import org.netbeans.api.java.source.query.NodeScanner;

/**
 * Reports try statements which have finally blocks that have throws or
 * return statements, which is considered wrong.
 */
public class BadFinallyBlocks extends Query<Void,Object> {
    
    @Override
    public Void visitTry(TryTree tree, Object p) {
	super.visitTry(tree, p);
	if (tree.getFinallyBlock() != null) {
	    NodeScanner<Void,Object> scanner = new NodeScanner<Void,Object>() {
                @Override
		public Void visitReturn(ReturnTree tree, Object p) {
		    addResult(currentSym, tree, "return in finally block");
                    return null;
		}
                @Override
		public Void visitThrow(ThrowTree tree, Object p) {
		    addResult(currentSym, tree, "throw in finally block");
                    return null;
		}

                /* Issue 82412: don't scan class or new class declarations, as 
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
            scanner.attach(env);
	    scanner.setCurrentElement(currentSym);
	    tree.getFinallyBlock().accept(scanner, p);
            scanner.release();
	}
        return null;
    }
}
