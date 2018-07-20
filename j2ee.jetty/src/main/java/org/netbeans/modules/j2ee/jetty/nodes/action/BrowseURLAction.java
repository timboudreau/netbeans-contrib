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

package org.netbeans.modules.j2ee.jetty.nodes.action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Class representing action that opens web browser and opens
 * web URL for given web module
 * @author novakm
 */
public class BrowseURLAction extends NodeAction {
    private static final Logger LOGGER = Logger.getLogger(BrowseURLAction.class.getName());    
    protected void performAction(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            BrowseURLActionCookie oCookie = (BrowseURLActionCookie) nodes[i].getCookie(BrowseURLActionCookie.class);
            if (oCookie != null) {
                try {
                    URLDisplayer.getDefault().showURL(new URL(oCookie.getWebURL()));
                } catch (MalformedURLException e) {
                    LOGGER.log(Level.INFO, null, e);
                    
                }
            }
        }
    }
    
    protected boolean enable(Node[] activatedNodes) {
        JetWebModuleCookie wCookie;
        BrowseURLActionCookie bCookie;
        for (int i = 0; i < activatedNodes.length; i++) {
            wCookie = (JetWebModuleCookie) activatedNodes[i].getCookie(JetWebModuleCookie.class);
            bCookie = (BrowseURLActionCookie) activatedNodes[i].getCookie(BrowseURLActionCookie.class);
            if (wCookie == null || !wCookie.isRunning() || bCookie == null) 
		return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return NbBundle.getMessage(BrowseURLAction.class, "LBL_OpenInBrowserAction"); // NOI18N
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
