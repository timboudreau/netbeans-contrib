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

/** RemoteManger holds  static table of created managers, client, 
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
   * @param host
   * @param port
   * @param user
   * @param password
   */
  protected RemoteManager(RemoteOwner owner, LogInfo loginfo, File cache) throws IOException {
    owners.addElement(owner);
    cacheroot = cache;
    if (!cacheroot.exists()) cacheroot.mkdirs();
    client = owner.createClient(loginfo,cache);
    //client.setLogInfo(loginfo);
    //root = RemoteFile.createRoot(this,cache);
  }
  
  /** Search for usable manager and if none is found, create new.
   * @param owner
   * @param cache
   * @throws IOException
   * @return
   */
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
  
  /** Return RemoteClient */
  public RemoteClient getClient() {
    return client; 
  }
  
  /** Remove owner from list and  if last owner is removed, disconnect from ftp server.
   * @param owner
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
   * @return
   */
  public final boolean moreOwners() {
    return (owners.size() > 1);

  }
  
  /** Get root.
   * @return
   */
  public RemoteFile getRoot() throws IOException {
    if (root == null) root = ((RemoteOwner)(owners.firstElement())).createRootFile(getClient(),cacheroot);
    return root;
  }
  
  /** Get root with specified start directory.
   * @param startdir
   * @throws IOException
   * @return
   */
  public RemoteFile getRoot(String startdir) throws IOException {
    RemoteFile f = getRoot().getRoot(startdir);
    return f;
  }
  

  //************************************************************************************
  /** Interface that owner of RemoteManager must implement. */
  public interface RemoteOwner {
  
    public RemoteClient createClient(LogInfo loginfo, File cache) throws IOException ;
    public RemoteFile createRootFile(RemoteClient client, File cache) throws IOException ;
  
    public void notifyIncorrectPassword() ;
    public boolean notifyIncorrectCache(java.io.File newcache);

  }

}
