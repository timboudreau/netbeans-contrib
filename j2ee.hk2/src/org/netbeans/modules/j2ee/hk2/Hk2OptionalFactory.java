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

import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.spi.IncrementalDeployment;
import org.netbeans.modules.j2ee.deployment.plugins.spi.OptionalDeploymentManagerFactory;
import org.netbeans.modules.j2ee.deployment.plugins.spi.StartServer;
import org.netbeans.modules.j2ee.hk2.ide.FastDeploy;
import org.openide.WizardDescriptor.InstantiatingIterator;

/**
 *
 * @author ludo
 */
public class Hk2OptionalFactory extends OptionalDeploymentManagerFactory {
    
    public StartServer getStartServer(DeploymentManager dm) {
        return new Hk2StartServer(dm);
    }
    
    public IncrementalDeployment getIncrementalDeployment(DeploymentManager dm) {
        return new FastDeploy(dm);
    }
    
    public FindJSPServlet getFindJSPServlet(DeploymentManager dm) {
        return null;
    }
    
    public InstantiatingIterator getAddInstanceIterator() {
        return new  org.netbeans.modules.j2ee.hk2.wizards.Hk2InstantiatingIterator();
    }
    
}
