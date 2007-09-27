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
