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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.rules.parser;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreeScanner;

/**
 * Checks whether a specified variable was assigned to in a tree.
 * @author James Gosling, Tom Ball
 */
class AssignChecker extends TreeScanner<Void,Object> {
    String name;
    boolean assigned;
    public boolean assignedIn(CharSequence n, Tree t) {
        name = n.toString();
        assigned = false;
        scan(t, null);
        return assigned;
    }
    @Override
    public Void scan(Tree tree, Object p) {
        if(!assigned)
            super.scan(tree, p);
        return null;
    }
    @Override
    public Void visitAssignment(AssignmentTree tree, Object p) {
        if(!checkTarget(tree.getVariable())) 
            super.visitAssignment(tree, p);
        return null;
    }
    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree tree, Object p) {
        if(!checkTarget(tree.getVariable())) 
            super.visitCompoundAssignment(tree, p);
        return null;
    }
    public boolean checkTarget(Tree t) {
        if(t instanceof IdentifierTree && 
           ((IdentifierTree)t).getName().toString().equals(name))
            assigned = true;
        return assigned;
    }
}    
