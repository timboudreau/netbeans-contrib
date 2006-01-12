/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70WebModulesRootNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.spi.TargetModuleID;

import org.openide.util.Lookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;


import java.util.Collection;

import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.RefreshWebModulesAction;
/**
 *
 * @author Administrator
 */
public class WS70WebModulesRootNode extends AbstractNode implements Node.Cookie{
    
    /** Creates a new instance of WS70WebModulesRootNode */
    public WS70WebModulesRootNode(Lookup lookup) {
        super(new WS70WebModulesChildren(lookup));
        setDisplayName(NbBundle.getMessage(WS70WebModulesRootNode.class, "LBL_WEAPP_NODE_NAME"));
        getCookieSet().add(this);
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/WebAppFolderIcon.gif");
    }
    public javax.swing.Action[] getActions(boolean context) {
        return new SystemAction[] {
            SystemAction.get(RefreshWebModulesAction.class)
        };        
    }    
    // Refresh to be called from the RefreshWebModulesAction performAction    
    public void refresh(){
        
        ((WS70WebModulesChildren)getChildren()).updateKeys();
    }
    
}
