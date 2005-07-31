/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
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
