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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.enterprise.deploy.model.DDBean;
import javax.enterprise.deploy.model.DDBeanRoot;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.model.XpathEvent;
import javax.enterprise.deploy.model.XpathListener;
import javax.enterprise.deploy.spi.DConfigBeanRoot;
import javax.enterprise.deploy.spi.DeploymentConfiguration;
import javax.enterprise.deploy.spi.exceptions.BeanNotFoundException;
import javax.enterprise.deploy.spi.exceptions.ConfigurationException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


public class PSConfiguration implements DeploymentConfiguration, XpathListener {
    
    private DeployableObject deplObj;
    
    public PSConfiguration (DeployableObject deplObj) {
        this.deplObj = deplObj;
    }
    
    public void init(File file) {
        
        
    }
    
    public String getContextPath() throws ConfigurationException {
        return "";
        // TODO: replace this with reading the context path from the server specific DD
    }
    
    public void setContextPath(String contextPath) throws ConfigurationException {
        // TODO: here put the code that will store the context path in the server specific DD
    }
        
    // XpathListener implementation -------------------------------------------
    
    public void fireXpathEvent(XpathEvent xpe) {
        DDBean eventDDBean = xpe.getBean();
        if ("/web-app/resource-ref".equals(eventDDBean.getXpath())) { // NIO18N
            // new resource reference added
            if (xpe.isAddEvent()) {
                String[] name = eventDDBean.getText("res-ref-name"); // NOI18N
                String[] type = eventDDBean.getText("res-type");     // NOI18N
                String[] auth = eventDDBean.getText("res-auth");     // NOI18N
                // TODO: take appropriate steps here
            }
        }
    }
    
    // JSR-88 methods ---------------------------------------------------------
    
    public DeployableObject getDeployableObject () {
        return deplObj;
    }
    
    public void save(OutputStream os) throws ConfigurationException {   
    }
    
    public DConfigBeanRoot getDConfigBeanRoot (DDBeanRoot dDBeanRoot) 
    throws ConfigurationException {
        return null;
    }
    
    public void removeDConfigBean (DConfigBeanRoot dConfigBeanRoot) 
    throws BeanNotFoundException {
    }
    
    public void restore (InputStream is) 
    throws ConfigurationException {
    }
    
    public DConfigBeanRoot restoreDConfigBean (InputStream is, DDBeanRoot dDBeanRoot) 
    throws ConfigurationException {
        return null;
    }
    
    public void saveDConfigBean (OutputStream os, DConfigBeanRoot dConfigBeanRoot) 
    throws ConfigurationException {
    }
}
