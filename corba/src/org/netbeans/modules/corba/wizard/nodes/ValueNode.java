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

import org.openide.TopManager;
import org.openide.DialogDescriptor;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
/**
 *
 * @author  tzezula
 * @version 
 */
public class ValueNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE ="org/netbeans/modules/corba/idl/node/state_member";
    
    /** Creates new ValueNode */
    public ValueNode(ValueKey key) {
        super (key);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
    
    public String generateSelf (int indent) {
        String code = "";
        for (int i=0; i< indent; i++)
            code = code + SPACE;
        ValueKey key = (ValueKey) this.key;
        if (key.isPublic())
            code = code + "public ";
        else
            code = code + "private ";
        code = code + key.getType() + " "+key.getName()+";\n";
        return code;
    }
    
    public ExPanel getEditPanel () {
        ValuePanel p = new ValuePanel ();
        ValueKey key = (ValueKey) this.key;
        p.setName (key.getName());
        p.setType (key.getType());
        p.setPublic (key.isPublic());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ValuePanel) {
            ValuePanel vp = (ValuePanel) p;
            ValueKey key = (ValueKey) this.key;
            String newName = vp.getName();
            String newType = vp.getType();
            boolean newPublic = vp.isPublic();
            if (!this.getName().equals(newName)) {
                this.setName (newName);
                key.setName (newName);
            }
            if (!key.getType().equals(newType)) {
                key.setType (newType);
            }
            key.setPublic (newPublic);
        }
    }

}
