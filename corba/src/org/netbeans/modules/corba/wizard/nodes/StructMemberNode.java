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

import java.util.StringTokenizer;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;

/** 
 *
 * @author  root
 * @version 
 */
public class StructMemberNode extends AbstractMutableLeafNode  {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/declarator";
  
    /** Creates new StructMemberNode */
    public StructMemberNode (NamedKey key) {
        super (key);
        this.setName (key.getName ());
        this.setIconBase (ICON_BASE);
    }
  
  
    public String generateSelf (int indent) {
        String code = new String ();
        for (int i =0; i< indent; i++) {
            code =code + SPACE;  //No I18N
        }
        AliasKey key = (AliasKey) this.key;
        code = code + key.getType () + " "; // No I18N
        code = code + this.getName ();
        if (key.getLength ().length () > 0) {
            StringTokenizer tk = new StringTokenizer (key.getLength(),",");
            code = code + " [" + tk.nextToken().trim() +"]";
        }
        code = code + ";\n"; // No I18N
        return code;
    }
    
    public ExPanel getEditPanel () {
        AliasPanel p = new AliasPanel ();
        p.setName (this.getName());
        p.setType (((AliasKey)key).getType());
        p.setLength (((AliasKey)key).getLength());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof AliasPanel) {
            AliasPanel ap = (AliasPanel) p;
            String newName = ap.getName();
            String newType = ap.getType();
            String newLength = ap.getLength();
            AliasKey key = (AliasKey) this.key;
            if (!key.getName().equals(newName)) {
                this.setName(newName);
                key.setName (newName);
            }
            if (!key.getType().equals(newType))
                key.setType (newType);
            if (!key.getLength().equals(newLength))
                key.setLength (newLength);
        }
    }
  
}
