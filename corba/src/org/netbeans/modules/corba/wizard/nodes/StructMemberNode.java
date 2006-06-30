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
