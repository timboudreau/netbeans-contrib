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
 * WaitNode.java
 *
 * Created on September 25, 2000, 11:07 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.openide.nodes.*;
/**
 *
 * @author  tzezula
 * @version
 */
public class WaitNode extends IRAbstractNode {

    private static final String ICON_BASE =
    "org/netbeans/modules/corba/browser/ir/resources/wait";

    /** Creates new WaitNode */
    public WaitNode() {
        super (Children.LEAF);
        this.setIconBase (ICON_BASE);
    }

    public String getName () {
        return org.netbeans.modules.corba.browser.ir.Util.getLocalizedString("MSG_PleaseWait");
    }
    
    public String getDisplayName () {
        return this.getName();
    }
    
    public boolean canCopy () {
        return false;
    }
    
    public boolean canDestroy () {
        return false;
    }
    
    public boolean canCut () {
        return false;
    }
    
    public boolean canRename () {
        return false;
    }

}
