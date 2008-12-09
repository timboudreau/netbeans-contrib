/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.selenium;

import org.netbeans.api.project.Project;
import org.netbeans.modules.web.api.webmodule.WebProjectConstants;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.netbeans.spi.project.ActionProvider;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Jindrich Sedek
 */
public final class RunSeleniumTestsAction extends NodeAction {

    protected void performAction(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            Project project = node.getLookup().lookup(Project.class);
            SeleniumServerRunner.runServer();
            ActionProvider provider = project.getLookup().lookup(ActionProvider.class);
            provider.invokeAction(WebProjectConstants.COMMAND_REDEPLOY, Lookup.EMPTY);
            provider.invokeAction(ActionProvider.COMMAND_TEST, Lookup.EMPTY);
        }
    }

    public String getName() {
        return NbBundle.getMessage(RunSeleniumTestsAction.class, "CTL_RunSeleniumTestsAction");
    }

    @Override
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            Project proj = node.getLookup().lookup(Project.class);
            if (proj == null){
                return false;
            }
            if (proj.getLookup().lookup(WebModuleProvider.class) == null){
                return false;
            }
        }
        return activatedNodes.length > 0;
    }
}

