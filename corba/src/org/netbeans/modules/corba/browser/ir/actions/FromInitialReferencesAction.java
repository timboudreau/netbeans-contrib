/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.browser.ir.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.FromInitialReferencesCookie;
/**
 * @name FromInitialReferencesAction
 * @author  Tomas Zezula
 */
public class FromInitialReferencesAction extends org.openide.util.actions.NodeAction {

    
    /** Creates new FromInitialReferencesAction */
    public FromInitialReferencesAction() {
    }
    
    
    public boolean enable (Node[] nodes) {
        if (nodes != null)
            for (int i = 0; i < nodes.length; i ++)
                if (nodes[i].getCookie (FromInitialReferencesCookie.class) == null)
                    return false;
        return true;        
    }
    
    public void performAction (Node[] nodes) {
        if (enable(nodes)) {
            ((FromInitialReferencesCookie)nodes[0].getCookie (FromInitialReferencesCookie.class)).fromInitialReferences();
        }
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public String getName () {
        return Util.getLocalizedString ("CTL_FromInitialReferences"); // No I18N
    }

}
