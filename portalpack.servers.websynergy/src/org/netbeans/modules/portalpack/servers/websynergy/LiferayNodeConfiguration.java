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

package org.netbeans.modules.portalpack.servers.websynergy;

import javax.swing.Action;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSNodeConfiguration;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.websynergy.common.LiferayConstants;
import org.netbeans.modules.portalpack.servers.websynergy.nodes.HookHolderNode;
import org.netbeans.modules.portalpack.servers.websynergy.nodes.ThemeHolderNode;
import org.openide.nodes.Node;

/**
 *
 * @author Satya
 */
public class LiferayNodeConfiguration extends DefaultPSNodeConfiguration{
    
    private static LiferayNodeConfiguration instance;
    /** Creates a new instance of LifeRayNodeConfiguration */
    private LiferayNodeConfiguration() {
    }
    
    public static PSNodeConfiguration getInstance(){
        
        if(instance == null)
        {
            synchronized(LiferayNodeConfiguration.class)
            {
                if(instance == null)
                    instance = new LiferayNodeConfiguration();
                
            }
            
        }
        return instance;
    }
    
    public boolean showContainerNodes() {
        return false;
    }
    
    public boolean showTopChannelsNode() {
        return false;
    }  
    
    public Action[] getDnActions() {
        
        javax.swing.Action[]  newActions = new javax.swing.Action[1] ;
        newActions[0]=(null);  
        //newActions[1] = SystemAction.get(ShowMultiplePortletsAction.class);
        return newActions;
    }

    @Override
    public Node[] getCustomChildrenForDnNode(PSDeploymentManager dm, String baseDn, String key) {
        PSConfigObject psconfig = dm.getPSConfig();
        String lrVersionStr = psconfig.getProperty(LiferayConstants.LR_VERSION);
        int liferayVersion = -1;
        try {
          liferayVersion = Integer.parseInt(lrVersionStr);
        } catch (Exception e) {
        }

        if(liferayVersion >= 5203) {
            return new Node[]{new HookHolderNode(dm, key), new ThemeHolderNode(dm, key)};
        }
        return new Node[]{new HookHolderNode(dm, key)};
    }

}
