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

public class InterfaceHeaderElement extends IDLElement {

  boolean is_abstract;
  Vector parents;

  public InterfaceHeaderElement(int id) {
    super(id);
    is_abstract = false;
    parents = new Vector ();
  }

  public InterfaceHeaderElement(IDLParser p, int id) {
    super(p, id);
    is_abstract = false;
    parents = new Vector ();
  }

  public void setInheritedParents (Vector value) {
    parents = value;
  }

  public Vector getInheritedParents () {
    return parents;
  }

  public void setAbstract (boolean value) {
    is_abstract = value;
  }

  public boolean isAbstract () {
    return is_abstract;
  }

}
