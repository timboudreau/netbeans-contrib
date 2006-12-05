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

import org.netbeans.modules.portalpack.servers.core.ui.InstallPanel;

/**
 * This interface can be implemented to return configuration panels which can be
 * shown during during server addition and server customization.
 *
 * @author Satya
 */
public interface PSConfigPanelManager {
    
    /**
     * Implements this method to return array of InstallPanel which are
     * shown when a server instance is added.
     * @param portal server version
     * @return array of InstallPanel which are shown when a new portal server instance
     * gets added.
     */
    public InstallPanel[]getInstallPanels(String psVersion);
    
    /**
     * Implements this method to return a array of ConfigPanel which are shown when
     * the customization panel is shown for a already added portal server instance.
     *
     * @param portal server version
     * @return array of ConfigPanel which are shown during customization.
     */
    public ConfigPanel[] getConfigPanels(String psVersion);
    
}
