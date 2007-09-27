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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
        if (element != null && 
                (elementUtils.overridesMethod(element) ||
                 elementUtils.implementsMethod(element)) &&
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
