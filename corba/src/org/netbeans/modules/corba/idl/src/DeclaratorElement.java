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

package com.netbeans.enterprise.modules.corba.idl.src;

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
    if (!super.getType ().ofDimension ().equals (getDimension ())) {
      //System.out.println ("setting right dimension for IDLType");
      super.getType ().setDimension (getDimension ());
    }
    return super.getType ();
  }

  public void jjtClose () {
    super.jjtClose ();
    //System.out.println ("DeclaratorElement.jjtClose ();");
    setName (((Identifier)getMember (0)).getName ());
    //getType ().setDimension (getDimension ());
  }
}

