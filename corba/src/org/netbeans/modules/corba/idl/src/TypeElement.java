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

public class TypeElement extends IDLElement {

  //private static final boolean DEBUG = true;
  public static final boolean DEBUG = false;

  private String type_name;
  private IDLType type;
  //private Vector members;

  static final long serialVersionUID =3084864756788217377L;
  public TypeElement (int id) {
    super(id);
    //members = new Vector ();
    setName ("typedef");
    setTypeName ("none");
  }

  public TypeElement (IDLParser p, int id) {
    super(p, id);
    //members = new Vector ();
    setName ("typedef");
    setTypeName ("none");
  }

  public void setTypeName (String t) {
    type_name = t;
  }

  public String getTypeName () {
    return type_name;
  }

  public IDLType getType () {
    return type;
  }

  public void setType (IDLType val) {
    type = val;
  }

  public void jjtSetParent (Node n) {
    super.jjtSetParent (n);
    String type;
    if (DEBUG) {
      System.out.println ("TypeElement.jjtSetParent ()");
      if (getType () != null)
	System.out.println ("type: " + getType ().getName ());
    }
    if (getMember (0) instanceof Identifier)
      setName (((Identifier)getMember (0)).getName ());

    if ((getMember (0) instanceof StructTypeElement) 
	|| (getMember (0) instanceof UnionTypeElement) 
	|| (getMember (0) instanceof EnumTypeElement)) {
      setType (new IDLType (-1 ,(((TypeElement)getMember (0)).getName ())));
    }

    if (DEBUG)
      System.out.println ("name: " + getName ());

    else {
      // constr type
      //setType (((Identifier)getMember (0).getMember (0)).getName ());
      //setType ("typedef");
      //for (int i = 1; i<getMembers ().size (); i++)
    } 
    for (int i = 0; i<getMembers ().size (); i++) {
      if (getMembers ().elementAt (i) instanceof DeclaratorElement) {
	//((DeclaratorElement)getMembers ().elementAt (i)).setType (new Type (-1, getName ()));
	((DeclaratorElement)getMembers ().elementAt (i)).setType (getType ());
      }
    }
  }      

}


