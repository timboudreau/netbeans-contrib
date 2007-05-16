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

package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TreeVisitor;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.source.WorkingCopy;

/**
 *
 * @author Tim
 */
public class UsedLocalVariableVisitorTest extends BaseTestCase <Void, Set<ParamDesc>> {
    
    public UsedLocalVariableVisitorTest(String testName) {
        super(testName, "UsedLocalsOne");
    }

    public void testUsedLocals() throws Exception {
        System.err.println("Test Name " + super.getName());
    }

    @Override
    protected Tree getTreeToUse(CompilationUnitTree root) {
        return findTree (root, Kind.METHOD, "doSomething");        
    }

    @Override
    protected TreeVisitor <Void, Set<ParamDesc>> createVisitor(WorkingCopy copy) {
        UsedLocalVariableVisitor visitor = new UsedLocalVariableVisitor(copy, 
                20, 50);
        return visitor;
    }

    @Override
    protected Set<ParamDesc> createArgument() {
        return new HashSet <ParamDesc> ();
    }
}
