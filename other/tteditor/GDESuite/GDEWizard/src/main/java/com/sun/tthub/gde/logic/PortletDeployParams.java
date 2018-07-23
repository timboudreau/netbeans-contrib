
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder. *
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
