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
