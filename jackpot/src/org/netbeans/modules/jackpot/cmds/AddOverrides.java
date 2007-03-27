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

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.netbeans.api.jackpot.ConversionOperations;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.TreeMaker;

/**
 * Add java.lang.Override annotations to those elements that do override
 * their parent class's declaration.
 */
public class AddOverrides extends TreePathTransformer<Void,Object>  {
    Elements elements;
    ElementUtilities elementUtils;
    TreeMaker make;
    Trees trees;
    ConversionOperations ops;
    TypeElement override;
    
    @Override
    public void attach(CompilationInfo info) {
	super.attach(info);
        elements = info.getElements();
        elementUtils = info.getElementUtilities();
        trees = info.getTrees();
	override = elements.getTypeElement("java.lang.Override");
        make = getWorkingCopy().getTreeMaker();
        ops = new ConversionOperations(getWorkingCopy());
    }
    
    @Override
    public void release() {
        super.release();
        elements = null;
        make = null;
        trees = null;
        ops = null;
        override = null;
    }
    
    /**
     * Check each method to see if it overrides a superclass method, and if so
     * add an @Override annotation.
     * 
     * @param tree the method to inspect
     * @param p unused parameter
     * @return null
     */
    @Override
    public Void visitMethod(MethodTree tree, Object p) {
	super.visitMethod(tree, p);
        ExecutableElement element = 
            (ExecutableElement)trees.getElement(getCurrentPath());
        if (element != null && elementUtils.overridesMethod(element) &&
                AddDeprecateds.needsAnnotation(element, override, elements))
            addOverridesAnnotation(element, tree.getModifiers());
        return null;
    }
    
    private void addOverridesAnnotation(ExecutableElement element, ModifiersTree modifiers) {
        List<? extends ExpressionTree> args = Collections.emptyList();
        AnnotationTree ann = make.Annotation(make.QualIdent(override), args);
        List<AnnotationTree> newAnns = new ArrayList<AnnotationTree>();
        newAnns.addAll(modifiers.getAnnotations());
        newAnns.add(ann);
        ModifiersTree newMods = make.Modifiers(modifiers, newAnns);
        ops.copyComments(modifiers, newMods);
        addChange(new TreePath(getCurrentPath(), modifiers), newMods);
    }
}
