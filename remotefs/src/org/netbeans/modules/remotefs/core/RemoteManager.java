/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
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
      Enumeration en = managers.elements();
      while (en.hasMoreElements()) {
        manager = (RemoteManager)(en.nextElement());
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
