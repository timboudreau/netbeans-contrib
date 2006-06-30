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

package org.netbeans.modules.corba.idl.node;

import org.openide.nodes.CookieSet;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.netbeans.modules.corba.idl.src.TypeElement;

/**
 * Class IDLStructTypeNode
 *
 * @author Karel Gardas
 */
public class IDLStructTypeNode extends IDLTypeNode {

    public static final String STRUCT_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/struct"; // NOI18N

    public IDLStructTypeNode (TypeElement value) {
        super (value);
        //System.out.println ("IDLStructTypeNode..."); // NOI18N
        setIconBase (STRUCT_ICON_BASE);
    }

}

/*
 * $Log
 * $
 */
