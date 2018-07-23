
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
public final class GDEPreferences implements Cloneable {

    protected String javaHome;
    protected String antHome;
    protected String gdeFolder;

    // Following are the parameters for the deployment of the 
    protected String appserverHome;
    protected String portalServerHome;
    protected String dirServerUserDN;
    
    /** Creates a new instance of GDEPreferences */
    public GDEPreferences() {}
    
    
    public GDEPreferences(String javaHome, String antHome, String gdeFolder, 
            String appServerHome, String portalServerHome, 
            String dirServerUserDN) {        
        this.javaHome = javaHome;
        this.antHome = antHome;
        this.gdeFolder = gdeFolder;
        this.appserverHome = appServerHome;
        this.dirServerUserDN = dirServerUserDN;
        this.portalServerHome = portalServerHome;        
    }
        
    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }
    
    public String getJavaHome() {
        return this.javaHome;
    }
    
    public void setAntHome(String antHome) {
        this.antHome = antHome;
    }
    
    public String getAntHome() {
        return this.antHome;
    }
    
    public void setGdeFolder(String gdeFolder) {
        this.gdeFolder = gdeFolder;
    }
    public String getGdeFolder() {
        return this.gdeFolder;
    }

    public String getAppserverHome() {
        return appserverHome;
    }
    
    public void setAppserverHome(String appserverHome) {
        this.appserverHome = appserverHome;
    }

    public String getDirServerUserDN() {
        return dirServerUserDN;
    }

    public void setDirServerUserDN(String dirServerUserDN) {
        this.dirServerUserDN = dirServerUserDN;
    }

    public String getPortalServerHome() {
        return portalServerHome;
    }

    public void setPortalServerHome(String portalServerHome) {
        this.portalServerHome = portalServerHome;
    }
    
    
    // Getter methods for the sub-folders of the GDE folder
    
    public String getPortletTemplatesFolder() { 
        return gdeFolder + "/portlet-templates";
    }
    
    public String getPortletConfigFilesFolder() {
        return gdeFolder + "/portlet-config-files";
    }
    
    public String getGeneratedFilesFolder() {
        return gdeFolder + "/gen-files";
    }
    
    // End of getter methods for the sub-folders of the GDE folder
    
    /**
     * Creates a clone of this object and returns.
     */
    public Object clone() {
        return new GDEPreferences(this.javaHome, 
                this.antHome, this.gdeFolder, this.appserverHome, 
                this.portalServerHome, this.dirServerUserDN);
    }
    
    public String toString() {        
        StringBuffer buffer = new StringBuffer();
        buffer.append("Java Home: '");
        buffer.append(this.javaHome);
        buffer.append("', Ant Home: '");
        buffer.append(this.antHome);
        buffer.append("', GDE Folder: '");
        buffer.append(this.gdeFolder);
        buffer.append("', App Server Home: '");
        buffer.append((this.appserverHome == null) ?
                        "" : this.appserverHome);        
        buffer.append("', Portal Server Home: '");
        buffer.append((this.portalServerHome == null) ? 
                        "" : this.portalServerHome);        
        buffer.append("', Dir Server User DN: '");
        buffer.append((this.dirServerUserDN == null) ? 
                    "" : this.dirServerUserDN);                
        return buffer.toString();
    }
}
