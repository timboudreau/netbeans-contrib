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

import org.netbeans.api.java.source.query.ElementReferenceList;
import org.netbeans.api.java.source.query.ReferenceFinder;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import java.util.LinkedList;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.query.QueryEnvironment;
import org.netbeans.api.java.source.transform.Transformer;
import org.netbeans.api.java.source.query.ElementReferenceList;
import org.netbeans.api.java.source.query.ReferenceFinder;
import java.util.EnumSet;
import java.util.Set;
import static org.netbeans.api.java.source.query.UseFinder.*;
import static javax.lang.model.element.ElementKind.*;

/**
 * Minimizes field access modifiers based on project usage.
 */
public class MinimizeFieldAccess extends Transformer<Void,Object>{
    private Types types;
    private ReferenceFinder referenceMap;
    
    { queryDescription = "Minimize Field Access"; }
    
    // properties set by PropertySheetInfo
    private boolean ignorePackagePrivate;
    private boolean ignoreConstants;
    public boolean getIgnorePackagePrivate() {
        return ignorePackagePrivate;
    }
    public void setIgnorePackagePrivate(boolean b) {
        ignorePackagePrivate = b;
    }
    public boolean getIgnoreConstants() {
        return ignoreConstants;
    }
    public void setIgnoreConstants(boolean b) {
        ignoreConstants = b;
    }
    
    @Override
    public void attach(QueryEnvironment env) {
	super.attach(env);
        types = env.getTypes();
    }
    
    @Override
    public void release() {
        super.release();
        types = null;
    }

    @Override
    public void apply() {
        Tree root = getRootNode();
        Set<Element> vars = findElements(root);
        referenceMap = ReferenceFinder.findReferences(env, vars, root);
        apply(root);
        show(result, getQueryDescription());
    }
    
    private Set<Element> findElements(Tree root) {
        final Set<Element> vars = new ListSet();
        root.accept(new TreeScanner<Void,Object>() {
            @Override 
            public Void visitVariable(VariableTree t, Object p) {
                if (isInteresting(t))
                    vars.add(getElement(t));
                return super.visitVariable(t, p);
            }
        }, null);
        return vars;
    }
    
    private boolean isInteresting(VariableTree t) {
        Element sym = getElement(t);
        ModifiersTree mods = t.getModifiers();
        Set<Modifier> flags = mods.getFlags();
        
        // ignore non-instance variables
        if (sym == null || !(sym.getEnclosingElement() instanceof TypeElement))
            return false;  
        TypeElement owner = (TypeElement)sym.getEnclosingElement();
        
        // ignore compiler-generated fields
        if (isSynthetic(t))
            return false;
        
        // ignore enum fields, whose flags are synthetic
        if (owner.getKind() == ElementKind.ENUM)
            return false;  
        
        // ignore fields in anonymous classes, since they aren't accessible
        if (owner.getNestingKind() == NestingKind.ANONYMOUS)
            return false; 
        
        // ignore static constants if possible
        if (ignoreConstants &&
            flags.contains(Modifier.STATIC) && flags.contains(Modifier.FINAL))
            return false;
        
        // ignore non-public fields if possible
        if (ignorePackagePrivate &&
            !flags.contains(Modifier.PUBLIC) && !flags.contains(Modifier.PROTECTED))
            return false;
        return true;
    }
    
    @Override
    public Void visitVariable(VariableTree t, Object p) {
        super.visitVariable(t, p);
        Element sym = getElement(t);
        if (!referenceMap.hasReferences(sym))  // true if variable isn't "interesting"
            return null;
        ElementReferenceList<Element> varRef = referenceMap.get(sym);
        int usage = varRef.getUsage();
        TypeElement owner = (TypeElement)sym.getEnclosingElement();

        ModifiersTree mods = t.getModifiers();
        Set<Modifier> flags = mods.getFlags();
        Set<Modifier> newFlags = EnumSet.noneOf(Modifier.class);
        newFlags.addAll(flags);

        if (flags.contains(Modifier.PUBLIC) && hasPublicUsage(usage)) {
            // see if public usage is limited to subclasses
            boolean onlyProtected = true;
            TypeMirror classType = owner.asType();
            for (Element e : varRef.getReferences())
                if (!types.isSubtype(getClassType(e), classType)) {
                    onlyProtected = false;
                    break;
                }
            if (onlyProtected) {
                newFlags.remove(Modifier.PUBLIC);
                newFlags.add(Modifier.PROTECTED);
            }
        }
        if ((flags.contains(Modifier.PUBLIC) || flags.contains(Modifier.PROTECTED)) 
                && !hasPublicUsage(usage)) {
            newFlags.remove(Modifier.PUBLIC);
            newFlags.remove(Modifier.PROTECTED);
        }
        if (!ignorePackagePrivate && !hasPublicUsage(usage) && !hasPackageUsage(usage)) {
            assert !newFlags.contains(Modifier.PUBLIC) && !newFlags.contains(Modifier.PROTECTED);
            newFlags.add(Modifier.PRIVATE);
        }
        
        if (!flags.equals(newFlags)) {
            ModifiersTree newMods = make.Modifiers(newFlags, mods.getAnnotations());
            changes.rewrite(mods, newMods);
            addResult(sym, newMods, resultNote(flags, newFlags));
        }
        return null;
    }
    
    static TypeMirror getClassType(Element e) {
        while (!(e instanceof TypeElement))
            e = e.getEnclosingElement();
        return e.asType();
    }
    
    static boolean hasPublicUsage(int usage) {
        return (usage & worldMask) != 0;
    }
    
    static boolean hasPackageUsage(int usage) {
        return (usage & packageMask) != 0;
    }
    
    private static final int worldMask =   (SETUSE | GETUSE) << WORLDSHIFT;
    private static final int packageMask = (SETUSE | GETUSE) << PACKAGESHIFT;
    
    static String resultNote(Set<Modifier> oldFlags, Set<Modifier> newFlags) {
        StringBuffer sb = new StringBuffer();
        formatFlags(oldFlags, sb);
        sb.append(" => ");  //NOI18N
        formatFlags(newFlags, sb);
        return sb.toString();
    }
    
    static void formatFlags(Set<Modifier> flags, StringBuffer buf) {
	if (flags.contains(Modifier.PUBLIC)) 
            buf.append("public");
        else if (flags.contains(Modifier.PROTECTED)) 
            buf.append("protected");
        else if (flags.contains(Modifier.PRIVATE)) 
            buf.append("private");
        else
            buf.append("package-private");
    }

    /**
     * Lightweight Set implementation, which is just a linked-list
     * without duplicates.
     */
    static class ListSet extends LinkedList<Element> implements Set<Element> {
        public boolean add(Element e) {
            if (contains(e))
                return false;
            return super.add(e);
        }
    }
}
