/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 2009 Sun
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
package org.netbeans.modules.java.editor.ext.fold;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.ClasspathInfo.PathKind;
import org.netbeans.api.java.source.CompilationInfo;
import org.openide.filesystems.FileObject;

/**
 *
 * @author lahvac
 */
public class Messages {

    private Messages() {}

    public static Call resolvePossibleMessageCall(CompilationInfo info, TreePath methodInvocation) {
        if (methodInvocation.getLeaf().getKind() != Kind.METHOD_INVOCATION) {
            throw new IllegalStateException();
        }

        MethodInvocationTree mit = (MethodInvocationTree) methodInvocation.getLeaf();
        Element invoked = info.getTrees().getElement(methodInvocation);

        if (invoked == null || invoked.getKind() != ElementKind.METHOD) {
            return null;
        }

        ExecutableElement method = (ExecutableElement) invoked;
        String methodName = fqn(method);

        if ("org.openide.util.NbBundle.getMessage".equals(methodName)) {
            FileObject bundle = null;
            ExpressionTree classSpec = mit.getArguments().get(0);
            TreePath clazz = new TreePath(methodInvocation, classSpec);
            TypeMirror type = info.getTrees().getTypeMirror(clazz);

            if (type.getKind() == TypeKind.DECLARED) {
                DeclaredType dt = (DeclaredType) type;
                Element bundleForElement = null;
                //TODO: check java.lang.Class

                if (dt.getTypeArguments().size() > 0) {
                    TypeMirror bundleFor = dt.getTypeArguments().get(0);

                    if (bundleFor.getKind() == TypeKind.DECLARED) {
                        bundleForElement = info.getTypes().asElement(bundleFor);
                    }
                } else {
                    //#169833:
                    if (classSpec.getKind() == Kind.MEMBER_SELECT && ((MemberSelectTree) classSpec).getIdentifier().contentEquals("class")) {
                        TreePath clazzSelect = new TreePath(clazz, ((MemberSelectTree) classSpec).getExpression());
                        Element e = info.getTrees().getElement(clazzSelect);

                        if (e != null && (e.getKind().isClass() || e.getKind().isInterface())) {
                            bundleForElement = e;
                        }
                    }
                }

                if (bundleForElement != null) {
                    String pack = info.getElements().getPackageOf(bundleForElement).getQualifiedName().toString();

                    bundle = info.getClasspathInfo().getClassPath(PathKind.SOURCE).findResource(pack.replace('.', '/') + "/Bundle.properties");
                }
            }

            String key = stringValue(info, new TreePath(methodInvocation, mit.getArguments().get(1)));

            if (bundle != null && key != null) {
                return new Call(bundle, key, new String[0]);
            }
        }

        return null;
    }

    private static Name methodName(ExpressionTree et) {
        switch (et.getKind()) {
            case IDENTIFIER: return ((IdentifierTree) et).getName();
            case MEMBER_SELECT: return ((MemberSelectTree) et).getIdentifier();
            default: return null;
        }
    }

    private static String fqn(ExecutableElement method) {
        //XXX: performance?
        return ((TypeElement) method.getEnclosingElement()).getQualifiedName().toString() + "." + method.getSimpleName().toString();
    }

    private static String stringValue(CompilationInfo info, TreePath c) {
        Concatenate s = new Concatenate(info);
        Boolean result = s.scan(c, null);

        if (result == null || !result) {
            return null;
        }

        return s.data.toString();
    }

    private static final class Concatenate extends TreePathScanner<Boolean, Void> {

        private final CompilationInfo info;
        private final StringBuilder data = new StringBuilder();

        public Concatenate(CompilationInfo info) {
            this.info = info;
        }

        @Override
        public Boolean visitLiteral(LiteralTree node, Void p) {
            if (node.getKind() == Kind.STRING_LITERAL) {
                data.append((String) node.getValue());
                return true;
            }
            
            return false;
        }

        @Override
        public Boolean visitBinary(BinaryTree node, Void p) {
            if (node.getKind() == Kind.PLUS) {
                return reduce(scan(node.getLeftOperand(), p), scan(node.getRightOperand(), p));
            }

            return false;
        }

        @Override
        public Boolean visitMemberSelect(MemberSelectTree node, Void p) {
            return resolveMemberSelectOrIdent();
        }

        @Override
        public Boolean visitIdentifier(IdentifierTree node, Void p) {
            return resolveMemberSelectOrIdent();
        }

        @Override
        public Boolean reduce(Boolean r1, Boolean r2) {
            if (r1 == null || r2 == null) {
                return false;
            }

            return r1 && r2;
        }

        private Boolean resolveMemberSelectOrIdent() {
            Element e = info.getTrees().getElement(getCurrentPath());

            if (e == null || e.getKind() != ElementKind.FIELD) {
                return false;
            }

            VariableElement ve = (VariableElement) e;

            if (!(ve.getConstantValue() instanceof String)) {
                return false;
            }

            data.append((String) ve.getConstantValue());
            
            return true;
        }

    }
    
    public static final class Call {
        public final FileObject bundle;
        public final String key;
        public final String[] parameters;

        public Call(FileObject bundle, String key, String[] parameters) {
            this.bundle = bundle;
            this.key = key;
            this.parameters = parameters;
        }
    }

}
