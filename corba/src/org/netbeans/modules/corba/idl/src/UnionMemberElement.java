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

public class UnionMemberElement extends TypeElement {

   //private 

   private String _cases;

   public UnionMemberElement(int id) {
      super(id);
   }

   public UnionMemberElement(IDLParser p, int id) {
      super(p, id);
   }

   public String getCases () {
      return _cases;
   }

   public void setCases (String s) {
      _cases = s;
   }
   
   /*
   public void jjtClose () {
      super.jjtClose ();
      setName (((DeclaratorElement)getMember (getMembers ().size () - 1)).getName ());
   }
   */
   public void jjtSetParent (Node n) {
      super.jjtSetParent (n);
      setName (((DeclaratorElement)getMember (getMembers ().size () - 1)).getName ());
   }
}
