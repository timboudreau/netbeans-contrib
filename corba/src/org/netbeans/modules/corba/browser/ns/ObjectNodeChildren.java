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

package org.netbeans.modules.corba.browser.ns;

import java.util.ArrayList;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.netbeans.modules.corba.browser.ns.keys.*;
import org.netbeans.modules.corba.browser.ir.nodes.IRInterfaceDefNode;
/**
 *
 * @author  Tomas Zezula
 */

public class ObjectNodeChildren extends Children.Keys {

    /** Creates new ObjectNodeChildren */
    public ObjectNodeChildren () {
        super ();
    }

    protected void addNotify () {
        this.update ();
    }

    protected void removeNotify () {
        this.setKeys ( new Object[0]);
    }
    
    public void update () {
        NamingServiceNode node = (NamingServiceNode) this.getNode();
        ArrayList keys = new ArrayList ();
        if (node != null) {
            org.omg.CORBA.InterfaceDef interfaceDef = node.getInterface ();
            if (interfaceDef != null) {
                keys.add ( new InterfaceDefKey (interfaceDef));
            }
        }
        this.setKeys (keys);
    }
    
    public Node[] createNodes (Object key) {
        if (key != null && key instanceof ObjectNodeKey) {
           switch (((ObjectNodeKey)key).getType()) {
               case ObjectNodeKey.INTERFACE:
                    return new Node[] { new IRInterfaceDefNode (org.omg.CORBA.InterfaceDefHelper.narrow((org.omg.CORBA.Object)((ObjectNodeKey)key).getValue()))};
           }
        }
        return new Node[0];
    }

}
