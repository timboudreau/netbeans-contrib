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
 * WS70ResourcesRootNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

import java.util.Collection;

import org.netbeans.modules.j2ee.sun.ws7.j2ee.ResourceType;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.RefreshResourcesAction;

/**
 *
 * @author Mukesh Garg
 */
public class WS70ResourcesRootNode extends AbstractNode implements Node.Cookie{
    
    /**
     * Creates a new instance of WS70ResourcesRootNode 
     */
    public WS70ResourcesRootNode(Lookup lookup, ResourceType resType) {
        super(new WS70ResourceChildren(lookup, resType));
        getCookieSet().add(this);        
        if(resType.eqauls(resType.JDBC)){
            setDisplayName(NbBundle.getMessage(WS70ResourcesRootNode.class, "LBL_JDBC_RESOURCE"));            
            setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/JdbcResIcon.gif");            
            
        }else if(resType.eqauls(resType.JNDI)){
            setDisplayName(NbBundle.getMessage(WS70ResourcesRootNode.class, "LBL_JNDI_RESOURCE"));         
            setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/JndiResIcon.gif");            
            
        }else if(resType.eqauls(resType.CUSTOM)){
            setDisplayName(NbBundle.getMessage(WS70ResourcesRootNode.class, "LBL_CUSTOM_RESOURCE"));        
            setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/JndiResIcon.gif");           
            
        }else if(resType.eqauls(resType.MAIL)){
            setDisplayName(NbBundle.getMessage(WS70ResourcesRootNode.class, "LBL_MAIL_RESOURCE"));   
            setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/MailResIcon.gif");             
        }
    }

    public javax.swing.Action[] getActions(boolean context) {
        return new SystemAction[] {
            SystemAction.get(RefreshResourcesAction.class)
        };        
    }
    

    // Refresh to be called from the RefreshResourcesAction performAction
    
    public void refresh(){        
        ((WS70ResourceChildren)getChildren()).updateKeys();
    }    
    
}
