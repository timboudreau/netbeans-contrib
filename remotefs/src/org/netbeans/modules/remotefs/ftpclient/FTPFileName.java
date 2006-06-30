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

package org.netbeans.modules.remotefs.ftpclient;

import org.netbeans.modules.remotefs.core.RemoteFileName;

/**
 *
 * @author  lmartinek
 * @version
 */
public class FTPFileName implements RemoteFileName {

    private String name;
    private String directory;

    /** Creates new FTPFileName
     * @param directory
     * @param name  */
    protected FTPFileName(String directory, String name) {
        this.name = name;
        this.directory = directory;
    }

    /** Get the name. Only last name is returned, not whole path
     * @return  name of this object */
    public String getName() {
        return name;
    }
    
    /** Set new name. Used for renaming. Only name is chnaged, path remains.
     * @param newname  new name */
    public void setName(String newname) {
        name = newname;
    }
    
    /** Get full name (with whole path).
     * @return  full name*/
    public String getFullName() {
        return (directory.equals("/")?"":directory) + (name.equals("/")?"":"/")  +name;
    }
    
    /** Get directory of this filename
     * @return directory of this filename */    
    protected String getDirectory() {
        return directory;   
    }

    /** Create new name object under this name object.
     * @param name name of new name object
     * @return created name object */ 
    public RemoteFileName createNew(String name) { 
        return new FTPFileName(getFullName(),name);
    }
    
    /** Get root
     * @return root */    
    public static RemoteFileName getRoot() {
        return new FTPFileName("","/");
    }
    
}
