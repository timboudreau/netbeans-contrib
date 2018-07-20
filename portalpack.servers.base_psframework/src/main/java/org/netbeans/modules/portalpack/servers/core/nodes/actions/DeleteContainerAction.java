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
import org.netbeans.modules.portalpack.servers.core.nodes.ContainerNode;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CookieAction;
import org.openide.windows.WindowManager;

/**
 * @author Satya
 */
public final class DeleteContainerAction extends CookieAction {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    protected void performAction(final Node[] nodes) {

        SwingUtilities.invokeLater(new Runnable(){
            
        public void run(){
        if( (nodes == null) || (nodes.length < 1) )
            return;
        for(int i=0;i<nodes.length;i++) {
            String containerNamePrefix = "";
                             
            ContainerNode cookie = (ContainerNode)nodes[i].getCookie(ContainerNode.class);
          
            if(cookie == null)
                return;
            PSDeploymentManager manager = cookie.getDeploymentManager();
            
            if(manager == null) {
                logger.log(Level.SEVERE,"PSDeploymentManager is null...return back");
                return;
            }
            
            logger.log(Level.FINE,"Base Dn for Node: "+cookie.getDn()+"  coookie: "+cookie.getClass().getName());
            
            if(JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(),org.openide.util.NbBundle.getMessage(DeleteContainerAction.class, "MSG_ARE_YOU_SURE_TO_DELETE")+cookie.getValue()+"\"",org.openide.util.NbBundle.getMessage(DeleteContainerAction.class, "MSG_Delete_Conatainer"),JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            {
                return;
            }    
            
            boolean success = false;
            try {
                manager.getTaskHandler().deleteChannel(cookie.getDn(),cookie.getValue(),cookie.getParentKey());
                logger.log(Level.FINE,"Node deleted.................."); 
                success  = true;
               
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"error",ex);
                success = false;
            }
            
            if(!success)
            {
                JOptionPane.showMessageDialog(null,cookie.getKey() + org.openide.util.NbBundle.getMessage(DeleteContainerAction.class, "MSG_COULD_NOT_BE_DELETED"));
                return;
            }
            
            ActionUtil.refresh(nodes[i]);
            
        }
        }});
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return org.openide.util.NbBundle.getMessage(DeleteContainerAction.class, "MSG_Delete_Container");
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
        
        return true;
    }
    
    protected boolean asynchronous() {
        return true;
    }
    
}

