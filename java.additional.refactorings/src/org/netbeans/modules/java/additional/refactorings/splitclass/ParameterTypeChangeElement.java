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

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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
 * @author Tim Boudreau
 */
public class ParameterTypeChangeElement extends SimpleRefactoringElementImplementation implements CancellableTask<WorkingCopy> {
    private final FileObject file;
    private final String newType;
    private final String oldType;
    private final TreePathHandle handle;
    private final Lookup context;
    private final String name;
    private final int paramIndex;
    public ParameterTypeChangeElement(String newType, String oldType, int paramIndex, TreePathHandle methodPathHandle, String name, Lookup context, FileObject file) {
        this.file = file;
        this.newType = newType;
        this.oldType = oldType;
        this.handle = methodPathHandle;
        this.context = context;
        this.name = name;
        this.paramIndex = paramIndex;
    }

    public String getText() {
        return "Rename parameter " + oldType + " to " + newType + " in " + name;
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
        copy.toPhase (Phase.RESOLVED);
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
        //??? not much to do here...
    }

    private void changeMethod (WorkingCopy copy, TreePath path, MethodTree tree, TreeMaker maker) {
        List <? extends VariableTree> params = tree.getParameters();
        VariableTree old = params.get(paramIndex);
        ModifiersTree mods = maker.Modifiers(Collections.<Modifier>emptySet());
        TypeKind prim = TypeKind.valueOf(newType.toUpperCase());
        Tree typeTree;
        if (prim != null) {
            TypeMirror ptype = copy.getTypes().getPrimitiveType(prim);
            typeTree = maker.Type(ptype);
        } else {
            typeTree = maker.Identifier(newType);
        }
        VariableTree nue = maker.Variable(mods, old.getName().toString(), typeTree, null);
        System.err.println("Change parameter " + paramIndex + " from " + old + " to " + nue);
        copy.rewrite (old, nue);
    }
}
