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

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.io.IOException;
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
public class RenameMethodReferenceElement extends SimpleRefactoringElementImplementation implements CancellableTask<WorkingCopy>, ModificationResultProvider {
    private final String renameTo;
    private final TreePathHandle toRenameIn;
    private final String name;
    private final FileObject file;
    private final Lookup context;
    
    public RenameMethodReferenceElement(TreePathHandle toRenameIn, String renameTo, String name, Lookup context, FileObject file) {
        this.toRenameIn = toRenameIn;
        this.renameTo = renameTo;
        this.name = name;
        this.context = context;
        this.file = file;
    }

    public String getText() {
        return "Change reference in " + name;
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
        if (cancelled) return;
        copy.toPhase(Phase.RESOLVED);
        TreePath path = toRenameIn.resolve(copy);
        Tree tree = path.getLeaf();
        TreeMaker maker = copy.getTreeMaker();
        if (tree.getKind() == Kind.METHOD_INVOCATION) {
            MethodInvocationTree invocationTree = (MethodInvocationTree) tree;
            MemberSelectTree oldSelect = (MemberSelectTree) invocationTree.getMethodSelect();
            MemberSelectTree newSelect = maker.MemberSelect(oldSelect.getExpression(), renameTo);
            copy.rewrite(oldSelect, newSelect);
        } else if (tree.getKind() == Kind.METHOD) {
            MethodTree oldMethod = (MethodTree) tree;
            MethodTree newMethod = maker.Method(oldMethod.getModifiers(), renameTo, oldMethod.getReturnType(), 
                    oldMethod.getTypeParameters(), oldMethod.getParameters(), 
                    oldMethod.getThrows(), oldMethod.getBody(), 
                    (ExpressionTree) oldMethod.getDefaultValue());
            copy.rewrite (oldMethod, newMethod);
        }
    }

    public ModificationResult getModificationResult() {
        JavaSource js = JavaSource.forFileObject (file);
        try {
            return js.runModificationTask(this);
        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
        return null;
    }
}
