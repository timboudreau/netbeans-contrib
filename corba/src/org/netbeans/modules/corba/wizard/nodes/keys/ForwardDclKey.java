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

package org.netbeans.modules.corba.wizard.nodes.keys;

/**
 *
 * @author  tzezula
 * @version 
 */
public class ForwardDclKey extends NamedKey {
    
    private boolean intf;

    /** Creates new FrowardDclKey */
    public ForwardDclKey(int kind, String name, boolean intf) {
        super (kind, name);
        this.intf = intf;
    }
    
    public void setInterface (boolean intf) {
        this.intf = intf;
    }
    
    public boolean isInterface () {
        return intf;
    }
    
    public int hashCode () {
        return this.kind();
    }
    
    public boolean equals (Object other) {
        if (!(other instanceof ForwardDclKey))
            return false;
        if (((ForwardDclKey)other).kind() != this.kind())
            return false;
        if (!((ForwardDclKey)other).getName().equals(this.getName()))
            return false;
        if (((ForwardDclKey)other).isInterface() != this.isInterface())
            return false;
        return true;
    }

}
