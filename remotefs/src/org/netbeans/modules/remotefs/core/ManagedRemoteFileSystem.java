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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.*;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Date;
import java.net.*;

import org.openide.*;
import org.openide.filesystems.*;
import org.openide.options.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.enum.SingletonEnumeration;
import org.openide.util.enum.SequenceEnumeration;

/** Managed Remote FIleSystem class
 * @author Libor Martinek
 * @version 1.0
 */
public abstract class ManagedRemoteFileSystem extends RemoteFileSystem 
      implements RemoteManager.RemoteOwner, RemoteFile.Notify {
  static final long serialVersionUID = 5983095716602271792L;
  private static final boolean DEBUG = true;
  
  /** RemoteManager */
  protected transient RemoteManager manager = null;
  
  /** Constructor.
  */
  public ManagedRemoteFileSystem() {
    super();
  }
 
  /** Constructor. Allows user to provide own capabilities
  * for this file system.
  * @param cap capabilities for this file system
  */
  public ManagedRemoteFileSystem(FileSystemCapability cap) {
    this ();
    setCapability (cap);
  }

  protected void removeClient() {
    if (manager != null) {
      manager.remove(this);
      manager = null;
    }
    client = null;
    rootFile = null;
  }
 
  /** Test whether filesystem is connected to server.
   * @return Value of property connected.
   */
  public boolean isConnected() {
    if (manager==null)  return false;
    return manager.getClient().isConnected();
  }
  
  /** Connect to or diconnect from server.
   * @param connected New value of property connected.
   */
  public void setConnected(boolean connected) {
    // is new state different?
    //System.out.println("ManagedRemoteFileSystem.setConnected");
    if (isConnected() == connected)   return; 
    if (!connected) {  // will be disconnected
           // exists other filesystem with same server
           if (manager.moreOwners()) {
                  switch (disconnectDialog(loginfo.displayName())) {
                    case 0:   removeClient();
                              break;
                    case 1:   manager.getClient().disconnect(); 
                              //TODO: notify other FS
                              break;
                    case 2:   return;
                  }
           }      
           else manager.getClient().disconnect();
        }    
    else {
       try { 
            if (manager == null) {
               manager = RemoteManager.getRemoteManager(this, cachedir,loginfo);
               client = manager.getClient();
               rootFile = null;
               if (manager == null) {
                  return;
               }
           }
           if (!isConnected()) {   
               if (manager.moreOwners()) {
                  if (connectDialog(loginfo.displayName())) {
                          manager.getClient().connect();
                          //TODO: notify other FS?
                  }        
                  else { removeClient();
                         return;
                         //TODO: or try to get ftproot if it's possible even when diconnected
                  }      
               }
               else manager.getClient().connect();
           }
           if (rootFile == null || (rootFile != null && !rootFile.getPath().equals(startdir))) {
                rootFile = manager.getRoot(startdir);
                if (rootFile == null) {
                      startdirNotFound(startdir,loginfo.displayName());
                      startdir = "/";
                      rootFile = manager.getRoot();
                }      
           }
       }
       catch(IOException e) {
        if (connected && manager!=null) manager.getClient().close(); 
        errorConnect(e.toString());
       }
       synchronize("/");
    }  
    fireFileStatusChanged(new FileStatusEvent(this,getRoot(),true,true));
    //refreshRoot();
    //try { org.openide.loaders.DataObject.find(super.getRoot()).getNodeDelegate().setDisplayName(getDisplayName()); }
    //catch (org.openide.loaders.DataObjectNotFoundException e) {}
    firePropertyChange("connected", null, new Boolean(isConnected()));
    //firePropertyChange(PROP_SYSTEM_NAME, "", getSystemName());
  }
  
  /** Informs user that also another filesystem is connected to the same server. */
  protected abstract int disconnectDialog(String server);
  
  /** Informs user that also another filesystem is disconnected from the same server. */
  protected abstract boolean connectDialog(String server);
  
} 