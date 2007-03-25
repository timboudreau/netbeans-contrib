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

package org.netbeans.modules.linetools.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;

/**
 * The action to enables and disables case sensitive sorting. This also affects
 * how the remove duplicates functionality works.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class MatchCase extends BooleanStateAction implements PropertyChangeListener {

    public MatchCase() {
        addPropertyChangeListener(this);
    }

    protected void initialize() {
        super.initialize();
        setBooleanState(LineOperations.isMatchCase());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROP_BOOLEAN_STATE)) {
            LineOperations.setMatchCase(getBooleanState());
        }
    }

    public String getName() {
        return NbBundle.getMessage(RemoveDuplicateLines.class, "CTL_MatchCase"); // NOI18N
    }

    protected String iconResource() {
        return "org/netbeans/modules/linetools/actions/matchcase.gif"; // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
}
