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
