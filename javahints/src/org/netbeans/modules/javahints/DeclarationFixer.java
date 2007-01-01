/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
