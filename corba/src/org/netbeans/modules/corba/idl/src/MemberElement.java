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

public class MemberElement extends IDLElement {
   
   IDLType type;

  static final long serialVersionUID =225631314107467399L;
   public MemberElement (int id) {
      super(id);
   }

   public MemberElement (IDLParser p, int id) {
      super(p, id);
   }

   public void setType (IDLType t) {
      type = t;
   }

   public IDLType getType () {
      return type;
   }
  /*
    public void jjtSetParent (Node n) {
    super.jjtSetParent (n);
    }
  */
  public void jjtClose () {
      //System.out.println ("MemberElement.jjtClose ()");
      super.jjtClose ();
      // remove all children of type Identifier
      /*
	java.util.Vector tmp_members = getMembers ();
	for (int i=0; i<tmp_members.size (); i++) {
	if (tmp_members.elementAt (i) instanceof Identifier) {
	tmp_members.removeElementAt (i);
	System.out.println ("remove element at " + i + " from " + getType ());
	}
	}
      */
      /*
	public void jjtSetParent (Node n) {
	super.jjtSetParent (n);
      */
      /*
      if (getMember (getMembers ().size () - 1) instanceof Identifier)
	 setName (((Identifier)getMember (getMembers ().size () - 1)).getName ());
      */
      if (getMember (0) instanceof DeclaratorElement) {
	 for (int i = 0; i<getMembers ().size (); i++) {
	    //System.out.println (((DeclaratorElement)getMember (i)).getName () 
	    //		+ " set type " + getType ());
	    ((DeclaratorElement)getMember (i)).setType (getType ());
	 }
      }
      if (getMember (0) instanceof TypeElement && !(getMember (0) instanceof DeclaratorElement)) {
	// first is struct, enum or union
	//Type type = ((TypeElement)getMember (0)).getType ();
	IDLType type = new IDLType (-1, ((TypeElement)getMember (0)).getName ());
	 for (int i = 1; i<getMembers ().size (); i++)
	    ((DeclaratorElement)getMember (i)).setType (type);
      }
      if (getMember (0) instanceof Identifier) {
	//String type = ((Identifier)getMember (0)).getName ();
	for (int i = 1; i<getMembers ().size (); i++)
	  if (getMember (i) instanceof DeclaratorElement) { 
	    // this is because of scoped names in Member
	    //((DeclaratorElement)getMember (i)).setType (type);
	    ((DeclaratorElement)getMember (i)).setType (getType ());
	  }
      }

	 
  }

}



