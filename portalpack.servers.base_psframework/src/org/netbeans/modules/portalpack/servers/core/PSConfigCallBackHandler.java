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
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.servers.core;

import java.util.Properties;

/**
 *
 * @author Satya
 */
public interface PSConfigCallBackHandler {
    public static String ADD_PROP_PREFIX = "ADD_PROP_";
    
    public void setPSHome(String pHome);
    public String getPSHome();
   
    public String getAdminUser();
    public void setAdminUser(String adUser);
    public String getAdminPassWord();
    public void setAdminPassWord(String password);
    public String getPortalId();
    public void setPortalId(String id);
    
    public String getServerHome();
    
    public void setServerHome(String home);
    
    public void setServerType(String type);
    public String getServerType();
    
    public String getIntanceId();
    
    public void setInstanceId(String instance);
    
    public String getDefaultDomain();
    
    public void setDefaultDomain(String dd);
      
    public boolean isRemote();
    public void setRemote(boolean flag);
    public String getHost();
    
    public void setHost(String h);
    
    public String getPort();
    
    public void setPort(String po);
    
    
    public void setAdminPort(String port);
    public String getAdminPort();
    
    public void setDomainDir(String dir);
    public String getDomainDir();
    
    public void setUri(String uri);
    public String getUri();
    
    public void setClassPath(String classpath);
    public String getClassPath();
    
    public void setPortalUri(String uri);
    public String getPortalUri();
    
    public void setProperty(String key,String value);
    public String getProperty(String key);
    
    public Properties getProperties();
    
}
