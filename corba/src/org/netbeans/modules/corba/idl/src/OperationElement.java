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

public class OperationElement extends IDLElement {

    private String op_attribute;
    //private Element op_type_spec;
    private IDLType op_type_spec;
    //private Element name;
    private Vector params;
    private Vector exceptions;
    private Vector contexts;

    static final long serialVersionUID =-533680242820260136L;
    public OperationElement(int id) {
        super(id);
        params = new Vector ();
        exceptions = new Vector ();
        contexts = new Vector ();
    }

    public OperationElement(IDLParser p, int id) {
        super(p, id);
        params = new Vector ();
        exceptions = new Vector ();
        contexts = new Vector ();
    }

    public void setAttribute (String attr) {
        op_attribute = attr;
    }

    public String getAttribute () {
        return op_attribute;
    }

    /*
    public void setReturnType (Element type) {
       op_type_spec = type;
}

    public Element getReturnType () {
       return op_type_spec;
}
    */

    public void setReturnType (IDLType type) {
        op_type_spec = type;
    }

    public IDLType getReturnType () {
        return op_type_spec;
    }

    public void setParameters (Vector ps) {
        params = ps;
    }

    public Vector getParameters () {
        return params;
    }

    public void setExceptions (Vector es) {
        exceptions = es;
    }

    public Vector getExceptions () {
        return exceptions;
    }


    public void setContexts (Vector cs) {
        contexts = cs;
    }

    public Vector getContexts () {
        return contexts;
    }

    public void jjtClose () {
        super.jjtClose ();
        for (int i=0; i<getMembers ().size (); i++) {
            if (getMember (i) instanceof ParameterElement)
                params.addElement ((ParameterElement)getMember (i));
        }
    }


}




