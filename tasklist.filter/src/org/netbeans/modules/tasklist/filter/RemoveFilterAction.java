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

package org.netbeans.modules.tasklist.filter;

import java.awt.Toolkit;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Removes the current filter
 * 
 * @author tl
 */
public class RemoveFilterAction extends CallableSystemAction {

    private static final long serialVersionUID = 1;

    protected boolean asynchronous() {
        return false;
    }

    public void performAction() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        
        // Pick the right list to use
        if (!(tc instanceof FilteredTopComponent)) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            FilteredTopComponent view = (FilteredTopComponent) tc;
            view.setFilter(null);
            setEnabled(false);
        }
    }

    public void enable() {
        TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
        
        // Pick the right list to use
        if (!(tc instanceof FilteredTopComponent)) {
            setEnabled(false);
        } else {
            FilteredTopComponent view = (FilteredTopComponent) tc;
            setEnabled(view.getFilter() != null);
        }
    }

    public String getName() {
        return NbBundle.getMessage(FilterAction.class, "RemoveFilter"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/tasklist/filter/removefilter.png"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected void initialize() {
        super.initialize();
        setEnabled(false);
    }
}
