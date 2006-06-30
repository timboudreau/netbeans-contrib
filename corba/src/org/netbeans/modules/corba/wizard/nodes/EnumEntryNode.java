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

import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.gui.ModulePanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
/**
 *
 * @author  root
 * @version
 */
public class EnumEntryNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/attribute";

    /** Creates new EnumEntryNode */
    public EnumEntryNode(NamedKey key) {
        super (key);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
    
    
    public String generateSelf ( int indent) {
        return this.getName ();
    }
    
    public ExPanel getEditPanel () {
        ModulePanel p = new ModulePanel ();
        p.setName (this.getName());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ModulePanel) {
            String newName = ((ModulePanel)p).getName();
            if (!((NamedKey)this.key).getName().equals(newName)) {
                this.setName (newName);
                ((NamedKey)this.key).setName (newName);
            }
        }
    }
}
