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
        if ("char".equals(type) && !value.startsWith("\'")) {
            value = "\'" + value + "\'";
        }
        else if ("string".equals(type) && !value.startsWith("\"")) {
            value = "\"" + value + "\"";
        }
        else if ("wchar".equals(type) && !value.startsWith("L\'")) {
            value = "L\'"+value+"\'";
        }
        else if ("wstring".equals(type) && !value.startsWith("L\"")) {
            value = "L\""+value+"\"";
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
