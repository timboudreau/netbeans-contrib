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

import java.util.*;

/** Remote File attributes. Class for storing attributes for files from remote server.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class RemoteFileAttributes   {

    private RemoteFileName name = null;
    private boolean isdirectory = true;
    private long size = 0;
    private java.util.Date date = new java.util.Date(0);

    /** Creates new RemoteFileAttributes
     * @param name name
     * @param isdirectory whether it's directory
     * @param size size of file
     * @param date last modification date */
    public RemoteFileAttributes(RemoteFileName name, boolean isdirectory, long size, java.util.Date date) {
        this.name=name;
        this.isdirectory=isdirectory;
        this.size=size;
        this.date=date;
    }
   
    /** Creates empty RemoteFileAttributes */
    public RemoteFileAttributes() {
    }
    
    /** Creates RemoteFileAttributes specified with name and isdirectory flag
     * @param name name
     * @param isdirectory whether it's directory */
    public RemoteFileAttributes(RemoteFileName name, boolean isdirectory) {
    	this.name = name;
        this.isdirectory = isdirectory;
    }	
    
    /** Set name of file
     * @param name name */
    public void setName(RemoteFileName name) { this.name=name;  }
    
    /** Set whether it is directory
     * @param dir true if it's directory */
    public void setIsDirectory(boolean dir) {  this.isdirectory=dir;   }
    
    /** Set size of file
     * @param size size of file */
    public void setSize(long size) {  this.size=size;}
    
    /** Set date of last modification
     * @param date set last modification date */
    public void setDate(Date date) {  this.date=date; }
    
    /** Tet name of file
     * @return  name*/
    public RemoteFileName getName() { return name; }
    
    /** Test whether it is directory
      * @return true if it is directory */
    public boolean isDirectory() { return isdirectory; }
    
    /** Get size of file
     * @return  size*/
    public long getSize() { return size; }
 
    /** Get date of last modification
     * @return  last modification date*/
    public Date getDate() { return date; }
}