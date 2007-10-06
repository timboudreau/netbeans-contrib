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

package org.netbeans.modules.tasklist.suggestions.ui;

import java.util.Iterator;
import java.util.HashSet;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.suggestions.*;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;


/**
 * Disables selected category.
 *
 * @author Tor Norbye
 */

public final class DisableAction extends NodeAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] node) {
        return true;
    }
    
    protected void performAction(Node[] node) {
        if (node == null) {
            return;
        }

        // Remove suggestion when we've performed it
        SuggestionManagerImpl manager =
            (SuggestionManagerImpl)SuggestionManager.getDefault();

        HashSet set = new HashSet(10);

        // Compute the set of types we're disabling
        for (int i = 0; i < node.length; i++) {
            SuggestionImpl s = (SuggestionImpl)TaskNode.getTask(node[i]);
            SuggestionType type = s.getSType();
            String message = NbBundle.getMessage(DisableAction.class, 
                "ConfirmDisable", type.getLocalizedName()); // NOI18N
            String title = NbBundle.getMessage(DisableAction.class, 
                "ConfirmDisableTitle"); // NOI18N
            NotifyDescriptor desc = new NotifyDescriptor.Confirmation(
                 message, title, NotifyDescriptor.YES_NO_OPTION);
            if (!NotifyDescriptor.YES_OPTION.equals(
               DialogDisplayer.getDefault().notify(desc))) {
                continue;
            }
            set.add(s.getSType());
        }
        // Disable each type
        Iterator it = set.iterator();
        while (it.hasNext()) {
            SuggestionType type = (SuggestionType)it.next();
            manager.setEnabled(type.getName(), false, false);
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(DisableAction.class, "LBL_Disable"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/suggestions/disableAction.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx(DisableAction.class);
    }
    
}
