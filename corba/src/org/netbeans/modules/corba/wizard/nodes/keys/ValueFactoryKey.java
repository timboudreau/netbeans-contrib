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
public class ValueFactoryKey extends NamedKey {

    private String params;
    
    /** Creates new ValueFactory */
    public ValueFactoryKey(int kind,String name,String params) {
        super (kind, name);
        this.params = params;
    }
    
    public String getParams () {
        return this.params;
    }
    
    public void setParams (String params) {
        this.params = params;
    }
    
    public String toString () {
        return "factory " + this.getName();
    }
    
    public int hashCode () {
        return this.kind();
    }
    
    public boolean equals (Object o) {
        if (!(o instanceof ValueFactoryKey))
            return false;
        ValueFactoryKey vk = (ValueFactoryKey) o;
        return (vk.getName().equals(this.getName()));
    }

}
