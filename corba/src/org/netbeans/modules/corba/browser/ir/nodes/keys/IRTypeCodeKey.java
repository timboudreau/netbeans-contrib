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

package org.netbeans.modules.corba.browser.ir.nodes.keys;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;


public class IRTypeCodeKey extends IRAbstractKey implements Cloneable {

  public String name;
  public TypeCode type;
  public Any label;


  
  /** Creates new IRTypeCodeKey */
  public IRTypeCodeKey(String name, TypeCode type, Any label){
    this.name = name;
    this.type = type;
    this.label = label;
  }

  public IRTypeCodeKey(String name, TypeCode type) {
    this (name,type,null);
  }
  
  public IRTypeCodeKey (String name){
    this (name,null,null);
  }
  
  
  public boolean equals (Object other){
    if (! (other instanceof IRTypeCodeKey))
      return false;
    if (! name.equals(((IRTypeCodeKey)other).name))
      return false;
    return true;
  }
  
  public int hashCode(){
    return this.name.hashCode();
  }
  
  public Object clone () throws CloneNotSupportedException {
    return super.clone();
  }
  
  
}
