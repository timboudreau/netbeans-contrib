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
 
package org.netbeans.modules.remotefs.ftpclient;

/** FTPException
 * @author Libor Martinek
 * @version 1.0
 */
public class FTPException extends java.io.IOException {
  static final long serialVersionUID = 4008993924766308326L;
  private FTPResponse response;

  /** Creates new FTPException
   * @param response FTPResponse object
   */
  public FTPException(FTPResponse response) {
    super(response.toString());
    this.response=response;
  }
  
  /** Returns FTPResponse of this exception
   * @return FTPResponse
   */
  public FTPResponse getResponse() {
    return response;
  }
  
}