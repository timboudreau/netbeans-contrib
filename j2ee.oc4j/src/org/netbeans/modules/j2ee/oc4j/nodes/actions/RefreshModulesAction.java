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

package org.netbeans.modules.j2ee.oc4j.nodes.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Michal Mocnak
 */
public class RefreshModulesAction extends NodeAction {
    
    /** Creates a new instance of RefreshModuleAction */
    public RefreshModulesAction() {
    }
    
    protected boolean enable(Node[] nodes) {
        for(Node node:nodes) {
            RefreshModulesCookie cookie = node.getCookie(RefreshModulesCookie.class);
            if (cookie == null)
                return false;
        }
        
        return true;
    }    
    
    public String getName() {
        return NbBundle.getMessage(RefreshModulesAction.class, "LBL_RefreshModulesAction"); // NOI18N
    }
    
    protected void performAction(Node[] nodes) {
        for(Node node:nodes) {
            RefreshModulesCookie cookie = node.getCookie(RefreshModulesCookie.class);
            if (cookie != null)
                cookie.refresh();
        }
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}