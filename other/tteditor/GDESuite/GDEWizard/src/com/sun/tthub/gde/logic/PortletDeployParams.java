
/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package com.sun.tthub.gde.logic;

/**
 *
 * @author Hareesh Ravindran
 */
public final class PortletDeployParams {
    
    private String portalServerHome;
    private String dirServerUserDN;
    private String dirServerUserPwd;
    private String webContainerPwd;
    
    /** Creates a new instance of PortletDeployParams */    
    public PortletDeployParams() {}
    
    public PortletDeployParams(String portalServerHome, 
            String dirServerUserDN, String dirServerUserPwd, 
            String webContainerPwd) {
            this.portalServerHome = portalServerHome;
            this.dirServerUserDN = dirServerUserDN;
            this.dirServerUserPwd = dirServerUserPwd;
            this.webContainerPwd = webContainerPwd;
    }

    public String getPortalServerHome() { return portalServerHome; }

    public void setPortalServerHome(String portalServerHome) 
        { this.portalServerHome = portalServerHome; }

    public String getDirServerUserDN() { return dirServerUserDN; }

    public void setDirServerUserDN(String dirServerUserDN) 
        { this.dirServerUserDN = dirServerUserDN; }

    public String getDirServerUserPwd() { return dirServerUserPwd; }

    public void setDirServerUserPwd(String dirServerUserPwd) 
        { this.dirServerUserPwd = dirServerUserPwd; }

    public String getWebContainerPwd() { return webContainerPwd; }

    public void setWebContainerPwd(String webContainerPwd) 
        { this.webContainerPwd = webContainerPwd; }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Portal Serv. Home: '");
        buffer.append(this.portalServerHome);
        buffer.append("', Dir. Serv. User DN: '");
        buffer.append(this.dirServerUserDN);
        buffer.append("', Dir. Serv. User Pwd: '");
        buffer.append(this.dirServerUserPwd);
        buffer.append("', Web Container Pwd: '");
        buffer.append(this.webContainerPwd);
        return buffer.toString();
    }
}
