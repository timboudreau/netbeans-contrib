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
package org.netbeans.modules.java.additional.refactorings.splitclass;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public class AddParameterElementImpl extends AbstractParameterChangeElementImpl {
    private final int paramIndex;
    private final String defaultValue;
    private final String paramName;
    private final String type;
    
    public AddParameterElementImpl(TreePathHandle methodPathHandle, String type, int paramIndex, String name, String defaultValue, Lookup context, FileObject file, String paramName) {
        super (methodPathHandle, name, context, file);
        this.paramIndex = paramIndex;
        this.defaultValue = defaultValue;
        this.paramName = paramName;
        this.type = type;
    }

    public String getText() {
        return "Add parameter " + name + " at position " + paramIndex;
    }

    protected void modifyArgs(List<ExpressionTree> args, TreeMaker maker) {
        LiteralTree litTree = maker.Literal(defaultValue);
        args.add(paramIndex, litTree);
    }

    protected void modifyOverrideArgs(List<VariableTree> args, TreeMaker maker) {
        Tree typeTree = maker.Identifier(type);
        ModifiersTree mods = maker.Modifiers(Collections.<Modifier>emptySet());
        VariableTree nue = maker.Variable(mods, paramName, typeTree, null);
        args.add(paramIndex, nue);
    }
}
