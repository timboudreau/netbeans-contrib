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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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