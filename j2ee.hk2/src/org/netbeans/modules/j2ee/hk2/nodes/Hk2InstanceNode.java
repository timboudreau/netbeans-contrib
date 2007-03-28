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


package org.netbeans.modules.j2ee.hk2.nodes;

import java.awt.Component;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.hk2.Hk2DeploymentManager;
import org.netbeans.modules.j2ee.hk2.Hk2J2eePlatformFactory;
import org.netbeans.modules.j2ee.hk2.customizer.Customizer;
import org.netbeans.modules.j2ee.hk2.customizer.CustomizerDataSupport;
import org.netbeans.modules.j2ee.hk2.ide.Hk2PluginProperties;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Ludo
 */
public class Hk2InstanceNode extends AbstractNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/j2ee/hk2/resources/server.gif"; // NOI18N
    private Lookup lookup;    
    public Hk2InstanceNode(Lookup lookup) {
        super(new Children.Array());
        getCookieSet().add(this);
        this.lookup = lookup;
        setIconBaseWithExtension(ICON_BASE);
    }
       
    public String getDisplayName() {
        return NbBundle.getMessage(Hk2InstanceNode.class, "TXT_MyInstanceNode");
    }
    
    public String getShortDescription() {
        return getAdminURL();
    }
    
    public javax.swing.Action[] getActions(boolean context) {
        return new javax.swing.Action[]{};
    }
    
    public boolean hasCustomizer() {
        return true;
    }
    
    public Component getCustomizer() {
        CustomizerDataSupport dataSup = new CustomizerDataSupport(getDeploymentManager());
        return new Customizer(dataSup, new Hk2J2eePlatformFactory().getJ2eePlatformImpl(getDeploymentManager()));
    }
    
        public Hk2DeploymentManager getDeploymentManager() {
        return ((Hk2DeploymentManager) lookup.lookup(Hk2DeploymentManager.class));
    }
    public String  getAdminURL() {
        InstanceProperties ip = getDeploymentManager().getProperties().getInstanceProperties();
        String host = ip.getProperty(Hk2PluginProperties.PROPERTY_HOST);
        String httpPort = ip.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
        return "http://" + host + ":" + httpPort ;
    }

}
