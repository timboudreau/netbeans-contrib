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

import java.io.IOException;
import java.io.File;

/** Remote Client. Interface which all new clients must implement.
 * 
 * @author  Libor Martinek
 * @version 1.0
 */
public interface RemoteClient {
  
  //public void setLogInfo(LogInfo loginfo);
  
  /** Returns file name object of the root.
  * @return  FileName of the root */    
  public RemoteFileName getRoot();  
  
  /** Connect to server.
  * @throws IOException  */  
  public void connect () throws IOException ;
  
  /** Test whether client is connected to server.
  * @return true in case that client is connected to server */  
  public boolean isConnected();

  /** Compare this information.
   * @param loginfo login information to compare
   * @return 0 if login information are equal;
   *         1 if login information refer to the same resource but can't be uses to login;
   *           (e.g. server and username is same but password is different)
   *        -1 if login information are different
   */
  public int compare(LogInfo loginfo);
  
  /** Get file from server.
   * @param what file on host to receive
   * @param where new file to create
   * @throws IOException  */
  public  void get(RemoteFileName what, File where) throws IOException ;

  /** Put file to server.
   * @param what file to send
   * @param where where to file send
   * @throws IOException  */
  public void put(File what, RemoteFileName where) throws IOException ;
 
  /** Return list of files in directory
   * @param directory 
   * @throws IOException 
   * @return  directory to list*/
  public RemoteFileAttributes[] list(RemoteFileName directory) throws IOException ;
  
  /** Rename file 
   * @param oldname 
   * @param newname 
   * @throws IOException 
   */
  public void rename(RemoteFileName oldname, String newname) throws IOException ;

  /** Delete directory
   * @param name what file to delete
   * @throws IOException  */
  public void delete(RemoteFileName name) throws IOException ;
 
  /** Make directory
   * @param name of the new directory
   * @throws IOException  */
  public void mkdir(RemoteFileName name) throws IOException ;
  
  /** Remove directory
   * @param name of the directory to remove
   * @throws IOException  */
  public void rmdir(RemoteFileName name) throws IOException ;
  
  /** Log out from server and close connection */
  public void disconnect() ;
    
  /** Immediately close connection with server */
  public void close();
}