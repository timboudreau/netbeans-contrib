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

public class ValueInheritanceSpecElement extends IDLElement {

  private Vector supported_interfaces;
  private Vector inherited_values;
  private boolean truncatable;

  public ValueInheritanceSpecElement(int id) {
    super(id);
    truncatable = false;
  }

  public ValueInheritanceSpecElement(IDLParser p, int id) {
    super(p, id);
    truncatable = false;
  }

  public void setTruncatable (boolean value) {
    truncatable = value;
  }

  public boolean getTruncatable () {
    return truncatable;
  }

  public void setInterfaces (Vector value) {
    supported_interfaces = value;
  }

  public Vector getInterfaces () {
    return supported_interfaces;
  }

  public void setValues (Vector value) {
    inherited_values = value;
  }

  public Vector getValues () {
    return inherited_values;
  }

  

}
