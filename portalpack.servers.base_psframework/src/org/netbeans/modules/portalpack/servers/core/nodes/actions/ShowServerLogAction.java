/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.servers.core.nodes.actions;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.nodes.PSInstanceNode;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

/**
 *
 * @author satyaranjan
 */
public class ShowServerLogAction extends CookieAction{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    @Override
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[] {};
    }

    @Override
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
                    } else {
                        manager.showServerLog();
                    }   
                }
                catch (Exception e){
                    logger.log(Level.SEVERE,"Error",e);
                           
                    return;//nothing much to do
                }
                            
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ShowServerLogAction.class, "CTL_ShowServerLog");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    @Override
    protected boolean enable(Node[] nodes) {

        if (nodes == null || nodes.length != 1)
            return false;
        
        boolean running = true;

        for (int i = 0; i < nodes.length; i++) {
            Object node = nodes[i].getLookup().lookup(PSInstanceNode.class);
            if (!(node instanceof PSInstanceNode)) {
                running = false;
                break;
            } else {
                PSDeploymentManager manager = ((PSInstanceNode)node).getDeploymentManager();
                try{
                    if(manager == null)
                    {
                        
                    }else { 
                        if(manager.isShowServerLogSupported())
                            return true;
                        else
                            return false;
                    } 
                }
                catch (Exception e){
                    logger.log(Level.SEVERE,"Error",e);
                }
            }    
            if (!running)
                break;
        }
         
        return running;
    }

}
