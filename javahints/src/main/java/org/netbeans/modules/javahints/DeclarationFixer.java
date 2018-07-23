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
package org.netbeans.modules.javahints;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.modules.java.hints.spi.ErrorRule.Data;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class DeclarationFixer implements ErrorRule<Void> {
    
    public DeclarationFixer() {
    }
    
    public Set<String> getCodes() {
        return Collections.singleton("compiler.warn.prob.found.req");
    }
    
    public List<Fix> run(CompilationInfo info, String diagnosticKey, int offset, TreePath treePath, Data<Void> data) {
        TreePath tp = info.getTreeUtilities().pathFor(offset);
        Tree leaf = tp.getLeaf();
        
        if (leaf.getKind() != Kind.VARIABLE) {
            return Collections.<Fix>emptyList();
        }
            
        VariableTree variable = (VariableTree) leaf;
        
        if (variable.getInitializer().getKind() != Kind.NEW_CLASS || variable.getType().getKind() != Kind.PARAMETERIZED_TYPE) {
            return Collections.<Fix>emptyList();
        }
        
        ParameterizedTypeTree leftType = (ParameterizedTypeTree) variable.getType();
        
        if (leftType.getTypeArguments().isEmpty()) {
            return Collections.<Fix>emptyList();
        }
        
        NewClassTree nct = (NewClassTree) variable.getInitializer();
        
        if (!nct.getTypeArguments().isEmpty()) {
            return Collections.<Fix>emptyList();
        }
        
        return Collections.<Fix>singletonList(new FixImpl(TreePathHandle.create(new TreePath(tp, nct), info), TreePathHandle.create(new TreePath(tp, leftType), info), info.getJavaSource()));
    }

    public void cancel() {
        //XXX: not yet implemented
    }

    public String getId() {
        return DeclarationFixer.class.getName();
    }
    
    public String getDisplayName() {
        return "Fix Generic Declarations";
    }
    
    public String getDescription() {
        return "Fix Generic Declarations";
    }
    
    private static final class FixImpl implements Fix {
        
        private TreePathHandle nct;
        private TreePathHandle type;
        private JavaSource js;
        
        public FixImpl(TreePathHandle nct, TreePathHandle type, JavaSource js) {
            this.nct = nct;
            this.type = type;
            this.js = js;
        }

        public String getText() {
            return "Fix variable initialization to match the declaration";
        }

        public ChangeInfo implement() {
            try {
                js.runModificationTask(new CancellableTask<WorkingCopy>() {
                    public void cancel() {}
                    public void run(WorkingCopy copy) throws IOException {
                        copy.toPhase(Phase.PARSED);
                        TreePath nct = FixImpl.this.nct.resolve(copy);
                        TreePath type = FixImpl.this.type.resolve(copy);
                        
                        if (nct == null || type == null) {
                            //the code has been changed, nothing to do
                            return ;
                        }
                        
                        NewClassTree ncTree = (NewClassTree) nct.getLeaf();
                        ParameterizedTypeTree typeTree = (ParameterizedTypeTree) type.getLeaf();
                        
                        @SuppressWarnings("unchecked")
                        NewClassTree nueTree = copy.getTreeMaker().NewClass(ncTree.getEnclosingExpression(), (List<? extends ExpressionTree>) typeTree.getTypeArguments(), ncTree.getIdentifier(), ncTree.getArguments(), ncTree.getClassBody());
                        
                        copy.rewrite(ncTree, nueTree);
                    }
                });
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
            
            return null;
        }
}
    
}
