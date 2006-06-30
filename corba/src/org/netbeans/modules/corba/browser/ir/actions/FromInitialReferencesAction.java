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
