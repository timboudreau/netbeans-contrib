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

package com.netbeans.enterprise.modules.jndi;

import javax.naming.NameClassPair;

/** 
 * This class represents the key for Children.Keys used by this module
 * @author  tzezula
 * @version 1.0
 * @see JndiChildren
 */
public final class JndiKey extends Object {
  
  /* Failed this node while listing*/
  public boolean failed;
  /* Wait Node */
  public boolean wait;
  /* The name class pair*/
  public NameClassPair name;

  /** Constructor used for Keys representing the WaitCursor
   */
  public JndiKey () {
    this.wait = true;
    this.failed =false;
  }
  
  /** Constructor used for Keys representing remote objects
   *  @param NameClassPair name, name and class of remote object
   */
  public JndiKey (NameClassPair name) {
    this.name = name;
    this.failed = false;
    this.wait = false;
  }
  
  /** Constructor used for Keys representing remote objects
   *  @param NameClassPair name, name and class of remote object
   *  @param boolean failed, if the node is failed
   */
  public JndiKey (NameClassPair name, boolean failed){
    this.name = name;
    this.failed = failed;
    this.wait = false;
  }
  
  /** Comparator
   *  @param Object obj, object to compare with
   *  @return boolean, true if equals
   */
  public boolean equals(Object obj){
    if (! (obj instanceof JndiKey)){
      return false;
    }
    JndiKey key = (JndiKey) obj;
    if (key.wait == true && this.wait == true) 
      return true; // both WaitNode
    else if (key.wait == true) // Other WaitNode
      return false;
    else if (this.wait == true) // Me WaitNode
      return false;
    else if (!this.name.getName().equals(key.name.getName()) || !this.name.getClassName().equals(key.name.getClassName()))
      return false;
    else return true;
  }
  
  /** Hash code of object
   *  @return int hash code of object
   */
  public int hashCode(){
    if (this.wait == true) return 0;
    else return this.name.getName().hashCode();
  }
  
  /** Returns the name of key
   *  return String name
   */
  public String toString () {
    if (this.wait == true) return "";
    else return name.toString();
  }
  
}
