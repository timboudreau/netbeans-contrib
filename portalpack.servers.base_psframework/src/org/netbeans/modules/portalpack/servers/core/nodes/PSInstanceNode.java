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

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.ui.PSCustomizerPanel;
import org.netbeans.modules.portalpack.servers.core.common.enterprise.NodeTypeConstants;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ShowAdminToolAction;
import java.awt.Component;
import java.awt.Label;
import javax.swing.JPanel;
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
public class PSInstanceNode extends AbstractNode implements Node.Cookie {
    
    private static String ICON_BASE = "org/netbeans/modules/portalpack/servers/core/resources/ServerInstanceIcon.gif"; // NOI18N
    private PSDeploymentManager manager;
    public PSInstanceNode(Lookup lookup) {
        super(new Children.Map());
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
        manager = (PSDeploymentManager)lookup.lookup(PSDeploymentManager.class);        
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(PSInstanceNode.class, "TXT_MyInstanceNode");
    }
    
    public String getShortDescription() {
        return manager.getUri(); // NOI18N
    }
    
    public PSDeploymentManager getDeploymentManager()
    {
        return manager;
    }
    
    public javax.swing.Action[] getActions(boolean context) {
        javax.swing.Action[]  newActions = new javax.swing.Action[2] ;
        newActions[0]=(null);        
        newActions[1]= (SystemAction.get(ShowAdminToolAction.class));
         
        return newActions;
    }
    
    public boolean hasCustomizer() {
        return true;
    }
    
    public Component getCustomizer() {
        JPanel panel = new PSCustomizerPanel(manager);
        return panel;
    }    
}
