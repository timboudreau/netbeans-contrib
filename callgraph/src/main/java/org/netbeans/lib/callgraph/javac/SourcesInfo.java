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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.lang.model.element.Element;
import org.netbeans.lib.callgraph.util.ComparableCharSequence;

/**
 * Container for shared state that is gathered during the parse and discarded
 * after.
 *
 * @author Tim Boudreau
 */
public final class SourcesInfo implements AutoCloseable {

    public final Set<SourceElement> allElements = new ConcurrentSkipListSet<>();
    public final Map<Element, SourceElement> elements = new ConcurrentHashMap<>();

    private final Map<ClassTree, List<ClassTree>> inners = new ConcurrentHashMap<>();

    public final EightBitStrings strings;
    private final ComparableCharSequence DOLLARS_DOT;
    private final ComparableCharSequence DEFAULT_PACKAGE;
    private final ComparableCharSequence OPEN_PARENS;
    private final ComparableCharSequence COMMA;
    private final ComparableCharSequence CLOSE_PARENS;
    private final ComparableCharSequence OPEN_CLOSE_PARENS;

    public SourcesInfo(boolean eightBitStringsDisabled, boolean aggressive) {
        this.strings = new EightBitStrings(eightBitStringsDisabled, aggressive);
        DOLLARS_DOT = strings.create("$");
        DEFAULT_PACKAGE = strings.create("<defaultPackage>");
        OPEN_PARENS = strings.create("(");
        CLOSE_PARENS = strings.create(")");
        OPEN_CLOSE_PARENS = strings.create("()");
        COMMA = strings.create(",");
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
    
    int edgeCount() {
        int ct = 0;
        for (SourceElement e : allElements) {
            ct += e.getInboundReferences().size() + e.getOutboundReferences().size();
        }
        return ct;
    }

    ComparableCharSequence parametersOf(TreePath path) {
        if (path.getLeaf() instanceof MethodTree) {
            MethodTree method = (MethodTree) path.getLeaf();
            List<? extends VariableTree> params = method.getParameters();
            if (params.isEmpty()) {
                return OPEN_CLOSE_PARENS;
            }
            List<CharSequence> l = new ArrayList<>(((params.size() -1) * 2) + 2);
            l.add(OPEN_PARENS);
            for (Iterator<? extends VariableTree> it = params.iterator(); it.hasNext();) {
                VariableTree variable = it.next();
                l.add(strings.create(variable.getType().toString()));
                if (it.hasNext()) {
                    l.add(COMMA);
                }
            }
            l.add(CLOSE_PARENS);
            return strings.concat(l.toArray(new CharSequence[l.size()]));
        } else {
            return ComparableCharSequence.EMPTY;
        }
    }

    ComparableCharSequence packageNameOf(TreePath path) {
        ExpressionTree pkgName = path.getCompilationUnit().getPackageName();
        return pkgName == null ? DEFAULT_PACKAGE : strings.create(pkgName.toString());
    }

    ComparableCharSequence nameOf(TreePath tree) {
        TreePath containingClass = containingClassOf(tree);
        ClassTree clazz = (ClassTree) containingClass.getLeaf();
        ComparableCharSequence name = strings.create(clazz.getSimpleName());
        if (name.length() == 0) {
            // Recursively generate $1, $2 names, handling the case of
            // mutiple nesting
            name = nameOf(containingClass);
            TreePath nestedIn = containingClassOf(containingClass);
            ClassTree nestingParent = (ClassTree) nestedIn.getLeaf();
            List<ClassTree> innerClasses = inners.get(nestingParent);
            if (innerClasses == null) {
                innerClasses = new LinkedList<>();
                inners.put(nestingParent, innerClasses);
            }
            innerClasses.add(clazz);
            return strings.concat(name, DOLLARS_DOT, Integer.toString(innerClasses.size()));
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
