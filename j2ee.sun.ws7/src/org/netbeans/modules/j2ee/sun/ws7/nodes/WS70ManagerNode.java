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
import org.openide.ErrorManager;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;

import org.netbeans.modules.j2ee.deployment.plugins.api.J2eePlatformImpl;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.ViewAdminConsoleAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.ViewAdminServerLogAction;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.netbeans.modules.j2ee.sun.ws7.j2ee.WS70LogViewer;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentFactory;
import org.netbeans.modules.j2ee.sun.ws7.j2ee.WS70J2eePlatformFactory;
import org.netbeans.modules.j2ee.sun.ws7.ui.WS70Customizer;

import javax.swing.Action;

import java.util.Collection;
import java.io.File;
/**
 *
 * @author Administrator
 */
public class WS70ManagerNode extends AbstractNode implements Node.Cookie{
    static java.util.Collection bogusNodes = java.util.Arrays.asList(new Node[] { Node.EMPTY, Node.EMPTY });
    private WS70SunDeploymentManager manager;
    /** Creates a new instance of WS70ManagerNode */
    public WS70ManagerNode(DeploymentManager dm) {
        super(new MyChildren(bogusNodes));
        manager = (WS70SunDeploymentManager)dm;        
        setDisplayName(NbBundle.getMessage(WS70ManagerNode.class, "LBL_WS70_MANAGER_NODE_NAME")); //NOI18N
        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/ServerInstanceIcon.gif");
        setShortDescription(manager.getHost()+":"+manager.getPort());
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
    public boolean hasCustomizer() {
        return true;
    }    
    public java.awt.Component getCustomizer() {
        WS70J2eePlatformFactory fact = new WS70J2eePlatformFactory();
        J2eePlatformImpl platform = fact.getJ2eePlatformImpl(manager);
        return new WS70Customizer(platform, manager);
    }    
    public String  getAdminURL() {
        String url = null;
        WS70SunDeploymentManager cDm= WS70SunDeploymentFactory.getConnectedCachedDeploymentManager(manager.getUri());
        if(cDm.isAdminOnSSL()){
            url = "https://";// NOI18N
        }else{
            url = "http://";// NOI18N
        }
        url = url+cDm.getHost() + ":" + // NOI18N
            String.valueOf(cDm.getPort());        
        return url;
    }
    public void invokeLogViewer(){
        String uri = manager.getUri();
        String location = manager.getServerLocation();
        location = location+File.separator+"admin-server"+
                File.separator+"logs"+File.separator+"errors";

        WS70LogViewer logViewer = new WS70LogViewer(new File(location));
        
        try{
            logViewer.showLogViewer(UISupport.getServerIO(uri));
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
  
    public static class MyChildren extends Children.Array {
        public MyChildren(Collection nodes) {
            super(nodes);
        }
    }   
}
