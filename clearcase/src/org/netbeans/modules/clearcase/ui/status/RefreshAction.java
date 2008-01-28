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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.clearcase.ui.status;

import java.awt.EventQueue;
import javax.swing.Action;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.netbeans.modules.clearcase.util.Utils;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

public class RefreshAction extends NodeAction {
    
    private VCSContext context;

    public RefreshAction() {        
    }    
    
    public RefreshAction(String name, VCSContext context) {
        this.context = context;
        putValue(Action.NAME, name);
    }    

    @Override
    protected void performAction(Node[] activatedNodes) {
        final VCSContext ctx = Utils.getCurrentContext(activatedNodes);                
        // XXX not in awt ???
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                final VersioningTopComponent vtc = VersioningTopComponent.findInstance();
                vtc.setContentTitle(/* XXX getContextDisplayName(nodes)*/ "Clearcase");        
                vtc.setContext(ctx);
                vtc.open(); 
                vtc.requestActive();                
                vtc.performRefreshAction();
            }
        });
        
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return "Show changes";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(getClass());
    }
    
    
}
