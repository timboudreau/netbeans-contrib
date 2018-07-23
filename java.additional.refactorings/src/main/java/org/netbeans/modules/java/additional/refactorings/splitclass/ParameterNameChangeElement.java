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
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ElementUtilities;
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
public class ParameterNameChangeElement extends SimpleRefactoringElementImplementation implements CancellableTask<WorkingCopy>, ModificationResultProvider {
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
        assert copy.getCompilationUnit() != null : "Compilation unit from " + //NOI18N        
                "working copy is null"; //NOI18N        
        TreePath path = handle.resolve(copy);
        assert path != null : "Tree path " + path + " resolved to null"; //NOI18N        
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
        ParameterUsagesCollector collector = new ParameterUsagesCollector();
        collector.scan (tree, oldName);
        Trees trees = copy.getTrees();
        ElementUtilities elemUtils = copy.getElementUtilities();
        for (IdentifierTree old : collector.trees) {
            TreePath pathToId = TreePath.getPath(copy.getCompilationUnit(), old);
            Element elemForParam = trees.getElement(pathToId);
            if (elemUtils.isLocal(elemForParam)) {
                IdentifierTree nue = maker.Identifier(newName);
                copy.rewrite (old, nue);
            }
        }
        List <? extends VariableTree> params = tree.getParameters();
        for (VariableTree vt : params) {
            if (vt.getName().contentEquals(oldName)) {
                VariableTree nue = maker.Variable(vt.getModifiers(), newName, 
                        vt.getType(), vt.getInitializer());
                
                copy.rewrite (vt, nue);
                break;
            }
        }
    }
    
    private static final class ParameterUsagesCollector extends TreeScanner <Void, String> {
        Set <IdentifierTree> trees = new HashSet <IdentifierTree>();
        @Override
        public Void visitIdentifier(IdentifierTree tree, String nameToFind) {
            //XXX make sure it doesn't find name matches with things that are name-matched
            //fields of other objects, etc.
            if (tree.getName().toString().equals(nameToFind)) {
                trees.add (tree);
            }
            return super.visitIdentifier(tree, nameToFind);
        }
    }

    public void performChange() {
        ModificationResult res = getModificationResult();
        try {
            if (res != null) res.commit();
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
}
