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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.swing.Timer;

import org.openide.TopManager;
import org.openide.loaders.DataFolder;
import org.openide.filesystems.*;
import org.openide.util.actions.CookieAction;
import org.openide.util.actions.CallbackSystemAction;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.enum.AlterEnumeration;
import org.openide.util.enum.FilterEnumeration;
import org.openide.util.enum.SequenceEnumeration;
import org.openide.util.enum.QueueEnumeration;

/** Action for connect/disconnect filesystem.
*
* @author Libor Martinek
*/
public class ConnectAction extends NodeAction {
  private RemoteFileSystem fs = null;  
    
  /** @return DataFolder class */
//  protected Class[] cookieClasses () {
//    return new Class[] { DataFolder.class };
//  }

  protected boolean enable(Node[] nodes) {
    if (nodes == null) return false;
    DataFolder df = (DataFolder)nodes[0].getCookie (DataFolder.class);
      if (df != null && nodes.length==1) {
        FileObject fo = df.getPrimaryFile ();
        return (fo.isRoot());
      }
    return false;
  }

  protected void performAction (Node[] nodes)  {
      DataFolder df = (DataFolder)nodes[0].getCookie (DataFolder.class);
      if (df != null) {
        FileObject fo = df.getPrimaryFile ();
        try {
           FileSystem fs = fo.getFileSystem();
           if (fs instanceof RemoteFileSystem) 
               ((RemoteFileSystem)fs).connectOnBackground(!((RemoteFileSystem)fs).isConnected());
        }
        catch (FileStateInvalidException e) { 
          TopManager.getDefault().notifyException(e);
        }
      }
  }

//  protected int mode () {
//    return MODE_ALL;
//  }

  protected void setFS(RemoteFileSystem fs) {
    this.fs=fs;
  }

  public String getName () {
    if (fs==null) return "Connect/Disconnect";
    if (fs.isConnected()) return "Go Offline";
    else return "Go Online";
  }

  public HelpCtx getHelpCtx () {
    return HelpCtx.DEFAULT_HELP;
  }

}
