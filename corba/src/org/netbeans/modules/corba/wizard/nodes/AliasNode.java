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
import org.netbeans.modules.corba.wizard.nodes.keys.*;
/** 
 *
 * @author  root
 * @version 
 */
public class AliasNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/type";
  
    /** Creates new AliasNode */
    public AliasNode (NamedKey key) {
        super (key);
        this.setName(key.getName());
        this.setIconBase (ICON_BASE);
    }
  
    public String generateSelf (int indent) {
        String code = new String ();
        for (int i=0; i<indent; i++)
            code = code + SPACE; // No I18N
        AliasKey key = (AliasKey) this.key;
        code = code + "typedef " + key.getType () +" ";
        code = code + this.getName() + " ";
        if (key.getLength().length () > 0) {
            StringTokenizer tk = new StringTokenizer (key.getLength(),",");
            while (tk.hasMoreTokens ()) {
                code = code +"["+ tk.nextToken () +"] ";
            }
        }
        code = code +";\n";
        return code;
    }
  
}
