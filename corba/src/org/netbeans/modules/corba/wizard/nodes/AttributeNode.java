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
import org.netbeans.modules.corba.wizard.nodes.actions.DestroyAction;
import org.netbeans.modules.corba.wizard.nodes.actions.EditAction;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.gui.AttributePanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
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
    
    public ExPanel getEditPanel () {
        AttributePanel p = new AttributePanel ();
        String type = ((AttributeKey)this.key).getType();
        boolean ro = ((AttributeKey)this.key).isReadOnly();
        p.setName (this.getName());
        p.setType (type);
        p.setReadOnly (ro);
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof AttributePanel) {
            String newName = ((AttributePanel)p).getName();
            String newType = ((AttributePanel)p).getType();
            boolean ro = ((AttributePanel)p).isReadOnly();
            if (!this.getName().equals(newName)) {
                this.setName (newName);
                ((AttributeKey)this.key).setName (newName);
            }
            if (!newType.equals(((AttributeKey)this.key).getType()))
                ((AttributeKey)this.key).setType (newType);
            if (ro != ((AttributeKey)this.key).isReadOnly())
                ((AttributeKey)this.key).setReadOnly (ro);
        }
    }
}
