/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

import org.netbeans.modules.j2ee.deployment.plugins.spi.J2eePlatformImpl;
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
    public boolean isLocalServer(){
        return manager.isLocalServer();
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
