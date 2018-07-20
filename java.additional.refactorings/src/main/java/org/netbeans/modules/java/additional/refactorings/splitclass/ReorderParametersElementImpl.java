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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.VariableTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreePathHandle;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Tim
 */
public class ReorderParametersElementImpl extends AbstractParameterChangeElementImpl {
    private List<Swap> oldToNewPositions;
    public ReorderParametersElementImpl(TreePathHandle methodPathHandle, String name, Lookup context, FileObject file, Collection <Swap> oldToNewPositions) {
        super (methodPathHandle, name, context, file);
        this.oldToNewPositions = new ArrayList <Swap> (oldToNewPositions);
        Collections.sort (this.oldToNewPositions);
    }

    public String getText() {
        return "Reorder parameters on " + name;
    }

    protected void modifyArgs(List<ExpressionTree> args, TreeMaker maker) {
        for (Swap swap : oldToNewPositions) {
            swap.swap (args);
        }
    }

    protected void modifyOverrideArgs(List<VariableTree> args, TreeMaker maker) {
        for (Swap swap : oldToNewPositions) {
            swap.swap (args);
        }
    }
}
