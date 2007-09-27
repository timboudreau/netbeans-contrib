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
import java.io.File;

/** Remote Client. Interface which all new clients must implement.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public interface RemoteClient {

  //public void setLogInfo(LogInfo loginfo);

  /** Returns file name object of the root.
  * @return  FileName of the root */
  public RemoteFileName getRoot();

  /** Connect to server.
  * @throws IOException  */
  public void connect () throws IOException ;
  
  /** Test whether client is connected to server.
  * @return true in case that client is connected to server */  
  public boolean isConnected();

  /** Compare this information.
   * @param loginfo login information to compare
   * @return 0 if login information are equal;
   *         1 if login information refer to the same resource but can't be uses to login;
   *           (e.g. server and username is same but password is different)
   *        -1 if login information are different
   */
  public int compare(LogInfo loginfo);
  
  /** Get file from server.
   * @param what file on host to receive
   * @param where new file to create
   * @throws IOException  */
  public  void get(RemoteFileName what, File where) throws IOException ;

  /** Put file to server.
   * @param what file to send
   * @param where where to file send
   * @throws IOException  */
  public void put(File what, RemoteFileName where) throws IOException ;
 
  /** Return list of files in directory
   * @param directory 
   * @throws IOException 
   * @return  directory to list*/
  public RemoteFileAttributes[] list(RemoteFileName directory) throws IOException ;
  
  /** Rename file 
   * @param oldname 
   * @param newname 
   * @throws IOException 
   */
  public void rename(RemoteFileName oldname, String newname) throws IOException ;

  /** Delete directory
   * @param name what file to delete
   * @throws IOException  */
  public void delete(RemoteFileName name) throws IOException ;
 
  /** Make directory
   * @param name of the new directory
   * @throws IOException  */
  public void mkdir(RemoteFileName name) throws IOException ;
  
  /** Remove directory
   * @param name of the directory to remove
   * @throws IOException  */
  public void rmdir(RemoteFileName name) throws IOException ;
  
  /** Log out from server and close connection */
  public void disconnect() ;
    
  /** Immediately close connection with server */
  public void close();
}