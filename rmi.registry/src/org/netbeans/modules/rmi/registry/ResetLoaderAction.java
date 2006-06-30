/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.registry;

import java.awt.datatransfer.StringSelection;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.openide.*;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.actions.CookieAction;

/** An action that is responsible for creating of client's lookup code.
* It can be invoked on InterfaceNode. Generated code is placed into the
* clipboard.
*
* @author Martin Ryzl
*/
public class ResetLoaderAction extends CookieAction {

    /** Serial version UID. */
    static final long serialVersionUID = 5503624129695358662L;

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
        return MODE_EXACTLY_ONE;
    }

    /** Action.
    */
    protected void performAction(final Node[] nodes) {
        for(int i = 0; i < nodes.length; i++) {
            RMIRegistryNode node = (RMIRegistryNode) nodes[i].getCookie(RMIRegistryNode.class);
            if (node != null) {
                ((RMIRegistryChildren) node.getChildren()).setCleanFlag(true);
                node.refresh();
            }
        }
    }


    /** Get name of the action.
    */
    public String getName() {
        return NbBundle.getBundle(ResetLoaderAction.class).getString("LBL_ResetLoaderAction"); // NOI18N
    }

    /** Get help context for the action.
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ResetLoaderAction.class);
    }
}
