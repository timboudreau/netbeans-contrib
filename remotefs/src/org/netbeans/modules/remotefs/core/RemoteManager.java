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

import java.util.*;
import java.io.*;

/** RemoteManger holds static table of created managers, client, 
 * cache file and  root file.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public final class RemoteManager  {
  
  /** List of created FTPManagers */
  private static Vector managers = new Vector();
  /** List of Objects that own this FTPManager */
  private Vector owners = new Vector();
  /** Cache directory */
  private File cacheroot = null;
  /** Root directory */
  private RemoteFile root = null;
  
  private RemoteClient client;
  
  /** Creates new FTPManager.
   * @param owner object that uses this manager
   * @param loginfo log info
   * @param cache cache file
 * @throws IOException  */
  protected RemoteManager(RemoteOwner owner, LogInfo loginfo, File cache) throws IOException {
    owners.addElement(owner);
    cacheroot = cache;
    if (!cacheroot.exists()) cacheroot.mkdirs();
    client = owner.createClient(loginfo,cache);
    //client.setLogInfo(loginfo);
    //root = RemoteFile.createRoot(this,cache);
  }
  
  /** Search for usable manager and if none is found, create new.
   * @return RemoteManager object
   * @param loginfo log info
   * @param owner object that uses this manager
   * @param cache cache file
 * @throws IOException  */
  public static RemoteManager getRemoteManager(RemoteOwner owner,File cache,LogInfo loginfo)  throws IOException  {
    boolean managerexist = false;
    RemoteManager manager = null;
      // find existing manager
      Enumeration enum = managers.elements();
      while (enum.hasMoreElements()) {
        manager = (RemoteManager)(enum.nextElement());
        int compareresult = manager.getClient().compare(loginfo);
        if (compareresult == 0)  {
          // same
          managerexist = true;
          // cache must be equal
          if (!cache.equals(manager.cacheroot))
             if (!owner.notifyIncorrectCache(manager.cacheroot))
               return null;
          break;
        }
        else if (compareresult > 0) {
          // same except password
          owner.notifyIncorrectPassword();
          return null;
        }
      }
      if (!managerexist) {  // manager not found
        //RemoteFile f = RemoteFile.createRoot(manager,cache);
        manager = new RemoteManager(owner,loginfo,cache); 
        managers.addElement(manager);
      }
      else
        manager.owners.addElement(owner);
      return manager;
  }
  
  /** Return RemoteClient
 * @return  RemoteClient object*/
  public RemoteClient getClient() {
    return client; 
  }
  
  /** Remove owner from list and  if last owner is removed, disconnect from ftp server.
   * @param owner owner to remove
   */
  public final void remove(RemoteOwner owner) {
    // remove owner from list
    owners.removeElement(owner);
    // if it was last owner, diconnect
    if (owners.isEmpty()) {
      if (getClient().isConnected()) getClient().disconnect();
      managers.removeElement(this);
    }
  }

  /** Has this manager more than one owner?
   * @return true if manager has more than one owner
   */
  public final boolean moreOwners() {
    return (owners.size() > 1);

  }
  
  /** Get root.
   * @return root file
   * @throws IOException  */
  public RemoteFile getRoot() throws IOException {
    if (root == null) root = ((RemoteOwner)(owners.firstElement())).createRootFile(getClient(),cacheroot);
    return root;
  }
  
  /** Get root with specified start directory.
   * @param startdir
   * @throws IOException
   * @return root file
   */
  public RemoteFile getRoot(String startdir) throws IOException {
    RemoteFile f = getRoot().find(startdir);
    return f;
  }
  

  //************************************************************************************
  /** Interface that owner of RemoteManager must implement. */
  public interface RemoteOwner {
  
    /** Create new client with this log info and cache
     * @param loginfo
     * @param cache
     * @throws IOException
     * @return  created Client */      
    public RemoteClient createClient(LogInfo loginfo, File cache) throws IOException ;
    
    /** Create new root file 
     * @param client
     * @param cache
     * @throws IOException
     * @return  */    
    public RemoteFile createRootFile(RemoteClient client, File cache) throws IOException ;
  
    /** Notify user that incorrect password was entered
     */
    public void notifyIncorrectPassword() ;
    
    /** Notify user that another cache that existing was entered
     * @param newcache
     * @return  */    
    public boolean notifyIncorrectCache(java.io.File newcache);

  }

}
