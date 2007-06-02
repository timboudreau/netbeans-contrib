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
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterRenamePolicy;
import org.netbeans.modules.java.additional.refactorings.visitors.RequestedParameterChanges;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.additional.refactorings.Refactoring;
import org.netbeans.modules.java.additional.refactorings.Utils;
import org.netbeans.modules.java.additional.refactorings.splitclass.ChangeSignatureRefactoring.Transform;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext.ScanContext;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterScanner;
import org.netbeans.modules.java.additional.refactorings.visitors.VariableNameScanner;
import org.netbeans.modules.refactoring.api.Problem;
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
            this.info = info;
        }
        private CompilationInfo info;
        
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
        
//        Collection <ExecutableElement> overrides = Utils.getOverridingMethods(theMethod, wc);
        this.overrideHandles = Utils.getOverridingMethodHandles(theMethod, wc);        
        this.invocations = Utils.getInvocationsOf (theMethod, wc);
        System.err.println(overrideHandles.size() + " overrides");
        System.err.println(invocations.size() + " invocations");

        ParameterRenamePolicy policy = refactoring.policy;
        RequestedParameterChanges requestedChanges = refactoring.getParameterModificationInfo();
        CompilationUnitTree compUnit = wc.getCompilationUnit();
        
        if (!requestedChanges.isEmpty()) {
            final ScanCtxImpl scan = new ScanCtxImpl(theMethod, wc);
            
            VariableNameScanner variableNameCollector = new VariableNameScanner (
                    VariableNameScanner.Mode.SCAN_METHOD_BODIES);
            
            ElementHandle <ExecutableElement> theMethodHandle = 
                    ElementHandle.<ExecutableElement>create (theMethod);
            
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
                for (final TreePathHandle override : overrideHandles) {
                    final MethodTree tree = Utils.<MethodTree>resolveTreePathHandle(override);
                    final ParameterScanner pscanner = new ParameterScanner();
                    final class X implements CancellableTask <CompilationController> {
                        private volatile boolean cancelled;
                        public void cancel() {
                            this.cancelled = true;
                        }

                        public void run(CompilationController cc) throws Exception {
                            if (cancelled) return;
                            cc.toPhase (Phase.RESOLVED);
                            final VariableNameScanner nameCollector = 
                                    new VariableNameScanner (VariableNameScanner.Mode.SCAN_METHOD_BODIES);
                            nameCollector.scan(tree, changes);
                            List <? extends VariableTree> parameters = tree.getParameters();
                            int max = parameters.size();
                            ExecutableElement methodElement = (ExecutableElement) 
                                    override.resolveElement(cc);
//                                    cc.getTrees().getElement(TreePath.getPath(cc.getCompilationUnit(), tree));
                            overriddenMethods.add(ElementHandle.create(methodElement));
                            scan.reset (methodElement, cc);
                            for (int i=0; i < max; i++) {
                                VariableTree vt = parameters.get(i);
                                parameterScanner.scan (vt, changes);
                                pscanner.scan(tree, changes);
                                scan.inc();
                            }
                        }
                    };
                    FileObject fileContainingOverride = override.getFileObject();
                    JavaSource src = JavaSource.forFileObject (fileContainingOverride);
                    X x = new X();
                    src.runUserActionTask(x, true);
                }
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
//        Iterable <MethodTree> overrides = Utils.resolveTreePathHandles(overrideHandles);

        System.err.println(l.size() + " transforms created.");
        //Iterate the changes made to the method signature
        for (final Transform t : l) {
            System.err.println("  Transform: " + t);
            //Iterate all invocations, and generate a refactoring element for each
            for (final TreePathHandle h : invocations) {
                final FileObject fob = h.getFileObject();
                assert fob != null : "Null fileobject for " + h;
                final class Y implements CancellableTask <CompilationController> {
                    private volatile boolean cancelled;
                    public void cancel() {
                        this.cancelled = true;
                    }
                    public void run(CompilationController wc) throws Exception {
                        if (cancelled) return;
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
                        System.err.println("Process " + t + " on " + fob.getPath());
                        SimpleRefactoringElementImplementation refactorElement = t.getElement(invocation, wc, refactoring.getContext(), fob);
                        bag.add (refactoring, refactorElement);
                    }
                };
                JavaSource src = JavaSource.forFileObject (fob);
                Y y = new Y();
                src.runUserActionTask(y, true);
            }

            //Iterate all overrides, and generate a refactoring element for each
            for (final TreePathHandle e : overrideHandles) {
                final FileObject fob = e.getFileObject();
                assert fob != null : "Null fileobject for " + e;
                final class Z implements CancellableTask <CompilationController> {
                    private volatile boolean cancelled;
                    public void cancel() {
                        this.cancelled = true;
                    }
                    public void run(CompilationController wc) throws Exception {
                        if (cancelled) return;
                        wc.toPhase (Phase.RESOLVED);
                        MethodTree methodTree = (MethodTree) e.resolve(wc);
                        ExecutableElement element = (ExecutableElement) e.resolveElement(wc);
                        assert methodTree != null : "Got null method tree for " + e;
                        TypeElement typeEl = wc.getElementUtilities().enclosingTypeElement(element);
                        ElementHandle<TypeElement> eh = ElementHandle.<TypeElement>create(wc.getElementUtilities().enclosingTypeElement(typeEl));
                        System.err.println("Process " + t + " on " + fob.getPath());
                        SimpleRefactoringElementImplementation refactorElement = t.getElement(methodTree, wc, refactoring.getContext(), fob);
                        bag.add (refactoring, refactorElement);
                    }
                };
                JavaSource src = JavaSource.forFileObject(file);
                Z z = new Z();
                src.runUserActionTask(z, true);
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
