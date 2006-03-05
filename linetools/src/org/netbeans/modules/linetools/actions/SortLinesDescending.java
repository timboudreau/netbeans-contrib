/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.linetools.actions;

import javax.swing.text.JTextComponent;
import org.openide.util.NbBundle;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class SortLinesDescending extends AbstractLineAction {

    protected void doLineOperation(JTextComponent textComponent) {
        LineOperations.sortLinesDescending(textComponent);
    }

    public String getName() {
        return NbBundle.getMessage(CopyLineUp.class, "CTL_SortLinesDescending"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/linetools/actions/sortlinesdescending.gif"; // NOI18N
    }
}

