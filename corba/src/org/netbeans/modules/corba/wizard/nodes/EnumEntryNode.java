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

package org.netbeans.modules.corba.wizard.nodes;

import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
/** 
 *
 * @author  root
 * @version 
 */
public class EnumEntryNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/attribute";
  
    /** Creates new EnumEntryNode */
    public EnumEntryNode(NamedKey key) {
        super (key);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
  
    public SystemAction[] createActions () {
        return new SystemAction[] { SystemAction.get (DestroyAction.class),
                                    SystemAction.get (RenameAction.class)};
    }
  
    public String generateSelf ( int indent) {
        return this.getName ();
    }
  
}
