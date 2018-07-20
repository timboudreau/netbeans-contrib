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

package org.netbeans.modules.portalpack.servers.core.nodes.actions;

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.servers.core.nodes.PSInstanceNode;
import org.openide.awt.HtmlBrowser;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class ShowAdminToolAction extends CookieAction {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected void performAction(Node[] nodes) {
        if( (nodes == null) || (nodes.length < 1) )
             return;
         
        for (int i = 0; i < nodes.length; i++) {
            Object node = nodes[i].getLookup().lookup(PSInstanceNode.class);
            if (node instanceof PSInstanceNode) {
                
                PSDeploymentManager manager = ((PSInstanceNode)node).getDeploymentManager();
                try{
                    if(manager == null)
                    {
                        logger.log(Level.WARNING,"Deployment Manager is Null");
                        return;
                    }
                    
                    String portalUrl = manager.getTaskHandler().constructAdminToolURL();
                    HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(portalUrl)); 
                }
                catch (Exception e){
                    logger.log(Level.SEVERE,"Error",e);
                           
                    return;//nothing much to do
                }
                            
            }
        }
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowAdminToolAction.class, "CTL_ShowAdmnToolAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {};
    }
    
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    
    
    protected boolean enable(Node[] nodes) {

        if (nodes == null || nodes.length < 1)
            return false;
        
        boolean running = true;

        for (int i = 0; i < nodes.length; i++) {
            Object node = nodes[i].getLookup().lookup(PSInstanceNode.class);
            if (!(node instanceof PSInstanceNode)) {
                running = false;
                break;
            }    
            if (!running)
                break;
        }
         
        return running;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}

