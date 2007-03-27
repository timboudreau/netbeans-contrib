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
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import org.netbeans.api.jackpot.ConversionOperations;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreeMaker;

/**
 * Add missing java.lang.Deprecated annotations.
 */
public class AddDeprecateds extends TreePathTransformer<Void,Object> {
    Elements elements;
    TreeMaker make;
    Trees trees;
    TypeElement deprecated;
    ConversionOperations ops;
    
    @Override
    public void attach(CompilationInfo info) {
	super.attach(info);
        elements = info.getElements();
        trees = info.getTrees();
	deprecated = elements.getTypeElement("java.lang.Deprecated");
        make = getWorkingCopy().getTreeMaker();
        ops = new ConversionOperations(getWorkingCopy());
    }
    
    @Override
    public void release() {
        super.release();
        elements = null;
        make = null;
        trees = null;
        deprecated = null;
        ops = null;
    }
    
    /**
     * Check each method to see if it is deprecated, and if so
     * add a @Deprecated annotation if defined using a Javadoc tag.
     * 
     * @param tree the method to inspect
     * @param p unused parameter
     * @return null
     */
    @Override
    public Void visitMethod(MethodTree tree, Object p) {
	super.visitMethod(tree, p);
        Element element = trees.getElement(getCurrentPath());
	if (element != null && elements.isDeprecated(element))
            addDeprecatedAnnotation(element, tree.getModifiers());
        return null;
    }
    
    /**
     * Check each variable to see if it is deprecated, and if so
     * add a @Deprecated annotation if defined using a Javadoc tag.
     * 
     * @param tree the method to inspect
     * @param p unused parameter
     * @return null
     */
    @Override
    public Void visitVariable(VariableTree tree, Object p) {
	super.visitVariable(tree, p);
        Element element = trees.getElement(getCurrentPath());
	if (element != null && elements.isDeprecated(element))
            addDeprecatedAnnotation(element, tree.getModifiers());
        return null;
    }
    
    private void addDeprecatedAnnotation(Element element, ModifiersTree modifiers) {
        if (needsAnnotation(element, deprecated, elements)) {
            List<? extends ExpressionTree> args = Collections.emptyList();
            AnnotationTree ann = make.Annotation(make.QualIdent(deprecated), args);
            List<AnnotationTree> newAnns = new ArrayList<AnnotationTree>();
            newAnns.addAll(modifiers.getAnnotations());
            newAnns.add(ann);
            ModifiersTree newMods = make.Modifiers(modifiers, newAnns);
            ops.copyComments(modifiers, newMods);
	    addChange(new TreePath(getCurrentPath(), modifiers), newMods);
	}
    }
    
    static boolean needsAnnotation(Element element, TypeElement annotation, Elements elements) {
        for (AnnotationMirror a : elements.getAllAnnotationMirrors(element))
            if (a.getAnnotationType().asElement().equals(annotation))
                return false;
        return true;
    }
}
