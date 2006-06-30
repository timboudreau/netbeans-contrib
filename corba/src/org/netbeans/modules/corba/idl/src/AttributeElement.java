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

public class AttributeElement extends IDLElement {

    private boolean readonly;
    private IDLType type;
    private Vector other;

    public AttributeElement(int id) {
        super(id);
        readonly = false;
        other = new Vector ();
    }

    public AttributeElement(IDLParser p, int id) {
        super(p, id);
    }

    public void setReadOnly (boolean v) {
        readonly = v;
    }

    public boolean getReadOnly () {
        return readonly;
    }

    public void setType (IDLType t) {
        type = t;
    }

    public IDLType getType () {
        return type;
    }

    public Vector getOther () {
        return other;
    }

    public void setOther (Vector o) {
        other = o;
    }

    public void addOther (SimpleDeclarator o) {
        other.addElement (o);
    }

}


