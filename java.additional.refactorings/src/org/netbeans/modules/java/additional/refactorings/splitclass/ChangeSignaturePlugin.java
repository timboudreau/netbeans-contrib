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
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.ClassIndex.SearchKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.Refactoring;
import org.netbeans.modules.java.additional.refactorings.Utils;
import org.netbeans.modules.java.additional.refactorings.splitclass.ChangeSignatureRefactoring.Transform;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
import org.netbeans.modules.refactoring.spi.Transaction;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tim Boudreau
 */
public class ChangeSignaturePlugin extends Refactoring {
    private final ChangeSignatureRefactoring refactoring;
    private final FileObject file;

    public ChangeSignaturePlugin(ChangeSignatureRefactoring refactoring, FileObject fob) {
        this.file = fob;
        this.refactoring = refactoring;
    }

    protected Problem preCheck(CompilationController wc) throws IOException {
        return null;
    }

    protected Problem checkParameters(CompilationController wc) throws IOException {
        /* Pending:
         *  If param names changed:
         *   - Scan all implementors for local variable with name that matches any
         *      changed parameter name if we should change implementation 
         *      param names;  if not, skip this.
         *  - If return type changed:
         *   - If old return type non-void
         *     - Try to find the actual type;  scan all usages of the returned
         *       object in invoking methods and determine if any methods which
         *       don't exist on the new type are called
         *  If param type changed
         *    - Do same as if return type changed
         */ 
        return null;
    }

    protected Problem fastCheckParameters(CompilationController wc) throws IOException {
        return null;
    }

    protected Problem prepare(WorkingCopy wc, RefactoringElementsBag bag) throws IOException {
        wc.toPhase(Phase.RESOLVED);
        List <Transform> l = refactoring.getChanges();
        TreePathHandle toRefactor = refactoring.methodHandle;
        TreePath path = toRefactor.resolve(wc);
        MethodTree theMethod = (MethodTree) path.getLeaf();
        ExecutableElement element = (ExecutableElement) wc.getTrees().getElement(path);
        Collection <ExecutableElement> overrides = Utils.getOverridingMethods(element, wc);
        Collection <TreePathHandle> invocations = Utils.getInvocationsOf (element, wc);
        System.err.println(overrides.size() + " overrides");
        System.err.println(invocations.size() + " invocations");
        
        bag.
        
        //Iterate the changes made to the method signature
        for (Transform t : l) {
            //Iterate all invocations, and generate a refactoring element for each
            for (TreePathHandle h : invocations) {
                TreePath pathToInvocation = h.resolve(wc);
                Tree tree = pathToInvocation.getLeaf();
                while (pathToInvocation != null && tree.getKind() != Kind.METHOD_INVOCATION) {
                    pathToInvocation = pathToInvocation.getParentPath();
                    tree = pathToInvocation.getLeaf();
                }
                if (pathToInvocation == null) {
                    throw new IllegalStateException ("Can't get there from here: " + h.resolve(wc));                    
                }
                MethodInvocationTree invocation = (MethodInvocationTree) tree;
                Element invokingMethodElement = wc.getTrees().getElement(pathToInvocation);
                ElementHandle<TypeElement> eh = ElementHandle.<TypeElement>create(wc.getElementUtilities().enclosingTypeElement(invokingMethodElement));
                Set <FileObject> fobs = wc.getJavaSource().getClasspathInfo().getClassIndex().getResources(eh, EnumSet.of(SearchKind.TYPE_REFERENCES), EnumSet.of(SearchScope.SOURCE));
                System.err.println("Process " + t + " on " + fobs);
                SimpleRefactoringElementImplementation refactorElement = t.getElement(invocation, wc, refactoring.getContext(), fobs.iterator().next());
                bag.add (refactoring, refactorElement);
            }
            
            //Iterate all overrides, and generate a refactoring element for each
            for (ExecutableElement e : overrides) {
                MethodTree methodTree = (MethodTree) wc.getTrees().getTree(e);
                TypeElement typeEl = wc.getElementUtilities().enclosingTypeElement(e);
                ElementHandle<TypeElement> eh = ElementHandle.<TypeElement>create(wc.getElementUtilities().enclosingTypeElement(typeEl));
                Set <FileObject> fobs = wc.getJavaSource().getClasspathInfo().getClassIndex().getResources(eh, EnumSet.of(SearchKind.TYPE_REFERENCES), EnumSet.of(SearchScope.SOURCE));
                System.err.println("Process " + t + " on " + fobs);
                SimpleRefactoringElementImplementation refactorElement = t.getElement(methodTree, wc, refactoring.getContext(), fobs.iterator().next());
                bag.add (refactoring, refactorElement);
            }
            SimpleRefactoringElementImplementation refactorElement = t.getElement(theMethod, 
                    wc, refactoring.getContext(), file);
            bag.add (refactoring, refactorElement);
        }
        return null;
    }

    protected FileObject getFileObject() {
        return file;
    }    
}
