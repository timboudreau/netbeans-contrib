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

package org.netbeans.modules.portalpack.servers.core.api;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.InstanceListener;

/**
 * @author Satya
 */
public abstract class PSDeploymentFactory implements DeploymentFactory, InstanceListener {

    final private Map<String,DeploymentManager> dms = new HashMap<String,DeploymentManager>();
    private boolean instanceListenerAdded;

    public boolean handlesURI(String uri) {
        return uri != null && uri.startsWith(getURIPrefix());
    }
    
    public DeploymentManager getDeploymentManager(String uri, String uname, String passwd) throws DeploymentManagerCreationException {
        registerInstanceListener();
        if (!handlesURI(uri)) {
            throw new DeploymentManagerCreationException("Invalid URI:" + uri); // NOI18N
        }

        synchronized(dms) {
            DeploymentManager dm = (DeploymentManager)dms.get(uri);
            if(dm == null) {
                dm = getPSDeploymentManager(uri,getPSVersion());
                dms.put(uri,dm);
            }
            return dm;
        }
    }
    
    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
        registerInstanceListener();
        if (!handlesURI(uri)) {
            throw new DeploymentManagerCreationException("Invalid URI:" + uri); // NOI18N
        }
        synchronized(dms) {
            DeploymentManager dm = (DeploymentManager)dms.get(uri);
            if(dm == null) {
                dm = getPSDeploymentManager(uri,getPSVersion());
                dms.put(uri,dm);
            }
            return dm;
        }
    }
    
    public String getProductVersion() {
        return "0.1"; // NOI18N
    }

    private void registerInstanceListener() {
        synchronized(dms) {
            if(!instanceListenerAdded) {
                Deployment.getDefault().addInstanceListener(this);
                instanceListenerAdded = true;
            }
        }
    }

    public void instanceAdded(String serverInstanceID) {

    }

    public void instanceRemoved(String serverInstanceID) {
        synchronized (dms) {
            // serverInstanceID is really the URI of this installed server :)
            dms.remove(serverInstanceID);
        }
    }

    public abstract DeploymentManager getPSDeploymentManager(String uri,String psVersion);
    public abstract String getDisplayName();
    
    public abstract String getURIPrefix();
    public abstract String getPSVersion();
}
