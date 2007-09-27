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

import org.openide.*;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.Lookup;
import org.openide.util.actions.CookieAction;
import org.openide.util.datatransfer.ExClipboard;

/** An action that is responsible for creating of client's lookup code.
* It can be invoked on InterfaceNode. Generated code is placed into the
* clipboard.
*
* @author Martin Ryzl
*/
public class CreateClientAction extends CookieAction {

    /** Serial version UID. */
    static final long serialVersionUID = 7903624129695358662L;

    /** Format used for generation of user's code.
    * {0} - name of the remote interface
    * {1} - url of the service
    */
    static final String FMT_CODE = "try '{'\n    {0} obj = ({0}) Naming.lookup(\"{1}\");\n'}' catch (Exception ex) '{'\n    ex.printStackTrace();\n'}'"; // NOI18N

    /** Get the cookies that this action requires.
    * @return a list of cookies
    */
    protected Class[] cookieClasses() {
        return new Class[] { InterfaceNode.class };
    }

    /** Get the mode of the action, i.e. how strict it should be about cookie support.
    * @return the mode of the action. Possible values are disjunctions of the MODE_XXX constants.
    */
    protected int mode() {
        return MODE_EXACTLY_ONE;
    }

    /** Action.
    */
    protected void performAction(final Node[] nodes) {
        if (nodes.length > 0) {
            InterfaceNode in = (InterfaceNode) nodes[0].getCookie(InterfaceNode.class);
            if (in != null) {
                Class cl = in.getInterface();
                if (cl != null) {
                    StringSelection ss = new StringSelection(MessageFormat.format(
                                             FMT_CODE,
                                             new Object[] {
                                                 in.getInterface().getName(),
                                                 in.getURLString()
                                             }
                                         ));
                    ExClipboard ec = (ExClipboard)Lookup.getDefault ().lookup (ExClipboard.class);
                    ec.setContents(ss, null);
                }
            }
        }
    }


    /** Get name of the action.
    */
    public String getName() {
        return NbBundle.getBundle(CreateClientAction.class).getString("PROP_CreateClientActionName"); // NOI18N
    }

    /** Get help context for the action.
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(RMIRegistryRefreshAction.class);
    }
}















