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

public class ParameterElement extends IDLElement {

    private int attr;
    private IDLType type;

    static final long serialVersionUID =-8349198998203868841L;
    public ParameterElement (int id) {
        super(id);
    }

    public ParameterElement (IDLParser p, int id) {
        super(p, id);
    }


    public void setAttribute (int a) {
        attr = a;
    }

    public int getAttribute () {
        return attr;
    }

    public void setType (IDLType t) {
        type = t;
    }

    public IDLType getType () {
        return type;
    }

}

