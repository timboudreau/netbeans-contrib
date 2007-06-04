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

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodInvocationTree;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterRenamePolicy;
import org.netbeans.modules.java.additional.refactorings.visitors.RequestedParameterChanges;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.Refactoring;
import org.netbeans.modules.java.additional.refactorings.Utils;
import org.netbeans.modules.java.additional.refactorings.Utils.TreePathHandleTask;
import org.netbeans.modules.java.additional.refactorings.splitclass.ChangeSignatureRefactoring.Transform;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext.ScanContext;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterScanner;
import org.netbeans.modules.java.additional.refactorings.visitors.VariableNameScanner;
import org.netbeans.modules.refactoring.api.Problem;
import org.netbeans.modules.refactoring.spi.RefactoringElementImplementation;
import org.netbeans.modules.refactoring.spi.RefactoringElementsBag;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
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
    
    private static final class ScanCtxImpl implements ScanContext {
        private int idx = 0;
        private ExecutableElement element = null;
        ScanCtxImpl (ExecutableElement element, CompilationInfo info) {
            this.element = element;
            this.info = info;
        }
        
        public int getParameterIndex() {
            return idx;
        }

        public ExecutableElement getCurrentMethodElement() {
            return element;
        }
        
        void inc() {
            idx++;
        }
        
        void reset(ExecutableElement element, CompilationInfo info) {
            idx = 0;
            this.element = element;
            setCompilationInfo(info);
        }
        private CompilationInfo info;
        
        public void setCompilationInfo (CompilationInfo info) {
            this.info = info;
        }
        
        public CompilationInfo getCompilationInfo() {
            return info;
        }

        public CompilationUnitTree getCompilationUnit() {
            return info.getCompilationUnit();
        }
    }

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
         *  For the parent and each implementing class:
         *    - Check that there is not already a method with the same name and
         *      types (checking erasure) which will cause the renamed impl class
         *      to be uncompilable
         */
    protected Problem checkParameters (CompilationController wc) throws IOException {
        Problem result = null;
        wc.toPhase(Phase.RESOLVED);
        TreePathHandle toRefactor = refactoring.methodHandle;
        TreePath path = toRefactor.resolve(wc);
        ExecutableElement theMethod = (ExecutableElement) wc.getTrees().getElement(path);
        MethodTree theMethodTree = (MethodTree) path.getLeaf();
        
        this.overrideHandles = Utils.getOverridingMethodHandles(theMethod, wc);        
        ElementHandle <ExecutableElement> theMethodHandle = ElementHandle.create (theMethod);
        this.invocations = Utils.getInvocationsOf (theMethodHandle, wc);

        ParameterRenamePolicy policy = refactoring.policy;
        RequestedParameterChanges requestedChanges = refactoring.getParameterModificationInfo();
        if (!requestedChanges.isEmpty()) {
            final ScanCtxImpl scan = new ScanCtxImpl(theMethod, wc);
            
            VariableNameScanner variableNameCollector = new VariableNameScanner (
                    VariableNameScanner.Mode.SCAN_METHOD_BODIES);
            
            final ParameterChangeContext changes = new ParameterChangeContext (requestedChanges, scan);
            BlockTree bodyTree = theMethodTree.getBody();
            
            //Here we scan the method body of the method that is to be 
            //refactored.  The scanner will collect all variable
            //names it encounters in the ChangeData returned by changes.data,
            //storing them mapped to the method
            variableNameCollector.scan (bodyTree, changes);
            List <? extends VariableTree> paramsOnMethodBeingRefactored = theMethodTree.getParameters();
            //Now we make sure none of the parameter names we want to use are
            //already in use as variables in the body of the method
            for (VariableTree param : paramsOnMethodBeingRefactored) {
                String paramName = param.getName().toString();
                //If we can't rename the parameter on the method being refactored
                //itself, we should give up.
                if (changes.changeData.isConflict(theMethodHandle, paramName, wc)) {
                    result = new Problem (true, "Cannot rename a parameter to \"" + //XXX I18N
                            paramName + "\".  A local variable is already using that " +
                            "name");                            
                }
            }
            
            //Now, if we are going to rename parameters in methods that override the
            //one we're changing, we need to do the same thing for all overrides.
            //In cases where there is a conflict (a changed parameter name is already
            //in use as a variable in the method), we will skip those.  Depending
            //on the RenameParameterPolicy, we may also skip renaming parameters
            //that do not have the exact name of the old parameter.
            //The ChangeData will keep a map of element handles to overriding 
            //methods : list of indices of parameters to skip
            final Set <ElementHandle <ExecutableElement>> overriddenMethods =
                    new HashSet <ElementHandle <ExecutableElement>> ();
            
            if (result == null && policy != ParameterRenamePolicy.DO_NOT_RENAME && !overrideHandles.isEmpty()) {
                final VariableNameScanner parameterScanner = new VariableNameScanner (
                        VariableNameScanner.Mode.SCAN_PARAMETERS);
                final ParameterScanner pscanner = new ParameterScanner();
                Utils.<Void, ParameterChangeContext>runAgainstSources(overrideHandles, changes, parameterScanner, pscanner);
            }
            Map<ElementHandle<ExecutableElement>, Set<String>> fatals = 
                    changes.changeData.getAllFatalConflicts(overriddenMethods, wc, 
                    requestedChanges);
            if (!fatals.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ElementHandle<ExecutableElement> h : fatals.keySet()) {
                    sb.append (nameOf (h, wc));
                    sb.append (" already contains variables using the new variable name(s) ");
                    sb.append (fatals.get (h));
                    sb.append ("\n");                    
                }
                result = new Problem (true, sb.toString());
            }
            System.err.println("Result is " + result);
            System.err.println("Change data: ");
            System.err.println(changes.changeData.toString(wc, requestedChanges));
            
            //Dispose of our compiler info so we don't leak
            changes.changeData.seal();
        }
        return result;
    }
    
    private String nameOf (ElementHandle<ExecutableElement> elem, CompilationController cc) {
        ExecutableElement el = elem.resolve (cc);
        TypeElement te = cc.getElementUtilities().enclosingTypeElement(el);
        return te.getQualifiedName() + "." + el.getSimpleName();
    }

    Collection <TreePathHandle> overrideHandles;
    Collection <TreePathHandle> invocations;

    protected Problem fastCheckParameters(CompilationController wc) throws IOException {
        return null;
    }

    protected Problem prepare(WorkingCopy wc, final RefactoringElementsBag bag) throws IOException {
        assert overrideHandles != null : "prepare called after checkParameters() failed"; //NOI18N
        wc.toPhase(Phase.RESOLVED);
        List <Transform> l = refactoring.getChanges();
        TreePathHandle toRefactor = refactoring.methodHandle;
        TreePath path = toRefactor.resolve(wc);
        MethodTree theMethod = (MethodTree) path.getLeaf();

        final List <RefactoringElementImplementation> refactoringElements = 
                new ArrayList<RefactoringElementImplementation>();
        
        //Iterate the changes made to the method signature
        for (final Transform t : l) {
            System.err.println("  Transform: " + t);
            
            //Iterate all invocations and generate a refactoring element 
            //corresponding to this transform for that invocation
            TreePathHandleTask<Transform> invocationHandler = new TreePathHandleTask <Transform>() {
                public void run(CompilationController cc, TreePathHandle h, FileObject file, Transform t) {
                    if (cancelled) return;
                    TreePath pathToInvocation = h.resolve(cc);
                    Tree tree = pathToInvocation.getLeaf();
                    while (pathToInvocation != null && tree.getKind() != Kind.METHOD_INVOCATION) {
                        pathToInvocation = pathToInvocation.getParentPath();
                        tree = pathToInvocation.getLeaf();
                    }
                    MethodInvocationTree invocation = (MethodInvocationTree) tree;
                    SimpleRefactoringElementImplementation refactorElement =
                            t.getElement(invocation, cc, refactoring.getContext(), file);

                    refactoringElements.add (refactorElement);
                }
            };
            Utils.<Transform>runAgainstSources(invocations, invocationHandler, t);
            //Iterate all overrides, and generate a refactoring element for each
            TreePathHandleTask<Transform> overrideHandler = new TreePathHandleTask <Transform>() {
                public void run(CompilationController cc, TreePathHandle handle, FileObject file, Transform arg) {
                    if (cancelled) return;
                    MethodTree methodTree = (MethodTree) handle.resolve(cc).getLeaf();
                    assert methodTree != null : "Got null method tree for " + handle;
                    SimpleRefactoringElementImplementation refactorElement = t.getElement(
                            methodTree, cc, refactoring.getContext(), file);
                    refactoringElements.add (refactorElement);
                }
            };
            
            Utils.<Transform>runAgainstSources(overrideHandles, overrideHandler, t);
            SimpleRefactoringElementImplementation refactorElement = t.getElement(theMethod,
                    wc, refactoring.getContext(), file);
            refactoringElements.add(refactorElement);
        }
        bag.addAll (refactoring, refactoringElements);
        return null;
    }

    protected FileObject getFileObject() {
        return file;
    }

}
