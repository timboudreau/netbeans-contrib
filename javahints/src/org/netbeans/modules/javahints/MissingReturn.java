/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javahints;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.java.source.*;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.modules.java.hints.errors.ChangeMethodReturnType;
import org.netbeans.modules.java.hints.errors.Utilities;
import org.netbeans.modules.java.hints.jackpot.spi.JavaFix;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author lahvac
 */
public class MissingReturn implements ErrorRule<Void> {

    private final static Set<String> CODES = new HashSet<String>(Arrays.asList(
            "compiler.err.missing.ret.stmt"
    ));

    @Override
    public Set<String> getCodes() {
        return CODES;
    }

    @Override
    public List<Fix> run(CompilationInfo info, String diagnosticKey, int offset, TreePath treePath, ErrorRule.Data<Void> data) {
        List<Fix> result = new ArrayList<Fix>();

        treePath = info.getTreeUtilities().pathFor(offset - 1);

        TreePath method = treePath.getParentPath();
        MethodTree mt = (MethodTree) method.getLeaf();
        TypeMirror type = info.getTrees().getTypeMirror(new TreePath(method, mt.getReturnType()));

        result.add(createChangeToTypeFix(info, treePath.getParentPath(), info.getTypes().getNoType(TypeKind.VOID)));

        if (type != null && treePath.getLeaf().getKind() == Kind.BLOCK) {
            result.add(new AddReturnFixImpl(info, treePath, type.getKind()));
        }

        return result;
    }

    @Override
    public String getId() {
        return MissingReturn.class.getName();
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(MissingReturn.class, "DN_MissingReturn");
    }

    @Override
    public void cancel() {}

    private static final class AddReturnFixImpl implements Fix {

        private final TreePathHandle targetBlock;
        private final TypeKind targetKind;

        public AddReturnFixImpl(CompilationInfo info, TreePath block, TypeKind kind) {
            this.targetBlock = TreePathHandle.create(block, info);
            this.targetKind = kind;
        }

        @Override
        public String getText() {
            return NbBundle.getMessage(MissingReturn.class, "FIX_AddReturn");
        }

        public ChangeInfo implement() throws Exception {
            FileObject file = targetBlock.getFileObject();
            JavaSource js = JavaSource.forFileObject(file);

            ModificationResult mr = js.runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy wc) throws Exception {
                    wc.toPhase(Phase.RESOLVED);

                    TreePath block = targetBlock.resolve(wc);

                    if (block == null) {
                        return ; //TODO: log
                    }
                    
                    //TODO:copied from NotInitializedVariable!
                    Object value;
                    if (targetKind.isPrimitive()) {
                        if (targetKind == TypeKind.BOOLEAN) {
                            value = false;
                        }
                        else {
                            value = 0;
                        }
                    }
                    else {
                        value = null;
                    }

                    TreeMaker make = wc.getTreeMaker();
                    LiteralTree returnValue = make.Literal(value);
                    BlockTree blockTree = (BlockTree) block.getLeaf();

                    wc.tag(returnValue, Utilities.TAG_SELECT);
                    wc.rewrite(blockTree, make.addBlockStatement(blockTree, make.Return(returnValue)));
                }
            });

            return Utilities.commitAndComputeChangeInfo(file, mr);
        }

    }

    public static Fix createChangeToTypeFix(CompilationInfo info, TreePath method, @NonNull TypeMirror newType) {
        return JavaFix.toEditorFix(new FixImpl(info, method, TypeMirrorHandle.create(newType), info.getTypeUtilities().getTypeName(newType).toString()));
    }

    private static final class FixImpl extends JavaFix {

        private final TypeMirrorHandle targetTypeHandle;
        private final String targetTypeDN;

        public FixImpl(CompilationInfo info, TreePath tp, TypeMirrorHandle targetTypeHandle, String targetTypeDN) {
            super(info, tp);
            this.targetTypeHandle = targetTypeHandle;
            this.targetTypeDN = targetTypeDN;
        }

        @Override
        protected String getText() {
            return NbBundle.getMessage(ChangeMethodReturnType.class, "FIX_ChangeMethodReturnType", targetTypeDN);
        }

        @Override
        protected void performRewrite(WorkingCopy wc, TreePath tp, boolean canShowUI) {
            TypeMirror targetType = targetTypeHandle.resolve(wc);

            if (targetType == null) {
                //XXX: log
                return ;
            }

            MethodTree mt = (MethodTree) tp.getLeaf();
            TreeMaker make = wc.getTreeMaker();

            wc.rewrite(mt.getReturnType(), make.Type(targetType));
        }

    }
}
