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
        System.out.println("testUsedLocals");
//        System.out.println(argument);
        Set <String> names = new HashSet <String> ();
        for (ParamDesc d : argument) {
            String nm = d.getName().toString();
            names.add (nm);
        }
        assertTrue (names.contains("y"));        
        assertTrue (names.contains("s"));
        assertFalse (names.contains("q"));
        assertFalse (names.contains("x"));        
        
        UsedLocalVariableVisitor v = (UsedLocalVariableVisitor) super.visitor;
        Set <String> locals = v.getLocallyAssigned();
        assertTrue (locals.contains("y"));        
        assertTrue (locals.contains("s"));        
        assertFalse (locals.contains("q"));        
        assertEquals (2, locals.size());
    }

    @Override
    protected Tree getTreeToUse(CompilationUnitTree root) {
        Tree result = findTree (root, Kind.METHOD, "doSomething");        
        return result;
    }

    @Override
    protected TreeVisitor <Void, Set<ParamDesc>> createVisitor(WorkingCopy copy) {
        int start = indexInFile("marker1");        
        int end = indexInFile("marker2");        
        if ("testUsedLocals".equals(getName())) {
            UsedLocalVariableVisitor visitor = new UsedLocalVariableVisitor(copy, 
                    start, end);
            return visitor;
        } else {
            return null; //XXX
        }
    }

    @Override
    protected Set<ParamDesc> createArgument() {
        return new HashSet <ParamDesc> ();
    }
}
