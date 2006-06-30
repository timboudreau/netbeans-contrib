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

/*
 * RefreshAction.java
 *
 * Created on November 13, 2000, 12:21 PM
 */

package org.netbeans.modules.corba.ioranalyzer;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.util.*;
import org.openide.util.actions.*;
/**
 *
 * @author  tzezula
 * @version
 */
public class RefreshAction extends CookieAction {

    /** Creates new RefreshAction */
    public RefreshAction() {
    }


    public Class[] cookieClasses() {
        return new Class[] {IORNode.class};
    }

    public String getName () {
        return NbBundle.getBundle(RefreshAction.class).getString("TXT_Refresh");
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    public boolean enable (Node[] nodes) {
        if (nodes.length == 0)
            return false;
        for (int i=0; i< nodes.length; i++) {
            IORNode node = (IORNode) nodes[i].getCookie (IORNode.class);
            if (node == null)
                return false;
        }
        
        return true;
    }
    
    protected int mode () {
        return org.openide.util.actions.CookieAction.MODE_ALL;
    }
    
    public void performAction (Node[] nodes) {
        for (int i=0; i< nodes.length; i++) {
            IORNode node = (IORNode) nodes[i].getCookie (IORNode.class);
            if (node != null)
                ((ProfileChildren)node.getChildren()).update();
        }
    }

}
