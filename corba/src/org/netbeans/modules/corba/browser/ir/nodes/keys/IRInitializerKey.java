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

package org.netbeans.modules.corba.browser.ir.nodes.keys;

import org.omg.CORBA.*;
/**
 *
 * @author  tzezula
 * @version 
 */
public class IRInitializerKey extends IRAbstractKey {

    public Initializer initializer;
    
    /** Creates new IRInitializerKey */
    public IRInitializerKey(Initializer initializer) {
        this.initializer = initializer;
    }
    
    
    public boolean equals (java.lang.Object other) {
        if ( ! (other instanceof IRInitializerKey))
            return false;
        IRInitializerKey otherInit = (IRInitializerKey) other;
        if (! this.initializer.name.equals(otherInit.initializer.name))
            return false;
        if (this.initializer.members.length != otherInit.initializer.members.length)
            return false;
        for (int i=0; i< this.initializer.members.length; i++) {
            if (!this.initializer.members[i].name.equals (otherInit.initializer.members[i].name))
                return false;
            if (!this.initializer.members[i].type.equal (otherInit.initializer.members[i].type))
                return false;
        }
        return true;
    }
    
    public int hashCode () {
        return ( this.initializer.name.hashCode() ^ this.initializer.members.length);
    }

}
