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

package org.netbeans.modules.corba.browser.ir.actions;
import org.openide.util.actions.NodeAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.netbeans.modules.corba.browser.ir.nodes.IRContainerNode;
import org.netbeans.modules.corba.browser.ir.Util;

public class RefreshAction extends NodeAction {

    /** Creates new RefreshAction */
    public RefreshAction() {
    }


    protected boolean enable (Node[] nodes){
        if (nodes != null)
            for (int i = 0; i < nodes.length; i ++)
                if (nodes[i].getCookie (IRContainerNode.class) == null)
                    return false;
        return true;
        
    }

    protected void performAction (Node[] nodes){
        if ( enable ( nodes)){
            ((IRContainerNode)nodes[0].getCookie (IRContainerNode.class)).refresh();
        }
    }

    public String getName() {
        return Util.getLocalizedString ("CTL_RefreshAction");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

}
