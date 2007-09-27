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
package org.netbeans.modules.java.additional.refactorings.splitclass;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.ModificationResultProvider;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
import org.openide.filesystems.FileObject;
import org.openide.text.PositionBounds;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public class MethodReturnTypeChangeElement extends SimpleRefactoringElementImplementation implements CancellableTask<WorkingCopy>, ModificationResultProvider {
    private final FileObject file;
    private final String newType;
    private final String oldName;
    private final TreePathHandle handle;
    private final Lookup context;
    
    public MethodReturnTypeChangeElement(String newType, TreePathHandle methodPathHandle, String name, Lookup context, FileObject file) {
        this.file = file;
        this.newType = newType;
        this.oldName = name;
        this.handle = methodPathHandle;
        this.context = context;
        
    }

    public String getText() {
        return "Rename parameter " + oldName + " to " + newType;
    }

    public String getDisplayText() {
        return getText();
    }

    public void performChange() {
        JavaSource js = JavaSource.forFileObject (file);
        try {
            js.runModificationTask(this).commit();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
    }
    
    ModificationResult result;
    public ModificationResult getModificationResult() {
        if (result == null) {
            JavaSource js = JavaSource.forFileObject (file);
            try {
                result = js.runModificationTask(this);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
            }
        }
        return result;
    }    

    public Lookup getLookup() {
        return context;
    }

    public FileObject getParentFile() {
        return file;
    }

    public PositionBounds getPosition() {
        return null;
    }

    volatile boolean cancelled;
    public void cancel() {
        cancelled = true;
    }

    public void run(WorkingCopy copy) throws Exception {
        copy.toPhase (Phase.RESOLVED);
        TreePath path = handle.resolve(copy);
        Tree tree = path.getLeaf();
        if (tree.getKind() == Kind.METHOD_INVOCATION) {
            changeMethodInvocation (copy, path, (MethodInvocationTree) tree);
        } else if (tree.getKind() == Kind.METHOD) {
            changeMethod (copy, path, (MethodTree) tree);
        } else {
            throw new IllegalArgumentException ("Don't know what to do with "
                    + tree.getKind() + ":\n" + tree);
        }
    }
    
    private void changeMethodInvocation (WorkingCopy copy, TreePath path, MethodInvocationTree tree) {
        
    }
    
    private void changeMethod (WorkingCopy copy, TreePath path, MethodTree tree) {
        TreeMaker maker = copy.getTreeMaker();
        Tree retTypeTree = tree.getReturnType();
        ReturnLocator ret = new ReturnLocator();
        Set <ReturnTree> returns = new HashSet <ReturnTree> ();
        ret.scan(path, returns);
        for (ReturnTree rt : returns) {
            ReturnTree nue;
            if ("void".equals(newType)) {
                //maybe we should look for statements that are just return statements,
                //see if they're in a block by themselves and if so remove
                //that block and any if/while/etc. that owns it - basically figure
                //out how to prove that the block now does nothing
                nue = maker.Return(null);
            } else {
                //Do what here?  Maybe nothing?
                nue = null;//maker.Return(maker.Identifier(newType));
            }
            if (nue != null) {
                copy.rewrite (rt, nue);
            }
            /*
            TreePath p = TreePath.getPath(copy.getCompilationUnit(), rt);
            do {
                p = p.getParentPath();
            } while (p != null && !(p.getLeaf() instanceof StatementTree));
            StatementTree st = p != null ? (StatementTree) p.getLeaf() : null;
            if (st != null) {
                do {
                    p = p.getParentPath();
                } while (p != null && !(p.getLeaf() instanceof BlockTree));
                if (p != null) {
                    BlockTree bt = (BlockTree) p.getLeaf();
                    assert bt.getStatements().contains (st);
                    List <StatementTree> statements = new ArrayList <StatementTree> (bt.getStatements());
                    //XXX we need to filter these statements - we're probably 
                    //removing too much here.  In some cases, probably need to
                    //leave a return with no argument.
                    statements.remove(st);
                    BlockTree nue = maker.Block(statements, bt.isStatic());
                    copy.rewrite (bt, nue);
                }
            }
             */ 
        }
        IdentifierTree nue = maker.Identifier(newType);
        copy.rewrite(retTypeTree, nue);
    }
    
    private static final class ReturnLocator extends TreePathScanner<Void, Set<ReturnTree>> {
        @Override
        public Void visitReturn(ReturnTree tree, Set<ReturnTree> set) {
            set.add (tree);
            //XXX check the expression - make sure there is nothing like
            //      return (foo[3] = 24);
            return super.visitReturn( tree, set );
        }
    }
}
