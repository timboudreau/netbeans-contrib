/* The contents of this file are subject to the terms of the Common Development
/* and Distribution License (the License). You may not use this file except in
/* compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
/* or http://www.netbeans.org/cddl.txt.
/*
/* When distributing Covered Code, include this CDDL Header Notice in each file
/* and include the License file at http://www.netbeans.org/cddl.txt.
/* If applicable, add the following below the CDDL Header, with the fields
/* enclosed by brackets [] replaced by your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
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
  static final long serialVersionUID = -7910677883191530621L;
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
