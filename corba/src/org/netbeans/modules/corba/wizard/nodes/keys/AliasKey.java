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
public class AliasKey extends NamedKey {

  private String type;
  private String length;
  
  /** Creates new AliasKey */
  public AliasKey (int kind, String name, String type, String length) {
    super (kind, name);
    this.type = type;
    this.length = length;
  }
  
  public String getType () {
    return this.type;
  }
  
  public String getLength () {
    return this.length;
  }
  
  public String toString () {
    return "AliasKey: "+name;  //No I18N
  }
  
}