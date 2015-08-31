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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * Finds usages.
 *
 * @author Tim Boudreau
 */
final class UsageFinder extends TreeScanner<Void, SourcesInfo> {

    private final CompilationUnitTree cc;
    private final Trees trees;
    public static final Set<Tree.Kind> CLASS_TREE_KINDS = EnumSet.of(
            Tree.Kind.ANNOTATION_TYPE,
            Tree.Kind.CLASS,
            Tree.Kind.ENUM,
            Tree.Kind.INTERFACE);

    public UsageFinder(CompilationUnitTree cc, Trees trees) {
        this.cc = cc;
        this.trees = trees;
    }

    @Override
    public Void visitIdentifier(IdentifierTree tree, SourcesInfo data) {
        addElement(tree, data);
        return super.visitIdentifier(tree, data);
    }

    @Override
    public Void visitVariable(VariableTree tree, SourcesInfo data) {
        addElement(tree, data);
        return super.visitVariable(tree, data);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree tree, SourcesInfo data) {
        addElement(tree, data);
        return super.visitMemberSelect(tree, data);
    }

    private void addElement(Tree tree, SourcesInfo map) {
        TreePath path = TreePath.getPath(cc, tree);
        Element el = trees.getElement(path);
        if (el == null || el.getKind() == ElementKind.PARAMETER) {
            return;
        }
        Element selectedElement = el;
        do {
            path = path.getParentPath();
        } while (path != null && path.getLeaf().getKind() != com.sun.source.tree.Tree.Kind.METHOD && !CLASS_TREE_KINDS.contains(path.getLeaf().getKind()));
        if (path != null && path.getLeaf().getKind() == com.sun.source.tree.Tree.Kind.METHOD) {
            Element enclosingElement = trees.getElement(path);
            SourceElement enclosing = map.elements.get(enclosingElement);
            SourceElement selected = map.elements.get(selectedElement);
            if (enclosing != null && selected != null) {
                enclosing.addOutboundReference(selected);
                selected.addInboundReference(enclosing);
            }
        }
    }
}
