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

import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.nodes.IRInterfaceDefNode;
import org.netbeans.modules.corba.browser.ir.util.Generatable;


public class GenerateCodeAction extends NodeAction {

    private static final boolean DEBUG = true;
    //private static final boolean DEBUG = false;

    protected void performAction (final Node[] nodes) {
        if ( enable ( nodes) ){
            TopManager.getDefault().setStatusText(Util.getLocalizedString("MSG_GenerateWait"));
            org.openide.util.RequestProcessor.postRequest (new Runnable () {
                public void run () {
                    try {
                        ((Generatable)nodes[0].getCookie(Generatable.class)).generateCode();
                        TopManager.getDefault().setStatusText(Util.getLocalizedString("MSG_GenerateDone"));
                    }catch (Exception e){
                        if (DEBUG)
                            e.printStackTrace();
                            TopManager.getDefault().notify ( new NotifyDescriptor.Message (e.toString(),NotifyDescriptor.Message.ERROR_MESSAGE));
                    }
                }
            });
        }
    }

    protected boolean enable (Node[] nodes) {
        Node node;        
        if (nodes != null){
            for (int i = 0; i < nodes.length; i ++){
                node = (Node) nodes[i];
                if (node.getCookie(Generatable.class) == null) return false;
                if (node.getParentNode() instanceof IRInterfaceDefNode) return false;
            }
            return true;
        }
        return false;
    }

    public String getName () {
        return Util.getLocalizedString("CTL_GenerateCode");
    }

    protected String iconResource () {
        return "GenerateCodeActionIcon.gif";
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

}
