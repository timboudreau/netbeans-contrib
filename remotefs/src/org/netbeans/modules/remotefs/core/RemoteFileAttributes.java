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

import java.util.Date;


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