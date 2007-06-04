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
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.TreePathHandle;

/**
 * Object provided to a ParameterScanner to provide info about the user's
 * choices and a place to put data about what parameters not to rename,
 * the list of methods found, etc.
 *
 * @author Tim Boudreau
 */
public final class ParameterChangeContext {
    public final RequestedParameterChanges mods;
    public final ChangeData changeData;
    public ParameterChangeContext (RequestedParameterChanges mods, ScanContext scan) {
        this.mods = mods;
        this.changeData = new ChangeData (scan);
    }
        
    /**
     * Stores data gathered during scans of methods that override a method whose
     * signature is to be changed.
     */ 
    public static final class ChangeData {
        private final Map <ElementHandle<ExecutableElement>, Set<Integer>> skips = 
                new HashMap <ElementHandle<ExecutableElement>, Set<Integer>>();
        private final Map <ElementHandle<ExecutableElement>, Set <String>> 
                usedVariableNames = new HashMap<ElementHandle<ExecutableElement>, 
                Set<String>>(); 
        private final Map <ElementHandle<ExecutableElement>, Set <TreePathHandle>>
                memberSelectsThatNeedQualifyingAfterParamChanges = 
                new HashMap<ElementHandle<ExecutableElement>, Set<TreePathHandle>>();
        
        private ScanContext scanContext;
        ChangeData (ScanContext scanContext) {
            this.scanContext = scanContext;
        }
        
        /**
         * Mark the parameter being scanned as needing to be skipped - it should
         * not be renamed on this particular method.
         * @param handle A handle to the override method 
         */ 
        void skipCurrentParameter (ElementHandle<ExecutableElement> handle) {
            int ix = getParameterIndex();
            Set <Integer> ints = skips.get (ix);
            if (ints == null) {
                ints = new HashSet <Integer> ();
            }
            ints.add (ix);
        }
        
        private Map <ExecutableElement, Set <Integer>> resolveSkips (CompilationInfo info) {
            Map <ExecutableElement, Set <Integer>> result = new HashMap <ExecutableElement, Set <Integer>> ();
            for (Map.Entry <ElementHandle<ExecutableElement>, Set<Integer>> e : skips.entrySet()) {
                ElementHandle<ExecutableElement> key = e.getKey();
                Set <Integer> val = e.getValue();
                ExecutableElement el = key.resolve(info);
                result.put (el, val);
            }
            return result;
        }
        
        private Map <ExecutableElement, Set <String>> resolveNames (CompilationInfo info) {
            Map <ExecutableElement, Set <String>> result = new HashMap <ExecutableElement, Set <String>> ();
            for (Map.Entry <ElementHandle<ExecutableElement>, Set<String>> e : usedVariableNames.entrySet()) {
                ElementHandle<ExecutableElement> key = e.getKey();
                Set <String> val = e.getValue();
                ExecutableElement el = key.resolve(info);
                result.put (el, val);
            }
            return result;
        }
        
        private Map <ExecutableElement, Set <Tree>> resolveRequalifies (CompilationInfo info) {
            Map <ExecutableElement, Set <Tree>> result = new HashMap <ExecutableElement, Set <Tree>> ();
            for (Map.Entry <ElementHandle<ExecutableElement>, Set<TreePathHandle>> e : memberSelectsThatNeedQualifyingAfterParamChanges.entrySet()) {
                ElementHandle<ExecutableElement> key = e.getKey();
                Set <TreePathHandle> val = e.getValue();
                ExecutableElement el = key.resolve(info);
                Set <Tree> nuvals = new HashSet <Tree> (val.size());
                for (TreePathHandle h : val) {
                    TreePath path = h.resolve(info);
                    Tree tree = path.getLeaf();
                    nuvals.add(tree);
                }
                result.put (el, nuvals);
            }
            return result;
        }
        
        void addUsedVariableName (ElementHandle <ExecutableElement> method, String varName) {
            Set <String> vNamesForThisMethod = usedVariableNames.get (method);
            if (vNamesForThisMethod == null) {
                vNamesForThisMethod = new HashSet <String> ();
                usedVariableNames.put (method, vNamesForThisMethod);
            }
            vNamesForThisMethod.add(varName);
        }
        
        public boolean isConflict (ElementHandle <ExecutableElement> method, int paramIndex, CompilationInfo info) {
            ExecutableElement theMethod = method.resolve (info);
            Map <ExecutableElement, Set<Integer>> map = resolveSkips(info);
            Set <Integer> indicesOfParamsNotToRename = map.get(theMethod);
            return indicesOfParamsNotToRename == null ? false : indicesOfParamsNotToRename.contains(paramIndex);
        }
        
        public boolean isConflict (ElementHandle <ExecutableElement> method, String paramName, CompilationInfo info) {
            ExecutableElement theMethod = method.resolve (info);
            Map <ExecutableElement, Set<String>> map = resolveNames (info);
            Set <String> names = map.get (theMethod);
            return names == null ? false : names.contains (paramName);
        }
        
        private Set<String> getFatalConflicts (ElementHandle<ExecutableElement> method, CompilationInfo info, RequestedParameterChanges mods) {
            Set <String> newParameterNames = mods.getNewParameterNames();
            Set <String> result = new HashSet <String> ();
            if (!newParameterNames.isEmpty()) {
                Map <ExecutableElement, Set <String>> namesUsedInMethods = resolveNames (info);
                Set <String> namesUsedInThisMethod = namesUsedInMethods.get (method.resolve(info));
                if (namesUsedInThisMethod != null) {
                    namesUsedInThisMethod = new HashSet <String> (namesUsedInThisMethod); //defensive copy
                    result = new HashSet <String> ();
                    for (String p : newParameterNames) {
                        if (namesUsedInThisMethod.contains(p)) {
                            result.add (p);
                        }
                    }
                }
            }
            return result;
        }
        
        public Map <ElementHandle<ExecutableElement>, Set <String>> getAllFatalConflicts (Iterable<ElementHandle<ExecutableElement>> methods, CompilationInfo info, RequestedParameterChanges mods) {
            if (methods == null || !methods.iterator().hasNext()){
                return Collections.<ElementHandle<ExecutableElement>, Set <String>>emptyMap();
            }
            Map <ElementHandle<ExecutableElement>, Set <String>> result = new HashMap <ElementHandle<ExecutableElement>, Set <String>> ();
            for (ElementHandle<ExecutableElement> elem : methods) {
                Set <String> conflicts = getFatalConflicts (elem, info, mods);
                if (!conflicts.isEmpty()) {
                    result.put (elem, conflicts);
                }
            }
            return result;
        }
        
        int getParameterIndex() {
            assert scanContext != null : "Already sealed"; //NOI18N
            return scanContext.getParameterIndex();
        }
        
        ExecutableElement getCurrentMethodElement() {
            assert scanContext != null : "Already sealed"; //NOI18N
            return scanContext.getCurrentMethodElement();
        }
        
        public Set <TreePathHandle> getMemberSelectsThatNeedRequalifying (ExecutableElement overriddingMethod, CompilationInfo info) {
            ElementHandle<ExecutableElement> handle = ElementHandle.<ExecutableElement>create(overriddingMethod);
            Set <TreePathHandle> result = memberSelectsThatNeedQualifyingAfterParamChanges.get(handle);
            return result == null ? Collections.<TreePathHandle>emptySet() : result;
        }
        
        /** Call when data done being added, so a reference to the compile tree
         * used for scanning is not held */
        public void seal () {
            scanContext = null;
        }
        
        public CompilationUnitTree getCompilationUnit() {
            return scanContext.getCompilationUnit();
        }
        
        public ElementHandle <ExecutableElement> getHandleToCurrentMethodElement() {
            return ElementHandle.create(scanContext.getCurrentMethodElement());
        }
        
        public void addMemberSelectThatNeedsRequalifying (TreePathHandle pathToMemberSelect, ElementHandle<ExecutableElement> on) {
            System.err.println("Added member that needs requalifying: " + pathToMemberSelect);
            Set <TreePathHandle> handles = memberSelectsThatNeedQualifyingAfterParamChanges.get(on);
            if (handles == null) {
                handles = new HashSet <TreePathHandle> ();
                memberSelectsThatNeedQualifyingAfterParamChanges.put (on, handles);
            }
            handles.add (pathToMemberSelect);
        }
        
        public String toString (CompilationController cc, RequestedParameterChanges mods) {
            StringBuilder sb = new StringBuilder();
            Map <ExecutableElement, Set <String>> names = resolveNames (cc);
            Map <ExecutableElement, Set <Integer>> skips = resolveSkips (cc);
            Map <ExecutableElement, Set <Tree>> requalify = resolveRequalifies(cc);
            System.err.println("NAMES:");
            for (ExecutableElement el : names.keySet()) {
                TypeElement te = cc.getElementUtilities().enclosingTypeElement(el);
                System.err.println(te.getQualifiedName().toString() + '.' + el.getSimpleName().toString() + " uses the following variables:");
                Set <String> nm = names.get(el);
                for (String s : nm) {
                    System.err.println("  " + s);
                }
            }
            System.err.println("PARAMS TO SKIP:");
            for (ExecutableElement el : skips.keySet()) {
                TypeElement te = cc.getElementUtilities().enclosingTypeElement(el);
                System.err.println(te.getQualifiedName().toString() + '.' +el.getSimpleName().toString() + " will skip the following parameters:");
                Set <Integer> ss = skips.get (el);
                System.err.println("  " + ss);
            }
            System.err.println("PARAMS TO REQUALIFY:");
            for (ExecutableElement el : requalify.keySet()) {
                TypeElement te = cc.getElementUtilities().enclosingTypeElement(el);
                System.err.println(te.getQualifiedName().toString() + '.' + el.getSimpleName().toString() + " will requalify the following parameters:");
                Set <Tree> reqs = requalify.get (el);
                for (Tree tree : reqs) {
                    System.err.println("  " + tree.getKind() + ": " + tree);
                }
            }
            return sb.toString();
        }
        
        public CompilationInfo getCompilationInfo() {
            return scanContext.getCompilationInfo();
        }
    }
    
    public interface ScanContext {
        int getParameterIndex();
        ExecutableElement getCurrentMethodElement();
        CompilationUnitTree getCompilationUnit();
        CompilationInfo getCompilationInfo();
    }
}
