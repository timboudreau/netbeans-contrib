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

package org.netbeans.modules.portalpack.servers.sunps7;

import org.netbeans.modules.portalpack.servers.core.api.ConfigPanel;
import org.netbeans.modules.portalpack.servers.core.api.PSConfigPanelManager;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.ui.ClasspathConfigPanel;
import org.netbeans.modules.portalpack.servers.core.ui.InstallPanel;
import org.netbeans.modules.portalpack.servers.sunps7.ui.PS71ConfigPanel;

/**
 *
 * @author Satya
 */
public class PS71ConfigPanelManagerImpl implements PSConfigPanelManager{

    /**
     * Creates a new instance of PS71ConfigPanelManagerImpl
     */
    public PS71ConfigPanelManagerImpl() {
    }

    public InstallPanel[] getInstallPanels(String psVersion) {
         InstallPanel[] installPanels = new InstallPanel[1];
         installPanels[0] = new InstallPanel(new PS71ConfigPanel(),true);
        // installPanels[1] = new InstallPanel(new PSConfigServerPanel());
         return installPanels;
    }

    public ConfigPanel[] getConfigPanels(String psVersion) {
        return new ConfigPanel[]{new PS71ConfigPanel(),new ClasspathConfigPanel()};
    }

}
