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

