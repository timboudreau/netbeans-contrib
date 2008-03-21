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

package org.netbeans.modules.glassfish.common.actions;

import java.awt.event.ActionEvent;
import org.netbeans.spi.glassfish.GlassfishModule;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Resfresh action refreshes the server state.
 *
 * @author Peter Williams
 */
public class RefreshAction extends NodeAction {
    
    protected void performAction(Node[] nodes) {
        performActionImpl(nodes);
    }
    
    private static void performActionImpl(Node[] activatedNodes) {
        for(Node node : activatedNodes) {
            GlassfishModule commonSupport = 
                    node.getLookup().lookup(GlassfishModule.class);
            if(commonSupport != null) {
                performActionImpl(commonSupport);
            }
        }
    }

    private static void performActionImpl(GlassfishModule commonSupport) {
        // Tell the server instance to refresh it's status.
    }

    protected boolean enable(Node[] nodes) {
        return enableImpl(nodes);
    }
    
    private static boolean enableImpl(Node[] activatedNodes) {
        boolean result = false;
        if(activatedNodes != null && activatedNodes.length > 0) {
            for(Node node : activatedNodes) {
                GlassfishModule commonSupport = node.getLookup().lookup(GlassfishModule.class);
                if(commonSupport != null) {
                    result = enableImpl(commonSupport);
                } else {
                    // No server instance found for this node.
                    result = false;
                }
                if(!result) {
                    break;
                }
            }
        }
        return result;
    }

    private static boolean enableImpl(GlassfishModule commonSupport) {
        // !PW FIXME what should this be?
//        if (commonSupport == null || commonSupport.getServerState() == ServerInstance.STATE_WAITING) {
//            return false;
//        }
        return true;
    }

    @Override
    protected boolean asynchronous() { 
        return false; 
    }
    
    public String getName() {
        return NbBundle.getMessage(RefreshAction.class, "LBL_Refresh"); // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    
    /** This action will be displayed in the server output window */
    public static class OutputAction extends AbstractOutputAction {
        
        private static final String ICON = 
                "org/netbeans/modules/glassfish/common/resources/refresh.png"; // NOI18N
        
        public OutputAction(final GlassfishModule commonSupport) {
            super(commonSupport, NbBundle.getMessage(RefreshAction.class, "LBL_RefreshOutput"), // NOI18N
                    NbBundle.getMessage(RefreshAction.class, "LBL_RefreshOutputDesc"), // NOI18N
                    ICON);
        }
        
        public void actionPerformed(ActionEvent e) {
            performActionImpl(commonSupport);
        }

        @Override
        public boolean isEnabled() {
            return enableImpl(commonSupport);
        }
        
    }
}