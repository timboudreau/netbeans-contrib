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

package org.netbeans.modules.corba.browser.ns.keys;

/**
 *
 * @author  Tomas Zezula
 */
public class InterfaceDefKey extends ObjectNodeKey {

    /** Creates new InterfaceDefKey */
    public InterfaceDefKey (org.omg.CORBA.InterfaceDef value) {
        super (INTERFACE, value);
    }

    public boolean equals (Object other) {
        if (! (other instanceof InterfaceDefKey) )
            return false;
        org.omg.CORBA.InterfaceDef otherInterfaceDef = (org.omg.CORBA.InterfaceDef) ((InterfaceDefKey)other).getValue();
        org.omg.CORBA.InterfaceDef thisInterfaceDef = (org.omg.CORBA.InterfaceDef) this.getValue();
        if (otherInterfaceDef == null && thisInterfaceDef == null)
            return true;
        else if (otherInterfaceDef == null && thisInterfaceDef != null)
            return false;
        else if (otherInterfaceDef != null && thisInterfaceDef == null)
            return false;
        else return otherInterfaceDef.name().equals (thisInterfaceDef.name());
    }
    
}
