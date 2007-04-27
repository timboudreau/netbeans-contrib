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

package org.netbeans.modules.j2ee.oc4j;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.DatasourceManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.OptionalDeploymentManagerFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.netbeans.modules.j2ee.oc4j.config.ds.OC4JDatasourceManager;
import org.netbeans.modules.j2ee.oc4j.ide.OC4JStartServer;
import org.netbeans.modules.j2ee.oc4j.ui.wizards.OC4JInstantiatingIterator;
import org.netbeans.modules.j2ee.oc4j.util.OC4JDebug;
import org.openide.WizardDescriptor.InstantiatingIterator;

/**
 * @author pblaha
 */
public class OC4JOptionalDeploymentManagerFactory extends OptionalDeploymentManagerFactory {
    
    public StartServer getStartServer(DeploymentManager dm) {
        return new OC4JStartServer(dm);
    }
    
    public IncrementalDeployment getIncrementalDeployment(DeploymentManager dm) {
        OC4JDebug.log(getClass().getName(), "Incremental deployment isn't supported yet."); // NOI18N
        return null;
    }
    
    public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        assert OC4JDeploymentManager.class.isAssignableFrom(dm.getClass()) :
            this + " can't use this deployment manager: " + dm; // NOI18N
        return new OC4JFindJSPServlet((OC4JDeploymentManager) dm);
    }
    
    public InstantiatingIterator getAddInstanceIterator() {
        return new OC4JInstantiatingIterator();
    }
    
    public DatasourceManager getDatasourceManager(DeploymentManager dm) {
        return new OC4JDatasourceManager(dm);
    }
}