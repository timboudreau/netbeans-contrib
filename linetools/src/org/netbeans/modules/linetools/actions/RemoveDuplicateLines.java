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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;

/**
 * The action to enable and disble highlihting of matching regular expressions in editor windows.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class RemoveDuplicateLines extends BooleanStateAction implements PropertyChangeListener {

    public RemoveDuplicateLines() {
        addPropertyChangeListener(this);
    }

    protected void initialize() {
        super.initialize();
        setBooleanState(LineOperations.isRemoveDuplicateLines());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROP_BOOLEAN_STATE)) {
            LineOperations.setRemoveDuplicateLines(getBooleanState());
        }
    }

    public String getName() {
        return NbBundle.getMessage(RemoveDuplicateLines.class, "CTL_RemoveDuplicateLines"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/linetools/actions/removeduplicatelines.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
}
