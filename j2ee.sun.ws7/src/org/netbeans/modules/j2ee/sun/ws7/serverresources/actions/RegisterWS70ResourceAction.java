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
 * RegisterWS70ResourceAction.java
 * 
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.actions;

import java.text.MessageFormat;
import javax.swing.SwingUtilities;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.actions.NodeAction;

import org.openide.loaders.DataObject;
import org.openide.filesystems.FileObject;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.WS70WizardConstants;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.loaders.SunWS70ResourceDataObject;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70BaseResourceNode;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70ResourceUtils;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.*;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.netbeans.modules.j2ee.sun.ws7.ui.Util;

import java.util.HashMap;
        
/**
 * Code reused from Appserver common API module 
 * @author Nitya Doraisamy
 */
public class RegisterWS70ResourceAction extends NodeAction implements WS70WizardConstants{

    protected void performAction(Node[] nodes) {        
        InstanceProperties target = getTargetServer(nodes[0]);
        WS70SunDeploymentManager manager = null;
        String configName = null;
        if(target!=null){
            DeploymentManager dm = target.getDeploymentManager();
            if(! (dm instanceof WS70SunDeploymentManager)){
                Util.showWarning(NbBundle.getMessage(RegisterWS70ResourceAction.class, "Msg_invalid_server"),
                        NbBundle.getMessage(RegisterWS70ResourceAction.class, "Title_invalid_server")
                        );
                return;
                
            }
            //
            manager= (WS70SunDeploymentManager)dm;
            configName = target.getProperty("configName");
        }else{            
            Util.showWarning(NbBundle.getMessage(RegisterWS70ResourceAction.class, "Msg_invalid_server"),
                        NbBundle.getMessage(RegisterWS70ResourceAction.class, "Title_invalid_server")
                        );
            return;
        }                        
        try{
            SunWS70ResourceDataObject dobj = (SunWS70ResourceDataObject)nodes[0].getCookie(SunWS70ResourceDataObject.class);
            WS70BaseResourceNode resNode = (WS70BaseResourceNode)dobj.getNodeDelegate();
            WS70Resources resources = resNode.getBeanGraph();
            String resourceType = dobj.getResourceType();
            if(resourceType == null){
                String message = MessageFormat.format(NbBundle.getMessage(RegisterWS70ResourceAction.class, "Err_InvalidXML"), new Object[]{nodes[0].getName()}); //NOI18N 
                Util.showError(message);
                return;
            }    

            WS70ResourceUtils.registerResource(resources, resourceType, configName, manager);
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            Util.showError(ex.getLocalizedMessage());
        }
    }
    
    protected boolean enable(Node[] nodes) {
       if( (nodes != null) && (nodes.length == 1) )
            return true;
        else
            return false;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    public String getName() {
        return NbBundle.getMessage(RegisterWS70ResourceAction.class, "LBL_RegisterAction"); //NOI18N
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/j2ee/sun/ws7/resources/ConfigIcon.gif"; //NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return null; // HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx(RegisterWS70ResourceAction.class);
    }
    
    private InstanceProperties getTargetServer(Node node){
        InstanceProperties server = null;
        DataObject dob = (DataObject) node.getCookie(DataObject.class);
        if(dob!=null){
            
            FileObject fo = dob.getPrimaryFile();
            Project holdingProj = FileOwnerQuery.getOwner(fo);
            if (holdingProj != null){
                J2eeModuleProvider modProvider = (J2eeModuleProvider) holdingProj.getLookup().lookup(J2eeModuleProvider.class);
                server = modProvider.getInstanceProperties();
            }
        }
        
        return server;
    }
    

    /** Perform extra initialization of this action's singleton.
     * PLEASE do not use constructors for this purpose!
     * protected void initialize() {
     * super.initialize();
     * putProperty(Action.SHORT_DESCRIPTION, NbBundle.getMessage(RegisterAction.class, "HINT_Action"));
     * }
     */
    
}
