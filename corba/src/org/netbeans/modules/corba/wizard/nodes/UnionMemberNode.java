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
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;

/** 
 *
 * @author  root
 * @version 
 */
public class UnionMemberNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/unionmember";
  
    /** Creates new UnionMemberNode */
    public UnionMemberNode (NamedKey key) {
        super (key);
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
        for (int i=0; i< indent; i++)
            code = code + SPACE;  // No I18N
        UnionMemberKey key = (UnionMemberKey) this.key;
        if (key.isDefaultValue ()) {
            code = code + "default: " + key.getType() + " " + this.getName (); // No I18N
        }
        else {
            // Bug for wchar, char
            code = code +"case " + key.getLabel() + ": ";  // No I18N
            code = code + key.getType () + " " + this.getName (); // No I18N 
        }
        // Handle array here
        if (key.getLength().length() > 0) {
            StringTokenizer tk = new StringTokenizer (key.getLength(),",");
            while (tk.hasMoreTokens()) {
                String dim = tk.nextToken().trim();
                code = code + " ["+ dim +"]";   // No I18N
            }
        }
        code = code + ";\n"; // No I18N
        return code;
    }


    public void destroy () {
        ((UnionNode)this.getParentNode ()).canAdd = true;
        super.destroy ();
    }
    
}
