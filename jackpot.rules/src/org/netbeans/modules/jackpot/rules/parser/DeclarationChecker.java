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

import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

/**
 * Checks whether a specified variable was declared in a tree.
 * @author James Gosling, Tom Ball
 */
class DeclarationChecker extends TreeScanner<Void,Object> {
    String name;
    boolean declared;
    public boolean declaredIn(CharSequence n, Tree t) {
        name = n.toString();
        declared = false;
        scan(t, null);
        return declared;
    }
    @Override
    public Void scan(Tree tree, Object p) {
        if(!declared)
            super.scan(tree, p);
        return null;
    }
    @Override
    public Void visitVariable(VariableTree tree, Object p) {
        if(!checkTarget(tree)) 
            super.visitVariable(tree, p);
        return null;
    }
    public boolean checkTarget(VariableTree t) {
        if(t.getName().equals(name))
            declared = true;
        return declared;
    }
}
