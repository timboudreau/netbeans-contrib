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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.j2ee.hk2;

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


/**
 * 
 * @author ludo
 */
public class Hk2Configuration implements DeploymentConfiguration, XpathListener {
    
    private DeployableObject deplObj;
    private String contextPath;
    /**
     * 
     * @param deplObj 
     */
    public Hk2Configuration (DeployableObject deplObj) {
        this.deplObj = deplObj;
    }
    
    /**
     * 
     * @param file 
     */
    public void init(File file) {
        try {
            FileObject folder = FileUtil.toFileObject(file.getParentFile());
            if (folder == null) {
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "The parent folder does not exist!"); // NOI18N
                return;
            }
            PrintWriter pw = null;
            FileLock lock = null;
            try {
                String name = file.getName();
                FileObject fo = folder.getFileObject(name);
                if (fo == null) {
                    fo = folder.createData(name);
                }
                lock = fo.lock();
                pw = new PrintWriter(new OutputStreamWriter(fo.getOutputStream(lock)));
                pw.println("<MyServer path=\"/mypath\"/>"); // NOI18N
            } finally {
                if (pw != null) {
                    pw.close();
                }
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        
        // web.xml represented as DDBean model
        DDBeanRoot root = deplObj.getDDBeanRoot();
        if (root != null) {
            // here we will listen to resource reference changes
            root.addXpathListener("/web-app/resource-ref", this); // NOI18N
        }
    }
    
    /**
     * 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.ConfigurationException 
     */
    public String getContextPath() throws ConfigurationException {
        // TODO: replace this with reading the context path from the server specific DD
        return this.contextPath;
    }
    
    /**
     * 
     * @param contextPath 
     * @throws javax.enterprise.deploy.spi.exceptions.ConfigurationException 
     */
    public void setContextPath(String contextPath) throws ConfigurationException {
        // TODO: here put the code that will store the context path in the server specific DD
        this.contextPath = contextPath;
    }
        
    // XpathListener implementation -------------------------------------------
    
    /**
     * 
     * @param xpe 
     */
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
    
    /**
     * 
     * @return 
     */
    public DeployableObject getDeployableObject () {
        System.out.println("in getDeployableObject" +deplObj);
        return deplObj;
    }
    
    /**
     * 
     * @param os 
     * @throws javax.enterprise.deploy.spi.exceptions.ConfigurationException 
     */
    public void save(OutputStream os) throws ConfigurationException {   
    }
    
    /**
     * 
     * @param dDBeanRoot 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.ConfigurationException 
     */
    public DConfigBeanRoot getDConfigBeanRoot (DDBeanRoot dDBeanRoot) 
    throws ConfigurationException {
        return null;
    }
    
    /**
     * 
     * @param dConfigBeanRoot 
     * @throws javax.enterprise.deploy.spi.exceptions.BeanNotFoundException 
     */
    public void removeDConfigBean (DConfigBeanRoot dConfigBeanRoot) 
    throws BeanNotFoundException {
    }
    
    /**
     * 
     * @param is 
     * @throws javax.enterprise.deploy.spi.exceptions.ConfigurationException 
     */
    public void restore (InputStream is) 
    throws ConfigurationException {
    }
    
    /**
     * 
     * @param is 
     * @param dDBeanRoot 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.ConfigurationException 
     */
    public DConfigBeanRoot restoreDConfigBean (InputStream is, DDBeanRoot dDBeanRoot) 
    throws ConfigurationException {
        return null;
    }
    
    /**
     * 
     * @param os 
     * @param dConfigBeanRoot 
     * @throws javax.enterprise.deploy.spi.exceptions.ConfigurationException 
     */
    public void saveDConfigBean (OutputStream os, DConfigBeanRoot dConfigBeanRoot) 
    throws ConfigurationException {
    }
}
