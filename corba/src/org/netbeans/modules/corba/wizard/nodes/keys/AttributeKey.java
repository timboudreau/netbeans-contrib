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
public class AttributeKey extends NamedKey {

    private boolean readonly;
    private String type;
  
    /** Creates new AttributeKey */
    public AttributeKey (int kind, String name, String type, boolean readonly) {
        super (kind, name);
        this.type = type;
        this.readonly = readonly;
    }
  
    public String getType () {
        return this.type;
    }
  
    public boolean isReadOnly () {
        return this.readonly;
    }
  
}
