/*                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version 
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is RemoteFS. The Initial Developer of the Original
 * Code is Libor Martinek. Portions created by Libor Martinek are 
 * Copyright (C) 2000. All Rights Reserved.
 * 
 * Contributor(s): Libor Martinek. 
 */

package org.netbeans.modules.remotefs.core;

/** Interface for storing login information.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public interface LogInfo extends java.io.Serializable{
  static final long serialVersionUID = -1698365418732665299L;
  /** Return human redable description of this LogInfo */
  public String displayName();
  
  
}