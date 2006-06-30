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

public class DeclaratorElement extends TypeElement {

    private Vector dim;

    public DeclaratorElement (int id) {
        super(id);
        dim = new Vector ();
    }

    public DeclaratorElement (IDLParser p, int id) {
        super(p, id);
        dim = new Vector ();
    }

    public void setDimension (Vector s) {
        dim = s;
    }

    public Vector getDimension () {
        return dim;
    }

    /*
      public void setType (String s) {
      System.out.println (getType () + " -> " + s);
      super.setType (s);
      Thread.dumpStack ();
      }
    */

    public IDLType getType () {
        if (super.getType ().ofDimension () != null) {
            if (!super.getType ().ofDimension ().equals (getDimension ())) {
                //System.out.println ("setting right dimension for IDLType"); // NOI18N
                super.getType ().setDimension (getDimension ());
            }
        }
        else {
            super.getType ().setDimension (new Vector ());
        }
        return super.getType ();
    }
    /*
      public void jjtClose () {
      super.jjtClose ();
      //System.out.println ("DeclaratorElement.jjtClose ();");
      setName (((Identifier)getMember (0)).getName ());
      //getType ().setDimension (getDimension ());
      }
    */}


