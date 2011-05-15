/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright © 2008-2011 Oracle and/or its affiliates. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.contrib.testng.output;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreePathScanner;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource.Phase;

/**
 *
 * @author lukas
 */
final class TestClassScanner implements CancellableTask<CompilationController> {

    private String className;
    private String methodName;
    private int offset;

    TestClassScanner(String className, String methodName) {
        assert className != null;
        this.className = className;
        this.methodName = methodName;
    }

    public void cancel() {
    }

    public void run(CompilationController controller) throws Exception {
        controller.toPhase(Phase.RESOLVED);
        ClassVisitor cv = methodName != null
                ? new ClassVisitor(controller, ClassVisitor.METHOD, methodName)
                : new ClassVisitor(controller, ClassVisitor.CLASS, className);
        offset = cv.scan(controller.getCompilationUnit(), className)[0];
    }

    int getOffset() {
        return offset;
    }

    private class ClassVisitor extends TreePathScanner<int[], String> {

        static final int CLASS = 0;
        static final int METHOD = 1;
        private CompilationInfo controller;
        private int type;
        String name;

        public ClassVisitor(CompilationInfo info, int type, String name) {
            this.controller = info;
            this.type = type;
            this.name = name;
        }

        @Override
        public int[] visitClass(ClassTree node, String p) {
            TypeElement el = (TypeElement) controller.getTrees().getElement(getCurrentPath());
            if (el != null) {
                if (el.getKind() == ElementKind.CLASS) {
                    if (methodName == null) {
                        if (name.equals(el.getSimpleName().toString())) {
                            ClassTree tree = controller.getTrees().getTree(el);
                            return controller.getTreeUtilities().findNameSpan(tree);
                        }
                    } else {
                        for (Element e : el.getEnclosedElements()) {
                            if (e.getKind() == ElementKind.METHOD) {
                                if (name.equals(e.getSimpleName().toString())) {

                                    MethodTree tree = controller.getTrees().getTree((ExecutableElement) e);
                                    return controller.getTreeUtilities().findNameSpan(tree);
                                }
                            }
                        }
                    }
                }
            }
            return new int[] { -1 };
        }
    }
}
