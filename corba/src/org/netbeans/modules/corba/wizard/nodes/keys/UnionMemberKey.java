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
 * @author  root
 * @version 
 */
public class UnionMemberKey extends AliasKey {

    private String label;
    private boolean defaultValue;
  
    /** Creates new UnionMemberKey */
    public UnionMemberKey(int kind, String name, String type, String length, String label) {
        super (kind, name, type, length);
        if (label != null) {
            this.label = label;
            this.defaultValue = false;
        }
        else
            this.defaultValue = true;
    }
  
    public String getLabel () {
        return this.label;
    }
    
    public void setLabel (String label) {
        this.label = label;
    }

    public boolean isDefaultValue () {
        return this.defaultValue;
    }
    
    public void setDefaultValue (boolean dv) {
        this.defaultValue = dv;
    }
  
}
