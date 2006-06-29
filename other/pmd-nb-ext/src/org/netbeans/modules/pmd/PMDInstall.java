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

package org.netbeans.modules.pmd;

import org.openide.modules.ModuleInstall;

/** Extends default set of rules avaliable in PMD.
 *
 * @author Radim Kubacki
 */
public class PMDInstall extends ModuleInstall {

    /** SVUID. */
    private static final long serialVersionUID = 5509528431801513139L;

    /** Adds custom RuleSetFactory when IDE is started. */
    public void restored () {
        pmd.config.ConfigUtils.addRuleSetFactory (NbRuleSetFactory.getDefault ());
    }

    /** Unregisters RuleSetFactory. */
    public void uninstalled () {
        pmd.config.ConfigUtils.removeRuleSetFactory (NbRuleSetFactory.getDefault ());
    }
}
