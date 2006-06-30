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
