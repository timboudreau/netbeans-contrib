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

import org.openide.nodes.*;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ForwardDclPanel;
/**
 *
 * @author  tzezula
 * @version 
 */
public class ForwardDcl extends AbstractMutableLeafNode {
    
    private static final String INTERFACE_ICON_BASE = "org/netbeans/modules/corba/idl/node/interface";
    private static final String VALUETYPE_ICON_BASE = "org/netbeans/modules/corba/idl/node/value";

    /** Creates new FrowardDcl */
    public ForwardDcl(ForwardDclKey key) {
        super (key);
        this.setName (key.getName());
        if (key.isInterface())
            this.setIconBase (INTERFACE_ICON_BASE);
        else
            this.setIconBase (VALUETYPE_ICON_BASE);
    }
    
    public String generateSelf (int indent) {
        String code = "";
        for (int i=0; i< indent; i++)
            code = code + SPACE;
        if (((ForwardDclKey)key).isInterface())
            code = code + "interface ";
        else
            code = code + "valuetype ";
        code = code + this.getName() + ";\n";
        return code;
    }
        
    
    public ExPanel getEditPanel () {
        ForwardDclPanel p = new ForwardDclPanel ();
        p.setName (this.getName());
        p.setInterface (((ForwardDclKey)key).isInterface());
        return p;
    }
    
    
    public void reInit (ExPanel p) {
        if (p instanceof ForwardDclPanel) {
            ForwardDclPanel fp = (ForwardDclPanel) p;
            String newName = fp.getName();
            boolean newInterface = fp.isInterface();
            if (!newName.equals(this.getName())) {
                this.setName (newName);
                this.key.setName (newName);
            }
            if (newInterface != ((ForwardDclKey)key).isInterface()) {
                ((ForwardDclKey)key).setInterface (newInterface);
                if (newInterface)
                    this.setIconBase (INTERFACE_ICON_BASE);
                else 
                    this.setIconBase (VALUETYPE_ICON_BASE);
            }
        }
    }

}
