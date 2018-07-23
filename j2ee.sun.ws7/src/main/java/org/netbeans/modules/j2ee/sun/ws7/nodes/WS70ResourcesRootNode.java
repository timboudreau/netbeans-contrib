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
 * WS70ResourcesRootNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;
import javax.enterprise.deploy.spi.DeploymentManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

import java.util.Collection;

import org.netbeans.modules.j2ee.sun.ws7.j2ee.ResourceType;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.RefreshResourcesAction;

/**
 *
 * @author Mukesh Garg
 */
public class WS70ResourcesRootNode extends AbstractNode implements Node.Cookie{
    
    /**
     * Creates a new instance of WS70ResourcesRootNode 
     */
    public WS70ResourcesRootNode(Lookup lookup, ResourceType resType) {
        super(new WS70ResourceChildren(lookup, resType));
        getCookieSet().add(this);        
        if(resType.eqauls(resType.JDBC)){
            setDisplayName(NbBundle.getMessage(WS70ResourcesRootNode.class, "LBL_JDBC_RESOURCE"));            
            setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/JdbcResIcon.gif");            
            
        }else if(resType.eqauls(resType.JNDI)){
            setDisplayName(NbBundle.getMessage(WS70ResourcesRootNode.class, "LBL_JNDI_RESOURCE"));         
            setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/JndiResIcon.gif");            
            
        }else if(resType.eqauls(resType.CUSTOM)){
            setDisplayName(NbBundle.getMessage(WS70ResourcesRootNode.class, "LBL_CUSTOM_RESOURCE"));        
            setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/JndiResIcon.gif");           
            
        }else if(resType.eqauls(resType.MAIL)){
            setDisplayName(NbBundle.getMessage(WS70ResourcesRootNode.class, "LBL_MAIL_RESOURCE"));   
            setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/MailResIcon.gif");             
        }
    }

    public javax.swing.Action[] getActions(boolean context) {
        return new SystemAction[] {
            SystemAction.get(RefreshResourcesAction.class)
        };        
    }
    

    // Refresh to be called from the RefreshResourcesAction performAction
    
    public void refresh(){        
        ((WS70ResourceChildren)getChildren()).updateKeys();
    }    
    
}
