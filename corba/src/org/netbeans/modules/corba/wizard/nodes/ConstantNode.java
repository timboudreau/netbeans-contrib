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

import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.gui.ConstPanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
/** 
 *
 * @author  root
 * @version 
 */
public class ConstantNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/const";
  
    /** Creates new CreateConstantAction */
    public ConstantNode (NamedKey key) {
        super (key);
        this.setName (key.getName ());
        this.setIconBase (ICON_BASE);
    }
  
  
  
    public String generateSelf (int indent){
        String code = new String ();
        String fill = new String ();
        for (int i=0; i<indent; i++)
            fill = fill + SPACE; // No I18N
        ConstKey key = (ConstKey) this.key;
        String type = key.getType();
        String value = key.getValue();
        if ("char".equals(type.trim()) && !value.trim().startsWith("\'")) {
            value = "\'" + value + "\'";
        }
        else if ("string".equals(type.trim()) && !value.trim().startsWith("\"")) {
            value = "\"" + value + "\"";
        }
        code = fill + "const "+ type + " "+ this.getName()+ " = "+ value +";\n"; // No I18N
        return code;
    }
    
    public ExPanel getEditPanel () {
        ConstPanel p = new ConstPanel();
        p.setName (this.getName());
        p.setType (((ConstKey)this.key).getType());
        p.setValue (((ConstKey)this.key).getValue());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ConstPanel) { 
            String newName = ((ConstPanel)p).getName();
            String newValue = ((ConstPanel)p).getValue();
            String newType = ((ConstPanel)p).getType();
            ConstKey key = (ConstKey) this.key;
            if (! key.getName ().equals(newName)) {
                key.setName (newName);
                this.setName (newName);
            }
            if (! key.getValue().equals(newValue))
                key.setValue (newValue);
            if (! key.getType().equals(newType))
                key.setType (newType);
        }
    }
  
}
