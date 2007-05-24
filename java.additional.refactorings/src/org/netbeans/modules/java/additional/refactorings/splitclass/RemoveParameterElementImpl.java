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
import com.sun.source.tree.VariableTree;
import java.util.List;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Tim Boudreau
 */
public class RemoveParameterElementImpl extends AbstractParameterChangeElementImpl {
    private final int paramIndex;
    
    public RemoveParameterElementImpl(TreePathHandle methodPathHandle, int paramIndex, String name, Lookup context, FileObject file) {
        super (methodPathHandle, name, context, file);
        this.paramIndex = paramIndex;
    }

    public String getText() {
        return "Remove parameter " + name + " at position " + paramIndex;
    }

    protected void modifyArgs(List<ExpressionTree> args, TreeMaker maker) {
        args.remove (paramIndex);
    }

    protected void modifyOverrideArgs(List<VariableTree> args, TreeMaker maker) {
        args.remove (paramIndex);
    }
}
