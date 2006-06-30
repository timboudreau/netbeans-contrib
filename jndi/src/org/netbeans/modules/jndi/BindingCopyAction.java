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

package org.netbeans.modules.jndi;

import org.openide.nodes.Node;
import org.openide.nodes.FilterNode;
import org.openide.util.HelpCtx;
/**
 *
 * @author  tzezula
 * @version
 */

public class BindingCopyAction extends org.openide.util.actions.NodeAction {

    /** Creates new BindingCopyAction */
    public BindingCopyAction() {
        super();
    }

    /** Performs copy of binding code.
    *
    * @param nodes an array of selected nodes
    */
    protected void performAction(Node[] nodes) {

        if (enable(nodes)) {
            ((JndiNode) nodes[0].getCookie(JndiNode.class)).bindingCopy();
        }
    }

    /** Should be the action enabled?
    *
    * @param nodes an array of selected nodes
    * @return <tt>true</tt> iff the array has length 1 and contains a JndiNode
    */
    protected boolean enable(Node[] nodes) {

        if ((nodes == null) ||
                (nodes.length != 1)) {
            return false;
        }
        return (nodes[0].getCookie(JndiNode.class) != null);
    }

    /** @return name of the action */
    public String getName() {
        return JndiRootNode.getLocalizedString("CTL_BindingCopy");
    }

    /** @return help */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
