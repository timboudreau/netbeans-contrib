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
 * WS70ManagerNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.ViewAdminConsoleAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.ViewAdminServerLogAction;
import javax.swing.Action;

import java.util.Collection;
/**
 *
 * @author Administrator
 */
public class WS70ManagerNode extends AbstractNode implements Node.Cookie{
    static java.util.Collection bogusNodes = java.util.Arrays.asList(new Node[] { Node.EMPTY, Node.EMPTY });
    
    /** Creates a new instance of WS70ManagerNode */
    public WS70ManagerNode(DeploymentManager dm) {
        super(new MyChildren(bogusNodes));        
        setDisplayName(NbBundle.getMessage(WS70ManagerNode.class, "LBL_WS70_MANAGER_NODE_NAME")); //NOI18N
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/ServerInstanceIcon.gif");
        getCookieSet().add(this);
    }
    public Node.Cookie getCookie (Class type) {
        if (WS70ManagerNode.class.isAssignableFrom(type)) {
            return this;
        }
 
        return super.getCookie (type);
    }
    public Action[] getActions(boolean context) {
        return new SystemAction[] {   
            null,
            SystemAction.get(ViewAdminConsoleAction.class),
            SystemAction.get(ViewAdminServerLogAction.class),
            null            
        };
    }
   
  
    public static class MyChildren extends Children.Array {
        public MyChildren(Collection nodes) {
            super(nodes);
        }
    }   
}
