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
 * @author  root
 * @version 
 */
public class InterfaceKey extends NamedKey {

  private String baseInterfaces;
  
  /** Creates new InterfaceKey
   *  @param int kind 
   *  @param String name
   *  @param String[] baseInterfaces, not null 
   */
  public InterfaceKey(int kind, String name, String baseInterfaces) {
    super (kind, name);
    this.baseInterfaces = baseInterfaces;
  }
  
  public String getbaseInterfaces () {
    return this.baseInterfaces;
  }
  
  public String toString () {
    return "InterfaceKey:" + name;  // No I18N
  }
  
}