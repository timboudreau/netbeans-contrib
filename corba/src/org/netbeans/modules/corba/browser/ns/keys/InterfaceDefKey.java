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
