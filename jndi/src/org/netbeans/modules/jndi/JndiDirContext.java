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

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

/** This class extends InitialDirContext with methods for timeout handling
 * 
 *  @author Ales Novak, Tom Zezula
 */
final class JndiDirContext extends InitialDirContext {
  
  
  
  /** Environment used for InitialContext*/	
  protected Hashtable envTable;

  /**
   * Constuctor 
   * @param env  hashtable of properties for InitialDirContext
   */
  public JndiDirContext(Hashtable env) throws NamingException {
    super(env);
    this.envTable = env;
  }
  
  /** Returns environment for which the Context was created
   *  @return Hashtable of key type java.lang.String, value type java.lang.String
   */ 
  public Hashtable getEnvironment() {
   // return envTable;
    try{
      return super.getEnvironment();
    }catch(Exception e){
      return null;
    }
  }
  
}
