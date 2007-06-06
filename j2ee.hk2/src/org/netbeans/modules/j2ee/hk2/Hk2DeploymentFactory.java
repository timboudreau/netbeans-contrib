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
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.openide.util.NbBundle;



/**
 *
 * @author Ludo
 */
public class Hk2DeploymentFactory implements DeploymentFactory {
    
    /**
     * 
     */
    public static final String URI_PREFIX = "deployer:hk2"; // NOI18N
    private static DeploymentFactory instance;
    
    /**
     * 
     * @return 
     */
    public static synchronized DeploymentFactory create() {
        if (instance == null) {
            instance = new Hk2DeploymentFactory();
            DeploymentFactoryManager.getInstance().registerDeploymentFactory(instance);
        }
        return instance;
    }
    
    /**
     * 
     * @param uri 
     * @return 
     */
    public boolean handlesURI(String uri) {
        if (uri==null){
            return false;
        }
        if(uri.startsWith("[")){//NOI18N
            if (uri.indexOf(URI_PREFIX)!=-1){
                return true;
            }
        }
        
        
        return false;
    }
    
    private static File getServerLocationFromURI(String uri) throws DeploymentManagerCreationException{
        
        if(uri.startsWith("[")){//NOI18N
            String loc = uri.substring(1,uri.indexOf("]"));
            return  new File(loc);
        }
    return null;
    }
    
    private static String getRealURI(String uri) throws DeploymentManagerCreationException{
        if(uri.startsWith("[")){//NOI18N
            return uri.substring(uri.indexOf("]")+1,uri.length());
        }
        return uri;// the old one.
    }
    
    /**
     * 
     * @param uri 
     * @param uname 
     * @param passwd 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException 
     */
    public DeploymentManager getDeploymentManager(String uri, String uname, String passwd) throws DeploymentManagerCreationException {
        if (!handlesURI(uri)) {
            throw new DeploymentManagerCreationException("Invalid URI:" + uri); // NOI18N
        }
        return new Hk2DeploymentManager(uri,uname,passwd);
        
    }
    
    /**
     * 
     * @param uri 
     * @return 
     * @throws javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException 
     */
    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
        if (!handlesURI(uri)) {
            throw new DeploymentManagerCreationException("Invalid URI:" + uri); // NOI18N
        }
        return new Hk2DeploymentManager(uri,null,null);
        
    }
    
    /**
     * 
     * @return 
     */
    public String getProductVersion() {
        return "0.1"; // NOI18N
    }
    
    /**
     * 
     * @return 
     */
    public String getDisplayName() {
        return NbBundle.getMessage(Hk2DeploymentFactory.class, "TXT_DisplayName"); // NOI18N
    }
}
