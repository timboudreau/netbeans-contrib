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
package org.netbeans.modules.remotefs.ftpclient;

import org.netbeans.modules.remotefs.core.LogInfo;

/** FTPLogInfo stores login information
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class FTPLogInfo implements LogInfo {

    static final long serialVersionUID = 4795532037339960289L;
    /** Host name */
    private String host = "localhost";
    /** Port number */
    private int port = FTPClient.DEFAULT_PORT;
    /** User name */
    private String user = "anonymous";
    /** Password */
    private String password = "forteuser@";
    private String rootFolder = "/";
    private boolean passiveMode = false;

    /** Create empty LogInfo */
    public FTPLogInfo() {
    }

    /** Create LogInfo
     * @param host
     * @param port
     * @param user
     * @param password 
     */
    public FTPLogInfo(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    /** Set host name
     * @param host 
     */
    public void setHost(String host) {
        this.host = host;
    }

    public void setPassiveMode(boolean aBoolean) {
        this.passiveMode = aBoolean;
    }

    public boolean isPassiveMode() {
        return this.passiveMode;
    }

    /** Set port number
     * @param port 
     */
    public void setPort(int port) {
        this.port = port;
    }

    /** Set user name
     * @param user 
     */
    public void setUser(String user) {
        this.user = user;
    }

    /** Set password
     * @param password 
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Get host name
     * @return 
     */
    public String getHost() {
        return host;
    }

    /** Get port number
     * @return 
     */
    public int getPort() {
        return port;
    }

    /** Get user name
     * @return 
     */
    public String getUser() {
        return user;
    }

    /** Get password
     * @return 
     */
    public String getPassword() {
        return password;
    }

    /** Return human redable description of this LogInfo */
    public String displayName() {
        return "ftp://" + ((user != null && user.equalsIgnoreCase("anonymous")) ? "" : user + "@") +
                host + ((port == FTPClient.DEFAULT_PORT) ? "" : (":" + String.valueOf(port)));
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }
}
