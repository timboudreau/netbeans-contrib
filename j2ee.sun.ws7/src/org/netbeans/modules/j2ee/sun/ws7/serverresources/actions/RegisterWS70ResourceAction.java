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
