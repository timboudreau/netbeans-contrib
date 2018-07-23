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

package org.netbeans.modules.portalpack.servers.core.nodes;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.CreateContainerAction;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ShowAdminToolAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author Satya
 */
public class DnNode extends BaseNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/dn.gif"; // NOI18N
    private String key = "";
    private String displayValue = "";
    private PSDeploymentManager manager;
    public DnNode()
    {
       super(new Children.Array());   
    }
    public DnNode(PSDeploymentManager manager, Lookup lookup,String key,String displayValue) {
       
        super(new DnChildrenNode(manager,lookup,NodeTypeConstants.ORGANIZATION,key));
        this.key = key;
        this.displayValue = displayValue;
        this.manager = manager;
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
        setDisplayName(displayValue);
        setShortDescription(getShortDescription());
        getCookieSet().add(this);    
    }
        
    public PSDeploymentManager getDeploymentManager()
    {
        return manager;
    }
    
    public String getShortDescription() {
        return key; // NOI18N
    }
    
    public String getDn()
    {
        return key;
    }
    
    public javax.swing.Action[] getActions(boolean context) {
        
        if(manager != null)
            return manager.getPSNodeConfiguration().getDnActions();
        javax.swing.Action[]  newActions = new javax.swing.Action[1] ;
        newActions[0]=(null);             
        return newActions;
    }
    
    public boolean hasCustomizer() {
        return true;
    }

    public String getKey() {
        return key;
    }
    
    public String getType() {
        return NodeTypeConstants.ORGANIZATION;
    }        
}
