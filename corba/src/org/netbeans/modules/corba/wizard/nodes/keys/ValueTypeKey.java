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
