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

package org.netbeans.modules.corba.idl.src;

import java.util.Vector;

public class ValueHeaderElement extends IDLElement {

    boolean custom;
    ValueInheritanceSpecElement inheritance;

    public ValueHeaderElement(int id) {
        super(id);
        custom = false;
    }

    public ValueHeaderElement(IDLParser p, int id) {
        super(p, id);
        custom = false;
    }

    public void setCustom (boolean value) {
        custom = value;
    }

    public boolean isCustom () {
        return custom;
    }

    public ValueInheritanceSpecElement getInheritanceSpecElement () {
        return inheritance;
    }

    public void jjtClose () {
        super.jjtClose ();
        Vector _members = super.getMembers ();
        Identifier id = (Identifier)_members.elementAt (0);
        setName (id.getName ());
        inheritance = (ValueInheritanceSpecElement)_members.elementAt (1);
    }

}

