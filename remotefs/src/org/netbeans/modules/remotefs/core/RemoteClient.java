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

/** Remote Client.
 * 
 * @author  Libor Martinek
 * @version 1.0
 */
public interface RemoteClient {
  
  //public void setLogInfo(LogInfo loginfo);
  
  // Connect to server.
  public void connect () throws IOException ;
  
  // Test whether client is connected to server.
  public boolean isConnected();

  /** Compare this information
   * @return 0 if login information are equal;
   *         1 if login information refer to the same resource but can't be uses to login;
   *        -1 if login information are different
   */
  public int compare(LogInfo loginfo);
  
  /** Get file from server.
   * @param what file on host to receive
   * @param where new file to create
   */
  public  void get(String what, File where) throws IOException ;

  /** Get file from server.
   */
  public void get(String accesspath, String what, File where) throws IOException;
 
  /** Put file to server.
   * @param what file to send
   * @param where where to file send
   */
  public void put(File what, String where) throws IOException ;
 
   /** Put file to server */
  public void  put(File what, String accesspath, String where) throws IOException ;

   /** Return list of files in directory */
  public RemoteFileAttributes[] list(String directory) throws IOException ;
  
  /** Return list of files in directory */
  public  RemoteFileAttributes[] list(String accesspath, String dirname) throws IOException;

  
  /** Rename file */
  public void rename(String from, String to) throws IOException ;

  /** Rename file */
  public  void rename(String fromaccesspath, String fromname, String toaccesspath, String toname) throws IOException ;
  
  /** Delete directory */
  public void delete(String path) throws IOException ;
 
  /** Delete directory */
  public void delete(String accesspath, String name) throws IOException;
  
  /** Make directory */
  public void mkdir(String path) throws IOException ;
  
  /** Make directory */
  public void mkdir(String accesspath, String name) throws IOException ;
  
  /** Remove directory */
  public void rmdir(String path) throws IOException ;
  
  /** Remove directory */
  public void rmdir(String accesspath, String name) throws IOException ;
  
  
  /** Disconnect from server */
  public void disconnect() ;
    
  /** Close connection with server */
  public void close();
}