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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext.ScanContext;

/**
 *
 * @author Tim Boudreau
 */
public class FindUnqualifiedMethodSelectsTest extends VisitorBaseTestCase<Void, ParameterChangeContext> {

    public FindUnqualifiedMethodSelectsTest(String name) {
        super (name, "Unqualifieds");
        super.pkgname = "foo.bar";
    }

    public void testSomething() {
        System.out.println("testSomething");
        String data = ctx.changeData.toString(copy, rpc);
        System.err.println(data);
        Set <ExecutableElement> elements = getAllExecutableElements (copy.getCompilationUnit(),
                copy.getTrees());

        System.err.println("Found elements " + elements);

        for (ExecutableElement el : elements) {
            Set <TreePathHandle> requalifies = ctx.changeData.getMemberSelectsThatNeedRequalifying(el, copy);
            if (requalifies != null && !requalifies.isEmpty()) {
            System.err.println("For " + el + ":");
            System.err.println("  requalifies: " + requalifies);
                for (TreePathHandle handle : requalifies) {
                    String newName = ctx.changeData.getQualifierFor(el, handle, copy);
                    TreePath path = handle.resolve (copy);
                    Tree leaf = path.getLeaf();
                    System.err.println(leaf + " -> " + newName);
        }
    }
        }
    }

    @Override
    protected void beforeScan(WorkingCopy copy) {
        sc.setCompilationInfo(copy);
    }

    @Override
    protected void setUp () throws Exception {
        sc = new SC();
        super.setUp();
    }

    RequestedParameterChanges rpc;
    SC sc;
    ParameterChangeContext ctx;
    protected TreeVisitor<Void, ParameterChangeContext> createVisitor(WorkingCopy copy) {
        UnqualifiedMemberScanner result = new UnqualifiedMemberScanner ();
        return result;
    }

    private static Set <ExecutableElement> getAllExecutableElements(CompilationUnitTree unit, Trees trees) {
        List <? extends Tree> types = unit.getTypeDecls();
        Set <ExecutableElement> elements = new HashSet <ExecutableElement> ();
        for (Tree tree : types) {
            TreePath path = TreePath.getPath(unit, tree);
            if (tree instanceof ClassTree) {
                TypeElement tel = (TypeElement) trees.getElement(path);
                getAllExes (tel, elements);
            }
        }
        return elements;
    }

    private static void getAllExes (TypeElement tel, Set <ExecutableElement> elements) {
        List <? extends Element> encs = tel.getEnclosedElements();
        for (Element e : encs) {
            if (e instanceof ExecutableElement) {
                elements.add ((ExecutableElement)e);
            } else if (e instanceof TypeElement) {
                getAllExes((TypeElement) e, elements);
            }
        }
    }

    protected ParameterChangeContext createArgument() {
        Collection <String> newOrChangedParameterNames = cs (FIELDS);
        Collection <String> newParameterNames = cs ("q");
        List <String> origParamsInOrder = cs ("a", "b", "c");
        ParameterRenamePolicy policy = ParameterRenamePolicy.RENAME_UNLESS_CONFLICT;
        rpc = new RequestedParameterChanges (newParameterNames, newOrChangedParameterNames, origParamsInOrder, policy);
        ctx = new ParameterChangeContext (rpc, sc);
        return ctx;
    }

    private static List <String> cs (String... s) {
        return new ArrayList <String> (Arrays.<String>asList(s));
    }


    static String[] FIELDS = new String[] {
        "ownedByInnerInnerClassOfInnerStaticsNonStaticSubclass",
        "ownedByInnerStaticsNonStaticSubclass",
        "ownedByInnerStaticClass2",
        "ownedByInnerStaticClass1",
        "ownedByInnerInner",
        "ownedByInner",
        "staticStringOwnedByTopLevel",
        "ownedByTopLevel",
        "MULTIPLE_INTERVAL_SELECTION",
        "SINGLE_INTERVAL_SELECTION",
    };

    private class SC implements ScanContext {
        public int getParameterIndex() {
            return -1;
        }

        public ExecutableElement getCurrentMethodElement() {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        public CompilationUnitTree getCompilationUnit() {
            return info.getCompilationUnit();
        }

        public CompilationInfo getCompilationInfo() {
            return info;
        }

        CompilationInfo info;
        public void setCompilationInfo(CompilationInfo info) {
            this.info = info;
        }
    }
}
