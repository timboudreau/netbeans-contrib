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

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.Util;

/** A node with some children.
 *
 * @author tzezula
 */
public class IRUnknownTypeNode extends IRLeafNode {
  
    private static final String UNKNOWN_ICON_BASE =
        "org/netbeans/modules/corba/browser/ir/resources/unknown";

    public IRUnknownTypeNode () {
        super ();
        setIconBase (UNKNOWN_ICON_BASE);
        setName (Util.getLocalizedString("TITLE_UnknownIRType")); 
    }
 
  
}
