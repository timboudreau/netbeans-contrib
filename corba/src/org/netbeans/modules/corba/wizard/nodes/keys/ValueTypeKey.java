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
public class ValueTypeKey extends InterfaceKey {
    
    String supports;
    boolean custom;
    boolean truncatable;

    /** Creates new ValueKey */
    public ValueTypeKey(int kind,String name,String base,String supports,boolean abst,boolean custom,boolean truncatable) {
        super (kind,name,base,abst);
        this.supports = supports;
        this.custom = custom;
        this.truncatable = truncatable;
    }
    
    public boolean isCustom () {
        return this.custom;
    }
    
    public boolean isTruncatable () {
        return this.truncatable;
    }
    
    public void setCustom (boolean custom) {
        this.custom = custom;
    }
    
    public void setTruncatable (boolean truncatable) {
        this.truncatable = truncatable;
    }
    
    public String getSupports () {
        return this.supports;
    }
    
    public void setSupports (String supports) {
        this.supports = supports;
    }
    
    public String toString () {
        return "Valuetype" + this.getName();
    }
    
    public int hashCode () {
        return this.kind();
    }
    
    public boolean equals (Object o) {
        if (! (o instanceof ValueTypeKey))
            return false;
        ValueTypeKey ov = (ValueTypeKey) o;
        if (!this.getName().equals(ov.getName()))
            return false;
        return true;
    }

}
