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

package org.netbeans.modules.targets;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

/**
 * Action that can always be invoked and work procedurally.
 *
 * @author  Marek Slama
 */
public class ShowTargets extends CallableSystemAction {
    public String TEST_MODE_NAME = "targets";

    public void performAction() {   
        Workspace ws = WindowManager.getDefault().getCurrentWorkspace();               
        Mode mode = ws.findMode("targets");
        if (mode == null) {
            mode = ws.createMode
            (TEST_MODE_NAME, NbBundle.getMessage(ShowTargets.class, "LBL_TestMode"), null);
        }
        TopComponent tc = TargetsPanel.findDefault();
        TopComponent[] tcs = mode.getTopComponents();
        
        boolean foundInMode = false;
        for (int i = 0; i < tcs.length; i++) {
            if (tc == tcs[i]) {
                foundInMode = true;
                break;
            }
        }
        
        if (!foundInMode) {
            mode.dockInto(tc);
        }
        
        tc.open();
        tc.requestActive();
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowTargets.class, "LBL_Action00");
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/targets/resources/editorMode.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
