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
