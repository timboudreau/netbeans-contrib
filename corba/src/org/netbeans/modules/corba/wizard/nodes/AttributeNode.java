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
import org.netbeans.modules.corba.wizard.nodes.actions.RenameAction;
import org.netbeans.modules.corba.wizard.nodes.actions.DestroyAction;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
/** 
 *
 * @author  root
 * @version 
 */
public class AttributeNode extends AbstractMutableLeafNode implements Node.Cookie{

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/attribute";
  
    /** Creates new AttributeNode */
    public AttributeNode (NamedKey key) {
        super (key);
        this.getCookieSet().add(this);
        this.setName (key.getName ());
        this.setIconBase (ICON_BASE);
    }
  
    public SystemAction[] createActions () {
        return new SystemAction [] {
            SystemAction.get (DestroyAction.class),
            SystemAction.get (RenameAction.class)
        };
    }
  
    public String generateSelf (int indent) {
        String code = new String ();
        String fill = new String ();
        for (int i=0; i<indent; i++)
            fill = fill + SPACE; // No I18N
        AttributeKey key = (AttributeKey) this.key;
        if (key.isReadOnly ()) {
            code = fill + "readonly ";  // No I18N
        }
        else {
            code = fill;
        }
        code = code + "attribute " + key.getType() + " "+this.getName () +";\n"; // No I18N
        return code;
    }
}
