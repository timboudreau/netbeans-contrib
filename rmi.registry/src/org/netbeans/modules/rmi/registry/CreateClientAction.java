/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
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















