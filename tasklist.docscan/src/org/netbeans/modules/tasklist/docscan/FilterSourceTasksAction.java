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

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.core.filter.FilterAction;
import org.openide.util.HelpCtx;

import java.awt.*;

/**
 * Filters source tasks. It hides south component and makes action modal.
 *
 * @author Petr Kuzel
 */
final class FilterSourceTasksAction extends FilterAction {
    protected Component createSubpanel() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(FilterSourceTasksAction.class);
    }

    public String getName() {
        return Util.getString("filter-todo");
    }

    protected boolean isModal() {
        return true;
    }
}
