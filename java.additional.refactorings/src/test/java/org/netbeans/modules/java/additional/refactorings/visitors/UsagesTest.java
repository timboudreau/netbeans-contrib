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

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import org.netbeans.api.java.source.ClassIndex.SearchKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.Utils;
import org.netbeans.modules.java.source.usages.RepositoryUpdater;

/**
 *
 * @author Tim Boudreau
 */
public class UsagesTest extends VisitorBaseTestCase  <MethodTree, Info> {

    public UsagesTest(String name) {
        super (name, "Usages");
        super.pkgname = "foo.bar";        
    }

    protected TreeVisitor<MethodTree, Info> createVisitor(WorkingCopy copy) {
        return new MethodFinder ();
    }

    protected Info createArgument() {
        return new Info ("Usages", "foo");
    }

    public void testUsages() throws Exception {
        System.out.println("testUsages");
        scanResult = argument.result;
        System.out.println("Scan Result: " + scanResult);
        //Sanity check
        assertNotNull (scanResult);
        assertEquals ("public abstract void foo(int a, int b, String c);", scanResult.toString().trim());

        RepositoryUpdater.getDefault().scheduleCompilationAndWait(srcRoot, srcRoot).await();
        
        final ClasspathInfo cpi = ClasspathInfo.create(srcRoot);
        final ClassIndex index = cpi.getClassIndex();
        Set <ElementHandle<TypeElement>> s = index.getDeclaredTypes("U*", NameKind.REGEXP, 
                EnumSet.of(SearchScope.SOURCE));
        
        System.err.println("ALL KNOWN SRC TYPES " + s);
        Set <String> pkgNames = index.getPackageNames("foo", false, EnumSet.of(ClassIndex.SearchScope.DEPENDENCIES, ClassIndex.SearchScope.SOURCE));
        System.err.println("Package Names: " + pkgNames);
        System.err.println("CPI: " + cpi);
        
        ExecutableElement el = (ExecutableElement) copy.getTrees().getElement(pathToMethod);
        Collection <ElementHandle<ExecutableElement>> c = Utils.getOverridingMethods(el, copy);
        System.err.println("FOUND " + c);
        ElementHandle handle = ElementHandle.create(el);
        ElementHandle classHandle = ElementHandle.create (copy.getElementUtilities().enclosingTypeElement(el));
        Set <ElementHandle<TypeElement>> els = index.getElements(classHandle,
                EnumSet.of(SearchKind.IMPLEMENTORS), EnumSet.of(SearchScope.SOURCE));
        System.err.println("ELEMENTS: " + els);
        assertEquals (2, els.size());
        assertEquals (1, pkgNames.size());
        assertTrue (pkgNames.contains("foo.bar"));        
    }

    protected TreePath pathToMethod;
    private class MethodFinder extends TreePathScanner <MethodTree, Info> {
        @Override
        public MethodTree visitMethod(MethodTree tree, Info info) {
            if (match(tree, super.getCurrentPath())) {
                System.err.println("FOUND IT: " + tree);
                argument.result = tree;
                pathToMethod = super.getCurrentPath();
                return tree;
            } else {
                return super.visitMethod(tree, info);
            }
        }

        public boolean match (MethodTree tree, TreePath path) {
            String nm = tree.getName().toString();
            boolean result = argument.method.equals (nm);
            if (result) {
                Element el = copy.getTrees().getElement(path);
                TypeElement type = copy.getElementUtilities().enclosingTypeElement(el);
                String clname = type.getSimpleName().toString();
                result = argument.clazz.equals(clname);
            }
            return result;
        }
    }
}
