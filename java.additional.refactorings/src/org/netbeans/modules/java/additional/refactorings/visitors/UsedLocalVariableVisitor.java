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

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.CompilationInfo;

/**
 *
 * @author Tim
 */
public final class UsedLocalVariableVisitor extends TreePathScanner<Void, Set<ParamDesc>> {
    private final CompilationInfo info;
    private final long excludeStart;
    private final long excludeEnd;
    
    public UsedLocalVariableVisitor(CompilationInfo info, long excludeStart, long excludeEnd) {
        this.excludeStart = excludeStart;
        this.excludeEnd = excludeEnd;
        this.info = info;
    }
    
    private HashSet <TypeMirror> types = new HashSet <TypeMirror> ();
    
    private static final Set<ElementKind> VARIABLES = EnumSet.of(ElementKind.EXCEPTION_PARAMETER, ElementKind.PARAMETER, ElementKind.LOCAL_VARIABLE);
    
    @Override
    public Void visitIdentifier(IdentifierTree node, Set<ParamDesc> p) {
        System.err.println("Visit identifier " + node);
        Element el = info.getTrees().getElement(getCurrentPath());
        TreePath elPath = el != null ? info.getTrees().getPath(el) : null;
        SourcePositions positions = info.getTrees().getSourcePositions();
        long start = positions.getStartPosition(info.getCompilationUnit(), node);
        long end = positions.getEndPosition(info.getCompilationUnit(), node);
        boolean include = notInExcludedRange (start, end);
        if (include) {
            if (el != null && elPath != null && VARIABLES.contains(el.getKind())) {
                VariableElement v = (VariableElement) el;

                TypeMirror type = v.asType();
                if (!types.contains(type)) {
                    p.add(new ParamDesc(v));
                    types.add(type);
                }
            }
        } else {
            System.err.println("Excluded " + node + " [" + start + "-" + end + "] selection  " + 
                    excludeStart + "-" + excludeEnd);
        }
        return super.visitIdentifier(node, p);
    }
    
    private boolean notInExcludedRange (long start, long end) {
        return !((start < excludeStart && end < excludeStart) ||
                (start > excludeEnd));
    }
    
    public void pruneLocallyDefined (Set <ParamDesc> desc) {
        for (Iterator <ParamDesc> i = desc.iterator(); i.hasNext();) {
            String s;
            if (locallyDefinedNames.contains (s = i.next().getName().toString())) {
                System.err.println("Pruned " + s);
                i.remove();
            }
        }
    }
    
    Set <String> locallyDefinedNames = new HashSet <String> ();
    
    @Override
    public Void visitVariable(VariableTree tree, Set<ParamDesc> set) {
        System.err.println("Visit variable " + tree);
        SourcePositions positions = info.getTrees().getSourcePositions();
        long start = positions.getStartPosition(info.getCompilationUnit(), tree);
        long end = positions.getEndPosition(info.getCompilationUnit(), tree);
        //Make sure we didn't catch anything like loop variables that are actually
        //defined in the new method
        if (!notInExcludedRange(start, end)) {
            locallyDefinedNames.add (tree.getName().toString());
        } else {
            System.err.println("Variable " + tree.getName() + " is locally defined");
        }
        return super.visitVariable(tree, set);
    }
}
