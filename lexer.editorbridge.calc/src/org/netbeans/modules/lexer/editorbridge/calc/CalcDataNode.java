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

package org.netbeans.modules.lexer.editorbridge.calc;

import org.openide.actions.OpenAction;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;

/**
 * Node that represents Calc data object.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */
public class CalcDataNode extends DataNode {

    public CalcDataNode(DataObject dobj) {
        super(dobj, Children.LEAF);

        setIconBase(CalcDataObject.ICON_BASE);
    }

    public SystemAction getDefaultAction() {
        SystemAction result = super.getDefaultAction();
        return (result == null)
            ? SystemAction.get(OpenAction.class)
            : result;
    }

}
