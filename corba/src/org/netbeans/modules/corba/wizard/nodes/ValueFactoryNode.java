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
