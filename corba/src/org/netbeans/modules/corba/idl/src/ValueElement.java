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

public class ValueElement extends ValueAbsElement {

  private boolean is_custom;
  private ValueInheritanceSpecElement inheritance;

  public ValueElement(int id) {
    super(id);
    is_custom = false;
  }

  public ValueElement(IDLParser p, int id) {
    super(p, id);
    is_custom = false;
  }

  public boolean isAbstract () {
    return is_abstract; // because ValueElement is never abstract
  }

  public void setCustom (boolean value) {
    is_custom = value;
  }

  public boolean isCustom () {
    return is_custom;
  }


  public void jjtClose () {
    super.jjtClose ();
    Vector _members = super.getMembers ();
    ValueHeaderElement header = (ValueHeaderElement)_members.elementAt (0);
    setName (header.getName ());
    setCustom (header.isCustom ());
  }
 
}


