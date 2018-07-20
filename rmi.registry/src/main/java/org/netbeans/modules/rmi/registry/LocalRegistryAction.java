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

package org.netbeans.modules.rmi.registry;

import java.awt.datatransfer.StringSelection;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.openide.*;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.actions.CookieAction;

/** An action that is responsible for starting and stopping the local registry
*
* @author Martin Ryzl
*/
public class LocalRegistryAction extends CookieAction {

    /** Serial version UID. */
    static final long serialVersionUID = 7876544129695358662L;

    /** Get the cookies that this action requires.
    * @return a list of cookies
    */
    protected Class[] cookieClasses() {
        return new Class[] { RMIRegistryNode.class };
    }

    /** Get the mode of the action, i.e. how strict it should be about cookie support.
    * @return the mode of the action. Possible values are disjunctions of the MODE_XXX constants.
    */
    protected int mode() {
        return MODE_ALL;
    }

    /** Action.
    */
    protected void performAction(final Node[] nodes) {
        String closeOpt = NbBundle.getBundle(LocalRegistryAction.class).getString("LAB_Close"); // NOI18N
        DialogDescriptor dd = new DialogDescriptor(
            new LocalRegistryPanel(), 
            NbBundle.getBundle(LocalRegistryAction.class).getString("LAB_LocalRegistryPanelName"), // NOI18N
            true,
//            DialogDescriptor.DEFAULT_OPTION,
//            closeOpt,
            null
        );
        
        Object[] opts = new Object[] { closeOpt };
        dd.setOptions(opts);
        dd.setClosingOptions(opts);
        
        java.awt.Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.show();
    }


    /** Get name of the action.
    */
    public String getName() {
        return NbBundle.getBundle(LocalRegistryAction.class).getString("PROP_LocalRegistryActionName"); // NOI18N
    }

    /** Get help context for the action.
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(LocalRegistryAction.class);
    }
}
