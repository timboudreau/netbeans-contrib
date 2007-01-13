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

import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

public final class CycleSlashBackslashDotDashAction extends AbstractCycleLineAction {

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(CycleSlashBackslashDotDashAction.class, "CTL_CycleSlashBackslashDotDashAction");
    }

    protected String iconResource() {
        return "org/netbeans/modules/linetools/actions/cycle.gif"; // NOI18N
    }

    protected String getCycleString() {
        return LineOperations.FILE_SEPARATORS_DOT_DASH;
    }
}
