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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.hk2.nodes.actions;

import java.net.MalformedURLException;
import java.net.URL;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.hk2.ide.Hk2PluginProperties;
import org.netbeans.modules.j2ee.hk2.nodes.Hk2InstanceNode;

import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.awt.HtmlBrowser.URLDisplayer;

/**
 * Action that can always be invoked and work procedurally.
 * This action will display the URL for the given admin server node in the runtime explorer
 */
public class ShowAdminToolAction extends CookieAction {
    
    protected Class[] cookieClasses() {
        return new Class[] {/* SourceCookie.class */};
    }
    
    protected int mode() {
        return MODE_EXACTLY_ONE;
        // return MODE_ALL;
    }
    
    protected void performAction(Node[] nodes) {
        if( (nodes == null) || (nodes.length < 1) )
            return;
        
        for (int i = 0; i < nodes.length; i++) {
            Object node = nodes[i].getLookup().lookup(Hk2InstanceNode.class);
            if (node instanceof Hk2InstanceNode) {
                try {
                    URL url = new URL(((Hk2InstanceNode) node).getAdminURL());
                    URLDisplayer.getDefault().showURL(url);
                } catch (MalformedURLException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
                }
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowAdminToolAction.class, "LBL_ShowAdminGUIAction");
    }
    
    public HelpCtx getHelpCtx() {
        return null; // HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx(RefreshAction.class);
    }
    
    protected boolean enable(Node[] nodes) {
        for (Node node : nodes) {
            Hk2InstanceNode iNode = (Hk2InstanceNode) node.getLookup().lookup(Hk2InstanceNode.class);
            if(iNode != null) {
                InstanceProperties prop = iNode.getDeploymentManager().getInstanceProperties();
                String port = prop.getProperty(InstanceProperties.HTTP_PORT_NUMBER);
                String host = prop.getProperty(Hk2PluginProperties.PROPERTY_HOST);
                return Hk2PluginProperties.isRunning(host, port);
            }
        }
        return false;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}