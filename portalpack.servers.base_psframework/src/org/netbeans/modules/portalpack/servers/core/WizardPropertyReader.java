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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.openide.WizardDescriptor;

/**
 *
 * @author Satya
 */
public class WizardPropertyReader implements PSConfigCallBackHandler{
    
    
    private WizardDescriptor desc;
    private Properties _addProperties;
    
    private static String ADD_PP_LIST_KEY = "ADD_PROP_LIST_KEY";
    private List addPropNameList;
    /** Creates a new instance of WizardPropertyReader */
    public WizardPropertyReader(WizardDescriptor d) {
        this.desc = d;
        addPropNameList = (List)desc.getProperty(ADD_PP_LIST_KEY);
        if(addPropNameList == null)
        {
            addPropNameList = new ArrayList();
            desc.putProperty(ADD_PP_LIST_KEY,addPropNameList);
        }
    }
    
    
    public void setProperty(String propName,String value)
    {
        propName = ADD_PROP_PREFIX + propName;
        desc.putProperty(propName,value);
        
        addPropNameList.add(propName);
    }
    
    public String getProperty(String propName)
    {
        propName = ADD_PROP_PREFIX + propName;
        return (String)desc.getProperty(propName);
    }
    
    private void _setProperty(String propName,String value)
    {
        
        desc.putProperty(propName,value);
    }
    
    private String _getProperty(String propName)
    {
        
        return (String)desc.getProperty(propName);
    }
    
    private boolean _getBooleanProperty(String propName)
    {
        try{
            return Boolean.valueOf((String)desc.getProperty(propName)).booleanValue();
        }catch(Exception e){return false;}
    }
    
    private void _setBooleanProperty(String propName,boolean value)
    {
        desc.putProperty(propName,Boolean.toString(value));
    }

    public void setPSHome(String pHome) {
        _setProperty("PS_HOME",pHome);
    }

    public String getPSHome() {
        return _getProperty("PS_HOME");
    }

    public String getAdminUser() {
        return _getProperty("ADMIN_USER");
    }

    public void setAdminUser(String adUser) {
         _setProperty("ADMIN_USER",adUser);
    }

    public String getAdminPassWord() {
        return _getProperty("ADMIN_PASSWORD");
    }

    public void setAdminPassWord(String password) {
        _setProperty("ADMIN_PASSWORD",password);
    }

    public String getPortalId() {
        return _getProperty("PORTAL_ID");
    }

    public void setPortalId(String id) {
        _setProperty("PORTAL_ID",id);
    }

    public String getServerHome() {
        return _getProperty("SERVER_HOME");
    }

    public void setServerHome(String home) {
        _setProperty("SERVER_HOME",home);
    }

    public void setServerType(String type) {
        _setProperty("SERVER_TYPE",type);
    }

    public String getServerType() {
        return _getProperty("SERVER_TYPE");
    }

    public String getIntanceId() {
        return _getProperty("INSTANCE_ID");
    }

    public void setInstanceId(String instance) {
        _setProperty("INSTANCE_ID",instance);
    }

    public String getDefaultDomain() {
        return _getProperty("DEFAULT_DOMAIN");
    }

    public void setDefaultDomain(String dd) {
        _setProperty("DEFAULT_DOMAIN",dd);
    }

    public boolean isRemote() {
        return _getBooleanProperty("IS_REMOTE");
    }

    public void setRemote(boolean flag) {
        _setBooleanProperty("IS_REMOTE",flag);
    }

    public String getHost() {
        return _getProperty("HOST");
    }

    public void setHost(String h) 
    {
        _setProperty("HOST",h);
    }

    public String getPort() {
        return _getProperty("PORT");
    }

    public void setPort(String po) {
        _setProperty("PORT",po);
    }

    public void setAdminPort(String port) {
        _setProperty("ADMIN_PORT",port);
    }

    public String getAdminPort() {
        return _getProperty("ADMIN_PORT");
    }

    public void setDomainDir(String dir) {
        _setProperty("DOMAIN_DIR",dir);
    }

    public String getDomainDir() {
        return _getProperty("DOMAIN_DIR");
    }
    
    public void setClassPath(String classpath)
    {
        _setProperty("CLASSPATH",classpath);
    }
    
    public String getClassPath()
    {
        return _getProperty("CLASSPATH");
    }
    
    public String getUri()
    {
        return _getProperty("URI");
    }
    
    public void setUri(String uri)
    {
        _setProperty("URI",uri);
    }
    
    public String getPortalUri()
    {
        return _getProperty("PORTAL_URI");
    }
    
    public void setPortalUri(String uri)
    {
        _setProperty("PORTAL_URI",uri);
    }

    public Properties getProperties() {
        
        Properties prop = new Properties();
        
        if(addPropNameList == null || addPropNameList.size() == 0)
            return prop;
        
        for(int i=0;i<addPropNameList.size();i++)
        {
            String value = (String)desc.getProperty((String)addPropNameList.get(i));
            if(value == null)
                continue;
            prop.put((String)addPropNameList.get(i),value);
        }
    
        return prop;
    }
}
