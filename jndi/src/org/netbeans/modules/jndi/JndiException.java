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

import javax.naming.NamingException;



/** JNDI Exception is exception used by JNDI package
 *
 *  @author Tomas Zezula 
 */
public final class JndiException extends NamingException {
  
  /** Constructor*/
  public JndiException() {
    super();
  }
  
  /** Constructor
   * @param msg message holds by exception
   */
  public JndiException(String msg) {
    super(msg);
  }
}

