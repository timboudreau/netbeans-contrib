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

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.util.NbBundle;

/**
 * Expanding/collapsing nodes.
 *
 * @author tl
 */
public class ExpandCollapseAction extends AbstractAction implements
ListSelectionListener {
    private TreeTable tt;
    private boolean expand;
    
    /**
     * Creates a new instance of ExpandCollapseAction.
     * 
     * @param expand true = expand
     * @param tt a TreeTable
     */
    public ExpandCollapseAction(boolean expand, TreeTable tt) {
        super(NbBundle.getMessage(ExpandCollapseAction.class, 
                expand ? "Expand" : "Collapse")); // NOI18N
        this.tt = tt;
        this.expand = expand;
        tt.getSelectionModel().addListSelectionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        TreePath tp = tt.getSelectedPath();
        if (expand)
            tt.expandPath(tp);
        else
            tt.getTree().collapsePath(tp);
        tt.select(tp);
    }

    public void valueChanged(ListSelectionEvent e) {
        TreePath tp = tt.getSelectedPath();
        if (tp != null) {
            boolean expanded = tt.getTree().isExpanded(tp);
            boolean children = tt.getTree().getModel().getChildCount(
                    tp.getLastPathComponent()) > 0;
            setEnabled(children && (expanded != this.expand));
        } else {
            setEnabled(false);
        }
    }
}
