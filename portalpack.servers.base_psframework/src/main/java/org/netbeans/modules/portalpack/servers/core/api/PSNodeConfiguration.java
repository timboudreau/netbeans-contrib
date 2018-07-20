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

import javax.swing.Action;
import org.openide.nodes.Node;

/**
 *
 * @author Satya
 */
public interface PSNodeConfiguration {
    
    public boolean showChannelNodes();

    public boolean showDnNodes();

    public boolean showPortletNodes();

    public boolean showContainerNodes();
    
    public boolean showTopChannelsNode();
    
    public boolean allowDragAndDrop();
    
    public Action[] getChannelActions(String channelType);

    public Action[] getDnActions();

    public Action[] getPortletActions();

    public Action[] getContainerActions();
    
    public Action[] getTopChannelsActions();
    
    public Action[] getChannelFolderActions();
    
    public Action[] getTopChannelFolderActions();
    
    public Node[] getCustomChildrenForDnNode(PSDeploymentManager dm,String baseDn,String key);

    public Node[] getCustomChildrenForContainerNode(PSDeploymentManager dm,String baseDn,String key);
    
    public Node[] getCustomChildrenForTopChannelsNode(PSDeploymentManager dm,String baseDn,String key);
    
    public Node[] getCustomChildrenForPortletNode(PSDeploymentManager dm,String baseDn,String key);
    
    public Node[] getCustomChildrenForChannelNode(PSDeploymentManager dm,String baseDn,String key);
    
    public Node[] getCustomChildrenForRootNode(PSDeploymentManager dm,String baseDn,String key);
}
