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
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

/**
 * Finds all methods.
 * 
 * @author Tim Boudreau
 */
final class ElementFinder extends TreeScanner<SourceElement, SourcesInfo> {

    private final CompilationUnitTree cc;
    private final Trees trees;
    private SourceElement last;
    private final boolean ignoreShallow;
    private final boolean ignoreAnonymous;
    private final boolean ignoreAbstract;

    public ElementFinder(CompilationUnitTree cc, Trees trees, boolean ignoreShallow, boolean ignoreAbstract, boolean ignoreAnonymous) {
        this.cc = cc;
        this.trees = trees;
        this.ignoreShallow = ignoreShallow;
        this.ignoreAbstract = ignoreAbstract;
        this.ignoreAnonymous = ignoreAnonymous;
    }

    @Override
    public SourceElement visitMethod(MethodTree tree, SourcesInfo info) {
        String nm = tree.getName().toString();
        SourceElement last = addItem(tree, info, SourceElementKind.METHOD, nm);
        super.visitMethod(tree, info);
        return this.last = last;
    }

    // uncomment to also deal in fields
    
//    @Override
//    public Void visitVariable(VariableTree tree, Set<SceneElement> set) {
//        String nm = tree.getName().toString();
//        addItem(tree, set, SceneObjectKind.FIELD, nm);
//        return super.visitVariable(tree, set);
//    }
//    private static final Pattern ANONYMOUS = Pattern.compile(".*\\$\\d+$");
    private SourceElement addItem(Tree tree, SourcesInfo info, SourceElementKind kind, String nm) {
        TreePath path = TreePath.getPath(cc, tree);
        Element el = trees.getElement(path);
        if (el != null && (el.getKind() == ElementKind.FIELD || el.getKind() == ElementKind.METHOD)) {
            boolean abstrakt = el.getKind() == ElementKind.METHOD 
                    && el.getModifiers().contains(Modifier.ABSTRACT);
            if (ignoreAbstract && abstrakt) {
                return null;
            }
            TypeMirror mirror = el.asType();
            
//            if (ignoreAnonymous && mirror != null) {
//                System.out.println(mirror.toString());
//                Matcher m = ANONYMOUS.matcher(mirror.toString());
//                if (m.find()) {
//                    System.out.println("IGNORE " + mirror.toString());
//                    return null;
//                }
//            }
            
            String typeStr = mirror != null ? mirror.toString() : "void";
            if (ignoreShallow && typeStr.indexOf('.') == typeStr.lastIndexOf('.')) {
                return null;
            }
            SourceElement nue = new SourceElement(kind, path, nm, typeStr, info, abstrakt);
            info.allElements.add(nue);
            info.elements.put(el, nue);
            return nue;
        }
        return null;
    }
}
