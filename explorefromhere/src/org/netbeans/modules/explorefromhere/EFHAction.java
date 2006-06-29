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
package org.netbeans.modules.explorefromhere;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
/**
 * Action which opens an explorer window rooted on some folder.
 * @author Timothy Boudreau
 */
public class EFHAction extends AbstractAction {
    public EFHAction() {
        putValue (Action.NAME, NbBundle.getMessage(EFHAction.class,"LBL_Action")); //NOI18N
    }
    
    public void actionPerformed (ActionEvent ae) {
        Node[] n = TopComponent.getRegistry().getActivatedNodes();
        if (n.length == 1) {
            ExplorerPanel ep = new ExplorerPanel();
            ep.setDisplayName (n[0].getDisplayName());
            BeanTreeView btv = new BeanTreeView();
            ep.setLayout(new BorderLayout());
            ep.add(btv, BorderLayout.CENTER);
            ep.getExplorerManager().setRootContext(n[0]);
            Mode m = WindowManager.getDefault().findMode("explorer"); //NOI18N
            if (m != null) {
                m.dockInto(ep);
            }
            
            ep.open();
            ep.requestActive();
        }
    }
}
