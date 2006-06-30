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