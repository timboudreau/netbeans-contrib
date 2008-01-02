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

import java.io.IOException;
import org.openide.filesystems.FileStatusEvent;
import org.openide.filesystems.FileSystemCapability;

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

    @Override
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
    @Override
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
    if (isConnected() == connected) {
            return;
        } 
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
           if (rootFile == null || (rootFile != null && !rootFile.getName().getFullName().equals(startdir))) {
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
    firePropertyChange("connected", null, isConnected() ? Boolean.TRUE : Boolean.FALSE);
    //firePropertyChange(PROP_SYSTEM_NAME, "", getSystemName());
  }
  
  /** Informs user that also another filesystem is connected to the same server. */
  protected abstract int disconnectDialog(String server);
  
  /** Informs user that also another filesystem is disconnected from the same server. */
  protected abstract boolean connectDialog(String server);
  
} 