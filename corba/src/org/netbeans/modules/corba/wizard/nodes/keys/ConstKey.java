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
public class ConstKey extends AliasKey {
  
    private String value;

    /** Creates new ConstKey */
    public ConstKey (int kind, String name, String type, String value) {
        super (kind,name,type,null);
        this.value = value;
    }
  
    public String getValue () {
        return this.value;
    }
  
  
    public String toString () {
        return "ConstKey: " + name;
    }
  
}
