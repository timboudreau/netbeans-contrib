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

import org.omg.CORBA.Contained;


public class IRContainedKey extends IRAbstractKey implements Cloneable{

  public Contained contained;
  // To improve the eficiency, by decreasing remote operations
  // cash the RepositoryId in private field;
  private String id;
  
  /** Creates new IRContainedKey */
  public IRContainedKey(Contained contained) {
    this.contained = contained;
  }
  
  /** Object.equals()
   */
  public boolean equals (Object other){
    if (other== null || !(other instanceof IRContainedKey))
      return false;
    if (!(this.getId().equals(((IRContainedKey)other).getId())))
      return false;
    return true;
  }
  
  /** Object.hashCode()
   */
  public int hashCode () {
    return this.getId().hashCode();
  }
  
  /** Returns the Repository Id of contained
   */
  private String getId () {
    if (this.id == null)
      this.id = contained.id();
    return this.id;
  }
  
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
  
}
