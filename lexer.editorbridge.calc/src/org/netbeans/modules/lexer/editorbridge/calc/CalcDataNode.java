/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
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
