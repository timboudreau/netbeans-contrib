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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.cmds;

import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.query.ElementReferenceList;
import org.netbeans.api.java.source.query.ReferenceFinder;
import org.netbeans.api.java.source.query.QueryEnvironment;
import org.netbeans.api.java.source.transform.Transformer;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.EnumSet;
import java.util.Set;

import static org.netbeans.api.java.source.query.UseFinder.*;

/**
 * Minimizes method access modifiers based on project usage.
 */
public class MinimizeMethodAccess extends Transformer<Void,Object> {
    private Types types;
    private ElementUtilities elements;
    private ReferenceFinder referenceMap;
    
    { queryDescription = "Minimize Method Access"; }

    // property set by PropertySheetInfo
    private boolean convertPackagePrivate = true;
    public boolean getConvertPackagePrivate() {
        return convertPackagePrivate;
    }
    public void setConvertPackagePrivate(boolean b) {
        convertPackagePrivate = b;
    }
    
    @Override
    public void attach(QueryEnvironment env) {
	super.attach(env);
        types = env.getTypes();
        elements = env.getElementUtilities();
    }
    
    @Override
    public void release() {
        super.release();
        types = null;
        elements = null;
    }

    @Override
    public void apply() {
        Tree root = getRootNode();
        Set<Element> methods = findElements(root);
        referenceMap = ReferenceFinder.findReferences(env, methods, root);
        apply(root);
        show(result, getQueryDescription());
    }
    
    private Set<Element> findElements(Tree root) {
        final Set<Element> set = new MinimizeFieldAccess.ListSet();
        root.accept(new TreeScanner<Void,Object>() {
            private ClassTree enclosingClass;
            
            @Override
            public Void visitMethod(MethodTree t, Object p) {
                if (isInteresting(t, enclosingClass))
                    set.add(getElement(t));
                return super.visitMethod(t, p);
            }

            @Override
            public Void visitClass(ClassTree node, Object p) {
                ClassTree oldClass = enclosingClass;
                enclosingClass = node;
                super.visitClass(node, p);
                enclosingClass = oldClass;
                return null;
            }
        }, null);
        return set;
    }
    
    private boolean isInteresting(MethodTree t, ClassTree enclosingClass) {
        // Ignore compiler-generated methods: synthetic and default constructors.
        if (isSynthetic(t) || getPos(t) == getPos(enclosingClass))
            return false;

        ExecutableElement sym = (ExecutableElement)getElement(t);
        if (sym == null)   // ignore unattributed elements, caused by source errors
            return false;
        
        if (sym.getSimpleName().toString().equals("filter") && 
                sym.getEnclosingElement().getSimpleName().toString().equals("ReplaceString"))
            System.out.println();  // elements.implementsMethod() should return true
        
        TypeElement owner = (TypeElement)sym.getEnclosingElement();
        if (owner.getKind() == ElementKind.ENUM)
            return false;  // ignore enum methods, whose flags are synthetic

        if (owner.getKind() == ElementKind.INTERFACE)
            return false;  // ignore methods defined in interfaces

        if (owner.getNestingKind() == NestingKind.ANONYMOUS)
            return false;  // ignore anonymous class methods

        // ignore non-public fields if possible
        ModifiersTree mods = t.getModifiers();
        Set<Modifier> flags = mods.getFlags();
        if (!flags.contains(Modifier.PUBLIC) && 
            !flags.contains(Modifier.PROTECTED) && 
            !convertPackagePrivate)
            return false;

        // ignore public no-arg constructors, which are often invoked by reflection
        if (sym.getKind() == ElementKind.CONSTRUCTOR && 
            flags.contains(Modifier.PUBLIC) && 
            t.getParameters().isEmpty())
            return false;

        // ignore any methods which override their superclass or implement interface methods.
        if (elements.overridesMethod(sym) || elements.implementsMethod(sym))
            return false;
        
        // ignore main methods
        if (flags.contains(Modifier.PUBLIC) && 
            flags.contains(Modifier.STATIC) &&
            "void".equals(t.getReturnType().toString()) &&
            "main".equals(t.getName().toString()))
            return false;

        return true;
    }

    public Void visitMethod(MethodTree t, Object p) {
        super.visitMethod(t, p);
        ExecutableElement sym = (ExecutableElement)getElement(t);
        if (!referenceMap.hasReferences(sym))  // true if method isn't "interesting"
            return null;
        ElementReferenceList<Element> methodRef = referenceMap.get(sym);

        int usage = methodRef.getUsage();
        TypeElement owner = (TypeElement)sym.getEnclosingElement();

        ModifiersTree mods = t.getModifiers();
        Set<Modifier> flags = mods.getFlags();
        Set<Modifier> newFlags = EnumSet.noneOf(Modifier.class);
        newFlags.addAll(flags);
        
        if (sym.getSimpleName().toString().equals("filter") && 
                sym.getEnclosingElement().getSimpleName().toString().equals("ReplaceString"))
            System.out.println(); // shouldn't get here, as this method shouldn't be interesting
        
        if (flags.contains(Modifier.PUBLIC) && MinimizeFieldAccess.hasPublicUsage(usage)) {
            // see if public usage is limited to subclasses
            boolean onlyProtected = true;
            TypeMirror classType = owner.asType();
            for (Element e : methodRef.getReferences())
                if (!types.isSubtype(MinimizeFieldAccess.getClassType(e), classType)) {
                    onlyProtected = false;
                    break;
                }
            if (onlyProtected) {
                newFlags.remove(Modifier.PUBLIC);
                newFlags.add(Modifier.PROTECTED);
            }
        }
        else if ((flags.contains(Modifier.PUBLIC) || 
                  flags.contains(Modifier.PROTECTED)) && 
                 !MinimizeFieldAccess.hasPublicUsage(usage)) {
            newFlags.remove(Modifier.PUBLIC);
            newFlags.remove(Modifier.PROTECTED);
        }
        if (convertPackagePrivate && 
                !newFlags.contains(Modifier.PUBLIC) &&
                !newFlags.contains(Modifier.PROTECTED) &&
                !flags.contains(Modifier.PRIVATE) && 
                !flags.contains(Modifier.ABSTRACT) &&
                !MinimizeFieldAccess.hasPublicUsage(usage) &&
                !MinimizeFieldAccess.hasPackageUsage(usage))
            newFlags.add(Modifier.PRIVATE);
        
        if (!flags.equals(newFlags)) {
            // Make sure overriding methods don't weaken access.
            ExecutableElement parentMethod = elements.getOverriddenMethod(sym);
            if (parentMethod == null || // true if method doesn't override
                    access(newFlags) >= access(parentMethod.getModifiers())) {
                ModifiersTree newMods = make.Modifiers(newFlags, mods.getAnnotations());
                changes.rewrite(mods, newMods);
                addResult(sym, newMods, MinimizeFieldAccess.resultNote(flags, newFlags));
            }
        }
        return null;
    }

    /** Returns a comparable int corresponding to the flags' access level */
    private int access(Set<Modifier> flags) {
        if (flags.contains(Modifier.PUBLIC))
            return 3;
        if (flags.contains(Modifier.PROTECTED))
            return 2;
        return flags.contains(Modifier.PRIVATE) ? 0 : 1;
    }
}
