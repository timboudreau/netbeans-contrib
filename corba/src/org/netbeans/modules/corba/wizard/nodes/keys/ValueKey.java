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
public class ValueKey extends NamedKey {

    String type;
    boolean pub;
    
    /** Creates new Value */
    public ValueKey(int kind, String name, String type, boolean pub) {
        super (kind, name);
        this.type = type;
        this.pub = pub;
    }
    
    public boolean isPublic () {
        return this.pub;
    }
    
    public void setPublic (boolean pub) {
        this.pub = pub;
    }
    
    public String getType () {
        return this.type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    public String toString () {
        return "Value " + this.getName();
    }
    
    public int hashCode () {
        return this.kind();
    }
    
    public boolean equals (Object o) {
        if (!(o instanceof ValueKey))
            return false;
        ValueKey vk = (ValueKey) o;
        if (!vk.getName().equals (this.getName()))
            return false;
        if (!vk.getType().equals (this.getType()))
            return false;
        return true;
    }

}
