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
package org.netbeans.modules.j2ee.jetty.nodes;

import java.awt.Component;
import org.netbeans.modules.j2ee.jetty.customizer.JetCustomizer;
import org.netbeans.modules.j2ee.jetty.customizer.JetCustomizerDataSupport;
import org.netbeans.modules.j2ee.jetty.JetDeploymentManager;
import org.netbeans.modules.j2ee.jetty.ide.JetJ2eePlatformFactory;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * Represents the new server in the Services window
 * @author novakm
 */
public class JetInstanceNode extends AbstractNode implements Node.Cookie {

    private static String ICON_BASE = "org/netbeans/modules/j2ee/jetty/resources/jicon.gif"; // NOI18N
    private Lookup lookup;

    public JetInstanceNode(Lookup lookup) {
        super(new Children.Array());
        this.lookup = lookup;
        getCookieSet().add(this);
        setIconBaseWithExtension(ICON_BASE);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(JetInstanceNode.class, "TXT_JetInstanceNode");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getShortDescription() {
        return "http://localhost:8080"; // NOI18N
    }
    
    /**
     * {@inheritDoc }
     */   
    @Override
    public javax.swing.Action[] getActions(boolean context) {
        return new javax.swing.Action[]{};
    }

    /**
     * {@inheritDoc }
     */   
    @Override
    public boolean hasCustomizer() {
        return true;
    }

    /**
     * {@inheritDoc }
     */   
    @Override
    public Component getCustomizer() {
        JetCustomizerDataSupport dataSup = new JetCustomizerDataSupport(getDeploymentManager());
        return new JetCustomizer(dataSup, new JetJ2eePlatformFactory().getJ2eePlatformImpl(getDeploymentManager()));
    }
    
    /**
     * @return JetDeploymentManager assossiated with given instance
     */
    public JetDeploymentManager getDeploymentManager() {
        return ((JetDeploymentManager) lookup.lookup(JetDeploymentManager.class));
    }
}
