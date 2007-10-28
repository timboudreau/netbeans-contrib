/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.fileopenserver;

import org.openide.modules.ModuleInstall;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class FileOpenServerInstaller extends ModuleInstall {
   
    /** Creates a new instance of FileOpenServerInstaller */
    public FileOpenServerInstaller() {
    }
    
    public void restored() {
        if (FileOpenServer.getFileOpenServerSettings().isStartAtStartup()) {
            FileOpenServer fileOpenServer = FileOpenServer.getFileOpenServer();
            fileOpenServer.setFileOpenRequestListener(new FileOpenRequestListenerImpl());
            fileOpenServer.startServer();
        }
    }
    
    public boolean closing() {
        FileOpenServer fileOpenServer = FileOpenServer.getFileOpenServer();
        if (fileOpenServer.isStarted()) {
            fileOpenServer.stopServer();
        }
        return true;
    }
}
