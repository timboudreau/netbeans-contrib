/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.servers.websynergy.nodes;

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.nodes.BaseNode;
import org.netbeans.modules.portalpack.servers.websynergy.nodes.actions.UndeployAction;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
/**
 *
 * @author Santh Chetan Chadalavada
 */
public class ThemeNode extends BaseNode implements Node.Cookie {

    private static String ICON_BASE = "org/netbeans/modules/portalpack/servers/websynergy/resources/theme.png"; // NOI18N
    private String key = "";
    private String dn;
    private PSDeploymentManager dm;
    
    
    public ThemeNode(PSDeploymentManager dm,String key,String dn) {
        
        super(Children.LEAF);
        this.key = key;
        this.dn = dn;
        this.dm = dm;
        
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
        setDisplayName(key);
        setShortDescription(getShortDescription()); 
        setName(key);
    }

    public PSDeploymentManager getDeploymentManager()
    {
        return dm;
    }

    public String getDN()
    {
        return dn;
    }
    public String getShortDescription() {
        return key; // NOI18N
    }

    public javax.swing.Action[] getActions(boolean context) {

       javax.swing.Action[]  newActions = new javax.swing.Action[2] ;
       newActions[0]=(null);
       newActions[1]= (SystemAction.get(UndeployAction.class));
       return newActions;

    }

    public boolean hasCustomizer() {
        return true;
    }

    public String getKey() {
        return key;
    }

    public String getDn() {
        return dn;
    }

    public String getType() {
        return LiferayNodeConstants.THEME_NODE_TYPE;
    }

}
