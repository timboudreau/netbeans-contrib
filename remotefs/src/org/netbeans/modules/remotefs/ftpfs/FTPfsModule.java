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

package org.netbeans.modules.remotefs.ftpfs;

import org.openide.modules.ModuleInstall;
import org.openide.filesystems.FileUtil;

/** FTP filesystem module class.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPfsModule extends ModuleInstall {
  static final long serialVersionUID = 2289861663533516525L;

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

