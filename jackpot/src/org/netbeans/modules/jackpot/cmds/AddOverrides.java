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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Add java.lang.Override annotations to those elements that do override
 * their parent class's declaration.
 */
public class AddOverrides extends Transformer<Void,Object> {
    TypeMirror overrideType;
        
    { queryDescription = "Overriding members"; }
    
    @Override
    public void attach(QueryEnvironment env) {
	super.attach(env);
	overrideType = env.getElements().getTypeElement("java.lang.Override").asType();
    }
    
    @Override
    public void release() {
        super.release();
        overrideType = null;
    }
    
    @Override
    public Void visitMethod(MethodTree tree, Object p) {
	super.visitMethod(tree, p);
        ExecutableElement ee = (ExecutableElement)getElement(tree);
        ModifiersTree mods = tree.getModifiers();
	if (ee != null && env.getElementUtilities().overridesMethod(ee) && !hasAnnotation(tree.getModifiers(), overrideType)) {
	    ModifiersTree newMods = addAnnotation(mods, (DeclaredType)overrideType);
	    changes.rewrite(mods, newMods);
	    addResult(newMods);
	}
        return null;
    }
}
