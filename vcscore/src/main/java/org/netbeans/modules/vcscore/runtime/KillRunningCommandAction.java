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

package org.netbeans.modules.vcscore.runtime;

import org.openide.util.actions.NodeAction;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.netbeans.modules.vcscore.runtime.*;


/**
 * The action that attempts to stop the running command.
 *
 * @author  Milos Kleint
 */
public class KillRunningCommandAction extends NodeAction {

    private static final long serialVersionUID = 6386534364548632176L;

    /** Creates new KillRunningCommandAction */
    private KillRunningCommandAction() {
    }

    public static KillRunningCommandAction getInstance() {
        return (KillRunningCommandAction)
            org.openide.util.actions.SystemAction.get(KillRunningCommandAction.class);
    }

    public String getName() {
        return g("CTL_Kill_Running_Command_Action"); // NOI18N
    }

    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    public void performAction(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            final RuntimeCommand comm = (RuntimeCommand) nodes[i].getCookie(RuntimeCommand.class);
            if (comm != null) {
                comm.killCommand();
            }
        }
    }

    public boolean enable(Node[] nodes){
        boolean enabled = false;
        for (int i = 0; i < nodes.length; i++) {
            RuntimeCommand comm = (RuntimeCommand) nodes[i].getCookie(RuntimeCommand.class);
            if (comm != null) {
                if (comm.getState() == comm.STATE_RUNNING) {
                    enabled = true;
                    break;
                }
            }
        }
        return enabled;
    }

    public HelpCtx getHelpCtx(){
        //D.deb("getHelpCtx()"); // NOI18N
        return null;
    }


    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(KillRunningCommandAction.class).getString(s);
    }
    
}
