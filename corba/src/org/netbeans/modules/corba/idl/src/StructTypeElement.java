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

public class StructTypeElement extends TypeElement {
    static final long serialVersionUID =-2986489021433601833L;
    public StructTypeElement(int id) {
        super(id);
    }

    public StructTypeElement(IDLParser p, int id) {
        super(p, id);
    }

    /*
    public void jjtClose () {

       System.out.println ("StructTypeElement.jjtClose ()");
       if (jjtGetChild (0) instanceof Identifier)
    setType (((Identifier)jjtGetChild (0)).getName ());
       else  // constr type
    setType (((Identifier)jjtGetChild (0).jjtGetChild (0)).getName ());
       for (int i=0; i<jjtGetNumChildren (); i++)
    if (jjtGetChild (i) instanceof Identifier) {
      // simple type
      addMember (jjtGetChild (i));
}
    else {
      addMember (jjtGetChild (i));
}
}      
    */

}


