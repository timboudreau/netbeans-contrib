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

package org.netbeans.modules.remotefs.ftpfs;

import org.openide.modules.ModuleInstall;
import org.openide.filesystems.FileUtil;

/** FTP filesystem module class.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPfsModule extends ModuleInstall {
  public FTPfsModule() {
    // A public default constructor is required!
    // Of course, Java makes one by default for a public class too.
  }

  public void installed() {
    // This module has been installed for the first time! Notify authors.
    // Handle setup within this session too:
    restored();
  }

  public void restored() {
    //FileUtil.setMIMEType("test", "text/x-clipboard-content-test");
  }

  public void uninstalled() {
    // Do not need to do anything special on uninstall.
    // Action will already be removed from Edit menu automatically.
  }

  public boolean closing() {
    // Ask the user to save any open, modified clipboard contents.
    // If the user selects "Cancel" on one of these dialogs, don't exit yet!
    //return DisplayClipboardAction.askAboutExiting();
    return true;
  }
}

