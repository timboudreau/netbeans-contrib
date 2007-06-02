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

import org.netbeans.modules.java.additional.refactorings.visitors.ParameterRenamePolicy;
import org.netbeans.modules.java.additional.refactorings.visitors.RequestedParameterChanges;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.api.AbstractRefactoring;
import org.netbeans.modules.refactoring.spi.SimpleRefactoringElementImplementation;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.netbeans.misc.diff.*;

/**
 * Computes a set of transforms which should be applied successively to get
 * from the current method signature to the new one.  These transforms will then
 * be used to generate a refactoring element for each invocation or override
 * of the method in question.  See ChangeSignaturePlugin for that.
 *
 * @author Tim Boudreau
 */
public class ChangeSignatureRefactoring extends AbstractRefactoring {
    final List<Parameter> orig;
    final List<Parameter> nue;
    final String methodName;
    final String returnType;
    final TreePathHandle methodHandle;
    final ParameterRenamePolicy policy;
    public ChangeSignatureRefactoring(TreePathHandle methodHandle, Lookup source, List <Parameter> orig, 
            List <Parameter> nue, String methodName, String returnType,
            ParameterRenamePolicy policy) {
        super (source);
        this.methodHandle = methodHandle;
        this.orig = orig;
        this.nue = nue;
        this.returnType = returnType;
        this.methodName = methodName;
        this.policy = policy;
    }

    boolean isReturnTypeChanged() {
        return returnType != null;
    }
    
    boolean isMethodNameChanged() {
        return methodName != null;
    }
    
    public List <Transform> getChanges() {
        if (changes == null) {
            changes = computeChanges();
        }
        return changes;
    }
    
    private Collection <String> getNewOrChangedParameterNames() {
        Set <String> result = new HashSet<String>();
        for (Parameter p : nue) {
            if (p.isNew() || p.isNameChanged()) {
                result.add (p.getName());
            }
        }
        return result;
    }
    
    private Collection <String> getNewParameterNames() {
        Set <String> result = new HashSet<String>();
        for (Parameter p : nue) {
            if (p.isNew()) {
                result.add (p.getName());
            }
        }
        return result;
    }
    
    private List <String> getOriginalParameterNamesInOrder() {
        List <String> result = new ArrayList<String>(orig.size());
        for (Parameter p : orig) {
            result.add (p.getOriginalName());
        }
        return result;
    }
    
    public RequestedParameterChanges getParameterModificationInfo() {
        return new RequestedParameterChanges (getNewParameterNames(), //NOI18N        
                getNewOrChangedParameterNames(), 
                getOriginalParameterNamesInOrder(), 
                policy);
    }
    
    private List changes;
    private List <Transform> computeChanges() {
        List <Transform> result = new LinkedList <Transform> ();
        List <Parameter> work = new LinkedList <Parameter> (orig);
        int origSize = orig.size();
        int newSize = nue.size();
        int max = Math.max (origSize, newSize);
        int[] offsets = new int[max];        
        
        Set <Parameter> origSet = new HashSet <Parameter> (orig);
        Set <Parameter> nueSet = new HashSet <Parameter> (nue);
        int offset = 0;
        //Handle deletions
        for (int i=0; i < max; i++) {
            offsets[i] = offset;
            if (i < origSize) {
                Parameter p = orig.get(i);
                if (!nueSet.contains(p)) {
                    result.add(new ParameterRemovalTransform(i + offsets[i]));
                    assert p == work.get (i + offsets[i]);                    
                    work.remove (p);
                    offset--;
                }
            }
        }
        
        //Handle additions
        offset = 0;
        for (int i=0; i < max; i++) {
            offsets[i] += offset;
            if (i < newSize) {
                Parameter p = nue.get(i);
                if (!origSet.contains(p)) {
                    result.add(new ParameterAdditionTransform(i + offsets[i], 
                            p.getDefaultValue(),
                            p.getName(), p.getTypeName()));
                    work.add(i + offsets[i], p);
                    offset++;
                }
            }
        }
        
        for (int i=0; i < work.size(); i++) {
            Parameter p = work.get (i);
            if (!p.isNew()) {
                int currIndex = i;
                int nueIndex = nue.indexOf (p);
                if (currIndex != nueIndex) {
                    result.add (new ParamOrderTransform(currIndex, nueIndex));
                    if (currIndex < nueIndex) {
                        //slow & ugly but works
                        Parameter[] pp = work.toArray (new Parameter[work.size()]);
                        Parameter hold = pp[currIndex];
                        pp[currIndex] = pp[nueIndex];                        
                        pp[nueIndex] = hold;
                        work.clear();
                        work.addAll(Arrays.<Parameter>asList(pp));
                        if (nueIndex < i) {
                            i = nueIndex - 1;
                        }
                    }
                }
            }
        }
        
        for (Parameter p : nue) {
            if (!p.isNew()) {
                //Do type changes before name changes, or the type change
                //will look for the wrong parameter name
                if (p.isTypeChanged()) {
                    result.add (new ParameterTypeChange(p.getTypeName()));
                }
                if (p.isNameChanged()) {
                    if ("void".equals(p.getName())) {
                        System.err.println("Changing param name to void: " + p);
                    }
                    result.add (new ParameterNameTransform(p.getName(), 
                            p.getOriginalName()));
                }
            }
        }
        
        if (isMethodNameChanged()) {
            result.add (new MethodNameTransform(methodName));
        }
        if (isReturnTypeChanged()) {
            result.add (new MethodReturnTypeTransform(returnType));
        }
        
        System.err.println("CHANGES:");
        for (Transform c : result) {
            System.err.println("  " + c);
        }
        
        return result;
    }    
    
    public static enum ChangeKind {
        RETURN_TYPE, PARAM_ORDER, PARAM_ADDITION, PARAM_REMOVAL, METHOD_NAME, 
        PARAM_TYPE, PARAM_NAME
    }
    
    public static abstract class Transform {
        private final ChangeKind kind;
        Transform (ChangeKind kind) {
            this.kind = kind;
        }
        
        public ChangeKind getKind() {
            return kind;
        }
        
        final SimpleRefactoringElementImplementation getElement (MethodInvocationTree tree, CompilationController cc, Lookup context, FileObject file) {
            TreePath path = TreePath.getPath(cc.getCompilationUnit(), tree);            
            TreePathHandle handle = TreePathHandle.create(path, cc);
            Element element = cc.getTrees().getElement(path);
            TypeElement type = cc.getElementUtilities().enclosingTypeElement(element);
            String name = type.getQualifiedName() + "." + element.getSimpleName();
            assert handle.resolve(cc) != null;
            assert handle.resolve(cc).getLeaf() != null;
            assert handle.resolve(cc).getLeaf().getKind() == tree.getKind() : "TreePathHandle for a " + tree.getKind() +  //NOI18N
                    " comes up as " + handle.resolve(cc).getLeaf().getKind(); //NOI18N
            assert handle.resolve(cc).getLeaf().getKind() == path.getLeaf().getKind() : "TreePathHandle for a " + tree.getKind() + //NOI18N
                    " comes up as " + path.getLeaf().getKind() + " when resolved from a TreePath"; //NOI18N
            assert handle.resolve(cc).getLeaf().equals(tree);
            return createElement(tree, element, handle, path, name, file);
        }
        
        public final SimpleRefactoringElementImplementation getElement (MethodTree tree, CompilationController cc, Lookup context, FileObject file) {
            TreePath path = TreePath.getPath(cc.getCompilationUnit(), tree);
            TreePathHandle handle;
            try {
                handle = TreePathHandle.create(path, cc);
            } catch (NullPointerException e) {
                throw new NullPointerException ("Could not create tree path handle for " + file);
            }
            Element element = cc.getTrees().getElement(path);
            TypeElement type = cc.getElementUtilities().enclosingTypeElement(element);
            String name = type.getQualifiedName() + "." + element.getSimpleName();
            assert handle.resolve(cc) != null;
            assert handle.resolve(cc).getLeaf() != null;
            assert handle.resolve(cc).getLeaf().getKind() == tree.getKind() : "TreePathHandle for a " + tree.getKind() +  //NOI18N
                    " comes up as " + handle.resolve(cc).getLeaf().getKind(); //NOI18N
            assert handle.resolve(cc).getLeaf().getKind() == path.getLeaf().getKind() : "TreePathHandle for a " + tree.getKind() + //NOI18N
                    " comes up as " + path.getLeaf().getKind() + " when resolved from a TreePath"; //NOI18N
            assert handle.resolve(cc).getLeaf().equals(tree);
            SourcePositions sp = cc.getTrees().getSourcePositions();
            return createElement(tree, element, handle, path, name, file);
        }
        
        protected abstract SimpleRefactoringElementImplementation createElement (
                MethodInvocationTree tree, Element element, 
                TreePathHandle handle, TreePath path, String name, 
                FileObject file);
        
        protected abstract SimpleRefactoringElementImplementation createElement (
                MethodTree tree, Element element, TreePathHandle handle, 
                TreePath path, String name, FileObject file);
        
        public String toString() {
            return kind.toString();
        }
    }
    
    private class ParamOrderTransform extends Transform {
        int orig;
        int nue;
        ParamOrderTransform(int orig, int nue) {
            super (ChangeKind.PARAM_ORDER);
            this.orig = orig;
            this.nue = nue;
        }
        
        protected SimpleRefactoringElementImplementation createElement (MethodInvocationTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            SimpleRefactoringElementImplementation result =
                    new MoveParameterElementImpl(handle, orig, nue, name, getContext(), file);
            return result;
        }

        protected SimpleRefactoringElementImplementation createElement(MethodTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            SimpleRefactoringElementImplementation result =
                    new MoveParameterElementImpl(handle, orig, nue, name, getContext(), file);
            return result;
        }
        
        public String toString() {
            return super.toString() + " at " + orig + " to " + nue;
        }
        
    }
    
    private class ParameterRemovalTransform extends Transform {
        private int index;
        ParameterRemovalTransform(int index) {
            super (ChangeKind.PARAM_REMOVAL);
            this.index = index;
        }
        
        protected SimpleRefactoringElementImplementation createElement (MethodInvocationTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            SimpleRefactoringElementImplementation result =
                    new RemoveParameterElementImpl(handle, index, name, getContext(), file);
            return result;
        }

        protected SimpleRefactoringElementImplementation createElement(MethodTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            SimpleRefactoringElementImplementation result =
                    new RemoveParameterElementImpl(handle, index, name, getContext(), file);
            return result;
        }
        
        public String toString() {
            return super.toString() + " at " + index;
        }
    }
    
    private class ParameterAdditionTransform extends Transform {
        private int index;
        private final String defValue;
        private final String paramName;
        private final String paramType;
        ParameterAdditionTransform (int index, String defValue, String paramName, String paramType) {
            super (ChangeKind.PARAM_ADDITION);
            this.index = index;
            this.defValue = defValue;
            this.paramName = paramName;
            this.paramType = paramType;
        }

        protected SimpleRefactoringElementImplementation createElement(MethodInvocationTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new AddParameterElementImpl(handle, paramType,
                    index, name, defValue, getContext(), file, paramName);
        }

        protected SimpleRefactoringElementImplementation createElement(MethodTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new AddParameterElementImpl(handle, paramType,
                    index, name, defValue, getContext(), file, paramName);
        }
        
        public String toString() {
            return super.toString() + " at " + index + " named " + paramName + 
                    " type " + paramType + " defValue " + defValue;
        }
    }
    
    private class MethodNameTransform extends Transform {
        private final String newName;
        MethodNameTransform (String newName) {
            super (ChangeKind.METHOD_NAME);
            this.newName = newName;
            System.err.println("MethodNameTransform new name " + newName);
            if ("void".equals(newName)) Thread.dumpStack();
        }

        protected SimpleRefactoringElementImplementation createElement(MethodInvocationTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new RenameMethodReferenceElement (handle, newName, name, getContext(), file);
        }

        protected SimpleRefactoringElementImplementation createElement(MethodTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new RenameMethodReferenceElement (handle, newName, name, getContext(), file);
        }
        
        public String toString() {
            return super.toString() + " to " + newName;
        }
    }
    
    private class ParameterNameTransform extends Transform {
        private final String newName;
        private final String oldName;
        ParameterNameTransform (String newName, String oldName) {
            super (ChangeKind.PARAM_NAME);
            this.newName = newName;
            this.oldName = oldName;
        }

        protected SimpleRefactoringElementImplementation createElement(MethodInvocationTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new ParameterNameChangeElement(newName, handle, oldName, getContext(), file);
        }

        protected SimpleRefactoringElementImplementation createElement(MethodTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new ParameterNameChangeElement(newName, handle, oldName, getContext(), file);
        }
        
        public String toString() {
            return super.toString() + " to " + newName;
        }
    }
    

    private class ParameterTypeChange extends Transform {
        private final String newName;
        ParameterTypeChange (String newName) {
            super (ChangeKind.PARAM_NAME);
            this.newName = newName;
        }

        protected SimpleRefactoringElementImplementation createElement(MethodInvocationTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new ParameterTypeChangeElement(newName, handle, name, getContext(), file);
        }

        protected SimpleRefactoringElementImplementation createElement(MethodTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new ParameterTypeChangeElement(newName, handle, name, getContext(), file);
        }
        
        public String toString() {
            return super.toString() + " to " + newName;
        }
    }    
    
    private class MethodReturnTypeTransform extends Transform {
        private final String newType;
        MethodReturnTypeTransform (String newType) {
            super (ChangeKind.PARAM_NAME);
            this.newType = newType;
        }

        protected SimpleRefactoringElementImplementation createElement(MethodInvocationTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new MethodReturnTypeChangeElement(newType, handle, name, getContext(), file);
        }

        protected SimpleRefactoringElementImplementation createElement(MethodTree tree, Element element, TreePathHandle handle, TreePath path, String name, FileObject file) {
            return new MethodReturnTypeChangeElement(newType, handle, name, getContext(), file);
        }
        
        public String toString() {
            return super.toString() + " to " + newType;
        }
    }    
}
