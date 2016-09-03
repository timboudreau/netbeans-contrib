/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 1997-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.lib.callgraph.javac;

import static org.netbeans.lib.callgraph.javac.UsageFinder.CLASS_TREE_KINDS;
import org.netbeans.lib.callgraph.util.EightBitStrings;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;

/**
 * Container for shared state that is gathered during the parse and discarded
 * after.
 *
 * @author Tim Boudreau
 */
public final class SourcesInfo implements AutoCloseable {

    public final Set<SourceElement> allElements = new HashSet<>();
    public final Map<Element, SourceElement> elements = new HashMap<>();

    private final Map<ClassTree, List<ClassTree>> inners = new HashMap<>();

    public final EightBitStrings strings;

    public SourcesInfo(boolean eightBitStringsDisabled, boolean aggressive) {
        this.strings = new EightBitStrings(eightBitStringsDisabled, aggressive);
    }

    // XXX rewrite to not hold references to ClassTree, just names,
    // to reduce memory consumption - and verify that that actually
    // makes a difference (not sure how much of javac's compile tree
    // can be gc'd while the compile is ongoing).  Use something similar
    // to NetBeans' ElementHandle.  That might also make it possible to
    // restart the compile if memory consumption gets too large, if we
    // retain enough bookkeeping here.
    TreePath containingClassOf(TreePath path) {
        do {
            path = path.getParentPath();
        } while (path != null && !CLASS_TREE_KINDS.contains(path.getLeaf().getKind()));
        return path;
    }

    String parametersOf(TreePath path) {
        if (path.getLeaf() instanceof MethodTree) {
            MethodTree method = (MethodTree) path.getLeaf();
            StringBuilder paramsString = new StringBuilder("(");
            for (Iterator<? extends VariableTree> it = method.getParameters().iterator(); it.hasNext();) {
                VariableTree variable = it.next();
                paramsString.append(variable.getType().toString());
                if (it.hasNext()) {
                    paramsString.append(',');
                }
            }
            return paramsString.append(")").toString();
        } else {
            return "";
        }
    }

    String packageNameOf(TreePath path) {
        ExpressionTree pkgName = path.getCompilationUnit().getPackageName();
        return pkgName == null ? "<defaultPackage>" : pkgName.toString();
    }

    String nameOf(TreePath tree) {
        TreePath containingClass = containingClassOf(tree);
        ClassTree clazz = (ClassTree) containingClass.getLeaf();
        String name = clazz.getSimpleName().toString();
        if (name.isEmpty()) {
            // Recursively generate $1, $2 names, handling the case of
            // mutiple nesting
            name = nameOf(containingClass);
            TreePath nestedIn = containingClassOf(containingClass);
            ClassTree nestingParent = (ClassTree) nestedIn.getLeaf();
            List<ClassTree> innerClasses = inners.get(nestingParent);
            if (innerClasses == null) {
                innerClasses = new ArrayList<>(3);
                inners.put(nestingParent, innerClasses);
            }
            innerClasses.add(clazz);
            name = name + ".$" + innerClasses.size();
        }
        return name;
    }

    @Override
    public void close() {
        // Clear references to objects from the parse
        inners.clear();
        elements.clear();
        strings.clear();
    }
}
