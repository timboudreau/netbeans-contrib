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

package org.netbeans.modules.corba.wizard.nodes.keys;

/** 
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public class MutableKey extends Object {
  
  public final static int MODULE    = 1;
  public final static int INTERFACE = 2;
  public final static int OPERATION = 3;
  public final static int ATTRIBUTE = 4;
  public final static int CONSTANT  = 5;
  public final static int ALIAS     = 6;
  public final static int EXCEPTION = 7;
  public final static int STRUCT    = 8;
  public final static int UNION     = 9;
  public final static int ENUM      = 10;
  public final static int UNION_MBR = 11;
  public final static int STRUCT_MBR = 12;
  public final static int ENUM_MBR = 13;

  private int kind;  
  
  /** Creates new MutableKey */
  public MutableKey (int kind) {
    this.kind = kind;
  }
  
  public int kind () {
    return this.kind;
  }
  
  public String toString () {
    return "org.netbeans.modules.corba.wizard.nodes.keys.MutableKey:"+kind;  // No I18N
  }
  
}