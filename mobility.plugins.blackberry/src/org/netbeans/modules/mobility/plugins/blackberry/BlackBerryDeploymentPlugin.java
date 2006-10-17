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

/*
 * BlackBerryDeploymentPlugin.java
 *
 */
package org.netbeans.modules.mobility.plugins.blackberry;

import java.awt.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.spi.mobility.deployment.DeploymentPlugin;
import org.openide.util.NbBundle;

/**
 *
 * @author Adam Sotona
 */
public class BlackBerryDeploymentPlugin implements DeploymentPlugin {

    static final String PROP_USB = "deployment.blackberry.usb"; //NOI18N
    static final String PROP_PORT_PIN = "deployment.blackberry.portpin"; //NOI18N
    static final String PROP_BAUD_RATE = "deployment.blackberry.baudrate"; //NOI18N
    static final String PROP_PASSWORD = "deployment.blackberry.password"; //NOI18N
    
    final Map propertyDefValues;
    
    /** Creates a new instance of BlackBerryDeploymentPlugin */
    public BlackBerryDeploymentPlugin() {
        HashMap m = new HashMap();
        m.put(PROP_USB, Boolean.TRUE);//NOI18N
        m.put(PROP_PORT_PIN, "");//NOI18N
        m.put(PROP_BAUD_RATE, "");//NOI18N
        m.put(PROP_PASSWORD, "");//NOI18N
        propertyDefValues = Collections.unmodifiableMap(m);
    }

    public String getDeploymentMethodName() {
        return "BlackBerry"; //NOI18N
    }

    public String getDeploymentMethodDisplayName() {
        return NbBundle.getMessage(BlackBerryDeploymentPlugin.class, "LBL_BB_Method_Name"); //NOI18N
    }

    public String getAntScriptLocation() {
        return "modules/scr/deploy-blackberry-impl.xml"; // NOI18N
    }

    public Map getGlobalPropertyDefaultValues() {
        return propertyDefValues;
    }

    public Component createGlobalCustomizerPanel() {
        return new BlackBerryCustomizerPanel();
    }

    public Map<String, Object> getProjectPropertyDefaultValues() {
        return Collections.EMPTY_MAP;
    }

    public Component createProjectCustomizerPanel() {
        return null;
    }
    
}
