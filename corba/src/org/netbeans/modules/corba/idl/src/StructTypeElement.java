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


