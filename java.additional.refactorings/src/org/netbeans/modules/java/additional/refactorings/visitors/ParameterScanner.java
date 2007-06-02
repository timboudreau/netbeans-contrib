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

import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;
import javax.lang.model.element.ExecutableElement;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.modules.java.additional.refactorings.visitors.ParameterChangeContext.ChangeData;

/**
 * Is given a set of data about the changes the user has requested and scans
 * parameter trees, marking those that should be skipped, either because of
 * name conflicts or the policy the user has specified.
 * 
 * @author Tim Boudreau
 */
public class ParameterScanner extends TreeScanner <Void, ParameterChangeContext> {
    public ParameterScanner () {
    }
    
    @Override
    public Void visitVariable(VariableTree tree, ParameterChangeContext ctx) {
        Trees trees = ctx.changeData.getCompilationInfo().getTrees();
        ElementUtilities elUtils = ctx.changeData.getCompilationInfo().getElementUtilities();
        String name = tree.getName().toString();
        RequestedParameterChanges pendingChanges = ctx.mods;
        ChangeData data = ctx.changeData;
        if (pendingChanges.isNewOrChangedParameterName(name)) {
            ExecutableElement method = data.getCurrentMethodElement();
            boolean skip;
            switch (pendingChanges.getPolicy()) {
                case RENAME_IF_SAME :
                    int ix = data.getParameterIndex();
                    skip = !pendingChanges.matchesOriginalParameterName(ix, name);
                    break;
                case RENAME_UNLESS_CONFLICT :
                    skip = pendingChanges.isNewParameterName(name);
                    break;
                default :
                    throw new AssertionError();
            }
            if (skip) {
                System.err.println("Will skip " + name + " in " + tree.getName());
                data.skipCurrentParameter(ElementHandle.create(method));
            }
        }
        return super.visitVariable(tree, ctx);
    }
}
