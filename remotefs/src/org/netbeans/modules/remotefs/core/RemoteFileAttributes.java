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

import java.util.*;

/** Remote File attributes.
 * Class for storing file attributes.
 *
 * @author  Libor Martinek
 * @version 1.0
 */ 
public class RemoteFileAttributes   {

    private String name = null;
    private boolean isdirectory = true;
    private long size = 0;
    private java.util.Date date = new java.util.Date(0);
    private String accessPath = null;

    /** Creates new RemoteFileAttributes */
    public RemoteFileAttributes(String name,boolean isdirectory,long size,java.util.Date date, String accessPath) {
        this.name=name;
        this.isdirectory=isdirectory;
        this.size=size;
        this.date=date;
        this.accessPath=accessPath;
    }
   
    /** Creates empty RemoteFileAttributes */
    public RemoteFileAttributes() {
    }
    
    /** Creates RemoteFileAttributes specified with name and isdirectory flag */
    public RemoteFileAttributes(String name, boolean isdirectory) {
    	this.name = name;
        this.isdirectory = isdirectory;
    }	
    
    /** Set name of file */
    public void setName(String name) { this.name=name;  }
    /** Set whether it is directory */
    public void setIsDirectory(boolean dir) {  this.isdirectory=dir;   }
    /** Set size of file */
    public void setSize(long size) {  this.size=size;}
    /** Set date of last modification */
    public void setDate(Date date) {  this.date=date; }
    /** Set access path */
    public void setAccessPath(String path) {this.accessPath=path; }
    
    /** Tet name of file */
    public String getName() { return name; }
    /** Test whether it is directory */
    public boolean isDirectory() { return isdirectory; }
    /** Get size of file */
    public long getSize() { return size; }
    /** Get date of last modification */
    public Date getDate() { return date; }
    /** Get access path */
    public String getAccessPath() { return accessPath; }
    
}