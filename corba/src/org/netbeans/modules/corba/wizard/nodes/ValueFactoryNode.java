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
import org.netbeans.modules.corba.wizard.nodes.keys.ValueFactoryKey;
import org.netbeans.modules.corba.wizard.nodes.gui.ValueFactoryPanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
/**
 *
 * @author  tzezula
 * @version 
 */
public class ValueFactoryNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/factory";
    
    /** Creates new ValueFactoryNode */
    public ValueFactoryNode(ValueFactoryKey key) {
        super (key);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
    
    
    public String generateSelf (int indent) {
        ValueFactoryKey key = (ValueFactoryKey) this.key;
        String code = ""; // No I18N
        for (int i=0; i< indent; i++) 
            code = code + SPACE;
        code = code + "factory " + this.getName() + " ("+key.getParams()+");\n";
        return code;
    }
    
    public ExPanel getEditPanel () {
        ValueFactoryPanel p = new ValueFactoryPanel ();
        p.setName (this.getName());
        p.setParams (((ValueFactoryKey)this.key).getParams());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ValueFactoryPanel) {
            ValueFactoryPanel vp = (ValueFactoryPanel) p;
            ValueFactoryKey key = (ValueFactoryKey) this.key;
            String newName = vp.getName();
            String newParams = vp.getParams();
            if (!newName.equals(this.getName())) {
                this.setName (newName);
                key.setName (newName);
            }
            if (!newParams.equals(key.getParams())) {
                key.setParams (newParams);
            }
        }
    }

}
