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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.hints.errors.Utilities;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.modules.java.hints.spi.ErrorRule.Data;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class TypeInForEachLoop implements ErrorRule<Void> {

    private static final Set<String> CODES = new HashSet<String>(Arrays.asList("compiler.err.expected"));
    private static final String ERROR = "<error>";

    public Set<String> getCodes() {
        return CODES;
    }

    public List<Fix> run(CompilationInfo info, String diagnosticKey, int offset, TreePath treePath, Data<Void> data) {
        TypeElement iterableElement = info.getElements().getTypeElement("java.lang.Iterable");

        if (iterableElement == null) {
            return null;
        }
        
        if (treePath.getLeaf().getKind() != Kind.VARIABLE) {
            return null;
        }

        TreePath variable = treePath;
        
        TreePath identifier = new TreePath(variable, ((VariableTree) variable.getLeaf()).getType());

        if (identifier.getLeaf().getKind() != Kind.IDENTIFIER) {
            return null;
        }

        treePath = treePath.getParentPath();

        if (treePath.getLeaf().getKind() != Kind.ENHANCED_FOR_LOOP) {
            return null;
        }

        VariableTree vt = (VariableTree) variable.getLeaf();

        if (!ERROR.equals(vt.getName().toString())) {
            return null;
        }

        TypeMirror tm = info.getTrees().getTypeMirror(identifier);

        if (tm == null || tm.getKind() == TypeKind.DECLARED) {
            return null;
        }

        TreePath iterable = new TreePath(treePath, ((EnhancedForLoopTree) treePath.getLeaf()).getExpression());
        TypeMirror iterableType = info.getTrees().getTypeMirror(iterable);

        if (iterableType == null) {
            return null;
        }

        TypeMirror designedType = null;
        
        if (iterableType.getKind() == TypeKind.DECLARED) {
            DeclaredType iterableDeclaredType = (DeclaredType) iterableType;

            if (!info.getTypes().isSubtype(info.getTypes().erasure(iterableDeclaredType), info.getTypes().erasure(iterableElement.asType()))) {
                return null;
            }

            ExecutableElement iteratorMethod = (ExecutableElement) iterableElement.getEnclosedElements().get(0); //XXX
            ExecutableType iteratorMethodType = (ExecutableType) info.getTypes().asMemberOf(iterableDeclaredType, iteratorMethod);

            designedType = ((DeclaredType) iteratorMethodType.getReturnType()).getTypeArguments().get(0); //XXX: assuming the method are correct...
        }

        if (iterableType.getKind() == TypeKind.ARRAY) {
            designedType = ((ArrayType) iterableType).getComponentType();
        }

        if (designedType == null) {
            return null;
        }

        String name = ((IdentifierTree) identifier.getLeaf()).getName().toString();
        TreePathHandle forHandle = TreePathHandle.create(treePath, info);
        
        designedType = Utilities.resolveCapturedType(info, designedType);
        
        TypeMirrorHandle desginedTypeHandle = TypeMirrorHandle.create(designedType);

        return Collections.<Fix>singletonList(new FixImpl(name, forHandle, desginedTypeHandle));
    }

    public String getId() {
        return TypeInForEachLoop.class.getName();
    }

    public String getDisplayName() {
        return "TypeInForEachLoop";
    }

    public void cancel() {
    }

    private static final class FixImpl implements Fix {

        private final String name;
        private final TreePathHandle forPath;
        private final TypeMirrorHandle type;

        public FixImpl(String name, TreePathHandle forPath, TypeMirrorHandle type) {
            this.name = name;
            this.forPath = forPath;
            this.type = type;
        }

        public String getText() {
            return "Create variable " + name;
        }

        public ChangeInfo implement() throws Exception {
            FileObject file = forPath.getFileObject();
            JavaSource js   = JavaSource.forFileObject(file);

            js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy wc) throws Exception {
                    wc.toPhase(Phase.RESOLVED);

                    TreePath forPath = FixImpl.this.forPath.resolve(wc);
                    TypeMirror designedType = FixImpl.this.type.resolve(wc);

                    if (forPath == null || designedType == null) {
                        //XXX: log
                        return ;
                    }

                    TreePath variable = new TreePath(forPath, ((EnhancedForLoopTree) forPath.getLeaf()).getVariable());
                    VariableTree origVariable = (VariableTree) variable.getLeaf();
                    Tree type = wc.getTreeMaker().Type(designedType);
                    Tree newVariable = wc.getTreeMaker().Variable(origVariable.getModifiers(), name, type, null);

                    wc.rewrite(origVariable, newVariable);
                }
            }).commit();

            return null;
        }
        
    }
}
