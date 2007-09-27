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
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
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
        private final Map <ElementHandle<ExecutableElement>, Set <RequalificationEntry>>
                memberSelectsThatNeedQualifyingAfterParamChanges = 
                new HashMap<ElementHandle<ExecutableElement>, Set<RequalificationEntry>>();
        
        public ScanContext scanContext; //XXX should be in same package
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
            for (Map.Entry <ElementHandle<ExecutableElement>, Set<RequalificationEntry>> e : memberSelectsThatNeedQualifyingAfterParamChanges.entrySet()) {
                ElementHandle<ExecutableElement> key = e.getKey();
                if (key == null) continue; //XXX unit tests
                Set <RequalificationEntry> val = e.getValue();
                ExecutableElement el = key.resolve(info);
                Set <Tree> nuvals = new HashSet <Tree> (val.size());
                for (RequalificationEntry entry : val) {
                    TreePath path = entry.pathToMemberSelect.resolve(info);
                    Tree tree = path.getLeaf();
                    nuvals.add(tree);
                }
                result.put (el, nuvals);
            }
            return result;
        }
        
        private Map <ExecutableElement, Set <RequalificationEntry>> resolveRequalifiers (CompilationInfo info) {
            //XXX this is fairly inefficient - could cache the result in a WeakHashMap keyed on the
            //CompilationInfo.  Several similar methods here that can do this
            //kind of iteration repeatedly.
            Map <ExecutableElement, Set <RequalificationEntry>> result = new HashMap <ExecutableElement, Set <RequalificationEntry>> ();
            for (Map.Entry <ElementHandle<ExecutableElement>, Set<RequalificationEntry>> e : memberSelectsThatNeedQualifyingAfterParamChanges.entrySet()) {
                ElementHandle<ExecutableElement> key = e.getKey();
                if (key == null) continue; //XXX unit tests
                Set <RequalificationEntry> val = e.getValue();
                ExecutableElement el = key.resolve(info);
                Set <RequalificationEntry> nuvals = new HashSet <RequalificationEntry> (val.size());
                for (RequalificationEntry entry : val) {
                    nuvals.add(entry);
                }
                result.put (el, nuvals);
            }
            return result;
        }
        
        public String getQualifierFor (ExecutableElement on, TreePathHandle handle, CompilationInfo info) {
            Map <ExecutableElement, Set <RequalificationEntry>> map = resolveRequalifiers (info);
            Set <RequalificationEntry> entries = map.get (on);
            if (entries != null) {
                TreePath path = handle.resolve(info);
                for (RequalificationEntry entry : entries) {
                    TreePath other = entry.pathToMemberSelect.resolve (info);
                    if (other != null && path.getLeaf().equals(other.getLeaf())) {
                        return entry.qualifier;
                    }
                }
            }
            return null;
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
        
        public Set <TreePathHandle> getMemberSelectsThatNeedRequalifying (ExecutableElement overridingMethod, CompilationInfo info) {
            Set <RequalificationEntry> entries = getRequalifySetFor (overridingMethod, info);
            Set <TreePathHandle> result = null;;
            if (entries != null) {
                result = new HashSet <TreePathHandle>(entries.size());
                for (RequalificationEntry entry : entries) {
                    result.add (entry.pathToMemberSelect);
                }
            }
            return result == null ? Collections.<TreePathHandle>emptySet() : result;
        }
        
        private Set <RequalificationEntry> getRequalifySetFor (ExecutableElement method, CompilationInfo info) {
            Set <RequalificationEntry> result = null;
            for (Map.Entry<ElementHandle<ExecutableElement>, Set<RequalificationEntry>> entry : memberSelectsThatNeedQualifyingAfterParamChanges.entrySet()) {
                ElementHandle<ExecutableElement> el = entry.getKey();
                Set <RequalificationEntry> set = entry.getValue();
                Element e = el.resolve(info);
                if (method.equals(e)) {
                    result = set;
                }
            }
            return result;
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

        public void addMemberSelectThatNeedsRequalifying (TreePathHandle pathToMemberSelect, ElementHandle<ExecutableElement> on, CompilationInfo info, String qualifier) {
            assert on != null;
            assert info != null;
            assert pathToMemberSelect != null;
            addMemberSelectThatNeedsRequalifying(pathToMemberSelect, on.resolve(info), info, qualifier);
        }
        
        public void addMemberSelectThatNeedsRequalifying (TreePathHandle pathToMemberSelect, ExecutableElement on, CompilationInfo info, String qualifier) {
            System.err.println("Added member that needs requalifying: " + pathToMemberSelect + " on " + on);
            Set <RequalificationEntry> entries = getRequalifySetFor(on, info);
            if (entries == null) {
                entries = new HashSet <RequalificationEntry> ();
                ElementHandle <ExecutableElement> handle = ElementHandle.<ExecutableElement>create(on);
                memberSelectsThatNeedQualifyingAfterParamChanges.put (handle, entries);
            }
            RequalificationEntry entry = new RequalificationEntry (pathToMemberSelect, qualifier);
            entries.add (entry);
        }
        
        private static class RequalificationEntry {
            final String qualifier;
            final TreePathHandle pathToMemberSelect;
            RequalificationEntry (TreePathHandle pathToMemberSelect, String qualifier) {
                this.qualifier = qualifier;
                this.pathToMemberSelect = pathToMemberSelect;
            }
        }
        
        public String toString (CompilationController cc, RequestedParameterChanges mods) {
            StringBuilder sb = new StringBuilder();
            Map <ExecutableElement, Set <String>> names = resolveNames (cc);
            Map <ExecutableElement, Set <Integer>> skips = resolveSkips (cc);
            Map <ExecutableElement, Set <Tree>> requalify = resolveRequalifies(cc);
            sb.append("NAMES:\n");
            for (ExecutableElement el : names.keySet()) {
                TypeElement te = cc.getElementUtilities().enclosingTypeElement(el);
                sb.append(te.getQualifiedName().toString() + '.' + el.getSimpleName().toString() + " uses the following variables:");
                Set <String> nm = names.get(el);
                for (String s : nm) {
                    sb.append("  " + s);
                }
                sb.append ("\n");
            }
            sb.append("PARAMS TO SKIP:\n");
            for (ExecutableElement el : skips.keySet()) {
                TypeElement te = cc.getElementUtilities().enclosingTypeElement(el);
                sb.append(te.getQualifiedName().toString() + '.' +el.getSimpleName().toString() + " will skip the following parameters:");
                Set <Integer> ss = skips.get (el);
                sb.append("  ");
                sb.append (ss);
                sb.append ("\n");
            }
            sb.append("PARAMS TO REQUALIFY:\n");
            for (ExecutableElement el : requalify.keySet()) {
                TypeElement te = cc.getElementUtilities().enclosingTypeElement(el);
                sb.append(te.getQualifiedName().toString() + '.' + el.getSimpleName().toString() + " will requalify the following parameters:");
                Set <Tree> reqs = requalify.get (el);
                for (Tree tree : reqs) {
                    sb.append("  " + tree.getKind() + ": " + tree);
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
        void setCompilationInfo(CompilationInfo info);
    }
}
