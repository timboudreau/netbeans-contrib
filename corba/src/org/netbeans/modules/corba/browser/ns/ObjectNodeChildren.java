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
