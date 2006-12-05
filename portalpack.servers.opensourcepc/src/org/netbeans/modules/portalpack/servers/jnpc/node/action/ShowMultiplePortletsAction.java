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

package org.netbeans.modules.portalpack.servers.jnpc.node.action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.nodes.BaseNode;
import org.netbeans.modules.portalpack.servers.core.nodes.PSInstanceNode;
import org.netbeans.modules.portalpack.servers.core.nodes.actions.ShowAdminToolAction;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.openide.awt.HtmlBrowser;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 *
 * @author root
 */
public final class ShowMultiplePortletsAction extends CookieAction {
    
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    
    protected void performAction(Node[] nodes) {
        //DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        if( (nodes == null) || (nodes.length < 1) )
             return;

//        if(nodes[0] instanceof BaseNode)
        {
            BaseNode nd= null;
          /*  
            try{
                nd = (BaseNode)nodes[0];
            }catch(Exception e){
                e.printStackTrace();
            }
            
            if(nd == null)
                return;*/
            
            PSDeploymentManager manager = (PSDeploymentManager)nodes[0].getLookup().lookup(DeploymentManager.class);
            //PSDeploymentManager manager = nd.getDeploymentManager();
            if(manager == null)
            {
                logger.info("Deployment manager is null");
                return;
            }
            String portalUrl = manager.getTaskHandler().constructAdminToolURL();
            try {
                HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(portalUrl)); 
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE,"Error opening browser",ex);
            }
        }
       
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowMultiplePortletsAction.class, "CTL_ShowMultiplePortlets");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {};
      //      DataObject.class
       // };
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    
    
    protected boolean enable(Node[] nodes) {

        if (nodes == null || nodes.length != 1)
            return false;
        
        if(nodes.length == 1)
            return true;
        else
            return false;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}