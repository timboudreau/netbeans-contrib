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

public class DeclaratorElement extends TypeElement {

   private String dim;

   public DeclaratorElement (int id) {
      super(id);
      dim = new String ("");
   }

   public DeclaratorElement (IDLParser p, int id) {
      super(p, id);
      dim = new String ("");
   }

   public void setDimension (String s) {
      dim = s;
   }

   public String getDimension () {
      return dim;
   }

   /*
   public void setType (String s) {
      System.out.println (getType () + " -> " + s);
      super.setType (s);
      Thread.dumpStack ();
   }
   */

   public void jjtClose () {
      super.jjtClose ();
      //System.out.println ("DeclaratorElement.jjtClose ();");
      setName (((Identifier)getMember (0)).getName ());
   }
}

