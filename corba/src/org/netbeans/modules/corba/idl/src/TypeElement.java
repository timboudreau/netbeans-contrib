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

   private String type;
   //private Vector members;

   public TypeElement (int id) {
      super(id);
      //members = new Vector ();
      setName ("typedef");
      setType ("none");
   }

   public TypeElement (IDLParser p, int id) {
      super(p, id);
      //members = new Vector ();
      setName ("typedef");
      setType ("none");
   }

   public void setType (String t) {
      type = t;
   }

   public String getType () {
      return type;
   }

   public void jjtSetParent (Node n) {
      super.jjtSetParent (n);
      String type;
      if (DEBUG) {
	 System.out.println ("TypeElement.jjtSetParent ()");
	 System.out.println ("type: " + getType ());
      }
      if (getMember (0) instanceof Identifier)
	 setName (((Identifier)getMember (0)).getName ());
      else {
	 // constr type
	 //setType (((Identifier)getMember (0).getMember (0)).getName ());
	 //setType ("typedef");
	 //for (int i = 1; i<getMembers ().size (); i++)
      } 
      for (int i = 0; i<getMembers ().size (); i++) {
	 if (getMembers ().elementAt (i) instanceof DeclaratorElement)
	    ((DeclaratorElement)getMembers ().elementAt (i)).setType (getType ());
      }
   }      

}

