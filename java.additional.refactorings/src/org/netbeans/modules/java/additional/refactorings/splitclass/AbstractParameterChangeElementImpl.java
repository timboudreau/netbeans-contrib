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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
import org.openide.filesystems.FileObject;
import org.openide.text.PositionBounds;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Tim
 */
public abstract class AbstractParameterChangeElementImpl extends SimpleRefactoringElementImplementation implements CancellableTask<WorkingCopy> {
    protected final TreePathHandle methodPathHandle;
    protected final String name;
    private final FileObject file;
    private final Lookup context;
    
    public AbstractParameterChangeElementImpl(TreePathHandle methodPathHandle, String name, Lookup context, FileObject file) {
        this.methodPathHandle = methodPathHandle;
        this.name = name;
        this.context = context;
        this.file = file;
        System.err.println("Created " + this);
        if ("void".equals(name)) {
            Thread.dumpStack();
        }
    }

    public abstract String getText();

    public final String getDisplayText() {
        return getText();
    }

    public final void performChange() {
        JavaSource js = JavaSource.forFileObject (file);
        try {
            js.runModificationTask(this).commit();
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
    }

    public final Lookup getLookup() {
        return context;
    }

    public final FileObject getParentFile() {
        return file;
    }

    public PositionBounds getPosition() {
        return null;
    }

    volatile boolean cancelled;
    public final void cancel() {
        cancelled = true;
    }

    public final void run(WorkingCopy copy) throws Exception {
        if (cancelled) return;
        copy.toPhase(Phase.RESOLVED);
        TreePath path = methodPathHandle.resolve(copy);
        Tree tree = path.getLeaf();
        System.err.println("Change parameter " + tree + " with " + this);
        TreeMaker maker = copy.getTreeMaker();
        if (tree.getKind() == Kind.METHOD_INVOCATION) {
            MethodInvocationTree oldInvocationTree = (MethodInvocationTree) tree;
            List <ExpressionTree> args = new ArrayList <ExpressionTree> (oldInvocationTree.getArguments());

            System.err.println("Size of old method invocation args " + args.size() + ": " + args);
            modifyArgs (args, maker);
            System.err.println("Size of old method invocation args " + args.size() + ": " + args);
            if (cancelled) return;

            @SuppressWarnings("unchecked")
            MethodInvocationTree newInvocationTree = maker.MethodInvocation((List <? extends ExpressionTree>) oldInvocationTree.getTypeArguments(), 
                    oldInvocationTree.getMethodSelect(), args);                
            copy.rewrite (oldInvocationTree, newInvocationTree);
        } else if (tree.getKind() == Kind.METHOD) {
            MethodTree oldTree = (MethodTree) tree;
            List <VariableTree> args = new ArrayList <VariableTree> (oldTree.getParameters());
            modifyOverrideArgs (args, maker);
            
            MethodTree nueTree = maker.Method(oldTree.getModifiers(), oldTree.getName().toString(), 
                    oldTree.getReturnType(), oldTree.getTypeParameters(), 
                    args, oldTree.getThrows(), oldTree.getBody(), (ExpressionTree) 
                    oldTree.getDefaultValue());
            copy.rewrite (oldTree, nueTree);
        } else {
            throw new IllegalArgumentException ("What is this? " + tree.getKind() + ": " + tree);
        }
    }
    
    protected abstract void modifyArgs (List <ExpressionTree> args, TreeMaker maker);
    protected abstract void modifyOverrideArgs (List <VariableTree> args, TreeMaker maker);
}
