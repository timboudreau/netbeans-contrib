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

package org.netbeans.modules.corba.wizard.nodes;

import org.openide.*;
import org.openide.nodes.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;
import org.netbeans.modules.corba.wizard.nodes.keys.*;

public class ValueBoxNode extends AbstractMutableLeafNode {

    public static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/value";
    
    public ValueBoxNode (AliasKey key) {
	super (key);
	this.setIconBase (ICON_BASE);
    }
    
    public String getName () {
	return this.getDisplayName();
    }
    
    public String getDisplayName () {
	return this.key.getName();
    }
    
    public String generateSelf (int indent) {
	String fill = "";
	for (int i=0; i< indent; i++)
	    fill = fill + SPACE;
	fill = fill + "valuetype " + this.getName () +" "+((AliasKey)this.key).getType()+";\n";
	return fill;
    }
    
    public ExPanel getEditPanel () {
        ValueBoxPanel p = new ValueBoxPanel ();
        p.setName (this.getName());
        p.setType (((AliasKey)this.key).getType());
	return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ValueBoxPanel) {
            ValueBoxPanel vp = (ValueBoxPanel) p;
            String newName = vp.getName();
            String newType = vp.getType();
            if (!newName.equals (this.getName())) {
                this.setName (newName);
                this.key.setName (newName);
            }
            if (!newType.equals (((AliasKey)this.key).getType())) {
                ((AliasKey)this.key).setType (newType);
            }
        }
    }
}