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

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
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
 * @author Tim Boudreau
 */
public class ParameterNameChangeElement extends SimpleRefactoringElementImplementation implements CancellableTask<WorkingCopy> {
    private final FileObject file;
    private final String newName;
    private final String oldName;
    private final TreePathHandle handle;
    private final Lookup context;
    
    public ParameterNameChangeElement(String newName, TreePathHandle methodPathHandle, String name, Lookup context, FileObject file) {
        this.file = file;
        this.newName = newName;
        this.oldName = name;
        this.handle = methodPathHandle;
        this.context = context;
        
    }

    public String getText() {
        return "Rename parameter " + oldName + " to " + newName;
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
        TreePath path = handle.resolve(copy);
        Tree tree = path.getLeaf();
        TreeMaker maker = copy.getTreeMaker();
        if (tree.getKind() == Kind.METHOD_INVOCATION) {
            changeMethodInvocation (copy, path, (MethodInvocationTree) tree, maker);
        } else if (tree.getKind() == Kind.METHOD) {
            changeMethod (copy, path, (MethodTree) tree, maker);
        } else {
            throw new IllegalArgumentException ("Don't know what to do with "
                    + tree.getKind() + ":\n" + tree);
        }
    }
    
    private void changeMethodInvocation (WorkingCopy copy, TreePath path, MethodInvocationTree tree, TreeMaker maker) {
        //do nothing - don't change parameter names in existing methods
    }
    
    private void changeMethod (WorkingCopy copy, TreePath path, MethodTree tree, TreeMaker maker) {
        //XXX check for local variables with the same name!
        ParameterUsagesCollector collector = new ParameterUsagesCollector();
        collector.scan (tree, oldName);
        for (IdentifierTree old : collector.paths) {
            IdentifierTree nue = maker.Identifier(newName);
            copy.rewrite (old, nue);
        }
    }
    
    private static final class ParameterUsagesCollector extends TreeScanner <Void, String> {
        Set <IdentifierTree> paths = new HashSet <IdentifierTree>();
        @Override
        public Void visitIdentifier(IdentifierTree tree, String nameToFind) {
            if (tree.getName().contentEquals(nameToFind)) {
                paths.add (tree);
            }
            return super.visitIdentifier(tree, nameToFind);
        }
    }
}
