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

import org.netbeans.api.java.source.query.UseFinder;
import org.netbeans.api.java.source.query.Query;
import org.netbeans.api.java.source.query.SearchResult;
import org.netbeans.api.java.source.query.QueryEnvironment;
import org.netbeans.api.java.source.query.Finder;
import org.netbeans.api.java.source.query.SetUseFinder;
import org.netbeans.api.java.source.query.UseFinder;
import com.sun.source.tree.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;
import java.util.Set;

public class FindUnusedVariables extends Query<Void,Object> {
    private static final String serialVersionUIDName = "serialVersionUID";
    PrimitiveType longType;
    TypeMirror serializableType;
    Types types;
    
    { queryDescription = "Unused Public Variables"; }
    
    /** user-set property */
    public boolean ignoreStatics;
    
    public static final int FOO = 1;
    
    @Override
    public void attach(QueryEnvironment env) {
        super.attach(env);
        longType = env.getTypes().getPrimitiveType(TypeKind.LONG);
	serializableType = 
            env.getElements().getTypeElement("java.io.Serializable").asType();
        types = env.getTypes();
    }
    
    @Override
    public void release() {
        super.release();
        longType = null;
        serializableType = null;
        types = null;
    }

    @Override
    public void apply(Tree t) {
        SearchResult allPublic = new Finder(queryDescription, this) {
            @Override
            public Void visitVariable(VariableTree tree, Object p) {
                super.visitVariable(tree, p);
                if (shouldTest(tree))
                    add(getElement(tree), tree, null, 0);
                return null;
            }
            @Override
            public Void visitMethod(MethodTree tree, Object p) {
                // ignore parameters
                scan(tree.getBody(), p);
                return null;
            }
            @Override
            public Void visitCatch(CatchTree tree, Object p) {
                // ignore parameters
                scan(tree.getBlock(), p);
                return null;
            }
        }.find(t);
        SetUseFinder suf = new SetUseFinder(env) {
            protected boolean acceptable(Element sym, int flags) {
                // return true if there are no world, package, or class uses.
                return (flags & ALL_USES) == 0;
            }
        };
        suf.computeSummary(allPublic, env.getRootNode());
        setResult(allPublic);
    }
    
    static final int ALL_USES = 
            UseFinder.GETUSE | 
            UseFinder.GETUSE << UseFinder.PACKAGESHIFT |
            UseFinder.GETUSE << UseFinder.CLASSSHIFT;
    
    private boolean shouldTest(VariableTree tree) {
        boolean ignore = ignoreStatics && isConstant(tree);
        return !ignore && !isSerialVersionUID(tree) && !isEnum(tree);
    }
    
    private boolean isEnum(VariableTree tree) {
	return getElement(tree).getKind() == ElementKind.ENUM_CONSTANT;
    }

    private boolean isSerialVersionUID(VariableTree tree) {
        Element sym = getElement(tree);
        return serialVersionUIDName.equals(tree.getName().toString()) && 
            sym.asType().equals(longType) && 
            isConstantField(tree) &&
	    isClassSerializable(sym.getEnclosingElement());
    }
    
    private boolean isClassSerializable(Element sym) {
	if (sym instanceof TypeElement)
            return types.isAssignable(sym.asType(), serializableType);
	return false;
    }

    private boolean isConstantField(VariableTree tree) {
        Set<Modifier> flags = tree.getModifiers().getFlags();
        boolean isStaticFinal = 
            flags.contains(Modifier.STATIC) && flags.contains(Modifier.FINAL);
        Element owner = elements.enclosingTypeElement(getElement(tree));
        boolean isInterfaceField = owner.getKind() == ElementKind.INTERFACE;
        return isStaticFinal || isInterfaceField;
    }
}
