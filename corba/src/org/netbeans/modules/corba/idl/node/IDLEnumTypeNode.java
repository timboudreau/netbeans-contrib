/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.idl.node;

import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.netbeans.modules.corba.idl.src.TypeElement;

/**
 * Class IDLEnumTypeNode
 *
 * @author Karel Gardas
 */
public class IDLEnumTypeNode extends IDLTypeNode {

    private static final String ENUM_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/enum";

    public IDLEnumTypeNode (TypeElement value) {
        super (value);
        setIconBase (ENUM_ICON_BASE);
    }

}

/*
 * $Log
 * $
 */
