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

import org.netbeans.api.java.source.query.QueryEnvironment;
import org.netbeans.api.java.source.transform.Transformer;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.VariableTree;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Add missing java.lang.Deprecated annotations.
 */
public class AddDeprecateds extends Transformer<Void,Object> {
    TypeMirror deprecatedType;
        
    { queryDescription = "Deprecated members"; }
    
    @Override
    public void attach(QueryEnvironment env) {
	super.attach(env);
	deprecatedType = env.getElements().getTypeElement("java.lang.Deprecated").asType();
    }
    
    @Override
    public void release() {
        super.release();
        deprecatedType = null;
    }
    
    @Override
    public Void visitMethod(MethodTree tree, Object p) {
	super.visitMethod(tree, p);
        Element element = getElement(tree);
	if (element != null && isDeprecated(element))
            addDeprecatedAnnotation(element, tree.getModifiers());
        return null;
    }
    
    @Override
    public Void visitVariable(VariableTree tree, Object p) {
	super.visitVariable(tree, p);
        Element element = getElement(tree);
	if (element != null && isDeprecated(element))
            addDeprecatedAnnotation(element, tree.getModifiers());
        return null;
    }
    
    private void addDeprecatedAnnotation(Element element, ModifiersTree mods) {
        if (!hasAnnotation(mods, deprecatedType)) {
	    ModifiersTree newMods = addAnnotation(mods, (DeclaredType)deprecatedType);
	    changes.rewrite(mods, newMods);
	    addResult(newMods);
	}
    }
}
