/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.jellytools.modules.corba.nodes;

import org.netbeans.jellytools.ExplorerOperator;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.modules.corba.actions.CopyClientBindingCodeAction;
import org.netbeans.jellytools.modules.corba.actions.UnbindObjectAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.operators.JTreeOperator;

public class NamingObjectNode extends Node {
    
    public NamingObjectNode(JTreeOperator tree, String path) {
       super(tree, "CORBA Naming Service" + path);
    }

    static final UnbindObjectAction unbindObjectAction = new UnbindObjectAction ();
    static final CopyClientBindingCodeAction copyClientBindingCodeAction = new CopyClientBindingCodeAction ();
    static final PropertiesAction propertiesAction = new PropertiesAction();
    
    public void copyClientBindingCode () {
        copyClientBindingCodeAction.perform (this);
    }
    
    public void unbindObject () {
        unbindObjectAction.perform (this);
    }
    
    public void properties() {
        propertiesAction.perform(this);
    }
   
}
