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

/*
 * NAME_SUBSTITUTION.java -- synopsis.
 *
 *
 * Date: 15.6.1998 12:22:29$
 * <<Revision>>
 *
 * SUN PROPRIETARY/CONFIDENTIAL:  INTERNAL USE ONLY.
 *
 * Copyright © 1997-1999 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 */

package com.netbeans.enterprise.modules.corba.idl.src;

public class MemberElement extends IDLElement {
   
   IDLType type;

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




/*
 * <<Log>>
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/5/99  Karel Gardas    
 *  2    Gandalf   1.1         8/3/99   Karel Gardas    
 *  1    Gandalf   1.0         7/10/99  Karel Gardas    initial revision
 * $
 */
